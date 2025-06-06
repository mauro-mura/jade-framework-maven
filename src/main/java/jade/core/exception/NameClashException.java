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

import java.io.Serial;

//#APIDOC_EXCLUDE_FILE

/**
 * This exception is thrown when trying to create an agent with an already
 * existing name.
 * 
 * @author Giovanni Rimassa - Universita` di Parma
 * @author Moreno LAGO
 * @version $Date: 2005-01-10 17:14:29 +0100 (lun, 10 gen 2005) $ $Revision: 5513 $
 */
public class NameClashException extends Exception {

	@Serial
	private static final long serialVersionUID = 6048730562334762984L;
	/**
	 * This constant string is used to distinguish a name clash from other reasons
	 * that may prevent the creation of an agent.
	 */
	public static final String KEYWORD = "Name-clash";

	/**
	 * Construct a <code>NameClashException</code> with no detail message
	 */
	public NameClashException() {
		super(KEYWORD);
	}

	/**
	 * Construct a <code>NameClashException</code> with the given message.
	 * 
	 * @param msg The exception message.
	 */
	public NameClashException(String msg) {
		super(KEYWORD + " " + msg);
	}
}
