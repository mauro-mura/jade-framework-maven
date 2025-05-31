package jade.content.exception;

import java.io.Serial;


import jade.util.WrapperException;

/**
 * Base class for OntologyException and CodecException
 */
public class ContentException extends WrapperException {

	@Serial
	private static final long serialVersionUID = 547523605281874847L;

	public ContentException(String message) {
		super(message);
	}

	public ContentException(String message, Throwable t) {
		super(message, t);
	}
}
