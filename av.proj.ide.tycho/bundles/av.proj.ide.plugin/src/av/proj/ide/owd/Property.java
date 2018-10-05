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

import org.eclipse.sapphire.ElementList;
import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.ListProperty;
import org.eclipse.sapphire.Type;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.Required;
import org.eclipse.sapphire.modeling.xml.annotations.CustomXmlListBinding;
import org.eclipse.sapphire.modeling.xml.annotations.CustomXmlValueBinding;

import av.proj.ide.common.PropertyEnum;
import av.proj.ide.common.PropertyType;
import av.proj.ide.custom.bindings.list.EnumsListBinding;
import av.proj.ide.custom.bindings.list.OWDMemberXmlListBinding;
import av.proj.ide.custom.bindings.value.BooleanAttributeRemoveIfFalseValueBinding;
import av.proj.ide.custom.bindings.value.GenericDualCaseXmlValueBinding;
import av.proj.ide.custom.bindings.value.GenericMultiwordXmlValueBinding;
import av.proj.ide.custom.bindings.value.SpecialDualCaseXmlValueBinding;

public interface Property extends av.proj.ide.common.Property {
	ElementType TYPE = new ElementType( Property.class );
	
	// *** Name ***
	@CustomXmlValueBinding(impl = GenericDualCaseXmlValueBinding.class)
	@Label(standard = "Name")
	@Required

	ValueProperty PROP_NAME = new ValueProperty(TYPE, "Name");

	Value<String> getName();
	void setName(String value);

	// *** Type ***
	@Type(base = PropertyType.class)
	@CustomXmlValueBinding(impl = SpecialDualCaseXmlValueBinding.class)
	@Label(standard = "Type")

	ValueProperty PROP_TYPE = new ValueProperty(TYPE, "Type");

	Value<PropertyType> getType();
	void setType(String value);
	void setType(PropertyType value);

	// *** Enums ***
	@Label(standard = "Enums")
	@Type(base = PropertyEnum.class)
	@CustomXmlListBinding(impl = EnumsListBinding.class)

	ListProperty PROP_ENUMS = new ListProperty(TYPE, "Enums");

	ElementList<PropertyEnum> getEnums();

	// *** ArrayLength ***
	@CustomXmlValueBinding(impl = GenericMultiwordXmlValueBinding.class)
	@Label(standard = "ArrayLength")

	ValueProperty PROP_ARRAY_LENGTH = new ValueProperty(TYPE, "ArrayLength");

	Value<String> getArrayLength();
	void setArrayLength(String value);

	// *** StringLength ***
	@CustomXmlValueBinding(impl = GenericMultiwordXmlValueBinding.class)
	@Label(standard = "StringLength")
	@Required

	ValueProperty PROP_STRING_LENGTH = new ValueProperty(TYPE, "StringLength");

	Value<String> getStringLength();
	void setStringLength(String value);

	// *** SequenceLength ***
	@CustomXmlValueBinding(impl = GenericMultiwordXmlValueBinding.class)
	@Label(standard = "SequenceLength")

	ValueProperty PROP_SEQUENCE_LENGTH = new ValueProperty(TYPE, "SequenceLength");

	Value<String> getSequenceLength();
	void setSequenceLength(String value);

	// *** ArrayDimensions ***
	@CustomXmlValueBinding(impl = GenericMultiwordXmlValueBinding.class)
	@Label(standard = "ArrayDimensions")

	ValueProperty PROP_ARRAY_DIMENSIONS = new ValueProperty(TYPE, "ArrayDimensions");

	Value<String> getArrayDimensions();
	void setArrayDimensions(String value);

	// *** Default ***
	@CustomXmlValueBinding(impl = GenericDualCaseXmlValueBinding.class)
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
	
	// *** Padding ***
	@Type(base = Boolean.class)
	@CustomXmlValueBinding(impl = BooleanAttributeRemoveIfFalseValueBinding.class )
	@Label(standard = "Padding")

	ValueProperty PROP_PADDING = new ValueProperty(TYPE, "Padding");

	Value<Boolean> getPadding();
	void setPadding(String value);
	void setPadding(Boolean value);
		
	// *** ReadSync ***
	@CustomXmlValueBinding( impl=GenericMultiwordXmlValueBinding.class )
	@Type( base = Boolean.class )
	@Label( standard = "ReadSync" )
	
	ValueProperty PROP_READ_SYNC = new ValueProperty(TYPE, "ReadSync");

	Value<Boolean> getReadSync();
	void setReadSync( String value );
	void setReadSync( Boolean value ); 
	
	// *** WriteSync ***
	@Type( base = Boolean.class )
	@CustomXmlValueBinding( impl=GenericMultiwordXmlValueBinding.class )
	@Label( standard = "WriteSync" )
		
	ValueProperty PROP_WRITE_SYNC = new ValueProperty(TYPE, "WriteSync");

	Value<Boolean> getWriteSync();
	void setWriteSync( String value );
	void setWriteSync( Boolean value ); 
	
	// *** ReadError ***
	@Type( base = Boolean.class )
	@CustomXmlValueBinding( impl=GenericMultiwordXmlValueBinding.class )
	@Label( standard = "ReadError" )
	
	ValueProperty PROP_READ_ERROR = new ValueProperty(TYPE, "ReadError");

	Value<Boolean> getReadError();
	void setReadError( String value );
	void setReadError( Boolean value ); 
	
	// *** WriteError ***
	@Type( base = Boolean.class )
	@CustomXmlValueBinding( impl=GenericMultiwordXmlValueBinding.class )
	@Label( standard = "WriteError" )
		
	ValueProperty PROP_WRITE_ERROR = new ValueProperty(TYPE, "WriteError");

	Value<Boolean> getWriteError();
	void setWriteError( String value );
	void setWriteError( Boolean value );
	
	// *** Members ***
	@Type ( base = Member.class )
	//@XmlListBinding( mappings = { @XmlListBinding.Mapping( element = "Member", type = Member.class ), @XmlListBinding.Mapping( element = "member", type = MemberLower.class) } )
	@CustomXmlListBinding(impl = OWDMemberXmlListBinding.class)
	@Label( standard = "Members" )
		
	ListProperty PROP_MEMBERS = new ListProperty( TYPE, "Members" );
	    
	ElementList<Member> getMembers();
}
