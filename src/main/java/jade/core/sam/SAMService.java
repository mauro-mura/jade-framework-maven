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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import jade.core.AID;
import jade.core.Agent;
import jade.core.AgentContainer;
import jade.core.BaseService;
import jade.core.Filter;
import jade.core.HorizontalCommand;
import jade.core.Node;
import jade.core.Profile;
import jade.core.Service;
import jade.core.ServiceHelper;
import jade.core.Specifier;
import jade.core.VerticalCommand;
import jade.core.exception.IMTPException;
import jade.core.exception.ProfileException;
import jade.core.exception.ServiceException;
import jade.core.management.AgentManagementSlice;
import jade.core.replication.MainReplicationSlice;
import jade.util.Logger;

/**
 * JADE Kernel service supporting System Activity Monitoring (SAM).
 */
public class SAMService extends BaseService {
	
	public static final String POLLING_PERIOD = "jade_core_sam_SAMService_pollingperiod";
	public static final int POLLING_PERIOD_DEFAULT = 1; // 1 minute

	public static final String SAM_INFO_HANDLERS = "jade_core_sam_SAMService_handlers";
	public static final String SAM_INFO_HANDLERS_DEFAULT = "jade.core.sam.DefaultSAMInfoHandlerImpl";

	public static final String AGENTS_TO_MONITOR = "jade_core_sam_SAMService_agentstomonitor";

	private final List<EntityInfo> monitoredEntities = new ArrayList<>();
	private final List<CounterInfo> monitoredCounters = new ArrayList<>();

	private Poller poller;

	private final SAMHelper myHelper = new SAMHelperImpl();
	private static final Object singletonLock = new Object();
	private static SAMHelper singletonHelper;
	private final ServiceComponent localSlice = new ServiceComponent();
	private Filter outgoingFilter;

	private Profile myProfile;
	private AgentContainer myContainer;

	private Timer samTimer;
	private final List<MediatedMeasureProvider> providers = new ArrayList<>();
	private MediatedMeasureProvider[] providersArray;

	@SuppressWarnings("unused")
	private String[] agentsToMonitor;

	public String getName() {
		return SAMHelper.SERVICE_NAME;
	}

	@Override
	public void init(AgentContainer ac, Profile p) throws ProfileException {
		super.init(ac, p);
		myContainer = ac;
		if (p.isMain()) {
			outgoingFilter = new Filter() {
				@Override
				public boolean accept(VerticalCommand cmd) {
					String name = cmd.getName();
					try {
						if (AgentManagementSlice.SHUTDOWN_PLATFORM.equals(name)) {
							// If the platform is shutting down stop polling: some
							// peripheral container may be already down causing annoying exceptions
							if (poller != null) {
								poller.stopPolling();
							}
						} else if (MainReplicationSlice.LEADERSHIP_ACQUIRED.equals(name)) {
							// If this is a backup Main Container that has just taken the leadership
							// start polling again
							startPolling();
						}
					} catch (Exception e) {
						myLogger.log(Logger.WARNING, "Error processing command " + name + ". ", e);
					}

					// Never veto a command
					return true;
				}
			};
		}

		synchronized (singletonLock) {
			if (singletonHelper == null) {
				singletonHelper = myHelper;
			}
		}
	}

	@Override
	public void boot(Profile p) throws ServiceException {
		super.boot(p);
		myProfile = p;
		if (myProfile.isMasterMain()) {
			startPolling();
		}

		try {
			List<Specifier> specs = myProfile.getSpecifiers(AGENTS_TO_MONITOR);
			if (specs != null) {
				Iterator<Specifier> it = specs.iterator();
				while (it.hasNext()) {
					Specifier s = it.next();
					final String name = s.getClassName();
					// FIXME: manage specifier arguments queue, sent, posted, received
					MediatedMeasureProvider provider = new MediatedMeasureProvider(new MeasureProvider() {
						@Override
						public Number getValue() {
							Number ret = Double.NaN;
							AID id = new AID(name, AID.ISLOCALNAME);
							Agent a = myContainer.acquireLocalAgent(id);
							if (a != null) {
								ret = a.getCurQueueSize();
								myContainer.releaseLocalAgent(id);
							}
							return ret;
						}
					});
					myHelper.addEntityMeasureProvider(name + "-avg-queue-size", provider);
					myHelper.addCounterValueProvider(name + "-sent-message-count", new AbsoluteCounterValueProvider() {
						@Override
						public long getValue() {
							long ret = -1;
							AID id = new AID(name, AID.ISLOCALNAME);
							Agent a = myContainer.acquireLocalAgent(id);
							if (a != null) {
								ret = a.getSentMessagesCnt();
								myContainer.releaseLocalAgent(id);
							}
							return ret;
						}
					});
					myHelper.addCounterValueProvider(name + "-received-message-count",
							new AbsoluteCounterValueProvider() {
								@Override
								public long getValue() {
									long ret = -1;
									AID id = new AID(name, AID.ISLOCALNAME);
									Agent a = myContainer.acquireLocalAgent(id);
									if (a != null) {
										ret = a.getReceivedMessagesCnt();
										myContainer.releaseLocalAgent(id);
									}
									return ret;
								}
							});
				}
			}
		} catch (ProfileException pe) {
			myLogger.log(Logger.WARNING, "Error processing " + AGENTS_TO_MONITOR + " configuration property", pe);
		}
	}

	private void startPolling() throws ServiceException {
		int periodMinutes = POLLING_PERIOD_DEFAULT;
		try {
			periodMinutes = Integer.parseInt(myProfile.getParameter(POLLING_PERIOD, null));
		} catch (Exception e) {
			// Keep default;
		}
		myLogger.log(Logger.CONFIG, "Polling period = " + periodMinutes + " minutes");

		try {
			String hh = myProfile.getParameter(SAM_INFO_HANDLERS, SAM_INFO_HANDLERS_DEFAULT);
			Vector<String> handlerClasses = new Vector<>();
			if (!"none".equalsIgnoreCase(hh)) {
				handlerClasses = Specifier.parseList(hh, ';');
			}
			SAMInfoHandler[] handlers = new SAMInfoHandler[handlerClasses.size()];
			for (int i = 0; i < handlerClasses.size(); ++i) {
				String className = handlerClasses.get(i);
				myLogger.log(Logger.CONFIG, "Loading SAMInfoHandler class = " + className + "...");
				handlers[i] = (SAMInfoHandler) Class.forName(className).getDeclaredConstructor().newInstance();
				handlers[i].initialize(myProfile);
				myLogger.log(Logger.CONFIG, "SAMInfoHandler of class = " + className + " successfully initialized");
			}
			poller = new Poller(this, periodMinutes * 60000, handlers);
			poller.startPolling();
		} catch (Exception e) {
			throw new ServiceException("Error initializing SAMInfoHandler", e);
		}
	}

	@Override
	public void shutdown() {
		if (poller != null) {
			poller.stopPolling();
		}
		stopTimer();
		super.shutdown();
	}

	@Override
	public Filter getCommandFilter(boolean direction) {
		if (direction == Filter.OUTGOING) {
			return outgoingFilter;
		} else {
			return null;
		}
	}

	@Override
	public ServiceHelper getHelper(Agent a) {
		return myHelper;
	}

	public static SAMHelper getSingletonHelper() {
		synchronized (singletonLock) {
			return singletonHelper;
		}
	}

	@Override
	public Class<?> getHorizontalInterface() {
		return SAMSlice.class;
	}

	@Override
	public Service.Slice getLocalSlice() {
		return localSlice;
	}

	private Map<String, AverageMeasure> getEntityMeasures() {
		// Mutual exclusion with modifications of entities/providers
		synchronized (myHelper) {
			Map<String, AverageMeasure> entityMeasures = new HashMap<>();
			for (EntityInfo info : monitoredEntities) {
				entityMeasures.put(info.getName(), info.getMeasure());
			}
			return entityMeasures;
		}
	}

	private Map<String, Long> getCounterValues() {
		// Mutual exclusion with modifications of counters/providers
		synchronized (myHelper) {
			Map<String, Long> counterValues = new HashMap<>();
			for (CounterInfo info : monitoredCounters) {
				counterValues.put(info.getName(), info.getValue());
			}
			return counterValues;
		}
	}

	/**
	 * Inner class SAMHelperImpl
	 */
	private class SAMHelperImpl implements SAMHelper {

		public synchronized void addEntityMeasureProvider(String entityName, final MeasureProvider provider) {
			// Wrap the "one shot" MeasureProvider into an AverageMeasureProvider to treat
			// all providers in a uniform way
			addEntityMeasureProvider(entityName, new AverageMeasureProvider() {
				public AverageMeasure getValue() {
					Number value = provider.getValue();
					if (value != null) {
						return new AverageMeasure(value.doubleValue(), 1);
					} else {
						return new AverageMeasure(0, 0);
					}
				}
			});
		}

		public synchronized void addEntityMeasureProvider(String entityName, AverageMeasureProvider provider) {
			EntityInfo info = getEntityInfo(entityName);
			info.addProvider(provider);
		}

		public synchronized void addCounterValueProvider(String counterName, CounterValueProvider provider) {
			CounterInfo info = getCounterInfo(counterName);
			info.addProvider(provider);
		}

		public void addHandler(SAMInfoHandler handler, boolean first) {
			try {
				if (poller != null) {
					handler.initialize(myProfile);
					poller.addHandler(handler, first);
				}
			} catch (Exception e) {
				throw new RuntimeException("Handler initialization error.", e);
			}
		}

		public void removeHandler(SAMInfoHandler handler) {
			if (poller != null) {
				poller.removeHandler(handler);
			}
		}

		public void init(Agent a) {
			// Nothing to do as there is a single helper for all agents
		}
	} // END of inner class SAMHelperImpl

	/**
	 * Inner class ServiceComponent
	 */
	private class ServiceComponent implements Service.Slice {

		private static final long serialVersionUID = -1077400125428346237L;

		// Implementation of the Service.Slice interface
		public Service getService() {
			return SAMService.this;
		}

		public Node getNode() throws ServiceException {
			try {
				return SAMService.this.getLocalNode();
			} catch (IMTPException imtpe) {
				throw new ServiceException("Problem contacting the IMTP Manager", imtpe);
			}
		}

		public VerticalCommand serve(HorizontalCommand cmd) {
			try {
				String cmdName = cmd.getName();
				if (SAMSlice.H_GETSAMINFO.equals(cmdName)) {
					// Collect all SAM information from the local node
					SAMInfo info = new SAMInfo(getEntityMeasures(), getCounterValues());
					cmd.setReturnValue(info);
				}
			} catch (Throwable t) {
				cmd.setReturnValue(t);
			}
			// Do not issue any VerticalCommand
			return null;
		}
	} // END of inner class ServiceComponent

	/**
	 * Inner class EntityInfo
	 */
	private class EntityInfo {
		private String name;
		private List<AverageMeasureProvider> providers = new ArrayList<>();

		EntityInfo(String name) {
			this.name = name;
		}

		String getName() {
			return name;
		}

		void addProvider(AverageMeasureProvider provider) {
			providers.add(provider);

			if (provider instanceof MediatedMeasureProvider measureProvider) {
				connect(measureProvider);
			}
		}

		AverageMeasure getMeasure() {
			AverageMeasure result = new AverageMeasure();
			for (AverageMeasureProvider p : providers) {
				AverageMeasure m = p.getValue();
				result.update(m);
			}
			return result;
		}
	} // END of inner class EntityInfo

	private EntityInfo getEntityInfo(String entityName) {
		for (EntityInfo info : monitoredEntities) {
			if (info.getName().equals(entityName)) {
				return info;
			}
		}
		// Entity not found --> create it
		EntityInfo info = new EntityInfo(entityName);
		monitoredEntities.add(info);
		return info;
	}

	/**
	 * Inner class CounterInfo
	 */
	private class CounterInfo {
		private String name;
		private List<CounterValueProvider> providers = new ArrayList<>();
		private List<Long> previousTotalValues = new ArrayList<>();

		CounterInfo(String name) {
			this.name = name;
		}

		String getName() {
			return name;
		}

		void addProvider(CounterValueProvider provider) {
			providers.add(provider);
			previousTotalValues.add((long) 0);
		}

		long getValue() {
			long result = 0;
			for (int i = 0; i < providers.size(); ++i) {
				CounterValueProvider p = providers.get(i);
				long v = p.getValue();
				if (p.isDifferential()) {
					// The provider returns a differential value. Add it directly
					result += v;
				} else {
					// The provider returns a total value. Add the differential value and update the
					// previous total value
					result += v - previousTotalValues.get(i);
					previousTotalValues.set(i, v);
				}
			}
			return result;
		}
	} // END of inner class CounterInfo

	private CounterInfo getCounterInfo(String counterName) {
		for (CounterInfo info : monitoredCounters) {
			if (info.getName().equals(counterName)) {
				return info;
			}
		}
		// Counter not found --> create it
		CounterInfo info = new CounterInfo(counterName);
		monitoredCounters.add(info);
		return info;
	}

	private synchronized void connect(MediatedMeasureProvider p) {
		providers.add(p);
		providersArray = providers.toArray(new MediatedMeasureProvider[0]);

		if (samTimer == null) {
			samTimer = new Timer();
			samTimer.scheduleAtFixedRate(new TimerTask() {
				@Override
				public void run() {
					for (MediatedMeasureProvider provider : providersArray) {
						provider.collectNewValue();
					}
				}
			}, 0, 19000); // Tick every 19 secs
		}
	}

	synchronized void stopTimer() {
		if (samTimer != null) {
			samTimer.cancel();
		}
	}
}
