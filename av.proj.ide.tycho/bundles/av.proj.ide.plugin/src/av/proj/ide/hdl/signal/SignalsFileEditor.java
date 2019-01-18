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

import java.util.HashSet;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.ElementList;
import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.modeling.xml.RootXmlResource;
import org.eclipse.sapphire.ui.SapphireEditor;
import org.eclipse.sapphire.ui.swt.xml.editor.XmlEditorResourceStore;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PartInitException;
import org.eclipse.wst.sse.ui.StructuredTextEditor;

public class SignalsFileEditor extends SapphireEditor {
	
	protected StructuredTextEditor xmlSourceEditor;
	protected String      name;
	protected ElementType type;
	protected String      me;
	protected HashSet<String> modMessages = new HashSet<String>();
	
	protected String messageInfo = "has programmatically updated this files XML to use the current signal name and direction attributes."
			+ " Any signals modified or added to this file will use this convention. If the file is saved, the new format will remain it it."
			+ " This message appears one time per Eclipse session.";

	protected String modificationMessage; 
	protected String messageHeader;
	
	public SignalsFileEditor() {
		type = Signals.TYPE;
		name = "SignalsFileEditorPage";
		modificationMessage = "The Signals File XML editor " + messageInfo;
		messageHeader = "Signal Definition XML Modifications";
		me = this.getClass().toString();
	}
	
	protected void presentModWarning() {
		Display.getDefault().asyncExec(new Runnable(){
			public void run() {
				MessageDialog.openInformation(Display.getDefault().getActiveShell(), messageHeader, modificationMessage);
			}
		});
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
	
	protected void modifyOldAttributes(ElementList<Signal> signals) {
    	/**
    	 * For simplicity the UI currently present signal definitions using
    	 * the current attribute set defining a name and a direction.
    	 */
		boolean changed = false;
    	for(Signal signal : signals) {
    		String name = signal.getName().content();
    		if( name != null )
    			continue;
    		
    		changed = true;
    		if(signal.getInput().content() != null) {
    			signal.setDirection(SignalDirection.input);
    			signal.setName(signal.getInput().content());
    			signal.setInput(null);
    		}
    		else if(signal.getOutput().content() != null) {
    			signal.setDirection(SignalDirection.output);
    			signal.setName(signal.getOutput().content());
    			signal.setOutput(null);
	   		}
	    		else if(signal.getInout().content() != null) {
	    			signal.setDirection(SignalDirection.inout);
	    			signal.setName(signal.getInout().content());
	    			signal.setInout(null);
	   		}
    		else if(signal.getBidirectional().content() != null) {
    			signal.setDirection(SignalDirection.bidirectional);
    			signal.setName(signal.getBidirectional().content());
    			signal.setBidirectional(null);
    		}
    	}
    	if(changed) {
    		if(modMessages.contains(me)) {
    			return;
    		}
    		presentModWarning();
    		modMessages.add(me);
    	}
	}
	
	protected void updateSignalDefinitions(Element element) {
    	Signals fileElement = (Signals)element;
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
