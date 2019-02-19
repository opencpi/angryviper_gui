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

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import av.proj.ide.internal.AngryViperAssetService;
import av.proj.ide.internal.AssetModelData;
import av.proj.ide.internal.CreateAssetFields;
import av.proj.ide.internal.OpenCPICategory;
import av.proj.ide.internal.OpencpiEnvService;
import av.proj.ide.wizards.internal.WizardInputConverter;

public class AssetPageWidgets {
	Composite container;
	// general across all assets except projects and unit tests:
	Label assetType;
	Combo assetSelection;
	String selectedAsset;
	
	Label  assetLabel;
	Text assetName;
	
	Label projectLabel;
	Combo projectCombo = null;
	
	public AssetPageWidgets (Composite container) {
		this.container = container;
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

	/****************************
	 *    Setters and getters
	 ****************************/
	String getAssetSelection() {
		return assetSelection.getItem(assetSelection.getSelectionIndex());
	}

	String getProjectName() {
		return projectCombo.getItem(projectCombo.getSelectionIndex());
	}

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
	
	String getProjectSelection() {
		if(projectCombo != null) {
			return projectCombo.getItem(projectCombo.getSelectionIndex());
		}
		else return null;
	}

	String getDestinationProject() {
		return projectCombo.getItem(projectCombo.getSelectionIndex());
	}

	String getAssetName() {
		return assetName.getText();
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
	}
	
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
		
	}
	
	void addNewProjectGroup(int inputWidth, Collection<String> depsList) {
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
	}

	void addLibAndSpecInputs(int inputWidth) {
		addLibraryDropdown(inputWidth);
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
	
	void loadProjectCombo(Collection<AssetModelData> projects, String initialProjectSelected) {
		int idx = 0;
		int selIdx = 0;
		for(AssetModelData project : projects) {
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
	
	void loadLibraryOptions(Collection<String> libraryOptions, String initialLib) {
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

	
	String loadSpecCombo (Collection<String> comps, String initialSpec) {
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
		else if (comps.size() == 1) {
			specCombo.select(0);
		}
		return null;
		
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

	public void addAppInputs(int width) {
		Label label = new Label(container, SWT.NULL);
		label.setText("XML Only App");
		GridData gd = new GridData(SWT.END, SWT.CENTER, false, false);
		label.setLayoutData(gd);
		
		xmlApp = new Button(container, SWT.CHECK);
		gd = new GridData(SWT.BEGINNING, SWT.CENTER, true, false);
		gd.widthHint = width;
		xmlApp.setLayoutData(gd);
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
}
