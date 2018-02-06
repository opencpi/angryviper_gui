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

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

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
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;

import av.proj.ide.wizards.internal.NewOcpiAssetWizardPage1;
import av.proj.ide.wizards.internal.ScrollableDialog;

public class NewOcpiAssetWizard extends Wizard implements INewWizard {
	private ISelection selection;
	private NewOcpiAssetWizardPage1 page1;
	private String project;
	private static final boolean OK = true;
	private boolean canFinish, confirmed;
	
	public NewOcpiAssetWizard() {
		super();
		setWindowTitle("New ANGRYVIPER Asset");
		setNeedsProgressMonitor(true);
		this.project = "";
		this.confirmed = false;
	}
	
	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		this.selection = selection;
		this.project = getProjectName(selection);
	}

	@Override
	public boolean performFinish() {
		canFinish = true;
		final String commandName = page1.getCommandName();
		IRunnableWithProgress op = new IRunnableWithProgress() {
			public void run(IProgressMonitor monitor) throws InvocationTargetException {
				try {
					if (!doFinish(commandName, monitor)) {
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
			Throwable realException = e.getTargetException();
			MessageDialog.openError(getShell(), "This Error", realException.getMessage());
			return false;
		}
		return canFinish;
	}
	
	private boolean doFinish(String commandName, IProgressMonitor monitor) 
			throws CoreException {
		// Run the ocpidev tool command
		monitor.setTaskName("Creating asset with ocpidev...");

		// Build command as list of strings
		final List<String> command = new ArrayList<String>();
		command.add("ocpidev");

		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		String addToProject = "";
		String projectName = "";
		String libraryOptionName = "";
		String specName = "";
		String protocolName = "";
		String addToType = "";
		String applicationName = "";
		boolean isXmlApp = false;
		String workerName = "";
		String workerModel = "";
		String assemblyName = "";
		switch (commandName) {
		case "Project":
			projectName = page1.getProjectName();
			String projectPrefix = page1.getProjectPrefix();
			//String projectPackage = page1.getProjectPackage();
			String[] projectDependencies = page1.getProjectDependencies();
			addToProject = root.getLocation().toString();
			command.add("-d");
			command.add(addToProject);
			command.add("create");
			command.add("project");
			command.add(projectName);
			command.add("--register");
			
			if (!projectPrefix.equals("")) {
				command.add("-F");
				command.add(projectPrefix);
			}
//			if (!projectPackage.equals("")) {
//				command.add("-K");
//				command.add(projectPackage);
//			}
			if (projectDependencies != null && projectDependencies.length > 0) {
				command.add("-D");
				String deps = "";
				boolean first = true;
				for (String s : projectDependencies) {
					if (first) {
						deps += s;
						first = false;
					} else {
						deps += ":" + s;
					}
				}
				command.add(deps);
			}
			break;
		case "Application":
			addToProject = page1.getAddToProject();
			applicationName = page1.getApplicationName();
			if (applicationName.endsWith(".xml")) {
				applicationName = applicationName.replace(".xml", "");
			}
			command.add("-d");
			command.add(addToProject);
			isXmlApp = page1.getIsXMLApp();
			if (isXmlApp) {
				command.add("-X");
			}
			command.add("create");
			command.add("application");
			command.add(applicationName);
			break;
		case "Library":
			addToProject = page1.getAddToProject();
			String libraryName = page1.getLibraryName();
			command.add("-d");
			command.add(addToProject);
			command.add("create");
			command.add("library");
			command.add(libraryName);
			break;
		case "Component":
			addToProject = page1.getAddToProject();
			libraryOptionName = page1.getLibraryOptionName();
			specName = page1.getSpecName();
			if (specName.endsWith(".xml")) {
				specName = specName.replace(".xml", "");
			}
			addToType = page1.getAddToType();
			command.add("-d");
			command.add(addToProject);
			if (addToType.equals("topLevel")) {
				command.add("-p");
			} else if (addToType.equals("library")) {
				if (!libraryOptionName.equals("") && !libraryOptionName.equals("components (default)")) {
					command.add("-l");
					command.add(libraryOptionName);
				} 
			}
			command.add("create");
			command.add("spec");
			command.add(specName);
			break;
		case "Protocol":
			addToProject = page1.getAddToProject();
			libraryOptionName = page1.getLibraryOptionName();
			protocolName = page1.getProtocolName();
			if (protocolName.endsWith(".xml")) {
				protocolName = protocolName.replace(".xml", "");
			}
			addToType = page1.getAddToType();
			command.add("-d");
			command.add(addToProject);
			if (addToType.equals("topLevel")) {
				command.add("-p");
			} else if (addToType.equals("library")) {
				if (!libraryOptionName.equals("") && !libraryOptionName.equals("components (default)")) {
					command.add("-l");
					command.add(libraryOptionName);
				} 
			}
			command.add("create");
			command.add("protocol");
			command.add(protocolName);
			break;
		case "Worker":
			addToProject = page1.getAddToProject();
			libraryOptionName = page1.getLibraryOptionName();
			workerName = page1.getWorkerName();
			if (workerName.endsWith(".xml")) {
				workerName = workerName.replace(".xml", "");
			}
			// Hack for the new package labeling
			String workerSpec = page1.getWorkerSpec();
			String[] separated = workerSpec.split("\\.");
			workerSpec = separated[separated.length -1];
			
			workerModel = page1.getWorkerModel();
			String workerLang = page1.getWorkerLang();
			command.add("-d");
			command.add(addToProject);
			if (!libraryOptionName.equals("") && !libraryOptionName.equals("components (default)")) {
				command.add("-l");
				command.add(libraryOptionName);
			}
			command.add("create");
			command.add("worker");
			command.add(workerName + "." + workerModel);
			command.add("-S");
			command.add(workerSpec);
			command.add("-L");
			command.add(workerLang);
			break;
		case "HDL Assembly":
			addToProject = page1.getAddToProject();
			assemblyName = page1.getAssemblyName();
			if (assemblyName.endsWith(".xml")) {
				assemblyName = assemblyName.replace(".xml", "");
			}
			command.add("-d");
			command.add(addToProject);
			command.add("create");
			command.add("hdl");
			command.add("assembly");
			command.add(assemblyName);
			break;
		case "HDL Card":
			addToProject = page1.getAddToProject();
			String cardName = page1.getCardName();
			command.add("-d");
			command.add(addToProject);
			command.add("create");
			command.add("card");
			command.add(cardName);
			break;
		case "HDL Slot":
			addToProject = page1.getAddToProject();
			String slotName = page1.getSlotName();
			command.add("-d");
			command.add(addToProject);
			command.add("create");
			command.add("slot");
			command.add(slotName);
			break;
		case "HDL Device":
			addToProject = page1.getAddToProject();
			String deviceName = page1.getDeviceName();
			command.add("-d");
			command.add(addToProject);
			command.add("create");
			command.add("device");
			command.add(deviceName);
			break;
		case "Proxy":
			addToProject = page1.getAddToProject();
			String proxyName = page1.getProxyName();
			command.add("-d");
			command.add(addToProject);
			command.add("create");
			command.add("proxy");
			command.add(proxyName);
			break;
		case "HDL Platform":
			addToProject = page1.getAddToProject();
			String platformName = page1.getPlatformName();
			command.add("-d");
			command.add(addToProject);
			command.add("create");
			command.add("hdl");
			command.add("platform");
			command.add(platformName);
			
			String partNumber = page1.getPartNumber();
			command.add("-g"); command.add(partNumber);
			String timeServerFreq = page1.getTimeServerFrequency();
			command.add("-q"); command.add(timeServerFreq);
			
			break;
		case "HDL Primitive Library":
			addToProject = page1.getAddToProject();
			String primLibName = page1.getPrimLibName();
			command.add("-d");
			command.add(addToProject);
			command.add("create");
			command.add("hdl");
			command.add("primitive");
			command.add("library");
			command.add(primLibName);
			break;
		case "HDL Primitive Core":
			addToProject = page1.getAddToProject();
			String primCoreName = page1.getPrimCoreName();
			command.add("-d");
			command.add(addToProject);
			command.add("create");
			command.add("primitive");
			command.add("core");
			command.add(primCoreName);
			break;
		default:
			break;
		}

		// Execute command
		ProcessBuilder pb = new ProcessBuilder(command);
		pb.redirectErrorStream(true);
		Process process;
		InputStream in = null;
		BufferedReader r = null;
		OutputStream stdOut = null;
		PrintWriter pw = null;
		try {
			process = pb.start();
		
			int result = process.waitFor();
	
			// Display an error dialog if something went wrong
			if (result != 0) {
				in = process.getInputStream();
				r = new BufferedReader(new InputStreamReader(in));
				StringBuilder sb = new StringBuilder();
				String line;
				while ((line = r.readLine()) != null) {
					sb.append(line + "\n");
				}
				process.destroy();
				in.close();
				r.close();
				final String error = sb.toString();
				final ScrollableDialog dialog = new ScrollableDialog(getShell(), "Error Creating Asset",
						"There was a problem with ocpidev.\n ->", error);
				
				Display.getDefault().syncExec(new Runnable() {
					public void run() {
						int retval = dialog.open();
						if (retval == 0) {
							confirmed = MessageDialog.openConfirm(getShell(), "Confirm", 
									"Would you like to delete the generated resources associated with previous creation attempt?");
						}
					}
				});
				if (confirmed) {
					int idx = command.indexOf("create");
					command.add(idx, "delete");
					command.remove("create");
					if (command.contains("-S")) {
						idx = command.indexOf("-S");
						command.remove(idx);
						command.remove(idx);
					}
					if (command.contains("-L")) {
						idx = command.indexOf("-L");
						command.remove(idx);
						command.remove(idx);
					}
					pb = new ProcessBuilder(command);
					process = pb.start();
					stdOut = process.getOutputStream();
					pw = new PrintWriter(stdOut);
					pw.println("y");
					pw.flush();
					pw.close();
					stdOut.close();
					process.waitFor();
					process.destroy();

				}
				return !OK;
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			e.printStackTrace();
		} finally {
			try {
				if (in != null) {
					in.close();
				}
				if (r != null) {
					r.close();
				}
				if (stdOut != null) {
					r.close();
				}
				if (pw != null) {
					r.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		

		final IProject project;
		if (getProject().equals("")) {
			String[] split = addToProject.split(root.getFullPath().toString());
			String projName = split[split.length - 1];
			String dotProjName = getDotProjectName(new File(addToProject));
			if (!dotProjName.equals("")) {
				project = root.getProject(dotProjName);
			} else {
				project = root.getProject(projName);
			}
		} else {
			project = root.getProject(getProject());
		}
		project.refreshLocal(2, monitor);

		switch (commandName) {
		case "Project":
			final IProject newProject = root.getProject(projectName);
			if (!newProject.exists()) {
				newProject.create(monitor);
				newProject.open(monitor);
			}
			break;
		case "Component":
			// Get the spec file
			IFile specFile = null;
			if (project.exists()) {
				if (addToProject.equals("true")) {
					IFolder specsFolder = project.getFolder("specs");
					if (specsFolder.exists()) {
						specFile = specsFolder.getFile(specName + "-spec.xml");
					}
				} else {
					final IFolder compsFolder = project.getFolder("components");
					if (compsFolder.exists()) {
						if (libraryOptionName.equals("") || libraryOptionName.equals("components (default)")) {
							IFolder specsFolder = compsFolder.getFolder("specs");
							if (specsFolder.exists()) {
								specFile = specsFolder.getFile(specName + "-spec.xml");
							}
						} else {
							IFolder libFolder = compsFolder.getFolder(libraryOptionName);
							if (libFolder.exists()) {
								IFolder specsFolder = libFolder.getFolder("specs");
								if (specsFolder.exists()) {
									specFile = specsFolder.getFile(specName + "-spec.xml");
								}
							}
						}
					}
				}
			}
			// Open spec file with editor
			openSpecFile(specFile, monitor);
			break;
		case "Protocol":
			// Get the protocol file
			IFile protocolFile = null;
			if (project.exists()) {
				if (addToProject.equals("true")) {
					IFolder specsFolder = project.getFolder("specs");
					if (specsFolder.exists()) {
						protocolFile = specsFolder.getFile(protocolName + "-prot.xml");
					}
				} else {
					IFolder compsFolder = project.getFolder("components");
					if (compsFolder.exists()) {
						if (libraryOptionName.equals("") || libraryOptionName.equals("components (default)")) {
							IFolder specsFolder = compsFolder.getFolder("specs");
							if (specsFolder.exists()) {
								protocolFile = specsFolder.getFile(protocolName + "-prot.xml");
							}
						} else {
							IFolder libFolder = compsFolder.getFolder(libraryOptionName);
							if (libFolder.exists()) {
								IFolder specsFolder = libFolder.getFolder("specs");
								if (specsFolder.exists()) {
									protocolFile = specsFolder.getFile(protocolName + "-prot.xml");
								}
							}
						}
					}
				}
			}
			// Open protocol file with editor
			openProtocolFile(protocolFile, monitor);
			break;
		case "Application":
			// Get the application file
			IFile applicationFile = null;
			if (project.exists()) {
				IFolder appsFolder = project.getFolder("applications");
				if (appsFolder.exists()) {
					if (isXmlApp) {
						applicationFile = appsFolder.getFile(applicationName + ".xml");
					} else {
						IFolder appFolder = appsFolder.getFolder(applicationName);
						if (appFolder.exists()) {
							applicationFile = appFolder.getFile(applicationName + ".xml");
						}
					}
				}
			}
			// Open the application file with editor
			openApplicationFile(applicationFile, monitor);
			break;
		case "Worker":
			// Get the worker file
			IFile workerFile = null;
			if (project.exists()) {
				IFolder compsFolder = project.getFolder("components");
				if (compsFolder.exists()) {
					if (libraryOptionName.equals("") || libraryOptionName.equals("components (default)")) {
						IFolder workerFolder = compsFolder.getFolder(workerName + "." + workerModel.toLowerCase());
						if (workerFolder.exists()) {
							workerFile = workerFolder.getFile(workerName + ".xml");
						}

					} else {
						IFolder libFolder = compsFolder.getFolder(libraryOptionName);
						if (libFolder.exists()) {
							IFolder workerFolder = libFolder.getFolder(workerName + "." + workerModel.toLowerCase());
							if (workerFolder.exists()) {
								workerFile = workerFolder.getFile(workerName + ".xml");
							}
						}
					}
				}
			}
			// Open the application file with editor
			openWorkerFile(workerFile, workerModel, monitor);
			break;
		case "HDL Assembly":
			// Get the assembly file
			IFile assemblyFile = null;
			if (project.exists()) {
				IFolder hdlFolder = project.getFolder("hdl");
				if (hdlFolder.exists()) {
					IFolder assembliesFolder = hdlFolder.getFolder("assemblies");
					if (assembliesFolder.exists()) {
						IFolder assemblyFolder = assembliesFolder.getFolder(assemblyName);
						if (assemblyFolder.exists()) {
							assemblyFile = assemblyFolder.getFile(assemblyName + ".xml");
						}
					}
				}
			}
			// Open the assembly file with editor
			openAssemblyFile(assemblyFile, monitor);
			break;
		case "HDL Platform":
			// Get the platform file
			IFile platformFile = null;
			if (project.exists()) {
				IFolder hdlFolder = project.getFolder("hdl");
				if (hdlFolder.exists()) {
					IFolder platformsFolder = hdlFolder.getFolder("platforms");
					if (platformsFolder.exists()) {
						String platformName = page1.getPlatformName();
						IFolder platformFolder = platformsFolder.getFolder(platformName);
						if (platformFolder.exists()) {
							platformFile = platformFolder.getFile(platformName + ".xml");
						}
					}
				}
			}
			// Open the assembly file with editor
			openPlatformFile(platformFile, monitor);
			break;
		default:
			break;
		}
		
		return OK;
	}
	
	private void openSpecFile(final IFile specFile, IProgressMonitor monitor) {
		monitor.setTaskName("Opening component spec file...");
		getShell().getDisplay().asyncExec(new Runnable() {
			public void run() {
				IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
				try {
					if (specFile != null) {
						IDE.openEditor(page, specFile, "av.proj.ide.ocs.OCSEditor");
					}
				} catch (PartInitException e) {
				}
			}
		});
		monitor.worked(1);
	}
	
	private void openProtocolFile(final IFile protocolFile, IProgressMonitor monitor) {
		monitor.setTaskName("Opening protocol spec file...");
		getShell().getDisplay().asyncExec(new Runnable() {
			public void run() {
				IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
				try {
					if (protocolFile != null) {
						IDE.openEditor(page, protocolFile, "av.proj.ide.ops.OPSEditor");
					}
				} catch (PartInitException e) {
				}
			}
		});
		monitor.worked(1);
	}
	
	private void openApplicationFile(final IFile applicationFile, IProgressMonitor monitor) {
		monitor.setTaskName("Opening application file...");
		getShell().getDisplay().asyncExec(new Runnable() {
			public void run() {
				IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
				try {
					if (applicationFile != null) {
						IDE.openEditor(page, applicationFile, "av.proj.ide.oas.OASEditor");
					}
				} catch (PartInitException e) {
				}
			}
		});
		monitor.worked(1);
	}
	
	private void openWorkerFile(final IFile workerFile, final String workerModel, IProgressMonitor monitor) {
		monitor.setTaskName("Opening worker file...");
		getShell().getDisplay().asyncExec(new Runnable() {
			public void run() {
				IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
				try {
					if (workerFile != null) {
						if (workerModel.toLowerCase().equals("rcc")) {
							IDE.openEditor(page, workerFile, "av.proj.ide.owd.rcc.RccWorker");
						} else if (workerModel.toLowerCase().equals("hdl")) {
							IDE.openEditor(page, workerFile, "av.proj.ide.owd.hdl.HdlWorker");
						}
					}
				} catch (PartInitException e) {
				}
			}
		});
	}
	
	private void openAssemblyFile(final IFile assemblyFile, IProgressMonitor monitor) {
		monitor.setTaskName("Opening assembly file...");
		getShell().getDisplay().asyncExec(new Runnable() {
			public void run() {
				IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
				try {
					if (assemblyFile != null) {
						IDE.openEditor(page, assemblyFile, "av.proj.ide.ohad.OHADEditor");
					}
				} catch (PartInitException e) {
				}
			}
		});
		monitor.worked(1);
	}
	
	private void openPlatformFile(final IFile platformFile, IProgressMonitor monitor) {
		monitor.setTaskName("Opening platform file...");
		getShell().getDisplay().asyncExec(new Runnable() {
			public void run() {
				IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
				try {
					if (platformFile != null) {
						IDE.openEditor(page, platformFile, "av.proj.ide.hplat.HdlPlatform");
					}
				} catch (PartInitException e) {
					System.out.println(e);
				}
			}
		});
		monitor.worked(1);
	}
	
	private String getDotProjectName(File dir) {
		String name = "";
		
		if (dir != null && dir.exists() && dir.isDirectory()) {
			String[] children = dir.list();
			for (String s : children) {
				if (s.equals(".project")) {
					BufferedReader bufferedReader = null;
					File dotProj = new File(dir, s);
					String line = null;
					try {
						FileReader fileReader = new FileReader(dotProj);
						bufferedReader = new BufferedReader(fileReader);
						while((line = bufferedReader.readLine()) != null) {
							if (line.contains("<name>")) {
								name = line.replace("<name>", "").replace("</name>", "").trim();
								break;
							}
						}
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
					finally {
						if(bufferedReader != null) {
							try {
								bufferedReader.close();
							} catch (IOException e) {
							}
						}
					}
				}
			}
		}
		
		return name;
	}
	
	
	@Override
	public void addPages() {
		page1 = new NewOcpiAssetWizardPage1(this, selection);
		addPage(page1);
	}
	
	public void setProject(String project) {
		this.project = project;
	}
	
	public String getProject() {
		return this.project;
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
