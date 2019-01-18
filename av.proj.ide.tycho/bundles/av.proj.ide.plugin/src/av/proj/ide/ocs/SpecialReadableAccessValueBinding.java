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

package av.proj.ide.ocs;

import org.eclipse.sapphire.modeling.xml.XmlElement;
import org.eclipse.sapphire.modeling.xml.XmlPath;

import av.proj.ide.custom.bindings.value.BooleanAttributeRemoveIfFalseValueBinding;

public class SpecialReadableAccessValueBinding extends BooleanAttributeRemoveIfFalseValueBinding {
 
    @Override
    public void write( final String value )
    {
		super.write(value);
		// Set initial = true as well.
		if(value.equals("true") ){
			XmlElement component = parentElement;
			XmlPath tmpPath = new XmlPath("@Initial", resource().getXmlNamespaceResolver());
			component.setChildNodeText(tmpPath, value, false);
		}
    }
}
