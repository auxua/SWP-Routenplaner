package graphexceptions;

/**
 * 
 * Die Exception wird geworfen, wenn eine GPS Koordinate ungueltig initialisiert wird
 */
public class InvalidGPSCoordinateException extends Exception
{


	/**
	 * 
	 */
	private static final long serialVersionUID = 2668765729170578000L;

	public InvalidGPSCoordinateException()
	{
	}

	public InvalidGPSCoordinateException(String arg0)
	{
		super(arg0);
	}

	public InvalidGPSCoordinateException(Throwable arg0)
	{
		super(arg0);
	}

	public InvalidGPSCoordinateException(String arg0, Throwable arg1)
	{
		super(arg0, arg1);
	}

}
