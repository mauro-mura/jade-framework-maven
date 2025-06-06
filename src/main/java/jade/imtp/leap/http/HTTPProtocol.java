/*--- formatted by Jindent 2.1, (www.c-lab.de/~jindent) ---*/

/**
 * ***************************************************************
 * The LEAP libraries, when combined with certain JADE platform components,
 * provide a run-time environment for enabling FIPA agents to execute on
 * lightweight devices running Java. LEAP and JADE teams have jointly
 * designed the API for ease of integration and hence to take advantage
 * of these dual developments and extensions so that users only see
 * one development platform and a
 * single homogeneous set of APIs. Enabling deployment to a wide range of
 * devices whilst still having access to the full development
 * environment and functionalities that JADE provides.
 * Copyright (C) 2001 Telecom Italia LAB S.p.A.
 * Copyright (C) 2001 Motorola.
 * 
 * GNU Lesser General Public License
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation,
 * version 2.1 of the License.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330,
 * Boston, MA  02111-1307, USA.
 * **************************************************************
 */
package jade.imtp.leap.http;

import java.util.List;

import jade.imtp.leap.TransportProtocol;
import jade.imtp.leap.exception.ICPException;
import jade.mtp.TransportAddress;

/**
 * Class declaration
 * 
 * @author Giovanni Caire - TILAB
 * @author Ronnie Taib - Motorola
 * @author Steffen Rusitschka - Siemens
 * @author Moreno LAGO
 */
public class HTTPProtocol extends TransportProtocol {

	/**
	 * The protocol's name, as used in a URL protocol field.
	 */
	public static final String NAME = "http";
	public static final int DEFAULT_PORT = 80;

	private static final HTTPProtocol theInstance = new HTTPProtocol();

	public static HTTPProtocol getInstance() {
		return theInstance;
	}

	/**
	 */
	public String addrToString(TransportAddress ta) throws ICPException {

		// Check that the specified ta is actually a JICP address
		HTTPAddress hta = null;

		try {
			hta = (HTTPAddress) ta;
		} catch (ClassCastException cce) {
			throw new ICPException("The TransportAddress " + ta.toString() + " is not an HTTP Address");
		}

		return hta.toString();
	}

	/**
	 */
	public TransportAddress stringToAddr(String s) throws ICPException {
		List<String> addressFields = parseURL(s);
		String protocol = addressFields.get(0);

		if (!NAME.equals(protocol)) {
			throw new ICPException("Unexpected protocol \"" + protocol + "\" when \"" + NAME + "\" was expected.");
		}

		String host = addressFields.get(1);
		String port = addressFields.get(2);
		String file = addressFields.get(3);
		String anchor = addressFields.get(4);

		return new HTTPAddress(host, port, file, anchor);
	}

	/**
	 */
	public TransportAddress buildAddress(String host, String port, String file, String anchor) {
		return new HTTPAddress(host, port, file, anchor);
	}

	/**
	 * Method declaration
	 * 
	 * @return
	 * @see
	 */
	public String getName() {
		return NAME;
	}

}
