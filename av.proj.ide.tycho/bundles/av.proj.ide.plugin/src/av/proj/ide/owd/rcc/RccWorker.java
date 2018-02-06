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

package av.proj.ide.owd.rcc;

import org.eclipse.sapphire.ElementList;
import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.ListProperty;
import org.eclipse.sapphire.Type;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.Service;
import org.eclipse.sapphire.modeling.xml.annotations.CustomXmlListBinding;
import org.eclipse.sapphire.modeling.xml.annotations.CustomXmlRootBinding;

import av.proj.ide.custom.bindings.list.ControlOperationsListBinding;
import av.proj.ide.custom.bindings.list.OWDPortXmlListBinding;
import av.proj.ide.custom.bindings.root.RccWorkerRootXmlBinding;
import av.proj.ide.owd.ControlOperation;
import av.proj.ide.owd.Worker;
import av.proj.ide.services.ControlOperationsPossibleValueService;

@CustomXmlRootBinding( value = RccWorkerRootXmlBinding.class )

public interface RccWorker extends Worker {
	ElementType TYPE = new ElementType( RccWorker.class );

	// *** ControlOperations ***
	@Label(standard = "ControlOperations")
	@Type(base = ControlOperation.class)
	@CustomXmlListBinding(impl = ControlOperationsListBinding.class)
	@Service(impl = ControlOperationsPossibleValueService.class)
	
	ListProperty PROP_CONTROL_OPERATIONS = new ListProperty(TYPE, "ControlOperations");

	ElementList<ControlOperation> getControlOperations();
		
	// *** Ports ***
	@Type( base = Port.class )
	//@XmlListBinding( mappings = { @XmlListBinding.Mapping( element = "Port", type = Port.class ), @XmlListBinding.Mapping( element = "port", type = PortLower.class ) } )
	@CustomXmlListBinding(impl=OWDPortXmlListBinding.class)
	@Label( standard = "Ports" )
					
	ListProperty PROP_PORTS = new ListProperty( TYPE, "Ports" );
					
	ElementList<Port> getPorts();
}
