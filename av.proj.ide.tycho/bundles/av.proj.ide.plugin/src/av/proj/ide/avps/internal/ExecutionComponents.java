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

import java.util.List;

import org.eclipse.ui.console.MessageConsole;

/***
 * ExecutionComponents ties together everything associated with fulfilling a run
 * configuration. Since the user can execute a request from several points in
 * the UI and there is a re-run requirement, this class persists as long as the
 * status bar remains in place. It is a linking class that maintains the console
 * and the status bar for the request as well as the assets to build/run,
 * re-run. It is also used by the build service to manage run configuration
 * resources and the build number.
 */

public class ExecutionComponents {
	public Integer executionNumber;
	public CommandExecutor  commandExecutor;
	MessageConsole bldConsole;
	StatusNotificationInterface statusMonitor;
	List<ExecutionAsset> executionAssets;	
	
	public List<ExecutionAsset> getExecutionAssets() {
		return executionAssets;
	}

	public void setExecutionAssets(List<ExecutionAsset> executionAssets) {
		this.executionAssets = executionAssets;
	}

	public ExecutionComponents(Integer exNumber, MessageConsole bldConsole, StatusNotificationInterface statusMonitor) {
		this.executionNumber = exNumber;
		this.bldConsole = bldConsole;
		this.statusMonitor = statusMonitor;
	}

}
