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

package jade.core.sam;

//#DOTNET_EXCLUDE_FILE

import java.util.Date;
import java.util.List;

import jade.core.Service;
import jade.core.Service.Slice;
import jade.core.Timer;
import jade.core.TimerDispatcher;
import jade.core.TimerListener;
import jade.core.exception.ServiceException;
import jade.util.Logger;

class Poller extends Thread {
	private final SAMService myService;
	private volatile SAMInfoHandler[] handlers;
	private final long period;
	private boolean active;
	
	private Timer watchDogTimer;
	private final Logger myLogger = Logger.getMyLogger(getClass().getName()); 
	
	Poller(SAMService service, long p, SAMInfoHandler[] hh) {
		super();
		myService = service;
		period = p;
		handlers = hh;
		setName("SAMService-Poller");
	}
	
	public synchronized void startPolling() {
		active = true;
		start();
	}
	
	public synchronized void stopPolling() {
		active = false;
		interrupt();
	}
	
	public synchronized void addHandler(SAMInfoHandler h, boolean first) {
		SAMInfoHandler[] newHandlers = new SAMInfoHandler[handlers.length + 1];
		int k = 0;
		if (first) {
			// Add new handler at the beginning
			newHandlers[0] = h;
			k = 1;
		}
		// Copy current handlers
		for (int i = 0; i < handlers.length; ++i) {
			newHandlers[k+i] = handlers[i];
		}
		if (!first) {
			// Add new handler at the end
			newHandlers[handlers.length] = h;
		}
		handlers = newHandlers;
	}
	
	public synchronized void removeHandler(SAMInfoHandler handler) {
		boolean found = false;
		for (SAMInfoHandler h : handlers) {
			if (h == handler) {
				found = true;
				break;
			}
		}

		if (found) {
			SAMInfoHandler[] newHandlers = new SAMInfoHandler[handlers.length - 1];
			// Copy current handlers
			int k = 0;
			for (int i = 0; i < handlers.length; ++i) {
				if (handlers[i] != handler) {
					newHandlers[k++] = handlers[i];	
				}
			}
			handlers = newHandlers;
		}
	}

	public void run() {
		myLogger.log(Logger.INFO, "SAMService poller thread started");
		try {
			while (active) {
				Thread.sleep(period);
				poll();
			}
		}
		catch (InterruptedException ie) {
			if (active) {
				myLogger.log(Logger.WARNING, "SAMService poller thread unexpectedly interrupted");
			}
		}
		for (SAMInfoHandler h : handlers) {
			h.shutdown();
		}
		myLogger.log(Logger.CONFIG, "SAMService poller thread terminated");
	}
	
	/**
	 * This is the method that produces a new record for each monitored entity/counter.
	 * It is invoked periodically by the poller Thread. At each invocation it retrieves the 
	 * relevant information from each SAM Service slice.  
	 */
	void poll() {
		myLogger.log(Logger.FINE, "SAMService poller - Retrieving SAM information from all nodes");
		Date timeStamp = new Date();
		SAMInfo globalInfo = new SAMInfo();
		try {
			List<Slice> slices = myService.getAllSlices();
			for (int i = 0; i < slices.size(); i++) {
				SAMSlice s = (SAMSlice) slices.get(i);
				String nodeName = s.getNode().getName();
				startWatchDog(Thread.currentThread(), nodeName);
				try {
					if (myLogger.isLoggable(Logger.FINER)) {
						myLogger.log(Logger.FINER, "SAMService poller - Retrieving SAM information from node " + nodeName);
					}
					SAMInfo sliceInfo = s.getSAMInfo();
					globalInfo.update(sliceInfo);
					if (myLogger.isLoggable(Logger.FINEST)) {
						myLogger.log(Logger.FINEST, "SAMService poller - SAM information successfully retrieved from node " + nodeName);
					}
				}
				catch (Exception imtpe) {
					// Note that getAllSlices() always retrieves "fresh" slices --> no need for any retry
					myLogger.log(Logger.WARNING, "SAMService poller - Error retrieving SAM information from node "+nodeName, imtpe);
				}
				finally {
					stopWatchDog();
				}
			}
			
			globalInfo.computeAggregatedValues();
			
			for (SAMInfoHandler h : handlers) {
				try {
					h.handle(timeStamp, globalInfo);
				}
				catch (Exception e) {
					myLogger.log(Logger.WARNING, "SAMService poller - Error processing info by handler "+h, e);
				}
			}
		}
		catch (ServiceException se) {
			myLogger.log(Logger.WARNING, "SAMService poller - Error retrieving SAM slices", se);
		}
		catch (Exception e) {
			myLogger.log(Logger.WARNING, "SAMService poller - Unexpected error polling SAM information", e);
		}
	}
	
	private synchronized void startWatchDog(final Thread thread, final String nodeName) {
		watchDogTimer = TimerDispatcher.getTimerDispatcher().add(new Timer(System.currentTimeMillis()+10000, new TimerListener() {
			@Override
			public void doTimeOut(Timer t) {
				synchronized (Poller.this) {
					if (t == watchDogTimer) {
						// The watchDog timer could have been cleared just between expiration and doTimeOut() execution
						myLogger.log(Logger.WARNING, "SAMService - WatchDog timer expired while retrieving SAM information from node "+nodeName);
						thread.interrupt();
						myLogger.log(Logger.WARNING, "SAMService - Poller Thread interrupted!!!");
					}
				}
			}
		}));
	}

	private synchronized void stopWatchDog() {
		if (watchDogTimer != null) {
			TimerDispatcher.getTimerDispatcher().remove(watchDogTimer);
			watchDogTimer = null;
		}
		// Anyway reset the interrupted state of the Poller Thread
		Thread.interrupted();
	}
}
