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

import java.util.List;

import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

/**
 * The interface to be implemented by agent message queue implementations
 * 
 * @see Agent#createMessageQueue()
 * @author Arend Freije
 * @author Moreno LAGO
 */
public interface MessageQueue {
	/**
	 * Add a message to the front of this queue.
	 */
	void addFirst(ACLMessage msg);

	/**
	 * Add a message to the end of this queue.
	 */
	void addLast(ACLMessage msg);

	/**
	 * Return the maximum size of this queue. This queue may remove old messages to
	 * prevent exeding the maximum size.
	 */
	int getMaxSize();

	/**
	 * Set the maximum size of this queue. This queue may remove old messages to
	 * prevent exeding the maximum size.
	 */
	void setMaxSize(int newSize);

	/**
	 * Return true when this queue contains no messages.
	 */
	boolean isEmpty();

	/**
	 * Return and remove the first message that matches the specified message
	 * template.
	 */
	ACLMessage receive(MessageTemplate pattern);

	/**
	 * Return and remove the first n messages that match the specified message
	 * template.
	 */
	List<ACLMessage> receive(MessageTemplate pattern, int max);

	/**
	 * Copy all messages to a given list.
	 */
	void copyTo(List<ACLMessage> list);

	/**
	 * @return the number of messages currently in the queue
	 */
	int size();
}