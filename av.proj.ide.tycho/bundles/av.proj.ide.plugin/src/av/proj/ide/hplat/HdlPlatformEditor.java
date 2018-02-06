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

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.ElementList;
import org.eclipse.sapphire.modeling.xml.RootXmlResource;
import org.eclipse.sapphire.ui.SapphireEditor;
import org.eclipse.sapphire.ui.swt.xml.editor.XmlEditorResourceStore;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PartInitException;
import org.eclipse.wst.sse.ui.StructuredTextEditor;

import av.proj.ide.common.Signal;
import av.proj.ide.common.SignalDirection;

public class HdlPlatformEditor extends SapphireEditor {
	
	private StructuredTextEditor hplatSourceEditor;
	protected static boolean signaledFileModMessage = false;
	
	protected void presentModWarning() {
		if(signaledFileModMessage == false) {
			Display.getDefault().asyncExec(new Runnable(){
				public void run() {
					String message = "The HDL Platform XML editor is going to programmatically add an external signal tag to support presentation of existing platform files. Please save these files and make them part of your baseline.";
					MessageDialog.openInformation(Display.getDefault().getActiveShell(), "HDL Platform XML File Modifications", message);
				}
			});
			signaledFileModMessage = true;
		}
	}
    
	@Override
    protected void createEditorPages() throws PartInitException 
    {
        addDeferredPage( "Design", "HdlPlatformEditorPage" );
        
        this.hplatSourceEditor = new StructuredTextEditor();
        this.hplatSourceEditor.setEditorPart(this);
        
        int index = addPage( this.hplatSourceEditor, getEditorInput() );
        setPageText( index, "Source" );
    }
    
    @Override
    protected Element createModel() 
    {
    	Element element = HdlPlatform.TYPE.instantiate(new RootXmlResource(new XmlEditorResourceStore(this, this.hplatSourceEditor)));
    	HdlPlatform hdlp = (HdlPlatform)element;
    	ElementList<Signal> signals = hdlp.getSignals();

    	/**
    	 * Backward compatibility with older signal definitions.  Current UI depends on the direction attribute.
    	 */
    	for(Signal signal : signals) {
    		SignalDirection sd = signal.getDirection().content();
    		if( ! (sd == null || sd == SignalDirection.NOTSET) )
    			continue;
    		
    		if(signal.getInput().content() != null) {
    			signal.setDirection(SignalDirection.INPUT);
    		}
    		else if(signal.getOutput().content() != null) {
    			signal.setDirection(SignalDirection.OUTPUT);
    		}
    		else if(signal.getInout().content() != null) {
    			signal.setDirection(SignalDirection.INOUT);
    		}
    		else if(signal.getBidirectional().content() != null) {
    			signal.setDirection(SignalDirection.BIDIRECTIONAL);
    		}
    		presentModWarning();
    	}
    	return element;
    }
}
