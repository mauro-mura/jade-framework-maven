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

package jade.core.messaging;

import java.util.Hashtable;
import java.util.List;

import jade.core.Node;
import jade.core.SliceProxy;
import jade.core.exception.IMTPException;
import jade.core.exception.NotFoundException;
import jade.core.exception.ServiceException;
import jade.core.GenericCommand;
import jade.core.AID;
import jade.core.ContainerID;
import jade.security.JADESecurityException;

import jade.domain.FIPAAgentManagement.Envelope;

import jade.mtp.MTPDescriptor;
import jade.mtp.exception.MTPException;


/**
 The remote proxy for the JADE kernel-level service managing
 the message passing subsystem installed in the platform.
 
 @author Giovanni Rimassa - FRAMeTech s.r.l.
 @author Moreno LAGO
 */
public class MessagingProxy extends SliceProxy implements MessagingSlice {
	
	
	public void dispatchLocally(AID senderID, GenericMessage msg, AID receiverID) throws IMTPException, NotFoundException, JADESecurityException {
		try {
			GenericCommand cmd = new GenericCommand(H_DISPATCHLOCALLY, NAME, null);
			cmd.addParam(senderID);
			cmd.addParam(msg);
			cmd.addParam(receiverID);
			long timeStamp = msg.getTimeStamp();
			if (timeStamp > 0) {
				cmd.addParam(Long.valueOf(timeStamp));
			}			
			cmd.setPrincipal(msg.getSenderPrincipal());
			cmd.setCredentials(msg.getSenderCredentials());
			
			Node n = getNode();
			Object result = n.accept(cmd);
			if((result != null) && (result instanceof Throwable throwable)) {
				if(result instanceof IMTPException exception2) {
					throw exception2;
				}
				else if(result instanceof NotFoundException exception1) {
					throw exception1;
				}
				else if(result instanceof JADESecurityException exception) {
					throw exception;
				}
				else {
					throw new IMTPException("An undeclared exception was thrown", throwable);
				}
			}
		}
		catch(ServiceException se) {
			throw new IMTPException("Unable to access remote node", se);
		}
	}
	
	public void routeOut(Envelope env, byte[] payload,AID receiverID, String address) throws IMTPException, MTPException {
		try {
			GenericCommand cmd = new GenericCommand(H_ROUTEOUT, NAME, null);
			cmd.addParam(env);
			cmd.addParam(payload);
			cmd.addParam(receiverID);
			cmd.addParam(address);
			
			
			Node n = getNode();
			Object result = n.accept(cmd);
			if((result != null) && (result instanceof Throwable throwable)) {
				if(result instanceof IMTPException exception1) {
					throw exception1;
				}
				else if(result instanceof MTPException exception) {
					throw exception;
				}
				else {
					throw new IMTPException("An undeclared exception was thrown", throwable);
				}
			}
		}
		catch(ServiceException se) {
			throw new IMTPException("Unable to access remote node", se);
		}
	}
	
	public ContainerID getAgentLocation(AID agentID) throws IMTPException, NotFoundException {
		try {
			GenericCommand cmd = new GenericCommand(H_GETAGENTLOCATION, NAME, null);
			cmd.addParam(agentID);
			
			
			Node n = getNode();
			Object result = n.accept(cmd);
			if((result != null) && (result instanceof Throwable throwable)) {
				if(result instanceof IMTPException exception1) {
					throw exception1;
				}
				else if(result instanceof NotFoundException exception) {
					throw exception;
				}
				else {
					throw new IMTPException("An undeclared exception was thrown", throwable);
				}
			}
			return (ContainerID)result;
		}
		catch(ServiceException se) {
			throw new IMTPException("Unable to access remote node", se);
		}
	}
	
	public MTPDescriptor installMTP(String address, String className) throws IMTPException, ServiceException, MTPException {
		try {
			GenericCommand cmd = new GenericCommand(H_INSTALLMTP, NAME, null);
			cmd.addParam(address);
			cmd.addParam(className);
			
			
			Node n = getNode();
			Object result = n.accept(cmd);
			if((result != null) && (result instanceof Throwable throwable)) {
				if(result instanceof IMTPException exception2) {
					throw exception2;
				}
				else if(result instanceof MTPException exception1) {
					throw exception1;
				}
				else if(result instanceof ServiceException exception) {
					throw exception;
				}
				else {
					throw new IMTPException("An undeclared exception was thrown", throwable);
				}
			}
			return (MTPDescriptor)result;
		}
		catch(ServiceException se) {
			throw new IMTPException("Unable to access remote node", se);
		}
	}
	
	public void uninstallMTP(String address) throws IMTPException, ServiceException, NotFoundException, MTPException {
		try {
			GenericCommand cmd = new GenericCommand(H_UNINSTALLMTP, NAME, null);
			cmd.addParam(address);
			
			
			Node n = getNode();
			Object result = n.accept(cmd);
			if((result != null) && (result instanceof Throwable throwable)) {
				if(result instanceof IMTPException exception2) {
					throw exception2;
				}
				else if(result instanceof NotFoundException exception1) {
					throw exception1;
				}
				else if(result instanceof ServiceException exception) {
					throw exception;
				}
				else {
					throw new IMTPException("An undeclared exception was thrown", throwable);
				}
			}
		}
		catch(ServiceException se) {
			throw new IMTPException("Unable to access remote node", se);
		}
	}
	
	public void newMTP(MTPDescriptor mtp, ContainerID cid) throws IMTPException, ServiceException {
		try {
			GenericCommand cmd = new GenericCommand(H_NEWMTP, NAME, null);
			cmd.addParam(mtp);
			cmd.addParam(cid);
			
			
			Node n = getNode();
			Object result = n.accept(cmd);
			if((result != null) && (result instanceof Throwable throwable)) {
				if(result instanceof IMTPException exception1) {
					throw exception1;
				}
				else if(result instanceof ServiceException exception) {
					throw exception;
				}
				else {
					throw new IMTPException("An undeclared exception was thrown", throwable);
				}
			}
		}
		catch(ServiceException se) {
			throw new IMTPException("Unable to access remote node", se);
		}
	}
	
	public void deadMTP(MTPDescriptor mtp, ContainerID cid) throws IMTPException, ServiceException {
		try {
			GenericCommand cmd = new GenericCommand(H_DEADMTP, NAME, null);
			cmd.addParam(mtp);
			cmd.addParam(cid);
			
			
			Node n = getNode();
			Object result = n.accept(cmd);
			if((result != null) && (result instanceof Throwable throwable)) {
				if(result instanceof IMTPException exception1) {
					throw exception1;
				}
				else if(result instanceof ServiceException exception) {
					throw exception;
				}
				else {
					throw new IMTPException("An undeclared exception was thrown", throwable);
				}
			}
		}
		catch(ServiceException se) {
			throw new IMTPException("Unable to access remote node", se);
		}
	}
	
	public void addRoute(MTPDescriptor mtp, String sliceName) throws IMTPException, ServiceException {
		try {
			GenericCommand cmd = new GenericCommand(H_ADDROUTE, NAME, null);
			cmd.addParam(mtp);
			cmd.addParam(sliceName);
			
			
			Node n = getNode();
			Object result = n.accept(cmd);
			if((result != null) && (result instanceof Throwable throwable)) {
				if(result instanceof IMTPException exception1) {
					throw exception1;
				}
				else if(result instanceof ServiceException exception) {
					throw exception;
				}
				else {
					throw new IMTPException("An undeclared exception was thrown", throwable);
				}
			}
		}
		catch(ServiceException se) {
			throw new IMTPException("Unable to access remote node", se);
		}
	}
	
	public void removeRoute(MTPDescriptor mtp, String sliceName) throws IMTPException, ServiceException {
		try {
			GenericCommand cmd = new GenericCommand(H_REMOVEROUTE, NAME, null);
			cmd.addParam(mtp);
			cmd.addParam(sliceName);
			
			
			Node n = getNode();
			Object result = n.accept(cmd);
			if((result != null) && (result instanceof Throwable throwable)) {
				if(result instanceof IMTPException exception1) {
					throw exception1;
				}
				else if(result instanceof ServiceException exception) {
					throw exception;
				}
				else {
					throw new IMTPException("An undeclared exception was thrown", throwable);
				}
			}
		}
		catch(ServiceException se) {
			throw new IMTPException("Unable to access remote node", se);
		}
	}

	public void newAlias(AID alias, AID agent) throws IMTPException {
		try {
			GenericCommand cmd = new GenericCommand(H_NEWALIAS, NAME, null);
			cmd.addParam(alias);
			cmd.addParam(agent);
					
			Node n = getNode();
			Object result = n.accept(cmd);
			if((result != null) && (result instanceof Throwable throwable)) {
				if(result instanceof IMTPException exception) {
					throw exception;
				}
				else {
					throw new IMTPException("An undeclared exception was thrown", throwable);
				}
			}
		}
		catch(ServiceException se) {
			// Should never happen
			throw new IMTPException("Unable to access remote node stub", se);
		}
	}

	public void deadAlias(AID alias) throws IMTPException {
		try {
			GenericCommand cmd = new GenericCommand(H_DEADALIAS, NAME, null);
			cmd.addParam(alias);
					
			Node n = getNode();
			Object result = n.accept(cmd);
			if((result != null) && (result instanceof Throwable throwable)) {
				if(result instanceof IMTPException exception) {
					throw exception;
				}
				else {
					throw new IMTPException("An undeclared exception was thrown", throwable);
				}
			}
		}
		catch(ServiceException se) {
			// Should never happen
			throw new IMTPException("Unable to access remote node stub", se);
		}
	}
	
	public void currentAliases(Hashtable aliases) throws IMTPException {
		try {
			GenericCommand cmd = new GenericCommand(H_CURRENTALIASES, NAME, null);
			cmd.addParam(aliases);
					
			Node n = getNode();
			Object result = n.accept(cmd);
			if((result != null) && (result instanceof Throwable throwable)) {
				if(result instanceof IMTPException exception) {
					throw exception;
				}
				else {
					throw new IMTPException("An undeclared exception was thrown", throwable);
				}
			}
		}
		catch(ServiceException se) {
			// Should never happen
			throw new IMTPException("Unable to access remote node stub", se);
		}
	}

	public void transferLocalAliases(AID agent, List aliases) throws IMTPException {
		try {
			GenericCommand cmd = new GenericCommand(H_TRANSFERLOCALALIASES, NAME, null);
			cmd.addParam(agent);
			cmd.addParam(aliases);
					
			Node n = getNode();
			Object result = n.accept(cmd);
			if((result != null) && (result instanceof Throwable throwable)) {
				if(result instanceof IMTPException exception) {
					throw exception;
				}
				else {
					throw new IMTPException("An undeclared exception was thrown", throwable);
				}
			}
		}
		catch(ServiceException se) {
			// Should never happen
			throw new IMTPException("Unable to access remote node stub", se);
		}
	}
}
