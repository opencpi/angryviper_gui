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

import org.eclipse.sapphire.modeling.xml.XmlAttribute;
import org.eclipse.sapphire.modeling.xml.XmlElement;
import org.eclipse.sapphire.modeling.xml.XmlPath;

/***
 * This class was apparently written to correct some changes XML.  This used to reside in a
 * class named GenericXmlValueBinding.
 */
public class SpecialPropertyTypeValueBinding extends CaseInsenitiveAttributeValueBinding {

    @Override
    public void write( final String value )
    {
		if (this.lowerName.equals("@type")) {
			
        	if (value != null) {
        		super.write(value);
        		
    			// They user has changed the type of an existing property or member.
        		// Strings, Enums, have additional inputs and Structs have members.
    			// This clears those additional attributes when the type field is changed.
        		
            	List<XmlAttribute> attrs = parentElement.getAttributes();
             	for(XmlAttribute attribute : attrs) {
            		String attributeName = attribute.getDomNode().getLocalName();
					if (attributeName != null) {
						String attrName = attributeName.toLowerCase();
						switch (attrName) {
						case "stringlength":
						case "enums":
							String pathLabel = "@" + attributeName;
							XmlPath tmpPath = new XmlPath(pathLabel, resource().getXmlNamespaceResolver());
							parentElement.removeChildNode(tmpPath);
							return;
						}
					}
             	}
        		
//       			if (!value.equals("string") && !value.equals("String")) {
//    				XmlPath tmpPath = new XmlPath("@stringLength", resource().getXmlNamespaceResolver());
//    				XmlElement element = xml( false );
//		        	if (element != null) {
//		        		element.removeChildNode(tmpPath);
//		        	}
//		        	
//		        	tmpPath = new XmlPath("@StringLength", resource().getXmlNamespaceResolver());
//		            element = xml(false);
//		            if( element != null ){
//		                element.removeChildNode( tmpPath );
//		            }
//		                
//		            tmpPath = new XmlPath("@stringlength", resource().getXmlNamespaceResolver());
//		            element = xml( false );
//		            if( element != null ) {
//		                element.removeChildNode( tmpPath );
//		            }
//    			}
//    			
//    			if (!value.equals("enum") && !value.equals("Enum")) {
//    				XmlPath tmpPath = new XmlPath("@enums", resource().getXmlNamespaceResolver());
//    				XmlElement element = xml( false );
//		        	if (element != null) {
//		        		element.removeChildNode(tmpPath);
//		        	}
//		        	
//		        	tmpPath = new XmlPath("@Enums", resource().getXmlNamespaceResolver());
//		            element = xml(false);
//		            if( element != null ){
//		                element.removeChildNode( tmpPath );
//		            }
//    			}
    			
    			if (!value.equals("struct") && !value.equals("Struct")) {
    				XmlElement element = xml(false);
    				if (element != null) {
    					for (XmlElement e : element.getChildElements()) {
    						e.remove();
    					}
    				}
    			}
        		
        	}
			
		}
		else {
			super.write(value);
		}
    }

 }
