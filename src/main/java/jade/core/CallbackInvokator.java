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

//#APIDOC_EXCLUDE_FILE

import java.io.Serial;
import java.lang.reflect.*;

import jade.util.Logger;

/**
 * This class is used internally by the framework and is not accessible to
 * users.
 * 
 * @author Giovanni Caire - TILAB
 * @author Moreno LAGO
 */
public class CallbackInvokator implements java.io.Serializable {

	private final Logger myLogger = Logger.getMyLogger(this.getClass().getName());
	@Serial
	private static final long serialVersionUID = -5610398426819185225L;

	// Package-scoped constructor to avoid creation outside the
	// container
	CallbackInvokator() {
	}

	public void invokeCallbackMethod(Agent a, String name) {
		recursiveInvoke(a, a.getClass(), name);
	}

	private void recursiveInvoke(Agent a, Class<?> agentClass, String name) {
		Method callbackMethod = null;
		try {
			callbackMethod = agentClass.getDeclaredMethod(name, (Class[]) null);

			// #DOTNET_EXCLUDE_BEGIN
			boolean accessibilityChanged = false;
			if (!callbackMethod.isAccessible()) {
				try {
					callbackMethod.setAccessible(true);
					accessibilityChanged = true;
				} catch (SecurityException se) {
					myLogger.log(Logger.WARNING, "Callback method " + name + "() of agent " + a.getName() + " not accessible.");
				}
			}
			// #DOTNET_EXCLUDE_END

			try {
				callbackMethod.invoke(a, (Object[]) null);

				// #DOTNET_EXCLUDE_BEGIN
				// Restore accessibility if changed
				if (accessibilityChanged) {
					callbackMethod.setAccessible(false);
				}
				// #DOTNET_EXCLUDE_END

			} catch (Exception e) {
				myLogger.log(Logger.WARNING, "Error executing callback method " + name + "() of agent " + a.getName() + ". " + e);
			}
		} catch (NoSuchMethodException e) {
			// Callback method not defined. Try in the superclass if any.
			// Otherwise just ignore it
			Class<?> superClass = agentClass.getSuperclass();
			if (superClass != null) {
				recursiveInvoke(a, superClass, name);
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}
}
