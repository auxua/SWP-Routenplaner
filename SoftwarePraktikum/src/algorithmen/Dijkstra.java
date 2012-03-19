/**
 * 
 */
package algorithmen;

import graphenbib.HierarchyMapEdge;
import graphenbib.HierarchyMapGraph;
import graphenbib.HierarchyMapNode;
import graphenbib.MapEdge;
import graphenbib.MapGraph;
import graphenbib.MapNode;
import graphenbib.Path;
import graphexceptions.EmptyInputException;
import graphexceptions.InvalidInputException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import main.Config;
import main.Logger;

/**
 * Da diese Klasse keine eigenen Variablen besitzt
 * und auch die Zwischenergebnisse ( z.B: PrQueue oder Suchbaeume) der enthaltenen Algorithmen aus Speichergruenden wieder geloescht werden sollen,
 * wurden alle Funktionen als static markiert.
 * <p>
 * Diese Klasse besteht im Wesentlichen aus 2 Teilen.
 * Der erste Teil besteht aus der Methode neighbourhood sowie ihre private Hilfsmethode updateNeighbours.
 * Mit diesen Funktion kann fuer einen HierarchyMapGraph die Nachbarschafte eines Knoten berechnet werden.
 * <p> 
 * Die 2te Haelfte der Klasse stellt einen gewohnlichen Wegalgorithmus zur Verfuegung.
 * Sie enthaelt 2 verschiedne Varianten der Methode bidirectional.
 * Je nach uebergebenem Parameter wird der kuerzeste Weg zwischen 2 Nodes auf einem MapGraph oder auf einem HierarchyMapGraph verwendet werden.
 * Da diese Wegberechnung allerdings eventuell aufgebaute Hierarchien ignoriert, ist die Laufzeit dieser Methoden nicht ideal und wird daher nicht von der GUI/Main
 * verwendet. Ihre Aufgabe liegt vielmehr in den Test, bei denen der von dieser Klasse gefundene Weg als kuerzester/schnellster Weg definiert wurde, mit Hilfe dessen
 * ueberprueft werden kann ob die Ergebnisse der Query richtig sind. 
 * 
 */
public class Dijkstra {

	static boolean debug = false; //falls True, werden zusuetzliche Infos auf der Konsole ausgegeben
	
	private static Logger logger = Logger.getInstance();
	
	private static void updateNeighbours(HashMap<Integer,Vertex> nodes, PrQueue queue, HierarchyMapEdge newEdge, Vertex node, boolean mode) throws Exception{
		//mode = true bedeutet vorwaerts, sonst ruckwarts
		if (mode){
			if (newEdge.getNodeEnd().getUID() == node.node.getUID()) return;	// ignoriere Zykel
		}else{
			if (newEdge.getNodeStart().getUID() == node.node.getUID()) return; // ignoriere Zykel
		}
		
		Vertex newNode = null; //Vertex ist die Klasse die Infos, wie Vorgaenger, Entfernung zum Start speichert
		if (mode){ // 
			if ( nodes.containsKey(newEdge.getNodeEnd().getUID())){
				// Vertex existiert schon => ueberpruefe ob neuer Weg besser ist
				newNode = nodes.get(newEdge.getNodeEnd().getUID());
				if (newNode.getDist() > node.getDist() + newEdge.getWeight()){
					newNode.setDist( node.getDist() + newEdge.getWeight() );
					queue.update(newNode);	//pruefe ob der Vertex umsortiert werden muss
				}
				return;
			}	
		}else{
			if ( nodes.containsKey( newEdge.getNodeStart().getUID() )){ 
				// Vertex existiert schon => ueberprduefe ob neuer Weg besser ist
				newNode = nodes.get(newEdge.getNodeStart().getUID());
				if (newNode.getDist() > node.getDist() + newEdge.getWeight()){
					newNode.setDist( node.getDist() + newEdge.getWeight() );
					queue.update(newNode);	//pruefe ob der Vertex umsortiert werden muss
				}
				return;
			}		
		}
		try{ //Wenn wir bis hier kommen, existiert der Vertex noch nicht => wir legen einen neuen an.
			if(mode){
				newNode = new Vertex( newEdge.getNodeEnd(), node.getDist() + newEdge.getWeight()); 
				nodes.put(newNode.node.getUID(),newNode);
				queue.insert(newNode);
			}else{
				newNode = new Vertex( newEdge.getNodeStart(), node.getDist() + newEdge.getWeight()); 
				nodes.put(newNode.node.getUID(),newNode);
				queue.insert(newNode);			
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();//Hier sollte man nie hinkommen
		}

	}
	

	/**
	 * Diese Methode berechnet die Entfernung des H-ten Nachbarn zum Startknoten auf einem bestimmten Level des HierarchyMapGraph.
	 * Die Definition des H (default 50) findet sich in Main.Constants, sollte allerdings KEINESFALLS zur Laufzeit veraendert werden,
	 * da sonst die berechneten Nachbarschaften keinerlei Aussagekraft mehr besitzen. 
	 * <p>
	 * Warning: diese Methode gibt nur den berechneten Wert zurueck, speichert diesen aber nicht innerhalb des HierarchyMapGraphen.
	 * Dies sollte daher von der aufrufenden Methode uebernommen werden.
	 * 
	 * @param graph der HierarchyMapGraph auf dem zu einem bestimmten Knoten die Nachbarschaft berechnet wird.
	 * @param startnode die ID des Startnodes. Diese sollte sich nach Moeglichkeit auch innerhalb des Graphen befinden,
	 * um nicht sofort bei den Sicherheitsabfragen dieser Methode eine Exception zu verursachen.
	 * @param forward Fuer jeden Knoten kann auf einem Level 2 Nachbarschaften existieren. Da (aufgrund von Einbhnstrassen)
	 * beim Wegfahren von einem Knoten andere Knoten in der unmittelbaren Nachbraschaft liegen als wenn man zu einem Knoten hinfaehrt.
	 * <p>
	 * Daher kann ueber den Mode entschieden werden in welche Richtung die Nachbarschaft aufgebaut werden.
	 * Bei true werden nur OutGoingEdges netrachtet, sonst nur IncomingEdges.
	 * @param level Dies stellt das Level ein auf dem die Nachbarschaft verwendet wird. Ausgehende/Eingehende Kante deren Level zu erging ist, werden dabei ignoriert.
	 * Dadurch steigt die Groesse der naschbarschaften immer weiter an, je groesser dieser Werte gewaehlt wird.
	 * Voraussetzung ist allerdings, das bereits Nodes auf diesem Level existieren (ansonten gibt diese Methode -1 zurueck).
	 * 
	 * @throws Exception Eine Exception wird geworfen, falls ungueltige Eingaben gefunden wurden. 
	 * <p>
	 * Dies kann sein: Leerer Graph oder Startnode gar nicht im Graphen vorhanden.
	 * 
	 * @return um Speicherplatz zu sparen wird hier nicht die Liste aller Nodes die sich in der Nachbarschaft befinden ausgegeben.
	 * <p>
	 * Stattdessen wird die Entfernung des Startknotens zum H-ten Nachbarn ausgegeben.
	 * Andere Methoden koennen so leicht herausfinden ob man sich noch innerhalb der Nachbarschaft befindet, oder diese schon verlassen hat.  
	 * <p>
	 * Sollte ein Knoten keine H Nachbarn haben, gibt diese Methode -1 zurueck.
	 */
	public static long neighbourhood(HierarchyMapGraph graph, int startnode, byte level, boolean forward) throws Exception{
		
		if (graph == null) throw new EmptyInputException("Leerer Graph uebergeben");
		if (graph.getNode(startnode) == null) throw new InvalidInputException("Ungueltige ID uebergeben");
		
		int found = -1; //erster Knoten, der gefunden wird, ist der Startknoten, dieser wird nicht mitgezaehlt bei der Anzahl der Nachbarschaftsknoten
		final HashMap<Integer,Vertex> nodes = new HashMap<Integer,Vertex>(); //Liste in der alle besuchten Knoten gespeichert werden.
		final PrQueue queue = new PrQueue(); //Priority Queue, die alle erreichbaren Knoten enthaelt
		
		final Vertex start = new Vertex(graph.getNode(startnode),0); //Vertex ist die Hilfsklasse, in der zusaetzliche Infos ueber Knoten gespeichert werden.
		nodes.put(startnode, start);
		
		queue.insert(start);
		
		while (queue.getSize() != 0){
			final Vertex node = queue.extractMin();
			final HierarchyMapNode realNode = (HierarchyMapNode) node.node;

			found++;
			if (found == Config.H) return node.getDist(); //Abbruchbedingung erfuellt
			
			Iterator <HierarchyMapEdge> edges = null;

			if (forward){
				edges = realNode.getOutgoingEdgesByHierarchy(level).iterator();
			}else{
				edges = realNode.getIncomingEdgesByHierarchy(level).iterator();
			}
						
			while (edges.hasNext()){ //ueberpruefe alle benachbarten Knoten
				final HierarchyMapEdge newEdge = edges.next();
				updateNeighbours(nodes, queue, newEdge, node, forward);

			}
		} return -1; //wenn man bis hier kommt, gibt es keine H erreichbaren Knoten
	}
	
	private static boolean handleQueues(HashMap<Integer,Vertex> nodes1, PrQueue forward, MapGraph graph, 
									HashMap<Integer,Vertex> nodes2, PrQueue backward, boolean mode) throws Exception{
		//mode = true bedeutet vorwaerts, sonst ruckwarts
		Vertex vertex = null;
		
		final MapNode node;
		if (mode){
			//Laufe vorwaerts
			vertex = forward.extractMin();
			node = (MapNode) vertex.node;
			//Abbruchkriterium: Knoten von beiden Seiten aus "settled", also in keiner Queue mehr
			if (nodes2.containsKey(node.getUID())){ //Ist der selbe Knoten schon von der anderen Seite besucht worden
				if ( nodes2.get(node.getUID()).getDist() < backward.getMinValue()) { //Ist Knoten noch in der anderen Queue?
					forward = null;  //Wir leeren die Queues, damit Abbruchbedingung der WHILE 
					backward = null; //Schleife erfuellt ist
					return true;
				}
			}
		}else{
			//Laufe rueckwaerts
			vertex = backward.extractMin();
			node = (MapNode) vertex.node;
			if (nodes1.containsKey(node.getUID())) {
				if (nodes1.get(node.getUID()).getDist() < forward.getMinValue()) {
					forward = null;
					backward = null;
					return true;
				}				
			}
		}
		
		final long Dist = vertex.getDist();
		
		MapEdge [] edges = null;
		
		if (mode){
			edges = node.getOutgoingEdges();
		}else{
			edges = node.getIncomingEdges();
		}
		
		for (MapEdge newEdge: edges){ //ueberpruefe alle benachbarten Knoten		
			if (( mode && newEdge.getNodeEnd() == node)
			  || ( !mode && newEdge.getNodeStart() == node) ) continue;
		
			Vertex newNode = null;
			if(mode){
				if ( nodes1.containsKey(newEdge.getNodeEnd().getUID()))	{ 
					// Vertex existiert schon => hole ihn ab
					newNode = nodes1.get(newEdge.getNodeEnd().getUID());
				}
			}else{
				if ( nodes2.containsKey(newEdge.getNodeStart().getUID()))	{ 
					newNode = nodes2.get(newEdge.getNodeStart().getUID());					
				}
			}
			if(newNode != null){ //Vergleiche die beiden Wege
				if (newNode.getDist() > Dist + newEdge.getWeight()){
					newNode.setDist( Dist + newEdge.getWeight() );
					newNode.setPredecessor(node.getUID());
					if (mode){
						forward.update(newNode);
					}else{
						backward.update(newNode);
					}
				}
			}else{ //Vertex existiert nicht => lege neuen an
				try{
					if(mode){
						newNode = new Vertex( newEdge.getNodeEnd(), Dist + newEdge.getWeight(), node.getUID()); 
						nodes1.put(newNode.node.getUID(),newNode);
						forward.insert(newNode);
					}else{
						newNode = new Vertex( newEdge.getNodeStart(), Dist + newEdge.getWeight(), node.getUID()); 
						nodes2.put(newNode.node.getUID(),newNode);
						backward.insert(newNode);			
					}
				}
				catch (Exception e)
				{
					e.printStackTrace();//Hier sollte man nie hinkommen
				}
			}
		}
		return false;
	}

	
	/** 
	 * Diese Funktion berechnet die kuerzeste Entfernung zwischen 2 Punkten.
	 * Verwendet wird hierbei ein MapGraph, dadurch ist nur die Verwendung zu
	 * Kontrollzwecken/Tests moeglich, da der MapGraph, durch das Tiling, nur einen Kartenausschnitt darstellt
	 * und zum anderen keinerlei Hierarchien verwendet werden, was sich nicht gerade foerderlich auf die Laufzeit des Algorithmus auswirkt.
	 * <p>
	 * Da diese Methode schon laenger nicht mehr aktualisiert wurde gibt sie noch eine ArrayList zurueck, statt einen Path
	 * 
 	 * @param graph Der Mapgraph auf dem der Weg berechnet werden soll.
 	 * @param startnode  die ID des Startnodes. Diese sollte sich nach Moeglichkeit auch innerhalb des Graphen befinden,
	 * um nicht sofort bei den Sicherheitsabfragen dieser Methode eine Exception zu verursachen.
 	 * @param endnode  die ID des Endnodes. Diese sollte sich nach Moeglichkeit auch innerhalb des Graphen befinden,
	 * um nicht sofort bei den Sicherheitsabfragen dieser Methode eine Exception zu verursachen.
 	 * @throws Exception Exception werden bei fehlerhaften Eingaben geworfen, wie ein leerer Graph oder Start und Ziel befinden sich nicht auf dem Graphen.
 	 * 
 	 *  @return gibt eine Liste aller MapNodes die auf dem kuerzesten Pfad liegen zurueck. Falls kein Weg von Start nach Ziel existiert, wird eine leere Liste zurueckgegeben.
 	 */
	public static ArrayList<MapNode> bidirectional(MapGraph graph, int startnode, int endnode) throws Exception{
		
		if (graph == null) throw new EmptyInputException("Leerer Graph uebergeben");
		//Hier breche ich immer wieder ab, was echt bloed ist und mich nervt das langsam echt!
		if (graph.getNode(startnode) == null || graph.getNode(endnode) == null || startnode == endnode) return new ArrayList<MapNode>();
		
		HashMap<Integer,Vertex> nodes1 = new HashMap<Integer,Vertex>(); // In einzelnen Listen wird gespeichert welcher Knoten.
		HashMap<Integer,Vertex> nodes2 = new HashMap<Integer,Vertex>(); //von welcher Richtung aus schon besucht worden ist.
		
		PrQueue forward = new PrQueue(); //Priority Queue, die alle erreichbaren Knoten enthaelt
		PrQueue backward = new PrQueue(); //Priority Queue, die alle erreichbaren Knoten enthaelt
		
		Vertex start = new Vertex(graph.getNode(startnode),0,0); //Vertex ist die Hilfsklasse, in der zusaetzliche Infos ueber Knoten gespeichert werden.
		Vertex end = new Vertex(graph.getNode(endnode),0,0); //Vertex ist die Hilfsklasse, in der zusaetzliche Infos ueber Knoten gespeichert werden.
		
		nodes1.put(startnode,start); 
		nodes2.put(endnode,end);
		
		forward.insert(start);
		backward.insert(end);
        
		while (forward.getSize() != 0 && backward.getSize() != 0){ //Wenn beide Queues leer, Abbruch
			boolean abort;
			abort = handleQueues(nodes1,forward,graph,nodes2,backward,true); //Berechne vorwaerts
			if(abort) break;
			abort = handleQueues(nodes1,forward,graph,nodes2,backward,false); //Berechne rueckwaerts
			if(abort) break;
		}
		
		long distance = -1;
		Vertex meet = null;
		
		
		//Wir suchen den Punkt ueber den der kuerzeste Weg verlueuft
		for (Vertex currentNode : nodes1.values()){
			
			//ueber pruefe ob Node in beiden Listen vorhanden ist
			
			int ID = currentNode.node.getUID();
			if (nodes2.containsKey(ID) == false){
				continue;  //node noch nicht von anderer Seite ueberprueft => liegt nicht auf kuerzestem Weg
			}
			
			if (distance == -1) {
				distance = nodes1.get(ID).getDist() + nodes2.get(ID).getDist();
				meet = nodes1.get(ID);
				

			}else{
				long newDist = nodes1.get(ID).getDist() + nodes2.get(ID).getDist();
				if (distance > newDist){
					distance = nodes1.get(ID).getDist() + nodes2.get(ID).getDist();
					meet = nodes1.get(ID);
				}
			}
		}
		
		
		if (meet == null) return new ArrayList<MapNode>();
		
		ArrayList<MapNode> route = new ArrayList<MapNode>();

		route.add(graph.getNode(meet.node.getUID() ) );
		Vertex pos = meet;
		
		while (pos.getPredecessor() != 0){ //Ermittle alle Knoten, die vor meet besucht wurden
			Vertex parent = nodes1.get(pos.getPredecessor());
			route.add(0, graph.getNode(parent.node.getUID()));
			pos = parent;
		}
		
		pos = nodes2.get( meet.node.getUID());
		
		while (pos.getPredecessor() != 0){ //Ermittle alle Knoten die nach meet besucht wurden 
			Vertex parent = nodes2.get(pos.getPredecessor());
			route.add(graph.getNode(parent.node.getUID()));
			pos = parent;
		}		
		
		if(debug){ 
			logger.log("Dijkstra.biderectional","Vom Startknoten aus evaluiert: "+nodes1.values().size());
			logger.log("Dijkstra.biderectional","Vom Zielknoten aus evaluiert: "+nodes2.values().size());
			logger.log("Dijkstra.biderectional","Knoten auf der Route: "+route.size());
		}
		
		return route; //gib die Liste zurueck
	}

	private static boolean handleQueues(HashMap<Integer,Vertex> nodes1, PrQueue forward, 
									    HashMap<Integer,Vertex> nodes2, PrQueue backward, boolean mode) throws Exception{
		//mode = true bedeutet vorwaerts, sonst ruckwarts
		Vertex vertex = null;
		
		final HierarchyMapNode node;
		
		if (mode){
			//Laufe vorwaerts
			vertex = forward.extractMin();
			node = (HierarchyMapNode) vertex.node;
			//Abbruchkriterium: Knoten von beiden Seiten aus "settled", also in keiner Queue mehr
			if (nodes2.containsKey(node.getUID())){ //Ist der selbe Knoten schon von der anderen Seite besucht worden
				if ( nodes2.get(node.getUID()).getDist() < backward.getMinValue()) { //Ist Knoten noch in der anderen Queue?
					forward = null;  //Wir leeren die Queues, damit Abbruchbedingung der WHILE
					backward = null; //Schleife erfuellt ist
					return true;
				}
			}
		}else{
			//Laufe rueckwaerts
			vertex = backward.extractMin();
			node = (HierarchyMapNode) vertex.node;
			if (nodes1.containsKey(node.getUID())) {
				if (nodes1.get(node.getUID()).getDist() < forward.getMinValue()) {
					forward = null;
					backward = null;
					return true;
				}
			}
		}

		final Iterator<HierarchyMapEdge> edges;
		
		if (mode){
			edges = node.getOutgoingEdgesByHierarchy((byte)0).iterator();
		}else{
			edges = node.getIncomingEdgesByHierarchy((byte)0).iterator();
		}
		
		while (edges.hasNext()){ //ueberpruefe alle benachbarten Knoten
			final HierarchyMapEdge newEdge = edges.next();
			final HierarchyMapNode newEnd = newEdge.getNodeEnd();
			final HierarchyMapNode newStart = newEdge.getNodeStart();
			final long newDist = newEdge.getWeight();
			final long nodeDist = vertex.getDist();
			if (( mode && newEnd == node)
					|| ( !mode && newStart == node) ) continue;

			Vertex newNode = null;
			if(mode){
				if ( nodes1.containsKey(newEnd.getUID()))	{
					// Vertex existiert schon => hole ihn ab
					newNode = nodes1.get(newEnd.getUID());
				}
			}else{
				if ( nodes2.containsKey(newStart.getUID()))	{ 
					newNode = nodes2.get(newStart.getUID());				
				}
			}
			
			if(newNode != null){ //Vergleiche die beiden Wege
				if (newNode.getDist() > nodeDist + newDist){
					newNode.setDist( nodeDist + newDist );
					newNode.setPredecessor(node.getUID());
					if (mode){
						forward.update(newNode);
					}else{
						backward.update(newNode);
					}
				}
			}else{ //Vertex existiert nicht => lege neuen an
				try{
					if(mode){
						newNode = new Vertex( newEnd, nodeDist + newDist, node.getUID());
						nodes1.put(newNode.node.getUID(),newNode);
						forward.insert(newNode);
					}else{
						newNode = new Vertex( newStart, nodeDist + newDist, node.getUID());
						nodes2.put(newNode.node.getUID(),newNode);
						backward.insert(newNode);
					}
				}
				catch (Exception e)
				{
					e.printStackTrace();//Hier sollte man nie hinkommen
				}
			}
		}
		return false;
	}

	/** 
	 * Diese Funktion berechnet die kuerzeste Entfernung zwischen 2 Punkten.
	 * Verwendet wird hierbei ein HierarchyMapGraph, dennoch ist nur die Verwendung zu
	 * Kontrollzwecken/Tests empfholen, da keinerlei Hierarchien verwendet werden,
	 * wodurch die Laufzeit bei groesserer Entfernung sehr stark ansteigt.
	 *  
 	 * @param graph Der HierarchyMapGraph auf dem der Weg berechnet werden soll.
 	 * @param startnode  die ID des Startnodes. Diese sollte sich nach Moeglichkeit auch innerhalb des Graphen befinden,
	 * um nicht sofort bei den Sicherheitsabfragen dieser Methode eine Exception zu verursachen.
 	 * @param endnode  die ID des Endnodes. Diese sollte sich nach Moeglichkeit auch innerhalb des Graphen befinden,
	 * um nicht sofort bei den Sicherheitsabfragen dieser Methode eine Exception zu verursachen.
	 * 
 	 * @throws Exception Exception werden bei fehlerhaften Eingaben geworfen, wie ein leerer Graph oder Start und Ziel befinden sich nicht auf dem Graphen.
 	 * 
 	 * @return Es wird ein Objekt der Klasse Path zurueckgegeben, das alle Nodes die auf dem Pfad liegen enthaelt. Auf das Objekt wurde bereits reconstructPath ausgefuehrt. 
 	 */
	public static Path bidirectional(HierarchyMapGraph graph, int startnode, int endnode) throws Exception{
		
		if (graph == null) throw new EmptyInputException("Leerer Graph uebergeben");
		if (graph.getNode(startnode) == null || graph.getNode(endnode) == null || startnode == endnode) return new Path();
		
		HashMap<Integer,Vertex> nodes1 = new HashMap<Integer,Vertex>(); // In einzelnen Listen wird gespeichert welcher Knoten.
		HashMap<Integer,Vertex> nodes2 = new HashMap<Integer,Vertex>(); //von welcher Richtung aus schon besucht worden ist.
		
		PrQueue forward = new PrQueue(); //Priority Queue, die alle erreichbaren Knoten enthaelt
		PrQueue backward = new PrQueue(); //Priority Queue, die alle erreichbaren Knoten enthaelt
		
		Vertex start = new Vertex(graph.getNode(startnode),0,0); //Vertex ist die Hilfsklasse, in der zusaetzliche Infos ueber Knoten gespeichert werden.
		Vertex end = new Vertex(graph.getNode(endnode),0,0); //Vertex ist die Hilfsklasse, in der zusaetzliche Infos ueber Knoten gespeichert werden.
				
		nodes1.put(startnode,start); 
		nodes2.put(endnode,end);
		
		forward.insert(start);
		backward.insert(end);
        
		while (forward.getSize() != 0 && backward.getSize() != 0){ //Wenn beide Queues leer, Abbruch
			
			boolean abort;
			abort = handleQueues(nodes1,forward,nodes2,backward,true); //Berechne vorwaerts
			if(abort) break;
			abort = handleQueues(nodes1,forward,nodes2,backward,false); //Berechne rueckwaerts
			if(abort) break;
		}
		
		long distance = -1;
		Vertex meet = null;
		
		
		//Wir suchen den Punkt ueber den der kuerzeste Weg verlueuft
		for (Vertex currentNode : nodes1.values()){
			
			//ueber pruefe ob Node in beiden Listen vorhanden ist
			
			int ID = currentNode.node.getUID();
			if (nodes2.containsKey(ID) == false){
				continue;  //node noch nicht von anderer Seite ueberprueft => liegt nicht auf kuerzestem Weg
			}
			
			if (distance == -1) {
				distance = nodes1.get(ID).getDist() + nodes2.get(ID).getDist();
				meet = nodes1.get(ID);
				

			}else{
				long newDist = nodes1.get(ID).getDist() + nodes2.get(ID).getDist();
				if (distance > newDist){
					distance = nodes1.get(ID).getDist() + nodes2.get(ID).getDist();
					meet = nodes1.get(ID);
				}
			}
		}
		
		
		if (meet == null) return new Path();
		
		ArrayList<HierarchyMapNode> route = new ArrayList<HierarchyMapNode>();

		route.add((HierarchyMapNode) meet.node );
		Vertex pos = meet;
		
		while (pos.getPredecessor() != 0){ //Ermittle alle Knoten, die vor meet besucht wurden
			Vertex parent = nodes1.get(pos.getPredecessor());
			route.add(0, graph.getNode(parent.node.getUID()));
			pos = parent;
		}
		
		pos = nodes2.get( meet.node.getUID());
		
		while (pos.getPredecessor() != 0){ //Ermittle alle Knoten die nach meet besucht wurden 
			Vertex parent = nodes2.get(pos.getPredecessor());
			route.add( (HierarchyMapNode) parent.node );
			pos = parent;
		}		
		
		if(debug){
			logger.log("Dijkstra.biderectional","Vom Startknoten aus evaluiert: "+nodes1.values().size());
			logger.log("Dijkstra.biderectional","Vom Zielknoten aus evaluiert: "+nodes2.values().size());
			logger.log("Dijkstra.biderectional","Knoten auf der Route: "+route.size());
		}
		
		Path result = new Path();
		for (HierarchyMapNode node: route){
			result.appendNode(node);
		}
		//System.out.println("Starte Reconstruct mit "+result.size()+" Knoten");
		result.reconstructPath(graph);
		return result; //gib die Liste zurueck
	}
	
}

