package graphexceptions;

/**
 * 
 * Diese Exception wird geworfen, wenn der Input vielleicht vorhanden ist, aber
 * gegebene Voraussetzungen nicht erfuellt.
 */
public class InvalidInputException extends Exception
{

	/**
	 * 
	 */
	private static final long serialVersionUID = -6560208393705332008L;

	public InvalidInputException()
	{
	}

	public InvalidInputException(String arg0)
	{
		super(arg0);
	}

	public InvalidInputException(Throwable arg0)
	{
		super(arg0);
	}

	public InvalidInputException(String arg0, Throwable arg1)
	{
		super(arg0, arg1);
	}

}
