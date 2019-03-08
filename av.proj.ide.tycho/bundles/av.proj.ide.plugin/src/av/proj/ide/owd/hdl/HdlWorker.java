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

package av.proj.ide.owd.hdl;

import org.eclipse.sapphire.ElementHandle;
import org.eclipse.sapphire.ElementList;
import org.eclipse.sapphire.ElementProperty;
import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.ListProperty;
import org.eclipse.sapphire.Type;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.Enablement;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.Service;
import org.eclipse.sapphire.modeling.xml.annotations.CustomXmlListBinding;
import org.eclipse.sapphire.modeling.xml.annotations.CustomXmlRootBinding;
import org.eclipse.sapphire.modeling.xml.annotations.CustomXmlValueBinding;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;

import av.proj.ide.custom.bindings.list.ControlOperationsListBinding;
import av.proj.ide.custom.bindings.list.OWDStreamInterfaceXmlListBinding;
import av.proj.ide.custom.bindings.root.HdlWorkerRootXmlBinding;
import av.proj.ide.custom.bindings.value.BooleanAttributeRemoveIfFalseValueBinding;
import av.proj.ide.custom.bindings.value.CaseInsenitiveAttributeValueBinding;
import av.proj.ide.owd.ControlOperation;
import av.proj.ide.owd.Worker;
import av.proj.ide.services.HdlControlOperationsPossibleValueService;

@CustomXmlRootBinding( value = HdlWorkerRootXmlBinding.class )

public interface HdlWorker extends Worker {
	ElementType TYPE = new ElementType( HdlWorker.class );

	/***
	 * Additional HDL Worker Attributes
	 */
	
	// *** ControlOperations ***
	@Label(standard = "ControlOperations")
	@Type(base = ControlOperation.class)
	@CustomXmlListBinding(impl = ControlOperationsListBinding.class)
	@Service(impl = HdlControlOperationsPossibleValueService.class)
	
	ListProperty PROP_CONTROL_OPERATIONS = new ListProperty(TYPE, "ControlOperations");

	ElementList<ControlOperation> getControlOperations();
	
	// *** DataWidth *** 
	@CustomXmlValueBinding( impl=CaseInsenitiveAttributeValueBinding.class )
	@Label(standard = "DataWidth") 
		
	ValueProperty PROP_DATA_WIDTH = new ValueProperty(TYPE, "DataWidth");

	Value<String> getDataWidth();
	void setDataWidth(String value);

	// *** Raw Properties ***
	@Type( base = Boolean.class )
	@CustomXmlValueBinding( impl=BooleanAttributeRemoveIfFalseValueBinding.class )
	@Label( standard = "Raw Properties" )
	@Enablement(expr="false")
		
	ValueProperty PROP_RAW_PROPERTIES = new ValueProperty(TYPE, "RawProperties");
		
	Value<Boolean> getRawProperties();
	void setRawProperties( String value );
	void setRawProperties( Boolean value );

	// *** Clock *** 
	@CustomXmlValueBinding( impl=CaseInsenitiveAttributeValueBinding.class )
	@Label(standard = "First Raw Property")
	@Enablement(expr = "${ RawProperties == null }")
			
	ValueProperty PROP_FIRST_RAW_PROPERTY = new ValueProperty(TYPE, "FirstRawProperty");

	Value<String> getFirstRawProperty();
	void setFirstRawProperty(String value);

	/***
	 * Infrastructure HDL Worker Attributes
	 */
	@Type( base = Boolean.class )
	@CustomXmlValueBinding( impl=BooleanAttributeRemoveIfFalseValueBinding.class )
	@Label( standard = "Outer" )
		
	ValueProperty PROP_OUTER = new ValueProperty(TYPE, "Outer");
		
	Value<Boolean> getOuter();
	void setOuter( String value );
	void setOuter( Boolean value );

	// These two have yet to be supported.  Leave them.
	// *** Clock *** 
	@CustomXmlValueBinding( impl=CaseInsenitiveAttributeValueBinding.class )
	@Label(standard = "Pattern")
			
	ValueProperty PROP_PATTERN = new ValueProperty(TYPE, "Pattern");

	Value<String> getPattern();
	void setPattern(String value);
	
	@CustomXmlValueBinding( impl=CaseInsenitiveAttributeValueBinding.class )
	@Label(standard = "Port Pattern")
			
	ValueProperty PROP_PORT_PATTERN = new ValueProperty(TYPE, "PortPattern");

	Value<String> getPortPattern();
	void setPortPattern(String value);
	

	@CustomXmlValueBinding( impl=CaseInsenitiveAttributeValueBinding.class )
	@Label(standard = "SizeOfConfigSpace")
			
	ValueProperty PROP_SIZEOF_CONFIG_SPACE = new ValueProperty(TYPE, "SizeOfConfigSpace");

	Value<String> getSizeOfConfigSpace();
	void setSizeOfConfigSpace(String value);
	
	@Type( base = Boolean.class )
	@CustomXmlValueBinding( impl=BooleanAttributeRemoveIfFalseValueBinding.class )
	@Label( standard = "Sub32BitConfigProperties" )
		
	ValueProperty PROP_SUB32_BIT_CONFIG_PROPERTIES = new ValueProperty(TYPE, "Sub32BitConfigProperties");
		
	Value<Boolean> getSub32BitConfigProperties();
	void setSub32BitConfigProperties( String value );
	void setSub32BitConfigProperties( Boolean value );
	
	/***
	 * Additional HDL Worker Elements
	 */
	
	// *** ControlInterface ***
	@Type( base = ControlInterface.class )
	@XmlBinding( path = "ControlInterface" )
	/**
	 * Experimenting.  The problem is that when using the <with> sdef element a path has to be defined
	 * and that is the path (element). ElementHandle ends up creating it when the with checkbox is selected.
	 * When the custom binding is used, then the createUnderlyingObject is call and that adds it again.
	 * I don't see a good work around for this other than using a button actuator and doing it all manually. 
	 *
	@CustomXmlElementBinding(impl=CaseInsensitiveElementBinding.class )
	@XmlElementBinding.Mapping(element = "ControlInterface", type = ControlInterface.class)
	 */
	@Label( standard = "ControlInterface" )

	ElementProperty PROP_CONTROL_INTERFACE = new ElementProperty( TYPE, "ControlInterface" );
	
    ElementHandle<ControlInterface> getControlInterface();
	
	// *** TimeInterface ***
	@Type( base = TimeInterface.class )
	@XmlBinding( path = "TimeInterface" )
	@Label( standard = "TimeInterface" )
	
	ElementProperty PROP_TIME_INTERFACE = new ElementProperty( TYPE, "TimeInterface" );
	
    ElementHandle<TimeInterface> getTimeInterface();
    
	// *** StreamInterfaces ***
	@Type( base = StreamInterface.class )
	@CustomXmlListBinding(impl=OWDStreamInterfaceXmlListBinding.class)
	@Label( standard = "StreamInterfaces" )
				
	ListProperty PROP_STREAM_INTERFACES = new ListProperty( TYPE, "StreamInterfaces" );
				
	ElementList<StreamInterface> getStreamInterfaces();
	
	
}
