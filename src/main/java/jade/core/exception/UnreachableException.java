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

package jade.core.exception;

//#APIDOC_EXCLUDE_FILE

import java.io.Serial;


//#APIDOC_EXCLUDE_FILE

import jade.util.WrapperException;

/**
 * This exception is thrown when some agent container cannot be contacted.
 * 
 * @author Giovanni Rimassa - Universita` di Parma
 * @author Moreno LAGO
 * @version $Date: 2003-11-18 14:23:42 +0100 (mar, 18 nov 2003) $ $Revision: 4548 $
 */
public class UnreachableException extends WrapperException {

	@Serial
	private static final long serialVersionUID = 85196280208096964L;

	/**
	 * Construct an <code>UnreachableException</code> with the given message.
	 * 
	 * @param msg The exception message.
	 */
	public UnreachableException(String msg) {
		super(msg);
	}

	/**
	 * Construct an <code>UnreachableException</code> with the given message and
	 * exception cause.
	 * 
	 * @param msg The exception message.
	 * @param t   The <code>Throwable</code> tht caused this exception.
	 */
	public UnreachableException(String msg, Throwable t) {
		super(msg, t);
	}

}
