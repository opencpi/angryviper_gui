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
import org.json.simple.JSONObject;

public class RegisteredProjectSearchTool {
	private Map<String, String> eclipseToRegisterProjectName;
	private Map<String, String> specs;
	private Map<String, List<String>> specLookup;
	private Set<String> registeredProjects;
	private Set<String> registeredProjectsLessCore;

	private Set<String> protocols;
	private Set<String> hdlAdapters;
	private HashSet<String> invalidSpecPath;
	
	
	public RegisteredProjectSearchTool() {
		this.eclipseToRegisterProjectName = new HashMap<String,String>();
		this.specs = new TreeMap<String,String>();
		this.specLookup = new HashMap<String,List<String>>();
		this.protocols = new TreeSet<String>();
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
		searchForSpecs();
	}
	
	protected String [] getProjectsCmd = {"ocpidev", "show", "projects", "--json"};

	public class Project {
		public String name;
//		public Boolean registered;
//		public Boolean exists;
		public String fullPath;
		public String eclipseName;
		public String projectDirectory;
		
		protected Project (String name, JSONObject prjData) {
			this.name = name;
			fullPath = (String) prjData.get("real_path");
			String[] pathSegments = fullPath.split("/");
			projectDirectory = pathSegments[pathSegments.length -1];
/*(**			
 Not needed at this time.			
			Object obj = prjData.get("registered");
			if(obj instanceof Boolean) {
				registered = (Boolean) obj;
			}
			
			obj = prjData.get("exists");
			if(obj instanceof Boolean) {
				exists = (Boolean) obj;
			}
***/
		}
	}
	
	protected List <Project> getOcpiProjects() {
		
		// Figure out various pieces of the environment.
		HashMap<String, String> eclipseProjectInfo = new HashMap<String, String>();
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IProject [] eProjects = workspace.getRoot().getProjects();
		IProject eProject;
		for(int i= 0; i<eProjects.length; i++) {
			eProject = eProjects[i];
			
			if(! eProject.isOpen()) continue;
			if("RemoteSystemsTempFiles".equals(eProject.getName())) continue;
			
			String eProjectName = eProject.getName();
			IPath path = eProject.getLocation();
			String[] pathSegments = path.segments();
			String projectDir = pathSegments[pathSegments.length -1];
			eclipseProjectInfo.put(projectDir, eProjectName);
		}
		
		JSONObject jsonObject = EnvBuildTargets.getEnvInfo(getProjectsCmd);
        ArrayList<Project> envProjects = new ArrayList<Project>();
		if(jsonObject != null) {
        	JSONObject projectsObj = (JSONObject) jsonObject.get("projects");

        	if(projectsObj != null) {
    	        @SuppressWarnings("unchecked")
    			Set<String> projects = projectsObj.keySet();
    	        for(String key : projects) {
    	        	Project project = new Project(key, (JSONObject) projectsObj.get(key));
    	        	String eProjectName = eclipseProjectInfo.get(project.projectDirectory);
    	        	project.eclipseName = eProjectName;
    	        	envProjects.add(project);
        			registeredProjects.add(project.name);
        			if(project.eclipseName != null) {
        	        	eclipseToRegisterProjectName.put(project.eclipseName, project.name);
        			}
        			else {
        				// Try this
        	        	eclipseToRegisterProjectName.put(project.projectDirectory, project.name);
        			}
        			if(!( project.name.equalsIgnoreCase("ocpi.core") || project.name.equalsIgnoreCase("ocpi.cdk"))) {
        				registeredProjectsLessCore.add(project.name);
        			}
    	        }
   		    }
		}
		return envProjects;
	}
	
	public void searchForSpecs() {
		List<Project> projects = getOcpiProjects();
		
		getProjectEnvSpecs(projects);
	}
	
	public void searchForAdapters() {
		List<Project> projects = getOcpiProjects();
		getProjectEnvHdlAdapters(projects);
	}
	
	private void getProjectEnvHdlAdapters(List<Project> projects) {
		hdlAdapters = new HashSet<String>();
		for (Project project : projects) {
			if("ocpi.cdk".equals(project.name))
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

	public Map<String, String> getSpecs() {
		return specs;
	}
	
	public Set<String> getSpecsList() {
		return specs.keySet();
	}
	
	public Collection<String> getApplicationComponents() {
		return specs.values();
	}
	
	public String getApplicationSpecName(String eclipseProjectName, String libName, String specFileName) {
		String errorMessage = null;
		
		List<String> specNames = specLookup.get(specFileName);
		if(specNames != null) {
			String registeredProjectName = eclipseToRegisterProjectName.get(eclipseProjectName);
			if(registeredProjectName != null) {
				
				for(String name : specNames) {
					if(name.contains(registeredProjectName)) {
						return specs.get(name);
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
	
	public Map<String, List<String>> getSpecLookup() {
		return specLookup;
	}

	public Set<String> getProtocols() {
		return protocols;
	}
	public Set<String> getHdlAdapters() {
		return hdlAdapters;
	}
	
	public Set<String> getRegisteredProjects() {
		return registeredProjects;
	}

	public Set<String> getRegisteredProjectsLessCore() {
		return registeredProjectsLessCore;
	}

	private void getProjectEnvSpecs(List<Project> projects) {
		for (Project project : projects) {
			if("ocpi.cdk".equals(project.name))
				continue;
			
			File projectFolder = new File(project.fullPath);
			if (projectFolder != null && projectFolder.exists()) {
				File topSpecsDir = new File(projectFolder, "specs");
				if(topSpecsDir.exists() && topSpecsDir.isDirectory() ){
					String packageName = getPackageId(topSpecsDir, topSpecsDir.list());
					getSpecs(topSpecsDir, packageName);
				}
				
				findSpecs(projectFolder,"components");
				
				File hdlFolder = new File(projectFolder, "hdl");
				findSpecs(hdlFolder, "adapters");
				findSpecs(hdlFolder, "cards");
				findSpecs(hdlFolder, "devices");
				findSpecs(hdlFolder, "platforms");
			}
		}
	}
	
	protected boolean itsNotOneLike(String dirName) {
		boolean itsNotLike = true;
		if( dirName.endsWith(".rcc") 
			|| dirName.endsWith(".hdl") || dirName.endsWith(".hdl")
			|| dirName.endsWith(".test")) {
			itsNotLike = false;
		}
		return itsNotLike;
	}
	
	protected void findSpecs (File parentDir, String directory) {
		File dir = new File(parentDir, directory);
		if(dir.exists() && dir.isDirectory()) {
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
			
		}
	}
	
	protected void getSpecs(File specsDir, String packageName) {
		if( ! specsDir.exists() || ! specsDir.isDirectory())
			return;
		
		String[] children = specsDir.list();
		for(String child : children) {
			File specFile = new File(specsDir, child);
			if(specFile.isFile()){
				String name = specFile.getName();
				String specName;
				String frameworkComponentName;
				if (name.endsWith("-spec.xml")) {
					
					specName = packageName + name.substring(0, name.length() - 4);
					frameworkComponentName = packageName + name.replace("-spec.xml", "");
					specs.put(specName, frameworkComponentName);
					List<String> specsList = specLookup.get(name);
					if(specsList != null) {
						specsList.add(specName);
					}
					else {
						specsList = new ArrayList<String> ();
						specsList.add(specName);
						specLookup.put(name, specsList);
					}
				} else if (name.endsWith("_spec.xml")) {
					specName = packageName + name.substring(0, name.length() - 4);
					frameworkComponentName = packageName + name.replace("_spec.xml", "");
					specs.put(specName, frameworkComponentName);
					List<String> specsList = specLookup.get(name);
					if(specsList != null) {
						specsList.add(specName);
					}
					else {
						specsList = new ArrayList<String> ();
						specsList.add(specName);
						specLookup.put(name, specsList);
					}
				}
				else if (name.endsWith("-prot.xml") ||
						name.endsWith("_prot.xml") ||
						name.endsWith("-protocol.xml") ||
						name.endsWith("_protocol.xml")) {
					protocols.add(name.replace(".xml", ""));
				}
				
			}
		}
		
	}
	
	protected String getPackageId(File pkgIdFolder, String[] children) {
		String packageName = "";
		for(String file : children) {
			if("package-id".equals(file)) {
				File packageFile = new File(pkgIdFolder, file);
				String line = null;
				FileReader fileReader = null;
				BufferedReader bufferedReader = null;
				try {
					fileReader = new FileReader(packageFile);
					bufferedReader = new BufferedReader(fileReader);
					line = bufferedReader.readLine();
					if(line != null) {
						packageName = line +".";
						break;
					}
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					try {
						if (fileReader != null) {
							fileReader.close();
						}
						if (bufferedReader != null) {
							bufferedReader.close();
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				
			}
		}
		return packageName;
	}

	protected String getPackage(File dir, String[] children) {
		String packageName = "";
		for(String child : children) {
			if("lib".equals(child)) {
				File libDir = new File(dir, child);
				children = libDir.list();
				packageName = getPackageId(libDir, children);
			}
		}
		return packageName;
	}
	
}
