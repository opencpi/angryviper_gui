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

import org.eclipse.sapphire.Element;
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

import av.proj.ide.custom.bindings.list.EnumsListBinding;
import av.proj.ide.custom.bindings.value.CaseInsenitiveAttributeValueBinding;
import av.proj.ide.custom.bindings.value.SpecialDualCaseXmlValueBinding;

public interface Property extends Element {
	ElementType TYPE = new ElementType(Property.class);
	// *** Name ***
	@CustomXmlValueBinding(impl = CaseInsenitiveAttributeValueBinding.class)
	@Label(standard = "Name")
	@Required

	ValueProperty PROP_NAME = new ValueProperty(TYPE, "Name");

	Value<String> getName();
	void setName(String value);

	// *** Type ***
	@Type(base = PropertyType.class)
	@CustomXmlValueBinding(impl = SpecialDualCaseXmlValueBinding.class)
	@Label(standard = "Type")
	@DefaultValue(text = "uLong")

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

	// *** StringLength ***
	@CustomXmlValueBinding(impl = CaseInsenitiveAttributeValueBinding.class)
	@Label(standard = "StringLength")
	@Required( "${ Type == 'String' }" )

	ValueProperty PROP_STRING_LENGTH = new ValueProperty(TYPE, "StringLength");

	Value<String> getStringLength();
	void setStringLength(String value);

	// *** SequenceLength ***
	@CustomXmlValueBinding(impl = CaseInsenitiveAttributeValueBinding.class)
	@Label(standard = "SequenceLength")

	ValueProperty PROP_SEQUENCE_LENGTH = new ValueProperty(TYPE, "SequenceLength");

	Value<String> getSequenceLength();
	void setSequenceLength(String value);

	// *** ArrayDimensions ***
	@CustomXmlValueBinding(impl = CaseInsenitiveAttributeValueBinding.class)
	@Label(standard = "ArrayDimensions")

	ValueProperty PROP_ARRAY_DIMENSIONS = new ValueProperty(TYPE, "ArrayDimensions");

	Value<String> getArrayDimensions();
	void setArrayDimensions(String value);
}
