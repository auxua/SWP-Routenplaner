/**
 * 
 */
package algorithmen;

import static org.junit.Assert.fail;
import graphenbib.HierarchyMapGraph;
import graphenbib.HierarchyMapNode;
import graphenbib.Path;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;

import main.Config;
import main.Logger;

import org.junit.BeforeClass;
import org.junit.Test;

import Import.OSMImporter;


public class QueryAachenTest {

	
	private static HierarchyMapGraph hgraph = null;
	
	//private int start1 = 60387946; //Ein Ende der Paulusstrasse
	
	//private int start2 = 35590961; //Ein Ende der Paulusstrasse
	
	//private int ende1 = 913957655; //Ein Knoten des Holzgraben
	
	//private int ende2 = 913957738; //Ein Knoten des Holzgraben
	

	//private int start3 = 60003232; //Ein Knoten des Sandkaulbaches

	//private int ende3 = 1138173750; //Ein Knoten der Rosstrasse
		/*
	private int startk = 60776422; //Ein Knoten des Johannes-von-den-Driesch-Weg.
	private int endek = 48668746; //Ein Knoten der Karmanstr.
		*/
	private static Logger logger = Logger.getInstance();
	
	private static String sender = "QueryAachenTest"; 
	


	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		
		//Importiere Graph und erstelle Hierarchien
		try{
			Config.H = 40;
			String dir = "testdateien"+File.separatorChar+"aachen_stadt.osm";
			OSMImporter mainImporter = new OSMImporter(new File( dir )); //Rufe Preprocessing an uebergebenem OSMFile automatisch auf
			hgraph = mainImporter.exportToHierarchyMapGraph();
			HHierarchyMT.buildHierarchyGraph(hgraph); //Baut die Hierarchien im HierarchieGraphen auf
			
		} catch (Exception e) {
			fail("Es ab einen Fehler beim Import: "+e.getLocalizedMessage());
		}	 
	}

	
	@Test
	public void testQuery() throws Exception{
		//Teste hier ein paar Wege und gib die Laenge aus
		ArrayList<HierarchyMapNode> nodes = new ArrayList<HierarchyMapNode>();
		Iterator<HierarchyMapNode> iterator = hgraph.getNodeIt();
		while (iterator.hasNext()){
			nodes.add(iterator.next());
		}
		logger.log("QueryAachenTest",nodes.size());
		int nullfehler = 0;
		int laengenfehler = 0;
		int strecken = 0;
		
		
		long timeDijkstra = 0;
		long timeQuery = 0;
	    
		routenberechnung:
	    for(int i = 0; i < nodes.size(); i++){
			for(int j = i+1; j<nodes.size(); j++){
				HierarchyMapNode node1 = nodes.get(i);
				HierarchyMapNode node2 = nodes.get(j);
				
				long run = System.currentTimeMillis();
				Path dijkstra = Dijkstra.bidirectional(hgraph, node1.getUID(), node2.getUID());
				run = System.currentTimeMillis() - run;
				timeDijkstra +=run;
				
				
				strecken++;
				if (strecken % 100 == 0){
					logger.log("QueryAachenTest", strecken +" von "+ nodes.size()*(nodes.size()-1)/2 +" Strecken berechnet");
				}

				if (strecken>100000) break routenberechnung; 
				if (dijkstra.getPathNodeIDs().size() == 0){
					nullfehler++;
				}

				run = System.currentTimeMillis();
				Path query = Query.computeHierarchyPath(hgraph, node1.getUID(), node2.getUID());
				run = System.currentTimeMillis() - run;
				timeQuery +=run;				
				
				if ( dijkstra.size() != query.size() ){
					laengenfehler++;

					logger.log("QueryAachenTest","Dijkstra von a nach b: "+dijkstra.getPathNodeIDs());
					logger.log("QueryAachenTest","Query von a nach b: "+query.getPathNodeIDs());
				}
			}
		}
		float percentage = (float)(100*nullfehler) / (float)strecken;
		logger.log("QueryAachenTest","Routen mit Laenge 0: "+nullfehler+", "+percentage+"%");

		float percentage2 = (float)(100*laengenfehler) / (float)strecken;
		logger.log("QueryAachenTest","Routen mit Unterschied zwischen D und Q: "+laengenfehler+", "+percentage2+"%");
		
		logger.log("QueryAachenTest","Dijkstra: Zeit "+timeDijkstra);
		logger.log("QueryAachenTest","Query: Zeit "+timeQuery);
	}


	@Test
	public void analyse() throws Exception{
		Iterator<HierarchyMapNode> iterator = hgraph.getNodeIt();
		int[] levels = new int[Config.maxHierarchyLayer+1];
		while( iterator.hasNext()){
			HierarchyMapNode node = iterator.next();
			levels[node.getLevel()]++;
		}
		for( int i= 0; i< levels.length;i++){
			logger.log("QueryAachenTest","Knoten auf Level "+i+": "+levels[i]);
		}
	}
	
	
	@Test
	public void testPath() throws Exception{
		int startnode = 33414648;
		int endnode = 86130148;
		
		Path route = Dijkstra.bidirectional(hgraph, startnode, endnode);
		
		ArrayList<Integer> nodes = route.getPathNodeIDs();
		
		long distance = 0;
		
		int last= route.getStartNodeID();
		
		for( int node : nodes){
			
			
			HierarchyMapNode hnode= hgraph.getNode(node);
			
			if (hnode != null){
				if (node != route.getStartNodeID()){
					distance += hnode.getEdgeToNeighbour(last).getWeight();
				}
				System.out.println(node + " " + hnode.getLevel() + " " + distance + " " + hnode.getdH(hnode.getLevel(), true) + " " + hnode.getdH(hnode.getLevel(), false));
				last = node;
			}
		}
		
		System.out.println(Query.computeHierarchyPath(hgraph, startnode, endnode));
		System.out.println(hgraph.getNode(966985326));
	}
	
	
}
