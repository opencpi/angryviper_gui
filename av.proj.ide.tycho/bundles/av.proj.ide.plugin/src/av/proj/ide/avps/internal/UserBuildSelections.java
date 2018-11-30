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

import java.util.ArrayList;
import java.util.List;

import av.proj.ide.internal.AngryViperAsset;
import av.proj.ide.internal.OcpidevVerb;

public class UserBuildSelections {
	
	public OcpidevVerb    verb;
	public Boolean        noAssemblies = true;
	public String         buildDescription = null;
	public List<AngryViperAsset>  assetSelections = new ArrayList<AngryViperAsset>();
	public BuildTargetSelections  buildTargetSelections = null;
	
	public UserBuildSelections() {}
	public UserBuildSelections(UserBuildSelections cmd) {
		noAssemblies = cmd.noAssemblies;
		buildDescription = cmd.buildDescription;
		assetSelections = cmd.assetSelections;
		buildTargetSelections = cmd.buildTargetSelections;
	}
	
	private Integer myConfigHash = null;
	public int getConfigurationHash() {
		if(myConfigHash == null) {
			StringBuilder sb = new StringBuilder();
			
			for(AngryViperAsset asset : assetSelections) {
				Integer assetHash =  asset.hashCode();
				sb.append(assetHash);
			}
			String[] selects = buildTargetSelections.hdlBldSelects;
			for(int i= 0; i< selects.length; i++) {
				sb.append(selects[i]);
			}
			selects = buildTargetSelections.rccBldSelects;
			for(int i= 0; i< selects.length; i++) {
				sb.append(selects[i]);
			}
			//sb.append(verb.getVerb());

			String hashString = sb.toString();
			int stringHash = hashString.hashCode();
			myConfigHash = new Integer(stringHash);
		}
		
		return myConfigHash;
	}
	
}
