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

import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.modeling.DelimitedListBindingImpl;
import org.eclipse.sapphire.modeling.xml.StandardXmlNamespaceResolver;
import org.eclipse.sapphire.modeling.xml.XmlElement;
import org.eclipse.sapphire.modeling.xml.XmlNode;
import org.eclipse.sapphire.modeling.xml.XmlPath;
import org.eclipse.sapphire.modeling.xml.XmlResource;

import av.proj.ide.owd.Worker;

public class ControlOperationsListBinding extends DelimitedListBindingImpl {
	private static final StandardXmlNamespaceResolver NAMESPACE_RESOLVER = new StandardXmlNamespaceResolver( Worker.TYPE );
	private final XmlPath path;
	
	public ControlOperationsListBinding() {
		this.path = new XmlPath( "@controlOperations", NAMESPACE_RESOLVER );
	}

	@Override
	protected String readListString() {
		final Element parent = property().element();
	    final XmlElement parentXmlElement = ( (XmlResource) parent.resource() ).getXmlElement();
	    
	    if( parentXmlElement == null )
	    {
	        return null;
	    }
	    
	    XmlNode listXmlNode = parentXmlElement.getChildNode( this.path, false );
	    
	    if( listXmlNode == null ) {
	        XmlPath tmpPath = new XmlPath("@ControlOperations", NAMESPACE_RESOLVER);
	        listXmlNode = parentXmlElement.getChildNode(tmpPath , false);
	        
	        if (listXmlNode == null) {
	        	tmpPath = new XmlPath("@controloperations", NAMESPACE_RESOLVER);
		        listXmlNode = parentXmlElement.getChildNode(tmpPath , false);
		        
		        if (listXmlNode == null) {
		        	return null;
		        }
	        }
	    }
	    
	    return listXmlNode.getText();
	}

	@Override
	protected void writeListString(String str) {
		final Element parent = property().element();
	    final XmlElement parentXmlElement = ( (XmlResource) parent.resource() ).getXmlElement( true );
	    XmlNode listXmlNode = parentXmlElement.getChildNode( this.path, false );
	    
	    if( str == null )
	    {
	        if( listXmlNode != null )
	        {
	            listXmlNode.remove();
	        }
	    }
	    else
	    {
	        parentXmlElement.setChildNodeText( this.path, str, false );
	                    
            XmlPath tmpPath = new XmlPath("@ControlOperations", NAMESPACE_RESOLVER);
            listXmlNode = parentXmlElement.getChildNode(tmpPath , false);
            if( listXmlNode != null ){
            	listXmlNode.remove();
            }
                
            tmpPath = new XmlPath("@controloperations", NAMESPACE_RESOLVER);
            listXmlNode = parentXmlElement.getChildNode(tmpPath , false);
            if( listXmlNode != null ) {
            	listXmlNode.remove();
            }
	    }
	}
}