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
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.console.MessageConsole;
import org.json.simple.JSONObject;

import av.proj.ide.avps.internal.AvpsResourceManager;
import av.proj.ide.avps.internal.CommandExecutor;

/***
 * This class provides information about the OpenCPI environment on the development system.
 * This was necessary because some information is more global than the eclipse workspace
 * and projects open in eclipse. This becomes very apparent in application and assembly
 * development where component specs and workers may be selected from the entire registered
 * environment; not just those found in the project or the eclipse workspace.
 * 
 * Since there are users of the service with differing needs lazy loading is used.  AngryViperProjectInfo
 * information is globally used so this information is obtained on construction. Spec and
 * worker information is not obtained until requested.
 *
 */
public class OpencpiEnvService {
	
	// Purposely given package scope.
	OpencpiEnvService() {
		
		this.registeredProjects = new TreeSet<String>();
		this.registeredProjectsLessCore = new TreeSet<String>();
		
		invalidSpecPath = new HashSet<String>();
		invalidSpecPath.add("applications");
		invalidSpecPath.add("doc");
		invalidSpecPath.add("exports");
		invalidSpecPath.add("imports");
		invalidSpecPath.add("scripts");
		invalidSpecPath.add("gen");
		invalidSpecPath.add("lib");
		invalidSpecPath.add("assemblies");
		invalidSpecPath.add("primitives");
		
		getEnvironmentProjects();
	}

	//***************************************
	// Project Information
	//***************************************
	/***
	 * lookup the project by the project pathname (the one common element). This holds project info
	 * on every project in the environment including non-opencpi projects
	 * in the eclipse workspace.
	 */
	private Map<String, AngryViperProjectInfo> projectLookup;
	private Map<String, String> projectPathLookup;
	private Set<String> registeredProjects;
	private Set<String> registeredProjectsLessCore;

	/***
	 * UI Services that support the wizard and the OpenCPI Project view.
	 */
	public boolean unregisterProject(AngryViperAsset asset, StringBuilder s) {
		unregisterCmd.set(2, asset.projectLocation.projectPath);
	    StringBuilder sb = new StringBuilder();
		MessageConsole consoleMsg = AvpsResourceManager.getInstance().getNoticeConsoleInView();
		boolean result = CommandExecutor.executeCommand(unregisterCmd, consoleMsg, sb);
        return result;
	}

	public boolean registerProject(AngryViperAsset asset, StringBuilder s) {
		registerCmd.set(2, asset.projectLocation.projectPath);
	    StringBuilder sb = new StringBuilder();
		MessageConsole consoleMsg = AvpsResourceManager.getInstance().getNoticeConsoleInView();
		boolean result = CommandExecutor.executeCommand(registerCmd, consoleMsg, sb);
        return result;
	}
	
	private List<String> registerCmd = new ArrayList<String>();
	private List<String> unregisterCmd = new ArrayList<String>();
	{
		registerCmd.add("ocpidev");
		registerCmd.add("-d");
		registerCmd.add(null);
		registerCmd.add("register");
		registerCmd.add("project");    
		
		unregisterCmd.add("ocpidev");
		unregisterCmd.add("-d");
		unregisterCmd.add(null);
		unregisterCmd.add("unregister");
		unregisterCmd.add("project");    
		unregisterCmd.add("-f");
	}
	
	public Set<String> getRegisteredProjects() {
		return registeredProjects;
	}

	public Set<String> getRegisteredProjectsLessCore() {
		return registeredProjectsLessCore;
	}
	
	public AngryViperProjectInfo getProjectByPath(String path) {
		AngryViperProjectInfo info = projectLookup.get(path);
		if(info == null) {
			// See if it's there just not registered.
			showProjectCmd[2] = path;
			JSONObject jsonObject = EnvBuildTargets.getEnvInfo(showProjectCmd);
			
			if(jsonObject == null) {
				return null;
			}
			JSONObject projObject = (JSONObject)jsonObject.get("project");
			if(projObject == null) {
				return null;
			}
			String packageId = (String) projObject.get("package");
			String directory = (String) projObject.get("directory");
			info = new AngryViperProjectInfo();
			String[] pathSegments = directory.split("/");
			String name = pathSegments[pathSegments.length -1];
			info.fullPath = directory;
			info.name = name;
			info.packageId = packageId;
			info.projectDirectory = name;
			return info;
		}
		else {
			return info;	
		}
	}

	public AngryViperProjectInfo getProjectInfo(String projectName) {
		String projectPath = projectPathLookup.get(projectName);
		return projectLookup.get(projectPath);
	}
	
	protected void getEnvironmentProjects() {
		projectLookup = new HashMap<String, AngryViperProjectInfo>();
		projectPathLookup = new HashMap<String, String>();
		
		// This gets the registered projects. Unregistered Opencpi projects are not
		// picked up here.
		JSONObject jsonObject = EnvBuildTargets.getEnvInfo(getProjectsCmd);
        envProjects = new ArrayList<AngryViperProjectInfo>();
		if(jsonObject != null) {
        	JSONObject projectsObj = (JSONObject) jsonObject.get("projects");

        	if(projectsObj != null) {
    	        @SuppressWarnings("unchecked")
    			Set<String> projects = projectsObj.keySet();
    	        for(String key : projects) {
    	        	AngryViperProjectInfo project = new AngryViperProjectInfo(key, (JSONObject) projectsObj.get(key));
    	        	project.isRegistered = true;
    	        	projectLookup.put(project.fullPath, project);
    	        	projectPathLookup.put(project.packageId, project.fullPath);
    	        	projectPathLookup.put(project.name, project.fullPath);
    	        	envProjects.add(project);
        			registeredProjects.add(project.name);
        			
        			if(!( project.packageId.equalsIgnoreCase("ocpi.core") || project.packageId.equalsIgnoreCase("ocpi.cdk"))) {
        				registeredProjectsLessCore.add(project.packageId);
        			}
    	        }
   		    }
		}
		
		// Now see what projects are open in the Eclipse workspace.  Note the
		// eclipse project name can differ from the registered project name.
		
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IProject [] eProjects = workspace.getRoot().getProjects();
		IProject eProject;
		for(int i= 0; i<eProjects.length; i++) {
			eProject = eProjects[i];
			
			if(! eProject.isOpen()) continue;
			if("RemoteSystemsTempFiles".equals(eProject.getName())) continue;
			
			String eProjectName = eProject.getName();
			IPath path = eProject.getLocation();
			String fullpath = path.toOSString();
			
			AngryViperProjectInfo ocpiProject = projectLookup.get(fullpath);
			if(ocpiProject != null) {
				ocpiProject.eclipseName = eProjectName;
				ocpiProject.isOpenInEclipse = true;
				continue;
			}
			
			// It is not registered, see if it was before.
			ocpiProject = getProjectByPath(fullpath);
			if(ocpiProject != null) {
				// This project was registered, it is not now.
				ocpiProject.isOpenInEclipse = true;
				ocpiProject.eclipseName = eProjectName;
				projectLookup.put(fullpath, ocpiProject);
				projectPathLookup.put(eProjectName, fullpath);
				projectPathLookup.put(ocpiProject.name, fullpath);
			}
			else {
				// It's not an Opencpi Project, but is is open in eclipse.
				AngryViperProjectInfo eclipseProject = new AngryViperProjectInfo();
				eclipseProject.eclipseName = eProjectName;
				eclipseProject.fullPath = fullpath;
				eclipseProject.isOpenInEclipse = true;
				projectLookup.put(fullpath, eclipseProject);
				projectPathLookup.put(eProjectName, fullpath);
			}
		}
	}

	//***************************************
	// Component and Protocol Information
	//***************************************

	/***
	 * Components are referenced several ways.  Given the component file name is
	 * file_read_spec.xml where "file_read" is the component name:
	 * - Application OAS wants packageId + component name.
	 * - Worker OWD wants the component file name minus ".xml"
	 * - Unit Test creation wants the component name.
	 * 
	 * Note that a component name is not unique, however the component qualified by
	 * its packageId is unique. The common element for components is the component
	 * name.
	 * 
	 * Protocols are referenced in Component Port definitions. They are referenced one
	 * way but their file names vary across several naming conventions.
	 * Given the protocol name is TimeStamped_IQ:
	 *  - the current convention gives the filename TimeStamped_IQ-prot.xml
	 *  - The OCS port definition references it as TimeStamped_IQ-prot.
	 *  
	 */
	
	/***
	 * ocpiProjectComponents - holds a list of UiComponentSpec objects with the component XML filename as the key.
	 * A list is held because a components can have the same name but live in different packages.
	 * A UiComponentSpec holds the variety of reference names for the component used by the framework.
	 */
	Map<String, List<UiComponentSpec>> ocpiProjectComponents;
	
	/***
	 * uiSpecsLookup - provides a direct lookup of the UiComponentSpec using the component display name as a key.
	 * This display name is used in a number of drop downs.
	 */
	Map<String, UiComponentSpec> uiSpecsLookup = null;
	
	/***
	 * componentsLookup - holds a projects components referenced by the project package ID. This
	 * map is loaded lazily at the first request for a projects components.
	 */
	private Map<String, Set<String>> componentsLookup;
	
	private Set<String> protocols = null;
	
	/***
	 * Change Processing.
	 * A component asset is created or deleted.
	 */
	public void addComponent(AngryViperAsset newAsset) {
		clearComponents();
	}
	public void removeComponent(AngryViperAsset asset) {
		clearComponents();
	}
	
	void clearComponents() {
		protocols = null;
		uiSpecsLookup = null;
		ocpiProjectComponents = null;
	}

	//***************************************
	//  Component Queries
	//***************************************
	
	/***
	 * provide the all protocols defined in the registered environment by the 
	 * OCS reference name.
	 */
	public Set<String> getProtocols() {
		if(protocols == null) {
			getProjectEnvSpecs();
		}
		return protocols;
	}
	
	/***
	 * Given the component XML file name, provide the component
	 * name.
	 */
	public String getComponentName (String specFileName) {
		
		if(specFileName.toLowerCase().endsWith("spec.xml")) {
			String componentName = specFileName.substring(0, specFileName.length() -9);
			return componentName;
		}
		return null;
	}
	
	public Collection<String> getApplicationComponents() {
		if(uiSpecsLookup == null) {
			getProjectEnvSpecs();
		}
		return uiSpecsLookup.keySet();
	}
	
	/***
	 * Given the display name (typically used in drop down lists),
	 * find and return the spec info on the component.
	 * @return UiComponentSpec or null if not found.
	 */
	public UiComponentSpec getUiSpecByDisplayName(String specSelection) {
		if(uiSpecsLookup == null) {
			getProjectEnvSpecs();
		}
		return uiSpecsLookup.get(specSelection);
	}

	public Set<String> getComponentsAvailableToProject(String eclipseProjectName) {
		if(uiSpecsLookup == null) {
			getProjectEnvSpecs();
		}
		String projectPath = projectPathLookup.get(eclipseProjectName);
		AngryViperProjectInfo p =  projectLookup.get(projectPath);
		if(p == null) {
			return null;
		}
		if(p.packageId == null || ! p.isRegistered()) {
			//this is not a registered project.
			return null;
		}
		
		String packageId = p.packageId;
		if(componentsLookup.containsKey(packageId)) {
			return componentsLookup.get(packageId);
		}
		
		TreeSet<String> components = new TreeSet<String>();
		componentsLookup.put(packageId, components);
		Filter f = getProjectDependencies(p);
		
		for(UiComponentSpec spec :  uiSpecsLookup.values()) {
			if(f.meetsCondition(spec)) {
				components.add(spec.getDisplayName());
			}
		}
		return components;
	}

	/***
	 * This was needed by the drag and drop service. It knows the eclipse project name,
	 * the name of the library, and the component XML file name.
	 * @param projectName - the name of the project in the eclipse workspace.
	 * @param library - the name of the library that holds it.
	 * @param specFileName - the component XML filename.
	 * @return UiComponentSpec (it will always exist).
	 */
	public String getApplicationSpecName(String fulProjectPathname, String libName, String specFileName) {
		if(ocpiProjectComponents == null) {
			getProjectEnvSpecs();
		}
		
		String errorMessage = null;
//		if(specFileName.endsWith(".xml")) {
//			specFileName = specFileName.substring(0, specFileName.length()-4);
//		}
		
		List<UiComponentSpec> specNames = ocpiProjectComponents.get(specFileName);

		if(specNames != null) {
			AngryViperProjectInfo project = projectLookup.get(fulProjectPathname);
			if(project != null) {
				for(UiComponentSpec name : specNames) {
					if(name.oasReference.contains(project.packageId)) {
						return name.oasReference;
					}
				}
				errorMessage =	"You have pulled a unknown spec filename.\n\n";
			}
			else {
				errorMessage = "You have pulled a spec from an unknown project.\n\n";
			}
		}
		else {
			errorMessage = "You have pulled a unknown spec filename.\n\n";
		}
		
		errorMessage += "Verify the project is registered properly and try a refresh via OpenCPI Projects View."
				+ " If that doesn't work, here are some suggestions:\n"
				+ " - Check the project package ID in the exports/project-package-id file.\n"
				+ "   If the project package ID does not match the registered package ID,\n"
				+ "   the project needs to be cleaned and rebuilt.\n"
				+ " - refresh Eclipse (right-click into AngryViperProjectInfo Explorer) then refresh\n"
				+ "   the OpenCPI AngryViperProjectInfos view.";

		MessageDialog.openInformation(Display.getDefault().getActiveShell(), "Environment State Error", errorMessage);		
		return null;
	}
	
	
	//***************************************
	//    Workers 
	//***************************************
	
	/**
	 * Change Processing.
	 * An HDL worker asset is created or deleted.
	 */
	public void addHdlWorker(AngryViperAsset newAsset) {
		// Kept it simple for now.
		allHdlWorkers = null;
	}
	public void removeHdlWorker(AngryViperAsset asset) {
		allHdlWorkers = null;
	}
	
	/***
	 * The OHAD Editor needs the HDL Workers.
	 * The worker name is the name of the worker
	 * directory.
	 */
	private Set<String> allHdlWorkers = null;

	public Set<String> getAllHdlWorkers() {
		if(allHdlWorkers != null) {
			return allHdlWorkers;
		}
		allHdlWorkers = new HashSet<String>();
		//searchForAdapters();
		//allHdlWorkers.addAll(getHdlAdapters());
		
		// Uses ocpidev to get all env workers.
		String[] allWorkers = getProjectEnvWorkers ();
		
		for(String workerFolderName : allWorkers) {
			if(workerFolderName.endsWith(".hdl")) {
				String workerName = workerFolderName.substring(0, workerFolderName.length() -4);
				if(!allHdlWorkers.contains(workerName)) {
					allHdlWorkers.add(workerName);
					//System.out.println( workerName + " was not in the set.");
				}
			}
		}
		return allHdlWorkers;
	}
	
	//***************************************
	//  Data Assembly Methods
	//***************************************
	
	private HashSet<String> invalidSpecPath;
	private ArrayList<AngryViperProjectInfo> envProjects = null;
	
//	private Set<String> hdlAdapters = null;
//	public void searchForAdapters() {
//		getProjectEnvHdlAdapters();
//	}
//	public Set<String> getHdlAdapters() {
//	if(hdlAdapters == null) {
//		searchForAdapters();
//	}
//	return hdlAdapters;
//}
	
	private abstract class Filter {
		String [] testSamples;
		public Filter(String [] tests) {
			testSamples = tests;
		}
		public abstract boolean meetsCondition(UiComponentSpec sample);
	}
	private Filter getProjectDependencies(AngryViperProjectInfo project) {
		String [] tests = getProjectDependencies(project.fullPath, project.packageId);
		return new Filter(tests) {
			
			public boolean meetsCondition(UiComponentSpec sample) {
				for(String test : tests) {
					if(sample.packageId.startsWith(test)) {
						return true;
					}
				}
				return false;
			}
		};
	}

	private String[] getProjectDependencies(String fullPath, String packageId) {
		File dir = new File(fullPath);
		File projectMk = null;
		
		if(dir.exists() && dir.isDirectory()) {
			String[] children = dir.list();
			for(String child : children) {
				if("Project.mk".equals(child)) {
					projectMk = new File(dir,"Project.mk" );
					break;
				}
			}
		}
		if(projectMk != null) {
			if(projectMk.exists()) {
				String dependenciesList = readDependencies(projectMk);
				String[] depsList;
				int i=0;
				if(dependenciesList == null ) {
					depsList = new String[2];
				}
				else {
					String[] s = dependenciesList.split(" ");
					depsList = new String[s.length+2];
					for(String dep : s) {
						depsList[i++] = dep;
					}
				}
				depsList[i++] = "ocpi.core";
				depsList[i] = packageId;
				return depsList;
			}
		}
		return new String[0];
	}

	private String readDependencies(File projectMk) {
		FileReader r;
		try {
			r = new FileReader(projectMk);
		} catch (FileNotFoundException e) {
			return null;
		}
		BufferedReader br = new BufferedReader(r);
		String line;
		try {
			while((line = br.readLine()) != null) {
				line = line.trim();
				if(line.startsWith("#"))
					continue;
				
				if(line.startsWith("ProjectDependencies")) {
					String[] s = line.split("=");
					if(s.length>=2) {
						return s[s.length -1];
					}
				}
			}
		} catch (IOException e) {
			return null;
		}
		finally {
			try {
				br.close();
			} catch (IOException e) {
			}
		}
		return null;
	}

	protected String [] showProjectCmd = {"ocpidev", "-d", null, "show", "project", "--local-scope", "--json"};
	
	protected String [] getProjectsCmd = {"ocpidev", "show", "projects", "--json"};
	protected String [] getWorkersCmd = {"ocpidev", "show", "workers", "--simple"};
	protected String [] getComponentsCmd = {"ocpidev", "show", "components", "--simple"};

	
	private String[] getProjectEnvWorkers () {
		MessageConsole cons = AvpsResourceManager.getInstance().getNoticeConsoleInView();
		StringBuilder errMessage = new StringBuilder();
		String workerList = CommandExecutor.getCommandResult(getWorkersCmd, cons, errMessage);
		String[] ocpiWorkers;
		if(workerList != null) {
			String[] outputLines = workerList.split("\n");
			ArrayList<String> list = new ArrayList<String>(outputLines.length);
			for(String line : outputLines) {
				String[] parts = line.split(":");
				if(parts[0].contains("Wor")) {
					list.add(parts[1].trim());
				}
			}
			ocpiWorkers = list.toArray(new String[list.size()]);
		}
		else {
			ocpiWorkers = new String[0];
		}
		return ocpiWorkers;
	}
	
//	private void getProjectEnvHdlAdapters() {
//		hdlAdapters = new HashSet<String>();
//		Collection<AngryViperProjectInfo> projects = projectLookup.values();
//		for (AngryViperProjectInfo project : projects) {
//			if(project.name == null || "ocpi.cdk".equals(project.name))
//				continue;
//			
//			File projectFolder = new File(project.fullPath);
//			if (projectFolder != null && projectFolder.exists()) {
//				File adaptersDir = new File(projectFolder, "hdl/adapters");
//				if(adaptersDir.exists() && adaptersDir.isDirectory() ){
//					String[] children = adaptersDir.list();
//					for(String child : children) {
//						if(child.endsWith(".hdl")) {
//							hdlAdapters.add(child.substring(0, child.length() -4));
//						}
//					}
//				}
//			}
//		}
//	}
//
	
	private void getProjectEnvSpecs() {
		// keys are the file name, values are the fully qualified component name.
		uiSpecsLookup = new TreeMap<String,UiComponentSpec>();
		// Can have specs with the same name in different projects.
		ocpiProjectComponents = new HashMap<String,List<UiComponentSpec>>();
		protocols = new TreeSet<String>();
		componentsLookup = new HashMap<String,Set<String>>();
		
		Collection<AngryViperProjectInfo> projects = projectLookup.values();
		for (AngryViperProjectInfo project : projects) {
			if(project.name == null || "ocpi.cdk".equals(project.name) || ! project.isRegistered)
				continue;
			
			File projectFolder = new File(project.fullPath);
			if (projectFolder != null && projectFolder.exists()) {
				File topSpecsDir = new File(projectFolder, "specs");
				if(topSpecsDir.exists() && topSpecsDir.isDirectory() ){
					String packageName = getPackage(topSpecsDir, null);
					
					getSpecs(topSpecsDir, packageName, project.packageId);
				}
				
				findSpecs(projectFolder,"components", project.packageId);
				
				File hdlFolder = new File(projectFolder, "hdl");
				findSpecs(hdlFolder, "adapters", project.packageId);
				findSpecs(hdlFolder, "cards", project.packageId);
				findSpecs(hdlFolder, "devices", project.packageId);
				findSpecs(hdlFolder, "platforms", project.packageId);
			}
		}
	}
	
	protected boolean itsNotOneLike(String dirName) {
		boolean itsNotLike = true;
		if( "Makefile".equals(dirName) || dirName.endsWith(".rcc") 
			|| dirName.endsWith(".hdl") || dirName.endsWith(".hdl")
			|| dirName.endsWith(".test") || dirName.endsWith(".mk")) {
			itsNotLike = false;
		}
		return itsNotLike;
	}
	
	protected void findSpecs (File parentDir, String directory, String projectPackageId) {
		File dir = new File(parentDir, directory);
		if(dir.exists() && dir.isDirectory()) {
			File specsDir = new File(dir, "specs");
			if(specsDir.exists() && specsDir.isDirectory()) {
				String libPackageId = getPackage(dir, null);
				getSpecs(specsDir, libPackageId, projectPackageId);
			}
			else {
				String[] children = dir.list();
				for(String child : children) {
					if( ! invalidSpecPath.contains(child) && itsNotOneLike(child)) {
						findSpecs(dir, child, projectPackageId);
					}
				}
			}
		}
	}
	
	protected void getSpecs(File specsDir, String libPackageId, String projectPackageId) {
		if( ! specsDir.exists() || ! specsDir.isDirectory())
			return;
		
		String[] children = specsDir.list();
		for(String child : children) {
			File specFile = new File(specsDir, child);
			if(specFile.isFile()){
				String fileName = specFile.getName();
				if (UiComponentSpec.isComponentFile(fileName)) {
					UiComponentSpec aSpec = new UiComponentSpec();
					aSpec.fileName = fileName;
					aSpec.packageId = libPackageId;
					aSpec.projectPackageId = projectPackageId;
					try {
					aSpec.componentName = fileName.substring(0, fileName.length() - 9);
					}
					catch(Exception e) {
						System .out.println("err- " + aSpec.componentName);
						continue;
					}
					aSpec.owdReference = fileName.substring(0, fileName.length() - 4);
					aSpec.oasReference = libPackageId + "." + aSpec.componentName;
					
					uiSpecsLookup.put(aSpec.getDisplayName(), aSpec);
					List<UiComponentSpec> specsList = ocpiProjectComponents.get(fileName);
					if(specsList != null) {
						specsList.add(aSpec);
					}
					else {
						specsList = new ArrayList<UiComponentSpec> ();
						specsList.add(aSpec);
						ocpiProjectComponents.put(fileName, specsList);
					}
				}
				// Protocols can live anywhere, they are not treated like
				// components.
				else if (fileName.endsWith("-prot.xml") ||
						fileName.endsWith("_prot.xml") ||
						fileName.endsWith("-protocol.xml") ||
						fileName.endsWith("_protocol.xml")) {
					protocols.add(fileName.replace(".xml", ""));
				}
				
			}
		}
		
	}
	
	protected String getPackageId(File packageFile) {
		String packageName = "";
		String line = null;
		FileReader fileReader = null;
		BufferedReader bufferedReader = null;
		try {
			fileReader = new FileReader(packageFile);
			bufferedReader = new BufferedReader(fileReader);
			line = bufferedReader.readLine();
			if(line != null) {
				packageName = line; // +".";
			}
		} catch (FileNotFoundException e) {
		} catch (IOException e) {
		} finally {
			try {
				if (fileReader != null) {
					fileReader.close();
				}
				if (bufferedReader != null) {
					bufferedReader.close();
				}
			} catch (IOException e) {
			}
		}
				
		return packageName;
	}

	protected String getPackage(File dir, String[] children) {
		String packageName = "";
		File libDir = new File(dir, "lib");
		if(libDir.exists() && libDir.isDirectory()) {
			File packageIdFile = new File(libDir, "package-id"); 
			if(packageIdFile.exists() && packageIdFile.isFile()) {
				packageName = getPackageId(packageIdFile);
			}
		}
		return packageName;
	}

}
