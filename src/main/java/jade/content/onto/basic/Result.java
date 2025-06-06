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

import jade.content.*;
import jade.content.abs.*;
import jade.content.onto.*;
import java.util.List;
import java.io.Serial;
import java.util.ArrayList;

/**
   This class implements the <code>result</code> operator of the
   FIPA SL0 action.
   @author Giovanni Caire - TILAB
 */
public class Result implements Predicate {

	@Serial
	private static final long serialVersionUID = -2631264683217210449L;
	private Concept action;
	private Object value;
	
	public Result() {
		action = null;
		value = null;
	}
	
	public Result(Concept a, Object v) {
		setAction(a);
		setValue(v);
	}
	
	public Concept getAction() {
		return action;
	}
	
	public void setAction(Concept a) {
		action = a;
	}	
	
	public Object getValue() {
		return value;
	}
	
	public void setValue(Object v) {
		value = v;
	}	
	
	public List getItems() {
		if (value instanceof List list) {
			return list;
		}
		else {
			List<Object> l = new ArrayList<>(1);
			if (value != null) {
				l.add(value);
			}
			return l;
		}
	}
	
	public void setItems(List l) {
		value = l;
	}	
	
}
