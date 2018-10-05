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

import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class GetInfoDialog extends TitleAreaDialog {

	private Text userInput;
	private Label inputLabel;
	
	private String resultInput = null;

	public GetInfoDialog(Shell parentShell) {
        super(parentShell);
    }

//    @Override
//    public void create() {
//        super.create();
//        setTitle("This is my first custom dialog");
//        setMessage("This is a TitleAreaDialog", IMessageProvider.INFORMATION);
//        userInput.setText("export OCPI_REMOTE_TEST_SYSTEMS:=");
//    }
    
    @Override
    protected Control createDialogArea(Composite parent) {
        Composite area = (Composite) super.createDialogArea(parent);
        Composite container = new Composite(area, SWT.NONE);
        container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        GridLayout layout = new GridLayout(2, false);
        container.setLayout(layout);
        
        inputLabel = new Label(container, SWT.NONE);

        GridData gd = new GridData();
        gd.grabExcessHorizontalSpace = true;
        gd.horizontalAlignment = GridData.FILL;
        gd.widthHint = 300;

        userInput = new Text(container, SWT.BORDER);
        userInput.setLayoutData(gd);
        

        return area;
    }


    // overriding this methods allows you to set the
    // title of the custom dialog
//    @Override
//    protected void configureShell(Shell newShell) {
//        super.configureShell(newShell);
//        newShell.setText("Create an OCPI_REMOTE_TEST_SYSTEMS Env Variable");
//    }

    @Override
    protected Point getInitialSize() {
        return new Point(800, 400);
    }
	
    @Override
    protected boolean isResizable() {
        return true;
    }
    
    @Override
    protected void okPressed() {
    	//System.out.println("ok pressed -> input\n" + userInput.getText());
    	resultInput = userInput.getText();
        super.okPressed();
    }
	
	public static void main(String[] args) {
	    Display display = new Display();
	    Shell shell = new Shell(display);
	    shell.setSize(800, 400);
		//shell.setLayout(new FillLayout());
	    shell.open();
	    
	    GetInfoDialog dl = new GetInfoDialog(shell);
	    dl.configureShell(shell);
	    //dl.createContents(shell);
	    dl.createDialogArea(shell);
	    dl.create();
	    dl.open();
	    
	    //shell.pack();

	    while (!shell.isDisposed()) {
	      if (!display.readAndDispatch()) {
	        display.sleep();
	      }
	    }
	    display.dispose();
		
	}

	public void setInputLabel(String string) {
		inputLabel.setText(string);
	}

	public void setInitialText(String string) {
		userInput.setText(string);
	}

	public String getInput() {
		return resultInput;
	}

}
