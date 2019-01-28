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

package av.proj.ide.hdl.signal;

import static org.eclipse.sapphire.modeling.xml.XmlUtil.createQualifiedName;

import java.util.LinkedHashMap;

import javax.xml.namespace.QName;

import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.Property;
import org.eclipse.sapphire.modeling.xml.XmlElement;
import org.eclipse.sapphire.modeling.xml.XmlNamespaceResolver;
import org.eclipse.sapphire.modeling.xml.XmlPath;
import org.eclipse.sapphire.modeling.xml.XmlResource;

import av.proj.ide.custom.bindings.list.MultiCaseXmlListBinding;

public class SignalXmlListBinding extends MultiCaseXmlListBinding {
	
	
    @Override
	protected void initNames(Property p) {
        this.name = "Signal";
        this.lowerName = this.name.toLowerCase();
		theseDocElements = new LinkedHashMap<String, QName>();
        final XmlNamespaceResolver xmlNamespaceResolver = ( (XmlResource) p.element().resource() ).getXmlNamespaceResolver();
        QName qn = createQualifiedName(this.name, xmlNamespaceResolver);
        theseDocElements.put(this.name, qn);
   	
	}

    @Override
    protected Object insertUnderlyingObject( final ElementType type,
                                             final int position )
    {
    	XmlElement childElement = (XmlElement)super.insertUnderlyingObject(type, position);
        final XmlNamespaceResolver xmlNamespaceResolver = ( (XmlResource) property().element().resource() ).getXmlNamespaceResolver();
		XmlPath tmpPath = new XmlPath("@Direction", xmlNamespaceResolver);
		childElement.setChildNodeText(tmpPath, "in", false);
   	
    	return childElement;

    }
}
