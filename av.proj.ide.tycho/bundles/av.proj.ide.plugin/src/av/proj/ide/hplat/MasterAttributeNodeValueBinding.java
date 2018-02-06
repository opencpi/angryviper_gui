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
import org.eclipse.sapphire.modeling.xml.StandardXmlValueBindingImpl;
import org.eclipse.sapphire.modeling.xml.XmlElement;
import org.eclipse.sapphire.modeling.xml.XmlPath;

public class MasterAttributeNodeValueBinding extends StandardXmlValueBindingImpl {
	
	private String name = "";
	private String lowerName = "";
	
	@Override
	protected void initBindingMetadata()
    {
		super.initBindingMetadata();
        final Value<?> property = (Value<?>) property();
        this.name = property.name();
        this.lowerName = this.name.toLowerCase();
        // This object persists for the life of the document.
        // Want to get the path to the element on the first read.
        this.path = null;
    }
	
	@Override
    public String read()
    {
		String value = "false";
		// Get doc root node
        final XmlElement element = xml( false );
        if( element == null ) return value;
        if(this.path != null) {
            if(element.getChildNode( this.path, false ) != null ) {
              	value = "true";
            }
            return value;
        }
        // First read--go find it...
        
		XmlPath tmpPathLc = new XmlPath(this.lowerName, resource().getXmlNamespaceResolver());
		XmlPath tmpPathUc = new XmlPath(this.name, resource().getXmlNamespaceResolver());
		// Determine property value and set the path to the node if it's found.
		// Test two cases.
        if(element.getChildNode( tmpPathLc, false ) != null ) {
        	this.path = tmpPathLc;
          	value = "true";
        }
        else if(element.getChildNode( tmpPathUc, false ) != null ) {
        	this.path = tmpPathUc;
          	value = "true";
        }
        return value;
    }

    @Override
    public void write( final String value )
    {
		if (value.equals("true")) {
			
			// Put the node in place using lower case and preserve that path.
			
	        this.path = new XmlPath(this.lowerName , resource().getXmlNamespaceResolver());
    		xml( true ).setChildNodeText( this.path, "", false );
    		xml(true).getChildElement(this.lowerName, false).setChildNodeText("@master", value, true);
    	} else {
    		XmlElement element = xml( false );
        	if (element != null) {
        		element.removeChildNode(this.path);
        	}
    	}
    }
}
