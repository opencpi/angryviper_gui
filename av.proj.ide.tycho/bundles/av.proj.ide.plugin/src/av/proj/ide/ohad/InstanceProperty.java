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

package av.proj.ide.ohad;

import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.Required;
import org.eclipse.sapphire.modeling.xml.annotations.CustomXmlValueBinding;

import av.proj.ide.custom.bindings.value.CaseInsenitiveAttributeValueBinding;
import av.proj.ide.custom.bindings.value.GenericDualCaseXmlValueBinding;

public interface InstanceProperty extends Element {
	ElementType TYPE = new ElementType( InstanceProperty.class );
	
	// *** Name ***
	@CustomXmlValueBinding( impl=GenericDualCaseXmlValueBinding.class )
	@Label(standard = "Name")
	@Required
	
	ValueProperty PROP_NAME = new ValueProperty(TYPE, "Name");

	Value<String> getName();
	void setName(String value);
	
	// *** Value ***
	@CustomXmlValueBinding( impl=GenericDualCaseXmlValueBinding.class )
	@Label(standard = "Value")
	@Required( "${ ValueFile == null }" )
	
	ValueProperty PROP_VALUE = new ValueProperty(TYPE, "Value");

	Value<String> getValue();
	void setValue(String value);
	
	// *** ValueFile ***
	@CustomXmlValueBinding( impl=CaseInsenitiveAttributeValueBinding.class )
	@Label(standard = "ValueFile")
	
	ValueProperty PROP_VALUE_FILE = new ValueProperty(TYPE, "ValueFile");

	Value<String> getValueFile();
	void setValueFile(String value);
	
	// *** DumpFile ***
	@CustomXmlValueBinding( impl=CaseInsenitiveAttributeValueBinding.class )
	@Label(standard = "DumpFile")
	
	ValueProperty PROP_DUMP_FILE = new ValueProperty(TYPE, "DumpFile");

	Value<String> getDumpFile();
	void setDumpFile(String value);
}
