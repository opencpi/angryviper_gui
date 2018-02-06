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

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.sapphire.LoggingService;
import org.eclipse.sapphire.Sapphire;
import org.eclipse.sapphire.ui.DragAndDropService;
import org.eclipse.sapphire.ui.Point;
import org.eclipse.sapphire.ui.diagram.editor.DiagramNodePart;
import org.eclipse.sapphire.ui.diagram.editor.SapphireDiagramEditorPagePart;

import av.proj.ide.oas.Application;
import av.proj.ide.oas.Instance;
import av.proj.ide.parsers.ocs.ComponentSpec;
import av.proj.ide.parsers.ocs.OCSXMLParser;

public class ApplicationInstanceDragAndDropService extends DragAndDropService {

	@Override
	public boolean droppable(DropContext context) {
		if (context.object() instanceof IFile) {
			IFile file = (IFile) context.object();
			if (!file.getName().endsWith("-spec.xml") && !file.getName().endsWith("_spec.xml")) {
				return false;				
			}
		}
		return true;
	}

	@Override
	public void drop(DropContext context) {
		IFile specFile = (IFile) context.object();
		String projectName = specFile.getProject().getName();
		IResource library = specFile.getParent().getParent();
		String packageName = determinePackageName(library);
		String prefix = "";
		if (packageName.equals("")) {
			packageName = findTopLevelPackageName(specFile.getProject());
			if (!packageName.equals("")) {
				prefix = packageName+".";
				if (!library.getName().equals("components")) {
					prefix += library.getName()+".";
				}
			}	
		} else {
			prefix = packageName+".";
		}
		InputStream in = null;
		OCSXMLParser parser = new OCSXMLParser();
		try {
			in = specFile.getContents();
			parser.parse(in);
			ComponentSpec spec = parser.getComponentSpec();
			String instanceName = "";
			if (spec.getName() != null && !spec.getName().equals("")) {
				instanceName = prefix+spec.getName();
			} else {
				if (specFile.getName().endsWith("_spec.xml")) {
					instanceName = prefix+specFile.getName().replaceAll("_spec.xml", "");
				} else if (specFile.getName().endsWith("-spec.xml")) {
					instanceName = prefix+specFile.getName().replaceAll("-spec.xml", "");
				}
			}
			if (!instanceName.equals("")) {
				final SapphireDiagramEditorPagePart diagram = context( SapphireDiagramEditorPagePart.class );
	            final Application app = context( Application.class );
	            
	            final Point initialDropPosition = context.position();
	            
	            int x = initialDropPosition.getX();
	            int y = initialDropPosition.getY();
	            
	            final Instance instance = app.getInstances().insert();
	            instance.setComponent(instanceName);
	            
	            final DiagramNodePart instanceNodePart = diagram.getDiagramNodePart(instance);
	            instanceNodePart.setNodeBounds(x, y);
			}
		} catch (CoreException e) {
			Sapphire.service( LoggingService.class ).log( e );
		} finally {
			try {
				if (in != null) {
					in.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	
	}
	
	private String determinePackageName(IResource library) {
		String packageName = "";
		if (library instanceof IFolder) {
			IFolder libFolder = (IFolder)library;
			FileReader fileReader = null;
			BufferedReader bufferedReader = null;
			try {
				for (IResource r : libFolder.members()) {
					if (r.getName().equals("Makefile")) {
						String line = null;
						fileReader = new FileReader(r.getLocation().toString());
						bufferedReader = new BufferedReader(fileReader); 
						while((line = bufferedReader.readLine()) != null) {
							if (line.startsWith("Package=")) {
								packageName = line.replace("Package=", "");
								break;
							}
						}
					}
				}
			} catch (CoreException e) {
				e.printStackTrace();
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
		return packageName;
	}
	
	private String findTopLevelPackageName(IProject project) {
		String packageName = "";
		
		if (project != null && project.exists()) {
			IFile projectMk = project.getFile("Project.mk");
			if (projectMk != null && projectMk.exists()) {
				String line = null;
				FileReader fileReader = null;
				BufferedReader bufferedReader = null;
				try {
					fileReader = new FileReader(projectMk.getLocation().toFile());
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
		
		return packageName;
	}

}
