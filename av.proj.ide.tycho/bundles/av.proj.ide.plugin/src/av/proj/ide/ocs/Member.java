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

import org.eclipse.sapphire.ElementList;
import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.ListProperty;
import org.eclipse.sapphire.Type;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.DefaultValue;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.Required;
import org.eclipse.sapphire.modeling.xml.annotations.CustomXmlListBinding;
import org.eclipse.sapphire.modeling.xml.annotations.CustomXmlValueBinding;

import av.proj.ide.common.MemberPropertyType;
import av.proj.ide.common.PropertyEnum;
import av.proj.ide.custom.bindings.list.EnumsListBinding;
import av.proj.ide.custom.bindings.list.OCSMemberXmlListBinding;
import av.proj.ide.custom.bindings.value.GenericDualCaseXmlValueBinding;
import av.proj.ide.custom.bindings.value.GenericMultiwordXmlValueBinding;
import av.proj.ide.custom.bindings.value.SpecialDualCaseXmlValueBinding;

public interface Member extends av.proj.ide.common.Property {
	ElementType TYPE = new ElementType(Member.class);

	// *** Name ***
	@CustomXmlValueBinding(impl = GenericDualCaseXmlValueBinding.class)
	@Label(standard = "Name")
	@Required

	ValueProperty PROP_NAME = new ValueProperty(TYPE, "Name");

	Value<String> getName();
	void setName(String value);

	// *** Type ***
	@Type(base = MemberPropertyType.class)
	@CustomXmlValueBinding(impl = SpecialDualCaseXmlValueBinding.class)
	@Label(standard = "Type")
	@DefaultValue(text = "uLong")

	ValueProperty PROP_TYPE = new ValueProperty(TYPE, "Type");

	Value<MemberPropertyType> getType();
	void setType(String value);
	void setType(MemberPropertyType value);

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
	
	// *** Members ***
	@Type ( base = Member.class )
	//@XmlListBinding( mappings = { @XmlListBinding.Mapping( element="Member", type=Member.class ), @XmlListBinding.Mapping( element = "member", type = MemberLower.class ) } )
	@CustomXmlListBinding(impl = OCSMemberXmlListBinding.class )
	@Label( standard = "Members" )
				
	ListProperty PROP_MEMBERS = new ListProperty( TYPE, "Members" );
		    
	ElementList<Member> getMembers();
}
