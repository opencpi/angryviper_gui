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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

import av.proj.ide.avps.internal.AngryViperAsset;
import av.proj.ide.avps.internal.AngryViperAssetService;
import av.proj.ide.avps.internal.AngryViperAssetService.AckModelDataUpdate;
import av.proj.ide.avps.internal.AngryViperAssetService.ModelDataUpdate;
import av.proj.ide.avps.internal.AssetModelData;
import av.proj.ide.avps.internal.AvpsResourceManager;
import av.proj.ide.avps.internal.BuildTargetSelections;
import av.proj.ide.avps.internal.ExecutionAsset.CommandVerb;
import av.proj.ide.avps.internal.OcpiAssetCategory;
import av.proj.ide.avps.internal.ProjectBuildService;
import av.proj.ide.avps.internal.ProjectBuildService.ProvideBuildSelections;
import av.proj.ide.avps.internal.SelectionsInterface;
import av.proj.ide.avps.internal.UserBuildSelections;

public class ProjectViewSwtDisplay extends Composite implements SelectionsInterface {
	Tree projectsTree;
	ArrayList<TreeItem> projectTrees;
	private Composite headerArea;
	
	Button addSelectionsButton;
	Button synchronizeButton;
	private Composite gap;
	private ProjectImages projectImages;
	private  AckModelDataUpdate	ackUpdate = null;
	
	BuildTargetSelections lastSelections = new BuildTargetSelections();
	
	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public ProjectViewSwtDisplay(Composite parent, int style) {
	super(parent, style);

		projectTrees = new ArrayList<TreeItem>();
		AvpsResourceManager.getInstance().registerSelectionProviders((SelectionsInterface) this);
		
		headerArea = new Composite(this, SWT.BORDER_SOLID);
		headerArea.setLayout(new GridLayout(1, false) );
		
		Composite buttons = new Composite(this, SWT.NONE);
		buttons.setLayout(new FillLayout(SWT.HORIZONTAL));
		
		synchronizeButton = new Button(buttons, SWT.PUSH);
		synchronizeButton.setText("Refresh");
		synchronizeButton.setToolTipText("Refresh assets from the file system.");
		
		gap = new Composite(this, SWT.NONE);
		gap.setLayout(new GridLayout(1, false) );
		
		projectsTree = new Tree(this, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		
		
		// ===================================================================
		//                          Panel Layout 
		// ===================================================================
		FormLayout layout = new FormLayout();
		layout.marginTop = 5;
		layout.marginBottom = 5;
		this.setLayout(layout);
		
		FormData data;
		
		// Header Panel - top and right of buildPanel
		data = new FormData();
		data.top = new FormAttachment(0, 5);
		data.left = new FormAttachment(0, 5);
		data.right = new FormAttachment(100, -5);
		data.bottom  = new FormAttachment(0, 25);
		headerArea.setLayoutData(data);	

		// Buttons
		data = new FormData();
		data.top = new FormAttachment(headerArea, 5);
		data.left = new FormAttachment(0, 20);
		data.right = new FormAttachment(0, 200);
		buttons.setLayoutData(data);
	
		// Gap
		data = new FormData();
		data.top = new FormAttachment(buttons, 5);
		data.left = new FormAttachment(0, 10);
		data.right = new FormAttachment(100, -10);
		gap.setLayoutData(data);

		
		// Selection Panel
		data = new FormData();
		data.top = new FormAttachment(gap, 15);
		data.left = new FormAttachment(0, 5);
		data.right = new FormAttachment(100, -5);
		data.bottom  = new FormAttachment(100, -5);
		projectsTree.setLayoutData(data);
		
		addSelectionMenu();
	}
	
	
	public void setPanelColorScheme(AvColorScheme colorScheme) {
		headerArea.setBackground(colorScheme.getPrimary());
		gap.setBackground(colorScheme.getSecondary());
	}
	
	public void loadModelData(AngryViperAssetService  assetServices, ProjectImages images ) {
		
		projectImages = images;
		Collection<AssetModelData> projects = assetServices.getProjects().values();
		for(AssetModelData project: projects) {
			addProject(project);
		}
		ackUpdate = AngryViperAssetService.getInstance().registerProjectModelRefresh(new ModelDataUpdate(){
			public void processChangeSet(Set<AssetModelData> removedAssets, Set<AssetModelData> newAssets) {
				processChanges(removedAssets, newAssets);
			}
		});
	}
	
	private void addProject(AssetModelData project) {
		TreeItem projItem = new TreeItem(projectsTree, SWT.NONE);
		AngryViperAsset projectAsset = project.getAsset();
		projItem.setText(projectAsset.assetName);
		projItem.setImage(projectImages.getProject());
		projItem.setData(project.getAsset());
		projectAsset.assetUiItem = projItem;
		projectTrees.add(projItem);
		loadAsset(project.getChildList(), projItem);
	}
	
	
	private void loadAsset(ArrayList<AssetModelData> childList, TreeItem parentItem) {
		
		for(AssetModelData child : childList) {
			AngryViperAsset childAsset = child.getAsset();
			TreeItem childItem = new TreeItem(parentItem, SWT.NONE);
			childAsset.assetUiItem = childItem;
			childItem.setText(childAsset.assetName);
			childItem.setImage(projectImages.getImage(childAsset.category));
			childItem.setData(childAsset);
			if(childAsset.category == OcpiAssetCategory.hdlLibrary) {
				if("devices".equals(childAsset.assetName)) {
					childItem.setImage(projectImages.getDevices());
				}
				else {
					childItem.setImage(projectImages.getCards());
				}
			}
			loadAsset(child.getChildList(), childItem);
		}
	}


	public void processChanges(Set<AssetModelData> removedAssets,  Set<AssetModelData> newAssets) {
		removeAssets(removedAssets);
		for(AssetModelData assetModel : newAssets) {
			AngryViperAsset asset = assetModel.getAsset();
			if(asset.category == OcpiAssetCategory.project) {
				addProject(assetModel);
			}
			else {
				//System.out.println(asset.toString());
				addNewAsset(asset);
			}
		}
		ackUpdate.updateCompleted();
	}
	
	private void addNewAsset(AngryViperAsset asset) {
		if(asset.parent == null) {
			// hit the project tree item
			return;
		}
		if( asset.parent.assetUiItem != null && asset.assetUiItem == null) {
			TreeItem newItem = new TreeItem(asset.parent.assetUiItem, SWT.NONE);
			asset.assetUiItem = newItem;
			newItem.setText(asset.assetName);
			newItem.setImage(projectImages.getImage(asset.category));
			newItem.setData(asset);
		} else {
			// go up a level
			addNewAsset(asset.parent);
			// The parent was added now check to add self.
			if(asset.assetUiItem == null) {
				TreeItem newItem = new TreeItem(asset.parent.assetUiItem, SWT.NONE);
				asset.assetUiItem = newItem;
				newItem.setText(asset.assetName);
				newItem.setImage(projectImages.getImage(asset.category));
				newItem.setData(asset);
			}
		}
	}
	
	private void removeAssets(Collection<AssetModelData> removedAssets) {
		for(AssetModelData asset: removedAssets) {
			TreeItem item = asset.getAsset().assetUiItem;
			
			if(item != null && ! item.isDisposed()) {
				item.dispose();
			}
		}
	}

	private SelectionAdapter menuAdapter = new SelectionAdapter() {
		
     	private ProjectBuildService srv = ProjectBuildService.getInstance();
    	
		public void widgetSelected(SelectionEvent event) {
			MenuItem item =  (MenuItem)event.widget;
			CommandVerb verb = (CommandVerb)item.getData();
			ProvideBuildSelections provider = ProjectBuildService.getInstance().getBuildselectionProvider();
			
			if(provider != null) {
				UserBuildSelections userSelections = provider.getBuildSelections();
				userSelections.verb = verb;
				if(userSelections.assetSelections.size() > 0 ) {
					userSelections.assetSelections.clear();
				}
				TreeItem[] sels = projectsTree.getSelection();
				for( int i=0; i < sels.length; i++) {
					AngryViperAsset asset = (AngryViperAsset)sels[i].getData();
					userSelections.assetSelections.add(asset);
				}
				if(userSelections.buildDescription == null) {
					userSelections.buildDescription = MainOperationSwtDisplayV1.makeBuildLabel(userSelections);
				}
				srv.processBuildRequest(userSelections);
			}
		}
	};

	private void addSelectionMenu () {
	    final Menu menu = new Menu(projectsTree);
	    projectsTree.setMenu(menu);
	    
        MenuItem newItem = new MenuItem(menu, SWT.NONE);
        newItem.setText("build");
        newItem.setData(CommandVerb.build);
        newItem.addSelectionListener(menuAdapter);
        
        newItem = new MenuItem(menu, SWT.NONE);
        newItem.setText("clean");
        newItem.setData(CommandVerb.clean);
        newItem.addSelectionListener(menuAdapter);

//  Future use - give the dropdown some context.
//	    menu.addMenuListener(new MenuAdapter()
//	    {
//	        public void menuShown(MenuEvent e)
//	        {
//	            MenuItem[] items = menu.getItems();
//	            for (int i = 0; i < items.length; i++)
//	            {
//	                ;
//	            }
//	        }
//	    });
		
	}
	
	// Meet the interface requirements.
	@Override
	public void addSelections(TreeItem[] items) {
		// do nothing.
		
	}
	@Override
	public TreeItem[] getSelections() {
		TreeItem[] selections = projectsTree.getSelection();
		return selections;
	}
}
