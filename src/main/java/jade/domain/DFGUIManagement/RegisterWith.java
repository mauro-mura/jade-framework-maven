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


package jade.domain.DFGUIManagement;


import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.content.AgentAction;
import jade.core.AID;

/**
   This class implement the registerWith action.
   If allows the df to request to another df to register an agent.
   @author Elisabetta Cortese -  TiLab S.p.A.
   @version $Date: 2002-07-12 09:53:36 +0200 (ven, 12 lug 2002) $
*/


public class RegisterWith implements AgentAction{
	
	private AID df;
	private DFAgentDescription description;
	
	public void setDf(AID parent)
	{
		df = parent;
	}
	 
	public AID getDf()
	{
		return df;
	} 
	
	public void setDescription(DFAgentDescription desc)
	{
		description = desc;
	}
  
	public DFAgentDescription getDescription()
	{
		return description;
	}
}