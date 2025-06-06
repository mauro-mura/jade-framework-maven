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
package jade.content.frame;

import java.io.Serial;
import java.util.Vector;

/**
 * Generic class representing all frames (such as aggregates and
 * content-element-lists) whose composing elements can be retrieved by an index.
 * 
 * @author Giovanni Caire - TILAB
 * @author Moreno LAGO
 */
public class OrderedFrame extends Vector implements Frame {

	@Serial
	private static final long serialVersionUID = -5323790975629585524L;
	private final String typeName;

	/**
	 * Create an OrderedFrame with a given type-name.
	 * 
	 * @param typeName The type-name of the OrderedFrame to be created.
	 */
	public OrderedFrame(String typeName) {
		super();
		this.typeName = typeName;
	}

	/**
	 * Retrieve the type-name of this OrderedFrame.
	 * 
	 * @return the type-name of this OrderedFrame
	 */
	public String getTypeName() {
		return typeName;
	}
}
