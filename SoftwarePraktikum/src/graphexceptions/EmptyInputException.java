package graphexceptions;

/**
 * 
 * Diese Exception soll geworfen werden, wenn eine Funktion mit Nullpointern aufgerufen wird,
 * wobei definitiv gueltige Instanzen benoetigt worden sind
 */
public class EmptyInputException extends Exception
{


	/**
	 * 
	 */
	private static final long serialVersionUID = -1028018799942303509L;

	public EmptyInputException()
	{
	}

	public EmptyInputException(String arg0)
	{
		super(arg0);
	}

	public EmptyInputException(Throwable arg0)
	{
		super(arg0);
	}

	public EmptyInputException(String arg0, Throwable arg1)
	{
		super(arg0, arg1);
	}

}
