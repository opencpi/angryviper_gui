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

package av.proj.ide.hplat;

import org.eclipse.sapphire.ElementList;
import org.eclipse.sapphire.ElementProperty;
import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.ListProperty;
import org.eclipse.sapphire.Type;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.Enablement;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.Required;
import org.eclipse.sapphire.modeling.annotations.Service;
import org.eclipse.sapphire.modeling.xml.annotations.CustomXmlElementBinding;
import org.eclipse.sapphire.modeling.xml.annotations.CustomXmlListBinding;
import org.eclipse.sapphire.modeling.xml.annotations.CustomXmlRootBinding;
import org.eclipse.sapphire.modeling.xml.annotations.CustomXmlValueBinding;

import av.proj.ide.custom.bindings.list.MultiCaseXmlListBinding;
import av.proj.ide.custom.bindings.list.SimpleDualCaseXmlListBinding;
import av.proj.ide.custom.bindings.value.CaseInsenitiveAttributeValueBinding;
import av.proj.ide.hdl.device.HdlDevice;
import av.proj.ide.hplat.specialBinds.CpMasterNodeElementBinding;
import av.proj.ide.hplat.specialBinds.MasterElementAndNameBinding;
import av.proj.ide.hplat.specialBinds.MetadataNodeElementBinding;
import av.proj.ide.hplat.specialBinds.ReadTimeserverBinding;
import av.proj.ide.hplat.specialBinds.TimeBaseNodeElementBinding;
import av.proj.ide.services.NameValidationService;

/***
 * Interface to the HDL Platform XML document. The root tag = <HdlPlatform  Language="" spec="">
 */
@CustomXmlRootBinding( value = HdlPlatformRootXmlBinding.class )

public interface HdlPlatform extends HdlDevice
{
	ElementType TYPE = new ElementType(HdlPlatform.class);

	// *** Language attribute***
	@CustomXmlValueBinding(impl = CaseInsenitiveAttributeValueBinding.class)
	@Label(standard = "Language")
	@Required
	@Service(impl=NameValidationService.class)
	ValueProperty PROP_LANGUAGE = new ValueProperty(TYPE, "Language");

	Value<String> getLanguage();
	void setLanguage(String value);

	// *** spec attribute***
	@CustomXmlValueBinding(impl = CaseInsenitiveAttributeValueBinding.class)
	//@TO DO: why is not this binding reflect in the editor...It seems like some
	//method is capitalizing the first Letter. This effect is done ONLY to the
	//first letter.
	@Label(standard = "spec")
	@Required
	@Service(impl=NameValidationService.class)
	@Enablement( expr="false")
	// "Spec" is 
	ValueProperty PROP_SPEC = new ValueProperty(TYPE, "Spec");

	Value<String> getSpec();
	void setSpec(String value);

	// Required Elements

	// *** Metadata Access Port ***
	@Type( base = Boolean.class )
	@CustomXmlValueBinding(impl=MetadataNodeElementBinding.class)
	@Label( standard = "Metadata Access Port" )
	@Enablement( expr="${Metadata == null }")
	ValueProperty PROP_METADATA = new ValueProperty(TYPE, "MetaData");
	
	Value<Boolean> getMetadata();
	void setMetadataPort(String value);
	void setMetadataPort(Boolean value);
	
	//  ***  Timebase Output Port
	@Type( base = Boolean.class )
	@CustomXmlValueBinding(impl=TimeBaseNodeElementBinding.class)
	@Label( standard = "Timebase Output Port" )
	@Enablement( expr="${TimeBase == null }")
	ValueProperty PROP_TIMEBASE = new ValueProperty(TYPE, "TimeBase");
	
	// *** Time Server Device ***
	@Type( base = Boolean.class )
	@CustomXmlValueBinding(impl=ReadTimeserverBinding.class)
	@Label( standard = "Time Server Device" )
	@Enablement( expr="false")
	ValueProperty PROP_TIMESERVER_READ = new ValueProperty(TYPE, "TimeServerRead");
	Value<Boolean> getTimeServerRead();

	Value<Boolean> getTimebase();
	
	// *** Scalable Data Plane ***
	// Note: Set the property name this way... 
	
	@Type( base = SDP.class )
	@CustomXmlElementBinding(impl = MasterElementAndNameBinding.class)
	@Label( standard = "SDP" )
	@Enablement( expr="${UNOC == null}")
	ElementProperty PROP_SDP = new ElementProperty(TYPE, "SDP");
	
	SDP getSDP();


	// *** Scalable Data Plane ***
	// Note: Set the property name this way... 
	@Type( base = UNOC.class )
	@CustomXmlElementBinding(impl = MasterElementAndNameBinding.class)
	@Label( standard = "UNOC" )
	@Enablement( expr="${SDP == null}")
	ElementProperty PROP_UNOC = new ElementProperty(TYPE, "UNOC");
	
	UNOC getUnoc();

	//  Additional Ports (optional)
	//  ***  Control Plane Port
	
	@Type( base = Boolean.class )
	@CustomXmlValueBinding(impl=CpMasterNodeElementBinding.class)
	@Label( standard = "Directly Support the Control Plane Port" )

	ValueProperty PROP_CPMASTER = new ValueProperty(TYPE, "CpMaster");
	Value<Boolean> getCpmaster();
	void setCpmaster(String value);
	void setCpmaster(Boolean value);
	

	//  Element Lists
	
	// *** SpecProperties ***

	@Type( base = SpecProperty.class )
	@CustomXmlListBinding(impl = MultiCaseXmlListBinding.class)
	@Label( standard = "SpecProperties" )
			
	ListProperty PROP_SPEC_PROPERTIES = new ListProperty( TYPE, "SpecProperties" );
			
	@Override
	ElementList<SpecProperty> getSpecProperties();
	
	// *** Devices ***
	@Type( base = Device.class )
	@CustomXmlListBinding(impl = SimpleDualCaseXmlListBinding.class)
	@Label( standard = "Devices" )
			
	ListProperty PROP_DEVICES = new ListProperty( TYPE, "Devices" );
			
	ElementList<Device> getDevices();
	
  	// *** Slots ***
	@Type( base = Slot.class )
	@CustomXmlListBinding(impl = SimpleDualCaseXmlListBinding.class)
	@Label( standard = "Slots" )
			
	ListProperty PROP_SLOTS = new ListProperty( TYPE, "Slots" );
			
	ElementList<Slot> getSlots();
	
}