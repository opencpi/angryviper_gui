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

package av.proj.ide.ohad.internal;

import java.io.InputStream;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.sapphire.LoggingService;
import org.eclipse.sapphire.Sapphire;
import org.eclipse.sapphire.ui.DragAndDropService;
import org.eclipse.sapphire.ui.Point;
import org.eclipse.sapphire.ui.diagram.editor.DiagramNodePart;
import org.eclipse.sapphire.ui.diagram.editor.SapphireDiagramEditorPagePart;

import av.proj.ide.ohad.HdlAssembly;
import av.proj.ide.ohad.Instance;
import av.proj.ide.parsers.owd.OWDXMLParser;
import av.proj.ide.parsers.owd.Worker;

public class AssemblyInstanceDragAndDropService extends DragAndDropService {
	
	@Override
	public boolean droppable(DropContext context) {
		if (context.object() instanceof IFile) {
			IFile file = (IFile) context.object();
			if (!file.getName().endsWith(".xml")) {
				return false;				
			}
			if (!file.getParent().getName().endsWith(".hdl")) {
				return false;
			}
		}
		return true;
	}

	@Override
	public void drop(DropContext context) {
		IFile assyWorkerFile = (IFile) context.object();
		InputStream in = null;
		OWDXMLParser parser = new OWDXMLParser();
		try {
			in = assyWorkerFile.getContents();
			parser.parse(in);
			Worker worker = parser.getWorker();
			String instanceName = "";
			if (worker.getName() != null && !worker.getName().equals("")) {
				instanceName = worker.getName();
			} else {
				instanceName = assyWorkerFile.getName().replace(".xml", "");
			}
			if (!instanceName.equals("")) {
				final SapphireDiagramEditorPagePart diagram = context( SapphireDiagramEditorPagePart.class );
	            final HdlAssembly app = context( HdlAssembly.class );
	            
	            final Point initialDropPosition = context.position();
	            
	            int x = initialDropPosition.getX();
	            int y = initialDropPosition.getY();
	            
	            final Instance instance = app.getInstances().insert();
	            instance.setWorker(instanceName);
	            
	            final DiagramNodePart instanceNodePart = diagram.getDiagramNodePart(instance);
	            instanceNodePart.setNodeBounds(x, y);
//	            System.out.println("Printing stack trace:");
//	            StackTraceElement[] elements = Thread.currentThread().getStackTrace();
//	            for (int i = 1; i < elements.length; i++) {
//	              StackTraceElement s = elements[i];
//	              System.out.println("\tat " + s.getClassName() + "." + s.getMethodName()
//	                  + "(" + s.getFileName() + ":" + s.getLineNumber() + ")");
//	            }
	        }
		} catch (CoreException e) {
			Sapphire.service( LoggingService.class ).log( e );
		}
	
	}

}
