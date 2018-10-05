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

package av.proj.ide.testeditor;

import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.ElementList;
import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.ListProperty;
import org.eclipse.sapphire.Type;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.Enablement;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.Required;
import org.eclipse.sapphire.modeling.xml.annotations.CustomXmlListBinding;
import org.eclipse.sapphire.modeling.xml.annotations.CustomXmlRootBinding;
import org.eclipse.sapphire.modeling.xml.annotations.CustomXmlValueBinding;

import av.proj.ide.custom.bindings.list.SimpleDualCaseXmlListBinding;
import av.proj.ide.custom.bindings.root.GenericMultiCaseRootBinding;
import av.proj.ide.custom.bindings.value.BooleanAttributeRemoveIfFalseValueBinding;
import av.proj.ide.custom.bindings.value.GenericDualCaseXmlValueBinding;
import av.proj.ide.custom.bindings.value.GenericMultiwordXmlValueBinding;

@CustomXmlRootBinding( value = GenericMultiCaseRootBinding.class )
public interface Property extends Element
{
	ElementType TYPE = new ElementType(Property.class);
	
	// Name
	@CustomXmlValueBinding(impl = GenericDualCaseXmlValueBinding.class)
	@Label(standard = "Name")
    @Required

	ValueProperty PROP_NAME = new ValueProperty(TYPE, "Name");
	
	Value<String> getName();
	void setName(String value);

	// Source of property value or values
	// Only one source can be set
	
	// Value Attribute
	@CustomXmlValueBinding(impl = GenericDualCaseXmlValueBinding.class)
	@Label(standard = "Value")
    @Enablement( expr = "${ Values == null && ValueFile == null && ValuesFile == null && Generate == null}" )

	ValueProperty PROP_VALUE = new ValueProperty(TYPE, "Value");
	
	Value<String> getValue();
	void setValue(String value);
	
	// Values Attribute
	@CustomXmlValueBinding(impl = GenericDualCaseXmlValueBinding.class)
	@Label(standard = "Values")
    @Enablement( expr = "${ Value == null && ValueFile == null && ValuesFile == null && Generate == null}" )

	ValueProperty PROP_VALUES = new ValueProperty(TYPE, "Values");
	
	Value<String> getValues();
	void setValues(String value);
	
	// ValueFile Attribute
	@CustomXmlValueBinding(impl = GenericMultiwordXmlValueBinding.class)
	@Label(standard = "Value File")
    @Enablement( expr = "${ Value == null && Values == null && ValuesFile == null && Generate == null}" )

	ValueProperty PROP_VALUEFILE = new ValueProperty(TYPE, "ValueFile");
	
	Value<String> getValueFile();
	void setValueFile(String value);
	
	// ValuesFile Attribute
	@CustomXmlValueBinding(impl = GenericMultiwordXmlValueBinding.class)
	@Label(standard = "Values File")
    @Enablement( expr = "${ Value == null && Values == null && ValueFile == null && Generate == null}" )

	ValueProperty PROP_VALUES_FILE = new ValueProperty(TYPE, "ValuesFile");
	
	Value<String> getValuesFile();
	void setValuesFile(String value);

	// Generate Attribute
	@CustomXmlValueBinding(impl = GenericDualCaseXmlValueBinding.class)
	@Label(standard = "Generate")
	   @Enablement( expr = "${ Value == null && Values == null && ValueFile == null && ValuesFile == null}" )

	ValueProperty PROP_GENERATE = new ValueProperty(TYPE, "Generate");
	
	Value<String> getGenerate();
	void setGenerate(String value);
	
	// ValuesFile Attribute
	@Type(base = Boolean.class)
	@CustomXmlValueBinding(impl = BooleanAttributeRemoveIfFalseValueBinding.class )
	@Label(standard = " Set the test attribute (indicates the is a property of the test not a component).")

	ValueProperty PROP_TEST = new ValueProperty(TYPE, "Test");
	
	Value<String> getTest();
	void setTest(String value);

	// Set
	
	// *** Property Set element ***
	public interface Set extends Element
	{
	 	ElementType TYPE = new ElementType(Set.class);
	 	
	 	// *** name attribute***
		@CustomXmlValueBinding(impl = GenericDualCaseXmlValueBinding.class)
	 	@Label(standard = "Delay")
	 	ValueProperty PROP_DELAY = new ValueProperty(TYPE, "Delay");
	 	
	 	Value<String> getDelay();
	 	void setDelay(String value);


	 	// *** name attribute***
		@CustomXmlValueBinding(impl = GenericDualCaseXmlValueBinding.class)
	 	@Label(standard = "Value")
	 	ValueProperty PROP_VALUE = new ValueProperty(TYPE, "Value");
	 	
	 	Value<String> getValue();
	 	void setValue(String value);
	}
	
	@Type(base = Set.class)
	@CustomXmlListBinding(impl = SimpleDualCaseXmlListBinding.class)
	@Label( standard = "Set Elements" )
			
	ListProperty PROP_SETS = new ListProperty( TYPE, "Sets" );
			
	ElementList<Set> getInputs();

	
	// Only
	// TODO - add a service for only and exclude - has the platforms.
	@CustomXmlValueBinding(impl = GenericDualCaseXmlValueBinding.class)
	@Label(standard = "Only")
    @Enablement( expr = "${ Exclude == null && Add == null}" )
	
	ValueProperty PROP_ONLY = new ValueProperty(TYPE, "Only");
	
	Value<String> getOnly();
	void setOnly(String value);
	
	// Exclude
	@CustomXmlValueBinding(impl = GenericDualCaseXmlValueBinding.class)
	@Label(standard = "Exclude")
    @Enablement( expr = "${ Only == null && Add == null}" )
	
	ValueProperty PROP_EXCLUDE = new ValueProperty(TYPE, "Exclude");
	
	Value<String> getExclude();
	void setExclude(String value);
	
	// Add 
	@CustomXmlValueBinding(impl = GenericDualCaseXmlValueBinding.class)
	@Label(standard = "Add")
    @Enablement( expr = "${ Only == null && Exclude == null}" )

	ValueProperty PROP_ADD = new ValueProperty(TYPE, "Add");
	
	Value<String> getAdd();
	void setAdd(String value);

	
	
}
