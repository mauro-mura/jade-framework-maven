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

package jade.core.replication;

import java.io.Serial;

import jade.core.GenericCommand;
import jade.core.Node;
import jade.core.SliceProxy;
import jade.core.exception.IMTPException;
import jade.core.exception.ServiceException;


/**
   The remote proxy for the JADE kernel-level service distributing the
   <i>Service Manager</i> address list throughout the platform.

   @author Giovanni Rimassa - FRAMeTech s.r.l.
*/
public class AddressNotificationProxy extends SliceProxy implements AddressNotificationSlice {

	@Serial
	private static final long serialVersionUID = -4121018855219231977L;

	public void addServiceManagerAddress(String addr) throws IMTPException {
	try {
	    GenericCommand cmd = new GenericCommand(H_ADDSERVICEMANAGERADDRESS, NAME, null);
	    cmd.addParam(addr);

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
	    throw new IMTPException("Unable to access remote node", se);
	}
    }

    /*public void removeServiceManagerAddress(String addr) throws IMTPException {
	try {
	    GenericCommand cmd = new GenericCommand(H_REMOVESERVICEMANAGERADDRESS, NAME, null);
	    cmd.addParam(addr);

	    Node n = getNode();
	    Object result = n.accept(cmd);
	    if((result != null) && (result instanceof Throwable)) {
		if(result instanceof IMTPException) {
		    throw (IMTPException)result;
		}
		else {
		    throw new IMTPException("An undeclared exception was thrown", (Throwable)result);
		}
	    }

	}
	catch(ServiceException se) {
	    throw new IMTPException("Unable to access remote node", se);
	}
    }*/

    public String getServiceManagerAddress() throws IMTPException {
	try {
	    GenericCommand cmd = new GenericCommand(H_GETSERVICEMANAGERADDRESS, NAME, null);

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

	    return (String)result;

	}
	catch(ServiceException se) {
	    throw new IMTPException("Unable to access remote node", se);
	}
    }

}
