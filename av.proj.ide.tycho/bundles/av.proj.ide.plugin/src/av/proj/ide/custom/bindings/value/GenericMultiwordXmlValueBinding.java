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

package av.proj.ide.custom.bindings.value;

import org.eclipse.sapphire.modeling.xml.XmlPath;

public class GenericMultiwordXmlValueBinding extends BooleanAttributeRemoveIfFalseValueBinding {
	
	protected String camelName = "";
	
	@Override
	protected void initBindingMetadata()
    {
        super.initBindingMetadata();
        char c[] = this.name.toCharArray();
        c[1] = Character.toLowerCase(c[1]);
        this.camelName = new String(c);
    }
	
    @Override
    public String read()
    {
        String value = super.read();
        if(value == null || value.isEmpty()) {
            this.path = new XmlPath(this.camelName , resource().getXmlNamespaceResolver());
            value = super.read();
        }
        if(value == null || value.isEmpty()) {
            this.path = null;
        }
        return value;
    }
}
