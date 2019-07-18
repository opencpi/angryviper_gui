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

import static org.eclipse.sapphire.modeling.xml.XmlUtil.createQualifiedName;

import java.util.LinkedHashMap;

import javax.xml.namespace.QName;

import org.eclipse.sapphire.Property;
import org.eclipse.sapphire.modeling.xml.XmlNamespaceResolver;
import org.eclipse.sapphire.modeling.xml.XmlResource;

import av.proj.ide.custom.bindings.list.MultiCaseXmlListBinding;

public class HdlCtrlInterfaceXmlBinding extends MultiCaseXmlListBinding {
	
	
    @Override
	protected void initNames(Property p) {
        this.name = "ControlInterface";
        this.lowerName = this.name.toLowerCase();
		theseDocElements = new LinkedHashMap<String, QName>();
        final XmlNamespaceResolver xmlNamespaceResolver = ( (XmlResource) p.element().resource() ).getXmlNamespaceResolver();
        QName qn = createQualifiedName(this.name, xmlNamespaceResolver);
        theseDocElements.put(this.name, qn);
   	
	}

}
