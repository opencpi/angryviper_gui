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

import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;

import av.proj.ide.internal.AssetDetails.AuthoringModel;
import av.proj.ide.internal.AngryViperAssetService;
import av.proj.ide.internal.CreateAssetFields;
import av.proj.ide.internal.CreateProjectFields;
import av.proj.ide.internal.CreateWorkerFields;
import av.proj.ide.internal.HdlPlatformFields;
import av.proj.ide.internal.OpenCPICategory;
import av.proj.ide.internal.OpencpiEnvService;
import av.proj.ide.internal.UiComponentSpec;

public class WizardInputConverter {
	

	public CreateAssetFields getHdlPlatformInputs(String givenAssetName, String destinationProject,
			                                      String partNumber, String timeServerFreq) {
	    HdlPlatformFields hdlPlatformInputs = 
	    	new HdlPlatformFields(destinationProject, null, givenAssetName, partNumber, timeServerFreq);
		return hdlPlatformInputs;
	}

	public CreateAssetFields getWorkerInputs(String givenAssetName, String destinationProject, 
			                                 Combo libraryCombo, Combo specCombo,
                                             Combo modelCombo, Combo languageCombo ) {
		String lib = libraryCombo.getItem(libraryCombo.getSelectionIndex());
		String model = modelCombo.getItem(modelCombo.getSelectionIndex());
		String lang = languageCombo.getItem(languageCombo.getSelectionIndex());
		AuthoringModel am;
		if(model.equals("RCC")) {
			am = AuthoringModel.RCC;
		}
		else {
			am = AuthoringModel.HDL;
		}
		String selectedSpec = specCombo.getItem(specCombo.getSelectionIndex());
		OpencpiEnvService srv = AngryViperAssetService.getInstance().getEnvironment();
		UiComponentSpec spec = srv.getUiSpecByDisplayName(selectedSpec);
		CreateWorkerFields wf = 
			new  CreateWorkerFields(destinationProject, null, givenAssetName, spec.getOwdReference(),am, lang);
		wf.setLibrary(lib);
		return wf;
	}

	public CreateAssetFields getComponentInputs(String givenAssetName, String destinationProject,
			                                    Button libButton, Combo libraryCombo, boolean topLevelSpec) {
		CreateAssetFields inputs = new CreateAssetFields(destinationProject, null, givenAssetName);
		if(topLevelSpec) {
			inputs.addToTopSpec();
		}
		else {
			// Library Selected
			if(libraryCombo != null && ! libraryCombo.isDisposed()) {
				String library = libraryCombo.getItem(libraryCombo.getSelectionIndex());
				inputs.setLibrary(library);
			}
			else {
				// Single lib project
				String[] split = libButton.getText().split(" ");
				if(split[0].startsWith("comp")) {
					inputs.setLibrary(OpenCPICategory.componentsLibrary.getFrameworkName());
				}
				else {
					inputs.setLibrary(split[0]);
				}
			}
		}
		return inputs;
	}

	public CreateAssetFields getUnitTestInputs(String destinationProject, Combo libraryCombo, Combo specCombo) {
		String library = libraryCombo.getItem(libraryCombo.getSelectionIndex());
		String specSelect = specCombo.getItem(specCombo.getSelectionIndex());

		OpencpiEnvService srv = AngryViperAssetService.getInstance().getEnvironment();
		String componentName = srv.getComponentName(specSelect);
		CreateAssetFields inputs = 
				new CreateAssetFields(OpenCPICategory.test, destinationProject, null, 
						componentName);
		inputs.setType(OpenCPICategory.test);
		inputs.setLibrary(library);
		return inputs;
	}

	public CreateAssetFields getNewProjectInputs(String newProjectName, String newPackagePrefix,
			                                     String newPackageName, Button[] projectDepButtons) {
		CreateProjectFields projectInputs = new CreateProjectFields(newProjectName, null, newPackageName, newPackagePrefix);
		for(int i=0; i<projectDepButtons.length; i++) {
			Button b = projectDepButtons[i];
			if(b.getSelection()) {
				projectInputs.addDependency((String)b.getData());
			}
		}
		return projectInputs;
	}
}
