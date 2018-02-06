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

import av.proj.ide.avps.internal.ExecutionAsset.CommandVerb;

/**
 * This interface allows communication between the BuildService and the Status Monitor
 */
public interface StatusNotificationInterface {
	public void setCompletedStatusEntry(Integer buildNumber, boolean completedSuccessfully);
	public void updateBuildStatus(Integer buildNumber, OcpiBuildStatus status);
	public void registerBuild(Integer buildNumber, CommandVerb verb, String consoleName, String buildLabel);
	public void restartBuild(Integer myBuildNumber, CommandVerb verb);
}
