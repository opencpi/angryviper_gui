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

package av.proj.ide.ocs;

import org.eclipse.core.resources.IFile;
import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.modeling.xml.RootXmlResource;
import org.eclipse.sapphire.ui.SapphireEditor;
import org.eclipse.sapphire.ui.swt.xml.editor.XmlEditorResourceStore;
import org.eclipse.ui.PartInitException;
import org.eclipse.wst.sse.ui.StructuredTextEditor;

public class OCSEditor extends SapphireEditor {
	
	private StructuredTextEditor specSourceEditor;
	private IFile currFile;
    
	@Override
    protected void createEditorPages() throws PartInitException 
    {
        addDeferredPage( "Design", "OCSEditorPage" );
        
        this.specSourceEditor = new StructuredTextEditor();
        this.specSourceEditor.setEditorPart(this);
        
        int index = addPage( this.specSourceEditor, getEditorInput() );
        setPageText( index, "Source" );
        this.currFile = (IFile) getEditorInput().getAdapter(IFile.class);
    }
    
    @Override
    protected Element createModel() 
    {
    	Element element = ComponentSpec.TYPE.instantiate(new RootXmlResource(new XmlEditorResourceStore(this, this.specSourceEditor)));
    	ComponentSpec spec = (ComponentSpec)element;
    	if (this.currFile != null) {
    		spec.setLocation(this.currFile.getLocation().toString());
    	}
    	return element;
    }

}
