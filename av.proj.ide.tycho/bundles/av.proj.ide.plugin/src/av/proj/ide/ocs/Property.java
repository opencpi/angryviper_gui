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

import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.Type;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.xml.annotations.CustomXmlValueBinding;

import av.proj.ide.custom.bindings.value.BooleanAttributeRemoveIfFalseValueBinding;
import av.proj.ide.custom.bindings.value.CaseInsenitiveAttributeValueBinding;

public interface Property extends av.proj.ide.ops.Argument {
	ElementType TYPE = new ElementType(Property.class);

	// *** Default ***
	@CustomXmlValueBinding( impl = CaseInsenitiveAttributeValueBinding.class ) 
	@Label(standard = "Default")
	
	ValueProperty PROP_DEFAULT = new ValueProperty(TYPE, "Default");

	Value<String> getDefault();
	void setDefault(String value);
	
	// *** Parameter ***
	@Type(base = Boolean.class)
	@CustomXmlValueBinding(impl = BooleanAttributeRemoveIfFalseValueBinding.class )
	@Label(standard = "Parameter")
	
	ValueProperty PROP_PARAMETER = new ValueProperty(TYPE, "Parameter");
	
	Value<Boolean> getParameter();
	void setParameter(String value);
	void setParameter(Boolean value);
	
	// *** Readable ***
	@Type(base = Boolean.class)
	@CustomXmlValueBinding(impl = BooleanAttributeRemoveIfFalseValueBinding.class )
	@Label(standard = "Readable")
	
	ValueProperty PROP_READABLE = new ValueProperty(TYPE, "Readable");
	
	Value<Boolean> getReadable();
	void setReadable(String value);
	void setReadable(Boolean value);
	
	// *** Volatile ***
	@Type(base = Boolean.class)
	@CustomXmlValueBinding(impl = BooleanAttributeRemoveIfFalseValueBinding.class )
	@Label(standard = "Volatile")
	
	ValueProperty PROP_VOLATILE = new ValueProperty(TYPE, "Volatile");
	
	Value<Boolean> getVolatile();
	void setVolatile(String value);
	void setVolatile(Boolean value);
	
	// *** Writable ***
	@Type(base = Boolean.class)
	@CustomXmlValueBinding(impl = BooleanAttributeRemoveIfFalseValueBinding.class )
	@Label(standard = "Writable")
	
	ValueProperty PROP_WRITABLE = new ValueProperty(TYPE, "Writable");
	
	Value<Boolean> getWritable();
	void setWritable(String value);
	void setWritable(Boolean value);
	
	// *** Initial ***
	@Type(base = Boolean.class)
	@CustomXmlValueBinding(impl = BooleanAttributeRemoveIfFalseValueBinding.class )
	@Label(standard = "Initial")
	
	ValueProperty PROP_INITIAL = new ValueProperty(TYPE, "Initial");
	
	Value<Boolean> getInitial();
	void setInitial(String value);
	void setInitial(Boolean value);
}
