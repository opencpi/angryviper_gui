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
import org.eclipse.sapphire.modeling.xml.annotations.CustomXmlRootBinding;
import org.eclipse.sapphire.modeling.xml.annotations.CustomXmlValueBinding;

import av.proj.ide.custom.bindings.root.GenericMultiCaseRootBinding;
import av.proj.ide.custom.bindings.value.BooleanAttributeRemoveIfFalseValueBinding;
import av.proj.ide.custom.bindings.value.CaseInsenitiveAttributeValueBinding;

@CustomXmlRootBinding( value = GenericMultiCaseRootBinding.class )
public interface Output extends Element {
	ElementType TYPE = new ElementType(Output.class);
	
	// Name
	@CustomXmlValueBinding(impl = CaseInsenitiveAttributeValueBinding.class)
	@Label(standard = "Name")
    @Enablement( expr = "${ Port == null }" )

	ValueProperty PROP_NAME = new ValueProperty(TYPE, "Name");
	
	Value<String> getName();
	void setName(String value);

	// Or Port 
	@CustomXmlValueBinding(impl = CaseInsenitiveAttributeValueBinding.class)
	@Label(standard = "Port")
    @Enablement( expr = "${ Name == null }" )

	ValueProperty PROP_PORT = new ValueProperty(TYPE, "Port");
	
	Value<String> getPort();
	void setPort(String value);

	
	// File 
	@CustomXmlValueBinding(impl = CaseInsenitiveAttributeValueBinding.class)
	@Label(standard = "File")
    @Enablement( expr = "${ Script == null }" )

	ValueProperty PROP_FILE = new ValueProperty(TYPE, "File");
	
	Value<String> getFile();
	void setFile(String value);

	// Or Script 
	@CustomXmlValueBinding(impl = CaseInsenitiveAttributeValueBinding.class)
	@Label(standard = "Script")
    @Enablement( expr = "${ File  == null }" )

	ValueProperty PROP_SCRIPT = new ValueProperty(TYPE, "Script");
	
	Value<String> getScript();
	void setScript(String value);

	@CustomXmlValueBinding(impl = CaseInsenitiveAttributeValueBinding.class)
	@Label(standard = "View")

	ValueProperty PROP_VIEW = new ValueProperty(TYPE, "View");
	
	Value<String> getView();
	void setView(String value);

	
	// Count (optional)
	@Type(base = Boolean.class)
	@CustomXmlValueBinding(impl = BooleanAttributeRemoveIfFalseValueBinding.class )
	@Label(standard = "Stop on EOF")
	
	ValueProperty PROP_STOPON_EOF = new ValueProperty(TYPE, "StopOnEof");
	
	Value<String> getStopOnEof();
	void setStopOnEof(String value);
	
	// Back Pressure
	@Type(base = Boolean.class)
	@CustomXmlValueBinding(impl = BooleanAttributeRemoveIfFalseValueBinding.class )
	@Label(standard = "Disable Backpressure")
	
	ValueProperty PROP_DISABLE_BACKPRESSURE = new ValueProperty(TYPE, "DisableBackPressure");
	
	Value<String> getDisableBackPressure();
	void setDisableBackPressure(String value);
	
}
