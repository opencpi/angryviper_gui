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

public class ComponentSpec {
	String name;
	List<Property> properties;
	List<Port> ports;
	
	public ComponentSpec() {
		this.properties = new ArrayList<Property>();
		this.ports = new ArrayList<Port>();
		this.name = "";
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getName() {
		return this.name;
	}
	
	public void addProperty(Property prop) {
		this.properties.add(prop);
	}
	
	public List<Property> getProperties() {
		return this.properties;
	}

	public void addPort(Port port) {
		this.ports.add(port);
	}
	
	public List<Port> getPorts() {
		return this.ports;
	}
	
	public void clearFields() {
		this.properties.clear();
		this.ports.clear();
	}
}
