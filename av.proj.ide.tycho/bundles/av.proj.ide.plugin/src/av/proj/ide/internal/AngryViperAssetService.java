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

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.sapphire.modeling.ResourceStoreException;
import org.eclipse.sapphire.modeling.xml.RootXmlResource;
import org.eclipse.sapphire.modeling.xml.XmlResourceStore;
import org.eclipse.swt.widgets.Display;

import av.proj.ide.avps.internal.AvpsResourceManager;
import av.proj.ide.avps.xml.Project;
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
 * This class is responsible for assembling AngryViper project information and environment
 * information for presentation.  It is implemented as a singleton. When it is instantiated
 * if looks for open projects in the workspace. Each open project has the project.xml file 
 * regenerated, then read, and the data model for the project is produced. It also obtains
 * the OCPI build targets and platforms from the development environment.  This information
 * is provided to the respective project tool view upon request.
 * 
 * The service is also responsible for assembling workspace change information. Refresh is
 * currently initiated by the user.  In the future this will be automated.
 */
public class AngryViperAssetService {

	protected OpenCpiAssets assetsRepo = null;
	protected OpencpiEnvService environmentService = null;
	protected OcpiAssetDifferences newSnapshotDiff = null;
	
	private static AngryViperAssetService instance = null;

	private File   scriptsDir;
	private String cdkPath;
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
		if(instance == null) {
			instance = new AngryViperAssetService();
		}

		// Ran into a conflict where assets were constructed twice
		// and flow of control was the main thread?  Somewhere things
		// were handed off to another thread and constructAssets was 
		// entered twice.  Created a lock to solve the problem.
		
		if(instance.assetsBuilt == false)
			instance.constructAssets();
		return instance;
	}
	
	public List<HdlPlatformInfo> getHdlPlatforms() {
		return assetsRepo.hdlPlatforms;
	}
	public List<RccPlatformInfo> getRccPlatforms() {
		return assetsRepo.rccPlatforms;
	}
	public List<HdlVendor> getHdlTargets() {
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
			environmentService = new OpencpiEnvService();
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
			environmentService = new OpencpiEnvService();
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
        	environmentService = new OpencpiEnvService();
			AngryViperProjectInfo info = environmentService.getProjectByPath(asset.projectLocation.projectPath);
			asset.projectLocation.packageId = info.packageId;
			asset.qualifiedName = info.packageId;
			asset.assetDetails = info;
			OpenCpiAssets otherOcpiAssets = new OpenCpiAssets();
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
        	environmentService = new OpencpiEnvService();
			AngryViperProjectInfo info = (AngryViperProjectInfo)asset.assetDetails;
			if(info != null) {
				info.isRegistered = false;
			}
			OpenCpiAssets otherOcpiAssets = new OpenCpiAssets();
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
	
	protected void runGenProject(String projectPath) throws IOException, InterruptedException {
		String [] cmdp = {"./genProjMetaData.py",  projectPath};
		Process p=Runtime.getRuntime().exec(cmdp, null, scriptsDir);
		p.waitFor();
		BufferedReader rd = new BufferedReader(new InputStreamReader(p.getInputStream()) );
		String line = rd.readLine();
		while (line != null) {
			//System.out.println(line);
			line = rd.readLine();
		}
		rd = new BufferedReader(new InputStreamReader(p.getErrorStream()) );
		line = rd.readLine();
		while (line != null) {
			//System.out.println(line);
			line = rd.readLine();
		}
	}
	
	protected void constructAssets() {
		// To solve the conflict the current thread
		// is locked out and loading is run in another thread.
		TLock.acquire();
		if(! this.assetsBuilt) {
			cdkPath = System.getenv("OCPI_CDK_DIR");
			String scripts = cdkPath + "/scripts";
			scriptsDir = new File(scripts);
			
			new Thread(new Runnable() {
		        public void run() {
					OpenCpiAssets assets = new OpenCpiAssets();
					loadWorkspaceProjects(assets);
					assetsRepo = assets;
					assetsBuilt = true;
					TLock.release();
		        };
			}).start();
		}
		else {
			TLock.release();
		}
	}
	
	protected void loadWorkspaceProjects(OpenCpiAssets ocpiAssets) {
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IProject [] projects = workspace.getRoot().getProjects();
		IProject project;
		for(int i= 0; i<projects.length; i++) {
			project = projects[i];
			
			if(! project.isOpen()) continue;
			if("RemoteSystemsTempFiles".equals(project.getName())) continue;
			
			String projectName = project.getName();
			IPath path = project.getLocation();
			IFile projectFile = null;
			InputStream is = null;
			try {
				String projpath = path.toOSString();
				runGenProject(projpath);
				projectFile = project.getFile("project.xml");
				
				if(! projectFile.exists()) {
					project.refreshLocal(IResource.DEPTH_ONE, new NullProgressMonitor());
					projectFile = project.getFile("project.xml");
				}
				
				is = projectFile.getContents(true);
				XmlResourceStore store = new XmlResourceStore(is);
				RootXmlResource xmlResource = new RootXmlResource(store);
				Project proj = Project.TYPE.instantiate(xmlResource);

				ProjectLocation location = new ProjectLocation(projectName, projpath);
				//System.out.println("Loading asset for " + projectName);
				ocpiAssets.loadProject(location, proj, environmentService);
				
			} catch (CoreException | ResourceStoreException | IOException | InterruptedException e) {
				AvpsResourceManager.getInstance().writeToNoticeConsole("Error obtaining project metadata. This happens when a project is not an ANGRYVIPER project.\n --> " + e.toString() );
				//e.printStackTrace();
			}
			finally{
				if(is != null) {
					try {
						is.close();
					} catch (IOException e) {
					}
				}
			}
		}
	}

	public void synchronizeWithFileSystem() {
		// Get a fresh snapshot of the OpenCPI Env.
		OpencpiEnvService projEnv = new OpencpiEnvService();
		environmentService = projEnv;
		
		OpenCpiAssets ocpiAssets = new OpenCpiAssets();
		loadWorkspaceProjects(ocpiAssets);
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
		// This name could be the package Id or the name.  Get it right.
		AngryViperProjectInfo info = environmentService.getProjectInfo(projectName);
		if(info != null) {
			projectName = info.name;
		}

		AssetModelData project = assetsRepo.getProject(projectName);
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


