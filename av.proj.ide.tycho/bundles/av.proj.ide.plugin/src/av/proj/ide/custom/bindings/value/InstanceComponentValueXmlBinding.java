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

import org.eclipse.sapphire.modeling.xml.XmlNode;
import org.eclipse.sapphire.modeling.xml.XmlPath;

import av.proj.ide.internal.AngryViperAssetService;
import av.proj.ide.internal.OpencpiEnvService;
import av.proj.ide.internal.UiComponentSpec;

/***
 * Requirements
 * 1. Applies to the Instance element component attribute.
 * 2. Must be able to find it in lower case and leading capital C.
 * 3. If the component attribute is found when reading the document
 * 	  the name needs to be checked. If it doesn't exist write it out
 * 	  (UI wrongly uses it in the display) if it does exist, keep the
 *    existing name.
 * 4. This should try to preserve the case used in the original document.
 * 5. ANGRYVIPER convention: elements an attribute are capitalized.   
 * 6. If the user changes the instance component, Sapphire first write
 *    out the attribute then read is called to update the diagram. 
 *    This change must be detected and a new name applied.
 * 7. Dealing with components in the various XML documents is tricky.
 *    They are referred to in various way.  As of Rel 1.4 the UI makes
 *    a more readable and common name for respective displays. The OAS
 *    XML uses the fully qualified component name 
 *    (library package-Id.componentName). This name must be written
 *    as the component value.
 * 8. The diagram editor lists component selections by the component name.
 * 9. Drag-and-drop acts on the component file name and the instance
 *    write passes in the fully qualified name.  Write must cover both
 *    cases.   
 */
public class InstanceComponentValueXmlBinding extends GenericDualCaseXmlValueBinding {
	private XmlPath namePath = null;
	String priorComponent = null;
	
	@Override
    public String read()
    {
		String value = super.read();
		if(value != null) {
			if(priorComponent == null) {
				// First read.
				priorComponent = value;
			}
			String name = getName();
			if (name == null) {
				// It didn't exist before; write it out.
				writeName(value);
			}
			else if( ! value.equals(priorComponent) ){
				// The user changed the component. Update the name in the 
				// diagram.
				writeName(value);
				priorComponent = value;
			}
		}
        
        return value;
    }
	
	/***
	 * Must deal with the case of name as it does the case of component. 
	 */
	private String getName() {
		String name = null;
		XmlNode node;
		if(namePath == null) {
			XmlPath ucPath = new XmlPath("@Name", resource().getXmlNamespaceResolver());
			node = xml(false).getChildNode(ucPath, false);
			if (node == null) {
				namePath = new XmlPath("@name", resource().getXmlNamespaceResolver());
				node = xml(false).getChildNode(namePath, false);
				if(node == null) {
	  				namePath = ucPath;
	  				return null;
				}
			}
			else {
				// Default to upper case.
				namePath = ucPath;
			}
		}
		node = xml(false).getChildNode(namePath, false);
		if(node != null) {
			name = node.getText();
		}
		return name;
	}

	/***
	 * Two points of entry: the editor and drag-and-drop.
	 */
	private String writeName( final String value ) {
    	UiComponentSpec spec = AngryViperAssetService.getInstance()
    			               .getEnvironment().getUiSpecByDisplayName(value);
    	String oasComp = null;
    	if(spec != null) {
    		oasComp = spec.getComponentName();
    	}
    	else {
    		// This is used to deal with the "nothing" component.
        	String[] split = value.split("\\.");
        	if (split.length > 0) {
        		oasComp = split[split.length-1];
        	}
    	}
		XmlNode node = xml(false).getChildNode(namePath, false);
		if (node == null) {
			// we're adding it.
			xml(true).setChildNodeText(namePath, oasComp, this.removeNodeOnSetIfNull);
		}
		else {
			// It's being updated
			xml(true).setChildNodeText(namePath, oasComp, this.removeNodeOnSetIfNull);
		}
		return oasComp;
	}

    @Override
    public void write( final String value )
    {
    	// There are two paths to write.  Once it view instance comp selction in the editor
    	// the other is through drag & drop.  Drag and Drop passes the oasReference of the component.
    	OpencpiEnvService service = AngryViperAssetService.getInstance().getEnvironment();
    	UiComponentSpec spec = service.getUiSpecByDisplayName(value);
    	if(spec != null) {
    		// Editor operation
        	super.write(spec.getOasReference());
     	}
    	else {
    		// Drag/drop operation.
        	super.write(value);
    	}
   }

}
