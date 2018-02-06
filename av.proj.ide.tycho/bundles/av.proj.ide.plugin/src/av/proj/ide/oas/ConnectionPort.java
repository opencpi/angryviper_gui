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

package av.proj.ide.oas;

import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.ElementReference;
import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.ReferenceValue;
import org.eclipse.sapphire.Transient;
import org.eclipse.sapphire.TransientProperty;
import org.eclipse.sapphire.Type;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.Reference;
import org.eclipse.sapphire.modeling.annotations.Required;
import org.eclipse.sapphire.modeling.annotations.Service;
import org.eclipse.sapphire.modeling.xml.annotations.CustomXmlValueBinding;

import av.proj.ide.custom.bindings.value.GenericDualCaseXmlValueBinding;
import av.proj.ide.oas.internal.ConnectionPortPossibleValueService;

public interface ConnectionPort extends Element {
	ElementType TYPE = new ElementType( ConnectionPort.class );
	
	// *** Name ***
	@CustomXmlValueBinding( impl=GenericDualCaseXmlValueBinding.class )
	@Label(standard = "Port Name")
	@Required
	@Service( impl = ConnectionPortPossibleValueService.class )
	
	ValueProperty PROP_NAME = new ValueProperty(TYPE, "Name");

	Value<String> getName();
	void setName(String value);
	
	// *** Instance ***
	@Label( standard = "Instance" )
	@CustomXmlValueBinding( impl=GenericDualCaseXmlValueBinding.class )
	@Reference( target = Instance.class )
	@ElementReference( list = "../../Instances", key = "Name" )
	
	ValueProperty PROP_INSTANCE = new ValueProperty( TYPE, "Instance" );
	
	ReferenceValue<String, Instance> getInstance();
	void setInstance( String value );
	
	// *** Component Name ***
	@Type( base = String.class )
	TransientProperty PROP_COMPONENT_NAME = new TransientProperty(TYPE, "ComponentName");
	Transient<String> getComponentName();
	void setComponentName(String value);
	

}
