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

import java.util.List;

import org.eclipse.swt.widgets.TreeItem;

/**
 * This class is a central element of the data model supporting the project
 * view. It identifies an asset, where it is, what it is, and aspects of how to
 * build it (OcpiAssetCategory enumeration). It is also augmented by the UI
 * holding its presentation widget. This was done to readily allow updates
 * supporting its removal or readily updating its children.
 * 
 * Note that from a build perspective, a project, project subfolders are
 * considered assets as well as individual applications, component, assemblies,
 * etc.
 * 
 * This class is also used as a lookup key in a map so it can be readily found
 * (equals and hashcode are implemented). This was also done to support refresh
 * updates and makes determination of what's new and what's been removed easy
 * so changes can be sent to presentation (rather than a complete reconstruction).
 */
public class AngryViperAsset {
	
	public ProjectLocation projectLocation;
	public OpenCPICategory category;
	
	// Name is used for leaf assets.
	public String assetName;
	public String qualifiedName = null;
	public String buildName = null;
	public String libraryName = null;
	public boolean buildable = true;
	public Object assetDetails;
	
	public TreeItem assetUiItem;
	public AngryViperAsset parent;
	
	protected List<String> buildString = null;
	public void setLocation(ProjectLocation location) {
		this.projectLocation = location;
	}
	
	// Location from the project location
	String pathToAsset = null;
	String xmlFilename = null;
	String assetFolder = null;
	
	public void getPathToAsset(String path) {
		pathToAsset = path;
	}
	public void getPathToAssetFile(String path) {
		pathToAsset = path;
	}
	
	
	public void setAssetFolder(String path) {
		assetFolder = path;
	}
	
	public String getXmlFilename() {
		return xmlFilename;
	}

	public void setXmlFilename(String xmlFilename) {
		this.xmlFilename = xmlFilename;
	}

	public String getAssetLocation() {
		return pathToAsset;
	}
	
	// Purposely  placed in package scope so the factory is used to create them.
	AngryViperAsset(){}
	AngryViperAsset(String assetName, ProjectLocation location, OpenCPICategory category){
		this.assetName = assetName;
		this.projectLocation = location;
		this.category = category;
	}
	public AngryViperAsset(AngryViperAsset other){
		this.assetName = other.assetName;
		this.projectLocation = other.projectLocation;
		this.category = other.category;
		this.libraryName = other.libraryName;
		this.buildString = other.buildString;
	}

	private int myHashcode;
	private boolean hashNotBuild = true;
	@Override
	public boolean equals(Object asset) {
		// The expectation is typically it will be
		// the same object or not.
		
		if(super.equals(asset) == true) return true;
		
		if(asset instanceof AngryViperAsset)
		{
			AngryViperAsset other = (AngryViperAsset)asset;
			return this.category == other.category &&
					this.assetName.equals(other.assetName) &&
					this.projectLocation.projectName.equals(other.projectLocation.projectName) &&
					this.projectLocation.projectPath.equals(other.projectLocation.projectPath) &&
					(this.libraryName == null ? true : this.libraryName.equals(other.libraryName));
		}
		return false;
	}
	@Override
	public int hashCode() {
		if(hashNotBuild) {
			myHashcode = assetName.hashCode() + projectLocation.projectPath.hashCode() + category.name().hashCode();
			if(buildName != null) {
				myHashcode += buildName.hashCode();
			}
			if(libraryName != null) {
				myHashcode += libraryName.hashCode();
			}
			hashNotBuild = false;
		}
		return myHashcode;
	}
	private String myString = null;
	private String format = "%s %s, %s";
	
	@Override
	public String toString() {
		if(myString == null) {
			myString = String.format(format, assetName, category.getFrameworkName(), projectLocation.projectPath);
		}
		return myString;
	}
}
