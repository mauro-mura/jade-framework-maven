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

import java.io.Serial;

import jade.mtp.TransportAddress;

/**
 * Class declaration
 * 
 * @author Giovanni Caire - TILAB
 * @author Moreno LAGO
 */
public class HTTPAddress implements TransportAddress {

	@Serial
	private static final long serialVersionUID = -3763807130507228464L;
	private final String host;
	private final String port;
	private final String file;
	private final String anchor;

	/**
	 * Constructor declaration
	 * 
	 * @param host
	 * @param port
	 * @param file
	 * @param anchor
	 */
	public HTTPAddress(String host, String port, String file, String anchor) {
		this.host = host;
		this.port = port != null ? port : String.valueOf(HTTPProtocol.DEFAULT_PORT);
		this.file = file;
		this.anchor = anchor;
	}

	/**
	 * Method declaration
	 * 
	 * @return
	 * @see
	 */
	public String getProto() {
		return HTTPProtocol.NAME;
	}

	/**
	 * Method declaration
	 * 
	 * @return
	 * @see
	 */
	public String getHost() {
		return host;
	}

	/**
	 * Method declaration
	 * 
	 * @return
	 * @see
	 */
	public String getPort() {
		return port;
	}

	/**
	 * Method declaration
	 * 
	 * @return
	 * @see
	 */
	public String getFile() {
		return file;
	}

	/**
	 * Method declaration
	 * 
	 * @return
	 * @see
	 */
	public String getAnchor() {
		return anchor;
	}

	public String toString() {
		StringBuffer address = new StringBuffer();

		address.append(getProto());
		address.append("://");
		address.append(host);

		if (port != null) {
			address.append(":");
			address.append(port);
		}

		if (file != null) {
			address.append("/");
			address.append(file);
		}

		if (anchor != null) {
			address.append("#");
			address.append(anchor);
		}

		return address.toString();
	}
}
