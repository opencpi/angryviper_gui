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
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.List;

import org.eclipse.ui.console.MessageConsole;

import av.proj.ide.internal.AngryViperAsset;
import av.proj.ide.internal.OcpidevVerb;

public class CommandExecutor {
	Thread executionTread;
	// Arrays are a hack to eliminate the final variable issue
	// in a thread.
	boolean[] threadStopped = new boolean[] {false};
	boolean[] buildProblem = new boolean[] {false};
	
	protected static Thread inheritIO(final InputStream src, final PrintStream dest) {
	  Thread t = new Thread(new Runnable() {
	        public void run() {
	        	int result;
	        	try {
					while((result = src.read())> -1) {
						dest.print((char)result);
					}
				} catch (IOException e) {
				}
	        }
	    });
		return t;
	}
	
	public void stopExecution() {
		if(executionTread != null && executionTread.isAlive()) {
			executionTread.interrupt();
		}
	}
	
	public void executeCommandSet(ExecutionComponents components, OcpidevVerb verb, Boolean flag) {
		// Reruns - reset these in case they we set before.
		threadStopped[0] = false;
		buildProblem[0] = false;
		
		executionTread = new Thread(new Runnable() {
	        public void run() {
	        	OcpiBuildStatus status = new OcpiBuildStatus();
	        	PrintStream console = new PrintStream(components.bldConsole.newMessageStream());
	        	
	        	List<String> cmd;
				for(ExecutionAsset exAsset : components.executionAssets) {
					if(threadStopped[0] == true)
						break;
		
					cmd = exAsset.getCommand(verb, flag);
					String c = cmd.toString();
					c = c.replaceAll(", ", " ");
	        		console.println(c + "\n");
	        		
					AngryViperAsset asset = exAsset.getAsset();

	    			if(asset.buildName != null) {
	    				status.asset = asset.buildName;
	    			}
	    			else {
	    				status.asset = asset.assetName;
	    			}
	        		status.buildString = exAsset.getDisplayString(verb);
	        		status.project = asset.projectLocation.projectName;
	        		status.lib = asset.libraryName;
	        		
	        		File executionLocation = exAsset.getExecutionDir();
	        		ProcessBuilder pb = new ProcessBuilder(cmd);
	        		pb.redirectErrorStream(true);
	        		if(executionLocation != null) {
		        		pb.directory(executionLocation);
	        		}
		        	Thread ot = null;
		        	Process process = null;
	        		
		        	try {
						process = pb.start();

						ot = inheritIO(process.getInputStream(), console);
						ot.start();
						
						components.statusMonitor.updateBuildStatus(components.executionNumber, status);
		        		int result = process.waitFor();
		        		ot.join();
		        		console.println("== > Command completed. Rval = " + result);
		        		if(result != 0) {
		        			buildProblem[0] = true;
//		                  Scanner sc = new Scanner(process.getErrorStream());
//		                  while (sc.hasNextLine()) {
//		                	  console.println(sc.nextLine());
//		                  }
//		                  sc.close();
			        	}
					} catch (IOException  e) {
						AvpsResourceManager.getInstance().writeToNoticeConsole(e.getStackTrace().toString());
					} catch (InterruptedException e) {
						ot.interrupt();
						threadStopped[0] = true;
					}
	        	}
	        	console.flush();
	        	console.close();
	        	if( ! threadStopped[0]){
	        		components.statusMonitor.setCompletedStatusEntry(components.executionNumber, ! buildProblem[0]);
	        	}
	        }
	    });
		executionTread.start();
		
	}

	public boolean isActive() {
		return(executionTread != null && executionTread.isAlive());
	}
	
	public static String getCommandResult(String[] command, MessageConsole cons, StringBuilder errMessage) {
    	PrintStream console = new PrintStream(cons.newMessageStream());
		String c = command.toString();
		c = c.replaceAll(", ", " ");
		console.println(c + "\n");
		ProcessBuilder pb = new ProcessBuilder(command);
		//pb.redirectErrorStream(true);
		
		Process process;
		try {
			process = pb.start();
			int result = process.waitFor();
			InputStream in = process.getInputStream();
			if(result == 0) {
				StringBuilder sb = new StringBuilder();
				while((result = in.read())> -1) {
					sb.append((char)result);
				}
				return sb.toString();
				
			}
			else {
				while((result =in.read())> -1) {
					console.print((char)result);
					errMessage.append((char)result);
				}
			}
		} catch (IOException | InterruptedException e) {
			StackTraceElement[] trace = e.getStackTrace();
			for(StackTraceElement elem : trace) {
				console.println(elem.toString());
			}
		}
		return null;
	}

	public static boolean executeCommand(List<String> command, MessageConsole cons, StringBuilder errMessage) {
    	PrintStream console = new PrintStream(cons.newMessageStream());
		String c = command.toString();
		c = c.replaceAll(", ", " ");
		console.println(c + "\n");
		ProcessBuilder pb = new ProcessBuilder(command);
		pb.redirectErrorStream(true);
		boolean success = true;
		
		Process process;
		try {
			process = pb.start();
			int result = process.waitFor();
			InputStream in = process.getInputStream();
			if(result == 0) {
				while((result = in.read())> -1) {
					console.print((char)result);
				}
				
			}
			else {
				success = false;	
				while((result =in.read())> -1) {
					console.print((char)result);
					errMessage.append((char)result);
				}
			}
		} catch (IOException | InterruptedException e) {
			StackTraceElement[] trace = e.getStackTrace();
			for(StackTraceElement elem : trace) {
				console.println(elem.toString());
			}
		}
		return success;
	}
}
