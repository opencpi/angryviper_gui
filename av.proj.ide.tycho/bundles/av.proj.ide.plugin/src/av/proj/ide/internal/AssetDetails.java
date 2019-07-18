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

public class AssetDetails {
	
	AuthoringModel authoringModel;
	String[] availableBuilds;
	String[] currentBuilds;
	
	public enum AuthoringModel {
		RCC, HDL, NA;
		
		public static AuthoringModel getAuthoringModel(AngryViperAsset asset) {
			switch(asset.category) {
			case primitive:
			case primitives:
			case platform:
			case platforms:
			case assembly:
			case assemblies:
				return HDL;
				
			case device:
			case card:
			case worker:
				if(asset.assetName.toLowerCase().endsWith(".rcc")) {
					return RCC;
				}
				return HDL;
				
			default:
				return NA;
			}
		};
	}
	
	public AssetDetails(AngryViperAsset asset) {
		this.authoringModel = AuthoringModel.getAuthoringModel(asset);
	}
	
	public AuthoringModel getAuthoringModel() {
		return authoringModel;
	}

	public String[] getAvailableBuilds() {
		return availableBuilds;
	}

	public String[] getCurrentBuilds() {
		return currentBuilds;
	}
	public String[] getTargetBuilds() {
		return availableBuilds;
	}
	public void setTargetBuilds(String[] targetBuilds) {
		this.availableBuilds = targetBuilds;
	}
	public String[] getPlatformBuilds() {
		return currentBuilds;
	}
	public void setPlatformBuilds(String[] platformBuilds) {
		this.currentBuilds = platformBuilds;
	}
	
}
