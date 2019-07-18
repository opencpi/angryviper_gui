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

package av.proj.ide.avps.internal;

import org.eclipse.swt.widgets.TreeItem;

/**
 * Part of the status notification interface to update the status bar of an execution.
 * It is currently used to update time info in the main status and the details lines
 * (third column.  See StatusRegistration for more information.
 */
public class OcpiBuildStatus {
	String   statusUpdate = null;
	int[]    lineIdx = null;
	String[] lineUpdates;
	
	public void updateRunStatusLine(TreeItem line) {
		if(statusUpdate != null) {
			String current = line.getText(2);
			line.setText(2, current + statusUpdate);
		}
		if (lineIdx == null) return;
		int i = 0;
		for(int detailIdx : lineIdx) {
			TreeItem detailLine = line.getItem(detailIdx);
			detailLine.setText(2, lineUpdates[i]);
			i++;
		}
	}
}
