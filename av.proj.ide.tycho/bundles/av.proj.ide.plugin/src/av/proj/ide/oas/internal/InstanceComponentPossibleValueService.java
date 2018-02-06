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

package av.proj.ide.oas.internal;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IPathVariableManager;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.sapphire.PossibleValuesService;
import org.eclipse.sapphire.modeling.Status;

import av.proj.ide.services.ProjectProperties;

public final class InstanceComponentPossibleValueService extends PossibleValuesService {

	@Override
	protected void initPossibleValuesService() {
		this.invalidValueSeverity = Status.Severity.OK;
	}

	@Override
	protected void compute(Set<String> values) {
		
		String extLibName = "ExternalLibraries";

		IWorkspaceRoot currProject = ResourcesPlugin.getWorkspace().getRoot();

		// get "shared" (external) libraries
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IPathVariableManager pathManager = workspace.getPathVariableManager();
		String nameRoot = "LIB";
		String name = null;

		// create link within each project for each external lib
		for (IProject project : currProject.getProjects()) {

//			String libraryPathStr = System.getenv("OCPI_LIBRARY_PATH");
			ProjectProperties projectProperties = new ProjectProperties(project);
			ArrayList<String> libraries = new ArrayList<String>(Arrays.asList(projectProperties.getExternalDependencies()));

			IFolder linkBase = project.getFolder(extLibName);
			
			URI uri = null;
			int libNameSuffix = 0;
			for(String libPath : libraries) {
				uri = new File(libPath).toURI();
				name = nameRoot + String.valueOf(libNameSuffix);
				libNameSuffix++;
				if(pathManager.validateName(name).isOK() && pathManager.validateValue(uri).isOK()) {
					try {
						pathManager.setURIValue(name, uri);
						URI location = new File(libPath).toURI();
						if (workspace.validateLinkLocationURI(linkBase, uri).isOK()) {
							try {
								linkBase.createLink(location, IResource.NONE, null);
							} catch (CoreException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					} catch (CoreException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} else {
					//invalid name
				}
			}
		}

		if (currProject != null) {
			for (IProject pr : currProject.getProjects()) {
				try {
					for (IResource r : pr.members()) {
						if (r.getName().equals("components")) {
							IFolder compsFolder = pr.getFolder(r.getName());
							if (compsFolder != null && compsFolder.exists()) {
								for (IResource fo : compsFolder.members()) {
									if (fo instanceof IFolder) {
										for (IResource f : ((IFolder)fo).members()) {
											if (f.getName().equals("specs")) {
												IFolder specsFolder = ((IFolder)fo).getFolder(f.getName());
												if (specsFolder != null && specsFolder.exists()) {
													for (IResource fi : specsFolder.members()) {
														if (fi.getName().contains("_spec.xml")) {
															values.add(fi.getName().replace("_spec.xml", ""));
														}
													}
												}
											}
										}
									}
								}
							}
						}
					}
				} catch (CoreException e) {
					e.printStackTrace();
				}
			}
		}
		
		// Also pull in specs from linked libraries
		try {
			for (IProject project : currProject.getProjects()) {

				IFolder linkBase = project.getFolder(extLibName);
				
				for (IResource child : linkBase.members()) {
					if(child.getName().equals("specs")) {
						IFolder specsFolder = ((IFolder)linkBase).getFolder(child.getName());
						if (specsFolder != null && specsFolder.exists()) {
							for (IResource fi : specsFolder.members()) {
								if (fi.getName().contains("_spec.xml")) {
									if(!values.contains(fi.getName().replace("_spec.xml", ""))) {
										values.add(fi.getName().replace("_spec.xml", ""));
									}
								}
							}
						}
					}
				}
			}
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
