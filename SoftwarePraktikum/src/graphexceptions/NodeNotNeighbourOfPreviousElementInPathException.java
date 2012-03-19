package graphexceptions;

public class NodeNotNeighbourOfPreviousElementInPathException extends Exception
{

	/**
	 * 
	 */
	private static final long serialVersionUID = -9114511152700168965L;

	public NodeNotNeighbourOfPreviousElementInPathException()
    {
	    super();
    }

	public NodeNotNeighbourOfPreviousElementInPathException(String arg0,
            Throwable arg1)
    {
	    super(arg0, arg1);
    }

	public NodeNotNeighbourOfPreviousElementInPathException(String arg0)
    {
	    super(arg0);
    }

	public NodeNotNeighbourOfPreviousElementInPathException(Throwable arg0)
    {
	    super(arg0);
    }

}
