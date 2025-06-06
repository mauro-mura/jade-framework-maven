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
package jade.core.resource;

import java.io.Serial;

import jade.core.GenericCommand;
import jade.core.Node;
import jade.core.SliceProxy;
import jade.core.exception.IMTPException;
import jade.core.exception.ServiceException;


//#APIDOC_EXCLUDE_FILE

/**
 * Slice Proxy for the ResourceManagementService
 */
public class ResourceManagementProxy extends SliceProxy implements ResourceManagementSlice {

	@Serial
	private static final long serialVersionUID = 4875855388746802968L;

	public byte[] getResource(String name, int fetchMode) throws Exception {
		try {
			GenericCommand cmd = new GenericCommand(H_GETRESOURCE, ResourceManagementService.NAME, null);
			cmd.addParam(name);
			cmd.addParam(fetchMode);
			
			Node n = getNode();
			Object result = n.accept(cmd);
			if((result != null) && (result instanceof Throwable throwable)) {
				if(result instanceof Exception exception) {
					throw exception;
				}
				else {
					throw new IMTPException("An undeclared exception was thrown", throwable);
				}
			}
			return (byte[])result;
		}
		catch(ServiceException se) {
			throw new IMTPException("Unable to access remote node", se);
		}
	}
}
