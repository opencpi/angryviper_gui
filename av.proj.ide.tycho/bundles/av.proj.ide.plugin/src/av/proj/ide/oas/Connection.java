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

package av.proj.ide.oas;

import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.ElementList;
import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.ListProperty;
import org.eclipse.sapphire.Type;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.xml.annotations.CustomXmlListBinding;
import org.eclipse.sapphire.modeling.xml.annotations.CustomXmlValueBinding;

import av.proj.ide.custom.bindings.list.OASPortXmlListBinding;
import av.proj.ide.custom.bindings.value.GenericDualCaseXmlValueBinding;

public interface Connection extends Element {
	ElementType TYPE = new ElementType( Connection.class );
	
	// *** Name ***
	@CustomXmlValueBinding( impl=GenericDualCaseXmlValueBinding.class )
	@Label(standard = "Connection Name")
	
	ValueProperty PROP_NAME = new ValueProperty(TYPE, "Name");

	Value<String> getName();
	void setName(String value);
	
	// *** Transport ***
	@CustomXmlValueBinding( impl=GenericDualCaseXmlValueBinding.class )
	@Label( standard = "Transport" )
	
	ValueProperty PROP_TRANSPORT = new ValueProperty( TYPE, "Transport" );
	
	Value<String> getTransport();
	void setTransport( String value );
	
	// *** External ***
	@CustomXmlValueBinding( impl=GenericDualCaseXmlValueBinding.class )
	@Label( standard = "External" )
		
	ValueProperty PROP_EXTERNAL = new ValueProperty( TYPE, "External" );
		
	Value<String> getExternal();
	void setExternal( String value );
	
	// *** Ports ***
	//@Type( base = ConnectionPort.class, possible = { ConnectionPort.class, ConnectionPortLower.class } )
	@Type( base=ConnectionPort.class )
	//@XmlListBinding( mappings = { @XmlListBinding.Mapping( element = "Port", type = ConnectionPort.class ), @XmlListBinding.Mapping( element = "port", type = ConnectionPortLower.class ) } )
	@CustomXmlListBinding( impl=OASPortXmlListBinding.class )
	@Label( standard = "Ports" )
				
	ListProperty PROP_PORTS = new ListProperty( TYPE, "Ports" );
				
	ElementList<ConnectionPort> getPorts();
}
