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

import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.modeling.xml.StandardXmlValueBindingImpl;
import org.eclipse.sapphire.modeling.xml.XmlAttribute;
import org.eclipse.sapphire.modeling.xml.XmlElement;
import org.eclipse.sapphire.modeling.xml.XmlNamespaceResolver;
import org.eclipse.sapphire.modeling.xml.XmlNode;
import org.eclipse.sapphire.modeling.xml.XmlPath;
import org.eclipse.sapphire.modeling.xml.XmlResource;

/***
 * This was developed to support tag attributes. Since the XML files used by open cpi
 * are not standardized xml, binding needs to deal with multiple styles of case. 
 * This class will try to find the attribute as the in the following cases:
 *  - the case of the property name (in the Interface definition), lower case,
 *  camel case and finally a search.
 *  
 *  Over the last year, the Framework has adopted in standard name case: Leading capitol
 *  letter and Camel case is used for multiple word names.
 */
public class CaseInsenitiveAttributeValueBinding extends StandardXmlValueBindingImpl {
	protected String propertyName;
	protected String name;
	protected String lowerName;
	protected String camelName;
	protected boolean parentStartsUpperCase;
	protected XmlElement parentElement;
	
	protected void getPropertyName() {
        final Value<?> property = (Value<?>) property();
        propertyName = property.name();
	}
	
	protected void initNames() {
        this.name = "@"+propertyName;  // Sapphire identifies an attribute with a leading @ character.
        this.lowerName = this.name.toLowerCase();
        char c[] = this.name.toCharArray();
        c[1] = Character.toLowerCase(c[1]);
        // If it is a multiple word camel cased this will take a shot at it.
        this.camelName = new String(c);
		
	}
	@Override
	protected void initBindingMetadata()
    {
		super.initBindingMetadata();
		this.removeNodeOnSetIfNull=true;
		getPropertyName();
 		initNames();
		
		final Element parent = property().element();
		parentElement = ( (XmlResource) parent.resource() ).getXmlElement();
		if(parentElement != null) {
		String parentName = parentElement.getLocalName();
		char chr = parentName.charAt(0);
			if(Character.isLowerCase(chr)) {
				parentStartsUpperCase = false;
			}
			else {
				parentStartsUpperCase = true;
			}
		}
        this.path = null;
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
         return null;
    }
    
    protected void getPathToThisProperty() {
    	XmlNamespaceResolver xmlResolver = resource().getXmlNamespaceResolver();
    	final XmlElement element = xml( false );
        if (element == null) {
        	// pretty mucked up - says a root node could not be read.
        	// TODO: put up a dialog that indicates this file is not interpretable?
        	return;
        }
        
        XmlPath propPath = new XmlPath(this.name , xmlResolver);
        XmlNode node = parentElement.getChildNode(propPath, false);
         if(node != null) {
        	this.path = propPath;
        	return;
        }
         
    	propPath = new XmlPath(this.lowerName , xmlResolver);
        node = parentElement.getChildNode(propPath, false);
        if(node != null) {
        	this.path = propPath;
        	return;
        }
        
    	propPath = new XmlPath(this.camelName , xmlResolver);
        node = parentElement.getChildNode(propPath, false);
        if(node != null) {
        	this.path = propPath;
        	return;
        }

        this.path = propPath = lookForIt(xmlResolver);
    }

    protected XmlPath lookForIt(XmlNamespaceResolver xmlResolver) {
        // Try rooting through the existing attributes to see if it's there;
    	
    	List<XmlAttribute> attrs = parentElement.getAttributes();
    	String lowercasePropName = propertyName.toLowerCase();
    	for(XmlAttribute attribute : attrs) {
    		String attributeName = attribute.getDomNode().getLocalName();
    		String testName = attributeName.toLowerCase();
    		
    		if(testName.equals(lowercasePropName) ){
    			XmlPath propPath = new XmlPath("@"+attributeName , xmlResolver);
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
    public void write( final String value )
    {
    	if(this.path == null) {
    		// If this is a new instance of this attribute, default it to property name.
            // TODO: In case the attribute doesn't exist- want to follow the trend
            // of the document i.e., use lower case if most or the file uses it.
    		
    		this.path = new XmlPath(this.name , resource().getXmlNamespaceResolver());
    	}
    	super.write(value);
    }
}
