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

package av.proj.ide.avps.internal;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.sapphire.ElementList;
import org.eclipse.sapphire.modeling.ResourceStoreException;
import org.eclipse.sapphire.modeling.xml.RootXmlResource;
import org.eclipse.sapphire.modeling.xml.XmlResourceStore;
import org.eclipse.swt.widgets.TreeItem;

import av.proj.ide.avps.internal.EnvBuildTargets.HdlPlatformInfo;
import av.proj.ide.avps.internal.EnvBuildTargets.HdlVendor;
import av.proj.ide.avps.internal.EnvBuildTargets.RccPlatformInfo;
import av.proj.ide.avps.xml.Components;
import av.proj.ide.avps.xml.Hdl;
import av.proj.ide.avps.xml.Hdl.Assembly;
import av.proj.ide.avps.xml.Hdl.Platform;
import av.proj.ide.avps.xml.Hdl.Primitive;
import av.proj.ide.avps.xml.Library;
import av.proj.ide.avps.xml.Project;
import av.proj.ide.avps.xml.Project.Application;
import av.proj.ide.avps.xml.Test;
import av.proj.ide.avps.xml.Worker;


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
	
	//protected List<Project> workspaceProjects;
	protected Map<String, AssetModelData> projects;
	
	// Leverages load order.
	protected LinkedHashMap<AngryViperAsset, AssetModelData> assetLookup;

	protected List<HdlVendor> hdlVendors;
	protected List<HdlPlatformInfo> hdlPlatforms;
	protected List<RccPlatformInfo> rccPlatforms;
	
//	protected Map<String, List<String>> specsLookup = null;
//	protected Map<String, String>  allSpecs = null;
//	protected Set<String> allProtocols = null;
	protected Set<String> allHdlWorkers = null;
	
	protected RegisteredProjectSearchTool registerProjectsTool = null;

	private File   scriptsDir;
	private String cdkPath;
	private static AngryViperAssetService instance = null;
	private boolean assetsBuilt = false;
	
	private AngryViperAssetService(boolean buildAssets) {
		if(! buildAssets)
			return;
		
		buildAssets();
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
			instance = new AngryViperAssetService(true);
		}
		// This is such a hack but something needed to be
		// done quickly.  Assets construction takes noticeably
		// long when someone is just using the editors. 
		if(! instance.assetsBuilt) {
			instance.buildAssets();
		}
		return instance;
	}
	
	public Map<String, AssetModelData> getProjects() {
		return projects;
	}
	
	public AssetModelData lookupAsset(AngryViperAsset asset) {
		return assetLookup.get(asset);
	}
	
	public List<HdlPlatformInfo> getHdlPlatforms() {
		return hdlPlatforms;
	}
	public List<HdlVendor> getHdlTargets() {
		return hdlVendors;
	}
	public List<RccPlatformInfo> getRccPlatforms() {
		return rccPlatforms;
	}
	
	public static RegisteredProjectSearchTool getRegistedProjectTool() {
		if(instance == null) {
			instance = new AngryViperAssetService(false);
		}
		if(instance.registerProjectsTool == null) {
			synchronized (instance) {
				instance.registerProjectsTool = new RegisteredProjectSearchTool();
			}
		}
		return instance.registerProjectsTool;
	}
	
	public static  Collection<String> getApplicationComponents() {
		if(instance == null) {
			instance = new AngryViperAssetService(false);
		}
		if(instance.registerProjectsTool == null) {
			getRegistedProjectTool();
		}
		return instance.registerProjectsTool.getApplicationComponents();
	}
	
	public static String getApplicationSpecName(String projectName, String libName, String specFileName) {
		if(instance == null) {
			instance = new AngryViperAssetService(false);
		}
		if(instance.registerProjectsTool == null) {
			getRegistedProjectTool();
		}
		return instance.registerProjectsTool.getApplicationSpecName(projectName, libName, specFileName);
	}
	
	public Set<String> getAllHdlWorkers() {
		if(allHdlWorkers == null) {
			allHdlWorkers = new HashSet<String>();
			RegisteredProjectSearchTool search = new RegisteredProjectSearchTool();
			search.searchForAdapters();
			allHdlWorkers.addAll(search.getHdlAdapters());
			
			for(AngryViperAsset asset : assetLookup.keySet()) {
				if(asset.category == OcpiAssetCategory.component) {
					if(asset.buildName.endsWith(".hdl")) {
						int len = asset.buildName.length();
						allHdlWorkers.add(asset.buildName.substring(0, len -4));
					}
				}
			}
		}
		return allHdlWorkers;
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
	public interface HdlPlatformUpdate {
		public void addHdlPlatforms(List<HdlPlatformInfo> hdlPlatforms);
		public void removeHdlPlatforms(List<HdlPlatformInfo> hdlPlatforms);
	}
	
	private HdlPlatformUpdate platformUpdater = null;
	private ModelDataUpdate projectsViewUpdate = null;
	private LinkedHashMap<AngryViperAsset, AssetModelData> addedLookup = null;
	private Set<AssetModelData> removeSet = null;
	protected Map<String, AssetModelData> projectsAdded = null;
	protected Map<String, AssetModelData> projectsRemoved = null;

	protected EnvBuildTargets envBuildInfo;
	
	public AckModelDataUpdate registerProjectModelRefresh(ModelDataUpdate presentationUpdate) {
		projectsViewUpdate = presentationUpdate;
		return new AckModelDataUpdate(){
			public void updateCompleted() {
				processUpdateCompleted();
			}
		};
	}
	
	public void registerHdlPlatformRefresh(HdlPlatformUpdate platformUpdater) {
		this.platformUpdater = platformUpdater;
	}
	
	
	protected void buildAssets() {
		projects = new HashMap <String, AssetModelData> ();
		envBuildInfo = new EnvBuildTargets();
		cdkPath = System.getenv("OCPI_CDK_DIR");
		String scripts = cdkPath + "/scripts";
		scriptsDir = new File(scripts);
		getBuildPlatformsAndTargets();
		//workspaceProjects = new ArrayList<Project> ();
		assetLookup = getAvProjects(projects, true);
		assetsBuilt = true;
	}
	
	protected void runGenProject(String projectPath) throws IOException, InterruptedException {
		String [] cmdp = {"python", "./genProjMetaData.py",  projectPath};
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

	protected void getBuildPlatformsAndTargets() {
		hdlVendors = envBuildInfo.getHdlVendors();
		hdlPlatforms = envBuildInfo.getHdlPlatforms();
		rccPlatforms = envBuildInfo.getRccPlatforms();
	}

	
	public void synchronizeWithFileSystem(TreeItem[] selections) {
		
		
		Map<String, AssetModelData> projects2 = new HashMap <String, AssetModelData> ();
		LinkedHashMap<AngryViperAsset, AssetModelData> latestAssetsLookup = null;
		
		// this synchronizes with the file system.
//		if(selections.length == 0) {
			latestAssetsLookup = getAvProjects(projects2, true);
//		}
//		else {
//			latestAssets = getAvProjectsForThese(projects2, selections);
//		}
		
		
		// Figure out what changed.  First look for new projects.
		Set<AssetModelData> addedAssets = new LinkedHashSet<AssetModelData>();
		Set<AssetModelData> addedChangeset = new LinkedHashSet<AssetModelData>();

		Map<String, AssetModelData> newProjects = new HashMap <String, AssetModelData> ();
		for(String projectName: projects2.keySet()) {
			if( ! projects.containsKey(projectName))
				newProjects.put(projectName, projects2.get(projectName));
		}
		if(newProjects.size() > 0) {
			addedChangeset.addAll(newProjects.values());
			// Will need to cull out the assets contained in the new projects.
		}
		
		// New see what else might be new.
		LinkedHashMap<AngryViperAsset, AssetModelData> newAssetLookup = 
			     new LinkedHashMap <AngryViperAsset,AssetModelData> ();
		for(AngryViperAsset asset : latestAssetsLookup.keySet()) {

			AssetModelData newAsset = latestAssetsLookup.get(asset);
			if( ! assetLookup.containsKey(asset)) {
				addedAssets.add(newAsset);
				newAssetLookup.put(asset, newAsset);
				// Components Hack
				if(asset.category != OcpiAssetCategory.project &&
					asset.parent.assetName.equals(OcpiAssetCategory.components.name())) {
					// Need to get the component
					AngryViperAsset componentsParent = asset.parent.parent;
					asset.parent = componentsParent;
				}
				if(assetLookup.containsKey(asset.parent)) {
					// Use that parent.
					AssetModelData parent = assetLookup.get(asset.parent);
					if(parent != null)
						asset.parent = parent.asset;
				}
				// don't gather the assets contained in the new projects.
				if( ! newProjects.containsKey(asset.location.projectName)) {
					addedChangeset.add(newAsset);
				}
			}
		}

		// See if projects have been removed.
		Map<String, AssetModelData> removedProjects = new HashMap <String, AssetModelData> ();
		Set<AssetModelData> removedChangeset = new HashSet<AssetModelData>();

		for(String projectName: projects.keySet()) {
			if( ! projects2.containsKey(projectName))
				removedProjects.put(projectName, projects.get(projectName));
		}
		if(removedProjects.size() > 0) {
			removedChangeset.addAll(removedProjects.values());
		}
		
		Set<AssetModelData> assetsRemoved = new HashSet<AssetModelData>();
		for(AngryViperAsset asset : assetLookup.keySet()) {
			AssetModelData currentAsset = assetLookup.get(asset);
			if( ! latestAssetsLookup.containsKey(asset)) {
				assetsRemoved.add(currentAsset);
				// don't gather the assets contained in the removed projects.
				if( ! removedProjects.containsKey(asset.location.projectName))
					removedChangeset.add(currentAsset);
			}
		}
		List<HdlPlatformInfo> hdlPlats = envBuildInfo.getHdlPlatforms();
		ArrayList<HdlPlatformInfo> changeList = new ArrayList<HdlPlatformInfo> ();
		Boolean add_remove = null;
		if(hdlPlats.size() > hdlPlatforms.size() ){
			add_remove = true;
			for (HdlPlatformInfo platform : hdlPlats) {
				if(! hdlPlatforms.contains(platform)) {
					changeList.add(platform);
				}
			}
		}
		else if(hdlPlats.size() < hdlPlatforms.size() ){
			add_remove = false;
			for (HdlPlatformInfo platform : hdlPlatforms) {
				if(! hdlPlats.contains(platform)) {
					changeList.add(platform);
				}
			}
		}
		
		// Reset the specs, applicationComponents, and projects list to be reloaded
		// no matter what.
		registerProjectsTool = null;
		
		// Nothing Changed 
		if(addedChangeset.size() == 0 && removedChangeset.size() == 0 && add_remove == null)
			return;
		
		if(newAssetLookup.size() > 0)
			addedLookup = newAssetLookup;	

		if(assetsRemoved.size() > 0)
			removeSet = assetsRemoved;
		
		if(newProjects.size() > 0)
			projectsAdded = newProjects;
		
		if(removedProjects.size() > 0)
			projectsRemoved = removedProjects;
		
		
		// Signal the change
		if(projectsViewUpdate != null)
			projectsViewUpdate.processChangeSet(removedChangeset, addedChangeset);
	
		// Don't add or removed items
		// until presentation has made the update.
		
		if(platformUpdater == null)
			return;
		
		if(add_remove != null) {
			hdlPlatforms = hdlPlats;
			
			if(add_remove) {
				platformUpdater.addHdlPlatforms(changeList);
			}
			else {
				platformUpdater.removeHdlPlatforms(changeList);
			}
		}
	}
	
/***
 * Currently not in use.  May have bugs.
 
	private LinkedHashMap<AngryViperAsset, AssetModelData> getAvProjectsForThese(Map<String, AssetModelData> projectLookup, TreeItem[] selections) {
		Set<String> projectNames = new HashSet<String>();
		for( int i=0; i < selections.length; i++) {
			TreeItem item = selections[i];
			String projectName = getAssetProject(item);
			if(projectName != null) {
				projectNames.add(projectName);
			}
		}
		LinkedHashMap<AngryViperAsset, AssetModelData> lookup = new LinkedHashMap<AngryViperAsset, AssetModelData> ();
		
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		
		IProject [] projects = workspace.getRoot().getProjects();
		IProject project;
		for(int i= 0; i<projects.length; i++) {
			project = projects[i];
			if( projectNames.contains(project.getName()) ){
				String projectName = project.getName();
				IPath path = project.getLocation();
				IFile projectFile = project.getFile("project.xml");
				InputStream is = null;
				if(projectFile != null) {
					try {
						String projpath = path.toOSString();
						runGenProject(projpath);
						is = projectFile.getContents();
						XmlResourceStore store = new XmlResourceStore(is);
						RootXmlResource xmlResource = new RootXmlResource(store);
						Project proj = Project.TYPE.instantiate(xmlResource);
						AssetLocation location = new AssetLocation(projectName, projpath);
						AssetModelData projectData = new AssetModelData(new AngryViperAsset(projectName, location, OcpiAssetCategory.project));
						projectLookup.put(projectName, projectData);
						LinkedHashMap<AngryViperAsset, AssetModelData> projLookup = buildData(proj, projectData);
						if(projLookup != null)
							lookup.putAll(projLookup);
					} catch (CoreException e) {
					} catch (ResourceStoreException e) {
					} catch (IOException e) {
					} catch (InterruptedException e) {
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
		}
		return lookup;
	}
****/
	protected void processUpdateCompleted() {
		if(addedLookup != null){
			assetLookup.putAll(addedLookup);
			addedLookup = null;
		}
		if(removeSet != null) {
			for(AssetModelData asset : removeSet) {
				assetLookup.remove(asset.asset);
				
			}
			removeSet = null;
		}
		if(projectsAdded != null) {
			projects.putAll(projectsAdded);
			projectsAdded = null;
		}
		if(projectsRemoved != null) {
			for(String removedProject : projectsRemoved.keySet()) {
				projects.remove(removedProject);
			}
			projectsRemoved = null;
		}
	}
	
	/**
	 * Central method to obtain the current workspace projects and assemble
	 * the projects data model.
	 * @param projectLookup - a project map to be populated.
	 * @param buildLookup - a flag to build an asset lookup or not.
	 */
	protected LinkedHashMap<AngryViperAsset, AssetModelData> getAvProjects(Map<String, AssetModelData> projectLookup,
			             boolean buildLookup){
		
		LinkedHashMap<AngryViperAsset, AssetModelData> lookup = null;
		if(buildLookup)
			lookup = new LinkedHashMap<AngryViperAsset, AssetModelData> ();
		
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
					
					if(! projectFile.exists()) {
						AvpsResourceManager.getInstance().writeToNoticeConsole("Attempting to read OpenCPI project metadata. Project "+ project.getName() +" does not appear to be an OpenCPI project. Continuing.");
						continue;
					}
					is = projectFile.getContents(true);
					XmlResourceStore store = new XmlResourceStore(is);
					RootXmlResource xmlResource = new RootXmlResource(store);
					Project proj = Project.TYPE.instantiate(xmlResource);
					
					AssetLocation location = new AssetLocation(projectName, projpath);
					AssetModelData projectData = new AssetModelData(new AngryViperAsset(projectName, location, OcpiAssetCategory.project));
					projectLookup.put(projectName, projectData);
					Map<AngryViperAsset, AssetModelData> projLookup = buildData(proj, projectData);
					if(buildLookup && projLookup != null)
						lookup.putAll(projLookup);
					
				} catch (CoreException | ResourceStoreException | IOException | InterruptedException e) {
					AvpsResourceManager.getInstance().writeToNoticeConsole("Error obtaining project metadata. --> " + e.toString() );
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
		return lookup;
	}
	
	/**
	 * The central routine to build the asset data model from the project XML.
	 * @param projectXmlModel - the XML document for the project.
	 * @param project - the project model data container to be populated.
	 * @return - an asset lookup map for this project.
	 */
	protected static LinkedHashMap<AngryViperAsset, AssetModelData> buildData(Project projectXmlModel, AssetModelData project) {
		
		LinkedHashMap<AngryViperAsset, AssetModelData> lookup = new LinkedHashMap<AngryViperAsset, AssetModelData> ();
		
		AssetLocation location = project.asset.location;
		lookup.put(project.asset, project);
		
		ElementList<Application> apps = projectXmlModel.getApplications();
		if(apps.size() > 0) {
			AssetModelData asset = new 
				AssetModelData(new AngryViperAsset(OcpiAssetCategory.applications.getListText(), location, OcpiAssetCategory.applications));
			asset.asset.parent = project.asset;
			project.childList.add(asset);
			lookup.put(asset.asset, asset);
			
			for(Application childAsset : apps) {
				String name = childAsset.getName().content();
				AssetModelData c = new 
						AssetModelData(new AngryViperAsset(name, location, OcpiAssetCategory.application));
				c.asset.buildName = name;
				c.asset.parent = asset.asset;
				asset.childList.add(c);
				lookup.put(c.asset, c);
			}
		}
		Hdl hdlElement = projectXmlModel.getHdl();
		ElementList<Assembly> assemblies = hdlElement.getAssemblies();
		if(assemblies.size() > 0 ) {
			AssetModelData asset = new 
				AssetModelData(new AngryViperAsset(OcpiAssetCategory.assemblies.getListText(), location, OcpiAssetCategory.assemblies));
			asset.asset.parent = project.asset;
			project.childList.add(asset);
			lookup.put(asset.asset, asset);
			
			for(Assembly childElement : assemblies) {
				String name = childElement.getName().content();
				AssetModelData c = new 
						AssetModelData(new AngryViperAsset(name, location, OcpiAssetCategory.assembly));
				c.asset.buildName = name;
				c.asset.parent = asset.asset;
				asset.childList.add(c);
				lookup.put(c.asset, c);
			}
		}
		
		ElementList<Platform> platforms = hdlElement.getPlatforms();
		if(platforms.size() > 0 ) {
			AssetModelData asset = new 
				AssetModelData(new AngryViperAsset(OcpiAssetCategory.platforms.getListText(), location, OcpiAssetCategory.platforms));
			asset.asset.parent = project.asset;
			project.childList.add(asset);
			lookup.put(asset.asset, asset);
			
			for(Platform childElement : platforms) {
				String name = childElement.getName().content();
				AssetModelData c = new 
						AssetModelData(new AngryViperAsset(name, location, OcpiAssetCategory.platform));
				c.asset.buildName = name;
				c.asset.parent = asset.asset;
				asset.childList.add(c);
				lookup.put(c.asset, c);
			}
		}
		
		ElementList<Primitive> primitivies = hdlElement.getPrimitives();
		if(primitivies.size() > 0 ) {
			AssetModelData asset = new 
					AssetModelData(new AngryViperAsset(OcpiAssetCategory.primitives.getListText(), location, OcpiAssetCategory.primitives));
			asset.asset.parent = project.asset;
			project.childList.add(asset);
			lookup.put(asset.asset, asset);
			
			for(Primitive childElement : primitivies) {
				String name = childElement.getName().content();
				AssetModelData c = new 
						AssetModelData(new AngryViperAsset(name, location, OcpiAssetCategory.primitive));
				c.asset.buildName = name;
				c.asset.parent = asset.asset;
				asset.childList.add(c);
				lookup.put(c.asset, c);
			}
		}
		ElementList<Library> hdlLibraries = hdlElement.getLibraries();
		if(hdlLibraries.size() > 0 ) {
			for(Library childElement : hdlLibraries) {
				String name = childElement.getName().content();
				OcpiAssetCategory subcategory = null;
				AssetLocation loc = null;
				if("cards".equals(name)) {
					subcategory = OcpiAssetCategory.card;
					loc = new AssetLocation(location.projectName, location.projectPath + "/hdl/cards");
				}
				else {
					subcategory = OcpiAssetCategory.device;
					loc = new AssetLocation(location.projectName, location.projectPath + "/hdl/devices");
				}
				AssetModelData library = new 
						AssetModelData(new AngryViperAsset(name, location, OcpiAssetCategory.hdlLibrary));
				library.asset.buildName = name;
				library.asset.parent = project.asset;
				project.childList.add(library);
				lookup.put(library.asset, library);
				
				ElementList<Worker> workers = childElement.getWorkers();
				for(Worker worker : workers) {
					String workerName = worker.getName().content();
					AssetModelData w = new 
							AssetModelData(new AngryViperAsset(workerName, loc, subcategory));
					w.asset.buildName = workerName;
					w.asset.parent = library.asset;
					library.childList.add(w);
					lookup.put(w.asset, w);
				}
				
				ElementList<Test> tests = childElement.getTests();
				for(Test test : tests) {
					String testName = test.getName().content();
					AssetModelData t = new 
							AssetModelData(new AngryViperAsset(testName, loc, OcpiAssetCategory.hdlTest));
					t.asset.buildName = testName;
					t.asset.libraryName = library.asset.buildName;
					t.asset.parent = library.asset;
					library.childList.add(t);
					lookup.put(t.asset, t);
				}
			}
		}
		
		Components comps = projectXmlModel.getComponents();

		// AV Rules - workers will either be in a top level components directory
		// (it is the library) or they will be in library subDirectories; never
		// both.
		
		ElementList<Library> libraries = comps.getLibraries();
		
		if(libraries.size() >0) {
			AssetModelData components = new 
					AssetModelData(new AngryViperAsset(OcpiAssetCategory.components.getListText(), location, OcpiAssetCategory.components));
			components.asset.buildName = OcpiAssetCategory.components.toString();
			components.asset.parent = project.asset;
			project.childList.add(components);
			lookup.put(components.asset, components);
			
			for(Library childElement : libraries) {
				String name = childElement.getName().content();
				AssetModelData library = new 
						AssetModelData(new AngryViperAsset(name, location, OcpiAssetCategory.library));
				library.asset.buildName = name;
				library.asset.parent = components.asset;
				components.childList.add(library);
				lookup.put(library.asset, library);
				
				ElementList<Worker> workers = childElement.getWorkers();
				for(Worker worker : workers) {
					String workerName = worker.getName().content();
					AssetModelData w = new 
							AssetModelData(new AngryViperAsset(workerName, location, OcpiAssetCategory.component));
					w.asset.buildName = workerName;
					w.asset.libraryName = library.asset.buildName;
					w.asset.parent = library.asset;
					library.childList.add(w);
					lookup.put(w.asset, w);
				}
				
				ElementList<Test> tests = childElement.getTests();
				for(Test test : tests) {
					String testName = test.getName().content();
					AssetModelData t = new 
							AssetModelData(new AngryViperAsset(testName, location, OcpiAssetCategory.test));
					t.asset.buildName = testName;
					t.asset.libraryName = library.asset.buildName;
					t.asset.parent = library.asset;
					library.childList.add(t);
					lookup.put(t.asset, t);
				}
			}
			// If this project just has the components library - attach it's children to
			// components.  and remove it from the lookup. 
			if(libraries.size() == 1) {
				AssetModelData componentslibrary = components.childList.get(0);
				components.childList = componentslibrary.childList;
				lookup.remove(componentslibrary.asset);
			}
		}
		
		return lookup;
	}
	
}


