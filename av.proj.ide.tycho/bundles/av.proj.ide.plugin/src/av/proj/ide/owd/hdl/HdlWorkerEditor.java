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

package av.proj.ide.owd.hdl;

import java.util.ArrayList;

import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.modeling.xml.RootXmlResource;
import org.eclipse.sapphire.ui.swt.xml.editor.XmlEditorResourceStore;
import org.eclipse.ui.PartInitException;
import org.eclipse.wst.sse.ui.StructuredTextEditor;
import org.w3c.dom.Document;

import av.proj.ide.hdl.slot.SlotFileEditor;
import av.proj.ide.internal.OcpiXmlDocScanner;

public class HdlWorkerEditor extends SlotFileEditor {
	
	private static OcpiXmlDocScanner docScan = null;
	
	public HdlWorkerEditor() {
		type = HdlWorker.TYPE;
		name = "HdlWorkerEditorPage";
		if(docScan == null) {
			docScan = new OcpiXmlDocScanner();
			docScan.setEditorName("HDL App Worker OWD Editor");
//			docScan.addScanElements("controlinterface", "ControlInterface");
//			docScan.addScanElements("timeinterface", "TimeInterface");
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
    	XmlEditorResourceStore xe = new XmlEditorResourceStore(this, this.xmlSourceEditor);
    	ArrayList<String> repairs = new ArrayList<String>();
    	Document doc = xe.getDomDocument();
    	//docScan.scanEditorsList(doc, repairs);
    	docScan.checkXiInclude(doc, repairs);
    	xe.validateEdit();
    	
    	RootXmlResource r = new RootXmlResource(xe);
    	Element element = type.instantiate(r);
    	docScan.processModifications(repairs);
    	return element;
    }

}
