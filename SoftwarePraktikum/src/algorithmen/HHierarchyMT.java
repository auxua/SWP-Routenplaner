/**
 * 
 */
package algorithmen;

import graphenbib.HierarchyMapEdge;
import graphenbib.HierarchyMapGraph;
import graphenbib.HierarchyMapNode;
import graphexceptions.InvalidInputException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import main.Config;
import main.Logger;

/**
 * Diese Klasse sorgt dafuer, dass fuer einen HierarchyMapGraph die einzelnen Hierarchyien berechnet werden.
 * Die Klasse setzt allerdings voraus, das der HierarchyMapGraph bereits vollstaendig initialisiert wurde
 * (alle Nodes/Edges auf Level 0, HierarchyMapEdge.contractEdges bereits einmal ausgefuhrt).
 * Diese Klasse entscheidet automatisch ob, und wieviele Threads genutzt werden
 * 
 * 
 */
public class HHierarchyMT {
	
	//private static Logger logger = Logger.getInstance();
	
	//private static Lock lock = new ReentrantLock();
	
	//private static int[] z = { 0,0,0,0};
	
	//private volatile static HashSet<Integer> set = new HashSet<Integer>();
	
	//private static synchronized void add(int i) { set.add(i); }
	
	//private synchronized static void inc(int i) { z[i]++; }
	
	/**
	 * Anzahl genutzter Threads (2*Kernanzahl - dadurch wird evtl. HT effektiv genutzt
	 */
	private static int thNum = Runtime.getRuntime().availableProcessors()*2;
	//private static int thNum = 3;
	
	/**
	 * Die Anzahl an Nodes, ab der mit MT gearbeitet wird
	 */
	private static int NodesThreshold = 500;
	//private static int NodesThreshold = Integer.MAX_VALUE;

	
	
	/**
	 * Hier werden aus einem Baum zwischen der Wurzel und einem Blatt alle HierarchieEdges identifiziert
	 * Definition zur Idenifizierung siehe Paper.
	 * Im uebergebenen HierarchyMapGraphen werden dann die HierarchyEdges entsprechend der uebergebenen Hierarchie gesetzt.
	 * @param tree Der Baum der zuvor von computeTree berechnet wurde.
	 * @param graph Der HierarchieGraph
	 * @param leaf Ein Blatt des Baumes (nur zwischen diesem Blatt und der Wurzel wird nach HierarchyEdges gesucht
	 * @param startnode Die Wurzel des Baumes
	 * @param hierarchy Level, auf den die gefundenen Kanten geliftet werden sollen 
	 */
	private static void extractHighwayEdges(HashMap<Integer,Vertex> tree, Vertex leaf, HierarchyMapNode startnode, byte hierarchy){
		
		Vertex current = leaf;
				
		while (current.node != startnode){
			final Vertex predecessor = tree.get(current.getPredecessor()); //Berechne die Entfernung zum Blatt des Baumes
			predecessor.setLeafDistance( leaf.getDist() - predecessor.getDist() ); 
			if (current.getDist() > startnode.getdH((byte) (hierarchy-1), true) 
				&& predecessor.getLeafDistance() > ((HierarchyMapNode) leaf.node).getdH((byte) (hierarchy-1), false)){

				final Iterator<HierarchyMapEdge> edges = ( (HierarchyMapNode) predecessor.node).getOutgoingEdgesByHierarchy((byte) (hierarchy-1)).iterator();
				//Wir suchen die Kante, die die beiden Knoten verbindet
				while(edges.hasNext()){
					final HierarchyMapEdge edge = edges.next();
					if (edge.getNodeEnd() == current.node){
						edge.setLevel(hierarchy); //Setze das Level der Kante (verwende nicht increase, da sonst eine Kante mehrmals erhueht wird)
						edge.getNodeStart().setLevel(hierarchy);
						edge.getNodeEnd().setLevel(hierarchy);
						break;
					}
				}
			}
			
			current = predecessor; //Wir bewegen uns weiter auf den Startnode zu
		}
		return;
	}
	
	/** Hier wird fuer einen Knoten getestet, ob die Abbruchbedingung des Shortest-Path-Tree erfuellt ist.
	 * (Definition siehe Paper)
	 * Am Ende der Funktion wird im Vertex newNode der State auf active bzw. passive gestellt (Abbruch nicht erfuellt bzw. erfuellt)
	 * @param graph Der HierarchieMapGraph
	 * @param newNode Der Knoten fuer den die Abbruchbedingung getestet werden soll
	 * @param predecessor Der direkte Vorgaenger des zu testenden Knoten
	 * @param level hierarchy aus computeHierarchie
	 */
	private static void updateAbort(Vertex newNode, Vertex predecessor, int level) {
		if (predecessor.getState() == State.Passive) {
			newNode.setState(State.Passive);
			return;
		}
		newNode.setState(State.Active);
		final HierarchyMapNode predec = (HierarchyMapNode) predecessor.node;
		final long predDist = predecessor.getDist();
		final long newDist = newNode.getDist();
		
		if (predecessor.getPredecessor() == 0){ //Predeccossor ist startnode
			newNode.setCrit1( ((HierarchyMapNode) newNode.node).getdH((byte)(level-1), true));
			return;
		}
		if (predecessor.getCrit1() >= newDist-predDist){ //Laufe bis die Nachbarschaft von s1 verlassen wird
			newNode.setCrit1(predecessor.getCrit1() - newDist + predDist);
			return;
		}
		newNode.setCrit2(predecessor.getCrit2() + newDist - predDist );
		if (newNode.getCrit2() <= predec.getdH((byte)(level-1), false)){
			return;
		}
		if (predecessor.getCrit2() <= predec.getdH((byte)(level-1), false)){
			return;
		}		
		newNode.setState(State.Passive); //Keine der obigen Bedinungen ist mehr erfuellt
	}
	
	/**
	 * Diese Funktion berechnet fuer einen Knoten den Shortest-Path-Tree.
	 * Aus diesem koennen dann spaeter alle HierarchyEdges identifiziert werden) 
	 * @param graph Uebergabe des HGraphen auf dem der Tree berechnet wird.
	 * @param startnode Wurzel des zu berechnenden Baumes
	 * @param hierarchy Wert aus computeHierarchie
	 * @return Hier wird der Baum zurueckgegeben: Jedes Vertex enthaelt einen "Zeiger"
	 * 		 auf seinen direkten Vorgaenger im Baum
	 * @throws Exception
	 */
	private static HashMap<Integer,Vertex> computeTree(HierarchyMapNode startnode, byte hierarchy) throws Exception{
		
		if (startnode == null) throw new InvalidInputException("Ungueltige ID uebergeben");
		
		final HashMap<Integer,Vertex> nodes = new HashMap<Integer,Vertex>(); //Liste in der alle besuchten Knoten gespeichert werden.
		final PrQueue queue = new PrQueue(); //Priority Queue, die alle erreichbaren Knoten enthaelt
		
		final Vertex start = new Vertex(startnode,0, 0, State.Active, 0, 0); //Vertex ist die Hilfsklasse, in der zusaetzliche Infos ueber Knoten gespeichert werden.
		nodes.put(startnode.getUID(), start);
		
		queue.insert(start);
        
		while (queue.getSize() != 0){
			if (queue.containsActive() == false) return nodes;
			final Vertex vertex = queue.extractMin();
			final HierarchyMapNode node = (HierarchyMapNode) vertex.node;
			final long Dist = vertex.getDist();
			vertex.setLeaf(); //Neuer Knoten ist Blatt des Baumes
			if (vertex.getPredecessor() != 0)
				nodes.get(vertex.getPredecessor()).noLeaf(); //Vorgaenger ist kein Blatt des Bauemes
			
			Iterator<HierarchyMapEdge> edges = null;
			edges = node.getOutgoingEdgesByHierarchy((byte)(hierarchy-1)).iterator();
			
			while (edges.hasNext()){ //ueberpruefe alle benachbarten Knoten
				HierarchyMapEdge newEdge = edges.next();
				if (newEdge.getNodeEnd() == node) continue;	// ignoriere Zykel
				Vertex newNode = null; //Vertex ist die Klasse die Infos, wie Vorgaenger, Entfernung zum Start speichert
				 
				if ( nodes.containsKey(newEdge.getNodeEnd().getUID())){
					// Vertex existiert schon => ueberpruefe ob neuer Weg besser ist
					newNode = nodes.get(newEdge.getNodeEnd().getUID());
					if (newNode.getDist() > Dist + newEdge.getWeight()){
						newNode.setDist( Dist + newEdge.getWeight() );
						newNode.setPredecessor(node.getUID());
						updateAbort(newNode, vertex, hierarchy);
						queue.update(newNode);	//pruefe ob der Vertex umsortiert werden muss
					}
					continue;
				}
				try{ //Wenn wir bis hier kommen, existiert der Vertex noch nicht => wir legen einen neuen an.
					newNode = new Vertex( newEdge.getNodeEnd(), Dist + newEdge.getWeight(), node.getUID()); 
					nodes.put(newNode.node.getUID(),newNode);
					updateAbort(newNode, vertex, hierarchy);
					queue.insert(newNode);
				}
				catch (Exception e)
				{
					e.printStackTrace();//Hier sollte man nie hinkommen
				}
			
			}
		} return nodes; //wenn man bis hier kommt, gibt es keine H erreichbaren Knoten
	}
	
	/**
	 * Diese private Klasse kapselt einen Iterator und macht diesen auf diese Art Threadsafe (minimaler Umfang, daher kein voller Iterator noetig)
	 *
	 * @param <DataType>
	 */
	@Deprecated
	public static class MyIT<DataType> {
		
		private Iterator<DataType> it;
		
		public MyIT(Iterator<DataType> it) {
			this.it = it;
		}
		
		public synchronized boolean hasNext() {
			return it.hasNext();
		}
		
		public synchronized DataType next() {
			return it.next();
		}
	}
	
	@Deprecated
	public static class MyArrayIT implements Iterator<HierarchyMapNode> {
		
		private HierarchyMapNode[] it;
		int pos = 0;
		
		public void revert() {
			List<HierarchyMapNode> list = Arrays.asList(it);
			Collections.reverse(list);
			it = (HierarchyMapNode[]) list.toArray();
		}
		
		public void shuffle() {
			HierarchyMapNode temp;
			int zufall;
			
			for (int i = 0; i<100; i++) {
				temp = it[0];
				zufall = (int) Math.round(Math.random()*(it.length-1));
				it[0] = it[zufall];
				it[zufall] = temp;
			}
		}
		
		public MyArrayIT(HierarchyMapNode[] it) {
			this.it = it;
		}
		
		public synchronized boolean hasNext() {
			return (pos<it.length);
		}
		
		public synchronized HierarchyMapNode next() {
			return it[pos++];
		}

		@Override
		public void remove() {
			// TODO Auto-generated method stub
			
		}
	}
	
	/**
	 * Diese Klasse beschreibt das Programm, dass die nachbarschaften berechnen kann in Zusammenarbeit mit weiteren solcher Instanzen (MT!)
	 */
	private static class NeighbourhoodWorker implements Runnable {

		private final HierarchyMapGraph graph;
		private final byte hierarchy;
		//private HierarchyMapNode node;
		//private Iterator<HierarchyMapNode> it;
		private final Iterator<HierarchyMapNode> it;
		
		
		
		//public NeighbourhoodWorker(HierarchyMapGraph graph, byte hierarchy, Iterator<HierarchyMapNode> it) {
		public NeighbourhoodWorker(HierarchyMapGraph graph, byte hierarchy, Iterator<HierarchyMapNode> it) {
			this.graph = graph;
			this.hierarchy = hierarchy;
			//this.node = node;
			this.it = it;
		}



		@Override
		public void run() {
		  
			while(it.hasNext()) {
			  try {
				final HierarchyMapNode node = it.next();
				final int UID = node.getUID();  
				//Berechne Nachbarschaft vorwaerts
				long distance = Dijkstra.neighbourhood(graph, UID,(byte)(hierarchy-1), true);
				node.setdH(distance, (byte) (hierarchy-1), true);

				
				//Berechne Nachbarschaft rueckwaerts
				distance = Dijkstra.neighbourhood(graph, UID,(byte)(hierarchy-1), false);
				node.setdH(distance, (byte) (hierarchy-1), false);
			  } catch (NoSuchElementException e) {
				  //ignoriere - das ist die tolle Threadsafety
			  } catch (Exception e) {
				e.printStackTrace();
			} 
			}
		  
		}
			
	}

	/**
	 * Diese Klasse beschreibt das Programm, dass die Baeume berechnen kann in Zusammenarbeit mit weiteren solcher Instanzen (MT!)
	 */
	private static class TreeWorker implements Runnable {
		private final HierarchyMapGraph graph;
		private final byte hierarchy;
		//private  HierarchyMapNode node;
		//private  Iterator<HierarchyMapNode> it;
		private final Iterator<HierarchyMapNode> it;
		//final Lock lock = HHierarchyMT.lock; 
		//public int ID;
		
		public TreeWorker(HierarchyMapGraph graph, byte hierarchy,	Iterator<HierarchyMapNode> myIT) {
			this.graph = graph;
			this.hierarchy = hierarchy;
			//this.node = node;
			this.it = myIT;
		}

		@Override
		public void run() {
			
			//nochmalvon vorne
			try {
				while(it.hasNext()){ //Wir berechnen fuer jeden knoten die Shortest-Path-Trees
					
					final HierarchyMapNode node = it.next();;
					//node = it.next();
					//add(node.getUID());
					if (node.getLevel() >= hierarchy-1 && node.getdH((byte) (hierarchy-1), true) > 0 ){
						//Berechne Baum
						
						final HashMap<Integer,Vertex> tree = computeTree( node, hierarchy);

						if (tree == null) {
							//inc(this.ID);
							continue;
						}
							
						
						
						//MyIT<Vertex> treeNodes = new MyIT<Vertex>(tree.values().iterator());
						final Iterator<Vertex> treeNodes = tree.values().iterator();
						
						//Iterator<Vertex> treeNodes = tree.values().iterator();
						while (treeNodes.hasNext()){ //Wir suchen alle Bluetter des Baumes
							final Vertex current = treeNodes.next(); 
							if( current.isLeaf()){
								//Finde alle HierarchyEdges zwischen Zentrum des Baumes und Blatt
								extractHighwayEdges(tree, current, node, hierarchy);
								
							}
						}					
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			
		  
		}

		
	}
	
	
	/**
	 * Diese Funkton berechnet die naechste Hierarchie fuer einen HierarchieMapGraph.
	 * Die Funktion muss iterativ aufgerufen werden (wenn eine Hierarchie berechnet wird,
	 *  sollten alle kleineren Hierarchien bereits bestehten).
	 *  Alle Veraenderungen werden im uebergebenen Hierarchiegraphen gemacht
	 *  Anhand einer Heuristik wird entschieden, ob mit mehrereen Threads gearbeitet wird
	 * @param graph Uebergebe hier den Graphen der aktualisiert werden soll.
	 * @param hierarchy Die Hierarchie die berechnet wird (Annahme: level < hierarchie <=> level bereits berechnet)
	 */
	public static void computeHierarchy(HierarchyMapGraph graph, byte hierarchy){
		
		// Wenn der Graph klein ist, ist MT nciht hilfreich
		if (graph.getSize()<NodesThreshold) {
			HHierarchyMT.computeHierarchyST(graph, hierarchy);
			return;
		}
		
		//-------------------------- MultiTHreading -------------------------
		
		//-------------------------- Nachbarschaften --------------------------
		//unschoen, aber Java erlaubt keine Gerneric-Arrays
		ArrayList<HashSet<HierarchyMapNode>> teilMengen = new ArrayList<HashSet<HierarchyMapNode>>();
		int teilGroessen = (graph.getSize()/thNum);
		Iterator<HierarchyMapNode> it = graph.getNodeIt();
		
		//Lege alle noetigen Sets an
		for (int i=0; i<thNum; i++) {
			teilMengen.add(new HashSet<HierarchyMapNode>());
		}
		
		//Teile Nodes auf die Sets auf
		//aktGroesse gibt die Menge an Nodes an, die noch in aktuelles Set gehoeren
		int aktGroesse = teilGroessen;
		//speicher das aktuelle Set zwischen
		HashSet<HierarchyMapNode> aktSet = teilMengen.get(0);
		int aktSetNr = 0;
		while (it.hasNext()) {
			//Neues Set noetig?
			if (aktGroesse < 0) {
				aktGroesse = teilGroessen;
				aktSet = teilMengen.get(++aktSetNr);
			}
			
			//Fuelle Set
			aktSet.add(it.next());
			
			//eine Nde weniger noetig
			aktGroesse--;
		}
		
		//Bereite noetige Threads vor
		Thread[] myThreads = new Thread[thNum];
		//Lasse Threads arbeiten
		for(int i = 0; i<thNum; i++) {
			myThreads[i] = new Thread(new NeighbourhoodWorker(graph,hierarchy,teilMengen.get(i).iterator()));
			myThreads[i].start();
		}
		
		//Joine bitte alle Threads, damit diese auch fertig sind!
		
		try {
			for (int i= 0; i<thNum; i++) {
				myThreads[i].join();
			}
	
		} catch (InterruptedException e) {
			e.printStackTrace();
			//ignoriere das - das darf eigentlich nie passieren!
		} 
		

		//----------------------------- Treeworking --------------------------------
		
		

		//Bereite noetige Threads vor - schaffe neue Threads zur Sicherheit
		myThreads = new Thread[thNum];
		//Lasse Threads arbeiten
		for(int i = 0; i<thNum; i++) {
			myThreads[i] = new Thread(new TreeWorker(graph,hierarchy,teilMengen.get(i).iterator()));

			//myThreads[i] = new Thread(new TreeWorker(graph,hierarchy,MIT));
			myThreads[i].start();
		}
		

		//Joine bitte alle Threads, damit diese auch fertig sind!
		
		try {
			for (int i= 0; i<thNum; i++) {
				myThreads[i].join();
			}
	
		} catch (InterruptedException e) {
			e.printStackTrace();
			//ignoriere das - das darf eigentlich nie passieren!
		} 
		
		
	}
	
	/**
	 * Diese Klasse uebernimmt das builden der hoechsten Hierarchy-Stufe (siehe buildhighestHierarchy)
	 * Diese Klasse ist zum MT gedacht
	 *
	 */
	private static class BuildHHWorker implements Runnable {

		private final Iterator<HierarchyMapNode> iterator;
		private final byte hierarchy;
		private HierarchyMapNode node;
		
		public BuildHHWorker(HierarchyMapGraph graph, byte hierarchy, Iterator<HierarchyMapNode> iterator) {
			this.hierarchy = hierarchy;
			this.iterator = iterator;
		}
		
		@Override
		public void run() {

			while(iterator.hasNext()){ 
					node = iterator.next();
				if (node.getLevel() == hierarchy){
						node.setdH(-1, (byte) hierarchy, true);

					node.setdH(-1, (byte) hierarchy, false);
				}
			}

		}
		
		
	}
	
	/**
	 * Diese Funkton setzt die Nachbarschaften aller Knoten im obersten Level auf -1.
	 * Dadurch gibt es auf dem obersten Level keine Grenze mehr, wann wir die Query abbrechen. 
	 *  Alle Veraenderungen werden im uebergebenen Hierarchiegraphen gemacht
	 * @param graph Uebergebe hier den Graphen der aktualisiert werden soll.
	 * @param hierarchy Die Hierarchie die berechnet wird (Annahme: level < hierarchie <=> level bereits berechnet)
	 */
	public static void buildhighestHierarchy(HierarchyMapGraph graph, byte hierarchy){
		
		//Vermeide MT auf zu kleinen Graphen
		if (graph.getSize()<NodesThreshold) {
			HHierarchyMT.buildhighestHierarchyST(graph, hierarchy);
			return;
		}
		
		//------------------------- MultiThreading ---------------------------
		
		final Thread[] myThreads = new Thread[thNum];
		
		//------------------------ splitting --------------
		final ArrayList<HashSet<HierarchyMapNode>> teilMengen = new ArrayList<HashSet<HierarchyMapNode>>();
		final int teilGroessen = (graph.getSize()/thNum);
		final Iterator<HierarchyMapNode> it = graph.getNodeIt();
		
		//Lege alle noetigen Sets an
		for (int i=0; i<thNum; i++) {
			teilMengen.add(new HashSet<HierarchyMapNode>());
		}
		
		//Teile Nodes auf die Sets auf
		//aktGroesse gibt die Menge an Nodes an, die noch in aktuelles Set gehoeren
		int aktGroesse = teilGroessen;
		//speicher das aktuelle Set zwischen
		HashSet<HierarchyMapNode> aktSet = teilMengen.get(0);
		int aktSetNr = 0;
		while (it.hasNext()) {
			//Neues Set noetig?
			if (aktGroesse < 0) {
				aktGroesse = teilGroessen;
				aktSet = teilMengen.get(++aktSetNr);
			}
			
			//Fuelle Set
			aktSet.add(it.next());
			
			//eine Nde weniger noetig
			aktGroesse--;
		}
		
		
		for (int i = 0; i<myThreads.length; i++) {
			myThreads[i] = new Thread(new BuildHHWorker(graph, hierarchy, teilMengen.get(i).iterator()));
			myThreads[i].start();
		}
		try {
			for (int i = 0; i<myThreads.length; i++) {
				myThreads[i].join();
			}
		} catch (InterruptedException e) {
			//Das darf nicht passieren
		}
		
			/*
		try{
			while(iterator.hasNext()){ //Wir berechnen fuer jeden knoten die Nachbarschaften
				HierarchyMapNode node = iterator.next();
				if (node.getLevel() == hierarchy){
					//Berechne Nachbarschaft vorwaerts
					node.setdH(-1, (byte) hierarchy, true);

					//Berechne Nachbarschaft rueckwaerts
					node.setdH(-1, (byte) hierarchy, false);
				}
			}
		}catch(Exception e1){
			e1.printStackTrace();
		}
			// */
	}
	
	/**
	 * Diese Funktion berechnet die kompletten Hierarchystufen bis zur maxHierarchyLayer im uebergebenen hGraph, wobei hierfuer folgende
	 * Methode verwendet werden:
	 * -computeHierarchy
	 * -deleteSelfLoopAndParallelEdges
	 * -contractEdges
	 * -computeTwoCore
	 *Dabei gilt zu beachten, dass es stets folgende Zwischenlevel gibt:
	 *0. Level: Kopie der MapGraphen
	 *1. Level: Kontraktion der Kanten und entfernen von Schlingen undparallelen katen
	 *2. Level: HHierarchy von Level 1
	 *3. Level: der 2Core darauf und erneute Kontraktion und Entfernung von Schlingen und parallelen Kanten
	 *
	 *Nun werden die Schritte des levels 2 und 3 wiederhlt bis zum maxHierarchyLayer-1
	 */
	public static void buildHierarchyGraph(HierarchyMapGraph hGraph)
	{

		//Vermeide MT auf zu kleinen Graphen
		if (hGraph.getSize()<NodesThreshold) {
			HHierarchyMT.buildHierarchyGraphST(hGraph);
			return;
		}
		
		byte level=0;
		Logger.getInstance().log("HHierarchyMT", "Starte Erstellen des HierarchyMapGraph");
		hGraph.contractEdges(level);
		hGraph.increaseHighestLevel(level);
		Logger.getInstance().log("HHierarchyMT", "Level 0 erstellt.");
		level++;
		hGraph.deleteSelfLoopAndParallelEdges(level);
		Logger.getInstance().log("HHierarchyMT", "Level 1 erstellt.");
		do{
			//Berechnung der einzelnen Hierarchien
			level++;
			HHierarchyMT.computeHierarchy(hGraph,level);
			
			Logger.getInstance().log("HHierarchyMT", "Level "+level+" erstellt.");
			//hGraph.computeTwoCore(level);
			level++;
			hGraph.contractEdges(level);
			hGraph.deleteSelfLoopAndParallelEdges(level);
			Logger.getInstance().log("HHierarchyMT", "Level "+level+" erstellt.");
		} while (level<=Config.maxHierarchyLayer-2);
		HHierarchyMT.buildhighestHierarchy(hGraph, (byte) (Config.maxHierarchyLayer-1));
		Logger.getInstance().log("HHierarchyMT", "HierarchyMapGraph erfolgreich erstellt.");
		
		//---------------------- DEBUGGING------------------
		Iterator<HierarchyMapNode> iterator;
		iterator = hGraph.getNodeIt();
		int [] levels = new int[Config.maxHierarchyLayer+1];
		while( iterator.hasNext()){
			HierarchyMapNode node = iterator.next();
			levels[node.getLevel()]++;
		}
		for( int i= 0; i< levels.length;i++){
			Logger.getInstance().log("HHierarchyMT", "Knoten auf Level "+i+": "+levels[i]);	
		}
		
	}
	
	/**
	 * 
	 * Diese Funktion berechnet die kompletten Hierarchystufen bis zur maxHierarchyLayer im uebergebenen hGraph, wobei hierfuer folgende
	 * Methode verwendet werden:
	 * -computeHierarchy
	 * -deleteSelfLoopAndParallelEdges
	 * -contractEdges
	 * -computeTwoCore
	 * (SingleThreading-Variante)
	 *Dabei gilt zu beachten, dass es stets folgende Zwischenlevel gibt:
	 *0. Level: Kopie der MapGraphen
	 *1. Level: Kontraktion der Kanten und entfernen von Schlingen undparallelen katen
	 *2. Level: HHierarchy von Level 1
	 *3. Level: der 2Core darauf und erneute Kontraktion und Entfernung von Schlingen und parallelen Kanten
	 *
	 *Nun werden die Schritte des levels 2 und 3 wiederhlt bis zum maxHierarchyLayer-1
	 */
	public static void buildHierarchyGraphST(HierarchyMapGraph hGraph)
	{
		byte level=0;
		Logger.getInstance().log("HHierarchy", "Starte Erstellen des HierarchyMapGraph");
		hGraph.contractEdges(level);
		hGraph.increaseHighestLevel(level);
		Logger.getInstance().log("HHierarchy", "Level 0 erstellt.");
		level++;
		hGraph.deleteSelfLoopAndParallelEdges(level);
		Logger.getInstance().log("HHierarchy", "Level 1 erstellt.");
		do{
			//Berechnung der einzelnen Hierarchien
			level++;
			HHierarchyMT.computeHierarchyST(hGraph,level);
			
			Logger.getInstance().log("HHierarchy", "Level "+level+" erstellt.");
			//hGraph.computeTwoCore(level);
			level++;
			hGraph.contractEdges(level);
			hGraph.deleteSelfLoopAndParallelEdges(level);
			Logger.getInstance().log("HHierarchy", "Level "+level+" erstellt.");
		} while (level<=Config.maxHierarchyLayer-2);
		HHierarchyMT.buildhighestHierarchyST(hGraph, (byte) (Config.maxHierarchyLayer-1));
		Logger.getInstance().log("HHierarchy", "HierarchyMapGraph erfolgreich erstellt.");
		

		//---------------------- DEBUGGING------------------
		Iterator<HierarchyMapNode> iterator;
		iterator = hGraph.getNodeIt();
		int [] levels = new int[Config.maxHierarchyLayer+1];
		while( iterator.hasNext()){
			HierarchyMapNode node = iterator.next();
			levels[node.getLevel()]++;
		}
		for( int i= 0; i< levels.length;i++){
			Logger.getInstance().log("HHierarchy", "Knoten auf Level "+i+": "+levels[i]);	
		}
	}
	
	/**
	 * Diese Funkton setzt die Nachbarschaften aller Knoten im obersten Level auf -1.
	 * Dadurch gibt es auf dem obersten Level keine Grenze mehr, wann wir die Query abbrechen. 
	 *  Alle Veraenderungen werden im uebergebenen Hierarchiegraphen gemacht
	 *  (SingleThreading-Variante)
	 * @param graph Uebergebe hier den Graphen der aktualisiert werden soll.
	 * @param hierarchy Die Hierarchie die berechnet wird (Annahme: level < hierarchie <=> level bereits berechnet)
	 */
	public static void buildhighestHierarchyST(HierarchyMapGraph graph, byte hierarchy){
		Iterator<HierarchyMapNode> iterator = graph.getNodeIt();
		try{
			while(iterator.hasNext()){ //Wir berechnen fuer jeden knoten die Nachbarschaften
				HierarchyMapNode node = iterator.next();
				if (node.getLevel() == hierarchy){
					//Berechne Nachbarschaft vorwaerts
					node.setdH(-1, (byte) hierarchy, true);

					//Berechne Nachbarschaft rueckwaerts
					node.setdH(-1, (byte) hierarchy, false);
				}
			}
		}catch(Exception e1){
			e1.printStackTrace();
		}
	}
	
	/**
	 * Diese Funkton berechnet die naechste Hierarchie fuer einen HierarchieMapGraph.
	 * Die Funktion muss iterativ aufgerufen werden (wenn eine Hierarchie berechnet wird,
	 *  sollten alle kleineren Hierarchien bereits bestehten).
	 *  Alle Veraenderungen werden im uebergebenen Hierarchiegraphen gemacht
	 *  (SingleThreading-Variante)
	 * @param graph Uebergebe hier den Graphen der aktualisiert werden soll.
	 * @param hierarchy Die Hierarchie die berechnet wird (Annahme: level < hierarchie <=> level bereits berechnet)
	 */
	public static void computeHierarchyST(HierarchyMapGraph graph, byte hierarchy){
		Iterator<HierarchyMapNode> iterator = graph.getNodeIt();
		try{
			while(iterator.hasNext()){ //Wir berechnen fuer jeden knoten die Nachbarschaften
				HierarchyMapNode node = iterator.next();
				if (node.getLevel()==hierarchy-1){
					//Berechne Nachbarschaft vorwaerts
					long distance = Dijkstra.neighbourhood(graph, node.getUID(),(byte)(hierarchy-1), true);

					node.setdH(distance, (byte) (hierarchy-1), true);
					
					//Berechne Nachbarschaft rueckwaerts
					distance = Dijkstra.neighbourhood(graph, node.getUID(),(byte)(hierarchy-1), false);

					node.setdH(distance, (byte) (hierarchy-1), false);
					
				}
			}
			iterator = graph.getNodeIt(); //starte wieder am Anfang
			while(iterator.hasNext()){ //Wir berechnen fuer jeden knoten die Shortest-Path-Trees
				HierarchyMapNode node = iterator.next();
				if (node.getLevel() >= hierarchy-1 && node.getdH((byte) (hierarchy-1), true) > 0 ){
					//Berechne Baum
					HashMap<Integer,Vertex> tree = computeTree(node, hierarchy);
					if (tree == null) {
						continue;
					}
						
					Iterator<Vertex> treeNodes = tree.values().iterator();
					while (treeNodes.hasNext()){ //Wir suchen alle Bluetter des Baumes
						Vertex current = treeNodes.next(); 
						if( current.isLeaf()){;
							//Finde alle HierarchyEdges zwischen Zentrum des Baumes und Blatt
							extractHighwayEdges(tree, current, node, hierarchy);
						}
					}					
				}
			}
			
		}catch(Exception e1){
			e1.printStackTrace();
		}
	}
	
	
	/**
	 * Holt die aktuelle Threadanzahl im MT (normalerweise 2*Kernanzahl)
	 */
	public static int getThNum() {
		return thNum;
	}
	
	/**
	 * Setzt die Threadanzahl im MT
	 */
	public static void setThNum(int tNum) {
		if (tNum >1)
			thNum = tNum;
	}
	
}
