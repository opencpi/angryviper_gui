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

package av.proj.ide.hdl.signal;

import java.util.ArrayList;

import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.ElementList;
import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.modeling.xml.RootXmlResource;
import org.eclipse.sapphire.ui.SapphireEditor;
import org.eclipse.sapphire.ui.swt.xml.editor.XmlEditorResourceStore;
import org.eclipse.ui.PartInitException;
import org.eclipse.wst.sse.ui.StructuredTextEditor;

import av.proj.ide.internal.OcpiXmlDocScanner;

public class SignalsFileEditor extends SapphireEditor {
	
	protected StructuredTextEditor xmlSourceEditor;
	protected String      name;
	protected ElementType type;
	private static OcpiXmlDocScanner docScan = null;
	
	public SignalsFileEditor() {
		type = Signals.TYPE;
		name = "SignalsFileEditorPage";
		if(docScan == null) {
			docScan = new OcpiXmlDocScanner();
			docScan.setEditorName("HDL Signals File Editor");
			docScan.setShowXTimes(2);
		}
	}
	
    
	@Override
    protected void createEditorPages() throws PartInitException 
    {
        addDeferredPage( "Design", name );
        this.xmlSourceEditor = new StructuredTextEditor();
        this.xmlSourceEditor.setEditorPart(this);
        int index = addPage( this.xmlSourceEditor, getEditorInput() );
        setPageText( index, "Source" );
    }
	
    
    @Override
    protected Element createModel() 
    {
    	Element element = type.instantiate(new RootXmlResource(new XmlEditorResourceStore(this, this.xmlSourceEditor)));
    	Signals fileElement = (Signals)element;
    	ElementList<DeviceSignal> signals = fileElement.getSignals();
       	ArrayList<String> repairs = new ArrayList<String>();
    	docScan.scanDeviceSignalElements(signals, repairs);
    	docScan.processModifications(repairs);
    	return element;
    }
}
