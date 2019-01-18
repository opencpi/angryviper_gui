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

package av.proj.ide.hdl.slot;

import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.ElementList;
import org.eclipse.sapphire.modeling.xml.RootXmlResource;
import org.eclipse.sapphire.ui.swt.xml.editor.XmlEditorResourceStore;

import av.proj.ide.hdl.signal.Signal;
import av.proj.ide.hdl.signal.SignalsFileEditor;

public class SlotFileEditor extends SignalsFileEditor {
	
	public SlotFileEditor () {
		type = SlotType.TYPE;
		name = "SlotFileEditorPage";
		modificationMessage = "The Slot File XML editor " + messageInfo;
		messageHeader = "Signal Definition XML Modifications";
		me = this.getClass().toString();
	}
	
	@Override
	protected void updateSignalDefinitions(Element element) {
    	SlotType fileElement = (SlotType)element;
    	ElementList<Signal> signals = fileElement.getSignals();
    	modifyOldAttributes(signals);
	}
    
    @Override
    protected Element createModel() 
    {
    	Element element = type.instantiate(new RootXmlResource(new XmlEditorResourceStore(this, this.xmlSourceEditor)));
    	updateSignalDefinitions(element);
    	return element;
    }
}
