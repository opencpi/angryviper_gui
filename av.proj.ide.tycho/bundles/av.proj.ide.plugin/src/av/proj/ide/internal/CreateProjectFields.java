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

import java.util.HashSet;

public class CreateProjectFields extends CreateAssetFields {
	String prefix, packageName;
	HashSet<String> dependencies;
	
	public CreateProjectFields(String project, String projectPath, String projectPkg, String projectPrefix) {

		super(project, projectPath, project);
		packageName = projectPkg.isEmpty() ? null : projectPkg;
		prefix = projectPrefix.isEmpty() ? null : projectPrefix;
		dependencies = new HashSet<String>(4);
	}
	
	public void addDependency(String dependency){
		dependencies.add(dependency);
	}

	public void setProjectPath(String fullPath) {
		this.fullProjectPath = fullPath;
	}

}
