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

import static org.eclipse.sapphire.modeling.xml.XmlUtil.createQualifiedName;

import java.util.List;

import javax.xml.namespace.QName;

import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.Property;
import org.eclipse.sapphire.Resource;
import org.eclipse.sapphire.modeling.xml.ChildXmlResource;
import org.eclipse.sapphire.modeling.xml.StandardXmlElementBindingImpl;
import org.eclipse.sapphire.modeling.xml.XmlElement;
import org.eclipse.sapphire.modeling.xml.XmlNamespaceResolver;
import org.eclipse.sapphire.modeling.xml.XmlNode;
import org.eclipse.sapphire.modeling.xml.XmlPath;
import org.eclipse.sapphire.modeling.xml.XmlPath.Segment;
import org.eclipse.sapphire.modeling.xml.XmlResource;

public class CaseInsensitiveSingleElementBinding extends StandardXmlElementBindingImpl {
 
    //private PossibleTypesService possibleTypesService;
    //private Listener possibleTypesServiceListener;
	
	protected QName xmlElementName = null;
    protected XmlElement parentElement = null; 
    protected XmlPath path = null;
    
	protected Property property;
	protected String propertyName;
	
	// Possible Names
	protected String name;
	protected String lowerName;
	protected String camelName;
	protected boolean lowerCaseUsed = false;
	//protected boolean parentStartsUpperCase;
   
	protected void initNames() {
		// Expected to be leading upperCase
        this.name = propertyName;
        this.lowerName = this.name.toLowerCase();
        char c[] = this.name.toCharArray();
        c[1] = Character.toLowerCase(c[1]);
        // If it is a multiple word camel cased this will take a shot at it.
        this.camelName = new String(c);
	}
    protected void getPathToThisProperty() {
        final Element element = property.element();
        final XmlNamespaceResolver xmlNamespaceResolver = ( (XmlResource) element.resource() ).getXmlNamespaceResolver();
        if (parentElement == null) {
        	// pretty mucked up - says a root node could not be read.
        	// TODO: put up a dialog that indicates this file is not interpretable?
        	return;
        }
        
        XmlPath propPath = new XmlPath(this.name , xmlNamespaceResolver);
        XmlNode node = parentElement.getChildNode(propPath, false);
         if(node != null) {
        	this.path = propPath;
        	
        	return;
        }
         
    	propPath = new XmlPath(this.lowerName , xmlNamespaceResolver);
        node = parentElement.getChildNode(propPath, false);
        if(node != null) {
        	this.path = propPath;
        	lowerCaseUsed = true;
        	return;
        }
        
    	propPath = new XmlPath(this.camelName , xmlNamespaceResolver);
        node = parentElement.getChildNode(propPath, false);
        if(node != null) {
        	this.path = propPath;
        	return;
        }

        this.path = propPath = lookForIt(xmlNamespaceResolver);
    }

    protected XmlPath lookForIt(XmlNamespaceResolver xmlResolver) {
        // Try rooting through the existing attributes to see if it's there;
    	List<XmlElement> elements = parentElement.getChildElements();
    	for(XmlElement element : elements) {
    		String elementName = element.getDomNode().getLocalName();
    		String testName = elementName.toLowerCase();
    		
    		if(testName.equals(this.lowerName) ){
    			XmlPath propPath = new XmlPath(elementName , xmlResolver);
    			XmlNode node = parentElement.getChildNode(propPath, false);
    	        if(node != null) {
    	        	return propPath;
    	        }
    		}
        }
		// Else - looks like it doesn't exist yet.
    	return null;
    }
    
   
    @Override
    public void init( final Property property )
    {
        if( property == null )
        {
            throw new IllegalArgumentException();
        }
        
        this.property = property;
        this.propertyName = property.name();
        
        initBindingMetadata();
    }
    
    private void initBindingMetadata()
    {
        if(parentElement == null) {
        	parentElement = parent(false);
        }
        initNames();
    }
    
    /**
     * Creates the XML element name for a type that does not have an explicit mapping. This method can be
     * overridden to provide custom behavior.
     * 
     * @param type the model element type
     * @param xmlNamespaceResolver the resolver of XML namespace suffixes to declared namespaces
     * @return the qualified XML element name for the given model element type
     */
    
//    protected QName createDefaultElementName( final ElementType type, 
//                                              final XmlNamespaceResolver xmlNamespaceResolver )
//    {
//        return XmlUtil.createDefaultElementName( type );
//    }
    
    @Override
    public ElementType type( final Resource resource )
    {
        final XmlElement xmlElement = ( (XmlResource) resource ).getXmlElement();
        final QName xmlElementName = createQualifiedName( xmlElement.getDomNode() );
        if(this.lowerName.equals(xmlElementName.getLocalPart().toLowerCase()) ) {
        	return this.property.definition().getType();
        }
        
        throw new IllegalStateException();
    }

    @Override
    protected Object readUnderlyingObject()
    {
    	if(this.path == null) {
    		getPathToThisProperty();
            if(this.path != null) {
                if( parentElement != null )
                {
                   	XmlNode el = parentElement.getChildNode(this.path, false);
                   	return el;
                }
            }
    		
    	}
    	else {
           	XmlNode el = parentElement.getChildNode(this.path, false);
           	if(el != null) {
           		return el;
           	}
           	else {
           		// Try to find it - covers the case that the tag was changed in source.
           		XmlPath currentPath = this.path;
           		this.path = null;
           		Object isThere = readUnderlyingObject();
           		if(isThere != null) {
           			return isThere;
           		}
           		else {
           			// save the original path.
           			this.path = currentPath;
           		}
           		
           	}
    		
    	}
    	
        if(this.path != null) {
           if( parentElement != null )
           {
              	XmlNode el = parentElement.getChildNode(this.path, false);
              	return el;
           }
         }
         return null;
    }

    @Override
    protected Object createUnderlyingObject( final ElementType type )
    {
    	QName xmlElementName;
    	if(this.path == null) {
    		xmlElementName = new QName( parentElement.getNamespace(), this.name);
    		
    	}
    	else {
    		Segment seg = this.path.getSegment(this.path.getSegmentCount() -1);
    		xmlElementName = seg.getQualifiedName();
    		
    	}
        
        return parentElement.getChildElement( xmlElementName, true );
    }

    @Override
    protected Resource createResource( final Object obj )
    {
        final XmlElement xmlElement = (XmlElement) obj;
        final XmlResource parentXmlResource = (XmlResource) property.element().resource();
        
        return new ChildXmlResource( parentXmlResource, xmlElement );
    }
    
    @Override
    public void remove()
    {
         parentElement.removeChildNode(this.path);
    }

    protected XmlElement parent( final boolean createIfNecessary )
    {
        XmlElement parent = base( createIfNecessary );
        
        if( parent != null && this.path != null )
        {
            parent = (XmlElement) parent.getChildNode( this.path, createIfNecessary );
        }
        
        return parent;
    }
    
    protected XmlElement base( final boolean createIfNecessary )
    {
        final XmlResource resource = (XmlResource) property.element().resource();
        return resource.getXmlElement( createIfNecessary );
    }

    @Override
    public void dispose()
    {
        super.dispose();
    }
}
