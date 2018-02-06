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
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.TreeItem;

import av.proj.ide.avps.internal.AngryViperAssetService;
import av.proj.ide.avps.internal.AvpsResourceManager;
import av.proj.ide.avps.internal.ExecutionAsset.CommandVerb;
import av.proj.ide.avps.internal.SelectionsInterface;

public class AddViewControls {
	
	public static void addProjectPanelControls(ProjectViewSwtDisplay projectDisplay) {
		
		projectDisplay.synchronizeButton.addListener(SWT.Selection, new Listener(){

			@Override
			public void handleEvent(Event arg0) {
				TreeItem[] selections = projectDisplay.projectsTree.getSelection();
				AngryViperAssetService.getInstance().synchronizeWithFileSystem(selections);
			}
		}); 
	}
	
	public static void addOperationsPanelControls(MainOperationSwtDisplayV1 opsDisplay) {
		
		opsDisplay.buildSelectPanel.getHdlPlatformsWidgit().addSelectionListener(new SelectionAdapter() {
		public void widgetSelected(SelectionEvent event) {
			opsDisplay.buildTargetsChanged = true;
			opsDisplay.selectionPanel.text.setText("");
		}
		});
		opsDisplay.buildSelectPanel.getRccPlatformsWidgit().addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				opsDisplay.buildTargetsChanged = true;
				opsDisplay.selectionPanel.text.setText("");
			}
		});
		opsDisplay.buildSelectPanel.getHdlTargetsWidgit().addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				opsDisplay.buildTargetsChanged = true;
				opsDisplay.selectionPanel.text.setText("");
			}
		});
		
		opsDisplay.selectionPanel.addSelectionsButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				List<SelectionsInterface> providers = AvpsResourceManager.getInstance().getSelectionProviders();
				for(SelectionsInterface provider : providers) {
					opsDisplay.addSelections(provider.getSelections());
				}
			}
		});	
		
		opsDisplay.selectionPanel.buildButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				opsDisplay.doBuild(CommandVerb.build);
			}
		});	
		opsDisplay.selectionPanel.cleanButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				opsDisplay.doBuild(CommandVerb.clean);
			}
		});		
		opsDisplay.selectionPanel.buildTestsButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				opsDisplay.doTestBuild(CommandVerb.build);
			}
		});	
		opsDisplay.selectionPanel.runButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				opsDisplay.doRun(CommandVerb.runtest);
			}
		});		
		opsDisplay.selectionPanel.addSelectionsButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				List<SelectionsInterface> providers = AvpsResourceManager.getInstance().getSelectionProviders();
				for(SelectionsInterface provider : providers){
					TreeItem[] selections = provider.getSelections();
					opsDisplay.addSelections(selections);
				}
			}
		});	
		opsDisplay.selectionPanel.removeSelectionsButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				opsDisplay.removeSelected();
			}
		});		
		opsDisplay.selectionPanel.clearSelectionsButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				opsDisplay.clearEntries();
			}
		});		
	}
	
}
