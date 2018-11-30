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

import static org.eclipse.sapphire.modeling.xml.XmlUtil.contains;
import static org.eclipse.sapphire.modeling.xml.XmlUtil.createQualifiedName;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.Property;
import org.eclipse.sapphire.modeling.xml.StandardXmlListBindingImpl;
import org.eclipse.sapphire.modeling.xml.XmlElement;
import org.eclipse.sapphire.modeling.xml.XmlNamespaceResolver;
import org.eclipse.sapphire.modeling.xml.XmlResource;
import org.eclipse.sapphire.modeling.xml.annotations.XmlListBinding;
import org.eclipse.sapphire.util.ListFactory;

/***
 * Supports simple plural list elements where the singular element name is 
 * obtained removal or the ending s or changing ies plural to a y. Note
 * the list can have a mix of the element tags - older ones may follow the
 * convention of capitalized name while older ones can be in lower case.
 */
public class MultiCaseXmlListBinding extends StandardXmlListBindingImpl {
	String name = null;
	String lowerName = null;
//	String camelName = null;
	protected XmlElement parentElement;
	Map<String, QName> theseDocElements;	

	protected void initNames(Property p) {
		String name = p.name();
        String genericListElementName = null;
        if(name.endsWith("ies")) {
            genericListElementName = name.substring(0, name.length()-3) + "y";
        }
        else {
            genericListElementName = name.substring(0, name.length()-1);
         }

        this.name = genericListElementName;
        this.lowerName = this.name.toLowerCase();
		theseDocElements = new LinkedHashMap<String, QName>();
        final XmlNamespaceResolver xmlNamespaceResolver = ( (XmlResource) p.element().resource() ).getXmlNamespaceResolver();
        QName qn = createQualifiedName(this.name, xmlNamespaceResolver);
        theseDocElements.put(this.name, qn);
	}
	
	@Override
	protected void initBindingMetadata() {
		super.initBindingMetadata();
		
        final XmlListBinding annotation = property().definition().getAnnotation( XmlListBinding.class );

        if( annotation != null ) {
        	// stuff setup in parent class.
        	return;
        }
		final Property p = property();
		initNames(p);
		final Element parent = property().element();
		parentElement = ( (XmlResource) parent.resource() ).getXmlElement();
		
    	List<XmlElement> elements = parentElement.getChildElements();
    	for(XmlElement element: elements) {
    		String name = element.getDomNode().getLocalName();
    		if(name.toLowerCase().equals(lowerName)) {
	    		if(! theseDocElements.containsKey(name)) {
	                QName xmlElementName = createQualifiedName( element.getDomNode() );
	                theseDocElements.put(name,xmlElementName);
	    		}
    		}
    	}
        ElementType elementType = p.definition().getType();
        int size = theseDocElements.size();
		this.modelElementTypes = new ElementType[size];
		this.xmlElementNames = new QName[ size ];
		int i = 0;
		for(String foundName : theseDocElements.keySet()) {
			this.modelElementTypes[i] = elementType;
			QName qn = theseDocElements.get(foundName);
			this.xmlElementNames[i] = qn;
			i++;
		}
	}
	
    @Override
    protected List<?> readUnderlyingList()
    {
        if( this.xmlElementNames.length == 0 )
        {
            return Collections.emptyList();
        }
        else
        {
            final ListFactory<XmlElement> list = ListFactory.start();
            
            for( XmlElement element : parentElement.getChildElements() )
            {
                final QName xmlElementName = createQualifiedName( element.getDomNode() );
                
                if( contains( this.xmlElementNames, xmlElementName, xmlElementName.getNamespaceURI() ) )
                {
                    list.add( element );
                }
            }
            
            return list.result();
        }
    }

	
    @Override
    protected Object insertUnderlyingObject( final ElementType type,
                                             final int position )
    {
        QName xmlElementName = this.xmlElementNames[ indexOf( this.modelElementTypes, type ) ];
        
        if( xmlElementName.getNamespaceURI().equals( "" ) )
        {
            xmlElementName = xmlElementNames[0];
        }
        
        final List<?> list = readUnderlyingList();
        final XmlElement refXmlElement = (XmlElement) ( position < list.size() ? list.get( position ) : null );
        return parentElement.addChildElement( xmlElementName, refXmlElement );
    }
    
    protected int indexOf(ElementType[] arr, ElementType type) {
    	for(int i=0; i<arr.length; ++i) {
            if((arr[i].getQualifiedName()).equals(type.getQualifiedName())) {
                return i;
            }
        }
        
        throw new IllegalArgumentException();
    }
}
