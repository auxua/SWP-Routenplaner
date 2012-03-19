/**
 * 
 */
package graphenbib;

import graphexceptions.NodeNotNeighbourOfPreviousElementInPathException;
import graphexceptions.PathNotFullyInitialized;

import java.util.ArrayList;

/**
 *	Diese Klasse dient dazu, einen Pfad in unseren Graphen darzustellen und bietet noch die Funktionalitaet,
 *  dass ein Pfad rekonstruiert wird, d.h. die kontrahierten Knoten in den Pfad eingefuegt werden.
 */
public class Path
{
	private ArrayList<Integer> pathInHierarchyMapGraph= new ArrayList<Integer>();
	private int pathLength;
	private long pathTime;
	boolean reconstruted=true;
	boolean validLength=false;
	boolean validTime=false;
	
	
	/**
	 * Fuegt einen Knoten mit der UID uid in den Pfad ein. Prueft dabei nicht, ob der hinzugefuegte Knoten in irgendeinem
	 * erdenkbaren Graphen Nachbar des zuletzt eingefuegten ist
	 * @param node HierarchyMapNode, deren ID in den Pfad eingefuegt werden soll.
	 */
	public void appendNode(HierarchyMapNode node)
	{
		this.validLength=false;
		this.validTime=false;
		this.reconstruted=false;
		pathInHierarchyMapGraph.add(node.getUID());
	}
	
	/**
	 * Fuegt einen KnotenID in den Pfad ein. Prueft dabei nicht, ob der hinzugefuegte Knoten in irgendeinem
	 * erdenkbaren Graphen Nachbar des zuletzt eingefuegten ist
	 * @param uid Einzufuegende KnotenId
	 */
	public void appendNode(int uid)
	{
		this.validLength=false;
		this.validTime=false;
		this.reconstruted=false;
		pathInHierarchyMapGraph.add(uid);
	}

	/**
	 * Setzt die Laenge des Pfades manuell. Dies wird manchmal benoetigt, wenn es sich nicht um einen rekonstruierbaren Pfad handelt.
	 * Danach kann die Fahrzeit mittels des entsprechenden Getter abgefragt werden. Sobald danach aber wieder Knoten eingefuegt werden,
	 * wird die Pfadlaenge als nicht mehr aktuell gesehen und es wuerde beim Abfragen der Pfadlaenge zu einem Fehler kommen.
	 * @param length Zu setzende Laenge
	 */
	public void setLength (int length)
	{
		this.validLength=true;
		this.pathLength = length;
	}
	
	/**
	 * Setzt die Fahrzeit des Pfades manuell, analog zur Laenge.
	 * @param time Zu setzende Zeit.
	 */
	public void setTime(long time)
	{
		this.validTime=true;
		this.pathTime = time;
	}
	
	
	/**
	 * Falls ein Pfad nicht rekonstruiert werden kann, kann man den Status manuell auf rekonstruiert setzen, sodass danach mittels
	 * der getPathNodeIDs Methode die PfadIDs abgefragt werden koennen.
	 */
	public void setPathAsReconstructed() {
		this.reconstruted=true;
	}
	

	/**
	 * Getter fuer das Gewicht des Pfades, welches sich abhaengig von der Konstante in Constants.fastestPathMode berechnet.
	 * Es wird automatisch hierbei der Pfad mittels des uebergebenen HierarchyMapGraphen rekonstruiert, falls dies
	 * nicht schon in der Vergangenheit geschehen ist. Es wird eine Exception geworfen, falls bei der Rekonstruktion
	 * ein Fehler auftritt oder der Graph keine Knoten enhaelt und auch die Laenge beziehungsweise Fahrzeit nicht gesetzt wurde.
	 * @param hGraph
	 * @return Pfadgewicht
	 * @throws PathNotFullyInitialized
	 * @throws NodeNotNeighbourOfPreviousElementInPathException
	 */
	public long getPathWeight(HierarchyMapGraph hGraph) throws PathNotFullyInitialized, NodeNotNeighbourOfPreviousElementInPathException
	{
		if(!reconstruted) {
			this.reconstructPath(hGraph);
		}
		if(main.Config.fastestPathMode) {
			return this.getPathTime();
		} else {
			return this.getPathLength();
		}
	}
	

	/**
	 * Gibt die Laenge des Pfades in Dezimetern zurueck. Wurde der Pfad nicht rekonstruiert (und dabei die Laenge neue berechnet,
	 * falls mindestens ein Knoten im Pfad existiert) oder  vorher die Laenge explizit gesetzt wird eine Exception geworfen.
	 * @return Laenge des Pfades in Dezimentern
	 * @throws PathNotFullyInitialized
	 */
	public int getPathLength()  throws PathNotFullyInitialized
	{
		if(!validLength) {
			throw new PathNotFullyInitialized("Path.getPathWeight aufgerufen, ohne dass die Pfadlaenge gesetzt wurde.");
		}
		return this.pathLength;
	}
	
	/**
	 * Gibt die Dauer den Pfad zurueckzulegen in Sekunden zurueck. Wurde der Pfad nicht rekonstruiert (und dabei die Zeit neue berechnet,
	 * falls mindestens ein Knoten im Pfad existiert) oder  vorher die Zeit explizit gesetzt wird eine Exception geworfen.
	 * @return Fahrzeit
	 * @throws PathNotFullyInitialized
	 */
	public long getPathTime()  throws PathNotFullyInitialized
	{
		if(!validTime) {
			throw new PathNotFullyInitialized("Path.getPathWeight aufgerufen, ohne dass die Pfadzeit gesetzt wurde.");
		}
		return this.pathTime;
	}
	
	/**
	 * Es muss vorher der Pfad explizit rekontruiert werden oder der Status des Pfades auf rekonstruiert gesetzt sein
	 * um mittels dieser Methode den Pfad als eine Liste von KnotenID zu bekommen. Ansonsten wird eine Exception geworfen.
	 * @return  Gibt den gespeicherten Pfad als ArrayList von UIDs der Nodes zurueck.
	 * @throws PathNotFullyInitialized 
	 */
	public ArrayList<Integer> getPathNodeIDs() throws PathNotFullyInitialized
    {
		if(!this.reconstruted) {
			throw new PathNotFullyInitialized("Path.getPathNodeIDs aufgerufen, ohne dass Pfad rekonstruiert wurde.");
		}
    	return pathInHierarchyMapGraph;
    }
	
	
	/**
	 * Gibt zurueck, aus wieviel Knoten der Pfad besteht.
	 * @return Anzahl der bisher hinzugefuegten Knoten
	 */
	public int size()
	{
		return this.pathInHierarchyMapGraph.size();
	}
	

	/**
	 * Rekonstruiert den Pfad auf dem HierarchyMapGraphen, der uebergeben wird, und markiert den Pfad als rekonstruiert,
	 * falls der Pfad aus mindestens einem Knoten besteht. Wirft einen Fehler, falls die in den Pfad eingefuegten Knoten
	 * sich nicht auch tatsaechlich auf einem Pfad im HierarchyMapGraphen befinden.
	 * @param hGraph HierarchyMapGraph, in dem sich der Pfad befindet.
	 * @throws NodeNotNeighbourOfPreviousElementInPathException
	 */
	public void reconstructPath(HierarchyMapGraph hGraph) throws NodeNotNeighbourOfPreviousElementInPathException
	{
		if(!this.reconstruted && this.pathInHierarchyMapGraph.size()>0 && hGraph!=null) {
			this.pathLength = 0; //Wir berechnen hier die Pfadlaenge im hGraphen immer wieder neu, also setzen wir es erstmal zurueck auf 0
			this.pathTime=0L;
			this.reconstruted=true;
			this.validLength=true;
			this.validTime=true;
			if(this.pathInHierarchyMapGraph.size()<2) {
				return;
			}
			ArrayList<Integer> result = new ArrayList<Integer>();
			result.add(pathInHierarchyMapGraph.get(0));
			for (int i = 0; i<this.pathInHierarchyMapGraph.size()-1;++i)
			{//iteriere ueber alle Knoten im aktuellen Pfad
				//Zunaechst addieren wir die Laenge der Teilstuecke auf
				HierarchyMapNode tempStartNode=hGraph.getNode(pathInHierarchyMapGraph.get(i));
				if(tempStartNode==null) {
					throw new NodeNotNeighbourOfPreviousElementInPathException("in getReconstructedPath: " +
							"Knoten mit UID "+pathInHierarchyMapGraph.get(i)+"befindet sich nicht im HierarchyMapGraph.");
				}
				HierarchyMapEdge tempEdge = tempStartNode.getEdgeToNeighbour(pathInHierarchyMapGraph.get(i+1));
				if (tempEdge == null) {
					throw new NodeNotNeighbourOfPreviousElementInPathException("in getReconstructedPath: " +
							"Knoten mit UID "+pathInHierarchyMapGraph.get(i)+" ist nicht ueber eine Kante " +
									"mit Knoten "+pathInHierarchyMapGraph.get(i+1)+" verbunden.");
				}
				this.pathLength+=tempEdge.getLength();
				this.pathTime += tempEdge.getTime();
				
				int nodeIDsInEdge[]=tempEdge.getContractedNodeIDs();
				for (int j = 0; j < nodeIDsInEdge.length; j++) {
					result.add(nodeIDsInEdge[j]);
				}
				result.add(pathInHierarchyMapGraph.get(i+1));
			}//iteriere ueber alle Knoten im aktuellen Pfad
			this.pathInHierarchyMapGraph=result;
		}
		//Logger.getInstance().log("Path", "Aktuelle Entfernung: "+pathLength +" Aktuelle Fahrzeit: "+ pathTime);
	}
	
	/**
	 * Gibt die ID des ersten Knotens des Pfades zurueck, falls dieser mindestens einen Knoten enthaelt. Ansonsten
	 * wird eine Exception geworfen.
	 * @return ID des Startknotens
	 * @throws PathNotFullyInitialized
	 */
	public int getStartNodeID() throws PathNotFullyInitialized {
		if(pathInHierarchyMapGraph.size()==0) {
			throw new PathNotFullyInitialized("Feher in Path.getStartNode: Der Pfad ist bisher leer");
		} else {
			return pathInHierarchyMapGraph.get(0);
		}
	}
	
	/**
	 * Gibt die ID des letzten Knotens des Pfades zurueck, falls dieser mindestens einen Knoten enthaelt. Ansonsten
	 * wird eine Exception geworfen.
	 * @return ID des Endknotens
	 * @throws PathNotFullyInitialized
	 */
	public int getEndNodeID() throws PathNotFullyInitialized {
		if(pathInHierarchyMapGraph.size()==0) {
			throw new PathNotFullyInitialized("Feher in Path.getStartNode: Der Pfad ist bisher leer");
		} else {
			return pathInHierarchyMapGraph.get(pathInHierarchyMapGraph.size()-1);
		}
	}
	
	public String toString()
	{
		if (pathInHierarchyMapGraph.size()==0)
			return "EMPTY PATH";
		String result = "";
		result += pathInHierarchyMapGraph.get(0);
		for (int i = 1; i<pathInHierarchyMapGraph.size();++i)
		{
			result += " --> "+pathInHierarchyMapGraph.get(i);
		}
		return result;
	}
}
