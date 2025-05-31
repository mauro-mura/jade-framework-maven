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
package jade.content.lang.leap;

import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import jade.content.abs.AbsAggregate;
import jade.content.abs.AbsContentElement;
import jade.content.abs.AbsContentElementList;
import jade.content.abs.AbsHelper;
import jade.content.abs.AbsObject;
import jade.content.abs.AbsPrimitive;
import jade.content.abs.AbsTerm;
import jade.content.lang.ByteArrayCodec;
import jade.content.onto.Ontology;
import jade.content.schema.ObjectSchema;

/**
 * Content language codec for the LEAP language
 * 
 * @author Federico Bergenti - Universita` di Parma
 * @author Moreno LAGO
 */
public class LEAPCodec extends ByteArrayCodec {

	@Serial
	private static final long serialVersionUID = -8110787384183685969L;

	public static final String NAME = "LEAP";

	private transient ByteArrayOutputStream outBuffer = new ByteArrayOutputStream();
	private transient DataOutputStream outStream = new DataOutputStream(outBuffer);
	private transient List<String> stringReferences = new ArrayList<>();

	private void readObject(java.io.ObjectInputStream oin) throws java.io.IOException, ClassNotFoundException {
		oin.defaultReadObject();
		outBuffer = new ByteArrayOutputStream();
		outStream = new DataOutputStream(outBuffer);
		stringReferences = new ArrayList<>();
	}

	// Types
	@SuppressWarnings("unused")
	private static final byte PRIMITIVE = 0;
	private static final byte AGGREGATE = 1;
	private static final byte CONTENT_ELEMENT_LIST = 2;
	private static final byte OBJECT = 3;

	// Markers for structured types
	private static final byte ELEMENT = 4;
	private static final byte END = 5;

	// Primitive types
	private static final byte STRING = 6;
	private static final byte BOOLEAN = 7;
	private static final byte INTEGER = 8;
	private static final byte LONG = 9;
	private static final byte FLOAT = 10;
	private static final byte DOUBLE = 11;
	private static final byte DATE = 12;
	private static final byte BYTE_SEQUENCE = 13;
	private static final byte BIG_STRING = 14;

	// Modifiers
	private static final byte MODIFIER = (byte) 0x10; // Only bit five set to 1
	private static final byte UNMODIFIER = (byte) 0xEF; // Only bit five cleared to 1

	/*
	 * LEAP Language operators public static final String INSTANCEOF = "INSTANCEOF";
	 * public static final String INSTANCEOF_ENTITY = "entity"; public static final
	 * String INSTANCEOF_TYPE = "type";
	 * 
	 * public static final String IOTA = "IOTA";
	 */

	/**
	 * Construct a LEAPCodec object i.e. a Codec for the LEAP language
	 */
	public LEAPCodec() {
		super(NAME);
	}

	/**
	 * Encodes an abstract descriptor holding a content element into a byte array.
	 * 
	 * @param content the content as an abstract descriptor.
	 * @return the content as a byte array.
	 * @throws CodecException
	 */
	public synchronized byte[] encode(AbsContentElement content) throws CodecException {
		try {
			outBuffer.reset();
			stringReferences.clear();
			write(outStream, content);

			return outBuffer.toByteArray();
		} catch (Throwable t) {
			throw new CodecException("Error encoding content", t);
		}
	}

	/**
	 * Encodes a content into a byte array.
	 * 
	 * @param ontology the ontology
	 * @param content  the content as an abstract descriptor.
	 * @return the content as a byte array.
	 * @throws CodecException
	 */
	public byte[] encode(Ontology ontology, AbsContentElement content) throws CodecException {
		return encode(content);
	}

	/**
	 * Decodes the content to an abstract descriptor.
	 * 
	 * @param content the content as a byte array.
	 * @return the content as an abstract description.
	 * @throws CodecException
	 */
	public AbsContentElement decode(byte[] content) throws CodecException {
		throw new CodecException("Not supported");
	}

	/**
	 * Decodes the content to an abstract description.
	 * 
	 * @param ontology the ontology.
	 * @param content  the content as a byte array.
	 * @return the content as an abstract description.
	 * @throws CodecException
	 */
	public synchronized AbsContentElement decode(Ontology ontology, byte[] content) throws CodecException {
		if (content.length == 0) {
			return null;
		}
		try {
			ByteArrayInputStream inpBuffer = new ByteArrayInputStream(content);
			DataInputStream inpStream = new DataInputStream(inpBuffer);

			stringReferences.clear();
			AbsObject obj = read(inpStream, ontology);
			inpStream.close();
			return (AbsContentElement) obj;
		} catch (Throwable t) {
			throw new CodecException("Error decoding content", t);
		}
	}

	private void write(DataOutputStream stream, AbsObject abs) throws Throwable {
		// PRIMITIVE
		if (abs instanceof AbsPrimitive primitive) {
			// stream.writeByte(PRIMITIVE);

			Object obj = primitive.getObject();

			if (obj instanceof String s) {
				if (s.length() >= 65535) {
					writeBigString(stream, BIG_STRING, s);
				} else {
					writeString(stream, STRING, s);
				}
			} else if (obj instanceof Boolean boolean1) {
				stream.writeByte(BOOLEAN);
				stream.writeBoolean(boolean1.booleanValue());
			} else if (obj instanceof Integer integer) {
				stream.writeByte(INTEGER);
				stream.writeInt(integer.intValue());
			} else if (obj instanceof Long long1) {
				stream.writeByte(LONG);
				stream.writeLong(long1.longValue());
			}

			else if (obj instanceof Float float1) {
				stream.writeByte(FLOAT);
				stream.writeFloat(float1.floatValue());
			} else if (obj instanceof Double double1) {
				stream.writeByte(DOUBLE);
				stream.writeDouble(double1.doubleValue());
			}

			else if (obj instanceof Date date) {
				stream.writeByte(DATE);
				stream.writeLong(date.getTime());
			} else if (obj instanceof byte[] b) {
				stream.writeByte(BYTE_SEQUENCE);
				stream.writeInt(b.length);
				stream.write(b, 0, b.length);
			}

			return;
		}

		// AGGREGATE
		if (abs instanceof AbsAggregate aggregate) {
			writeString(stream, AGGREGATE, abs.getTypeName());

			for (int i = 0; i < aggregate.size(); i++) {
				stream.writeByte(ELEMENT);
				write(stream, aggregate.get(i));
			}

			stream.writeByte(END);

			return;
		}

		// CONTENT_ELEMENT_LIST
		if (abs instanceof AbsContentElementList acel) {
			stream.writeByte(CONTENT_ELEMENT_LIST);

			for (int i = 0; i < acel.size(); i++) {
				stream.writeByte(ELEMENT);
				write(stream, acel.get(i));
			}

			stream.writeByte(END);

			return;
		}

		// If we get here it must be a complex OBJECT
		writeString(stream, OBJECT, abs.getTypeName());

		String[] names = abs.getNames();

		for (int i = 0; i < abs.getCount(); i++) {
			writeString(stream, ELEMENT, names[i]);
			AbsObject child = abs.getAbsObject(names[i]);
			write(stream, child);
		}

		stream.writeByte(END);
	}

	private AbsObject read(DataInputStream stream, Ontology ontology) throws Throwable {
		byte type = stream.readByte();

		// PRIMITIVE
		// if (type == PRIMITIVE) {
		// byte primitiveType = stream.readByte();
		// AbsPrimitive abs = null;

		if ((type & UNMODIFIER) == STRING) {
			return AbsPrimitive.wrap(readString(stream, type));
		}
		if ((type & UNMODIFIER) == BIG_STRING) {
			return AbsPrimitive.wrap(readBigString(stream, type));
		}
		if (type == BOOLEAN) {
			boolean value = stream.readBoolean();
			return AbsPrimitive.wrap(value);
		}
		if (type == INTEGER) {
			int value = stream.readInt();
			return AbsPrimitive.wrap(value);
		}
		if (type == LONG) {
			long value = stream.readLong();
			return AbsPrimitive.wrap(value);
		}

		if (type == FLOAT) {
			float value = stream.readFloat();
			return AbsPrimitive.wrap(value);
		}
		if (type == DOUBLE) {
			double value = stream.readDouble();
			return AbsPrimitive.wrap(value);
		}

		if (type == DATE) {
			long value = stream.readLong();
			return AbsPrimitive.wrap(new Date(value));
		}
		if (type == BYTE_SEQUENCE) {
			byte[] value = new byte[stream.readInt()];
			stream.read(value, 0, value.length);
			return AbsPrimitive.wrap(value);
		}

		// return abs;
		// }

		// AGGREGATE
		if ((type & UNMODIFIER) == AGGREGATE) {
			String typeName = readString(stream, type);
			AbsAggregate abs = new AbsAggregate(typeName);
			byte marker = stream.readByte();

			do {
				if (marker == ELEMENT) {
					AbsObject elementValue = read(stream, ontology);

					if (elementValue != null) {
						try {
							abs.add((AbsTerm) elementValue);
						} catch (ClassCastException cce) {
							throw new CodecException("Non term element in aggregate");
						}
					}

					marker = stream.readByte();
				}
			} while (marker != END);

			return abs;
		}

		// CONTENT_ELEMENT_LIST
		if (type == CONTENT_ELEMENT_LIST) {
			AbsContentElementList abs = new AbsContentElementList();
			byte marker = stream.readByte();

			do {
				if (marker == ELEMENT) {
					AbsObject elementValue = read(stream, ontology);

					if (elementValue != null) {
						try {
							abs.add((AbsContentElement) elementValue);
						} catch (ClassCastException cce) {
							throw new CodecException("Non content-element element in content-element-list");
						}
					}

					marker = stream.readByte();
				}
			} while (marker != END);

			return abs;
		}

		// If we get here it must be a complex OBJECT
		String typeName = readString(stream, type);
		// DEBUG System.out.println("Type is "+typeName);
		ObjectSchema schema = ontology.getSchema(typeName);
		// DEBUG System.out.println("Schema is "+schema);
		AbsObject abs = schema.newInstance();

		byte marker = stream.readByte();

		do {
			if ((marker & UNMODIFIER) == ELEMENT) {
				String attributeName = readString(stream, marker);
				AbsObject attributeValue = read(stream, ontology);

				if (attributeValue != null) {
					AbsHelper.setAttribute(abs, attributeName, attributeValue);
				}

				marker = stream.readByte();
			}
		} while (marker != END);

		return abs;
	}

	private final void writeString(DataOutputStream stream, byte tag, String s) throws Throwable {
		int index = stringReferences.indexOf(s);
		if (index >= 0) {
			// Write the tag modified and just put the index
			// System.out.println("String "+s+" already encoded");
			stream.writeByte(tag | MODIFIER);
			stream.writeByte(index);
		} else {
			stream.writeByte(tag);
			stream.writeUTF(s);
			if ((s.length() > 1) && (stringReferences.size() < 256)) {
				stringReferences.add(s);
			}
		}
	}

	// This method is equal to writeString, but is used to encode String whose
	// length is >= 65535. Therefore the string is not encoded using writeUTF().
	private final void writeBigString(DataOutputStream stream, byte tag, String s) throws Throwable {
		int index = stringReferences.indexOf(s);
		if (index >= 0) {
			// Write the tag modified and just put the index
			// System.out.println("String "+s+" already encoded");
			stream.writeByte(tag | MODIFIER);
			stream.writeByte(index);
		} else {
			stream.writeByte(tag);
			byte[] bytes = s.getBytes();
			stream.writeInt(bytes.length);
			stream.write(bytes, 0, bytes.length);
			if ((s.length() > 1) && (stringReferences.size() < 256)) {
				stringReferences.add(s);
			}
		}
	}

	private final String readString(DataInputStream stream, byte tag) throws Throwable {
		String s = null;
		if ((tag & MODIFIER) != 0) {
			int index = stream.readUnsignedByte();
			if (index < stringReferences.size()) {
				s = stringReferences.get(index);
			}
		} else {
			s = stream.readUTF();
			if ((s.length() > 1) && (stringReferences.size() < 256)) {
				stringReferences.add(s);
			}
		}
		return s;
	}

	// This method is equal to readString, but is used to decode String whose
	// length is >= 65535. Therefore the string is not decoded using writeUTF().
	private final String readBigString(DataInputStream stream, byte tag) throws Throwable {
		String s = null;
		if ((tag & MODIFIER) != 0) {
			int index = stream.readUnsignedByte();
			if (index < stringReferences.size()) {
				s = stringReferences.get(index);
			}
		} else {
			byte[] bytes = new byte[stream.readInt()];
			stream.read(bytes, 0, bytes.length);
			s = new String(bytes);
			if ((s.length() > 1) && (stringReferences.size() < 256)) {
				stringReferences.add(s);
			}
		}
		return s;
	}
}
