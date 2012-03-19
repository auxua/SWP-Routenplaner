/**
 * 
 */
package Import;

import static org.junit.Assert.fail;
import graphenbib.GPSCoordinate;
import main.Config;
import main.Logger;

import org.junit.BeforeClass;
import org.junit.Test;



public class FileTilerImportTest {

	private static Logger logger = Logger.getInstance();
	//private static OSMImporter osm_imp = null;
	
	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		//unnoetig, da getTiles nciht mehr erlaubt in der Form
//		if (Constants.maxSingleFileSize != 4) {
//			logger.log("FAIL", "Dieser Tesst funktioniert nur, wenn Constants.maxSingleFileSize auf 4 Byte eingestellt ist!");
//			fail();
//		}
//		osm_imp = new OSMImporter(new File("testdateien/TilerGraph1.osm"));
//		logger.log("FileTilerImportTest","starte PP");
//		osm_imp.preProcess();
	}
	
	/**
	 * Test fuer Distanz-formel.
	 */
	@Test
	public void testTileLength() {
		
		int sidelength= 50000; //Seitenlaenge des Quadrats in dM
		//float minlat, minlon, maxlat, maxlon;
		int erdradius = Config.erdRadius;
		float latSize= (float) (sidelength*360/(2*Math.PI*erdradius));
		//float lonSize= (float) (sidelength*360/(2*Math.PI*erdradius)); 
		float lonSize= (float) (sidelength*360/(2*Math.PI*erdradius));
		GPSCoordinate a=null,b = null;
		int eps = sidelength/100;
		
		
		logger.log("FileTilerImportTest", "Sollwert fuer seitenlaenge: 50.000");
		logger.log("FileTilerImportTest", "latsize: "+latSize);
		logger.log("FileTilerImportTest", "latsize: "+lonSize);
		logger.log("FileTilerImportTest", "Berechnung: (Basis: (0,0))");
		
		try {
			a = new GPSCoordinate(latSize,lonSize);
			b = new GPSCoordinate(0,lonSize);
		} catch (Exception e) { 
			fail("Fehler beim Anlegen einer GPSCoordinate: "+e.getLocalizedMessage());
		}
		
		int latErg = 0;
		try {
			latErg = a.distanceTo(b);
		} catch (Exception e) {
			fail("Fehler bei der Abstandsberechnung: "+e.getLocalizedMessage());
		}
		
		try {
			b = new GPSCoordinate(latSize,0);
		} catch (Exception e) {
			fail("Fehler beim Anlegen einer GPSCoordinate: "+e.getLocalizedMessage());
		}
		
		int lonErg = 0;
		try {
			lonErg = a.distanceTo(b);
		} catch (Exception e) {
			fail("Fehler bei der Abstandsberechnung: "+e.getLocalizedMessage());
		}
		
		
		logger.log("FileTilerImportTest", "Ergebnis Entfernung der lat.: "+latErg);
		logger.log("FileTilerImportTest", "Ergebnis Entfernung der lon.: "+lonErg);
		
		if ((latErg< sidelength-eps) || (latErg > sidelength+eps))
			fail("Damit ist die lat.-Eigenschaft zu weit vom gewuenschten Wert entfernt");
		if ((lonErg< sidelength-eps) || (lonErg > sidelength+eps))
			fail("Damit ist die lon.-Eigenschaft zu weit vom gewuenschten Wert entfernt");
		
		
		
		logger.log("FileTilerImportTest", "Berechnung: (Basis: (45,45))");
		
		try {
			a = new GPSCoordinate(45+latSize,45+lonSize);
			b = new GPSCoordinate(45,45+lonSize);
		} catch (Exception e) { 
			fail("Fehler beim Anlegen einer GPSCoordinate: "+e.getLocalizedMessage());
		}
		
		latErg = 0;
		try {
			latErg = a.distanceTo(b);
		} catch (Exception e) {
			fail("Fehler bei der Abstandsberechnung: "+e.getLocalizedMessage());
		}
		
		try {
			b = new GPSCoordinate(45+latSize,45);
		} catch (Exception e) {
			fail("Fehler beim Anlegen einer GPSCoordinate: "+e.getLocalizedMessage());
		}
		
		lonErg = 0;
		try {
			lonErg = a.distanceTo(b);
		} catch (Exception e) {
			fail("Fehler bei der Abstandsberechnung: "+e.getLocalizedMessage());
		}
		
		
		logger.log("FileTilerImportTest", "Ergebnis Entfernung der lat.: "+latErg);
		logger.log("FileTilerImportTest", "Ergebnis Entfernung der lon.: "+lonErg);
		
		if ((latErg< sidelength-eps) || (latErg > sidelength+eps))
			fail("Damit ist die lat.-Eigenschaft zu weit vom gewuenschten Wert entfernt");
		//if ((lonErg< sidelength-eps) || (lonErg > sidelength+eps))
		/*
		 * Korrektur - nun werden Trapeze erwartet - halte mit Test das Trapez konstant!
		 */
		if (lonErg != 35328)
			fail("Damit ist die lon.-Eigenschaft zu weit vom gewuenschten Wert entfernt");
		
		
		logger.log("FileTilerImportTest", "Berechnung: (Basis: (80,170))");
		
		try {
			a = new GPSCoordinate(80+latSize,170+lonSize);
			b = new GPSCoordinate(80,170+lonSize);
		} catch (Exception e) { 
			fail("Fehler beim Anlegen einer GPSCoordinate: "+e.getLocalizedMessage());
		}
		
		latErg = 0;
		try {
			latErg = a.distanceTo(b);
		} catch (Exception e) {
			fail("Fehler bei der Abstandsberechnung: "+e.getLocalizedMessage());
		}
		
		try {
			b = new GPSCoordinate(80+latSize,170);
		} catch (Exception e) {
			fail("Fehler beim Anlegen einer GPSCoordinate: "+e.getLocalizedMessage());
		}
		
		lonErg = 0;
		try {
			lonErg = a.distanceTo(b);
		} catch (Exception e) {
			fail("Fehler bei der Abstandsberechnung: "+e.getLocalizedMessage());
		}
		
		
		logger.log("FileTilerImportTest", "Ergebnis Entfernung der lat.: "+latErg);
		logger.log("FileTilerImportTest", "Ergebnis Entfernung der lon.: "+lonErg);
		
		if ((latErg< sidelength-eps) || (latErg > sidelength+eps))
			fail("Damit ist die lat.-Eigenschaft zu weit vom gewuenschten Wert entfernt");
		//if ((lonErg< sidelength-eps) || (lonErg > sidelength+eps))
		/*
		 * Anpassung an Trapeze
		 */
		if (lonErg != 8644)
			fail("Damit ist die lon.-Eigenschaft zu weit vom gewuenschten Wert entfernt");
	}

	/**
	 * Test method for {@link Import.FileTiler#TileFile(java.lang.String, java.lang.String)}.
	 * -> existiert nciht mehr - kein aequivalenter Ersatz dieser Methode existent
	 */
//	@Test
//	public void testTileFile(){
//		MapGraph graph = null;
//		
//		logger.log("FileTileImporter", "teste nun die getTile(GPS)-Methode");
//		
//		try {
//			graph = osm_imp.getTile(new GPSCoordinate(4,-4));
//		} catch (Exception e) {
//			fail("Fehler beimabholen eines Tiles/erstellen einer Koordinate: "+e.getLocalizedMessage());
//		}
//		if (graph == null)
//			fail("Es wurde ein Nullpointer statt eines Graphen zurueckgeliefert");
//		if (graph.getNode(1) == null)
//			fail("Der zurueckgeliferte Graph beinhaltet nciht die angeforderte Node");
//		
//		try {
//			graph = osm_imp.getTile(new GPSCoordinate(2,-1));
//		} catch (Exception e) {
//			fail("Fehler beimabholen eines Tiles/erstellen einer Koordinate: "+e.getLocalizedMessage());
//		}
//		if (graph == null)
//			fail("Es wurde ein Nullpointer statt eines Graphen zurueckgeliefert");
//		if (graph.getNode(9) == null)
//			fail("Der zurueckgeliferte Graph beinhaltet nciht die angeforderte Node");
//		
//		logger.log("FileTileImporter", "teste nun die getAllTiles-Methode");
//		
//		ArrayList<MapGraph> liste = null;
//		try {
//			liste = osm_imp.getAllTiles();
//		} catch (Exception e) {
//			fail("Es gab einen Fehler beim abholen aller Tiles: "+e.getLocalizedMessage());
//		}
//		
//		if (liste == null)
//			fail("Statt einer ArrayList wurde ein Nullpointer zurueckgeliefert");
//		if (liste.size() !=324)
//			fail("Die ArrayList hat nciht die erwartete Groesse, sondern: "+liste.size());
//		
//			/*
//		logger.log("FileTileImporter", "teste nun die getTilesForPath-Methode");
//		
//		try {
//			graph = osm_imp.getTilesForPath(1, 5);
//		} catch (Exception e) {
//			fail("Fehler bei der getTilesForPath-Methode: "+e.getLocalizedMessage());
//		}
//			
//		//Pruefe, ob die korrekten Nodes drin sind (Soll:1,2,3,9,10,11,12,5,6,7)
//		if (graph == null)
//			fail("Es wurde ein Nullpointer stat eines MapGraphen geliefert");
//		int[] nodes1 = {1,2,3,9,10,11,12,5,6,7};
//		for (int i = 0; i<nodes1.length; i++)
//		{
//			if (graph.getNode(nodes1[i]) == null)
//				fail("Node "+i+" ist nicht im Graphen enthalten, obwohl sie in dem Rechteck liegt");
//		}
//		
//		//Pruefe, ob die anderen draussen sind
//		int[] nodes2 = {4,8,13};
//		for (int i = 0; i<nodes2.length; i++)
//		{
//			if (graph.getNode(nodes2[i]) == null)
//				fail("Node "+i+" ist im Graphen enthalten, obwohl sie nicht in dem Rechteck liegt");
//		}
//			
//		ArrayList<MapNode> nodeListe = null;
//		//Pruefe, ob Dijkstra hier laeuft (sollte er!)
//		try {
//			nodeListe = Dijkstra.bidirectional(graph, 1, 5);
//		} catch (Exception e) {
//			fail("Dijkstra failt auf diesem Graphen: "+e.getLocalizedMessage());
//		}
//		
//		if (nodeListe.size() != 4)
//			fail("DIe Rueckgabegroese stimmt nciht mit dem kuerzesten We ueberein. Erwartete Groesse: 4, Haben: "+nodeListe.size());
//			*/
//		//Analo daz der gleiche Test, aber dieses mal bitte als HH
//		ArrayList<MapNode> nodeListe = null;
//		HierarchyMapGraph hgraph = null;
//		try {
//			hgraph = osm_imp.exportToHierarchyMapGraph();
//		} catch (Exception e) {
//			fail("Es gabe einen ehler beim Export zu einem HH: "+e.getLocalizedMessage());
//		}
//		Path weg = null;
//		try {
//			weg = Dijkstra.bidirectional(hgraph, 1, 5);
//		} catch (Exception e) {
//			fail("Dijkstra failt auf diesem Graphen: "+e.getLocalizedMessage());
//		}
//		
//		if (weg == null)
//			fail("Es wurde ein Nullpinter zurueckgegeben");
//		//ArrayList<HierarchyMapNode> hnodeListe = weg.getCurrentPathViaNodes();
//		ArrayList<Integer> hnodeListe = weg.getCurrentPathViaNodes();
//		if (hnodeListe.size() != 4)
//			fail("DIe Rueckgabegroese stimmt nciht mit dem kuerzesten We ueberein. Erwartete Groesse: 4, Haben: "+hnodeListe.size());
//		
//	}

}
