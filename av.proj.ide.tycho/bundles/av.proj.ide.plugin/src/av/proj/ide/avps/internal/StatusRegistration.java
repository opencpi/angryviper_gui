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

import java.io.PrintStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import av.proj.ide.internal.AngryViperAsset;
import av.proj.ide.internal.AssetDetails.AuthoringModel;
import av.proj.ide.internal.OcpidevVerb;

/***
 * Part of the status notification interface to setup a new status bar or re-run an
 * existing execution. StatusRegistration and OcpiBuildStatus integrate with the layout
 * of the displayed status line and the information placed in each column.
 *
 */
public class StatusRegistration {
	protected OcpidevVerb verb;
	String consoleName;
	/***
	 * The main status line has 3 columns.
	 *  column 1 (idx 0) - consists of project & asset information.
	 *  column 2 is a summary of the build targets
	 *  column 3 has date/time and the verb.  This line is updated later with the total duration.
	 */
	String [] statusLineEntries = new String[3];
	
	/***
	 * Each asset in the build configuration gets a status line in the details section.
	 * Following a similar convention to the main status line, these have 3 columns.
	 *  column 1 (idx 0) - the asset.
	 *  column 2 is a summary of the applicable build targets
	 *  column 3 hold start and finish time for the asset build/run.
	 */
	String[][] detailEntries;
	
	/***
	 * The console config summary for the console is constructed here (for practical reasons).
	 * This information is printed to the console when the execution starts.
	 */
	String configSummary = null;
	
	public OcpidevVerb getVerb() {
		return verb;
	}

	public String getConsoleName() {
		return consoleName;
	}

	public String[] getStatusLineEntries() {
		return statusLineEntries;
	}

	public String[][] getDetailEntries() {
		return detailEntries;
	}

	public StatusRegistration(OcpidevVerb initialVerb, List<ExecutionAsset> exAssets, String[] hdlTargets, String[] rccTargets) {
		// Initialize the main status line.
		
		// initialize column 3.
		resetVerb(initialVerb);
		StringBuilder configSummary = new StringBuilder("Configuration:\n");
		configSummary.append(verb.getVerb());
		configSummary.append(" ");

		StringBuilder sb = null;
		StringBuilder lineSb = new StringBuilder();
		String rccLine = null;
		String hdlLine = null;
		String summaryLine = null;

		// Assemble target information
		if(hdlTargets.length > 0 && rccTargets.length > 0) {
			sb = new StringBuilder();
			lineSb.append("HDL: ");
			addTargets(hdlTargets, lineSb);
			hdlLine = lineSb.toString();
			sb = lineSb;
			lineSb = new StringBuilder();
			sb.append(" ");
			lineSb.append("RCC: ");
			addTargets(rccTargets, lineSb);
			rccLine = lineSb.toString();
			sb.append(rccLine);
			summaryLine = sb.toString();
		}
		else {
			if(hdlTargets.length > 0) {
				lineSb.append("HDL: ");
				addTargets(hdlTargets, lineSb);
				hdlLine = lineSb.toString();
				summaryLine = hdlLine;
			}
			if(rccTargets.length > 0) {
				lineSb.append("RCC: ");
				addTargets(rccTargets, lineSb);
				rccLine = lineSb.toString();
				summaryLine = rccLine;
			}
		}
		statusLineEntries[1] = summaryLine;

		// Assemble the summary column information and the details entries.
		int count = exAssets.size();
		switch(count) {
		case 1:
			statusLineEntries[0] = formatAsset(exAssets.get(0).getAsset());
			
			detailEntries = new String[1][3];
			detailEntries[0][2] = "BEGIN";
			configSummary.append(statusLineEntries[0]); configSummary.append(" ");
			configSummary.append(statusLineEntries[1]); configSummary.append("\n");
			break;
		case 2:
			sb = new StringBuilder();
			String entry1 = formatAsset(exAssets.get(0).getAsset());
			sb.append(entry1);
			sb.append("\n");
			String entry2 = formatAsset(exAssets.get(1).getAsset());
			sb.append(entry2);
			statusLineEntries[0] = sb.toString();
			addDetailsEntries(exAssets, hdlLine, rccLine);
			configSummary.append(statusLineEntries[1]); configSummary.append("\n");
			break;
		case 0:
			break;
		default:
			statusLineEntries[0] = formatCollection(exAssets);
			addDetailsEntries(exAssets, hdlLine, rccLine);
			configSummary.append(statusLineEntries[1]); configSummary.append("\n");
		}
		if(detailEntries.length > 1) {
			for(String[] detail : detailEntries) {
				configSummary.append(detail[0]); configSummary.append("\n");
			}
		}
		configSummary.append("\n");
		this.configSummary = configSummary.toString();
	}
	public void resetVerb(OcpidevVerb newverb) {
		verb = newverb;
    	Date sTime = new Date();
		String curTime = DateFormat.getTimeInstance().format(sTime);
    	String dateFormat = "M-dd-yy";
    	SimpleDateFormat dateFormatter = new SimpleDateFormat(dateFormat, new Locale("en"));
    	String curDate = dateFormatter.format(sTime);
   	
		statusLineEntries[2] = curTime + " " + curDate + " " +verb.toString();
	}

	public void printConfig(PrintStream log) {
		if(configSummary != null) {
			log.print(configSummary);
		}
	}
	
	private void addDetailsEntries(List<ExecutionAsset> exAssets, String hdlLine, String rccLine) {
		detailEntries = new String[exAssets.size()][3];
		int i = 0;
		String tab = "  ";
		AngryViperAsset asset;
		for(ExecutionAsset exAsset : exAssets) {
			asset = exAsset.getAsset();
			detailEntries[i][0] = tab + asset.qualifiedName;
			detailEntries[i][2] = "PENDING";
			AuthoringModel model  = AuthoringModel.getAuthoringModel(asset);
			switch(model) {
			case HDL:
				detailEntries[i][1] = hdlLine;
				break;
			case NA:
				detailEntries[i][1] = "Mixed HDL and RCC";
				break;
			case RCC:
				detailEntries[i][1] = rccLine;
				break;
			}
			i++;
		}
	}

	private void addTargets(String[] buildTargets, StringBuilder sb) {
		int count = 0;
		int last = buildTargets.length -1;
		for(String target : buildTargets) {
			sb.append(target);
			if(count < last) {
				sb.append(" ");
			}
			count++;
		}
	}

	private String formatCollection(List<ExecutionAsset> exAssets) {
		StringBuilder sb = new StringBuilder();
		int multipleCount = 0;
		int hdlCount = 0;
		int rccCount = 0;
		int projectCount = 1;
		String lastProject = exAssets.get(0).getAsset().projectLocation.projectName;
		for(ExecutionAsset exAsset : exAssets) {
			AngryViperAsset asset = exAsset.getAsset();
			if( !  lastProject.equals(asset.projectLocation.projectName)) {
				projectCount++;
				lastProject = asset.projectLocation.projectName;
			}
			
			AuthoringModel model  = AuthoringModel.getAuthoringModel(asset);
			switch(model) {
			case HDL:
				hdlCount++;
				break;
			case NA:
				multipleCount++;
				break;
			case RCC:
				rccCount++;
				break;
			}
		}
		String space = " ";
		if(projectCount > 1) {
			sb.append("Multiple Projects ");
		}
		else {
			String label = exAssets.get(0).getAsset().projectLocation.packageId;
			sb.append(label);
		}
		if(multipleCount > 0 && hdlCount > 0 && rccCount > 0) {
			// put it on the top line, other 2 fall below.
			sb.append(" Mixed Builds: "); sb.append(multipleCount);
			sb.append("\n ");
		}
		else {
			sb.append("\n ");
			if(multipleCount > 0) {
				sb.append(" Mixed Builds: "); sb.append(multipleCount);
			}
		}

		// Gather one of the 2 remaining.
		if(hdlCount > 0) {
			sb.append(space); sb.append(" HDL Builds: "); sb.append(hdlCount);
		}
		if(rccCount > 0) {
			sb.append(space); sb.append(" RCC Builds: "); sb.append(rccCount);
		}
		return sb.toString();
	}

	private String formatAsset(AngryViperAsset asset) {
		return asset.qualifiedName;
	}

	public StatusRegistration(UserTestSelections selections) {
		verb = selections.verb;
	}
	
//	public static void main(String[] args) {
//		List<AngryViperAsset> assets = new ArrayList<AngryViperAsset>();
//		ProjectLocation location = new ProjectLocation("assets", "~/assets");
//		location.packageId = "ocpi.assets";
//		String[] temp = {"worker1.rcc", "worker2.hdl"};
//		for (int i = 0; i< 2; i++) {
//			AngryViperAsset asset = OpenCPIAssetFactory.createOcpiAsset(temp[i], "dsp_comps", OpenCPICategory.worker, location);
//			assets.add(asset);
//		}
//		UserBuildSelections sels = new UserBuildSelections();
//		sels.verb = OcpidevVerb.build;
//		sels.assetSelections = assets;
//		String [] hdls = {"hone", "htwo"};
//		String [] rccs = {"rone", "rtwo"};
//		BuildTargetSelections sels1 = new BuildTargetSelections();
//		sels1.hdlBldSelects = hdls;
//		sels1.rccBldSelects = rccs;
//		sels1.isHdlPlatforms = true;
//		sels.buildTargetSelections = sels1;
//		List<ExecutionAsset> exAssets = BuildExecAsset.createBuildAssets(sels.verb, sels);
//		StatusRegistration reg = new StatusRegistration(sels.verb, exAssets, hdls, rccs);
//		System.out.println("done");
//	}

}
