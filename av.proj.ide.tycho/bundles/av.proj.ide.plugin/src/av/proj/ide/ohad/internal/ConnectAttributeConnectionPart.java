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
import org.eclipse.sapphire.ReferenceValue;
import org.eclipse.sapphire.services.ReferenceService;
import org.eclipse.sapphire.ui.Point;
import org.eclipse.sapphire.ui.SapphireActionSystem;
import org.eclipse.sapphire.ui.diagram.ConnectionAddEvent;
import org.eclipse.sapphire.ui.diagram.ConnectionBendpointsEvent;
import org.eclipse.sapphire.ui.diagram.ConnectionDeleteEvent;
import org.eclipse.sapphire.ui.diagram.ConnectionEndpointsEvent;
import org.eclipse.sapphire.ui.diagram.DiagramConnectionPart;
import org.eclipse.sapphire.ui.diagram.def.IDiagramConnectionDef;
import org.eclipse.sapphire.ui.diagram.editor.DiagramNodePart;

import av.proj.ide.ohad.ConnectAttribute;
import av.proj.ide.ohad.Instance;

public class ConnectAttributeConnectionPart extends DiagramConnectionPart {

	private ConnectAttribute srcElement;
	private Instance targetElement;
	private List<Point> bendpoints = new ArrayList<>();
	private AssemblyDiagramEventHandler eventHandler;
	private Listener listener;
	private ReferenceService<?> referenceService;
	private AssemblyConnectionService connectionService;
	private final String connID = "ConnectAttributeConnection";

	public ConnectAttributeConnectionPart(ConnectAttribute src, Instance target, 
			AssemblyConnectionService service, AssemblyDiagramEventHandler handler) {
		this.srcElement = src;
		this.targetElement = target;
		this.connectionService = service;
		this.eventHandler = handler;
	}
	
	@Override
	protected void init() {
		initializeListeners();
		this.eventHandler.onConnectionAddEvent(new ConnectionAddEvent(this));
	}
	
	private void initializeListeners() {
		ReferenceValue<String, Instance> reference = srcElement.getConnect();
		if (reference.target() != null) {
			reference.target().refresh();
		}
		referenceService = reference.service(ReferenceService.class);
		listener = new Listener() {
			@Override
			public void handle(Event event) {
				Instance newTarget = srcElement.getConnect().target();
				if (newTarget == null) {
					referenceService.detach(this);
					eventHandler.onConnectionDeleteEvent(new ConnectionDeleteEvent(ConnectAttributeConnectionPart.this));
				} else if (newTarget != targetElement) {
					changeTargetElement(newTarget);
				}
			}
		};
		referenceService.attach(listener);
	}
	
	private void changeTargetElement(Instance newTarget) {
		targetElement = newTarget;
		srcElement.setConnect(targetElement.getWorker().content());
		removeAllBendpoints();
		eventHandler.onConnectionEndpointsEvent(new ConnectionEndpointsEvent(this));
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
		srcElement.setConnect(null);
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
		return srcElement;
	}

	@Override
	public Element getEndpoint2() {
		return targetElement;
	}

}
