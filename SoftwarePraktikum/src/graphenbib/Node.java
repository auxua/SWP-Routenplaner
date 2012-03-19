package graphenbib;

import java.util.HashSet;

public interface Node
{
	MapEdge[] getIncomingEdges();
	MapEdge[] getOutgoingEdges();
	HashSet<MapNode> getNeighbours();
	GPS getGPS();
	int getUID();
}
