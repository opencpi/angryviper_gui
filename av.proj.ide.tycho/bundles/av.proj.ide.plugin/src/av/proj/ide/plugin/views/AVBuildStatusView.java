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

package av.proj.ide.plugin.views;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

import av.proj.ide.swt.ColorSchemeBlue;
import av.proj.ide.swt.StatusViewSwtDisplay2;


public class AVBuildStatusView extends ViewPart {

	public static final String ID = "av.proj.ide.plugin.views.BuildStatusView";
	
	StatusViewSwtDisplay2 viewDisplay;


	public void createPartControl(Composite parent) {
		ColorSchemeBlue colorScheme = new ColorSchemeBlue();
		viewDisplay = new StatusViewSwtDisplay2(parent, SWT.NONE);
		viewDisplay.setPanelColorScheme(colorScheme);
	}

	@Override
	public void setFocus() {
	}
}