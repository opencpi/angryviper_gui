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

import java.io.InputStream;
import java.util.LinkedHashMap;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MenuAdapter;
import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;

import av.proj.ide.avps.internal.AvpsResourceManager;
import av.proj.ide.avps.internal.OcpiBuildStatus;
import av.proj.ide.avps.internal.ProjectBuildService;
import av.proj.ide.avps.internal.StatusNotificationInterface;
import av.proj.ide.avps.internal.StatusRegistration;
import av.proj.ide.internal.OcpidevVerb;

public class StatusViewSwtDisplay2 extends Composite implements StatusNotificationInterface {

	class StatusItemControls {
		TreeItem theItem;
		Integer buildNumber;
		Color color;
		String consoleName;
		Boolean isActive = true;
		Boolean isRun = false;
	}
	
	class StatusLineUpdater {
		TreeItem item;
		OcpiBuildStatus latestStatus = null;
		public StatusLineUpdater (TreeItem item) {
			this.item = item;
		}
		public void updateStatus(OcpiBuildStatus status) {
			this.latestStatus = status;
			Display.getDefault().asyncExec(new Runnable(){
				public void run() {
					status.updateRunStatusLine(item);
				}
			});
		}
	}
	
	private Composite headerArea;
	private Tree statusListing;
	Button expand;

	private AvColorScheme colorScheme;
	LinkedHashMap<Integer, StatusItemControls> buildControls;
	LinkedHashMap<Integer, StatusLineUpdater> statusUpdaters;
	StatusContextMenu contextMenu;
	
	@Override
	public void dispose() {
		AvpsResourceManager mgr = AvpsResourceManager.getInstance();
		mgr.deRegisterStatusReceiver((StatusNotificationInterface)this);
		super.dispose();
	}
	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public StatusViewSwtDisplay2(Composite parent, int style) {
		super(parent, style);
		this.setLayout(new FormLayout());

		AvpsResourceManager mgr = AvpsResourceManager.getInstance();
		mgr.registerStatusReceiver((StatusNotificationInterface)this);
		
		buildControls = new LinkedHashMap<Integer, StatusItemControls>();
		statusUpdaters = new LinkedHashMap<Integer, StatusLineUpdater>();
		
		headerArea = new Composite(this, SWT.BORDER_SOLID);
		headerArea.setLayout(new GridLayout(2, false) );
		ColorSchemeBlue cs = new ColorSchemeBlue();
		headerArea.setBackground(cs.primary);
		expand = new Button(headerArea, SWT.TOGGLE);
		expand.setBackground(cs.secondary);
	    ClassLoader cl = this.getClass().getClassLoader();
		InputStream stream = cl.getResourceAsStream("icons/expandall.gif");		
		ImageData imd = new ImageData(stream);
		Image plusImage = new Image(parent.getDisplay(), imd);
		expand.setImage(plusImage);
		expand.pack();
		
		// Made it a single select to simplify the context menu.
		statusListing = new Tree (this, SWT.BORDER | SWT.MULTI);
		statusListing.setLinesVisible (true);
		statusListing.addListener(SWT.Selection, new Listener() {
			
			@Override
			public void handleEvent(Event event) {
				TreeItem[] selects = statusListing.getSelection();
				if(selects.length == 1) {
					TreeItem item = selects[0];
					StatusItemControls control = (StatusItemControls)item.getData();
					// control will be null if a sub item is selected.
					if(control == null) return;
					AvpsResourceManager.getInstance().bringConsoleToView(control.consoleName);
				}
			}
		});
		
		// ===================================================================
		//                          Panel Layout 
		// ===================================================================
		FormData data;
		
		// Header Panel - top and right of buildPanel
		data = new FormData();
		data.top = new FormAttachment(0, 5);
		data.left = new FormAttachment(0, 5);
		data.right = new FormAttachment(100, -5);
		data.bottom  = new FormAttachment(0, 40);
		headerArea.setLayoutData(data);
		
		data = new FormData();
		data.top = new FormAttachment(headerArea, 5);
		data.left = new FormAttachment(0, 10);
		data.right = new FormAttachment(100, -10);
		data.bottom  = new FormAttachment(100, -5);
		statusListing.setLayoutData(data);
		
		statusListing.setHeaderVisible(true);
		TreeColumn column1 = new TreeColumn(statusListing, SWT.NONE);
		column1.setWidth(200);
		
		TreeColumn column2 = new TreeColumn(statusListing, SWT.NONE);
		column2.setWidth(200);
		
		TreeColumn column3 = new TreeColumn(statusListing, SWT.NONE);
		// Hack to make horizontal scroll work better.
		column3.setWidth(1000);
		contextMenu = new StatusContextMenu();
		contextMenu.addSelectionMenu(statusListing);
		
		expand.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				expandStatusBars(expand.getSelection()); 
			}
		});
	}
	
	public void setPanelColorScheme(AvColorScheme colorScheme) {
		headerArea.setBackground(colorScheme.getPrimary());
		this.colorScheme = colorScheme;
	}
	public void expandStatusBars(boolean exp) {
		TreeItem[] items = statusListing.getItems();
		for(int i = 0; i < items.length; i++){
			TreeItem itm = items[i];
			itm.setExpanded(exp);
		}
	}
	
	
	@Override
	public void registerBuild(Integer buildNumber, StatusRegistration registration) {
		
		addStatusEntry(buildNumber, registration);
	}
	@Override
	public void updateBuildStatus(Integer buildNumber, OcpiBuildStatus status) {
		StatusLineUpdater updater = statusUpdaters.get(buildNumber);
		if(updater != null)
			updater.updateStatus(status);
	}
	
	@Override
	public void setCompletedStatusEntry(Integer buildNumber, boolean completedSuccessfully) {
		Display.getDefault().asyncExec(new Runnable(){
			public void run() {
				StatusItemControls ctrl = buildControls.get(buildNumber);
				if(ctrl == null) return;
				TreeItem item = ctrl.theItem;
				StatusItemControls control = (StatusItemControls)item.getData();
				control.isActive = false;
				
				if(completedSuccessfully) {
					item.setBackground(colorScheme.getSuccess());
				}
				else {
					item.setBackground(colorScheme.getDanger());
				}
			}
		});
	}
	@Override
	public void restartBuild(Integer buildNumber, StatusRegistration registration) {
		StatusItemControls ctrl = buildControls.get(buildNumber);
		if(ctrl == null) return;
		TreeItem item = ctrl.theItem;
		item.setBackground(ctrl.color);
		StatusItemControls control = (StatusItemControls)item.getData();
		control.isActive = true;
		
		// Only the date,time, verb info may change in the main line.
		String[] s = registration.getStatusLineEntries();
		item.setText(2, s[2]);
		
		String[][] detailLines = registration.getDetailEntries();
		TreeItem[] childList = item.getItems();
		for(int i = 0; i < childList.length; i++ ) {
			String[] lineEntries = detailLines[i];
			// Only the state and exec times change.
			childList[i].setText(2, lineEntries[2]);
		}
	}
	
	
	public void addStatusEntry(Integer buildNumber, StatusRegistration registration) {

		TreeItem item = new TreeItem(statusListing, SWT.NONE);
		String[] s = registration.getStatusLineEntries();
		item.setText(s);
		StatusLineUpdater updater = new StatusLineUpdater(item);
		statusUpdaters.put(buildNumber, updater);
		
	    StatusItemControls controls = new StatusItemControls();
	    controls.theItem = item;
	    controls.buildNumber = buildNumber;
	    controls.consoleName = registration.getConsoleName();
	    
	    if(registration.getVerb() == OcpidevVerb.run)
	    	controls.isRun = true;
		buildControls.put(buildNumber, controls);
		item.setData(controls);
		
		String[][] detailLines = registration.getDetailEntries();
		for(String[] line : detailLines) {
			TreeItem lineitem = new TreeItem(item, SWT.NONE);
			lineitem.setText(line);
		}
	}

	private class StatusContextMenu {
		
		Menu menu;
		MenuItem build, clean, stop, delete , run;
		
		protected void disableAllItems() {
            MenuItem[] items = menu.getItems();
            for (int i = 0; i < items.length; i++)
            {
            	MenuItem item = items[i];
            	item.setEnabled(false);
            }
		}

		protected void setInactiveSelections() {
			build.setEnabled(true);
			run.setEnabled(false);
			clean.setEnabled(true);
			stop.setEnabled(false);
			delete.setEnabled(true);			
		}

		protected void setActiveSelections() {
			build.setEnabled(false);
			run.setEnabled(false);
			clean.setEnabled(false);
			stop.setEnabled(true);
			delete.setEnabled(false);			
		}
		protected void setInactiveRunSelections() {
			build.setEnabled(false);
			run.setEnabled(true);
			clean.setEnabled(false);
			stop.setEnabled(false);
			delete.setEnabled(true);			
		}
		
		protected void setActiveRunSelections() {
			build.setEnabled(false);
			run.setEnabled(false);
			clean.setEnabled(false);
			stop.setEnabled(true);
			delete.setEnabled(false);			
		}
		
		protected void addSelectionMenu (Tree tree) {
		    menu = new Menu(tree);
		    tree.setMenu(menu);

		    
	        MenuItem newItem = new MenuItem(menu, SWT.NONE);
	        newItem.setText("build");
	        build = newItem;
	        newItem.addSelectionListener(new SelectionAdapter() {
	 			public void widgetSelected(SelectionEvent event) {
	 				TreeItem selection = statusListing.getSelection()[0];
 					StatusItemControls control = (StatusItemControls)selection.getData();
 					while(control == null) {
 						selection = selection.getParentItem();
 						control = (StatusItemControls)selection.getData();
 					}
					ProjectBuildService.getInstance().reRun(OcpidevVerb.build, null, control.buildNumber);
				}
			});
	        
	        newItem = new MenuItem(menu, SWT.NONE);
	        newItem.setText("run");
	        run = newItem;
	        newItem.addSelectionListener(new SelectionAdapter() {
	 			public void widgetSelected(SelectionEvent event) {
	 				TreeItem selection = statusListing.getSelection()[0];
 					StatusItemControls control = (StatusItemControls)selection.getData();
 					while(control == null) {
 						selection = selection.getParentItem();
 						control = (StatusItemControls)selection.getData();
 					}
					ProjectBuildService.getInstance().reRun(OcpidevVerb.run, null, control.buildNumber);
				}
	 		});
	        
	        newItem = new MenuItem(menu, SWT.NONE);
	        newItem.setText("clean");
	        clean = newItem;
	        newItem.addSelectionListener(new SelectionAdapter() {
	 			public void widgetSelected(SelectionEvent event) {
	 				TreeItem selection = statusListing.getSelection()[0];
 					StatusItemControls control = (StatusItemControls)selection.getData();
 					while(control == null) {
 						selection = selection.getParentItem();
 						control = (StatusItemControls)selection.getData();
 					}
					ProjectBuildService.getInstance().reRun(OcpidevVerb.clean, null, control.buildNumber);
				}
	 		});
	        
	        newItem = new MenuItem(menu, SWT.NONE);
	        newItem.setText("stop");
	        stop = newItem;
	        newItem.addSelectionListener(new SelectionAdapter() {
	 			public void widgetSelected(SelectionEvent event) {
	 				TreeItem selection = statusListing.getSelection()[0];
 					StatusItemControls control = (StatusItemControls)selection.getData();
 					while(control == null) {
 						selection = selection.getParentItem();
 						control = (StatusItemControls)selection.getData();
 					}
 					ProjectBuildService.getInstance().stopBuild(control.buildNumber);
 					selection.setBackground(colorScheme.getYield());
 					control.isActive = false;
				}
	 		});
	        newItem = new MenuItem(menu, SWT.NONE);
	        newItem.setText("delete");
	        delete = newItem;
	        newItem.addSelectionListener(new SelectionAdapter() {
	 			public void widgetSelected(SelectionEvent event) {
	 				TreeItem[] selections = statusListing.getSelection();
		            for (int i = 0; i < selections.length; i++)
		            {
	 					TreeItem selection = selections[i];
	 					StatusItemControls control = (StatusItemControls)selection.getData();
	 					while(control == null) {
	 						selection = selection.getParentItem();
	 						control = (StatusItemControls)selection.getData();
	 					}
	 					if(control.isActive)
	 						continue;
	 					
	 					buildControls.remove(control.buildNumber);
	 					statusUpdaters.remove(control.buildNumber);
	 					ProjectBuildService.getInstance().deRegisterStatusMontor(control.buildNumber);
	 					selection.dispose();
		            }
				}
	 		});
	        
		    menu.addMenuListener(new MenuAdapter()
		    {
		        public void menuShown(MenuEvent e)
		        {
	 				TreeItem[] selections = statusListing.getSelection();
	 				int length = selections.length;
	 				if(length == 0) {
	 					disableAllItems();
	 				}
	 				else if(length == 1) {
	 					TreeItem selection = selections[0];
	 					StatusItemControls control = (StatusItemControls)selection.getData();
	 					while(control == null) {
	 						selection = selection.getParentItem();
	 						control = (StatusItemControls)selection.getData();
	 					}
	 					
	 					if(control.isActive) {
	 						if(control.isRun) {
		 						setActiveRunSelections();
	 						}
	 						else {
		 						setActiveSelections();
	 						}
	 					}
	 					else {
	 						if(control.isRun) {
		 						setInactiveRunSelections();
	 						}
	 						else {
		 						setInactiveSelections();
	 						}
	 					}
	 				}
	 				else {
	 					disableAllItems();
	 					delete.setEnabled(true);			
	 				}
		        }
		    });
		}
	}

	/***
	 * Run the panel
	 *
	 */
	/***
	public static void main(String[] args) {
	    Display display = new Display();
	    Shell shell = new Shell(display);
	    shell.setLayout(new FormLayout());
	    
    	Button button = new Button(shell, SWT.PUSH);
		button.setText(">>");
	    FormData data = new FormData();
		data.top = new FormAttachment(0, 5);
		data.left = new FormAttachment(0, 5);
		data.right = new FormAttachment(0, 30);
		data.bottom  = new FormAttachment(0, 30);
		
		button.setLayoutData(data);
	    
	    data = new FormData();
		data.top = new FormAttachment(30, 5);
		data.left = new FormAttachment(0, 5);
		data.right = new FormAttachment(100, -5);
		data.bottom  = new FormAttachment(100, -5);
	    
	    StatusViewSwtDisplay2 statDisplay = new 	StatusViewSwtDisplay2(shell, SWT.NONE);
	    data = new FormData();
		data.top = new FormAttachment(30, 5);
		data.left = new FormAttachment(0, 5);
		data.right = new FormAttachment(100, -5);
		data.bottom  = new FormAttachment(100, -5);
	    statDisplay.setLayoutData(data);
	    
		ColorSchemeBlue colorScheme = new ColorSchemeBlue();
		statDisplay.setPanelColorScheme(colorScheme);

		OcpiBuildStatus bldStatus;
		for(int i=1; i < 6; i++) {
			// ===================================================================
			//                          Line 1 
			// ===================================================================
			bldStatus = new OcpiBuildStatus();
			//bldStatus.name="buildX";
			bldStatus.asset="complex_mixer";
			bldStatus.project="Base";
			bldStatus.lib="dsp_comps";
			bldStatus.buildString = "build worker complex_mixer --rccplatform=centos7";
			statDisplay.updateBuildStatus(i, bldStatus);	
		}
		
	    int buildNo = 1;
		button.addListener(SWT.Selection, new Listener(){
			public void handleEvent(Event e) {
				Button b = (Button)e.widget;
				if(b.getText().equals(">>")) {
					OcpiBuildStatus status = new OcpiBuildStatus();
					//status.name="buildXYZ";
					status.asset="complex_mixer";
					status.project="Base";
					status.lib="dsp_comps";
					status.buildString = "build worker complex_mixer --rccplatform=centos7";
					statDisplay.updateBuildStatus(buildNo, status);
					b.setText("<<");
				}
				else {
					statDisplay.setCompletedStatusEntry(buildNo, false);
					b.setText(">>");
				}
			}
		});
	    
	    shell.layout();
	    shell.open();
	    while (!shell.isDisposed()) {
	      if (!display.readAndDispatch())
	        display.sleep();
	    }
	    display.dispose();
	}
***/   
}
