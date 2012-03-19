package algorithmen;

import static org.junit.Assert.fail;
import graphenbib.MapEdge;
import graphenbib.MapGraph;
import graphenbib.MapNode;

import java.io.File;
import java.util.ArrayList;

import main.Logger;

import org.junit.BeforeClass;
import org.junit.Test;

import Import.OSMImporter;


public class DijkstraAachenTest {

	private static MapGraph graph = null;
	private int start1 = 60387946; //Ein Ende der Paulusstrasse
	private int start2 = 35590961; //Ein Ende der Paulusstrasse
	private int ende1 = 913957655; //Ein Knoten des Holzgraben
	private int ende2 = 913957738; //Ein Knoten des Holzgraben
	
	private int start3 = 60003232; //Ein Knoten des Sandkaulbaches
	private int ende3 = 1138173750; //Ein Knoten der Rosstrasse
		/*
	private int startk = 60776422; //Ein Knoten des Johannes-von-den-Driesch-Weg.
	private int endek = 48668746; //Ein Knoten der Karmanstr.
		*/
	private static Logger logger = Logger.getInstance();
	
	private double weglaenge(ArrayList<MapNode> liste) {
		
		double test = 0;
		MapNode nachfolger = null;
		MapEdge kante = null;
		int j = 0;

		for (int i=0;i<liste.size()-2;i++) {
			nachfolger = liste.get(i+1);
			j=0;
			while (liste.get(i).getOutgoingEdges()[j].getNodeEnd() != nachfolger) j++; //Naiver Test - Wende daher nur auf garantierte Pfade von Dijkstra an!
			kante = liste.get(i).getOutgoingEdges()[j];
			test = test + kante.getWeight();
		}
		return test;
	}
	
	private void wegausgabe(ArrayList<MapNode> liste) {
		
		MapNode nachfolger = null;

		for (int i=0;i<liste.size();i++) {
			nachfolger = liste.get(i);
			logger.log("Route", String.valueOf(nachfolger.getUID()));
		}
	}
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		
		//Importiere Graph
		try{
			//OSMImporter os_imp = new OSMImporter("testdateien/Aachen_Innenring_fix.osm","tmp");
			OSMImporter os_imp = new OSMImporter(new File("testdateien/aachen_stadt.osm"));
			graph = os_imp.getTile(os_imp.getMapCenter(), 0);
			graph = os_imp.getTile(os_imp.getMapCenter(), 0);
			logger.log("DijkstraAachenTest","Import beedent - nun DIN");
			graph.deleteIsolatedNodes();
			logger.log("DijkstraAachenTest","DIN beendet - freier Sepciher: "+Runtime.getRuntime().freeMemory());
			System.gc();
			logger.log("DijkstraAachenTest","GC beendet - freier Sepciher: "+Runtime.getRuntime().freeMemory());
		} catch (Exception e) {
			fail("Es ab einen Fehler beim Import: "+e.getLocalizedMessage());
		}
		 
	}

	/*@Test
	public void testNeighbourhood() {
		//grober Test - nicht die Werte, sondern mehr, ob es funzt
		int test = 0;
		try {
			Dijkstra.H = 100;
			test = Dijkstra.neighbourhood(graph, start1, true);
			logger.log("DijkstraAachenTest","Nachbarschaft zu Knoten start1: "+test);
			test = Dijkstra.neighbourhood(graph, start2, true);
			logger.log("DijkstraAachenTest","Nachbarschaft zu Knoten start2: "+test);
			
			test = Dijkstra.neighbourhood(graph, ende1, false);
			logger.log("DijkstraAachenTest","Nachbarschaft zu Knoten ende1: "+test);
			test = Dijkstra.neighbourhood(graph, ende2, false);
			logger.log("DijkstraAachenTest","Nachbarschaft zu Knoten ende2: "+test);
		} catch (Exception e) {
			fail("Es gab einen Fehler bei der nachbarschaft auf Aachen: "+e.getLocalizedMessage());
		}
	}*/

	@Test
	public void testBidirectional() {
		//Teste hier ein paar Wege und gib die Laenge aus
		ArrayList<MapNode> liste = null;
		try {
			liste = Dijkstra.bidirectional(graph, start1, ende1);
			if (weglaenge(liste) != 0) fail("Es wurde ien Weg gefunden, obwohl dieser nciht exisitert (fussweg!)");
			logger.log("DijkstraAachenTest","Wege von start1 nach ende1 hat Laenge: "+weglaenge(liste));
			
			liste = Dijkstra.bidirectional(graph, start2, ende1);
			if (weglaenge(liste) != 0) fail("Es wurde ien Weg gefunden, obwohl dieser nciht exisitert");
			logger.log("DijkstraAachenTest","Wege von start2 nach ende1 hat Laenge: "+weglaenge(liste));
			
			liste = Dijkstra.bidirectional(graph, start1, ende2);
			if (weglaenge(liste) != 0) fail("Es wurde ien Weg gefunden, obwohl dieser nciht exisitert");
			logger.log("DijkstraAachenTest","Wege von start1 nach ende2 hat Laenge: "+weglaenge(liste));
			
			liste = Dijkstra.bidirectional(graph, start2, ende2);
			if (weglaenge(liste) != 0) fail("Es wurde ien Weg gefunden, obwohl dieser nicht exisitert");
			logger.log("DijkstraAachenTest","Wege von start2 nach ende2 hat Laenge: "+weglaenge(liste));
			
			liste = Dijkstra.bidirectional(graph, start3, ende3);
			if (weglaenge(liste) == 0) fail("Es wurde kien Weg gefunden, obwohl dieser exisitert");
			logger.log("DijkstraAachenTest","Wege von start3 nach ende3 hat Laenge: "+weglaenge(liste));
			wegausgabe(liste);
				/*
			liste = Dijkstra.bidirectional(graph, startk, endek);
			if (weglaenge(liste) == 0) fail("Es wurde kien Weg gefunden, obwohl dieser exisitert");
			logger.log("DijkstraAachenTest","Wege von startk nach endek hat Laenge: "+weglaenge(liste));
			
			liste = Dijkstra.bidirectional(graph, endek, startk);
			if (weglaenge(liste) == 0) fail("Es wurde kien Weg gefunden, obwohl dieser exisitert");
			logger.log("DijkstraAachenTest","Wege von endek nach startk hat Laenge: "+weglaenge(liste));
				*/
		} catch (Exception e) {
			fail("Fehler beim Berechnen der kuerzesten Wege/ihrer Laenge: "+e.getLocalizedMessage());
		}
		
		
		
		//Kleiner Test am Rande: Fuehre DIN aus
		long start =System.currentTimeMillis();
		graph.deleteIsolatedNodes();
		long ende = System.currentTimeMillis();
		logger.log("DijkstraAachenTest","Optimierungsdauer: "+(ende-start)+"ms");
	}

}
