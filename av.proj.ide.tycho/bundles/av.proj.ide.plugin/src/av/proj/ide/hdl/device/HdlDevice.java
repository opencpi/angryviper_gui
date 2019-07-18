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

package av.proj.ide.hdl.device;

import org.eclipse.sapphire.ElementHandle;
import org.eclipse.sapphire.ElementList;
import org.eclipse.sapphire.ElementProperty;
import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.ListProperty;
import org.eclipse.sapphire.Type;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.xml.annotations.CustomXmlElementBinding;
import org.eclipse.sapphire.modeling.xml.annotations.CustomXmlListBinding;
import org.eclipse.sapphire.modeling.xml.annotations.CustomXmlRootBinding;

import av.proj.ide.custom.bindings.list.MultiCaseXmlListBinding;
import av.proj.ide.custom.bindings.root.HdlDeviceWorkerRootXmlBinding;
import av.proj.ide.custom.bindings.value.CaseInsensitiveSingleElementBinding;
import av.proj.ide.hdl.signal.Signal;
import av.proj.ide.owd.hdl.HdlWorker;

@CustomXmlRootBinding( value = HdlDeviceWorkerRootXmlBinding.class )

public interface HdlDevice extends HdlWorker {
	ElementType TYPE = new ElementType(HdlDevice.class);

	// *** RawProp Port ***
	@Type( base = RawProp.class )
	@CustomXmlElementBinding(impl = CaseInsensitiveSingleElementBinding.class)
	@Label( standard = "RawProp" )
	
	ElementProperty PROP_RAWPROP = new ElementProperty( TYPE, "RawProp" );
	
    ElementHandle<RawProp> getRawProp();

	// *** DevSignals ***
	@Type( base = DevSignal.class )
	@CustomXmlListBinding(impl = MultiCaseXmlListBinding.class)
	@Label( standard = "Dev Signals" )
			
	ListProperty PROP_DEV_SIGNALS = new ListProperty( TYPE, "DevSignals" );
			
	ElementList<Signal> getDevSignals();
	
	
	// *** Signals ***
	@Type( base = Signal.class )
	@CustomXmlListBinding(impl = MultiCaseXmlListBinding.class)
	@Label( standard = "Signals" )
			
	ListProperty PROP_SIGNALS = new ListProperty( TYPE, "Signals" );
			
	ElementList<Signal> getSignals();
	
	// *** Supports ***
	@Type( base = Supports.class )
	@CustomXmlListBinding(impl = MultiCaseXmlListBinding.class)
	@Label( standard = "Support Workers" )
			
	ListProperty PROP_SUPPORTSS = new ListProperty( TYPE, "Supportss" );
			
	ElementList<Supports> getSupportss();

}
