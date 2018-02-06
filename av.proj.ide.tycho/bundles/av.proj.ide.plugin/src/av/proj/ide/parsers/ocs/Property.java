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

public class Property {
	private String name;
	private String type;
	private String stringLength;
	private ArrayList<String> enums;
	private String arrayLength;
	private String sequenceLength;
	private String arrayDimensions;
	private String defaultAtt;
	private String readable;
	private String volatileAtt;
	private String writable;
	private String initial;
	private String padding;
	
	public Property() {
		this.name = "";
		this.type = "";
		this.stringLength = "";
		this.enums = new ArrayList<String>();
		this.arrayLength = "";
		this.sequenceLength = "";
		this.arrayDimensions = "";
		this.defaultAtt = "";
		this.readable = "";
		this.volatileAtt = "";
		this.writable = "";
		this.initial = "";
		this.padding = "";
	}
	
	public String getName() {
		return this.name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getType() {
		return this.type;
	}
	
	public void setType(String type) {
		this.type = type;
	}
	
	public String getStringLength() {
		return this.stringLength;
	}
	
	public void setStringLength(String stringLength) {
		this.stringLength = stringLength;
	}
	
	public String[] getEnums() {
		String[] empty = {};
		if (this.enums.size() != 0) {
			this.enums.toArray(empty);
		}
		return empty;
	}
	
	public void setEnums(ArrayList<String> enums) {
		this.enums = enums;
	}
	
	public void addEnum(String e) {
		this.enums.add(e);
	}
	
	public String getArrayLength() {
		return this.arrayLength;
	}
	
	public void setArrayLength(String arrayLength) {
		this.arrayLength = arrayLength;
	}
	
	public String getSequenceLength() {
		return this.sequenceLength;
	}
	
	public void setSequenceLength(String sequenceLength) {
		this.sequenceLength = sequenceLength;
	}
	
	public String getArrayDimentsions() {
		return this.arrayDimensions;
	}
	
	public void setArrayDimensions(String arrayDimensions) {
		this.arrayDimensions = arrayDimensions;
	}
	
	public String getDefault() {
		return this.defaultAtt;
	}
	
	public void setDefault(String defaultAtt) {
		this.defaultAtt = defaultAtt;
	}
	
	public String getReadable() {
		return this.readable;
	}
	
	public void setReadable(String readable) {
		this.readable = readable;
	}
	
	public String getVolatile() {
		return this.volatileAtt;
	}
	
	public void setVolatile(String volatileAtt) {
		this.volatileAtt = volatileAtt;
	}
	
	public String getWritable() {
		return this.writable;
	}
	
	public void setWritable(String writable) {
		this.writable = writable;
	}
	
	public String getInitial() {
		return this.initial;
	}
	
	public void setInitial(String initial) {
		this.initial = initial;
	}
	
	public String getPadding() {
		return this.padding;
	}
	
	public void setPadding(String padding) {
		this.padding = padding;
	}
	
	public String toString() {
		String retval = "";
		retval += "<Property";
		if (!this.name.equals("")) {
			retval += " Name=\""+this.name+"\"";
		}
		if (!this.type.equals("")) {
			retval += " Type=\""+this.type+"\"";
		}
		if (!this.stringLength.equals("")) {
			retval += " StringLength=\""+this.stringLength+"\"";
		}
		if (this.enums.size() != 0) {
			retval += " Enums=\""+this.enums+"\"";
		}
		if (!this.arrayLength.equals("")) {
			retval += " ArrayLength=\""+this.arrayLength+"\"";
		}
		if (!this.sequenceLength.equals("")) {
			retval += " SequenceLength=\""+this.sequenceLength+"\"";
		}
		if (!this.arrayDimensions.equals("")) {
			retval += " ArrayDimensions=\""+this.arrayDimensions+"\"";
		}
		if (!this.defaultAtt.equals("")) {
			retval += " Default=\""+this.defaultAtt+"\"";
		}
		retval += "/>\n";
		return retval;
	}
}
