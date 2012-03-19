/**
 * 
 */
package graphexceptions;

/**
 * Diese Exception wird geworfen, wenn auf Attribute der Pfadklasse zugegriffen wird, waehrend sich der Pfad noch in
 * einem nicht gueltigen Zustand befindet beziehungsweise diese Attribute noch nicht gesetzt wurden.
  */
public class PathNotFullyInitialized extends Exception {


	/**
	 * 
	 */
	private static final long serialVersionUID = -8879401031164068381L;

	public PathNotFullyInitialized() {
	}

	/**
	 * @param message
	 */
	public PathNotFullyInitialized(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public PathNotFullyInitialized(Throwable cause) {
		super(cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public PathNotFullyInitialized(String message, Throwable cause) {
		super(message, cause);
	}

}
