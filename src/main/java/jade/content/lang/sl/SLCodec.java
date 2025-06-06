/**
 * ***************************************************************
 * JADE - Java Agent DEvelopment Framework is a framework to develop
 * multi-agent systems in compliance with the FIPA specifications.
 * Copyright (C) 2000 CSELT S.p.A.
 * 
 * GNU Lesser General Public License
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation,
 * version 2.1 of the License.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330,
 * Boston, MA  02111-1307, USA.
 * **************************************************************
 */
package jade.content.lang.sl;

import java.io.BufferedReader; // only for debugging purposes in the main
import java.io.InputStreamReader; // only for debugging purposes in the main
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.util.Base64;
import java.util.Date;
import java.util.Iterator;

import jade.content.abs.AbsAggregate;
import jade.content.abs.AbsConcept;
import jade.content.abs.AbsContentElement;
import jade.content.abs.AbsContentElementList;
import jade.content.abs.AbsIRE;
import jade.content.abs.AbsObject;
import jade.content.abs.AbsPredicate;
import jade.content.abs.AbsPrimitive;
import jade.content.abs.AbsReference;
import jade.content.abs.AbsTerm;
import jade.content.abs.AbsVariable;
import jade.content.lang.StringCodec;
import jade.content.onto.Ontology;
import jade.content.onto.exception.OntologyException;
import jade.content.schema.ObjectSchema;
import jade.core.CaseInsensitiveString;
import jade.domain.FIPANames;
import jade.lang.acl.ISO8601;

/**
 * The codec class for the <b><i>FIPA-SL</i>n</b> languages. This class
 * implements the <code>Codec</code> interface and allows converting back and
 * forth between strings and frames, according to the SL grammar. By default the
 * class implements full SL grammar, otherwise the proper value must be used in
 * the constructor.
 * 
 * @author Fabio Bellifemine - TILAB
 * @author Nicolas Lhuillier - Motorola (added support for byte[] primitive)
 * @author Moreno LAGO
 * @version $Date: 2017-11-09 09:25:16 +0100 (gio, 09 nov 2017) $ $Revision: 6832 $
 */
public class SLCodec extends StringCodec {

	public static final String PRESERVE_JAVA_TYPES = "SL-preserve-java-types";

	private transient SLParser parser;
	private transient ExtendedSLParser extendedParser;
	private SL0Ontology slOnto; // ontology of the content language
	private Ontology domainOnto; // application ontology
	/** This is the StringBuffer used by the encode method **/
	private transient StringBuilder buffer;
	/**
	 * This variable is true, when meta symbols are allowed (metas are a
	 * semantics-specific extension to the SL Grammar)
	 **/
	private boolean metaAllowed = true; // FIXME set/unset this variable to do

	private boolean preserveJavaTypes;

	/**
	 * Construct a Codec object for the full SL-language (FIPA-SL).
	 */
	public SLCodec() {
		this(3, readPreserveJavaTypesProperty());
	}

	/**
	 * Create an SLCodec for the full SL-language (FIPA-SL) specifying whether or
	 * not java primitive types (long, int, float, double) must be preserved. This
	 * is achieved by encoding long values as <numeric-value>L and float values as
	 * <numeric-valueF. It should be noticed that this encoding is NOT FIPA SL
	 * standard
	 * 
	 * @param preserveJavaTypes Indicates whether or not java primitive types must
	 *                          be preserved
	 */
	public SLCodec(boolean preserveJavaTypes) {
		this(3, preserveJavaTypes);
	}

	/**
	 * Construct a Codec object for the given profile of SL-language.
	 * 
	 * @param slType specify 0 for FIPA-SL0, 1 for FIPA-SL1, 2 for FIPA-SL2, any
	 *               other value can be used for full FIPA-SL
	 */
	public SLCodec(int slType) {
		this(slType, readPreserveJavaTypesProperty());
	}

	/**
	 * Create an SLCodec for the given profile of SL-language specifying whether or
	 * not java primitive types (long, int, float, double) must be preserved.
	 * 
	 * @param slType            specify 0 for FIPA-SL0, 1 for FIPA-SL1, 2 for
	 *                          FIPA-SL2, any other value can be used for full
	 *                          FIPA-SL
	 * @param preserveJavaTypes Indicates whether or not java primitive types must
	 *                          be preserved
	 */
	public SLCodec(int slType, boolean preserveJavaTypes) {
		super(slType == 0 ? FIPANames.ContentLanguage.FIPA_SL0
				: (slType == 1 ? FIPANames.ContentLanguage.FIPA_SL1
						: (slType == 2 ? FIPANames.ContentLanguage.FIPA_SL2 : FIPANames.ContentLanguage.FIPA_SL)));
		if ((slType < 0) || (slType > 2)) { // if outside range, set to full SL
			slType = 3;
		}
		slOnto = (SL0Ontology) (slType == 0 ? SL0Ontology.getInstance()
				: (slType == 1 ? SL1Ontology.getInstance()
						: (slType == 2 ? SL2Ontology.getInstance() : SLOntology.getInstance())));
		this.preserveJavaTypes = preserveJavaTypes;
		initParser();
	}

	private static boolean readPreserveJavaTypesProperty() {
		String strPreserveJavaTypes = System.getProperty(PRESERVE_JAVA_TYPES);
		return "true".equals(strPreserveJavaTypes);
	}

	private void initParser() {
		int slType = jade.domain.FIPANames.ContentLanguage.FIPA_SL0.equals(getName()) ? 0
				: jade.domain.FIPANames.ContentLanguage.FIPA_SL2.equals(getName()) ? 1
						: jade.domain.FIPANames.ContentLanguage.FIPA_SL2.equals(getName()) ? 2 : 3;

		if (preserveJavaTypes) {
			extendedParser = new ExtendedSLParser(new StringReader(""));
			extendedParser.setSLType(slType);
		} else {
			parser = new SLParser(new StringReader(""));
			parser.setSLType(slType);
		}
	}

	public boolean getPreserveJavaTypes() {
		return preserveJavaTypes;
	}

	/**
	 * Encodes a content into a String.
	 * 
	 * @param content the content as an abstract descriptor.
	 * @return the content as a String.
	 * @throws CodecException
	 */
	public String encode(AbsContentElement content) throws CodecException {
		return encode(null, content);
	}

	/**
	 * Encodes a content into a String.
	 * 
	 * @param ontology the ontology
	 * @param content  the content as an abstract descriptor.
	 * @return the content as a String.
	 * @throws CodecException
	 */
	public synchronized String encode(Ontology ontology, AbsContentElement content) throws CodecException {
		try {
			domainOnto = ontology;
			buffer = new StringBuilder("(");
			if (content instanceof AbsContentElementList list) {
				for (Iterator i = list.iterator(); i.hasNext();) {
					AbsObject o = (AbsObject) i.next();
					encodeAndAppend(o);
					buffer.append(' ');
				}
			}
			else {
				encodeAndAppend(content);
			}
			buffer.append(')');
			return buffer.toString();
		} finally {
			buffer = null; // frees the memory
		}
	}

	/**
	 * Encode a string, taking care of quoting separated words and escaping strings,
	 * if necessary. And append it to the buffer.
	 **/
	private void encodeAndAppend(String val) {
		// if the slotName is a String of words then quote it. If it is a meta (i.e.
		// startsWith "??") do not quote it.
		String out = SimpleSLTokenizer.isAWord(val) || (metaAllowed && val.startsWith("??")) ? val
				: SimpleSLTokenizer.quoteString(val);
		buffer.append(out);
	}

	/** Encode the passed Abstract Predicate and append its encoding to buffer **/
	private void encodeAndAppend(AbsPredicate val) throws CodecException {
		String propositionSymbol = val.getTypeName();
		if (val.getCount() > 0) { // predicate with arguments
			String[] slotNames = getSlotNames(val);
			buffer.append('(');
			if (slOnto.isUnaryLogicalOp(propositionSymbol)) {
				// Unary logical operator of the SL language (NOT)
				buffer.append(propositionSymbol);
				buffer.append(' ');
				try {
					encodeAndAppend((AbsPredicate) val.getAbsObject(slotNames[0]));
				} catch (RuntimeException e) {
					throw new CodecException("A UnaryLogicalOp requires a formula argument", e);
				}
			} else if (slOnto.isBinaryLogicalOp(propositionSymbol)) {
				// Bynary logical operator of the SL language (AND, OR)
				buffer.append(propositionSymbol);
				buffer.append(' ');
				try {
					encodeAndAppend((AbsPredicate) val.getAbsObject(slotNames[0]));
					buffer.append(' ');
					encodeAndAppend((AbsPredicate) val.getAbsObject(slotNames[1]));
				} catch (RuntimeException e) {
					throw new CodecException("A BinaryLogicalOp requires 2 formula arguments", e);
				}
			} else if (slOnto.isQuantifier(propositionSymbol)) {
				// Quantifier operator of the SL language (EXISTS, FORALL)
				buffer.append(propositionSymbol);
				buffer.append(' ');
				try {
					encodeAndAppend((AbsVariable) val.getAbsObject(slotNames[0]));
					buffer.append(' ');
					encodeAndAppend((AbsPredicate) val.getAbsObject(slotNames[1]));
				} catch (RuntimeException e) {
					throw new CodecException("A Quantifier requires a variable and a formula arguments", e);
				}
			} else if (slOnto.isConditionedQuantifier(propositionSymbol)) {
				// Conditioned Quantifier operator of the extended-SL language (FOREACH)
				buffer.append(propositionSymbol);
				buffer.append(' ');
				try {
					encodeAndAppend((AbsVariable) val.getAbsObject(slotNames[0]));
					buffer.append(' ');
					encodeAndAppend((AbsPredicate) val.getAbsObject(slotNames[1]));
					buffer.append(' ');
					encodeAndAppend((AbsPredicate) val.getAbsObject(slotNames[2]));
				} catch (RuntimeException e) {
					throw new CodecException("A Quantifier requires a variable and a formula arguments", e);
				}
			} else if (slOnto.isModalOp(propositionSymbol)) {
				// Modal operator of the SL language (B, I, U, PG)
				buffer.append(propositionSymbol);
				buffer.append(' ');
				try {
					encodeAndAppend((AbsTerm) val.getAbsObject(slotNames[0]));
					buffer.append(' ');
					encodeAndAppend((AbsPredicate) val.getAbsObject(slotNames[1]));
				} catch (RuntimeException e) {
					throw new CodecException("A ModalOp requires a term and a formula arguments", e);
				}
			} else if (slOnto.isActionOp(propositionSymbol)) {
				// Action operator of the SL language (DONE, FEASIBLE)
				buffer.append(propositionSymbol);
				buffer.append(' ');
				try {
					encodeAndAppend((AbsTerm) val.getAbsObject(slotNames[0])); // FIXME check it is an action expression
					AbsPredicate ap = (AbsPredicate) val.getAbsObject(slotNames[1]);
					if (ap != null) { // Second argument is optional
						buffer.append(' ');
						encodeAndAppend(ap);
					}
				} catch (RuntimeException e) {
					throw new CodecException(
							"An ActionOp requires an actionexpression and (optionally) a formula arguments", e);
				}
			} else if (slOnto.isBinaryTermOp(propositionSymbol)) {
				// Binary term operator of the SL language (RESULT, =)
				buffer.append(propositionSymbol);
				buffer.append(' ');
				try {
					encodeAndAppend((AbsTerm) val.getAbsObject(slotNames[0]));
					buffer.append(' ');
					encodeAndAppend((AbsTerm) val.getAbsObject(slotNames[1]));
				} catch (RuntimeException e) {
					throw new CodecException("A BinaryTermOp requires 2 term arguments", e);
				}
			} else {
				encodeAndAppend(propositionSymbol);
				// Predicate in the ontology
				try {
					encodeSlotsByOrder(val, slotNames);
				} catch (RuntimeException e) {
					throw new CodecException("SL allows predicates with term arguments only", e);
				}
			}
			buffer.append(')');
		}
		else {
			// Proposition
			encodeAndAppend(propositionSymbol);
		}
	}

	private void encodeAndAppend(AbsIRE val) throws CodecException {
		buffer.append('(');
		encodeAndAppend(val.getTypeName());
		buffer.append(' ');
		encodeAndAppend(val.getTerm());
		buffer.append(' ');
		encodeAndAppend(val.getProposition());
		buffer.append(')');
	}

	private void encodeAndAppend(AbsVariable val) throws CodecException {
		String var = val.getName();
		if (var.charAt(0) == '?') {
			encodeAndAppend(var);
		} else {
			buffer.append('?');
			encodeAndAppend(var);
		}
	}

	private void encodeAndAppend(AbsReference val) throws CodecException {
		String objectType = val.getType();
		String name = val.getName();
		buffer.append(AbsReference.asString(objectType, name));
	}

	private void encodeAndAppend(AbsConcept val) throws CodecException {
		String functionSymbol = val.getTypeName();
		buffer.append('(');
		String[] slotNames = getSlotNames(val);
		if (slOnto.isSLFunctionWithoutSlotNames(functionSymbol)) {
			// A Functional operator of the SL language (ACTION, + ...)
			// The form is: functionSymbol Term*
			buffer.append(functionSymbol);
			try {
				encodeSlotsByOrder(val, slotNames);
			} catch (RuntimeException e) {
				throw new CodecException("A FunctionalOperator requires 1 or 2 Term arguments", e);
			}
		} else {
			// A generic term in the ontology. The form can be both
			// functionSymbol Parameter* or functionSymbol Term*. Get the
			// preferred way from the ontology.
			encodeAndAppend(functionSymbol);
			try {
				// FIXME: To improve performances the two operations that imply
				// retrieving a schema from the ontology (getting slot names and
				// getting the preferred encoding type) should be carried out at
				// the same time.
				if (getEncodingByOrder(val)) {
					encodeSlotsByOrder(val, slotNames);
				} else {
					encodeSlotsByName(val, slotNames);
				}
			} catch (RuntimeException e) {
				throw new CodecException("A FunctionalTerm requires Terms arguments", e);
			}
		}

		buffer.append(')');
	}

	private void encodeAndAppend(AbsAggregate val) throws CodecException {
		buffer.append('(');
		encodeAndAppend(val.getTypeName());
		for (Iterator i = val.iterator(); i.hasNext();) {
			buffer.append(' ');
			encodeAndAppend((AbsObject) i.next());
		}
		buffer.append(')');
	}

	private void encodeAndAppend(AbsPrimitive val) throws CodecException {
		Object v = val.getObject();
		if (v instanceof Date date) {
			buffer.append(ISO8601.toString(date));
		}
		else if (v instanceof Number) {
			buffer.append(v.toString());
			if (preserveJavaTypes) {
				if (v instanceof Long) {
					buffer.append('L');
				}
				else if (v instanceof Float) {
					buffer.append('F');
				}
			}
		}
		else if (v instanceof byte[] b) {
			b = Base64.getEncoder().encode(b);

			buffer.append('#');
			buffer.append(b.length);
			buffer.append('"');
			try {
				buffer.append(new String(b, "US-ASCII"));
			} catch (UnsupportedEncodingException uee) {
				throw new CodecException("Error encoding byte-array to Base64 US-ASCII", uee);
			}
		}
		else if (v instanceof Boolean) {
			buffer.append(v.toString());
		}
		else {
			String vs = v.toString();
			if ((CaseInsensitiveString.equalsIgnoreCase("true", vs))
				|| (CaseInsensitiveString.equalsIgnoreCase("false", vs))) {
				// quote true and false to avoid confusion with booleans
				buffer.append('"');
				buffer.append(vs);
				buffer.append('"');
			}
			else {
				encodeAndAppend(vs);
			}
		}
	}

	private void encodeAndAppend(AbsObject val) throws CodecException {
		if (val instanceof AbsPrimitive primitive) {
			encodeAndAppend(primitive);
		}
		else if (val instanceof AbsPredicate predicate) {
			encodeAndAppend(predicate);
		}
		else if (val instanceof AbsIRE rE) {
			encodeAndAppend(rE);
		}
		else if (val instanceof AbsVariable variable) {
			encodeAndAppend(variable);
			// if (val instanceof AbsAgentAction) return toString( (AbsAgentAction)val);
		}
		else if (val instanceof AbsAggregate aggregate) {
			encodeAndAppend(aggregate);
		}
		else if (val instanceof AbsConcept concept) {
			encodeAndAppend(concept);
		}
		else if (val instanceof AbsReference reference) {
			encodeAndAppend(reference);
		}
		else {
			throw new CodecException("SLCodec cannot encode this object " + val);
		}
	}

	/**
	 * Decodes the content to an abstract description.
	 * 
	 * @param content the content as a String.
	 * @return the content as an abstract description.
	 * @throws CodecException
	 */
	public AbsContentElement decode(String content) throws CodecException {
		return decode(null, content);
	}

	/**
	 * Decodes the content to an abstract description.
	 * 
	 * @param ontology the ontology.
	 * @param content  the content as a String.
	 * @return the content as an abstract description.
	 * @throws CodecException
	 */
	public synchronized AbsContentElement decode(Ontology ontology, String content) throws CodecException {
		if (ontology == null) {
			ontology = slOnto;
		}
		try {
			AbsContentElementList tuple = null;
			if (preserveJavaTypes) {
				extendedParser.reinit(ontology, content);
				tuple = extendedParser.Content();
			} else {
				parser.reinit(ontology, content);
				tuple = parser.Content();
			}
			if (tuple.size() > 1) {
				return tuple;
			}
			else { // if there is a single ContentExpression than return just it, not the tuple
				return tuple.get(0);
			}
		} catch (Throwable e) { // both ParseException and TokenMgrError
			throw new CodecException("Parse exception", e);
		}
	}

	/**
	 * Decodes the content to an abstract description, where the content is known to
	 * be a Term.
	 * 
	 * @param ontology the ontology.
	 * @param cterm    the term as a String.
	 * @return the content as an abstract description.
	 * @throws CodecException
	 * @since JADE 3.4
	 */
	public synchronized AbsTerm decodeTerm(Ontology ontology, String term) throws CodecException {
		if (ontology == null) {
			ontology = slOnto;
		}
		try {
			if (preserveJavaTypes) {
				extendedParser.reinit(ontology, term);
				return extendedParser.Term();
			} else {
				parser.reinit(ontology, term);
				return parser.Term();
			}
		} catch (Throwable e) { // both ParseException and TokenMgrError
			throw new CodecException("Parse exception", e);
		}
	}

	/**
	 * Encodes the content into a String, where the content is known to be a Term.
	 * 
	 * @param ontology the ontology.
	 * @param term     the termt as an abstract descriptor
	 * @return the content as a String
	 * @throws CodecException
	 * @since JADE 3.4
	 */
	public synchronized String encodeTerm(Ontology ontology, AbsTerm term) throws CodecException {
		try {
			domainOnto = ontology;
			buffer = new StringBuilder();
			encodeAndAppend(term);
			return buffer.toString();
		} finally {
			buffer = null; // frees the memory
		}
	}

	/**
	 * Decodes the content to an abstract description, where the content is known to
	 * be a Well-formed Formula
	 * 
	 * @param ontology the ontology.
	 * @param formula  the content as a String.
	 * @return the content as an abstract description.
	 * @throws CodecException
	 * @since JADE 3.4
	 */
	public synchronized AbsPredicate decodeFormula(Ontology ontology, String formula) throws CodecException {
		if (ontology == null) {
			ontology = slOnto;
		}
		try {
			if (preserveJavaTypes) {
				extendedParser.reinit(ontology, formula);
				return extendedParser.Wff();
			} else {
				parser.reinit(ontology, formula);
				return parser.Wff();
			}
		} catch (Throwable e) { // both ParseException and TokenMgrError
			throw new CodecException("Parse exception", e);
		}
	}

	/**
	 * Encodes the content into a String, where the content is known to be a
	 * Well-formed Formula
	 * 
	 * @param ontology the ontology.
	 * @param formula  the formula as an abstract descriptor
	 * @return the content as a String
	 * @throws CodecException
	 * @since JADE 3.4
	 */
	public synchronized String encodeFormula(Ontology ontology, AbsPredicate formula) throws CodecException {
		try {
			domainOnto = ontology;
			buffer = new StringBuilder();
			encodeAndAppend(formula);
			return buffer.toString();
		} finally {
			buffer = null; // frees the memory
		}
	}

	public static void main(String[] args) {
		SLCodec codec = null;
		if (args.length > 0) {
			// Use a standard SL Codec of the indicated level (0, 1, 2)
			try {
				codec = new SLCodec(Integer.parseInt(args[0]));
			} catch (Exception e) {
				System.out.println(
						"usage: SLCodec SLLevel [ContentType]\n where SLLevel can be 0 for SL0, 1 for SL1, 2 for SL2, 3 or more for full SL \n and where ContentType is a char representing the type of content to be parsed: C for a contentexpression (default), T for a term, F for a formula");
				System.exit(0);
			}
		} else {
			// Use an extended SL codec
			codec = new SLCodec(true);
		}

		try {
			BufferedReader buff = new BufferedReader(new InputStreamReader(System.in));
			while (true) {
				char contentType = 'F';
				System.out.println("Insert an SL expression (either Wff or T:Term):");
				String str = buff.readLine();
				if (str.startsWith("T:")) {
					contentType = 'T';
					str = str.substring(2);
				}
				System.out.println("\n\n");
				try {
					if (contentType == 'F') {
						AbsPredicate result = codec.decodeFormula(null, str);
						System.out.println("ABS representation:");
						System.out.println(result);
						System.out.println("Encoded ABS representation:");
						System.out.println(codec.encodeFormula(null, result));
						System.out.println("\n");
					} else if (contentType == 'T') {
						AbsTerm result = codec.decodeTerm(null, str);
						System.out.println("ABS representation:");
						System.out.println(result);
						System.out.println("Encoded ABS representation:");
						System.out.println(codec.encodeTerm(null, result));
						System.out.println("\n");
					} else {
						AbsContentElement result = codec.decode(str);
						System.out.println("DUMP OF THE DECODE OUTPUT (just for debugging):");
						System.out.println(result);
						System.out.println("\n\n");
						System.out.println("AFTER ENCODE:");
						System.out.println(codec.encode(result));
						System.out.println("\n\n");
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} catch (Exception pe) {
			pe.printStackTrace();
			// System.exit(0);
		}
	}

	/**
	 * @return the ontology containing the schemas of the operator defined in this
	 *         language
	 */
	public Ontology getInnerOntology() {
		return slOnto;
	}

	private String[] getSlotNames(AbsObject abs) throws CodecException {
		String[] slotNames = null;
		String type = abs.getTypeName();
		if (domainOnto != null && abs.getTypeName() != SL0Vocabulary.AID) {
			// If an ontology is specified, get the slot names from it
			// (and not directly from the abstract descriptor val) to preserve the order
			//
			// NOTA: Per supportare la gestione degli UserDefinedSlot di AID e' necessario
			// recuperare i nomi degli slot non dall'ontologia ma direttamente dall'abstract
			// decriptor
			// questo in quanto gli UserDefinedSlot secondo lo standard FIPA sono gestiti
			// come
			// slot con il formato :X-<key> <value> che non posso essere descritti
			// ontologicamente
			try {
				ObjectSchema s = domainOnto.getSchema(type);
				if (s == null) {
					throw new CodecException("No schema found for symbol " + type);
				}
				slotNames = s.getNames();
			} catch (OntologyException oe) {
				throw new CodecException("Error getting schema for symbol " + type, oe);
			}
		} else {
			slotNames = abs.getNames();
		}
		return slotNames;
	}

	private boolean getEncodingByOrder(AbsObject abs) throws CodecException {
		if (domainOnto != null) {
			String type = abs.getTypeName();
			try {
				ObjectSchema s = domainOnto.getSchema(type);
				return s.getEncodingByOrder();
			} catch (Exception e) {
				// Just ignore it
			}
		}
		return false;
	}

	/**
	 * Encode the slots of an abstract descriptor by order, i.e. without writing the
	 * slot names. Also take into account that, in order to ensure a correct
	 * parsing, empty slots can only occur at the end. Append this encoded string to
	 * buffer.
	 */
	private void encodeSlotsByOrder(AbsObject val, String[] slotNames) throws CodecException {
		boolean lastSlotEmpty = false;
		for (int i = 0; i < slotNames.length; i++) {
			AbsTerm t = (AbsTerm) val.getAbsObject(slotNames[i]);
			if (t != null) {
				if (lastSlotEmpty) {
					throw new CodecException(
							"Non-empty slot " + slotNames[i] + " follows empty slot " + slotNames[i - 1]);
				}
				buffer.append(' ');
				encodeAndAppend(t);
			} else {
				lastSlotEmpty = true;
			}
		}
	}

	/**
	 * Encode the slots of an abstract descriptor by name, i.e. writing for each
	 * non-empty slot the slot name followed by the slot value. Append this encoded
	 * string to buffer.
	 */
	private void encodeSlotsByName(AbsObject val, String[] slotNames) throws CodecException {
		for (int i = 0; i < slotNames.length; i++) {
			AbsTerm t = (AbsTerm) val.getAbsObject(slotNames[i]);
			if (t != null) {
				// if this isn't un unnamed slot, then encode it otherwise just encode its value
				if (!slotNames[i].startsWith(this.UNNAMEDPREFIX)) {
					buffer.append(" :");
					encodeAndAppend(slotNames[i]);
				}
				buffer.append(' ');
				encodeAndAppend(t);
			}
		}
	}

	/**
	 * Restore parser after deserialization. <br>
	 * The readResolve method is called when ObjectInputStream has read an object
	 * from the stream and is preparing to return it to the caller. <br>
	 * The readResolve method is not invoked on the object until the object is fully
	 * constructed.
	 */
	protected Object readResolve() throws java.io.ObjectStreamException {
		initParser();
		return this;
	}

}
