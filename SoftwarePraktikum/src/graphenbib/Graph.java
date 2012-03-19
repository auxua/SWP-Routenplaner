package graphenbib;

import graphexceptions.EmptyInputException;
import graphexceptions.InvalidInputException;
import graphexceptions.NodeNotInGraphException;

public interface Graph
{
	void insertNode(int uid, GPSCoordinate gps) throws EmptyInputException,InvalidInputException;
	void insertEdge(int startNodeUID, int endNodeUID, int wayID, int length, StreetType streetType) throws InvalidInputException, NodeNotInGraphException;
	Node getNode(int uid);
	GPS getUpperLeft();
	GPS getLowerRight();
}
