package graphenbib;

import static org.junit.Assert.fail;
import main.Logger;

import org.junit.Test;

public class MapGraphBelastungstest {

	//Diese Methode gibt eine Zufallszahl aus. min und max bezeichnen dabei allerdings die Vorkomma-Zahl - max.99 ist daher moeglich
	private static float randomDouble(float min, float max) {
		return (float) (Math.random() * (max - min + 1)) + min;
	}
	
	
	@Test
	public void test() {
		//Wieviele Knoten/Kanten sollen betrachtet werden?
		int max_node = 1000000;
		int max_edge = 500000;
		int i=1;
		//Abschaeutungen fuer den Speicherverbrauch
		float grkoord, grnode, gredge, grgraph;
		//Zeitmessung
		long timekoord, timenode, timeedge, timegraph;
		
		
		try {
			Logger.getInstance().log("MapGraphBelastungstest","Bitte dieses Programm durchlaufen lassen - es raeumt am ende selbststaendig den Speicher auf (bei Terminierungen gab es immer grosse Reste)");
			
			Logger.getInstance().log("MapGraphBelastungstest","Speicherratio: "+Runtime.getRuntime().freeMemory()/1000000+" MB frei von "+Runtime.getRuntime().totalMemory()/1000000);
			
			
			//Lege Koordinaten an (Array)
			GPSCoordinate[] koords = new GPSCoordinate[max_node];
			Logger.getInstance().log("MapGraphBelastungstest","Koordinaten-Array angelegt");
			//Lege Graph an
			
			//Hole Speicherauslastung z beginn ab
			grgraph = Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory();
			timegraph = System.currentTimeMillis();
			
			MapGraph graph = null;
			try {
				graph = new MapGraph(new GPSCoordinate(90,-180), new GPSCoordinate(-90,180));
			} catch (Exception e) {
				fail("Fehler bim MapGraph: "+e.getLocalizedMessage());
			}
			
			Logger.getInstance().log("MapGraphBelastungstest","Nodes-Array und Graph angelegt");
			if (graph == null)
				fail("graphist null");
			
			Logger.getInstance().log("MapGraphBelastungstest","Speicherratio: "+Runtime.getRuntime().freeMemory()/1000000+" MB frei von "+Runtime.getRuntime().totalMemory()/1000000);
		
			//Hole Speicherauslastung z beginn ab
			grkoord = Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory();
			timekoord = System.currentTimeMillis();
			
			//Setze/konstruiere die Koordinaten
			for (i=1; i<max_node; i++) {
				try {
					koords[i] = new GPSCoordinate(randomDouble(-90,89),randomDouble(-180,179));
				} catch (Exception e) {
					fail("fehler: "+e.getLocalizedMessage());
				}
			}
			//Hole Speicherauslastung danach ab
			grkoord = (Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory()) - grkoord;
			timekoord = System.currentTimeMillis()-timekoord;
			//Rechne runer auf einzelne Koordinate
			grkoord = (grkoord / max_node);
			
			
			Logger.getInstance().log("MapGraphBelastungstest","Koordinaten angelegt");
			//Logger.getInstance().log("MapGraphBelastungstest","freier Speicher nach Koordinaten: "+Runtime.getRuntime().totalMemory()/1000000);
			Logger.getInstance().log("MapGraphBelastungstest","Speicherratio: "+Runtime.getRuntime().freeMemory()/1000000+" MB frei von "+Runtime.getRuntime().totalMemory()/1000000);
		
			//Hole Speicherauslastung z beginn ab
			grnode = Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory();
			timenode = System.currentTimeMillis();
			
			//Setze/konstruiere die Nodes
			for (i=1; i<max_node; i++) {
				try {
					graph.insertNode(i, koords[i]);
					//nodes[i] = new MapNode(i,koords[i],NodeType.CROSSING);
				} catch (Exception e) {
					fail("fehler: "+e.getLocalizedMessage()+" i: "+i+" koords: "+koords[i]);
				}
			}
			
			//Hole Speicherauslastung danach ab
			grnode = (Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory()) - grnode;
			timenode = System.currentTimeMillis()-timenode;
			//Rechne runer auf einzelne Node
			grnode = (grnode / max_node);
			
			Logger.getInstance().log("MapGraphBelastungstest","Nodes im Graphen angelegt");
			//Logger.getInstance().log("MapGraphBelastungstest","freier Speicher nach Nodes: "+Runtime.getRuntime().totalMemory()/1000000);
			Logger.getInstance().log("MapGraphBelastungstest","Speicherratio: "+Runtime.getRuntime().freeMemory()/1000000+" MB frei von "+Runtime.getRuntime().totalMemory()/1000000);
			
			//Hole Speicherauslastung z beginn ab
			gredge = Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory();
			timeedge = System.currentTimeMillis();
			
			//Fuege nun Kanten hinzu
			for (i=1; i<max_edge; i++) {
				try {
					graph.insertEdge((int) Math.random()*max_node + 1, (int) Math.random()*max_node + 1, i, (int) Math.random(), StreetType.MOTORWAY);
				} catch (Exception e) {
					fail("fehler: "+e.getLocalizedMessage());
				}
			}
			
			//Hole Speicherauslastung danach ab
			gredge = (Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory()) - gredge;
			timeedge = System.currentTimeMillis()-timeedge;
			//Rechne runer auf einzelne Node
			gredge = (gredge / max_edge);
			
			//Hole Speicherauslastung danach ab
			grgraph = (Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory()) - grgraph;
			timegraph = System.currentTimeMillis()-timegraph;
			
			Logger.getInstance().log("MapGraphBelastungstest","Edges im Graph angelegt");
			Logger.getInstance().log("MapGraphBelastungstest","erfolgreich durchgelaufen. Anzahl nodes/GPS: "+max_node+" Anzahl Edges: "+max_edge);
			Logger.getInstance().log("MapGraphBelastungstest","Speicherratio: "+Runtime.getRuntime().freeMemory()/1000000+" MB frei von "+Runtime.getRuntime().totalMemory()/1000000);
			
			Logger.getInstance().log("MapGraphBelastungstest","----------- Groessen-Abschaetzungen innerhalb des Graphen ----------");
			Logger.getInstance().log("MapGraphBelastungstest","Abschaetung Groesse einer Koordinate (in Bytes): "+grkoord);
			Logger.getInstance().log("MapGraphBelastungstest","Abschaetung Groesse einer Node (in Bytes): "+grnode);
			Logger.getInstance().log("MapGraphBelastungstest","Abschaetung Groesse einer Edge (in Bytes): "+gredge);
			Logger.getInstance().log("MapGraphBelastungstest","Abschaetung Groesse des Graphen (in MBytes): "+grgraph/1000000);
			Logger.getInstance().log("MapGraphBelastungstest","----------- Zeiten ----------");
			Logger.getInstance().log("MapGraphBelastungstest","Zeit fuer Koordinaten (sek): "+timekoord/1000);
			Logger.getInstance().log("MapGraphBelastungstest","Zeit fuer Nodes (sek): "+timenode/1000);
			Logger.getInstance().log("MapGraphBelastungstest","Zeit fuer Edge (sek): "+timeedge/1000);
			Logger.getInstance().log("MapGraphBelastungstest","Zeit fuer Graphen (sek): "+timegraph/1000);
			
			//Aufraeumen
			koords = null;
			graph = null;
			Runtime.getRuntime().gc();
		} catch (OutOfMemoryError e) {
			fail("Fehler - zu viel Speicherverbrauch - i: "+i+" Tipps: setze -Xms, -Xmx. Versuche verschiedene Bit-Versionen fuer Java/Eclipse");
		}
	}

}
