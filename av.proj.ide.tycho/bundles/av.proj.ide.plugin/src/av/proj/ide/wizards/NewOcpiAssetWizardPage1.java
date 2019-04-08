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

package av.proj.ide.wizards;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import av.proj.ide.internal.AngryViperAsset;
import av.proj.ide.internal.AngryViperAssetService;
import av.proj.ide.internal.AngryViperProjectInfo;
import av.proj.ide.internal.AssetModelData;
import av.proj.ide.internal.CreateAssetFields;
import av.proj.ide.internal.OpenCPICategory;
import av.proj.ide.internal.OpencpiEnvService;
import av.proj.ide.wizards.internal.WizardInputConverter;


public class NewOcpiAssetWizardPage1 extends WizardPage {
	Composite container;
	

	final String[] modelOptions = {"RCC", "HDL"};
	final String[] hdlLangOptions = {"VHDL"};
	final String[] rccLangOptions = {"C++", "C"};
	
	final String[] assetOptions = {"Project", "Library", "Component",
			                               "Worker", "Protocol","Application",
			                               "HDL Assembly", "HDL Primitive Library",
                                             "HDL Platform", "Unit Test"
                                            };

	
	// Services for the OpenCPI Project view to relate
	// initial selection info to the wizard.
	private OpenCPICategory presentInitialWizard = null;
	private String initialLib  = null;
	private String initialSpec  = null;
	private String initialProjectSelected = null;
	//private String initialProjectName = null;
	private OpenCPICategory anticipatedSpecLocation = null;
	
	private AngryViperAsset initialAssetSelection = null;
	
	// Used when brought up from eclipse project explorer.
	public void setInitialProjectName(String projectName) {
		//initialProjectName =  projectName;
		OpencpiEnvService srv = AngryViperAssetService.getInstance().getEnvironment();
		AngryViperProjectInfo info = srv.getProjectInfo(projectName);
		if(info != null) {
			initialProjectSelected = info.packageId;
		}
		else {
			initialProjectSelected = projectName;
		}
	}
	public void setInitialAssetWizard(OpenCPICategory selectedAssetType) {
		this.presentInitialWizard = selectedAssetType;
	}
	
	public void setInitialAssetSelection(AngryViperAsset initialSelection) {
		initialAssetSelection = initialSelection;
		initialProjectSelected = initialSelection.projectLocation.packageId;
		switch(initialSelection.category) {
		case cards:
			break;
		case component:
		case protocol:
			initialSpec = initialSelection.assetName;
			AngryViperAsset parent = initialSelection.parent;
			if(parent.category == OpenCPICategory.topLevelSpecs) {
				anticipatedSpecLocation = OpenCPICategory.topLevelSpecs;
			}
			else {
				// It's a library component.
				initialLib = parent.parent.assetName;
			}
			
			break;
		case specs:
		case test:
		case worker:
			initialLib = initialAssetSelection.parent.assetName;
			break;
		case componentsLibrary:
		case library:
			initialLib = initialAssetSelection.assetName;
			break;
		case devices:
			break;
		case platform:
			break;
		case topLevelSpecs:
			anticipatedSpecLocation = initialSelection.category;
			break;
		default:
			break;
		
		}
	}

	// general across all assets except projects and unit tests:
	Label assetType;
	Combo assetSelection;
	String selectedAsset;
	
	Label  assetLabel;
	Text assetName;
	
	Label projectLabel;
	Combo projectCombo = null;

	void setProjectSelection(String projectName) {
		
		String[] projects = projectCombo.getItems();
		int idx = 0;
		for(String project : projects) {
			if(project.equals(projectName)) {
				projectCombo.select(idx);
				break;
			}
			idx++;
		}
	}
	
	void loadProjectCombo() {
		Map<String, AssetModelData> projects = AngryViperAssetService.getInstance().getWorkspaceProjects();
		
		int idx = 0;
		int selIdx = 0;
		for(AssetModelData project : projects.values()) {
			String name = project.getAsset().qualifiedName;
			projectCombo.add(name);
			if(name.equals(initialProjectSelected)) {
				selIdx = idx;
			}
			idx++;
		}
		if(projects.size() != 0)
			projectCombo.select(selIdx);
	}
	
	String getProjectSelection() {
		if(projectCombo != null) {
			return projectCombo.getItem(projectCombo.getSelectionIndex());
		}
		else return null;
	}

	// Project inputs are unique to the Projects
	Text projectName;
	Text packagePrefix;
	Text packageName;
	Set<String> depsList;
	
	// Applications
	Button xmlApp;
	Button[] projectDepButtons;
	
	// Specs and protocols
	List<String> libraryOptions;
	Combo libraryCombo;
	Combo specCombo;
	Button topSpecsButton;
	Button libButton;
	
	// Workers
	Combo modelCombo;
	Combo languageCombo;

	// HDL Platform 
	Text partNumber;
	Text timeServerFreq;
	
	public NewOcpiAssetWizardPage1(NewOcpiAssetWizard wizard, ISelection selection) {
		super("wizardPage");
		setTitle("Create a new OpenCPI Asset");
		setDescription("Select the asset type from the drop down and complete the form.");
		//ctx = new DataBindingContext();
		libraryOptions = new ArrayList<String>();
	}

	// Flow of control:
	// 1. commands/NewAssetHandler instantiates NewOcpiAssetWizard then sets the Project.
	//    If not project was selected it is an empty String.
	// 2. Next WizardDialog.createContents is called (eclipse wizard code, this calls 
	//    addPages() and the screen is instantiated. The only selection in the initial
	//    screen is the assetType selection dropdown.
	// 3. User selects an asset.
	
	public void createControl(Composite parent) {
		container = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout(2, false);
		layout.verticalSpacing = 9;
		container.setLayout(layout);
		
		int width = 350;
		//width = new PixelConverter(assetSelection).convertWidthInCharsToPixels(25);
		
		// +++++++++++++++++
		assetType = new Label(container, SWT.NULL);
		assetType.setText("Asset Type:");
		GridData gd = new GridData(SWT.END, SWT.CENTER, false, false);
		assetType.setLayoutData(gd);
				
		assetSelection = new Combo(container, SWT.BORDER | SWT.READ_ONLY);
		gd = new GridData(SWT.BEGINNING, SWT.CENTER, true, false);
		gd.widthHint = width;
		assetSelection.setLayoutData(gd);
		
		loadAssetTypes();
		if(presentInitialWizard != null) {
			layoutPanel(presentInitialWizard);
			setAssetSelection(presentInitialWizard);
		}
		
		assetSelection.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				commandChanged(container);
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
			}
		});
		
		setControl(container);
		container.layout();
	}
	
	String helpMessage = null;
	
	@Override
	public void performHelp() {
		if(helpMessage != null) {
			MessageDialog.openInformation(Display.getDefault().getActiveShell(), "Wizard Help", helpMessage);			
		}
	}

	private void setAssetSelection(OpenCPICategory cat) {
		String selection = null;
		switch(cat) {
		case application:
			selection = "Application";
			break;
		case assembly:
			selection = "HDL Assembly";
			break;
		case component:
			selection = "Component";
			break;
		case platform:
			selection = "HDL Platform";
			break;
		case primitive:
			selection = "HDL Primitive Library";
			break;
		case protocol:
			selection = "Protocol";
			break;
		case worker:
			selection = "Worker";
			break;
		case test:
			selection = "Unit Test";
			break;
		default:
			break;
		}
		if(selection != null) {
			int idx = assetSelection.indexOf(selection);
			assetSelection.select(idx);
		}
		
	}

	private void loadAssetTypes() {
		for(String asset : assetOptions) {
			assetSelection.add(asset);
		}
	}
	
	// S E T    U P    A S S E T     S C R E E N
	class MyPoint {
		int width;
		int height;
		boolean resize = false;
	}
	
	private MyPoint layoutPanel(String selectedAsset) {
		OpenCPICategory initialAsset = OpenCPICategory.getCategory(selectedAsset);
		return layoutPanel(initialAsset);
	}
	
	private static String stdHelpMessage = "Create the framework asset and associated framework directory and files. If applicable the asset XML file is created and opened in the respective asset editor.";

	// Easier to track the selection by the enum.
	private OpenCPICategory currentAsset = null;
	
	private MyPoint layoutPanel(OpenCPICategory assetCatgory) {
		currentAsset = assetCatgory;
		int width = 350;
		GridData gd;
		MyPoint dimension = new MyPoint();
		// Basic asset dimensions
		dimension.width = 550;
		dimension.height = 500;
		
		boolean needsBasePanel = (projectCombo == null || assetName == null || assetName.isDisposed() );
		boolean initialFormComplete = false;
		if(! needsBasePanel) {
			int len = getAssetName().length();
			if(len > 2) {
				initialFormComplete = true;
			}
		}
		
		clearStatus();
		setMessage(null);
		helpMessage = stdHelpMessage;
		String status = null;
		
		switch (assetCatgory) {
		
		case project:
			StringBuilder sb = new StringBuilder();
			sb.append("OpenCPI projects are registered with a Package-ID that fully identifies the project. ");
			sb.append("The project Package-ID is used to reference a project as a project dependency and it ");
			sb.append("becomes a part of lower level Package-Ids that identify lower level project assets.\n\n");
			
			sb.append("The project Package-ID is represented as: package-prefix.package-name. ");
			sb.append("The default project Package-ID is \"local.<project name>\".");
			
			clearAllAssetWidgets();
			setDescription("Add a new Project");
			setMessage("Create and register a new OpenCPI project. Press help (\"?\" below) for more information.", IMessageProvider.INFORMATION);
			helpMessage = sb.toString();
			addNewProjectGroup(width);
			
			dimension.resize = true;
			dimension.width = 650;
			dimension.height = 550;
			initialFormComplete = true;
			break;
		
		case application:
			clearAccessoryWidgets();
			if(needsBasePanel) {
				addBasicAssetInputs(width);
			}
			
			setDescription("Add a new Application");
			assetLabel.setText("Application Name:");
			
			Label label = new Label(container, SWT.NULL);
			label.setText("XML Only App");
			gd = new GridData(SWT.END, SWT.CENTER, false, false);
			label.setLayoutData(gd);
			
			xmlApp = new Button(container, SWT.CHECK);
			gd = new GridData(SWT.BEGINNING, SWT.CENTER, true, false);
			gd.widthHint = width;
			xmlApp.setLayoutData(gd);
			break;
			
		case library:
			clearAccessoryWidgets();
			if(needsBasePanel) {
				addBasicAssetInputs(width);
			}
			
			setDescription("Add a new library");
			assetLabel.setText("Library Name:");
			if(libraryOptions == null || libraryOptions.isEmpty()) {
				assetName.setText("components");
				initialFormComplete = true;
			}
			// This project already has a library, this is an additional
			// lib.  Leave the input blank.
			break;
			
		case assembly:
			clearAccessoryWidgets();
			if(needsBasePanel) {
				addBasicAssetInputs(width);
			}
			
			setDescription("Add a new HDL Assembly");
			assetLabel.setText("Assembly Name:");
			break;
			
		case platform:
			clearAccessoryWidgets();
			if(needsBasePanel) {
				addBasicAssetInputs(width);
			}
			
			setDescription("Generate a new HDL platfrom");
			assetLabel.setText("HDL Platform Name:");
		
			addHDLPlatfromGroup(width);
			dimension.height = 500;
			dimension.width  = 620;
			dimension.resize = true;
	
			break;
			
		case primitive:
			clearAccessoryWidgets();
			if(needsBasePanel) {
				addBasicAssetInputs(width);
			}
			
			setDescription("Add a new HDL Primitive Library");
			assetLabel.setText("Primitive Lib Name:");
			break;
			
		case component:
			clearAccessoryWidgets();
			if(needsBasePanel) {
				addBasicAssetInputs(width);
			}
			
			setDescription("Add a new Component");
			assetLabel.setText("Component Name:");
			status = loadComponentInputSelections(width);
			
			if(status != null) {
				updateStatus(status);
			}
			dimension.height = 500;
			dimension.resize = true;
			break;
			
		case protocol:
			clearAccessoryWidgets();
			if(needsBasePanel) {
				addBasicAssetInputs(width);
			}
			
			setDescription("Add a new Protocol");
			assetLabel.setText("Protocol Name:");
			status = loadComponentInputSelections(width);

			if(status != null) {
				updateStatus(status);
			}
			dimension.height = 500;
			dimension.resize = true;
			break;
			
		case worker:
			clearAccessoryWidgets();
			if(needsBasePanel) {
				addBasicAssetInputs(width);
			}
			
			setDescription("Create a new Worker");
			assetLabel.setText("Worker Name:");
			addWorkerInputs(width);
			addLibraryDropdown(width);
			addSpecDropdown(550);
			
			status = loadWorkerInputSelections();
			if(status != null) {
				updateStatus(status);
				initialFormComplete = false;
			}
			else {
				// Check project registration.  
				String projectName = getProjectSelection();
				AngryViperProjectInfo proj = AngryViperAssetService.getInstance().getEnvironment().getProjectInfo(projectName);
				if(proj != null) {
					if( ! proj.isRegistered()) {
						setMessage("Warning - this project is not registered.");
					}
				}
				
			}
			dimension.height = 600;
			dimension.width  = 750;
			dimension.resize = true;
			
			break;

		case test:
			clearAccessoryWidgets();
			if(assetName != null) {
				assetLabel.dispose();
				assetName.dispose();
				assetName = null;
			}
			setDescription("Add a Unit Test");
			addUnitTestInputs(width);
			
			status = loadTestInputSelections();
			if(status != null) {
				updateStatus(status);
				initialFormComplete = false;
			}
			else {
				initialFormComplete = true;
			}
			dimension.height = 450;
			dimension.resize = true;
			break;

		default:
			break;
		}
		this.setPageComplete(initialFormComplete);
		return dimension;
	}

	void loadLibraryOptions() {
		int idx = 0;
		int libIdx = -1;
		if(libraryOptions.size() == 0) return;
		for (String s : libraryOptions) {
			this.libraryCombo.add(s);
			if(s.equals(initialLib)) {
				libIdx = idx;
				break;
			}
			idx++;
		}
		if(libIdx != -1) {
			this.libraryCombo.select(libIdx);
		}
		else {
			this.libraryCombo.select(0);
		}
	}
	
	void reloadLibOptions() {
		boolean libraryAsset = false;
		switch(currentAsset) {
		default:
			break;
		case component:
		case protocol:
			// it didn't work well to try and update the 
			// destination radio group when the project selection
			// changes.  Just go ahead and reconstruct the wizard.
			commandChanged(container);
			break;
			
		case worker:
		case test:
			libraryAsset = true;
			break;
		}
		
		if(libraryAsset) {
			libraryCombo.removeAll();
			if(libraryOptions.size() == 0) {
				updateStatus("A components library must be created before creating this asset.");
			}
			else {
				loadLibraryOptions();
			}
		}
	}
	
	// The spec combo box needs to reflect the correct specs list for
	// workers and unit tests. This is a consideration when the project
	// or library selection changes.
	String updateSpecCombo(String project) {
		if(specCombo == null || specCombo.isDisposed())
			return null;
		
		specCombo.removeAll();
		
		switch(currentAsset) {
		default:
			break;
		case worker:
			OpencpiEnvService srv = AngryViperAssetService.getInstance().getEnvironment();
			Collection<String> specs = srv.getComponentsAvailableToProject(project);
			return loadSpecCombo(specs);
		case test:
			 String selectedLibrary = libraryCombo.getText();
			String projectName = getProjectSelection();
			Set<String> comps = AngryViperAssetService.getInstance().getComponentsInLibrary(projectName, selectedLibrary);
			return loadSpecCombo(comps);
		}
		
		return null;
	}
	
	
	String loadComponentInputSelections(int inputWidth) {
		if(libraryOptions.size() == 0) {
			addToplevelSpecsDefault(inputWidth);
			return null;
		}
		
		if(libraryOptions.size() > 1) {
			addRadioGroupForLibs(inputWidth);
			addLibraryDropdown(inputWidth);
			loadLibraryOptions();
		}
		else {
			// Single lib project
			String lib = libraryOptions.get(0);
			addRadioGroupForSingleLib(inputWidth, lib);
			if(OpenCPICategory.topLevelSpecs == anticipatedSpecLocation) {
				topSpecsButton.setSelection(true);
				libButton.setSelection(false);
			}
		}
		return null;
	}

	String loadTestInputSelections() {
		String projectName = getProjectSelection();
		if(libraryOptions.size() == 0) {
			return "A components library must be created before creating this asset.";
		}
		loadLibraryOptions();
		int idx = libraryCombo.getSelectionIndex();
		if(idx > -1) {
			String selectedLibrary = libraryCombo.getItem(idx);
			Set<String> comps = AngryViperAssetService.getInstance().getComponentsInLibrary(projectName, selectedLibrary);
			return loadSpecCombo(comps);
		}
		
		return null;
	}
	
	String loadSpecCombo (Collection<String> comps) {
		if(comps == null || comps.isEmpty()) {
			return "There are no available components in this library.";
		}
		String specName = null;
		boolean setSpec = false;
		if(initialSpec != null) {
			OpencpiEnvService srv = AngryViperAssetService.getInstance().getEnvironment();
			specName = srv.getComponentName(initialSpec);
			setSpec = true;
		}
		int idx = 0;
		int specIdx = -1;
		for(String comp : comps) {
			specCombo.add(comp);
			if(setSpec && comp.contains(specName)) {
				specIdx = idx;
				break;
			}
			idx ++;
		}
		if( specIdx != -1) {
			specCombo.select(specIdx);
		}
		else if (comps.size() >= 1) {
			specCombo.select(0);
		}
		return null;
		
	}
	
	String loadWorkerInputSelections() {
		String projectName = getProjectSelection();
		if(libraryOptions.size() == 0) {
			return "A components library must be created before creating this asset.";
		}
		loadLibraryOptions();
		
		OpencpiEnvService srv = AngryViperAssetService.getInstance().getEnvironment();
		Collection<String> specs = srv.getComponentsAvailableToProject(projectName);
		if(specs == null) {
			return"There are no available components for this project.";
		}
		loadSpecCombo(specs);
		return null;
	}

	// *********************************************
	//                Form Layouts
	// *********************************************
	
	private boolean initialCommand = true;

	//this function detect the asset selection from Asset Type drop down and
	//set the layout panel.
	private void commandChanged(Composite container) {
		
		selectedAsset = getAssetSelection();
		MyPoint p;
		if(initialCommand) {
			p = layoutPanel(selectedAsset);
			p.resize = true;
		} else {
			p = layoutPanel(selectedAsset);
		}
		
		initialCommand = false;
		if(p.resize) {
			getShell().setSize(p.width, p.height);
		}
		container.layout();
		
	}
	
	void addBasicAssetInputs(int inputWidth) {
		// +++++++++++++++++
		assetLabel = new Label(container, SWT.NULL);
		assetLabel.setText("Asset Name");
		GridData gd = new GridData(SWT.END, SWT.CENTER, false, false);
		assetLabel.setLayoutData(gd);
		assetLabel.setToolTipText("All assets must be named.");
		
		assetName = new Text(container, SWT.NONE);
		gd = new GridData(SWT.BEGINNING, SWT.CENTER, true, false);
		gd.widthHint = inputWidth -8;
		assetName.setLayoutData(gd);
		addProjectInput(inputWidth);
		assetName.setMessage("required");
		
		// Add basic asset Name validation.
		// TODO: Need a way to control setting
		// page complete when other inputs require
		// validation.
		NewOcpiAssetWizardPage1 me = this;
		assetName.addModifyListener( new ModifyListener() {
			boolean displayedMessage = false;
			@Override
			public void modifyText(ModifyEvent e) {
		       String currentText = ((Text)e.widget).getText();
				if (currentText.length() > 2) {
					boolean bad = p.matcher(currentText).find();
					if(bad) {
						me.updateStatus("Invalid characters used in asset name.");
						displayedMessage = true;
					}
					else {
						me.setPageComplete(true);
						if(displayedMessage) {
							setErrorMessage(null);
							displayedMessage = false;
						}
					}
				}
				else {
					me.setPageComplete(false);
				}
			}
		});
	}
	
	// Regex notes: \p{Punct} - build it for all punctuation characters (backslash is escaped),
	// and not underscore, dash or period; dash and period escaped.
	private Pattern p = Pattern.compile("[\\p{Punct} && [^_\\-]]");
	
	void addProjectInput(int inputWidth) {
		
		if(projectCombo != null) {
			return;
		}
		projectLabel = new Label(container, SWT.NULL);
		projectLabel.setText("Add to Project:");
		GridData gd = new GridData(SWT.END, SWT.CENTER, false, false);
		projectLabel.setLayoutData(gd);
		
		projectCombo = new Combo(container, SWT.BORDER);
		gd = new GridData(SWT.BEGINNING, SWT.CENTER, true, false);
		gd.widthHint = inputWidth;
		projectCombo.setLayoutData(gd);
		
		loadProjectCombo();
		getLibraryOptions();

		projectCombo.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				
				// The destination project changed.
				setErrorMessage(null);
				String[] priorOptions = libraryOptions.toArray(new String[libraryOptions.size()]);
				getLibraryOptions();
				int delta = priorOptions.length - libraryOptions.size();
				boolean notEqual = false;
				// Make sure they are the same
				if(delta == 0) {
					for(String lib : priorOptions) {
						if( ! libraryOptions.contains(lib) ) {
							notEqual = true;
							break;
						}
					}
					if(notEqual) {
						reloadLibOptions();
					}
				}
				else {
					reloadLibOptions();
				}
				initialProjectSelected = getProjectSelection();
				
				// If a worker or a unit test is selected, must update
				// the spec list.
				String status = updateSpecCombo(initialProjectSelected);
				if(status != null) {
					updateStatus(status);
				}
			}
			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {/* DO NOTHING*/}
		});	
	}
	
	void addNewProjectGroup(int inputWidth) {
		//+++++++++++++++++++++++++++++++++++++++++++
		Label label = new Label(container, SWT.NONE);
		label.setText("Project Name");
		GridData gd = new GridData(SWT.END, SWT.CENTER, false, false);
		label.setLayoutData(gd);
		
		projectName = new Text(container, SWT.NONE);
		gd = new GridData(SWT.BEGINNING, SWT.CENTER, true, false);
		gd.widthHint = inputWidth -8;
		projectName.setLayoutData(gd);
		projectName.setMessage("required (results as the Project Directory)");
		
		//+++++++++++++++++++++++++++++++++++++++++++
		String tip =
		"Part of the project Package-ID; provide your own package-prefix here. Press help (\"?\"  below) for more information.";
		
		label = new Label(container, SWT.NONE);
		label.setText("Package Prefix");
		gd = new GridData(SWT.END, SWT.CENTER, false, false);
		label.setLayoutData(gd);
		label.setToolTipText(tip);
		
		packagePrefix = new Text(container, SWT.NONE);
		gd = new GridData(SWT.BEGINNING, SWT.CENTER, true, false);
		gd.widthHint = inputWidth -8;
		packagePrefix.setLayoutData(gd);
		packagePrefix.setMessage("optional (Default is \"local\")");
		
		//+++++++++++++++++++++++++++++++++++++++++++
		tip = "Part of the project Package-ID; provide your own package-name here. Press help (\"?\"  below) for more information.";
		label = new Label(container, SWT.NONE);
		label.setText("Package Name");
		gd = new GridData(SWT.END, SWT.CENTER, false, false);
		label.setLayoutData(gd);
		label.setToolTipText(tip);
		
		packageName = new Text(container, SWT.NONE);
		gd = new GridData(SWT.BEGINNING, SWT.CENTER, true, false);
		gd.widthHint = inputWidth -8;
		packageName.setLayoutData(gd);
		packageName.setMessage("optional (Default is the project name)");
		
		//+++++++++++++++++++++++++++++++++++++++++++
		label = new Label(container, SWT.NONE);
		label.setText("Project Dependencies");
		gd = new GridData(SWT.END, SWT.CENTER, false, false);
		label.setLayoutData(gd);
		
		Composite dependencyList = new Composite(container, SWT.BORDER);
		gd = new GridData(SWT.BEGINNING, SWT.CENTER, true, false);
		gd.widthHint = inputWidth;
		dependencyList.setLayoutData(gd);
		GridLayout layout = new GridLayout(2, false);
		dependencyList.setLayout(layout);
		
		if (depsList == null) {
			OpencpiEnvService srv = AngryViperAssetService.getInstance().getEnvironment();
			depsList = srv.getRegisteredProjectsLessCore();
		}
		
		projectDepButtons = new Button[depsList.size()];
		int i = 0;
		for(String projectName : depsList) {
			Button b = new Button(dependencyList, SWT.CHECK);
			projectDepButtons[i++] = b;
			
			label = new Label(dependencyList, SWT.NONE);
			label.setText(projectName);
			gd = new GridData(SWT.BEGINNING, SWT.CENTER, true, false);
			gd.widthHint = 300;
			label.setLayoutData(gd);
			b.setData(projectName);
		}
	}
	void addToplevelSpecsDefault(int inputWidth) {
		Label label = new Label(container, SWT.NONE);
		label.setText("Location:");
		GridData gd = new GridData(SWT.END, SWT.CENTER, false, false);
		label.setLayoutData(gd);
		
		topSpecsButton = new Button(container, SWT.CHECK);
		topSpecsButton.setText("Top Level Specs");
		topSpecsButton.setSelection(true);
	}
	
	
	void addRadioGroupForSingleLib(int inputWidth, String selectedLib) {
		Label label = new Label(container, SWT.NONE);
		label.setText("Location:");
		GridData gd = new GridData(SWT.END, SWT.CENTER, false, false);
		label.setLayoutData(gd);
		
		Group group = new Group(container, SWT.NONE);
		group.setLayout(new RowLayout());
		gd = new GridData(SWT.BEGINNING, SWT.CENTER, true, false);
		gd.widthHint = inputWidth;
		group.setLayoutData(gd);
		
		topSpecsButton = new Button(group, SWT.RADIO);
		topSpecsButton.setText("Top Level Specs");
		libButton = new Button(group, SWT.RADIO);
		libButton.setText(selectedLib + " Library");
		libButton.setSelection(true);
	}

	void addRadioGroupForLibs(int inputWidth) {
		Label label = new Label(container, SWT.NONE);
		label.setText("Location:");
		GridData gd = new GridData(SWT.END, SWT.CENTER, false, false);
		label.setLayoutData(gd);
		
		Group group = new Group(container, SWT.NONE);
		group.setLayout(new RowLayout());
		gd = new GridData(SWT.BEGINNING, SWT.CENTER, true, false);
		gd.widthHint = inputWidth;
		group.setLayoutData(gd);
		
		topSpecsButton = new Button(group, SWT.RADIO);
		topSpecsButton.setText("Top Level Specs");
		libButton = new Button(group, SWT.RADIO);
		libButton.setText("Library");
		libButton.setSelection(true);
	}

	void addLibraryDropdown(int inputWidth) {
		Label label = new Label(container, SWT.NONE);
		label.setText("Add to Library");
		GridData gd = new GridData(SWT.END, SWT.CENTER, false, false);
		label.setLayoutData(gd);
		
		libraryCombo = new Combo(container, SWT.BORDER | SWT.READ_ONLY);
		
		gd = new GridData(SWT.BEGINNING, SWT.CENTER, true, false);
		gd.widthHint = inputWidth;
		libraryCombo.setLayoutData(gd);
		libraryCombo.addSelectionListener(new SelectionListener() {
		@Override
		public void widgetSelected(SelectionEvent arg0) {
			// Unit tests specs must be be updated if the library
			// changes. 
			if(currentAsset != OpenCPICategory.test) return;
			String project = getProjectSelection();
			String status = updateSpecCombo(project);
			if(status != null) {
				updateStatus(status);
			}
		}
		@Override
		public void widgetDefaultSelected(SelectionEvent arg0) {/* DO NOTHING*/}
		});	
		
	}
	
	void addWorkerInputs(int inputWidth) {
		Label label = new Label(container, SWT.NONE);
		label.setText("Model");
		GridData gd = new GridData(SWT.END, SWT.CENTER, false, false);
		label.setLayoutData(gd);
		
		modelCombo = new Combo(container, SWT.BORDER | SWT.READ_ONLY);
		
		gd = new GridData(SWT.BEGINNING, SWT.CENTER, true, false);
		gd.widthHint = inputWidth;
		modelCombo.setLayoutData(gd);
		
		label = new Label(container, SWT.NONE);
		label.setText("Language");
		gd = new GridData(SWT.END, SWT.CENTER, false, false);
		label.setLayoutData(gd);
		languageCombo = new Combo(container, SWT.BORDER | SWT.READ_ONLY);
		
		gd = new GridData(SWT.BEGINNING, SWT.CENTER, true, false);
		gd.widthHint = inputWidth;
		languageCombo.setLayoutData(gd);
		
		for(String option : modelOptions) {
			modelCombo.add(option);
		}
		modelCombo.select(0);
		for(String opt : rccLangOptions) {
			languageCombo.add(opt);
		}
		languageCombo.select(0);
		
		modelCombo.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				String selection = modelCombo.getItem(modelCombo.getSelectionIndex());
				languageCombo.removeAll();
				if("RCC".equals(selection)) {
					for(String opt : rccLangOptions) {
						languageCombo.add(opt);
					}
				}
				else {
					for(String opt : hdlLangOptions) {
						languageCombo.add(opt);
					}
				}
				languageCombo.select(0);
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
				
			}
		});
		
	}

	void addSpecDropdown(int inputWidth) {
		// +++++++++++++++++
		Label label = new Label(container, SWT.NULL);
		label.setText("Component Spec:");
		
		specCombo = new Combo(container, SWT.BORDER | SWT.READ_ONLY);
		
		GridData gd = new GridData(SWT.BEGINNING, SWT.CENTER, true, false);
		gd.widthHint = inputWidth;
		specCombo.setLayoutData(gd);
	}

	
	void addUnitTestInputs(int inputWidth) {
		
		addProjectInput(inputWidth); // gets the library option when loaded
		addLibraryDropdown(inputWidth);

		// +++++++++++++++++
		Label label = new Label(container, SWT.NULL);
		label.setText("Component Spec:");
		
		specCombo = new Combo(container, SWT.BORDER | SWT.READ_ONLY);
		
		GridData gd = new GridData(SWT.BEGINNING, SWT.CENTER, true, false);
		gd.widthHint = inputWidth;
		specCombo.setLayoutData(gd);
		
	}


	// *******************************************
	//             General Support Methods
	// *******************************************
	
	int getLibraryOptions() {
		
		String projectName = getProjectSelection();
		AngryViperAsset[] libraries = AngryViperAssetService.getInstance().getProjectLibraries(projectName);
		libraryOptions.clear();
		for(AngryViperAsset lib : libraries) {
			libraryOptions.add(lib.assetName);
		}
		return libraries.length;
	}
	
//	private void updateLibraryOptionCombo() {
//		if (this.libraryOptionNameCombo != null && ! this.libraryOptionNameCombo.isDisposed()) {
//			this.libraryOptionNameCombo.removeAll();
//			for (String s : libraryOptions) {
//				this.libraryOptionNameCombo.add(s);
//			}
//			this.libraryOptionNameCombo.select(0);
//		}
//		else {
//			createLibraryOptionGroup();
//		}
//	}
	void addHDLPlatfromGroup(int inputWidth){
		
		//+++++++++++++++++++++++++++++++++++++++++++
		Label label = new Label(container, SWT.NONE);
		label.setText("Part Number: ");
		GridData gd = new GridData(SWT.END, SWT.CENTER, false, false);
		label.setLayoutData(gd);
		
		partNumber = new Text(container, SWT.NONE);
		gd = new GridData(SWT.BEGINNING, SWT.CENTER, true, false);
		gd.widthHint = inputWidth -8;
		partNumber.setLayoutData(gd);
		
		//+++++++++++++++++++++++++++++++++++++++++++
		label = new Label(container, SWT.NONE);
		label.setText("Time Server Frequency: ");
		gd = new GridData(SWT.END, SWT.CENTER, false, false);
		label.setLayoutData(gd);
		
		timeServerFreq = new Text(container, SWT.NONE);
		gd = new GridData(SWT.BEGINNING, SWT.CENTER, true, false);
		gd.widthHint = inputWidth -8;
		timeServerFreq.setLayoutData(gd);
		
	}
	void clearAllAssetWidgets() {
		// Delete all widgets currently being displayed in wizard
		for (Control widget : container.getChildren()) {
			if (	widget.equals(assetType) ||
					widget.equals(assetSelection)) {
				continue;
			} else {
				widget.dispose();
			}
		}
		projectCombo = null;
		assetName = null;
	}
	
	void clearAccessoryWidgets() {
		
		for (Control widget : container.getChildren()) {
			if (	widget.equals(assetType) ||
					widget.equals(assetSelection) ||
					widget.equals(assetLabel) ||
					widget.equals(assetName) ||
					widget.equals(projectLabel) ||
					widget.equals(projectCombo)) {
				continue;
			} else {
				widget.dispose();
			}
		}
	}
	
	void clearStatus() {
		setErrorMessage(null);
		setPageComplete(false);
	}
	
	// validators use this.
	public void updateStatus(String message) {
//		for (Object b : ctx.getBindings().toArray()) {
//			((Binding)b).validateModelToTarget();
//		}
		setErrorMessage(message);
		setPageComplete(message == null);
	}
	
	/****************************
	 *    Setters and getters
	 ****************************/
	public String getAssetSelection() {
		return assetSelection.getItem(assetSelection.getSelectionIndex());
	}

	public String getProjectName() {
		return projectCombo.getItem(projectCombo.getSelectionIndex());
	}

	private WizardInputConverter converter = new WizardInputConverter();
	
	/***
	 * Primary interface to the page.  This returns the
	 * selections made by the user.
	 */
	public CreateAssetFields getUsersRequest() {
		String assetSelection = getAssetSelection();
		
		CreateAssetFields assetInputs = null;
		OpenCPICategory assetType = null;
		
		switch(assetSelection) {
		case "Application":
			assetType = OpenCPICategory.application;
			assetInputs = getBasicAssetInputs(getAssetName());
			boolean xmlonly = xmlApp.getSelection();
			if( ! xmlonly) {
				assetInputs.notXmlOnly();
			}
			break;
		case "Library":
			assetType = OpenCPICategory.library;
			assetInputs = getBasicAssetInputs(getAssetName());
			break;
		case "Component":
			assetType = OpenCPICategory.component;
			assetInputs = 
			 converter.getComponentInputs(getAssetName(), getDestinationProject(), libButton, libraryCombo, topSpecsButton.getSelection());
			break;
		case "Protocol":
			assetType = OpenCPICategory.protocol;
			assetInputs = 
			 converter.getComponentInputs(getAssetName(), getDestinationProject(), libButton, libraryCombo, topSpecsButton.getSelection());
			break;
		case "Worker":
			assetType = OpenCPICategory.worker;
			assetInputs = 
			 converter.getWorkerInputs(getAssetName(), getDestinationProject(),
					                   libraryCombo, specCombo, modelCombo, languageCombo);
			break;
		case "HDL Assembly":
			assetType = OpenCPICategory.assembly;
			assetInputs = getBasicAssetInputs(getAssetName());
			break;
		case "HDL Platform":
			assetType = OpenCPICategory.platform;
			assetInputs = 
			converter.getHdlPlatformInputs(getAssetName(),getDestinationProject(),
					                                     partNumber.getText(), timeServerFreq.getText());
			break;
		case "HDL Primitive Library":
			assetType = OpenCPICategory.primitive;
			assetInputs = getBasicAssetInputs(getAssetName());
			break;
		case "Project":
			assetType = OpenCPICategory.project;
			assetInputs = 
			 converter.getNewProjectInputs(projectName.getText(), packagePrefix.getText(), 
					                       packageName.getText(), projectDepButtons);
			 
			break;
		
		case "Unit Test":
			assetType = OpenCPICategory.test;
			assetInputs = 
			 converter.getUnitTestInputs(getDestinationProject(), libraryCombo, specCombo);
			break;
		default:
			break;
		}
		assetInputs.setType(assetType);
		return assetInputs;
	}

	private CreateAssetFields getBasicAssetInputs(String givenAssetName) {
		String destinationProject = getDestinationProject();
		return new CreateAssetFields(destinationProject, null, givenAssetName);
	}


	private String getDestinationProject() {
		return projectCombo.getItem(projectCombo.getSelectionIndex());
	}

	private String getAssetName() {
		return assetName.getText();
	}

}