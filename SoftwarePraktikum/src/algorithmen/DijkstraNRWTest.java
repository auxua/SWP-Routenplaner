/**
 * 
 */
package algorithmen;

import static org.junit.Assert.fail;
import graphenbib.HierarchyMapGraph;
import graphenbib.MapEdge;
import graphenbib.MapGraph;
import graphenbib.MapNode;

import java.io.File;
import java.util.ArrayList;

import main.Logger;

import org.junit.BeforeClass;
import org.junit.Test;

import Import.OSMImporter;

/**
 * 
 */
public class DijkstraNRWTest {

	private static MapGraph graph = null;
	
	private static Logger logger = Logger.getInstance();
	
	
	private int startk = 255299241; //Ein Knoten derRichasrztstr.
	private int endek = 253904185; //Ein Knoten der pastor-jaeaensch-Weg
	
	/*
	private int startk = 60776422; //Ein Knoten des Johannes-von-den-Driesch-Weg.
	private int endek = 48668746; //Ein Knoten der Karmanstr.
	private int fussweg3 = 60004529; //Ein Knoten eines Fussweges
	private int fussweg1 = 820866216; //Ein Knoten eines Fussweges
	private int fussweg2 = 48668748; //Ein Knoten eines Fussweges
	private int fussweg4 = 986318698; //Ein Knoten eines Fussweges
	private int fussweg5 = 986318697; //Ein Knoten eines Fussweges
	*/
	
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
	


	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		
		long start = System.currentTimeMillis();
		try{
			OSMImporter os_imp = new OSMImporter(new File("testdateien/NRW.osm"));
			graph = os_imp.getTile(os_imp.getMapCenter(), 0);
		} catch (Exception e) {
			fail("Es ab einen Fehler beim Import: "+e.getLocalizedMessage());
		}
		long ende = System.currentTimeMillis();
		logger.log("DijkstraNRWTest","Einlesedauer: "+(ende-start)+"ms");
		//Kleiner Test am Rande: Fuehre DIN aus
		start =System.currentTimeMillis();
		try {
			graph.deleteIsolatedNodes();
		} catch (Exception e) {
			fail("DIN-Methode hat ncihtfunktioniert auf NRW: "+e.getLocalizedMessage());
		}
		ende = System.currentTimeMillis();
		logger.log("DijkstraNRWTest","Optimierungsdauer: "+(ende-start)+"ms");
	}

	@Test
	public void test() {
		ArrayList<MapNode> liste = null;
		try {
			
			long start = System.currentTimeMillis();
			liste = Dijkstra.bidirectional(graph, endek, startk);
			long ende = System.currentTimeMillis();
			logger.log("DijkstraNRWTest","Wege von ende nach start hat Laenge: "+weglaenge(liste));
			logger.log("DijkstraNRWTest","Wege von ende nach start hat Knoten: "+liste.size());
			logger.log("DijkstraNRWTest","Berechnungsdauer: "+(ende-start)+"ms");
			
			logger.log("DijkstraNRWTest","--------------------");
			
			start = System.currentTimeMillis();
			liste = Dijkstra.bidirectional(graph, startk, endek);
			ende = System.currentTimeMillis();
			logger.log("DijkstraNRWTest","Wege von start nach ende hat Laenge: "+weglaenge(liste));
			logger.log("DijkstraNRWTest","Wege von start nach ende hat Knoten: "+liste.size());
			logger.log("DijkstraNRWTest","Berechnungsdauer: "+(ende-start)+"ms");
			//wegausgabe(liste);
			
			
			start = System.currentTimeMillis();
			liste = Dijkstra.bidirectional(graph, endek, startk);
			ende = System.currentTimeMillis();
			logger.log("DijkstraNRWTest","Wege von ende nach start hat Laenge: "+weglaenge(liste));
			logger.log("DijkstraNRWTest","Wege von ende nach start hat Knoten: "+liste.size());
			logger.log("DijkstraNRWTest","Berechnungsdauer: "+(ende-start)+"ms");
			
			logger.log("DijkstraNRWTest","--------------------");
			
			start = System.currentTimeMillis();
			liste = Dijkstra.bidirectional(graph, startk, endek);
			ende = System.currentTimeMillis();
			logger.log("DijkstraNRWTest","Wege von start nach ende hat Laenge: "+weglaenge(liste));
			logger.log("DijkstraNRWTest","Wege von start nach ende hat Knoten: "+liste.size());
			logger.log("DijkstraNRWTest","Berechnungsdauer: "+(ende-start)+"ms");
			
				
			
		} catch (Exception e) {
			fail("Fehler beim Berechnen der kuerzesten Wege/ihrer Laenge: "+e.getLocalizedMessage());
		}
		
		

	}

	@Test
	public void Htest() {
		//Importiere NRW, exportiere zu HGraph und mache eine Hierarchie
		HierarchyMapGraph hgraph = new HierarchyMapGraph();
		try {
			hgraph = graph.exportToHierachyGraph(hgraph);
		} catch (Exception e) {
			fail("fehler beim export: "+e.getLocalizedMessage());
		} 
		
		//Erstelle nun Hierarchie
		HHierarchyMT.computeHierarchy(hgraph, (byte)2);
	}
}
