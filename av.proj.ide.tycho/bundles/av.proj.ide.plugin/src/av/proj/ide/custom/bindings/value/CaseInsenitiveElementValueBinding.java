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

import java.util.List;

import org.eclipse.sapphire.modeling.xml.XmlElement;
import org.eclipse.sapphire.modeling.xml.XmlNamespaceResolver;
import org.eclipse.sapphire.modeling.xml.XmlNode;
import org.eclipse.sapphire.modeling.xml.XmlPath;

/***
 * The was developed to support tag attributes.  It might work for tags as well.
 * Since the XML files used by open cpi is not standardized xml binding need to deal
 * with multiple styles of case. This class is intended to work with single syllable
 * words.  There is another class that deals with multiple syllable words.
  */
public class CaseInsenitiveElementValueBinding extends CaseInsenitiveAttributeValueBinding {

	// TODO: need an init -- attribute names start with @
//	@Override
//	protected void initBindingMetadata()
//    {
//		super.initBindingMetadata();
//    }
	
	@Override
	protected void initNames() {
        this.name = propertyName;
        this.lowerName = this.name.toLowerCase();
        char c[] = this.name.toCharArray();
        c[1] = Character.toLowerCase(c[0]);
        // If it is a multiple word camel cased this will take a shot at it.
        this.camelName = new String(c);
		
	}
    @Override
    public String read()
    {
    	if(this.path == null)
    		getPathToThisProperty();
    	
        if(this.path != null) {
        	// it's there
        	return super.read();
        }
        // Doesn't exist yet.
        return null;
    }
    
    @Override
    protected XmlPath lookForIt(XmlNamespaceResolver xmlResolver) {
        // Try rooting through the existing elements to see if it's there;
    	// String elm = parentElement.getDomNode().getLocalName();
    	List<XmlElement> elements = parentElement.getChildElements();	
    	for(XmlElement elem : elements) {
    		String elemName = elem.getLocalName();
    		
    		if(lowerName.equals(elemName.toLowerCase()) ){
    	    	XmlPath propPath = new XmlPath(elemName , xmlResolver);
    	        XmlNode node = parentElement.getChildNode(propPath, false);
    	        if(node != null) {
    	        	return propPath;
    	        }
    		}
    	}
    	
    	return null;
    }

    @Override
    public void write( final String value )
    {
    	if(this.path == null) {
    		// If this is a new instance of this attribute, default it to property name.
    		this.path = new XmlPath(this.name , resource().getXmlNamespaceResolver());
    	}
    	super.write(value);
    }
}
