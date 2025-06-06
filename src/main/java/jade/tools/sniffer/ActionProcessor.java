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

import java.util.Map;
import java.util.HashMap;
import jade.gui.AgentTree;

//#DOTNET_EXCLUDE_BEGIN
import javax.swing.JTree;
import javax.swing.tree.TreePath;
//#DOTNET_EXCLUDE_END

/*#DOTNET_INCLUDE_BEGIN
import System.Windows.Forms.*;
#DOTNET_INCLUDE_END*/

   /**
   Javadoc documentation for the file
   @author Francisco Regi, Andrea Soracchi - Universita` di Parma
   <Br>
   <a href="mailto:a_soracchi@libero.it"> Andrea Soracchi(e-mail) </a>
   @version $Date: 2014-04-03 09:02:19 +0200 (gio, 03 apr 2014) $ $Revision: 6710 $
   */

 class ActionProcessor {

   public static final String  CLEARCANVAS_ACTION="ClearCanvasAction";
   public static final String  DISPLAYLOGFILE_ACTION="DisplayLogFileAction";
   public static final String  WRITELOGFILE_ACTION="WriteLogFileAction";
   public static final String  WRITEMESSAGELIST_ACTION="MessageFileAction";
   public static final String  EXIT_SNIFFER_ACTION="ExitSnifferAction";
   public static final String  DO_SNIFFER_ACTION="DoSnifferAction";
   public static final String  DO_NOT_SNIFFER_ACTION="DoNotSnifferAction";
   public static final String  SWOW_ONLY_ACTION="ShowOnlyAction";
   public final Map actions=new HashMap<>(10);
	 private final MainPanel mainPanel;
   private SnifferAction action;

  public ActionProcessor(Sniffer mySniffer,MainPanel mainPanel) {
    this.mainPanel=mainPanel;
    actions.put(CLEARCANVAS_ACTION,new ClearCanvasAction(this,mainPanel));
    actions.put(DISPLAYLOGFILE_ACTION,new DisplayLogFileAction(this,mainPanel));
    actions.put(WRITELOGFILE_ACTION,new WriteLogFileAction(this,mainPanel));
    actions.put(WRITEMESSAGELIST_ACTION,new WriteMessageListAction(this,mainPanel));
    actions.put(EXIT_SNIFFER_ACTION,new ExitAction(this,mySniffer));
    actions.put(DO_SNIFFER_ACTION,new DoSnifferAction(this,mainPanel,mySniffer));
    actions.put(DO_NOT_SNIFFER_ACTION,new DoNotSnifferAction(this,mainPanel,mySniffer));
    actions.put(SWOW_ONLY_ACTION,new ShowOnlyAction(this,mainPanel,mySniffer));
 }

 public void process(SnifferAction act) {
  int lungpath;
  AgentTree.Node now;
  FixedAction fx;
  AgentAction agentAction;
  action = act;
  //#DOTNET_EXCLUDE_BEGIN
  TreePath[] paths;
  paths = mainPanel.treeAgent.tree.getSelectionPaths();
	 //#DOTNET_EXCLUDE_END
	 /*#DOTNET_INCLUDE_BEGIN
	 ystem.Collections.IList paths = new System.Collections.ArrayList();
	 reeNode node;
	 
	 reeNode aNode = mainPanel.treeAgent.tree.get_SelectedNode();
	 hile (aNode != null)
	 
	 	aths.Add( aNode );
	 	Node = aNode.get_Parent();
	 	f ( !(aNode instanceof AgentTree.AgentNode) ) 
	 		ode = null;
	 		DOTNET_INCLUDE_END*/

	 // Fixed actions are without parameters, so they are executed once,
	 // regardless how many tree elements are selected

	 if (action instanceof FixedAction) {
		 fixedAct();

		 // Other actions are executed for every selected tree element. This
		 // means that, if no selection is present, no action is performed.
	 }
	 else {
		 if (paths != null) {
			 //#DOTNET_EXCLUDE_BEGIN
			 lungpath = paths.length;
			 for (int i = 0;i < lungpath;i++) {
				 now = (AgentTree.Node) (paths[i].getLastPathComponent());
				 agentAct(now);
			 }
			 //#DOTNET_EXCLUDE_END
			 /*#DOTNET_INCLUDE_BEGIN
		 ungpath=paths.get_Count();
		 for (int i=0;i<lungpath;i++) 
		 {
		 	ry 
		 		
		 			w = (AgentTree.Node) (paths.get_Item(i));
		 			entAct(now);
		 		
		 		tch (ClassCastException cce) {}
		 
		 #DOTNET_INCLUDE_END*/
		 }
	 }

 } // End Process

 private void fixedAct(){
  FixedAction fx=(FixedAction)action;
  fx.doAction();
 }

 private void agentAct(AgentTree.Node node){
  AgentAction ag=(AgentAction) action;
  AgentTree.AgentNode nod;
   try {

    if(node instanceof AgentTree.AgentNode agentNode) {
       nod=agentNode;
       ag.doAction(nod);
    }

		else {
			throw new StartException();
		}

   } catch(StartException a)  {
         a.handle();
     }
 }

} 
