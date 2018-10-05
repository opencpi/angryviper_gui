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
import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.Type;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.Enablement;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.Service;
import org.eclipse.sapphire.modeling.xml.annotations.CustomXmlRootBinding;
import org.eclipse.sapphire.modeling.xml.annotations.CustomXmlValueBinding;

import av.proj.ide.custom.bindings.root.GenericMultiCaseRootBinding;
import av.proj.ide.custom.bindings.value.BooleanAttributeRemoveIfFalseValueBinding;
import av.proj.ide.custom.bindings.value.GenericDualCaseXmlValueBinding;
import av.proj.ide.custom.bindings.value.GenericMultiwordXmlValueBinding;

@CustomXmlRootBinding( value = GenericMultiCaseRootBinding.class )
public interface Input extends Element {
	ElementType TYPE = new ElementType(Input.class);
	
	// Name
	@CustomXmlValueBinding(impl = GenericDualCaseXmlValueBinding.class)
	@Label(standard = "Name")
    @Enablement( expr = "${ Port == null }" )

	ValueProperty PROP_NAME = new ValueProperty(TYPE, "Name");
	
	Value<String> getName();
	void setName(String value);

	// Or Port 
	@CustomXmlValueBinding(impl = GenericDualCaseXmlValueBinding.class)
	@Label(standard = "Port")
    @Enablement( expr = "${ Name == null }" )

	ValueProperty PROP_PORT = new ValueProperty(TYPE, "Port");
	
	Value<String> getPort();
	void setPort(String value);

	
	// File 
	@CustomXmlValueBinding(impl = GenericDualCaseXmlValueBinding.class)
	@Label(standard = "File")
    @Enablement( expr = "${ Script == null }" )

	ValueProperty PROP_FILE = new ValueProperty(TYPE, "File");
	
	Value<String> getFile();
	void setFile(String value);

	// Or Script 
	@CustomXmlValueBinding(impl = GenericDualCaseXmlValueBinding.class)
	@Label(standard = "Script")
    @Enablement( expr = "${ File  == null }" )

	ValueProperty PROP_SCRIPT = new ValueProperty(TYPE, "Script");
	
	Value<String> getScript();
	void setScript(String value);

	// Stressor Mode
	@CustomXmlValueBinding(impl = GenericMultiwordXmlValueBinding.class)
	@Label(standard = "Stressor Mode")
	@Service(impl=SuppressorModeValuesService.class)
	
	ValueProperty PROP_STRESSOR_MODE = new ValueProperty(TYPE, "StressorMode");
	
	Value<String> getStressorMode();
	void setStressorMode(String value);
	
	// Message Size
	@Type(base = Integer.class)
	@CustomXmlValueBinding(impl = GenericMultiwordXmlValueBinding.class)
	@Label(standard = "Message Size")
	
	ValueProperty PROP_MESSAGE_SIZE = new ValueProperty(TYPE, "MessageSize");
	
	Value<Integer> getMessageSize();
	void setMessageSize(String value);
	void setMessageSize(Integer value);
	
	// Messages In File
	@Type(base = Boolean.class)
	@CustomXmlValueBinding(impl = BooleanAttributeRemoveIfFalseValueBinding.class )
	@Label(standard = "Message In File")
	
	ValueProperty PROP_MESSAGES_IN_FILE = new ValueProperty(TYPE, "MessagesInFile");
	
	Value<String> getMessagesInFile();
	void setMessagesInFile(String value);
	
	// Suppress EOF (optional)
	@Type(base = Boolean.class)
	@CustomXmlValueBinding(impl = BooleanAttributeRemoveIfFalseValueBinding.class )
	@Label(standard = "Suppress EOF")
	
	ValueProperty PROP_SUPPRESS_EOF = new ValueProperty(TYPE, "SuppressEof");
	
	Value<String> getSuppressEof();
	void setSuppressEof(String value);
	
}
