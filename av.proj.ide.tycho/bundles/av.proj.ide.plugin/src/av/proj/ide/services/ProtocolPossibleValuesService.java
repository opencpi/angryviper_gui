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
import java.util.Set;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.eclipse.sapphire.PossibleValuesService;
import org.eclipse.sapphire.Transient;

import av.proj.ide.avps.internal.AngryViperAssetService;
import av.proj.ide.avps.internal.RegisteredProjectSearchTool;
import av.proj.ide.ocs.ComponentSpec;

public class ProtocolPossibleValuesService extends PossibleValuesService {

	private File currFile;
	private IProject currProject;
	
	public ProtocolPossibleValuesService() {
	}
	
	@Override
	protected void compute(Set<String> values) {
		values.clear();
		if (initCurrent()) {
			RegisteredProjectSearchTool tool = AngryViperAssetService.getRegistedProjectTool();
			Set<String> prots =  tool.getProtocols();
			values.addAll(prots);
		}
	}
	
	private boolean initCurrent() {
		ComponentSpec spec = context(ComponentSpec.class);
		if (spec != null) {
			Transient<String> loc = spec.getLocation();
			if (loc != null) {
				if (loc.content() != null) {
					this.currFile = new File(loc.content());
					IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
					IContainer cont = root.getContainerForLocation(new Path(this.currFile.getPath()));
					if (cont != null) {
						this.currProject = cont.getProject();
						if (this.currProject != null && this.currProject.exists()) {
							return true;
						}
					}
				}
			}
		}
		return false;
	}
}
