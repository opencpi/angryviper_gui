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

package av.proj.ide.oas.internal;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.sapphire.ui.DragAndDropService;
import org.eclipse.sapphire.ui.Point;
import org.eclipse.sapphire.ui.diagram.editor.DiagramNodePart;
import org.eclipse.sapphire.ui.diagram.editor.SapphireDiagramEditorPagePart;

import av.proj.ide.internal.AngryViperAssetService;
import av.proj.ide.oas.Application;
import av.proj.ide.oas.Instance;

public class ApplicationInstanceDragAndDropService extends DragAndDropService {

	@Override
	public boolean droppable(DropContext context) {
		if (context.object() instanceof IFile) {
			IFile file = (IFile) context.object();
			if(file == null)
				return false;
			
			if (file.getName().endsWith("-spec.xml") || file.getName().endsWith("_spec.xml")) {
				return true;				
			}
		}
		return false;
	}

	@Override
	public void drop(DropContext context) {
		IFile specFile = (IFile) context.object();
		String name = specFile.getName();
		String fullProjectPathname = specFile.getProject().getLocation().toOSString();
		
		IResource library = specFile.getParent().getParent();
		String instanceName = AngryViperAssetService.getInstance().getEnvironment().
				getApplicationSpecName(fullProjectPathname, library.getName(), name);
		final SapphireDiagramEditorPagePart diagram = context( SapphireDiagramEditorPagePart.class );
        final Application app = context( Application.class );
        
        final Point initialDropPosition = context.position();
        
        int x = initialDropPosition.getX();
        int y = initialDropPosition.getY();
        
        final Instance instance = app.getInstances().insert();
		if(instanceName == null) {
			instance.setName("UnavailableSpec");
		}
		else {
			instance.setComponent(instanceName);
		}
        
        final DiagramNodePart instanceNodePart = diagram.getDiagramNodePart(instance);
        instanceNodePart.setNodeBounds(x, y);
	}
}
