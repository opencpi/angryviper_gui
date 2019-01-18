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
import org.eclipse.sapphire.modeling.annotations.Enablement;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.Required;
import org.eclipse.sapphire.modeling.xml.annotations.CustomXmlListBinding;
import org.eclipse.sapphire.modeling.xml.annotations.CustomXmlValueBinding;

import av.proj.ide.custom.bindings.list.EnumsListBinding;
import av.proj.ide.custom.bindings.value.CaseInsenitiveAttributeValueBinding;
import av.proj.ide.custom.bindings.value.SpecialDualCaseXmlValueBinding;

public interface PropertyAttributes extends Element {
	ElementType TYPE = new ElementType(PropertyAttributes.class);
	// *** Name ***
	@CustomXmlValueBinding(impl = CaseInsenitiveAttributeValueBinding.class)
	@Label(standard = "Name")
	@Required

	ValueProperty PROP_NAME = new ValueProperty(TYPE, "Name");

	Value<String> getName();
	void setName(String value);

	// *** Type ***
	// types cover primitive data types and more complex types like
	// Strings, Enumerations, and structures.
	@Type(base = PropertyType.class)
	@CustomXmlValueBinding(impl = SpecialDualCaseXmlValueBinding.class)
	@Label(standard = "Type")
	@DefaultValue(text = "uLong")

	ValueProperty PROP_TYPE = new ValueProperty(TYPE, "Type");

	Value<PropertyType> getType();
	void setType(String value);
	void setType(PropertyType value);

	// *** Enums ***
	// The user supplies the enumeration values here.  They become a 
	// comma separated list as the attribute value.
	@Label(standard = "Enums")
	@Type(base = PropertyEnum.class)
	@CustomXmlListBinding(impl = EnumsListBinding.class)
	@Required( "${ Type == 'enum' }" )

	ListProperty PROP_ENUMS = new ListProperty(TYPE, "Enums");

	ElementList<PropertyEnum> getEnums();

	// *** StringLength ***
	// This needs to appear in the OPS Argument interface and OCS Property.
	// It is not required in an Argument but is in the OCS Property.  

	// *** SequenceLength ***
	@CustomXmlValueBinding(impl = CaseInsenitiveAttributeValueBinding.class)
	@Label(standard = "SequenceLength")

	ValueProperty PROP_SEQUENCE_LENGTH = new ValueProperty(TYPE, "SequenceLength");

	Value<String> getSequenceLength();
	void setSequenceLength(String value);

	// *** ArrayLength ***
	// ArrayLength and ArrayDimensions (a multi-dimensional array) are mutually
	// exclusive.
	@CustomXmlValueBinding(impl = CaseInsenitiveAttributeValueBinding.class)
    @Enablement( expr = "${ false }" )
	@Label(standard = "ArrayLength")

	ValueProperty PROP_ARRAY_LENGTH = new ValueProperty(TYPE, "ArrayLength");

	Value<String> getArrayLength();
	void setArrayLength(String value);

	// *** ArrayDimensions ***
	@CustomXmlValueBinding(impl = CaseInsenitiveAttributeValueBinding.class)
	@Label(standard = "ArrayDimensions")
	@Enablement( expr = "${ ArrayLength == null}" )
	
	ValueProperty PROP_ARRAY_DIMENSIONS = new ValueProperty(TYPE, "ArrayDimensions");

	Value<String> getArrayDimensions();
	void setArrayDimensions(String value);
}
