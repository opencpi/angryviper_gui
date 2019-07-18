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

import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.Type;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.Enablement;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.LongString;
import org.eclipse.sapphire.modeling.xml.annotations.CustomXmlValueBinding;

import av.proj.ide.custom.bindings.value.BooleanAttributeRemoveIfFalseValueBinding;
import av.proj.ide.custom.bindings.value.CaseInsenitiveAttributeValueBinding;

public interface Property extends  OwdPropertyAttributes {
	ElementType TYPE = new ElementType( Property.class );
	// TODO:  If this gets nailed down and SpecProperty does not somehow diverge,
	// SepcProperty can be in the parent tree of Property.
	// *** Description ***
	@CustomXmlValueBinding( impl=CaseInsenitiveAttributeValueBinding.class )
	@Label(standard = "Description")
	@LongString
	
	ValueProperty PROP_DESCRIPTION = new ValueProperty(TYPE, "Description");

	Value<String> getDescription();
	void setDescription(String value);

	/*%%%%%%%%%%%%%%%  Special Access property attributes %%%%%%%%%%%%%%%%%%%*/
	// *** Padding ***
	@Type(base = Boolean.class)
	@CustomXmlValueBinding(impl = BooleanAttributeRemoveIfFalseValueBinding.class )
	@Label(standard = "Padding")
    @Enablement( expr = "${ Parameter == null && Volatile == null && Writable == null && Initial == null  && Readable == null }" )

	ValueProperty PROP_PADDING = new ValueProperty(TYPE, "Padding");

	Value<Boolean> getPadding();
	void setPadding(String value);
	void setPadding(Boolean value);

	// *** Parameter ***
	@Type(base = Boolean.class)
	@CustomXmlValueBinding(impl = BooleanAttributeRemoveIfFalseValueBinding.class )
	@Label(standard = "Parameter")
    @Enablement( expr = "${ Padding == null && Volatile == null && Writable == null && Initial == null }" )
	
	ValueProperty PROP_PARAMETER = new ValueProperty(TYPE, "Parameter");
	
	Value<Boolean> getParameter();
	void setParameter(String value);
	void setParameter(Boolean value);
	
	/*%%%%%%%%%%%%%%%  Write attributes %%%%%%%%%%%%%%%%%%%*/
	// *** Writable ***
	@Type(base = Boolean.class)
	@CustomXmlValueBinding(impl = BooleanAttributeRemoveIfFalseValueBinding.class )
    @Enablement( expr = "${ Padding == null &&  Parameter == null && Initial == null }" )
	@Label(standard = "Writable")
	
	ValueProperty PROP_WRITABLE = new ValueProperty(TYPE, "Writable");
	
	Value<Boolean> getWritable();
	void setWritable(String value);
	void setWritable(Boolean value);
	
	// *** Initial ***
	@Type(base = Boolean.class)
	@CustomXmlValueBinding(impl = BooleanAttributeRemoveIfFalseValueBinding.class )
    @Enablement( expr = "${ Padding == null &&  Parameter == null && Writable == null}" )
	@Label(standard = "Initial")
	
	ValueProperty PROP_INITIAL = new ValueProperty(TYPE, "Initial");
	
	Value<Boolean> getInitial();
	void setInitial(String value);
	void setInitial(Boolean value);
	
	
	/*%%%%%%%%%%%%%%%  Read attributes %%%%%%%%%%%%%%%%%%%*/
	@Type(base = Boolean.class)
	@CustomXmlValueBinding(impl = BooleanAttributeRemoveIfFalseValueBinding.class )
	@Label(standard = "Readable")
	   @Enablement( expr = "${ Padding == null && Volatile == null}" )

	ValueProperty PROP_READABLE = new ValueProperty(TYPE, "Readable");

	Value<Boolean> getReadable();
	void setReadable(String value);
	void setReadable(Boolean value);
		
	// *** Volatile ***
	@Type(base = Boolean.class)
	@CustomXmlValueBinding(impl = BooleanAttributeRemoveIfFalseValueBinding.class )
    @Enablement( expr = "${ Padding == null &&  Parameter == null && Readable == null}" )
	@Label(standard = "Volatile")
	
	ValueProperty PROP_VOLATILE = new ValueProperty(TYPE, "Volatile");
	
	Value<Boolean> getVolatile();
	void setVolatile(String value);
	void setVolatile(Boolean value);
	
}
