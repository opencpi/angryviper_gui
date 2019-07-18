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

import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.xml.annotations.CustomXmlValueBinding;

import av.proj.ide.custom.bindings.value.CaseInsenitiveAttributeValueBinding;


public interface Signal extends BaseSignal {
	ElementType TYPE = new ElementType(Signal.class);
	
	/***
	 * Signal direction/name attributes
	 * This section has to deal with the direction="name" attributes use in prior models.
	 */
	// *** input signal attribute***
	@CustomXmlValueBinding( impl = CaseInsenitiveAttributeValueBinding.class ) 

	ValueProperty PROP_INPUT = new ValueProperty(TYPE, "Input");
	
	Value<String> getInput();
	void setInput(String value);
	
	// *** output signal attribute***
	@CustomXmlValueBinding( impl = CaseInsenitiveAttributeValueBinding.class ) 
	@Label(standard = "Signal Name")
    
	ValueProperty PROP_OUTPUT = new ValueProperty(TYPE, "Output");
	
	Value<String> getOutput();
	void setOutput(String value);
	
	// *** bidirectional signal attribute***
	@CustomXmlValueBinding( impl = CaseInsenitiveAttributeValueBinding.class ) 
	@Label(standard = "Bidirectional Signal Name")
 
	ValueProperty PROP_BIDIRECTIONAL = new ValueProperty(TYPE, "Bidirectional");
	
	Value<String> getBidirectional();
	void setBidirectional(String value);

	// *** in/out signal attribute***
	@CustomXmlValueBinding( impl = CaseInsenitiveAttributeValueBinding.class ) 
	@Label(standard = "In/Out Signal Name")

	ValueProperty PROP_INOUT = new ValueProperty(TYPE, "Inout");
	
	Value<String> getInout();
	void setInout(String value);

}
