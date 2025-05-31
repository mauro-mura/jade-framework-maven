package jade.content.onto;

import java.io.Serial;

import jade.content.onto.exception.OntologyException;

//#APIDOC_EXCLUDE_FILE

public class NotASpecialType extends OntologyException {

	@Serial
	private static final long serialVersionUID = 5605563404941865968L;

	public NotASpecialType() {
		super("");
	}

	public Throwable fillInStackTrace() {
		return this;
	}
}
