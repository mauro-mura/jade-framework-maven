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

import jade.core.Service;
import jade.core.VerticalCommand;
import jade.core.AID;
import jade.core.Filter;

import java.util.Iterator;
import java.util.Map;

import jade.core.management.AgentManagementSlice;
import jade.domain.FIPAAgentManagement.Envelope;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.ACLCodec;
import jade.lang.acl.LEAPACLCodec;

/**
 * Class that filters incoming commands related to encoding of ACL messages.
 *
 * @author Jerome Picault - Motorola Labs
 * @author Nicolas Lhuillier - Motorola Labs
 * @author Moreno LAGO
 * @version $Date: 2015-02-23 08:58:35 +0100 (lun, 23 feb 2015) $ $Revision:
 *          6739 $
 */
public class IncomingEncodingFilter extends Filter {

	private final Map messageEncodings;
	private final MessagingService myService;

	public IncomingEncodingFilter(Map m, MessagingService svc) {
		messageEncodings = m;
		myService = svc;
		setPreferredPosition(50);
	}

	/**
	 * Receive a command object for processing.
	 *
	 * @param cmd A <code>VerticalCommand</code> describing what operation has been
	 *            requested from previous layers (that can be the actual prime
	 *            source of the command or previous filters in the chain).
	 */
	public boolean accept(VerticalCommand cmd) {
		String name = cmd.getName();
		if (MessagingSlice.SEND_MESSAGE.equals(name)) {
			Object[] params = cmd.getParams();
			GenericMessage gmsg = (GenericMessage) params[1];

			try {
				restore(gmsg);
			} catch (Exception e) {
				e.printStackTrace();
				cmd.setReturnValue(e);
				return false;
			}

//			// The command always contains a non-null ACLMessage (for the purpose
//			// of notification of failures), but it contains the real ACLMessage
//			// when the payload is null
//			byte[] payload = gmsg.getPayload();
//			if (payload == null){
//				// If a real ACLMessage is present, just do nothing!
//				return true;
//			}
//			else {
//				Envelope env = gmsg.getEnvelope();
//				try{
//					ACLMessage msg = decodeMessage(env,payload);
//					msg.setEnvelope(env);
//					
//					if (env != null){
//						// If the 'sender' AID has no addresses, replace it with the
//						// 'from' envelope slot
//						AID sender = msg.getSender();
//						if(sender == null) {
//							System.err.println("ERROR: Trying to dispatch a message with a null sender.");
//							System.err.println("Aborting send operation...");
//							return true;
//						}
//						Iterator itSender = sender.getAllAddresses();
//						if(!itSender.hasNext()) {
//							msg.setSender(env.getFrom());
//						}
//					}
//					gmsg.update(msg,null,null);
//				} catch (MessagingService.UnknownACLEncodingException ee){
//					ee.printStackTrace();
//					cmd.setReturnValue(ee);
//					return false;
//				} catch (ACLCodec.CodecException ce){
//					ce.printStackTrace();
//					cmd.setReturnValue(ce);
//					return false;
//				}
//			}
		} else if (AgentManagementSlice.INFORM_KILLED.equals(name)) {
			// An agent is terminating --> remove its global aliases if any
			Object[] params = cmd.getParams();
			myService.removeGlobalAliases((AID) params[0]);
			myService.replicationHandle.invokeReplicatedMethod("removeGlobalAliases", params);
		}
		return true;
	}

	private void restore(GenericMessage gmsg)
			throws MessagingService.UnknownACLEncodingException, ACLCodec.CodecException {
		
		// If gmsg represents a MultipleGenericMessage recursively call restore() for
		// each message
		if (gmsg instanceof MultipleGenericMessage message) {
			for (GenericMessage g : message.getMessages()) {
				restore(g);
			}
			return;
		}
		

		// The command always contains a non-null ACLMessage (for the purpose
		// of notification of failures), but it contains the real ACLMessage
		// when the payload is null
		byte[] payload = gmsg.getPayload();
		if (payload == null) {
		} else {
			Envelope env = gmsg.getEnvelope();
			ACLMessage msg = decodeMessage(env, payload);
			msg.setEnvelope(env);

			if (env != null) {
				// If the 'sender' AID has no addresses, replace it with the
				// 'from' envelope slot
				AID sender = msg.getSender();
				if (sender == null) {
					throw new IllegalArgumentException("Trying to dispatch a message with a null sender.");
				}
				Iterator<String> itSender = sender.getAllAddresses();
				if (!itSender.hasNext()) {
					msg.setSender(env.getFrom());
				}
			}
			gmsg.update(msg, null, null);
		}
	}

	public void postProcess(VerticalCommand cmd) {
		String name = cmd.getName();
		if (Service.REATTACHED.equals(name)) {
			// The Main Container lost all information about the local container -->
			// Send it again local MTPs information
			myService.notifyLocalMTPs();

			// Send it again local aliases information
			myService.notifyLocalAliases();
		}
	}

	/**
	 * Decodes an endoded ACL message according to the acl-representation described
	 * in the envelope.
	 * 
	 * @param env     the Envelope of the message
	 * @param payload the encoded message
	 * @return the decoded <code>ACLMessage</code>
	 */
	public ACLMessage decodeMessage(Envelope env, byte[] payload)
			throws MessagingService.UnknownACLEncodingException, ACLCodec.CodecException {
		String enc;
		if (env != null) {
			enc = env.getAclRepresentation();
		} else {
			// no envelope means inter-container communication; use LEAP codec
			enc = LEAPACLCodec.NAME;
		}
		if (enc != null) { // A Codec was selected
			ACLCodec codec = (ACLCodec) messageEncodings.get(enc.toLowerCase());
			if (codec != null) {
				// Supported Codec
				String charset;
				if ((env == null) || ((charset = env.getPayloadEncoding()) == null)) {
					charset = ACLCodec.DEFAULT_CHARSET;
				}
				return codec.decode(payload, charset);
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

} // End of IncomingEncodingFilter class
