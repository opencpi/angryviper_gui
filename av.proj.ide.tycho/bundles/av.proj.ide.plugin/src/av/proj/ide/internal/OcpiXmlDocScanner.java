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

package av.proj.ide.internal;

import java.util.ArrayList;
import java.util.HashMap;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.sapphire.ElementList;
import org.eclipse.sapphire.modeling.xml.XmlUtil;
import org.eclipse.swt.widgets.Display;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import av.proj.ide.hdl.signal.DeviceSignal;
import av.proj.ide.hdl.signal.DeviceSignalDirection;
import av.proj.ide.hdl.signal.Signal;
import av.proj.ide.hdl.signal.SignalDirection;

public class OcpiXmlDocScanner {
	
	protected String modificationMessage = null;
	protected String messageHeader = "Notice: File Modification Occurred";
	protected String editorName;
	protected int    showXTimes;
	protected int    warningCount = 0;

	protected HashMap<String, String> scanElements = new HashMap<String, String>();
	protected String messageFormat = 
			"  The XML in this file was modified to support presentation in the %s. Modifications include: %s. \n  You do not have to save these changes however they will remain if the file is saved.";
	
    public static final String XMLNS = XmlUtil.XMLNS;
    
	public void setShowXTimes(int showXTimes) {
		this.showXTimes = showXTimes;
	}
	
	public void setEditorName(String editorName) {
		this.editorName = editorName;
	}
	
	public void addScanElements(String errorTag, String correctTag) {
		scanElements.put(errorTag, correctTag);
	}
	
	public void processModifications ( ArrayList<String> repairs) {
		if(repairs == null || repairs.size() == 0) return;
		
		if(modificationMessage == null) {
			StringBuilder repairList = new StringBuilder();
			boolean first = true;
			for(String repair : repairs) {
				if(! first) {
					repairList.append(", ");
				}
				else {
					first = false;
				}
				repairList.append(repair);
			}
			modificationMessage = String.format(messageFormat, editorName, repairList.toString());
		}
		presentModWarning();
	}
	
	/**
	 * For simplicity the UI currently presents signal definitions using
	 * the current name and a direction and direction attributes. This method
	 * changes any signal declared in the old direction=<name> to this...
	 */
	public boolean scanSignalElements(ElementList<Signal> signals, ArrayList<String> repairs) {
		boolean changed = false;
    	for(Signal signal : signals) {
    		String name = signal.getName().content();
    		if( name != null )
    			continue;
    		
    		changed = true;
    		if(signal.getInput().content() != null) {
    			signal.setDirection(SignalDirection.in);
    			signal.setName(signal.getInput().content());
    			signal.setInput(null);
    		}
    		else if(signal.getOutput().content() != null) {
    			signal.setDirection(SignalDirection.out);
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
    		repairs.add("Signal definitions changed to use current name and direction attributes");
    	}
    	return changed;
	}
	public boolean scanDeviceSignalElements(ElementList<DeviceSignal> signals, ArrayList<String> repairs) {
		boolean changed = false;
    	for(DeviceSignal signal : signals) {
    		String name = signal.getName().content();
    		if( name != null )
    			continue;
    		
    		changed = true;
    		if(signal.getInput().content() != null) {
    			signal.setDirection(DeviceSignalDirection.in);
    			signal.setName(signal.getInput().content());
    			signal.setInput(null);
    		}
    		else if(signal.getOutput().content() != null) {
    			signal.setDirection(DeviceSignalDirection.out);
    			signal.setName(signal.getOutput().content());
    			signal.setOutput(null);
	   		}
    	}
    	if(changed) {
    		repairs.add("Signal definitions changed to use current name and direction attributes");
    	}
    	return changed;
	}
	
	public boolean scanEditorsList(Document doc, ArrayList<String> repairs) {

		org.w3c.dom.Element root = doc.getDocumentElement();
		NodeList l = root.getElementsByTagName("*");
		
    	int len = l.getLength();
    	if(len == 0) return false;
    	
    	boolean changed = false;
       	ArrayList<Node> nodesToRepair = new ArrayList<Node>();
       
		for (int i = 0; i < len; i++) {
    		Node n = l.item(i);
    		String name = n.getLocalName();
    		if(name == null) continue;
    		
    		
    		if(scanElements.containsKey(name.toLowerCase())) {
    			String nodeTag = name.toLowerCase();
        		String validName = scanElements.get(nodeTag.toLowerCase());
        		if( !  name.equals(validName)) {
        			nodesToRepair.add(n);
        		}
    		}
		}
		
		if(nodesToRepair.isEmpty()) return false;
		
		changed = true;
    	for(Node n : nodesToRepair) {
    		String nodeTag = n.getLocalName();
    		String validName = scanElements.get(nodeTag.toLowerCase());
    		
    		repairs.add(nodeTag);
    		String ns = n.getNamespaceURI();
    		NamedNodeMap nm = n.getAttributes();
    		org.w3c.dom.Element et = doc.createElementNS(ns, validName);
    		root.insertBefore(et, n);
    		root.removeChild(n);

    		for (int i = 0; i < nm.getLength(); i++) {
    			Attr attr = (Attr) nm.item(i);
    			Attr newAttr = (Attr) attr.cloneNode(true);
    			et.setAttributeNode(newAttr);
    		}
    	}
		return changed;
	}
	
	public boolean checkXiInclude(Document doc, ArrayList<String> repairs) {
		org.w3c.dom.Element root = doc.getDocumentElement();
		NamedNodeMap a = root.getAttributes();
		NodeList nl = root.getChildNodes();
		int nodes = nl.getLength();
		boolean hasIncludeTag = false;
		boolean hasNamespace = false;
		
		Outer:
		for(int i=0; i< nodes; i++) {
			Node n = nl.item(i);
			String name = n.getNodeName();
			if(name.contains("include")) {
				hasIncludeTag = true;
				
				// Now see if the namespace is declared.
				int atrs =a.getLength();
				for(int j = 0; j<atrs; j++) {
					n = a.item(j);
					name=n.getNodeName();
					if(name.contains(XMLNS)) {
						hasNamespace=true;
						break Outer;
					}
				}
			}
		}
		
		boolean makeMod = hasIncludeTag && ! hasNamespace;
		if(makeMod) {
			root.setAttribute("xmlns:xi", "http://www.w3.org/2001/XInclude");
			repairs.add("xi namespace declared");
		}
		return makeMod;
	}
	
	protected void presentModWarning() {
		if(warningCount < showXTimes) {
			warningCount++;
			if(warningCount == showXTimes) {
				modificationMessage += "\n\nThis is that last warning from this editor.";
			}
			Display.getDefault().asyncExec(new Runnable(){
				public void run() {
					MessageDialog.openInformation(Display.getDefault().getActiveShell(), messageHeader, modificationMessage);
				}
			});
		}
	}
	

/***
	protected void scanFileForCaseCriticalElements(IFile file, String editor) {
		boolean changed = false;
		try {
			InputStream in = file.getContents();
			BufferedReader reader =
				      new BufferedReader(new InputStreamReader(in));
			String line = null;
			int bytesToLine = 0;
			int lineCount = 0;
//			char[] buf = new char[128];
//			int len = 0;
//			while ((len = reader.read(buf)) != -1) {
//				
//				scanLine.contains("interface>");
//			}
			while ((line = reader.readLine()) != null) {
				String scanLine = line.toLowerCase();
				if(scanLine.contains("interface>")) {
					int idx = scanLine.indexOf("interface>");
					
				}
			}
		} catch (CoreException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	if(changed) {
    		if(modMessages.containsKey(editor)) {
    			return;
    		}
    		presentModWarning();
    		modMessageCount.put(editor, new Integer(1));
    	}
	}
****/
}
