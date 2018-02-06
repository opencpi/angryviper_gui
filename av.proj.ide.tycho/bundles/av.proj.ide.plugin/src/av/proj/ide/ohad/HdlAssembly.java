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

import java.util.List;

import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.ElementList;
import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.ListProperty;
import org.eclipse.sapphire.Transient;
import org.eclipse.sapphire.TransientProperty;
import org.eclipse.sapphire.Type;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.xml.annotations.CustomXmlListBinding;
import org.eclipse.sapphire.modeling.xml.annotations.CustomXmlRootBinding;
import org.eclipse.sapphire.modeling.xml.annotations.CustomXmlValueBinding;

import av.proj.ide.custom.bindings.list.OHADConnectionXmlListBinding;
import av.proj.ide.custom.bindings.list.OHADInstanceXmlListBinding;
import av.proj.ide.custom.bindings.list.OHADPropertyXmlListBinding;
import av.proj.ide.custom.bindings.root.HdlAssemblyRootXmlBinding;
import av.proj.ide.custom.bindings.value.GenericDualCaseXmlValueBinding;

@CustomXmlRootBinding( value = HdlAssemblyRootXmlBinding.class )

public interface HdlAssembly extends Element {
	ElementType TYPE = new ElementType( HdlAssembly.class );
	
	// *** Name ***
	@CustomXmlValueBinding( impl=GenericDualCaseXmlValueBinding.class )
	@Label(standard = "Name")
	
	ValueProperty PROP_NAME = new ValueProperty(TYPE, "Name");

	Value<String> getName();
	void setName(String value);
	
	// *** Instances ***
	@Type ( base=Instance.class )
	@CustomXmlListBinding( impl=OHADInstanceXmlListBinding.class )
	@Label( standard = "Instances" )
			
	ListProperty PROP_INSTANCES = new ListProperty( TYPE, "Instances" );
			
	ElementList<Instance> getInstances();
		
	// *** Connections *** 
	@Type( base=Connection.class )
	@CustomXmlListBinding( impl=OHADConnectionXmlListBinding.class )
	@Label( standard = "Connections" )
			
	ListProperty PROP_CONNECTIONS = new ListProperty( TYPE, "Connections" );
			
	ElementList<Connection> getConnections();
		
	// *** Properties ***
	@Type( base = ApplicationProperty.class )
	@CustomXmlListBinding(impl = OHADPropertyXmlListBinding.class)
	@Label( standard = "Properties" )
			
	ListProperty PROP_PROPERTIES = new ListProperty( TYPE, "Properties" );
			
	ElementList<ApplicationProperty> getProperties();
	
	// *** Assembly File Location ***
	@Type( base = String.class )
	TransientProperty PROP_LOCATION = new TransientProperty(TYPE, "Location");
	Transient<String> getLocation();
	void setLocation(String value);
	
	// *** Assembly Initialized Value ***
	@Type(base = Boolean.class)
	TransientProperty PROP_INITIALIZED = new TransientProperty(TYPE, "Initialized");
	Transient<Boolean> getInitialized();
	void setInitialized(Boolean value);

	// *** Assembly Possible Values ***
	@Type(base = String.class)
	TransientProperty PROP_VALUES = new TransientProperty(TYPE, "Values");
	Transient<String> getValues();
	void setValues(String value);

	// *** Assembly Editor Thread ***
	@Type(base = Thread.class)
	TransientProperty PROP_THREAD = new TransientProperty(TYPE, "Thread");
	Transient<Thread> getThread();
	void setThread(Thread thread);

	// *** Assembly Shared List ***
	@Type(base = List.class)
	TransientProperty PROP_LIST = new TransientProperty(TYPE, "List");
	Transient<List<Boolean>> getList();
	void setList(List<Boolean> list);

}
