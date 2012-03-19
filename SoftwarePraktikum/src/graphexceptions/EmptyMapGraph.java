/**
 * 
 */
package graphexceptions;

/**
 *Diese Exception soll geworfen werden, wenn die Methode getClosedNodeID keine
 *Knoten mit gueltiger ID im MapGraph findet.
 */
public class EmptyMapGraph extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8192943575537454658L;

	public EmptyMapGraph() {
	}

	public EmptyMapGraph(String message) {
		super(message);
	}

	public EmptyMapGraph(Throwable cause) {
		super(cause);
	}

	public EmptyMapGraph(String message, Throwable cause) {
		super(message, cause);
	}

}
