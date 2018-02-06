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

import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.sapphire.PossibleValuesService;
import org.eclipse.sapphire.modeling.Status;

import av.proj.ide.oas.Application;
import av.proj.ide.oas.ConnectionPort;
import av.proj.ide.oas.Instance;
import av.proj.ide.parsers.ocs.ComponentSpec;
import av.proj.ide.parsers.ocs.OCSXMLParser;
import av.proj.ide.parsers.ocs.Port;

public final class ConnectionPortPossibleValueService extends PossibleValuesService {
	
	@Override
    protected void initPossibleValuesService()
    {
        this.invalidValueSeverity = Status.Severity.OK;
    }
	
	@Override
	protected void compute(Set<String> values) {
		final Application app = context(Application.class);
		final ConnectionPort p = context (ConnectionPort.class);
		
		IWorkspaceRoot currProject = ResourcesPlugin.getWorkspace().getRoot();
		if (currProject != null) {
			for (IProject pr : currProject.getProjects()) {
				if(! pr.isOpen())
					continue;
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
													String instName = p.getInstance().content();
													String compName = "";
													for (Instance i : app.getInstances()) {
														if (instName.equals(i.getName().content()) ) {
															compName = i.getComponent().content();
															String[] split = compName.split("\\.");
															compName = split[split.length-1];
															break;
														}
													}
													IFile specFile = specsFolder.getFile(compName+"-spec.xml");
													if (specFile != null && specFile.exists()) {
														OCSXMLParser parser = new OCSXMLParser();
														parser.parse(specFile.getContents());
														ComponentSpec spec = parser.getComponentSpec();
														if (spec != null) {
															List<Port> ports = spec.getPorts();
															for (Port po : ports) {
																values.add(po.getName());
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
					}
				} catch (CoreException e) {
					e.printStackTrace();
				}
			}
		}		
	}

}
