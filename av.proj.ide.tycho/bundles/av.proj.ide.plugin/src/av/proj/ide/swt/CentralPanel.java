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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.wb.swt.SWTResourceManager;

import av.proj.ide.avps.internal.AvpsResourceManager;
import av.proj.ide.avps.internal.UserTestSelections;
import av.proj.ide.internal.AngryViperAsset;
import av.proj.ide.internal.OpenCPICategory;


public class CentralPanel  extends Composite  {
	
	// External Control needs to have access to these.
//	Button addSelectionsButton;
//	Button clearSelectionsButton;
//	Button removeSelectionsButton;
	Tree   selectedComponents;

	Combo remotesCombo;
	Combo testCaseCombo;
	
	Button buildButton;
	Button buildAssembliesButton;
	Button runButton;
	Button cleanButton;
	Text   text;
	
	Button assets;
	Button tests;
	protected Composite buildControls;
	protected Composite testControls;
	protected Button newCases;
	protected Button newRemote;
	protected List<String> remoteSystemEntries = null;
	protected Set<String> remoteSystemSelections = null;
	protected List<String>  testCaseEntries = null;
	protected Set<String> testCaseSelections = null;
	
	Button generate;
	Button build;
	Button prepare;
	Button run;
	Button verify;
	Button view;
	Button genBuild;
	Button prepRun;
	Button prepRunVerify;

	Button runCln;
	Button simCln;
	Button allCln;

	private Button accErrs;
	private Button viewScr;
	private Button keepSim;
	private Composite toggleControls;
	private Label cleanupHeader;

	private Composite remotesAndCases;
	private Composite buttonArea;
	private Composite remoteCasePanel;

	private Button showRemotes;
	private Button showCases;
	private Table listing;
	private Menu menu;
	

	public CentralPanel (Composite parent, int style) {
		super(parent, style);
		
		remoteSystemEntries = new ArrayList<String>();
		remoteSystemSelections = new HashSet<String>();
		testCaseEntries = new ArrayList<String>();
		testCaseSelections = new HashSet<String>();
		
		FormLayout formLo = new FormLayout();
		formLo.marginTop = 5;
		formLo.marginBottom = 5;
		this.setLayout(formLo);
		
		
		// Selected Assets Panel
		selectedComponents  = new Tree(this, SWT.BORDER);
		remoteCasePanel = new Composite(this, SWT.BORDER);

		toggleControls = new Composite(this, SWT.BORDER);
		buttonArea = new Composite(this, SWT.BORDER);
		formLo = new FormLayout();
		formLo.marginTop = 2;
		formLo.marginLeft = 2;
		formLo.marginRight = 2;
		formLo.marginBottom = 2;
		buttonArea.setLayout(formLo);
		
		// B U I L D   C O N T R O L S
		buildControls = new Composite(buttonArea, SWT.BORDER);
		
		// T E S T   C O N T R O L S
		testControls = new Composite(buttonArea, SWT.BORDER);
		

		// Lay it out.
		FormData fd = new FormData();
		fd.top = new FormAttachment(0, 5);
		fd.left = new FormAttachment(0, 5);
		fd.right = new FormAttachment(100, -180);
		fd.bottom  = new FormAttachment(remoteCasePanel, -5);
		selectedComponents.setLayoutData(fd);
		
		fd = new FormData();
//		fd.top = new FormAttachment(100, 0);
//		fd.bottom  = new FormAttachment(100, 0);
		fd.top = new FormAttachment(100, -110);
		fd.left = new FormAttachment(0, 5);
		fd.right = new FormAttachment(100, -180);
		fd.bottom  = new FormAttachment(100, -5);
		remoteCasePanel.setLayoutData(fd);
		
		fd = new FormData();
		fd.top = new FormAttachment(0, 5);
		fd.left = new FormAttachment(selectedComponents, 2);
		fd.right = new FormAttachment(100, -2);
		fd.bottom  = new FormAttachment(0, 35);
		toggleControls.setLayoutData(fd);
		
		
		fd = new FormData();
		fd.top = new FormAttachment(toggleControls, 5);
		fd.left = new FormAttachment(selectedComponents, 2);
		fd.right = new FormAttachment(100, -2);
		fd.bottom  = new FormAttachment(100, -5);
		buttonArea.setLayoutData(fd);
		
		FormData hide = new FormData();
		hide.top = new FormAttachment(buttonArea, 0);
		hide.bottom  = new FormAttachment(buttonArea, 0);
		
		FormData show = new FormData();
		show.top = new FormAttachment(buttonArea, 5);
		show.left = new FormAttachment(buttonArea, 5);
		show.right = new FormAttachment(100, -5);
		show.bottom  = new FormAttachment(100, -5);
		
		testControls.setLayoutData(hide);
		buildControls.setLayoutData(show);

		GridLayout gl = null;
		FillLayout fl = null;
		GridData gd =  null;
		
		// Populate the  T E S T   REMOTES/CASES  P A N E L
		
		formLo = new FormLayout();
		remoteCasePanel.setLayout(formLo);
		
		// Controls for remote systems and test cases tables.
		remotesAndCases = new Composite(remoteCasePanel, SWT.NONE);
		gl = new GridLayout(2, false); 
		remotesAndCases.setLayout(gl);
		
		fd = new FormData();
		fd.top = new FormAttachment(0, 5);
		fd.left = new FormAttachment(0, 2);
		fd.right = new FormAttachment(0, 155);
		fd.bottom  = new FormAttachment(100, -5);
		remotesAndCases.setLayoutData(fd);
		
		listing = new Table(remoteCasePanel, SWT.MULTI | SWT.SCROLLBAR_OVERLAY);
		menu = new Menu(listing);
		listing.setMenu(menu);

		fd = new FormData();
		fd.top = new FormAttachment(0, 5);
		fd.left = new FormAttachment(remotesAndCases, 5);
		fd.right = new FormAttachment(100, -5);
		fd.bottom  = new FormAttachment(100, -5);
		listing.setLayoutData(fd);
		
		showRemotes = new Button(remotesAndCases, SWT.RADIO);
		showRemotes.setFont(SWTResourceManager.getFont("Cantarell", 8, SWT.NORMAL));
		showRemotes.setText("remotes");
		showRemotes.setSelection(true);
		
		showCases = new Button(remotesAndCases, SWT.RADIO);
		showCases.setFont(SWTResourceManager.getFont("Cantarell", 8, SWT.NORMAL));
		showCases.setText("test cases");
		
		newRemote = new Button(remotesAndCases, SWT.PUSH);
		newRemote.setFont(SWTResourceManager.getFont("Cantarell", 8, SWT.NORMAL));
		newRemote.setText("+ remotes");
		
		newCases = new Button(remotesAndCases, SWT.PUSH);
		newCases.setFont(SWTResourceManager.getFont("Cantarell", 8, SWT.NORMAL));
		newCases.setText("+ cases");
		newCases.setEnabled(false);
		
		// Populate Toggle Controls
		gl = new GridLayout(2, true);
		toggleControls.setLayout(gl);
		
		gd =  new GridData(SWT.FILL, GridData.FILL, true, true);
		assets = new Button(toggleControls, SWT.RADIO);
		assets.setText("Assets");
		assets.setLayoutData(gd);
		assets.setSelection(true);
		
		gd =  new GridData(SWT.FILL, GridData.FILL, true, true);
		tests = new Button(toggleControls, SWT.RADIO);
		tests.setText("Tests");
		tests.setLayoutData(gd);
		tests.setSelection(false);
		
		// Populate the B U I L D   P A N E L
		
		gl = new GridLayout(1, false);
		buildControls.setLayout(gl);
		
		Label l = new Label(buildControls, SWT.NONE);
		l.setText("Build Label (optional):");
		gd =  new GridData(SWT.FILL, GridData.BEGINNING, true, false);
		l.setLayoutData(gd);
		
		text = new Text(buildControls, SWT.BORDER);
		gd =  new GridData(SWT.FILL, GridData.BEGINNING, true, false);
		text.setLayoutData(gd);
		
		buildAssembliesButton = new Button(buildControls, SWT.CHECK);
		buildAssembliesButton.setText("Build Assemblies");
		
		buildButton = new Button(buildControls, SWT.PUSH);
		buildButton.setLayoutData(new GridData(SWT.FILL, GridData.BEGINNING, true, false));
		buildButton.setText("Build");
		buildButton.setToolTipText("Build the selected assets.");
		
		cleanButton = new Button(buildControls, SWT.PUSH);
		cleanButton.setText("Clean");
		gd =  new GridData(SWT.FILL, GridData.BEGINNING, true, false);
		cleanButton.setLayoutData(gd);
		
		// Populate the  T E S T   P A N E L
		
		gl = new GridLayout(2, true);
		gl.marginTop = 2;
		gl.marginHeight = 2;
		gl.verticalSpacing = 2;
		//gl.marginWidth = 2;
		gl.marginLeft=2;
		gl.marginRight=2;
		gl.horizontalSpacing=4;
		gl.marginBottom=2;
		testControls.setLayout(gl);
		
		// ROW 1
		generate = new Button(testControls, SWT.PUSH);
		generate.setText("generate");
		gd =  new GridData(SWT.FILL, GridData.BEGINNING, true, false);
		generate.setLayoutData(gd);
		
		genBuild = new Button(testControls, SWT.PUSH);
		genBuild.setText("gen +\nbuild");
		gd =  new GridData(SWT.FILL, GridData.CENTER, true, false);
		gd.verticalSpan = 2;
		genBuild.setLayoutData(gd);
		
//		build = new Button(testControls, SWT.PUSH);
//		build.setText("build");
//		gd =  new GridData(SWT.FILL, GridData.BEGINNING, true, false);
//		build.setLayoutData(gd);
		new Label(testControls, SWT.NONE);
		// ROW 2
		prepare = new Button(testControls, SWT.PUSH);
		prepare.setText("prepare");
		gd =  new GridData(SWT.FILL, GridData.BEGINNING, true, false);
		prepare.setLayoutData(gd);
		
		prepRun = new Button(testControls, SWT.PUSH);
		prepRun.setText("prep\n+ run");
		gd =  new GridData(SWT.FILL, GridData.CENTER, true, false);
		gd.verticalSpan = 2;
		prepRun.setLayoutData(gd);
		
		run = new Button(testControls, SWT.PUSH);
		run.setText("run");
		gd =  new GridData(SWT.FILL, GridData.BEGINNING, true, false);
		run.setLayoutData(gd);
		
		// ROW 3
		verify = new Button(testControls, SWT.PUSH);
		verify.setText("verify");
		gd =  new GridData(SWT.FILL, GridData.BEGINNING, true, false);
		verify.setLayoutData(gd);
		
		prepRunVerify = new Button(testControls, SWT.PUSH);
		prepRunVerify.setFont(SWTResourceManager.getFont("Cantarell", 10, SWT.NORMAL));
		prepRunVerify.setText("prep\n+ run\n+ verify");
		gd =  new GridData(SWT.FILL, GridData.CENTER, true, false);
		gd.verticalSpan = 2;
		prepRunVerify.setLayoutData(gd);
		
		view = new Button(testControls, SWT.PUSH);
		view.setText("view");
		gd =  new GridData(SWT.FILL, GridData.BEGINNING, true, false);
		view.setLayoutData(gd);
		
		// Additional Options
		Composite addedControls = new Composite(testControls, SWT.NONE);
		gd =  new GridData(GridData.BEGINNING, GridData.CENTER, true, false);
		gd.horizontalSpan = 2;
		addedControls.setLayoutData(gd);
		
		RowLayout rl = new RowLayout(SWT.VERTICAL);
		rl.marginTop = 2;
		rl.marginBottom = 2;
		addedControls.setLayout(rl);
		
		accErrs = new Button(addedControls, SWT.CHECK);
		accErrs.setFont(SWTResourceManager.getFont("Cantarell", 10, SWT.NORMAL));
		accErrs.setText("accumulate errors");
		viewScr = new Button(addedControls, SWT.CHECK);
		viewScr.setFont(SWTResourceManager.getFont("Cantarell", 10, SWT.NORMAL));
		viewScr.setText("run view script");
		keepSim = new Button(addedControls, SWT.CHECK);
		keepSim.setFont(SWTResourceManager.getFont("Cantarell", 10, SWT.NORMAL));
		keepSim.setText("keep simulations");

		cleanupHeader = new Label(testControls, SWT.NONE);
		cleanupHeader.setAlignment(SWT.CENTER);
		cleanupHeader.setFont(SWTResourceManager.getFont("Cantarell", 10, SWT.NORMAL));
		cleanupHeader.setText("clean test execution");
		gd =  new GridData(GridData.FILL, GridData.CENTER, true, false);
		gd.horizontalSpan = 2;
		cleanupHeader.setLayoutData(gd);
		
		// Cleanup Results Options
		Composite clean = new Composite(testControls, SWT.BORDER);
		gd =  new GridData(GridData.FILL, GridData.CENTER, true, false);
		gd.horizontalSpan = 2;
		clean.setLayoutData(gd);

		fl = new FillLayout();
		clean.setLayout(fl);
		
		runCln = new Button(clean, SWT.PUSH);
		runCln.setFont(SWTResourceManager.getFont("Cantarell", 10, SWT.NORMAL));
		runCln.setText("run");
		simCln = new Button(clean, SWT.PUSH);
		simCln.setFont(SWTResourceManager.getFont("Cantarell", 10, SWT.NORMAL));
		simCln.setText("sim");
		allCln = new Button(clean, SWT.PUSH);
		allCln.setFont(SWTResourceManager.getFont("Cantarell", 10, SWT.NORMAL));
		allCln.setText("all");
		
		addLocalControls();
		setAssetsPresentation();
	}

	protected void updateListing(List<String> entries, Set<String> selected) {
		listing.removeAll();
		if(entries != null && entries.size()>0) {
	    	int idx = 0;
	    	ArrayList<Integer> selectionIndices = new ArrayList<Integer>();
	    	for(String entry : entries) {
    			TableItem item = new TableItem(listing, SWT.NONE);
    			item.setText(entry);
    			if (selected.contains(entry)) {
    				selectionIndices.add(idx);
    			}
    			idx++;
    		}
    		int size = selectionIndices.size();
	    	if(size > 0) {
	    		int[] indices = new int[size];
	    		for(int i=0; i< size; i++) {
	    			indices[i] = selectionIndices.get(i);
	    		}
	    		listing.setSelection(indices);
	    	}
		}
		listing.layout();
	}

	public void setPanelColorScheme(AvColorScheme colorScheme) {
		Color blfBg = buildControls.getBackground();
		Color tstBg = testControls.getBackground();
		buildControls.setBackgroundMode(SWT.INHERIT_NONE);
		testControls.setBackgroundMode(SWT.INHERIT_NONE);
		buttonArea.setBackground(colorScheme.getSecondary());
		buildControls.setBackground(blfBg);
		testControls.setBackground(tstBg);
		cleanupHeader.setBackground(colorScheme.getSecondary());
		remoteCasePanel.setBackground(colorScheme.getSecondary());
		remotesAndCases.setBackground(blfBg);
	}

	public void getCurrentSelections(UserTestSelections selections) {
		TreeItem[] entries = selectedComponents.getItems();
		boolean logHldTestMessage = false;
		for(TreeItem item : entries) {
			AngryViperAsset asset = (AngryViperAsset) item.getData();
			if (asset.category == OpenCPICategory.hdlTest) {
				logHldTestMessage = true;
				continue;
			}
			else if(asset.category != OpenCPICategory.test) {
				continue;
			}
			
			selections.assetSelections.add(asset);
		}
		selections.accumulateErrors = accErrs.getSelection();
		selections.keepSimulations = keepSim.getSelection();
		selections.runViewScript = viewScr.getSelection();
		Set<String> ref;
		
		// Ensure the selection set is up to date with the currently displayed listing.
		if(showRemotes.getSelection()) {
			ref = remoteSystemSelections;
		}
		else {
			ref = testCaseSelections;
		}
		ref.clear();
		for(TableItem selection : listing.getSelection()) {
			ref.add(selection.getText());
		}
		
		if(remoteSystemSelections.size()>0) {
			String[] remotesList = remoteSystemSelections.toArray(new String[remoteSystemSelections.size()]);
			selections.remoteList = remotesList;
		}
		
		if(testCaseSelections.size()>0) {
			String[] caseList = testCaseSelections.toArray(new String[testCaseSelections.size()]);
			selections.testCaseList = caseList;
		}
		if(logHldTestMessage) {
			AvpsResourceManager.getInstance()
			.writeToNoticeConsole("Note: The ability to run HDL tests is not supported. Run these tests by command line.\n HDL tests may be built using the assets panel.");
		}
	}
	public void setTestCases(List<String> entries, Set<String> selected) {
		testCaseEntries = entries;
		testCaseSelections = selected;
		if(showCases.getSelection()) {
			updateListing(entries, selected);
		}
	}
	
	public void setRemoteSystems(List<String> entries, Set<String> selected) {
		remoteSystemEntries = entries;
		remoteSystemSelections = selected;
		if(showRemotes.getSelection()) {
			updateListing(entries, selected);
		}
	}
	public void setAssetsPresentation() {
		tests.setSelection(false);
		assets.setSelection(true);
		displayAssetsPanel();
	}
	public void setTestsPresentation() {
		assets.setSelection(false);
		tests.setSelection(true);
		displayTestsPanel();
	}
	
	FormData hideTest = new FormData();
	FormData showAssets = new FormData();
	FormData hideCasePanel = new FormData();
	FormData hideAssets = new FormData();
	FormData showTest = new FormData();
	FormData showCasePanel = new FormData();
	{
		hideTest.top = new FormAttachment(buttonArea, 0);
		hideTest.bottom  = new FormAttachment(buttonArea, 0);

		showTest.top = new FormAttachment(buttonArea, 5);
		showTest.left = new FormAttachment(buttonArea, 5);
		showTest.right = new FormAttachment(100, -5);
		showTest.bottom  = new FormAttachment(100, -5);
		
		showCasePanel.top = new FormAttachment(100, -110);
		showCasePanel.left = new FormAttachment(0, 5);
		showCasePanel.right = new FormAttachment(100, -180);
		showCasePanel.bottom  = new FormAttachment(100, -5);

		hideCasePanel.top = new FormAttachment(100, 0);
		hideCasePanel.bottom  = new FormAttachment(100, 0);
		
		hideAssets.top = new FormAttachment(buttonArea, 0);
		hideAssets.bottom  = new FormAttachment(buttonArea, 0);

		showAssets.top = new FormAttachment(buttonArea, 5);
		showAssets.left = new FormAttachment(buttonArea, 5);
		showAssets.right = new FormAttachment(100, -5);
		showAssets.bottom  = new FormAttachment(100, -5);
	}
	
	protected void displayTestsPanel() {
		buildControls.setLayoutData(hideAssets);
		testControls.setLayoutData(showTest);
		remoteCasePanel.setLayoutData(showCasePanel);
		buttonArea.layout();
		this.layout();
	}
	
	protected void displayAssetsPanel() {
		testControls.setLayoutData(hideTest);
		buildControls.setLayoutData(showAssets);
		remoteCasePanel.setLayoutData(hideCasePanel);
		buttonArea.layout();
		this.layout();
	}
	
	protected int processTestCasesInput(String response, String initialText, StringBuilder sb) {
		int errors = 0;
    	if(response == null || response.isEmpty()) {
    		return -2;
    	}
		if(response.equals(initialText)) {
			return -1;
		}
		String resp = response.replaceAll("," , " ");
		String[] cases = resp.split(" ");
		
		boolean firstCase = true;
		doNext:
		for(String testCase : cases) {
			if(testCase.isEmpty()) continue;
			testCase = testCase.trim();
			if(testCase.isEmpty()) continue;
			testCase = testCase.toLowerCase();
			
			if(! testCase.startsWith("case")) {
				errors++;
				continue;
			}
			else {
				String caseNumber = testCase.substring(4,testCase.length());
				String[] segments = new String[2];
				int splitPoint = caseNumber.indexOf('.');
				if(splitPoint == -1) {
					errors++;
					continue;
				}
				segments[0] = caseNumber.substring(0, splitPoint);
				segments[1] = caseNumber.substring(splitPoint+1, caseNumber.length());
				
				// format supported is caseNumber.subCaseNumber. Minimum representation is
				// "xx.xx" max is "xxx.xxx" -> 999 cases, 999 sub cases
				
				for(String segment : segments) {
					int segLen = segment.length();
					if(segLen == 0 || segLen > 3) {
						errors++;
						break doNext;
					}
					if(segment.length() == 1 && segment.charAt(0) != '*') {
						errors++;
						break doNext;
					}
					for(int i=0; i< segment.length(); i++) {
						char value = segment.charAt(i);
						// only digits and '*'s allowed.
						if(Character.isDigit(value)==false && value != '*') {
							errors++;
							break doNext;
						}
					}
				}
			}
			
			// Have a good test case
			if(! firstCase) {
				sb.append(" ");
			}
			sb.append(testCase);
			if(firstCase) {
				firstCase = false;
			}
		}
		
		return errors;
	}

	protected void presentDialog(Shell shell, String title, String message, String label,
			                     String initialText, String errorMessage, String processedInput) {

		GetInfoDialog dl = new GetInfoDialog(shell);
		dl.create();
		dl.setTitle(title);
		
		if (errorMessage != null) {
			dl.setMessage(errorMessage + message, IMessageProvider.INFORMATION);
		} else {
			dl.setMessage(message, IMessageProvider.INFORMATION);
		}
		
		dl.setInputLabel(label);
		if (processedInput != null) {
			dl.setInitialText(processedInput);
		} else {
			dl.setInitialText(initialText);
		}
		int r = dl.open();
		
		if (r == 0) {
			String response = dl.getInput();
			StringBuilder sb = new StringBuilder();
			int errors = processTestCasesInput(response, initialText, sb);
			if (errors == 0 && sb.length() > 4) {
				String caseStr = sb.toString();
				testCaseEntries.add(caseStr);
				TableItem item = new TableItem(listing, SWT.NONE);
				item.setText(caseStr);
				listing.layout();
			}
			if (errors == -2) {
				// ? user cleared the input?
				presentDialog(shell, title, message, label, initialText, "Didn't get that?\n", null);
			} else if (errors == -1) {
				// user submitted the initial example
				presentDialog(shell, title, message, label, initialText, "I got the example back, change it.\n", null);
			} else if (sb.length() == 0) {
				presentDialog(shell, title, message, label, initialText,
						"I could not find valid test case entries, try again.\n", null);
			}
			else if (errors > 0) {
				// one or more parse errors
				presentDialog(shell, title, message, label, initialText,
						"Had some problems with the input. Is this what you meant?\n", sb.toString());
			}		}
	}

	protected void addLocalControls() {
		Composite controlArea = buttonArea;
		
		this.assets.addSelectionListener(new SelectionAdapter() {
			
			public void widgetSelected(SelectionEvent event) {
				
				Button b = (Button)event.getSource();
				if(b != null && b.getSelection() == false) {
					return;
				}
				displayAssetsPanel();
			}
		});
		
		this.tests.addSelectionListener(new SelectionAdapter() {
			
			public void widgetSelected(SelectionEvent event) {
				Button b = (Button)event.getSource();
				if(b != null && b.getSelection() == false) {
					return;
				}
				displayTestsPanel();
			}
		});	
		
		this.newRemote.addSelectionListener(new SelectionAdapter() {
			
			public void widgetSelected(SelectionEvent event) {
			    Shell shell = controlArea.getShell();
			    GetInfoDialog dl = new GetInfoDialog(shell);
			    //dl.setBlockOnOpen(false);
			    dl.create();
			    
			    dl.setTitle("Add an OCPI Remote to the Drop-down list.");
			    dl.setMessage("Do not add \"OCPI_REMOTE_TEST_SYSTEMS\" to the export string; provide remotes as shown.\n" +
			    		"Entries in the list must be selected to include them in the run.", IMessageProvider.INFORMATION);
			    dl.setInputLabel("OCPI_REMOTE_TEST_SYSTEMS ");
			    dl.setInitialText("192.1.2.3=root=root=/mnt/<project>");
			    int r = dl.open();
			    if(r==0) {
			    	String response = dl.getInput();
			    	if(response != null && ! response.isEmpty()) {
			    		if(! response.equals("192.1.2.3=root=root=/mnt/<project>")) {
				    		remoteSystemEntries.add(response);
			    			TableItem item = new TableItem(listing, SWT.NONE);
			    			item.setText(response);
			    			listing.layout();
			    		}
			    	}
			    }
			}
		});
		
		this.newCases.addSelectionListener(new SelectionAdapter() {
			
			@Override
			public void widgetSelected(SelectionEvent event) {
				
				StringBuilder message = new 
				StringBuilder( "Use a space to separate each case as shown. Valid formats: case00.00 case00.* case0*.* case00.0*\n");
				message.append("Entries in the list must be selected to include them in the run.");
				
				presentDialog(controlArea.getShell(),
						      "Add a new set of test cases to the Drop-down list.",
						      message.toString(),
						      "Test Cases to Execute: ",
						      "case00.00 case00.01 etc", null, null );
			}
		});
		
		this.showRemotes.addSelectionListener(new SelectionAdapter() {
			
			public void widgetSelected(SelectionEvent event) {
				if(showRemotes.getSelection()) {
					// Save the current test case selections
					testCaseSelections.clear();
					for(TableItem selection : listing.getSelection()) {
						testCaseSelections.add(selection.getText());
					}
					
					updateListing(remoteSystemEntries, remoteSystemSelections);
					newRemote.setEnabled(true);
					newCases.setEnabled(false);
				}
			}
		});
		this.showCases.addSelectionListener(new SelectionAdapter() {
			
			public void widgetSelected(SelectionEvent event) {
				if(showCases.getSelection()) {
					// Save the current remote systems selections
					remoteSystemSelections.clear();
					for(TableItem selection : listing.getSelection()) {
						remoteSystemSelections.add(selection.getText());
					}
					
					updateListing(testCaseEntries, testCaseSelections);
					newRemote.setEnabled(false);
					newCases.setEnabled(true);
				}
			}
		});
    	SelectionAdapter menuAdapter = new SelectionAdapter() {
    		
    		public void widgetSelected(SelectionEvent event) {
				TableItem[] items = listing.getSelection();
				if(items.length==0) return;
				
				List<String> entryStore;
				String label;
				String message;
				if(showRemotes.getSelection()) {
					entryStore = remoteSystemEntries;
					label = "Remote Entry: ";
					message = "Example: 192.1.2.3=root=root=/mnt/myProject";
				}
				else {
					entryStore = testCaseEntries;
					label = "Current Case Entry: ";
					message = "Use a space to separate each case. Valid formats: case00.00 case00.* case0*.* case00.0*";
				}

				MenuItem item = (MenuItem)event.widget;
    			if("edit".equals(item.getText())) {
     				TableItem selection = items[0];
     				String input = selection.getText();
    			    Shell shell = controlArea.getShell();
    			    GetInfoDialog dl = new GetInfoDialog(shell);
    			    dl.create();
    			    
    			    dl.setTitle("Modify this entry?");
    			    dl.setInitialText(input);
    			    dl.setMessage(message);
    			    dl.setInputLabel(label);
    			    
    			    int r = dl.open();
    			    if(r==0) {
    			    	String response = dl.getInput();
    			    	if(response != null && ! response.isEmpty()) {
    			    		if(entryStore == testCaseEntries) {
    			    			StringBuilder sb = new StringBuilder();
    			    			int res = processTestCasesInput(response, "", sb);
    			    			if(res != 0) {
    								MessageDialog.openError(shell, "Syntax Errors Found", 
    										"Valid formats: case00.00, case00.*, case0*.* case00.0*");
    			    				return;
    			    			}
    			    		}
    			    		selection.setText(response);
    			    		int idx = entryStore.indexOf(input);
    			    		if(idx > -1) {
    			    			entryStore.remove(idx);
    			    			entryStore.add(idx, response);
    			    		}
    			    	}
    			    }
    			}
    			else {
    				for(TableItem itm : items) {
    					entryStore.remove(itm.getText());
    					itm.dispose();
    				}
    			}
    		}
    	};
		
        MenuItem newItem = new MenuItem(menu, SWT.NONE);
        newItem.setText("edit");
        newItem.addSelectionListener(menuAdapter);
        
        newItem = new MenuItem(menu, SWT.NONE);
        newItem.setText("delete");
        newItem.addSelectionListener(menuAdapter);
	}
	
//	public static void main(String[] args) {
//		putupPanel();
//	}
	
	protected static void putupPanel() {
	    Display display = new Display();
	    Shell shell = new Shell(display);
		shell.setLayout(new FillLayout());
		CentralPanel p = new CentralPanel(shell, SWT.NONE);
		p.remoteSystemEntries = new ArrayList<String>();
		p.remoteSystemEntries.add(":=192.11.22.33=do=do=/mnt/projectV");
		p.remoteSystemEntries.add(":=192.11.22.33=do=do=/mnt/projectW");
		p.remoteSystemEntries.add(":=192.11.22.33=do=do=/mnt/projectX");
		p.remoteSystemEntries.add(":=192.11.22.33=do=do=/mnt/projectY");
		p.remoteSystemEntries.add(":=192.11.22.33=do=do=/mnt/projectZ");
		p.remoteSystemSelections = new HashSet<String>();
		p.remoteSystemSelections.add(":=192.11.22.33=do=do=/mnt/projectX");
	    
		p.testCaseEntries = new ArrayList<String>();
		p.testCaseEntries.add("case00.00 case00.01 case00.02");
		p.testCaseEntries.add("case01.00 case01.01 case01.02");
		p.testCaseEntries.add("case02.00 case02.01 case02.02");
		p.testCaseSelections = new HashSet<String>();
		p.testCaseSelections.add("case01.00, case01.01, case01.02");
		p.updateListing(p.remoteSystemEntries, p.testCaseSelections);
	    shell.pack();
	    shell.open();
	    while (!shell.isDisposed()) {
	      if (!display.readAndDispatch()) {
	        display.sleep();
	      }
	    }
	    display.dispose();
	}
	
}
