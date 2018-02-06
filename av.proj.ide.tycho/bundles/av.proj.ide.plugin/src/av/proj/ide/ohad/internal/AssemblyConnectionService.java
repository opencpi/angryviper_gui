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
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.ElementList;
import org.eclipse.sapphire.FilteredListener;
import org.eclipse.sapphire.PropertyContentEvent;
import org.eclipse.sapphire.ReferenceValue;
import org.eclipse.sapphire.ui.def.DefinitionLoader;
import org.eclipse.sapphire.ui.diagram.ConnectionAddEvent;
import org.eclipse.sapphire.ui.diagram.ConnectionDeleteEvent;
import org.eclipse.sapphire.ui.diagram.ConnectionEndpointsEvent;
import org.eclipse.sapphire.ui.diagram.DiagramConnectionPart;
import org.eclipse.sapphire.ui.diagram.StandardConnectionService;
import org.eclipse.sapphire.ui.diagram.editor.DiagramNodePart;
import org.eclipse.sapphire.ui.diagram.editor.SapphireDiagramEditorPagePart;
import org.eclipse.sapphire.ui.forms.swt.SapphireWizard;
import org.eclipse.swt.widgets.Display;

import av.proj.ide.ohad.ConnectAttribute;
import av.proj.ide.ohad.Connection;
import av.proj.ide.ohad.ConnectionPort;
import av.proj.ide.ohad.HdlAssembly;
import av.proj.ide.ohad.Instance;
import av.proj.ide.ohad.OHADEditor;

public final class AssemblyConnectionService extends StandardConnectionService {
	
	private List<DiagramConnectionPart> connections;
	private Set<ConnectAttribute> connectAttributeElements = new HashSet<>();
	private Map<ConnectAttribute, Instance> connectNodesMap = new HashMap<>();
	private Map<Instance, Instance> connectionNodesMap = new HashMap<>();
	private SapphireDiagramEditorPagePart diagramPart;
	private ConnectEventHandler connectEventHandler = new ConnectEventHandler();
	private ConnectionEventHandler connectionEventHandler = new ConnectionEventHandler();
	private final String connectID = "ConnectAttributeConnection";
	private final String connectionID = "ConnectionElementConnection";
	
	@Override
	protected void init() {
		super.init();
		this.diagramPart = context(SapphireDiagramEditorPagePart.class);
	}
	
	@Override
	public boolean valid(DiagramNodePart node1, DiagramNodePart node2, String connectionType) {
		if (connectionType.equals(connectID)) {
			return validConnect(node1, node2);
		} else if (connectionType.equals(connectionID)) {
			return validConnection(node1, node2);
		} else {
			return super.valid(node1, node2, connectionType);
		}
	}
	
	@Override
    public DiagramConnectionPart connect(final DiagramNodePart node1, final DiagramNodePart node2, final String connectionType) {
		if (connectionType.equals(connectID)) {
			return connectConnect(node1, node2);
		} else if (connectionType.equals(connectionID)) {
			return connectionConnect(node1, node2);
		} else {
			return super.connect(node1, node2, connectionType);
		}
    }

	@Override
	public List<DiagramConnectionPart> list() {
		List<DiagramConnectionPart> allConns = new ArrayList<>();
		if (this.connections == null) {
			initConnections();
		}
		allConns.addAll(this.connections);
		allConns.addAll(super.list());
		
		return allConns;
	}
	
	private boolean validConnect(DiagramNodePart node1, DiagramNodePart node2) {
		Element src = node1.getLocalModelElement();
		if (!(src instanceof ConnectAttribute)) {
			return false;
		}
		
		Instance target = (Instance) node2.getLocalModelElement();
		if (target.getName().empty()) {
			// target must have name specified
			return false;
		}
		
		if (src.equals(target)) {
			return false; // no self-loop
		}
		
		// Source must not already be a source for existing connection
		if (this.connectionNodesMap.containsKey((Instance)src) || this.connectNodesMap.containsKey((Instance)src)) {
			return false;
		}
		
		Instance existingConnectionTarget = this.connectNodesMap.get(src);
		
		// returns true if connection does not exist yet
		return existingConnectionTarget == null || !existingConnectionTarget.equals(target);
	}
	
	private boolean validConnection(DiagramNodePart node1, DiagramNodePart node2) {
		// Source must be an instance
		Element src = node1.getLocalModelElement();
		if (!(src instanceof Instance)) {
			return false;
		}
		// Target must be an instance
		Element target = node2.getLocalModelElement();
		if (!(src instanceof Instance)) {
			return false;
		}
		// Both worker and name attributes must be specified
		Instance srcI = (Instance) src;
		Instance targetI = (Instance) target;
		if (srcI.getWorker().empty() || targetI.getWorker().empty()) {
			return false;
		}
		if (srcI.getName().empty() || targetI.getName().empty()) {
			return false;
		}
		// No self loops
		if (src.equals(target)) {
			return false;
		}
		// Source must not already be a source for existing connection
		if (this.connectionNodesMap.containsKey(srcI) || this.connectNodesMap.containsKey(src)) {
			return false;
		}
		// Connection must not exist already
		Instance existingConnectionTarget = this.connectionNodesMap.get(src);
		return existingConnectionTarget == null || !existingConnectionTarget.equals(target);
	}
	
	private DiagramConnectionPart connectConnect(DiagramNodePart node1, DiagramNodePart node2) {
		Instance target = (Instance) node2.getLocalModelElement();
		String name = target.getName().content();
		ConnectAttribute src = (ConnectAttribute) node1.getLocalModelElement();
		src.setConnect(name);
		
		Instance existingEndpoint = this.connectNodesMap.get(node1.getLocalModelElement());
		if (existingEndpoint == null) {
			return addConnectionPart(src, target);
		} else {
			return null;
		}
	}
	
	private DiagramConnectionPart connectionConnect(DiagramNodePart node1, DiagramNodePart node2) {
		Instance src = (Instance) node1.getLocalModelElement();
		Instance target = (Instance) node2.getLocalModelElement();
		
		HdlAssembly app = (HdlAssembly) diagramPart.getLocalModelElement();
		Connection conn = app.getConnections().insert();
		ConnectionPort srcPort = conn.getPorts().insert(); // create src port
		if (src.getName().content().isEmpty()) {
			srcPort.setInstance(src.getWorker().content());
		} else {
			srcPort.setInstance(src.getName().content());
		}
		srcPort.setName("out");
		srcPort.setComponentName(src.getWorker().content());
		ConnectionPort targetPort = conn.getPorts().insert(); // create target port
		if (target.getName().content().isEmpty()) {
			targetPort.setInstance(target.getWorker().content());
		} else {
			targetPort.setInstance(target.getName().content());
		}
		targetPort.setName("in");
		targetPort.setComponentName(target.getWorker().content());
		
		final DiagramConnectionPart connPart =  addConnectionPart(src, target);
		
		// Open up a wizard to select the port names
		final SapphireWizard<Connection> wizard = new SapphireWizard<Connection>(conn,
				DefinitionLoader.sdef(OHADEditor.class).wizard("DefineConnectionWizard")) {
			@Override
			public boolean performCancel() {
				connPart.remove();
				return true;
			}
		};

		final WizardDialog dialog = new WizardDialog(Display.getDefault().getActiveShell(), wizard);

		dialog.open();

		return (conn.disposed() ? null : connPart);
	}
	
	private void initConnections() {
		this.connections = new ArrayList<>();
		
		if (diagramPart != null) {
			HdlAssembly currentModelRoot = (HdlAssembly) diagramPart.getLocalModelElement();
			attachListenerForNewNodes(currentModelRoot.getInstances());
			
			//get the application connections
			//get the components within the application
			//match up each application connection with its respective component-connection
			//this assumes that the source is listed first, then the target, 
			//	although the way the application xml is defined it does not matter the order
			for (Connection connection : currentModelRoot.getConnections()) {
				if (connection instanceof Connection) {
					ElementList<ConnectionPort> ports = connection.getPorts();
					ElementList<Instance> instances = currentModelRoot.getInstances();
					Instance src = null;
					Instance target = null;
					for(ConnectionPort port : ports) {
						for(Instance instance : instances) {
							if (instance.getName().content() == null) {
								if(instance.getWorker().content().endsWith(port.getInstance().content())) {
									if(src == null) {
										src = instance;
									}
									else if(target == null) {
										target = instance;
									}
									if((src != null) && (target != null)) {
										initializeConnectionElement(src, target);
									}
								}
							} else {
								if (instance.getName().content().equals(port.getInstance().content())) {
									if(src == null) {
										src = instance;
									}
									else if(target == null) {
										target = instance;
									}
									if((src != null) && (target != null)) {
										initializeConnectionElement(src, target);
									}
								}
							}
						}
					}
				}
			}
		
			for (Instance src : currentModelRoot.getInstances()) {
				if (src instanceof ConnectAttribute) {
					initializeConnectAttribute((ConnectAttribute) src);
				}
				initializeTargetElement(src);
			}
		}
	}
	
	private void initializeTargetElement(final Instance target) {
		target.getName().attach(new FilteredListener<PropertyContentEvent>() {
			@Override
			protected void handleTypedEvent(PropertyContentEvent event) {
				connectIfIsTarget(target);
			}
		});
	}
	
	private void connectIfIsTarget(Instance target) {
		for (ConnectAttribute src : connectAttributeElements) {
			String connect = src.getConnect().content();
			if (connect != null && connect.equals(target.getName().content()) && !this.connectNodesMap.containsKey(src)) {
				addConnectionPart(src, target);
			}
		}
	}
	
	private void initializeConnectAttribute(ConnectAttribute src) {
		ReferenceValue<String, Instance> connect = src.getConnect();
		
		if (connect.target() != null) {
			addConnectionPart(src, connect.target());
		}
		this.connectAttributeElements.add(src);
		attachListenerForNewConnection(src);
	}
	
	private void initializeConnectionElement(Instance src, Instance target) {
		if (!src.getName().content().equals("") && !target.getName().content().equals("")) {
			addConnectionPart(src, target);
		}
		attachListenerForNewConnection(src, target);
	}
	
	private void attachListenerForNewNodes(final ElementList<Instance> instances) {
		instances.attach(new FilteredListener<PropertyContentEvent>() {
			@Override
			protected void handleTypedEvent(PropertyContentEvent event) {
				HdlAssembly assy = context(HdlAssembly.class);
				List<String> instanceNames = new ArrayList<String>();
				for (Instance instance : instances) {
					String name = instance.getName().content();
					if (name != null) {
						instanceNames.add(name);
					}
				}
				if (assy != null) {
					boolean connFound = false;
					// Iterate through all current assy connections
					for (Connection conn : assy.getConnections()) {
						// Look at all ports associated with this connection
						for (ConnectionPort port : conn.getPorts()) {
							String portName = port.getInstance().content();
							if (portName != null) {
								// Port instance must in the current list of instances
								if (!instanceNames.contains(portName)) {
									// Remove connection from assy and remove the diagram part associated with it
									connFound = true;
									Iterator<DiagramConnectionPart> connIt = connections.iterator();
									while (connIt.hasNext()) {
										DiagramConnectionPart c = connIt.next();
										if (c instanceof ConnectionElementConnectionPart) {
											ConnectionElementConnectionPart ce = (ConnectionElementConnectionPart)c;
											String end1Name = ce.getEndpoint1Name();
											String end2Name = ce.getEndpoint2Name();
											if (end1Name != null && end2Name != null) {
												if (end1Name.equals(portName) || end2Name.equals(portName)) {
													connIt.remove();
													connectionNodesMap.remove(c.getEndpoint1());
													broadcast(new ConnectionDeleteEvent(c));
												}
											}
										}
									}
									assy.getConnections().remove(conn);
									break;
								}
							}
							if (connFound) {
								connFound = false;
								break;
							}
						}
					}
				}
				
				Iterator<ConnectAttribute> connectIt = connectAttributeElements.iterator();
				while (connectIt.hasNext()) {
					ConnectAttribute connect = connectIt.next();
					if (!instances.contains(connect)) {
						connectIt.remove();
						Iterator<DiagramConnectionPart> connIt = connections.iterator();
						while (connIt.hasNext()) {
							DiagramConnectionPart c = connIt.next();
							if (c.getEndpoint1().equals(connect)) {
								connIt.remove();
								connectNodesMap.remove(c.getEndpoint1());
								broadcast(new ConnectionDeleteEvent(c));
							}
						}
					}
				}
				
				for (Instance instance : instances) {
					if (instance instanceof ConnectAttribute && !connectAttributeElements.contains(instance)) {
						initializeConnectAttribute((ConnectAttribute) instance);
					} 
					initializeTargetElement(instance);
					connectIfIsTarget(instance);
				}
			}
		});
	}
	
	private void attachListenerForNewConnection(final ConnectAttribute element) {
		element.attach(new FilteredListener<PropertyContentEvent>() {
			@Override
			protected void handleTypedEvent(PropertyContentEvent event) {
				if (!connectNodesMap.containsKey(element)) {
					addConnectionPart(element, element.getConnect().target());
				}
			}			
		}, ConnectAttribute.PROP_CONNECT.name());
	}
	
	private void attachListenerForNewConnection(final Instance src, final Instance target) {
		src.attach(new FilteredListener<PropertyContentEvent>() {
			@Override
			protected void handleTypedEvent(PropertyContentEvent event) {
				if (!connectionNodesMap.containsKey(src)) {
					addConnectionPart(src, target);
				}
			}
		}, Instance.PROP_NAME.name());
	}
	
	private DiagramConnectionPart addConnectionPart(ConnectAttribute src, Instance target) {
		this.connectNodesMap.put(src, target);
		ConnectAttributeConnectionPart connectionPart = new ConnectAttributeConnectionPart(src, target, this, this.connectEventHandler);
		connectionPart.init(diagramPart, src, diagramPart.getDiagramConnectionDef(connectID),
			Collections.<String, String> emptyMap());
		connectionPart.initialize();
		connections.add(connectionPart);
		return connectionPart;
	}
	
	private DiagramConnectionPart addConnectionPart(Instance src, Instance target) {
		this.connectionNodesMap.put(src, target);
		ConnectionElementConnectionPart connectionPart = new ConnectionElementConnectionPart(src, target, this, this.connectionEventHandler);
		connectionPart.init(diagramPart, src, diagramPart.getDiagramConnectionDef(connectionID),
			Collections.<String, String> emptyMap());
		connectionPart.initialize();
		connections.add(connectionPart);
		return connectionPart;
	}
	
	public SapphireDiagramEditorPagePart getDiagramEditorPagePart() {
		return this.diagramPart;
	}
	
	private final class ConnectEventHandler implements AssemblyDiagramEventHandler {

		@Override
		public void onConnectionEndpointsEvent(ConnectionEndpointsEvent event) {
			connectNodesMap.put((ConnectAttribute) event.part().getEndpoint1(),
				(Instance) event.part().getEndpoint2());
			AssemblyConnectionService.this.broadcast(event);
		}

		@Override
		public void onConnectionAddEvent(ConnectionAddEvent event) {
			AssemblyConnectionService.this.broadcast(event);
		}

		@Override
		public void onConnectionDeleteEvent(ConnectionDeleteEvent event) {
			connections.remove(event.part());
			connectNodesMap.remove(event.part().getEndpoint1());
			AssemblyConnectionService.this.broadcast(event);
		}
		
	}
	
	private final class ConnectionEventHandler implements AssemblyDiagramEventHandler {

		@Override
		public void onConnectionEndpointsEvent(ConnectionEndpointsEvent event) {
			connectionNodesMap.put((Instance) event.part().getEndpoint1(),
				(Instance) event.part().getEndpoint2());
			AssemblyConnectionService.this.broadcast(event);
		}

		@Override
		public void onConnectionAddEvent(ConnectionAddEvent event) {
			AssemblyConnectionService.this.broadcast(event);
		}

		@Override
		public void onConnectionDeleteEvent(ConnectionDeleteEvent event) {
			connections.remove(event.part());
			connectionNodesMap.remove(event.part().getEndpoint1());
			AssemblyConnectionService.this.broadcast(event);
		}
		
	}
}
