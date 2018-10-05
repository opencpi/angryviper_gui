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

package av.proj.ide.internal;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;

import av.proj.ide.avps.internal.AvpsResourceManager;
import av.proj.ide.internal.AssetDetails.AuthoringModel;

public class OcpiAssetFileService {
	
	public static IFolder getAssetFolder(AngryViperAsset asset, IProject project) {
		if (! project.exists())
			return null;
		
		IFolder topLevelFolder = null;
		IFolder hdlFolder = null;
		IFolder assetFolder = null;
		

		switch(asset.category) {
		case application:
		case xmlapp:
			topLevelFolder = project.getFolder("applications");
			if(topLevelFolder.exists()) {
				if(asset.buildable) {
					assetFolder = topLevelFolder.getFolder(asset.assetName);
				}
				else {
					assetFolder = topLevelFolder;
				}
			}
			break;
		case assembly:
			hdlFolder = project.getFolder("hdl");
			if (hdlFolder.exists()) {
				topLevelFolder = hdlFolder.getFolder("assemblies");
				if(topLevelFolder.exists()) {
					assetFolder = topLevelFolder.getFolder(asset.assetName);
				}
			}
			break;
		case card:
			break;
		case component:
		case protocol:
			AngryViperAsset library = asset.parent.parent;
			if(library.category == OpenCPICategory.project) {
				assetFolder = project.getFolder("specs");
			}
//			else if(library.category == OpenCPICategory.componentsLibrary) {
//				topLevelFolder = project.getFolder("components");
//				IFolder specsFolder = topLevelFolder.getFolder("specs");
//				if(specsFolder.exists()) {
//					assetFolder = specsFolder;
//				}
//			}
			else {
//				//library.category == OpenCPICategory.library
//				topLevelFolder = project.getFolder("components");
//				IFolder libFolder = topLevelFolder.getFolder(library.buildName);
//				if(libFolder.exists()) {
//					assetFolder = libFolder.getFolder("specs");
//				}
				assetFolder = project.getFolder(asset.assetFolder);
			}
			break;
		case device:
			break;
		case hdlTest:
			break;
		case platform:
			hdlFolder = project.getFolder("hdl");
			if (hdlFolder.exists()) {
				topLevelFolder = hdlFolder.getFolder("platforms");
				if(topLevelFolder.exists()) {
					assetFolder = topLevelFolder.getFolder(asset.assetName);
				}
			}
			break;
		case primitive:
			break;
		case test:
			assetFolder = project.getFolder(asset.assetFolder);
//		{
//			AngryViperAsset lib = asset.parent;
//			IFolder libFolder = null;
//			if(lib.category == OpenCPICategory.componentsLibrary) {
//				libFolder = project.getFolder("components");
//			}
//			else {
//				topLevelFolder = project.getFolder("components");
//				libFolder = topLevelFolder.getFolder(lib.buildName);
//				if(libFolder.exists()) {
//					assetFolder = libFolder.getFolder(asset.assetName);
//				}
//			}
//		}
			break;
		case worker:
			topLevelFolder = project.getFolder("components");
			if(topLevelFolder.exists()) {
				if(asset.libraryName != null && ! "components".equals(asset.libraryName)) {
					IFolder libFolder = topLevelFolder.getFolder(asset.libraryName);
					if(libFolder.exists()) {
						assetFolder = libFolder.getFolder(asset.assetName);
					}
				}
				else {
					assetFolder = topLevelFolder.getFolder(asset.assetName);
				}
			}
			
			break;

		default:
			break;
		
		}
		return assetFolder;
	}
			
	
	public static IFile getAssetFile(AngryViperAsset asset, IFolder assetFolder) {
		
		if (assetFolder == null || ! assetFolder.exists())
			return null;
		
		String xmlFileName = null;
		String fileExtention = null;
		
		switch(asset.category) {
		
		default:
			fileExtention = ".xml";
			xmlFileName = asset.assetName + fileExtention;
			break;
		case xmlapp:
			xmlFileName = asset.assetName;
			break;
		case component:
			xmlFileName = asset.assetName;
			break;
		case protocol:
			xmlFileName = asset.assetName;
			break;
		case worker:
			fileExtention = ".xml";
			
			AuthoringModel aModel =AuthoringModel.getAuthoringModel(asset);
			String splitStr = "." + aModel.toString().toLowerCase();
			String s[] = asset.assetName.split(splitStr);
			xmlFileName = s[0];
			xmlFileName += fileExtention;
			break;
			
		case test:
			fileExtention = ".xml";
			xmlFileName = asset.assetName.replace('.','-' );
			xmlFileName += fileExtention;
			break;
			
		}
		 IFile assetFile = assetFolder.getFile(xmlFileName);
		 return assetFile;
	}
	
	
	public static File[] getAssetXmlFiles(IFolder parentFolder) {
		IPath parentFolderPath = parentFolder.getLocation();
		File dir = new File(parentFolderPath.toOSString());
		FileFilter filter = new XmlFileFilter();
		if(dir.exists() && dir.isDirectory()) {
			File[] files = dir.listFiles(filter);
			if(files.length > 0 ){
				return files;
			}
		}
		return null;
	}
	
	public static void openEditor(AngryViperAsset asset, Display display, IFile assetFile,
			IWorkbenchPage page, IProgressMonitor monitor, boolean monitorThread) throws CoreException{

		if(assetFile == null)
			return;


		switch(asset.category) {
		case application:
		case xmlapp:
			if(monitorThread) {
				openApplicationFile(assetFile, display, page, monitor);
			}
			else {
				IDE.openEditor(page, assetFile, "av.proj.ide.oas.OASEditor");
			}
			break;
		case assembly:
			if(monitorThread) {
				openAssemblyFile(assetFile, display, page, monitor);
			}
			else {
				IDE.openEditor(page, assetFile, "av.proj.ide.ohad.OHADEditor");
			}
			break;
		case component:
			if(monitorThread) {
				openSpecFile(assetFile, display, page, monitor);
			}
			else {
				IDE.openEditor(page, assetFile, "av.proj.ide.ocs.OCSEditor");			}
			break;
		case platform:
			if(monitorThread) {
				openPlatformFile(assetFile, display,page, monitor);
			}
			else {
				IDE.openEditor(page, assetFile, "av.proj.ide.hplat.HdlPlatform");
			}
			break;
		case protocol:
			if(monitorThread) {
				openProtocolFile(assetFile, display, page, monitor);
			}
			else {
				IDE.openEditor(page, assetFile, "av.proj.ide.ops.OPSEditor");
			}
			break;
		case test:
			if(monitorThread) {
				openUnitTestFile(assetFile, display, page, monitor);
			}
			else {
				IDE.openEditor(page, assetFile, "av.proj.ide.test.editor");
			}
			break;
		case worker:
			AuthoringModel aModel =AuthoringModel.getAuthoringModel(asset);
			if(monitorThread) {
				openWorkerFile(assetFile, display, aModel, page, monitor);
			}
			else {
				if (aModel == AuthoringModel.RCC) {
					IDE.openEditor(page, assetFile, "av.proj.ide.owd.rcc.RccWorker");
				} else if (aModel == AuthoringModel.HDL) {
					IDE.openEditor(page, assetFile, "av.proj.ide.owd.hdl.HdlWorker");
				}
			}
			break;

		default:
			break;
		
		}
	}

	
	private  static void openSpecFile(final IFile specFile, Display display, IWorkbenchPage page, IProgressMonitor monitor) {
		if(monitor != null)
			monitor.setTaskName("Opening component spec file...");
			display.asyncExec(new Runnable() {
			public void run() {
				IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
				try {
					if (specFile != null) {
						IDE.openEditor(page, specFile, "av.proj.ide.ocs.OCSEditor");
					}
				} catch (PartInitException e) {
					AvpsResourceManager.getInstance().writeToNoticeConsole("Initialize editor error occurred. \n --> " + e.toString() );
				}
			}
		});
		if(monitor != null)
			monitor.worked(1);
	}
	
	private  static void openProtocolFile(final IFile protocolFile, Display display, IWorkbenchPage page, IProgressMonitor monitor) {
		if(monitor != null)
			monitor.setTaskName("Opening protocol spec file...");
			display.asyncExec(new Runnable() {
			public void run() {
				IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
				try {
					if (protocolFile != null) {
						IDE.openEditor(page, protocolFile, "av.proj.ide.ops.OPSEditor");
					}
				} catch (PartInitException e) {
					AvpsResourceManager.getInstance().writeToNoticeConsole("Initialize editor error occurred. \n --> " + e.toString() );
				}
			}
		});
		if(monitor != null)
			monitor.worked(1);
	}
	
	private  static void openUnitTestFile(final IFile protocolFile, Display display, IWorkbenchPage page, IProgressMonitor monitor) {
		if(monitor != null)
			monitor.setTaskName("Opening unit test file...");
			display.asyncExec(new Runnable() {
			public void run() {
				IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
				try {
					if (protocolFile != null) {
						IDE.openEditor(page, protocolFile, "av.proj.ide.test.editor");
					}
				} catch (PartInitException e) {
					AvpsResourceManager.getInstance().writeToNoticeConsole("Initialize editor error occurred. \n --> " + e.toString() );
				}
			}
		});
		if(monitor != null)
			monitor.worked(1);
	}
	
	private  static void openApplicationFile(final IFile applicationFile, Display display, IWorkbenchPage page, IProgressMonitor monitor) {
		if(monitor != null)
			monitor.setTaskName("Opening application file...");
			display.asyncExec(new Runnable() {
			public void run() {
				IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
				try {
					if (applicationFile != null) {
						IDE.openEditor(page, applicationFile, "av.proj.ide.oas.OASEditor");
					}
				} catch (PartInitException e) {
					AvpsResourceManager.getInstance().writeToNoticeConsole("Initialize editor error occurred. \n --> " + e.toString() );
				}
			}
		});
		if(monitor != null)
			monitor.worked(1);
	}
	
	private  static void openWorkerFile(final IFile workerFile, Display display, final AuthoringModel authoringModel, IWorkbenchPage page, IProgressMonitor monitor) {
		if(monitor != null)
			monitor.setTaskName("Opening worker file...");
			display.asyncExec(new Runnable() {
			public void run() {
				IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
				try {
					if (workerFile != null) {
						if (authoringModel == AuthoringModel.RCC) {
							IDE.openEditor(page, workerFile, "av.proj.ide.owd.rcc.RccWorker");
						} else if (authoringModel == AuthoringModel.HDL) {
							IDE.openEditor(page, workerFile, "av.proj.ide.owd.hdl.HdlWorker");
						}
					}
				} catch (PartInitException e) {
					AvpsResourceManager.getInstance().writeToNoticeConsole("Initialize editor error occurred. \n --> " + e.toString() );
				}
			}
		});
	}
	
	private  static void openAssemblyFile(final IFile assemblyFile, Display display, IWorkbenchPage page, IProgressMonitor monitor) {
		if(monitor != null)
			monitor.setTaskName("Opening assembly file...");
			display.asyncExec(new Runnable() {
			public void run() {
				IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
				try {
					if (assemblyFile != null) {
						IDE.openEditor(page, assemblyFile, "av.proj.ide.ohad.OHADEditor");
					}
				} catch (PartInitException e) {
					AvpsResourceManager.getInstance().writeToNoticeConsole("Initialize editor error occurred. \n --> " + e.toString() );
				}
			}
		});
		if(monitor != null)
			monitor.worked(1);
	}
	
	private  static void openPlatformFile(final IFile platformFile, Display display, IWorkbenchPage page, IProgressMonitor monitor) {
		if(monitor != null)
			monitor.setTaskName("Opening platform file...");
			display.asyncExec(new Runnable() {
			public void run() {
				IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
				try {
					if (platformFile != null) {
						IDE.openEditor(page, platformFile, "av.proj.ide.hplat.HdlPlatform");
					}
				} catch (PartInitException e) {
					AvpsResourceManager.getInstance().writeToNoticeConsole("Initialize editor error occurred. \n --> " + e.toString() );
				}
			}
		});
		if(monitor != null)
			monitor.worked(1);
	}
	
	public  static String getDotProjectName(File dir) {
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
					} catch (IOException e) {
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

}
