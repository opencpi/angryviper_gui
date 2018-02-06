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

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleConstants;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.IConsoleView;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;


/**
 * This class is responsible for managing eclipse consoles used by the project tool
 * when building and running things. It's purpose is to limit the number of console
 * used in building and to provide an appropriate console for a build so each execution
 * has it's own console.  It is also used as a communications hub between Project
 * Tool views.
 */
public class AvpsResourceManager {
	
	private static AvpsResourceManager instance = null;
	
	public static AvpsResourceManager getInstance() {
		if (instance == null) {
			instance = new AvpsResourceManager();
		}
		return instance;
	}

	protected Map<String, BuildMessageConsole> buildConsoles = null;

	protected int maxConsoles = 25;
	protected int consolesInUse = 0;
	protected int lastConsoleNumber = 0;
	
	protected StatusNotificationInterface statusMonitor;
	protected ArrayList<SelectionsInterface> selectionReceivers;
	protected ArrayList<SelectionsInterface> selectionProviders;
	
	
	
	private AvpsResourceManager() {
		buildConsoles = new LinkedHashMap<String, BuildMessageConsole>();
		statusMonitor = null;
		selectionReceivers = new ArrayList<SelectionsInterface>();
		selectionProviders = new ArrayList<SelectionsInterface>();
	}

	// ++++                             +++++
	// ++++ Manage the build consoles   +++++
	// ++++                             +++++
	// Note that MessageConsole comes from eclipse.ui.console.
	// This had to be added as a dependency (plugin.xml ->
	// dependencies tab-->add look for the package.
	
	public void writeToNoticeConsole(String message) {
		MessageConsole console = getNoticeConsoleInView();
		MessageConsoleStream out =console.newMessageStream();
		PrintStream output = new PrintStream(out);
		output.println(message);
		output.flush();
		output.close();
		
	}
	public void returnConsole(MessageConsole bldConsole) {
		if(bldConsole == null) return;
		bldConsole.clearConsole();
		BuildMessageConsole console = buildConsoles.get(bldConsole.getName());
		console.setInUse(false);
		consolesInUse--;
	}
	
	public MessageConsole getNextConsole() {
		if(consolesInUse >= maxConsoles) {
			writeToNoticeConsole("To many build consoles in use. Close some status rows to free some up.");
			return null;
		}
		if(lastConsoleNumber < maxConsoles) {
			lastConsoleNumber++;
			String name = "console-" + lastConsoleNumber;
			MessageConsole console = getThisConsoleIfAvailable(name);
			if(console != null) {
				return console;
			}
			else{
				return getNextAvailableConsole();
			}
		}
		else {
			return getNextAvailableConsole();
		}
	}
	
	private MessageConsole getThisConsoleIfAvailable(String name) {
		BuildMessageConsole  bldConsole = buildConsoles.get(name);
		MessageConsole console = null;
		if(bldConsole == null) {
			console = findConsole(name);
			bldConsole = new BuildMessageConsole();
			bldConsole.setInUse(true);
			bldConsole.setConsoleName(name);
			buildConsoles.put(name, bldConsole);
			bringConsoleToView(console);
			consolesInUse++;
		}
		return console;
	}
	
	private MessageConsole getNextAvailableConsole() {
		if(consolesInUse >= maxConsoles) {
			writeToNoticeConsole("To many build consoles in use. Close some status rows to free some up.");
			return null;
		}
		MessageConsole console = null;
		int i;
		if(lastConsoleNumber >= maxConsoles) {
			i = 0;
		}
		else {
			i = lastConsoleNumber -1;
		}
		ArrayList<BuildMessageConsole> list = new ArrayList<BuildMessageConsole>(buildConsoles.values());
		BuildMessageConsole bldConsole;

		for(; i< list.size(); i++) {
			bldConsole = list.get(i);
			if(! bldConsole.isInUse()) {
				bldConsole.setInUse(true);
				String consoleName = bldConsole.getConsoleName();
				console = findConsole(consoleName);
				bringConsoleToView(console);
				consolesInUse++;
				lastConsoleNumber = i+1;
				break;
			}
		}
		return console;
	}
	
	public void bringConsoleToView(MessageConsole console) {
		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		IWorkbenchPage page = window.getActivePage();
		String id = IConsoleConstants.ID_CONSOLE_VIEW;
		try {
			IConsoleView view =  (IConsoleView) page.showView(id);
			view.display(console);
			view.setFocus();
		} catch (PartInitException e1) {
			AvpsResourceManager.getInstance().writeToNoticeConsole(e1.getStackTrace().toString());
		}
	}
	public void bringConsoleToView(String consoleName) {
		MessageConsole console = findConsole(consoleName);
		bringConsoleToView(console);
	}
	
	protected MessageConsole findConsole(String name) {
	      ConsolePlugin plugin = ConsolePlugin.getDefault();
	      IConsoleManager conMan = plugin.getConsoleManager();
	      IConsole[] existing = conMan.getConsoles();
	      for (int i = 0; i < existing.length; i++)
	         if (name.equals(existing[i].getName()))
	            return (MessageConsole) existing[i];
	      //no console found, so create a new one
	      MessageConsole myConsole = new MessageConsole(name, null);
	      conMan.addConsoles(new IConsole[]{myConsole});
	      return myConsole;
	}
	
	public MessageConsole getNoticeConsoleInView() {
		MessageConsole noticeConsole = findConsole("notice");
		if(  !  buildConsoles.containsKey("notice") ) {
			BuildMessageConsole myConsole = new BuildMessageConsole();
			myConsole.setInUse(true);
			myConsole.setConsoleName("notice");
			buildConsoles.put("notice", myConsole);
		}
		bringConsoleToView(noticeConsole);
		return noticeConsole;
	}
	
	public boolean isBuildActive() {
		return consolesInUse>0;
	}
	
	public void registerStatusReceiver(StatusNotificationInterface statusNotificationInterface) {
		statusMonitor = statusNotificationInterface;
	}
	public void deRegisterStatusReceiver(StatusNotificationInterface statusNotificationInterface) {
		statusMonitor = null;
	}
	public StatusNotificationInterface getStatusMonitor() {
		return statusMonitor;
	}
	public void registerSelectionReceivers(SelectionsInterface selectionInterface) {
		selectionReceivers.add(selectionInterface);
	}
	public void deRegisterSelectionReceivers(SelectionsInterface selectionInterface) {
		selectionReceivers.remove(selectionInterface);
	}
	public List<SelectionsInterface> getSelectionReceivers() {
		return selectionReceivers;
	}
	
	public void registerSelectionProviders(SelectionsInterface selectionInterface) {
		selectionProviders.add(selectionInterface);
	}
	public void deRegisterSelectionProviders(SelectionsInterface selectionInterface) {
		selectionProviders.remove(selectionInterface);
	}
	public List<SelectionsInterface> getSelectionProviders() {
		return selectionProviders;
	}
	
}

class BuildMessageConsole  {
	
	private String   myConsole;
	private boolean  isInUse;
	public String getConsoleName() {
		return myConsole;
	}
	public void setConsoleName(String myConsole) {
		this.myConsole = myConsole;
	}
	public boolean isInUse() {
		return isInUse;
	}
	public void setInUse(boolean isInUse) {
		this.isInUse = isInUse;
	}
	
}
