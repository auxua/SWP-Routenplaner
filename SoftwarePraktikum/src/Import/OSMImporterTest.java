package Import;

import graphenbib.HierarchyMapGraph;

import java.io.File;

import algorithmen.HHierarchyMT;


/**
 * Diese Klasse dient dem Testen der Klasse {@link OSMImporter}. Sie erzeugt ein Objekt der Klasse 
 * und liest eine map.osm Datei im Projektordner ein. map.osm ist der Standard-Dateiname, 
 * wenn man eine .osm Datei auf  http://www.openstreetmap.org/export herunterlaedt.
 * 
 *
 */

public class OSMImporterTest {

	public static void main(String[] args) {
		try{
			OSMImporter osm_imp = new OSMImporter(new File("E:/nrw.osm.mapfiles/ProcessedTilesConfig.tiles"));

			HierarchyMapGraph hgraph = osm_imp.exportToHierarchyMapGraph();
			HHierarchyMT.buildHierarchyGraph(hgraph);
			osm_imp.saveHGraph(hgraph);


			
//			osm_imp.loadPreProcessedTiles();
//			osm_imp.saveHGraph(osm_imp.exportToHierarchyMapGraph());
			
			//System.out.println(osm_imp.getTile(391616930).getNode(391616937).toString());
			
//			OSMImporter osmimp= new OSMImporter("tmp");
//			osmimp.loadPreProcessedTiles();
//			System.out.println(osmimp.getAllTiles().toString());
//			ArrayList<MapGraph> list = osm_imp.getTiles(new GPSCoordinate(6.075f, 50.78f), new GPSCoordinate(6.08f, 50.779f));
//			Iterator<MapGraph> it = list.iterator();
//			while(it.hasNext()){
//				MapGraph g =it.next();
//			}
		}
		catch(Exception e){
			System.err.println("Error:");
			e.printStackTrace();
		}

	}

}
