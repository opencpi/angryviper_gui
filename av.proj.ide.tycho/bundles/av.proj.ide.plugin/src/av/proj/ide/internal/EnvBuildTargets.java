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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import av.proj.ide.avps.internal.AvpsResourceManager;

/**
 * Here are classes used as the target and platform data model
 * as well as routines to obtained and parse the JSON objects.
 */
public class EnvBuildTargets {
	
	TreeMap<String, HdlVendor> buildEnvironment;
	TreeMap<String, HdlPlatformInfo> hdlPlatformList;
	TreeMap<String, RccPlatformInfo> rccPlatformList;
	
	public class RccPlatformInfo {
		String name;
		String target;
		
		public RccPlatformInfo(String name, JSONObject platform) {
			this.name = name;
			target = (String) platform.get("target");
		}

		public String getName() {
			return name;
		}

		public String getTarget() {
			return target;
		}
	}
	
	public class HdlTargetInfo {
		String name;
		String vendor;
		String toolSet;
		protected Collection<String> parts;
		
		public HdlTargetInfo(String name, String vendorName, JSONObject target) {
			this.name = name;
			vendor = vendorName;
			toolSet = null;
			parts = new TreeSet<String>();
			toolSet = (String)target.get("tool");
			JSONArray prts = (JSONArray) target.get("parts");
			if(prts == null)
				return;
			
			for (int i = 0; i < prts.size(); i++) {
				String part = (String) prts.get(i);
				parts.add(part);
			}
		}
		
		public Collection<String> getParts() {
			return parts;
		}
		
	}
	
	public class HdlPlatformInfo extends HdlTargetInfo {
		String part;
		String target;
		boolean built;
		
		public HdlPlatformInfo(String name, JSONObject platform) {
			super(name, (String) platform.get("vendor"), platform);
			part = (String) platform.get("part");
			target = (String) platform.get("target");
			built = (Boolean) platform.get("built");
		}

		public String getName() {
			return name;
		}

		public String getVendor() {
			return vendor;
		}

		public String getToolSet() {
			return toolSet;
		}

		public String getPart() {
			return part;
		}

		public String getTarget() {
			return target;
		}

		public boolean isBuilt() {
			return built;
		}
		
		public int hashCode() {
			return name.hashCode();
		}
		
		public boolean equals(Object o) {
			if(o instanceof HdlPlatformInfo) {
				
				return name.equals(((HdlPlatformInfo)o).name);
			}
			return false;
		}
	}
	
	public class HdlVendor {
		String vendor;
		TreeMap<String, HdlTargetInfo> targetList;
		TreeMap<String, HdlPlatformInfo> platformList;
		
		public HdlVendor(String name, JSONObject vendor) {
			this.vendor = name;
			targetList = new TreeMap<String, HdlTargetInfo>();
			
	        @SuppressWarnings("unchecked")
			Set<String> targetNames = (Set<String>)vendor.keySet();
			if(targetNames == null)
				return;

			for (String tname : targetNames) {
				JSONObject target = (JSONObject) vendor.get(tname);
				HdlTargetInfo targ = new HdlTargetInfo(tname, name, target);
				targetList.put(tname, targ);
			}
		}

		void loadPlatforms(Collection<HdlPlatformInfo> platforms) {
			platformList = new TreeMap<String, HdlPlatformInfo>();
			for(HdlPlatformInfo platform : platforms) {
				if(platform.vendor.equals(this.vendor)) {
					platformList.put(platform.name, platform);
				}
			}
		}
		
		public String getVendor() {
			return vendor;
		}

		public Collection <HdlTargetInfo> getTargetList() {
			return targetList.values();
		}

		public Collection<String> getTargets() {
			return targetList.keySet();
		}
		public Collection <HdlPlatformInfo> getPlatformList() {
			return platformList.values();
		}

		public Collection<String> getPlatforms() {
			return platformList.keySet();
		}
		
	}

	public Collection<HdlVendor> getVendors () {
		return buildEnvironment.values();
	}
	public Collection<HdlPlatformInfo> getHdlPlatforms() {
		return hdlPlatformList.values();
	}
	public Collection<RccPlatformInfo> getRccPlatforms() {
		return rccPlatformList.values();
	}
	
	public void loadVendorPlatforms () {
		for(HdlVendor vendor : buildEnvironment.values()) {
			vendor.loadPlatforms(hdlPlatformList.values());
		}
	}
	
	void buildHdlVendors() {
		buildEnvironment = new TreeMap<String, HdlVendor> ();
		
		JSONObject jsonObject = getEnvInfo(hdlTargetsCmd);		
 		if(jsonObject == null) {
			AvpsResourceManager.getInstance().writeToNoticeConsole("Null JSON object returned from the environment. Something is wrong with ocpidev show hdl targets.");
		}

        @SuppressWarnings("unchecked")
		Set<String> keys = jsonObject.keySet();
		if(keys == null)
			return;
        
        for(String key : keys) {
        	 JSONObject vendorObj = (JSONObject) jsonObject.get(key);
        	 HdlVendor vendor = new HdlVendor(key, vendorObj);
        	 buildEnvironment.put(key, vendor);
        }
	}
	
	void buildHdPlatforms() {
		hdlPlatformList = new TreeMap<String, HdlPlatformInfo>();
		JSONObject jsonObject = getEnvInfo(hdlPlatformsCmd);		
 
        if(jsonObject == null) {
			AvpsResourceManager.getInstance().writeToNoticeConsole("Null JSON object returned from the environment. Something is wrong with ocpidev show hdl platforms.");
			return;
        }

        @SuppressWarnings("unchecked")
		Set<String> keys = jsonObject.keySet();
        for(String key : keys) {
        	 JSONObject platformObj = (JSONObject) jsonObject.get(key);
        	 HdlPlatformInfo platform = new HdlPlatformInfo(key, platformObj);
        	 hdlPlatformList.put(key,platform);
        }
	}
	
	void buildRccPlatforms() {
		rccPlatformList = new TreeMap<String, RccPlatformInfo>();
		JSONObject jsonObject = getEnvInfo(rccPlatformsCmd);		
		if(jsonObject == null) {
			AvpsResourceManager.getInstance().writeToNoticeConsole("Null JSON object returned from the environment. Something is wrong with ocpidev show rcc platforms.");
			return;
		}

		@SuppressWarnings("unchecked")
		Set<String> keys = jsonObject.keySet();
		if(keys == null)
			return;
        
        for(String key : keys) {
        	 JSONObject platformObj = (JSONObject) jsonObject.get(key);
        	 RccPlatformInfo platform = new RccPlatformInfo(key, platformObj);
        	 rccPlatformList.put(key, platform);
        }
	}
	
	protected String [] hdlTargetsCmd = {"ocpidev", "show", "hdl", "targets", "--json"};
	protected String [] hdlPlatformsCmd = {"ocpidev", "show", "hdl", "platforms", "--json"};
	protected String [] rccPlatformsCmd = {"ocpidev", "show", "rcc", "platforms", "--json"};
		 
	
	public static JSONObject getEnvInfo(String [] command) {
		Process p;
		try {
			p = Runtime.getRuntime().exec(command);
			p.waitFor();
		} catch (IOException | InterruptedException e) {
			AvpsResourceManager.getInstance().writeToNoticeConsole(e.toString());
			return null;
		}
		BufferedReader rd = new BufferedReader(new InputStreamReader(p.getInputStream()) );
        JSONParser parser = new JSONParser();
        Object obj = null;
		try {
			obj = parser.parse(rd);
		} catch (IOException | ParseException e) {
			AvpsResourceManager.getInstance().writeToNoticeConsole(e.toString());
			return null;
		}
       return (JSONObject) obj;
	}
}
