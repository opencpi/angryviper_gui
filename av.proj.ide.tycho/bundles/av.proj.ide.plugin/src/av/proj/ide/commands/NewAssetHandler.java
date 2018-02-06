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

package av.proj.ide.commands;

import java.io.IOException;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.wizards.IWizardDescriptor;

import av.proj.ide.wizards.NewOcpiAssetWizard;

public class NewAssetHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		String project = getProjectName(HandlerUtil.getCurrentSelection(event));
		
		String id = "av.proj.ide.wizards.NewOcpiAssetWizard";
		IWizardDescriptor descriptor = PlatformUI.getWorkbench().getNewWizardRegistry().findWizard(id);
		if (descriptor == null) {
			descriptor = PlatformUI.getWorkbench().getImportWizardRegistry().findWizard(id);
		}
		if (descriptor == null) {
			descriptor = PlatformUI.getWorkbench().getExportWizardRegistry().findWizard(id);
		}
		try {
			if (descriptor != null) {
				Runtime rt = Runtime.getRuntime();
				Process proc;
				try {
					proc = rt.exec("ocpidev");
					int exitVal = proc.waitFor();
					if (exitVal != 1) {
						MessageDialog.openError(Display.getDefault().getActiveShell(), "\"ocpidev\" Command Not Found", 
								"Could not locate the \"ocpidev\" command.");
					} else {
						NewOcpiAssetWizard wizard = (NewOcpiAssetWizard)descriptor.createWizard();
						wizard.setProject(project);
						WizardDialog wd = new WizardDialog(HandlerUtil.getActiveWorkbenchWindow(event).getShell(), wizard);
						wd.setTitle(wizard.getWindowTitle());
						wd.open();
					}
				} catch (IOException | InterruptedException e) {
					MessageDialog.openError(Display.getDefault().getActiveShell(), "\"ocpidev\" Command Not Found", 
							"Could not locate the \"ocpidev\" command.");
					//e.printStackTrace();
				}
			}
		} catch (CoreException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private String getProjectName(ISelection selection) {
		String project = "";
		
		if (!selection.isEmpty()) {
			String selectionString = selection.toString();
			selectionString = selectionString.substring(1, selectionString.length()-1);
			String[] parts = selectionString.split("/");
			if (parts.length > 1) {
				project = parts[1];
			}
		}
		
		return project;
	}

}
