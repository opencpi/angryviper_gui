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

public class CreateAssetFields {
	OpenCPICategory type;
	String projectName;
	String fullProjectPath;
	String name;
	String libraryName;
	boolean xmlOnly = true;
	boolean topLevelSpec = false;
	
	public CreateAssetFields(OpenCPICategory type, String project, String projectPath, String name) {
		projectName = project;
		fullProjectPath = projectPath;
		this.name = name;
		this.type = type;
	}
	public CreateAssetFields(String project, String projectPath, String name) {
		projectName = project;
		fullProjectPath = projectPath;
		this.name = name;
	}
	
	public String getFullProjectPath() {
		return fullProjectPath;
	}
	public void setFullProjectPath(String fullProjectPath) {
		this.fullProjectPath = fullProjectPath;
	}
	public String getProjectName() {
		return projectName;
	}
	public void setType(OpenCPICategory type) {
		this.type = type;
	}
	
	public String getName() {
		return name;
	}
	public OpenCPICategory getType() {
		return type;
	}
	public void notXmlOnly () {
		xmlOnly = false;
	}
	public void addToTopSpec () {
		topLevelSpec = true;
	}
	public void setLibrary(String lib) {
		libraryName = lib;
	}
}
