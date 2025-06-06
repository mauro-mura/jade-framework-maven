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

package jade.core;

import java.io.Serializable;
import jade.content.Concept;

/**
  
  Abstract interface to represent JADE network locations. This interface can
  be used to access information about the various places where a JADE mobile
  agent can migrate.

  @author Giovanni Rimassa - Universita` di Parma
  @version $Date: 2002-02-04 11:31:40 +0100 (lun, 04 feb 2002) $ $Revision: 3043 $
*/
public interface Location extends Serializable, Concept {

  /**
    Read a unique ID for the location.
    @return A <code>String</code> representing the location.
  */
  String getID();
  
  /**
    Read the name of a location.
    @return A name for this location. The name has only a local meaning.
  */
  String getName();

  /**
    Read the protocol for a location.
    @return The name of the protocol used to reach this location.
  */
  String getProtocol();

  /**
    Read the address for a location.
    @return The transport address of this location (in the specified protocol).
  */
  String getAddress();

}
