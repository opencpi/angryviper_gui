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

public class BuildTargetSelections {
	
	public String[]       hdlBldSelects = null;
	public boolean        isHdlPlatforms = false; 
	public String[]       rccBldSelects = null;
	
	public BuildTargetSelections() {}
	public BuildTargetSelections(BuildTargetSelections other) {
		hdlBldSelects = other.hdlBldSelects;
		isHdlPlatforms = other.isHdlPlatforms; 
		rccBldSelects = other.rccBldSelects;
	}
	public boolean equals(BuildTargetSelections other) {
		if(other == null) {
			return false;
		}
		if(other.isHdlPlatforms != isHdlPlatforms) return false;
		
		int myCount = 0;
		int otherCount = 0;
		
		if(hdlBldSelects != null) myCount = hdlBldSelects.length;
		if(other.hdlBldSelects != null) otherCount = other.hdlBldSelects.length;
		if(myCount != otherCount) {
			return false;
		}
		else {
			for(int i=0; i < myCount; i++) {
				if(! hdlBldSelects[i].equals(other.hdlBldSelects[i])) {
					return false;
				}
			}
		}
		
		myCount = 0;
		otherCount = 0;
		if(rccBldSelects != null) myCount = rccBldSelects.length;
		if(other.rccBldSelects != null) otherCount = other.rccBldSelects.length;
		if(myCount != otherCount) {
			return false;
		}
		else {
			for(int i=0; i < myCount; i++) {
				if(! rccBldSelects[i].equals(other.rccBldSelects[i])) {
					return false;
				}
			}
		}
		return true;	
	}
	
	public static void main (String[] args) {
		
		String [] hdls = {"hone", "htwo"};
		String [] rccs = {"rone", "rtwo"};
		BuildTargetSelections sels1 = new BuildTargetSelections();
		sels1.hdlBldSelects = hdls;
		sels1.rccBldSelects = rccs;
		
		BuildTargetSelections sels2 = new BuildTargetSelections(sels1);
		
		sels1.equals(sels2);
		sels1.rccBldSelects = null;
		sels1.equals(sels2);
		String [] rccs2 = {"rone", "r2two"};
		sels1.rccBldSelects = rccs2;
		sels1.equals(sels2);
		
		
	}
}
