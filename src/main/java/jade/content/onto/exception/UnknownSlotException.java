package jade.content.onto.exception;

import java.io.Serial;

public class UnknownSlotException extends OntologyException {

	@Serial
	private static final long serialVersionUID = -951252911425921129L;

	public UnknownSlotException() {
		super(null);
	}

	public UnknownSlotException(String slotName) {
		super("Slot " + slotName + " does not exist");
	}
}
