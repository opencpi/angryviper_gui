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

package av.proj.ide.swt;

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.TreeItem;

import av.proj.ide.avps.internal.AvpsResourceManager;
import av.proj.ide.avps.internal.SelectionsInterface;
import av.proj.ide.avps.internal.TestMode;
import av.proj.ide.internal.AngryViperAssetService;
import av.proj.ide.internal.OcpidevVerb;

public class AddViewControls {
	
	public static void addProjectPanelControls(ProjectViewSwtDisplay projectDisplay) {
		
		projectDisplay.synchronizeButton.addListener(SWT.Selection, new Listener(){

			@Override
			public void handleEvent(Event arg0) {
				AngryViperAssetService.getInstance().synchronizeWithFileSystem();
			}
		}); 
	}
	
	public static void addOperationsPanelControls(MainOperationSwtDisplayV1 opsDisplay) {
		
		opsDisplay.buildSelectPanel.getHdlPlatformsWidgit().addSelectionListener(new SelectionAdapter() {
		public void widgetSelected(SelectionEvent event) {
			opsDisplay.buildTargetsChanged = true;
		}
		});
		opsDisplay.buildSelectPanel.getRccPlatformsWidgit().addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				opsDisplay.buildTargetsChanged = true;
			}
		});
		opsDisplay.buildSelectPanel.getHdlTargetsWidgit().addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				opsDisplay.buildTargetsChanged = true;
			}
		});
		
		
		opsDisplay.selectionPanel.buildButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				opsDisplay.doBuild(OcpidevVerb.build);
			}
		});	
		opsDisplay.selectionPanel.cleanButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				opsDisplay.doBuild(OcpidevVerb.clean);
			}
		});	
		
		opsDisplay.addSelectionsButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				List<SelectionsInterface> providers = AvpsResourceManager.getInstance().getSelectionProviders();
				for(SelectionsInterface provider : providers){
					TreeItem[] selections = provider.getSelections();
					opsDisplay.addSelections(selections);
				}
			}
		});	
		opsDisplay.removeSelectionsButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				opsDisplay.removeSelected();
			}
		});		
		opsDisplay.clearSelectionsButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				opsDisplay.clearEntries();
			}
		});		
	}

	public static void addTextPanelControls(MainOperationSwtDisplayV1 opsDisplay) {
		CentralPanel p = opsDisplay.selectionPanel;

		Button b = p.generate;
		b.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				opsDisplay.executeTests(TestMode.gen);
			}
		});		
		
		b = p.prepare;
		b.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				opsDisplay.executeTests(TestMode.prep);
			}
		});		
		b = p.run;
		b.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				opsDisplay.executeTests(TestMode.run);
			}
		});		

		b = p.verify;
		b.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				opsDisplay.executeTests(TestMode.verify);
			}
		});		
		b = p.view;
		b.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				opsDisplay.executeTests(TestMode.view);
			}
		});		
		b = p.genBuild;
		b.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				opsDisplay.executeTests(TestMode.gen_build);
			}
		});		
		b = p.prepRun;
		b.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				opsDisplay.executeTests(TestMode.prep_run);
			}
		});		
		b = p.prepRunVerify;
		b.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				opsDisplay.executeTests(TestMode.prep_run_verify);
			}
		});		

		b = p.runCln;
		b.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				opsDisplay.executeTests(TestMode.clean_run);
			}
		});		
		b = p.simCln;
		b.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				opsDisplay.executeTests(TestMode.clean_sim);
			}
		});		
		b = p.allCln;
		b.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				opsDisplay.executeTests(TestMode.clean_all);
			}
		});		

	}
}
