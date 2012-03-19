package graphenbib;

import graphexceptions.EmptyInputException;
import graphexceptions.EmptyMapGraph;
import graphexceptions.InvalidGPSCoordinateException;
import graphexceptions.InvalidInputException;
import graphexceptions.NodeNotInGraphException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import main.Config;
import main.Logger;

/**
 * 
 * @version 25.10.2011, 11.00 Uhr
 * Letzte Aenderung: Albert
 */

public class MapGraph implements Graph, Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -592280011176955776L;
	private HashMap<Integer,MapNode> nodes;
	//private GPS upperLeft;
	//private GPS lowerRight;
	private GPSRectangle MapRectangle;
	private String Filename;

	public String getFilename() {
		return Filename;
	}

	public void setFilename(String filename) {
		Filename = filename;
	}


	/**
	 * 
	 * @param upperLeft Die linke obere GPS Koordinate des Kartenstueckes, das wir betrachten
	 * @param lowerRight Die rechte untere GPS Koordinate des Kartenstueckes, das wir betrachten.
	 */
	public MapGraph(GPS upperLeft, GPS lowerRight) throws EmptyInputException, InvalidInputException, InvalidGPSCoordinateException
    {
	    GPSRectangle MapRect = new GPSRectangle(upperLeft,lowerRight);
	    //new MapGraph(MapRect);
		this.MapRectangle = MapRect;
		nodes=new HashMap<Integer,MapNode>();
	    
    }
	

	/*
	 * Ueberladung des Konstruktors fuer die Uebergabe eines Rectangles
	 */
	public MapGraph(GPSRectangle MapRectangle) throws EmptyInputException
	{
		if (MapRectangle == null) {
			throw new EmptyInputException("Konstruktor MapGraph: Das Rechteck im Konstruktor ist ein Nullpointer");
		}
		
		this.MapRectangle = MapRectangle;
		nodes=new HashMap<Integer,MapNode>();
	}
	
	/*
	 * Ueberladung fuer Uebergabe der Seiten 
	 */
	public MapGraph(float top, float right, float bottom, float left) throws EmptyInputException, InvalidInputException, InvalidGPSCoordinateException
	{
		GPSRectangle MapRect = new GPSRectangle(top,right,bottom,left);
		this.MapRectangle = MapRect;
		nodes = new HashMap<Integer,MapNode>();
	}
	
	/**
	 * Gebe die Menge der eingelesenen Nodes aus
	 * @return Menge der Nodes (int)
	 */
	public int getSize() {
		return nodes.size();
	}
	
	/**
	 * Diese Methode gibt einen Iterator auf den Nodes zurueck
	 * @return Iterator von MapNodes
	 */
	public Iterator<MapNode> getNodeIt(){
		return nodes.values().iterator();
	}
	
	/**
	 * Diese Methode gibt einen Iterator ueber alle Kanten des MapGraph zurueck
	 * @return Iterator von MapEdges
	 */
	public Iterator<MapEdge> getEdgeIt() {
		return new MapEdgeIterator(this);
	}
	
	public void insertNode(int uid, GPSCoordinate gps) throws EmptyInputException, InvalidInputException 
	{
		if (MapRectangle.GPSInside(gps))
		{
			MapNode test = nodes.get(uid);
			if (test != null)
				throw new InvalidInputException("insertNode in MapGraph: Es wurde versucht, einen Knoten doppelt einzufuegen. UID: "+uid);
			MapNode a= new MapNode(uid,gps);
			nodes.put(a.getUID(),a);
		}
		else
		{
			throw new InvalidInputException("insertNode in MapGraph: Es wurde eine Node uebergeben, die nicht im vom Graphen betrachteten Bereich liegt.\n UID: "+uid+" GPS "+gps);
		}
	}
	
	/**
	 * Methode zum Einfuegen eines Knoten, fuer den die GPS-Koordinate noch nicht bekannt ist.
	 * @param uid UID des einzufuegenden Knotens
	 */
	public void insertNodeWithoutGPS(int uid) {
		MapNode a=new MapNode(uid);
		nodes.put(a.getUID(), a);
	}
	
	public void insertOneWay(int startNodeUID, int endNodeUID, int wayID, int length, StreetType streetType, String name) 
			throws InvalidInputException, NodeNotInGraphException {
		this.insertEdge(startNodeUID, endNodeUID, wayID, length, streetType,name,true);
	}
	
	public void insertEdgeBothDirections(int startNodeUID, int endNodeUID, int wayID, int length, StreetType streetType, String name) 
			throws InvalidInputException, NodeNotInGraphException {
		this.insertEdge(startNodeUID, endNodeUID, wayID, length, streetType,name,false);
	}
	
	public void insertEdge(int startNodeUID, int endNodeUID, int wayID, int length, StreetType streetType) throws InvalidInputException, NodeNotInGraphException {
		this.insertEdge(startNodeUID, endNodeUID, wayID, length, streetType,"", true);
	}
	
	public void insertEdge(int startNodeUID, int endNodeUID, int wayID, int length, StreetType streetType, String name) 
			throws InvalidInputException, NodeNotInGraphException {
		this.insertEdge(startNodeUID, endNodeUID, wayID, length, streetType, name,true);
	}
			
	private void insertEdge(int startNodeUID, int endNodeUID, int wayID, int length, StreetType streetType, String name, boolean oneWay) 
					throws InvalidInputException, NodeNotInGraphException
	{
		if(name==null)
			name="";
		if (length < 0)
			throw new InvalidInputException("insertEdge in MapGraph: nicht-positives Kantengewicht " +
					"nicht erlaubt, aber denoch versucht einzufuegen. wayID: "+wayID);
		MapNode start=getNode(startNodeUID);
		MapNode end=getNode(endNodeUID);
		
		if(start==null || end==null) 
			throw new NodeNotInGraphException("insertEdge in MapGraph:"+
					"Mindestens einer der angegebenen Nodes ist nicht im Graphen vorhanden. \n" +
					"startNodeUID: "+startNodeUID+", endNodeUID "+endNodeUID+", wayID"+wayID+". \n"+						
					"Nullwerte bei Start: "+(start==null)+" bei end: "+(end == null));
		if(oneWay) {
			MapEdge e=new MapEdge(start,end,wayID,length,streetType,name,true,oneWay);
			start.addOutgoingEdge(e);
			end.addIncomingEdge(e);
		} else {
			MapEdge e1=new MapEdge(start,end,wayID,length,streetType,name,true,false);
			MapEdge e2=new MapEdge(end,start,wayID,length,streetType,name,false,false);
			start.addOutgoingEdge(e1);
			end.addIncomingEdge(e1);
			end.addOutgoingEdge(e2);
			start.addIncomingEdge(e2);
		}
	}
	
	/**
	 * Mit dieser Methode laesst sich der nahegelegenste Knoten zu einer gegebenen GPS-Koordinate
	 * finden. 
	 * @param gps
	 * @return UID des nahegelsegensten Knoten
	 */
	public MapNode getClosestNode(GPSCoordinate gps) throws InvalidGPSCoordinateException, EmptyMapGraph{
		if(gps==null) {
			throw new InvalidGPSCoordinateException("Fehler in getClosedNodeID: " +
					"Uebergebene GPS-Koordinate ist Null.");
		}
		Iterator<MapNode> nodeIt=this.getNodeIt();
		int minDistance=Integer.MAX_VALUE;
		MapNode closestNode =null;
		while (nodeIt.hasNext()) {
			MapNode currentNode=nodeIt.next();
			if(currentNode.hasGPS()) {
				if(gps.distanceTo(currentNode.getGPS())<minDistance) {
					minDistance=gps.distanceTo(currentNode.getGPS());
					closestNode=currentNode;
				}
			}
		}
		if(closestNode==null && minDistance==Integer.MAX_VALUE) {
			throw new EmptyMapGraph("MapGraph enthielt keine Knoten mit gueltiger GPS-Koordinate");
		}
		return closestNode;
	}
	/**
	 * Entfernt die isolierten Knoten, das heisst, Knoten zu denen es weder ausgehende
	 * noch eingehende Kanten gibt
	 */
	public void deleteIsolatedNodes() {
		//speichert UIDs Liste der zu loeschenden Knoten
		ArrayList<Integer> lliste = new ArrayList<Integer>(); 
		Iterator<MapNode> iterator = this.getNodeIt();
		MapNode currentNode;
		while(iterator.hasNext()) {
			currentNode = iterator.next();
			if((currentNode.getNumberOfIncomingEdges()==0) && (currentNode.getNumberOfOutgoingEdges()==0)) {
				lliste.add(currentNode.getUID());
			}
		}	
		//entferne:
		for (int i=0; i<lliste.size(); i++) {
			nodes.remove(lliste.get(i));
		}
	}
	
	/**
	 *Mittels dieser Methode koennen die Knoten und Kanten des MapGraphs, ueber den diese Methode
	 *aufgerufenen wird, in den HierarchyMapGraph, der als Argument uebergeben wird, hinzugefuegt werden.
	 *Iterativ koennen so mehrere MapGraphs in einen HierarchyGraphen exportiert werden.
	 * @param hGraph Dies in der HierarchyMapGraph in den der MapGraph, ueber den diese Methode aufgerufen werden,
	 * 		  hinzueguegt wird.
	 */

	public HierarchyMapGraph exportToHierachyGraph (HierarchyMapGraph hGraph) {
		ArrayList<MapEdge> allEdges = new ArrayList<MapEdge>();
		//Alle Knoten in den neuen Graphen einfuegen
		for (MapNode currentNode : nodes.values()) {
			if(hGraph.getNode(currentNode.getUID())==null) {
				//Es kann wegen des Tiling passieren, dass Knoten doppelt eingefuegt werden.
				//Dies wird hier abgefangen.
				hGraph.insertNode(currentNode.getUID(), currentNode.getNumberOfIncomingEdges(),
							currentNode.getNumberOfOutgoingEdges());
			} else if (currentNode.hasGPS()) {
				//Falls der Knoten schon drin liegt und der, der eingefuegt werden soll eine GPS-Koordinate
				//besitzt, wird die GPS im hGraph aktualisiert
				hGraph.getNode(currentNode.getUID()).setGPS(currentNode.getGPS());
			}
			
			//Um alle Kanten zu speichern, reicht es die ausgehenden Kanten zu speichern
			MapEdge[] edges = currentNode.getOutgoingEdges();
			for(int iterator = 0; iterator < edges.length; iterator++) {
				allEdges.add(edges[iterator]);
			}
		}
		//Nun alle Kanten in den neuen Graphen einfuegen
		byte level=0;
		for (MapEdge currentEdge : allEdges) {
			hGraph.insertEdge(currentEdge.getNodeStart().getUID(), currentEdge.getNodeEnd().getUID(), 
						currentEdge.getUID(), currentEdge.getLength(), currentEdge.getType(),level);
		}
		return hGraph;
	}
	
	/**
	 * @param uid Die UID des geforderten Knotens
	 * @return null, wenn Knoten im Graph nicht vorhanden, ansonsten gibt er den Knoten zurueck
	 */
	public MapNode getNode(int uid)
	{
		return nodes.get(uid);
	}
	
	/**
	 * @return Rechteck, in dem die Knoten des MapGraph liegen.
	 */
	public GPSRectangle getRect()
	{
		return this.MapRectangle;
	}

	public GPS getUpperLeft()
	{
		return MapRectangle.getUpperLeft();
	}
	
	public GPS getLowerRight()
	{
		return MapRectangle.getLowerRight();
	}
	
	/**
	 * Mit dieser Methode werden die Kantenlaengen korrigiert, da vom Import Kanten mit der Laenge
	 * Integer.MAX_VALUE eingefuegt werden, wenn die KnotenGPS nicht zur Verfuegung steht und daher
	 * keine Laengenberechnung moeglich ist.
	 */
	public void correctLength() {
		Iterator<MapNode> nodeIt=this.getNodeIt();
		while(nodeIt.hasNext()) {
			MapEdge outEdges[]=nodeIt.next().getOutgoingEdges();
			for(MapEdge e:outEdges) {
				if(e.getLength()==Config.initialTemporaryLengthForEdges) {
					if(e.getNodeStart().hasGPS() && e.getNodeEnd().hasGPS()) {
						try {
							e.setLength(e.getNodeStart().getGPS().distanceTo(e.getNodeEnd().getGPS()));
						} catch (InvalidGPSCoordinateException e1) {
							//Das sollte nie passieren, da vorher die Existenz der GPS ueberprueft wurde.
							e1.printStackTrace();
						}
					} else {
						Logger.getInstance().log("MapGraph.correctLength", "Warnung: Folgende Kante mit ungueltiger Laenge im MapGraph " +e);
					}
				}
			}
		}
	}
	
	public String toString() {
		String tempString="";
		Iterator<MapNode> iterator = this.getNodeIt();
		while(iterator.hasNext()) {
			tempString +=iterator.next().toString();
		}
		return tempString;
	}
	
}
