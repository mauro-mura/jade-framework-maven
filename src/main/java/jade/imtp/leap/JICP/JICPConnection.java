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

package jade.imtp.leap.JICP;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.BindException;
import java.net.InetSocketAddress;
import java.net.Socket;

import jade.mtp.TransportAddress;
import jade.util.Logger;

/**
 * Class declaration
 * 
 * @author Steffen Rusitschka - Siemens
 */
public class JICPConnection extends Connection {

	protected Socket sc;
	protected static Logger myLogger = Logger.getMyLogger(JICPConnection.class.getName());

	protected InputStream is;
	protected OutputStream os;

	protected JICPConnection() {
	}

	public JICPConnection(TransportAddress ta) throws IOException {
		this(ta, 0);
	}

	/**
	 * Constructor declaration
	 */
	public JICPConnection(TransportAddress ta, int timeout) throws IOException {
		this(ta, timeout, null, 0);
	}

	public JICPConnection(TransportAddress ta, int timeout, String bindHost, int bindPort) throws IOException {

		// For some reason the local address or port may be in use
		int bindExceptionCnt = 0;
		while (true) {
			try {
				sc = new Socket();
				if (bindHost != null || bindPort > 0) {
					// Local binding explicitly specified
					myLogger.log(Logger.INFO,
							"Binding JICPConnection with bindHost=" + bindHost + " and bindPort=" + bindPort);
					if (bindHost != null) {
						sc.bind(new InetSocketAddress(bindHost, bindPort));
					} else {
						sc.bind(new InetSocketAddress(bindPort));
					}
				} else {
					bindSocket(sc);
				}
				sc.setTcpNoDelay(true);
				sc.connect(new InetSocketAddress(ta.getHost(), Integer.parseInt(ta.getPort())), timeout);
				socketCnt++;
				is = sc.getInputStream();
				os = getOutputStream();
				break;
			} catch (BindException be) {
				bindExceptionCnt++;
				if (bindExceptionCnt >= 10) {
					myLogger.log(Logger.SEVERE,
							"Error binding JICPSConnection with bindHost=" + bindHost + " and bindPort=" + bindPort);
					throw be;
				}

				// Do nothing and try again
			}
		}
	}

	public void setReadTimeout(int timeout) throws IOException {
		if (sc != null) {
			sc.setSoTimeout(timeout);
		}
	}

	protected void bindSocket(Socket sc) {
		// Just do nothing.
	}

	/**
	 * Constructor declaration
	 */
	public JICPConnection(Socket s) {
		sc = s;
	}

	public JICPPacket readPacket() throws IOException {
		if (sc != null) {
			if (is == null) {
				is = sc.getInputStream();
			}
			return JICPPacket.readFrom(is);
		} else {
			throw new IOException("Connection closed");
		}
	}

	public int writePacket(JICPPacket pkt) throws IOException {
		if (sc != null) {
			if (os == null) {
				os = getOutputStream();
			}
			int ret = pkt.writeTo(os);
			os.flush();
			return ret;
		} else {
			throw new IOException("Connection closed");
		}
	}

	protected OutputStream getOutputStream() throws IOException {
		return new ByteArrayOutputStream() {
			private OutputStream realOs = null;

			@Override
			public void flush() throws IOException {
				if (realOs == null) {
					realOs = sc.getOutputStream();
				}
				realOs.write(buf, 0, count);
				realOs.flush();
				reset();
			}

			@Override
			public void close() throws IOException {
				super.close();
				if (realOs != null) {
					realOs.close();
					realOs = null;
				}
			}
		};
	}

	public void close() throws IOException {
		IOException firstExc = null;
		if (is != null) {
			try {
				is.close();
			} catch (IOException e) {
				firstExc = e;
			}
			is = null;
		}
		if (os != null) {
			try {
				os.close();
			} catch (IOException e) {
				firstExc = (firstExc != null ? firstExc : e);
			}
			os = null;
		}
		if (sc != null) {
			try {
				sc.close();
				socketCnt--;
			} catch (IOException e) {
				firstExc = (firstExc != null ? firstExc : e);
			}
			sc = null;
		}
		if (firstExc != null) {
			throw firstExc;
		}
	}

	public String getRemoteHost() throws Exception {
		return sc.getInetAddress().getHostAddress();
	}

	public String getLocalHost() {
		return sc.getLocalAddress().getHostAddress();
	}

	public int getLocalPort() {
		return sc.getLocalPort();
	}

}
