package Import;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Ein Objekt das alle Wege eines Wayfiles einer OSM Datei beinhaltet, welche
 * dann in den MapGraph eingefuegt werden.
 * 
 * 
 */
@SuppressWarnings("serial")
public class WayFile implements Serializable {
	private ArrayList<ImportEdge> ways = new ArrayList<ImportEdge>();

	void insertEdge(ImportEdge edge) {
		ways.add(edge);
	}

	Iterator<ImportEdge> getEdgeIt() {
		return ways.iterator();
	}

}
