package jade.core.messaging;


//#APIDOC_EXCLUDE_FILE

import java.util.HashMap;
import java.util.Map;

import jade.core.AID;

class LocalJVMMessageRouter {
	private static final Map<String, LocalJVMMessageRouter> localRouters = new HashMap<>();

	static synchronized LocalJVMMessageRouter getRouter(String platformID) {
		LocalJVMMessageRouter router = localRouters.get(platformID);
		if (router == null) {
			router = new LocalJVMMessageRouter();
			localRouters.put(platformID, router);
		}
		return router;
	}


	private final Map<String, MomMessagingService> localServices = new HashMap<>();
	
	public void register(String location, MomMessagingService mms) {
		localServices.put(location, mms);
	}
	
	public void deregister(String location) {
		localServices.remove(location);
	}
	
	public boolean localSendMessage(String deliveryID, AID senderID, GenericMessage msg, AID receiverID, String receiverLocation, MomMessagingService senderService) {
		MomMessagingService svc = localServices.get(receiverLocation);
		if (svc != null) {
			Object ret = svc.processIncomingMessage(senderID, msg, receiverID);
			senderService.handleDeliveryResult(deliveryID, (Throwable) ret);
			return true;
		}
		else {
			return false;
		}
	}
}
