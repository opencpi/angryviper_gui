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

package av.proj.ide.oas.internal;

import java.util.List;

import av.proj.ide.oas.Application;
import av.proj.ide.oas.Instance;
import av.proj.ide.services.SpecPossibleValueService;

public class ApplicationUpdateThread extends Thread {
	
	private String name;
	private Application app;
	private final List<Boolean> list;
	
	public ApplicationUpdateThread(String name, Application app, List<Boolean> list) {
		this.name = name;
		this.app = app;
		this.list = list;
	}
	
	public void run() {
		while (true) {
			try {
				update();
			} catch (InterruptedException ex) {
				return;
			}
		}
	}
	
	private void update() throws InterruptedException {
		synchronized(list) {
			while (!list.get(0)) {
				list.wait();
			}

			String values = "";
			if (this.app.getInstances().size() > 0) {
				SpecPossibleValueService s = this.app.getInstances().get(0).getComponent().service(SpecPossibleValueService.class);
				values = s.getSpecsString();
			}
			
			for (Instance i : this.app.getInstances()) {
				SpecPossibleValueService s = i.getComponent().service(SpecPossibleValueService.class);
				s.refreshInstance(values);
			}
			
			list.set(0, false);
		}
	}
	
}
