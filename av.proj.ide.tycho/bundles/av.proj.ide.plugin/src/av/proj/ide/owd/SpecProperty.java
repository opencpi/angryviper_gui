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
import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.Type;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.Required;
import org.eclipse.sapphire.modeling.xml.annotations.CustomXmlValueBinding;

import av.proj.ide.custom.bindings.value.BooleanAttributeRemoveIfFalseValueBinding;
import av.proj.ide.custom.bindings.value.GenericDualCaseXmlValueBinding;
import av.proj.ide.custom.bindings.value.GenericMultiwordXmlValueBinding;

public interface SpecProperty extends Element {
	ElementType TYPE = new ElementType( SpecProperty.class );
	
	// *** Name ***
	@CustomXmlValueBinding(impl = GenericDualCaseXmlValueBinding.class)
	@Label(standard = "Name")
	@Required

	ValueProperty PROP_NAME = new ValueProperty(TYPE, "Name");

	Value<String> getName();
	void setName(String value);
	
	// *** Default ***
	@CustomXmlValueBinding(impl = BooleanAttributeRemoveIfFalseValueBinding.class)
	@Label(standard = "Default")

	ValueProperty PROP_DEFAULT = new ValueProperty(TYPE, "Default");

	Value<String> getDefault();
	void setDefault(String value);
	
	// *** Readable ***
	@Type(base = Boolean.class)
	@CustomXmlValueBinding(impl = BooleanAttributeRemoveIfFalseValueBinding.class)
	@Label(standard = "Readable")
    //@Enablement( expr = "${ Volatile  == null }" )

	ValueProperty PROP_READABLE = new ValueProperty(TYPE, "Readable");

	Value<Boolean> getReadable();
	void setReadable(String value);
	void setReadable(Boolean value);

	// *** Volatile ***
	@Type(base = Boolean.class)
	@CustomXmlValueBinding(impl = BooleanAttributeRemoveIfFalseValueBinding.class)
	@Label(standard = "Volatile")
    //@Enablement( expr = "${ Readable  == null }" )

	ValueProperty PROP_VOLATILE = new ValueProperty(TYPE, "Volatile");

	Value<Boolean> getVolatile();
	void setVolatile(String value);
	void setVolatile(Boolean value);

	// *** Writable ***
	@Type(base = Boolean.class)
	@CustomXmlValueBinding(impl = BooleanAttributeRemoveIfFalseValueBinding.class)
	@Label(standard = "Writable")

	ValueProperty PROP_WRITABLE = new ValueProperty(TYPE, "Writable");

	Value<Boolean> getWritable();
	void setWritable(String value);
	void setWritable(Boolean value);

	// *** Initial ***
	@Type(base = Boolean.class)
	@CustomXmlValueBinding(impl = BooleanAttributeRemoveIfFalseValueBinding.class)
	@Label(standard = "Initial")

	ValueProperty PROP_INITIAL = new ValueProperty(TYPE, "Initial");

	Value<Boolean> getInitial();
	void setInitial(String value);
	void setInitial(Boolean value);
	
	// *** ReadSync ***
	@CustomXmlValueBinding(impl = GenericMultiwordXmlValueBinding.class)
	@Type(base = Boolean.class)
	@Label(standard = "ReadSync")

	ValueProperty PROP_READ_SYNC = new ValueProperty(TYPE, "ReadSync");

	Value<Boolean> getReadSync();
	void setReadSync(String value);
	void setReadSync(Boolean value);

	// *** WriteSync ***
	@Type(base = Boolean.class)
	@CustomXmlValueBinding(impl = GenericMultiwordXmlValueBinding.class)
	@Label(standard = "WriteSync")

	ValueProperty PROP_WRITE_SYNC = new ValueProperty(TYPE, "WriteSync");

	Value<Boolean> getWriteSync();
	void setWriteSync(String value);
	void setWriteSync(Boolean value);

	// *** ReadError ***
	@Type(base = Boolean.class)
	@CustomXmlValueBinding(impl = GenericMultiwordXmlValueBinding.class)
	@Label(standard = "ReadError")

	ValueProperty PROP_READ_ERROR = new ValueProperty(TYPE, "ReadError");

	Value<Boolean> getReadError();
	void setReadError(String value);
	void setReadError(Boolean value);

	// *** WriteError ***
	@Type(base = Boolean.class)
	@CustomXmlValueBinding(impl = GenericMultiwordXmlValueBinding.class)
	@Label(standard = "WriteError")

	ValueProperty PROP_WRITE_ERROR = new ValueProperty(TYPE, "WriteError");

	Value<Boolean> getWriteError();
	void setWriteError(String value);
	void setWriteError(Boolean value);
}
