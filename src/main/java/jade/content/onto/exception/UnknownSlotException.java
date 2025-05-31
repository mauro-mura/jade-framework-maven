package jade.content.onto.exception;

public class UnknownSlotException extends OntologyException {

	private static final long serialVersionUID = -951252911425921129L;

	public UnknownSlotException() {
		super(null);
	}

	public UnknownSlotException(String slotName) {
		super("Slot " + slotName + " does not exist");
	}
}
