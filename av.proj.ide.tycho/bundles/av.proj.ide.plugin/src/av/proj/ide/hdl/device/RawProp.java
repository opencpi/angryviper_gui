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
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.xml.annotations.CustomXmlValueBinding;

import av.proj.ide.custom.bindings.value.BooleanAttributeRemoveIfFalseValueBinding;
import av.proj.ide.custom.bindings.value.CaseInsenitiveAttributeValueBinding;


public interface RawProp extends Element {
	ElementType TYPE = new ElementType(RawProp.class);
	
	// *** Name *** 
	@CustomXmlValueBinding( impl=CaseInsenitiveAttributeValueBinding.class )
	@Label(standard = "Name")
		
	ValueProperty PROP_NAME = new ValueProperty(TYPE, "Name");

	Value<String> getName();
	void setName(String value);
	
	// *** Master ***
	@Type(base = Boolean.class)
	@CustomXmlValueBinding(impl = BooleanAttributeRemoveIfFalseValueBinding.class )
	@Label(standard = "Master")

	ValueProperty PROP_MASTER = new ValueProperty(TYPE, "Master");

	Value<Boolean> getMaster();
	void setMaster(String value);
	void setMaster(Boolean value);
	
	// *** Optional ***
	@Type( base = Boolean.class )
	@CustomXmlValueBinding( impl=BooleanAttributeRemoveIfFalseValueBinding.class )
	@Label( standard = "Optional" )
		
	ValueProperty PROP_OPTIONAL = new ValueProperty(TYPE, "Optional");
		
	Value<Boolean> getOptional();
	void setOptional( String value );
	void setOptional( Boolean value );
	
	// *** Count ***
	@CustomXmlValueBinding(impl = CaseInsenitiveAttributeValueBinding.class)
	@Label(standard = "Count")
//    @NumericRange( min = "0", max = "32" )
//	@DefaultValue(text = "0")
//    @Validation
//    (
//        rule = "${ Count >= 0 &&  Count <= 32 }",
//        message = "Valid range is 0 to 32",
//        severity = Status.Severity.ERROR
//    )
	
	ValueProperty PROP_COUNT = new ValueProperty(TYPE, "Count");

	Value<String> getCount();
	void setCount(String value);
	
	
}
