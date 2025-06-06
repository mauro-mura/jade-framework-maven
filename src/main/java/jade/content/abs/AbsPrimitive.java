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
package jade.content.abs;

import jade.content.onto.*;

import java.io.Serial;
import java.util.Date;

/**
 * An abstract descriptor that can hold a primitive expression.
 * 
 * @author Paola Turci, Federico Bergenti - Universita` di Parma
 * @author Giovanni Caire - TILAB
 * @author Moreno LAGO
 */
public class AbsPrimitive implements AbsTerm {

	@Serial
	private static final long serialVersionUID = -4976477270014235832L;
	private Object value;
	private String typeName;

	/**
	 * Construct an Abstract descriptor to hold a primitive of the proper type (e.g.
	 * String, int, boolean...) and set its value.
	 */
	private AbsPrimitive(String typeName, Object value) {
		this.typeName = typeName;
		this.value = value;
	}

	/**
	 * Construct an Abstract descriptor to hold a primitive of the proper type (e.g.
	 * String, int, boolean...).
	 * 
	 * @param typeName The name of the type of the primitive held by this abstract
	 *                 descriptor.
	 */
	public AbsPrimitive(String typeName) {
		this(typeName, null);
	}

	/**
	 * Create an AbsPrimitive of type <code>BasicOntology.STRING</code> containing a
	 * given <code>String</code> value.
	 */
	public static AbsPrimitive wrap(String value) {
		AbsPrimitive ret = null;
		if (value != null) {
			ret = new AbsPrimitive(BasicOntology.STRING, value);
		}
		return ret;
	}

	/**
	 * Create an AbsPrimitive of type <code>BasicOntology.BOOLEAN</code> containing
	 * a given <code>boolean</code> value.
	 */
	public static AbsPrimitive wrap(boolean value) {
		return new AbsPrimitive(BasicOntology.BOOLEAN, Boolean.valueOf(value));
	}

	/**
	 * Create an AbsPrimitive of type <code>BasicOntology.INTEGER</code> containing
	 * a given <code>int</code> value.
	 */
	public static AbsPrimitive wrap(int value) {
		return new AbsPrimitive(BasicOntology.INTEGER, Integer.valueOf(value));
	}

	/**
	 * Create an AbsPrimitive of type <code>BasicOntology.INTEGER</code> containing
	 * a given <code>long</code> value.
	 */
	public static AbsPrimitive wrap(long value) {
		return new AbsPrimitive(BasicOntology.INTEGER, Long.valueOf(value));
	}

	/**
	 * Create an AbsPrimitive of type <code>BasicOntology.FLOAT</code> containing a
	 * given <code>float</code> value.
	 */
	public static AbsPrimitive wrap(float value) {
		return new AbsPrimitive(BasicOntology.FLOAT, Float.valueOf(value));
	}

	/**
	 * Create an AbsPrimitive of type <code>BasicOntology.FLOAT</code> containing a
	 * given <code>double</code> value.
	 */
	public static AbsPrimitive wrap(double value) {
		return new AbsPrimitive(BasicOntology.FLOAT, Double.valueOf(value));
	}

	/**
	 * Create an AbsPrimitive of type <code>BasicOntology.DATE</code> containing a
	 * given <code>Date</code> value.
	 */
	public static AbsPrimitive wrap(Date value) {
		AbsPrimitive ret = null;
		if (value != null) {
			ret = new AbsPrimitive(BasicOntology.DATE, value);
		}
		return ret;
	}

	/**
	 * Create an AbsPrimitive of type <code>BasicOntology.BYTE_SEQUENCE</code>
	 * containing a given <code>byte[]</code> value.
	 */
	public static AbsPrimitive wrap(byte[] value) {
		AbsPrimitive ret = null;
		if (value != null) {
			ret = new AbsPrimitive(BasicOntology.BYTE_SEQUENCE, value);
		}
		return ret;
	}

	/**
	 * Set the value of this AbsPrimitive to the given String.
	 * 
	 * @param value The new value
	 * @throws IllegalArgumentException If the type of this AbsPrimitive is not
	 *                                  <code>BasicOntology.STRING</code>
	 */
	public void set(String value) {
		if (!BasicOntology.STRING.equals(getTypeName())) {
			throw new IllegalArgumentException("Wrong type");
		}
		this.value = value;
	}

	/**
	 * Set the value of this AbsPrimitive to the given boolean value.
	 * 
	 * @param value The new value
	 * @throws IllegalArgumentException If the type of this AbsPrimitive is not
	 *                                  <code>BasicOntology.BOOLEAN</code>
	 */
	public void set(boolean value) {
		if (!BasicOntology.BOOLEAN.equals(getTypeName())) {
			throw new IllegalArgumentException("Wrong type");
		}
		this.value = Boolean.valueOf(value);
	}

	/**
	 * Set the value of this AbsPrimitive to the given int value.
	 * 
	 * @param value The new value
	 * @throws IllegalArgumentException If the type of this AbsPrimitive is not
	 *                                  <code>BasicOntology.INTEGER</code>
	 */
	public void set(int value) {
		if (!BasicOntology.INTEGER.equals(getTypeName())) {
			throw new IllegalArgumentException("Wrong type");
		}
		this.value = Integer.valueOf(value);
	}

	/**
	 * Set the value of this AbsPrimitive to the given long value.
	 * 
	 * @param value The new value
	 * @throws IllegalArgumentException If the type of this AbsPrimitive is not
	 *                                  <code>BasicOntology.INTEGER</code>
	 */
	public void set(long value) {
		if (!BasicOntology.INTEGER.equals(getTypeName())) {
			throw new IllegalArgumentException("Wrong type");
		}
		this.value = Long.valueOf(value);
	}

	/**
	 * Set the value of this AbsPrimitive to the given float value.
	 * 
	 * @param value The new value
	 * @throws IllegalArgumentException If the type of this AbsPrimitive is not
	 *                                  <code>BasicOntology.FLOAT</code>
	 */
	public void set(float value) {
		if (!BasicOntology.FLOAT.equals(getTypeName())) {
			throw new IllegalArgumentException("Wrong type");
		}
		this.value = Float.valueOf(value);
	}

	/**
	 * Set the value of this AbsPrimitive to the given double value.
	 * 
	 * @param value The new value
	 * @throws IllegalArgumentException If the type of this AbsPrimitive is not
	 *                                  <code>BasicOntology.FLOAT</code>
	 */
	public void set(double value) {
		if (!BasicOntology.FLOAT.equals(getTypeName())) {
			throw new IllegalArgumentException("Wrong type");
		}
		this.value = Double.valueOf(value);
	}

	/**
	 * Set the value of this AbsPrimitive to the given Date value.
	 * 
	 * @param value The new value
	 * @throws IllegalArgumentException If the type of this AbsPrimitive is not
	 *                                  <code>BasicOntology.DATE</code>
	 */
	public void set(Date value) {
		if (!BasicOntology.DATE.equals(getTypeName())) {
			throw new IllegalArgumentException("Wrong type");
		}
		this.value = value;
	}

	/**
	 * Set the value of this AbsPrimitive to the given byte[] value.
	 * 
	 * @param value The new value
	 * @throws IllegalArgumentException If the type of this AbsPrimitive is not
	 *                                  <code>BasicOntology.BYTE_SEQUENCE</code>
	 */
	public void set(byte[] value) {
		if (!BasicOntology.BYTE_SEQUENCE.equals(getTypeName())) {
			throw new IllegalArgumentException("Wrong type");
		}
		this.value = value;
	}

	/**
	 * @return the value of this AbsPrimitive as a String.
	 * @throws ClassCastException If the type of this AbsPrimitive is not
	 *                            <code>BasicOntology.STRING</code>
	 */
	public String getString() {
		return (String) value;
	}

	/**
	 * @return the value of this AbsPrimitive as a boolean.
	 * @throws ClassCastException If the type of this AbsPrimitive is not
	 *                            <code>BasicOntology.BOOLEAN</code>
	 */
	public boolean getBoolean() {
		return ((Boolean) value).booleanValue();
	}

	/**
	 * @return the value of this AbsPrimitive as an int.
	 * @throws ClassCastException If the type of this AbsPrimitive is not
	 *                            <code>BasicOntology.INTEGER</code>
	 */
	public int getInteger() {
		try {
			return ((Integer) value).intValue();
		} catch (ClassCastException cce) {
			// Try as a Long
			return (int) ((Long) value).longValue();
		}
	}

	/**
	 * @return the value of this AbsPrimitive as a long.
	 * @throws ClassCastException If the type of this AbsPrimitive is not
	 *                            <code>BasicOntology.INTEGER</code>
	 */
	public long getLong() {
		try {
			return ((Long) value).longValue();
		} catch (ClassCastException cce) {
			// Try as an Integer
			return (long) ((Integer) value).intValue();
		}
	}

	/**
	 * @return the value of this AbsPrimitive as a float.
	 * @throws ClassCastException If the type of this AbsPrimitive is not
	 *                            <code>BasicOntology.FLOAT</code>
	 */
	public float getFloat() {
		try {
			return ((Float) value).floatValue();
		} catch (ClassCastException cce) {
			// Try as a Double
			return (float) ((Double) value).doubleValue();
		}
	}

	/**
	 * @return the value of this AbsPrimitive as a double.
	 * @throws ClassCastException If the type of this AbsPrimitive is not
	 *                            <code>BasicOntology.FLOAT</code>
	 */
	public double getDouble() {
		try {
			return ((Double) value).doubleValue();
		} catch (ClassCastException cce) {
			// Try as a Float
			return (double) ((Float) value).floatValue();
		}
	}

	/**
	 * @return the value of this AbsPrimitive as a Date.
	 * @throws ClassCastException If the type of this AbsPrimitive is not
	 *                            <code>BasicOntology.DATE</code>
	 */
	public Date getDate() {
		return (Date) value;
	}

	/**
	 * @return the value of this AbsPrimitive as a byte[].
	 * @throws ClassCastException If the type of this AbsPrimitive is not
	 *                            <code>BasicOntology.BYTE_SEQUENCE</code>
	 */
	public byte[] getByteSequence() {
		return (byte[]) value;
	}

	/**
	 * @return the value of this AbsPrimitive as an Object. If the type of this
	 *         AbsPrimitive is <code>BasicOntology.BOOLEAN
	 * BasicOntology.INTEGER or BasicOntology.FLOAT</code> a
	 *         <code>Boolean, Integer or Float</code> object is returned.
	 */
	public Object getObject() {
		return value;
	}

	/**
	 * @return The name of the type of the object held by this abstract descriptor.
	 * @see AbsObject#getTypeName()
	 */
	public String getTypeName() {
		return typeName;
	}

	/**
	 * Makes no sense in the case of an AbsPrimitive that has no attribute --> Just
	 * return null
	 */
	public AbsObject getAbsObject(String name) {
		return null;
	}

	/**
	 * Makes no sense in the case of an AbsPrimitive that has no attribute --> Just
	 * return null
	 */
	public String[] getNames() {
		return null;
	}

	/**
	 * Tests if this AbsPrimitive is grounded. It always returns true
	 */
	public boolean isGrounded() {
		return true;
	}

	/**
	 * Makes no sense in the case of an AbsPrimitive that has no attribute --> Just
	 * return 0
	 */
	public int getCount() {
		return 0;
	}

	public String toString() {
		return value.toString();
	}

	public boolean equals(Object obj) {
		if (obj instanceof AbsPrimitive primitive) {
			return getObject().equals(primitive.getObject());
		}
		else {
			return false;
		}
	}

	public int hashCode() {
		return getObject().hashCode();
	}

	public int getAbsType() {
		return ABS_PRIMITIVE;
	}
}
