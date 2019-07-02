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

package av.proj.ide.custom.bindings.root;

import static org.eclipse.sapphire.modeling.util.MiscUtil.equal;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.sapphire.modeling.xml.StandardRootElementController;
import org.eclipse.swt.widgets.Display;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/***
 * This is the latest generalized version for root nodes.
 * 
 * This class not only deals with the case issue with root nodes but it is
 * also responsible for updating files that use <xi:include> to include
 * the xmlns attribute so the editors will pick these up.
 *
 */
public class GenericMultiCaseRootBinding extends StandardRootElementController {

	protected static boolean provideNotice = true;
	
	public GenericMultiCaseRootBinding(String elementName) {
		super(elementName);
	}

	private boolean checked = false;
	
	protected void checkDoc(Document document) {
		if(checked) return;
		
		final Element root = document.getDocumentElement();
		NamedNodeMap a = root.getAttributes();
		NodeList nl = root.getChildNodes();
		int nodes = nl.getLength();
		boolean hasIncludeTag = false;
		boolean found = false;
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
						found=true;
						break;
					}
				}
			}
			if(found == true) {
				break;
			}
		}
		if(hasIncludeTag && ! found) {
			root.setAttribute("xmlns:xi", "http://www.w3.org/2001/XInclude");
			presentNotice();
		}
		checked = true;
	}
	
	@Override
	protected boolean checkRootElement(final Document document, final RootElementInfo rinfo) {
		final Element root = document.getDocumentElement();
		final String localName = root.getLocalName().toLowerCase();
		final String namespace = root.getNamespaceURI();
		boolean good = equal(localName, rinfo.elementName.toLowerCase()) && equal(namespace, rinfo.namespace);
		if(good) {
			checkDoc(document);
		}
		return good;
	}
	
	protected void presentNotice() {
		if(provideNotice) {
			Display.getDefault().asyncExec(new Runnable(){
				public void run() {
					String message = "This file includes another XML file using W3C xi:include tag. To support presentation, the namespace has been declared in the root element. This notice appears once per session.";
					MessageDialog.openInformation(Display.getDefault().getActiveShell(), "Root Element Namespace Attribute", message);
				}
			});
			provideNotice = false;
		}
	}
    

}
