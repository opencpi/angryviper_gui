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

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.sapphire.modeling.xml.StandardXmlValueBindingImpl;
import org.eclipse.sapphire.modeling.xml.XmlElement;
import org.eclipse.sapphire.modeling.xml.XmlNode;
import org.eclipse.sapphire.modeling.xml.XmlPath;
import org.eclipse.swt.widgets.Display;

public class InstanceComponentValueXmlBinding extends StandardXmlValueBindingImpl {
	private String existing = "";
	private boolean compChanged = false;
	
	@Override
    public String read()
    {
        String value = null;
        final XmlElement element = xml( false );
        this.path = new XmlPath("@component" , resource().getXmlNamespaceResolver());
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
                	XmlPath tmpPath = new XmlPath("@Component" , resource().getXmlNamespaceResolver());
                	value = element.getChildNodeText( tmpPath );
                }
            }
        }
        boolean isNotNamed = false;
		XmlPath tmpPath = new XmlPath("@Name", resource().getXmlNamespaceResolver());
		XmlNode node = xml(false).getChildNode(tmpPath, false);
		if (node == null) {
			tmpPath = new XmlPath("@name", resource().getXmlNamespaceResolver());
			node = xml(false).getChildNode(tmpPath, false);
			if (node == null) {
				isNotNamed = true;
			}
		}
        
        if (!existing.equals(value)) {
        	if (!existing.equals("")) {
        		compChanged = true;
        	}
 	    	String[] split = value.split("\\.");
	    	if (split.length > 0) {
	    		if(isNotNamed)
	    			presentModWarning();
	    		writeName(split[split.length-1]);
	    	}
        }
        
        existing = value;
        
        return value;
    }
	
	private static boolean signaledFileModMessage = false;
	
	protected void presentModWarning() {
		if(signaledFileModMessage == false) {
			Display.getDefault().asyncExec(new Runnable(){
				public void run() {
					String message = 
					"The Application XML editor programmatically modifies OAS XML files to support presentation (name attribute is added to instance elements)."
					+ "\n - These changes are cosmetic and do not impact XML functionality in the Framework."
					+ "\n - The default names chosen by the editor may be changed."
					+ "\n - XML files opened just to be viewed do not need to be saved.";
					MessageDialog.openInformation(Display.getDefault().getActiveShell(), "XML File Modifications", message);
				}
			});
			signaledFileModMessage = true;
		}
	}
	
	private void writeName( final String value ) {
		XmlPath tmpPath = new XmlPath("@Name", resource().getXmlNamespaceResolver());
		XmlNode node = xml(false).getChildNode(tmpPath, false);

		if (node == null) {
			tmpPath = new XmlPath("@name", resource().getXmlNamespaceResolver());
			node = xml(false).getChildNode(tmpPath, false);
			if (node == null) {
				xml(true).setChildNodeText(tmpPath, value, this.removeNodeOnSetIfNull);
			} else {
				if (!node.getText().equals(value) && this.compChanged) {
					xml(true).setChildNodeText(tmpPath, value, this.removeNodeOnSetIfNull);
					this.compChanged = false;
				}
			}
		} else {
			if (!node.getText().equals(value) && this.compChanged) {
				xml(true).setChildNodeText(tmpPath, value, this.removeNodeOnSetIfNull);
				this.compChanged = false;
			}
		}
	}

    @Override
    public void write( final String value )
    {
    	this.path = new XmlPath("@component" , resource().getXmlNamespaceResolver());
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
            
            XmlPath tmpPath = new XmlPath("@Component", resource().getXmlNamespaceResolver());
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
