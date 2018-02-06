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

import org.eclipse.sapphire.ElementList;
import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.ListProperty;
import org.eclipse.sapphire.Type;
import org.eclipse.sapphire.Unique;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.Required;
import org.eclipse.sapphire.modeling.annotations.Service;
import org.eclipse.sapphire.modeling.xml.annotations.CustomXmlListBinding;
import org.eclipse.sapphire.modeling.xml.annotations.CustomXmlValueBinding;

import av.proj.ide.custom.bindings.list.OASInstancePropertyXmlListBinding;
import av.proj.ide.custom.bindings.value.GenericDualCaseXmlValueBinding;
import av.proj.ide.custom.bindings.value.InstanceComponentValueXmlBinding;
import av.proj.ide.services.SpecPossibleValueService;

public interface Instance extends ConnectAttribute {
	ElementType TYPE = new ElementType( Instance.class );
	
	// *** Name ***
	@CustomXmlValueBinding( impl=GenericDualCaseXmlValueBinding.class )
	@Label(standard = "Name")
	@Required
	@Unique

	ValueProperty PROP_NAME = new ValueProperty(TYPE, "Name");

	Value<String> getName();
	void setName(String value);
	
	// *** Component ***
	@CustomXmlValueBinding( impl=InstanceComponentValueXmlBinding.class )
	@Label( standard="Component" )
	@Required
	@Service( impl=SpecPossibleValueService.class )
	
	ValueProperty PROP_COMPONENT = new ValueProperty(TYPE, "Component");

	Value<String> getComponent();
	void setComponent(String value);
	
	// *** Selection ***
	@CustomXmlValueBinding( impl=GenericDualCaseXmlValueBinding.class )
	@Label(standard = "Selection")
	
	ValueProperty PROP_SELECTION = new ValueProperty(TYPE, "Selection");

	Value<String> getSelection();
	void setSelection(String value);
	
	// *** From ***
	@CustomXmlValueBinding( impl=GenericDualCaseXmlValueBinding.class )
	@Label(standard = "From")
	
	ValueProperty PROP_FROM = new ValueProperty(TYPE, "From");

	Value<String> getFrom();
	void setFrom(String value);
	
	// *** To ***
	@CustomXmlValueBinding( impl=GenericDualCaseXmlValueBinding.class )
	@Label(standard = "To")
	
	ValueProperty PROP_TO = new ValueProperty(TYPE, "To");

	Value<String> getTo();
	void setTo(String value);
	
	// *** External ***
	@CustomXmlValueBinding( impl=GenericDualCaseXmlValueBinding.class )
	@Label(standard = "External")
	
	ValueProperty PROP_EXTERNAL = new ValueProperty(TYPE, "External");

	Value<String> getExternal();
	void setExternal(String value);
	
	// *** Slave ***
	@CustomXmlValueBinding( impl=GenericDualCaseXmlValueBinding.class )
	@Label(standard = "Slave")
		
	ValueProperty PROP_SLAVE = new ValueProperty(TYPE, "Slave");

	Value<String> getSlave();
	void setSlave(String value);
	
	// *** Properties ***
	@Type( base = InstanceProperty.class )
	@CustomXmlListBinding(impl = OASInstancePropertyXmlListBinding.class )
	@Label( standard = "Properties" )
		
	ListProperty PROP_PROPERTIES = new ListProperty( TYPE, "Properties" );
		
	ElementList<InstanceProperty> getProperties();
}
