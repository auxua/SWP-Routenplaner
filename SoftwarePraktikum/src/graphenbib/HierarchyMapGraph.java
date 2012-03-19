package graphenbib;

import graphexceptions.InvalidGPSCoordinateException;
import graphexceptions.InvalidInputException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import main.Config;
import main.Logger;

public class HierarchyMapGraph implements Serializable
{
	private static final long serialVersionUID = 7707750747363965525L;
	private HashMap<Integer,HierarchyMapNode> nodes;
	//Speichere in der HashMap in welcher Kante die kontrahierten IDs gespeichert sind.
	//Dies ist wichtig fuer die getNextCrossings Methode.
	private ArrayList<HashMap<Integer,HierarchyMapEdge>> contractedNodeIdToEdge;
	
	
	/**
	 * Kontruktor, der lediglich die Datenstrukturen passend initialisiert.
	 */
	public HierarchyMapGraph()
	{
		nodes=new HashMap<Integer,HierarchyMapNode>();
		contractedNodeIdToEdge=new ArrayList<HashMap<Integer,HierarchyMapEdge>>();
	}
	
	/**
	 * Gebe die Anzahl der im Graph gespeicherten Knoten aus.
	 * @return Anzahl der Knoten.
	 */
	public int getSize() {
		return nodes.size();
	}
	
	/**
	 * Diese Methode gibt einen Iterator ueber alle Knoten des Graphen zurueck.
	 * @return Iterator von HierarchyMapNodes
	 */
	public Iterator<HierarchyMapNode> getNodeIt(){
		return nodes.values().iterator();
	}


	/**
	 * 	/**
	 * Fuegt eine Kante des Typs HierarchyMapEdge eines bestimmten Levels in die Kantenmenge des Graphen ein.
	 * Sollte die Kante ein Level haben, der groesser ist als maxLevel, wird maxLevel aktualisiert.
	 * @param start Startknoten
	 * @param end Zielknoten
	 * @param wayID Die ID der Kante entsprechend der OSM Daten.
	 * @param length Laenge der Kante in Dezimtern.
	 * @param streetType Strassentyp der Kante.
	 * @param level Level auf dem die Kante eingefuegt werden soll.
	 * @param contractedNodeIDs Kontrahierte Knoten in der Kante.
	 * @param distOfNodes Die Abstaender der kontrahierten Knoten zum Startknoten.
	 */
	private void insertEdge(int startNodeUID, int endNodeUID, int wayID, int length, StreetType streetType, byte level, int contractedNodeIDs[], int distOfNodes[])
	{
		HierarchyMapNode start=getNode(startNodeUID);
		HierarchyMapNode end=getNode(endNodeUID);
		HierarchyMapEdge e=new HierarchyMapEdge(start,end,wayID,length,streetType,level,contractedNodeIDs,distOfNodes);
		start.addOutgoingEdge(e);
		end.addIncomingEdge(e);
	}
	
	/**
	 * Fuegt eine Kante des Typs HierarchyMapEdge eines bestimmten Levels in die Kantenmenge des Graphen ein.
	 * Sollte die Kante ein Level haben, der groesser ist als maxLevel, wird maxLevel aktualisiert.
	 * @param startNodeUID UID des Startknotens
	 * @param endNodeUID UID des Zielknotens
	 * @param wayID Die ID der Kante entsprechend der OSM Daten.
	 * @param length Laenge der Kante in Dezimtern.
	 * @param streetType Strassentyp der Kante.
	 * @param level Level auf dem die Kante eingefuegt werden soll.
	 */
	public void insertEdge(int startNodeUID, int endNodeUID, int wayID, int length, StreetType streetType, byte level)
	{
		this.insertEdge(startNodeUID, endNodeUID, wayID, length, streetType, level, new int[0],new int[0]);
	}
	
	/**
	 * Fuegt zwei Kante des Typs HierarchyMapEdge eines bestimmten Levels in die Kantenmenge des Graphen ein und
	 * zwar analog zu insertEdge jeweils eine Kante von startNodeUID nach endNodeUID und in die andere Richtung.
	 * Sollte die Kante ein Level haben, der groesser ist als maxLevel, wird maxLevel aktualisiert.
	 * @param startNodeUID
	 * @param endNodeUID
	 * @param wayID
	 * @param length
	 * @param streetType
	 * @param level
	 */
	protected void insertEdgeBothWays(int startNodeUID, int endNodeUID, int wayID, int length, StreetType streetType, byte level) {
		this.insertEdge(startNodeUID, endNodeUID, wayID, length, streetType, level);
		this.insertEdge(endNodeUID,startNodeUID, wayID, length, streetType, level);
	}
	
	/**
	 * Fuegt einen Knoten des Typs HierarchyMapNode in die Knotenmenge des Graphen ein.
	 * @param uid Die UID des Knoten.
	 * @param numberIncomingEdges Die mindeste Zahl an eingehenden Kanten. Werden weniger ausgehende Kanten 
	 * eingefuegt als hier angegeben, kommt es zu Fehlern.
	 * @param numberOutgoingEdges Die mindeste Zahl an ausgehenden Kanten. Werden weniger ausgehende Kanten 
	 * eingefuegt als hier angegeben, kommt es zu Fehlern.
	 */
	public void insertNode(int uid,int numberIncomingEdges, int numberOutgoingEdges)
	{
			HierarchyMapNode a= new HierarchyMapNode(uid,numberIncomingEdges,numberOutgoingEdges);
			nodes.put(a.getUID(),a);
	}
	
	/**
	 * Fuegt einen Knoten des Typs HierarchyMapNode in die Knotenmenge des Graphen ein.
	 * @param uid Die UID des Knoten.
	*/
	public void insertNode(int uid)
	{
			this.insertNode(uid, 0, 0);
	}
	
	/**
	 * Gibt eine HierarchyMapNode mit uebergebener ID zurueck
	 * @param uid der Node
	 */
	public HierarchyMapNode getNode(int uid) {
		return nodes.get(uid);
	}
	
	/**
	 *In dieser Methode werden Kantenzuege, die z.B. Kurven darstellen, zusammengefasst 
	 * um die darauffolgenden Berechnung im Graphen zu vereinfachen. Wird sie mit Level 0 aufgerufen,
	 * so werden zusammengefasste Kanten und betreffende Knoten geloescht und die ID und Distanzen zum
	 * Vorgaenerknoten in der Kante gespeichert. Ansonsten werden die Levels der betreffenden
	 * Kanten um ein Level verringert. Neue Kanten werden immer mit dem uebergebenen Level
	 * eingefuegt. Die Level der Knoten werden dabei geupdated.
	 * @param level Das Level auf denen Kanten zusammengefasst werden sollen.
	 */
	public void contractEdges(byte level){
		//Speichere die IDs der Knoten, die geloescht werden sollen und loesche sie dann alle am Ende
		HashSet<Integer> nodesToBeDeleted=new HashSet<Integer>();
		
		for (HierarchyMapNode currentNode : nodes.values()) {
			//Betrachte den Fall, dass der Knoten auf einer Einbahnstrasse liegt oder
			//das Ende einer Sackgasse ist. Im ersteren Fall koennen Kanten zusammengefasst werden.
			if(currentNode.getNumberOfIncomingEdges(level)==1 && 
					currentNode.getNumberOfOutgoingEdges(level)==1) {
				HierarchyMapEdge inEdge=currentNode.getIncomingEdgesByHierarchy(level).iterator().next();
				HierarchyMapEdge outEdge=currentNode.getOutgoingEdgesByHierarchy(level).iterator().next();
				HierarchyMapNode neighbour1=inEdge.getNodeStart();
				HierarchyMapNode neighbour2=outEdge.getNodeEnd();
				//Unterscheidet sich der Streettype oder ist currentNode nur das Ende einer
				//Sackgasse, dann werden keine Kanten zusammengefasst.
				if(neighbour1.getUID()!=neighbour2.getUID() && inEdge.getType()==outEdge.getType()) {
					this.combineEdges(inEdge, outEdge, level);
					if(level==0) {
						nodesToBeDeleted.add(currentNode.getUID());
					} else {
						currentNode.setLevel((byte) (level-1));
					}
				}	
			}
			
			//Ueberpruefe nun ob currentNode auf einem beidseitig befahrbaren Strassenzug
			//liegt, d.h. genau zwei Nachbarn besitzt, mit denen es durch jeweils eine
			//eine ausgehende und eingehende Kante verbunden ist.
			if(currentNode.getNumberOfIncomingEdges(level)==2 && 
					currentNode.getNumberOfOutgoingEdges(level)==2) {
				Iterator<HierarchyMapEdge> itIn=currentNode.getIncomingEdgesByHierarchy(level).iterator();
				Iterator<HierarchyMapEdge> itOut=currentNode.getOutgoingEdgesByHierarchy(level).iterator();
				HierarchyMapEdge inEdge1=itIn.next();
				HierarchyMapEdge inEdge2=itIn.next();
				HierarchyMapEdge outEdge1=itOut.next();
				HierarchyMapEdge outEdge2=itOut.next();
						
				//Die erste ausgehende und eingehende Kanten gehoeren nicht zu gleichen
				//Knotenpaar. Versuche zu tauschen. Wenn dies danach nicht gilt, sind
				//mehr als drei Nachbarknoten involviert (Vorgaenger und Nachfolger)
				//und currentNode kann nicht entfernt werden.
				if(inEdge1.getNodeStart().getUID()!=outEdge1.getNodeEnd().getUID()) {
					HierarchyMapEdge temp=inEdge1;
					inEdge1=inEdge2;
					inEdge2=temp;
				} 
							
				if (inEdge1.getNodeStart().getUID()==outEdge1.getNodeEnd().getUID() && 
						inEdge2.getNodeStart().getUID()==outEdge2.getNodeEnd().getUID())
				{
					//Fall das erste Paar Strassenkanten kompatibel ist, kann es zusammengefasst werden.
					this.combineEdges(inEdge1, outEdge2, level);
					
					//Fall das zweite Paar Strassenkanten kompatibel ist, kann es zusammengefasst werden.
					this.combineEdges(inEdge2, outEdge1, level);
					
					//Im Fall, dass dbeide Kantenpaare zusammengefasst wurden, muss currentNode
					//entweder geloescht werden oder sein Level verringert.
					if(inEdge1.getType()==outEdge2.getType() && inEdge2.getType()==outEdge1.getType()) {
						if (level==0) {
							nodesToBeDeleted.add(currentNode.getUID());
						} else {
							currentNode.setLevel((byte) (level-1));
						}
					}
				}				
			}
		}
		//Loesche noch alle Knoten, die nun ueberfluessig sind.
		for (Integer uid : nodesToBeDeleted) {
			nodes.remove(uid);
		}
		//Falls das aktuelle Level 0 ist, wurden Kanten geloescht und die Referenzen auf die
		//kontrahierten Knoten muessen aktualisiert werden.
		if(level==0) {
			this.createContractedNodeToEdgeRefs();
		}
	}
		
	
	/**
	 * Hilfsmethode fuer die contractEdges Methode, welche zwei Arrays und eine Integer in ein Array unter
	 * Erhaltung der Reihenfolge zusammenfuegt.
	 * @param nodeIDsEdge1 Erstes Array.
	 * @param deletedNodeID Gegebene Integer.
	 * @param nodeIDsEdge2 Zweites Array
	 * @return Neues Array, das Integers in Reihenfolge: erstes Array, int, zweites Array enthaelt.
	 */
	private int[] combindeArrays(int nodeIDsEdge1[], int deletedNodeID, int nodeIDsEdge2[]) {
		int [] result=new int[nodeIDsEdge1.length+nodeIDsEdge2.length+1];
		int i=0;
		for (int k = 0; k < nodeIDsEdge1.length; k++) {
			result[i]= nodeIDsEdge1[k];
			i++;
		}
		result[i]=deletedNodeID;
		i++;
		for (int k = 0; k < nodeIDsEdge2.length; k++) {
			result[i]= nodeIDsEdge2[k];
			i++;
		}
		return result;
	}
	
	/**
	 * Hilfsmethode fuer die contractEdges Methode. Hier werden zwei Kanten zu einer zusammengefuegt.
	 * Die alten Kanten werden dabei entweder geloescht und der geloeschte Knoten in der neuen Kante gespeichert
	 * (bei Level 0). Ansonsten werden das Level der alten Kanten um 1 verringert.
	 */
	private void combineEdges(HierarchyMapEdge e1, HierarchyMapEdge e2, byte level) {
		//Falls das Level 0 ist, loesche. Ansonsten  verringere Level der betreffenden Kanten.
		if(e1.getType()!=e2.getType()) {
			return;
		}
		HierarchyMapNode n1=e1.getNodeStart();
		HierarchyMapNode n2=e1.getNodeEnd(); //=e2.getNodeStart() nach Annahme
		HierarchyMapNode n3=e2.getNodeEnd();
		if(level==0) {
			n1.deleteOutgoingEdge(e1);
			n3.deleteIncomingEdge(e2);
		} else {
			e1.setLevel((byte) (level-1));
			e2.setLevel((byte) (level-1));
		}
		//Fuege Abkuerzung ein und speichere die durch Kontraktion geloeschten KnotenIDs, falls das Level 0 ist.
		int tempContractedNodeIDs[];
		int tempNodeDist[];
		tempContractedNodeIDs=this.combindeArrays(e1.getContractedNodeIDs(), 
				n2.getUID(), e2.getContractedNodeIDs());
		int distances2[]=e2.getContractedNodeDistances();
		for (int i = 0; i < distances2.length; i++) {
			distances2[i]+=e1.getLength();
		}
		tempNodeDist=this.combindeArrays(e1.getContractedNodeDistances(),e1.getLength(), 
				distances2);
		this.insertEdge(n1.getUID(), n3.getUID(),
				e1.getUID(), e1.getLength()+e2.getLength(), 
				e1.getType(),level, tempContractedNodeIDs,tempNodeDist);
	}
	
	/**
	 * Diese Hilfsmethode wird in der contractEdges() Methode aufgerufen, falls Kanten geloescht wurden und
	 * in den Kanten die IDs von kontrahierten Knoten gespeichert wurden. Dann traegt diese Methode in die
	 * HashMap ein, welche kontrahierte Knoten in welcher Edge gespeichert ist. Ein Knoten ist hoechstens auf
	 * zwei HierarchyMapEdges, also werden hierfuer zwei HashMaps benutzt. Eine Realisierung als Tupel wurde
	 * aus speichereffizienzgruenden vermieden.
	 */
	private void createContractedNodeToEdgeRefs() {
		contractedNodeIdToEdge.clear();
		contractedNodeIdToEdge.add(new HashMap<Integer, HierarchyMapEdge>());
		contractedNodeIdToEdge.add(new HashMap<Integer, HierarchyMapEdge>());
		Iterator<HierarchyMapNode> curNode= this.getNodeIt();
		while(curNode.hasNext()) {
			HierarchyMapEdge outEdges[]=curNode.next().getOutgoingEdges();
			for (HierarchyMapEdge e : outEdges) {
				for (int nodeId : e.getContractedNodeIDs()) {
					if(contractedNodeIdToEdge.get(0).containsKey(nodeId)) {
						contractedNodeIdToEdge.get(1).put(nodeId, e);
					} else {
						contractedNodeIdToEdge.get(0).put(nodeId, e);
					}
				}
			}
		}
		
	}
	
	/**
	 * Diese Methode entfernt Schlingen und parallele Kanten. Wird sie mit Level 0 aufgerufen,
	 * so werden die betreffen Kanten geloescht. Ansonsten werden die Levels der betreffenden
	 * Kanten um ein Level verringert. Die Level der Knoten werden anschliessend geupdated.
	 * @param level Das Level, aus dem die Schlingen und parallele Kanten entfernt werden sollen.
	 */
	public void deleteSelfLoopAndParallelEdges(byte level){
		//Speichere das neue Level, auf das Knoten gesetzt werden sollen, falls das gegebene level nicht null ist
		byte newLevel=0;  
		if(level!=0) {
			newLevel=(byte) (level-1);
		}
		HashSet<Integer> neighbourUIDs=new HashSet<Integer>();
		for (HierarchyMapNode currentNode : nodes.values()) {
			neighbourUIDs.clear(); //Starte mit leere Liste von NachbarIDs.
			//Um Schlingen zu finden, fuege den currentNode in die neighbourUIDS ein.
			//Fuege anschliessend alle KnotenUIDS, die durch eine ausgehende Kante
			//erreicht werden, in die Menge neighbourUIDs. Wenn dann eine UID doppelt
			//vorkommt, handelt es sich entweder um eine parallele Kante oder um eine
			//Schlinge.
			neighbourUIDs.add(currentNode.getUID());
			Iterator<HierarchyMapEdge> outgoingEdgeIt=currentNode.getOutgoingEdgesByHierarchy(level).iterator();
			HierarchyMapEdge currentEdge;
			while(outgoingEdgeIt.hasNext()) {
				currentEdge=outgoingEdgeIt.next();
				if(neighbourUIDs.contains(currentEdge.getNodeEnd().getUID())) {
					//Fall, dass es sich um eine Schlinge handelt
					if(currentEdge.getNodeStart().getUID()==currentEdge.getNodeEnd().getUID()) {
						//Falls das Level 1 ist loesche, sonst senke Level
						if (level==1) {
							currentNode.deleteIncomingEdge(currentEdge);
							currentNode.deleteOutgoingEdge(currentEdge);
						} else {
							currentEdge.setLevel(newLevel);
						}
						
					} else {
					//Es gibt also zwei Verbindungen zu einem Nachbarn
						Iterator<HierarchyMapEdge> outgoingEdgeIt2=currentNode.getOutgoingEdgesByHierarchy(level).iterator();
						HierarchyMapNode neighbour=currentEdge.getNodeEnd();
						HierarchyMapEdge secondEdge=null;
						//Finde, die zweite Kante, die schon zum Nachbarn existiert
						while(outgoingEdgeIt2.hasNext()) {
							secondEdge=outgoingEdgeIt2.next();
							if(secondEdge.getNodeEnd().getUID()==neighbour.getUID() && secondEdge!=currentEdge) {
								break;
							}
						}
						//DoppelCheck: Machen wir auch keine Dummheiten. Sind es wirklich zwei parallele Kanten
						if(currentEdge.getNodeStart().getUID()==secondEdge.getNodeStart().getUID()
								&& currentEdge.getNodeEnd().getUID()==secondEdge.getNodeEnd().getUID()) {
							//Loesche nun die laengere bzw. langsamere der beiden Kanten beziehungsweise verringere ihr Level
							if(currentEdge.getWeight()<secondEdge.getWeight()) {
								if(level==1) {
									currentNode.deleteOutgoingEdge(secondEdge);
									neighbour.deleteIncomingEdge(secondEdge);
								} else {
									secondEdge.setLevel(newLevel);
									neighbour.updateLevel();
								}
							} else {
								if(level==1) {
									currentNode.deleteOutgoingEdge(currentEdge);
									neighbour.deleteIncomingEdge(currentEdge);
								} else {
									currentEdge.setLevel(newLevel);
									neighbour.updateLevel();
								}
							}
						} else {
							//Falls dieser Fall eintritt, muss irgendetwas tierisch schief gelaufen sein.
							Logger.getInstance().log(" deleteSelfLoopAndParallelEgges()", "Schwerer Fehler in deleteSelfLoopAndParallelEgges(). Zwischen den" +
									"Knoten mit UIDs "+currentNode.getUID()+" und "+neighbour.getUID()+"" +
									"gibt es eine parallele Kante, bei deren Loeschen ein Fehler wegen inkonsistenter" +
									"Daten aufgetreten ist.");
						}
					}
				} else {
					neighbourUIDs.add(currentEdge.getNodeEnd().getUID());
				}		
			}
			//Falls Kanten geloescht wurden, wird das Level des Knoten geupdated.
			currentNode.updateLevel();
		}
	}
	
	
	/**
	 * Diese Funktion berechnet den 2-Core des HierarchyGraphen, wobei nur die Knoten und
	 * Kanten auf dem gegebenen Level betrachtet werden. Auf Levels darueber duerfen noch
	 * keine Knoten und Kanten existieren. In dieser Funktion werden dabei zunaechst die
	 * Levels aller Knoten und Kanten, die sich auf dem gegebenen Level befinden, erhoeht.
	 * Anschliessend werden die Levels aller Knoten und Kanten, die nicht zum 2-Core gehoeren,
	 * wieder um 1 verringert.
	 * @param level Hoechste Hierarchie die bereits berechnet wurde (level+1 wird berechnet)
	 */
	public void computeTwoCore(byte level){
		ArrayList<HierarchyMapNode> nodesWithOneNeighbour=new ArrayList<HierarchyMapNode>();
		for (HierarchyMapNode currentNode : this.nodes.values()) {
			//Nun werden die Levels aller Knoten und Kanten, die sich schon auf
			//dem derzeitig betrachtetetn Level level befinden, erhoeht.
			if(currentNode.getLevel()==level) {
				//Ueberpruefe gleichzeitig, ob aktueller Knoten auf dem hoechsten Level nur einen Nachbarn hat
				if(hasOneNeighbour(currentNode,level)) {
					nodesWithOneNeighbour.add(currentNode);
				}
				//Es genuegt, jeweils nur ausgehende Kanten zu betrachten
				for (HierarchyMapEdge outEdge : currentNode.getOutgoingEdgesByHierarchy(level)) {
					outEdge.increaseLevel();
				}
				//Isolierte Knoten sollen nicht geliftet werden
				if(!currentNode.isIsolated(level)) {
					currentNode.increaseLevel();
				}	
			}
		}
		byte newLevel=(byte)(level+1);
		HierarchyMapEdge outEdge=null;
		HierarchyMapEdge inEdge=null;
		boolean anotherOutEdge=true;
		boolean anotherInEdge=true;
		for (HierarchyMapNode currentNode : nodesWithOneNeighbour) {
			HierarchyMapNode neighbour=currentNode;
			while(hasOneNeighbour(neighbour,newLevel)) {
				neighbour.setLevel(level);
				anotherOutEdge=false;
				anotherInEdge=false;
				//Falls existent, verringere das Level der ausgehende Kante
				if(neighbour.getNumberOfOutgoingEdges(newLevel)==1) {
					outEdge=neighbour.getOutgoingEdgesByHierarchy(newLevel).iterator().next();
					outEdge.setLevel(level);
					anotherOutEdge=true;
				}
				//Falls existent, verringere das Level der eingehenden Kante
				if(neighbour.getNumberOfIncomingEdges(newLevel)==1) {
					inEdge=neighbour.getIncomingEdgesByHierarchy(newLevel).iterator().next();
					inEdge.setLevel(level);
					anotherInEdge=true;
				}
				//Falls es eine eingehende oder ausgehende Kante gab, gab es einen Nachbarn, mit dem
				//weitergearbeitet wird, fall er nun auch selbst nur einen Nachbarn besitzt.
				if(anotherOutEdge) {
					neighbour=outEdge.getNodeEnd();
				} else if(anotherInEdge) {
					neighbour=inEdge.getNodeStart();
				} else {
					break;
				}
			} 
			currentNode.setLevel(level);
		}
	}
	
	/**
	 * Funktion, die ueberpruef, ob gegebene Node n genau einen Nachbarn hat, mit dem sie
	 * ueber Kanten mit mindestens Level level verbunden ist. Fuer die Korrektheit wird angenommen,
	 * dass es im Graphen keine parallelen Kanten gibt.
	 * @param n
	 * @param level 
	 * @return
	 */
	private boolean hasOneNeighbour(HierarchyMapNode n, byte level) {
		if(n.isIsolated(level)) {
			return false; //Fall: Isolierter Knoten haben keine Nachbarn
		}
		if(n.getNumberOfIncomingEdges(level)<=1 && 
				n.getNumberOfOutgoingEdges(level)<=1) 
		{
			if(n.getNumberOfOutgoingEdges(level)==1 && 
				n.getNumberOfIncomingEdges(level)==1) 
			{
				if(n.getNeighbours(level).iterator().next().getUID()==
					n.getPredecessors(level).iterator().next().getUID()) 
				{
					return true; //Ein Vorgaenger und ein Nachfolger, beide gleich, also nur ein Nachbar
				} else {
					return false; //Vorgaenger und Nachfolger verschieden, also mehr als ein Nachbar
				}
			} 
			else 
			{
				return true; //Entweder nur einen Vorgaenger oder nur einen Nachfolger, also nur einen Nachbar
			}
		}
		return false;
	}
	
	/**
	 * Mit dieser Methode wird die hoechstens zwei moeglichen Pfade zur naechsten Kreuzung berechnet. Ist der uebergebene
	 * Knoten currentNodeID schon eine Kreuzung, so wird er einfach zurueckgegeben als Pfad der Laenge 0. Ansonsten ist currentNodeID
	 * ein kontrahierter Knoten, das heisst ein Knoten der auf maximal zwei Kanten liegt. 
	 * Es wird jeweils in vorwaerts oder rueckwaerts Richtung zur naechsten Kreuzung gegangen und die IDs auf dem Weg, einschliesslich
	 * der currentNodeId und der ID der Kreuzung, gespeichert. Die Zeit und die Strecke zu den Kreuzungen werden
	 * ebenfalls im Pfad gespeichert.
	 * @param currentNodeID Betrachteter Knoten
	 * @param forward Suchrichtung
	 */
	public ArrayList<Path> getNextCrossingIDs(int currentNodeID, boolean forward) throws InvalidInputException {
		ArrayList<Path> result= new ArrayList<Path>();
		if(nodes.containsKey(currentNodeID)) { //Falls, dass der uebergebener Knoten schon eine Kreuzung ist
			Path pathToCrossing = new Path();
			pathToCrossing.appendNode(currentNodeID);
			pathToCrossing.setLength(0);
			pathToCrossing.setTime(0);
			pathToCrossing.setPathAsReconstructed();
			result.add(pathToCrossing);
		} else {
			for (int i = 0; i < contractedNodeIdToEdge.size(); i++) { //Fall, dass uebergebener Knoten auf Kante liegt
				if(contractedNodeIdToEdge.get(i).containsKey(currentNodeID)) {
					Path pathToCrossing = new Path();
					HierarchyMapEdge e1=contractedNodeIdToEdge.get(i).get(currentNodeID);
					int lengthToCrossing;
					if(forward) {
						//Fuege nun die Knoten, von dem currentNode an, die in der Kante in Vorwaertsrichtung stehen,
						//in den Pfad ein
						boolean found=false;
						for (int j = 0 ; j<e1.getContractedNodeIDs().length ; j++) {
							if(e1.getContractedNodeIDs()[j]==currentNodeID) {
								found=true;
							}
							if(found) {
								pathToCrossing.appendNode(e1.getContractedNodeIDs()[j]);
							}
						}
						//Fuege noch die Kreuzung in den Pfad ein
						pathToCrossing.appendNode(e1.getNodeEnd().getUID());
						//Berechne Entfernung zur Kreuzung
						lengthToCrossing=e1.getLength()-e1.getContractedNodeDistance(currentNodeID);
					} else {
						//Fuege nun die Knoten, von der Kreuzung bis einschliesslich des currentNode in den Pfad ein. 
						//Beachte, dass dies die Reihenfolge im Pfad ist, in der
						//der Gesamtpfad nachher durchlaufen wird.
						pathToCrossing.appendNode(e1.getNodeStart().getUID());
						for (int j = 0; j <e1.getContractedNodeIDs().length; j++) {
							pathToCrossing.appendNode(e1.getContractedNodeIDs()[j]);
							if(e1.getContractedNodeIDs()[j]==currentNodeID) {
								break;
							}
						}
						//Berechne Entfernung zur Kreuzung
						lengthToCrossing=e1.getContractedNodeDistance(currentNodeID);
					}
					//Noch die Weglaenge und benoetigte Zeit zur Kreuzung aktualisieren
					pathToCrossing.setLength(lengthToCrossing);
					pathToCrossing.setTime(Math.round(lengthToCrossing*Config.getSpeedFactor(e1.getType())));
					pathToCrossing.setPathAsReconstructed();
					//Pfad zur Menge der Pfade zu einer Kreuzung hinzufuegen
					result.add(pathToCrossing);
				}
				
			}
		}
		return result;
	}
	
	/**
	 * Mit dieser Methode koennen die Kanten zurueckgegeben werden, auf denen die uebergebene ID als
	 * kontrahierter Knoten gespeichert ist. Falls der Knoten nicht kontrahiert wurde, also auf keiner Kante liegt,
	 * wird eine leere ArrayList zurueckgegeben, ansonsten entweder eine ArrayList mit einer Kante oder zwei, je nachdem
	 * auf wieviel Kanten der Knoten liegt.
	 * @param nodeID ID des betrachteten Knotens.
	 * @return ArrayList mit den Kanten, in denen die KnotenID gespeichert ist.
	 */
	public ArrayList<HierarchyMapEdge> getContractedEdge(int nodeID) {
		ArrayList<HierarchyMapEdge> result = new ArrayList<HierarchyMapEdge>();
		for (int i = 0; i < contractedNodeIdToEdge.size(); i++) {
			if(contractedNodeIdToEdge.get(i).containsKey(nodeID)) {
				result.add(contractedNodeIdToEdge.get(i).get(nodeID));
			}
		}
		return result;
	}
		
	/**
	 * Mit dieser Methode werden die Kantenlaengen korrigiert, da vom Import Kanten mit der Laenge
	 * Integer.MAX_VALUE eingefuegt werden, wenn die KnotenGPS nicht zur Verfuegung steht und daher
	 * keine Laengenberechnung moeglich ist.
	 */
	public void correctLength() {
		Iterator<HierarchyMapNode> nodeIt=this.getNodeIt();
		while(nodeIt.hasNext()) {
			HierarchyMapEdge outEdges[]=nodeIt.next().getOutgoingEdges();
			for(HierarchyMapEdge e:outEdges) {
				if(e.getLength()==Config.initialTemporaryLengthForEdges) {
					if(e.getNodeStart().hasGPS() && e.getNodeEnd().hasGPS()) {
						try {
							e.setLength(e.getNodeStart().getGPS().distanceTo(e.getNodeEnd().getGPS()));
						} catch (InvalidGPSCoordinateException e1) {
							//Das sollte nie passieren, da vorher die Existenz der GPS ueberprueft wurde.
							e1.printStackTrace();
						}
					} else {
						Logger.getInstance().log("HierarchyMapGraph.correctLength", "Warnung: Folgende Kante mit ungueltiger Laenge im HierarchyMapGraph."+e);
					}
				}
			}
		}
	}
	
	/**
	 * Erhoeht das Level aller Knoten und Kanten, welche das uebergebene Level besitzen,
	 * um 1.
	 * @param level Das Level der Knoten und Kanten, der Level erhoeht werden soll.
	 */
	public void increaseHighestLevel(byte level) {
		Iterator<HierarchyMapNode> nodeIt=this.getNodeIt();
		while(nodeIt.hasNext()) {
			HierarchyMapNode curNode= nodeIt.next();
			if(curNode.getLevel()==level) {
				curNode.increaseLevel();
			}
			for(HierarchyMapEdge outEdge:curNode.getOutgoingEdges()) {
				if(outEdge.getLevel()==level) {
					outEdge.increaseLevel();
				}
			}
		}
	}
	
	public String toString() {
		String tempString="";
		
		int dH[] = new int[Config.maxHierarchyLayer+1];
		int kanten = 0;
		
		Iterator<HierarchyMapNode> iterator = this.getNodeIt();
		while(iterator.hasNext()) {
			HierarchyMapNode node = iterator.next();
			
			dH[node.getLevel()] ++;
			kanten += node.getNumberOfOutgoingEdges();
		}
		for (int i = 0 ; i<dH.length;i++){
			tempString += dH[i] + " ";
		}
		tempString += "\n"+kanten;
		return tempString;
	}
}
