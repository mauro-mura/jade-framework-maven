package jade.core.faultRecovery;

import jade.core.ServiceHelper;
import jade.core.exception.ServiceException;

public interface FaultRecoveryHelper extends ServiceHelper {
	
	public static final String SERVICE_NAME = "jade.core.faultRecovery.FaultRecovery";
	
	void reattach() throws ServiceException;

}
