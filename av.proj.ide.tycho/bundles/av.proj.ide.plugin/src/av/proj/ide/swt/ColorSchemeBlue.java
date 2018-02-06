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

import org.eclipse.swt.graphics.Color;
import org.eclipse.wb.swt.SWTResourceManager;

public class ColorSchemeBlue implements AvColorScheme {
	protected Color primary;
	protected Color secondary;
	protected Color info;
	protected Color yield;
	protected Color success;
	protected Color warn;
	protected Color danger;
	
	public ColorSchemeBlue () {
		
		// Light Blue - turned the hue, saturation, and brightness down.
		
		primary = SWTResourceManager.getColor(119, 158, 191);  // 779EBF
		secondary = SWTResourceManager.getColor(168, 197, 221); // A8C5DD
		info = SWTResourceManager.getColor(216, 231, 243);  // D8E7F3
		//success = SWTResourceManager.getColor(39, 252, 193);  // 27FCC1
		//success = SWTResourceManager.getColor(84, 255, 208);  // 54FFD0
		success = SWTResourceManager.getColor(178, 255, 234);  // B2FFEA
		yield = SWTResourceManager.getColor(255, 230, 153);  // FFE380
		warn = SWTResourceManager.getColor(255, 183, 153); // FFB799
		danger = SWTResourceManager.getColor(255, 156, 153); // FF9C99  DIST 45deg
		
		// Darker Blue and much brighter.
//		primary = SWTResourceManager.getColor(56, 145, 252);  // 3891FC
//		secondary = SWTResourceManager.getColor(138, 191, 255); // 8ABFFF
//		info = SWTResourceManager.getColor(184, 216, 255);  // B8D8FF
//		//success = SWTResourceManager.getColor(39, 252, 193);  // 27FCC1
//		//success = SWTResourceManager.getColor(84, 255, 208);  // 54FFD0
//		success = SWTResourceManager.getColor(178, 255, 234);  // B2FFEA
//		yield = SWTResourceManager.getColor(255, 227, 128);  // FFE380
//		warn = SWTResourceManager.getColor(255, 206, 128); // FFCE80
//		danger = SWTResourceManager.getColor(255, 117, 39); // FF7527
	}

	public Color getPrimary() {
		return primary;
	}

	public Color getSecondary() {
		return secondary;
	}

	public Color getInfo() {
		return info;
	}

	public Color getYield() {
		return yield;
	}

	public Color getSuccess() {
		return success;
	}

	public Color getWarn() {
		return warn;
	}

	public Color getDanger() {
		return danger;
	}

}
