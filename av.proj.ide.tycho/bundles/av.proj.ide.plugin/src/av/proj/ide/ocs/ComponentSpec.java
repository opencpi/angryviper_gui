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

package av.proj.ide.ocs;

import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.ElementList;
import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.ListProperty;
import org.eclipse.sapphire.Transient;
import org.eclipse.sapphire.TransientProperty;
import org.eclipse.sapphire.Type;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.xml.annotations.CustomXmlListBinding;
import org.eclipse.sapphire.modeling.xml.annotations.CustomXmlRootBinding;
import org.eclipse.sapphire.modeling.xml.annotations.CustomXmlValueBinding;

import av.proj.ide.custom.bindings.list.OCSPortXmlListBinding;
import av.proj.ide.custom.bindings.list.OCSPropertyXmlListBinding;
import av.proj.ide.custom.bindings.root.ComponentSpecRootXmlBinding;
import av.proj.ide.custom.bindings.value.GenericMultiwordXmlValueBinding;
import av.proj.ide.custom.bindings.value.GenericDualCaseXmlValueBinding;

@CustomXmlRootBinding(value = ComponentSpecRootXmlBinding.class)

public interface ComponentSpec extends Element {
	ElementType TYPE = new ElementType( ComponentSpec.class );

	// *** Name ***
	@CustomXmlValueBinding( impl = GenericDualCaseXmlValueBinding.class ) 
	@Label( standard = "Name" )
	
	ValueProperty PROP_NAME = new ValueProperty( TYPE, "Name" );
	
	Value<String> getName();
	void setName( String value );
	
	// *** NoControl ***
	@Type(base = Boolean.class)
	@CustomXmlValueBinding(impl = GenericMultiwordXmlValueBinding.class )
	@Label(standard = "NoControl")
	
	ValueProperty PROP_NO_CONTROL = new ValueProperty(TYPE, "NoControl");
	
	Value<Boolean> getNoControl();
	void setNoControl(String value);
	void setNoControl(Boolean value);
		
	// *** Property Elements ***
	//@Type( base = Property.class, possible = { Property.class, PropertyLower.class } )
	@Type (base = Property.class )
	//@XmlListBinding( mappings = { @XmlListBinding.Mapping( element = "Property", type = Property.class ), @XmlListBinding.Mapping( element = "property", type = PropertyLower.class ) } )
	@CustomXmlListBinding( impl=OCSPropertyXmlListBinding.class )
	@Label( standard = "ComponentSpecProperties" )
	
	ListProperty PROP_COMPONENT_SPEC_PROPERTIES = new ListProperty( TYPE, "ComponentSpecProperties" );
	
	ElementList<Property> getComponentSpecProperties();
	
	/*
	// *** Properties Element ***
	@Type( base = Property.class, possible = { Property.class, PropertyLower.class } )
	@XmlListBinding( path = "Properties", mappings = { @XmlListBinding.Mapping( element = "Property", type = Property.class ), @XmlListBinding.Mapping( element = "property", type = PropertyLower.class ) } )
	@Label( standard = "Properties" )
	
	ListProperty PROP_PROPERTIES = new ListProperty( TYPE, "Properties" );
	
	ElementList<Property> getProperties();
	*/
	
	// *** Ports ***
	//@Type( base = Port.class, possible= { Port.class, PortLower.class } )
	@Type( base = Port.class )
	//@XmlListBinding( mappings = { @XmlListBinding.Mapping( element = "Port", type = Port.class ), @XmlListBinding.Mapping( element = "port", type = PortLower.class) } )
	@CustomXmlListBinding( impl = OCSPortXmlListBinding.class )
	@Label( standard = "Ports" )
	
	ListProperty PROP_PORTS = new ListProperty( TYPE, "Ports" );
	
	ElementList<Port> getPorts();
	
	// *** ComponentSpec File Location ***
	@Type( base = String.class )
	TransientProperty PROP_LOCATION = new TransientProperty(TYPE, "Location");
	Transient<String> getLocation();
	void setLocation(String value);
}
