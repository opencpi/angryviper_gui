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

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

import org.eclipse.core.filebuffers.FileBuffers;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MenuAdapter;
import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.ListSelectionDialog;
import org.eclipse.ui.wizards.IWizardDescriptor;

import av.proj.ide.avps.internal.AvpsResourceManager;
import av.proj.ide.avps.internal.BuildTargetSelections;
import av.proj.ide.avps.internal.ProjectBuildService;
import av.proj.ide.avps.internal.ProjectBuildService.ProvideBuildSelections;
import av.proj.ide.avps.internal.SelectionsInterface;
import av.proj.ide.avps.internal.UserBuildSelections;
import av.proj.ide.internal.AngryViperAsset;
import av.proj.ide.internal.AngryViperAssetService;
import av.proj.ide.internal.AngryViperAssetService.AckModelDataUpdate;
import av.proj.ide.internal.AngryViperAssetService.ModelDataUpdate;
import av.proj.ide.internal.AngryViperProjectInfo;
import av.proj.ide.internal.AssetModelData;
import av.proj.ide.internal.OcpiAssetFileService;
import av.proj.ide.internal.OcpidevVerb;
import av.proj.ide.internal.OpenCPICategory;
import av.proj.ide.internal.OpencpiEnvService;
import av.proj.ide.wizards.NewOcpiAssetWizard;

public class ProjectViewSwtDisplay extends Composite implements SelectionsInterface {
	Tree projectsTree;
	ArrayList<TreeItem> projectTrees;
	private Composite headerArea;
	
	Button addSelectionsButton;
	Button synchronizeButton;
	private Composite gap;
	private ProjectImages projectImages;
	private  AckModelDataUpdate	ackUpdate = null;
	Composite parent;
	BuildTargetSelections lastSelections = new BuildTargetSelections();
	
	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public ProjectViewSwtDisplay(Composite parent, int style) {
	super(parent, style);
		this.parent = parent;

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
		projectsTree.addMouseListener(new MouseAdapter() {
			@Override
		    public void mouseDoubleClick(MouseEvent e) {

				TreeItem[] items = projectsTree.getSelection();
				if(items.length == 1) {
					AngryViperAsset asset = (AngryViperAsset) items[0].getData();
					
					switch(asset.category) {
					case application:
					case xmlapp:
					case assembly:
					case component:
					case platform:
					case protocol:
					case test:
					case worker:
						openEditor(asset);
						break;
						
					default:
						break;
					
					}
				}

		    }			
		}
		);
/**
	    Transfer[] types = new Transfer[] { LocalSelectionTransfer.getTransfer(), ResourceTransfer.getInstance(), FileTransfer.getInstance() };
	    int operations = DND.DROP_COPY;
	    final DragSource source = new DragSource(projectsTree, operations);
	    source.setTransfer(types);
	    final IResource dragSourceItem[] = new IResource[1];
	    
	    source.addDragListener(new DragSourceListener() {
	        public void dragStart(DragSourceEvent event) {
	          TreeItem[] selection = projectsTree.getSelection();
	          
	          if (selection.length == 1) {
	        	  TreeItem item = selection[0];
	        	  AngryViperAsset asset = (AngryViperAsset) item.getData();
	        	  if(asset.category == OpenCPICategory.worker) {
	  	            event.doit = true;
//	  	            dragSourceItem[0] = "/home/tstrong/runtime-EclipseApplication/proj1/components/worker1.rcc/worker1.xml";
	  	            IWorkspaceRoot wsRoot = ResourcesPlugin.getWorkspace().getRoot();
	  	            Path path = new Path("/home/tstrong/runtime-EclipseApplication/proj1/components/worker1.rcc/worker1.xml");
	  	            IFile file = wsRoot.getFileForLocation(path);
	  	            dragSourceItem[0] = file;
		            //System.out.println("doit = true");
		            return;
	        	  }
	            
	          }
	            event.doit = false;
	            //System.out.println("doit = false");
	        };

	        public void dragSetData(DragSourceEvent event) {
	          event.data = dragSourceItem;
	          //System.out.println("event.data added ");
	        }

	        public void dragFinished(DragSourceEvent event) {
	          dragSourceItem[0] = null;
	        }
	      });
**/	    
		
	}
	
	
	public void setPanelColorScheme(AvColorScheme colorScheme) {
		headerArea.setBackground(colorScheme.getPrimary());
		gap.setBackground(colorScheme.getSecondary());
	}
	
	public void loadModelData(AngryViperAssetService  assetServices, ProjectImages images ) {
		
		projectImages = images;
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
		TreeItem projItem = new TreeItem(projectsTree, SWT.NONE);
		AngryViperAsset projectAsset = project.getAsset();
		projItem.setText(projectAsset.qualifiedName);
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

	private SelectionAdapter buildAdapter = new SelectionAdapter() {
		
     	private ProjectBuildService srv = ProjectBuildService.getInstance();
    	
		public void widgetSelected(SelectionEvent event) {
			MenuItem item =  (MenuItem)event.widget;
			OcpidevVerb verb = (OcpidevVerb)item.getData();
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
				srv.processBuildRequest(userSelections);
			}
		}
	};
	
	protected void openEditor(AngryViperAsset asset) {
		
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
	protected void openThisEditor(AngryViperAsset asset, IFile assetFile) {
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		
		try {
			OcpiAssetFileService.openEditor(asset, null, assetFile, page, null, false);
		} catch (CoreException e) {
			AvpsResourceManager.getInstance().writeToNoticeConsole("Internal Eclipse runtime error occurred. \n --> " + e.toString() );
		}
	}
	
	private SelectionAdapter fileSystemAdapter = new SelectionAdapter() {
		
		public void widgetSelected(SelectionEvent event) {
			MenuItem item =  (MenuItem)event.widget;
			String cmd = item.getText();
			TreeItem[] sels = projectsTree.getSelection();
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
				
			case "delete asset":
				deleteAsset(asset);
				break;
				
			case "register project":
				registerProject(asset);
				break;

			case "unregister project":
				unregisterProject(asset);
				break;

			default:
				return;
			}
		}
		
		private void unregisterProject(AngryViperAsset asset) {
			StringBuilder sb = new StringBuilder("Remove ");
			sb.append(asset.category.getFrameworkName());
			sb.append(" ");
			sb.append(asset.assetName);
			sb.append(" from the project registry?");

			boolean confirmed = MessageDialog.openConfirm(getShell(), "Confirm", 
			sb.toString());
			if (confirmed) {
				StringBuilder s = new StringBuilder();
				boolean r = AngryViperAssetService.getInstance().unregisterProject(asset, s);
				if (!r) {
					MessageDialog.openConfirm(getShell(), "OpenCPI Delete Failed", s.toString());
				}
			}
		}

		private void registerProject(AngryViperAsset asset) {
			StringBuilder s = new StringBuilder();
			boolean r = AngryViperAssetService.getInstance().registerProject(asset, s);
			if (!r) {
				MessageDialog.openConfirm(getShell(), "OpenCPI Delete Failed", s.toString());
			}
		}

		public void deleteAsset(AngryViperAsset asset) {
			StringBuilder sb = new StringBuilder("Delete ");
			sb.append(asset.category.getFrameworkName());
			sb.append(" ");
			sb.append(asset.assetName);
			if(asset.category != OpenCPICategory.project) {
				sb.append(" in project ");
				sb.append(asset.projectLocation.projectName);
			}
			boolean confirmed = MessageDialog.openConfirm(getShell(), "Confirm", 
			sb.toString());
			if (confirmed) {
;
				IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();

				IRunnableWithProgress op = new IRunnableWithProgress() {
					@Override
					public void run(IProgressMonitor monitor) {
						StringBuilder s = new StringBuilder();
						
						try {
							if(asset.category == OpenCPICategory.project){
								IProject project = root.getProject(asset.projectLocation.projectName);
								project.close(monitor);
								boolean r = AngryViperAssetService.getInstance().deleteAsset(asset, s);
								project.delete(false, monitor);
								if (!r) {
									MessageDialog.openConfirm(getShell(), "OpenCPI Delete Failed", s.toString());
								}
								root.refreshLocal(IResource.DEPTH_ONE, monitor);
							}
							else {
								IProject project = root.getProject(asset.projectLocation.projectName);
								IFile file = project.getFile(asset.assetName);
								if(file.exists()) {
									file.delete(false, monitor);
								}
								else {
									IFolder folder = project.getFolder(asset.assetName);
									if(folder.exists()) {
										folder.delete(false, monitor);
									}
								}
								
								boolean r = AngryViperAssetService.getInstance().deleteAsset(asset, s);
								if (!r) {
									MessageDialog.openConfirm(getShell(), "OpenCPI Delete Failed", s.toString());
								}
								project.refreshLocal(IResource.DEPTH_INFINITE, monitor);
							}
						} catch (CoreException e) {
							//e.printStackTrace();
						}
					}
				};
				Shell shell = parent.getShell();
				try {
					new ProgressMonitorDialog(shell).run(true, true, op);
				} catch (InvocationTargetException | InterruptedException e) {
				}
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
	
	private SelectionAdapter wizardAdapter = new SelectionAdapter() {
		
		public void widgetSelected(SelectionEvent event) {
			MenuItem item =  (MenuItem)event.widget;
			OpenCPICategory type = (OpenCPICategory)item.getData();
			
			TreeItem[] sels = projectsTree.getSelection();
			if((sels.length == 0 && type == null) || sels.length>1) {
				putUpWizard();
			}
			else {
				TreeItem sel = sels[0];
				AngryViperAsset asset = (AngryViperAsset)sel.getData();
				putUpSpecificWizard(type, asset);
			}
		}
	};
	
	private void putUpWizard() {
		try {
			NewOcpiAssetWizard wizard = createWizard();
			if(wizard == null) return;
			
			WizardDialog wd = new WizardDialog(Display.getDefault().getActiveShell(), wizard);
			wd.setTitle(wizard.getWindowTitle());
			wd.open();
		} catch (CoreException e) {
			AvpsResourceManager.getInstance().writeToNoticeConsole("Internal Eclipse runtime error occurred. \n --> "
					                         + e.toString() );
		}
	}
	private void putUpSpecificWizard(OpenCPICategory selectedType, AngryViperAsset asset) {
		try {
			NewOcpiAssetWizard wizard = createWizard();
			if(wizard == null) return;
			wizard.setupSpecificAsset(selectedType, asset);
			
			WizardDialog wd = new WizardDialog(Display.getDefault().getActiveShell(), wizard);
			wd.setTitle(wizard.getWindowTitle());
			wd.open();
		} catch (CoreException e) {
			AvpsResourceManager.getInstance().writeToNoticeConsole("Internal Eclipse runtime error occurred. \n --> "
                    + e.toString() );
		}
	}
	
	private NewOcpiAssetWizard createWizard() throws CoreException {
		String id = "av.proj.ide.wizards.NewOcpiAssetWizard";
		IWizardDescriptor descriptor = PlatformUI.getWorkbench().getNewWizardRegistry().findWizard(id);
		if (descriptor == null) {
			descriptor = PlatformUI.getWorkbench().getImportWizardRegistry().findWizard(id);
		}
		Runtime rt = Runtime.getRuntime();
		Process proc;
		
		try {
			proc = rt.exec("ocpidev");
			int exitVal = proc.waitFor();
			if (exitVal != 1) {
				MessageDialog.openError(Display.getDefault().getActiveShell(), "\"ocpidev\" Command Not Found", 
						"Could not locate the \"ocpidev\" command.");
			}
		} catch (IOException | InterruptedException e) {
			MessageDialog.openError(Display.getDefault().getActiveShell(), "\"ocpidev\" Command Not Found", 
					"Could not locate the \"ocpidev\" command.");
			return null;
		}
		NewOcpiAssetWizard wizard = (NewOcpiAssetWizard)descriptor.createWizard();
		return wizard;
	}
	
	private Menu menu = null;
	private MenuItem build, clean, open, delete;
	
	private void addSelectionMenu () {
	    menu = new Menu(projectsTree);
	    projectsTree.setMenu(menu);
	    
        MenuItem newItem = new MenuItem(menu, SWT.NONE);
	    
        newItem.setText("asset wizard");
        newItem.addSelectionListener(wizardAdapter);
        
        newItem = new MenuItem(menu, SWT.NONE);
        newItem.setText("build");
        newItem.setData(OcpidevVerb.build);
        newItem.addSelectionListener(buildAdapter);
        build = newItem;
        
        newItem = new MenuItem(menu, SWT.NONE);
        newItem.setText("clean");
        newItem.setData(OcpidevVerb.clean);
        newItem.addSelectionListener(buildAdapter);
        clean = newItem;
        
        newItem = new MenuItem(menu, SWT.NONE);
        newItem.setText("open");
        newItem.addSelectionListener(fileSystemAdapter);
        open = newItem;
        
        newItem = new MenuItem(menu, SWT.NONE);
        newItem.setText("delete asset");
        newItem.addSelectionListener(fileSystemAdapter);
        delete = newItem;
        

	    menu.addMenuListener(new MenuAdapter()
	    {
	    	@Override
	    	public void menuShown(MenuEvent e)
	        {
				TreeItem[] sels = projectsTree.getSelection();
				
				
	            MenuItem[] items = menu.getItems();
	            int number = items.length;
	            
	            // Remove all but the base menu items.  Now that
	            // register/unregister project is added there are
	            // issues with the use of different listeners.
				if(number > 5) {
					for(int i = 5; i < number; i++) {
						items[i].dispose();
					}
				}
	            
				if(sels.length == 0) {
					delete.setEnabled(false);
					build.setEnabled(false);
					clean.setEnabled(false);
					open.setEnabled(false);
					return;
				}
	            
	            if(sels.length > 1) {
					delete.setEnabled(false);
					open.setEnabled(false);
					return;
	            }
	            
	            // One item is selected
				
				delete.setEnabled(true);
				open.setEnabled(false);
				build.setEnabled(true);
				clean.setEnabled(true);
				TreeItem sel = sels[0];
				AngryViperAsset asset = (AngryViperAsset)sel.getData();
				OpenCPICategory cat = asset.category;
				MenuItem theItem = null;
				
				switch (cat) {
				case xmlapp:
					build.setEnabled(false);
					clean.setEnabled(false);
				case application:
					open.setEnabled(true);
				case applications:
					if(cat == OpenCPICategory.applications)
						delete.setEnabled(false);

					theItem = new MenuItem(menu, SWT.NONE);
			        theItem.addSelectionListener(wizardAdapter);
			        theItem.setText("new application");
			        theItem.setData(OpenCPICategory.application);
					
					break;
				case assembly:
					open.setEnabled(true);
				case assemblies:
					if(cat == OpenCPICategory.assemblies)
						delete.setEnabled(false);

					theItem = new MenuItem(menu, SWT.NONE);
			        theItem.addSelectionListener(wizardAdapter);
			        theItem.setText("new assembly");
			        theItem.setData(OpenCPICategory.assembly);
			        
					break;
				case card:
					
				case specs:
			        delete.setEnabled(false);
			        
			        theItem = new MenuItem(menu, SWT.NONE);
			        theItem.setText("new unit test");
			        theItem.setData(OpenCPICategory.test);
			        theItem.addSelectionListener(wizardAdapter);
			        
				case topLevelSpecs:
			        delete.setEnabled(false);
			        
					theItem = new MenuItem(menu, SWT.NONE);
			        theItem.addSelectionListener(wizardAdapter);
			        theItem.setText("new component");
			        theItem.setData(OpenCPICategory.component);

			        theItem = new MenuItem(menu, SWT.NONE);
			        theItem.setText("new protocol");
			        theItem.setData(OpenCPICategory.protocol);
			        theItem.addSelectionListener(wizardAdapter);
			        break;
				
				case component:
					open.setEnabled(true);
					build.setEnabled(false);
					clean.setEnabled(false);
			        theItem = new MenuItem(menu, SWT.NONE);
			        theItem.setText("new worker");
			        theItem.setData(OpenCPICategory.worker);
			        theItem.addSelectionListener(wizardAdapter);
			        
					if(asset.parent.category != OpenCPICategory.topLevelSpecs) {
				        theItem = new MenuItem(menu, SWT.NONE);
				        theItem.setText("new unit test");
				        theItem.setData(OpenCPICategory.test);
				        theItem.addSelectionListener(wizardAdapter);
					}
					break;
					
				case componentsLibrary:
				case componentsLibraries:
				case library:
					
				delete.setEnabled(false);
				
				theItem = new MenuItem(menu, SWT.NONE);
		        theItem.addSelectionListener(wizardAdapter);
		        theItem.setText("new component");
		        theItem.setData(OpenCPICategory.component);

		        theItem = new MenuItem(menu, SWT.NONE);
		        theItem.setText("new worker");
		        theItem.setData(OpenCPICategory.worker);
		        theItem.addSelectionListener(wizardAdapter);

		        theItem = new MenuItem(menu, SWT.NONE);
		        theItem.setText("new protocol");
		        theItem.setData(OpenCPICategory.protocol);
		        theItem.addSelectionListener(wizardAdapter);
		        
		        theItem = new MenuItem(menu, SWT.NONE);
		        theItem.setText("new unit test");
		        theItem.setData(OpenCPICategory.test);
		        theItem.addSelectionListener(wizardAdapter);
		        
					break;
				case device:
					break;
				case hdlTest:
					break;
				case platform:
					open.setEnabled(true);
				case platforms:
					if(cat == OpenCPICategory.platforms)
						delete.setEnabled(false);
					
					theItem = new MenuItem(menu, SWT.NONE);
			        theItem.addSelectionListener(wizardAdapter);
			        theItem.setText("new platform");
			        theItem.setData(OpenCPICategory.platform);
			        
					break;
				case primitive:
				case primitives:
					if(cat == OpenCPICategory.primitives)
						delete.setEnabled(false);
					
					theItem = new MenuItem(menu, SWT.NONE);
			        theItem.addSelectionListener(wizardAdapter);
			        theItem.setText("new primitive");
			        theItem.setData(OpenCPICategory.primitive);
					
					break;
				case project:
					// We've verified one item is selected in the view.  If the
					// menu had more the 6 selections, the selection past 6 have been
					// removed.  Now we know the user has selected a project and we want
					// to allow the user to unregister the project or register it.
					// Now we need to see if the project is registered or not and add
					// the appropriate selection.
					
					TreeItem selection = sels[0];
					AngryViperAsset project = (AngryViperAsset)selection.getData();
					String projectName = project.assetName;
					OpencpiEnvService srv = AngryViperAssetService.getInstance().getEnvironment();
					AngryViperProjectInfo projectInfo = srv.getProjectInfo(projectName);
					
					if( projectInfo != null && projectInfo.isRegistered() ) {
						theItem = new MenuItem(menu, SWT.NONE);
				        theItem.setText("unregister project");
				        theItem.setData(OpenCPICategory.project);
				        theItem.addSelectionListener(fileSystemAdapter);
						
					}
					else {
						theItem = new MenuItem(menu, SWT.NONE);
				        theItem.setText("register project");
				        theItem.setData(OpenCPICategory.project);
				        theItem.addSelectionListener(fileSystemAdapter);
					}
						
					break;
				case protocol:
					open.setEnabled(true);
					build.setEnabled(false);
					clean.setEnabled(false);
					
					theItem = new MenuItem(menu, SWT.NONE);
			        theItem.setText("new protocol");
			        theItem.setData(OpenCPICategory.protocol);
			        theItem.addSelectionListener(wizardAdapter);
					break;
				case test:
					open.setEnabled(true);
					break;
				case tests:
					if(cat == OpenCPICategory.tests)
						delete.setEnabled(false);
					
					break;
				case worker:
					open.setEnabled(true);
					
					theItem = new MenuItem(menu, SWT.NONE);
			        theItem.setText("new worker");
			        theItem.setData(OpenCPICategory.worker);
			        theItem.addSelectionListener(wizardAdapter);
					break;
					
				case cards:
				case devices:
					delete.setEnabled(false);
					break;
					
				default:
					break;
					

					
				}
	        }
	    });

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
