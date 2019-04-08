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

package av.proj.ide.internal;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.swt.widgets.Display;

import av.proj.ide.internal.EnvBuildTargets.HdlPlatformInfo;
import av.proj.ide.internal.EnvBuildTargets.HdlVendor;
import av.proj.ide.internal.EnvBuildTargets.RccPlatformInfo;
import av.proj.ide.internal.OpenCpiAssets.OcpiAssetDifferences;


//class ProjectCloseListener implements IPartListener2 {
//@Override
//public void partActivated(IWorkbenchPartReference arg0) {}
//@Override
//public void partBroughtToTop(IWorkbenchPartReference arg0) {}
//
//@Override
//public void partClosed(IWorkbenchPartReference arg0) {
//	String id = arg0.getId();
//	if(id.endsWith("AVPSEditor")) {
//		IWorkbenchPart part = arg0.getPart(true);
//		((AVPSEditor)part).deRegisterChangeNotice();
//		System .out.println(((AVPSEditor)part).getProjectName() + " closed");
//	}
//}
//
//@Override
//public void partDeactivated(IWorkbenchPartReference arg0) {}
//@Override
//public void partHidden(IWorkbenchPartReference arg0) {}
//@Override
//public void partInputChanged(IWorkbenchPartReference arg0) {
//	System .out.println("input changed");
//}
//@Override
//public void partOpened(IWorkbenchPartReference arg0) {}
//@Override
//public void partVisible(IWorkbenchPartReference arg0) {}
//}


/**
 * This and surrounding classes have evolved over time and much of the evolution was an effort
 * to reduce class size.  This class now primarily serves as the central point for the plugin 
 * application to obtain information. It primarily supports the OpenCPI Projects view and the
 * OpenCPI XML editors.
 * 
 *  The class now relies on several colleague classes that have more focused responsibility.
 *  The most recent addition to the support group is the LoadOpenCPIEnvironment class, 
 *  developed to optimize gathering and assembling environment data.  Others in the group:
 *  
 *  - OpenCpiAssets has become the repository for project assets.  It also holds the logic
 *    to construct the asset objects used by the UI for presentation and it holds the built
 *    target information. It also supports the asset operations provided by the Angryviper
 *    wizard.
 *    
 *  - OpencpiEnvService functions as a bridge to the ocpidev show interface used to obtain
 *    project registration and component information as well as ocpidev create and delete
 *    commands. Currently there is a mix of data gathering resources used in this class.
 *    In the future this  as we move to solely using ocpidev show work with the framework,
 *    these tools will move back to OpenCpiAssets.
 */
public class AngryViperAssetService {

	protected OpenCpiAssets assetsRepo = null;
	protected OpencpiEnvService environmentService = null;
	protected OcpiAssetDifferences newSnapshotDiff = null;
	
	private static AngryViperAssetService instance = null;

//	private File   scriptsDir;
//	private String cdkPath;
	protected boolean assetsBuilt = false;
	
	private AngryViperAssetService() {
    	environmentService = new OpencpiEnvService();
    	
		// Add Listeners to detect changes project assets.
		
//        IWorkspace ws =  ResourcesPlugin.getWorkspace();
//		IResourceChangeListener listener = new MyResourceChangeReporter();
//		// Note subsequent calls to this make no change.
//	    ws.addResourceChangeListener(listener, IResourceChangeEvent.POST_CHANGE);
//		
//		IPartListener2 listen = new ProjectCloseListener ();
//		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
//		page.addPartListener(listen);
	};


	public static AngryViperAssetService getInstance() {
		// Ran into a conflict where assets were constructed twice
		// and flow of control was the main thread?  Somewhere things
		// were handed off to another thread and constructAssets was 
		// entered twice.  Created a lock to solve the problem.
		TLock.acquire();
		if(instance == null) {
			instance = new AngryViperAssetService();
			LoadOpenCPIEnvironment load = new LoadOpenCPIEnvironment();
			load.loadDataStores();
			instance.assetsRepo = load.assetsRepo;
			instance.environmentService = load.environmentService;
			System.out.println(instance.assetsRepo.getProjectsMap().size() + " projects collected.");
		}
		TLock.release();

		return instance;
	}
	
	public Collection<HdlPlatformInfo> getHdlPlatforms() {
		return assetsRepo.hdlPlatforms;
	}
	public Collection<RccPlatformInfo> getRccPlatforms() {
		return assetsRepo.rccPlatforms;
	}
	public Collection<HdlVendor> getHdlTargets() {
		return assetsRepo.hdlVendors;
	}
	
	public OpencpiEnvService getEnvironment() {
		return environmentService;
	}
	/***
	 * This public interface is used by the UI to setup the ANGRYVIPER
	 * views. Information maintained in these object are relative to 
	 * open projects in the eclipse workspace.
	 */
	public Map<String, AssetModelData> getWorkspaceProjects() {
		//System.out.println("Projects Map requested.");
		return assetsRepo.getProjectsMap();
	}

	public AssetModelData lookupAsset(AngryViperAsset asset) {
		return assetsRepo.assetLookup.get(asset);
	}
	
	public Set<String> getComponentsInLibrary(String projectName, String libraryName) {
		AssetModelData project = assetsRepo.getProject(projectName);
		TreeSet<String> comps = new TreeSet<String>();
		
		AssetModelData componentsFolder = null;
		AssetModelData library = null;
		
		for(AssetModelData child : project.getChildList()) {
			switch (child.asset.category) {
				default:
					continue;
				// Project will have one or the other.
				case componentsLibrary:
				case componentsLibraries:
					componentsFolder = child;
					break;
			}
		}
		if(componentsFolder == null)
			return comps;
		
		if(libraryName.equals(OpenCPICategory.componentsLibrary.getFrameworkName())) {
			library = componentsFolder;
		}
		else {
			for(AssetModelData lib :componentsFolder.getChildList()) {
				if(libraryName.equals(lib.asset.assetName)) {
					library = lib;
				}
			}
		}
		
		if(library != null) {
			for(AssetModelData child : library.getChildList()) {
				if(child.asset.category == OpenCPICategory.specs) {
					for(AssetModelData comp : child.getChildList()) {
						if(comp.asset.category == OpenCPICategory.component) {
							comps.add(comp.asset.assetName);
						}
					}
				}
			}
			
		}
		return comps;
	}
	
	public AngryViperAsset createAsset (OpenCPICategory type, CreateAssetFields assetElem, StringBuilder sb) {
		AssetModelData  newAssetModel = null;
		AngryViperAsset newAsset = null;
		Set<AssetModelData> addedChangeset = assetsRepo.createAsset(type, assetElem, sb);
		if(addedChangeset == null) {
			return newAsset;
		}
		int numberOfNewAssets = addedChangeset.size();
		Iterator<AssetModelData> itor = addedChangeset.iterator();
		// The change set may include several layers up to the main holding element.
		for (int i = 1; i<numberOfNewAssets; i++) {
			itor.next();
		}
		if(itor.hasNext()) {
			newAssetModel = itor.next();
			newAsset = newAssetModel.asset;
		}
		if(type == OpenCPICategory.worker) {
			environmentService.addHdlWorker(newAsset);
		}
		else if(type == OpenCPICategory.component || type == OpenCPICategory.protocol) {
			environmentService.addComponent(newAsset);
		}
		else if(type == OpenCPICategory.project) {
			// Since this service maintains the environment service, it is responsible for adding
			// a new project to the projects repo using env information.
			LoadOpenCPIEnvironment load = new LoadOpenCPIEnvironment();
			load.loadCoreServices();
			environmentService = load.environmentService;
			AngryViperProjectInfo info = environmentService.getProjectInfo(newAsset.assetName);
			if(info != null) {
				newAsset.projectLocation.packageId = info.packageId;
				newAsset.qualifiedName = info.packageId;
				assetsRepo.addProject(newAsset, newAssetModel);
			}
		}
		
		if(projectsViewUpdate != null) { 
			Display.getDefault().asyncExec(new Runnable(){
				public void run() {
					projectsViewUpdate.processChangeSet(new LinkedHashSet<AssetModelData>(), addedChangeset);
				}
			});
			
		}
		return newAsset;
	}
	
	public boolean deleteAsset (AngryViperAsset asset, StringBuilder sb) {
		OcpiAssetDifferences diffs = assetsRepo.deleteAsset(asset, sb);
		if(diffs.removedChangeset == null) {
			return false;
		}
		newSnapshotDiff = diffs;
		if(asset.category == OpenCPICategory.worker) {
			environmentService.removeHdlWorker(asset);
		}
		else if(asset.category == OpenCPICategory.component || asset.category == OpenCPICategory.protocol) {
			environmentService.removeComponent(asset);
		}
		else if(asset.category == OpenCPICategory.project) {
			LoadOpenCPIEnvironment load = new LoadOpenCPIEnvironment();
			load.loadCoreServices();
			environmentService = load.environmentService;
		}
		if(projectsViewUpdate != null) { 
			Display.getDefault().asyncExec(new Runnable(){
				public void run() {
					projectsViewUpdate.processChangeSet(diffs.removedChangeset, new LinkedHashSet<AssetModelData>());
				}
			});
		}
		
		return true;
	}
	/***
	 * Data synchronization processing.  The projects view registers for 
	 * change updates. Since the updates may occur from other threads
	 * the projects view needs to be able to signal update completion.
	 */

	public interface ModelDataUpdate {
		public void processChangeSet(Set<AssetModelData> removedAssets, Set<AssetModelData> newAssets);
	}
	public interface AckModelDataUpdate {
		public void updateCompleted();
	}
	public interface BuildPlatformUpdate {
		public void addHdlPlatforms(List<HdlPlatformInfo> hdlPlatforms);
		public void removeHdlPlatforms(List<HdlPlatformInfo> hdlPlatforms);
		public void addRccPlatforms(List<RccPlatformInfo> rccPlatforms);
		public void removeRccPlatforms(List<RccPlatformInfo> rccPlatforms);
	}
	
	private BuildPlatformUpdate platformUpdater = null;
	private ModelDataUpdate projectsViewUpdate = null;

	public AckModelDataUpdate registerProjectModelRefresh(ModelDataUpdate presentationUpdate) {
		projectsViewUpdate = presentationUpdate;
		return new AckModelDataUpdate(){
			public void updateCompleted() {
				processUpdateCompleted();
			}
		};
	}

	public boolean registerProject(AngryViperAsset asset, StringBuilder s) {
		boolean result = environmentService.registerProject(asset, s);
        if(result == true) {
			LoadOpenCPIEnvironment load = new LoadOpenCPIEnvironment();
			load.loadCoreServices();
			environmentService = load.environmentService;
			
			AngryViperProjectInfo info = environmentService.lookupProjectByPath(asset.projectLocation.projectPath);
			asset.projectLocation.packageId = info.packageId;
			asset.qualifiedName = info.packageId;
			asset.assetDetails = info;
			
			OpenCpiAssets otherOcpiAssets = new OpenCpiAssets();
			otherOcpiAssets.setEnvTargets(load.getEnvBuildInfo());
			
			OcpiAssetDifferences platformDiff = assetsRepo.getPlatformDifferences (otherOcpiAssets);
			if(platformDiff.areBuildPlatformsDifferent()) {
				assetsRepo.hdlPlatforms = otherOcpiAssets.hdlPlatforms;
				assetsRepo.rccPlatforms = otherOcpiAssets.rccPlatforms;
				signalPlatformChanges(platformDiff);
			}
        }
		return result;
	}
	
	public boolean unregisterProject(AngryViperAsset asset, StringBuilder s) {
		boolean result = environmentService.unregisterProject(asset, s);
        if(result == true) {
			LoadOpenCPIEnvironment load = new LoadOpenCPIEnvironment();
			load.loadCoreServices();
			environmentService = load.environmentService;
			
			AngryViperProjectInfo info = (AngryViperProjectInfo)asset.assetDetails;
			if(info != null) {
				info.isRegistered = false;
			}
			OpenCpiAssets otherOcpiAssets = new OpenCpiAssets();
			otherOcpiAssets.setEnvTargets(load.getEnvBuildInfo());
			
			OcpiAssetDifferences platformDiff = assetsRepo.getPlatformDifferences (otherOcpiAssets);
			if(platformDiff.areBuildPlatformsDifferent()) {
				assetsRepo.hdlPlatforms = otherOcpiAssets.hdlPlatforms;
				assetsRepo.rccPlatforms = otherOcpiAssets.rccPlatforms;
				signalPlatformChanges(platformDiff);
			}
        }
		return result;
	}
	protected void signalPlatformChanges(OcpiAssetDifferences diffs) {
		if(platformUpdater != null) {
			if(! diffs.hdlAddList.isEmpty()) {
				platformUpdater.addHdlPlatforms(diffs.hdlAddList);
			}
			if(! diffs.hdlRemoveList.isEmpty()) {
				platformUpdater.removeHdlPlatforms(diffs.hdlRemoveList);
			}
			if(! diffs.rccAddList.isEmpty()) {
				platformUpdater.addRccPlatforms(diffs.rccAddList);
			}
			if(! diffs.rccRemoveList.isEmpty()) {
				platformUpdater.removeRccPlatforms(diffs.rccRemoveList);
			}
		}
	}
	
	public void registerHdlPlatformRefresh(BuildPlatformUpdate platformUpdater) {
		this.platformUpdater = platformUpdater;
	}
	
	public void synchronizeWithFileSystem() {
		// Get a fresh snapshot of the OpenCPI Env.
		LoadOpenCPIEnvironment load = new LoadOpenCPIEnvironment();
		load.loadDataStores();
		environmentService = load.environmentService;
		
		OpenCpiAssets ocpiAssets = load.assetsRepo;
		
		if(projectsViewUpdate == null) {
			// The OpenCPI Projects view is not open.  No further
			// processing needed.
			assetsRepo = ocpiAssets;
			return;
		}
		OcpiAssetDifferences diffs = assetsRepo.diff(ocpiAssets);
		if(! (diffs.areAssetsDifferent() || diffs.areBuildPlatformsDifferent()) )
			return;
		
		if(diffs.areAssetsDifferent()) {
			newSnapshotDiff = diffs;
			projectsViewUpdate.processChangeSet(diffs.removedChangeset, diffs.addedChangeset);
		}
		
		if(diffs.areBuildPlatformsDifferent()) {
			if(platformUpdater != null) {
				if(! diffs.hdlAddList.isEmpty()) {
					platformUpdater.addHdlPlatforms(diffs.hdlAddList);
				}
				if(! diffs.hdlRemoveList.isEmpty()) {
					platformUpdater.removeHdlPlatforms(diffs.hdlRemoveList);
				}
				if(! diffs.rccAddList.isEmpty()) {
					platformUpdater.addRccPlatforms(diffs.rccAddList);
				}
				if(! diffs.rccRemoveList.isEmpty()) {
					platformUpdater.removeRccPlatforms(diffs.rccRemoveList);
				}
			}
			assetsRepo.hdlPlatforms = ocpiAssets.hdlPlatforms;
			assetsRepo.rccPlatforms = ocpiAssets.rccPlatforms;
		}
		
		// Reset the specs, applicationComponents, and projects list to be reloaded
		// no matter what.

	}
	
	// Why was it done this way?
	// As memory serves I wanted to ensure the UI data model reflected 
	// the changes before updating the repo.  This way a second refresh
	// might capture it.  Also, existing model objects hold UI objects
	// so don't blow away the existing model data, update it with the
	// changes.
	
	protected void processUpdateCompleted() {
		if(newSnapshotDiff != null) {
			if(! newSnapshotDiff.removedProjects.isEmpty()){
				for(String project : newSnapshotDiff.removedProjects.keySet()) {
					assetsRepo.removeProject(project);
				}
			}
			if(! newSnapshotDiff.addedChangeset.isEmpty()) {
				for(AssetModelData newAssetModel : newSnapshotDiff.addedChangeset) {
					loadNewAsset(newAssetModel);
					if(newAssetModel.asset.category == OpenCPICategory.project) {
						assetsRepo.addProject(newAssetModel.asset, newAssetModel);
					}
				}
			}
			if(! newSnapshotDiff.removedChangeset.isEmpty()) {
				for(AssetModelData assetModel : newSnapshotDiff.removedChangeset) {
					removeAsset(assetModel);
				}
			}
			newSnapshotDiff = null;
		}
	}
	
	protected void loadNewAsset(AssetModelData newAssetModel) {
		for(AssetModelData childAssetModel : newAssetModel.childList ) {
			loadNewAsset(childAssetModel);
		}
		assetsRepo.assetLookup.put(newAssetModel.asset, newAssetModel);
	}

	protected void removeAsset(AssetModelData removedAssetModel) {
		for(AssetModelData childAssetModel : removedAssetModel.childList ) {
			removeAsset(childAssetModel);
		}
		assetsRepo.assetLookup.remove(removedAssetModel.asset);
	}


	public AngryViperAsset[] getProjectLibraries(String projectName) {
		AssetModelData project = assetsRepo.getProject(projectName);
		AngryViperProjectInfo info = environmentService.getProjectInfo(projectName);
		// This may be unnecessary now. All references to a project seem to use the
		// qualified name (project package-id);
		if(project == null) {
			if(info != null) {
				project = assetsRepo.getProject(info.eclipseName);
				if(project == null) {
					project = assetsRepo.getProject(info.name);
					if(project == null) {
						project = assetsRepo.getProject(info.packageId);
					}
				}
			}
		}
		if(project == null) {
			return new AngryViperAsset[0];
		}
		
		for(AssetModelData child : project.getChildList()) {
			if(child.asset.category == OpenCPICategory.componentsLibrary) {
				return new AngryViperAsset[] {child.asset};
			}
			else if (child.asset.category == OpenCPICategory.componentsLibraries) {
				AngryViperAsset[] libs = new AngryViperAsset[child.childList.size()];
				int idx = 0;
				for(AssetModelData lib : child.childList) {
					libs[idx++] = lib.asset;
				}
				return libs;
			}
		}
		
		return new AngryViperAsset[0];
	}

}


