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
 * Since there are users of the service with differing needs lazy loading is used.  Project
 * information is globally used so this information is obtained on construction. Spec and
 * worker information is not obtained until requested.
 *
 */
public class RegisteredProjectsService {
	
	/**
	 * The project registry 
	 */
	private Map<String, Project> ocpiProjectLookup;
	private Map<String, Project> eclipseProjectLookup;
	private Set<String> registeredProjects;
	private Set<String> registeredProjectsLessCore;

	/***
	 * Components are referenced several ways.  Given the component file name is
	 * file_read_spec.xml where "file_read" is the component name:
	 * - Application OAS wants packageId + component name.
	 * - Worker OWD wants the component file name minus ".xml"
	 * - Unit Test creation wants the component name.
	 */
	
	/***
	 * specLookup - holds a list of UiComponentSpec objects with the component XML filename as the key.
	 * A list is help because a components can have the same name but live in different packages.
	 * A UiComponentSpec holds the variety of reference names for the component used by the framework.
	 */
	Map<String, List<UiComponentSpec>> specLookup;
	
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
	
	/***
	 * These sets the respective assets derived from the entire registered
	 * opencpi environment.  
	 *  - protocols by the protocol filename minus the .xml extension,
	 *  - hdlAdapters by adapter foldername minus the .hdl extension,
	 *  - ocpiWorkers holds all the registered environment workers provided by 
	 *    the ocpidev show workers command.
	 *  - allHdlWorkes holds the union of hdlAdapters and ocpiWorkers.
	 *  
	 */
	private Set<String> protocols = null;
	private Set<String> hdlAdapters = null;
	private String[] ocpiWorkers = null;
	private Set<String> allHdlWorkers = null;
	
	private HashSet<String> invalidSpecPath;
	private ArrayList<Project> envProjects = null;
	
	public RegisteredProjectsService() {
		
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
		
		getOcpiProjects();
	}
	
	
	public void searchForAdapters() {
		getProjectEnvHdlAdapters();
	}
	
	public String[] getAllWorkers() {
		if(ocpiWorkers == null) {
			getProjectEnvWorkers ();
		}
		return ocpiWorkers;
	}
	
	public Set<String> getAllHdlWorkers() {
		if(allHdlWorkers != null) {
			return allHdlWorkers;
		}
		allHdlWorkers = new HashSet<String>();
		searchForAdapters();
		allHdlWorkers.addAll(getHdlAdapters());
		
		// Uses ocpidev to get all env workers.
		String[] allWorkers = getAllWorkers();
		
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
	
	public Collection<String> getApplicationComponents() {
		if(uiSpecsLookup == null) {
			getProjectEnvSpecs();
		}
		return uiSpecsLookup.keySet();
	}
	public UiComponentSpec getUiSpecByDisplayName(String specSelect) {
		if(uiSpecsLookup == null) {
			getProjectEnvSpecs();
		}
		return uiSpecsLookup.get(specSelect);
	}

	public Set<String> getComponentsAvailableToProject(String eclipseProjectName) {
		if(uiSpecsLookup == null) {
			getProjectEnvSpecs();
		}
		Project p = eclipseProjectLookup.get(eclipseProjectName);
		if(p == null) {
			return null;
		}
		if(p.packageId == null) {
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

	private abstract class Filter {
		String [] testSamples;
		public Filter(String [] tests) {
			testSamples = tests;
		}
		public abstract boolean meetsCondition(UiComponentSpec sample);
	}
	private Filter getProjectDependencies(Project project) {
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

	public String getApplicationSpecName(String fulProjectPathname, String libName, String specFileName) {
		if(specLookup == null) {
			getProjectEnvSpecs();
		}
		
		String errorMessage = null;
//		if(specFileName.endsWith(".xml")) {
//			specFileName = specFileName.substring(0, specFileName.length()-4);
//		}
		
		List<UiComponentSpec> specNames = specLookup.get(specFileName);

		if(specNames != null) {
			Project project = ocpiProjectLookup.get(fulProjectPathname);
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
		
		errorMessage += "Verify the project is registered properly and try a refresh via OpenCPI Projects view."
				+ " If that doesn't work, here are some suggestions:\n"
				+ " - Check the project package ID in the exports/project-package-id file.\n"
				+ "   If the project package ID does not match the registered package ID,\n"
				+ "   the project needs to be cleaned and rebuilt.\n"
				+ " - refresh Eclipse (right-click into Project Explorer) then refresh\n"
				+ "   the OpenCPI Projects view.";

		MessageDialog.openInformation(Display.getDefault().getActiveShell(), "Environment State Error", errorMessage);		
		return null;
	}
	
	public Map<String, List<UiComponentSpec>> getSpecLookup() {
		if(specLookup == null) {
			getProjectEnvSpecs();
		}
		return specLookup;
	}

	public Set<String> getProtocols() {
		if(protocols == null) {
			getProjectEnvSpecs();
		}
		return protocols;
	}
	public Set<String> getHdlAdapters() {
		if(hdlAdapters == null) {
			searchForAdapters();
		}
		return hdlAdapters;
	}

	
	public Set<String> getRegisteredProjects() {
		return registeredProjects;
	}

	public Set<String> getRegisteredProjectsLessCore() {
		return registeredProjectsLessCore;
	}

	public Project getProjectInfo(String projectName) {
		return eclipseProjectLookup.get(projectName);
	}
	
	
	public class Project {
		
		// Opencpi name - it also is the name of the folder
		// in which it resides. See the top-level Project.mk
		// file for more details.  This name will be null if
		// the project is not registered but is open in eclipse.
		public String name = null;
		
		// The project can have a different name in eclipse.
		// If the project is not open in eclipse this name
		// remains null.
		public String eclipseName = null;
		
		public String packageId;
		public String fullPath;
		public String projectDirectory;
		
		public boolean isOpenInEclipse = false;
		private ProjectLocation location = null;
		
		public ProjectLocation getProjectLocation() {
			if(location == null) {
				if(name == null) {
					location = new ProjectLocation(eclipseName, fullPath);
				}
				else {
					location = new ProjectLocation(name, fullPath);
				}
			}
			return location;
		}
		
		public boolean isRegistered() {
			if(name == null)
				return false;
			else
				return true;
		}
		
		public boolean isOpenInEclipse() {
			return isOpenInEclipse;
		}

		// These are created internally, don't provide
		// public construction.
		protected Project(){}
		protected Project (String packageId, JSONObject prjData) {
			this.packageId = packageId;
			fullPath = (String) prjData.get("real_path");
			String[] pathSegments = fullPath.split("/");
			name = projectDirectory = pathSegments[pathSegments.length -1];
		}
	}

	protected String [] getProjectsCmd = {"ocpidev", "show", "projects", "--json"};
	protected String [] getWorkersCmd = {"ocpidev", "show", "workers", "--simple"};
	protected String [] getComponentsCmd = {"ocpidev", "show", "components", "--simple"};

	protected void getOcpiProjects() {
		// Figure out various pieces of the environment.
		
		HashMap<String, Project> projectPathLookup = new HashMap<String, Project>();
		HashMap<String, Project> eclipseLookup = new HashMap<String, Project>();
		
		JSONObject jsonObject = EnvBuildTargets.getEnvInfo(getProjectsCmd);
        envProjects = new ArrayList<Project>();
		if(jsonObject != null) {
        	JSONObject projectsObj = (JSONObject) jsonObject.get("projects");

        	if(projectsObj != null) {
    	        @SuppressWarnings("unchecked")
    			Set<String> projects = projectsObj.keySet();
    	        for(String key : projects) {
    	        	Project project = new Project(key, (JSONObject) projectsObj.get(key));
    	        	projectPathLookup.put(project .fullPath, project);
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
			Project ocpiProject = projectPathLookup.get(fullpath);
			if(ocpiProject == null) {
				Project eclipseProject = new Project();
				eclipseProject.eclipseName = eProjectName;
				eclipseProject.fullPath = fullpath;
				projectPathLookup.put(eProjectName, eclipseProject);
				eclipseLookup.put(eProjectName, eclipseProject);
				//System.out.println("Got null for: " + fullpath);
				continue;
			}
			ocpiProject.eclipseName = eProjectName;
			ocpiProject.isOpenInEclipse = true;
			eclipseLookup.put(eProjectName, ocpiProject);
			
		}
		ocpiProjectLookup = projectPathLookup;
		eclipseProjectLookup = eclipseLookup;
	}
	
	private void getProjectEnvWorkers () {
		MessageConsole cons = AvpsResourceManager.getInstance().getNoticeConsoleInView();
		StringBuilder errMessage = new StringBuilder();
		String workerList = CommandExecutor.getCommandResult(getWorkersCmd, cons, errMessage);
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
	}
	
	private void getProjectEnvHdlAdapters() {
		hdlAdapters = new HashSet<String>();
		Collection<Project> projects = ocpiProjectLookup.values();
		for (Project project : projects) {
			if(project.name == null || "ocpi.cdk".equals(project.name))
				continue;
			
			File projectFolder = new File(project.fullPath);
			if (projectFolder != null && projectFolder.exists()) {
				File adaptersDir = new File(projectFolder, "hdl/adapters");
				if(adaptersDir.exists() && adaptersDir.isDirectory() ){
					String[] children = adaptersDir.list();
					for(String child : children) {
						if(child.endsWith(".hdl")) {
							hdlAdapters.add(child.substring(0, child.length() -4));
						}
					}
				}
			}
		}
	}

	
	private void getProjectEnvSpecs() {
		// keys are the file name, values are the fully qualified component name.
		uiSpecsLookup = new TreeMap<String,UiComponentSpec>();
		// Can have specs with the same name in different projects.
		specLookup = new HashMap<String,List<UiComponentSpec>>();
		protocols = new TreeSet<String>();
		componentsLookup = new HashMap<String,Set<String>>();
		
		Collection<Project> projects = ocpiProjectLookup.values();
		for (Project project : projects) {
			if(project.name == null || "ocpi.cdk".equals(project.name))
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

/***		
			String[] children = dir.list();
			for(String child : children) {
				if("specs".equals(child)) {
					File specsDir = new File(dir, child);
					String[] specDirs = specsDir.list();
					
					if(specDirs.length == 0)
						break;
					String packageName = getPackage(dir, children);
					getSpecs(specsDir, packageName);
				}
				else if( ! invalidSpecPath.contains(child) && itsNotOneLike(child)) {
					findSpecs(dir, child);
				}
			}
*****/			
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
					List<UiComponentSpec> specsList = specLookup.get(fileName);
					if(specsList != null) {
						specsList.add(aSpec);
					}
					else {
						specsList = new ArrayList<UiComponentSpec> ();
						specsList.add(aSpec);
						specLookup.put(fileName, specsList);
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

//		for(String child : children) {
//			if("lib".equals(child)) {
//				File libDir = new File(dir, child);
//				children = libDir.list();
//				packageName = getPackageId(libDir, children);
//			}
//		}
		return packageName;
	}

}
