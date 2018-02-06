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

public class Operation {
	private String name;
	private String twoway;
	private List<Argument> arguments;
	
	public Operation() {
		this.name = "";
		this.twoway = "";
		this.arguments = new ArrayList<Argument>();
	}
	
	public String getName() {
		return this.name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getTwoway() {
		return this.twoway;
	}
	
	public void setTwoway(String twoway) {
		this.twoway = twoway;
	}
	
	public void addArgument(Argument argument) {
		this.arguments.add(argument);
	}
	
	public List<Argument> getArguments() {
		return this.arguments;
	}
	
	public String toString() {
		String retval = "";
		retval += "      <Operation";
		if (!this.name.equals("")) {
			retval += " Name=\""+this.name+"\"";
		}
		if (!this.twoway.equals("")) {
			retval += " TwoWay=\""+this.twoway+"\"";
		}
		retval += ">\n";
		for (Argument a : this.arguments) {
			retval += a.toString();
		}
		retval += "      </Operation>\n";
		return retval;
	}
	
	
}
