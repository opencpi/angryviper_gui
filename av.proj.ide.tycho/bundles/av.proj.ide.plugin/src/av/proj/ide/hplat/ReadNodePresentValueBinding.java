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

import org.eclipse.sapphire.Value;

import av.proj.ide.custom.bindings.value.BooleanNodePresentBinding;

public class ReadNodePresentValueBinding extends BooleanNodePresentBinding {
	
	// Here the intent was to have just a property to read something to
	// ensure it is there but won't allow the element to be removed.
	// In this case if was the SDP.  The element has to be there but the
	// user can augment it with another attribute so a true SDP property.
	// The SdpRead property gave the two desired paths to the attribute.
	@Override
	protected void getPropertyName() {
        final Value<?> property = (Value<?>) property();
        String propName = property.name();
        int suffixIdx = propName.indexOf("Read");
        propertyName = propName.substring(0, suffixIdx);
	}
}
