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

/***
 * The AngryViperAsset has become a significant object.  It identifies the asset, has build
 * information and it is used to lookup UI model information.  This factory intends to isolate
 * AngryViperAsset construction to one place so it is constructed consistently.
 */
public class OpenCPIAssetFactory {

	public static AngryViperAsset createOcpiAsset(String name, String libraryName, OpenCPICategory cat, ProjectLocation projectLocation){
		
		AngryViperAsset asset = null;
		String libPath = null;
		switch(cat) {
		case project:
			asset = new AngryViperAsset(name, projectLocation, cat);
			asset.setAssetFolder(projectLocation.projectPath);
			break;
		case xmlapp:	
			asset = new AngryViperAsset(name, projectLocation, cat);
			asset.buildable = false;
			asset.setAssetFolder("applications");
			break;
		case application:
			asset = new AngryViperAsset(name, projectLocation, cat);
			asset.buildName = name;
			break;
		
		case library:
		case primitive:
		case platform: 
		case assembly:
			asset = new AngryViperAsset(name, projectLocation, cat);
			asset.buildName = name;
			asset.setAssetFolder(name);
			break;

		case card:
			// This represents a card worker
		case device:
			// This represents a device worker
		case hdlTest:
			// Little hack for ocpidev.  Don't overwrite project location since it is used
			// in all project assets.
			ProjectLocation loc = new ProjectLocation (projectLocation.projectName, projectLocation.projectPath + "/hdl/" + libraryName);
			asset = new AngryViperAsset(name, loc, cat);
			
			asset.buildName = name;
			asset.libraryName = libraryName;
			asset.setAssetFolder(name);
			break;

		case component:
		case protocol:
			asset = new AngryViperAsset(name, projectLocation, cat);
			asset.setAssetFolder(asset.libraryName);
			asset.buildable = false;
			asset.libraryName = libraryName;
			libPath = getLibraryPath(asset);
			asset.assetFolder = libPath + "/" + OpenCPICategory.specs.getFrameworkName();
			break;

		case worker:
			asset = new AngryViperAsset(name, projectLocation, cat);
			asset.buildName = name;
			asset.libraryName = libraryName;
			libPath = getLibraryPath(asset);
			asset.assetFolder = libPath + "/" + name;
			break;
			
		case test:
			asset = new AngryViperAsset(name, projectLocation, cat);
			asset.buildable = true;
			asset.buildName = name;
			asset.libraryName = libraryName;
			libPath = getLibraryPath(asset);
			asset.assetFolder = libPath + "/" + name;
			break;

	
		case componentsLibraries:
		case componentsLibrary:
		case tests:
			asset = new AngryViperAsset(cat.getFrameworkName(), projectLocation, cat);
			asset.buildName = cat.getFrameworkName();
			asset.setAssetFolder(cat.getFrameworkName());
			break;
			
		case applications:
			asset = new AngryViperAsset(cat.getFrameworkName(), projectLocation, cat);
			asset.setAssetFolder(name);
			break;
			
		case primitives:
		case platforms :
		case assemblies:
			asset = new AngryViperAsset(cat.getFrameworkName(), projectLocation, cat);
			asset.setAssetFolder("hld/" + name);
			break;
			
		case cards:
		case devices:
			asset = new AngryViperAsset(cat.getFrameworkName(), projectLocation, cat);
			asset.buildName = cat.getFrameworkName();
			asset.setAssetFolder("hld/" + name);
			break;
			
		// These represent non-buildable folders
		case specs:
		case topLevelSpecs:
			asset = new AngryViperAsset(cat.getFrameworkName(), projectLocation, cat);
			asset.buildable = false;
			asset.libraryName = libraryName;
			asset.setAssetFolder("specs");
			break;
			
		case workers:
			asset = new AngryViperAsset(cat.getFrameworkName(), projectLocation, cat);
			asset.buildable = false;
			// needs to be though out.  Actually this is constructed in another
			// flow of control that is not un use yet.
			asset.setAssetFolder("components");
			break;

		}
		return asset;
	}
	
	
	private static String getLibraryPath(AngryViperAsset asset) {
		String libPath = null;
		if (asset.libraryName != null) {
			String compsFolder = OpenCPICategory.componentsLibrary.getFrameworkName();
			if(asset.libraryName.equals(compsFolder)){
				libPath = asset.libraryName;
			}
			else if(asset.libraryName.equals(OpenCPICategory.specs.getFrameworkName())) {
				libPath = "";
			}
			else {
				libPath = compsFolder + "/" +asset.libraryName;
			}
		}
		return libPath;
	}


	public static AngryViperAsset createParentOcpiAsset(AngryViperAsset asset, String parentName){
		AngryViperAsset nextParent = null;
		if(asset == null)
			return null;
		
		switch(asset.category) {
		default: 
			nextParent = createOcpiAsset(asset.projectLocation.projectName, null, OpenCPICategory.project, asset.projectLocation);
			break;
			
		case project:
			nextParent = null;
			break;

			
		case application:
		case xmlapp:
			nextParent = createOcpiAsset(null, null, OpenCPICategory.applications ,asset.projectLocation);
			break;
		case primitive:
			nextParent = createOcpiAsset(null, null, OpenCPICategory.primitives ,asset.projectLocation);
			break;
		case platform: 
			nextParent = createOcpiAsset(null, null, OpenCPICategory.platforms ,asset.projectLocation);
			break;
		case assembly:
			nextParent = createOcpiAsset(null, null, OpenCPICategory.assemblies ,asset.projectLocation);
			break;

		// This is not used yet.  Creating the assets is readily supported by the asset wizard however,
		// so these cases are here to match things up. It needs to be thought out.
		case card:
		case device:
		case hdlTest:
			//nextParent = createOcpiAsset(null, null, asset.category ,asset.projectLocation);
			break;

		case component:
		case protocol:
			if(OpenCPICategory.componentsLibrary.getFrameworkName().equals(asset.libraryName)) {
				nextParent = createOcpiAsset(OpenCPICategory.specs.getFrameworkName(), asset.libraryName, OpenCPICategory.specs, asset.projectLocation);
			}
			else if(asset.libraryName.equals(OpenCPICategory.specs.getFrameworkName())) {
				nextParent = createOcpiAsset(OpenCPICategory.topLevelSpecs.getFrameworkName(), null, OpenCPICategory.topLevelSpecs, asset.projectLocation);
			}
			else {
				nextParent = createOcpiAsset(OpenCPICategory.specs.getFrameworkName(), asset.libraryName, OpenCPICategory.specs, asset.projectLocation);
			}
			
			break;
		case worker:
		case test:
			if(OpenCPICategory.componentsLibrary.getFrameworkName().equals(asset.libraryName)) {
				nextParent = createOcpiAsset(asset.libraryName, null, OpenCPICategory.componentsLibrary, asset.projectLocation);
			}
			else {
				nextParent = createOcpiAsset(asset.libraryName, null, OpenCPICategory.library ,asset.projectLocation);
			}
			break;
			
		case library:
			nextParent = createOcpiAsset(null, null, OpenCPICategory.componentsLibraries ,asset.projectLocation);
			break;
			
		case specs:
			if(OpenCPICategory.componentsLibrary.getFrameworkName().equals(asset.libraryName)) {
				nextParent = createOcpiAsset(asset.libraryName, null, OpenCPICategory.componentsLibrary, asset.projectLocation);
			}
			else {
				nextParent = createOcpiAsset(asset.libraryName, null, OpenCPICategory.library ,asset.projectLocation);
			}
			break;
		
		case topLevelSpecs:
			nextParent = createOcpiAsset(asset.projectLocation.projectName, null, OpenCPICategory.project, asset.projectLocation);
			break;
			
		//case hdlLibrary:
		//case components:
		//case applications:
		// case tests:
		//case primitives:
		//case platforms :
		//case assemblies:
		}
		
		return nextParent;
	}


}
