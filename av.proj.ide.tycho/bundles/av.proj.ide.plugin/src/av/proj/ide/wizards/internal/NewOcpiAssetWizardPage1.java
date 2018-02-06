/*
 * This file is protected by Copyright. Please refer to the COPYRIGHT file
 * distributed with this source distribution.
 *
 * This file is part of OpenCPI <http://www.opencpi.org>
 *
 * OpenCPI is free software: you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 *
 * OpenCPI is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package av.proj.ide.wizards.internal;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.eclipse.core.databinding.Binding;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.beans.PojoProperties;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.observable.value.SelectObservableValue;
import org.eclipse.core.databinding.observable.value.WritableValue;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.jface.layout.PixelConverter;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.internal.ide.IDEWorkbenchPlugin;
import org.eclipse.ui.internal.wizards.datatransfer.DataTransferMessages;

import av.proj.ide.avps.internal.AngryViperAssetService;
import av.proj.ide.wizards.NewOcpiAssetWizard;
import av.proj.ide.wizards.internal.validators.AddToProjectValidator;
import av.proj.ide.wizards.internal.validators.ApplicationValidator;
import av.proj.ide.wizards.internal.validators.AssemblyValidator;
import av.proj.ide.wizards.internal.validators.HdlPrimitiveLibraryValidator;
import av.proj.ide.wizards.internal.validators.LibraryOptionValidator;
import av.proj.ide.wizards.internal.validators.LibraryValidator;
import av.proj.ide.wizards.internal.validators.ProjectValidator;
import av.proj.ide.wizards.internal.validators.SpecValidator;


public class NewOcpiAssetWizardPage1 extends WizardPage {
	private Label commandLabel, addToProjectLabel, libraryOptionLabel;
	private Combo langCombo;
	private final String[] commandOptions = {"Project", "Application", 
			"HDL Assembly", "Worker", "Component", "Library", "Protocol",
			/*", Proxy", "HDL Card", "HDL Slot", "HDL Device",*/
			"HDL Platform", "HDL Primitive Library"/*, 
			 * "HDL Primitive Core"*/
	};
	private boolean isXMLApp;
	private String previousProjBrowse;
	private final String[] modelOptions = {"RCC", "HDL"};
	private final String[] hdlLangOptions = {"VHDL"/*, "Verilog"*/};
	private final String[] rccLangOptions = {"C", "C++"};
	private List<String> libraryOptions;
	private Combo commandCombo, libraryOptionNameCombo, workerSpecNameCombo;
	private GridData gd;
	private Button projectBrowseButton, isXMLAppButton;
	private String commandName, workerName, cardName, slotName, 
		deviceName, proxyName, platformName, primCoreName, workerSpec, 
		workerModel, workerLang, partNumber, timeServerFrequency;

	private WritableValue<String> addToProject, projectName, applicationName, libraryName,
		libraryOptionName, specName, primLibName, assemblyName, projectPrefix,
		addToType, protocolName;
	
	private Text addToProjectText, projectNameText, applicationNameText, 
		libraryNameText, specNameText, protocolNameText, workerNameText,
		assemblyNameText, cardNameText, slotNameText, deviceNameText,
		proxyNameText, platformNameText, primLibNameText, primCoreNameText,
		projectPrefixText, partNumberText, timeServerFrequencyText;
	
	private org.eclipse.swt.widgets.List projectDependencyList;
	private DataBindingContext ctx;
	private NewOcpiAssetWizard wizard;
	private String currProj;
	private List<String> depsList;
	private Composite container;

	public NewOcpiAssetWizardPage1(NewOcpiAssetWizard wizard, ISelection selection) {
		super("wizardPage");
		setTitle("Create a new ANGRYVIPER Asset");
		setDescription("This wizard creates a new ANGRYVIPER asset");
		this.previousProjBrowse = ""; 
		this.libraryOptions = new ArrayList<String>();
		this.gd = new GridData(GridData.FILL_HORIZONTAL);
		this.gd.horizontalSpan = 2;
		this.ctx = new DataBindingContext();
		this.wizard = wizard;
		this.currProj = "";
	}

	public void createControl(Composite parent) {
		container = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		container.setLayout(layout);
		layout.numColumns = 3;
		layout.verticalSpacing = 9;
		
		createCommandGroup();
		
		setControl(container);
		
		getShell().setMinimumSize(500, 600);
	}
	
	private void createCommandGroup() {
		this.commandLabel = new Label(container, SWT.NULL);
		this.commandLabel.setText("Asset Type:");
		
		this.commandName = "";
		this.commandCombo = new Combo(container, SWT.BORDER | SWT.READ_ONLY);
		for (String command : this.commandOptions) {
			this.commandCombo.add(command);
		}
		GridData commandComboData = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL);
		commandComboData.widthHint = new PixelConverter(this.commandCombo).convertWidthInCharsToPixels(25);
		commandComboData.horizontalSpan = 2;
		this.commandCombo.setLayoutData(commandComboData);
		this.commandCombo.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				commandChanged(container);
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
			}
		});
	}
	
	private void createAddToProjectGroup() {
		// Widgets for add to project field
		this.addToProjectLabel = new Label(container, SWT.NULL);
		this.addToProjectLabel.setText("Add to Project:");
		
		this.addToProject = new WritableValue<String>("", String.class);
		this.addToProjectText = new Text(container, SWT.BORDER);
		GridData directoryTextData = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL);
		directoryTextData.widthHint = new PixelConverter(this.addToProjectText).convertWidthInCharsToPixels(25);
		this.addToProjectText.setLayoutData(directoryTextData);
		/*this.addToProjectText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				if (libraryOptionNameCombo != null) {
					updateLibraryCombo();
				}
			}
		});*/
		
		this.projectBrowseButton = new Button(container, SWT.PUSH);
		this.projectBrowseButton.setText("Browse...");
		setButtonLayoutData(this.projectBrowseButton);
		this.projectBrowseButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				handleProjectBrowse();
			}
		});
		
		if (!this.wizard.getProject().equals("")) {
			String project = this.wizard.getProject();
			final IProject proj = ResourcesPlugin.getWorkspace().getRoot().getProject(project);
			if (proj.exists()) {
				setAddToProject(proj.getLocation().toString());
			}
		}
		
		IObservableValue<Boolean> target = WidgetProperties.text(SWT.Modify).observe(this.addToProjectText);
		IObservableValue<?> model = PojoProperties.value(this.getClass(), "addToProject").observe(this);
		UpdateValueStrategy strategy = new UpdateValueStrategy();
		strategy.setBeforeSetValidator(new AddToProjectValidator(this));
		ctx.bindValue(target, model, strategy, null);
	}
	
	public void updateLibraryOptions(String project) {
		this.libraryOptions.clear();
		File proj = new File(project);
		if (proj.isDirectory()) {
			File compsFolder = null;
			for (File f : proj.listFiles()) {
				if (f.getName().trim().equals("components")) {
					compsFolder = f;
					break;
				}
			}
			if (compsFolder != null && compsFolder.isDirectory()) {
				for (File f : compsFolder.listFiles()) {
					if (f.isDirectory()) {
						String name = f.getName().trim();
						if (!name.endsWith(".rcc") 
								&& !name.endsWith(".hdl")
								&& !name.endsWith(".ocl")
								&& !name.endsWith(".test") && !name.equals("specs")
								&& !name.equals("lib") && !name.equals("gen")
								&& !name.equals("include")) {
							this.libraryOptions.add(f.getName());
						}
					}
				}
			} else {
				updateStatus("Project must have a components library before adding this asset");
			}
		}		
		if (this.libraryOptions.size() == 0) {
			this.libraryOptions.add("components (default)");
		}	
		updateLibraryOptionCombo();
	}
	
	private void updateLibraryOptionCombo() {
		if (this.libraryOptionNameCombo != null) {
			this.libraryOptionNameCombo.removeAll();
			for (String s : libraryOptions) {
				this.libraryOptionNameCombo.add(s);
			}
			this.libraryOptionNameCombo.select(0);
		}
	}
	
	@SuppressWarnings("restriction")
	private void handleProjectBrowse() {
		DirectoryDialog dialog = new DirectoryDialog(this.addToProjectText.getShell(), SWT.SHEET);
		dialog.setMessage(DataTransferMessages.FileImport_importFileSystem);
		
		if (this.previousProjBrowse.equals("")) {
			dialog.setFilterPath(IDEWorkbenchPlugin.getPluginWorkspace().getRoot().getLocation().toOSString());
		} else {
			dialog.setFilterPath(this.previousProjBrowse);
		}
		String selectedDirectory = dialog.open();
		if (selectedDirectory != null) {
			if (!previousProjBrowse.equals(selectedDirectory)) {
				//this.projectChanged = true;
				setAddToProject(selectedDirectory);
				this.addToProjectText.setText(getAddToProject());
				this.previousProjBrowse = selectedDirectory;
			}
		}
	}
	
	private void createCreateProjectGroup() {
		// Widgets to create new project
		Label label = new Label(container, SWT.NULL);
		label.setText("Project Name:");
		this.projectName = new WritableValue<String>("", String.class);
		this.projectNameText = new Text(container, SWT.BORDER);
		this.projectNameText.setLayoutData(gd);
		this.projectNameText.setMessage("required");
		
		label = new Label(container, SWT.NULL);
		label.setText("Project Prefix:");
		label.setToolTipText("Your project is registered as \"prefix.projectName\"");
		this.projectPrefix = new WritableValue<String>("", String.class);
		this.projectPrefixText = new Text(container, SWT.BORDER);
		this.projectPrefixText.setLayoutData(gd);
		this.projectPrefixText.setMessage("optional");
		
//		label = new Label(container, SWT.NULL);
//		label.setText("Project Package:");
//		this.projectPackage = new WritableValue("", String.class);
//		this.projectPackageText = new Text(container, SWT.BORDER);
//		this.projectPackageText.setLayoutData(gd);
//		this.projectPackageText.setMessage("optional");
		
		label = new Label(container, SWT.NULL);
		label.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_BEGINNING));
		label.setText("Project Dependencies:");
		this.projectDependencyList = new org.eclipse.swt.widgets.List(container, SWT.BORDER | SWT.MULTI | SWT.BORDER);
		GridData listgd = new GridData(GridData.FILL_BOTH);
		listgd.horizontalSpan = 2;
		this.projectDependencyList.setLayoutData(listgd);
		this.depsList = new ArrayList<String>();
		label = new Label(container, SWT.NULL);
		final Button addButton = new Button(container, SWT.BORDER);
		addButton.setText("Add");
		addButton.addSelectionListener(new SelectionAdapter() {
			String previousDepBrowse = "";
			
			public void widgetSelected(SelectionEvent e) {
				handleDependencyAdd();
			}
			
			public void handleDependencyAdd() {
				DirectoryDialog dialog = new DirectoryDialog(addButton.getShell(), SWT.SHEET);
				dialog.setMessage(DataTransferMessages.FileImport_importFileSystem);
				
				if (previousDepBrowse.equals("")) {
					dialog.setFilterPath(IDEWorkbenchPlugin.getPluginWorkspace().getRoot().getLocation().toOSString());
				} else {
					dialog.setFilterPath(previousDepBrowse);
				}
				String selectedDirectory = dialog.open();
				if (selectedDirectory != null) {
					if (!Arrays.asList(projectDependencyList.getItems()).contains(selectedDirectory)) {
						projectDependencyList.add(selectedDirectory);
						depsList.add(selectedDirectory);
					}
					previousDepBrowse = selectedDirectory;
				}
			}
		});
		
		Button removeButton = new Button(container, SWT.BORDER);
		removeButton.setText("Remove");
		removeButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				projectDependencyList.remove(projectDependencyList.getSelectionIndices());
				depsList.removeAll(Arrays.asList(projectDependencyList.getSelection()));
			}
		});
		
		IObservableValue<Boolean> target = WidgetProperties.text(SWT.Modify).observe(this.projectNameText);
		IObservableValue<?> model = PojoProperties.value(this.getClass(), "projectName").observe(this);
		UpdateValueStrategy strategy = new UpdateValueStrategy();
		strategy.setBeforeSetValidator(new ProjectValidator(this));
		ctx.bindValue(target, model, strategy, null);
		
		target = WidgetProperties.text(SWT.Modify).observe(this.projectPrefixText);
		model = PojoProperties.value(this.getClass(), "projectPrefix").observe(this);
		ctx.bindValue(target, model, null, null);
		
//		target = WidgetProperties.text(SWT.Modify).observe(this.projectPackageText);
//		model = PojoProperties.value(this.getClass(), "projectPackage").observe(this);
//		ctx.bindValue(target, model, null, null);
	}
	
	private void createAddApplicationGroup() {
		// Widgets to add an application
		Label label = new Label(container, SWT.NULL);
		label.setText("Application Name:");
		
		this.applicationName = new WritableValue<String>("", String.class);
		this.applicationNameText = new Text(container, SWT.BORDER);
		this.applicationNameText.setLayoutData(gd);
		
		IObservableValue<Boolean> target = WidgetProperties.text(SWT.Modify).observe(this.applicationNameText);
		IObservableValue<?> model = PojoProperties.value(this.getClass(), "applicationName").observe(this);
		UpdateValueStrategy strategy = new UpdateValueStrategy();
		strategy.setBeforeSetValidator(new ApplicationValidator(this));
		ctx.bindValue(target, model, strategy, null);
		
		label = new Label(container, SWT.NULL);
		label.setText("XML Only:");
		
		this.isXMLApp = false;
		this.isXMLAppButton = new Button(container, SWT.CHECK);
		this.isXMLAppButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (isXMLAppButton.getSelection()) {
					setIsXMLApp(true);
				} else {
					setIsXMLApp(false);
				}
			}
		});
	}
	
	private void createAddLibraryGroup(Composite container) {
		// Widgets to add a library
		Label label = new Label(container, SWT.NULL);
		label.setText("Library Name:");
		
		this.libraryName = new WritableValue<String>("", String.class);
		this.libraryNameText = new Text(container, SWT.BORDER);
		this.libraryNameText.setLayoutData(gd);
		
		if (!this.wizard.getProject().equals("")) {
			String project = this.wizard.getProject();
			final IProject proj = ResourcesPlugin.getWorkspace().getRoot().getProject(project);
			if (proj.exists()) {
				final IFolder compsFolder = proj.getFolder("components");
				if (compsFolder != null && !compsFolder.exists()) {
					setLibraryName("components");
				}
			}
		}

		// Data bindings
		IObservableValue<Boolean> target = WidgetProperties.text(SWT.Modify).observe(this.libraryNameText);
		IObservableValue<?> model = PojoProperties.value(this.getClass(), "libraryName").observe(this);
		UpdateValueStrategy strategy = new UpdateValueStrategy();
		strategy.setBeforeSetValidator(new LibraryValidator(this));
		ctx.bindValue(target, model, strategy, null);
	}
	
	private void createAddSpecGroup() {
		// Widgets to add a spec
		Label label = new Label(container, SWT.NULL);
		label.setText("Component Name:");
		
		this.specName = new WritableValue<String>("", String.class);
		this.specNameText = new Text(container, SWT.BORDER);
		this.specNameText.setLayoutData(gd);
		
		label = new Label(container, SWT.NULL);
		label.setText("Add To:");
		this.addToType = new WritableValue<String>("", String.class);
		Group buttonGroup = new Group(container, SWT.NULL);
		buttonGroup.setLayout(new RowLayout(SWT.HORIZONTAL));
		buttonGroup.setLayoutData(this.gd);
		Button topLevelTypeButton = new Button(buttonGroup, SWT.RADIO);
		topLevelTypeButton.setText("Top Level of Project");
		Button libraryTypeButton = new Button(buttonGroup, SWT.RADIO);
		libraryTypeButton.setText("Library");
		setAddToType("library");
		
		// Data bindings
		IObservableValue<Boolean> target = WidgetProperties.text(SWT.Modify).observe(this.specNameText);
		IObservableValue<?> model = PojoProperties.value(this.getClass(), "specName").observe(this);
		UpdateValueStrategy strategy = new UpdateValueStrategy();
		strategy.setBeforeSetValidator(new SpecValidator(this));
		ctx.bindValue(target, model, strategy, null);
		
		SelectObservableValue<String> selectedRadioObservable = new SelectObservableValue<String>();
		selectedRadioObservable.addOption("topLevel", WidgetProperties.selection().observe(topLevelTypeButton));
		model = PojoProperties.value(this.getClass(), "addToType").observe(this);
		selectedRadioObservable.addOption("library", WidgetProperties.selection().observe(libraryTypeButton));
		model = PojoProperties.value(this.getClass(), "addToType").observe(this);
		ctx.bindValue(selectedRadioObservable, model);
	}
	
	private void createLibraryOptionGroup() {
		if (this.libraryOptionName == null || this.libraryOptionNameCombo.isDisposed()) {
			// Widgets for specifying library option
			this.libraryOptionLabel = new Label(container, SWT.NULL);
			this.libraryOptionLabel.setText("Add To Library:");
			
			this.libraryOptionName = new WritableValue<String>("", String.class);
			this.libraryOptionNameCombo = new Combo(container, SWT.READ_ONLY);
			this.libraryOptionNameCombo.setLayoutData(gd);
			
			// Data bindings
			IObservableValue<Boolean> target = WidgetProperties.selection().observe(this.libraryOptionNameCombo);
			IObservableValue<?> model = PojoProperties.value(this.getClass(), "libraryOptionName").observe(this);
			UpdateValueStrategy strategy = new UpdateValueStrategy();
			strategy.setBeforeSetValidator(new LibraryOptionValidator(this));
			ctx.bindValue(target, model, strategy, null);
	
			updateLibraryOptionCombo();
		}
	}
	
	private void removeLibraryOptionGroup() {
		for (Control widget : container.getChildren()) {
			if (widget.equals(this.libraryOptionLabel) || widget.equals(this.libraryOptionNameCombo)) {
				widget.dispose();
			}
		}
		refreshStrings();
	}
	
	private void createAddProtocolGroup() {
		// Widgets to add a protocol
		Label label = new Label(container, SWT.NULL);
		label.setText("Protocol Name:");
		
		this.protocolName = new WritableValue<String>("", String.class);
		this.protocolNameText = new Text(container, SWT.BORDER);
		this.protocolNameText.setLayoutData(gd);
		
		label = new Label(container, SWT.NULL);
		label.setText("Add To:");
		this.addToType = new WritableValue<String>("", String.class);
		Group buttonGroup = new Group(container, SWT.NULL);
		buttonGroup.setLayout(new RowLayout(SWT.HORIZONTAL));
		buttonGroup.setLayoutData(this.gd);
		Button topLevelTypeButton = new Button(buttonGroup, SWT.RADIO);
		topLevelTypeButton.setText("Top Level of Project");
		Button libraryTypeButton = new Button(buttonGroup, SWT.RADIO);
		libraryTypeButton.setText("Library");
		setAddToType("library");
		
		// Data bindings
		IObservableValue<Boolean> target = WidgetProperties.text(SWT.Modify).observe(this.protocolNameText);
		IObservableValue<?> model = PojoProperties.value(this.getClass(), "protocolName").observe(this);
		UpdateValueStrategy strategy = new UpdateValueStrategy();
		strategy.setBeforeSetValidator(new SpecValidator(this));
		ctx.bindValue(target, model, strategy, null);
		
		SelectObservableValue<String> selectedRadioObservable = new SelectObservableValue<String>();
		selectedRadioObservable.addOption("topLevel", WidgetProperties.selection().observe(topLevelTypeButton));
		model = PojoProperties.value(this.getClass(), "addToType").observe(this);
		selectedRadioObservable.addOption("library", WidgetProperties.selection().observe(libraryTypeButton));
		model = PojoProperties.value(this.getClass(), "addToType").observe(this);
		ctx.bindValue(selectedRadioObservable, model);
	}
	
	private void createAddWorkerGroup() {
		// Widgets to add a worker
		Label label = new Label(container, SWT.NULL);
		label.setText("Worker Name:");
		
		this.workerName = "";
		this.workerNameText = new Text(container, SWT.BORDER);
		this.workerNameText.setLayoutData(gd);
		this.workerNameText.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent arg0) {
				//if (dialogChanged("Worker", workerNameText.getText())) {
					workerName = workerNameText.getText();
				//}
			}
		});
		
		createLibraryOptionGroup();
		
		label = new Label(container, SWT.NULL);
		label.setText("Component:");
		
		this.workerSpec = "";
		this.workerSpecNameCombo = new Combo(container, SWT.READ_ONLY | SWT.V_SCROLL);
		this.workerSpecNameCombo.setLayoutData(gd);
		this.workerSpecNameCombo.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				workerSpec = getWorkerSpecComboText();
				if (workerSpec.equals("--- Local Library Specs ---") || workerSpec.equals("--- Local Project Specs ---")
						|| workerSpec.equals("--- Project Dependency Specs ---")
						|| workerSpec.equals("--- OCPI_PROJECT_PATH Specs ---")
						|| workerSpec.equals("--- OCPI_CDK_DIR Specs ---")) {
					workerSpecNameCombo.select(workerSpecNameCombo.getSelectionIndex() + 1);
					workerSpec = getWorkerSpecComboText();
				}
			}

			private String getWorkerSpecComboText() {
				return workerSpecNameCombo.getItem(workerSpecNameCombo.getSelectionIndex());
			}
		});
		
		setComponentSpecs();
		//updateWorkerSpecCombo(getAddToProject(), true);
		//updateWorkerSpecCombo(getLibraryOptionName(), false);
		
		label = new Label(container, SWT.NULL);
		label.setText("Model:");
		
		final Combo modelCombo = new Combo(container, SWT.READ_ONLY);
		modelCombo.setLayoutData(gd);
		for (String option : this.modelOptions) {
			modelCombo.add(option);
		}
		modelCombo.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				handleModelSelection(modelCombo.getItem(modelCombo.getSelectionIndex()));
			}			
		});
		
		label = new Label(container, SWT.NULL);
		label.setText("Prog. Lang:");
		
		this.workerLang = "";
		this.langCombo = new Combo(container, SWT.READ_ONLY);
		this.langCombo.setLayoutData(gd);
		this.langCombo.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				//if (dialogChanged("Worker Lang", getWorkerLangComboText())) {
					workerLang = getWorkerLangComboText();
				//}
			}
			
			private String getWorkerLangComboText() {
				return langCombo.getItem(langCombo.getSelectionIndex());
			}
		});
	}

	public void setComponentSpecs ()
	{
		Set<String> specs = AngryViperAssetService.getAllOcpiSpecs();
		
		if (!this.workerSpecNameCombo.isDisposed()) {
			this.workerSpecNameCombo.removeAll();

			for (String s : specs) {
				this.workerSpecNameCombo.add(s);
			}
		}
	}
	
	// HERE

	public void updateWorkerSpecCombo(String value, boolean isProj) {
		if (this.workerSpecNameCombo != null) {
			String projectName = "";
			String libOption = "";
			if (isProj) {
				String[] split = value.split(ResourcesPlugin.getWorkspace().getRoot().getFullPath().toString());
				projectName = split[split.length-1];
				if (projectName.length() == 0) {
					return;
				}
				this.currProj = projectName;
			} else {
				projectName = this.currProj;
				libOption = value;
			}
			if (projectName.length() == 0) {
				return;
			}
			IProject selectedProject = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
			IFolder compsFolder = selectedProject.getFolder("components");
			List<String> specs = new ArrayList<String>();
			boolean found = false;
			if (compsFolder.exists()) {
				// Local library specs
				if (libOption.length() > 0) {
					if (libOption.equals("components (default)")) {
						IFolder specsFolder = compsFolder.getFolder("specs");
						if (specsFolder.exists()) {
							try {
								for (IResource r : specsFolder.members()) {
									String name = r.getName();
									if (name.endsWith("_spec.xml") || name.endsWith("-spec.xml")) {
										if (!found) {
											specs.add("--- Local Library Specs ---");
										}
										specs.add(name.replace(".xml", ""));
										found = true;
									}
								}
							} catch (CoreException e) {
								e.printStackTrace();
							}
						}
					} else {
						IFolder libFolder = compsFolder.getFolder(libOption);
						if (libFolder.exists()) {
							IFolder specsFolder = libFolder.getFolder("specs");
							if (specsFolder.exists()) {
								try {
									for (IResource r : specsFolder.members()) {
										String name = r.getName();
										if (name.endsWith("_spec.xml") || name.endsWith("-spec.xml")) {
											if (!found) {
												specs.add("--- Local Library Specs ---");
											}
											specs.add(name.replace(".xml", ""));
											found = true;
										}
									}
								} catch (CoreException e) {
									e.printStackTrace();
								}
							}
						}
					}
				}
				
				// Local project top level specs
				found = false;
				IFolder specsFolder = selectedProject.getFolder("specs");
				if (specsFolder.exists()) {
					try {
						for (IResource r : specsFolder.members()) {
							String name = r.getName();
							if (name.endsWith("_spec.xml") || name.endsWith("-spec.xml")) {
								if (!found) {
									specs.add("--- Local Project Specs ---");
								}
								specs.add(name.replace(".xml", ""));
								found = true;
							}
						}
					} catch (CoreException e) {
						e.printStackTrace();
					}
				}
			}
			
			// Project dependencies top level specs
			found = false;
			String[] dependencies = getDependencies(selectedProject);
			for (String s : dependencies) {
				File dependencyFolder = new File(s);
				if (dependencyFolder != null && dependencyFolder.exists() && dependencyFolder.isDirectory()) {
					for (String fo : dependencyFolder.list()) {
						if (fo.equals("specs")) {
							File specsFolder = new File(s, fo);
							if (specsFolder != null && specsFolder.exists() && specsFolder.isDirectory()) {
								for (String sp : specsFolder.list()) {
									if (sp.endsWith("_spec.xml") || sp.endsWith("-spec.xml")) {
										if (!found) {
											specs.add("--- Project Dependency Specs ---");
										}
										specs.add(sp.replace(".xml", ""));
										found = true;
									}
								}
							}
						}
					}
				}
			}
			
			// PROJECT_PATH top level specs
			found = false;
			String projPath = System.getenv("OCPI_PROJECT_PATH");
			if (projPath != null) {
				String[] projPaths = projPath.split(":");
				for (String s : projPaths) {
					File dependencyFolder = new File(s);
					if (dependencyFolder != null && dependencyFolder.exists() && dependencyFolder.isDirectory()) {
						for (String fo : dependencyFolder.list()) {
							if (fo.equals("specs")) {
								File specsFolder = new File(s, fo);
								if (specsFolder != null && specsFolder.exists() && specsFolder.isDirectory()) {
									for (String sp : specsFolder.list()) {
										if (sp.endsWith("_spec.xml") || sp.endsWith("-spec.xml")) {
											if (!found) {
												specs.add("--- Project Dependency Specs ---");
											}
											specs.add(sp.replace(".xml", ""));
											found = true;
										}
									}
								}
							}
						}
					}
				}
			}
			
			// CDK_DIR top level specs
			found = false;
			File cdkDir = new File(System.getenv("OCPI_CDK_DIR"));
			if (cdkDir != null && cdkDir.exists() && cdkDir.isDirectory()) {
				for (String fo : cdkDir.list()) {
					if (fo.equals("specs")) {
						File specsFolder = new File(cdkDir, fo);
						if (specsFolder != null && specsFolder.exists() && specsFolder.isDirectory()) {
							for (String fi : specsFolder.list()) {
								if (fi.endsWith("_spec.xml") || fi.endsWith("-spec.xml")) {
									if (!found) {
										specs.add("--- OCPI_CDK_DIR Specs ---");
									}
									specs.add(fi.replace(".xml", ""));
									found = true;
								}
							}
						}
					}
				}
			}
			
			if (!this.workerSpecNameCombo.isDisposed()) {
				this.workerSpecNameCombo.removeAll();
				for (String s : specs) {
					this.workerSpecNameCombo.add(s);
				}
			}
		}
	}
	
	private String[] getDependencies(IProject currProject) {
		String[] dependencies = {};
		IFile projectMk = currProject.getFile("Project.mk");
		if (projectMk.exists()) {
			String line = null;
			FileReader fileReader = null;
			BufferedReader bufferedReader = null;
			try {
				fileReader = new FileReader(projectMk.getLocation().toString());
				bufferedReader = new BufferedReader(fileReader);
				while ((line = bufferedReader.readLine()) != null) {
					if (line.startsWith("ProjectDependencies=")) {
						dependencies = line.replace("ProjectDependencies=", "").split(" ");
						break;
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					if (fileReader != null) {
						fileReader.close();
					}
					if (bufferedReader != null) {
						bufferedReader.close();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return dependencies;
	}
	
	private void handleModelSelection(String model) {
		this.langCombo.removeAll();
		if (model.equals("RCC")) {
			this.workerModel = "rcc";
			addLangComboOptions(this.rccLangOptions);
		} else if (model.equals("HDL")) {
			this.workerModel = "hdl";
			addLangComboOptions(this.hdlLangOptions);
		}
	}
	
	private void addLangComboOptions(String[] options) {
		for (String option : options) {
			this.langCombo.add(option);
		}
	}
	
	private void createAddAssemblyGroup() {
		// Widgets to add an assembly
		Label label = new Label(container, SWT.NULL);
		label.setText("Assembly Name:");
		
		this.assemblyName = new WritableValue<String>("", String.class);
		this.assemblyNameText = new Text(container, SWT.BORDER);
		this.assemblyNameText.setLayoutData(gd);

		// Data bindings
		IObservableValue<Boolean> target = WidgetProperties.text(SWT.Modify).observe(this.assemblyNameText);
		IObservableValue<?> model = PojoProperties.value(this.getClass(), "assemblyName").observe(this);
		UpdateValueStrategy strategy = new UpdateValueStrategy();
		strategy.setBeforeSetValidator(new AssemblyValidator(this));
		ctx.bindValue(target, model, strategy, null);
		
		//dialogChanged("Assembly", this.assemblyNameText.getText());
	}
	
	private void createAddCardGroup(Composite container) {
		// Widgets to add a card
		Label label = new Label(container, SWT.NULL);
		label.setText("Card Name:");
		
		this.cardName = "";
		this.cardNameText = new Text(container, SWT.BORDER);
		this.cardNameText.setLayoutData(gd);
		this.cardNameText.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent arg0) {
				//if (dialogChanged("Hdl Card", cardNameText.getText())) {
					cardName = cardNameText.getText();
				//}
			}
		});
	}
	
	private void createAddSlotGroup(Composite container) {
		// Widgets to add a slot
		Label label = new Label(container, SWT.NULL);
		label.setText("Slot Name:");
		
		this.slotName = "";
		this.slotNameText = new Text(container, SWT.BORDER);
		this.slotNameText.setLayoutData(gd);
		this.slotNameText.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent arg0) {
				//if (dialogChanged("Hdl Slot", slotNameText.getText())) {
					slotName = slotNameText.getText();
				//}
			}
		});
	}
	
	private void createAddDeviceGroup(Composite container) {
		// Widgets to add a device
		Label label = new Label(container, SWT.NULL);
		label.setText("Device Name:");
		
		this.deviceName = "";
		this.deviceNameText = new Text(container, SWT.BORDER);
		this.deviceNameText.setLayoutData(gd);
		this.deviceNameText.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent arg0) {
				//if (dialogChanged("Hdl Device", deviceNameText.getText())) {
					deviceName = deviceNameText.getText();
				//}
			}
		});
	}
	
	private void createAddProxyGroup(Composite container) {
		// Widgets to add a proxy
		Label label = new Label(container, SWT.NULL);
		label.setText("Proxy Name:");
		
		this.proxyName = "";
		this.proxyNameText = new Text(container, SWT.BORDER);
		this.proxyNameText.setLayoutData(gd);
		this.proxyNameText.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent arg0) {
				//if (dialogChanged("Proxy", proxyNameText.getText())) {
					proxyName = proxyNameText.getText();
				//}
			}
		});
		
		label = new Label(container, SWT.NULL);
		label.setText("Slave Worker Name:");
		
		Text text = new Text(container, SWT.BORDER);
		text.setLayoutData(gd);
	}
	
	private void createAddPlatformGroup(Composite container) {
		// Widgets to add a platform
		Label label = new Label(container, SWT.NULL);
		label.setText("Platform Name:");
		
		this.platformName = "";
		this.platformNameText = new Text(container, SWT.BORDER);
		this.platformNameText.setLayoutData(gd);
		this.platformNameText.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent arg0) {
				//if (dialogChanged("Hdl Platform", platformNameText.getText())) {
					platformName = platformNameText.getText();
				//}
			}
		});
		Label partLabel = new Label(container, SWT.NULL);
		partLabel.setText("Part Number:");
		
		this.partNumber = "";
		this.partNumberText = new Text(container, SWT.BORDER);
		this.partNumberText.setLayoutData(gd);
		this.partNumberText.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent arg0) {
				partNumber = partNumberText.getText();
			}
		});
		
		Label timeSeverLabel = new Label(container, SWT.NULL);
		timeSeverLabel.setText("Time Server Frequency:");
		
		this.timeServerFrequency = "";
		this.timeServerFrequencyText = new Text(container, SWT.BORDER);
		this.timeServerFrequencyText.setLayoutData(gd);
		this.timeServerFrequencyText.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent arg0) {
				timeServerFrequency = timeServerFrequencyText.getText();
			}
		});

	}
	
	private void createAddPrimitiveLibraryGroup() {
		// Widgets to add a primitive library
		Label label = new Label(container, SWT.NULL);
		label.setText("HDL Primitive Library Name:");
		
		this.primLibName = new WritableValue<String>("", String.class);
		this.primLibNameText = new Text(container, SWT.BORDER);
		this.primLibNameText.setLayoutData(gd);
		
		// Data bindings
		IObservableValue<Boolean> target = WidgetProperties.text(SWT.Modify).observe(this.primLibNameText);
		IObservableValue<?> model = PojoProperties.value(this.getClass(), "primLibName").observe(this);
		UpdateValueStrategy strategy = new UpdateValueStrategy();
		strategy.setBeforeSetValidator(new HdlPrimitiveLibraryValidator(this));
		ctx.bindValue(target, model, strategy, null);
	}
	
	private void createAddPrimitiveCore(Composite container) {
		// Widgets to add a primitive core
		Label label = new Label(container, SWT.NULL);
		label.setText("Primitive Core Name:");
		
		this.primCoreName = "";
		this.primCoreNameText = new Text(container, SWT.BORDER);
		this.primCoreNameText.setLayoutData(gd);
		this.primCoreNameText.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent arg0) {
				//if (dialogChanged("Hdl Primitive Core", primCoreNameText.getText())) {
					primCoreName = primCoreNameText.getText();
				//}
			}
		});
	}
	
	private void clearWidgets() {
		// Delete all widgets currently being displayed in wizard
		for (Control widget : container.getChildren()) {
			if (widget.equals(this.commandLabel) ||
					widget.equals(this.commandCombo) ||
					widget.equals(this.addToProjectLabel) ||
					widget.equals(this.addToProjectText) ||
					widget.equals(this.projectBrowseButton)) {
				continue;
			} else {
				widget.dispose();
			}
		}
		refreshStrings();
	}
	
	private void refreshStrings() {
		if (this.libraryOptionName != null) {
			setLibraryOptionName("");
		}
	}
	
	private void commandChanged(Composite container) {
		this.commandName = getCommandName();
		clearWidgets();
		switch (this.commandName) {
		case "Project":
			setDescription("Generate an empty project");
			if (this.addToProjectLabel != null) {
				this.addToProjectLabel.dispose();
			}
			if (this.addToProjectText != null) {
				this.addToProjectText.dispose();
			}
			if (this.projectBrowseButton != null) {
				this.projectBrowseButton.dispose();
			}
			createCreateProjectGroup();
			break;
		case "Application":
			setDescription("Generate a new application");
			if (this.addToProjectText == null) {
				createAddToProjectGroup();
			}
			createAddApplicationGroup();
			break;
		case "Library":
			setDescription("Generate a new library");
			if (this.addToProjectText == null) {
				createAddToProjectGroup();
			}
			createAddLibraryGroup(container);
			break;
		case "Component":
			setDescription("Generate a new component spec");
			if (this.addToProjectText == null) {
				createAddToProjectGroup();
			}
			createAddSpecGroup();
			break;
		case "Protocol":
			setDescription("Generate a new protocol spec");
			if (this.addToProjectText == null) {
				createAddToProjectGroup();
			}
			createAddProtocolGroup();
			break;
		case "Worker":
			setDescription("Generate a new worker");
			if (this.addToProjectText == null) {
				createAddToProjectGroup();
			}
			createAddWorkerGroup();
			break;
		case "HDL Assembly":
			setDescription("Generate a new HDL assembly");
			if (this.addToProjectText == null) {
				createAddToProjectGroup();
			}
			createAddAssemblyGroup();
			break;
		case "HDL Card":
			setDescription("Generate a new HDL card");
			createAddToProjectGroup();
			createAddCardGroup(container);
			break;
		case "HDL Slot":
			setDescription("Generate a new HDL slot");
			createAddToProjectGroup();
			createAddSlotGroup(container);
			break;
		case "HDL Device":
			setDescription("Generate a new HDL slot");
			createAddToProjectGroup();
			createAddDeviceGroup(container);
			break;
		case "Proxy":
			setDescription("Generate a new proxy worker");
			createAddToProjectGroup();
			createAddProxyGroup(container);
			break;
		case "HDL Platform":
			setDescription("Generate a new HDL platform");
			if (this.addToProjectText == null) {
				createAddToProjectGroup();
			}
			createAddPlatformGroup(container);
			break;
		case "HDL Primitive Library":
			setDescription("Generate a new HDL primitive library");
			if (this.addToProjectText == null) {
				createAddToProjectGroup();
			}
			createAddPrimitiveLibraryGroup();
			break;
		case "HDL Primitive Core":
			setDescription("Generate a new HDL primitive core");
			createAddToProjectGroup();
			createAddPrimitiveCore(container);
			break;
		default:
			break;
		}
		container.layout();
	}
	
	public void updateStatus(String message) {
		for (Object b : ctx.getBindings().toArray()) {
			((Binding)b).validateModelToTarget();
		}
		setErrorMessage(message);
		setPageComplete(message == null);
	}
	
	/****************************
	 * Setters and getters
	 ****************************/
	public String getCommandName() {
		return this.commandCombo.getItem(this.commandCombo.getSelectionIndex());
	}
	
	public void setCommandName(String value) {
		this.commandName = value;
	}
	
	public String getProjectName() {
		return (String)this.projectName.doGetValue();
	}
	
	public void setProjectName(String value) {
		this.projectName.doSetValue(value);
	}
	
	public String getProjectPrefix() {
		return (String)this.projectPrefix.doGetValue();
	}
	
	public void setProjectPrefix(String value) {
		this.projectPrefix.doSetValue(value);
	}
	
//	public String getProjectPackage() {
//		return (String)this.projectPackage.doGetValue();
//	}
//	
//	public void setProjectPackage(String value) {
//		this.projectPackage.doSetValue(value);
//	}
	
	public String getApplicationName() {
		return (String)this.applicationName.doGetValue();
	}
	
	public void setApplicationName(String value) {
		this.applicationName.doSetValue(value);
	}
	
	public String getLibraryName() {
		return (String)this.libraryName.doGetValue();
	}
	
	public void setLibraryName(String value) {
		this.libraryName.doSetValue(value);
	}
	
	public String getSpecName() {
		return (String)this.specName.doGetValue();
	}
	
	public void setSpecName(String value) {
		this.specName.doSetValue(value);
	}
	
	public String getAddToType() {
		return (String)this.addToType.doGetValue();
	}
	
	public void setAddToType(String value) {
		if (value.equals("topLevel")) {
			removeLibraryOptionGroup();
		} else if (value.equals("library")) {
			createLibraryOptionGroup();
		}
		container.layout();
		this.addToType.doSetValue(value);
	}
	
	public boolean getIsXMLApp() {
		return this.isXMLApp;
	}
	
	public void setIsXMLApp(boolean value) {
		this.isXMLApp = value;
	}
	
	public String getProtocolName() {
		return (String)this.protocolName.doGetValue();
	}
	
	public void setProtocolName(String value) {
		this.protocolName.doSetValue(value);
	}
	
	public String getWorkerName() {
		return this.workerName;
	}
	
	public String getAssemblyName() {
		return (String)this.assemblyName.doGetValue();
	}
	
	public void setAssemblyName(String value) {
		this.assemblyName.doSetValue(value);
	}
	
	public String getCardName() {
		return this.cardName;
	}
	
	public String getSlotName() {
		return this.slotName;
	}
	
	public String getDeviceName() {
		return this.deviceName;
	}
	
	public String getProxyName() {
		return this.proxyName;
	}
	
	public String getPlatformName() {
		return this.platformName;
	}
	
	public String getPrimLibName() {
		return (String)this.primLibName.doGetValue();
	}
	
	public void setPrimLibName(String value) {
		this.primLibName.doSetValue(value);
	}
	
	public String getPrimCoreName() {
		return this.primCoreName;
	}
	
	public String getLibraryOptionName() {
		return (String)this.libraryOptionName.doGetValue();
	}
	
	public void setLibraryOptionName(String value) {
		if (getCommandName().equals("Worker")) {
			//updateWorkerSpecCombo(value, false);
		}
		this.libraryOptionName.doSetValue(value);
	}
	
	public String getAddToProject() {
		return (String)this.addToProject.doGetValue();
	}
	
	public void setAddToProject(String value) {
		this.addToProject.doSetValue(value);
		if (getCommandName().equals("Worker")) {
			//updateWorkerSpecCombo(value, true);
		}
		updateLibraryOptions(value);
	}
	
	public String getWorkerSpec() {
		return this.workerSpec;
	}
	
	public String getWorkerModel() {
		return this.workerModel;
	}
	
	public String getWorkerLang() {
		return this.workerLang;
	}
	
	public String getPartNumber() {
		return partNumber;
	}

	public String getTimeServerFrequency() {
		return timeServerFrequency;
	}

	public Text getTimeServerFrequencyText() {
		return timeServerFrequencyText;
	}

	public void setPartNumberText(Text partNumberText) {
		this.partNumberText = partNumberText;
	}
	public String[] getProjectDependencies() {
		String[] retval = new String[this.depsList.size()];
		retval = this.depsList.toArray(retval);
		return retval;
	}

}