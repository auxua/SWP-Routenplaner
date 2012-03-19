package algorithmen;

import graphenbib.HierarchyMapEdge;
import graphenbib.HierarchyMapGraph;
import graphenbib.HierarchyMapNode;
import graphenbib.Path;
import graphenbib.PathExtendedDec;
import graphexceptions.EmptyInputException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import main.Config;

/**
 * Hier kommt die Query rein, also die eigentliche Routingabfrage
 */
public class Query {

	/**
	 * Hier wird das erste Element der PrQueue entfernt, anschlieueend Abbruchbedingungen geprueft,
	 * die noch erlaubte Distanz auf der aktuellen Hierarchie berechnet und neue Elemente in die Queues eingefuegt.
	 * uenderungen werden in den beiden HashMaps sowie den Queues (forward/backward) gespeichert).
	 * @param hgraph der HierarchyMapGraph, dieser muss zwingend bereits alle Hierarchien von 0 bis 10 verwenden
	 * @param nodes1 Liste aller Knoten die vom Startknoten aus settled oder reached sind.
	 * @param forward Liste aller Knoten die vom Startknoten aus reached aber nicht settled sind.
	 * @param nodes2 Liste aller Knoten die vom Endknoten aus settled oder reached sind.
	 * @param backward Liste aller Knoten die vom Endknoten aus reached aber nicht settled sind.
	 * @param mode Bestimmt ob wir aktuell vom Startknoten (true) oder vom Zielknoten aus suchen (false)
	 * @throws Exception
	 */
	private static boolean handleQueues(HashMap<Integer,Vertex> nodes1, PrQueue forward,  
			HashMap<Integer,Vertex> nodes2, PrQueue backward, boolean mode) throws Exception{
		//mode = true bedeutet vorwaerts, sonst ruckwarts
		Vertex vertex = null;
				
		if (mode){
			//Laufe vorwaerts
			if( forward.getSize() == 0) return true;
			vertex = forward.extractMin();

			//System.out.println(" vorwaerts "+vertex.node.getUID());
			//Abbruchkriterium: Knoten von beiden Seiten aus "settled", also in keiner Queue mehr
			if (nodes2.containsKey(vertex.node.getUID())){ //Ist der selbe Knoten schon von der anderen Seite besucht worden
				if ( nodes2.get(vertex.node.getUID()).getDist() < backward.getMinValue()) { //Ist Knoten noch in der anderen Queue?
					//forward = null;  //Wir leeren die Queues, damit Abbruchbedingung der WHILE
					//backward = null; //Schleife erfuellt ist
					//System.out.println("Abbruch vorwaerts");
					return true;
				}
			}
		}else{
			//Laufe rueckwaerts

			if( backward.getSize() == 0) return true;
			vertex = backward.extractMin();
			
			//System.out.println(" rueckwarts " + vertex.node.getUID());	
			if (nodes1.containsKey(vertex.node.getUID())) {
				if (nodes1.get(vertex.node.getUID()).getDist() < forward.getMinValue()) {
					//forward = null;
					//backward = null;
					//System.out.println("Abbruch rueckwaerts");
					return true;
				}
			}
		}
		
		Iterator<HierarchyMapEdge> hierarchyedges = null;
		
		if (mode){
			hierarchyedges = ((HierarchyMapNode) vertex.node).getOutgoingEdgesByHierarchy(vertex.getLevel()).iterator();
		}else{
			hierarchyedges = ((HierarchyMapNode) vertex.node).getIncomingEdgesByHierarchy(vertex.getLevel()).iterator();
		}
				
		while (hierarchyedges.hasNext()){ //ueberpruefe alle benachbarten Knoten
			
			
			HierarchyMapEdge newEdge = hierarchyedges.next();
			if (( mode && newEdge.getNodeEnd() == vertex.node)
					|| ( !mode && newEdge.getNodeStart() == vertex.node )) continue;
			
			//Pruefe nun ob die Kante betrachtet werden darf
			byte level = vertex.getLevel();
			long gap = vertex.getGap();
			
			//if (mode){
			//	System.out.println(newEdge.getNodeEnd().getUID());
			//}else{
			//	System.out.println(newEdge.getNodeStart().getUID());
			//}
			
			if (level == 0 && ((HierarchyMapNode) vertex.node).getdH((byte) 0, mode) == 0){
				level++;
				gap = ((HierarchyMapNode) vertex.node).getdH((byte) 1, mode);
			}
			
			if (newEdge.getLevel()<level) continue; //Level der Kante zu klein, ignorierere die Kante. 
				
			while ( gap < newEdge.getWeight() || gap == -1  ){
				if( gap == -1 && newEdge.getLevel() > level){ //Wir sind im Core, und die Kante existiert auch noch in der nuechsten Hierarchie
					level++;
							
					if(mode){
						gap = ((HierarchyMapNode) vertex.node).getdH(level, true);
					}else{
						gap = ((HierarchyMapNode) vertex.node).getdH(level, false);
					}
					continue;
				}
				if( gap < newEdge.getWeight() && newEdge.getLevel() > level ){
					level++;	
					gap = -1;
					continue;
				}
				break;
			}
			//System.out.println(gap);
			if (gap >= 0 && gap < newEdge.getWeight() ) continue;

			
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
			
			if( gap != -1 ){ //Wir berechnen Gap fuer den Endknoten der betrachteten Kante.
				gap = gap-newEdge.getWeight();
			}

			if(newNode != null){ //Vergleiche die beiden Wege
				if (newNode.getDist() > vertex.getDist() + newEdge.getWeight()){
					newNode.setDist( vertex.getDist() + newEdge.getWeight() );
					newNode.setPredecessor(vertex.node.getUID());
					newNode.setLevel(level);
					if (gap == -1){
						newNode.setGap(-1);
					}else{
						newNode.setGap(gap);
					}
					if (mode){
						forward.update(newNode);
					}else{
						backward.update(newNode);
					}
				}
			}else{ //Vertex existiert nicht => lege neuen an
				try{
					if(mode){
						newNode = new Vertex( newEdge.getNodeEnd(), vertex.getDist() + newEdge.getWeight(), vertex.node.getUID(), level, gap); 
						nodes1.put(newNode.node.getUID(),newNode);
						forward.insert(newNode);
					}else{
						newNode = new Vertex( newEdge.getNodeStart(), vertex.getDist() + newEdge.getWeight(), vertex.node.getUID(), level, gap);
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
		//if (mode){
		//	System.out.println(forward.getMinValue());
		//}else{
		//	System.out.println(backward.getMinValue());
		//}
		return false;
	}

	/**
	 * berechnet die Entfernung zwischen 2 Knoten unter Verwendung des Algorithmus aus unserem Paper
	 * @param hgraph der HierarchyMapGraph, dieser muss zwingend bereits alle Hierarchien von 0 bis 10 verwenden
	 * @param startnode die ID des Startnodes, dieser muss zwingend im OriginalGraph existieren, nicht aber im HGraphen
	 * @param endnode die ID des Endknotens, dieser muss zwingend im OriginalGraph existieren, nicht aber im HGraphen
	 * @return Path der Liste aller besuchten knoten enthuelt. Wichtig: Aufgrund von Shortcuts wird diese Route nicht als Weg im OGraphen existieren
	 * 			(Verwende Path.getPathInOriginalGraphViaNodes/Edges um die Shortcuts zu entfernen), Luenge des Pfades bleibt dabei aber unveruendert
	 */
	public static Path computeHierarchyPath(HierarchyMapGraph hgraph, int startnode, int endnode) throws Exception{

		if (hgraph == null ) throw new EmptyInputException("Leerer Graph uebergeben");
		if (hgraph.getNode(startnode) == null || hgraph.getNode(endnode) == null || startnode == endnode) return new Path();		
		HashMap<Integer,Vertex> nodes1 = new HashMap<Integer,Vertex>(); // In einzelnen Listen wird gespeichert welcher Knoten.
		HashMap<Integer,Vertex> nodes2 = new HashMap<Integer,Vertex>(); //von welcher Richtung aus schon besucht worden ist.
		
		PrQueue forward = new PrQueue(); //Priority Queue, die alle erreichbaren Knoten enthaelt
		PrQueue backward = new PrQueue(); //Priority Queue, die alle erreichbaren Knoten enthaelt
		
		Vertex start;
		Vertex end;
		
		// Starte im HGraphen auf Level 0
		start = new Vertex(hgraph.getNode(startnode),0,0,(byte)0, hgraph.getNode(startnode).getdH((byte)0, true));
		end = new Vertex(hgraph.getNode(endnode),0,0,(byte)0, hgraph.getNode(endnode).getdH((byte)0, false));
						
		nodes1.put(startnode,start);
		nodes2.put(endnode,end);
		
		forward.insert(start);
		backward.insert(end);

		boolean abortforward;
		boolean abortbackward;
		
		while ( 1 == 1){ //Wenn beide Queues leer, Abbruch


			abortforward = handleQueues(nodes1,forward,nodes2,backward,true); //Berechne vorwaerts		
			abortbackward = handleQueues(nodes1,forward,nodes2,backward,false); //Berechne rueckwaerts
			if(abortforward && abortbackward) break; //Teste ob Abbruchbedingung noch erfuellt ist
			
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
		
		route.add( (HierarchyMapNode) meet.node );
		Vertex pos = meet;
		
		while (pos.getPredecessor() != 0){ //Ermittle alle Knoten, die vor meet besucht wurden
			Vertex parent = nodes1.get(pos.getPredecessor());
			route.add(0, (HierarchyMapNode) parent.node  );
			pos = parent;
		}
		
		pos = nodes2.get( meet.node.getUID());
		
		while (pos.getPredecessor() != 0){ //Ermittle alle Knoten die nach meet besucht wurden
			Vertex parent = nodes2.get(pos.getPredecessor());
			route.add( (HierarchyMapNode) parent.node );
			pos = parent;
		}
				
		//System.out.println("Knoten in der Query: "+route.size()+" " + distance);
		Path result = new Path();
		for (HierarchyMapNode node: route){
			result.appendNode(node);
			//System.out.println("Knoten in der Path: "+result.size());
		}
		result.reconstructPath(hgraph);
		return result; //gib die Liste zurueck
	}
	
	public static Path computeShortestPath(int startID,int endID,HierarchyMapGraph hGraph) throws Exception {
		//Fall, dass Start und Ziel gleich sind abfagen
		if(startID==endID) {
			Path optimalPath=new Path();
			optimalPath.appendNode(startID);
			optimalPath.appendNode(endID);
			optimalPath.setLength(0);
			optimalPath.setTime(0);
			optimalPath.setPathAsReconstructed();
			return optimalPath;
		}
		
		//Ueberprufe, ob Start- und Zielknoten kontrahierte Knoten sind und in gleicher Kante gespeichert werden
		ArrayList<HierarchyMapEdge> edgesOfStartNode=hGraph.getContractedEdge(startID);
		ArrayList<HierarchyMapEdge> edgesOfEndNode=hGraph.getContractedEdge(endID);
		HierarchyMapEdge commonEdge=null;
		for(HierarchyMapEdge e:edgesOfStartNode) {
			if(edgesOfEndNode.contains(e)) {
				commonEdge=e;
				break;
			}
		}
		if(commonEdge!=null) {
			int startPos=0;
			int endPos=0;
			int contractedNodeIDs[]=commonEdge.getContractedNodeIDs();
			int contractedNodeDistances[]=commonEdge.getContractedNodeDistances();
			for (int i = 0; i < contractedNodeIDs.length; i++) {
				if(contractedNodeIDs[i]==startID) {
					startPos=i;
				} 
				if(contractedNodeIDs[i]==endID) {
					endPos=i;
				}
			}
			Path optimalPath=new Path();
			if(startPos<endPos) {
				for (int i = startPos; i <= endPos; i++) {
					optimalPath.appendNode(contractedNodeIDs[i]);
				}
				int pathLength=contractedNodeDistances[endPos]-contractedNodeDistances[startPos];
				optimalPath.setLength(pathLength);
				optimalPath.setTime(Math.round(pathLength*Config.getSpeedFactor(commonEdge.getType())));
			} else {
				for (int i = startPos; i >= endPos; i--) {
					optimalPath.appendNode(contractedNodeIDs[i]);
				}
				int pathLength=contractedNodeDistances[startPos]-contractedNodeDistances[endPos];
				optimalPath.setLength(pathLength);
				optimalPath.setTime(Math.round(pathLength*Config.getSpeedFactor(commonEdge.getType())));
			}
			optimalPath.setPathAsReconstructed();
			return optimalPath;
		}
		
		//Standardfall: Bei Knoten liegen auf verschiedenen Kanten oder sind Kreuzungen
		ArrayList<Path> pathsFromStartToStartCrossings=hGraph.getNextCrossingIDs(startID, true);	
		ArrayList<Path> pathsFromEndCrossingsToEnd=hGraph.getNextCrossingIDs(endID, false);
		Path optimalPath=new Path();
		optimalPath.setLength(-1);
		optimalPath.setTime(-1);
		long optimalPathWeight=Long.MAX_VALUE;
		for (int i = 0; i < pathsFromStartToStartCrossings.size(); i++) {
			for (int j = 0; j < pathsFromEndCrossingsToEnd.size(); j++) {
				Path pathFromStartToStartX=pathsFromStartToStartCrossings.get(i);
				Path pathFromEndXToEnd=pathsFromEndCrossingsToEnd.get(j);
	
				Path pathBetweenCrossings;
				if(pathFromStartToStartX.getEndNodeID()==pathFromEndXToEnd.getStartNodeID()) {
					//Sonderfall: Start und Ziel sind gleiche Kreuzung
					pathBetweenCrossings=new Path();
					pathBetweenCrossings.appendNode(pathFromStartToStartX.getEndNodeID());
					pathBetweenCrossings.setLength(0);
					pathBetweenCrossings.setTime(0);
					pathBetweenCrossings.setPathAsReconstructed();
				} else {
					//Berechene kuerzesten Weg zwischen jeweiligen Kreuzungen
					pathBetweenCrossings=Query.computeHierarchyPath(hGraph,pathFromStartToStartX.getEndNodeID(),pathFromEndXToEnd.getStartNodeID());
					//pathBetweenCrossings = Dijkstra.bidirectional(hGraph, pathFromStartToStartX.getEndNodeID(), pathFromEndXToEnd.getStartNodeID());
				}
					
				//Ueberpruefe ob die gerade berechnete Strecke ein gueltiger Pfad ist und ihre Laenge, 
				//plus die Wege zur den Kreuzungen kuerzer/schneller ist
				//als die bisherige optimale Pfaddauer/Laenge. Falls ja aktualisiere
				if( pathBetweenCrossings.size()>0 && (pathBetweenCrossings.getPathWeight(hGraph)+
						pathFromStartToStartX.getPathWeight(hGraph)+pathFromEndXToEnd.getPathWeight(hGraph)<optimalPathWeight)) {
					//aktualisiere optimales GesamtPfadweight
					optimalPathWeight=pathBetweenCrossings.getPathWeight(hGraph)+
							pathFromStartToStartX.getPathWeight(hGraph)+pathFromEndXToEnd.getPathWeight(hGraph);
					//Fuege Pfad zusammen zu bisherigen optimalen Gesamtpfad
					PathExtendedDec tempPath=new PathExtendedDec(pathBetweenCrossings);
					tempPath.prependPath(pathFromStartToStartX);
					tempPath.appendPath(pathFromEndXToEnd);
					optimalPath=tempPath;
				}
			}
		}
		optimalPath.setPathAsReconstructed();
		return optimalPath;
	}	
}
