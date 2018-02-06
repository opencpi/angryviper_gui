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
import org.eclipse.sapphire.modeling.xml.XmlElement;
import org.eclipse.sapphire.modeling.xml.XmlPath;
import org.eclipse.swt.widgets.Display;

/***
 * This class was apparently written to correct some changes XML.  This used to reside in a
 * class named GenericXmlValueBinding.
 */
public class SpecialDualCaseXmlValueBinding extends BooleanAttributeRemoveIfFalseValueBinding {
	private static boolean signaledFileModMessage = false;
	
	protected void presentModWarning() {
		if(signaledFileModMessage == false) {
			Display.getDefault().asyncExec(new Runnable(){
				public void run() {
					String message = "The Component XML editors are going to programmatically correct XML files when they are openned (type attribute set to certain primitives). Please save these files and make them part of your baseline.";
					MessageDialog.openInformation(Display.getDefault().getActiveShell(), "XML File Modifications", message);
				}
			});
			signaledFileModMessage = true;
		}
	}
	@Override
    public String read()
    {
		String value = super.read();
        
        if (this.name.equals("@Type")) {
        	boolean changeOccured = false;
        	switch(value) {
        	case "Char":
        		write("char");
        		changeOccured = true;
        		break;
        	case "uchar":
        		write("uChar");
        		changeOccured = true;
        		break;
        	case "UChar":
        		write("UChar");
        		break;
        	case "Short":
        		write("short");
        		changeOccured = true;
        		break;
        	case "ushort":
        		write("uShort");
        		changeOccured = true;
        		break;
        	case "UShort":
        		write("uShort");
        		break;
        	case "Long":
        		write("long");
        		changeOccured = true;
        		break;
        	case "ulong":
        		write("uLong");
        		changeOccured = true;
        		break;
        	case "ULong":
        		write("uLong");
        		changeOccured = true;
        		break;
        	case "longlong":
        		write("longLong");
        		changeOccured = true;
        		break;
        	case "LongLong":
        		write("longLong");
        		changeOccured = true;
        		break;
        	case "ulonglong":
        		write("uLongLong");
        		break;
        	case "ULongLong":
        		write("uLongLong");
        		changeOccured = true;
        		break;
        	case "Float":
        		write("float");
        		changeOccured = true;
        		break;
        	case "Double":
        		write("double");
        		changeOccured = true;
        		break;
        	case "Bool":
        		write("bool");
        		changeOccured = true;
        		break;
        	case "String":
        		write("string");
        		changeOccured = true;
        		break;
        	case "Enum":
        		write("enum");
        		changeOccured = true;
        		break;
        	case "Struct":
        		write("struct");
        		changeOccured = true;
        		break;
        	default:
        		break;
        	}
        	if(changeOccured) {
        		presentModWarning();
        	}
        }
        
        return value;
    }

    @Override
    public void write( final String value )
    {
		if (this.lowerName.equals("@type")) {
			
			// Strings, Enums, have additional inputs. Structs may have at one time.
			// This clears those additional attributes when the type field is changed.
        	if (value != null) {
        		super.write(value);
       			if (!value.equals("string") && !value.equals("String")) {
    				XmlPath tmpPath = new XmlPath("@stringLength", resource().getXmlNamespaceResolver());
    				XmlElement element = xml( false );
		        	if (element != null) {
		        		element.removeChildNode(tmpPath);
		        	}
		        	
		        	tmpPath = new XmlPath("@StringLength", resource().getXmlNamespaceResolver());
		            element = xml(false);
		            if( element != null ){
		                element.removeChildNode( tmpPath );
		            }
		                
		            tmpPath = new XmlPath("@stringlength", resource().getXmlNamespaceResolver());
		            element = xml( false );
		            if( element != null ) {
		                element.removeChildNode( tmpPath );
		            }
    			}
    			
    			if (!value.equals("enum") && !value.equals("Enum")) {
    				XmlPath tmpPath = new XmlPath("@enums", resource().getXmlNamespaceResolver());
    				XmlElement element = xml( false );
		        	if (element != null) {
		        		element.removeChildNode(tmpPath);
		        	}
		        	
		        	tmpPath = new XmlPath("@Enums", resource().getXmlNamespaceResolver());
		            element = xml(false);
		            if( element != null ){
		                element.removeChildNode( tmpPath );
		            }
    			}
    			
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
