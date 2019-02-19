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
import org.eclipse.sapphire.modeling.annotations.Enablement;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.xml.annotations.CustomXmlValueBinding;

import av.proj.ide.custom.bindings.value.BooleanAttributeRemoveIfFalseValueBinding;

public interface OcsProperty extends Member  {
	ElementType TYPE = new ElementType(OcsProperty.class);

	// *** Parameter ***
	@Type(base = Boolean.class)
	@CustomXmlValueBinding(impl = BooleanAttributeRemoveIfFalseValueBinding.class )
	@Label(standard = "Parameter")
    @Enablement( expr = "${  Writable == null && Initial == null && Volatile == null && Padding == null }" )
	
	ValueProperty PROP_PARAMETER = new ValueProperty(TYPE, "Parameter");
	
	Value<Boolean> getParameter();
	void setParameter(String value);
	void setParameter(Boolean value);
	
	// *** Parameter ***
	// Padding should not be set in the OCS.  This is put here to display it and 
	// indicate it is deprecated.
	@Type(base = Boolean.class)
	@CustomXmlValueBinding(impl = BooleanAttributeRemoveIfFalseValueBinding.class )
	@Label(standard = "Padding")
    @Enablement( expr = "${  false }" )
	
	ValueProperty PROP_PADDING = new ValueProperty(TYPE, "Padding");
	
	Value<Boolean> getPadding();
	
	
	// *** Writable ***
	@Type(base = Boolean.class)
	@CustomXmlValueBinding(impl = BooleanAttributeRemoveIfFalseValueBinding.class )
    @Enablement( expr = "${  Parameter == null && Initial == null && Padding == null }" )
	@Label(standard = "Writable")
	
	ValueProperty PROP_WRITABLE = new ValueProperty(TYPE, "Writable");
	
	Value<Boolean> getWritable();
	void setWritable(String value);
	void setWritable(Boolean value);
	
	// *** Initial ***
	@Type(base = Boolean.class)
	@CustomXmlValueBinding(impl = BooleanAttributeRemoveIfFalseValueBinding.class )
    @Enablement( expr = "${  Parameter == null && Writable == null && Padding == null }" )
	@Label(standard = "Initial")
	
	ValueProperty PROP_INITIAL = new ValueProperty(TYPE, "Initial");
	
	Value<Boolean> getInitial();
	void setInitial(String value);
	void setInitial(Boolean value);
	
	
	// *** Volatile ***
	@Type(base = Boolean.class)
	@CustomXmlValueBinding(impl = BooleanAttributeRemoveIfFalseValueBinding.class )
    @Enablement( expr = "${ Parameter == null && Padding == null }" )
	@Label(standard = "Volatile")
	
	ValueProperty PROP_VOLATILE = new ValueProperty(TYPE, "Volatile");
	
	Value<Boolean> getVolatile();
	void setVolatile(String value);
	void setVolatile(Boolean value);
	
	
}
