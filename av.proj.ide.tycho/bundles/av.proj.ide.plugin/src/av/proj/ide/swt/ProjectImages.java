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

import java.io.InputStream;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.widgets.Display;

import av.proj.ide.internal.OpenCPICategory;

public class ProjectImages {
	
	private Image warnImage;
	private Image projImage;
	private Image applicationImg;
	private Image workerImg;
	private Image componentImg;
	private Image assemblyImg;
	private Image platformImg;
	private Image primitiveImg;
	private Image cardsImage;
	private Image devicesImage;
	private Image folderImage;
	private Image dlFolderImage;
	private Image testImage;
	private Image diagramImage;
	private Image equipmentImage;

	public ProjectImages(ClassLoader cl, Display display) {
		InputStream stream = cl.getResourceAsStream("icons/if_dialog-warning_32px.png");		
		ImageData imd = new ImageData(stream);
		warnImage = new Image(display, imd);
		
	    stream = cl.getResourceAsStream("icons/Toolbox.gif");
		imd = new ImageData(stream);
		projImage = new Image(display, imd);
		
	    stream = cl.getResourceAsStream("icons/Computer-16x16.gif");
		imd = new ImageData(stream);
		applicationImg = new Image(display, imd);
		
	    stream = cl.getResourceAsStream("icons/Component.gif");
		imd = new ImageData(stream);
		workerImg = new Image(display, imd);
		
	    stream = cl.getResourceAsStream("icons/Equipment.gif");
		imd = new ImageData(stream);
		componentImg = new Image(display, imd);
		
	    stream = cl.getResourceAsStream("icons/Briefcase.gif");
	    imd = new ImageData(stream);
		assemblyImg = new Image(display, imd);
		
	    stream = cl.getResourceAsStream("icons/Calculator.gif");
		imd = new ImageData(stream);
		platformImg = new Image(display, imd);

	    stream = cl.getResourceAsStream("icons/Message.gif");
		imd = new ImageData(stream);
		cardsImage = new Image(display, imd);

	    stream = cl.getResourceAsStream("icons/Iphone.gif");
		imd = new ImageData(stream);
		devicesImage = new Image(display, imd);

	    stream = cl.getResourceAsStream("icons/Pinion.gif");
		imd = new ImageData(stream);
		primitiveImg = new Image(display, imd);
		
		stream = cl.getResourceAsStream("icons/Diagram.gif");		
		imd = new ImageData(stream);
		diagramImage = new Image(display, imd);
		
		stream = cl.getResourceAsStream("icons/Equipment.gif");		
		imd = new ImageData(stream);
		equipmentImage = new Image(display, imd);
		
		
		stream = cl.getResourceAsStream("icons/Magic wand.gif");		
		imd = new ImageData(stream);
		testImage = new Image(display, imd);
		
		stream = cl.getResourceAsStream("icons/Folder.gif");		
		imd = new ImageData(stream);
		folderImage = new Image(display, imd);
		
		stream = cl.getResourceAsStream("icons/Downloads folder.gif");		
		imd = new ImageData(stream);
		dlFolderImage = new Image(display, imd);
	}
	
	public Image getWarning() {
		return warnImage;
	}

	public Image getProject() {
		return projImage;
	}

	public Image getApplications() {
		return folderImage;
	}
	
	public Image getAssemblies() {
		return diagramImage;
	}

	public Image getPlatforms() {
		return dlFolderImage;
	}

	public Image getComponents() {
		return folderImage;
	}

	public Image getPrimitives() {
		return dlFolderImage;
	}

	public Image getApplication() {
		return applicationImg;
	}

	public Image getAssembly() {
		return assemblyImg;
	}

	public Image getPlatform() {
		return platformImg;
	}

	public Image getPrimitive() {
		return primitiveImg;
	}
	public Image getComponent() {
		return componentImg;
	}
	public Image getProtocol() {
		return componentImg;
	}


	public Image getWorker() {
		return workerImg;
	}
	public Image getCard() {
		return workerImg;
	}
	public Image getDevice() {
		return workerImg;
	}
	public Image getCards() {
		return cardsImage;
	}
	public Image getDevices() {
		return devicesImage;
	}
	public Image getWorkers() {
		return folderImage;
	}

	public Image getTest() {
		return testImage;
	}

	public Image getLibrary() {
		return folderImage;
	}
	public Image getTopLevelSpecs() {
		return equipmentImage;
	}


	public Image getImage(OpenCPICategory category) {
		Image image = null;
		switch(category) {
		case applications:
			image = folderImage;
			break;
		case application:
			image = applicationImg;
			break;
		case xmlapp:
			image = componentImg;
			break;
		case assemblies:
			image = diagramImage;
			break;
		case assembly:
			image = assemblyImg;
			break;
		case componentsLibrary:
		case componentsLibraries:
		case workers:
			image = folderImage;
			break;
		case component:
		case protocol:
			image = componentImg;
			break;
			
		case cards:
			image = cardsImage;
			break;
		case devices:
			image = devicesImage;
			break;
			
		case card:
		case device:
			image = workerImg;
			break;
		case library:
			image = folderImage;
			break;
			
		case platforms:
			image = dlFolderImage;
			break;
		case platform:
			image = platformImg;
			break;
		case primitives:
			image = dlFolderImage;
			break;
		case primitive:
			image = primitiveImg;
			break;
		case project:
			break;
		case hdlTest:
		case test:
			image = testImage;
			break;
		case tests:
			image = dlFolderImage;
			break;
//		case hdlLibrary:
//			break;
//		case protocol:
//			break;
		case worker:
			image = workerImg;
			break;

		case specs:
		case topLevelSpecs:
			image = folderImage;
			break;
		default:
			break;
		
		}
		return image;
	}



}
