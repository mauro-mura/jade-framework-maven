package jade.core.nodeMonitoring;


//#APIDOC_EXCLUDE_FILE

import jade.core.Service;
import jade.core.exception.IMTPException;
import jade.core.exception.ServiceException;

public interface UDPNodeMonitoringSlice extends Service.Slice {
	static final String H_ACTIVATEUDP = "H-ACTIVATEUDP";
	static final String H_DEACTIVATEUDP = "H-DEACTIVATEUDP";
	
	/*
	 * Request a given node to start sending UDP packets
	 */
	void activateUDP(String label, String host, int port, int pingDelay, long key) throws IMTPException, ServiceException;
	
	/*
	 * Request a given node to stop sending UDP packets
	 */
	void deactivateUDP(String label, long key) throws IMTPException;
}
