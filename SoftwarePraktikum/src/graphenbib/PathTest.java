package graphenbib;

import static org.junit.Assert.fail;
import graphexceptions.EmptyInputException;
import graphexceptions.InvalidGPSCoordinateException;
import graphexceptions.InvalidInputException;
import graphexceptions.NodeNotInGraphException;
import graphexceptions.NodeNotNeighbourOfPreviousElementInPathException;

import java.util.ArrayList;

import org.junit.Test;

import algorithmen.HHierarchyMT;


public class PathTest
{
	

	/**
	 * Das ist ein kleiner Test, der zumindest auf einer kleinen Flaeche testen soll, ob mein Rekonstruieralgorithmus
	 * sinnvolles tut.
	 * Der konstruierte Graph ist ein zweistoeckiges Haus vom Nikolaus, wobei die Knoten da wie folgt angeordnet sind:
	 *     4
	 * 
	 * 3      5
	 * 
	 * 2      6
	 * 
	 * 1      7
	 * Auf Level 1 habe ich dann eine Kante eingefuegt, die 1 und 3 direkt verbindet, und noch eine Kante, Knoten
	 * 3 und 4 verbindet
	 * Auf Level 2 ist eine Kante, die 1 und 4 verbindet
	 * Nun soll der Weg von 1 nach 4 rekonstruiert werden, der dann klarerweise im Mapgraphen ueber 2 und 3 fuehrt.
	 */
	@Test
	public void testGetPathInOriginalGraphViaNodes()
	{
		MapGraph graph = null;
		try
        {
	        graph = new MapGraph(90, 90, 0, 0);
	        graph.insertNode(1, new GPSCoordinate(10,10));
	        graph.insertNode(2, new GPSCoordinate(10,25));
	        graph.insertNode(3, new GPSCoordinate(10,40));
	        graph.insertNode(4, new GPSCoordinate(20,65));
	        graph.insertNode(5, new GPSCoordinate(30,40));
	        graph.insertNode(6, new GPSCoordinate(30,25));
	        graph.insertNode(7, new GPSCoordinate(30,10));
	        graph.insertEdge(1, 2, 1, 6, StreetType.MOTORWAY);
	        graph.insertEdge(1, 6, 2, 6, StreetType.MOTORWAY);
	        graph.insertEdge(1, 7, 3, 6, StreetType.MOTORWAY);
	        graph.insertEdge(2, 1, 4, 6, StreetType.MOTORWAY);
	        graph.insertEdge(2, 7, 5, 6, StreetType.MOTORWAY);
	        graph.insertEdge(2, 6, 6, 6, StreetType.MOTORWAY);
	        graph.insertEdge(2, 5, 7, 6, StreetType.MOTORWAY);
	        graph.insertEdge(2, 3, 8, 6, StreetType.MOTORWAY);
	        graph.insertEdge(3, 2, 9, 6, StreetType.MOTORWAY);
	        graph.insertEdge(3, 6, 10, 6, StreetType.MOTORWAY);
	        graph.insertEdge(3, 5, 11, 6, StreetType.MOTORWAY);
	        graph.insertEdge(3, 4, 12, 6, StreetType.MOTORWAY);
	        graph.insertEdge(4, 3, 13, 6, StreetType.MOTORWAY);
	        graph.insertEdge(4, 5, 14, 6, StreetType.MOTORWAY);
	        graph.insertEdge(5, 4, 15, 6, StreetType.MOTORWAY);
	        graph.insertEdge(5, 3, 16, 6, StreetType.MOTORWAY);
	        graph.insertEdge(5, 2, 17, 6, StreetType.MOTORWAY);
	        graph.insertEdge(5, 6, 18, 6, StreetType.MOTORWAY);
	        graph.insertEdge(6, 5, 19, 6, StreetType.MOTORWAY);
	        graph.insertEdge(6, 3, 20, 6, StreetType.MOTORWAY);
	        graph.insertEdge(6, 2, 21, 6, StreetType.MOTORWAY);
	        graph.insertEdge(6, 1, 22, 6, StreetType.MOTORWAY);
	        graph.insertEdge(6, 7, 23, 6, StreetType.MOTORWAY);
	        graph.insertEdge(7, 1, 24, 6, StreetType.MOTORWAY);
	        graph.insertEdge(7, 2, 25, 6, StreetType.MOTORWAY);
	        graph.insertEdge(7, 6, 26, 6, StreetType.MOTORWAY);
        } catch (Exception e)
        {
	        e.printStackTrace();
        }
        HierarchyMapGraph hGraph = new HierarchyMapGraph();
        try
        {
	        hGraph = graph.exportToHierachyGraph(hGraph);
        } catch (Exception e)
        {
	        e.printStackTrace();
        }
        hGraph.insertEdge(1, 3, 1, 12, StreetType.MOTORWAY, (byte)1);
        hGraph.insertEdge(3, 4, 12, 6, StreetType.MOTORWAY, (byte)1);
        hGraph.insertEdge(1, 4, 1, 18, StreetType.MOTORWAY, (byte)2);
        Path way = new Path();
        way.appendNode(hGraph.getNode(1));
        way.appendNode(hGraph.getNode(4));
        int iter = 1;
//        try TODO: Dieser Test wurde auskommentiert, das er deprecated Methoden testet.
//        {
//	        for (HierarchyMapNode n : way.reconstructWayBetweenNodes(graph, hGraph, hGraph.getNode(1), hGraph.getNode(4)))
//	        {
//	        	if (n.getUID()!=iter)
//	        		fail("Der gegebene Nikolausweg wurde nicht rekonstruiert");
//	        	iter++;
//	        }
//        } catch (NodeNotNeighbourOfPreviousElementInPathException e)
//        {
//	        e.printStackTrace();
//	        fail("Es ging schon dabei schief, dass der Pfad nicht gueltig war");
//        }
//        Logger.getInstance().log("PathTest",way);
	}
	
	@Test
	public void testPaths() throws EmptyInputException, InvalidInputException, InvalidGPSCoordinateException, NodeNotInGraphException {
		
		/*
		 * Test 1
		 * trivialer Graph, triviales Path-testen
		 */
		HierarchyMapGraph hGraph = new HierarchyMapGraph();
		ArrayList<Integer> liste = null;
		Path path = new Path();
		
		hGraph.insertNode(1);
		hGraph.insertNode(2);
		hGraph.insertNode(3);
		
		hGraph.insertEdge(1, 2, 500, 2, StreetType.LIVING_STREET, (byte) 0);
		hGraph.insertEdge(2, 3, 501, 3, StreetType.LIVING_STREET, (byte) 0);
		
		path.appendNode(1);
		path.appendNode(2);
		path.appendNode(3);
		
		try {
			path.reconstructPath(hGraph);
			liste=path.getPathNodeIDs();
		} catch (Exception e) {
			e.printStackTrace();
			fail("Rekonstruierter path konnte nciht abgeholt werden");
		}
		
		if (liste.size() != 3)
			fail("Rueckgabeliste hat die falsche Groesse. Soll: 3, Ist: "+liste.size());
		
		for(int i=0; i<liste.size(); i++) {
			try {
				if (liste.get(i) != i+1)
					fail("Reihenflge der Nodes in der Liste ist falsch. Erwarte Knoten: "+i+1+", Ist: "+liste.get(i));
			} catch (Exception e) {
				e.printStackTrace();
				fail("Fehler beim Zugriff auf die Liste");
			}
		}
		
		/*
		 * Test 2
		 * Ein Graph, der aus einem MapGraph kommt und kontrahierte Knten enthaelt
		 */
		
		MapGraph graph = new MapGraph(90.0f, 90.0f, 0.0f, 0.0f);
		
		graph.insertNodeWithoutGPS(1);
		graph.insertNodeWithoutGPS(2);
		graph.insertNodeWithoutGPS(3);
		graph.insertNodeWithoutGPS(4);
		graph.insertNodeWithoutGPS(5);
		
		graph.insertNodeWithoutGPS(6);
		graph.insertNodeWithoutGPS(7);
		graph.insertNodeWithoutGPS(10);
		graph.insertNodeWithoutGPS(11);
		graph.insertNodeWithoutGPS(20);
		
		graph.insertNodeWithoutGPS(21);
		graph.insertNodeWithoutGPS(30);
		graph.insertNodeWithoutGPS(31);
		graph.insertNodeWithoutGPS(40);
		graph.insertNodeWithoutGPS(41);
		
		int ID = 500;
		
		
		//hin
		graph.insertEdge(1, 2, ID++, 1, StreetType.LIVING_STREET);
		graph.insertEdge(1, 10, ID++, 1, StreetType.LIVING_STREET);
		graph.insertEdge(1, 11, ID++, 1, StreetType.LIVING_STREET);
		graph.insertEdge(2, 20, ID++, 1, StreetType.LIVING_STREET);
		graph.insertEdge(2, 21, ID++, 1, StreetType.LIVING_STREET);
		
		graph.insertEdge(2, 5, ID++, 1, StreetType.LIVING_STREET);
		graph.insertEdge(3, 30, ID++, 1, StreetType.LIVING_STREET);
		graph.insertEdge(3, 31, ID++, 1, StreetType.LIVING_STREET);
		graph.insertEdge(3, 4, ID++, 1, StreetType.LIVING_STREET);
		graph.insertEdge(4, 40, ID++, 1, StreetType.LIVING_STREET);
		
		graph.insertEdge(4, 41, ID++, 1, StreetType.LIVING_STREET);
		graph.insertEdge(5, 6, ID++, 1, StreetType.LIVING_STREET);
		graph.insertEdge(6, 7, ID++, 1, StreetType.LIVING_STREET);
		graph.insertEdge(7, 3, ID++, 1, StreetType.LIVING_STREET);
		
		//und zurueck
		graph.insertEdge(2, 1, ID++, 1, StreetType.LIVING_STREET);
		graph.insertEdge(10, 1, ID++, 1, StreetType.LIVING_STREET);
		graph.insertEdge(11, 1, ID++, 1, StreetType.LIVING_STREET);
		graph.insertEdge(20, 2, ID++, 1, StreetType.LIVING_STREET);
		graph.insertEdge(21, 2, ID++, 1, StreetType.LIVING_STREET);
		
		graph.insertEdge(5, 2, ID++, 1, StreetType.LIVING_STREET);
		graph.insertEdge(30, 3, ID++, 1, StreetType.LIVING_STREET);
		graph.insertEdge(31, 3, ID++, 1, StreetType.LIVING_STREET);
		graph.insertEdge(4, 3, ID++, 1, StreetType.LIVING_STREET);
		graph.insertEdge(40, 4, ID++, 1, StreetType.LIVING_STREET);
		
		graph.insertEdge(41, 4, ID++, 1, StreetType.LIVING_STREET);
		graph.insertEdge(6, 5, ID++, 1, StreetType.LIVING_STREET);
		graph.insertEdge(7, 6, ID++, 1, StreetType.LIVING_STREET);
		graph.insertEdge(3, 7, ID++, 1, StreetType.LIVING_STREET);
		
		//Die Knoten 5,6,7 verschwinden im HGraph
		
		hGraph = new HierarchyMapGraph();
		hGraph = graph.exportToHierachyGraph(hGraph);
		HHierarchyMT.buildHierarchyGraph(hGraph);
		
		path = new Path();
		//Stelle den Path jetzt manuell zusammen
		path.appendNode(1);
		path.appendNode(2);
		//path.appendNode(5);
		//path.appendNode(6);
		//path.appendNode(7);
		path.appendNode(3);
		path.appendNode(4);
		
		try {
			path.reconstructPath(hGraph);
			liste=path.getPathNodeIDs();
		} catch (Exception e) {
			e.printStackTrace();
			fail("Ein korrekter Path konnte nciht reonstruiert werden");
		}
		
		if (liste.size() != 7)
			fail("Die Ruecgabeliste hat die falsche Groesse. Soll: 7, Ist: "+liste.size());
		
		if (liste.get(0) != 1)
			fail("An Position 0 wurde erwartet: 1 - stattdessen: "+liste.get(0));
		
		if (liste.get(1) != 2)
			fail("An Position 1 wurde erwartet: 2 - stattdessen: "+liste.get(1));

		if (liste.get(2) != 5)
			fail("An Position 2 wurde erwartet: 5 - stattdessen: "+liste.get(2));
		
		if (liste.get(3) != 6)
			fail("An Position 3 wurde erwartet: 6 - stattdessen: "+liste.get(3));
		
		if (liste.get(4) != 7)
			fail("An Position 4 wurde erwartet: 7 - stattdessen: "+liste.get(4));
		
		if (liste.get(5) != 3)
			fail("An Position 5 wurde erwartet: 3 - stattdessen: "+liste.get(5));
		
		if (liste.get(6) != 4)
			fail("An Position 6 wurde erwartet: 4 - stattdessen: "+liste.get(6));
		
		
		/*
		 * Erstelle nun einen sehr grossen und langen Graphen. Dort kann regelmaessig was ausgelassen werden, allerdings sollte der Path darauf dennoch problemfrei funzen
		 */
		
		int i;
		//
		//
		// x--o--x--o--x....
		//
		// wobei x Kreuzungen und o auslassbare Knoten
		
		path = new Path();
		graph = new MapGraph(90,90,0,0);
		int gr = 1000; //bitte geraden wert!s
		
		for (i=1; i<gr;i++) {
			graph.insertNodeWithoutGPS(i);
			//jeder zweite Knoten ist eine Kreuzung
			if ((i % 2) == 1) {
				graph.insertNodeWithoutGPS(i+10*gr);
				graph.insertNodeWithoutGPS(i+20*gr);
				path.appendNode(i);
			}
			//baue Verbindungsstrassen
			if (i>1) {
				graph.insertEdge(i-1, i, 100*gr + i, 1, StreetType.PRIMARY);
				graph.insertEdge(i, i-1, 100*gr + i, 1, StreetType.PRIMARY);
			}
			//Baue Kreuzungen
			if ((i % 2) == 1) {
				graph.insertEdge(i, i+10*gr, 200*gr+i, 2, StreetType.TERTIARY);
				graph.insertEdge(i+10*gr,i, 200*gr+i, 2, StreetType.TERTIARY);
				graph.insertEdge(i, i+20*gr, 300*gr+i, 2, StreetType.TERTIARY);
				graph.insertEdge(i, i+20*gr, 300*gr+i, 2, StreetType.TERTIARY);
				
				if (i>2) {
					graph.insertEdge((i-2)+10*gr, i+10*gr, 200*gr+i, 5, StreetType.LIVING_STREET);
					graph.insertEdge((i)+10*gr, (i-2)+10*gr, 200*gr+i, 5, StreetType.LIVING_STREET);
					graph.insertEdge((i)+20*gr, (i-2)+20*gr, 300*gr+i, 5, StreetType.LIVING_STREET);
					graph.insertEdge((i-2)+20*gr, (i)+20*gr, 300*gr+i, 5, StreetType.LIVING_STREET);
				}
			}
		}
		
		//Nun fehlt nur noch der komplette hGraph
		hGraph = new HierarchyMapGraph();
		hGraph = graph.exportToHierachyGraph(hGraph);
		HHierarchyMT.buildHierarchyGraph(hGraph);
		
		try {
			path.reconstructPath(hGraph);
			liste=path.getPathNodeIDs();
		} catch (Exception e) {
			e.printStackTrace();
			fail("korrekter Pfad konnte nciht rekonstruiert werden");
		}
		
		if (liste.size() != gr-1)
			fail("rekonstruierte Gresse falsch. Erwarte: 9999, Ist: "+liste.size());
		
		for (i=0; i<liste.size(); i++) {
			if (liste.get(i) != i+1)
				fail("Falscher Knoten in Rekonstruktion: "+liste.get(i));
		}
		
		
		
	}
	
	@Test
	public void testMoreInvalidPaths() {
		HierarchyMapGraph hGraph = new HierarchyMapGraph();
		ArrayList<Integer> liste = null;
		Path path = new Path();
		boolean fehler = false;
		
		hGraph.insertNode(1);
		hGraph.insertNode(2);
		hGraph.insertNode(3);
		
		hGraph.insertEdge(1, 2, 500, 2, StreetType.LIVING_STREET, (byte) 0);
		hGraph.insertEdge(2, 3, 501, 3, StreetType.LIVING_STREET, (byte) 0);
		
		// Hole von leerem Path ab
				
		try {
			path.reconstructPath(hGraph);
			liste=path.getPathNodeIDs();
		} catch (NodeNotNeighbourOfPreviousElementInPathException e) {
			e.printStackTrace();
			fail("Rekonstruierter path konnte nciht abgeholt werden");
		} catch (Exception e) {
			e.printStackTrace();
			fail("Es gab eine Exception bei leerem Path, statt einfach null zurueckzugeben");
		}
		
		path.appendNode(1);
		//path.appendNode(2);
		path.appendNode(3);
		
		try {
			path.reconstructPath(hGraph);
			liste=path.getPathNodeIDs();
		} catch (Exception e) {
			fehler = true;
		}
		if (fehler != true)
			fail("Es wurde auf uengueltigem Path keine Exception geworfen, wie vrgesehen (fehlende Knoten/keine Nachbarn");
		
		fehler = false;
		
		
	}
}
