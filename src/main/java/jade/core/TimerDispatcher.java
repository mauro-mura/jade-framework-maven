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


import java.util.Comparator;
import java.util.TreeSet;

//#APIDOC_EXCLUDE_FILE

import jade.util.Logger;

/**
 * This class implements the JADE internal timing system. It should not be used
 * by application developers.
 * 
 * @author Giovanni Rimassa - Universita' di Parma
 * @author Moreno LAGO
 * @version $Date: 2014-06-03 15:12:45 +0200 (mar, 03 giu 2014) $ $Revision:
 *          6713 $
 */

public class TimerDispatcher implements Runnable {
	// The singleton TimerDispatcher
	private static TimerDispatcher theDispatcher;

	private Thread myThread;

	// In J2ME we use a Vector to keep timers to minimize the number of classes.
	// In J2SE, where we can have thousands of timers, using a Vector can be highly
	// inefficient -->
	// We use a TreeSet wrapped into a class that mimic the methods of a Vector.
	
	private final TimersList timers = new TimersList();
	
	/*
	 * #J2ME_INCLUDE_BEGIN private Vector timers = new Vector<>(); #J2ME_INCLUDE_END
	 */
	private boolean active;

	protected Logger myLogger = Logger.getJADELogger(getClass().getName());

	void setThread(Thread t) {
		if (myThread == null) {
			myThread = t;
		}
	}

	public synchronized Timer add(Timer t) {
		if (myThread == null) {
			myThread = new Thread(this);
			start();
		}
		while (!addTimer(t)) {
			t.setExpirationTime(t.expirationTime() + 1);
		}
		// If this is the first timer, wake up the dispatcher thread
		if (timers.firstElement() == t) {
			wakeUp();
		}
		return t;
	}

	public synchronized void remove(Timer t) {
		timers.removeElement(t);
	}

	public void run() {
		try {
			while (active) {
				Timer t = null;
				synchronized (this) {
					while (active) {
						long timeToWait = 0;
						if (!timers.isEmpty()) {
							t = (Timer) timers.firstElement();
							if (t.isExpired()) {
								timers.removeElement(t);
								break;
							} else {
								timeToWait = t.expirationTime() - System.currentTimeMillis();
								if (timeToWait <= 0) {
									// Avoid wait(0), that means 'for ever'
									timeToWait = 1;
								}
							}
						}
						sleep(timeToWait);
					}
				}
				// This check just avoids NullPointerException on termination
				if (active) {
					t.fire();
				}
			}
		} catch (InterruptedException ie) {
			// Do nothing, but just return, since this is a shutdown.
		}
		timers.removeAllElements();
	}

	protected void sleep(long sleepTime) throws InterruptedException {
		// myLogger.log(Logger.INFO, "TD going to sleep for "+timeToWait+" ms
		// ............");
		wait(sleepTime);
		// myLogger.log(Logger.INFO, "TD WakeUp!!!!!!");
	}

	protected void wakeUp() {
		notifyAll();
	}

	void start() {
		synchronized (myThread) {
			active = true;
			myThread.start();
		}
	}

	void stop() {
		if (myThread != null) {
			synchronized (myThread) {
				if (Thread.currentThread().equals(myThread)) {
					System.out.println("Deadlock avoidance: TimerDispatcher thread calling stop on itself!");
				} else {
					active = false;
					synchronized (this) {
						wakeUp();
					}
					try {
						myThread.join();
					} catch (InterruptedException ignore) {
						// Do nothing
					}
				}
				myThread = null;
			}
		}
	}

	public static TimerDispatcher getTimerDispatcher() {
		if (theDispatcher == null) {
			theDispatcher = new TimerDispatcher();
		}
		return theDispatcher;
	}

	public static void setTimerDispatcher(TimerDispatcher td) {
		theDispatcher = td;
	}

	private boolean addTimer(Timer t) {
		
		return timers.add(t);
		
		/*
		 * #J2ME_INCLUDE_BEGIN if (!timers.contains(t)) { int size = timers.size(); for
		 * (int i = 0; i < size; i++) { Timer t1 = (Timer) timers.elementAt(i); if
		 * (t.expirationTime() < t1.expirationTime()) { timers.insertElementAt(t, i);
		 * return true; } } timers.addElement(t); return true; } return false;
		 * #J2ME_INCLUDE_END
		 */
	}

	
	private class TimersList {
		private TreeSet<Object> set = new TreeSet(new TimerComparator());

		private final Object firstElement() {
			return set.first();
		}

		private final void removeElement(Object obj) {
			set.remove(obj);
		}

		private final void removeAllElements() {
			set.clear();
		}

		private final boolean isEmpty() {
			return set.isEmpty();
		}

		private boolean add(Object obj) {
			return set.add(obj);
		}
	}

	private class TimerComparator implements Comparator {
		public int compare(Object o1, Object o2) {
			Timer t1 = (Timer) o1;
			Timer t2 = (Timer) o2;
			if (t1.expirationTime() < t2.expirationTime()) {
				return -1;
			} else if (t1.expirationTime() == t2.expirationTime()) {
				return 0;
			} else {
				return 1;
			}
		}
	}
	
}
