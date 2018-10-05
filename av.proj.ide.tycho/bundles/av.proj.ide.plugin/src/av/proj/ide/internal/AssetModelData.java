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

import java.util.ArrayList;

/**
 * This is a container class used to accumulate an asset and its children.
 * The project view obtains these as projects when it initializes and constructs
 * the project tree.  Change updates are also sent in this container.
 */
public class AssetModelData {
	AngryViperAsset asset;
	public AngryViperAsset getAsset() {
		return asset;
	}

	public ArrayList<AssetModelData> getChildList() {
		return childList;
	}

	ArrayList<AssetModelData> childList;
	
	AssetModelData(AngryViperAsset asset) {
		this.asset= asset;
		childList = new ArrayList<AssetModelData>();
	}

}
