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

package jade.core.mobility;

import java.io.Serial;
import java.util.List;

import jade.core.AID;
import jade.core.ContainerID;
import jade.core.GenericCommand;
import jade.core.Location;

//#MIDP_EXCLUDE_FILE

import jade.core.Node;
import jade.core.SliceProxy;
import jade.core.exception.IMTPException;
import jade.core.exception.NameClashException;
import jade.core.exception.NotFoundException;
import jade.core.exception.ServiceException;
import jade.security.Credentials;
import jade.security.JADESecurityException;

/**

   The remote proxy for the JADE kernel-level service managing
   the mobility-related agent life cycle: migration and clonation.

   @author Giovanni Rimassa - FRAMeTech s.r.l.
   @author Moreno LAGO
 */
public class AgentMobilityProxy extends SliceProxy implements AgentMobilitySlice {

	@Serial
	private static final long serialVersionUID = 9178261487411863141L;

	public void createAgent(AID agentID, byte[] serializedInstance, String classSiteName, boolean isCloned, boolean startIt) throws IMTPException, ServiceException, NotFoundException, NameClashException, JADESecurityException {
		try {
			GenericCommand cmd = new GenericCommand(H_CREATEAGENT, AgentMobilitySlice.NAME, null);
			cmd.addParam(agentID);
			cmd.addParam(serializedInstance);
			cmd.addParam(classSiteName);
			cmd.addParam(Boolean.valueOf(isCloned));
			cmd.addParam(Boolean.valueOf(startIt));


			Node n = getNode();
			Object result = n.accept(cmd);
			if(result instanceof Throwable) {
				if(result instanceof IMTPException exception3) {
					throw exception3;
				}
				else if(result instanceof NotFoundException exception2) {
					throw exception2;
				}
				else if(result instanceof NameClashException exception1) {
					throw exception1;
				}
				else if(result instanceof JADESecurityException exception) {
					throw exception;
				}
				else {
					throw new IMTPException("An undeclared exception was thrown", (Throwable)result);
				}
			}
		}
		catch(ServiceException se) {
			throw new IMTPException("Unable to access remote node", se);
		}
	}

	public byte[] fetchClassFile(String className, String agentName) throws IMTPException, ClassNotFoundException {
		try {
			GenericCommand cmd = new GenericCommand(H_FETCHCLASSFILE, AgentMobilitySlice.NAME, null);
			cmd.addParam(className);
			cmd.addParam(agentName);


			Node n = getNode();
			Object result = n.accept(cmd);
			if((result != null) && (result instanceof Throwable)) {
				if(result instanceof IMTPException exception1) {
					throw exception1;
				}
				else if(result instanceof ClassNotFoundException exception) {
					throw exception;
				}
				else {
					throw new IMTPException("An undeclared exception was thrown", (Throwable)result);
				}
			}
			return (byte[])result;
		}
		catch(ServiceException se) {
			throw new IMTPException("Unable to access remote node", se);
		}
	}

	public void moveAgent(AID agentID, Location where) throws IMTPException, NotFoundException {
		try {
			GenericCommand cmd = new GenericCommand(H_MOVEAGENT, AgentMobilitySlice.NAME, null);
			cmd.addParam(agentID);
			cmd.addParam(where);


			Node n = getNode();
			Object result = n.accept(cmd);
			if((result != null) && (result instanceof Throwable)) {
				if(result instanceof IMTPException exception1) {
					throw exception1;
				}
				else if(result instanceof NotFoundException exception) {
					throw exception;
				}
				else {
					throw new IMTPException("An undeclared exception was thrown", (Throwable)result);
				}
			}
		}
		catch(ServiceException se) {
			throw new IMTPException("Unable to access remote node", se);
		}
	}

	public void copyAgent(AID agentID, Location where, String newName) throws IMTPException, NotFoundException {
		try {
			GenericCommand cmd = new GenericCommand(H_COPYAGENT, AgentMobilitySlice.NAME, null);
			cmd.addParam(agentID);
			cmd.addParam(where);
			cmd.addParam(newName);


			Node n = getNode();
			Object result = n.accept(cmd);
			if((result != null) && (result instanceof Throwable)) {
				if(result instanceof IMTPException exception1) {
					throw exception1;
				}
				else if(result instanceof NotFoundException exception) {
					throw exception;
				}
				else {
					throw new IMTPException("An undeclared exception was thrown", (Throwable)result);
				}
			}
		}
		catch(ServiceException se) {
			throw new IMTPException("Unable to access remote node", se);
		}
	}

	public boolean prepare() throws IMTPException {
		try {
			GenericCommand cmd = new GenericCommand(H_PREPARE, AgentMobilitySlice.NAME, null);


			Node n = getNode();
			Object result = n.accept(cmd);
			if((result != null) && (result instanceof Throwable)) {
				if(result instanceof IMTPException exception) {
					throw exception;
				}
				else {
					throw new IMTPException("An undeclared exception was thrown", (Throwable)result);
				}
			}

			return ((Boolean)result).booleanValue();
		}
		catch(ServiceException se) {
			throw new IMTPException("Unable to access remote node", se);
		}
	}

	public boolean transferIdentity(AID agentID, Location src, Location dest) throws IMTPException, NotFoundException {
		try {
			GenericCommand cmd = new GenericCommand(H_TRANSFERIDENTITY, AgentMobilitySlice.NAME, null);
			cmd.addParam(agentID);
			cmd.addParam(src);
			cmd.addParam(dest);


			Node n = getNode();
			Object result = n.accept(cmd);
			if((result != null) && (result instanceof Throwable)) {
				if(result instanceof IMTPException exception1) {
					throw exception1;
				}
				else if(result instanceof NotFoundException exception) {
					throw exception;
				}
				else {
					throw new IMTPException("An undeclared exception was thrown", (Throwable)result);
				}
			}

			return ((Boolean)result).booleanValue();
		}
		catch(ServiceException se) {
			throw new IMTPException("Unable to access remote node", se);
		}
	}

	public void handleTransferResult(AID agentID, boolean result, List messages) throws IMTPException, NotFoundException {
		try {
			GenericCommand cmd = new GenericCommand(H_HANDLETRANSFERRESULT, AgentMobilitySlice.NAME, null);
			cmd.addParam(agentID);
			cmd.addParam(Boolean.valueOf(result));
			cmd.addParam(messages);

			Node n = getNode();
			Object res = n.accept(cmd);
			if(res instanceof Throwable) {
				if(res instanceof IMTPException exception1) {
					throw exception1;
				}
				else if(res instanceof NotFoundException exception) {
					throw exception;
				}
				else {
					throw new IMTPException("An undeclared exception was thrown", (Throwable)res);
				}
			}
		}
		catch(ServiceException se) {
			throw new IMTPException("Unable to access remote node", se);
		}
	}

	public void clonedAgent(AID agentID, ContainerID cid, Credentials creds) throws IMTPException, JADESecurityException, NotFoundException, NameClashException {
		try {
			GenericCommand cmd = new GenericCommand(H_CLONEDAGENT, AgentMobilitySlice.NAME, null);
			cmd.addParam(agentID);
			cmd.addParam(cid);
			cmd.addParam(creds);


			Node n = getNode();
			Object result = n.accept(cmd);
			if((result != null) && (result instanceof Throwable)) {
				if(result instanceof IMTPException exception3) {
					throw exception3;
				}
				else if(result instanceof JADESecurityException exception2) {
					throw exception2;
				}
				else if(result instanceof NotFoundException exception1) {
					throw exception1;
				}
				else if(result instanceof NameClashException exception) {
					throw exception;
				}
				else {
					throw new IMTPException("An undeclared exception was thrown", (Throwable)result);
				}
			}
		}
		catch(ServiceException se) {
			throw new IMTPException("Unable to access remote node", se);
		}
	}

	
	public void cloneCodeLocatorEntry(AID oldAgentID, AID newAgentID) throws IMTPException, NotFoundException {
		try {
			GenericCommand cmd = new GenericCommand(H_CLONECODELOCATORENTRY, AgentMobilitySlice.NAME, null);
			cmd.addParam(oldAgentID);
			cmd.addParam(newAgentID);

			Node n = getNode();
			Object result = n.accept(cmd);
			if((result != null) && (result instanceof Throwable)) {
				if(result instanceof IMTPException exception1) {
					throw exception1;
				}
				else if(result instanceof NotFoundException exception) {
					throw exception;
				}
				else {
					throw new IMTPException("An undeclared exception was thrown", (Throwable)result);
				}
			}
		}
		catch(ServiceException se) {
			throw new IMTPException("Unable to access remote node", se);
		}
	}

	public void removeCodeLocatorEntry(AID name) throws IMTPException, NotFoundException {
		try {
			GenericCommand cmd = new GenericCommand(H_REMOVECODELOCATORENTRY, AgentMobilitySlice.NAME, null);
			cmd.addParam(name);

			Node n = getNode();
			Object result = n.accept(cmd);

			if((result != null) && (result instanceof Throwable)) {
				if(result instanceof IMTPException exception1) {
					throw exception1;
				}
				else if(result instanceof NotFoundException exception) {
					throw exception;
				}
				else {
					throw new IMTPException("An undeclared exception was thrown", (Throwable)result);
				}
			}
		}
		catch(ServiceException se) {
			throw new IMTPException("Unable to access remote node", se);
		}
	}
	
}
