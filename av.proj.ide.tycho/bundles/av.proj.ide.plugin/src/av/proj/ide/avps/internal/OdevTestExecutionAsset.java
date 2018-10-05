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
import av.proj.ide.internal.OcpidevVerb;
import av.proj.ide.internal.OpenCPICategory;

public class OdevTestExecutionAsset extends ExecutionAsset {
	protected static String format = "%s/%s";
	protected static String formatTest = "Tests=%s";
	
	public String remoteSysExport = null;
	
	public OdevTestExecutionAsset(AngryViperAsset test, UserTestSelections selections) {
		asset = test;
		command = new ArrayList<String>(testCmd);
		StringBuilder sb = new StringBuilder("ocpidev run test ");
		
		command.set(3, asset.assetName);
		command.set(5, asset.projectLocation.projectPath);
		sb.append(asset.assetName);
		
		command.add(asset.libraryName);
		sb.append(" -l ");
		sb.append(asset.libraryName);
		
		if(selections.testMode != null) {
			command.add("--mode");
			sb.append(" --mode ");
			command.add(selections.testMode.toString());
			sb.append(selections.testMode.toString());
		}
		sb.append("\n");
		
		String[] hdlPlatforms = selections.buildTargetSelections.hdlBldSelects;
		if(selections.testMode == TestMode.gen || selections.testMode == TestMode.gen_build) {
			addPlatforms(sb, "--hdl-platform", hdlPlatforms);
		}
		else {
			addPlatforms(sb, "--only-platform", hdlPlatforms);
		}

		String[] rccPlatforms = selections.buildTargetSelections.rccBldSelects;
		if(selections.testMode == TestMode.gen || selections.testMode == TestMode.gen_build) {
			addPlatforms(sb, "--rcc-platform", rccPlatforms);
		}
		else {
			addPlatforms(sb, "--only-platform", rccPlatforms);
		}

		if(selections.accumulateErrors || selections.keepSimulations || selections.runViewScript) {
			sb.append("\n");
		}
		if(selections.accumulateErrors) {
			command.add("--accumulate-errors");
			sb.append(" --accumulate-errors");
		}
		if(selections.keepSimulations) {
			command.add("--keep-simulations");
			sb.append(" --keep-simulations");
		}
		if(selections.runViewScript ) {
			command.add("--view");
			sb.append(" --view");
		}
		addTestCases(sb, selections.testCaseList);
		addRemotes(sb, selections.remoteList);
		shortCmd = sb.toString();
	}
	
	private void addRemotes(StringBuilder sb, String[] remoteList) {
		if(remoteList == null)return;
		
		sb.append("\n remotes:");
		for(String remote : remoteList) {
			command.add("--remotes");
			command.add(remote);
			sb.append(" ");
			sb.append(remote);
		}
	}

	private void addTestCases(StringBuilder sb, String[] testCaseList) {
		if(testCaseList == null)return;
		

		for(String caseList : testCaseList) {
			String[] cases;
			// see if commas were used as requested.
			// if it is go with it, if not try spaces.
			
			if(caseList.indexOf(',') == -1) {
				if(caseList.indexOf(' ') > -1) {
					cases = caseList.split(" ");
				}
				else {
					cases = new String[1];
					cases[0] = caseList;
				}
			}
			else {
				cases = caseList.split(",");
			}
			
			for(String testCase : cases) {
				if(testCase.isEmpty()) continue;
				testCase = testCase.trim();
				if(testCase.isEmpty()) continue;
				command.add("--case");
				command.add(testCase);
				sb.append(' ');
				sb.append(testCase);
			}
		}
	}

	private void addPlatforms(StringBuilder sb, String platformFlag, String[] platforms) {
		if(platforms != null && platforms.length > 0) {
			command.add(platformFlag);
			sb.append(" " + platformFlag);
			command.add(platforms[0]);
			sb.append(" ");
			sb.append(platforms[0]);
			for ( int i = 1; i < platforms.length; i++) {
				command.add(platformFlag);
				command.add(platforms[i]);
				sb.append(" " + platformFlag);
				sb.append(" ");
				sb.append(platforms[i]);
			}
		}
	}

	@Override
	public List<String> getCommand(OcpidevVerb verb, Boolean flag) {
		if(verb == OcpidevVerb.run) {
			return command;
		}
		return null;
	}

	public static List<ExecutionAsset> createTestAssets(OcpidevVerb verb, UserTestSelections selections) {
		
		ArrayList<ExecutionAsset> testExecs = new ArrayList<ExecutionAsset>();
		boolean tryingHdlTests = false;
		for(AngryViperAsset test : selections.assetSelections) {
			if(test.category == OpenCPICategory.test) {
				OdevTestExecutionAsset testex = 
						new OdevTestExecutionAsset(test, selections);
				testExecs.add(testex);
			}
			else if(test.category == OpenCPICategory.hdlTest) {
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
			testCmd.add("ocpidev");
			testCmd.add("run");
			testCmd.add("test");
			testCmd.add(null);
			testCmd.add("-d");
			testCmd.add(null);
			testCmd.add("-l");
		}
	}
	
	@Override
	public File getExecutionDir() {
		return null;
	}

}
