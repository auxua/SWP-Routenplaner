/**
 * 
 */
package algorithmen;

import static org.junit.Assert.fail;
import graphenbib.GPSCoordinate;
import graphenbib.HierarchyMapGraph;
import graphenbib.MapEdge;
import graphenbib.MapGraph;
import graphenbib.MapNode;
import graphenbib.StreetType;

import java.io.File;
import java.util.ArrayList;

import main.Config;
import main.Logger;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import Import.OSMImporter;


public class DijkstraTest {

	private static HierarchyMapGraph test3;
	private static HierarchyMapGraph test5;
	private static HierarchyMapGraph test8;
	private static HierarchyMapGraph testGraph1;
	private static HierarchyMapGraph testGraph2;
	private static HierarchyMapGraph testGraph3;
	private static HierarchyMapGraph testGraph4;
	
	//private Logger logger = Logger.getInstance();
	
	private static boolean fastBackup;
	
	/**
	 * Diese Methode gibt eine Zufallszahl aus. min und max bezeichnen dabei allerdings die Vorkomma-Zahl - max.99 ist daher moeglich
	 * @param min
	 * @param max
	 * @return
	 */
	private static float randomFloat(float min, float max) {
		return (float)((Math.random() * (max - min + 1)) + min);
	}
	
	private double weglaenge(ArrayList<MapNode> liste) {
		double test = 0;
		MapNode aktuell = null;
		MapNode nachfolger = null;
		MapEdge kante = null;
		int j = 0;
		//boolean abort = false;

		for (int i=0;i<liste.size()-1;i++) {
			aktuell = liste.get(i);
			nachfolger = liste.get(i+1);
			j=0;
			
			for (j=0;j<aktuell.getNumberOfOutgoingEdges(); j++) {
				if ((kante == null) || (kante.getNodeEnd()!=nachfolger) || ((aktuell.getOutgoingEdges()[j].getWeight()<kante.getWeight()) && aktuell.getOutgoingEdges()[j].getNodeEnd()==nachfolger)) {
					kante = aktuell.getOutgoingEdges()[j];
				}
						
			}
			test = test + kante.getWeight();
		}
		return test;
	}
	
	/**
	 * Diese Hilfsmethode gibt die tatsaechliche Weglaenge aus - nicht deren Gewicht! also strikt nach laenge!
	 */
	private double weglaengeStrict(ArrayList<MapNode> liste) {
		double test = 0;
		MapNode aktuell = null;
		MapNode nachfolger = null;
		MapEdge kante = null;
		int j = 0;
		//boolean abort = false;

		for (int i=0;i<liste.size()-1;i++) {
			aktuell = liste.get(i);
			nachfolger = liste.get(i+1);
			j=0;
			
			for (j=0;j<aktuell.getNumberOfOutgoingEdges(); j++) {
				if ((kante == null) || (kante.getNodeEnd()!=nachfolger) || ((aktuell.getOutgoingEdges()[j].getLength()<kante.getLength()) && aktuell.getOutgoingEdges()[j].getNodeEnd()==nachfolger)) {
					kante = aktuell.getOutgoingEdges()[j];
				}
						
			}
			test = test + kante.getLength();
		}
		return test;
	}
	
	private void weg(MapGraph graph, int start, int ende, int wayID, int laenge) throws Exception {
		graph.insertEdge(start, ende, wayID, laenge, StreetType.LIVING_STREET);
		graph.insertEdge(ende, start, wayID, laenge, StreetType.LIVING_STREET);
	}
	
	private String Pfad(ArrayList<MapNode> liste) {
		String test = "";
		for (int i=0;i<liste.size()-1;i++) {
			test = test + + liste.get(i).getUID() + "-" ;
		}
		test = test + liste.get(liste.size()-1).getUID();
		return test;
	}
	
	private long BelastungsTest(int nodes, float p, int e) throws Exception {
		Logger.getInstance().log("DijkstraTest","Beginne Belastungstest: "+nodes+" Nodes");
		MapGraph graph = randomGraph(nodes,p,e);
		long start = System.currentTimeMillis();
		ArrayList<MapNode> liste = null;
		try {
			liste = Dijkstra.bidirectional(graph, 0, nodes+1);
		} catch (Exception ex) {
			fail("Es gab einen Fehler: "+ex.getLocalizedMessage());
		}
		long ende = System.currentTimeMillis();
		//grobe abschaetzung, wie lang ein Pfad sein kann
		if (liste.size()>nodes+2)
			fail("Der Pfad des kuerzesten Weges enthaelt mehr Knoten als im Graph vorhanden -> es existieren Zykel => nciht der kuerzeste Weg");
		Logger.getInstance().log("DijkstraTest","Test durchgelaufen. Zeit: "+(ende-start)+"ms");
		return ende-start;
	}
	

	
	/**
	 * Erstellt einen zufallsgraphen mit parametrisierbarer Nodezhal und Kantenwahrscheinlichkeit
	 * Dabei ist Node 0 und node n+1 nciht direkt verbunden! (fuer Dijkstra-Tests z.B.)
	 * @param nodes - die Anzahl der Nodes im Graphen
	 * @param p - die Wahrscheinlichkeit der Kantenexistenz in der e-Umgebung
	 * @param e - beschreibt die e-Umgebung
	 * @return Einen zufaelligen MapGraph mit den obigen Parametern erstellt
	 */
	private MapGraph randomGraph(int nodes, float p, int e) throws Exception {
		MapGraph graph = new MapGraph(new GPSCoordinate(50,-50),new GPSCoordinate(-50,50));
		int i;
		int j = 1;
		int eps;
		//Lege Knoten an
		for (i=1;i<=nodes;i++) {
			graph.insertNode(i, new GPSCoordinate(randomFloat(-30,30),randomFloat(-30,30)));
		}
		graph.insertNode(0, new GPSCoordinate(-40,-40));
		graph.insertNode(nodes+1, new GPSCoordinate(40,40));
		
		//Lge Kanten an (Anfang und Ende muessen angeschlossen sein!
		for (i=1; i<e+1; i++) {
			weg(graph,0,i,j++,(int)randomFloat(1,100));
		}
		for (i=nodes; i>=nodes-e; i--) {
			weg(graph,nodes+1,i,j++,(int)randomFloat(1,100));
		}
		
		//Lege nun Kanten in der e-Umgebung an anhand der Zufallsgrenze
		for (i=1; i<=nodes; i++) {
			for(eps = 1;eps<=e;eps++) {
				if ((Math.random() >= p) && (i>eps)) {
					weg(graph,i-eps,i,j++,(int)randomFloat(1,100));
				}
			}
		}
		
		return graph;
	}
	
	/**
	 * Erstellt einen Graphen in Form eines Gitters wie in der VL vorgestellt
	 * @param n - die Seitenlaenge des (quadatischen) Gitters
	 * @return den entsprechenden mapGraph
	 * @throws Exception 
	 */
	private MapGraph GitterGraph(int n) throws Exception {
		MapGraph graph = null;
		try {
			graph = new MapGraph(90, 90, -90, -90);
		} catch (Exception e) {
			fail("Fehler beim Gitter: "+e.getLocalizedMessage());
		}
		
		//Erstelle Knoten
		for (int j= 1; j<n*n+1;j++)
			graph.insertNode(j, new GPSCoordinate(5,5));
		
		//kurze kanten
		int pos = 2+n;
		int i = 3;
		weg(graph,1,2,(n*n)+1,1);
		weg(graph,2,2+n,(n*n)+2,1);
		while (pos<(n*n)-n) {
			weg(graph,pos,pos+1,(n*n)+i++,1);
			weg(graph,pos+1,pos+n+1,(n*n)+i++,1);
			pos = pos+n+1;
		}
		
		//alle kanten
		i =1;
		for (i=1; i<n*n;i++) {
			if ((i % n) > 0) {
				weg(graph,i,i+1,i,10);
				if (i <= (n*n) -n) {
					weg(graph,i+1,i+1+n,i,10);
				}
			}
				
		}
		
		
		return graph;
	}
	
	/**
	 * Entspricht weitestgehend dem Gittergraphen - aber nutze hier verschiedene Wege fuer andere Ergebnisse
	 */
	private MapGraph GitterWeightGraph(int n) throws Exception {
		MapGraph graph = null;
		try {
			graph = new MapGraph(90, 90, -90, -90);
		} catch (Exception e) {
			fail("Fehler beim Gitter: "+e.getLocalizedMessage());
		}
		
		//Erstelle Knoten
		for (int j= 1; j<n*n+1;j++)
			graph.insertNode(j, new GPSCoordinate(5,5));
		
		//kurze kanten
		int pos = 2+n;
		int i = 3;
		weg(graph,1,2,(n*n)+1,2);
		weg(graph,2,2+n,(n*n)+2,2);
		while (pos<(n*n)-n) {
			weg(graph,pos,pos+1,(n*n)+i++,2);
			weg(graph,pos+1,pos+n+1,(n*n)+i++,2);
			pos = pos+n+1;
		}
		
		//alle kanten
		i =1;
		for (i=1; i<n*n;i++) {
			if ((i % n) > 0) {
				weg(graph,i,i+1,i,10);
				if (i <= (n*n) -n) {
					weg(graph,i+1,i+1+n,i,10);
				}
			}
				
		}
		
		for(i =1; i< n*n - n ; i+=n) {
			graph.insertEdge(i, i+n, 100+i, 3, StreetType.PRIMARY);
			graph.insertEdge(i+n, i, 100+i, 3, StreetType.PRIMARY);
		}
		for(i=(n*n)-n+1;i<n*n;i++) {
			graph.insertEdge(i, i+1, 100+i, 3, StreetType.PRIMARY);
			graph.insertEdge(i+1, i, 100+i, 3, StreetType.PRIMARY);
		}
		
		
		return graph;
	}
	
	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		
		fastBackup = Config.fastestPathMode;
		
		//Hier werden die TestGraphen angelegt, die nachher benutzt werden.
		//Test3 - ein isolierter Knoten
		test3 = new HierarchyMapGraph();
		test3.insertNode(2,1,1);
		
		//Test5 - fuenf Knoten, Knoten 5 hat keine ausgehenden Kanten
		test5 = new HierarchyMapGraph();
		test5.insertNode(1,1,1);
		test5.insertNode(2,1,1);
		test5.insertNode(3,1,1);
		test5.insertNode(4,1,1);
		test5.insertNode(5,1,1);
		test5.insertEdge(1, 5, 10, 1, StreetType.MOTORWAY,(byte)0);
		test5.insertEdge(2, 5, 20, 1, StreetType.MOTORWAY,(byte)0);
		test5.insertEdge(3, 5, 30, 1, StreetType.MOTORWAY,(byte)0);
		test5.insertEdge(4, 5, 40, 1, StreetType.MOTORWAY,(byte)0);
		
		//test8 - 4 Knoten mit einem Zykel
		test8 = new HierarchyMapGraph();
		test8.insertNode(1,1,1);
		test8.insertNode(2,1,1);
		test8.insertNode(3,1,1);
		test8.insertNode(4,1,1);
		test8.insertEdge(1, 1, 10, 1, StreetType.MOTORWAY,(byte)0);
		test8.insertEdge(1, 2, 20, 5, StreetType.MOTORWAY,(byte)0);
		test8.insertEdge(1, 3, 30, 10, StreetType.MOTORWAY,(byte)0);
		test8.insertEdge(1, 4, 40, 15, StreetType.MOTORWAY,(byte)0);
		
		//testGraph1 - 5 Knoten 
		testGraph1 = new HierarchyMapGraph();
		testGraph1.insertNode(1,1,1);
		testGraph1.insertNode(2,1,1);
		testGraph1.insertNode(3,1,1);
		testGraph1.insertNode(4,1,1);
		testGraph1.insertNode(5,1,1);
		testGraph1.insertEdge(1, 2, 10, 10, StreetType.MOTORWAY,(byte)0);
		testGraph1.insertEdge(2, 1, 20, 10, StreetType.MOTORWAY,(byte)0);
		testGraph1.insertEdge(1, 3, 30, 1, StreetType.MOTORWAY,(byte)0);
		testGraph1.insertEdge(1, 4, 40, 1, StreetType.MOTORWAY,(byte)0);
		testGraph1.insertEdge(1, 5, 50, 1, StreetType.MOTORWAY,(byte)0);
		
		//testGraph2 - 6 Knoten mit Pfaden
		testGraph2 = new HierarchyMapGraph();
		testGraph2.insertNode(1,1,1);
		testGraph2.insertNode(2,1,1);
		testGraph2.insertNode(3,1,1);
		testGraph2.insertNode(4,1,1);
		testGraph2.insertNode(5,1,1);
		testGraph2.insertNode(6,1,1);
		testGraph2.insertEdge(1, 2, 10, 10, StreetType.MOTORWAY,(byte)0);
		testGraph2.insertEdge(2, 1, 20, 10, StreetType.MOTORWAY,(byte)0);
		testGraph2.insertEdge(1, 3, 30, 1, StreetType.MOTORWAY,(byte)0);
		testGraph2.insertEdge(1, 4, 40, 3, StreetType.MOTORWAY,(byte)0);
		testGraph2.insertEdge(1, 5, 50, 3, StreetType.MOTORWAY,(byte)0);
		testGraph2.insertEdge(3, 6, 60, 1, StreetType.MOTORWAY,(byte)0);
		
		//testGraph3 - 5 Knoten
		testGraph3 = new HierarchyMapGraph();
		testGraph3.insertNode(1,1,1);
		testGraph3.insertNode(2,1,1);
		testGraph3.insertNode(3,1,1);
		testGraph3.insertNode(4,1,1);
		testGraph3.insertNode(5,1,1);
		testGraph3.insertEdge(2, 1, 20, 1, StreetType.MOTORWAY,(byte)0);
		testGraph3.insertEdge(1, 3, 30, 10, StreetType.MOTORWAY,(byte)0);
		testGraph3.insertEdge(1, 4, 40, 100, StreetType.MOTORWAY,(byte)0);
		testGraph3.insertEdge(1, 5, 50, 1000, StreetType.MOTORWAY,(byte)0);
		
		
		//testGraph4 - 4Knoten und Kreis
		testGraph4 = new HierarchyMapGraph();
		testGraph4.insertNode(1,1,1);
		testGraph4.insertNode(2,1,1);
		testGraph4.insertNode(3,1,1);
		testGraph4.insertNode(4,1,1);
		testGraph4.insertEdge(1, 2, 10, 1, StreetType.MOTORWAY,(byte)0);
		testGraph4.insertEdge(2, 1, 20, 1, StreetType.MOTORWAY,(byte)0);
		testGraph4.insertEdge(1, 3, 30, 4, StreetType.MOTORWAY,(byte)0);
		testGraph4.insertEdge(3, 1, 35, 4, StreetType.MOTORWAY,(byte)0);
		testGraph4.insertEdge(1, 4, 40, 3, StreetType.MOTORWAY,(byte)0);
		testGraph4.insertEdge(2, 3, 45, 1, StreetType.MOTORWAY,(byte)0);
		testGraph4.insertEdge(3, 2, 50, 1, StreetType.MOTORWAY,(byte)0);
	}

	/**
	 * Test method for {@link algorithmen.Dijkstra#neighbourhood(graphenbib.HierarchyMapGraph, int, byte, boolean)}.
	 */
	@Test
	public void testNeighbourhoodShort() throws Exception {
		
		double test = 0;
		boolean fehler;
		Config.fastestPathMode = false;
		
		/*
		 * Test 1 - Nullpointer als Graph - erwarte Fehler/Exception
		 */
		/*
		Dijkstra.H = 2;
		fehler = false;
		try {
			test = Dijkstra.neighbourhood(null, 1, true);
		} catch (Exception e) {
			fehler =true;
		}
		
		if (fehler == false)
			fail("Test 1: Einen Nullpointer zu uebergeben hat keine Exception geworfen");
		*/
		
		/*
		 * Test 2 - illegale Node-ID - nutze dazu test3
		 */
		
		fehler = false;
		try{
			test = Dijkstra.neighbourhood(test3, 55,(byte)0, true);
		}catch(Exception e) {
			fehler = true;
		}
		if (fehler == false){
			fail("Test 2: Bei Eingabe einer ungueltigen Node-ID wurde keine Exception geworfen");
		}
		
		/*
		 * Test 3 - nutze test3 - erwarte fuer Startknoten 2 Ausgabe -1
		 */
		
		test = Dijkstra.neighbourhood(test3, 2, (byte)0,true);
		if (test != -1)
			fail("Test 3: Die vorwaerts-scuhe auf einem isolierten Knoten liefert nicht -1 sondern: "+test);
		
		/*
		 * Test 4 - nutze test3 - erwarte fuer Startknoten 2 Ausgabe -1
		 */
		
		test = Dijkstra.neighbourhood(test3, 2,(byte)0, false);
		if (test != -1)
			fail("Test 4: Die ruekwaerts-scuhe auf einem isolierten Knoten liefert nicht -1 sondern: "+test);
		
		/*
		 * Test 5 - nutze test5 - es existieren Kanten, aber wieder soll -1 ausgegben werden
		 */
		
		test = Dijkstra.neighbourhood(test5, 5, (byte)0,true);
		if (test != -1)
			fail("Test 5: Die vorwaerts-scuhe auf einem isolierten Knoten bzql. ausgehender Knoten liefert nicht -1 sondern: "+test);
		
		
		/*
		 * Test 6 - nutze test5 - es existieren Kanten, aber wieder soll -1 ausgegben werden
		 */
		
		test = Dijkstra.neighbourhood(test5, 2, (byte)0,false);
		if (test != -1)
			fail("Test 6: Die rueck.-scuhe auf einem isolierten Knoten bzql. eingehender Knoten liefert nicht -1 sondern: "+test);
		
		
		/*
		 * Test 7 - uebergebe einen leeren Graphen - erwarte auch hier -1
		 */
		fehler = false;
		
		try{
			test = Dijkstra.neighbourhood(new HierarchyMapGraph(), 5, (byte)0,true);
		}catch(Exception e){
			fehler = true;
		}
		if (fehler == false)
			fail("Test 7: Der leere Graph liefert nicht -1 sondern: "+test);
		
		
		/*
		 * Test 8 - nutze test8 - es existiert ein Zykel - evtl. bringt dieser den Algo durcheinander - erwarte 10
		 */
			/*
		Dijkstra.H = 2;		
		test = Dijkstra.neighbourhood(test8, 1, true);
		if (test != 10)
			fail("Test 8: Der Graph mit trivialem Zykel liefert nicht 10 sondern: "+test);
			*/
		
		/*
		 * testGraph1 - teste korrekte Werte
		 */
		Config.H = 2;
		test = Dijkstra.neighbourhood(testGraph1, 1,(byte)0, true);
		if (test != 1)
			fail("TestGraph1: Der Algo liefert nicht 1 sondern: "+test);
		
		
		/*
		 * testGraph2 - teste korrekte Werte
		 */
		Config.H = 2;
		test = Dijkstra.neighbourhood(testGraph2, 1,(byte)0, true);
		if (test != 2)
			fail("TestGraph2: Der Algo liefert nicht 2 sondern: "+test);
		
		Config.H = 4;
		test = Dijkstra.neighbourhood(testGraph2, 1, (byte)0,true);
		if (test != 3)
			fail("TestGraph2: Der Algo liefert nicht 3 sondern: "+test);
		
		Config.H = 5;
		test = Dijkstra.neighbourhood(testGraph2, 1,(byte)0, true);
		if (test != 10)
			fail("TestGraph2: Der Algo liefert nicht 10 sondern: "+test);
		
		
		Config.H = 6;
		test = Dijkstra.neighbourhood(testGraph2, 1, (byte)0,true);
		if (test != -1)
			fail("TestGraph2: Der Algo liefert nicht -1 sondern: "+test);
		
		
		/*
		 * testGraph3 - teste korrekte Werte
		 */
		Config.H = 3;
		test = Dijkstra.neighbourhood(testGraph3, 1, (byte)0,true);
		if (test != 1000)
			fail("TestGraph3: Der Algo liefert nicht 1000 sondern: "+test);
		
		Config.H = 4;
		test = Dijkstra.neighbourhood(testGraph3, 1, (byte)0,true);
		if (test != -1)
			fail("TestGraph3: Der Algo liefert nicht -1 sondern: "+test);
		
		
		/*
		 * testGraph4 - teste korrekte Werte (nciht-trivialer Zykel, keine Dreiecksungleichung)
		 */
		
		Config.H = 2;
		test = Dijkstra.neighbourhood(testGraph4, 1,(byte)0, true);
		if (test != 2)
			fail("TestGraph4: Der Algo liefert nicht 2 sondern: "+test);
		
		Config.H = 3;
		test = Dijkstra.neighbourhood(testGraph4, 1,(byte)0, true);
		if (test != 3)
			fail("TestGraph4: Der Algo liefert nicht 3 sondern: "+test);
		
		Config.H = 4;
		test = Dijkstra.neighbourhood(testGraph4, 1, (byte)0,true);
		if (test != -1)
			fail("TestGraph4: Der Algo liefert nicht -1 sondern: "+test+" evtl. Knoten doppelt begangen?");
		
		
		/*
		 * testGraph4 - teste korrekte Werte (nciht-trivialer Zykel, keine Dreiecksungleichung) fuer rueckwaerts
		 */

		
		Config.H = 2;
		test = Dijkstra.neighbourhood(testGraph4, 2, (byte)0,false);

		if (test != 1)
			fail("TestGraph4: Der Algo(false) liefert nicht 1 sondern: "+test);

		
		Config.H = 3;
		test = Dijkstra.neighbourhood(testGraph4, 2,(byte)0, false);

		if (test != -1)
			fail("TestGraph4: Der Algo(false) liefert nicht -1 sondern: "+test);

		
		Config.H = 4;
		test = Dijkstra.neighbourhood(testGraph4, 2,(byte)0, false);
		if (test != -1)
			fail("TestGraph4: Der Algo(false) liefert nicht -1 sondern: "+test+" evtl. Knoten doppelt begangen?");
	}
	
	@Test
	public void testNeighbourhoodFast() throws Exception {
		long test = 0;
		boolean fehler;
		Config.fastestPathMode = true;
		
		/*
		 * Test 1 - Nullpointer als Graph - erwarte Fehler/Exception
		 */
		/*
		Dijkstra.H = 2;
		fehler = false;
		try {
			test = Dijkstra.neighbourhood(null, 1, true);
		} catch (Exception e) {
			fehler =true;
		}
		
		if (fehler == false)
			fail("Test 1: Einen Nullpointer zu uebergeben hat keine Exception geworfen");
		*/
		
		/*
		 * Test 2 - illegale Node-ID - nutze dazu test3
		 */
		
		fehler = false;
		try{
			test = Dijkstra.neighbourhood(test3, 55,(byte)0, true);
		}catch(Exception e) {
			fehler = true;
		}
		if (fehler == false){
			fail("Test 2: Bei Eingabe einer ungueltigen Node-ID wurde keine Exception geworfen");
		}
		
		/*
		 * Test 3 - nutze test3 - erwarte fuer Startknoten 2 Ausgabe -1
		 */
		
		test = Dijkstra.neighbourhood(test3, 2, (byte)0,true);
		if (test != -1)
			fail("Test 3: Die vorwaerts-scuhe auf einem isolierten Knoten liefert nicht -1 sondern: "+test);
		
		/*
		 * Test 4 - nutze test3 - erwarte fuer Startknoten 2 Ausgabe -1
		 */
		
		test = Dijkstra.neighbourhood(test3, 2,(byte)0, false);
		if (test != -1)
			fail("Test 4: Die ruekwaerts-scuhe auf einem isolierten Knoten liefert nicht -1 sondern: "+test);
		
		/*
		 * Test 5 - nutze test5 - es existieren Kanten, aber wieder soll -1 ausgegben werden
		 */
		
		test = Dijkstra.neighbourhood(test5, 5, (byte)0,true);
		if (test != -1)
			fail("Test 5: Die vorwaerts-scuhe auf einem isolierten Knoten bzql. ausgehender Knoten liefert nicht -1 sondern: "+test);
		
		
		/*
		 * Test 6 - nutze test5 - es existieren Kanten, aber wieder soll -1 ausgegben werden
		 */
		
		test = Dijkstra.neighbourhood(test5, 2, (byte)0,false);
		if (test != -1)
			fail("Test 6: Die rueck.-scuhe auf einem isolierten Knoten bzql. eingehender Knoten liefert nicht -1 sondern: "+test);
		
		
		/*
		 * Test 7 - uebergebe einen leeren Graphen - erwarte auch hier -1
		 */
		fehler = false;
		
		try{
			test = Dijkstra.neighbourhood(new HierarchyMapGraph(), 5, (byte)0,true);
		}catch(Exception e){
			fehler = true;
		}
		if (fehler == false)
			fail("Test 7: Der leere Graph liefert nicht -1 sondern: "+test);
		
		
		/*
		 * Test 8 - nutze test8 - es existiert ein Zykel - evtl. bringt dieser den Algo durcheinander - erwarte 10
		 */
			/*
		Dijkstra.H = 2;		
		test = Dijkstra.neighbourhood(test8, 1, true);
		if (test != 10)
			fail("Test 8: Der Graph mit trivialem Zykel liefert nicht 10 sondern: "+test);
			*/
		
		/*
		 * testGraph1 - teste korrekte Werte
		 */
		Config.H = 2;
		test = Dijkstra.neighbourhood(testGraph1, 1,(byte)0, true);
		if (test!= 1*Config.getSpeedFactor(StreetType.MOTORWAY))
			fail("TestGraph1: Der Algo liefert nicht "+1*Config.getSpeedFactor(StreetType.MOTORWAY)*10000+" sondern: "+test);
		
		
		/*
		 * testGraph2 - teste korrekte Werte
		 */
		Config.H = 2;
		test = Dijkstra.neighbourhood(testGraph2, 1,(byte)0, true);
		if (test!= 2*Config.getSpeedFactor(StreetType.MOTORWAY))
			fail("TestGraph2: Der Algo liefert nicht "+2*Config.getSpeedFactor(StreetType.MOTORWAY)*10000+" sondern: "+test);
		
		Config.H = 4;
		test = Dijkstra.neighbourhood(testGraph2, 1, (byte)0,true);
		if (test!= 3*Config.getSpeedFactor(StreetType.MOTORWAY))
			fail("TestGraph2: Der Algo liefert nicht "+3*Config.getSpeedFactor(StreetType.MOTORWAY)*10000+" sondern: "+test);
		
		Config.H = 5;
		test = Dijkstra.neighbourhood(testGraph2, 1,(byte)0, true);
		if (test!= 10*Config.getSpeedFactor(StreetType.MOTORWAY))
			fail("TestGraph2: Der Algo liefert nicht "+10*Config.getSpeedFactor(StreetType.MOTORWAY)*10000+" sondern: "+test);
		
		
		Config.H = 6;
		test = Dijkstra.neighbourhood(testGraph2, 1, (byte)0,true);
		if (test != -1)
			fail("TestGraph2: Der Algo liefert nicht -1 sondern: "+test);
		
		
		/*
		 * testGraph3 - teste korrekte Werte
		 */
		Config.H = 3;
		test = Dijkstra.neighbourhood(testGraph3, 1, (byte)0,true);
		if (test!= 1000*Config.getSpeedFactor(StreetType.MOTORWAY))
			fail("TestGraph3: Der Algo liefert nicht "+1000*Config.getSpeedFactor(StreetType.MOTORWAY)*10000+" sondern: "+test);
		
		Config.H = 4;
		test = Dijkstra.neighbourhood(testGraph3, 1, (byte)0,true);
		if (test != -1)
			fail("TestGraph3: Der Algo liefert nicht -1 sondern: "+test);
		
		
		/*
		 * testGraph4 - teste korrekte Werte (nciht-trivialer Zykel, keine Dreiecksungleichung)
		 */
		
		Config.H = 2;
		test = Dijkstra.neighbourhood(testGraph4, 1,(byte)0, true);
		if (test!= 2*Config.getSpeedFactor(StreetType.MOTORWAY))
			fail("TestGraph4: Der Algo liefert nicht "+2*Config.getSpeedFactor(StreetType.MOTORWAY)*10000+" sondern: "+test);
		
		Config.H = 3;
		test = Dijkstra.neighbourhood(testGraph4, 1,(byte)0, true);
		if (test!= 3*Config.getSpeedFactor(StreetType.MOTORWAY))
			fail("TestGraph4: Der Algo liefert nicht "+3*Config.getSpeedFactor(StreetType.MOTORWAY)*10000+" sondern: "+test);
		
		Config.H = 4;
		test = Dijkstra.neighbourhood(testGraph4, 1, (byte)0,true);
		if (test != -1)
			fail("TestGraph4: Der Algo liefert nicht -1 sondern: "+test+" evtl. Knoten doppelt begangen?");
		
		
		/*
		 * testGraph4 - teste korrekte Werte (nciht-trivialer Zykel, keine Dreiecksungleichung) fuer rueckwaerts
		 */

		
		Config.H = 2;
		test = Dijkstra.neighbourhood(testGraph4, 2, (byte)0,false);

		if (test!= 1*Config.getSpeedFactor(StreetType.MOTORWAY))
			fail("TestGraph4: Der Algo(false) liefert nicht 1 sondern: "+test);

		
		Config.H = 3;
		test = Dijkstra.neighbourhood(testGraph4, 2,(byte)0, false);

		if (test != -1)
			fail("TestGraph4: Der Algo(false) liefert nicht -1 sondern: "+test);

		
		Config.H = 4;
		test = Dijkstra.neighbourhood(testGraph4, 2,(byte)0, false);
		if (test != -1)
			fail("TestGraph4: Der Algo(false) liefert nicht -1 sondern: "+test+" evtl. Knoten doppelt begangen?");
	}
	
	/**
	 * Test method for {@link algorithmen.Dijkstra#bidirectional(graphenbib.MapGraph, int, int)}.
	 */

	@Test
	public void testBidirectional() throws Exception {
		
		Config.fastestPathMode = false;
		
		//boolean fehler = false;
		ArrayList<MapNode> liste = null;
		MapGraph graph = null;
		String test;
		
		/*
		 * Test 1 
		 * Teste auf einem leeren Graphen - erwarte eine leere Liste
		 */
		
		
		try {
			liste = Dijkstra.bidirectional(new MapGraph(new GPSCoordinate(50,-50),new GPSCoordinate(-50,50)), 5, 6);
		} catch (Exception e) {
			fail("Auf einem leeren Graohen wurde keine Exception geworfen");
		}
			
		/*
		if (liste.size() != 0)
			fail("Die Ausgabe auf einem leeren Graphen sollte leer sein. Stattdessen gibt es so viele Elemente: "+liste.size());
		*/
		
		/*
		 * Test 2
		 * Teste mit nciht-existerenden End-Knoten - erwarte eine leere Liste
		 */
			
		graph = randomGraph(50,0.3f,3);
			
		//fehler = false;
		try {
			liste = Dijkstra.bidirectional(graph, 5, 100);
		} catch (Exception e) {
			fail("Bei nicht-existierendem Endknoten wurde eine Exception geworfen, statt einer leeren Liste auszugeben: "+e.getLocalizedMessage());
		}
		if (liste.size() != 0)
			fail("Die Ausgabe ohne existerenden Endknoten sollte leer sein. Stattdessen gibt es so viele Elemente: "+liste.size());
			
		
		
		/*
		 * Test 3
		 * Teste mit unerrecihbaren End-Knoten - erwarte leere Liste
		 */
		
		
		graph.insertNode(10001, new GPSCoordinate(0,0));
		try {
			liste = Dijkstra.bidirectional(graph, 5, 10001);
		} catch (Exception e) {
			fail("Bei nicht-erreichbaren Endknoten wurde eine Exception geworfen, statt einer leeren Liste auszugeben: "+e.getLocalizedMessage());
		}
		if (liste.size() != 0)
			fail("Die Ausgabe sollte leer sein. Stattdessen gibt es so viele Elemente: "+liste.size());
			
		
		/*
		 * Test 4
		 * Teste mit gleichen start-/End-Knoten - erwarte leere Liste
		 */
			
		try {
			liste = Dijkstra.bidirectional(graph, 5, 5);
		} catch (Exception e) {
			fail("Bei nicht-existierendem Endknoten wurde eine Exception geworfen, statt einer leeren Liste auszugeben: "+e.getLocalizedMessage());
		}
		if (liste.size() != 0)
			fail("Die Ausgabe auf start=Ende sollte leer sein. Stattdessen gibt es so viele Elemente: "+liste.size());
			
		
		
		/*
		 * Test 5
		 * Test mit wohldefiniertem Graphen die korrekten Entfernungen
		 * Test zunaechst im Falle von Einbahnstrassen
		 */
		// Definiere Graph:
			
		graph = new MapGraph(new GPSCoordinate(50,-50),new GPSCoordinate(-50,50));
		for(int i=1;i<9;i++) {
			graph.insertNode(i, new GPSCoordinate(i,i));
		}
		graph.insertEdge(1, 2, 1, 1, StreetType.SECONDARY);
		graph.insertEdge(1, 3, 2, 1, StreetType.SECONDARY);
		graph.insertEdge(1, 4, 3, 5, StreetType.SECONDARY);
		graph.insertEdge(2, 3, 4, 2, StreetType.SECONDARY);
		graph.insertEdge(2, 4, 5, 3, StreetType.SECONDARY);
		graph.insertEdge(3, 5, 6, 6, StreetType.SECONDARY);
		graph.insertEdge(4, 6, 7, 1, StreetType.SECONDARY);
		graph.insertEdge(4, 5, 8, 1, StreetType.SECONDARY);
		graph.insertEdge(4, 7, 9, 4, StreetType.SECONDARY);
		graph.insertEdge(6, 7, 10, 2, StreetType.SECONDARY);
		graph.insertEdge(5, 7, 11, 3, StreetType.SECONDARY);
		graph.insertEdge(5, 8, 12, 5, StreetType.SECONDARY);
		graph.insertEdge(7, 8, 13, 2, StreetType.SECONDARY);
		
		Logger.getInstance().log("DijkstraTest","======");
		
		//Test nur die Strecken 1->8 und die Strecke 8->1 (erwarte Laenge 9 und eine leere Liste)
		liste = Dijkstra.bidirectional(graph, 1, 8);
		if (liste.size() != 6)
			fail("Ausgabeliste hat falsche Groesse - erwarte 6, habe: "+liste.size());
		//Teste korrekte Reihenfolge
		
		test = Pfad(liste);
		if (!test.equalsIgnoreCase("1-2-4-6-7-8"))
			fail("Die Reihenfolge der besuchten Knoten ist falsch. Erwarte: 1-2-4-6-7-8, habe: "+test);
		
		liste = Dijkstra.bidirectional(graph, 8, 1);
		if (liste.size() != 0)
			fail("Ausgabeliste hat falsche Groesse - erwarte 0, habe: "+liste.size());
			
		/*
		 * Test 6
		 * Teste nun mit wegen (beide Richtungen) verschiedene kurze wege
		 */
		
		//Definiere Graph
		graph = new MapGraph(new GPSCoordinate(50,-50),new GPSCoordinate(-50,50));
		for(int i=1;i<9;i++) {
			graph.insertNode(i, new GPSCoordinate(i,i));
		}
		weg(graph,1,2,1,1);
		weg(graph,1,3,2,1);
		weg(graph,1,4,3,5);
		weg(graph,2,3,4,2);
		weg(graph,2,4,5,3);
		weg(graph,3,5,6,6);
		weg(graph,4,6,7,1);
		weg(graph,4,5,8,1);
		weg(graph,4,7,9,4);
		weg(graph,6,7,10,2);
		weg(graph,5,7,11,3);
		weg(graph,5,8,12,5);
		weg(graph,7,8,13,2);

		//Teste nun wieder 1->8 (gleiches Ergebnis wie eben bitte!)
		liste = Dijkstra.bidirectional(graph, 1, 8);
		if (liste.size() != 6)
			fail("Ausgabeliste hat falsche Groesse - erwarte 6, habe: "+liste.size());
		//teste Reihenfolge
		test = Pfad(liste);
		if (!test.equalsIgnoreCase("1-2-4-6-7-8"))
			fail("Die Reihenfolge der besuchten Knoten ist falsch. Erwarte: -1-2-4-6-7-8, habe: "+test);
		
		//Teste 8->1
		liste = Dijkstra.bidirectional(graph, 8, 1);
		if (liste.size() != 6)
			fail("Ausgabeliste hat falsche Groesse - erwarte 6, habe: "+liste.size());
		//teste Reihenfolge
		test = Pfad(liste);
		if (!test.equalsIgnoreCase("8-7-6-4-2-1"))
			fail("Die Reihenfolge der besuchten Knoten ist falsch. Erwarte: 8-7-6-4-2-1, habe: "+test);
		
		
		//Test nun 1->5
		liste = Dijkstra.bidirectional(graph, 1, 5);
		if (liste.size() != 4)
			fail("Ausgabeliste hat falsche Groesse - erwarte 3, habe: "+liste.size());
		//teste Reihenfolge
		test = Pfad(liste);
		if (!test.equalsIgnoreCase("1-2-4-5"))
			fail("Die Reihenfolge der besuchten Knoten ist falsch. Erwarte: 1-4-5, habe: "+test);
		
		//Teste nun 3->4
		liste = Dijkstra.bidirectional(graph, 3, 4);
		if (liste.size() != 3)
			fail("Ausgabeliste hat falsche Groesse - erwarte 3, habe: "+liste.size());
		//teste Reihenfolge
		test = Pfad(liste);
		if (!test.equalsIgnoreCase("3-2-4"))
			fail("Die Reihenfolge der besuchten Knoten ist falsch. Erwarte: 3-2-4, habe: "+test);
		
		//Teste 7->4
		liste = Dijkstra.bidirectional(graph, 7, 4);
		if (liste.size() != 3)
			fail("Ausgabeliste hat falsche Groesse - erwarte 3, habe: "+liste.size());
		//teste Reihenfolge
		test = Pfad(liste);
		if (!test.equalsIgnoreCase("7-6-4"))
			fail("Die Reihenfolge der besuchten Knoten ist falsch. Erwarte: 7-6-4, habe: "+test);
		
		/*
		 * Test 7
		 * erstelle zufallige Graphen und pruefe, ob ein ehler auftritt
		 * Teste dabei auch die Zeit
		 */
		
		try {
			//for (int l = 0;l<10;l++) {
				BelastungsTest(50,0.3f,3);
				BelastungsTest(100,0.3f,5);
				BelastungsTest(500,0.3f,5);
				BelastungsTest(1000,0.3f,5);
				BelastungsTest(5000,0.3f,5);
				//BelastungsTest(10000,0.3f,5);
				//BelastungsTest(50000,0.3f,5);
				//BelastungsTest(100000,0.3f,5);
				//BelastungsTest(500000,0.3f,5);
				//BelastungsTest(1000000,0.3f,5);
			//}
		} catch (Exception e) {
			fail("Es gab einen Fehler innerhalb der Belastungstests: "+e.getLocalizedMessage());
		}
		
		/*
		long time =0;
		try{
			for (int i=0; i<10;i++){
				//Logger.getInstance().log("DijkstraTest","Durchlauf"+ (i+1));
				time += BelastungsTest(5000,0.1f,5);
				//Logger.getInstance().log("DijkstraTest","Gesamtzeit bisher: "+time+"ms");
				time += BelastungsTest(5000,0.2f,5);
				//Logger.getInstance().log("DijkstraTest","Gesamtzeit bisher: "+time+"ms");
				time += BelastungsTest(5000,0.3f,5);
				//Logger.getInstance().log("DijkstraTest","Gesamtzeit bisher: "+time+"ms");
				time += BelastungsTest(5000,0.4f,5);
				//Logger.getInstance().log("DijkstraTest","Gesamtzeit bisher: "+time+"ms");
				time += BelastungsTest(5000,0.5f,5);
				//Logger.getInstance().log("DijkstraTest","Gesamtzeit bisher: "+time+"ms");
				time += BelastungsTest(5000,0.6f,5);
				//Logger.getInstance().log("DijkstraTest","Gesamtzeit bisher: "+time+"ms");
				time += BelastungsTest(5000,0.7f,5);
				//Logger.getInstance().log("DijkstraTest","Gesamtzeit bisher: "+time+"ms");
				time += BelastungsTest(5000,0.8f,5);
				//Logger.getInstance().log("DijkstraTest","Gesamtzeit bisher: "+time+"ms");
				time += BelastungsTest(5000,0.9f,5);
				//Logger.getInstance().log("DijkstraTest","Gesamtzeit bisher: "+time+"ms");
				time += BelastungsTest(5000,1.0f,5);
				//Logger.getInstance().log("DijkstraTest","Gesamtzeit bisher: "+time+"ms");
			}
		} catch (Exception e) {
			fail("Es gab einen Fehler innerhalb der Belastungstests: "+e.getLocalizedMessage());
		}
		*/
		

		/*
		 * Test 8
		 * Teste die Dtestgraph
		 */
		
		MapGraph testGraph = null;
		OSMImporter osm_imp;
		
		try {
			osm_imp = new OSMImporter(new File("testdateien/DtestGraph.osm"));
			testGraph = osm_imp.getTile(osm_imp.getMapCenter(), 0);
		} catch (Exception e) {
			fail("Fehler beim Einlesen der DtestGraph: "+e.getLocalizedMessage());
		}
		
		ArrayList<MapNode> myway = Dijkstra.bidirectional(testGraph, 3, 10);
		
		Logger.getInstance().log("DijkstraTest", "Die Laenge der Strecke ist: "+weglaenge(myway));
		
		/*
		 * Gitter-Test
		 */
		Logger.getInstance().log("DijkstraTest","Starte Gitter-Test (n=3)");
		MapGraph Gittertest = GitterGraph(3);
		liste = Dijkstra.bidirectional(Gittertest, 1, 3*3);
		if (!(Pfad(liste).equals("1-2-5-6-9")))
			fail("Der Algo ibt nicht auf dem Gitter die kuerzeste Route aus");
		Logger.getInstance().log("DijkstraTest","Weglaenge (soll: 4): "+weglaenge(liste));
		if (weglaenge(liste) != 4)
			fail("Der berechnete Weg ist nciht der kuerzeste!");
			/*
		Logger.getInstance().log("DijkstraTest","Starte Gitter-Test (n=1.000)");
		Gittertest = GitterGraph(1000);
		liste = Dijkstra.bidirectional(Gittertest, 1, 1000*1000);
		Logger.getInstance().log("DijkstraTest","Weglaenge (soll: 1998): "+weglaenge(liste));
		if (weglaenge(liste) != 1998)
			fail("Der berechnete Weg ist nciht der kuerzeste!");
			*/
	}		
	
	
	@Test
	public void testWeight() throws Exception {
		
		Config.fastestPathMode = true;
		
		//Hier wird ein Graph erstellt, wo die kuerzeste Route nicht der schnellsten entspricht.
		int gr = 4;
		MapGraph weightGraph = GitterWeightGraph(gr);
		
		Logger.getInstance().log("DijkstraTest","Starte WeightGitter-Test (n="+gr+")");

		ArrayList<MapNode> liste = Dijkstra.bidirectional(weightGraph, 1, gr*gr);
		Logger.getInstance().log("DijkstraTest","Weglaenge (soll: "+((2*gr)-2)*3+"): "+weglaengeStrict(liste));
		Logger.getInstance().log("DijkstraTest", "WegGewicht: "+weglaenge(liste));
		Logger.getInstance().log("DijkstraTest","Pfad: "+Pfad(liste));
		
		//Pruefe auf krrekte Weglaenge - nicht dauer!
		if (weglaenge(liste) == ((2*gr)-2)*2)
			fail("Der berechnete Weg ist nciht der schnellste, sondern der kuerzeste");
		
		if (weglaengeStrict(liste) != ((2*gr)-2)*3)
			fail("Der berechnete Weg ist nciht der schnellste. Soll:"+((2*gr)-2)*3+" Ist:"+weglaengeStrict(liste));
		
		
	}
	
	@AfterClass
	public static void cleanUp() {
		//wiederherstellen
		Config.fastestPathMode = fastBackup;
	}
	
}
