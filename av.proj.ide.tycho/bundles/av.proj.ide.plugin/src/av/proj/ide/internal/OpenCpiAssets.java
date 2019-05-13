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

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.eclipse.sapphire.ElementList;
import org.eclipse.ui.console.MessageConsole;

import av.proj.ide.avps.internal.AvpsResourceManager;
import av.proj.ide.avps.internal.CommandExecutor;
import av.proj.ide.avps.xml.Components;
import av.proj.ide.avps.xml.Hdl;
import av.proj.ide.avps.xml.Hdl.Assembly;
import av.proj.ide.avps.xml.Hdl.Platform;
import av.proj.ide.avps.xml.Hdl.Primitive;
import av.proj.ide.avps.xml.Library;
import av.proj.ide.avps.xml.Project;
import av.proj.ide.avps.xml.Project.Application;
import av.proj.ide.avps.xml.Spec;
import av.proj.ide.avps.xml.Test;
import av.proj.ide.avps.xml.Worker;
import av.proj.ide.internal.AssetDetails.AuthoringModel;
import av.proj.ide.internal.EnvBuildTargets.HdlPlatformInfo;
import av.proj.ide.internal.EnvBuildTargets.HdlVendor;
import av.proj.ide.internal.EnvBuildTargets.RccPlatformInfo;


/**
 * This class is responsible for constructing the data model used by presentation.
 * OpenCPI project XML objects are provided and asset objects are loaded into data stores.

 * It also obtains the OCPI build targets and platforms from the development environment.
 * 
 * The class is also responsible for assembling differences between OpenCpiAssets object to
 * facilitate environment and workspaces update processing for the UI.
 */
public class OpenCpiAssets {
	
	private Map<String, AssetModelData> projects;
	/**
	 * This had to be done to compensate to a lookup by packageId and
	 * a lookup by the name.
	 */
	private Map<String, String> projectNameLookup;
	
	Map<String, AssetModelData> getProjectsMap() {
		return projects;
	}
	AssetModelData getProject(String referredName) {
		String mappedName = projectNameLookup.get(referredName);
		if(mappedName != null) {
			return projects.get(mappedName);
		}
		// Try the other one.
		return projects.get(referredName);
	}
	public void removeProject(String projectRefer) {
		String projectName = projectNameLookup.get(projectRefer);
		AssetModelData projectModel = projects.get(projectName);
		String packageId = null;
		if(projectModel != null) {
			packageId = projectModel.asset.projectLocation.packageId;
		}
		projectNameLookup.remove(projectName);
		if(packageId != null) {
			projectNameLookup.remove(packageId);
		}
		projects.remove(projectName);
	}
	
	public void addProject(AngryViperAsset asset, AssetModelData newAssetModel) {
		String packageId = asset.projectLocation.packageId;
		String projectName = asset.projectLocation.projectName;
		if(packageId != null) {
			projectNameLookup.put(packageId, projectName);
		}
		projectNameLookup.put(projectName, projectName);
		projects.put(projectName, newAssetModel);
	}
	
	// Leverages load order.
	LinkedHashMap<AngryViperAsset, AssetModelData> assetLookup;
	
	Collection<HdlVendor> hdlVendors;
	Collection<HdlPlatformInfo> hdlPlatforms;
	Collection<RccPlatformInfo> rccPlatforms;
	
	OpenCpiAssets() {
		projects = new TreeMap <String, AssetModelData> ();
		assetLookup = new LinkedHashMap<AngryViperAsset, AssetModelData> ();
		projectNameLookup = new HashMap <String, String>();
		
	};
	
	public void setEnvTargets(EnvBuildTargets envBuildInfo) {
		hdlVendors = envBuildInfo.getVendors();
		hdlPlatforms = envBuildInfo.getHdlPlatforms();
		rccPlatforms = envBuildInfo.getRccPlatforms();
	}

	private static ArrayList<String> baseCmd = null;
	private static ArrayList<String> getBaseCmd() {
		if(baseCmd == null) {
			baseCmd = new ArrayList<String>(4);
			baseCmd.add("ocpidev");
			baseCmd.add("-d");
			baseCmd.add(null);
			baseCmd.add("create");
		}
		return baseCmd;
		
	}
	
	AssetModelData createModel(AngryViperAsset newAsset, AngryViperAsset parentAsset) {
		AssetModelData assetModel = null;
		
		if(assetLookup.containsKey(parentAsset)) {
			AssetModelData parentAssetModel =  assetLookup.get(parentAsset);
			newAsset.parent = parentAssetModel.asset;
			assetModel = new AssetModelData(newAsset);
			parentAssetModel.childList.add(assetModel);
		}
		else {
			// parent node needs to be added too.
			AssetModelData parentAssetModel = new AssetModelData(parentAsset);
			parentAssetModel.childList.add(assetModel);
			assetModel = parentAssetModel;
		}
		return assetModel;
	}
	
	Set<AssetModelData> createModel(AngryViperAsset newAsset, String parentName) {
		ArrayList<AssetModelData> changeList = new ArrayList<AssetModelData>(4);
		
		AssetModelData assetModel = new AssetModelData(newAsset);
		changeList.add(assetModel);
		
		if(newAsset.category == OpenCPICategory.project) {
			LinkedHashSet<AssetModelData> additions = new LinkedHashSet<AssetModelData> (4);
			additions.addAll(changeList);
			return additions;
		}
		
		AngryViperAsset parentAsset = OpenCPIAssetFactory.createParentOcpiAsset(newAsset, parentName);
		boolean notDone = true;
		int depth = 4;
		// Need to find the parent asset that already exists and setup each 
		// new asset model for the UI.  Note that update process take individual
		// asset models at this point.
		while(0 <= depth--) {
			
			if(assetLookup.containsKey(parentAsset)) {
				AssetModelData parentAssetModel =  assetLookup.get(parentAsset);
				newAsset.parent = parentAssetModel.asset;
				parentAssetModel.childList.add(assetModel);
				notDone = false;
				break;
			}
			
			newAsset.parent = parentAsset;
			AssetModelData parentModel = new AssetModelData(parentAsset);
			parentModel.childList.add(assetModel);
			changeList.add(parentModel);
			
			// Next iteration
			newAsset = parentAsset;
			assetModel = parentModel;
			parentAsset = OpenCPIAssetFactory.createParentOcpiAsset(newAsset, null);
		}
		if(notDone) 
			return null;
		
		LinkedHashSet<AssetModelData> additions = new LinkedHashSet<AssetModelData> (4);
		int len = changeList.size();
		for (int i = len -1; i > -1;  i--){
			additions.add(changeList.get(i));
		}
		return additions;
	}
	
	public Set<AssetModelData> createAsset (OpenCPICategory type, CreateAssetFields assetElem, StringBuilder sb) {

		ProjectLocation projectLocation = null; 
		
		final List<String> command = new ArrayList<String>(getBaseCmd());
		command.set(2, assetElem.fullProjectPath);
		command.addAll(type.getOcpiBuildNowns());
		if(type != OpenCPICategory.project) {
			if(assetElem.name.endsWith(".xml")) {
				assetElem.name.replace(".xml", "");
			}
			OpencpiEnvService srv = AngryViperAssetService.getInstance().getEnvironment();
			AngryViperProjectInfo info = srv.lookupProjectByPath(assetElem.fullProjectPath);
			projectLocation = new ProjectLocation(info.name, assetElem.fullProjectPath);
			projectLocation.packageId = info.packageId;
			projectLocation.eclipseName = info.eclipseName;
		}
		else {
			// Can't get the package Id until the project is created.
			String pathname =  assetElem.fullProjectPath + "/" + assetElem.name;
			projectLocation = new ProjectLocation(assetElem.name, pathname);
		}
		command.add(assetElem.name);
		
		String parentAssetName = null;
		AngryViperAsset asset = null;
		switch(type) {
		case application:
			if (assetElem.xmlOnly) {
				command.add("-X");
				asset = OpenCPIAssetFactory.createOcpiAsset(assetElem.name + ".xml", null, OpenCPICategory.xmlapp, projectLocation);
			}
			else {
				asset = OpenCPIAssetFactory.createOcpiAsset(assetElem.name, null, type, projectLocation);
			}
			break;
		case assembly:
			asset = OpenCPIAssetFactory.createOcpiAsset(assetElem.name, null, type, projectLocation);
			break;
		case platform:
			HdlPlatformFields platform = (HdlPlatformFields)assetElem;
			asset = OpenCPIAssetFactory.createOcpiAsset(assetElem.name, null, type, projectLocation);
			if(platform.partNumber != null && ! platform.partNumber.isEmpty()) {
				command.add("-g");
				command.add(platform.partNumber);
			}
			if(platform.timerServerFreq != null && ! platform.timerServerFreq.isEmpty()) {
				command.add("-q");
				command.add(platform.timerServerFreq);
			}
			break;
		case primitive:
			asset = OpenCPIAssetFactory.createOcpiAsset(assetElem.name, null, type, projectLocation);
			break;
		case component:
			if (assetElem.topLevelSpec) {
				command.add("-p");
				asset = OpenCPIAssetFactory.createOcpiAsset(assetElem.name, OpenCPICategory.specs.getFrameworkName(), type, projectLocation);
			} else {
				if (assetElem.libraryName != null) {
					command.add("-l");
					command.add(assetElem.libraryName);
					asset = OpenCPIAssetFactory.createOcpiAsset(assetElem.name, assetElem.libraryName, type, projectLocation);
				} 
			}
			asset.xmlFilename = assetElem.name+"-spec.xml";
			// display shows the comp as the xml file.
			asset.assetName = asset.xmlFilename;
			
			break;
 		case protocol:
			if (assetElem.topLevelSpec) {
				command.add("-p");
				asset = OpenCPIAssetFactory.createOcpiAsset(assetElem.name, OpenCPICategory.specs.getFrameworkName(), type, projectLocation);
				asset.assetFolder = "/specs";
			} else {
				if (assetElem.libraryName != null) {
					command.add("-l");
					command.add(assetElem.libraryName);
					asset = OpenCPIAssetFactory.createOcpiAsset(assetElem.name, assetElem.libraryName, type, projectLocation);
				} 
			}
			asset.xmlFilename = assetElem.name+"-prot.xml";
			// cammand has the comp name, the display shows the comp as the xml file.
			asset.assetName = asset.xmlFilename;
			
			break;
		case library:
			if("components".equals(assetElem.name)) {
				asset = OpenCPIAssetFactory.createOcpiAsset(null, null, OpenCPICategory.componentsLibrary, projectLocation);
			}
			else {
				asset = OpenCPIAssetFactory.createOcpiAsset(assetElem.name, null, type, projectLocation);
			}
			break;
			
		case worker:
			CreateWorkerFields worker = (CreateWorkerFields)assetElem;
			int last = command.size() - 1;
			String name = command.get(last);
			if(worker.model == AuthoringModel.HDL) {
				name += ".hdl";
			}
			else {
				name += ".rcc";
			}
			command.set(last, name);

			if (assetElem.libraryName != null) {
				command.add("-l");
				command.add(assetElem.libraryName);
				parentAssetName = assetElem.libraryName;
				asset = OpenCPIAssetFactory.createOcpiAsset(name, assetElem.libraryName, type, projectLocation);				
			}
			else {
				parentAssetName = assetElem.libraryName;
				asset = OpenCPIAssetFactory.createOcpiAsset(name, 
						OpenCPICategory.componentsLibrary.getFrameworkName(), type, projectLocation);				
			}
			command.add("-S");
			command.add(worker.componentSpec);
			command.add("-L");
			command.add(worker.language);
			
			break;
			
		case test:
			if (assetElem.libraryName != null) {
				command.add("-l");
				command.add(assetElem.libraryName);
				parentAssetName = assetElem.libraryName;
				asset = OpenCPIAssetFactory.createOcpiAsset(assetElem.name+= ".test", assetElem.libraryName, type, projectLocation);
			}
			break;
			
		case project:
			asset = OpenCPIAssetFactory.createOcpiAsset(assetElem.name, null, type, projectLocation);
			command.add("--register");
			CreateProjectFields project = (CreateProjectFields)assetElem;
			if(project.packageName != null) {
				command.add("-N");
				command.add(project.packageName);
			}
			if(project.prefix != null) {
				command.add("-F");
				command.add(project.prefix);
			}
			if(! project.dependencies.isEmpty()) {
				for(String dep : project.dependencies) {
					command.add("-D");
					command.add(dep);
				}
			}
			break;
			
		default:
			
		}
//		Set<AssetModelData> assetModel = null;
//		if(asset != null) {
//			assetModel = createModel(asset, parentAssetName);
//		}
		
		MessageConsole cons = AvpsResourceManager.getInstance().getNoticeConsoleInView();
		if( CommandExecutor.executeCommand(command, cons, sb) ){
			if(asset != null){
				Set<AssetModelData> assetModel = createModel(asset, parentAssetName);
				for(AssetModelData model : assetModel){
					// Assets service will load the project after getting 
					// updated environment info.
					//if(model.asset.category == OpenCPICategory.project) {
					assetLookup.put(model.asset,  model);
				}
				return assetModel;
			}
			else {
				return new LinkedHashSet<AssetModelData> (1);				
			}
		}
		else {
		return null;
		}
	}

	public OcpiAssetDifferences deleteAsset(AngryViperAsset asset, StringBuilder sb) {
		
		final List<String> command = new ArrayList<String>(getBaseCmd());
		command.set(2, asset.projectLocation.projectPath);
		command.set(3, "delete");
		boolean addLibraryName = false;

		switch(asset.category) {
		case application:
			command.add("application");
			break;
		case xmlapp:
			command.add("application");
			String name = asset.assetName.substring(0, asset.assetName.length() -4);
			command.add(name);
			command.add("-X");
			break;

		case assembly:
			command.add("hdl");
			command.add("assembly");
			break;
		case card:
			command.add("hdl");
			command.add("card");
			break;
		case component:
			command.add("spec");
			addLibraryName = true;
			break;
		case componentsLibrary:
			command.add("library");
			break;
		case device:
			command.add("hdl");
			command.add("device");
			break;
//		case hdlLibrary:
//			break;
//		case hdlTest:
//			break;
		case library:
			command.add("library");
			break;
		case platform:
			command.add("hdl");
			command.add("platform");
			break;
		case primitive:
			command.add("hdl");
			command.add("primitive");
			command.add("library");
			break;
		case project:
			String projectPath = asset.projectLocation.projectPath;
			String[] pathParts = projectPath.split("/");
			String projectDir = pathParts[pathParts.length -1];
			String ParentDir = projectPath.substring(0, projectPath.length() - projectDir.length());
			command.set(2, ParentDir);
			command.add("project");
			command.add(projectDir);
			break;
		case protocol:
			command.add("protocol");
			addLibraryName = true;
			break;
		case test:
			command.add("test");
			addLibraryName = true;
			break;
		case worker:
			command.add("worker");
			addLibraryName = true;
			break;
		default:
			return new OcpiAssetDifferences();
		}
		
		if(asset.category != OpenCPICategory.project && asset.category != OpenCPICategory.xmlapp)
			command.add(asset.assetName);
		
		if(addLibraryName) {
			if ("specs".equals(asset.libraryName)) {
			command.add("-p");
			} else {
				command.add("-l");
			}
			command.add(asset.libraryName);
		}
		// Don't want the prompt back
		command.add("-f");
		
		MessageConsole cons = AvpsResourceManager.getInstance().getNoticeConsoleInView();
		//System.out.println(command.toString()); 
		boolean result = CommandExecutor.executeCommand(command, cons, sb);
		if(result) {
			LinkedHashSet<AssetModelData> changeList = new LinkedHashSet<AssetModelData> (4);
			AssetModelData assetModel = assetLookup.get(asset);
			if(assetModel == null) {
				assetModel = new AssetModelData(asset);
			}
		
			changeList.add(assetModel);
			AngryViperAsset parent = asset.parent;
			if(parent != null && parent.category != OpenCPICategory.componentsLibrary) {
				AssetModelData parentModel = assetLookup.get(parent);
				if(parentModel != null && parentModel.getChildList().size() <= 1) {
					changeList.add(parentModel);
				}
			}
			OcpiAssetDifferences diffs = new OcpiAssetDifferences();
			diffs.addedChangeset = new LinkedHashSet<AssetModelData> (0);
			diffs.removedChangeset = changeList;
			diffs.removedProjects = new  HashMap<String, AssetModelData> (4);
			if(asset.category == OpenCPICategory.project) {
				diffs.removedProjects.put(asset.assetName, assetModel);
			}

			return diffs;
		}
		return null;
	}

	void loadProject(ProjectLocation location, Project projectXml, AngryViperProjectInfo info) {
		AngryViperAsset asset = OpenCPIAssetFactory.createOcpiAsset(null, null,OpenCPICategory.project,location);
		asset.assetDetails = info;
		
		AssetModelData projectData = new AssetModelData(asset);
		synchronized (this) {
			addProject(asset, projectData);
		}
		LinkedHashMap<AngryViperAsset, AssetModelData> temp = new LinkedHashMap<AngryViperAsset, AssetModelData> ();
		buildData(projectXml, projectData, temp);
		synchronized (this) {
			assetLookup.putAll(temp);
		}
	}
	
	/**
	 * The central routine to build the asset data model from the project XML.
	 * @param projectXmlModel - the XML document for the project.
	 * @param project - the project model data container to be populated.
	 * @return - an asset lookup map for this project.
	 */
	protected static void 
	  buildData(Project projectXmlModel, AssetModelData project, LinkedHashMap<AngryViperAsset, AssetModelData> lookup) {
		
		ProjectLocation location = project.asset.projectLocation;
		lookup.put(project.asset, project);
		
		ElementList<Spec> topSpecs = projectXmlModel.getSpecs();
		
		if(topSpecs.size() > 0) {
			AngryViperAsset specsFolder = 
					OpenCPIAssetFactory.createOcpiAsset(OpenCPICategory.topLevelSpecs.getFrameworkName(), null, OpenCPICategory.topLevelSpecs, location);
			specsFolder.parent = project.asset;
			AssetModelData projSpecs = new	AssetModelData(specsFolder);
			project.childList.add(projSpecs);
			lookup.put(projSpecs.asset, projSpecs);
			
			AssetModelData s;
			for(Spec spec : topSpecs) {
				String specName = spec.getName().content();
				if(isSpecName(specName)) {
					s = new AssetModelData(
						OpenCPIAssetFactory.createOcpiAsset(specName, OpenCPICategory.topLevelSpecs.getFrameworkName(), OpenCPICategory.component, location));
					
				}
				else {
					s = new AssetModelData(
							OpenCPIAssetFactory.createOcpiAsset(specName, OpenCPICategory.topLevelSpecs.getFrameworkName(), OpenCPICategory.protocol, location));
				}
				s.asset.parent = projSpecs.asset;
				s.asset.assetFolder = "/specs";
				projSpecs.childList.add(s);
				lookup.put(s.asset, s);
			}
		}
		
		ElementList<Application> apps = projectXmlModel.getApplications();
		List<String> xmlApps = getXmlOnlyApps(location);
		if(apps.size() > 0 || xmlApps.size() > 0 ) {
			AngryViperAsset asset = OpenCPIAssetFactory.createOcpiAsset(null, null,OpenCPICategory.applications, location);
			asset.parent = project.asset;
			
			AssetModelData assetModel = new	AssetModelData(asset);
			project.childList.add(assetModel);
			lookup.put(assetModel.asset, assetModel);
			
			for(Application childAsset : apps) {
				String name = childAsset.getName().content();
				AssetModelData c = new AssetModelData(
					OpenCPIAssetFactory.createOcpiAsset(name, null, OpenCPICategory.application, location));
				c.asset.parent = assetModel.asset;
				assetModel.childList.add(c);
				lookup.put(c.asset, c);
			}
			for(String xmlApp : xmlApps) {
				AssetModelData c = new AssetModelData(
					OpenCPIAssetFactory.createOcpiAsset(xmlApp, null, OpenCPICategory.xmlapp, location));
				c.asset.parent = assetModel.asset;
				assetModel.childList.add(c);
				lookup.put(c.asset, c);
			}
		}
		
		Components comps = projectXmlModel.getComponents();

		// AV Rules - workers will either be in a top level components directory
		// (it is the library) or they will be in library subDirectories; never
		// both.
		
		ElementList<Library> libraries = comps.getLibraries();
		
		if(libraries.size() >0) {
			AssetModelData loadPoint;// = new AssetModelData(asset);
			
			boolean loadSubLibraries = false;
			AssetModelData library = null;
			if(libraries.size() == 1) {
				// This section deals with the fact that a project can have one
				// library but it may not be named "components". So this is
				// makes that determination then sets up to create the3 asset objects
				// accordingly. LoadPoint is the reference to the parent object to
				// which workers, tests, and components are added to the child list.
				
				Library lib = libraries.get(0);
				String name = lib.getName().content();
				
				if(OpenCPICategory.componentsLibrary.getFrameworkName().equals(name)) {
					// This is the components library.
					AngryViperAsset asset = OpenCPIAssetFactory.createOcpiAsset(null, null, OpenCPICategory.componentsLibrary, location);
					loadPoint = project;
					library = new AssetModelData(asset);
					library.asset.buildName = name;
					library.asset.parent = loadPoint.asset;
					loadPoint.childList.add(library);
					lookup.put(library.asset, library);
				}
				else{
					// This is a library under the components folder.
					AngryViperAsset asset = OpenCPIAssetFactory.createOcpiAsset(null, null, OpenCPICategory.componentsLibraries, location);
					loadPoint = new AssetModelData(asset);
					loadPoint.asset.parent = project.asset;
					project.childList.add(loadPoint);
					lookup.put(loadPoint.asset, loadPoint);
					loadSubLibraries = true;
				}
			}
			else {
				// This project has multiple libraries that go under the components folder.
				AngryViperAsset asset = OpenCPIAssetFactory.createOcpiAsset(null, null, OpenCPICategory.componentsLibraries, location);
				loadPoint = new AssetModelData(asset);
				loadPoint.asset.parent = project.asset;
				project.childList.add(loadPoint);
				lookup.put(loadPoint.asset, loadPoint);
				loadSubLibraries = true;
			}
			
			for(Library childElement : libraries) {
				if(loadSubLibraries){
					String name = childElement.getName().content();
					library = new AssetModelData(
						OpenCPIAssetFactory.createOcpiAsset(name, null, OpenCPICategory.library, location));
					library.asset.parent = loadPoint.asset;
					loadPoint.childList.add(library);
					lookup.put(library.asset, library);
				}
				
				ElementList<Spec> specs = childElement.getSpecs();
				
				if(specs.size() > 0) {
					AngryViperAsset specsFolder = OpenCPIAssetFactory.createOcpiAsset(null, library.asset.buildName, OpenCPICategory.specs, location);
					specsFolder.parent = library.asset;
					AssetModelData librarySpecs = new	AssetModelData(specsFolder);
					library.childList.add(librarySpecs);
					lookup.put(librarySpecs.asset, librarySpecs);
					
					for(Spec spec : specs) {
						String specName = spec.getName().content();
						OpenCPICategory type;
						if(isSpecName(specName)) {
							type = OpenCPICategory.component;
						}
						else {
							type = OpenCPICategory.protocol;
						}
						AssetModelData s = new AssetModelData(
							OpenCPIAssetFactory.createOcpiAsset(specName, library.asset.buildName, type, location));
						s.asset.parent = librarySpecs.asset;
						librarySpecs.childList.add(s);
						lookup.put(s.asset, s);
					}
				}
				
				ElementList<Worker> workers = childElement.getWorkers();
				for(Worker worker : workers) {
					String workerName = worker.getName().content();
					AssetModelData w = new AssetModelData(
						OpenCPIAssetFactory.createOcpiAsset(workerName, library.asset.buildName, OpenCPICategory.worker, location));
					w.asset.parent = library.asset;
					library.childList.add(w);
					lookup.put(w.asset, w);
				}
				
				ElementList<Test> tests = childElement.getTests();
				for(Test test : tests) {
					String testName = test.getName().content();
					AssetModelData t = new AssetModelData(
						OpenCPIAssetFactory.createOcpiAsset(testName, library.asset.buildName, OpenCPICategory.test, location));
					t.asset.parent = library.asset;
					library.childList.add(t);
					lookup.put(t.asset, t);
				}
			}
		}
		
		Hdl hdlElement = projectXmlModel.getHdl();
		ElementList<Assembly> assemblies = hdlElement.getAssemblies();
		if(assemblies.size() > 0 ) {
			AssetModelData parentAsset = new AssetModelData(
				OpenCPIAssetFactory.createOcpiAsset(null, null, OpenCPICategory.assemblies, location));
			parentAsset.asset.parent = project.asset;
			project.childList.add(parentAsset);
			lookup.put(parentAsset.asset, parentAsset);
			
			for(Assembly childElement : assemblies) {
				String name = childElement.getName().content();
				AssetModelData c = new AssetModelData(
					OpenCPIAssetFactory.createOcpiAsset(name, null, OpenCPICategory.assembly, location));
				c.asset.parent = parentAsset.asset;
				parentAsset.childList.add(c);
				lookup.put(c.asset, c);
			}
		}
		
		ElementList<Platform> platforms = hdlElement.getPlatforms();
		if(platforms.size() > 0 ) {
			AssetModelData asset = new AssetModelData(
				OpenCPIAssetFactory.createOcpiAsset(null, null, OpenCPICategory.platforms, location));
			asset.asset.parent = project.asset;
			project.childList.add(asset);
			lookup.put(asset.asset, asset);
			
			for(Platform childElement : platforms) {
				String name = childElement.getName().content();
				AssetModelData c = new AssetModelData(
					OpenCPIAssetFactory.createOcpiAsset(name, null, OpenCPICategory.platform, location));
				c.asset.parent = asset.asset;
				asset.childList.add(c);
				lookup.put(c.asset, c);
			}
		}
		
		ElementList<Primitive> primitivies = hdlElement.getPrimitives();
		if(primitivies.size() > 0 ) {
			AssetModelData asset = new AssetModelData(
				OpenCPIAssetFactory.createOcpiAsset(null, null, OpenCPICategory.primitives, location));
			asset.asset.parent = project.asset;
			project.childList.add(asset);
			lookup.put(asset.asset, asset);
			
			for(Primitive childElement : primitivies) {
				String name = childElement.getName().content();
				AssetModelData c = new AssetModelData(
					OpenCPIAssetFactory.createOcpiAsset(name, null, OpenCPICategory.primitive, location));
				c.asset.parent = asset.asset;
				asset.childList.add(c);
				lookup.put(c.asset, c);
			}
		}
		
		ElementList<Library> hdlLibraries = hdlElement.getLibraries();
		if(hdlLibraries.size() > 0 ) {
			for(Library childElement : hdlLibraries) {
				String name = childElement.getName().content();
				OpenCPICategory category = null;
				OpenCPICategory subcategory = null;
				if("cards".equals(name)) {
					category = OpenCPICategory.cards;
					subcategory = OpenCPICategory.card;
				}
				else {
					category = OpenCPICategory.devices;
					subcategory = OpenCPICategory.device;
				}
				AssetModelData library = new AssetModelData(
						OpenCPIAssetFactory.createOcpiAsset(name, null, category, location));
				//library.asset.buildName = name;
				library.asset.parent = project.asset;
				project.childList.add(library);
				lookup.put(library.asset, library);
				
				ElementList<Worker> workers = childElement.getWorkers();
				for(Worker worker : workers) {
					String workerName = worker.getName().content();
					AssetModelData w = new AssetModelData(
							OpenCPIAssetFactory.createOcpiAsset(workerName, library.asset.buildName, subcategory, location));
					w.asset.parent = library.asset;
					library.childList.add(w);
					lookup.put(w.asset, w);
				}
				
				ElementList<Test> tests = childElement.getTests();
				for(Test test : tests) {
					String testName = test.getName().content();
					AssetModelData t = new AssetModelData(
							OpenCPIAssetFactory.createOcpiAsset(testName, library.asset.buildName, OpenCPICategory.hdlTest, location));
					t.asset.parent = library.asset;
					library.childList.add(t);
					lookup.put(t.asset, t);
				}
			}
		}
	}

	private static List<String> getXmlOnlyApps(ProjectLocation location) {
		ArrayList<String> apps = new ArrayList<String>();
		File projectFolder = new File(location.projectPath);
		if (projectFolder != null && projectFolder.exists()) {
			File appsDir = new File(projectFolder, "applications");
			if (appsDir != null && appsDir.exists()) {
				FileFilter filter = new XmlFileFilter();
				File[] files = appsDir.listFiles(filter);
				for(File appfile : files) {
					apps.add(appfile.getName());
				}
			}
		}
		
		return apps;
	}

	private static boolean isSpecName(String specName) {
		if(specName.toLowerCase().endsWith("spec.xml"))
			return true;
		
		return false;
	}

	public class OcpiAssetDifferences {
		Set<AssetModelData> addedChangeset;
		Set<AssetModelData> removedChangeset;
		Map<String, AssetModelData> removedProjects;
		List<HdlPlatformInfo> hdlAddList;
		List<HdlPlatformInfo> hdlRemoveList;
		Boolean add_remove;
		List<RccPlatformInfo> rccAddList;
		List<RccPlatformInfo> rccRemoveList;
		
		boolean areAssetsDifferent() {
			boolean areDifferent = true;
			if(addedChangeset.isEmpty() && removedChangeset.isEmpty() &&  removedProjects.isEmpty()) {
				areDifferent = false;
			}
			
			return areDifferent;
		}
		
		boolean areBuildPlatformsDifferent() {
			boolean areDifferent = true;
			if(hdlAddList.isEmpty() && hdlRemoveList.isEmpty() &&
				rccAddList.isEmpty() && rccRemoveList.isEmpty()) {
				areDifferent = false;
			}
			
			return areDifferent;
		}
	}
	
	/***
	 * This must compare the current repo against the new.
	 */
	OcpiAssetDifferences getPlatformDifferences (OpenCpiAssets otherOcpiAssets ) {
		// See if the HDL platforms have changed.
		Collection<HdlPlatformInfo> hdlPlats = otherOcpiAssets.hdlPlatforms;
		ArrayList<HdlPlatformInfo> hdlAddList = new ArrayList<HdlPlatformInfo> ();

		if(hdlPlats.size() > this.hdlPlatforms.size() ){
			for (HdlPlatformInfo platform : hdlPlats) {
				if(! this.hdlPlatforms.contains(platform)) {
					hdlAddList.add(platform);
				}
			}
		}
		ArrayList<HdlPlatformInfo> hdlRemoveList = new ArrayList<HdlPlatformInfo> ();
		
		if(hdlPlats.size() < this.hdlPlatforms.size() ){
			for (HdlPlatformInfo platform : this.hdlPlatforms) {
				if(! hdlPlats.contains(platform)) {
					hdlRemoveList.add(platform);
				}
			}
		}
		
		// See if the RCC platforms have changed.
		Collection<RccPlatformInfo> rccPlats = otherOcpiAssets.rccPlatforms;
		ArrayList<RccPlatformInfo> rccAddList = new ArrayList<RccPlatformInfo> ();

		if(rccPlats.size() > this.rccPlatforms.size() ){
			for (RccPlatformInfo platform : rccPlats) {
				if(! this.rccPlatforms.contains(platform)) {
					rccAddList.add(platform);
				}
			}
		}
		ArrayList<RccPlatformInfo> rccRemoveList = new ArrayList<RccPlatformInfo> ();
		
		if(rccPlats.size() < this.rccPlatforms.size() ){
			for (RccPlatformInfo platform : this.rccPlatforms) {
				if(! rccPlats.contains(platform)) {
					rccRemoveList.add(platform);
				}
			}
		}
		
		OcpiAssetDifferences result = new OcpiAssetDifferences();
		result.hdlAddList = hdlAddList;
		result.hdlRemoveList = hdlRemoveList;
		result.rccAddList = rccAddList;
		result.rccRemoveList = rccRemoveList;
		
		return result;
		
		
	}
	public OcpiAssetDifferences diff(OpenCpiAssets otherOcpiAssets) {
		Map<String, AssetModelData> otherProjects = otherOcpiAssets.projects;
		LinkedHashMap<AngryViperAsset, AssetModelData> otherAssetsLookup = otherOcpiAssets.assetLookup;
		
		// Figure out what changed.  First look for new projects.
		Set<AssetModelData> addedChangeset = new LinkedHashSet<AssetModelData>();

		Map<String, AssetModelData> newProjects = new HashMap <String, AssetModelData> ();
		for(String projectName: otherProjects.keySet()) {
			if( ! this.projects.containsKey(projectName))
				newProjects.put(projectName, otherProjects.get(projectName));
		}
		if(newProjects.size() > 0) {
			addedChangeset.addAll(newProjects.values());
			// Will need to cull out the assets contained in the new projects.
		}
		
		// Now see what else might be new.
		LinkedHashMap<AngryViperAsset, AssetModelData> newAssetLookup = 
			     new LinkedHashMap <AngryViperAsset,AssetModelData> ();
		for(AngryViperAsset asset : otherAssetsLookup.keySet()) {

			// don't gather the assets contained in the new projects.
			if(newProjects.containsKey(asset.projectLocation.projectName)) {
				continue;
			}

			AssetModelData newAsset = otherAssetsLookup.get(asset);
			if( ! this.assetLookup.containsKey(asset)) {
				addedChangeset.add(newAsset);
				newAssetLookup.put(asset, newAsset);
				// Components Hack
				if(asset.category != OpenCPICategory.project &&
					asset.parent.assetName.equals(OpenCPICategory.componentsLibrary.getFrameworkName())) {
					// Need to get the component
					AngryViperAsset componentsParent = asset.parent.parent;
					asset.parent = componentsParent;
				}
				if(this.assetLookup.containsKey(asset.parent)) {
					// Use that parent.
					AssetModelData parent = assetLookup.get(asset.parent);
					if(parent != null)
						asset.parent = parent.asset;
				}
			}
		}

		// See if projects have been removed.
		Map<String, AssetModelData> removedProjects = new HashMap <String, AssetModelData> ();
		Set<AssetModelData> removedChangeset = new HashSet<AssetModelData>();

		for(String projectName: this.projects.keySet()) {
			if( ! otherProjects.containsKey(projectName))
				removedProjects.put(projectName, projects.get(projectName));
		}
		if(removedProjects.size() > 0) {
			removedChangeset.addAll(removedProjects.values());
		}
		
		for(AngryViperAsset asset : this.assetLookup.keySet()) {
			
			// don't gather the assets contained in the removed projects.
			if(removedProjects.containsKey(asset.projectLocation.projectName))
				continue;
			
			AssetModelData currentAsset = this.assetLookup.get(asset);
			if( ! otherAssetsLookup.containsKey(asset)) {
				removedChangeset.add(currentAsset);
			}
		}
		OcpiAssetDifferences platformDiff = getPlatformDifferences (otherOcpiAssets);
		
		OcpiAssetDifferences result = new OcpiAssetDifferences();
		result.addedChangeset = addedChangeset;
		result.removedChangeset = removedChangeset;
		result.removedProjects	= removedProjects;
		result.hdlAddList = platformDiff.hdlAddList;
		result.hdlRemoveList = platformDiff.hdlRemoveList;
		result.rccAddList = platformDiff.rccAddList;
		result.rccRemoveList = platformDiff.rccRemoveList;
		
		return result;
		
	}
}


