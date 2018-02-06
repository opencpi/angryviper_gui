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

/**
 * Asset enumeration.  This is used to make common categories 
 * of assets supporting how they are built and how they are presented
 * in a listing.
 */
public enum OcpiAssetCategory {
	project("project", null, true), 
	primitives("primitives", "primitives", true),
	primitive("primitive", "hdl primitive library", true),
	platforms ("platforms", "hdl platforms", true), 
	platform ("platform", "--hdl-platform", true), 
	assemblies("assemblies", "hdl assemblies", true),
	assembly("assembly", "--hdl-assembly", false),
	hdlLibrary("HDL library", "hdl library", true),
	card("card", "--worker", true),
	//devices("Devices", "hdl devices"),
	device("device", "--worker", true),
	library("library", "library", true),
	hdlTest("test", "test", false),
	components("components", "library", true),
	component("component", "worker", true),
	tests("tests", "test -l", true),
	test("test", "test", false),
	applications("applications", "applications", false),
	application("application","application", false);

	private String presentationName;
	private List<String> ocpiBuildNowns;
	private boolean expensiveClean;
	
	public boolean isCleanExpensive() {
		return expensiveClean;
	}
	public List<String> getOcpiBuildNowns() {
		return ocpiBuildNowns;
	}
	public String getListText() {
		return presentationName;
	}

	private OcpiAssetCategory(String presentationFolder, String ocpiBuildNouns, boolean expensiveClean) {
		this.presentationName = presentationFolder;
		this.expensiveClean = expensiveClean;
		this.ocpiBuildNowns = new ArrayList<String>();
		
		
		if(ocpiBuildNouns == null) return;
		
		String[] nowns = ocpiBuildNouns.split(" ");
		for(int i = 0; i< nowns.length; i++) {
			this.ocpiBuildNowns.add(nowns[i]);
		}
	}
}
