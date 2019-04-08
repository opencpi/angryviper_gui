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

package av.proj.ide.hplat.specialBinds;

import org.eclipse.sapphire.Value;

import av.proj.ide.custom.bindings.value.CaseInsenitiveAttributeValueBinding;
import av.proj.ide.hplat.Slot.Signal;

/***
 * This special binding was implemented to ensure when a new slot signal is
 * added and the slot attribute is entered, the platform="" attribute is assigned.
 * 
 * Also note: as understanding is gained on custom bindings, things are getting
 * simpler.  Here the parentElement is used to put down the need attribute.
 */
public class SlotSignalSlotBinding extends CaseInsenitiveAttributeValueBinding {
	

    @Override
    public void write( final String value )
    {
    	super.write(value);
    	Signal slotSig = (Signal)property().element();
    	Value<String> plat = slotSig.getPlatform();
    	if(plat.content() == null) {
    		String platformAttr = null;
    		if(parentStartsUpperCase) {
    			platformAttr = "@Platform";
    		}
    		else {
    			platformAttr = "@platform";
     		}
    		parentElement.setChildNodeText(platformAttr, "", false);
    	}
     }
}
