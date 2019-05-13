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
import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.modeling.ResourceStoreException;
import org.eclipse.sapphire.modeling.xml.RootXmlResource;
import org.eclipse.sapphire.modeling.xml.XmlResourceStore;

import av.proj.ide.avps.internal.AvpsResourceManager;
import av.proj.ide.avps.xml.Project;

/***
 * This class is working to optimize reading the environment since ocpidev show is taking
 * much longer. The intent is to use this class to construct the colleague classes used by 
 * AngryViperAssetService.  This was done quickly to make the UI startup more responsive
 * and to integrate with the current implementations to provide correct information and
 * not impact refresh processing.  All of this could use a refactoring touch. One of the core
 * issues faced with these classes was class size as the UI grew in capability.
 */
public class LoadOpenCPIEnvironment {
	
	ArrayList<IProject> eclipseProjects = null; 
	EnvBuildTargets envBuildInfo = null;
	OpencpiEnvService environmentService =  null;
	OpenCpiAssets assetsRepo = null;
	
	private String cdkPath;
	private String scripts;
	private File scriptsDir;
	private ArrayList<Thread> loadThreads = new ArrayList<Thread>();
	
	public LoadOpenCPIEnvironment() {
		cdkPath = System.getenv("OCPI_CDK_DIR");
		scripts = cdkPath + "/scripts";
		scriptsDir = new File(scripts);
	}


	public void loadCoreServices() {
		loadBuildTargets();
		getEclipseWorkspaceProjects();
		loadOcpiEnvService();
		waitOnThreads();
		envBuildInfo.loadVendorPlatforms();		
		environmentService.mergeElipseProjectData(eclipseProjects);
		
		// In current usage, this is not time critical.
		scanForOcpiProjects(environmentService.getEnvProjects());
	}
	
	public boolean loadDataStores () {
		boolean loadOk = true;
		loadBuildTargets();
		loadEclipseProjects();
		loadOcpiEnvService();
		waitOnThreads();
		
		environmentService.mergeElipseProjectData(eclipseProjects);
		envBuildInfo.loadVendorPlatforms();		
		assetsRepo = new OpenCpiAssets();
		loadUiAssets();
		waitOnThreads();
		
		assetsRepo.setEnvTargets(envBuildInfo);
		return loadOk;
	}
	
	/***
	 * These public interfaces will lazy load the respective data store
	 * if it has not been done already.  In most cases no concurrency is used.
	 */
	public ArrayList<IProject> getEclipseProjects() {
		if(eclipseProjects == null) {
			getEclipseWorkspaceProjects();
		}
		return eclipseProjects;
	}

	public EnvBuildTargets getEnvBuildInfo() {
		if(envBuildInfo == null) {
			envBuildInfo = new EnvBuildTargets();
			loadBuildTargets();
			waitOnThreads();
			envBuildInfo.loadVendorPlatforms();		
		}
		return envBuildInfo;
	}

	public OpencpiEnvService getEnvironmentService() {
		if(environmentService == null) {
			if(eclipseProjects == null) {
				getEclipseProjects();
			}
			environmentService = new OpencpiEnvService();
			environmentService.getEnvironmentProjects();
			environmentService.mergeElipseProjectData(eclipseProjects);
		}
		return environmentService;
	}


	public OpenCpiAssets getAssetsRepo() {
		if(assetsRepo == null) {
			if(environmentService == null) {
				loadDataStores();
			}
			else {
				assetsRepo = new OpenCpiAssets();
				getEnvBuildInfo();
				getEclipseProjects();
				loadUiAssets();
				waitOnThreads();
				assetsRepo.setEnvTargets(envBuildInfo);
			}
		}
		return assetsRepo;
	}
	
	/***
	 * Internal concurrent loading.
	 */
	protected void waitOnThreads() {
        boolean done = false;
        while (!done)
        {
            try
            {
        		for(Thread loading : loadThreads) {
        			loading.join();
        			//System.out.println(loading.getName() + " finished " +System.currentTimeMillis());
        		}
                done = true;
                loadThreads.clear();
            }
            catch (InterruptedException e)
            {
    			//System.out.println("Got Interrupt");
            }
        }
        // Perform Second check.
		for(Thread loading : loadThreads) {
			try {
				loading.join();
			} catch (InterruptedException e) {
			}
			//System.out.println(loading.getName() + " finished " +System.currentTimeMillis());
		}
        
	}

	protected void loadBuildTargets() {
    	envBuildInfo = new EnvBuildTargets();
    	long start = System.currentTimeMillis();
		Thread loader = new Thread(new Runnable() {
            public void run()
            {
            	envBuildInfo.buildHdPlatforms();
            }
        });
		loadThreads.add(loader);
		loader.setName(start + " Start HdlPlatforms ");
		loader.start();
		
		loader = new Thread(new Runnable() {
            public void run()
            {
            	envBuildInfo.buildHdlVendors();
            }
        });
		loadThreads.add(loader);
		loader.setName(start + " Start HdlVendors ");
		loader.start();
		
		loader = new Thread(new Runnable() {
            public void run()
            {
            	envBuildInfo.buildRccPlatforms();
            }
        });
		loadThreads.add(loader);
		loader.setName(start + " Start RCC Platforms ");
		loader.start();
	}
	
	protected void getEclipseWorkspaceProjects() {
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IProject [] projects = workspace.getRoot().getProjects();
		int count = projects.length;
		eclipseProjects = new ArrayList<IProject>(count);
		
		IProject project;
		for(int i= 0; i < count; i++) {
			project = projects[i];
			if(! project.isOpen()) continue;
			if("RemoteSystemsTempFiles".equals(project.getName())) continue;
			
			eclipseProjects.add(project);
		}
	}
	
	protected void loadEclipseProjects() {
    	long start = System.currentTimeMillis();
		Thread loader = new Thread(new Runnable() {
            public void run()
            {
            	getEclipseWorkspaceProjects();
            }
        });
		loadThreads.add(loader);
		loader.setName(start + " Eclipse Projects ");
		loader.start();
	}
	
	protected void loadOcpiEnvService() {
		environmentService = new OpencpiEnvService();
    	long start = System.currentTimeMillis();
		Thread loader = new Thread(new Runnable() {
            public void run()
            {
            	environmentService.getEnvironmentProjects();
            }
        });
		loadThreads.add(loader);
		loader.setName(start + " Environment Service ");
		loader.start();
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
	
	protected void scanForOcpiProjects(Collection<AngryViperProjectInfo> projects) {
		for(AngryViperProjectInfo project : projects) {
			if(! project.isOpenCpiProject()) {
				runOcpiProjectCheck(project.fullPath, project);
			}
		}
	}
	
	protected void runOcpiProjectCheck(String projpath, AngryViperProjectInfo info) {
		Thread loader; 
    	long start = System.currentTimeMillis();
		loader = new Thread(new Runnable() {
            public void run()
            {
            	environmentService.checkForOpciPackageId(projpath, info);
            }
        });
		loadThreads.add(loader);
		loader.setName(start + " OcpiCheck " + info.eclipseName);
		loader.start();
	}
	
	protected void loadUiAssets () {
		Thread loader; 
    	long start = System.currentTimeMillis();
		for(IProject eProject : eclipseProjects) {
			loader = new Thread(new Runnable() {
	            public void run()
	            {
	            	loadProjectAssets(eProject);
	            }
	        });
			loadThreads.add(loader);
			loader.setName(start + " Load XML " + eProject.getName());
			loader.start();
		}
	}
	
	
	protected void loadProjectAssets(IProject project) {
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
			// This throws the exception if project.xml doesn't exist.
			is = projectFile.getContents(true);
			
			XmlResourceStore store = new XmlResourceStore(is);
			RootXmlResource xmlResource = new RootXmlResource(store);
			Project proj = Project.TYPE.instantiate(xmlResource);
			
			// Project XML gives the project name as the package Id. This information
			// is preserved in the projectLocation object. This is used in a number
			// of places to get project info when processing the project's assets.
			// 
			// pInfo should have a name for the project based on project directory.
			// The eclipse project holds the name of the project in eclipse which is
			// a one off for the core and assets projects.
			
			/***
			 * TODO: re-think some of this.
			 * 1. Eclipse IProject is the first thing obtained.  It is used throughout
			 *    the application (from varied sources).
			 * 2. ProjectLocation and AngryViperProjectInfo contain much of the same info.
			 *    Both originate in this class.  They should become one class that is well
			 *    defined.  IProject may belong in it; need to consider it going stale?
			 * 2. Re-evaluate how these objects are created and used when ocpidev show is
			 *    integrated.
			 */
			
			Value<String> projNameVal = proj.getName();
			String packageId = null;
			if(projNameVal != null) {
				packageId = projNameVal.content();
			}
			// This project is open in eclipse - pInfo will exist.
			AngryViperProjectInfo pInfo = environmentService.lookupProjectByPath(projpath);
			
			if(! pInfo.isOpenCpiProject()) {
				// This is an OpenCPI project because there is a project XML but it is not
				// registered. Update ProjectInfo to get the repo current.
				if(packageId != null) {
					environmentService.updateOpciPackageId(pInfo, packageId, projpath);
				}
				else {
					// Try another way--shouldn't happen if project.xml is well formed.
		            environmentService.checkForOpciPackageId(projpath, pInfo);
				}
			}
			
			// Should get this from pInfo as well.
			ProjectLocation location = pInfo.getProjectLocation();
			assetsRepo.loadProject(location, proj, pInfo);
		} catch (CoreException | ResourceStoreException | IOException | InterruptedException e) {
			AvpsResourceManager.getInstance().writeToNoticeConsole("Error obtaining project metadata. This happens when a project is not an ANGRYVIPER project.\n --> " + e.toString() );
		}
		finally {
			if(is != null) {
				try {
					is.close();
				} catch (IOException ex) {
				}
			}
		}
	}

}
