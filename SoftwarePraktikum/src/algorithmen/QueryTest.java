/**
 * 
 */
package algorithmen;

import static org.junit.Assert.fail;
import graphenbib.GPSCoordinate;
import graphenbib.HierarchyMapGraph;
import graphenbib.HierarchyMapNode;
import graphenbib.MapGraph;
import graphenbib.Path;
import graphenbib.StreetType;

import java.util.ArrayList;
import java.util.Iterator;

import main.Config;
import main.Logger;

import org.junit.BeforeClass;
import org.junit.Test;

public class QueryTest {

	private static HierarchyMapGraph graph1;
	private static MapGraph test1;
	
	private static HierarchyMapGraph graph2;
	private static MapGraph test2;
	
	private static HierarchyMapGraph graph3;
	private static MapGraph test3;
	
	private static HierarchyMapGraph graph4;
	private static MapGraph test4;
	
	private static HierarchyMapGraph graph5;
	private static MapGraph test5;
	
	private static HierarchyMapGraph graph6;
	private static MapGraph test6;
	
	private static HierarchyMapGraph graph7;
	private static MapGraph test7;
	
	private static HierarchyMapGraph graph8;
	private static MapGraph test8;
	
	private static HierarchyMapGraph graph9;
	private static MapGraph test9;
	

	private static Logger logger = Logger.getInstance();
	
	private static String sender = "QueryTest";
	private static void weg(MapGraph graph, int start, int ende, int wayID, int laenge) throws Exception {
		graph.insertEdge(start, ende, wayID, laenge, StreetType.ROAD);
		graph.insertEdge(ende, start, wayID, laenge, StreetType.ROAD);
	}
	
	private String Pfad(ArrayList<Integer> liste) {
		String test = "";
		for (int i=0;i<liste.size()-1;i++) {
			test = test + liste.get(i) + "-" ;
		}
		test = test + liste.get(liste.size()-1);
		return test;
	}
	
	

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		
		GPSCoordinate graphKoord1 = new GPSCoordinate(90,-180);
		GPSCoordinate graphKoord2 = new GPSCoordinate(-90,180);
		
		//Hier werden die TestGraphen angelegt, die nachher benutzt werden.
		
		//Test1: 5 Knoten, ohne Nachbarschaft => keine Query moeglich
		test1 = new MapGraph(graphKoord1,graphKoord2);
		graph1 = new HierarchyMapGraph();
		
		test1.insertNode(1,new GPSCoordinate(0,0));
		test1.insertNode(2,new GPSCoordinate(1,1));
		test1.insertNode(3,new GPSCoordinate(2,2));
		test1.insertNode(4,new GPSCoordinate(3,3));
		test1.insertNode(77,new GPSCoordinate(4,4));
		
		weg(test1,1,2,1,1);
		weg(test1,2,3,2,2);
		weg(test1,3,4,3,2);
		weg(test1,4,77,4,1);

		test1.exportToHierachyGraph(graph1);
		Iterator<HierarchyMapNode> iter = graph1.getNodeIt();
		while(iter.hasNext()){
			HierarchyMapNode node = iter.next();
			node.setdH(0, (byte)0, true);
			node.setdH(0, (byte)0, false);
		}
		
		//Test2: 5 Knoten, mit Nachbarschaft => Query moeglich
		test2 = new MapGraph(graphKoord1,graphKoord2);
		graph2 = new HierarchyMapGraph();
		
		test2.insertNode(1,new GPSCoordinate(0,0));
		test2.insertNode(2,new GPSCoordinate(1,1));
		test2.insertNode(3,new GPSCoordinate(2,2));
		test2.insertNode(4,new GPSCoordinate(3,3));
		test2.insertNode(77,new GPSCoordinate(4,4));
		
		weg(test2,1,2,1,1);
		weg(test2,2,3,2,2);
		weg(test2,3,4,3,2);
		weg(test2,4,77,4,1);

		test2.exportToHierachyGraph(graph2);
		iter = graph2.getNodeIt();
		while(iter.hasNext()){
			HierarchyMapNode node = iter.next();
			Config.H = 2;
			long distance = Dijkstra.neighbourhood(graph2, node.getUID(),(byte) 0, true);
			node.setdH(distance, (byte) 0, true);

			distance = Dijkstra.neighbourhood(graph2, node.getUID(),(byte) 0, false);
			node.setdH(distance, (byte) 0, false);
		}
		
		//Test3: 6 Knoten, ohne Hierarchien (aber mit Nachbarschaften) => keine Query moeglich
		test3 = new MapGraph(graphKoord1,graphKoord2);
		graph3 = new HierarchyMapGraph();
		
		test3.insertNode(1,new GPSCoordinate(0,0));
		test3.insertNode(2,new GPSCoordinate(1,1));
		test3.insertNode(3,new GPSCoordinate(2,2));
		test3.insertNode(4,new GPSCoordinate(3,3));
		test3.insertNode(5,new GPSCoordinate(4,4));
		test3.insertNode(77,new GPSCoordinate(5,5));
		
		weg(test3,1,2,1,1);
		weg(test3,2,3,2,2);
		weg(test3,3,4,3,3);
		weg(test3,4,5,4,2);
		weg(test3,5,77,5,1);

		test3.exportToHierachyGraph(graph3);
		iter = graph3.getNodeIt();
		
		while(iter.hasNext()){
			HierarchyMapNode node = iter.next();
			Config.H = 2;
			long distance = Dijkstra.neighbourhood(graph3, node.getUID(),(byte) 0, true);
			node.setdH(distance, (byte) 0, true);

			distance = Dijkstra.neighbourhood(graph3, node.getUID(),(byte) 0, false);
			node.setdH(distance, (byte) 0, false);
		}
		
		//Test4: 6 Knoten, ohne Hierarchien (aber mit Nachbarschaften) => Query moeglich
		test4 = new MapGraph(graphKoord1,graphKoord2);
		graph4 = new HierarchyMapGraph();
		
		test4.insertNode(1,new GPSCoordinate(0,0));
		test4.insertNode(2,new GPSCoordinate(1,1));
		test4.insertNode(3,new GPSCoordinate(2,2));
		test4.insertNode(4,new GPSCoordinate(3,3));
		test4.insertNode(5,new GPSCoordinate(4,4));
		test4.insertNode(77,new GPSCoordinate(5,5));
		
		weg(test4,1,2,1,1);
		weg(test4,2,3,2,2);
		weg(test4,3,4,3,3);
		weg(test4,4,5,4,2);
		weg(test4,5,77,5,1);

		test4.exportToHierachyGraph(graph4);
		iter = graph4.getNodeIt();
		
		while(iter.hasNext()){
			HierarchyMapNode node = iter.next();
			Config.H = 2;
			long distance = Dijkstra.neighbourhood(graph4, node.getUID(),(byte) 0, true);
			node.setdH(distance, (byte) 0, true);

			distance = Dijkstra.neighbourhood(graph4, node.getUID(),(byte) 0, false);
			node.setdH(distance, (byte) 0, false);
		}
		
		graph4.getNode(3).getEdgeToNeighbour(4).setLevel((byte)1);
		graph4.getNode(4).getEdgeToNeighbour(3).setLevel((byte)1);
		

		//Test5: 7 Knoten, die mit Hierarchien 0-2 => Query moeglich
		test5 = new MapGraph(graphKoord1,graphKoord2);
		graph5 = new HierarchyMapGraph();
		
		test5.insertNode(1,new GPSCoordinate(0,0));
		test5.insertNode(2,new GPSCoordinate(1,1));
		test5.insertNode(3,new GPSCoordinate(2,2));
		test5.insertNode(4,new GPSCoordinate(3,3));
		test5.insertNode(5,new GPSCoordinate(4,4));
		test5.insertNode(6,new GPSCoordinate(5,5));
		test5.insertNode(77,new GPSCoordinate(6,6));
		
		weg(test5,1,2,1,1);
		weg(test5,2,3,2,2);
		weg(test5,3,4,3,3);
		weg(test5,4,5,4,3);
		weg(test5,5,6,5,2);
		weg(test5,6,77,6,1);

		test5.exportToHierachyGraph(graph5);
		iter = graph5.getNodeIt();
		
		while(iter.hasNext()){
			HierarchyMapNode node = iter.next();
			Config.H = 2;
			long distance = Dijkstra.neighbourhood(graph5, node.getUID(),(byte) 0, true);
			node.setdH(distance, (byte) 0, true);

			distance = Dijkstra.neighbourhood(graph5, node.getUID(),(byte) 0, false);
			node.setdH(distance, (byte) 0, false);
		}
	
		graph5.getNode(3).getEdgeToNeighbour(4).setLevel((byte)1);
		graph5.getNode(4).getEdgeToNeighbour(3).setLevel((byte)1);
		
		graph5.getNode(4).getEdgeToNeighbour(5).setLevel((byte)2);
		graph5.getNode(5).getEdgeToNeighbour(4).setLevel((byte)2);
		
		graph5.getNode(5).getEdgeToNeighbour(6).setLevel((byte)2);
		graph5.getNode(6).getEdgeToNeighbour(5).setLevel((byte)2);

		int[] array = {4,5,6};
		
		for (int node : array){
			long distance = Dijkstra.neighbourhood(graph5, node,(byte) 2, true);
			graph5.getNode(node).setdH(distance, (byte) 2, true);

			distance = Dijkstra.neighbourhood(graph5, node,(byte) 2, false);
			graph5.getNode(node).setdH(distance, (byte) 2, false);

		}

		//Test6: 6 Knoten, mit Hierarchien => Query moeglich
		//Achtung hier wird nicht der kuerzete Weg erwartet: Dies ist ein Test ob auch wirklich die Hierarchien verwendet werden.
		//Kuerzester Weg ist nur unter Missachtung aller Hierarchien findbar 
		test6 = new MapGraph(graphKoord1,graphKoord2);
		graph6 = new HierarchyMapGraph();
		
		test6.insertNode(1,new GPSCoordinate(0,0));
		test6.insertNode(2,new GPSCoordinate(1,1));
		test6.insertNode(3,new GPSCoordinate(2,2));
		test6.insertNode(4,new GPSCoordinate(3,3));
		test6.insertNode(5,new GPSCoordinate(4,4));
		test6.insertNode(6,new GPSCoordinate(5,5));
		test6.insertNode(77,new GPSCoordinate(6,6));
		
		weg(test6,1,2,1,10);
		weg(test6,2,3,2,20);
		weg(test6,3,4,3,30);
		weg(test6,4,5,4,30);
		weg(test6,5,6,5,20);
		weg(test6,6,77,6,10);
		weg(test6,2,4,7,51);
		weg(test6,4,6,8,51);
		
		test6.exportToHierachyGraph(graph6);
		iter = graph6.getNodeIt();
		
		while(iter.hasNext()){
			HierarchyMapNode node = iter.next();
			Config.H = 2;
			long distance = Dijkstra.neighbourhood(graph6, node.getUID(),(byte) 0, true);
			node.setdH(distance, (byte) 0, true);

			distance = Dijkstra.neighbourhood(graph6, node.getUID(),(byte) 0, false);
			node.setdH(distance, (byte) 0, false);
		}
		
		graph6.getNode(2).getEdgeToNeighbour(4).setLevel((byte)1);
		graph6.getNode(4).getEdgeToNeighbour(2).setLevel((byte)1);
		
		graph6.getNode(4).getEdgeToNeighbour(6).setLevel((byte)1);
		graph6.getNode(6).getEdgeToNeighbour(4).setLevel((byte)1);
		
		//Test7: 37 Knoten, mit Hierarchien => Query moeglich
		//Hier wird insbesondere Getestet ob das gehen auf einem Core mueglich ist 
		test7 = new MapGraph(graphKoord1,graphKoord2);
		graph7 = new HierarchyMapGraph();
		
		test7.insertNode(77,new GPSCoordinate(0,0));
		
		test7.insertNode(1,new GPSCoordinate(1,1));
		test7.insertNode(2,new GPSCoordinate(2,2));
		test7.insertNode(3,new GPSCoordinate(3,3));
		test7.insertNode(4,new GPSCoordinate(4,4));
		
		for (int i= 1; i<=4;i++){
			test7.insertNode(i*10+1,new GPSCoordinate(5,5));
			test7.insertNode(i*10+2,new GPSCoordinate(6,6));
			test7.insertNode(i*10+3,new GPSCoordinate(7,7));
			test7.insertNode(i*10+4,new GPSCoordinate(8,8));
			test7.insertNode(i*10+5,new GPSCoordinate(9,9));
			test7.insertNode(i*10+6,new GPSCoordinate(10,10));
			test7.insertNode(i*10+7,new GPSCoordinate(11,11));
			test7.insertNode(i*10+8,new GPSCoordinate(12,12));
		}
		
		
		int i = 0;
		weg(test7,77,1,i++,100);
		weg(test7,77,2,i++,100);
		weg(test7,77,3,i++,100);
		weg(test7,77,4,i++,100);
		
		for(int j = 1; j<=4 ; j++){
			weg(test7,j,j*10+4,i++,10);
			weg(test7,j,j*10+8,i++,10);
			
			weg(test7,j*10+1,j*10+2,i++,1);
			weg(test7,j*10+1,j*10+3,i++,1);
			weg(test7,j*10+4,j*10+3,i++,1);
			weg(test7,j*10+4,j*10+2,i++,1);
			
			weg(test7,j*10+5,j*10+6,i++,1);
			weg(test7,j*10+5,j*10+7,i++,1);
			weg(test7,j*10+8,j*10+7,i++,1);
			weg(test7,j*10+8,j*10+6,i++,1);
		}
		
		test7.exportToHierachyGraph(graph7);
		iter = graph7.getNodeIt();
		
		while(iter.hasNext()){
			HierarchyMapNode node = iter.next();
			Config.H = 3;
			long distance = Dijkstra.neighbourhood(graph7, node.getUID(),(byte) 0, true);
			node.setdH(distance, (byte) 0, true);

			distance = Dijkstra.neighbourhood(graph7, node.getUID(),(byte) 0, false);
			node.setdH(distance, (byte) 0, false);
		}
		
		for (int node=1; node <= 4; node++){
			graph7.getNode(node).getEdgeToNeighbour(node*10+4).setLevel((byte)1);
			graph7.getNode(node*10+4).getEdgeToNeighbour(node).setLevel((byte)1);
			
			graph7.getNode(node).getEdgeToNeighbour(node*10+8).setLevel((byte)1);
			graph7.getNode(node*10+8).getEdgeToNeighbour(node).setLevel((byte)1);
			
			graph7.getNode(node).getEdgeToNeighbour(77).setLevel((byte)1);
			graph7.getNode(77).getEdgeToNeighbour(node).setLevel((byte)1);
		}
		

		/*
		 * Test8
		 * Baue HGraphen, berechne Hierarchy 0 (mittels HHierarchyMT.computeHierarchy)
		 * vergleiche Dijkstra und Query Ergebnis
		 */
		
		test8 = new MapGraph(graphKoord1,graphKoord2);
		graph8 = new HierarchyMapGraph();
		
		for(int j=1;j<9;j++) {
			test8.insertNode(j, new GPSCoordinate(j,j));
		}
		weg(test8,1,2,1,1);
		weg(test8,1,3,2,1);
		weg(test8,1,4,3,5);
		weg(test8,2,3,4,2);
		weg(test8,2,4,5,3);
		weg(test8,3,5,6,6);
		weg(test8,4,6,7,1);
		weg(test8,4,5,8,1);
		weg(test8,4,7,9,4);
		weg(test8,6,7,10,2);
		weg(test8,5,7,11,3);
		weg(test8,5,8,12,5);
		weg(test8,7,8,13,2);
		
		Config.H = 2;
		test8.exportToHierachyGraph(graph8);
		HHierarchyMT.computeHierarchy(graph8, (byte)1);

		/*
		 * Test 9
		 * 
		 */
		
		test9 = new MapGraph(graphKoord1,graphKoord2);
		graph9 = new HierarchyMapGraph();
		
		test9.insertNode(1, new GPSCoordinate(1,1));
		test9.insertNode(2, new GPSCoordinate(2,2));
		test9.insertNode(3, new GPSCoordinate(3,3));
		test9.insertNode(4, new GPSCoordinate(4,4));
		test9.insertNode(5, new GPSCoordinate(5,5));
		test9.insertNode(6, new GPSCoordinate(6,6));
		test9.insertNode(7, new GPSCoordinate(7,7));
		test9.insertNode(8, new GPSCoordinate(8,8));
		test9.insertNode(9, new GPSCoordinate(9,9));
		test9.insertNode(12, new GPSCoordinate(10,10));
		test9.insertNode(13, new GPSCoordinate(11,11));
		test9.insertNode(14, new GPSCoordinate(12,12));
		test9.insertNode(15, new GPSCoordinate(13,13));
		test9.insertNode(16, new GPSCoordinate(14,14));
		test9.insertNode(17, new GPSCoordinate(15,15));
		test9.insertNode(18, new GPSCoordinate(16,16));
		test9.insertNode(19, new GPSCoordinate(17,17));
		test9.insertNode(77, new GPSCoordinate(18,18));
		
		i=1;
		weg(test9,1,2,i++,1);
		weg(test9,1,3,i++,10);
		weg(test9,2,4,i++,1);
		weg(test9,2,5,i++,1);
		weg(test9,2,6,i++,1);
		weg(test9,6,7,i++,1);
		weg(test9,6,8,i++,1);
		weg(test9,6,9,i++,2);
		weg(test9,77,13,i++,10);
		weg(test9,77,12,i++,1);
		weg(test9,12,14,i++,1);
		weg(test9,12,15,i++,1);
		weg(test9,12,16,i++,1);
		weg(test9,16,17,i++,1);
		weg(test9,16,18,i++,1);
		weg(test9,16,19,i++,2);
		weg(test9,3,13,i++,10);
		weg(test9,9,19,i++,100);
		
		test9.exportToHierachyGraph(graph9);
		
		Config.H = 5;
		HHierarchyMT.computeHierarchy(graph9, (byte)1);

	}	 
		
		
	/**
	 * Test method for {@link algorithmen.Query#computeHierarchyPath(graphenbib.HierarchyMapGraph, int, int)}.
	 */

	@Test
	public void testComputePath() throws Exception {
		
		Path liste = null;
		
		/*
		 * Test 0a) 
		 * Teste auf einem leeren Graphen - erwarte eine leere Liste
		 */
		
		
		try {
			liste = Query.computeHierarchyPath(new HierarchyMapGraph(), 1, 77);
		} catch (Exception e) {
			fail("Falls der Graph leer ist, wurde keine Exception geworfen");
		}
		
		
		try {
			liste = Query.computeHierarchyPath(graph1, 1, 8);
		} catch (Exception e) {
			fail("Bei nicht-existierendem Endknoten wurde eine Exception geworfen, statt einen leeren Path auszugeben: "+e.getLocalizedMessage());
		}
		if (liste.getPathNodeIDs().size()!=0)
			fail("Die Ausgabe ohne existierenden Endknoten sollte leer sein. Stattdessen gibt es so viele Elemente: "+liste.getPathNodeIDs().size());
			
		/*
		 * Test 0b)
		 * Teste mit unerreichbaren End-Knoten - erwarte leere Liste
		 */
		
		test1.insertNode(8, new GPSCoordinate(0,0));
		graph1.insertNode(8, 0, 0);
		
		try {
			liste = Query.computeHierarchyPath(graph1, 77, 8);
		} catch (Exception e) {
			fail("Bei nicht-erreichbaren Endknoten wurde eine Exception geworfen, statt einer leeren Liste auszugeben: "+e.getLocalizedMessage());
		}
		if (liste.getPathNodeIDs().size() != 0)
			fail("Die Ausgabe sollte leer sein. Stattdessen gibt es so viele Elemente: "+liste.getPathNodeIDs().size());
			
		
		/*
		 * Test 0c)
		 * Teste mit gleichen start-/End-Knoten - erwarte leere Liste
		 */
		
		try {
			liste = Query.computeHierarchyPath(graph1, 77, 77);
		} catch (Exception e) {
			fail("Bei gueltigen Endknoten wurde eine Exception geworfen, statt einer leeren Liste auszugeben: "+e.getLocalizedMessage());
		}
		
		if (liste.getPathNodeIDs().size() != 0)
			fail("Die Ausgabe auf start=Ende sollte leer sein. Stattdessen gibt es so viele Elemente: "+liste.getPathNodeIDs().size());
			
		
		/*
		 * Test 1
		 * keine Nachbarschaften definiert
		 * erwarte Abbruch
		 */
				
		try {
			liste = Query.computeHierarchyPath(graph1, 1, 77);
		} catch (Exception e) {
			fail("Bei gueltigen Endknoten wurde eine Exception geworfen, statt einem Pfad auszugeben: "+e.getLocalizedMessage());
		}
		if (liste.getPathNodeIDs().size() != 0)
			fail("Die Ausgabe auf sollte leer sein. Stattdessen gibt wird er folgende Pfad berechnet: "+Pfad(liste.getPathNodeIDs()));
		logger.log(sender,"Test1 es konnte wie erwartet (fehlende Nachbarschaften) kein Weg berechnet werden.");
		
		/*
		 * Test 2
		 * hier sollte Weg existieren
		 */
		
		//try {
			liste = Query.computeHierarchyPath(graph2, 1, 77);
		//} catch (Exception e) {
		//	fail("Bei gueltigen Endknoten wurde eine Exception geworfen, statt einer leeren Liste auszugeben: "+e.getLocalizedMessage());
		//}
		if (liste.getPathNodeIDs().size() != 5)
			fail("Die Ausgabe sollte den folgenden Weg ergeben: 1-2-3-4-77. Stattdessen wurde der folgende Pfad berechnet: "+Pfad(liste.getPathNodeIDs()));
		logger.log(sender,"Test2 Weg: "+Pfad(liste.getPathNodeIDs()));
		
		/*
		 * Test 3
		 * Langer HierarchyPfad ohne Hierarchieberechnung
		 * erwarte auch hier Fehler
		 */
				
		try {
			liste = Query.computeHierarchyPath(graph3, 1, 77);
		} catch (Exception e) {
			fail("Bei gueltigen Endknoten wurde eine Exception geworfen, statt einer leeren Liste auszugeben: "+e.getLocalizedMessage());
		}
		if (liste.getPathNodeIDs().size() != 0)
			fail("Die Ausgabe auf start=Ende sollte leer sein. Stattdessen gibt es so viele Elemente: "+liste.getPathNodeIDs().size());
		logger.log(sender,"Test3 es konnte wie erwartet (fehlende Hierarchien) kein Weg berechnet werden.");
		
		/*
		 * Test 4
		 * hier sollte Weg existieren
		 */

		
		try {
			liste = Query.computeHierarchyPath(graph4, 1, 77);
		} catch (Exception e) {
			fail("Bei gueltigen Endknoten wurde eine Exception geworfen, statt einer leeren Liste auszugeben: "+e.getLocalizedMessage());
		}
		if (liste.getPathNodeIDs().size() != 6)
			fail("Die Ausgabe sollte den folgenden Weg ergeben: 1-2-3-4-5-77. Stattdessen wurde der folgende Pfad berechnet: "+Pfad(liste.getPathNodeIDs()));
		logger.log(sender,"Test4 Weg: "+Pfad(liste.getPathNodeIDs()));
		
		/*
		 * Test 5
		 * hier sollte Weg existieren
		 */
		
		try {
			liste = Query.computeHierarchyPath(graph5, 1, 77);
		} catch (Exception e) {
			fail("Bei gueltigen Endknoten wurde eine Exception geworfen, statt einer leeren Liste auszugeben: "+e.getLocalizedMessage());
		}
		if (liste.getPathNodeIDs().size() != 7)
			fail("Die Ausgabe auf sollte den folgenden Pfad ergeben: 1-2-3-4-5-6-77. Stattdessen wurde der folgenden Pfad berechnen: "+Pfad(liste.getPathNodeIDs()));
		logger.log(sender,"Test5 Weg: "+Pfad(liste.getPathNodeIDs()));
	
		/*
		 * Test 6
		 * hier sollte Weg existieren
		 */
		
		try {
			liste = Query.computeHierarchyPath(graph6, 1, 77);
		} catch (Exception e) {
			fail("Bei gueltigen Endknoten wurde eine Exception geworfen, statt einer leeren Liste auszugeben: "+e.getLocalizedMessage());
		}
		if (liste.getPathNodeIDs().size() != 5)
			fail("Die Ausgabe auf sollte den folgenden Pfad ergeben: 1-2-4-6-77. Stattdessen wurde der folgenden Pfad berechnen: "+Pfad(liste.getPathNodeIDs()));
		logger.log(sender,"Test6 Weg: "+Pfad(liste.getPathNodeIDs()));
		
		/*
		 * Test 7 (Test lauft noch nicht weil Graph initalisierung noch fehlt)
		 * hier sollte Weg existieren
		 */
		

		try {
			liste = Query.computeHierarchyPath(graph7, 12, 35);
		} catch (Exception e) {
			fail("Bei gueltigen Endknoten wurde eine Exception geworfen, statt einer leeren Liste auszugeben: "+e.getLocalizedMessage());
		}
		if (liste.getPathNodeIDs().size() != 8)
			fail("Die Ausgabe auf sollte den folgenden Pfad ergeben: 12-14-1-77-3-38-36-35. Stattdessen wurde der folgenden Pfad berechnen: "+Pfad(liste.getPathNodeIDs()));
		logger.log(sender,"Test7 Weg: "+Pfad(liste.getPathNodeIDs()));
		
		
				
		/*
		 * Test 8
		 * 1. Vergleich zwischen Dijkstra und Query
		 */
		
		Path list = null;
		//Teste 1->8

		list = Dijkstra.bidirectional(graph8, 1, 8);
		liste = Query.computeHierarchyPath(graph8, 1, 8);
		
		if (list.size() != liste.getPathNodeIDs().size())
			fail("Dijkstra ergibt den folgenden Pfad: "+Pfad(list.getPathNodeIDs())+". Stattdessen berechnet die Query den folgenden Pfad: "+Pfad(liste.getPathNodeIDs()));
		//teste Reihenfolge
		String test = Pfad(liste.getPathNodeIDs());
		if (!test.equalsIgnoreCase(Pfad(list.getPathNodeIDs())))
			fail("Die Reihenfolge der besuchten Knoten ist falsch. Erwarte: "+Pfad(list.getPathNodeIDs())+", habe: "+test);
		logger.log(sender,"Test8.1 Weg: "+Pfad(liste.getPathNodeIDs()));
		
		//Teste 8->1
		list = Dijkstra.bidirectional(graph8, 8, 1);
		liste = Query.computeHierarchyPath(graph8, 8, 1);
		if (list.size() != liste.getPathNodeIDs().size())
			fail("Dijkstra ergibt den folgenden Pfad: "+Pfad(list.getPathNodeIDs())+". Stattdessen berechnet die Query den folgenden Pfad: "+Pfad(liste.getPathNodeIDs()));
		//teste Reihenfolge
		test = Pfad(liste.getPathNodeIDs());
		if (!test.equalsIgnoreCase(Pfad(list.getPathNodeIDs())))
			fail("Die Reihenfolge der besuchten Knoten ist falsch. Erwarte: "+Pfad(list.getPathNodeIDs())+", habe: "+test);
		logger.log(sender,"Test8.2 Weg: "+Pfad(liste.getPathNodeIDs()));		
		
		//Test nun 1->5
		list = Dijkstra.bidirectional(graph8, 1, 5);
		liste = Query.computeHierarchyPath(graph8, 1, 5);
		if (list.size() != liste.getPathNodeIDs().size())
			fail("Dijkstra ergibt den folgenden Pfad: "+Pfad(list.getPathNodeIDs())+". Stattdessen berechnet die Query den folgenden Pfad: "+Pfad(liste.getPathNodeIDs()));
		//teste Reihenfolge
		test = Pfad(liste.getPathNodeIDs());
		if (!test.equalsIgnoreCase(Pfad(list.getPathNodeIDs())))
			fail("Die Reihenfolge der besuchten Knoten ist falsch. Erwarte: "+Pfad(list.getPathNodeIDs())+", habe: "+test);
		logger.log(sender,"Test8.3 Weg: "+Pfad(liste.getPathNodeIDs()));
		
		//Teste nun 3->4
		list = Dijkstra.bidirectional(graph8, 3, 4);
		liste = Query.computeHierarchyPath(graph8, 3, 4);
		if (list.size() != liste.getPathNodeIDs().size())
			fail("Dijkstra ergibt den folgenden Pfad: "+Pfad(list.getPathNodeIDs())+". Stattdessen berechnet die Query den folgenden Pfad: "+Pfad(liste.getPathNodeIDs()));
		//teste Reihenfolge
		test = Pfad(liste.getPathNodeIDs());
		if (!test.equalsIgnoreCase(Pfad(list.getPathNodeIDs())))
			fail("Die Reihenfolge der besuchten Knoten ist falsch. Erwarte: "+Pfad(list.getPathNodeIDs())+", habe: "+test);
		logger.log(sender,"Test8.4 Weg: "+Pfad(liste.getPathNodeIDs()));
		
		//Teste 7->4
		list = Dijkstra.bidirectional(graph8, 7, 4);
		liste = Query.computeHierarchyPath(graph8, 7, 4);
		if (list.size() != liste.getPathNodeIDs().size())
			fail("Dijkstra ergibt den folgenden Pfad: "+Pfad(list.getPathNodeIDs())+". Stattdessen berechnet die Query den folgenden Pfad: "+Pfad(liste.getPathNodeIDs()));
		//teste Reihenfolge
		test = Pfad(liste.getPathNodeIDs());
		if (!test.equalsIgnoreCase(Pfad(list.getPathNodeIDs())))
			fail("Die Reihenfolge der besuchten Knoten ist falsch. Erwarte: "+Pfad(list.getPathNodeIDs())+", habe: "+test);
		logger.log(sender,"Test8.5 Weg: "+Pfad(liste.getPathNodeIDs()));
		
		/*
		 * Test 9
		 * Test ab der richtige Ausgang aus einer Nachbarschaft gefunden wird.
		 * Verlassen der Nachbarschaft zum erstmueglichen Zeitpunkt liefert hier falsches Ergebniss
		 */
		
		//Teste 1->77
		list = Dijkstra.bidirectional(graph9, 1, 77);
		liste = Query.computeHierarchyPath(graph9, 1, 77);
		if (list.size() != liste.getPathNodeIDs().size())
			fail("Dijkstra ergibt den folgenden Pfad: "+Pfad(list.getPathNodeIDs())+". Stattdessen berechnet die Query den folgenden Pfad: "+Pfad(liste.getPathNodeIDs()));
		//teste Reihenfolge
		test = Pfad(liste.getPathNodeIDs());
		if (!test.equalsIgnoreCase(Pfad(list.getPathNodeIDs())))
			fail("Die Reihenfolge der besuchten Knoten ist falsch. Erwarte: "+Pfad(list.getPathNodeIDs())+", habe: "+test);
		logger.log(sender,"Test9 Weg: "+Pfad(liste.getPathNodeIDs()));
		
		//--------- Neue Testreiehe fuer details im Zusammenhang mit der Path und reconstruct ----------
		
		/*
		 * Hier soll ein Graph erstellt werden, der die Route von 2->4 beinhaltet, aber genau diese beiden Knoten kontrahiert werden.
		 *  2 1 1 2  (Laengen)
		 * x-o-x-o-x
		 * 1 2 3 4 5  (Knoten)
		 * 
		 * wobei x kreuzungen, und o kontrahierende Knten
		 * Route 2->4 existiert mit Laenge 2
		 */
		
		MapGraph graph = new MapGraph(90,90,0,0);
		//Knoten
		graph.insertNodeWithoutGPS(1);
		graph.insertNodeWithoutGPS(2);
		graph.insertNodeWithoutGPS(3);
		graph.insertNodeWithoutGPS(4);
		graph.insertNodeWithoutGPS(5);
		
		graph.insertNodeWithoutGPS(10);
		graph.insertNodeWithoutGPS(11);
		graph.insertNodeWithoutGPS(30);
		graph.insertNodeWithoutGPS(31);
		graph.insertNodeWithoutGPS(50);
		
		graph.insertNodeWithoutGPS(51);
		
		//Wege
		int w = 100;
		graph.insertEdge(1, 2, w++, 2, StreetType.PRIMARY);
		graph.insertEdge(2, 3, w++, 1, StreetType.PRIMARY);
		graph.insertEdge(3, 4, w++, 1, StreetType.PRIMARY);
		graph.insertEdge(4, 5, w++, 2, StreetType.PRIMARY);
		
		graph.insertEdge(2, 1, w++, 2, StreetType.PRIMARY);
		graph.insertEdge(3, 2, w++, 1, StreetType.PRIMARY);
		graph.insertEdge(4, 3, w++, 1, StreetType.PRIMARY);
		graph.insertEdge(5, 4, w++, 2, StreetType.PRIMARY);
		
		//Kreuzungen bauen
		graph.insertEdge(1, 10, w++, 20, StreetType.LIVING_STREET);
		graph.insertEdge(1, 11, w++, 20, StreetType.LIVING_STREET);
		graph.insertEdge(10, 1, w++, 20, StreetType.LIVING_STREET);
		graph.insertEdge(11, 1, w++, 20, StreetType.LIVING_STREET);
		graph.insertEdge(3, 30, w++, 20, StreetType.LIVING_STREET);
		
		graph.insertEdge(3, 31, w++, 20, StreetType.LIVING_STREET);
		graph.insertEdge(30, 3, w++, 20, StreetType.LIVING_STREET);
		graph.insertEdge(31, 3, w++, 20, StreetType.LIVING_STREET);
		graph.insertEdge(5, 50, w++, 20, StreetType.LIVING_STREET);
		graph.insertEdge(5, 51, w++, 20, StreetType.LIVING_STREET);
		
		graph.insertEdge(50, 5, w++, 20, StreetType.LIVING_STREET);
		graph.insertEdge(51, 5, w++, 20, StreetType.LIVING_STREET);
		
		//Nun bauen wir uns einen HGraph
		
		HierarchyMapGraph hGraph = new HierarchyMapGraph();
		hGraph = graph.exportToHierachyGraph(hGraph);
		//Berechne Hierarchien - das ist nahe an der Praxis und kontrahiert wi im echten leben
		HHierarchyMT.buildHierarchyGraph(hGraph);
		
		//NUn zur Query
		Path weg = new Path();
		
		try {
			weg = Query.computeShortestPath(2, 4, hGraph);
		} catch (Exception e) {
			e.printStackTrace();
			fail("Auf kontrahierten hat die Query ne Exception geworfen (Route sollte existieren)");
		}
		
		boolean fehlersammler = false;
		
		if (weg.getStartNodeID() != 2) {
			logger.log(sender,"Fehler: der Path verwirft den eigentlichen Startknoten und ersetzt ihn. Soll:2, Ist:"+weg.getStartNodeID());
			fehlersammler = true;
		}
		
		if (weg.getEndNodeID() != 4) {
			logger.log(sender,"Fehler: der Path verwirft den eigentlichen Endknoten und ersetzt ihn. Soll:2, Ist:"+weg.getStartNodeID());
			fehlersammler = true;
		}
		
		//Pruefe nun die Laenge der Route
		if (weg.getPathLength() != 2) {
			logger.log(sender,"Der Weg auf den kontrahierten Knoten lieferte nciht die korrekte Laenge. Soll:2, Ist:"+weg.getPathLength());
			fehlersammler=true;
		}
		
		ArrayList<Integer> nodes = new ArrayList<Integer>();
		weg.reconstructPath(hGraph);
		nodes = weg.getPathNodeIDs();
		
		if (nodes == null)
			fail("Path.getReconstructedPath gibt einen Nullpointer zurueck. Dies darf NIE passieren. Entweder Inhalt oder leere Liste!");
		
		if (nodes.size() != 3) {
			logger.log(sender, "Der rekonstruierte Pfad hat die falsche Laenge. Soll:3, Ist:"+nodes.size());
			fehlersammler = true;
		}
		
		if (fehlersammler)
			fail("Es gab schwere Fehler (weg)");
		
		/*
		 * Naechste Testroutine
		 *  1 2 3 3 2 1  (Laengen)
		 * x-o-o-x-o-o-x
		 * 1 2 3 4 5 6 7  (Knoten)
		 * 
		 * Route 2->3 Laenge 2
		 * Route 2->5 Laenge 8
		 * Route 2->6 Laenge 10
		 * Route 3->6 Laenge 8
		 */
		
		graph = new MapGraph(90,90,0,0);
		//Knoten
		graph.insertNodeWithoutGPS(1);
		graph.insertNodeWithoutGPS(2);
		graph.insertNodeWithoutGPS(3);
		graph.insertNodeWithoutGPS(4);
		graph.insertNodeWithoutGPS(5);
		
		graph.insertNodeWithoutGPS(6);
		graph.insertNodeWithoutGPS(7);
		
		graph.insertNodeWithoutGPS(10);
		graph.insertNodeWithoutGPS(11);
		graph.insertNodeWithoutGPS(40);
		graph.insertNodeWithoutGPS(41);
		graph.insertNodeWithoutGPS(70);
		
		graph.insertNodeWithoutGPS(71);
		
		//Wege
		w = 100;
		graph.insertEdge(1, 2, w++, 1, StreetType.PRIMARY);
		graph.insertEdge(2, 3, w++, 2, StreetType.PRIMARY);
		graph.insertEdge(3, 4, w++, 3, StreetType.PRIMARY);
		graph.insertEdge(4, 5, w++, 3, StreetType.PRIMARY);
		graph.insertEdge(5, 6, w++, 2, StreetType.PRIMARY);
		graph.insertEdge(6, 7, w++, 1, StreetType.PRIMARY);
		
		graph.insertEdge(2, 1, w++, 1, StreetType.PRIMARY);
		graph.insertEdge(3, 2, w++, 2, StreetType.PRIMARY);
		graph.insertEdge(4, 3, w++, 3, StreetType.PRIMARY);
		graph.insertEdge(5, 4, w++, 3, StreetType.PRIMARY);
		graph.insertEdge(6, 5, w++, 2, StreetType.PRIMARY);
		graph.insertEdge(7, 6, w++, 1, StreetType.PRIMARY);
		
		//Kreuzungen bauen
		graph.insertEdge(1, 10, w++, 20, StreetType.LIVING_STREET);
		graph.insertEdge(1, 11, w++, 20, StreetType.LIVING_STREET);
		graph.insertEdge(10, 1, w++, 20, StreetType.LIVING_STREET);
		graph.insertEdge(11, 1, w++, 20, StreetType.LIVING_STREET);
		graph.insertEdge(4, 40, w++, 20, StreetType.LIVING_STREET);
		
		graph.insertEdge(4, 41, w++, 20, StreetType.LIVING_STREET);
		graph.insertEdge(40, 4, w++, 20, StreetType.LIVING_STREET);
		graph.insertEdge(41, 4, w++, 20, StreetType.LIVING_STREET);
		graph.insertEdge(7, 70, w++, 20, StreetType.LIVING_STREET);
		graph.insertEdge(7, 71, w++, 20, StreetType.LIVING_STREET);
		
		graph.insertEdge(70, 7, w++, 20, StreetType.LIVING_STREET);
		graph.insertEdge(71, 7, w++, 20, StreetType.LIVING_STREET);
		
		
		//Nun bauen wir uns einen HGraph

		hGraph = new HierarchyMapGraph();
		hGraph = graph.exportToHierachyGraph(hGraph);
		//Berechne Hierarchien - das ist nahe an der Praxis und kontrahiert wi im echten leben
		HHierarchyMT.buildHierarchyGraph(hGraph);
		
		//NUn zur Query
		Path weg1 = new Path();
		Path weg2 = new Path();
		Path weg3 = new Path();
		Path weg4 = new Path();
		
		weg1 = Query.computeShortestPath(2, 3, hGraph);
		weg2 = Query.computeShortestPath(2, 5, hGraph);
		weg3 = Query.computeShortestPath(2, 6, hGraph);
		weg4 = Query.computeShortestPath(3, 6, hGraph);
		
		//Starte mit weg1
		
		if (weg1.getStartNodeID() != 2) {
			logger.log(sender,"Fehler: der Path verwirft den eigentlichen Startknoten und ersetzt ihn. Soll:2, Ist:"+weg1.getStartNodeID());
			fehlersammler = true;
		}
		
		if (weg1.getEndNodeID() != 3) {
			logger.log(sender,"Fehler: der Path verwirft den eigentlichen Endknoten und ersetzt ihn. Soll:3, Ist:"+weg1.getStartNodeID());
			fehlersammler = true;
		}
		
		if (weg1.getPathLength() != 2) {
			logger.log(sender,"Der Weg auf den kontrahierten Knoten lieferte nciht die korrekte Laenge. Soll:2, Ist:"+weg1.getPathLength());
			fehlersammler=true;
		}
		
		nodes = new ArrayList<Integer>();
		
		weg1.reconstructPath(hGraph);
		nodes=weg1.getPathNodeIDs();
		if (nodes == null)
			fail("Path.getReconstructedPath gibt einen Nullpointer zurueck. Dies darf NIE passieren. Entweder Inhalt oder leere Liste!");
		
		if (nodes.size() != 2) {
			logger.log(sender, "Der rekonstruierte Pfad hat die falsche Laenge. Soll:2, Ist:"+nodes.size());
			fehlersammler = true;
		}
		
		if (fehlersammler)
			fail("Es gab schwere Fehler (weg1)");
		
		//Weg2
		
		if (weg2.getStartNodeID() != 2) {
			logger.log(sender,"Fehler: der Path verwirft den eigentlichen Startknoten und ersetzt ihn. Soll:2, Ist:"+weg2.getStartNodeID());
			fehlersammler = true;
		}
		
		if (weg2.getEndNodeID() != 5) {
			logger.log(sender,"Fehler: der Path verwirft den eigentlichen Endknoten und ersetzt ihn. Soll:5, Ist:"+weg2.getStartNodeID());
			fehlersammler = true;
		}
		
		if (weg2.getPathLength() != 8) {
			logger.log(sender,"Der Weg auf den kontrahierten Knoten lieferte nciht die korrekte Laenge. Soll:8, Ist:"+weg2.getPathLength());
			fehlersammler=true;
		}
		
		nodes = new ArrayList<Integer>();
		
		weg2.reconstructPath(hGraph);
		nodes=weg2.getPathNodeIDs();
		
		if (nodes == null)
			fail("Path.getReconstructedPath gibt einen Nullpointer zurueck. Dies darf NIE passieren. Entweder Inhalt oder leere Liste!");
		
		if (nodes.size() != 4) {
			logger.log(sender, "Der rekonstruierte Pfad hat die falsche Laenge. Soll:4, Ist:"+nodes.size());
			fehlersammler = true;
		}
		
		if (fehlersammler)
			fail("Es gab schwere Fehler (weg2)");
		
		
		//weg 3
		
		if (weg3.getStartNodeID() != 2) {
			logger.log(sender,"Fehler: der Path verwirft den eigentlichen Startknoten und ersetzt ihn. Soll:2, Ist:"+weg3.getStartNodeID());
			fehlersammler = true;
		}
		
		if (weg3.getEndNodeID() != 6) {
			logger.log(sender,"Fehler: der Path verwirft den eigentlichen Endknoten und ersetzt ihn. Soll:6, Ist:"+weg3.getStartNodeID());
			fehlersammler = true;
		}
		
		if (weg3.getPathLength() != 10) {
			logger.log(sender,"Der Weg auf den kontrahierten Knoten lieferte nciht die korrekte Laenge. Soll:10, Ist:"+weg3.getPathLength());
			fehlersammler=true;
		}
		
		nodes = new ArrayList<Integer>();
		
		weg3.reconstructPath(hGraph);
		nodes=weg3.getPathNodeIDs();
		
		if (nodes == null)
			fail("Path.getReconstructedPath gibt einen Nullpointer zurueck. Dies darf NIE passieren. Entweder Inhalt oder leere Liste!");
		
		if (nodes.size() != 5) {
			logger.log(sender, "Der rekonstruierte Pfad hat die falsche Laenge. Soll:5, Ist:"+nodes.size());
			fehlersammler = true;
		}
		
		if (fehlersammler)
			fail("Es gab schwere Fehler (weg3)");
		
		
		// weg 4
		
		if (weg4.getStartNodeID() != 3) {
			logger.log(sender,"Fehler: der Path verwirft den eigentlichen Startknoten und ersetzt ihn. Soll:3, Ist:"+weg4.getStartNodeID());
			fehlersammler = true;
		}
		
		if (weg4.getEndNodeID() != 6) {
			logger.log(sender,"Fehler: der Path verwirft den eigentlichen Endknoten und ersetzt ihn. Soll:6, Ist:"+weg4.getStartNodeID());
			fehlersammler = true;
		}
		
		if (weg4.getPathLength() != 8) {
			logger.log(sender,"Der Weg auf den kontrahierten Knoten lieferte nciht die korrekte Laenge. Soll:8, Ist:"+weg4.getPathLength());
			fehlersammler=true;
		}
		
		nodes = new ArrayList<Integer>();
		
		weg4.reconstructPath(hGraph);
		nodes=weg4.getPathNodeIDs();
		
		if (nodes == null)
			fail("Path.getReconstructedPath gibt einen Nullpointer zurueck. Dies darf NIE passieren. Entweder Inhalt oder leere Liste!");
		
		if (nodes.size() != 4) {
			logger.log(sender, "Der rekonstruierte Pfad hat die falsche Laenge. Soll:4, Ist:"+nodes.size());
			fehlersammler = true;
		}
		
		if (fehlersammler)
			fail("Es gab schwere Fehler (weg4)");
		
		

		/*
		 * 
		 *  2 1  2  (Laengen)
		 * x-o-x o-x
		 * 1 2 3 4 5  (Knoten)
		 * 
		 * wobei x kreuzungen, und o kontrahierende Knten
		 * Route 2->4 existiert nicht
		 */
		
		graph = new MapGraph(90,90,0,0);
		//Knoten
		graph.insertNodeWithoutGPS(1);
		graph.insertNodeWithoutGPS(2);
		graph.insertNodeWithoutGPS(3);
		graph.insertNodeWithoutGPS(4);
		graph.insertNodeWithoutGPS(5);
		
		graph.insertNodeWithoutGPS(10);
		graph.insertNodeWithoutGPS(11);
		graph.insertNodeWithoutGPS(30);
		graph.insertNodeWithoutGPS(31);
		graph.insertNodeWithoutGPS(50);
		
		graph.insertNodeWithoutGPS(51);
		
		//Wege
		w = 100;
		graph.insertEdge(1, 2, w++, 2, StreetType.PRIMARY);
		graph.insertEdge(2, 3, w++, 1, StreetType.PRIMARY);
		//graph.insertEdge(3, 4, w++, 1, StreetType.PRIMARY);
		graph.insertEdge(4, 5, w++, 2, StreetType.PRIMARY);
		
		graph.insertEdge(2, 1, w++, 2, StreetType.PRIMARY);
		graph.insertEdge(3, 2, w++, 1, StreetType.PRIMARY);
		//graph.insertEdge(4, 3, w++, 1, StreetType.PRIMARY);
		graph.insertEdge(5, 4, w++, 2, StreetType.PRIMARY);
		
		//Kreuzungen bauen
		graph.insertEdge(1, 10, w++, 20, StreetType.LIVING_STREET);
		graph.insertEdge(1, 11, w++, 20, StreetType.LIVING_STREET);
		graph.insertEdge(10, 1, w++, 20, StreetType.LIVING_STREET);
		graph.insertEdge(11, 1, w++, 20, StreetType.LIVING_STREET);
		graph.insertEdge(3, 30, w++, 20, StreetType.LIVING_STREET);
		
		graph.insertEdge(3, 31, w++, 20, StreetType.LIVING_STREET);
		graph.insertEdge(30, 3, w++, 20, StreetType.LIVING_STREET);
		graph.insertEdge(31, 3, w++, 20, StreetType.LIVING_STREET);
		graph.insertEdge(5, 50, w++, 20, StreetType.LIVING_STREET);
		graph.insertEdge(5, 51, w++, 20, StreetType.LIVING_STREET);
		
		graph.insertEdge(50, 5, w++, 20, StreetType.LIVING_STREET);
		graph.insertEdge(51, 5, w++, 20, StreetType.LIVING_STREET);
		
		//Nun bauen wir uns einen HGraph
		
		hGraph = new HierarchyMapGraph();
		hGraph = graph.exportToHierachyGraph(hGraph);
		//Berechne Hierarchien - das ist nahe an der Praxis und kontrahiert wi im echten leben
		HHierarchyMT.buildHierarchyGraph(hGraph);
		
		//NUn zur Query
		weg = new Path();
		
		try {
			weg = Query.computeShortestPath(2, 4, hGraph);
		} catch (Exception e) {
			e.printStackTrace();
			fail("Auf kontrahierten hat die Query ne Exception geworfen (Route sollte nciht existieren)");
		}
		
		fehlersammler = false;
		
		try {
			if (weg.getPathLength() != -1) {
				logger.log(sender, "Es wurde auf einem nciht-existierenden Pfad eine Laenge ausgegeben, die nciht der verinbarten -1 entspricht");
				fehlersammler = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
			fail("Es wurde auf einem leeren Weg eine Exception geworfen. Dies soll nur bei schweren Fehlern passieren, die den Programmablauf verhindert.");
		}
		
		try {
			if (weg.getPathTime() != -1) {
				logger.log(sender, "Es wurde auf einem nciht-existierenden Pfad eine zeit ausgegeben, die nciht der verinbarten -1 entspricht");
				fehlersammler = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
			fail("Es wurde auf einem leeren Weg eine Exception geworfen. Dies soll nur bei schweren Fehlern passieren, die den Programmablauf verhindert.");
		}
		
		try {
			if (weg.size() != 0) {
				logger.log(sender, "Es wurde auf einem nciht-existierenden Pfad eine Groesse ausgegeben, die nciht der verinbarten 0 entspricht, sondern: "+ weg.size());
				fehlersammler = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
			fail("Es wurde auf einem leeren Weg eine Exception geworfen. Dies soll nur bei schweren Fehlern passieren, die den Programmablauf verhindert.");
		}
		
		if (fehlersammler)
			fail("Es gab schwere Fehler");
		
		logger.log(sender,"Alle Tests bis hier laufen durch");
		
	}
}
