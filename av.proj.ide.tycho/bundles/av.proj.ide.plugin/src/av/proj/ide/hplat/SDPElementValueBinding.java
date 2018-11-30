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

import av.proj.ide.custom.bindings.value.CaseInsenitiveElementValueBinding;

/**
 * Don't know why, but this class is never initialized or used. It's kept
 * just in case, or we learn more later.  It appears that the count property
 * editor goes directly to the attribute. This must get put in place when
 * the doc is read in.  I'm guessing this would be used if another SDP
 * node could be created but that doesn't happen.
 */
public class SDPElementValueBinding extends CaseInsenitiveElementValueBinding {
	
	// The property name was something else entirely, so go specifically
	// for the SDP.
	@Override
	protected void initNames() {
        this.name = "SDP";
        this.lowerName = "sdp";
        this.camelName = "Sdp";
	}
	
}
