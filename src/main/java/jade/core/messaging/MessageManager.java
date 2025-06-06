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

package jade.core.messaging;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import jade.util.Logger;
import jade.lang.acl.ACLMessage;
import jade.domain.FIPAAgentManagement.InternalError;
import jade.core.ResourceManager;
import jade.core.exception.NotFoundException;
import jade.core.exception.ProfileException;
import jade.core.exception.UnreachableException;
import jade.core.AID;
import jade.core.Profile;
import jade.core.sam.AverageMeasureProviderImpl;

/**
 * This class manages the delivery of ACLMessages to remote destinations in an
 * asynchronous way. If network problems prevent the delivery of a message, this
 * class also embeds a mechanism to buffer the message and periodically retry to
 * deliver it.
 * 
 * @author Giovanni Caire - TILAB
 * @author Elisabetta Cortese - TILAB
 * @author Fabio Bellifemine - TILAB
 * @author Jerome Picault - Motorola Labs
 * @author Moreno LAGO
 * @version $Date: 2018-09-11 12:35:04 +0200 (mar, 11 set 2018) $ $Revision: 6837 $
 */
final class MessageManager {

	public interface Channel {
		void deliverNow(GenericMessage msg, AID receiverID) throws UnreachableException, NotFoundException;

		void notifyFailureToSender(GenericMessage msg, AID receiver, InternalError ie);
	}

	// A shared instance to have a single thread pool
	// FIXME: Maybe a table, indexed by a profile subset, would be better?
	private static MessageManager theInstance; 

	private static final String DUMMY_RECEIVER_NAME = "___DUMMY_";

	private static final int POOL_SIZE_DEFAULT = 5;
	@SuppressWarnings("unused")
	private static final int MAX_POOL_SIZE = 100;

	private static final int DELIVERY_TIME_THRESHOLD_DEFAULT = 1000; // ms
	private static final int DELIVERY_TIME_THRESHOLD2_DEFAULT = 5000; // ms
	private static final int DELIVERY_STUCK_TIME_DEFAULT = 60000; // ms

	// 10MBytes
	private static final int WARNING_QUEUE_SIZE_DEFAULT = 10000000;
	// 100MBytes
	private static final int MAX_QUEUE_SIZE_DEFAULT = 100000000;
	// ms/MByes, -1=no sleep time
	private static final int SLEEP_TIME_FACTOR_DEFAULT = -1;

	private OutBox outBox;
	private Thread[] delivererThreads;
	private Deliverer[] deliverers;
	private boolean active = true;
	private long deliveryTimeThreshold;
	private long deliveryTimeThreshold2;
	private long deliveryStuckTime;

	private long totSubmittedCnt;
	private long totServedCnt;
	private long totDiscardedCnt;
	private long totSlowDeliveryCnt;
	private long totVerySlowDeliveryCnt;

	// How many times multiple-message-delivery was triggered
	private long totMultipleDeliveryCnt;
	// Average number of messages delivered in multiple-message-delivery
	private final AverageMeasureProviderImpl avgMsgCountPerMultipleDelivery = new AverageMeasureProviderImpl();

	private final Logger myLogger = Logger.getMyLogger(getClass().getName());

	private MessageManager() {
	}

	public static synchronized MessageManager instance(Profile p) {
		if (theInstance == null) {
			theInstance = new MessageManager();
			theInstance.initialize(p);
		}

		return theInstance;
	}

	public void initialize(Profile p) {
		String tmp = null;

		// POOL_SIZE
		int poolSize = POOL_SIZE_DEFAULT;
		try {
			tmp = p.getParameter("jade_core_messaging_MessageManager_poolsize", null);
			poolSize = Integer.parseInt(tmp);
		} catch (Exception e) {
			// Keep default. If a value was specified print a warning
			if (tmp != null) {
				myLogger.log(Logger.WARNING, "\"" + tmp
						+ "\" is not a valid int value for parameter jade_core_messaging_MessageManager_poolsize. Keep default");
			}
		}

		// DELIVERY_TIME_THRESHOLD 1 (Slow)
		deliveryTimeThreshold = DELIVERY_TIME_THRESHOLD_DEFAULT;
		try {
			tmp = p.getParameter("jade_core_messaging_MessageManager_deliverytimethreshold", null);
			deliveryTimeThreshold = Long.parseLong(tmp);
		} catch (Exception e) {
			// Keep default. If a value was specified print a warning
			if (tmp != null) {
				myLogger.log(Logger.WARNING, "\"" + tmp
						+ "\" is not a valid int long for parameter jade_core_messaging_MessageManager_deliverytimethreshold. Keep default");
			}
		}

		// DELIVERY_TIME_THRESHOLD 2 (Very Slow)
		deliveryTimeThreshold2 = DELIVERY_TIME_THRESHOLD2_DEFAULT;
		try {
			tmp = p.getParameter("jade_core_messaging_MessageManager_deliverytimethreshold2", null);
			deliveryTimeThreshold2 = Long.parseLong(tmp);
		} catch (Exception e) {
			// Keep default. If a value was specified print a warning
			if (tmp != null) {
				myLogger.log(Logger.WARNING, "\"" + tmp
						+ "\" is not a valid long value for parameter jade_core_messaging_MessageManager_deliverytimethreshold2. Keep default");
			}
		}

		// DELIVERY_STUCK_TIME (If delivery is pending since more than this time, likely
		// we are stuck)
		deliveryStuckTime = DELIVERY_STUCK_TIME_DEFAULT;
		try {
			tmp = p.getParameter("jade_core_messaging_MessageManager_deliveryStuckTime", null);
			deliveryStuckTime = Long.parseLong(tmp);
		} catch (Exception e) {
			// Keep default. If a value was specified print a warning
			if (tmp != null) {
				myLogger.log(Logger.WARNING, "\"" + tmp
						+ "\" is not a valid long value for parameter jade_core_messaging_MessageManager_deliveryStuckTime. Keep default");
			}
		}

		// OUT_BOX_WARNING_SIZE
		int warningQueueSize = WARNING_QUEUE_SIZE_DEFAULT;
		try {
			tmp = p.getParameter("jade_core_messaging_MessageManager_warningqueuesize", null);
			warningQueueSize = Integer.parseInt(tmp);
		} catch (Exception e) {
			// Keep default. If a value was specified print a warning
			if (tmp != null) {
				myLogger.log(Logger.WARNING, "\"" + tmp
						+ "\" is not a valid int value for parameter jade_core_messaging_MessageManager_warningqueuesize. Keep default");
			}
		}

		// OUT_BOX_MAX_SIZE
		int maxQueueSize = MAX_QUEUE_SIZE_DEFAULT;
		try {
			tmp = p.getParameter("jade_core_messaging_MessageManager_maxqueuesize", null);
			maxQueueSize = Integer.parseInt(tmp);
		} catch (Exception e) {
			// Keep default. If a value was specified print a warning
			if (tmp != null) {
				myLogger.log(Logger.WARNING, "\"" + tmp
						+ "\" is not a valid int value for parameter jade_core_messaging_MessageManager_maxqueuesize. Keep default");
			}
		}

		// OUT_BOX_SLEEP_TIME_FACTOR
		int sleepTimeFactor = SLEEP_TIME_FACTOR_DEFAULT;
		try {
			tmp = p.getParameter("jade_core_messaging_MessageManager_sleeptimefactor", null);
			sleepTimeFactor = Integer.parseInt(tmp);
		} catch (Exception e) {
			// Keep default. If a value was specified print a warning
			if (tmp != null) {
				myLogger.log(Logger.WARNING, "\"" + tmp
						+ "\" is not a valid int value for parameter jade_core_messaging_MessageManager_sleeptimefactor. Keep default");
			}
		}

		// MULTIPLE_DELIVERY
		boolean enableMultipleDelivery = p
				.getBooleanProperty("jade_core_messaging_MessageManager_enablemultipledelivery", true);

		outBox = new OutBox(warningQueueSize, maxQueueSize, sleepTimeFactor, enableMultipleDelivery, this);

		try {
			ResourceManager rm = p.getResourceManager();
			delivererThreads = new Thread[poolSize];
			deliverers = new Deliverer[poolSize];
			for (int i = 0; i < poolSize; ++i) {
				String pad = i < 10 ? "0" : "";
				String name = "Deliverer-" + pad + i;
				deliverers[i] = new Deliverer(name);
				delivererThreads[i] = rm.getThread(ResourceManager.TIME_CRITICAL, name, deliverers[i]);
				if (myLogger.isLoggable(Logger.FINE)) {
					myLogger.log(Logger.FINE, "Starting deliverer " + name + ". Thread=" + delivererThreads[i]);
				}
				delivererThreads[i].start();
			}

			// When the JADE Runtime terminates stop all deliverers.
			jade.core.Runtime.instance().invokeOnTermination(new Runnable() {

				public void run() {
					shutdown();
				}
			});
		} catch (ProfileException pe) {
			throw new RuntimeException("Can't get ResourceManager. " + pe.getMessage());
		}
	}

	private void shutdown() {
		myLogger.log(Logger.INFO, "MessageManager shutting down ...");
		active = false;
		// Submit 1 dummy message for each deliverer.
		for (int i = 0; i < deliverers.length; ++i) {
			outBox.addLast(new AID(DUMMY_RECEIVER_NAME + i, AID.ISGUID), new GenericMessage(), null);
		}
		// Reset the MessageManager singleton instance
		theInstance = null;
	}

	private int getDelivererIndex(String name) {
		if (name != null) {
			int length = name.length();
			return (name.charAt(length - 2) - '0') * 10 + (name.charAt(length - 1) - '0');
		} else {
			return -1;
		}
	}

	boolean isStuck(String name) {
		int index = getDelivererIndex(name);
		if (index >= 0 && index < deliverers.length) {
			Deliverer d = deliverers[index];
			return d.isStuck();
		} else {
			return false;
		}
	}

	/**
	 * Activate the asynchronous delivery of a GenericMessage
	 */
	public void deliver(GenericMessage msg, AID receiverID, Channel ch) {
		if (active) {
			totSubmittedCnt++;
			try {
				outBox.addLast(receiverID, msg, ch);
			} catch (Exception e) {
				totDiscardedCnt++;
				// If queue is full, trying to send back a FAILURE is useless since the FAILURE
				// would be discarded too
				if (!(e instanceof QueueFullException)) {
					if (e instanceof StuckDeliverer deliverer) {
						String name = deliverer.getDelivererName();

						myLogger.log(Logger.WARNING,
								"Deliverer " + name + " appears to be stuck!!!!! Try to interrupt it...");
						int index = getDelivererIndex(name);
						if (index >= 0 && index < delivererThreads.length) {
							delivererThreads[index].interrupt();
						}

						/*
						 * #MIDP_INCLUDE_BEGIN myLogger.log(Logger.WARNING,
						 * "Deliverer "+name+" appears to be stuck!!!!!"); #MIDP_INCLUDE_END
						 */
					}
					ch.notifyFailureToSender(msg, receiverID, new InternalError(e.getMessage()));
				}
			}
		} else {
			myLogger.log(Logger.WARNING, "MessageManager NOT active. Cannot deliver message " + stringify(msg));
		}
	}

	/**
	 * Inner class Deliverer
	 */
	class Deliverer implements Runnable {

		private String name;
		private long lastDeliveryStartTime = -1;
		private long lastDeliveryEndTime = -1;
		private boolean delivering;
		// For debugging purpose
		private long servedCnt;

		Deliverer(String name) {
			this.name = name;
		}

		public void run() {
			while (active) {
				// Get a message from the OutBox (block until there is one)
				PendingMsg pm = outBox.get();

				DeliveryTracing.beginTracing();

				lastDeliveryStartTime = System.currentTimeMillis();
				GenericMessage msg = pm.getMessage();
				AID receiverID = pm.getReceiver();

				// Deliver the message
				Channel ch = pm.getChannel();
				if (ch != null) {
					delivering = true;
					// Ch is null only in the case of dummy messages used to make the deliverers
					// terminate.
					// See shutdown() method
					try {
						ch.deliverNow(msg, receiverID);
					} catch (Throwable t) {
						// deliverNow() never throws exception. This is just a last protection since a
						// MessageManager deliverer thread must never die
						myLogger.log(Logger.WARNING, "MessageManager cannot deliver message " + stringify(msg)
								+ " to agent " + receiverID.getName(), t);
						ch.notifyFailureToSender(msg, receiverID,
								new InternalError(ACLMessage.AMS_FAILURE_UNEXPECTED_ERROR + ": " + t));
					} finally {
						delivering = false;
					}
					int k = msg.getMessagesCnt();
					servedCnt += k;
					totServedCnt += k;
					if (k > 1) {
						totMultipleDeliveryCnt++;

						avgMsgCountPerMultipleDelivery.addSample(k);

					}
					outBox.handleServed(receiverID, k);

					lastDeliveryEndTime = System.currentTimeMillis();
					long deliveryTime = lastDeliveryEndTime - lastDeliveryStartTime;
					try {
						if (deliveryTimeThreshold > 0) {
							// For normal messages just compare delivery time with deliveryTimeThreshold.
							// For multiple-messages compute the threshold as (deliveryTimeThreshold / 2) *
							// number-of-messages --> So if deliveryTimeThreshold is 1000 and
							// we have a multiple-message with 5 messages --> threshold is 2500.
							// Always consider slow delivery over 10 sec (regardless of how many messages
							// are there).
							long threshold = k == 1 ? deliveryTimeThreshold
									: Math.min((deliveryTimeThreshold / 2) * k, 10000);
							if (deliveryTime > threshold) {
								totSlowDeliveryCnt += k;
								String msgDetail = k == 1 ? "message size = " + msg.length()
										: "block of " + k + " messages with overall size = " + msg.length();

								myLogger.log(Logger.WARNING,
										"Deliverer Thread " + name + " - Delivery-time over threshold (" + deliveryTime
												+ "). Receiver = " + receiverID.getLocalName() + ", " + msgDetail + ". "
												+ DeliveryTracing.report());

								/*
								 * #J2ME_INCLUDE_BEGIN myLogger.log(Logger.WARNING, "Deliverer Thread "+name+
								 * " - Delivery-time over threshold ("+deliveryTime+"). Receiver = "+receiverID.
								 * getLocalName()+", "+msgDetail+"."); #J2ME_INCLUDE_END
								 */

								// See comment above
								long threshold2 = k == 1 ? deliveryTimeThreshold2
										: Math.min((deliveryTimeThreshold2 / 2) * k, 30000);
								if (deliveryTime > threshold2) {
									totVerySlowDeliveryCnt++;
								}
							}
						}
					} catch (Exception e) {
						// Should never happen, but Deliverers must never die and so...
						myLogger.log(Logger.WARNING, "Unexpected error computing message delivery time", e);
					}
				}
			}

			myLogger.log(Logger.CONFIG, "Deliverer Thread " + name + " terminated");
		}

		long getServedCnt() {
			return servedCnt;
		}

		long getLastDeliveryStartTime() {
			return lastDeliveryStartTime;
		}

		long getLastDeliveryEndTime() {
			return lastDeliveryEndTime;
		}

		boolean isStuck() {
			if (delivering) {
				// Delivery process started but not completed yet
				return System.currentTimeMillis() - lastDeliveryStartTime > deliveryStuckTime;
			} else {
				return false;
			}
		}
	} // END of inner class Deliverer

	/**
	 * Inner class PendingMsg
	 */
	public static class PendingMsg {
		private GenericMessage msg;
		private final AID receiverID;
		private final Channel channel;
		private long deadline;

		public PendingMsg(GenericMessage msg, AID receiverID, Channel channel, long deadline) {
			this.msg = msg;
			this.receiverID = receiverID;
			this.channel = channel;
			this.deadline = deadline;
		}

		public void setMessage(GenericMessage msg) {
			this.msg = msg;
		}

		public GenericMessage getMessage() {
			return msg;
		}

		public AID getReceiver() {
			return receiverID;
		}

		public Channel getChannel() {
			return channel;
		}

		public long getDeadline() {
			return deadline;
		}

		public void setDeadline(long deadline) {
			this.deadline = deadline;
		}
	} // END of inner class PendingMsg

	/**
	 */
	public static String stringify(GenericMessage m) {

		if (m instanceof MultipleGenericMessage mm) {
			// MULTIPLE message
			StringBuilder sb = new StringBuilder("[SET");
			List<GenericMessage> l = mm.getMessages();
			int cnt = 0;
			for (GenericMessage gm : l) {
				sb.append(" ");
				sb.append(stringify(gm));
				// Avoid stringifying to many messages
				cnt++;
				if (cnt > 10 && cnt < l.size()) {
					sb.append("...").append(l.size()).append(" messages in total");
					break;
				}
			}
			sb.append("]");
			return sb.toString();
		} else {

			// SINGLE (normal) message
			ACLMessage msg = m.getACLMessage();
			if (msg != null) {
				return msg.shortToString();
			} else {
				return "\"Unavailable\"";
			}

		}

	}

	public static void main(String[] args) {
		GenericMessage gm = new GenericMessage();
		ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
		msg.setSender(new AID("pippo@P1", AID.ISGUID));
		msg.addReceiver(new AID("pluto@P1", AID.ISGUID));
		gm.setACLMessage(msg);
		System.out.println(stringify(gm));

		MultipleGenericMessage mgm = new MultipleGenericMessage(200);
		List<GenericMessage> l = new ArrayList<>();
		l.add(gm);
		l.add(gm);
		mgm.setMessages(l);
		System.out.println(stringify(mgm));
	}

	// For debugging purpose
	String[] getQueueStatus() {
		return outBox.getStatus();
	}

	// For debugging purpose
	int getSize() {
		return outBox.getSize();
	}

	int getPendingCnt() {
		return outBox.getPendingCnt();
	}

	long getSubmittedCnt() {
		return totSubmittedCnt;
	}

	long getServedCnt() {
		return totServedCnt;
	}

	long getDiscardedCnt() {
		return totDiscardedCnt;
	}

	long getSlowDeliveryCnt() {
		return totSlowDeliveryCnt;
	}

	long getVerySlowDeliveryCnt() {
		return totVerySlowDeliveryCnt;
	}

	long getMultipleDeliveryCnt() {
		return totMultipleDeliveryCnt;
	}

	AverageMeasureProviderImpl getAvgMsgCountPerMultipleDeliveryProvider() {
		return avgMsgCountPerMultipleDelivery;
	}

	// For debugging purpose
	String getGlobalInfo() {
		return "Submitted-messages = " + totSubmittedCnt + ", Served-messages = " + totServedCnt
				+ ", Discarded-messages = " + totDiscardedCnt + ", Queue-size (byte) = " + outBox.getSize()
				+ ", Multiple-delivery-occurrences = " + totMultipleDeliveryCnt;
	}

	private final SimpleDateFormat timeFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

	// For debugging purpose
	String[] getThreadPoolStatus() {
		String[] status = new String[deliverers.length];
		for (int i = 0; i < deliverers.length; ++i) {
			Deliverer d = deliverers[i];
			String details = null;
			if (d.isStuck()) {

				details = "STUCK!!! last-delivery-start-time="
						+ timeFormat.format(new java.util.Date(d.getLastDeliveryStartTime()));

				/*
				 * #MIDP_INCLUDE_BEGIN details =
				 * "STUCK!!! last-delivery-start-time="+formatDate(d.getLastDeliveryStartTime())
				 * ; #MIDP_INCLUDE_END
				 */
			} else {

				details = "last-delivery-end-time=" + timeFormat.format(new java.util.Date(d.getLastDeliveryEndTime()));

				/*
				 * #MIDP_INCLUDE_BEGIN details =
				 * "last-delivery-end-time="+formatDate(d.getLastDeliveryEndTime());
				 * #MIDP_INCLUDE_END
				 */
			}
			status[i] = "(" + d.name + ": thread-alive=" + delivererThreads[i].isAlive() + ", Served-messages="
					+ d.getServedCnt() + ", " + details + ")";
		}
		return status;
	}

	// For debugging purpose
	Thread[] getThreadPool() {
		return delivererThreads;
	}
}
