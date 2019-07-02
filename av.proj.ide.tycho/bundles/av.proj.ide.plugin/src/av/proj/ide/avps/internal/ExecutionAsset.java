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

import java.io.File;
import java.util.List;

import av.proj.ide.internal.AngryViperAsset;
import av.proj.ide.internal.OcpidevVerb;

/**
 * This class provides a common interface to execute an ocpidev command given an
 * asset or a group of assets. This had to be separated out as another class because
 * a given asset might be used and a variety of builds (could not be done in that asset
 * object itself since it is persistent in the app). Extensions of this class are used
 * to assemble the ocpidev command for the asset then allow easy execution of
 * that command without needing additional details.
 */
public abstract class ExecutionAsset {

	protected AngryViperAsset asset;
	protected List<String>  command = null;
	protected String   shortCmd;
	
	public AngryViperAsset getAsset(){return asset;}

	public abstract List<String> getCommand(OcpidevVerb verb, Boolean flag);
	public String getDisplayString(OcpidevVerb verb){return shortCmd;}
	public abstract File getExecutionDir();
	
}
