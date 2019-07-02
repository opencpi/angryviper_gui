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
import java.util.List;

/***
 * This appears to be an idea that was never fully implemented and put into
 * service.
 */
public class OcpidevCommand {
	
	List<String> ocpidevCmd;
	OcpidevVerb currentVerb = null;
	
	protected static int VERB_LOC = 3;
	protected static ArrayList<String> cmdStart = new ArrayList<String>();
	{
		cmdStart.add("ocpidev");
		cmdStart.add("-d");
	};
	
	protected OcpidevCommand() {
		ocpidevCmd = new ArrayList<String>(cmdStart);
	}
	
	public void swapVerb(OcpidevVerb verb) {
		ocpidevCmd.add(VERB_LOC, verb.getVerb());
	}

	/***
	 * For single tests:
	 * 
	 * ocpidev -d /home/tstrong/AV/core build test bias.test -l components --hdl-platform modelsim
	 * ocpidev -d /home/tstrong/AV/assets build test mfsk_mapper.test -l comms_comps --hdl-platform xsim
	 * ocpidev -d /home/tstrong/AV/core run test bias.test -l components --hdl-platform modelsim
	 * ocpidev -d /home/tstrong/AV/core run test bias.test -l components --hdl-platform modelsim --mode prep
	 */
	
	public static OcpidevCommand createCommand(OcpidevVerb verb, AngryViperAsset asset) {
		OcpidevCommand command = new OcpidevCommand();
		List<String> cmd = command.ocpidevCmd;
		cmd.add(asset.projectLocation.projectPath);
		cmd.add(verb.getVerb());
		
		
		switch(verb) {
		case build:
			command.ocpidevCmd.add("build");
			break;
		case clean:
			command.ocpidevCmd.add("clean");
			break;
		case create:
			break;
		case delete:
			break;
		case register:
			break;
		case run:
			break;
		case show:
			break;
		case unregister:
			break;
		default:
			break;
		}
		
		return command;
	}

}
