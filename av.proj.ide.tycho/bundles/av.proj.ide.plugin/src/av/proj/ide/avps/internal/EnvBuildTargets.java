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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * Here are classes used as the target and platform data model
 * as well as routines to obtained and parse the JSON objects.
 */
public class EnvBuildTargets {
	
	public class HdlTargetInfo {
		String name;
		String tool;
		List<String> parts;
		
		public HdlTargetInfo(String name, JSONObject target) {
			this.name = name;
			tool = null;
			parts = new ArrayList<String>();
			tool = (String)target.get("tool");
			JSONArray prts = (JSONArray) target.get("parts");
			for (int i = 0; i < prts.size(); i++) {
				String part = (String) prts.get(i);
				parts.add(part);
			}
		}
	}
	public class HdlVendor {
		String vendor;
		List<HdlTargetInfo> targetList;
		String[] targets;
		
		public HdlVendor(String name, JSONObject vendor) {
			this.vendor = name;
			targetList = new ArrayList<HdlTargetInfo>();
			
	        @SuppressWarnings("unchecked")
			Set<String> targetNames = (Set<String>)vendor.keySet();
	        for(String tname : targetNames) {
	       	 JSONObject target = (JSONObject) vendor.get(tname);
	      	 HdlTargetInfo targ = new HdlTargetInfo(tname, target);
	      	 targetList.add(targ);
	        }
	        targets = new String[targetList.size()];
	        
	        int i = 0;
	        for(HdlTargetInfo target: targetList) {
	        	targets[i++] = target.name;
	        }
		}

		public String getVendor() {
			return vendor;
		}

		public List<HdlTargetInfo> getTargetList() {
			return targetList;
		}

		public String[] getTargets() {
			return targets;
		}
		
	}

	public class HdlPlatformInfo {
		String name;
		String vendor;
		String tool;
		String part;
		String target;
		boolean built;
		
		public HdlPlatformInfo(String name, JSONObject platform) {
			this.name = name;
			vendor = (String) platform.get("vendor");
			tool = (String) platform.get("tool");
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

		public String getTool() {
			return tool;
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
	
	public List<HdlVendor> getHdlVendors() {
		
		JSONObject jsonObject = getEnvInfo(hdlTargetsCmd);		
        @SuppressWarnings("unchecked")
		Set<String> keys = jsonObject.keySet();
        ArrayList<HdlVendor> vendors = new ArrayList<HdlVendor>();
        for(String key : keys) {
        	 JSONObject vendorObj = (JSONObject) jsonObject.get(key);
        	 HdlVendor vendor = new HdlVendor(key, vendorObj);
        	 vendors.add(vendor);
        }
		return vendors;
	}
	
	public List<HdlPlatformInfo> getHdlPlatforms() {
		JSONObject jsonObject = getEnvInfo(hdlPlatformsCmd);		
        @SuppressWarnings("unchecked")
		Set<String> keys = jsonObject.keySet();
        ArrayList<HdlPlatformInfo> platforms = new ArrayList<HdlPlatformInfo>();
        for(String key : keys) {
        	 JSONObject platformObj = (JSONObject) jsonObject.get(key);
        	 HdlPlatformInfo platform = new HdlPlatformInfo(key, platformObj);
        	 platforms.add(platform);
        }
		return platforms;
	}
	public List<RccPlatformInfo> getRccPlatforms() {
		JSONObject jsonObject = getEnvInfo(rccPlatformsCmd);		
        @SuppressWarnings("unchecked")
		Set<String> keys = jsonObject.keySet();
        ArrayList<RccPlatformInfo> rccPlatforms = new ArrayList<RccPlatformInfo>();
        for(String key : keys) {
        	 JSONObject platformObj = (JSONObject) jsonObject.get(key);
        	 RccPlatformInfo platform = new RccPlatformInfo(key, platformObj);
        	 rccPlatforms.add(platform);
        }
		return rccPlatforms;
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
