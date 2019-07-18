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

import java.util.Collection;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.wb.swt.SWTResourceManager;

import av.proj.ide.internal.EnvBuildTargets.HdlPlatformInfo;
import av.proj.ide.internal.EnvBuildTargets.HdlVendor;
import av.proj.ide.internal.EnvBuildTargets.RccPlatformInfo;

public class BuildSelectionSidePanel  extends Composite  {
	
	List hdlPlatforms;
	List rccPlatforms;
	Tree hdlTargets;
	Button targetsButton;
	
	protected Group hdlPanel;
	protected Group rccPanel;
	protected Group hdlTarPanel;

	public enum HdlBuildSelection {
		HDL_PLATFORMS, HDL_TARGETS
	}
	
	protected HdlBuildSelection hdlView;
	private FormData show;
	private FormData hide;
	
	public BuildSelectionSidePanel (Composite parent, int style) {
		super(parent, style);
		
		
		FormLayout layout = new FormLayout();
		layout.marginHeight = 0;
		layout.marginWidth = 5;
		this.setLayout(layout);
		GridLayout glayout;
		
		rccPanel = new Group(this, SWT.NONE);
		rccPanel.setText("RCC Platforms");
		glayout = new GridLayout(1, false);
		rccPanel.setLayout(glayout);
		
		rccPlatforms = new List(rccPanel, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.MULTI);
		
		GridData gd = new GridData(GridData.FILL, GridData.FILL, true, true);
		gd.widthHint = 160;
		gd.heightHint = 100;
		rccPlatforms.setLayoutData(gd);
		
		targetsButton = new Button(this, SWT.CHECK);
		targetsButton.setText("HDL Targets");
	
		hdlPanel = new Group(this, SWT.H_SCROLL | SWT.V_SCROLL);
		hdlPanel.setText("HDL Platforms");
		glayout = new GridLayout(1, false);
		hdlPanel.setLayout(glayout);
		
		hdlPlatforms = new List(hdlPanel, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.MULTI);
		
		gd = new GridData(GridData.FILL, GridData.FILL, true, true);
		gd.widthHint = 160;
		gd.heightHint = 200;
		hdlPlatforms.setLayoutData(gd);
		hdlView = HdlBuildSelection.HDL_PLATFORMS;

		
		FormData data = new FormData();
		data.top = new FormAttachment(0, 5);
		data.left = new FormAttachment(0, 5);
		data.right = new FormAttachment(100, -5);
		data.bottom = new FormAttachment(40);
		rccPanel.setLayoutData(data);
		
		data = new FormData();
		data.top = new FormAttachment(rccPanel);
		data.left = new FormAttachment(0, 5);
		targetsButton.setLayoutData(data);
		
		data = new FormData();
		data.top = new FormAttachment(targetsButton, 5);
		data.bottom = new FormAttachment(100, -5);
		data.left = new FormAttachment(0, 5);
		data.right = new FormAttachment(100, -5);

		hdlPanel.setLayoutData(data);
		
		hdlTarPanel = new Group(this,   SWT.H_SCROLL | SWT.V_SCROLL);
		glayout = new GridLayout(1, false);
		hdlTarPanel.setLayout(glayout);
		
		hdlTargets = new Tree(hdlTarPanel, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.MULTI);
		
		gd = new GridData(GridData.FILL, GridData.FILL, true, true);
		gd.widthHint = 160;
		gd.heightHint = 200;
		hdlTargets.setLayoutData(gd);
		
		data = new FormData ();
		data.top = new FormAttachment (0);
		data.bottom = new FormAttachment (0);
		data.left = new FormAttachment (0);
		data.right = new FormAttachment (0);
		
		hdlTarPanel.setLayoutData(data);

		this.setBackground(SWTResourceManager.getColor(SWT.COLOR_TITLE_BACKGROUND));
		rccPanel.setBackground(SWTResourceManager.getColor(SWT.COLOR_WIDGET_BACKGROUND));
		hdlPanel.setBackground(SWTResourceManager.getColor(SWT.COLOR_WIDGET_BACKGROUND));
		hdlTarPanel.setBackground(SWTResourceManager.getColor(SWT.COLOR_WIDGET_BACKGROUND));

		show = new FormData();
		show.top = new FormAttachment(targetsButton, 5);
		show.bottom = new FormAttachment(100, -5);
		show.left = new FormAttachment(0, 5);
		show.right = new FormAttachment(100, -5);
		
		hide = new FormData();
		hide.top = new FormAttachment (0);
		hide.bottom = new FormAttachment (0);
		hide.left = new FormAttachment (0);
		hide.right = new FormAttachment (0);

		targetsButton.addListener(SWT.Selection,
				new Listener(){
			public void handleEvent(Event e) {
//				Button b = (Button)e.widget;
//				if(b.getSelection()) {
					toggleHdlSelection();
//				}
			}
		});

		
		// Mockup for Window Builder
//		rccPlatforms.setItems(new String[]{"plat1","plat2","plat3","plat4" });
//		hdlPlatforms.setItems(new String[]{"plat1","plat2","plat3","plat4","plat5","plat6","plat2","plat3","plat4","plat5" });
	}
	
	public void toggleHdlSelection() {
		
		if(hdlView == HdlBuildSelection.HDL_PLATFORMS){
			// For using an updated exlipse env, the text
			// in the hidden panel didn't hide.
			hdlPanel.setText("");
			hdlPanel.setLayoutData(hide);
			hdlTarPanel.setText("HDL Targets");
			hdlTarPanel.setLayoutData(show);
			hdlView = HdlBuildSelection.HDL_TARGETS;
		}
		else {
			hdlTarPanel.setText("");
			hdlTarPanel.setLayoutData(hide);
			hdlPanel.setText("HDL Platforms");
			hdlPanel.setLayoutData(show);
			hdlView = HdlBuildSelection.HDL_PLATFORMS;
		}
		this.layout();
		//this.pack();
	}
	public List getRccPlatformsWidgit() {
		return rccPlatforms;
	}
	public List getHdlPlatformsWidgit() {
		return hdlPlatforms;
	}
	public Tree getHdlTargetsWidgit() {
		return hdlTargets;
	}
	
	public void setRccPlatforms(Collection<RccPlatformInfo> platformList) {
		String[] platforms = new String[platformList.size()];
		int i=0;
		for(RccPlatformInfo platform :  platformList) {
			platforms[i++] = platform.getName();
		}
		rccPlatforms.setItems(platforms);
	}
	public void setHdlPlatforms(Collection<HdlPlatformInfo> platformList) {
		String[] platforms = new String[platformList.size()];
		int i=0;
		for(HdlPlatformInfo platform :  platformList) {
			platforms[i++] = platform.getName();
		}
		hdlPlatforms.setItems(platforms);
	}
	
	public void addHdlPlatform(HdlPlatformInfo platform) {
		hdlPlatforms.add(platform.getName());
	}
	
	public void removeHdlPlatform(String platform) {
		int idx = hdlPlatforms.indexOf(platform);
		hdlPlatforms.remove(idx);
	}
	
	public void addRccPlatform(RccPlatformInfo platform) {
		rccPlatforms.add(platform.getName());
	}
	
	public void removeRccPlatform(String platform) {
		int idx = rccPlatforms.indexOf(platform);
		rccPlatforms.remove(idx);
	}
	
	public void setHdlTargets(Collection<HdlVendor>  targets) {
		TreeItem level1;
		
		for(HdlVendor info : targets) {
			level1 = new TreeItem(hdlTargets, SWT.NONE);
			level1.setText(info.getVendor());
			TreeItem item;
			for(String targ : info.getTargets()){
				item = new TreeItem(level1, SWT.NONE); 
				item.setText(targ);
			}
		}
	}
	
	
	public String[] getHdlPlatforms() {
		return hdlPlatforms.getSelection();
	}

	public String[] getRccPlatforms() {
		return rccPlatforms.getSelection();
	}

	public String[] getHdlTargets() {
		TreeItem[] selects = hdlTargets.getSelection();
		int len = selects.length;
		String[] sels = new String[len];
		TreeItem item;
		for (int i=0; i<len; i++) {
			item = selects[i];
			sels[i] = item.getText();
		}
		
		return sels;
	}

	public void setWidgitBackground(Color color) {
		hdlPlatforms.setBackground(color);
		hdlTargets.setBackground(color);
		rccPlatforms.setBackground(color);
	}

	public boolean isTargetsButtonSelected() {
		return targetsButton.getSelection();
	}

	public void setPanelColorScheme(AvColorScheme colorScheme) {
		this.setBackground(colorScheme.getSecondary());
	}
}
