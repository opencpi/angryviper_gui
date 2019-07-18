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

import org.eclipse.sapphire.modeling.xml.XmlPath;

import av.proj.ide.custom.bindings.value.CaseInsenitiveAttributeValueBinding;

/***
 * This special binding is need to deal with the disconnected signal
 * seen in XML by Platform='' attribute.
  */
public class SlotSignalPlatformBinding extends CaseInsenitiveAttributeValueBinding {
	
	@Override
	protected void initBindingMetadata()
    {
		super.initBindingMetadata();
		this.removeNodeOnSetIfNull=false;
    }
	
	
    @Override
    public String read()
    {
    	if(this.path == null)
    		getPathToThisProperty();
    	
        if(this.path != null) {
        	// it's there
        	String value = super.read();
        	if(value != null && value.isEmpty()) {
        		return "<disconnected>";
        	}
        	else {
        		return value;
        	}
        }
         return null;
    }
    

    @Override
    public void write( final String value )
    {
    	if(this.path == null) {
    		// If this is a new instance of this attribute, default it to property name.
    		this.path = new XmlPath(this.name , resource().getXmlNamespaceResolver());
    	}
    	if(value == null) {
    		super.write("");
    	}
    	else {
    		super.write(value);
    	}
    }
    
}
