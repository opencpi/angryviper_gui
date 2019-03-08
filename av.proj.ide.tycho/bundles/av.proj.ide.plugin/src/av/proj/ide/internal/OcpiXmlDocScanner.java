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
import org.eclipse.sapphire.ui.swt.xml.editor.XmlEditorResourceStore;
import org.eclipse.swt.widgets.Display;
import org.w3c.dom.Attr;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class OcpiXmlDocScanner {
	
	protected String modificationMessage; 
	protected String messageHeader;
	protected String editorName;
	protected int    showXTimes;
	protected int    warningCount = 0;
	
	public void setShowXTimes(int showXTimes) {
		this.showXTimes = showXTimes;
	}
	public void setModMessage(String editorName, String header, String message) {
		this.editorName = editorName;
		messageHeader = header;
		modificationMessage = message;
	}
	public void addScanElements(String errorTag, String correctTag) {
		scanElements.put(errorTag, correctTag);
	}
	
	protected HashMap<String, String> scanElements = new HashMap<String, String>();
	
	
	protected void presentModWarning() {
		if(warningCount < showXTimes) {
			warningCount++;
			Display.getDefault().asyncExec(new Runnable(){
				public void run() {
					MessageDialog.openInformation(Display.getDefault().getActiveShell(), messageHeader, modificationMessage);
				}
			});
		}
	}
	
	public boolean scanAndUpdateXmlIssues(XmlEditorResourceStore xe) {
		boolean foundSome = false;
		org.w3c.dom.Element root = xe.getDomDocument().getDocumentElement();
		NodeList l = root.getElementsByTagName("*");
		
    	int len = l.getLength();
    	if(len == 0) return foundSome;
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
            		foundSome = true;
        		}
    		}
		}
    	for(Node n : nodesToRepair) {
    		String nodeTag = n.getLocalName();
    		
    		String validName = scanElements.get(nodeTag.toLowerCase());
    		if(nodeTag.equals(validName)) continue;
    		
    		foundSome = true;
    		String ns = n.getNamespaceURI();
    		NamedNodeMap nm = n.getAttributes();
    		org.w3c.dom.Element et = xe.getDomDocument().createElementNS(ns, validName);
    		root.insertBefore(et, n);
    		root.removeChild(n);

    		for (int i = 0; i < nm.getLength(); i++) {
    			Attr attr = (Attr) nm.item(i);
    			Attr newAttr = (Attr) attr.cloneNode(true);
    			et.setAttributeNode(newAttr);
    		}
    	}
    	if(foundSome) {
    		presentModWarning();
    	}
		return foundSome;
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
