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

package jade.tools.dfgui;

// Import required Java classes 
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import jade.gui.GuiEvent;
import jade.domain.DFGUIAdapter;
/**
 @author Tiziana Trucco - CSELT S.p.A
 @version $Date: 2000-11-08 15:54:37 +0100 (mer, 08 nov 2000) $ $Revision: 1961 $
*/

class DFGUIRefreshAppletAction extends AbstractAction
{
	private final DFGUI gui;

	public DFGUIRefreshAppletAction(DFGUI gui)
	{
		super ("Refresh GUI");
		this.gui = gui;
	}
	
	public void actionPerformed(ActionEvent e) 
	{
		GuiEvent ev = new GuiEvent((Object)gui,DFGUIAdapter.REFRESHAPPLET);
	  gui.myAgent.postGuiEvent(ev);
	}
}