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

package av.proj.ide.hdl.device;

import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.Type;
import org.eclipse.sapphire.Validation;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.xml.annotations.CustomXmlValueBinding;

import av.proj.ide.custom.bindings.value.BooleanAttributeRemoveIfFalseValueBinding;
import av.proj.ide.custom.bindings.value.CaseInsenitiveAttributeValueBinding;


public interface DevSignal extends Element {
	ElementType TYPE = new ElementType(DevSignal.class);
	
	@CustomXmlValueBinding( impl = CaseInsenitiveAttributeValueBinding.class )
	@Label(standard = "Name")
	@Validation(rule     = "${Name == null || Name.Size > 2 }",
			    message  = "Must match the name of a worker in the same library",
			    severity = Status.Severity.WARNING)
	ValueProperty PROP_NAME = new ValueProperty(TYPE, "Name");

	Value<String> getName();
	void setName(String value);
	
	// *** Master ***
	@Type( base = Boolean.class )
	@CustomXmlValueBinding( impl=BooleanAttributeRemoveIfFalseValueBinding.class )
	@Label( standard = "Master" )
		
	ValueProperty PROP_MASTER = new ValueProperty(TYPE, "Master");
		
	Value<Boolean> getMaster();
	void setMaster( String value );
	void setMaster( Boolean value );
	
	// *** Master ***
	@Type( base = Boolean.class )
	@CustomXmlValueBinding( impl=BooleanAttributeRemoveIfFalseValueBinding.class )
	@Label( standard = "Optional" )
		
	ValueProperty PROP_OPTIONAL = new ValueProperty(TYPE, "Optional");
		
	Value<Boolean> getOptional();
	void setOptional( String value );
	void setOptional( Boolean value );
	
	// *** Count ***
	@CustomXmlValueBinding( impl = CaseInsenitiveAttributeValueBinding.class )
	@Label(standard = "Count")
	
	ValueProperty PROP_COUNT = new ValueProperty(TYPE, "Count");

	Value<String> getCount();
	void setCount(String value);
	
	
	// *** Signals Bundle File ***
	@CustomXmlValueBinding( impl = CaseInsenitiveAttributeValueBinding.class )
	@Label(standard = "Signals File Bundle")
	
	ValueProperty PROP_SIGNALS = new ValueProperty(TYPE, "Signals");

	Value<String> getSignals();
	void setSignals(String value);
	

}
