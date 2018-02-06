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

import av.proj.ide.avps.internal.OcpiAssetCategory;

public class ProjectImages {
	
	private Image warnImage;
	private Image projImage;
	private Image applicationImg;
	private Image assemblyImg;
	private Image platformImg;
	private Image workerImg;
	private Image primitiveImg;
	private Image cardsImage;
	private Image devicesImage;
	private Image libImage;
	private Image testsImage;
	private Image testImage2;
	private Image testImage;

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

	    stream = cl.getResourceAsStream("icons/Component.gif");
		imd = new ImageData(stream);
		workerImg = new Image(display, imd);
		
	    stream = cl.getResourceAsStream("icons/Pinion.gif");
		imd = new ImageData(stream);
		primitiveImg = new Image(display, imd);
		
		stream = cl.getResourceAsStream("icons/Folder.gif");		
		imd = new ImageData(stream);
		libImage = new Image(display, imd);
		
		stream = cl.getResourceAsStream("icons/Downloads folder.gif");		
		imd = new ImageData(stream);
		testsImage = new Image(display, imd);
		
		stream = cl.getResourceAsStream("icons/Diagram.gif");		
		imd = new ImageData(stream);
		testImage = new Image(display, imd);
		
		stream = cl.getResourceAsStream("icons/Magic wand.gif");		
		imd = new ImageData(stream);
		testImage2 = new Image(display, imd);
		
	}
	
	public Image getWarning() {
		return warnImage;
	}

	public Image getProject() {
		return projImage;
	}

	public Image getApplications() {
		return libImage;
	}
	
	public Image getAssemblies() {
		return testImage;
	}

	public Image getPlatforms() {
		return testsImage;
	}

	public Image getComponents() {
		return libImage;
	}

	public Image getPrimitives() {
		return testsImage;
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

	public Image getTest() {
		return testImage2;
	}

	public Image getLibrary() {
		return libImage;
	}

	public Image getImage(OcpiAssetCategory category) {
		Image image = null;
		switch(category) {
		case applications:
			image = libImage;
			break;
		case application:
			image = applicationImg;
			break;
		case assemblies:
			image = testImage;
			break;
		case assembly:
			image = assemblyImg;
			break;
		case components:
			image = libImage;
			break;
		case component:
		case card:
		case device:
			image = workerImg;
			break;
		case library:
			image = libImage;
			break;
			
		case platforms:
			image = testsImage;
			break;
		case platform:
			image = platformImg;
			break;
		case primitives:
			image = testsImage;
			break;
		case primitive:
			image = primitiveImg;
			break;
		case project:
			break;
		case hdlTest:
		case test:
			image = testImage2;
			break;
		case tests:
			image = testsImage;
			break;
		default:
			break;
		
		}
		return image;
	}



}
