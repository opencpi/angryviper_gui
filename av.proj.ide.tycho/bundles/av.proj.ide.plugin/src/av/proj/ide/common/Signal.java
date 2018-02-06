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

package av.proj.ide.common;

import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.Type;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.ClearOnDisable;
import org.eclipse.sapphire.modeling.annotations.Enablement;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;


public interface Signal extends SignalCommon {
	ElementType TYPE = new ElementType(Signal.class);
	
	/***
	 * Signal direction/name attributes
	 */
	// *** input signal attribute***
    @Enablement( expr = "${ Direction == 'INPUT' }" )
	@XmlBinding(path = "@input")
	@Label(standard = "Input Signal Name")
    @ClearOnDisable

	ValueProperty PROP_INPUT = new ValueProperty(TYPE, "Input");
	
	Value<String> getInput();
	void setInput(String value);
	
	// *** output signal attribute***
    @Enablement( expr = "${ Direction == 'OUTPUT' }" )
	@XmlBinding(path = "@output")
	@Label(standard = "Output Signal Name")
    @ClearOnDisable
    
	ValueProperty PROP_OUTPUT = new ValueProperty(TYPE, "Output");
	
	Value<String> getOutput();
	void setOutput(String value);
	
	// *** bidirectional signal attribute***
    @Enablement( expr = "${ Direction == 'BIDIRECTIONAL' }" )
	@XmlBinding(path = "@bidirectional")
	@Label(standard = "Bidirectional Signal Name")
    @ClearOnDisable

	ValueProperty PROP_BIDIRECTIONAL = new ValueProperty(TYPE, "Bidirectional");
	
	Value<String> getBidirectional();
	void setBidirectional(String value);

	// *** in/out signal attribute***
    @Enablement( expr = "${ Direction == 'INOUT' }" )
	@XmlBinding(path = "@inout")
	@Label(standard = "In/Out Signal Name")
    @ClearOnDisable

	ValueProperty PROP_INOUT = new ValueProperty(TYPE, "Inout");
	
	Value<String> getInout();
	void setInout(String value);

	// *** In/Out signal names format override attributes ***
	// Inbound Signal format
    @Enablement( expr = "${ Direction == 'INOUT' }" )
	@XmlBinding(path = "@in")
	@Label(standard = "Inbound Name Format Override")
    @ClearOnDisable
    
	ValueProperty PROP_IN = new ValueProperty(TYPE, "In");
	
	Value<String> getIn();
	void setIn(String value);

	// Outbound Signal format
    @Enablement( expr = "${ Direction == 'INOUT' }" )
	@XmlBinding(path = "@out")
	@Label(standard = "Onbound Name Format Override")
    @ClearOnDisable
    
	ValueProperty PROP_OUT = new ValueProperty(TYPE, "Out");
	
	Value<String> getOut();
	void setOut(String value);

	// Output Enabled format
    @Enablement( expr = "${ Direction == 'INOUT' }" )
	@XmlBinding(path = "@oe")
	@Label(standard = "Output Enabled Override")
    @ClearOnDisable
    
	ValueProperty PROP_OE = new ValueProperty(TYPE, "Oe");
	
	Value<String> getOe();
	void setOe(String value);

	// Additional Signal Characteristics
	
	// *** differential signal attribute***
	@Type(base = Boolean.class)
    @Enablement( expr = "${ Direction != 'INOUT' }" )
	@XmlBinding(path = "@differential")
	@Label(standard = "Differential Signal")
    @ClearOnDisable
    
	ValueProperty PROP_DIFFERENTIAL = new ValueProperty(TYPE, "Differential");
	
	Boolean getDifferential();
	void setDifferential(Boolean value);
	void setDifferential(String value);
	
	// *** Differential signal name format override attributes ***
    @Enablement( expr = "${ Differential == true }" )
	@XmlBinding(path = "@pos")
	@Label(standard = "Positive Suffix Override")
    @ClearOnDisable
    
    ValueProperty PROP_POS = new ValueProperty(TYPE, "Pos");
	
	Value<String> getPos();
	void setPos(String value);
	
    @Enablement( expr = "${ Differential == true }" )
	@XmlBinding(path = "@neg")
	@Label(standard = "Negative Suffix Override")
    @ClearOnDisable
    
	ValueProperty PROP_NEG = new ValueProperty(TYPE, "Neg");
	
	Value<String> getNeg();
	void setNeg(String value);
}
