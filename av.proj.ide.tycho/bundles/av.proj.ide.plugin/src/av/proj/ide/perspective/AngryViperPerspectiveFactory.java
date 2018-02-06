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

package av.proj.ide.perspective;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

import av.proj.ide.plugin.views.AVBuildStatusView;
import av.proj.ide.plugin.views.AVMainOperationView;
import av.proj.ide.plugin.views.AVProjectView;

public class AngryViperPerspectiveFactory implements IPerspectiveFactory {
	
    public void createInitialLayout(IPageLayout layout) {
    	
 
    	IFolderLayout topLeft = layout.createFolder("topLeft", IPageLayout.LEFT, 0.2f, IPageLayout.ID_EDITOR_AREA);
        IFolderLayout bottomLeft = layout.createFolder("bottomLeft", IPageLayout.BOTTOM, 0.50f, "topLeft");
        topLeft.addView(AVProjectView.ID);
        bottomLeft.addView(IPageLayout.ID_PROJECT_EXPLORER);
     
        
        IFolderLayout topRight = layout.createFolder("topRight", IPageLayout.RIGHT, 0.6f, IPageLayout.ID_EDITOR_AREA);
       	IFolderLayout bottomRight = layout.createFolder("bottomRight", IPageLayout.BOTTOM, 0.5f, "topRight");
       	
        topRight.addView(AVBuildStatusView.ID);
    	bottomRight.addView(IPageLayout.ID_PROBLEM_VIEW);
    	
        IFolderLayout top = layout.createFolder("top", IPageLayout.TOP, 0.6f, IPageLayout.ID_EDITOR_AREA);
        top.addView(AVMainOperationView.ID);

    }
}
