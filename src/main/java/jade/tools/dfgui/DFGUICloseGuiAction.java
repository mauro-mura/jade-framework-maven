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
@author Giovanni Caire - CSELT S.p.A
@version $Date: 2004-04-06 11:39:40 +0200 (mar, 06 apr 2004) $ $Revision: 4967 $
*/

class DFGUICloseGuiAction extends AbstractAction
{
	private final DFGUI gui;

	public DFGUICloseGuiAction(DFGUI gui)
	{
		super ("Close GUI");
		this.gui = gui;
	}
	
	public void actionPerformed(ActionEvent e) 
	{
		//gui.myAgent.postCloseGuiEvent((Object) gui);
		GuiEvent ev = new GuiEvent((Object)gui,DFGUIAdapter.CLOSEGUI);
		gui.myAgent.postGuiEvent(ev);
	}
}
	
