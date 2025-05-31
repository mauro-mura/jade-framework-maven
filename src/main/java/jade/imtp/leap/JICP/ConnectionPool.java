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

import jade.mtp.TransportAddress;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import jade.imtp.leap.*;
import jade.imtp.leap.exception.ICPException;
import jade.imtp.leap.http.HTTPProtocol;

/**
 * @author Giovanni Caire - TILAB
 */
class ConnectionPool {
	private final Map<String, List<ConnectionWrapper>> connections = new HashMap<>();
	private final TransportProtocol myProtocol;
	private final ConnectionFactory myFactory;
	private int maxSize;
	private int size;
	private boolean closed;

	private long hitCnt;
	private long missCnt;

	ConnectionPool(TransportProtocol p, ConnectionFactory f, int ms) {
		myProtocol = p;
		myFactory = f;
		// Temporary hack for HTTP since HTTP connections cannot be re-used
		if (myProtocol instanceof HTTPProtocol) {
			maxSize = 0;
		} else {
			maxSize = ms;
		}
		size = 0;
	}

	// The actual connection creation operation must NOT be included in the
	// synchronized block. In facts
	// in certain cases it may take a lot of time due to TCP timeouts expiration.
	ConnectionWrapper acquire(TransportAddress ta, boolean requireFreshConnection) throws ICPException {
		ConnectionWrapper cw = null;
		List<ConnectionWrapper> l = null;
		String url = myProtocol.addrToString(ta);
		synchronized (this) {
			if (closed) {
				throw new ICPException("Pool closed");
			}

			l = connections.get(url);
			if (l == null) {
				l = new ArrayList<>();
				connections.put(url, l);
			}

			if (requireFreshConnection) {
				// We are checking a given destination. This means that this destination may be
				// no longer valid
				// --> In order to avoid keeping invalid connections that can lead to very long
				// waiting times,
				// close all non-used connections towards this destination.
				closeConnections(l);
			} else {
				Iterator<ConnectionWrapper> it = l.iterator();
				while (it.hasNext()) {
					cw = it.next();
					if (cw.lock()) {
						cw.setReused();
						hitCnt++;
						return cw;
					}
				}
			}
		}

		// If we get here no connection is available --> create a new one
		try {
			Connection c = myFactory.createConnection(ta);
			synchronized (this) {
				cw = new ConnectionWrapper(c, ta);
				if (size < maxSize) {
					// Reusable connection --> Store it
					l.add(cw);
					size++;
				} else {
					// OneShot connection --> don't even store it.
					cw.setOneShot();
				}
				missCnt++;
				return cw;
			}
		} catch (IOException ioe) {
			throw new ICPException("Error creating connection. ", ioe);
		} finally {
			// We may have created a new list of connections that end up to be useless (e.g.
			// because the connection is one-shot,
			// or because there was an error creating the connection) --> remove it
			synchronized (this) {
				if (l.isEmpty()) {
					connections.remove(url);
				}
			}
		}
	}

	private void closeConnections(List<ConnectionWrapper> l) {
		List<ConnectionWrapper> closedConnections = new ArrayList<>();
		Iterator<ConnectionWrapper> it = l.iterator();
		while (it.hasNext()) {
			ConnectionWrapper cw = it.next();
			if (cw.lock()) {
				cw.close();
				cw.unlock();
				closedConnections.add(cw);
			}
		}
		// Now remove all closed connections
		it = closedConnections.iterator();
		while (it.hasNext()) {
			if (l.remove(it.next())) {
				size--;
			}
		}
	}

	synchronized void release(ConnectionWrapper cw) {
		cw.unlock();
	}

	synchronized void remove(ConnectionWrapper cw) {
		try {
			String url = myProtocol.addrToString(cw.getDestAddress());
			List<ConnectionWrapper> l = connections.get(url);
			if (l != null && l.remove(cw)) {
				size--;
				if (l.isEmpty()) {
					connections.remove(url);
				}
			}
			cw.getConnection().close();
		} catch (Exception e) {
			// Just ignore it
		}
	}

	synchronized void shutdown() {
		Iterator<List<ConnectionWrapper>> it = connections.values().iterator();

		while (it.hasNext()) {
			List<ConnectionWrapper> l = it.next();
			for (int i = 0; i < l.size(); i++) {
				ConnectionWrapper cw = l.get(i);
				cw.close();
			}
			l.clear();
		}
		connections.clear();
		closed = true;
	}

	void clearExpiredConnections(long currentTime) {
		Iterator<ConnectionWrapper> it = getConnectionsList().iterator();
		while (it.hasNext()) {
			ConnectionWrapper cw = it.next();
			if (cw.isExpired(currentTime)) {
				remove(cw);
				cw.unlock();
			}
		}
	}

	public String toString() {
		return "[Connection-pool: total-hit=" + hitCnt + ", total-miss=" + missCnt + ", current-size=" + size
				+ " connections=" + connections + "]";
	}

	private synchronized List<ConnectionWrapper> getConnectionsList() {
		List<ConnectionWrapper> cc = new ArrayList<>();
		Iterator<List<ConnectionWrapper>> it = connections.values().iterator();
		while (it.hasNext()) {
			List<ConnectionWrapper> l = it.next();
			Iterator<ConnectionWrapper> it1 = l.iterator();
			while (it1.hasNext()) {
				cc.add(it1.next());
			}
		}
		return cc;
	}
}
