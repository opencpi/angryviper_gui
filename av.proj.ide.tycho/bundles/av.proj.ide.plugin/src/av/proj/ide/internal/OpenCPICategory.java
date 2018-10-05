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

/**
 * Asset enumeration and framework information. Used to identify an asset
 * and to be a common point for framework strings. It also holds
 * the ocpidev nowns used to create an build an asset.  Finally, it is used
 * to provide string labels for the gui and assign images in the display.
 */
public enum OpenCPICategory {
	
	/***
 	Currently ocpidev supports the follow assets:
	ocpidev create
	- application library project protocol spec test worker
	- hdl
	-- assembly card device platform primitive slot
	
	ocpidev build
	- application library project test worker applications
    --  components is a library or a group of libraries.  "build library components" get them.
	- hdl
	-- assembly card device platform primitive slot
	-- platforms, assemblies, primitives
	-- cards and devices are hdl libraries so they are hdl library cards, etc.
	****/
	xmlapp("XML app","app", false),
	// Concrete OpenCPI Assets.  All are buildable, many are create-able.
	// Construction is the framework nowns, create-able and expensive clean flags.
	project("project", "project", true, true), 
	library("library", "library", true, true),
	component("component", "spec", true, true),
	worker("worker", "worker", true, true),
	protocol("protocol", "protocol", true,  false),
	application("application", "application", true, false),
	test("test", "test", true, false),
	// HDL
	primitive("primitive", "hdl primitive library", true, true),
	platform ("platform", "hdl platform", true, true), 
	assembly("assembly", "hdl assembly", true, false),
	//   create nown, build nown
	card("card", "card", "worker", false, false), 
	device("device", "device", "worker", false, false),
	hdlTest("test", "test", false, false),
	
	// Grouped Assets
	
	// Since the "components" library is somewhat standardized, create
	// an enum for it rather than depend on a library named "components".
	//                create/build nowns, display label.
	componentsLibrary( "components", "library", true),
	componentsLibraries("components", "library", true),
	applications("applications", "applications", false),
	tests( "tests", "test -l", true),
	workers("workers", "library", false),
	// Folders
	specs("specs", "component specs", false),
	topLevelSpecs("specs", "project specs", false),
	
	// HDL
	//          create/build nowns, display label.
	primitives("primitives", "hdl primitives", true),
	platforms ("platforms", "hdl platforms", true), 
	assemblies("assemblies", "hdl assemblies", true),
	cards("cards", "hdl library", true),
	devices("devices", "hdl library", true);
	
	private boolean buildable = true;
	private boolean expensiveClean;
	private boolean createable = false;
	
	private String frameworkName;
	private String ocpiBuildNownsString = null;
	private String ocpiCreateNownsString = null;
	
	private List<String> ocpiBuildNowns = null;
	private List<String> ocpiCreateNowns = null;
	
	public boolean isCleanExpensive() {
		return expensiveClean;
	}
	public boolean isBuildable() {
		return buildable;
	}
	public boolean isCreateable() {
		return createable;
	}
	public List<String> getOcpiBuildNowns() {
		if(ocpiBuildNowns == null) {
			ocpiBuildNowns = createNownsList(ocpiBuildNownsString);
		}
		return ocpiBuildNowns;
	}
	public List<String> getOcpiCreateNowns() {
		if(ocpiCreateNowns == null) {
			if(ocpiCreateNownsString != null) {
				ocpiCreateNowns = createNownsList(ocpiCreateNownsString);
			}
			else {
				// create and build nowns are the same
				if(ocpiBuildNowns == null) {
					ocpiBuildNowns = createNownsList(ocpiBuildNownsString);
				}
				ocpiCreateNowns = ocpiBuildNowns;
			}
		}
		return ocpiCreateNowns;
	}
	
	// The display name is used be the UI.  As of now they
	// are the same.
	public String getFrameworkName() {
		return frameworkName;
	}
	
	private List<String> createNownsList(String nownsString) {
		List<String> ocpiNowns = new ArrayList<String>();
		if(nownsString == null) return ocpiNowns;
		String[] nowns = nownsString.split(" ");
		for(int i = 0; i< nowns.length; i++) {
			ocpiNowns.add(nowns[i]);
		}
		return ocpiNowns;
	}
	private OpenCPICategory(String frameworkName, String ocpiNouns, boolean createable, boolean expensiveClean) {
		this.frameworkName = frameworkName;
		this.expensiveClean = expensiveClean;
		this.createable = createable;
		ocpiBuildNownsString = ocpiNouns;
	}

	/***
	 * Buildable group assets where ocpidev nowns are the same for build and create.
	 * @param ocpiNouns
	 * @param displayName
	 * @param expensiveClean
	 */
	private OpenCPICategory(String frameworkName, String ocpiNouns, boolean expensiveClean) {
		ocpiBuildNownsString = ocpiNouns;
		this.frameworkName = frameworkName;
		this.expensiveClean = expensiveClean;
	}
	//
	private OpenCPICategory(String frameworkName, String ocpiCreateNouns, String ocpiBuildNouns, boolean createable,  boolean expensiveClean) {
		this.frameworkName = frameworkName;
		this.expensiveClean = expensiveClean;
		this.createable = createable;
		ocpiBuildNownsString = ocpiBuildNouns;
		ocpiCreateNownsString = ocpiCreateNouns;
	}
	public static OpenCPICategory getCategory(String assetLabel) {
		assetLabel = assetLabel.toLowerCase();
		OpenCPICategory category = null;
		
		switch(assetLabel) {
		
		case "project":
			category = project;
			break;
		case "library":
			category = library;
			break;
		case "component":
			category = component;
			break;
		case "worker":
			category = worker;
			break;
		case "protocol":
			category = protocol;
			break;
		case "application":
			category = application;
			break;
		case "unit test":
			category = test;
			break;
			
		case "hdl primitive library":
			category = primitive;
			break;
		case "hdl platform":
			category = platform;
			break;
		case "hdl assembly":
			category = assembly;
			break;
		case "card":
			category = card;
			break;
		case "device":
			category = device;
			break;
			
		default:
			break;
		}
		return category;
	}
}
