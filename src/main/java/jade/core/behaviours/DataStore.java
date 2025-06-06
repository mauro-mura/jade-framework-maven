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

package jade.core.behaviours;

import java.io.Serial;
import java.util.HashMap;

/**
 * @author Giovanni Caire - TILab S.p.A.
 * @author Moreno LAGO
 * @version $Date: 2005-04-15 18:38:51 +0200 (ven, 15 apr 2005) $ $Revision: 5674 $
 */
public class DataStore extends HashMap {

	@Serial
	private static final long serialVersionUID = 1157606487081560892L;

	public DataStore() {
		super();
	}

	public DataStore(int size) {
		super(size);
	}
}
