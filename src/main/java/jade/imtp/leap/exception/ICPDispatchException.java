package jade.imtp.leap.exception;

public class ICPDispatchException extends ICPException {

	private static final long serialVersionUID = 8094024808070587354L;
	
	private int sessionId = -1;
	
	public ICPDispatchException(String msg, int sessionId) {
		super(msg);
		this.sessionId = sessionId;
	}

	public ICPDispatchException(String msg, Throwable nested, int sessionId) {
		super(msg, nested);
		this.sessionId = sessionId;
	}

	public int getSessionId() {
		return sessionId;
	}
}
