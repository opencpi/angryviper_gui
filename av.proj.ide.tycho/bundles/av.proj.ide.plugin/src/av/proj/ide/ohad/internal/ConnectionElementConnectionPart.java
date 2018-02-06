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

package av.proj.ide.ohad.internal;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.Event;
import org.eclipse.sapphire.Listener;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.services.DataService;
import org.eclipse.sapphire.ui.Point;
import org.eclipse.sapphire.ui.SapphireActionSystem;
import org.eclipse.sapphire.ui.diagram.ConnectionAddEvent;
import org.eclipse.sapphire.ui.diagram.ConnectionBendpointsEvent;
import org.eclipse.sapphire.ui.diagram.ConnectionDeleteEvent;
import org.eclipse.sapphire.ui.diagram.ConnectionEndpointsEvent;
import org.eclipse.sapphire.ui.diagram.DiagramConnectionPart;
import org.eclipse.sapphire.ui.diagram.def.IDiagramConnectionDef;
import org.eclipse.sapphire.ui.diagram.editor.DiagramNodePart;

import av.proj.ide.ohad.Connection;
import av.proj.ide.ohad.ConnectionPort;
import av.proj.ide.ohad.HdlAssembly;
import av.proj.ide.ohad.Instance;

public class ConnectionElementConnectionPart extends DiagramConnectionPart {

	private Instance src, target;
	private String srcName, targetName;
	private List<Point> bendpoints = new ArrayList<>();
	private AssemblyDiagramEventHandler eventHandler;
	private DataService<?> dataService;
	private Listener listener;
	private AssemblyConnectionService connectionService;
	private final String connID = "ConnectionElementConnection";
	
	public ConnectionElementConnectionPart(Instance src, Instance target, 
			AssemblyConnectionService service, AssemblyDiagramEventHandler handler) {
		this.src = src;
		this.srcName = src.getName().content();
		this.target = target;
		this.targetName = target.getName().content();
		this.connectionService = service;
		this.eventHandler = handler;
	}
	
	@Override
	protected void init() {
		initializeListeners();
		this.eventHandler.onConnectionAddEvent(new ConnectionAddEvent(this));
	}
	
	private void initializeListeners() {
		Value<String> name = src.getName();
		dataService = name.service(DataService.class);
		listener = new Listener() {
			@Override
			public void handle(Event event) {
				Instance newTarget = target;
				if (newTarget == null) {
					dataService.detach(this);
					eventHandler.onConnectionDeleteEvent(new ConnectionDeleteEvent(ConnectionElementConnectionPart.this));
				} else {
					changeTargetElement(newTarget);
				}
			}
		};
		dataService.attach(listener);
	}
	
	@Override
	public Set<String> getActionContexts() {
		Set<String> contextSet = new HashSet<String>();
		contextSet.add(SapphireActionSystem.CONTEXT_DIAGRAM_CONNECTION);
		contextSet.add(SapphireActionSystem.CONTEXT_DIAGRAM_CONNECTION_HIDDEN);
		return contextSet;
	}
	
	@Override
	public boolean removable() {
		return true;
	}

	@Override
	public void remove() {
		// Iterate through connections looking for this src/target combination
		HdlAssembly app = (HdlAssembly)this.connectionService.getDiagramEditorPagePart().getLocalModelElement();
		for (Connection conn : app.getConnections()) {
			boolean srcFound = false;
			boolean targetFound = false;
			for (ConnectionPort port : conn.getPorts()) {
				String portInst = port.getInstance().content();
				if (portInst.equals(src.getName().content())) {
					srcFound = true;
				} else if (portInst.equals(target.getName().content())) {
					targetFound = true;
				}
			}
			
			if (srcFound && targetFound) {
				app.getConnections().remove(conn);
				this.srcName = null;
				this.targetName = null;
			}
		}
		this.eventHandler.onConnectionDeleteEvent(new ConnectionDeleteEvent(this));		
	}

	@Override
	public String getId() {
		StringBuilder builder = new StringBuilder();
		builder.append(connID);
		builder.append(this.connectionService.list().indexOf(this));
		return builder.toString();
	}

	@Override
	public String getConnectionTypeId() {
		return connID;
	}

	@Override
	public IDiagramConnectionDef getConnectionDef() {
		return (IDiagramConnectionDef) definition;
	}

	@Override
	public DiagramConnectionPart reconnect(DiagramNodePart newSrc,
			DiagramNodePart newTarget) {
		changeTargetElement((Instance) newTarget.getLocalModelElement());
		return this;
	}

	@Override
	public boolean canEditLabel() {
		return false;
	}

	@Override
	public List<Point> getBendpoints() {
		return new ArrayList<>(bendpoints);
	}

	@Override
	public void removeAllBendpoints() {
		this.bendpoints.clear();
		broadcast(new ConnectionBendpointsEvent(this));
	}

	@Override
	public void resetBendpoints(List<Point> bendpoints) {
		this.bendpoints = bendpoints;
		broadcast(new ConnectionBendpointsEvent(this));
	}

	@Override
	public void addBendpoint(int index, int x, int y) {
		this.bendpoints.add(index, new Point(x, y));
		broadcast(new ConnectionBendpointsEvent(this));
	}

	@Override
	public void updateBendpoint(int index, int x, int y) {
		this.bendpoints.set(index, new Point(x, y));
		broadcast(new ConnectionBendpointsEvent(this));
	}

	@Override
	public void removeBendpoint(int index) {
		this.bendpoints.remove(index);
		broadcast(new ConnectionBendpointsEvent(this));

	}

	@Override
	public String getLabel() {
		return null;
	}

	@Override
	public void setLabel(String newValue) {		
	}

	@Override
	public Point getLabelPosition() {
		return null;
	}

	@Override
	public void setLabelPosition(Point newPos) {		
	}

	@Override
	public Element getEndpoint1() {
		return src;
	}

	@Override
	public Element getEndpoint2() {
		return target;
	}
	
	public String getEndpoint1Name() {
		return this.srcName;
	}
	
	public String getEndpoint2Name() {
		return this.targetName;
	}

	private void changeTargetElement(Instance newTarget) {
		String oldTarget = target.getName().content();
		target = newTarget;
		targetName = target.getName().content();
		// Might have to search through all connections looking for old target then updating to new target
		HdlAssembly app = (HdlAssembly)this.connectionService.getDiagramEditorPagePart().getLocalModelElement();
		for (Connection conn : app.getConnections()) {
			for (ConnectionPort port : conn.getPorts()) {
				String portInst = port.getInstance().content();
				if (portInst.equals(oldTarget)) {
					port.setInstance(target.getName().content());
					break;
				}
			}
		}
		removeAllBendpoints();
		eventHandler.onConnectionEndpointsEvent(new ConnectionEndpointsEvent(this));
	}
}
