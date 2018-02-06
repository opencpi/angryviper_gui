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

package av.proj.ide.custom.bindings.list;

import static org.eclipse.sapphire.modeling.xml.XmlUtil.createQualifiedName;

import javax.xml.namespace.QName;

import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.modeling.xml.XmlNamespaceResolver;
import org.eclipse.sapphire.modeling.xml.XmlPath;
import org.eclipse.sapphire.modeling.xml.XmlResource;

import av.proj.ide.hplat.Device;

public class PlatformDeviceXmlListBinding extends PlatformSpecPropertyXmlListBinding {
	
	@Override
	protected void initBindingMetadata() {
		super.initBindingMetadata();
        final XmlNamespaceResolver xmlNamespaceResolver = ( (XmlResource) property().element().resource() ).getXmlNamespaceResolver();
		this.path = new XmlPath( "", xmlNamespaceResolver );
		this.modelElementTypes = new ElementType[2];
		this.modelElementTypes[0] = new ElementType(Device.class);
		this.modelElementTypes[1] = new ElementType(Device.class);
		this.xmlElementNames = new QName[ this.modelElementTypes.length ];            

		if (this.xmlElementNames.length == 2) {
			this.xmlElementNames[0] = createQualifiedName("Device", xmlNamespaceResolver);
			if (this.xmlElementNames[0] == null) {
				final ElementType type = this.modelElementTypes[0];
				this.xmlElementNames[0] = createDefaultElementName(type, xmlNamespaceResolver);
			}
		
			this.xmlElementNames[1] = createQualifiedName("device", xmlNamespaceResolver);
			if (this.xmlElementNames[1] == null) {
				final ElementType type = this.modelElementTypes[1];
				this.xmlElementNames[1] = createDefaultElementName(type, xmlNamespaceResolver);
			}
		}
	}
	
}
