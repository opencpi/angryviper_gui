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

import org.eclipse.sapphire.modeling.xml.XmlElement;

public class BooleanAttributeRemoveIfFalseValueBinding extends CaseInsenitiveAttributeValueBinding {
 
	@Override
    public String read()
    {
    	String value = super.read();
    	
    	// This hack deals with the fact that the framework takes
    	// boolean attributes true, or 1. Some of the older
    	// spec files use 1 and the editor would show it in error.
    	
    	if("1".equals(value)) {
    		return "true";
    	}
    	else if("0".equals(value)) {
    		return "false";
    	}
    	return value;
    }
	
    @Override
    public void write( final String value )
    {
		if ("false".equals(value)) {
    		final XmlElement element = xml( false );
			element.removeChildNode( this.path );
    	} else {
    		super.write(value);
    	}
    }
}
