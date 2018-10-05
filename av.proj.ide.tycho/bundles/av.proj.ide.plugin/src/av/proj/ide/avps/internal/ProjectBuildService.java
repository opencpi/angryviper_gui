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

import java.util.HashMap;
import java.util.List;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.console.MessageConsole;

import av.proj.ide.internal.OcpidevVerb;

/**
 * This class supports building and running requests coming from the other 
 * views. There are a number of things done in the class to make re-runs
 * more efficient.  Things are persisted so a re-run is not a total re-
 * construction at the beginning and the object dump at the end.  The service
 * also has to tie together a number of things to allow these re-runs: console
 * status bar, build number, original selections, etc. Plus, since a re-run
 * can occur from 3 of the views there is communications back and forth 
 * required. Finally, since there is a limit to the number of consoles
 * the service has to juggle when to return consoles to the pool.
 *
 */
public class ProjectBuildService {
	
	private static ProjectBuildService instance = null;
	private int nextExecutionNumber = 1;
	private HashMap<Integer, ExecutionComponents> registeredExecutions = new HashMap<Integer, ExecutionComponents>();
	private HashMap<Integer, Integer> buildConfigurations = new HashMap<Integer, Integer>();
	
	private ProjectBuildService() {}
	
    public static ProjectBuildService getInstance() {
    	if(instance == null) {
    		instance = new  ProjectBuildService();
    	}
    	return instance;
    }
    
    public interface ProvideBuildSelections {
    	public 	UserBuildSelections getBuildSelections();
    	public boolean haveBuildTargetsChanged();
    }

    /***
     * Late in 1.3 Dev implemented build configs.  This simplified how 
     * the various perspectives used the service and it made the service more 
     * cohesive.
      */
    public Integer getBuildByConfiguration(UserBuildSelections userSelections) {
    	if(buildConfigurations.containsKey(userSelections.getConfigurationHash()) ) {
    		Integer buildNumber = buildConfigurations.get(userSelections.getConfigurationHash());
    		if(haveBuildNumber(buildNumber)) {
    			return buildNumber;
    		}
    		else {
    			// build number is gone remove the config
    			buildConfigurations.remove(userSelections.getConfigurationHash());
    		}
    	}
    	return -1;
    }
    
    /***
     * Make sure user selected enough to do the build request.
     */
    public boolean isBuildRequestOk(UserBuildSelections userSelections) {
		boolean setupError = false;
		StringBuilder msg = new StringBuilder();
		if( userSelections.assetSelections == null || userSelections.assetSelections.size() == 0 ) {
			setupError = true;
			msg.append("No assets selected to build.\n");
		}
		BuildTargetSelections selections = userSelections.buildTargetSelections;
		if(		(userSelections.verb != OcpidevVerb.clean
			&& (selections.rccBldSelects == null || selections.rccBldSelects.length == 0) 
			&& (selections.hdlBldSelects == null || selections.hdlBldSelects.length == 0))) {
			setupError = true;
			msg.append("No platforms or targets selected to build.");
		}
		
		if(setupError) {
			AvpsResourceManager.getInstance().writeToNoticeConsole(msg.toString());
		}
		return ! setupError;
    }

    /***
	 * This is the entry point method for a new build or clean request. A
	 * buildNumber is assigned, resources are requested such a a console and a
	 * status bar and linked together, execution components are constructed and
	 * the execution is submitted if everything is in order. The build number is
	 * return to the originator where it is expected to be maintained for
	 * re-runs. A -1 is returned if a build number can't be assigned.
	 */
	public Integer processBuildRequest(UserBuildSelections selections) {
		if( ! isBuildRequestOk(selections) ){
			return -1;
		}
		
		Integer buildNumber = getBuildByConfiguration(selections);
		if(buildNumber > -1) {
			ExecutionComponents buildComps = registeredExecutions.get(buildNumber);
			if(buildComps.commandExecutor.isActive()) {
				boolean result = MessageDialog.openConfirm(Display.getDefault().getActiveShell(),
					"You have an active build ( no " + buildNumber + ") currently running", "Do you want to launch a new one?");
			    if(! result){
	   				return buildNumber;
			    }
			    // Another run is setup.
 			}
			else {
				reRun(selections.verb, selections.noAssemblies, buildNumber);
				return buildNumber;
			}
		}
		
		List<ExecutionAsset> exAssets = BuildExecAsset.createBuildAssets(selections.verb, selections);
		MessageConsole bldConsole = AvpsResourceManager.getInstance().getNextConsole();
		if (bldConsole == null) {
			return -1;
		}
		
		if(selections.verb == OcpidevVerb.clean) {
			boolean bigClean = false;
			String bigCleanMessage = null;
			
			if(selections.buildTargetSelections.hdlBldSelects.length == 0 && 
			   selections.buildTargetSelections.rccBldSelects.length == 0) {
				for(ExecutionAsset exAsset : exAssets) {
					
					switch(exAsset.asset.category) {
					case project:
					case componentsLibrary:
					case library:
					case primitives:
					case platforms:
					case assemblies:
					case devices:
					case cards:
						bigClean = true;
						break;
					default:
						
					}
					
					if(bigClean) {
						bigCleanMessage = "You are about to clean everything under your selection(s).";
						break;
					}
				}

			}
			else if (selections.buildTargetSelections.hdlBldSelects.length > 0){
				for(ExecutionAsset exAsset : exAssets) {
					BuildExecAsset bea = (BuildExecAsset)exAsset;
					if(bea.isHdlBuildExpensive()) {
						bigClean = true;
						bigCleanMessage = "You are setup to clean HDL which could take a long time to rebuild.";
					}
				}
			}
			if(bigClean) {
				boolean result = MessageDialog.openConfirm(Display.getDefault().getActiveShell(),
						            "Are you Sure?", bigCleanMessage);
			    if(! result) {
				    return -1;
				 }
			}
		}
		
		Integer thisBuildNumber = new Integer(nextExecutionNumber++);
		StatusNotificationInterface statusMonitor = AvpsResourceManager.getInstance().getStatusMonitor();
		if(statusMonitor != null) {
			statusMonitor.registerBuild(thisBuildNumber, selections.verb, bldConsole.getName(), selections.buildDescription);
		}
		
		
		ExecutionComponents buildComps = new ExecutionComponents(thisBuildNumber,bldConsole, statusMonitor);
		registeredExecutions.put(thisBuildNumber, buildComps);
		buildConfigurations.put(selections.getConfigurationHash(), thisBuildNumber);
		
		buildComps.setExecutionAssets(exAssets);
		CommandExecutor ex = new CommandExecutor();
		buildComps.commandExecutor = ex;
		
		ex.executeCommandSet(buildComps, selections.verb, selections.noAssemblies);
		
		return thisBuildNumber;
	}

	/***
	 * Note that the verb and no-assemblies flags can change from run to run thus that are
	 * variables to the method.
	 */
	public void reRun(OcpidevVerb verb, Boolean noAssemblies, Integer myBuildNumber) {
		ExecutionComponents buildComps = registeredExecutions.get(myBuildNumber);
		if(buildComps == null)
			return;
		
		if(verb == OcpidevVerb.clean) {
			List<ExecutionAsset> executionAssets = buildComps.executionAssets;
			boolean bigClean = false;
			for(ExecutionAsset exAsset : executionAssets) {
				BuildExecAsset bea = (BuildExecAsset)exAsset;
				if(bea.isHdlBuildExpensive()) {
					bigClean = true;
				}
			}
			if(bigClean) {
			   boolean result = MessageDialog.openConfirm(Display.getDefault().getActiveShell(), "Are you Sure?", "You are cleaning HDL which could take a long time to rebuild.");
			   if(! result) {
				   return;
				}
			}
		}
		buildComps.statusMonitor.restartBuild(myBuildNumber, verb);
		AvpsResourceManager.getInstance().bringConsoleToView(buildComps.bldConsole.getName());
		buildComps.commandExecutor.executeCommandSet(buildComps, verb, noAssemblies);
	}
	
	/***
	 * Entry point to run selected tests.  The mechanics are much like the build request.
	 * The build number is returned for future re-runs.
	 */
	public Integer processTestRequest(UserTestSelections selections, Integer runNumber) {
		// TODO-make reuse more efficient. That was where the OcpidevCommand class was going.
		List<ExecutionAsset> exAssets  = OdevTestExecutionAsset.createTestAssets(selections.verb, selections);
		if(exAssets.isEmpty()) {
			AvpsResourceManager.getInstance().writeToNoticeConsole("No viable build test configuration is provided in your selections.");
			return -1;
		}
		ExecutionComponents buildComps = null;
		if(runNumber != null) {
			buildComps = registeredExecutions.get(runNumber);
			if(buildComps != null) {
				buildComps.setExecutionAssets(exAssets);
				buildComps.statusMonitor.restartBuild(runNumber, selections.verb);
				AvpsResourceManager.getInstance().bringConsoleToView(buildComps.bldConsole.getName());
			}
		}
		else {
			runNumber = new Integer(nextExecutionNumber++);
			StatusNotificationInterface statusMonitor = AvpsResourceManager.getInstance().getStatusMonitor();
			MessageConsole bldConsole = AvpsResourceManager.getInstance().getNextConsole();
			if (bldConsole == null) {
				return -1;
			}
			if(statusMonitor != null) {
				statusMonitor.registerBuild(runNumber, selections.verb, bldConsole.getName(), "Unit Tests # " + runNumber );
			}
			
			buildComps = new ExecutionComponents(runNumber,bldConsole, statusMonitor);
			registeredExecutions.put(runNumber, buildComps);
			CommandExecutor ex = new CommandExecutor();
			buildComps.commandExecutor = ex;
			buildComps.setExecutionAssets(exAssets);
		}
		buildComps.commandExecutor.executeCommandSet(buildComps, selections.verb, true);
		return runNumber;
	}
	
	/**
	 * This method supports build number and resource management.  The status monitor 
	 * is the key component in the build configuration. When the user removes a monitor
	 * then the build configuration is no longer valid and this service cleans up resources.
	 */
	
	public void deRegisterStatusMontor(Integer myBuildNumber) {
		ExecutionComponents buildComps = registeredExecutions.get(myBuildNumber);
		if(buildComps != null) {
			AvpsResourceManager.getInstance().returnConsole(buildComps.bldConsole);			
			registeredExecutions.remove(myBuildNumber);
		}
	}

	public void stopBuild(Integer buildNumber) {
		ExecutionComponents build =  registeredExecutions.get(buildNumber);
		if(build != null) {
			build.commandExecutor.stopExecution();
		}
	}

	/**
	 * The following allows the project view to obtain the build parameters from
	 * the ops panel.
	 */
	private ProvideBuildSelections buildselectionProvider = null;
	public void setBuildSelectionProvider(ProvideBuildSelections provideBuildSelections) {
		buildselectionProvider = provideBuildSelections;
	}

	public ProvideBuildSelections getBuildselectionProvider() {
		return buildselectionProvider;
	}

	public boolean haveBuildNumber(Integer buildNumber) {
		return registeredExecutions.containsKey(buildNumber);
	}
}
