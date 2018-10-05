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

import java.util.ArrayList;
import java.util.List;

public enum TestMode {
	gen, prep, run,
	gen_build, prep_run, prep_run_verify,
	verify,view,
	clean_all, clean_run, clean_sim;
	
	private List<String> modeFlag = null;
	public List<String> getModeFlag() {
		if(modeFlag == null) {
			modeFlag = new ArrayList<String>();
			modeFlag.add("--mode");
			modeFlag.add(this.name());
		}
		return modeFlag;
	}
}
