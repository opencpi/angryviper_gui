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
import static org.eclipse.sapphire.modeling.xml.XmlUtil.equal;

import java.util.Collections;
import java.util.List;

import javax.xml.namespace.QName;

import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.Resource;
import org.eclipse.sapphire.modeling.xml.ChildXmlResource;
import org.eclipse.sapphire.modeling.xml.StandardXmlListBindingImpl;
import org.eclipse.sapphire.modeling.xml.XmlElement;
import org.eclipse.sapphire.modeling.xml.XmlNamespaceResolver;
import org.eclipse.sapphire.modeling.xml.XmlPath;
import org.eclipse.sapphire.modeling.xml.XmlResource;
import org.eclipse.sapphire.util.ListFactory;

import av.proj.ide.ops.Operation;

public class OPSOperationXmlListBinding extends StandardXmlListBindingImpl {
	
	@Override
	protected void initBindingMetadata() {
		super.initBindingMetadata();
        final XmlNamespaceResolver xmlNamespaceResolver = ( (XmlResource) property().element().resource() ).getXmlNamespaceResolver();
		this.path = new XmlPath( "", xmlNamespaceResolver );
		this.modelElementTypes = new ElementType[2];
		this.modelElementTypes[0] = new ElementType(Operation.class);
		this.modelElementTypes[1] = new ElementType(Operation.class);
		this.xmlElementNames = new QName[ this.modelElementTypes.length ];            

		if (this.xmlElementNames.length == 2) {
			this.xmlElementNames[0] = createQualifiedName("Operation", xmlNamespaceResolver);
			if (this.xmlElementNames[0] == null) {
				final ElementType type = this.modelElementTypes[0];
				this.xmlElementNames[0] = createDefaultElementName(type, xmlNamespaceResolver);
			}
		
			this.xmlElementNames[1] = createQualifiedName("operation", xmlNamespaceResolver);
			if (this.xmlElementNames[1] == null) {
				final ElementType type = this.modelElementTypes[1];
				this.xmlElementNames[1] = createDefaultElementName(type, xmlNamespaceResolver);
			}
		}
	}
	
	@Override
    public ElementType type( final Resource resource )
    {
        final XmlElement xmlElement = ( (XmlResource) resource ).getXmlElement();
        final QName xmlElementName = createQualifiedName( xmlElement.getDomNode() );
        final String xmlElementNamespace = xmlElementName.getNamespaceURI();
        
        for( int i = 0; i < this.xmlElementNames.length; i++ )
        {
            if( equal( this.xmlElementNames[ i ], xmlElementName, xmlElementNamespace ) )
            {
                return this.modelElementTypes[ i ];
            }
        }
        
        throw new IllegalStateException();
    }

    @Override
    protected Resource resource( final Object obj )
    {
        final XmlElement xmlElement = (XmlElement) obj;
        final XmlResource parentXmlResource = (XmlResource) property().element().resource();
        
        return new ChildXmlResource( parentXmlResource, xmlElement );
    }

    @Override
    protected List<?> readUnderlyingList()
    {
        final XmlElement parent = getXmlElement( false );
        
        if( parent == null )
        {
            return Collections.emptyList();
        }
        else
        {
            final ListFactory<XmlElement> list = ListFactory.start();
            
            for( XmlElement element : parent.getChildElements() )
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
        final XmlElement parent = getXmlElement( true );
        QName xmlElementName = this.xmlElementNames[ indexOf( this.modelElementTypes, type ) ];
        
        if( xmlElementName.getNamespaceURI().equals( "" ) )
        {
            xmlElementName = new QName( parent.getNamespace(), xmlElementName.getLocalPart() );
        }
        
        final List<?> list = readUnderlyingList();
        final XmlElement refXmlElement = (XmlElement) ( position < list.size() ? list.get( position ) : null );
        
        return parent.addChildElement( xmlElementName, refXmlElement );
    }
    
    private int indexOf(ElementType[] arr, ElementType type) {
    	for(int i=0; i<arr.length; ++i) {
            if((arr[i].getQualifiedName()).equals(type.getQualifiedName())) {
                return i;
            }
        }
        
        throw new IllegalArgumentException();
    }
    
    @Override
    public void move( final Resource resource, 
                      final int position )
    {
        final List<?> list = readUnderlyingList();
        final XmlElement xmlElement = ( (ChildXmlResource) resource ).getXmlElement();
        final XmlElement refXmlElement = (XmlElement) ( position < list.size() ? list.get( position ) : null );
        
        xmlElement.move( refXmlElement );
    }

    @Override
    public void remove( final Resource resource )
    {
        final XmlResource xmlResource = (XmlResource) resource;
        final XmlElement xmlElement = xmlResource.getXmlElement();
        
        xmlElement.remove();
        
        if( this.path != null )
        {
            final XmlElement base = getBaseXmlElement( false );
            
            if( base != null )
            {
                final XmlElement parent = (XmlElement) base.getChildNode( this.path, false );
                
                if( parent != null && parent.isEmpty() && !base.isEmpty())
                {
                    base.removeChildNode( this.path );
                }
            }
        }
    }

    protected XmlElement getXmlElement( final boolean createIfNecessary )
    {
        XmlElement parent = getBaseXmlElement( createIfNecessary );
        
        if( parent != null && this.path != null )
        {
            parent = (XmlElement) parent.getChildNode( this.path, createIfNecessary );
        }
        
        return parent;
    }
    
    protected XmlElement getBaseXmlElement( final boolean createIfNecessary )
    {
        final XmlResource resource = (XmlResource) property().element().resource();
        return resource.getXmlElement( createIfNecessary );
    }
    
    @Override
    public void dispose()
    {
        super.dispose();
    }
}
