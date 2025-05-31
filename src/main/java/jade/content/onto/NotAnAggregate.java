package jade.content.onto;

import java.io.Serial;

import jade.content.onto.exception.OntologyException;

//#APIDOC_EXCLUDE_FILE

public class NotAnAggregate extends OntologyException {

	@Serial
	private static final long serialVersionUID = 3424325228170070778L;

	public NotAnAggregate() {
		super("");
	}

	public Throwable fillInStackTrace() {
		return this;
	}
}
