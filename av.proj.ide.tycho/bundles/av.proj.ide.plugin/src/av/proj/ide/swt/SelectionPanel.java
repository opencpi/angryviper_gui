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

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;

public class SelectionPanel  extends Composite  {
	Button addSelectionsButton;
	Button clearSelectionsButton;
	Button removeSelectionsButton;
	Composite topBar;
	Tree   selectedComponents;

	Button buildButton;
	Button buildAssembliesButton;
	Button buildTestsButton;
	Button runButton;
	Button cleanButton;
	Composite bottomBar;	
	Text   text;

	public SelectionPanel (Composite parent, int style) {
		super(parent, style);
		GridLayout gl = new GridLayout(1, false);
		gl.horizontalSpacing =1;
		gl.marginTop =1;
		gl.marginBottom = 1;
		this.setLayout(gl);
		
		GridData gd;
		
		topBar = new Composite(this,  SWT.NONE);
		gl = new GridLayout(3, true);
		topBar.setLayout(gl);
		
		addSelectionsButton = new Button(topBar, SWT.PUSH);
		addSelectionsButton.setText("Add");
		gd =  new GridData(GridData.FILL, GridData.CENTER, true, false);
		addSelectionsButton.setLayoutData(gd);
		
		removeSelectionsButton = new Button(topBar, SWT.PUSH);
		//addSelectionsButton.setImage(image);
		removeSelectionsButton.setText("Remove");
		gd =  new GridData(GridData.FILL, GridData.CENTER, true, false);
		removeSelectionsButton.setLayoutData(gd);

		clearSelectionsButton = new Button(topBar, SWT.PUSH);
		clearSelectionsButton.setText("Clear");
		gd =  new GridData(GridData.FILL, GridData.CENTER, true, false);
		clearSelectionsButton.setLayoutData(gd);

		
		selectedComponents  = new Tree(this, SWT.BORDER);
		gd =  new GridData(GridData.FILL, GridData.FILL, true, true);
		gd.widthHint = 300;
		gd.heightHint = 400;
		selectedComponents.setLayoutData(gd);

		
		bottomBar = new Composite(this,  SWT.NONE);
		gl = new GridLayout(4, true);
		bottomBar.setLayout(gl);
		
		buildButton = new Button(bottomBar, SWT.PUSH);
		buildButton.setText("Build Assets");
		gd =  new GridData(GridData.FILL, GridData.CENTER, true, false);
		buildButton.setLayoutData(gd);
		buildButton.setToolTipText("Build the selected assets.");
		
		buildAssembliesButton = new Button(bottomBar, SWT.CHECK);
		buildAssembliesButton.setText("Assemblies");
		
		buildTestsButton = new Button(bottomBar, SWT.PUSH);
		buildTestsButton.setText("Build Tests");
		buildTestsButton.setToolTipText("All applicable workers must be built first. Test assemblies will be built automatically.");
		
		runButton = new Button(bottomBar, SWT.PUSH);
		runButton.setText("Run Tests");
		gd =  new GridData(GridData.FILL, GridData.CENTER, true, false);
		runButton.setLayoutData(gd);
		runButton.setToolTipText("Run all individual tests in the selection panel.");
		
		cleanButton = new Button(bottomBar, SWT.PUSH);
		cleanButton.setText("Clean");
		gd =  new GridData(GridData.FILL, GridData.CENTER, true, false);
		cleanButton.setLayoutData(gd);
		
		text = new Text(bottomBar, SWT.BORDER);
		gd =  new GridData(GridData.FILL, GridData.CENTER, true, false);
		gd.horizontalSpan = 2;
		text.setLayoutData(gd);
		Label l = new Label(bottomBar, SWT.NONE);
		l.setText("your build label");

	}

	public void setPanelColorScheme(AvColorScheme colorScheme) {
//		addSelectionsButton.setBackground(colorScheme.getSuccess());
//		removeSelectionsButton.setBackground(colorScheme.getYield());
//		clearSelectionsButton.setBackground(colorScheme.getWarn());
		
//		buildButton.setBackground(colorScheme.getSuccess());
//		runButton.setBackground(colorScheme.getYield());
//		cleanButton.setBackground(colorScheme.getDanger());
	}
}
