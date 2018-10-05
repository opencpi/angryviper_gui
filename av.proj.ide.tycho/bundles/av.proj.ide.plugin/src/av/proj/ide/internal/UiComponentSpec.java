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

public class UiComponentSpec {
	String componentName;
	String oasReference = null;
	String owdReference;
	String fileName;
	String displayName = null;
	String packageId;
	String projectPackageId;
	
	public String getProjectPackageId() {
		return projectPackageId;
	}
	public String getComponentName() {
		return componentName;
	}
	public String getOasReference() {
		return oasReference;
	}
	public String getOwdReference() {
		return owdReference;
	}
	public String getFileName() {
		return fileName;
	}
	public static boolean isComponentFile(String filename) {
		if(filename.endsWith("-spec.xml") || filename.endsWith("_spec.xml")) {
			return true;
		}
		return false;
	}
	public String getDisplayName() {
		if(displayName == null) {
			if(oasReference == null) return null;
			
			if(packageId.isEmpty()) {
				return componentName;
			}
			StringBuilder sb = new StringBuilder(componentName);
			sb.append(" (");
			sb.append(packageId);
			sb.append(")");
			displayName = sb.toString();
			//System.out.println(displayName);
		}
		return displayName;
	}
}
