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

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;

//import java.io.File;

public class ProjectProperties {
	
	private BufferedReader bufferedData = null;
	private String name = null;
	private String prefix = null;
	private String packag = null;  // "package" is java reserved word
	private String[] projectDependencies = null;
	private IProject currentProject = null;
	
	public ProjectProperties(IProject currentProject) {
		this.currentProject = currentProject;
	}
	
//	private IProject getCurrentProject(){
//		IProject project = null;
//		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
//	    if (window != null)
//	    {
//	        IStructuredSelection selection = (IStructuredSelection) window.getSelectionService().getSelection();
//	        Object firstElement = selection.getFirstElement();
//	        if (firstElement instanceof IAdaptable)
//	        {
//	            project = (IProject)((IAdaptable)firstElement).getAdapter(IProject.class);
//	        }
//	    }
//	    return project;
//    }
	
	// read in the av project file, the first time only
	private boolean parseProjectFile() {
		if(bufferedData == null) {
			IFile projectMkFile = currentProject.getFile("Project.mk");
			if(projectMkFile.exists()) {
				try {
					String location = projectMkFile.getLocationURI().getPath();
					FileReader input = new FileReader(location);
					bufferedData = new BufferedReader(input);
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					// should not ever hit this as we check for null above
					e.printStackTrace();
					return false;
				}
			}
			
			if (bufferedData != null) {
			//parse in to properties
			String myLine = null;
			try {
				while ( (myLine = bufferedData.readLine()) != null)
				{    
				    String[] property = myLine.split("=");
				    
				    //grab each property, ensuring it has a value first
				    if(property.length > 1) {
					    switch(property[0]) {
					    case "ProjectName":
					    	name = property[1];
					    	break;
					    case "ProjectPrefix":
					    	prefix = property[1];
					    	break;
					    case "ProjectPackage":
					    	packag = property[1];
					    	break;
					    case "ProjectDependencies":
					    	projectDependencies = property[1].split(" ");
					    	break;
				    }
				    }
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return false;
			}
			} else {
				return false;
			}
		}
		return true;
	}
	
	public String getName() {
		if(parseProjectFile())
			return name;
		return null;
	}

	public String getPrefix() {
		if(parseProjectFile())
			return prefix;
		return null;
	}

	public String getPackage() {
		if(parseProjectFile())
			return packag;
		return null;
	}

	public String[] getExternalDependencies() {
		if(parseProjectFile())
			return projectDependencies;
		return null;
	}

}
