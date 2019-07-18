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

import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.modeling.xml.XmlElement;
import org.eclipse.sapphire.modeling.xml.XmlNamespaceResolver;
import org.eclipse.sapphire.modeling.xml.XmlPath;
import org.eclipse.sapphire.modeling.xml.XmlResource;

import av.proj.ide.custom.bindings.value.CaseInsensitiveSingleElementBinding;

public class MasterElementAndNameBinding extends CaseInsensitiveSingleElementBinding {
 
   
	protected void initNames() {
		// Expected to be all upper case
        this.name = propertyName;
        this.lowerName = this.name.toLowerCase();
        char c[] = this.lowerName.toCharArray();
        c[1] = Character.toUpperCase(c[1]);
        // Take a shot at a leading cap.
        this.camelName = new String(c);
	}
	
    @Override
    protected Object createUnderlyingObject( final ElementType type )
    {
    	XmlElement thisElement = (XmlElement) super.createUnderlyingObject(type);
		if(thisElement != null) {
	    	String elementName = thisElement.getDomNode().getLocalName();
	    	char start = elementName.charAt(0);
	    	String elmPathName = null;
	    	String master = null;
	        final Element element = property.element();
	    	if(Character.isUpperCase(start)) {
	    		elmPathName = "@Name";
	    		master = "@Master";
	    	}
	    	else {
	    		elmPathName = "@name";
	    		master = "@master";
	    	}
	        final XmlNamespaceResolver xmlNamespaceResolver = ( (XmlResource) element.resource() ).getXmlNamespaceResolver();
	        XmlPath tmpPath = new XmlPath(elmPathName, xmlNamespaceResolver);
			thisElement.setChildNodeText(tmpPath, this.lowerName, false);
			tmpPath = new XmlPath(master, xmlNamespaceResolver);
			thisElement.setChildNodeText(tmpPath, "true", false);
		}
    	
        return thisElement;
    }
	
	
}
