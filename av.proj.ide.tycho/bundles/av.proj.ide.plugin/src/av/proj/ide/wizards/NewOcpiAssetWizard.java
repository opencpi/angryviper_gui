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

package av.proj.ide.wizards;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;

import av.proj.ide.avps.internal.AvpsResourceManager;
import av.proj.ide.internal.AngryViperAsset;
import av.proj.ide.internal.AngryViperAssetService;
import av.proj.ide.internal.AngryViperProjectInfo;
import av.proj.ide.internal.CreateAssetFields;
import av.proj.ide.internal.CreateProjectFields;
import av.proj.ide.internal.OcpiAssetFileService;
import av.proj.ide.internal.OpenCPICategory;
import av.proj.ide.internal.OpencpiEnvService;
import av.proj.ide.wizards.internal.ScrollableDialog;

public class NewOcpiAssetWizard extends Wizard implements INewWizard {
	private ISelection selection;
	private NewOcpiAssetWizardPage1 page1;
	private String project = null;
	private boolean canFinish;

	public boolean setupOther = false;
	public OpenCPICategory specificType = null;
	AngryViperAsset initialSelection = null;
	
	Shell shell = null;
	
	public void setShell(Shell shell) {
		this.shell = shell;
	}

	public void setProject(String project) {
		this.project = project;
	}
	public NewOcpiAssetWizard() {
		super();
		setWindowTitle("ANGRYVIPER OpenCPI Asset Wizard");
		//setTitleBarColor(color);
		setNeedsProgressMonitor(true);
		shell = getShell();
	}
	
	public void setupSpecificAsset(OpenCPICategory selectedType, AngryViperAsset selectedAsset) {
		setupOther = true;
		specificType = selectedType;
		initialSelection = selectedAsset;
	}
	
	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		//System.out.println("AssetWizard init called??");
	}

	@Override
	public boolean performFinish() {
		canFinish = true;
		final CreateAssetFields usersRequest = page1.getUsersRequest();
		
		IRunnableWithProgress op = new IRunnableWithProgress() {
			public void run(IProgressMonitor monitor) throws InvocationTargetException {
				try {
					if (!doFinish(usersRequest, monitor)) {
						canFinish = false;
					}
				} catch (CoreException e) {
					throw new InvocationTargetException(e);
				} finally {
					monitor.done();
				}
			}
		};
		try {
			getContainer().run(true, false, op);
		} catch (InterruptedException e) {
			return false;
		} catch (InvocationTargetException e) {
			Throwable realException = e.getCause();//getTargetException();
			MessageDialog.openError(this.shell, "Error", "The received input to create Asset type: "+
			                        usersRequest.getType().toString() +" is '"+realException.getMessage()+"'");
			return false;
		}
		return canFinish;
	}

	private AngryViperAsset createNewProject(IWorkspaceRoot root, CreateProjectFields projInputs) {
		String fullPath = root.getLocation().toString();
		projInputs.setProjectPath(fullPath);
		StringBuilder sb = new StringBuilder();
		AngryViperAsset newAsset = AngryViperAssetService.getInstance().createAsset(OpenCPICategory.project, projInputs, sb);
		if(newAsset == null) {
			
			final ScrollableDialog dialog = new ScrollableDialog(this.shell, "Error Creating the new project",
					"Any remnants from asset creation were removed.\n -> ocpidev output:", sb.toString());

			Display.getDefault().syncExec(new Runnable() {
				public void run() {
					dialog.open();
				}
			});
			return null;
		}
		return newAsset;
	}
	
/******************************************************************************************************************************/
	private boolean doFinish(final CreateAssetFields usersRequest, IProgressMonitor monitor) 
			throws CoreException {
		// Run the ocpidev tool command
		monitor.setTaskName("Creating asset with ocpidev...");

		final OpenCPICategory type = usersRequest.getType();
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		
		IProject eclipseProject;
		
		if(type == OpenCPICategory.project) {
			AngryViperAsset newProject = createNewProject(root, (CreateProjectFields)usersRequest);
			// Gets a resource handle for a project.  The project itself doesn't exist in the workspace
			// until it is created below.
			eclipseProject = root.getProject(usersRequest.getProjectName());
			
			if(eclipseProject != null) {
				if(! eclipseProject.exists()) {
					eclipseProject.refreshLocal(2, monitor);
					eclipseProject.create(monitor);
					eclipseProject.open(monitor);
					
					// TODO: Now this is getting kludgey now - need to update
					// project info with the now eclipse conditions. Also need
					// to update the asset. This should get pushed down into the 
					// service somehow.  This is also getting time consuming.
					// look into optimizing it and/or giving control back to
					// the UI.
					OpencpiEnvService srv = AngryViperAssetService.getInstance().getEnvironment();
					String eclipseName = eclipseProject.getName();
					AngryViperProjectInfo projectInfo = srv.getProjectInfo(eclipseName);
					if(projectInfo != null) {
						projectInfo.eclipseName = eclipseName;
						projectInfo.setOpenInEclipse(true);
						newProject.setLocation(projectInfo.getProjectLocation());
					}
				}
				return true;
			}
			else {
				return false;
			}
		}
		// TODO - this is dumb.  AssetService should be able to get the project name from
		// name used in the input.
		String opencpiProject = usersRequest.getProjectName();
		OpencpiEnvService srv = AngryViperAssetService.getInstance().getEnvironment();
		AngryViperProjectInfo projectInfo = srv.getProjectInfo(opencpiProject);
		String projectPath = projectInfo.fullPath;
		usersRequest.setFullProjectPath(projectPath);
		StringBuilder sb = new StringBuilder();
		
		AngryViperAsset newAsset = AngryViperAssetService.getInstance().createAsset(type, usersRequest, sb);
		if(newAsset == null) {
			
			final ScrollableDialog dialog = new ScrollableDialog(this.shell, "Error Creating Asset",
					"Any remnants from asset creation were removed.\n -> ocpidev output:", sb.toString());

			Display.getDefault().syncExec(new Runnable() {
				public void run() {
					dialog.open();
				}
			});
			return false;
		}
		eclipseProject = root.getProject(projectInfo.eclipseName);
		eclipseProject.refreshLocal(2, monitor);
		OpenCPICategory cat =  newAsset.category;
		if(cat == OpenCPICategory.componentsLibraries || cat ==  OpenCPICategory.componentsLibrary || 
			cat == OpenCPICategory.library || cat == OpenCPICategory.primitive) {
			// No editor to open
			return true;
		}
		IFolder folder = OcpiAssetFileService.getAssetFolder(newAsset, eclipseProject);
		if(folder == null || ! folder.exists()) {
			AvpsResourceManager.getInstance()
			.writeToNoticeConsole("Unable to find this asset's parent folder. Use Project Explorer to look for it.");
		}
		IFile assetFile = OcpiAssetFileService.getAssetFile(newAsset, folder);
		Display display = Display.getDefault();
		
		try {
			OcpiAssetFileService.openEditor(newAsset, display, assetFile, null, monitor, true);
			
		} catch (CoreException e) {
			AvpsResourceManager.getInstance().writeToNoticeConsole("Internal Eclipse runtime error occurred. \n --> " + e.toString() );
		}
		return true;
	}
	
	@Override
	public void addPages() {
		page1 = new NewOcpiAssetWizardPage1(this, selection);
		if(project != null &&  ! project.isEmpty()) {
			page1.setInitialProjectName(project);
		}
		if(setupOther) {
			page1.setInitialAssetWizard(specificType);
			page1.setInitialAssetSelection(initialSelection);
		}
		addPage(page1);
	}
}
