package Import;

import graphenbib.HierarchyMapGraph;
import graphenbib.MapGraph;
import graphenbib.MapNode;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;

import main.Logger;
import main.Config;

import algorithmen.HHierarchyMT;


/**
 * Diese Klasse dient beliebigen Tests und der Analyse von Daten.
 * 
 *
 */

public class OSMImporterTest {

	public static void main(String[] args) {
		try{
			//OSMImporter osm_imp = new OSMImporter(new File(Config.defaultFileString));
			OSMImporter osm_imp = new OSMImporter(new File("testdateien"+File.separatorChar+"bremen.osm"));
			
			// Getting Average node/edge-ratio
			ArrayList<MapGraph> liste = osm_imp.getAllTiles(0);
			int gr = liste.size();
			int act = 0;
			double nodes = 0;
			double ways = 0;
			MapNode node;
			Iterator<MapNode> it;
			MapGraph mg;
			
			while (act < gr) {
				mg = liste.get(act++);
				it = mg.getNodeIt();				
				while (it.hasNext()) {
					nodes++;
					node = it.next();
					ways = ways + node.getNumberOfIncomingEdges() + node.getNumberOfOutgoingEdges();
				}
			}
			
			Logger.getInstance().log("RatioTest", "Nodes: "+nodes+", Edges: "+ways+", Ratio (Edges per Node): "+(ways/nodes));
			
			
			/* Old test */
			//HierarchyMapGraph hgraph = osm_imp.exportToHierarchyMapGraph();
			//HHierarchyMT.buildHierarchyGraph(hgraph);
			//osm_imp.saveHGraph(hgraph);


			
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
