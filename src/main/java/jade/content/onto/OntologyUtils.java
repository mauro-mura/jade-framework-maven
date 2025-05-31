package jade.content.onto;


import jade.content.onto.exception.OntologyException;

public class OntologyUtils {

	public static void exploreOntology(Ontology ontology) throws OntologyException {
		ontology.dump();
	}
}
