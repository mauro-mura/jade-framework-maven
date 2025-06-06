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
package jade.content.onto.basic;

import java.io.Serial;

import jade.content.*;
import jade.content.onto.*;
import jade.content.onto.exception.OntologyException;

/**
 * This class implements the <code>=</code> operator of the FIPA SL0 language.
 * 
 * @author Giovanni Caire - TILAB
 * @author Moreno LAGO
 */
public class Equals implements Predicate {

	@Serial
	private static final long serialVersionUID = -9098386174239241547L;
	private Object left;
	private Object right;

	public Equals() {
		left = null;
		right = null;
	}

	public Equals(Object l, Object r) {
		setLeft(l);
		setRight(r);
	}

	public Object getLeft() {
		return left;
	}

	public void setLeft(Object l) {
		try {
			Ontology.checkIsTerm(l);
		} catch (OntologyException oe) {
			throw new IllegalArgumentException(oe.getMessage());
		}
		left = l;
	}

	public Object getRight() {
		return right;
	}

	public void setRight(Object r) {
		try {
			Ontology.checkIsTerm(r);
		} catch (OntologyException oe) {
			throw new IllegalArgumentException(oe.getMessage());
		}
		right = r;
	}

}
