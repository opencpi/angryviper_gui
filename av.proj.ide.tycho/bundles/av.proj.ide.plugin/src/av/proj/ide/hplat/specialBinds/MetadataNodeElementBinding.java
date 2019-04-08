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

/**
 * This Binding class is responsible for binding the model property from Metadata.java class. 
 */

public class MetadataNodeElementBinding extends BooleanNodePresentBinding{
	
	@Override
	protected void initNames() {
		this.name = "MetaData";
		this.lowerName = "metadata";
		this.camelName ="";
	}
	

	@Override
	public void write( final String value )
	    {
	         if (value.equals("true")) {
				
				
				if( this.path == null) {
			        this.path = new XmlPath(this.name , resource().getXmlNamespaceResolver());
				}
				
	    		xml( true ).setChildNodeText( this.path, "", false );
	    		
	    		XmlNode node = xml(true).getChildNode(this.path, false);
	    	    String nameCase = node.toString();
	    	    nameCase = nameCase.replaceAll("[^a-zA-Z]+","");
	    		
	    		if(nameCase.equals(name)){
	    		    xml(true).getChildElement(this.name, false).setChildNodeText("@Master", value, true);
	    		}
	    		if(nameCase.equals(lowerName)){
	    		    xml(true).getChildElement(this.lowerName, false).setChildNodeText("@Master", value, true);
	    		}
	    		
	    		
	    	} else {
	    		XmlElement element = xml( false );
	        	if (element != null) {
	        		element.removeChildNode(this.path);

	        	}
	    	}
	    }
	    

}
