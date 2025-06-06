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

//#MIDP_EXCLUDE_FILE

import java.util.Date;
import java.util.Iterator;
import java.util.Map;

import jade.core.AID;
import jade.core.AgentContainer;
import jade.core.ContainerID;
import jade.core.Filter;
import jade.core.Location;
import jade.core.VerticalCommand;
import jade.core.management.AgentManagementSlice;
import jade.domain.FIPAAgentManagement.Envelope;
import jade.lang.acl.ACLCodec;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.LEAPACLCodec;
import jade.lang.acl.StringACLCodec;

/**
 * Class that filters outgoing commands related to the encoding of ACL messages
 *
 * @author Jerome Picault - Motorola Labs
 * @author Nicolas Lhuillier - Motorola Labs
 * @author Moreno LAGO
 * @version $Date: 2014-07-15 12:54:25 +0200 (mar, 15 lug 2014) $ $Revision:
 *          6715 $
 */
public class OutgoingEncodingFilter extends Filter {

	private final Map messageEncodings;
	private final AgentContainer myAgentContainer;
	private final MessagingService myService;

	public OutgoingEncodingFilter(Map m, AgentContainer ac, MessagingService ms) {
		messageEncodings = m;
		myAgentContainer = ac;
		myService = ms;
		setPreferredPosition(10);
	}

	/**
	 * Process the SEND_MESSAGE VCommand encoding the ACLMessage with the proper
	 * representation and adjusting Envelope fields: 1) If the receiver lives in the
	 * local container --> Do not encode (to speed up performances) --> Don't touch
	 * the envelope 2) If the receiver lives in a remote container --> Encode using
	 * the specified representation or "LEAP" if no representation is specified -->
	 * If an envelope is present adjust its fields 3) If the receiver lives in a
	 * remote platform --> Encode using the specified representation or "String" if
	 * no representation is specified --> Create a default envelope if not present
	 * and adjust its fields
	 */
	public boolean accept(VerticalCommand cmd) {
		String name = cmd.getName();

		if (MessagingSlice.SEND_MESSAGE.equals(name)) {
			GenericMessage gmsg = (GenericMessage) cmd.getParam(1);
			myService.stamp(gmsg);
			AID sender = (AID) cmd.getParam(0);
			AID receiver = (AID) cmd.getParam(2);
			ACLMessage msg = gmsg.getACLMessage();

			// Set the sender unless already set
			try {
				if (msg.getSender().getName().length() < 1) {
					msg.setSender(sender);
				}
			} catch (NullPointerException e) {
				msg.setSender(sender);
			}

			// Check if the receiver is on the same container or not
			if (myAgentContainer.isLocalAgent(receiver)) {
				// Receiver is local --> do not encode the message
				return true;
			} else {
				// add necessary fields to the envelope
				prepareEnvelope(msg, receiver, gmsg);
			}

			// Encode the message using the specified encoding
			try {
				byte[] payload = encodeMessage(msg);
				Envelope env = msg.getEnvelope();
				if (env != null) {
					env.setPayloadLength(Long.valueOf(payload.length));
				}

				// Update the ACLMessage: some information are kept because they are
				// required in other services
				((GenericMessage) cmd.getParams()[1]).update(msg, env, payload);

			} catch (MessagingService.UnknownACLEncodingException ee) {
				// FIXME
				ee.printStackTrace();
			}
		} else if (AgentManagementSlice.INFORM_KILLED.equals(name)) {
			// A local agent is terminating --> remove its local aliases if any
			myService.removeLocalAliases((AID) cmd.getParam(0));
		}
		return true;
	}

	public void postProcess(VerticalCommand cmd) {
		String name = cmd.getName();

		if (jade.core.mobility.AgentMobilityHelper.INFORM_MOVED.equals(name)) {
			AID agent = (AID) cmd.getParam(0);
			Location destination = (Location) cmd.getParam(1);
			if (!myAgentContainer.isLocalAgent(agent)) {
				// The agent actually moved elsewhere
				if (destination instanceof ContainerID iD) {
					// The agent moved to a container inside the platform --> transfer its local
					// aliases there
					myService.transferLocalAliases(agent, iD);
				}
			}
		}
	}

	/**
	 * This method puts into the envelope the missing information if required
	 */
	public void prepareEnvelope(ACLMessage msg, AID receiver, GenericMessage gmsg) {
		Envelope env = msg.getEnvelope();
		String defaultRepresentation = null;
		if (myService.livesHere(receiver)) {
			// The agent lives in the platform
			if (env == null) {
				// Nothing to do
				return;
			} else {
				defaultRepresentation = LEAPACLCodec.NAME;
			}
		} else {
			// The agent lives outside the platform
			gmsg.setForeignReceiver(true);
			if (env == null) {
				msg.setDefaultEnvelope();
				env = msg.getEnvelope();
			} else {
				defaultRepresentation = StringACLCodec.NAME;
			}
		}

		// If no ACL representation is set, use the default one ("LEAP" for
		// local receivers and "String" for foreign receivers)
		String rep = env.getAclRepresentation();
		if (rep == null) {
			env.setAclRepresentation(defaultRepresentation);
		}

		// If no 'to' slot is present, copy the 'to' slot from the
		// 'receiver' slot of the ACL message
		Iterator<AID> itTo = env.getAllTo();
		if (!itTo.hasNext()) {
			Iterator<AID> itReceiver = msg.getAllReceiver();
			while (itReceiver.hasNext()) {
				env.addTo(itReceiver.next());
			}
		}

		// If no 'from' slot is present, copy the 'from' slot from the
		// 'sender' slot of the ACL message
		AID from = env.getFrom();
		if (from == null) {
			env.setFrom(msg.getSender());
		}

		// Set the 'date' slot to 'now' if not present already
		Date d = env.getDate();
		if (d == null) {
			env.setDate(new Date());
		}

		// Write 'intended-receiver' slot as per 'FIPA Agent Message
		// Transport Service Specification': this ACC splits all
		// multicasts, since JADE has already split them in the
		// handleSend() method
		env.clearAllIntendedReceiver();
		env.addIntendedReceiver(receiver);

		Long payloadLength = env.getPayloadLength();
		if (payloadLength == null) {
			env.setPayloadLength(Long.valueOf(-1));
		}
	}

	/**
	 * Encodes an ACL message according to the acl-representation described in the
	 * envelope. If there is no explicit acl-representation in the envelope, uses
	 * the String representation
	 * 
	 * @param msg the message to be encoded
	 * @return the payload of the message
	 */
	public byte[] encodeMessage(ACLMessage msg) throws MessagingService.UnknownACLEncodingException {

		Envelope env = msg.getEnvelope();
		String enc = env != null ? env.getAclRepresentation() : LEAPACLCodec.NAME;

		if (enc != null) { // A Codec was selected
			ACLCodec codec = (ACLCodec) messageEncodings.get(enc.toLowerCase());
			if (codec != null) {
				// Supported Codec
				// FIXME: should verify that the receivers supports this Codec
				String charset;
				if ((env == null) || ((charset = env.getPayloadEncoding()) == null)) {
					charset = ACLCodec.DEFAULT_CHARSET;
				}
				return codec.encode(msg, charset);
			} else {
				// Unsupported Codec
				// FIXME: find the best according to the supported, the MTP (and the receivers
				// Codec)
				throw new MessagingService.UnknownACLEncodingException("Unknown ACL encoding: " + enc + ".");
			}
		} else {
			// no codec indicated.
			// FIXME: find the better according to the supported Codec, the MTP (and the
			// receiver codec)
			throw new MessagingService.UnknownACLEncodingException("No ACL encoding set.");
		}
	}

} // End of EncodingOutgoingFilter class
