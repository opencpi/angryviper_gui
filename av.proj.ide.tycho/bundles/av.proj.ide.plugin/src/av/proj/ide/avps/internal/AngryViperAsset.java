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

package av.proj.ide.avps.internal;

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
	
	public String assetName;
	public String buildName = null;
	public AssetLocation location;
	public OcpiAssetCategory category;
	public String libraryName = null;
	public AssetDetails assetDetails;
	public TreeItem assetUiItem;
	public AngryViperAsset parent;
	
	protected List<String> buildString = null;
	public void setLocation(AssetLocation location) {
		this.location = location;
	}
	
	public AngryViperAsset(){}
	public AngryViperAsset(String assetName, AssetLocation location, OcpiAssetCategory category){
		this.assetName = assetName;
		this.location = location;
		this.category = category;
	}
	public AngryViperAsset(AngryViperAsset other){
		this.assetName = other.assetName;
		this.location = other.location;
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
					this.location.projectName.equals(other.location.projectName) &&
					this.location.projectPath.equals(other.location.projectPath) &&
					(this.libraryName == null ? true : this.libraryName.equals(other.libraryName));
		}
		return false;
	}
	@Override
	public int hashCode() {
		if(hashNotBuild) {
			myHashcode = assetName.hashCode() + location.projectPath.hashCode() + category.getListText().hashCode();
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
			myString = String.format(format, assetName, category.getListText(), location.projectPath);
		}
		return myString;
	}
}
