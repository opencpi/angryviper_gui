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

import org.eclipse.sapphire.modeling.xml.StandardXmlValueBindingImpl;
import org.eclipse.sapphire.modeling.xml.XmlElement;
import org.eclipse.sapphire.modeling.xml.XmlNode;
import org.eclipse.sapphire.modeling.xml.XmlPath;

public class HdlWorkerNameValueXmlBinding extends StandardXmlValueBindingImpl {
	
	@Override
    public String read()
    {
        String value = null;
        final XmlElement element = xml( false );
        this.path = new XmlPath("@name" , resource().getXmlNamespaceResolver());
        if( element != null )
        {
            if( this.treatExistanceAsValue )
            {
                final boolean exists = ( element.getChildNode( this.path, false ) != null );
                value = ( exists ? this.valueWhenPresent : this.valueWhenNotPresent );
            }
            else if( this.path == null )
            {
                value = element.getText();
            }
            else
            {
                value = element.getChildNodeText( this.path );
                if (value.equals("")) {
                	XmlPath tmpPath = new XmlPath("@Name" , resource().getXmlNamespaceResolver());
                	value = element.getChildNodeText( tmpPath );
            		// no name attribute.  assume worker value is name.
            		// this is mainly for backward compatibility
                    this.path = new XmlPath("@worker" , resource().getXmlNamespaceResolver());
                    if( element != null )
                    {
                        if( this.treatExistanceAsValue )
                        {
                            final boolean exists = ( element.getChildNode( this.path, false ) != null );
                            value = ( exists ? this.valueWhenPresent : this.valueWhenNotPresent );
                        }
                        else if( this.path == null )
                        {
                            value = element.getText();
                        }
                        else
                        {
                            value = element.getChildNodeText( this.path );
                            if (value.equals("")) {
                            	tmpPath = new XmlPath("@Worker" , resource().getXmlNamespaceResolver());
                            	value = element.getChildNodeText( tmpPath );
                            }
                        }
                    }
                    // create the "name" attribute
                    write(value);
                }
            }
        }
        
        return value;
    }

    @Override
    public void write( final String value )
    { 	
    	this.path = new XmlPath("@name" , resource().getXmlNamespaceResolver());
        if( this.treatExistanceAsValue )
        {
            final boolean nodeShouldBePresent = this.valueWhenPresent.equals( value );
            
            if( nodeShouldBePresent )
            {
                xml( true ).getChildNode( this.path, true );
            }
            else
            {
                final XmlElement element = xml( false );
                
                if( element != null )
                {
                    element.removeChildNode( this.path );
                }
            }
        }
        else if( this.path == null )
        {
            xml( true ).setText( value );
        }
        else
        {
            xml( true ).setChildNodeText( this.path, value, this.removeNodeOnSetIfNull );
            
            XmlPath tmpPath = new XmlPath("@Name", resource().getXmlNamespaceResolver());
            final XmlElement element = xml( false );
                
                if( element != null )
                {
                    element.removeChildNode( tmpPath );
                }
        }
    }

    @Override
    public XmlNode getXmlNode()
    {
        final XmlElement element = xml( false );
        if( element != null )
        {
            return element.getChildNode( this.path, false );   
        }
        
        return null;
    }

}
