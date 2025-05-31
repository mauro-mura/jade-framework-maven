package jade.content.abs;

import java.io.Serial;

public class AbsConceptSlotFunction extends AbsConcept {

	@Serial
	private static final long serialVersionUID = -7740682606676062905L;

	public AbsConceptSlotFunction(String slotName) {
		super(slotName);
	}
	
    @Override
    public int getAbsType() {
    	return ABS_CONCEPT_SLOT_FUNCTION;
    }
}
