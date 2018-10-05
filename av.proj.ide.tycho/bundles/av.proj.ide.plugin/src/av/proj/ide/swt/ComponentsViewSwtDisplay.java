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
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

import av.proj.ide.avps.internal.AvpsResourceManager;
import av.proj.ide.avps.internal.SelectionsInterface;
import av.proj.ide.internal.AngryViperAsset;
import av.proj.ide.internal.AngryViperAssetService;
import av.proj.ide.internal.AngryViperAssetService.AckModelDataUpdate;
import av.proj.ide.internal.AngryViperAssetService.ModelDataUpdate;
import av.proj.ide.internal.AssetModelData;
import av.proj.ide.internal.OpenCPICategory;

public class ComponentsViewSwtDisplay extends Composite implements SelectionsInterface {
	Tree componentsTree;
	ArrayList<TreeItem> componentTrees;
	private Composite headerArea;
	Map<String, TreeItem> projectFolders;
	
	Button synchronizeButton;
	private Composite gap;
	private ProjectImages projectImages;
	private  AckModelDataUpdate	ackUpdate = null;
	Composite parent;
	
	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public ComponentsViewSwtDisplay(Composite parent, int style) {
	super(parent, style);
		this.parent = parent;
		projectFolders = new HashMap<String, TreeItem>();
		componentTrees = new ArrayList<TreeItem>();
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
		
		componentsTree = new Tree(this, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		
		
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
		componentsTree.setLayoutData(data);
		
		//addSelectionMenu();
	}
	
	
	public void setPanelColorScheme(AvColorScheme colorScheme) {
		headerArea.setBackground(colorScheme.getPrimary());
		gap.setBackground(colorScheme.getSecondary());
	}
	
	public void loadModelData(AngryViperAssetService  assetServices, ProjectImages images ) {
		
		projectImages = images;
		TreeItem componentItem = new TreeItem(componentsTree, SWT.NONE);
		OpenCPICategory cat = OpenCPICategory.topLevelSpecs;		
		componentItem.setText(cat.getFrameworkName());
		componentItem.setImage(projectImages.getTopLevelSpecs());
		//componentItem.setData(project.getAsset());
		//projectAsset.assetUiItem = projItem;
		componentTrees.add(componentItem);
		
		componentItem = new TreeItem(componentsTree, SWT.NONE);
		cat = OpenCPICategory.componentsLibrary;		
		componentItem.setText(cat.getFrameworkName());
		componentItem.setImage(projectImages.getComponents());
		//componentItem.setData(project.getAsset());
		//projectAsset.assetUiItem = projItem;
		componentTrees.add(componentItem);
		
		componentItem = new TreeItem(componentsTree, SWT.NONE);
		cat = OpenCPICategory.workers;		
		componentItem.setText(cat.getFrameworkName());
		componentItem.setImage(projectImages.getWorkers());
		//componentItem.setData(project.getAsset());
		//projectAsset.assetUiItem = projItem;
		componentTrees.add(componentItem);
		
		Collection<AssetModelData> projects = assetServices.getWorkspaceProjects().values();
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
		String projectName = project.getAsset().assetName;
		TreeItem folderItem = null;
		
		String key = null;
		for(AssetModelData child : project.getChildList()) {
			
			boolean loadit = false;
			switch(child.getAsset().category) {
			default:
				break;
			case componentsLibrary:
			case componentsLibraries:
				 folderItem = componentTrees.get(2);
				 key = child.getAsset().category.getFrameworkName() + projectName;
				 loadit = true;
				break;
			case specs:
				 folderItem = componentTrees.get(1);
				 key = child.getAsset().category.getFrameworkName() + projectName;
				 loadit = true;
				break;
			case topLevelSpecs:
				 folderItem = componentTrees.get(0);
				 key = child.getAsset().category.getFrameworkName() + projectName;
				 loadit = true;
				break;
			case library:
				 folderItem = componentTrees.get(1);
				 key = child.getAsset().category.getFrameworkName() + projectName;
				 loadit = true;
			}
			
			if(loadit) {
				TreeItem projectItem = projectFolders.get(key);
				if(projectItem == null) {
					projectItem = new TreeItem(folderItem, SWT.NONE);
					projectItem.setText(projectName);
					projectItem.setImage(projectImages.getProject());
					//componentItem.setData(project.getAsset());
					//projectAsset.assetUiItem = projItem;
					projectFolders.put(key, projectItem);
				}
				loadAsset(child.getChildList(), projectItem);
			}
		}
	}
	
	
	private void loadAsset(ArrayList<AssetModelData> childList, TreeItem parentItem) {
		
		for(AssetModelData child : childList) {
			AngryViperAsset childAsset = child.getAsset();
			
			if(childAsset.category == OpenCPICategory.test) continue;
			
			TreeItem childItem = new TreeItem(parentItem, SWT.NONE);
			childAsset.assetUiItem = childItem;
			childItem.setText(childAsset.assetName);
			childItem.setImage(projectImages.getImage(childAsset.category));
			childItem.setData(childAsset);
			loadAsset(child.getChildList(), childItem);
		}
	}


	public void processChanges(Set<AssetModelData> removedAssets,  Set<AssetModelData> newAssets) {
		removeAssets(removedAssets);
		for(AssetModelData assetModel : newAssets) {
			AngryViperAsset asset = assetModel.getAsset();
			if(asset.category == OpenCPICategory.project) {
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
/***************
 * Future Implementation of a menu on the asset in the ops panel
 * 	
	private SelectionAdapter fileSystemAdapter = new SelectionAdapter() {
		
		public void widgetSelected(SelectionEvent event) {
			MenuItem item =  (MenuItem)event.widget;
			String cmd = item.getText();
			TreeItem[] sels = componentsTree.getSelection();
			if(sels.length != 1)
				return;
			
			TreeItem selection = sels[0];
			AngryViperAsset asset = (AngryViperAsset)selection.getData();
			if(asset == null)
				return;

			switch(cmd) {
			case "open":
				openEditor(asset);
				break;
				
			default:
				return;
			}
		}
		
		public void openThisEditor(AngryViperAsset asset, IFile assetFile) {
			IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
			
			try {
				OcpiAssetFileService.openEditor(asset, null, assetFile, page, null, false);
			} catch (CoreException e) {
				AvpsResourceManager.getInstance().writeToNoticeConsole("Internal Eclipse runtime error occurred. \n --> " + e.toString() );
			}
		}
		
		public void openEditor(AngryViperAsset asset) {
//			Display display = Display.getDefault();
//			//Display display = PlatformUI.getWorkbench().getDisplay();
//			display.sleep();
//			Runnable task = new Runnable() {
//				public void run() {
//					System.out.println("OpenEditor task starting");
			
			IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
			IProject project = root.getProject(asset.projectLocation.projectName);
			IFolder folder = OcpiAssetFileService.getAssetFolder(asset, project);
			if(folder == null || ! folder.exists()) {
				AvpsResourceManager.getInstance()
				.writeToNoticeConsole("Unable to find this asset's parent folder. Use Project Explorer to look for it.");
				return;
			}
			IFile assetFile = OcpiAssetFileService.getAssetFile(asset, folder);
			if(assetFile != null && assetFile.exists()) {
				openThisEditor(asset, assetFile);
				return;
			}
			
			File[] xmlFiles =  OcpiAssetFileService.getAssetXmlFiles(folder);
			if(xmlFiles == null) {
				AvpsResourceManager.getInstance()
				.writeToNoticeConsole("Unable to find any XML files for this asset. Use Project Explorer to look for it.");
				return;
			}
			String[] names = new String[xmlFiles.length];
			int i = 0;
			for(File file : xmlFiles) {
				names[i] = file.getName();
				i++;
			}
			ListSelectionDialog dialog = 
			new ListSelectionDialog(getShell(), xmlFiles, ArrayContentProvider.getInstance(),
					fileLabel, "Open one or more of these?");
			dialog.setTitle("Unable to find asset XML file.");
			//dialog.setInitialSelections(names);
			dialog.open();
			Object[] result = dialog.getResult();
			if(result != null) {
				for(Object name : result) {
					File zname = (File) name;
					IFile wbFile = FileBuffers.getWorkspaceFileAtLocation(Path.fromOSString(zname.getPath()));
					if(wbFile.exists()) {
						openThisEditor(asset, wbFile);
					}
				}
			}
		}
	};
	
	class FileLabelProvider extends LabelProvider {
		
		@Override
		public String getText(Object o) {
			if(o instanceof File) {
				File f = (File) o;
				return f.getName();
			}
			return null;
		}
	}
	private FileLabelProvider fileLabel = new FileLabelProvider();
	

	private Menu menu = null;
	//private MenuItem open;
	private void addSelectionMenu () {
	    menu = new Menu(componentsTree);
	    componentsTree.setMenu(menu);
	    
//        MenuItem newItem = new MenuItem(menu, SWT.NONE);
//       
//        newItem = new MenuItem(menu, SWT.NONE);
//        newItem.setText("open");
//        newItem.addSelectionListener(fileSystemAdapter);
//        open = newItem;
        

//  Future use - give the dropdown some context.
	    menu.addMenuListener(new MenuAdapter()
	    {
	    	@Override
	    	public void menuShown(MenuEvent e)
	        {
	        	//e.getSource();
//				TreeItem[] sels = componentsTree.getSelection();
//	            MenuItem[] items = menu.getItems();
//	            int number = items.length;
	        }
	    });

	}
	************/
	// Meet the interface requirements.
	@Override
	public void addSelections(TreeItem[] items) {
		// do nothing.
		
	}
	@Override
	public TreeItem[] getSelections() {
		TreeItem[] selections = componentsTree.getSelection();
		return selections;
	}
}
