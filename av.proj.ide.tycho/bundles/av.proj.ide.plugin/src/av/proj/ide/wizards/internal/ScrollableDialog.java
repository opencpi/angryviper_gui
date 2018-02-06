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

package av.proj.ide.wizards.internal;

import java.util.List;

import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class ScrollableDialog extends TitleAreaDialog {
	private String title;
	private String text;
	private String scrollableText;
	private List<String> command;
	private SystemCommandExecutor commandExecutor;
	
	public ScrollableDialog(Shell parentShell, String title, String text, String scrollableText) {
		super(parentShell);
		this.title = title;
		this.text = text;
		this.scrollableText = scrollableText;
	}
	
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite composite = (Composite)super.createDialogArea(parent);
		
		GridData gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessVerticalSpace = true;
		gridData.verticalAlignment = GridData.FILL;
		
		Text scrollable = new Text(composite, SWT.BORDER | SWT.V_SCROLL);
		scrollable.setEditable(false);
		scrollable.setLayoutData(gridData);
		scrollable.setText(scrollableText);
		
		return composite;
	}
	
	@Override
	public void create() {
		super.create();
		Rectangle rect = getShell().getBounds();
		int width = 800;
		int height = 500;
		int x = rect.x + rect.width / 2 - width / 2;
		int y = rect.y + rect.height /2 - height / 2;
		getShell().setLocation(x, y);
		getShell().setSize(width, height);
		setTitle(this.title);
		setMessage(text, IMessageProvider.ERROR);
	}
	
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		Button okButton = createButton(parent, OK, "OK", true);
		okButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				close();
			}
		});
	}
	
	@Override
	protected boolean isResizable() {
		return true;
	}
	
}
