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

package av.proj.ide.swt;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.wb.swt.SWTResourceManager;

import av.proj.ide.avps.internal.AngryViperAsset;
import av.proj.ide.avps.internal.AngryViperAssetService;
import av.proj.ide.avps.internal.AngryViperAssetService.HdlPlatformUpdate;
import av.proj.ide.avps.internal.AvpsResourceManager;
import av.proj.ide.avps.internal.BuildTargetSelections;
import av.proj.ide.avps.internal.EnvBuildTargets.HdlPlatformInfo;
import av.proj.ide.avps.internal.EnvBuildTargets.HdlVendor;
import av.proj.ide.avps.internal.EnvBuildTargets.RccPlatformInfo;
import av.proj.ide.avps.internal.ExecutionAsset.CommandVerb;
import av.proj.ide.avps.internal.ProjectBuildService;
import av.proj.ide.avps.internal.ProjectBuildService.ProvideBuildSelections;
import av.proj.ide.avps.internal.SelectionsInterface;
import av.proj.ide.avps.internal.UserBuildSelections;

public class MainOperationSwtDisplayV1 extends Composite implements SelectionsInterface, ProvideBuildSelections {
	Composite headerArea;
	BuildSelectionSidePanel buildSelectPanel;
	SelectionPanel selectionPanel;
	
	protected boolean selectionsChanged = false;
	protected boolean buildTargetsChanged = false;
	protected Set<AngryViperAsset> assetSelections;
	protected Map<String, Integer> processingNumbers;

	/***
	 * Support the interfaces.
	 */
	
	//ProvideBuildSelections
	@Override
	public 	UserBuildSelections getBuildSelections(){
		return  getUserSelections();
	}
	@Override
	public 	boolean haveBuildTargetsChanged(){
		return buildTargetsChanged;
	}
	
	//SelectionsInterface
	@Override
	public void dispose() {
		AvpsResourceManager mgr = AvpsResourceManager.getInstance();
		mgr.deRegisterSelectionReceivers((SelectionsInterface) this);
		super.dispose();
	}
	
	@Override
	public void addSelections(TreeItem[] items) {
		for(int i=0; i< items.length; i++) {
			TreeItem item = items[i];
			AngryViperAsset asset =  (AngryViperAsset)item.getData();
			if(assetSelections.contains(asset)) continue;
			
			assetSelections.add(asset);
			selectionsChanged = true;
			selectionPanel.text.setText("");
			if(selectionPanel.selectedComponents.indexOf(item) <1) {
				TreeItem copy = new TreeItem(selectionPanel.selectedComponents, SWT.NONE);
				makeCopy(item, copy);
			}
		}
	}
	private TreeItem[] emptyItems = new TreeItem[0];
	@Override
	public TreeItem[] getSelections() {
		return emptyItems;
	}

	public void setPanelColorScheme(AvColorScheme colorScheme) {
		headerArea.setBackground(colorScheme.getPrimary());
		buildSelectPanel.setPanelColorScheme(colorScheme);
		selectionPanel.setPanelColorScheme(colorScheme);
	}

	public MainOperationSwtDisplayV1(Composite parent, int style) {
		super(parent, SWT.BORDER);
		
		assetSelections = new HashSet<AngryViperAsset>();
		processingNumbers = new  HashMap<String, Integer> ();
		FormLayout layout = new FormLayout();
		layout.marginTop = 5;
		layout.marginBottom = 5;
		this.setLayout(layout);
		
		AvpsResourceManager.getInstance().registerSelectionReceivers((SelectionsInterface) this);
		ProjectBuildService.getInstance().setBuildSelectionProvider((ProvideBuildSelections)this);
		HdlPlatformUpdate platformUpdater = new HdlPlatformUpdate() {

			@Override
			public void addHdlPlatforms(List<HdlPlatformInfo> hdlPlatforms) {
				addPlatforms(hdlPlatforms);
			}

			@Override
			public void removeHdlPlatforms(List<HdlPlatformInfo> hdlPlatforms) {
				removePlatforms(hdlPlatforms);
			}
		};
		
		AngryViperAssetService.getInstance().registerHdlPlatformRefresh(platformUpdater);
		// ===================================================================
		//                   Header Section
		// ===================================================================
		
		
		headerArea = new Composite(this, SWT.BORDER_SOLID);
		headerArea.setLayout(new GridLayout(3, true) );
		new Label(headerArea, SWT.NONE);
		Label l = new Label(headerArea, SWT.NONE);
		l.setText("         ANGRYVIPER Operations Panel");
		l.setFont(SWTResourceManager.getFont("Cantarell", 11, SWT.BOLD));

		new Label(headerArea, SWT.NONE);
		
		// ===================================================================
		//                         Build Panel
		// ===================================================================

		buildSelectPanel = new BuildSelectionSidePanel(this, SWT.NONE);

		// ===================================================================
		//                         Selection Panel
		// ===================================================================
		selectionPanel = new SelectionPanel(this, SWT.NONE);
		// ===================================================================
		//                          Panel Layout 
		// ===================================================================
		FormData data;
		
		// Header Panel - top and right of buildPanel
		data = new FormData();
		data.top = new FormAttachment(0, 5);
		data.left = new FormAttachment(0, 5);
		data.right = new FormAttachment(100, -5);
		headerArea.setLayoutData(data);	

		// Build Panel - left side
		data = new FormData();
		data.top = new FormAttachment(headerArea, 5);
		data.left = new FormAttachment(0, 5);
		data.right = new FormAttachment(0, 170);
		data.bottom  = new FormAttachment(100, -10);
		buildSelectPanel.setLayoutData(data);

		// Selection Panel - central area, will grow out and down.
		data = new FormData();
		data.top = new FormAttachment(headerArea, 5);
		data.left = new FormAttachment(buildSelectPanel, 5);
		data.right = new FormAttachment(100, -5);
		data.bottom  = new FormAttachment(100, -5);
		selectionPanel.setLayoutData(data);
		
		populateBuildTypes();
		
	}
	
	public static String makeBuildLabel(UserBuildSelections userSelections)  {
		StringBuilder sb = new StringBuilder();
		BuildTargetSelections sels = userSelections.buildTargetSelections;
		int rccs = sels.rccBldSelects == null ? 0 : sels.rccBldSelects.length;
		int hdls = sels.hdlBldSelects == null ? 0 : sels.hdlBldSelects.length;
		if(rccs == 1) {
			sb.append(sels.rccBldSelects[0]);
		}
		else if(rccs > 1) {
			sb.append("rcc-" + rccs);
		}
		if(hdls == 1) {
			if(rccs > 0){
				sb.append(" ");
			}
			sb.append(sels.hdlBldSelects[0]);
		}
		else if(hdls > 1) {
			sb.append("hdl-" + hdls);
		}
		sb.append(",");
		int no = userSelections.assetSelections.size();
		if(no == 1) {
			sb.append(userSelections.assetSelections.get(0).assetName);
		}
		else if(no > 1) {
			sb.append(no + "-assets");
		}
		return sb.toString();
	}
	
	public static String makeRunLabel(UserBuildSelections userSelections)  {
		StringBuilder sb = new StringBuilder("Testing: ");
		BuildTargetSelections sels = userSelections.buildTargetSelections;
		int hdls = sels.hdlBldSelects == null ? 0 : sels.hdlBldSelects.length;
		if(hdls == 1) {
			sb.append(sels.hdlBldSelects[0]);
		}
		else if(hdls > 1) {
			sb.append(hdls +"-hdl-platforms,");
		}
		sb.append(" ");
		int no = userSelections.assetSelections.size();
		if(no == 1) {
			sb.append(userSelections.assetSelections.get(0).assetName);
		}
		else if(no > 1) {
			sb.append(no + "-selections");
		}
		return sb.toString();
	}
	
	public void doBuild(CommandVerb verb) {
		ProjectBuildService pb = ProjectBuildService.getInstance();
		if( processingStateChange(pb)) {
			doNewBuild(pb, verb);
		}
		else {
			Integer myBuildNumber = processingNumbers.get("build");
			if(myBuildNumber == null) {
				doNewBuild(pb, verb);
			}
			else {
				if(pb.haveBuildNumber(myBuildNumber)) {
					Boolean noAssemblies = true;
					if(selectionPanel.buildAssembliesButton.getSelection()) {
						noAssemblies = false;
					}
					pb.reRun(verb, noAssemblies, myBuildNumber);
				}
				else {
					processingNumbers.remove("build");
					doNewBuild(pb, verb);
				}
			}
		}
	}
	
	protected void doNewBuild(ProjectBuildService pb, CommandVerb verb) {
		UserBuildSelections userSelections = getUserSelections();
		if(userSelections.buildDescription == null) {
			userSelections.buildDescription = makeBuildLabel(userSelections);
		}
		userSelections.verb = verb;
		int buildNumber = pb.processBuildRequest(userSelections);
		if(buildNumber == -1) {
			return;
		}
		processingNumbers.put("build", buildNumber);
	}
	
	protected boolean processingStateChange(ProjectBuildService pb) {
		if( selectionsChanged || buildTargetsChanged) {
			selectionsChanged = false;
			buildTargetsChanged = false;
			processingNumbers.clear();
			return true;
		}
		return false;
		
	}
	
	public void doTestBuild(CommandVerb build) {
		ProjectBuildService pb = ProjectBuildService.getInstance();
		if( processingStateChange(pb)) {
			doNewTestBuild(pb, build);
		}
		else {
			Integer myBuildNumber = processingNumbers.get("buildtest");
			if(myBuildNumber == null) {
				doNewTestBuild(pb, build);
			}
			else {
				if(pb.haveBuildNumber(myBuildNumber)) {
					pb.reRun(build, true, myBuildNumber);
				}
				else {
					processingNumbers.remove("buildtest");
					doNewTestBuild(pb, build);
				}
			}
		}
	}

	protected void doNewTestBuild(ProjectBuildService pb, CommandVerb build) {
		UserBuildSelections userSelections = getUserSelections();
		if(userSelections.buildDescription == null) {
			userSelections.buildDescription = makeBuildLabel(userSelections);
		}
		userSelections.verb = build;
		int buildNumber = pb.processTestRequest(userSelections);
		if(buildNumber == -1) {
			return;
		}
		processingNumbers.put("buildtest", buildNumber);
	}
	

	public void doRun(CommandVerb verb) {
		ProjectBuildService pb = ProjectBuildService.getInstance();
		if( processingStateChange(pb)) {
			doNewRun(pb, verb);
		}
		else {
			Integer myRunNumber = processingNumbers.get("runtest");
			if(myRunNumber == null) {
				doNewRun(pb,verb);
			}
			else {
				if(pb.haveBuildNumber(myRunNumber)) {
					pb.reRun(verb, true, myRunNumber);
				}
				else {
					processingNumbers.remove("runtest");
					doNewRun(pb,verb);
				}
				pb.reRun(verb, true, myRunNumber);
			}
		}
	}
	
	protected void doNewRun(ProjectBuildService pb, CommandVerb verb) {
		UserBuildSelections userSelections = getUserSelections();
		if(userSelections.buildDescription == null) {
			userSelections.buildDescription = makeRunLabel(userSelections);
		}
		userSelections.verb = verb;
		Integer runNumber = pb.processTestRequest(userSelections);
		if(runNumber > -1) {
			processingNumbers.put("runtest", runNumber);
		}
	}
	public void removeSelected() {
		TreeItem[] selectedItems = selectionPanel.selectedComponents.getSelection();
		if(selectedItems.length > 0) {
			selectionsChanged = true;			
			selectionPanel.text.setText("");
		}

		for (TreeItem item : selectedItems){
			AngryViperAsset asset =  (AngryViperAsset)item.getData();
			assetSelections.remove(asset);
			item.dispose();
		}
	}

	public void clearEntries() {
		int itemCount = selectionPanel.selectedComponents.getItemCount();
		if(itemCount > 0) {
			assetSelections.clear();
			selectionsChanged = true;			
			selectionPanel.text.setText("");
			selectionPanel.selectedComponents.removeAll();
		}
	}
	
	
	protected void makeCopy(TreeItem srcItem, TreeItem itemCopy) {
		itemCopy.setText(srcItem.getText());
		itemCopy.setImage(srcItem.getImage());
		itemCopy.setData(srcItem.getData());
	}
	
	public void addPlatforms(List<HdlPlatformInfo> hdlPlatforms) {
		for(HdlPlatformInfo platform : hdlPlatforms) {
			buildSelectPanel.addHdlPlatform(platform);
		}
	}
	public void removePlatforms(List<HdlPlatformInfo> hdlPlatforms) {
		for(HdlPlatformInfo platform : hdlPlatforms) {
			buildSelectPanel.removeHdlPlatform(platform.getName());
		}
	}
	
	protected void populateBuildTypes() {
		AngryViperAssetService mgr = AngryViperAssetService.getInstance();
		List<HdlPlatformInfo> hdlPlatforms = mgr.getHdlPlatforms();
		buildSelectPanel.setHdlPlatforms(hdlPlatforms);
		
		List<RccPlatformInfo> rccPlatforms = mgr.getRccPlatforms();
		buildSelectPanel.setRccPlatforms(rccPlatforms);
		
		List<HdlVendor> hdlVendors = mgr.getHdlTargets();
		buildSelectPanel.setHdlTargets(hdlVendors);
	}
	
	protected UserBuildSelections getUserSelections() {
		UserBuildSelections selections = new UserBuildSelections();
		if(selectionPanel.buildAssembliesButton.getSelection()) {
			selections.noAssemblies = false;
		}
		else {
			selections.noAssemblies = true;
		}
		String[] rccBldSelects = buildSelectPanel.getRccPlatforms();
		String[] hdlBldSelects;
		boolean isHdlPlatform = true;
		if(buildSelectPanel.isTargetsButtonSelected()) {
			hdlBldSelects = buildSelectPanel.getHdlTargets();
			isHdlPlatform = false;
		}
		else {
			hdlBldSelects = buildSelectPanel.getHdlPlatforms();
		}
		BuildTargetSelections tSelects = new BuildTargetSelections();
		tSelects.rccBldSelects = rccBldSelects;
		tSelects.isHdlPlatforms = isHdlPlatform;
		tSelects.hdlBldSelects = hdlBldSelects;
		selections.buildTargetSelections = tSelects;

		TreeItem[] selects = selectionPanel.selectedComponents.getItems();
		AngryViperAsset asset;
		for(int i = 0; i<selects.length; i++) {
			TreeItem selection = selects[i];
			asset  =  (AngryViperAsset)selection.getData();
			selections.assetSelections.add(asset);
		}
		String descript = selectionPanel.text.getText();
		if(descript.length() == 0) {
			descript = null;
		}
		selections.buildDescription = descript;
		return selections;
	}
}
