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

package av.proj.ide.owd.hdl;

import org.eclipse.sapphire.ElementList;
import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.ListProperty;
import org.eclipse.sapphire.Type;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.Service;
import org.eclipse.sapphire.modeling.xml.annotations.CustomXmlListBinding;
import org.eclipse.sapphire.modeling.xml.annotations.CustomXmlRootBinding;
import org.eclipse.sapphire.modeling.xml.annotations.CustomXmlValueBinding;

import av.proj.ide.custom.bindings.list.ControlOperationsListBinding;
import av.proj.ide.custom.bindings.list.OWDStreamInterfaceXmlListBinding;
import av.proj.ide.custom.bindings.root.HdlWorkerRootXmlBinding;
import av.proj.ide.custom.bindings.value.CaseInsenitiveAttributeValueBinding;
import av.proj.ide.owd.ControlOperation;
import av.proj.ide.owd.Worker;
import av.proj.ide.services.HdlControlOperationsPossibleValueService;

@CustomXmlRootBinding( value = HdlWorkerRootXmlBinding.class )

public interface HdlWorker extends Worker {
	ElementType TYPE = new ElementType( HdlWorker.class );
	
	// *** ControlOperations ***
	@Label(standard = "ControlOperations")
	@Type(base = ControlOperation.class)
	@CustomXmlListBinding(impl = ControlOperationsListBinding.class)
	@Service(impl = HdlControlOperationsPossibleValueService.class)
	
	ListProperty PROP_CONTROL_OPERATIONS = new ListProperty(TYPE, "ControlOperations");

	ElementList<ControlOperation> getControlOperations();
	
	// *** DataWidth *** 
	@CustomXmlValueBinding( impl=CaseInsenitiveAttributeValueBinding.class )
	@Label(standard = "DataWidth") 
		
	ValueProperty PROP_DATA_WIDTH = new ValueProperty(TYPE, "DataWidth");

	Value<String> getDataWidth();
	void setDataWidth(String value);
	
	// *** StreamInterfaces ***
	@Type( base = StreamInterface.class )
	@CustomXmlListBinding(impl=OWDStreamInterfaceXmlListBinding.class)
	@Label( standard = "StreamInterfaces" )
				
	ListProperty PROP_STREAM_INTERFACES = new ListProperty( TYPE, "StreamInterfaces" );
				
	ElementList<StreamInterface> getStreamInterfaces();
}
