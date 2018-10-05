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

import av.proj.ide.internal.AngryViperAsset;
import av.proj.ide.internal.OcpidevVerb;
import av.proj.ide.internal.OpenCPICategory;

public class BuildTestExecAsset extends BuildExecAsset {
	
	public BuildTestExecAsset(AngryViperAsset asset, List<String> buildFlags, List<String> cleanflags) {
		super(asset, buildFlags, cleanflags);
	}
	
	@Override
	protected void createBuildString() {
		buildString = new ArrayList<String>(baseCmd);
		StringBuilder sb = new StringBuilder(asset.projectLocation.projectPath);

		switch (asset.category) {
		//case test:
		case componentsLibrary:
			sb.append("/components");
			break;
			
		case library:
			sb.append("/components");
			if( ! "components".equals(asset.assetName)) {
				sb.append("/");
				sb.append(asset.assetName);
			}
			break;
			
		case platforms:
			sb.append("/hdl/platforms");
			break;
			
		case platform:
			sb.append("/hdl/platforms/");
			sb.append(asset.buildName);
			break;
			
		case cards:
		case devices:
			sb.append("/hdl/");
			sb.append(asset.assetName);
			break;
			
		default:
			// do nothing.
		}
		
		buildString.add(sb.toString());
		
		verbIndex = buildString.size();
		buildString.add(null);
		buildString.add("test");
		
		if(asset.category == OpenCPICategory.test){
			if(asset.buildName != null) {
				buildString.add(asset.buildName);
			}
			buildString.add("-l");
			buildString.add(asset.libraryName);
		}
		else if(asset.category == OpenCPICategory.hdlTest) {
			buildString.add(asset.buildName);
		}
	}
	
	public static List<ExecutionAsset> createBuildAssets(OcpidevVerb verb, UserBuildSelections selections) {
		List<String> hdlbuildList = new ArrayList<String>();
		List<String> rccbuildList = new ArrayList<String>();
		BuildTargetSelections buildSelects = selections.buildTargetSelections;
		assembleRccPlaformList(buildSelects.rccBldSelects, rccbuildList);
		if(buildSelects.isHdlPlatforms)
			assembleHdlBuildList(buildSelects.isHdlPlatforms, buildSelects.hdlBldSelects, hdlbuildList);
		
		ArrayList<ExecutionAsset> buildAssets = new ArrayList<ExecutionAsset>(selections.assetSelections.size());
		for(AngryViperAsset selection : selections.assetSelections) {
			
			switch(selection.category){
			
			// These top level categories don't apply
//			case applications:
//			case primitives:
//			case assemblies:
//				continue;
//			
//			// These second level assets don't apply either.
//			case application:
//			case assembly:
//			case primitive:
//			case component:
//			case card:
//			case device:
//				continue;
				
			case test:
			case hdlTest:
			case componentsLibrary:
			case library:
			case platform:
			case project:
				break;
				
				default:
					continue;
			}
			
			List<String> buildFlagList = creatBuildTagSequence(selection, hdlbuildList, rccbuildList);
			BuildTestExecAsset bldAsset = new BuildTestExecAsset(selection, buildFlagList, buildFlagList);
			// go ahead and setup the command.
			bldAsset.getCommand(verb, false);
			buildAssets.add(bldAsset);
		}
		return buildAssets;
	}

	public static List<String>  creatBuildTagSequence(AngryViperAsset selection, List<String> hdlbuildList, List<String> rccbuildList) {
		List<String> tagList = new ArrayList<String>();
			tagList.addAll(hdlbuildList);
			tagList.addAll(rccbuildList);
		return tagList;
	}
	
}
