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

import org.eclipse.sapphire.ElementList;
import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.ListProperty;
import org.eclipse.sapphire.Type;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.xml.annotations.CustomXmlListBinding;
import org.eclipse.sapphire.modeling.xml.annotations.CustomXmlRootBinding;
import org.eclipse.sapphire.modeling.xml.annotations.CustomXmlValueBinding;

import av.proj.ide.custom.bindings.list.SimpleDualCaseXmlListBinding;
import av.proj.ide.custom.bindings.root.GenericMultiCaseRootBinding;
import av.proj.ide.custom.bindings.value.CaseInsenitiveAttributeValueBinding;

@CustomXmlRootBinding( value = GenericMultiCaseRootBinding.class )
public interface Case extends CoreTestAttributes
{
	ElementType TYPE = new ElementType(Case.class);

	// Name
	@CustomXmlValueBinding(impl = CaseInsenitiveAttributeValueBinding.class)
	@Label(standard = "Test Case Name")

	ValueProperty PROP_NAME = new ValueProperty(TYPE, "Name");
	
	Value<String> getName();
	void setName(String value);
	
	
	//  Elements
	
  	// *** Test Properties ***
	@Type( base = Property.class )
	@CustomXmlListBinding(impl = SimpleDualCaseXmlListBinding.class)
	@Label( standard = "Test Case Properties" )

	ListProperty PROP_PROPERTIES = new ListProperty( TYPE, "Properties" );
			
	ElementList<Property> getProperties();


	// *** Test Inputs ***
	@Type( base = Input.class )
	@CustomXmlListBinding(impl = SimpleDualCaseXmlListBinding.class)
	@Label( standard = "Test Case Inputs" )
			
	ListProperty PROP_INPUTS = new ListProperty( TYPE, "Inputs" );
			
	ElementList<Input> getInputs();


	// *** Test Outputs ***
	@Type( base = Output.class )
	@CustomXmlListBinding(impl = SimpleDualCaseXmlListBinding.class)
	@Label( standard = "Test Case Outputs" )
			
	ListProperty PROP_OUTPUTS = new ListProperty( TYPE, "Outputs" );
			
	ElementList<Input> getOutputs();

	
}