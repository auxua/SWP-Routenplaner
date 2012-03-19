package algorithmen;

import static org.junit.Assert.fail;
import graphenbib.HierarchyMapGraph;

import java.io.File;

import Import.OSMImporter;

public class ComponentTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		HierarchyMapGraph hGraph = null;
		
		//Importiere mal
		try{
			//OSMImporter mainImporter = new OSMImporter(new File("testdateien"+File.separatorChar+"aachen_stadt_TEST.osm")); //Rufe Preprocessing an uebergebenem OSMFile automatisch auf
			OSMImporter mainImporter = new OSMImporter(new File("testdateien"+File.separatorChar+"aachen.osm")); //Rufe Preprocessing an uebergebenem OSMFile automatisch auf
			hGraph = mainImporter.exportToHierarchyMapGraph();
			HHierarchyMT.buildHierarchyGraph(hGraph); //Baut die Hierarchien im HierarchieGraphen auf
			
		} catch (Exception e) {
			fail("Es ab einen Fehler beim Import: "+e.getLocalizedMessage());
		}	
		
		//Teste jetzt nach und nah
		//Lege einen Komponententester an
		Components comp = new Components(hGraph);
		
		
		for (byte level = 0; level < 12; level++) {
			comp.computeComponents(level);
			main.Logger.getInstance().log("ComponentTest", "Komponenten auf Level " + level + ": "+ comp.getNumberOfComponents());
			//System.out.println("Komponenten auf Level " + level + ": "+ comp.getNumberOfComponents());
		}
	}

}
