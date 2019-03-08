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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.wb.swt.SWTResourceManager;

import av.proj.ide.avps.internal.AvpsResourceManager;
import av.proj.ide.avps.internal.BuildTargetSelections;
import av.proj.ide.avps.internal.ProjectBuildService;
import av.proj.ide.avps.internal.ProjectBuildService.ProvideBuildSelections;
import av.proj.ide.avps.internal.SelectionsInterface;
import av.proj.ide.avps.internal.TestMode;
import av.proj.ide.avps.internal.UserBuildSelections;
import av.proj.ide.avps.internal.UserTestSelections;
import av.proj.ide.internal.AngryViperAsset;
import av.proj.ide.internal.AngryViperAssetService;
import av.proj.ide.internal.AngryViperAssetService.BuildPlatformUpdate;
import av.proj.ide.internal.EnvBuildTargets.HdlPlatformInfo;
import av.proj.ide.internal.EnvBuildTargets.HdlVendor;
import av.proj.ide.internal.EnvBuildTargets.RccPlatformInfo;
import av.proj.ide.internal.OcpidevVerb;
import av.proj.ide.internal.OpenCPICategory;

public class MainOperationSwtDisplayV1 extends Composite implements SelectionsInterface, ProvideBuildSelections {
	Composite headerArea;
	BuildSelectionSidePanel buildSelectPanel;
//	SelectionPanel selectionPanel;
	CentralPanel  selectionPanel;
	
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
		ArrayList<String> unsupportedAssets = null;
		for(int i=0; i< items.length; i++) {
			TreeItem item = items[i];
			AngryViperAsset asset =  (AngryViperAsset)item.getData();
			if(assetSelections.contains(asset)) continue;
			
			if(asset.category == OpenCPICategory.component || 
			   asset.category == OpenCPICategory.protocol ||
			   asset.category == OpenCPICategory.xmlapp  ) {
				if(unsupportedAssets == null) {
					unsupportedAssets = new ArrayList<String>();
				}
				unsupportedAssets.add(asset.category.getFrameworkName() + "s");
				continue;
			}
			assetSelections.add(asset);
			selectionsChanged = true;
			if(selectionPanel.selectedComponents.indexOf(item) <1) {
				TreeItem copy = new TreeItem(selectionPanel.selectedComponents, SWT.NONE);
				makeCopy(item, copy);
			}
		}
		if(unsupportedAssets != null) {
			int len = unsupportedAssets.size();
			StringBuilder sb = new StringBuilder("The Operations Panel offers no further support for ");
			sb.append(unsupportedAssets.get(0));
			for(int i = 1; i < len -1; i++) {
				sb.append(", ");
				sb.append(unsupportedAssets.get(i));
			}
			if(len > 1) {
				sb.append(" or ");
				sb.append(unsupportedAssets.get(len -1));
			}
			sb.append('.');
			MessageDialog.openInformation(this.getShell(), "No Further Support", sb.toString());
		}
	}
	private TreeItem[] emptyItems = new TreeItem[0];
	Button addSelectionsButton;
	Button removeSelectionsButton;
	Button clearSelectionsButton;
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
		BuildPlatformUpdate platformUpdater = new BuildPlatformUpdate() {

			@Override
			public void addHdlPlatforms(List<HdlPlatformInfo> hdlPlatforms) {
				addHDLPlatforms(hdlPlatforms);
			}

			@Override
			public void removeHdlPlatforms(List<HdlPlatformInfo> hdlPlatforms) {
				removeHDLPlatforms(hdlPlatforms);
			}

			@Override
			public void addRccPlatforms(List<RccPlatformInfo> rccPlatforms) {
				addRCCPlatforms(rccPlatforms);
			}

			@Override
			public void removeRccPlatforms(List<RccPlatformInfo> rccPlatforms) {
				removeRCCPlatforms(rccPlatforms);
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
		
		Composite addRemoveCtrl = new Composite(this,  SWT.NONE);
		GridLayout gl = new GridLayout(1, false);
		addRemoveCtrl.setLayout(gl);
		GridData gd;
		
		// Top Fill
		new Composite(addRemoveCtrl,  SWT.NONE);
		
		// Controls
		Composite centerSection = new Composite(addRemoveCtrl,  SWT.BORDER_SOLID);
		gl = new GridLayout(1, false);
		centerSection.setLayout(gl);
		gd =  new GridData(GridData.CENTER, GridData.BEGINNING, true, false);
		
		addSelectionsButton = new Button(centerSection, SWT.PUSH);
		addSelectionsButton.setText(">");
		gd =  new GridData(GridData.FILL, GridData.CENTER, true, false);
		addSelectionsButton.setLayoutData(gd);
		
		removeSelectionsButton = new Button(centerSection, SWT.PUSH);
		//addSelectionsButton.setImage(image);
		removeSelectionsButton.setText("<");
		gd =  new GridData(GridData.FILL, GridData.CENTER, true, false);
		removeSelectionsButton.setLayoutData(gd);

		clearSelectionsButton = new Button(centerSection, SWT.PUSH);
		clearSelectionsButton.setText("clr");
		gd =  new GridData(GridData.FILL, GridData.CENTER, true, false);
		clearSelectionsButton.setLayoutData(gd);
		// Bottom Fill
		new Composite(addRemoveCtrl,  SWT.NONE);

		// ===================================================================
		//                         Build Panel
		// ===================================================================

		buildSelectPanel = new BuildSelectionSidePanel(this, SWT.NONE);

		// ===================================================================
		//                         Selection Panel
		// ===================================================================
		selectionPanel = new CentralPanel(this, SWT.NONE);
		selectionPanel.assets.setSelection(true);

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

		// Add/Remove - left side
		data = new FormData();
		data.top = new FormAttachment(headerArea, 5);
		data.left = new FormAttachment(0, 2);
		data.right = new FormAttachment(0, 50);
		data.bottom  = new FormAttachment(100, -10);
		addRemoveCtrl.setLayoutData(data);

		// Build Panel - left side
		data = new FormData();
		data.top = new FormAttachment(headerArea, 5);
		data.left = new FormAttachment(addRemoveCtrl, 2);
		data.right = new FormAttachment(0, 200);
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
		//selectionPanel.setTestsPresentation();
		
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
	
	public void doBuild(OcpidevVerb verb) {
		ProjectBuildService pb = ProjectBuildService.getInstance();
		if( processingStateChange()) {
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
	
	protected void doNewBuild(ProjectBuildService pb, OcpidevVerb verb) {
		UserBuildSelections userSelections = getUserSelections();
		userSelections.verb = verb;
		int buildNumber = pb.processBuildRequest(userSelections);
		if(buildNumber == -1) {
			return;
		}
		processingNumbers.put("build", buildNumber);
	}
	
	protected boolean processingStateChange() {
		if( selectionsChanged || buildTargetsChanged) {
			selectionsChanged = false;
			buildTargetsChanged = false;
			processingNumbers.clear();
			return true;
		}
		return false;
		
	}
	

	public void executeTests(TestMode mode) {
		UserTestSelections selections = getUserTestSelections();
		selections.verb = OcpidevVerb.run;
		selections.testMode = mode;
		ProjectBuildService pb = ProjectBuildService.getInstance();
		String key = "run " + mode.toString();
		Integer runNumber =  processingNumbers.get(key);
		
		if( processingStateChange()) {
			runNumber = null;
		}
		if(runNumber != null) {
			if(pb.haveBuildNumber(runNumber)) {
				pb.processTestRequest(selections, runNumber);
				return;
			}
		}
		processingNumbers.remove(key);
		runNumber = pb.processTestRequest(selections, null);
		if(runNumber > -1) {
			processingNumbers.put(key, runNumber);
		}
	}
	
	public void removeSelected() {
		TreeItem[] selectedItems = selectionPanel.selectedComponents.getSelection();
		if(selectedItems.length > 0) {
			selectionsChanged = true;			
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
			selectionPanel.selectedComponents.removeAll();
		}
	}
	
	
	protected void makeCopy(TreeItem srcItem, TreeItem itemCopy) {
		itemCopy.setText(srcItem.getText());
		itemCopy.setImage(srcItem.getImage());
		itemCopy.setData(srcItem.getData());
	}
	
	public void addHDLPlatforms(List<HdlPlatformInfo> hdlPlatforms) {
		for(HdlPlatformInfo platform : hdlPlatforms) {
			buildSelectPanel.addHdlPlatform(platform);
		}
	}
	public void removeHDLPlatforms(List<HdlPlatformInfo> hdlPlatforms) {
		for(HdlPlatformInfo platform : hdlPlatforms) {
			buildSelectPanel.removeHdlPlatform(platform.getName());
		}
	}
	
	public void addRCCPlatforms(List<RccPlatformInfo> rccPlatforms) {
		for(RccPlatformInfo platform : rccPlatforms) {
			buildSelectPanel.addRccPlatform(platform);
		}
	}
	public void removeRCCPlatforms(List<RccPlatformInfo> rccPlatforms) {
		for(RccPlatformInfo platform : rccPlatforms) {
			buildSelectPanel.removeRccPlatform(platform.getName());
		}
	}
	
	protected void populateBuildTypes() {
		
		AngryViperAssetService srv = AngryViperAssetService.getInstance();
		Collection<HdlPlatformInfo> hdlPlatforms = srv.getHdlPlatforms();
		buildSelectPanel.setHdlPlatforms(hdlPlatforms);
		
		Collection<RccPlatformInfo> rccPlatforms = srv.getRccPlatforms();
		buildSelectPanel.setRccPlatforms(rccPlatforms);
		
		Collection<HdlVendor> hdlVendors = srv.getHdlTargets();
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
		return selections;
	}
	public UserTestSelections getUserTestSelections() {
		BuildTargetSelections tSelects = getBuildTargetSelects();
		UserTestSelections utSels = new UserTestSelections();
		utSels.buildTargetSelections = tSelects;
		selectionPanel.getCurrentSelections(utSels);
		return utSels;
	}
	
	private BuildTargetSelections getBuildTargetSelects() {
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
		return tSelects;
	}
}
