/*****************************************************************
JADE - Java Agent DEvelopment Framework is a framework to develop
multi-agent systems in compliance with the FIPA specifications.
Copyright (C) 2000 CSELT S.p.A. 

GNU Lesser General Public License

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation, 
version 2.1 of the License. 

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the
Free Software Foundation, Inc., 59 Temple Place - Suite 330,
Boston, MA  02111-1307, USA.
*****************************************************************/

package jade.tools.sniffer;

import java.io.Serial;
import java.util.ArrayList;
import java.util.List;

import jade.gui.AgentTree;
import jade.util.Logger;

/**
 Javadoc documentation for the file
 @author Francisco Regi, Andrea Soracchi - Universita` di Parma
 <Br>
 <a href="mailto:a_soracchi@libero.it"> Andrea Soracchi(e-mail) </a>
 
 @author Moreno LAGO
 @version $Date: 2004-04-06 15:14:07 +0200 (mar, 06 apr 2004) $ $Revision: 4971 $
*/

/**
 * For don'tsniff the Agent in the tree, and if necessary redrawing the box of
 * agent with yellow color.
 * 
 * @see jade.tools.sniffer.DoNotSnifferAction
 * @see jade.tools.sniffer.ShowOnlyAction
 */

public class ShowOnlyAction extends AgentAction {

	@Serial
	private static final long serialVersionUID = 5107905084397521486L;
	private final MainPanel mainPanel;
	private final Sniffer mySniffer;
	private final List<Agent> noSniffedAgents = new ArrayList<>();
	private Agent agent;

	public ShowOnlyAction(ActionProcessor actPro, MainPanel mainPanel, Sniffer mySniffer) {
		super("ShowOnlyActionIcon", "Show only agent(s)", actPro);
		this.mySniffer = mySniffer;
		this.mainPanel = mainPanel;
	}

	public void doAction(AgentTree.AgentNode node) {
		String realName;
		realName = checkString(node.getName());
		agent = new Agent(realName);
		noSniffedAgents.add(agent);
		if (!mainPanel.panelcan.canvAgent.isPresent(realName)) {
			mainPanel.panelcan.canvAgent.addAgent(agent); // add Agent in the Canvas
		}
		mainPanel.panelcan.canvAgent.repaintNoSniffedAgent(agent);
		mySniffer.sniffMsg(noSniffedAgents, Sniffer.SNIFF_OFF); // Sniff the Agents
		noSniffedAgents.clear();
	}

	private String checkString(String nameAgent) {
		int index;
		index = nameAgent.indexOf("@");
		if (index != -1) {
			return nameAgent.substring(0, index);
		}
		else {
			Logger.getMyLogger(this.getClass().getName()).log(Logger.WARNING, "The agent's name is not correct");
			return null;
		}
	}

}
