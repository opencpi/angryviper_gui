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

package av.proj.ide.services;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.eclipse.sapphire.PossibleValuesService;
import org.eclipse.sapphire.Transient;
import org.eclipse.sapphire.modeling.Status;

import av.proj.ide.avps.internal.AngryViperAssetService;
import av.proj.ide.oas.Application;

public class SpecPossibleValueService extends PossibleValuesService {
	
	private File currFile;
	private IProject currProject;
	private Application app;
	private boolean initialized;
	private List<Boolean> threadList;
	private Collection<String> files;
	private static volatile int instanceCount = 0;
	private static boolean appThreadStarted = false;
	
	@Override
	protected void initPossibleValuesService() {
		this.invalidValueSeverity = Status.Severity.OK;
		this.app = context(Application.class);
		if (this.app != null) {
			Transient<Boolean> init = this.app.getInitialized();
			if (init != null) {
				if (init.content() != null) {
					this.initialized = init.content();
				}
			}
				
			Thread appThread = this.app.getThread().content();
			this.threadList = this.app.getList().content();
			
			synchronized(this.threadList) {
				this.threadList.set(0, true);
				this.threadList.notify();
			}
			
			if (!appThreadStarted) {
				if (++instanceCount == this.app.getInstances().size()) {
					appThread.start();
					appThreadStarted = true;
				}
			}
		}
	}

	@Override
	protected void compute(Set<String> values) {
		if (!this.initialized) {
			values.clear();
			if (initCurrent()) {
				getSpecs();
				values.addAll(this.files);
			}
		} else {
			String possValues = "";
			if (app != null) {
				possValues = this.app.getValues().content();
			}
			values.addAll(new ArrayList<String>(Arrays.asList(possValues.split(","))));
		}
	}
	
	public void getSpecs() {
		this.files = AngryViperAssetService.getFrameworkComponents();

		String possValues = "";
		for (String s : this.files) {
			possValues += s+",";
		}
		if (this.app != null) {
			this.app.setValues(possValues);
		}
	}
	
	public String getSpecsString() {
		this.files = AngryViperAssetService.getFrameworkComponents();
		String possValues = "";
		for (String s : this.files) {
			possValues += s+",";
		}
		return possValues;
	}
	
	public void refreshInstance(String possValues) {
		if (this.app != null) {
			this.app.setValues(possValues);
		}
		refresh();
	}
	
	private boolean initCurrent() {
		Application app = context(Application.class);
		if (app != null) {
			Transient<String> loc = app.getLocation();
			if (loc != null) {
				if (loc.content() != null) {
					this.currFile = new File(loc.content());
					IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
					IContainer cont = root.getContainerForLocation(new Path(this.currFile.getPath()));
					if (cont != null) {
						this.currProject = cont.getProject();
						if (this.currProject != null && this.currProject.exists()) {
							this.initialized = true;
							this.app.setInitialized(true);
							return true;
						}
					}
				}
			}
		}
		
		return false;
	}

	/***
	private void getLocalSpecs() {
		String projPackage = getCurrentProjectPackage();
		this.traverser.setProjectPackage(projPackage);
		this.traverser.traverseForSpecs(new File(this.currProject.getLocation().toString()));
	}
	
	private void getExternalSpecs(String[] dependencies) {
		for (String s : dependencies) {
			File dependencyFolder = new File(s);
			if (dependencyFolder != null && dependencyFolder.exists()) {
				String projPackage = getProjectPackage(dependencyFolder);
				this.traverser.setProjectPackage(projPackage);
				this.traverser.traverseForSpecs(dependencyFolder);
			}
		}
	}
	
	private void getProjectPathSpecs() {
		String projPath = System.getenv("OCPI_PROJECT_PATH");
		if (projPath != null) {
			String[] split = projPath.split(":");
			for (String s : split) {
				File projPathFolder = new File(s);
				if (projPathFolder != null && projPathFolder.exists()) {
					String projPackage = getProjectPackage(projPathFolder);
					this.traverser.setProjectPackage(projPackage);
					this.traverser.traverseForSpecs(projPathFolder);
				}
			}
		}
	}
	
	private void getCdkDirSpecs() {
		String cdkDirPath = System.getenv("OCPI_CDK_DIR");
		if (cdkDirPath != null) {
			File cdkDir = new File(cdkDirPath);
			if (cdkDir != null && cdkDir.exists()) {
				this.traverser.setProjectPackage("ocpi");
				this.traverser.traverseForSpecs(cdkDir);
			}
		}
	}
	
	// Get the project dependencies from Project.mk
	private String[] getDependencies() {
		String[] dependencies = {};
		IFile projectMk = currProject.getFile("Project.mk");
		if (projectMk.exists()) {
			String line = null;
			FileReader fileReader = null;
			BufferedReader bufferedReader = null;
			try {
				fileReader = new FileReader(projectMk.getLocation().toString());
				bufferedReader = new BufferedReader(fileReader);
				while ((line = bufferedReader.readLine()) != null) {
					if (line.startsWith("ProjectDependencies=")) {
						dependencies = line.replace("ProjectDependencies=", "").split(" ");
						break;
					}
				}
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
		return dependencies;
	}
	
	// Get the project package from Project.mk
	private String getCurrentProjectPackage() {
		String projPackage = "";
		IFile projectMk = currProject.getFile("Project.mk");
		if (projectMk.exists()) {
			String line = null;
			FileReader fileReader = null;
			BufferedReader bufferedReader = null;
			try {
				fileReader = new FileReader(projectMk.getLocation().toString());
				bufferedReader = new BufferedReader(fileReader);
				while ((line = bufferedReader.readLine()) != null) {
					if (line.startsWith("ProjectPackage=")) {
						projPackage = line.replace("ProjectPackage=", "");
						break;
					}
				}
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
		return projPackage;
	}
	
	// Get the project package from Project.mk
	private String getProjectPackage(File file) {
		String packageName = "";
		if (file.isDirectory()) {
			String[] children = file.list();
			if (children != null) {
				for (String s : children) {
					if (s.equals("Project.mk")) {
						File makefile = new File(file, s);
						String line = null;
						FileReader fileReader = null;
						BufferedReader bufferedReader = null;
						try {
							fileReader = new FileReader(makefile);
							bufferedReader = new BufferedReader(fileReader);
							while((line = bufferedReader.readLine()) != null) {
								if (line.startsWith("ProjectPackage=")) {
									packageName = line.replace("ProjectPackage=", "");
									break;
								}
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
			}
		}
		return packageName;
	}
	***/
}
