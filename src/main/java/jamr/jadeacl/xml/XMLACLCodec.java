/*****************************************************************
JADE - Java Agent DEvelopment Framework is a framework to develop 
multi-agent systems in compliance with the FIPA specifications.
Copyright (C) 2000 CSELT S.p.A. 

Copyright (C) 2000, 2001 Laboratoire d'Intelligence
Artificielle, Ecole Polytechnique Federale de Lausanne

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

package jamr.jadeacl.xml;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.Date;
import java.util.Iterator;
/**
   Class used to convert between the XML ACL representation as
   defined in FIPA spec 00071 and JADE's ACLMessage object.

   @author Ion Constantinescu - EPFL
   @author Fabio Bellifemine - CSELT S.p.A.
   @version $Date: 2006-01-19 14:32:14 +0100 (gio, 19 gen 2006) $ $Revision: 632 $

 */
import java.util.Vector;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import jade.core.AID;
import jade.core.CaseInsensitiveString;
import jade.domain.FIPANames;
import jade.lang.acl.ACLCodec;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.ISO8601;

public class XMLACLCodec extends DefaultHandler implements ACLCodec {

	public static final String DEFAULT_PARSER_NAME = "org.apache.crimson.parser.XMLReaderImpl";

	// public static final String DEFAULT_PARSER_NAME=
	// "org.apache.xerces.parsers.SAXParser";

	static String parserClassName;

	static {
		parserClassName = System.getProperty("org.xml.sax.parser");
		if ((parserClassName == null) || "".equals(parserClassName)) {
			parserClassName = DEFAULT_PARSER_NAME;
		}
	}

	/**
	 * Key of the user-defined parameter used to signal the automatic JADE
	 * conversion of the content into Base64 encoding
	 **/
	private static final String BASE64ENCODING_KEY = new String("X-JADE-Encoding");
	/**
	 * Value of the user-defined parameter used to signal the automatic JADE
	 * conversion of the content into Base64 encoding
	 **/
	private static final String BASE64ENCODING_VALUE = new String("Base64");

	public static final String ACL_REPRESENTATION_NAME = FIPANames.ACLCodec.XML;

	public static final String HREF_ATTR = "href";

	public static final String FIPA_MESSAGE_TAG = "fipa-message";
	public static final String ACT_ATTR = "act";

	public static final String CONVERSATION_ID_ATTR = "conversation-id";

	public static final String SENDER_TAG = "sender";
	public static final String RECEIVER_TAG = "receiver";

	public static final String CONTENT_TAG = "content";
	public static final String LANGUAGE_TAG = "language";
	public static final String CONTENT_LANGUAGE_ENCODING_TAG = "encoding";
	public static final String ONTOLOGY_TAG = "ontology";
	public static final String PROTOCOL_TAG = "protocol";
	public static final String REPLY_WITH_TAG = "reply-with";
	public static final String IN_REPLY_TO_TAG = "in-reply-to";
	public static final String REPLY_BY_TAG = "reply-by";
	public static final String TIME_ATTR = "time";
	public static final String REPLY_TO_TAG = "reply-to";

	public static final String CONVERSATION_ID_TAG = "conversation-id";

	public static final String AID_TAG = "agent-identifier";
	public static final String NAME_TAG = "name";
	public static final String ID_ATTR = "id";
	public static final String ADDRESSES_TAG = "addresses";
	public static final String RESOLVERS_TAG = "resolvers";
	public static final String UD_TAG = "user-defined";
	public static final String URL_TAG = "url";

	XMLReader parser;

	boolean pcdata_accumulate;

	String pcdata_buf;

	public XMLACLCodec() throws CodecException {

		try {
			parser = (XMLReader) Class.forName(parserClassName).newInstance();
			parser.setContentHandler(this);
		} catch (ClassNotFoundException cexc) {
			throw new CodecException("While creating parser got ", cexc);
		} catch (InstantiationException iexc) {
			throw new CodecException("While creating parser got ", iexc);
		} catch (IllegalAccessException ilexc) {
			throw new CodecException("While creating parser got ", ilexc);
		}
	}

	public String getValueByLocalName(Attributes attributes, String localName) {

		for (int i = 0; i < attributes.getLength(); i++) {

			if (attributes.getLocalName(i).equalsIgnoreCase(localName)) {
				return attributes.getValue(i);
			}

		}
		return null;
	}

	Object current;

	Vector<Object> stack = new Vector<>();

	ACLMessage msg;

	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {

		String tmp = null;

		if (FIPA_MESSAGE_TAG.equalsIgnoreCase(localName)) {

			tmp = getValueByLocalName(attributes, ACT_ATTR);

			if (tmp == null) {
				throw new SAXException("Could not create fipa message with empty communicative act !");
			}

			msg = new ACLMessage(ACLMessage.getInteger(tmp.toUpperCase()));

			stack.clear();
		}

		if (SENDER_TAG.equalsIgnoreCase(localName) || RECEIVER_TAG.equalsIgnoreCase(localName)
				|| REPLY_TO_TAG.equalsIgnoreCase(localName) || RESOLVERS_TAG.equalsIgnoreCase(localName)) {
			current = new AID();
			stack.addElement(current);
		} else if (NAME_TAG.equalsIgnoreCase(localName)) {
			tmp = getValueByLocalName(attributes, ID_ATTR);
			if (tmp != null) {
				((AID) current).setName(tmp);
			} else {
				throw new SAXException("Empty name value not allowed !");
			}
		} else if (URL_TAG.equalsIgnoreCase(localName)) {
			tmp = getValueByLocalName(attributes, HREF_ATTR);
			if (tmp != null) {
				((AID) current).addAddresses(tmp);
			} else {
				throw new SAXException("Empty url value not allowed !");
			}
		} else if (REPLY_BY_TAG.equalsIgnoreCase(localName)) {
			tmp = getValueByLocalName(attributes, TIME_ATTR);

			if ((tmp != null) && (!"".equals(tmp))) {
				try {
					msg.setReplyByDate(ISO8601.toDate(tmp));
				} catch (Exception exc) {
					exc.printStackTrace();
				}
			} else {
				throw new SAXException("Empty reply by value not allowed !");
			}
		}

		tmp = getValueByLocalName(attributes, HREF_ATTR);

		if (tmp != null) {
			if (CONTENT_TAG.equalsIgnoreCase(localName)) {
				msg.setContent(tmp);
			} else if (LANGUAGE_TAG.equalsIgnoreCase(localName)) {
				msg.setLanguage(tmp);
			} else if (CONTENT_LANGUAGE_ENCODING_TAG.equalsIgnoreCase(localName)) {
				msg.setEncoding(tmp);
			} else if (ONTOLOGY_TAG.equalsIgnoreCase(localName)) {
				msg.setOntology(tmp);
			} else if (PROTOCOL_TAG.equalsIgnoreCase(localName)) {
				msg.setProtocol(tmp);
			} else if (REPLY_WITH_TAG.equalsIgnoreCase(localName)) {
				msg.setReplyWith(tmp);
			} else if (IN_REPLY_TO_TAG.equalsIgnoreCase(localName)) {
				msg.setInReplyTo(tmp);
			} else if (CONVERSATION_ID_TAG.equalsIgnoreCase(localName)) {
				msg.setConversationId(tmp);
			}
		} else {
			pcdata_accumulate = true;
		}

	}

	public void endElement(String uri, String localName, String qName) throws SAXException {

		if (pcdata_accumulate) {
			if (CONTENT_TAG.equalsIgnoreCase(localName)) {
				msg.setContent(pcdata_buf);
			} else if (LANGUAGE_TAG.equalsIgnoreCase(localName)) {
				msg.setLanguage(pcdata_buf);
			} else if (CONTENT_LANGUAGE_ENCODING_TAG.equalsIgnoreCase(localName)) {
				msg.setEncoding(pcdata_buf);
			} else if (ONTOLOGY_TAG.equalsIgnoreCase(localName)) {
				msg.setOntology(pcdata_buf);
			} else if (PROTOCOL_TAG.equalsIgnoreCase(localName)) {
				msg.setProtocol(pcdata_buf);
			} else if (REPLY_WITH_TAG.equalsIgnoreCase(localName)) {
				msg.setReplyWith(pcdata_buf);
			} else if (IN_REPLY_TO_TAG.equalsIgnoreCase(localName)) {
				msg.setInReplyTo(pcdata_buf);
			} else if (REPLY_BY_TAG.equalsIgnoreCase(localName)) {
				try {
					msg.setReplyByDate(ISO8601.toDate(pcdata_buf));
				} catch (Exception exc) {
					exc.printStackTrace();
				}
			} else if (CONVERSATION_ID_TAG.equalsIgnoreCase(localName)) {
				msg.setConversationId(pcdata_buf);
			} else if (localName.startsWith("X-")) {
				msg.addUserDefinedParameter(localName, pcdata_buf);
			}
			pcdata_accumulate = false;
			pcdata_buf = null;
		} else {
			if (SENDER_TAG.equalsIgnoreCase(localName)) {
				msg.setSender((AID) current);
				stack.removeElementAt(stack.size() - 1);
			} else if (RECEIVER_TAG.equalsIgnoreCase(localName)) {
				msg.addReceiver((AID) current);
				stack.removeElementAt(stack.size() - 1);
			} else if (REPLY_TO_TAG.equalsIgnoreCase(localName)) {
				msg.addReplyTo((AID) current);
				stack.removeElementAt(stack.size() - 1);
			} else if (RESOLVERS_TAG.equalsIgnoreCase(localName)) {
				AID tmpaid = (AID) current;
				stack.removeElementAt(stack.size() - 1);
				current = stack.elementAt(stack.size() - 1);
				((AID) current).addResolvers(tmpaid);
			}
		}
	}

	public void characters(char[] chars, int pos, int len) {
		if (pcdata_accumulate) {
			String str = new String(chars, pos, len).trim();
			if (!"".equals(str)) {
				if ((pcdata_buf == null) || "".equals(pcdata_buf)) {
					pcdata_buf = str;
				} else {
					pcdata_buf = pcdata_buf + " " + str;
				}
			}
		}
	}

	/** Ignorable whitespace. */
	public void ignorableWhitespace(char[] ch, int start, int length) {
	}

	private void encodeAID(StringBuffer sb, String prefix, AID aid) {
		sb.append(prefix);
		sb.append("<");
		sb.append(AID_TAG);
		sb.append(">\n");

		sb.append(prefix);
		sb.append("\t<");
		sb.append(NAME_TAG);
		sb.append(" ");
		sb.append(ID_ATTR);
		sb.append("=\"");
		sb.append(aid.getName());
		sb.append("\" />\n");

		String[] addrs = aid.getAddressesArray();
		if ((addrs != null) && (addrs.length > 0)) {
			sb.append(prefix);
			sb.append("\t<");
			sb.append(ADDRESSES_TAG);
			sb.append(">\n");

			for (int i = 0; i < addrs.length; i++) {
				sb.append(prefix);
				sb.append("\t\t<");
				sb.append(URL_TAG);
				sb.append(" ");
				sb.append(HREF_ATTR);
				sb.append("=\"");
				sb.append(addrs[i]);
				sb.append("\" />\n");
			}

			sb.append(prefix);
			sb.append("\t</");
			sb.append(ADDRESSES_TAG);
			sb.append(">\n");
		}

		sb.append(prefix);
		sb.append("</");
		sb.append(AID_TAG);
		sb.append(">\n");
	}

	/**
	 * Encodes an <code>ACLMessage</code> object into a byte sequence, according to
	 * the specific message representation.
	 * 
	 * @param msg The ACL message to encode.
	 * @return a byte array, containing the encoded message.
	 */
	public byte[] encode(ACLMessage msg, String charset) {
		StringBuffer sb = new StringBuffer("<");
		sb.append(FIPA_MESSAGE_TAG);
		sb.append(" ");
		sb.append(ACT_ATTR);
		sb.append("=\"");
		sb.append(ACLMessage.getAllPerformativeNames()[msg.getPerformative()]);
		sb.append("\" >\n");
		String tmp = null;

		if (msg.getSender() != null) {
			sb.append("\t<");
			sb.append(SENDER_TAG);
			sb.append(">\n");
			encodeAID(sb, "\t\t", msg.getSender());
			sb.append("\t</");
			sb.append(SENDER_TAG);
			sb.append(">\n");
		}

		for (Iterator<AID> it = msg.getAllReceiver(); it.hasNext();) {
			sb.append("\t<");
			sb.append(RECEIVER_TAG);
			sb.append(">\n");
			encodeAID(sb, "\t\t", it.next());
			sb.append("\t</");
			sb.append(RECEIVER_TAG);
			sb.append(">\n");
		}

		for (Iterator<AID> it = msg.getAllReplyTo(); it.hasNext();) {
			sb.append("\t<");
			sb.append(REPLY_TO_TAG);
			sb.append(">\n");
			encodeAID(sb, "\t\t", it.next());
			sb.append("\t</");
			sb.append(REPLY_TO_TAG);
			sb.append(">\n");

		}

		if (msg.hasByteSequenceContent()) {

			sb.append("\t<" + BASE64ENCODING_KEY + ">" + BASE64ENCODING_VALUE + "</" + BASE64ENCODING_KEY + ">\n");

			try {
				String b64 = new String(Base64.getEncoder().encode(msg.getByteSequenceContent()));
				sb.append("\t<");
				sb.append(CONTENT_TAG);
				sb.append(">");

				sb.append(b64);

				sb.append("</");
				sb.append(CONTENT_TAG);
				sb.append(">\n");

			} catch (java.lang.NoClassDefFoundError jlncdfe) {
				System.err.println("\n\t===== E R R O R !!! =======\n");
				System.err.println("Missing support for Base64 conversions");
				System.err.println("Please refer to the documentation for details.");
				System.err.println("=============================================\n\n");
				System.err.println("");
				try {
					Thread.currentThread().sleep(3000);
				} catch (InterruptedException ie) {
				}
			}
		} else {
			String content = msg.getContent();

			if (content != null) {
				content = content.trim();
				if (content.length() > 0) {

					sb.append("\t<");
					sb.append(CONTENT_TAG);
					sb.append(">");

					sb.append(escape(content));

					sb.append("</");
					sb.append(CONTENT_TAG);
					sb.append(">\n");
				}
			}
		}

		tmp = msg.getLanguage();
		if ((tmp != null) && (!"".equals(tmp))) {
			sb.append("\t<");
			sb.append(LANGUAGE_TAG);
			sb.append(">");
			sb.append(tmp);
			sb.append("</");
			sb.append(LANGUAGE_TAG);
			sb.append(">\n");
		}

		tmp = msg.getEncoding();
		if ((tmp != null) && (!"".equals(tmp))) {
			sb.append("\t<");
			sb.append(CONTENT_LANGUAGE_ENCODING_TAG);
			sb.append(">");
			sb.append(tmp);
			sb.append("</");
			sb.append(CONTENT_LANGUAGE_ENCODING_TAG);
			sb.append(">\n");
		}

		tmp = msg.getOntology();
		if ((tmp != null) && (!"".equals(tmp))) {
			sb.append("\t<");
			sb.append(ONTOLOGY_TAG);
			sb.append(">");
			sb.append(tmp);
			sb.append("</");
			sb.append(ONTOLOGY_TAG);
			sb.append(">\n");
		}

		tmp = msg.getProtocol();
		if ((tmp != null) && (!"".equals(tmp))) {
			sb.append("\t<");
			sb.append(PROTOCOL_TAG);
			sb.append(">");
			sb.append(tmp);
			sb.append("</");
			sb.append(PROTOCOL_TAG);
			sb.append(">\n");
		}

		tmp = msg.getConversationId();
		if ((tmp != null) && (!"".equals(tmp))) {
			sb.append("\t<");
			sb.append(CONVERSATION_ID_TAG);
			sb.append(">");
			sb.append(tmp);
			sb.append("</");
			sb.append(CONVERSATION_ID_TAG);
			sb.append(">\n");
		}

		tmp = msg.getReplyWith();
		if ((tmp != null) && (!"".equals(tmp))) {
			sb.append("\t<");
			sb.append(REPLY_WITH_TAG);
			sb.append(">");
			sb.append(tmp);
			sb.append("</");
			sb.append(REPLY_WITH_TAG);
			sb.append(">\n");
		}

		tmp = msg.getInReplyTo();
		if ((tmp != null) && (!"".equals(tmp))) {
			sb.append("\t<");
			sb.append(IN_REPLY_TO_TAG);
			sb.append(">");
			sb.append(tmp);
			sb.append("</");
			sb.append(IN_REPLY_TO_TAG);
			sb.append(">\n");
		}

		Date d = msg.getReplyByDate();
		if (d != null) {
			sb.append("\t<");
			sb.append(REPLY_BY_TAG);
			sb.append(" time=\"");
			sb.append(ISO8601.toString(d));
			sb.append("\" />\n");
		}

		sb.append("</");
		sb.append(FIPA_MESSAGE_TAG);
		sb.append(">\n");
		return sb.toString().getBytes();
	}

	/**
	 * Recovers an <code>ACLMessage</code> object back from raw data, using the
	 * specific message representation to interpret the byte sequence.
	 * 
	 * @param data The byte sequence containing the encoded message.
	 * @return A new <code>ACLMessage</code> object, built from the raw data.
	 * @exception CodecException If some kind of syntax error occurs.
	 */
	public ACLMessage decode(byte[] data, String charset) throws CodecException {
		try {
			parser.parse(new InputSource(new ByteArrayInputStream(data)));
			checkBase64Encoding();
			return msg;

		} catch (IOException iexc) {
			throw new CodecException("While decoding got ", iexc);
		} catch (SAXException sexc) {
			throw new CodecException("While decoding got ", sexc);
		}
	}

	private static String escape(String s) {
		// Make the stringBuffer a little larger than strictly
		// necessary in case we need to insert any additional
		// characters. (If our size estimate is wrong, the
		// StringBuffer will automatically grow as needed).
		StringBuffer result = new StringBuffer(s.length() + 20);
		for (int i = 0;i < s.length();i++) {
			if (s.charAt(i) == '"') {
				result.append("\\\"");
			}
			else {
				result.append(s.charAt(i));
			}
		}
		return result.toString();
	}

	/**
	 * if there was an automatical Base64 encoding, then it performs automatic
	 * decoding.
	 **/
	private void checkBase64Encoding() {

		// hack - this has to be fixed so the : handling is transparent to the user
		String encoding = msg.getUserDefinedParameter(":" + BASE64ENCODING_KEY);

		if (CaseInsensitiveString.equalsIgnoreCase(BASE64ENCODING_VALUE, encoding)) {
			try { // decode Base64
				String content = msg.getContent();
				if ((content != null) && (content.length() > 0)) {
					msg.setByteSequenceContent(Base64.getDecoder().decode(content.getBytes("US-ASCII")));
					msg.removeUserDefinedParameter(BASE64ENCODING_KEY); // reset the slot value for encoding
				}
			} catch (java.lang.StringIndexOutOfBoundsException e) {
				e.printStackTrace();
			} catch (java.lang.NullPointerException e2) {
				e2.printStackTrace();
			} catch (java.lang.NoClassDefFoundError jlncdfe) {
				System.err.println("\t\t===== E R R O R !!! =======\n");
				System.err.println("Missing support for Base64 conversions");
				System.err.println("Please refer to the documentation for details.");
				System.err.println("=============================================\n\n");
				try {
					Thread.currentThread().sleep(3000);
				} catch (InterruptedException ie) {
				}
			} catch (java.io.UnsupportedEncodingException e3) {
				e3.printStackTrace();
			}
		} // end of if CaseInsensitiveString
	}

	/**
	 * Query the name of the message representation handled by this
	 * <code>Codec</code> object. The FIPA standard representations have a name
	 * starting with <code><b>"fipa.acl.rep."</b></code>.
	 * 
	 * @return The name of the handled ACL message representation.
	 */
	public String getName() {
		return ACL_REPRESENTATION_NAME;
	}

	/*
	 * public static void main1( String args[] ) { try {
	 * jade.lang.acl.StringACLCodec sp=new jade.lang.acl.StringACLCodec();
	 * ACLMessage acl=sp.
	 * decode("(QUERY-REF :sender  ( agent-identifier  :name da0@beta.lausanne.agentcities.net :addresses (sequence http://srv02.lausanne.agentcities.net:8080/acc ))  :receiver  (set ( agent-identifier  :name acl_ping@dilbert.broadcom.ie :addresses (sequence http://192.107.110.39 )) )  :language  PlainText)"
	 * .getBytes());
	 * 
	 * acl.setByteSequenceContent("aaaaa".getBytes());
	 * 
	 * XMLACLCodec xp=new XMLACLCodec();
	 * 
	 * System.out.println("finally:\n"+new String(xp.encode(acl)));
	 * 
	 * 
	 * } catch( Exception exc ) { exc.printStackTrace(); } }
	 * 
	 * public static void main( String args[] ) { if( args.length <= 0 ) {
	 * System.out.println("usage: XMLACLCodec <acl file>"); System.exit(-1); }
	 * 
	 * try {
	 * 
	 * java.io.BufferedReader r=new BufferedReader(new FileReader(args[0]));
	 * 
	 * StringBuffer sb=new StringBuffer(); String tmp;
	 * 
	 * while( (tmp=r.readLine()) != null ) { sb.append(tmp); }
	 * 
	 * r.close();
	 * 
	 * System.out.println("got:\n"+sb.toString());
	 * 
	 * XMLACLCodec codec=new XMLACLCodec();
	 * 
	 * ACLMessage msg=codec.decode(sb.toString().getBytes());
	 * 
	 * System.out.println("msg:\n"+msg);
	 * 
	 * System.out.println("content:\n"+new String(msg.getByteSequenceContent()));
	 * 
	 * System.out.println("xml:\n"+new String(codec.encode(msg)));
	 * 
	 * } catch( Exception exc ){ exc.printStackTrace(); } }
	 */

}
