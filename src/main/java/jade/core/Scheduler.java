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

import java.io.Serializable;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import jade.core.behaviours.Behaviour;

/**
 @author Giovanni Rimassa - Universita' di Parma
 @author Moreno LAGO
 @version $Date: 2017-12-11 19:49:01 +0100 (lun, 11 dic 2017) $ $Revision: 6834 $
 */

/**************************************************************
 
 Name: Scheduler
 
 Responsibility and Collaborations:
 
 + Selects the behaviour to execute.
 (Behaviour)
 
 + Holds together all the behaviours of an agent.
 (Agent, Behaviour)
 
 + Manages the resources needed to synchronize and execute agent
 behaviours, such as thread pools, locks, etc.
 
 ****************************************************************/
public class Scheduler implements Serializable {

	private static final long serialVersionUID = 203639140849912829L;
	
	protected List<Behaviour> readyBehaviours = new LinkedList<>();
	protected List<Behaviour> blockedBehaviours = new LinkedList<>();
	
	/**
	 @serial
	 */
	private Agent owner;
	
	/**
	 @serial
	 */
	private int currentIndex;
	
	public Scheduler(Agent a) {
		owner = a;
		currentIndex = 0;
	}
	
	// Add a behaviour at the end of the behaviours queue. 
	// This can never change the index of the current behaviour.
	// If the behaviours queue was empty notifies the embedded thread of
	// the owner agent that a behaviour is now available.
	public synchronized void add(Behaviour b) {
		readyBehaviours.add(b);
		notify();
		owner.notifyAddBehaviour(b);
	}
	
	// Moves a behaviour from the ready queue to the sleeping queue.
	public synchronized void block(Behaviour b) {
		if (removeFromReady(b)) {
			blockedBehaviours.add(b);
			owner.notifyChangeBehaviourState(b, Behaviour.STATE_READY, Behaviour.STATE_BLOCKED);
			
		}
	}
	
	// Moves a behaviour from the sleeping queue to the ready queue.
	public synchronized void restart(Behaviour b) {
		if (removeFromBlocked(b)) {
			readyBehaviours.add(b);
			notify();
			owner.notifyChangeBehaviourState(b, Behaviour.STATE_BLOCKED, Behaviour.STATE_READY);
			
		}
	}
	
	/**
	 Restarts all behaviours. This method simply calls
	 Behaviour.restart() on every behaviour. The
	 Behaviour.restart() method then notifies the agent (with the
	 Agent.notifyRestarted() method), causing Scheduler.restart() to
	 be called (this also moves behaviours from the blocked queue to 
	 the ready queue --> we must copy all behaviours into a temporary
	 buffer to avoid concurrent modification exceptions).
	 Why not restarting only blocked behaviours?
	 Some ready behaviour can be a ParallelBehaviour with some of its
	 children blocked. These children must be restarted too.
	 */
	public synchronized void restartAll() {
		
		Behaviour[] behaviours = new Behaviour[readyBehaviours.size()];
		int counter = 0;
		
		for(Iterator<Behaviour> it = readyBehaviours.iterator(); it.hasNext();)
			behaviours[counter++] = it.next();
		
		for(int i = 0; i < behaviours.length; i++) {
			Behaviour b = behaviours[i];
			b.restart();
		}
		
		behaviours = new Behaviour[blockedBehaviours.size()];
		counter = 0;
		
		for(Iterator<Behaviour> it = blockedBehaviours.iterator(); it.hasNext();) {
			
			//#DOTNET_EXCLUDE_BEGIN
			behaviours[counter++] = it.next();
			//#DOTNET_EXCLUDE_END
			/*#DOTNET_INCLUDE_BEGIN
			 Object tmpB = null;
			 try // Hack: sometimes .NET inserts into this array a non-Behaviour object
			 { 
			 tmpB = it.next();
			 behaviours[counter++] = (Behaviour)tmpB;
			 }
			 catch(ClassCastException cce) 
			 {
			 System.out.println("Found an object of type "+tmpB.getClass().getName()+" instead of Behaviour");
			 cce.printStackTrace();
			 }
			 #DOTNET_INCLUDE_END*/
		}
		
		for(int i = 0; i < behaviours.length; i++) {
			Behaviour b = behaviours[i];
			b.restart();
			
		}
	}
	
	/**
	 Removes a specified behaviour from the scheduler
	 */
	public synchronized void remove(Behaviour b) {
		boolean found = removeFromBlocked(b);
		if(!found) {
			found = removeFromReady(b);
		}
		if (found) {
			
			owner.notifyRemoveBehaviour(b);    
			
		}
	}
	
	/**
	 Selects the appropriate behaviour for execution, with a trivial
	 round-robin algorithm.
	 */
	public synchronized Behaviour schedule() throws InterruptedException {
		while(readyBehaviours.isEmpty()) {
			owner.idle();
		}
		Behaviour b = (Behaviour)readyBehaviours.get(currentIndex);
		currentIndex = (currentIndex + 1) % readyBehaviours.size();
		return b;
	}
	
	public synchronized int size() {
		return blockedBehaviours.size() + readyBehaviours.size();
	}
	
	
	
	// Helper method for persistence service
	public synchronized Behaviour[] getBehaviours() {
		
		Behaviour[] result = new Behaviour[blockedBehaviours.size() + readyBehaviours.size()];
		Iterator<Behaviour> itReady = readyBehaviours.iterator();
		Iterator<Behaviour> itBlocked = blockedBehaviours.iterator();
		for(int i = 0; i < result.length; i++) {
			Behaviour b = null;
			if(itReady.hasNext()) {
				b = itReady.next();
			}
			else {
				b = itBlocked.next();
			}
			
			result[i] = b;
			
		}
		
		return result;
	}
	
	// Helper method for persistence service
	public void setBehaviours(Behaviour[] behaviours) {
		
		readyBehaviours.clear();
		blockedBehaviours.clear();
		
		for(int i = 0; i < behaviours.length; i++) {
			Behaviour b = behaviours[i];
			if(b.isRunnable()) {
				readyBehaviours.add(b);
			}
			else {
				blockedBehaviours.add(b);
			}
		}
		
		// The current index is not saved when persisting an agent
		currentIndex = 0;
	}
	
	
	
	
	// Removes a specified behaviour from the blocked queue.
	private boolean removeFromBlocked(Behaviour b) {
		return blockedBehaviours.remove(b);

	}
	
	// Removes a specified behaviour from the ready queue.
	// This can change the index of the current behaviour, so a check is
	// made: if the just removed behaviour has an index lesser than the
	// current one, then the current index must be decremented.
	private boolean removeFromReady(Behaviour b) {
		int index = readyBehaviours.indexOf(b);
		if(index != -1) {
			readyBehaviours.remove(b);
			if(index < currentIndex)
				--currentIndex;
			//if(currentIndex < 0)
			//  currentIndex = 0;
			else if (index == currentIndex && currentIndex == readyBehaviours.size())
				currentIndex = 0;
		}
		return index != -1;
	}
	
}
