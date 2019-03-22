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

package av.proj.ide.hplat;

import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.ElementList;
import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.ListProperty;
import org.eclipse.sapphire.Type;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.DefaultValue;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.MustExist;
import org.eclipse.sapphire.modeling.annotations.Required;
import org.eclipse.sapphire.modeling.annotations.Service;
import org.eclipse.sapphire.modeling.xml.annotations.CustomXmlListBinding;
import org.eclipse.sapphire.modeling.xml.annotations.CustomXmlValueBinding;

import av.proj.ide.custom.bindings.list.SimpleDualCaseXmlListBinding;
import av.proj.ide.custom.bindings.value.CaseInsenitiveAttributeValueBinding;
import av.proj.ide.services.NameValidationService;

public interface Device extends Element
{
	ElementType TYPE = new ElementType(Device.class);
	
	// *** worker attribute***
	@CustomXmlValueBinding(impl = CaseInsenitiveAttributeValueBinding.class)
	@Label(standard = "Worker")
	@Required
	@Service(impl=NameValidationService.class)
	ValueProperty PROP_WORKER = new ValueProperty(TYPE, "Worker");
	
	// *** card attribute***
	@CustomXmlValueBinding(impl = CaseInsenitiveAttributeValueBinding.class)
	@Label(standard = "Card")
	//@Service(impl=NameValidationService.class)
	ValueProperty PROP_CARD = new ValueProperty(TYPE, "Card");
	
	// *** Device property element ***
	public interface Property extends Element
	{
	 	ElementType TYPE = new ElementType(Property.class);
	 	
	 	// *** name attribute***
		@CustomXmlValueBinding(impl = CaseInsenitiveAttributeValueBinding.class)
	 	@Label(standard = "name")
	 	@Required
	 	@Service(impl=NameValidationService.class)
	 	ValueProperty PROP_NAME = new ValueProperty(TYPE, "Name");
	 	
	 	Value<String> getName();
	 	void setName(String value);


	 	// *** name attribute***
		@CustomXmlValueBinding(impl = CaseInsenitiveAttributeValueBinding.class)
	 	@Label(standard = "value")
	 	@Required
	 	ValueProperty PROP_VALUE = new ValueProperty(TYPE, "Value");
	 	
	 	Value<String> getValue();
	 	void setValue(String value);
	}
	@Type( base = Property.class )
	@CustomXmlListBinding(impl = SimpleDualCaseXmlListBinding.class)
	@Label( standard = "property" )
	ListProperty PROP_PROPERTIES = new ListProperty( TYPE, "Properties" );
	ElementList<Property> getProperties();

	// Device signal element (optional)
	public interface Signal extends Element
	{
		ElementType TYPE = new ElementType(Signal.class);
		
		// *** name attribute***
		@CustomXmlValueBinding(impl = CaseInsenitiveAttributeValueBinding.class)
		@Label(standard = "name")
		@Required
		ValueProperty PROP_NAME = new ValueProperty(TYPE, "Name");
		
		Value<String> getName();
		void setName(String value);
		
		// *** name attribute***
		@CustomXmlValueBinding(impl = CaseInsenitiveAttributeValueBinding.class)
		@Label(standard = "platform")
		@DefaultValue(text="")
		@MustExist
		ValueProperty PROP_PLATFORM = new ValueProperty(TYPE, "Platform");
		
		Value<String> getPlatform();
		void setPlatform(String value);
	}
	// *** Device property element ***
	@Type( base = Signal.class )
	@CustomXmlListBinding(impl = SimpleDualCaseXmlListBinding.class)
	@Label( standard = "signal" )
	ListProperty  PROP_SIGNALS = new ListProperty(TYPE, "Signals");
	
	ElementList<Signal> getSignals();

}
