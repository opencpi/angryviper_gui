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

package av.proj.ide.hplat.specialBinds;

import org.eclipse.sapphire.modeling.xml.XmlElement;
import org.eclipse.sapphire.modeling.xml.XmlNode;
import org.eclipse.sapphire.modeling.xml.XmlPath;

import av.proj.ide.custom.bindings.value.BooleanNodePresentBinding;

public class CpMasterNodeElementBinding extends BooleanNodePresentBinding {
	String masterName = "Master";
	
	@Override
	protected void initNames() {
        this.name = "CpMaster";
        this.lowerName = "cpmaster";
        this.camelName = "Cpmaster";
	}
	@Override
	protected void initBindingMetadata()
    {
		super.initBindingMetadata();
		if(! parentStartsUpperCase) {
			masterName = "master";
		}
    }
	
    @Override
    public void write( final String value )
    {
		if (value.equals("true")) {
			
			// There are only two updates possible, add or remove. It it was newly added then the path
			// won't exist--put the node in place using camel-case convention and preserve that path.
			// If the user un-checks it the original path is preserved so a re-add with you the original
			// path (and this style).
			if( this.path == null) {
				if(parentStartsUpperCase) {
					this.path = new XmlPath(this.name , resource().getXmlNamespaceResolver());
				}
				else {
			        this.path = new XmlPath(this.lowerName , resource().getXmlNamespaceResolver());
				}
			}
			XmlElement element = xml( true );
			XmlNode node = element.getChildNode( this.path, true );
			// Need to add the master=true attribute
			String name = node.getDomNode().getLocalName();
			XmlElement newElem = element.getChildElement(name, false);
			if(newElem != null) {
				XmlPath tmpPath = new XmlPath("@" + masterName, resource().getXmlNamespaceResolver());
				newElem.setChildNodeText(tmpPath, value, false);
			}
		} else {
    		XmlElement element = xml( false );
        	if (element != null) {
        		element.removeChildNode(this.path);
        	}
    	}
    }

}
