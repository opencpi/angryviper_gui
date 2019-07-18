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

package av.proj.ide.hdl.signal;

import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.Type;
import org.eclipse.sapphire.Validation;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.modeling.annotations.ClearOnDisable;
import org.eclipse.sapphire.modeling.annotations.Enablement;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.Required;
import org.eclipse.sapphire.modeling.xml.annotations.CustomXmlValueBinding;

import av.proj.ide.custom.bindings.value.BooleanAttributeRemoveIfFalseValueBinding;
import av.proj.ide.custom.bindings.value.CaseInsenitiveAttributeValueBinding;

/***
 * 
 */
public interface BaseSignal extends Element {
	ElementType TYPE = new ElementType(BaseSignal.class);

	// *** Name ***
	@CustomXmlValueBinding( impl = CaseInsenitiveAttributeValueBinding.class ) 
	@Label( standard = "Name" )
	@Required
	
	ValueProperty PROP_NAME = new ValueProperty( TYPE, "Name" );
	
	Value<String> getName();
	void setName( String value );
	
	// *** Direction ***
    @Type( base = SignalDirection.class )
	@CustomXmlValueBinding( impl = CaseInsenitiveAttributeValueBinding.class ) 
    @Label( standard = "Direction" )
	@Validation(   rule = "${  Direction != null }" ,
    message = "A signal direction must be set.",
    severity = Status.Severity.WARNING)
   
    ValueProperty PROP_DIRECTION = new ValueProperty( TYPE, "Direction" );
    
    Value<SignalDirection> getDirection();
    void setDirection( String value );
    void setDirection( SignalDirection value );
	
	// ***  signal width ***
	@CustomXmlValueBinding( impl = CaseInsenitiveAttributeValueBinding.class ) 
	@Label(standard = "Width")
	ValueProperty PROP_WIDTH = new ValueProperty(TYPE, "Width");
	
	Value<String> getWidth();
	void setWidth(String value);
	
	// *** In/Out signal names format override attributes ***
	// Inbound Signal format
    @Enablement( expr = "${ Direction == 'inout' || inout != null }" )
	@CustomXmlValueBinding( impl = CaseInsenitiveAttributeValueBinding.class ) 
	@Label(standard = "Inbound Name Format Override")
    @ClearOnDisable
    
	ValueProperty PROP_IN = new ValueProperty(TYPE, "In");
	
	Value<String> getIn();
	void setIn(String value);

	// *** Pin ***
	@Type( base = Boolean.class )
	@CustomXmlValueBinding( impl=BooleanAttributeRemoveIfFalseValueBinding.class )
	@Label( standard = "Pin" )
		
	ValueProperty PROP_PIN = new ValueProperty(TYPE, "Pin");
		
	Value<Boolean> getPin();
	void setPin( String value );
	void setPin( Boolean value );
	
	
	// Outbound Signal format
    @Enablement( expr = "${ Direction == 'inout' || inout != null }" )
	@CustomXmlValueBinding( impl = CaseInsenitiveAttributeValueBinding.class ) 
	@Label(standard = "Onbound Name Format Override")
    @ClearOnDisable
    
	ValueProperty PROP_OUT = new ValueProperty(TYPE, "Out");
	
	Value<String> getOut();
	void setOut(String value);

	// Output Enabled format
    @Enablement( expr = "${ Direction == 'inout'  || inout != null }" )
	@CustomXmlValueBinding( impl = CaseInsenitiveAttributeValueBinding.class ) 
	@Label(standard = "Output Enabled Override")
    @ClearOnDisable
    
	ValueProperty PROP_OE = new ValueProperty(TYPE, "Oe");
	
	Value<String> getOe();
	void setOe(String value);

	// *** differential signal attribute***
	@Type(base = Boolean.class)
    @Enablement( expr = "${ Direction != 'inout' || inout != null }" )
	@CustomXmlValueBinding( impl = CaseInsenitiveAttributeValueBinding.class ) 
	@Label(standard = "Differential")
    @ClearOnDisable
    
	ValueProperty PROP_DIFFERENTIAL = new ValueProperty(TYPE, "Differential");
	
	Boolean getDifferential();
	void setDifferential(Boolean value);
	void setDifferential(String value);
	
	// *** Differential signal name format override attributes ***
    @Enablement( expr = "${ Differential == true }" )
	@CustomXmlValueBinding( impl = CaseInsenitiveAttributeValueBinding.class ) 
	@Label(standard = "Positive Suffix Override")
    @ClearOnDisable
    
    ValueProperty PROP_POS = new ValueProperty(TYPE, "Pos");
	
	Value<String> getPos();
	void setPos(String value);
	
    @Enablement( expr = "${ Differential == true }" )
	@CustomXmlValueBinding( impl = CaseInsenitiveAttributeValueBinding.class ) 
	@Label(standard = "Negative Suffix Override")
    @ClearOnDisable
    
	ValueProperty PROP_NEG = new ValueProperty(TYPE, "Neg");
	
	Value<String> getNeg();
	void setNeg(String value);
	
}
