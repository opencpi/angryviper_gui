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

public class Port {
	private String name;
	private String producer;
	private String protocol;
	private String optional;
	private List<Protocol> protocols;
	
	public Port() {
		this.name = "";
		this.producer = "";
		this.protocol = "";
		this.optional = "";
		this.protocols = new ArrayList<Protocol>();
	}
	
	public String getName() {
		return this.name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getProducer() {
		return this.producer;
	}
	
	public void setProducer(String producer) {
		this.producer = producer;
	}
	
	public String getProtocol() {
		return this.protocol;
	}
	
	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}
	
	public String getOptional() {
		return this.optional;
	}
	
	public void setOptional(String optional) {
		this.optional = optional;
	}
	
	public void addProtocol(Protocol protocol) {
		this.protocols.add(protocol);
	}
	
	public List<Protocol> getProtocols() {
		return this.protocols;
	}
	
	
	public String toString() {
		String retval = "";
		retval += "<Port";
		if (!this.name.equals("")) {
			retval += " Name=\"" + this.name + "\"";
		}
		if (!this.producer.equals("")) {
			retval += " Producer=\"" + this.producer + "\"";
		}
		if (!this.protocol.equals("")) {
			retval += " Protocol=\"" + this.protocol + "\"";
		}
		if (!this.optional.equals("")) {
			retval += " Optional=\"" + this.optional + "\"";
		}
		retval += ">\n";
		for (Protocol p : this.protocols) {
			retval += p.toString();
		}
		return retval;
	}
	
	public void clearFields() {
		if (this.protocols != null) {
			this.protocols.clear();
		}
	}
	
}
