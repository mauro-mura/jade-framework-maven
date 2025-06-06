package jade.wrapper.gateway;



import jade.core.Agent;
import jade.core.behaviours.*;


import jade.util.Logger;

import java.io.Serial;

/**
 * This agent is the gateway able to execute all commands requests received via
 * JadeGateway.
 * <p>
 * <code>JadeGateway</code> enables two alternative ways to implement a gateway
 * that allows non-JADE code to communicate with JADE agents. <br>
 * The first one is to extend the <code>GatewayAgent</code> <br>
 * The second one is to extend this <code>GatewayBehaviour</code> and add an
 * instance of this Behaviour to your own agent that will have to function as a
 * gateway (see its javadoc for reference).
 * 
 * @see JadeGateway
 * @see GatewayBehaviour
 * @author Fabio Bellifemine, Telecom Italia LAB
 * @author Moreno LAGO
 * @version $Date: 2015-03-10 12:58:25 +0100 (mar, 10 mar 2015) $ $Revision: 6749 $
 **/
public class GatewayAgent extends Agent {

	@Serial
	private static final long serialVersionUID = -6380554009443776915L;
	private GatewayBehaviour myB;
	private GatewayListener listener;
	private final Logger myLogger = Logger.getMyLogger(this.getClass().getName());

	/**
	 * subclasses may implement this method. The method is called each time a
	 * request to process a command is received from the JSP Gateway.
	 * <p>
	 * The recommended pattern is the following implementation: <code>
	 if (c instanceof Command1)
	 exexCommand1(c);
	 else if (c instanceof Command2)
	 exexCommand2(c);
	 </code>
	 * </p>
	 * <b> REMIND THAT WHEN THE COMMAND HAS BEEN PROCESSED, YOU MUST CALL THE METHOD
	 * <code>releaseCommand</code>. <br>
	 * Sometimes, you might prefer launching a new Behaviour that processes this
	 * command and release the command just when the Behaviour terminates, i.e. in
	 * its <code>onEnd()</code> method.
	 **/
	protected void processCommand(final Object command) {
		if (command instanceof Behaviour behaviour) {
			SequentialBehaviour sb = new SequentialBehaviour(this);
			sb.addSubBehaviour(behaviour);
			sb.addSubBehaviour(new OneShotBehaviour(this) {

				@Serial
				private static final long serialVersionUID = 8267092688795587675L;

				public void action() {
					GatewayAgent.this.releaseCommand(command);
				}
			});
			addBehaviour(sb);
		} else {
			myLogger.log(Logger.WARNING, "Unknown command " + command);
		}
	}

	/**
	 * notify that the command has been processed and remove the command from the
	 * queue
	 * 
	 * @param command is the same object that was passed in the processCommand
	 *                method
	 **/
	public final void releaseCommand(Object command) {
		myB.releaseCommand(command);
	}

	public GatewayAgent() {
		// enable object2agent communication with queue of infinite length
		setEnabledO2ACommunication(true, 0);
	}

	/*
	 * Those classes that extends this setup method of the GatewayAgent MUST
	 * absolutely call <code>super.setup()</code> otherwise this method is not
	 * executed and the system would not work.
	 * 
	 * @see jade.core.Agent#setup()
	 */
	@Override
	protected void setup() {
		myLogger.log(Logger.INFO, "Started GatewayAgent " + getLocalName());
		myB = new GatewayBehaviour() {

			@Serial
			private static final long serialVersionUID = 1183903658420251701L;

			protected void processCommand(Object command) {
				((GatewayAgent) myAgent).processCommand(command);
			}
		};
		addBehaviour(myB);
		setO2AManager(myB);

		// Check if the listener is passed as agent argument
		if (listener == null) {
			Object[] args = getArguments();
			if (args != null) {
				for (int i = 0; i < args.length; i++) {
					if (args[i] instanceof GatewayListener gatewayListener) {
						listener = gatewayListener;
						break;
					}
				}
			}
		}

		if (listener != null) {
			listener.handleGatewayConnected();
		}
	}

	protected void takeDown() {
		if (listener != null) {
			listener.handleGatewayDisconnected();
		}
	}

	// No need for synchronizations since this is only called when the Agent Thread
	// has not started yet
	void setListener(GatewayListener listener) {
		this.listener = listener;
	}

}
