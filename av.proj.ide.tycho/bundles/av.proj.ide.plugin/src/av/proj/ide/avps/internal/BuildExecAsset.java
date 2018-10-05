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

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import av.proj.ide.internal.AngryViperAsset;
import av.proj.ide.internal.AssetDetails.AuthoringModel;
import av.proj.ide.internal.OcpidevVerb;
import av.proj.ide.internal.OpenCPICategory;

public class BuildExecAsset extends ExecutionAsset {
	
	protected List<String> buildFlags;
	protected List<String> cleanFlags;
	
	protected ArrayList<String> buildString;

	protected List<String> buildCommand = null;
	protected List<String> cleanCommand = null;
	protected int verbIndex;
	protected boolean noAssembliesSet = false;
	protected String shortBld = null;
	protected String shortClean= null;
	protected boolean isHdlBuildExpensive = false;
	
	
	public boolean isHdlBuildExpensive() {
		return isHdlBuildExpensive;
	}
	
	public BuildExecAsset(AngryViperAsset asset, List<String> buildFlags, List<String> cleanflags) {
		this.asset = asset;
		this.buildFlags = buildFlags;
		this.cleanFlags = cleanflags;
		createBuildString();
	}
	@Override
	public List<String> getCommand(OcpidevVerb verb, Boolean noAssemblies) {
		switch (verb) {
		case build:
			getBuildCommand();
			setNoAssemblies(noAssemblies);
    		return buildCommand;
		case clean:
			 getCleanCommand();
	    	return cleanCommand;
		default:
			break;
		}
		return null;
	}
	@Override
	public String getDisplayString(OcpidevVerb verb){
		if(verb == OcpidevVerb.build) {
			return shortBld;
		}
		else {
			return shortClean;
		}
	}
	@Override
	public File getExecutionDir() {
		return null;
	}
	
	protected void setHdlBuild(boolean b) {
		isHdlBuildExpensive = b;
	}
	
	
	protected void setNoAssemblies(Boolean noAssemblies) {
		if(asset.category != OpenCPICategory.project || noAssemblies == null)
			return;
		
		if(noAssemblies) {
			if(noAssembliesSet) 
				return;
			
			buildCommand.add("--no-assemblies");
			noAssembliesSet = true;
		}
		else {
			int idx = buildCommand.size() -1;
			if(buildCommand.get(idx).startsWith("--no-")) {
				buildCommand.remove(idx);
				noAssembliesSet = false;
			}
		}
	}
	
	protected List<String> getBuildCommand() {
		if(buildCommand == null) {
			buildCommand = new ArrayList<String>(buildString);
			buildCommand.addAll(buildFlags);
			buildCommand.set(verbIndex, OcpidevVerb.build.getVerb());
			List<String> shortCmd = buildCommand.subList(3, buildCommand.size());
    		shortBld =  shortCmd.toString();
    		shortBld = shortBld.replace(",", " ");
		}
		return buildCommand;
	}
	
	protected List<String> getCleanCommand() {
		if(cleanCommand == null) {
			cleanCommand = new ArrayList<String>(buildString);
			cleanCommand.addAll(buildFlags);
			cleanCommand.set(verbIndex, OcpidevVerb.clean.getVerb());
			List<String> shortCmd = cleanCommand.subList(3, cleanCommand.size());
    		shortClean =  shortCmd.toString();
    		shortClean = shortClean.replace(",", " ");
		}
		return cleanCommand;
	}
	protected static ArrayList<String> baseCmd = null;
	{
		if(baseCmd == null){
			baseCmd	 = new ArrayList<String>();	
			baseCmd.add("ocpidev");
			baseCmd.add("-d");
		}
	}
	protected void createBuildString() {
		buildString = new ArrayList<String>(baseCmd);
		buildString.add(asset.projectLocation.projectPath);
		verbIndex = buildString.size();
		buildString.add(null);
		buildString.addAll(asset.category.getOcpiBuildNowns());
		
		if(asset.buildName != null) {
			buildString.add(asset.buildName);
		}
		
		if(asset.category == OpenCPICategory.worker || asset.category == OpenCPICategory.test){
			buildString.add("-l");
			buildString.add(asset.libraryName);
		}
	}

	public static List<ExecutionAsset> createBuildAssets(OcpidevVerb verb, UserBuildSelections selections) {
		List<String> hdlbuildList = new ArrayList<String>();
		List<String> rccbuildList = new ArrayList<String>();
		BuildTargetSelections buildSelects = selections.buildTargetSelections;
		assembleRccPlaformList(buildSelects.rccBldSelects, rccbuildList);
		assembleHdlBuildList(buildSelects.isHdlPlatforms, buildSelects.hdlBldSelects, hdlbuildList);
		
		ArrayList<ExecutionAsset> buildAssets = new ArrayList<ExecutionAsset>(selections.assetSelections.size());
		for(AngryViperAsset selection : selections.assetSelections) {
			List<String> buildFlagList = creatBuildTagSequence(selection, hdlbuildList, rccbuildList);
			BuildExecAsset bldAsset = new BuildExecAsset(selection, buildFlagList, buildFlagList);
			if(selection.category.isCleanExpensive() && hdlbuildList.size() > 0) {
				AuthoringModel m = AuthoringModel.getAuthoringModel(bldAsset.asset);
				if(m == AuthoringModel.HDL) {
					bldAsset.setHdlBuild(true);
				}
			}
			// go ahead and setup the command.
			bldAsset.getCommand(verb, selections.noAssemblies);
			buildAssets.add(bldAsset);
		}
		return buildAssets;
	}
	
	public static List<String>  creatBuildTagSequence(AngryViperAsset selection, List<String> hdlbuildList, List<String> rccbuildList) {
		List<String> tagList = new ArrayList<String>();
		if(selection.category == OpenCPICategory.assemblies || selection.category == OpenCPICategory.assembly) {
			tagList.addAll(hdlbuildList);
		}
		else if(selection.category == OpenCPICategory.worker) {
			if(selection.assetName.endsWith("rcc")) {
				tagList.addAll(rccbuildList);
			}else {
				tagList.addAll(hdlbuildList);
			}
		}
		else {
			tagList.addAll(hdlbuildList);
			tagList.addAll(rccbuildList);
		}
		return tagList;
	}
	
	protected static void assembleHdlBuildList(boolean hdlPlatforms, String[] selections, List<String> list){
		if(selections == null || selections.length == 0) return; 
		String arg;
		if(hdlPlatforms) {
			arg = "--hdl-platform";
		}
		else {
			arg = "--hdl-target";
		}
		for(int i = 0; i < selections.length; i++){
			list.add(arg);
			list.add(selections[i]);
		}
	}

	protected static void assembleRccPlaformList(String[] selections, List<String> list){
		if(selections == null || selections.length == 0) return; 
		for(int i = 0; i < selections.length; i++){
			list.add("--rcc-platform");
			list.add(selections[i]);
		}
	}

}
