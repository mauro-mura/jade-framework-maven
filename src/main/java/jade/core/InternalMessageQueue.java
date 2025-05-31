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

package jade.core;

import jade.util.Logger;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

/**
 * @author Giovanni Rimassa - Universita` di Parma
 * @author Moreno LAGO
 * @version $Date: 2006-01-12 13:21:47 +0100 (gio, 12 gen 2006) $ $Revision: 5847 $
 */
class InternalMessageQueue implements MessageQueue {

	private LinkedList<ACLMessage> list;
	private int maxSize;
	private Agent myAgent;
	private Logger myLogger = Logger.getJADELogger(getClass().getName());

	public InternalMessageQueue(int size, Agent a) {
		maxSize = size;
		myAgent = a;
		list = new LinkedList<>();
	}

	public InternalMessageQueue() {
		this(0, null);
	}

	public boolean isEmpty() {
		return list.isEmpty();
	}

	public void setMaxSize(int newSize) throws IllegalArgumentException {
		if (newSize < 0)
			throw new IllegalArgumentException("Invalid MsgQueue size");
		maxSize = newSize;
	}

	public int getMaxSize() {
		return maxSize;
	}

	/**
	 * @return the number of messages currently in the queue
	 **/
	public int size() {
		return list.size();
	}

	public void addFirst(ACLMessage msg) {
		if ((maxSize != 0) && (list.size() >= maxSize)) {
			// FIFO replacement policy
			list.removeFirst(); 
		}
		list.addFirst(msg);
	}

	public void addLast(ACLMessage msg) {
		if ((maxSize != 0) && (list.size() >= maxSize)) {
			// FIFO replacement policy
			list.removeFirst(); 
			myLogger.log(Logger.SEVERE,
					"Agent " + getAgentName() + " - Message queue size exceeded. Message discarded!!!!!");
		}
		list.addLast(msg);
	}

	private String getAgentName() {
		return myAgent != null ? myAgent.getLocalName() : "null";
	}

	@SuppressWarnings("unused")
	public ACLMessage receive(MessageTemplate pattern) {
		ACLMessage result = null;
		// This is just for the MIDP implementation where iterator.remove() is not
		// supported.
		// We don't surround it with preprocessor directives to avoid making the code
		// unreadable
		int cnt = 0;
		for (Iterator<ACLMessage> messages = iterator(); messages.hasNext(); cnt++) {
			ACLMessage msg = messages.next();
			if (pattern == null || pattern.match(msg)) {
				messages.remove();
				result = msg;
				break;
			}
		}
		return result;
	}

	
	@Override
	public List<ACLMessage> receive(MessageTemplate pattern, int max) {
		List<ACLMessage> mm = null;
		int cnt = 0;
		for (Iterator<ACLMessage> messages = list.iterator(); messages.hasNext();) {
			ACLMessage msg = messages.next();
			if (pattern == null || pattern.match(msg)) {
				messages.remove();
				if (mm == null) {
					mm = new ArrayList<>(max);
				}
				mm.add(msg);
				cnt++;
				if (cnt == max) {
					break;
				}
			}
		}
		return mm;
	}
	

	private Iterator<ACLMessage> iterator() {
		return list.iterator();
	}

	// For persistence service
	private Long persistentID;

	// For persistence service
	@SuppressWarnings("unused")
	private Long getPersistentID() {
		return persistentID;
	}

	// For persistence service
	@SuppressWarnings("unused")
	private void setPersistentID(Long l) {
		persistentID = l;
	}

	public void copyTo(List<ACLMessage> messages) {
		for (Iterator<ACLMessage> i = iterator(); i.hasNext(); messages.add(i.next()));
	}

	public String dump(int limit) {
		StringBuilder sb = new StringBuilder();
		Object[] messages = list.toArray();
		if (messages.length > 0) {
			int max = limit > 0 ? limit : messages.length;
			for (int j = 0; j < max; ++j) {
				sb.append("Message # ");
				sb.append(j);
				sb.append('\n');
				sb.append(messages[j]);
				sb.append('\n');
			}
		} else {
			sb.append("Queue is empty\n");
		}
		return sb.toString();
	}

	@SuppressWarnings("unused")
	void cleanOldMessages(long maxTime, MessageTemplate pattern) {
		long now = System.currentTimeMillis();
		int cnt = 0;
		for (Iterator<ACLMessage> messages = iterator(); messages.hasNext(); cnt++) {
			ACLMessage msg = messages.next();
			long postTime = msg.getPostTimeStamp();
			if (postTime > 0 && ((now - postTime) > maxTime)) {
				if (pattern == null || pattern.match(msg)) {
					messages.remove();
				}
			}
		}
	}

}
