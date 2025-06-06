package jade.core.messaging;

class StuckDeliverer extends RuntimeException {

	private final String delivererName;
	
	public StuckDeliverer(String delivererName) {
		super();
		this.delivererName = delivererName;
	}
	
	public Throwable fillInStackTrace() {
		return this;
	}
	
	public String getDelivererName() {
		return delivererName;
	}
}
