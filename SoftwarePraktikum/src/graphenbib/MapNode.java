package graphenbib;
import graphexceptions.EmptyInputException;

import java.util.HashSet;

public class MapNode extends AbstractNode<MapEdge> implements Node
{
	private static final long serialVersionUID = -2997655001635365031L;

	public MapNode(int uid, GPSCoordinate gps) throws EmptyInputException
    {
		super(uid,new MapEdge[0],new MapEdge[0],false,0f,0f);
		if (gps == null) 
			throw new EmptyInputException("Konstruktor MapNode: Der Knoten mit der UID "+uid+" hat keine gueltigen GPS-Koordinaten bekommen");
		this.setGPS(gps);
	}
	
	protected MapNode(int uid)
	{
		super(uid,new MapEdge[0],new MapEdge[0],false,0f,0f);
	}
	
	public HashSet<MapNode> getNeighbours() {
		MapEdge outgoingEdges[]=this.getOutgoingEdges();
		HashSet<MapNode> neighbours = new HashSet<MapNode>();
		for(int i=0; i<outgoingEdges.length; i++) {
			neighbours.add(outgoingEdges[i].getNodeEnd());
		}
		return neighbours;
	}
	
	/**
	 * Prueft, ob eine Node mit der gegebenen UID ein Nachbar ist.
	 * Dabei wird nicht die Richtung der Kante, ueber die der Knoten mit dem Nachbarn verbunden ist, unterschieden.
	 * @param uid Die UID des Knotens, bei dem festgestellt werden soll, ob er direkter Nachbar ist.
	 * @return true, wenn der Knoten mit der gegebenen UID Nachbar ist, false sonst
	 */
	public boolean isNeighbour(int uid)
	{
		MapEdge incomingEdges[]=this.getIncomingEdges();
		MapEdge outgoingEdges[]=this.getOutgoingEdges();
		for (int i = 0; i < incomingEdges.length; i++)
        {
	        if (incomingEdges[i].getNodeStart().getUID() == uid)
	        	return true;
        }
		for (int i = 0; i < outgoingEdges.length; i++)
        {
	        if (outgoingEdges[i].getNodeEnd().getUID() == uid)
	        	return true;
        }
		return false;
	}
	
	@Override
	public String toString() {
		return "MapNode: "+super.toString();
	}
}
