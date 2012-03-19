package graphexceptions;


/**
 * 
 * Wenn ein Node in einem Graphen abgefragt wird, und dieser noch gar nicht da drin ist,
 * dann wird diese Exception geworfen.
 */
public class NodeNotInGraphException extends Exception
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 2970982480437282118L;

	public NodeNotInGraphException()
	{
	}

	public NodeNotInGraphException(String arg0)
	{
		super(arg0);
	}

	public NodeNotInGraphException(Throwable arg0)
	{
		super(arg0);
	}

	public NodeNotInGraphException(String arg0, Throwable arg1)
	{
		super(arg0, arg1);
	}

}
