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

import org.json.simple.JSONObject;

public class AngryViperProjectInfo {
	// Opencpi name - it also is the name of the folder
	// in which it resides. See the top-level Project.mk
	// file for more details.  This name will be null if
	// is not an OpenCPI project.
	public String name = null;
	
	// The project can have a different name in eclipse.
	// If the project is not open in eclipse this name
	// remains null.
	public String eclipseName = null;
	
	public String packageId = null;
	public String fullPath = null;
	public String projectDirectory;
	boolean isRegistered = false;
	
	boolean isOpenInEclipse = false;
	private ProjectLocation location = null;
	
	public ProjectLocation getProjectLocation() {
		if(location == null) {
			if(name == null) {
				location = new ProjectLocation(eclipseName, fullPath);
			}
			else {
				location = new ProjectLocation(name, fullPath);
			}
			location.packageId = packageId;
			location.eclipseName = eclipseName;
		}
		return location;
	}
	
	public boolean isRegistered() {
		return isRegistered;
	}
	public boolean isOpenCpiProject() {
		return packageId != null;
	}
	
	
	public boolean isOpenInEclipse() {
		return isOpenInEclipse;
	}

	public void setOpenInEclipse(boolean b) {
		isOpenInEclipse = b;
	}
	
	// Constructors purposely given package scope, this is for internal use.
	AngryViperProjectInfo(){}
	AngryViperProjectInfo (String packageId, JSONObject prjData) {
		this.packageId = packageId;
		fullPath = (String) prjData.get("real_path");
		String[] pathSegments = fullPath.split("/");
		name = projectDirectory = pathSegments[pathSegments.length -1];
	}

}
