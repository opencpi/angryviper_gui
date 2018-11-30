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

package av.proj.ide.owd;

import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.ElementList;
import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.ListProperty;
import org.eclipse.sapphire.Type;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.Required;
import org.eclipse.sapphire.modeling.xml.annotations.CustomXmlListBinding;
import org.eclipse.sapphire.modeling.xml.annotations.CustomXmlValueBinding;

import av.proj.ide.custom.bindings.list.OWDPropertyXmlListBinding;
import av.proj.ide.custom.bindings.list.OWDSpecPropertyXmlListBinding;
import av.proj.ide.custom.bindings.value.CaseInsenitiveAttributeValueBinding;

public interface Worker extends Element {
	ElementType TYPE = new ElementType( Worker.class );
	
	// *** Name ***
	@CustomXmlValueBinding( impl = CaseInsenitiveAttributeValueBinding.class )
	@Label(standard = "Name")
	
	ValueProperty PROP_NAME = new ValueProperty(TYPE, "Name");

	Value<String> getName();
	void setName(String value);
	
	// *** Spec ***
	@CustomXmlValueBinding( impl = CaseInsenitiveAttributeValueBinding.class )
	@Label(standard = "Spec")
	@Required
		
	ValueProperty PROP_SPEC = new ValueProperty(TYPE, "Spec");

	Value<String> getSpec();
	void setSpec(String value);
	
	// *** Language ***
	@CustomXmlValueBinding( impl = CaseInsenitiveAttributeValueBinding.class )
	@Label(standard = "Language")
	
	ValueProperty PROP_LANGUAGE = new ValueProperty(TYPE, "Language");

	Value<String> getLanguage();
	void setLanguage(String value);
	
	// *** Endian ***
	@Type( base = Endian.class)
	@CustomXmlValueBinding( impl = CaseInsenitiveAttributeValueBinding.class )
	@Label(standard = "Endian")
		
	ValueProperty PROP_ENDIAN = new ValueProperty( TYPE, "Endian" );
	   
	Value<Endian> getEndian();
	void setEndian( String value );
	void setEndian( Endian value ); 
	
	// *** Properties ***
	@Type( base = Property.class )
	//@XmlListBinding( mappings = { @XmlListBinding.Mapping( element = "Property", type = Property.class ), @XmlListBinding.Mapping( element = "property", type = PropertyLower.class ) } )
	@CustomXmlListBinding(impl = OWDPropertyXmlListBinding.class)
	@Label( standard = "Properties" )
		
	ListProperty PROP_PROPERTIES = new ListProperty( TYPE, "Properties" );
		
	ElementList<Property> getProperties();
	
	// *** SpecProperties ***
	@Type( base = SpecProperty.class )
	//@XmlListBinding( mappings = { @XmlListBinding.Mapping( element = "SpecProperty", type = SpecProperty.class ), @XmlListBinding.Mapping( element = "specproperty", type = SpecPropertyLower.class ) } )
	@CustomXmlListBinding(impl = OWDSpecPropertyXmlListBinding.class)
	@Label( standard = "SpecProperties" )
			
	ListProperty PROP_SPEC_PROPERTIES = new ListProperty( TYPE, "SpecProperties" );
			
	ElementList<SpecProperty> getSpecProperties();
}
