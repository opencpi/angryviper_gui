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

import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.modeling.xml.RootXmlResource;
import org.eclipse.sapphire.ui.SapphireEditor;
import org.eclipse.sapphire.ui.swt.xml.editor.XmlEditorResourceStore;
import org.eclipse.ui.PartInitException;
import org.eclipse.wst.sse.ui.StructuredTextEditor;

import av.proj.ide.internal.OcpiXmlDocScanner;

public class HdlWorkerEditor extends SapphireEditor {
	
	protected StructuredTextEditor xmlSourceEditor;
	protected String      name;
	protected ElementType type;
	protected static OcpiXmlDocScanner docScan = null;
	
	protected static String messageInfo;
	protected static String modificationMessage; 
	protected static String messageHeader;
	
	public HdlWorkerEditor() {
		type = HdlWorker.TYPE;
		name = "HdlWorkerEditorPage";
		//me = this.getClass().toString();
		
		if(docScan == null) {
			messageInfo = "has updated interface elements so they are the proper case for XML presentation."
					+ " If the file is saved, the new format will remain in it."
					+ " This message appears one time per Eclipse session.";
			modificationMessage = "WARNING: The HDL OWD File XML editor " + messageInfo;
			messageHeader = "HDL OWD File XML Modifications";
			docScan = new OcpiXmlDocScanner();
			docScan.setModMessage(name, messageHeader, modificationMessage);
			docScan.addScanElements("controlinterface", "ControlInterface");
			docScan.addScanElements("timeinterface", "TimeInterface");
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
    	RootXmlResource r = new RootXmlResource(xe);
    	docScan.scanAndUpdateXmlIssues(xe);
     	Element element = type.instantiate(r);
    	return element;
    }

}
