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

import av.proj.ide.custom.bindings.list.OASConnectionXmlListBinding;
import av.proj.ide.custom.bindings.list.OASInstanceXmlListBinding;
import av.proj.ide.custom.bindings.list.OASPropertyXmlListBinding;
import av.proj.ide.custom.bindings.root.ApplicationRootXmlBinding;
import av.proj.ide.custom.bindings.value.CaseInsenitiveAttributeValueBinding;
import av.proj.ide.custom.bindings.value.GenericDualCaseXmlValueBinding;

@CustomXmlRootBinding(value = ApplicationRootXmlBinding.class)

public interface Application extends Element {
	ElementType TYPE = new ElementType( Application.class );
	
	// *** Name ***
	@CustomXmlValueBinding( impl=GenericDualCaseXmlValueBinding.class )
	@Label(standard = "Name")
	
	ValueProperty PROP_NAME = new ValueProperty(TYPE, "Name");

	Value<String> getName();
	void setName(String value);
	
	// *** Package ***
	@CustomXmlValueBinding( impl=GenericDualCaseXmlValueBinding.class )
	@Label(standard = "Package")
	
	ValueProperty PROP_PACKAGE = new ValueProperty(TYPE, "Package");

	Value<String> getPackage();
	void setPackage(String value);
	
	// *** Done ***
	@CustomXmlValueBinding( impl=GenericDualCaseXmlValueBinding.class )
	@Label(standard = "Done")
	
	ValueProperty PROP_DONE = new ValueProperty(TYPE, "Done");

	Value<String> getDone();
	void setDone(String value);
	
	// *** MaxProcessors ***
	@CustomXmlValueBinding( impl=CaseInsenitiveAttributeValueBinding.class )
	@Label(standard = "MaxProcessors")
	
	ValueProperty PROP_MAX_PROCESSORS = new ValueProperty(TYPE, "MaxProcessors");

	Value<String> getMaxProcessors();
	void setMaxProcessors(String value);
	
	// *** Instances ***
	@Type( base = Instance.class )
	@CustomXmlListBinding( impl=OASInstanceXmlListBinding.class )
	@Label( standard = "Instances" )
		
	ListProperty PROP_INSTANCES = new ListProperty( TYPE, "Instances" );
		
	ElementList<Instance> getInstances();
	
	// *** Connections *** 
	@Type ( base = Connection.class )
	@CustomXmlListBinding( impl=OASConnectionXmlListBinding.class )
	@Label( standard = "Connections" )
		
	ListProperty PROP_CONNECTIONS = new ListProperty( TYPE, "Connections" );
		
	ElementList<Connection> getConnections();
	
	// *** Properties ***
	@Type( base = ApplicationProperty.class )
	//@XmlListBinding( mappings = { @XmlListBinding.Mapping( element = "Property", type = ApplicationProperty.class ), @XmlListBinding.Mapping( element = "property", type = ApplicationPropertyLower.class ) } )
	@CustomXmlListBinding(impl = OASPropertyXmlListBinding.class )
	@Label( standard = "Properties" )
		
	ListProperty PROP_PROPERTIES = new ListProperty( TYPE, "Properties" );
		
	ElementList<ApplicationProperty> getProperties();
	
	// *** Application File Location ***
	@Type( base = String.class )
	TransientProperty PROP_LOCATION = new TransientProperty(TYPE, "Location");
	Transient<String> getLocation();
	void setLocation(String value);
	
	// *** Application Initialized Value ***
	@Type( base = Boolean.class )
	TransientProperty PROP_INITIALIZED = new TransientProperty(TYPE, "Initialized");
	Transient<Boolean> getInitialized();
	void setInitialized(Boolean value);
	
	// *** Application Possible Values ***
	@Type( base = String.class )
	TransientProperty PROP_VALUES = new TransientProperty(TYPE, "Values");
	Transient<String> getValues();
	void setValues(String value);
	
	// *** Application Editor Thread ***
	@Type(base=Thread.class)
	TransientProperty PROP_THREAD = new TransientProperty(TYPE, "Thread");
	Transient<Thread> getThread();
	void setThread(Thread thread);
	
	// *** Application Shared List ***
	@Type(base=List.class)
	TransientProperty PROP_LIST = new TransientProperty(TYPE, "List");
	Transient<List<Boolean>> getList();
	void setList(List<Boolean> list);
}
