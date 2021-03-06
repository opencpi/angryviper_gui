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

package av.proj.ide.parsers.ocs;

import java.util.ArrayList;
import java.util.List;

public class Protocol {
	private List<Operation> operations;
	
	public Protocol() {
		this.operations = new ArrayList<Operation>();
	}
	
	public void addOperation(Operation operation) {
		this.operations.add(operation);
	}
	
	public List<Operation> getOperations() {
		return this.operations;
	}
	
	public String toString() {
		String retval = "";
		retval += "    <Protocol>\n";
		for (Operation o : this.operations) {
			retval += o.toString();
		}
		retval += "    </Protocol>\n";
		return retval;
	}

}
