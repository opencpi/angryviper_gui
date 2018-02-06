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

public class TestExecutionAsset extends ExecutionAsset {
	protected File projectDirectory;
	protected static String format = "%s/%s";
	protected static String formatTest = "Tests=%s";
	
	public TestExecutionAsset(AngryViperAsset test, File projDirectory, String[] hdlPlatforms, String[] rccPlatforms) {
		asset = test;
		projectDirectory = projDirectory;
		command = new ArrayList<String>(testCmd);
		StringBuilder sb = new StringBuilder("runtests -C ");
		String lib = asset.libraryName;
		if( !  "components".equalsIgnoreCase(lib)) {
			lib = String.format(format, "components", lib);
		}
		command.add(lib);
		sb.append(lib);
		sb.append(" Tests=");
		command.add(String.format(formatTest, asset.assetName));
		sb.append(asset.assetName);
		int platCount = 0;
		int hdlCount = 0;
		int rccCount = 0;
		if(hdlPlatforms != null) {
			platCount = hdlPlatforms.length;
			hdlCount = hdlPlatforms.length;
		}
		if(rccPlatforms != null) {
			platCount += rccPlatforms.length;
			rccCount = rccPlatforms.length;
		}
		
		//if(hdlPlayforms != null && hdlPlayforms.length > 0) {
		if(platCount > 0) {
			StringBuilder platforms = new StringBuilder("OnlyPlatforms=");
			sb.append(" OnlyPlatforms=");
			
			if(platCount == 1) {
				String platform = null;
				if(rccCount == 1) {
					platform = rccPlatforms[0];
				}
				else {
					platform = hdlPlatforms[0];
				}
				platforms.append(platform);
				sb.append(platform);
			}
			else {
				platforms.append("\"");
				sb.append("\"");
				
				if(hdlCount > 0){
					platforms.append(hdlPlatforms[0]);
					sb.append(hdlPlatforms[0]);
				}
				if(rccCount > 0){
					if(hdlCount == 0){
						platforms.append(rccPlatforms[0]);
						sb.append(rccPlatforms[0]);
					}
					else {
						platforms.append(" ");
						sb.append(" ");
						platforms.append(rccPlatforms[0]);
						sb.append(rccPlatforms[0]);
					}
				}
				
				for(int i=1; i < hdlCount; i++) {
					platforms.append(" ");
					sb.append(" ");
					platforms.append(hdlPlatforms[i]);
					sb.append(hdlPlatforms[i]);
				}
				for(int i=1; i < rccCount; i++) {
					platforms.append(" ");
					sb.append(" ");
					platforms.append(rccPlatforms[i]);
					sb.append(rccPlatforms[i]);
				}
				platforms.append("\"");
				sb.append("\"");
			}
			command.add(platforms.toString());
		}
		shortCmd = sb.toString();
	}
	
	@Override
	public List<String> getCommand(CommandVerb verb, Boolean flag) {
		if(verb == CommandVerb.runtest) {
			return command;
		}
		return null;
	}

	@Override
	public File getExecutionDir() {
		return projectDirectory;
	}
	
	public static List<ExecutionAsset> createTestAssets(CommandVerb verb, UserBuildSelections selections) {
		
		ArrayList<ExecutionAsset> testExecs = new ArrayList<ExecutionAsset>();
		boolean tryingHdlTests = false;
		for(AngryViperAsset test : selections.assetSelections) {
			if(test.category == OcpiAssetCategory.test) {
				File dir = new File(test.location.projectPath);
				TestExecutionAsset testex = 
						new TestExecutionAsset(test, dir, selections.buildTargetSelections.hdlBldSelects, selections.buildTargetSelections.rccBldSelects);
				testExecs.add(testex);
			}
			else if(test.category == OcpiAssetCategory.hdlTest) {
				tryingHdlTests = true;
			}
		}
		if(tryingHdlTests) {
			AvpsResourceManager.getInstance().writeToNoticeConsole("The IDE only supports running component tests.  HDL hardware tests can't run.");
		}
		return testExecs;
	}
	
	protected static List<String> testCmd = null;
	{  
		if(testCmd == null) {
			testCmd = new ArrayList<String>();
			testCmd.add("make");
			testCmd.add("runtests");
			testCmd.add("-C");
		}
	}

}
