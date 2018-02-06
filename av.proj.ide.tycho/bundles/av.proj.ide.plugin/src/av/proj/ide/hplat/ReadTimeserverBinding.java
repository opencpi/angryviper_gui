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

package av.proj.ide.hplat;

import java.util.List;

import org.eclipse.sapphire.modeling.xml.StandardXmlValueBindingImpl;
import org.eclipse.sapphire.modeling.xml.XmlElement;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

public class ReadTimeserverBinding extends StandardXmlValueBindingImpl {
	
	@Override
	protected void initBindingMetadata()
    {
		super.initBindingMetadata();
        this.path = null;
        
    }
	
	@Override
    public String read()
    {
		String value = "false";
		// Get the root node.
        final XmlElement element = xml( true );
        // look for device child elements.
		List<XmlElement> deviceElements = element.getChildElements("device");
		boolean timerServerPresent = findTimerServer(deviceElements);
		
		if (timerServerPresent)
			return "true";
		
		deviceElements = element.getChildElements("Device");
		timerServerPresent = findTimerServer(deviceElements);
		if (timerServerPresent) 
			return "true";
		
        return value;
    }
	
	protected boolean findTimerServer(List<XmlElement> deviceElements) {
		for(XmlElement element : deviceElements) {
        	Node theNode = element.getDomNode();
        	NamedNodeMap attributes = theNode.getAttributes();
        	Node worker = attributes.getNamedItem("worker");
        	if(worker != null) {
            	String text = worker.getNodeValue();
            	if("time_server".equals(text.toLowerCase())) {
                  	return true;
            	}
        	}
		}
		return false;
	}

}
