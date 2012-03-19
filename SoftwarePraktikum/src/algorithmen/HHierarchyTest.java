/**
 * 
 */
package algorithmen;

import static org.junit.Assert.fail;
import graphenbib.HierarchyMapEdge;
import graphenbib.HierarchyMapGraph;
import graphenbib.HierarchyMapNode;
import graphenbib.StreetType;

import java.util.Iterator;

import main.Config;
import main.Logger;

import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 */
public class HHierarchyTest {

	private static void weg(HierarchyMapGraph graph, int start, int ende, int ID, int l, byte level) {
		graph.insertEdge(start, ende, ID, l, StreetType.MOTORWAY, level);
		graph.insertEdge(ende, start, ID, l, StreetType.MOTORWAY, level);
	}
	
	private void weg(HierarchyMapGraph graph, int start, int ende, int wayID, int laenge) throws Exception {
		graph.insertEdge(start, ende, wayID, laenge, StreetType.LIVING_STREET,(byte)1);
		graph.insertEdge(ende, start, wayID, laenge, StreetType.LIVING_STREET,(byte)1);
	}
	
	private HierarchyMapGraph GitterGraph(int n) throws Exception {
		HierarchyMapGraph graph = null;
		try {
			graph = new HierarchyMapGraph();
		} catch (Exception e) {
			fail("Fehler beim Gitter: "+e.getLocalizedMessage());
		}
		
		//Erstelle Knoten
		for (int j= 1; j<n*n+1;j++)
			graph.insertNode(j, 1,1);
		
		//kurze kanten
		int pos = 2+n;
		int i = 3;
		weg(graph,1,2,(n*n)+1,1);
		weg(graph,2,2+n,(n*n)+2,1);
		while (pos<(n*n)-n) {
			weg(graph,pos,pos+1,(n*n)+i++,1);
			weg(graph,pos+1,pos+n+1,(n*n)+i++,1);
			pos = pos+n+1;
		}
		
		//alle kanten
		i =1;
		for (i=1; i<n*n;i++) {
			if ((i % n) > 0) {
				weg(graph,i,i+1,i,10);
				if (i <= (n*n) -n) {
					weg(graph,i+1,i+1+n,i,10);
				}
			}
				
		}
		
		
		return graph;
	}
	
	private static Logger logger = Logger.getInstance();
	
	@SuppressWarnings("unused")
	private void vergleichstest(int n,boolean progress) {
		int gr = n;
		
		
		//logger.log("HHierarchyMTTest","Alle Tests bestanden - Besser is das");
		
		int[] levels;
		Iterator<HierarchyMapNode> iterator;
		long timeN=0,timeMT = 0;
		HierarchyMapGraph test = null;
		test = null;
		long start,ende;
		
		

		
		//alte Variante
		if (progress)
			logger.log("HHierarchyMTTest","Erstelle Gitter mit "+(gr*gr)+" Knoten");
		try {
			 test = GitterGraph(gr);
		} catch (Exception e) {
			fail("Der GitterGraph konte nciht erstellt wrden: "+e.getLocalizedMessage());
		}
		if (progress)
			logger.log("HHierarchyMTTest","Teste nun die cmputeH. (setze H auf 50)");
		Config.H = 50;
		start = System.currentTimeMillis();
		//start = System.nanoTime();
		try {
			//HHierarchyMT.computeHierarchyST(test, (byte)2);
			HHierarchyMT.buildHierarchyGraphST(test);
		} catch (Exception e) {
			fail("Auf dem GitterGraph ist der computeH gescheitert: "+e.getLocalizedMessage());
		}
		ende = System.currentTimeMillis();
		//ende = System.nanoTime();
		
		if (progress)
			logger.log("HHierarchyMTTest","Algo ist terminiert - Laufzeit: "+(ende-start)+"ms");
		
		timeN = (ende-start);
		
      if (progress) {
		iterator = test.getNodeIt();
		levels = new int[Config.maxHierarchyLayer+1];
		while( iterator.hasNext()){
			HierarchyMapNode node = iterator.next();
			levels[node.getLevel()]++;
		}
		for( int i= 0; i< levels.length;i++){
			System.out.println("Knoten auf Level "+i+": "+levels[i]);
		}
	  }
		
		//MT-Version
		if (progress)
			logger.log("HHierarchyMTTest","Erstelle Gitter mit "+(gr*gr)+" Knoten");
		try {
			 test = GitterGraph(gr);
		} catch (Exception e) {
			fail("Der GitterGraph konte nciht erstellt wrden: "+e.getLocalizedMessage());
		}
		if (progress)
			logger.log("HHierarchyMTTest","Teste nun die cmputeH. (setze H auf 50) - MT");
		Config.H = 50;
		start = System.currentTimeMillis();
		//start = System.nanoTime();
		try {
			//HHierarchyMT.computeHierarchy(test, (byte)2);
			HHierarchyMT.buildHierarchyGraph(test);
		} catch (Exception e) {
			fail("Auf dem GitterGraph ist der computeH gescheitert: "+e.getLocalizedMessage());
		}
		ende = System.currentTimeMillis();
		//ende = System.nanoTime();
		
		if (progress)
			logger.log("HHierarchyMTTest","Algo ist terminiert - Laufzeit: "+(ende-start)+"ms");
		
		timeMT = ende-start;
		
		
		if (progress) {
			iterator = test.getNodeIt();
			levels = new int[Config.maxHierarchyLayer+1];
			while( iterator.hasNext()){
				HierarchyMapNode node = iterator.next();
				levels[node.getLevel()]++;
			}
		
			for( int i= 0; i< levels.length;i++){
				System.out.println("Knoten auf Level "+i+": "+levels[i]);
			}
		}
		
		
      int diff = (int) (timeN-timeMT);
		
	  if (progress)
			logger.log("HHierarchyMTTest", "Vergleich: "+(timeN-timeMT));
		
      int t = (int) timeN;
      //System.out.println(timeN +" == "+t);
		
      if (progress) {
		
		System.out.println("t = "+(double)t);
		System.out.println("diff = "+(double)diff);
		System.out.println("diff/t = "+((double)diff/(double)t));
      }
		double ratio = ((double)diff / (double)t)*100;
		
		//logger.log("HHierarchyTestMT", "Vergleichstest(n = "+n+" ): Gewinn: "+ratio+" Prozent");
		System.out.println(Math.round(ratio));
		
		//Runtime.getRuntime().gc();
	}
	
//	/**
//	 * Baut ein Gitter mit n^2 Knoten und berechnet das Level 2 darauf. Einmal mit klassichen HHierarchy und einmal mit der COPY-Version und vergleicht die Ergebnisse
//	 * @param n Groesse des Gitters
//	 * @param progress Sollen Debugging-Ausgaben erscheinen
//	 * @return true, wenn geshuffelte Daten das gleiche Ergebnis wie nrmale Reihenlge liefern, sonst false
//	 */
//	private boolean vergleichstestCOPY(int n,boolean progress) {
//		int gr = n;
//		
//		int level2soll = 0;
//		
//		//logger.log("CopyOfHHierarchyTest","Alle Tests bestanden - Besser is das");
//		
//		int[] levels = null;
//		Iterator<HierarchyMapNode> iterator;
//		long timeN=0,timeMT = 0;
//		HierarchyMapGraph test = null;
//		test = null;
//		long start,ende;
//		
//		
//
//		
//		//alte Variante
//		if (progress)
//			logger.log("CopyOfHHierarchyTest","Erstelle Gitter mit "+(gr*gr)+" Knoten");
//		try {
//			 //test = randomGraph(1000000,0.7f,5);
//			test = GitterGraph(gr);
//		} catch (Exception e) {
//			fail("Der GitterGraph konte nciht erstellt wrden: "+e.getLocalizedMessage());
//		}
//		if (progress)
//			logger.log("CopyOfHHierarchyTest","Teste nun die cmputeH. (setze H auf 50)");
//		Constants.H = 50;
//		start = System.currentTimeMillis();
//		//start = System.nanoTime();
//		try {
//			HHierarchyMT.computeHierarchyST(test, (byte)2);
//		} catch (Exception e) {
//			fail("Auf dem GitterGraph ist der computeH gescheitert: "+e.getLocalizedMessage());
//		}
//		ende = System.currentTimeMillis();
//		//ende = System.nanoTime();
//		
//		if (progress)
//			logger.log("CopyOfHHierarchyTest","Algo ist terminiert - Laufzeit: "+(ende-start)+"ms");
//		
//		timeN = (ende-start);
//		
//      if (progress) {
//		iterator = test.getNodeIt();
//		levels = new int[Constants.maxHierarchyLayer+1];
//		while( iterator.hasNext()){
//			HierarchyMapNode node = iterator.next();
//			levels[node.getLevel()]++;
//		}
//		for( int i= 0; i< levels.length;i++){
//			System.out.println("Knoten auf Level "+i+": "+levels[i]);
//		}
//	  }
//		level2soll = levels[2];
//		//COPY-Version
//		if (progress)
//			logger.log("CopyOfHHierarchyTest","Erstelle Gitter mit "+(gr*gr)+" Knoten");
//		try {
//			 //test = randomGraph(1000000,0.7f,5);
//			test = GitterGraph(gr);
//		} catch (Exception e) {
//			fail("Der GitterGraph konte nciht erstellt wrden: "+e.getLocalizedMessage());
//		}
//		if (progress)
//			logger.log("CopyOfHHierarchyTest","Teste nun die cmputeH. (setze H auf 50) - COPY");
//		Constants.H = 50;
//		start = System.currentTimeMillis();
//		//start = System.nanoTime();
//		try {
//			CopyOfHHierarchyMT.computeHierarchyST(test, (byte)2);
//		} catch (Exception e) {
//			fail("Auf dem GitterGraph ist der computeH gescheitert: "+e.getLocalizedMessage());
//		}
//		ende = System.currentTimeMillis();
//		//ende = System.nanoTime();
//		
//		if (progress)
//			logger.log("CopyOfHHierarchyTest","Algo ist terminiert - Laufzeit: "+(ende-start)+"ms");
//		
//		timeMT = ende-start;
//		
//		
//		if (progress) {
//			iterator = test.getNodeIt();
//			levels = new int[Constants.maxHierarchyLayer+1];
//			while( iterator.hasNext()){
//				HierarchyMapNode node = iterator.next();
//				levels[node.getLevel()]++;
//			}
//		
//			for( int i= 0; i< levels.length;i++){
//				System.out.println("Knoten auf Level "+i+": "+levels[i]);
//			}
//		}
//		return (levels[2] == level2soll);
//	}
	
	
	private static HierarchyMapGraph test1 = null;
	private static HierarchyMapGraph test2 = null;
	private static HierarchyMapGraph test3 = null;
	private static HierarchyMapGraph test4 = null;
	private static HierarchyMapGraph test5 = null;
	private static HierarchyMapGraph test6 = null;
	private static HierarchyMapGraph test7 = null;
	private static HierarchyMapGraph test8 = null;
	private static HierarchyMapGraph test9 = null;
	private static HierarchyMapGraph test10 = null;
	private static HierarchyMapGraph test11 = null;
	
	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		int i = 1;
		
		//TesstGraph 1 - 5 Knoten mit 4 Kanten verbunden
		//Knoten
		test1 = new HierarchyMapGraph();
		for(i=1;i<5;i++)
			test1.insertNode(i, 1, 1);
		test1.insertNode(1001, 1, 1);
		//Kanten
		weg(test1,1,2,1,1,(byte)1);
		weg(test1,2,3,2,1,(byte)1);
		weg(test1,3,4,3,1,(byte)1);
		weg(test1,4,1001,4,1,(byte)1);
		
		
		
		//TesstGraph 2 - 6 Knoten mit 5 Kanten verbunden
		//Knoten
		test2 = new HierarchyMapGraph();
		for(i=1;i<6;i++)
			test2.insertNode(i, 1, 1);
		test2.insertNode(1001, 1, 1);
		//Kanten
		i=1;
		weg(test2,1,2,i++,1,(byte)1);
		weg(test2,2,3,i++,1,(byte)1);
		weg(test2,3,4,i++,1,(byte)1);
		weg(test2,4,5,i++,1,(byte)1);
		weg(test2,5,1001,i++,1,(byte)1);
		
		
		
		//TesstGraph 3 - 7 Knoten mit 6 Kanten verbunden
		//Knoten
		test3 = new HierarchyMapGraph();
		for(i=1;i<7;i++)
			test3.insertNode(i, 1, 1);
		test3.insertNode(1001, 1, 1);
		//Kanten
		i=1;
		weg(test3,1,2,i++,1,(byte)1);
		weg(test3,2,3,i++,1,(byte)1);
		weg(test3,3,4,i++,1,(byte)1);
		weg(test3,4,5,i++,1,(byte)1);
		weg(test3,5,6,i++,1,(byte)1);
		weg(test3,6,1001,i++,1,(byte)1);
		
		
		
		//TesstGraph 4 - 10 Knoten mit 9 Kanten verbunden
		//Knoten
		test4 = new HierarchyMapGraph();
		for(i=1;i<10;i++)
			test4.insertNode(i, 1, 1);
		test4.insertNode(1001, 1, 1);
		//Kanten
		i=1;
		weg(test4,1,2,i++,1,(byte)1);
		weg(test4,1,3,i++,1,(byte)1);
		weg(test4,1,4,i++,1,(byte)1);
		weg(test4,1,5,i++,5,(byte)1);
		weg(test4,5,6,i++,10,(byte)1);
		weg(test4,6,1001,i++,5,(byte)1);
		weg(test4,7,1001,i++,1,(byte)1);
		weg(test4,8,1001,i++,1,(byte)1);
		weg(test4,9,1001,i++,1,(byte)1);
		
		
		
		//TesstGraph 5 - 6 Knoten mit 5 ger. Kanten verbunden
		//Knoten
		test5 = new HierarchyMapGraph();
		for(i=1;i<6;i++)
			test5.insertNode(i, 1, 1);
		test5.insertNode(1001, 1, 1);
		//Kanten
		i=1;
		test5.insertEdge(1, 2, i++, 1, StreetType.ROAD, (byte) 1);
		test5.insertEdge(2, 3, i++, 1, StreetType.ROAD, (byte) 1);
		test5.insertEdge(3, 4, i++, 1, StreetType.ROAD, (byte) 1);
		test5.insertEdge(4, 5, i++, 1, StreetType.ROAD, (byte) 1);
		test5.insertEdge(5, 1001, i++, 1, StreetType.ROAD, (byte) 1);
		
		
		
		//TesstGraph 6 - 10 Knoten mit 10 Kanten verbunden
		//Knoten
		test6 = new HierarchyMapGraph();
		for(i=1;i<10;i++)
			test6.insertNode(i, 1, 1);
		test6.insertNode(1001, 1, 1);
		//Kanten
		i=1;
		weg(test6,1,4,i++,5,(byte)1);
		weg(test6,1,7,i++,3,(byte)1);
		weg(test6,1,5,i++,12,(byte)1);
		weg(test6,4,2,i++,3,(byte)1);
		weg(test6,2,5,i++,5,(byte)1);
		weg(test6,5,9,i++,3,(byte)1);
		weg(test6,5,3,i++,3,(byte)1);
		weg(test6,3,1001,i++,4,(byte)1);
		weg(test6,9,8,i++,7,(byte)1);
		weg(test6,8,1001,i++,7,(byte)1);
		
		
		
		//TesstGraph 7 - 10 Knoten mit 10 Kanten verbunden (wie Test6)
		//Knoten
		test7 = new HierarchyMapGraph();
		for(i=1;i<10;i++)
			test7.insertNode(i, 1, 1);
		test7.insertNode(1001, 1, 1);
		//Kanten
		i=1;
		weg(test7,1,4,i++,5,(byte)1);
		weg(test7,1,7,i++,3,(byte)1);
		weg(test7,1,5,i++,12,(byte)1);
		weg(test7,4,2,i++,3,(byte)1);
		weg(test7,2,5,i++,5,(byte)1);
		weg(test7,5,9,i++,3,(byte)1);
		weg(test7,5,3,i++,3,(byte)1);
		weg(test7,3,1001,i++,4,(byte)1);
		weg(test7,9,8,i++,7,(byte)1);
		weg(test7,8,1001,i++,7,(byte)1);
		
		
		
		//TesstGraph 8 - 10 Knoten mit 10 Kanten verbunden (wie Test6)
		//Knoten
		test8 = new HierarchyMapGraph();
		for(i=1;i<10;i++)
			test8.insertNode(i, 1, 1);
		test8.insertNode(1001, 1, 1);
		//Kanten
		i=1;
		weg(test8,1,4,i++,5,(byte)1);
		weg(test8,1,7,i++,3,(byte)1);
		weg(test8,1,5,i++,12,(byte)1);
		weg(test8,4,2,i++,3,(byte)1);
		weg(test8,2,5,i++,5,(byte)1);
		weg(test8,5,9,i++,3,(byte)1);
		weg(test8,5,3,i++,3,(byte)1);
		weg(test8,3,1001,i++,4,(byte)1);
		weg(test8,9,8,i++,7,(byte)1);
		weg(test8,8,1001,i++,7,(byte)1);
		
		
		
		//TesstGraph 9 - 10 Knoten mit 10 Kanten verbunden (wie Test6, aber kante 7 hat anderes Gewicht)
		//Knoten
		test9 = new HierarchyMapGraph();
		for(i=1;i<10;i++)
			test9.insertNode(i, 1, 1);
		test9.insertNode(1001, 1, 1);
		//Kanten
		i=1;
		weg(test9,1,4,i++,5,(byte)1);
		weg(test9,1,7,i++,3,(byte)1);
		weg(test9,1,5,i++,12,(byte)1);
		weg(test9,4,2,i++,3,(byte)1);
		weg(test9,2,5,i++,5,(byte)1);
		weg(test9,5,9,i++,3,(byte)1);
		//weg(test9,5,3,i++,3.1f,(byte)1);
		weg(test9,5,3,i++,4,(byte)1);
		weg(test9,3,1001,i++,4,(byte)1);
		weg(test9,9,8,i++,7,(byte)1);
		weg(test9,8,1001,i++,7,(byte)1);
		
		
		
		//TesstGraph 10 - 5 Knoten mit 8 Kanten verbunden
		//Knoten
		test10 = new HierarchyMapGraph();
		for(i=1;i<5;i++)
			test10.insertNode(i, 1, 1);
		test10.insertNode(1001, 1, 1);
		//Kanten
		i=1;
		weg(test10,1,2,i++,1,(byte)1);
		weg(test10,2,3,i++,1,(byte)1);
		weg(test10,3,4,i++,1,(byte)1);
		weg(test10,4,1001,i++,1,(byte)1);
		weg(test10,1,3,i++,100,(byte)1);
		weg(test10,2,4,i++,100,(byte)1);
		weg(test10,3,1001,i++,100,(byte)1);
		weg(test10,1,1001,i++,1000,(byte)1);
		
		
		
		//TesstGraph 11 - 5 Knoten mit 8 Kanten verbunden (wie test10)
		//Knoten
		test11 = new HierarchyMapGraph();
		for(i=1;i<5;i++)
			test11.insertNode(i, 1, 1);
		test11.insertNode(1001, 1, 1);
		//Kanten
		i=1;
		weg(test11,1,2,i++,1,(byte)1);
		weg(test11,2,3,i++,1,(byte)1);
		weg(test11,3,4,i++,1,(byte)1);
		weg(test11,4,1001,i++,1,(byte)1);
		weg(test11,1,3,i++,100,(byte)1);
		weg(test11,2,4,i++,100,(byte)1);
		weg(test11,3,1001,i++,100,(byte)1);
		weg(test11,1,1001,i++,1000,(byte)1);
	}

	@Test
	public void test() {
		boolean[] korrekt = new boolean[10];
		HierarchyMapEdge kante;
		
		logger.log("HHierarchyTest","Starte Tests:");
		
		
		/*
		 * Test 1 - mit H=2 sllte hier keine HH rauskommen
		 */
		Config.H=2;
		try {
			HHierarchyMT.computeHierarchyST(test1, (byte)2);
		} catch (Exception e) {
			fail("Es gab einen ehler beim ausfuehren der comuteH.: "+e.getLocalizedMessage());
		}
		//Teste nun korrekte Menge der Wege auf Level 2
		for(int i = 1;i<5;i++)
			korrekt[i] = test1.getNode(i).getIncomingEdgesByHierarchy((byte)2).isEmpty();
		korrekt[0] = test1.getNode(1001).getIncomingEdgesByHierarchy((byte)2).isEmpty();
			
		for(int i=1;i<5;i++)
			if(korrekt[i] == false)
				fail("Test 1: Es sollten keine Edges auf Level 2 exisiteren - aber bei Knoten "+i+" gibt es "+test1.getNode(i).getIncomingEdgesByHierarchy((byte)2).size());
		
		if(korrekt[0] == false)
			fail("Test 1: Knoten 1001 (t)  sollte auf Level 2 keine Kanten haben. Hat aber:  "+test1.getNode(1001).getIncomingEdgesByHierarchy((byte)2).size());
			
		logger.log("HHierarchyTest","Test 1 bestanden");
		
		/*
		 * Test 2 - mit H=2 sllte hier genau eine HH rauskommen
		 */
		Config.H = 2;
		try {
			HHierarchyMT.computeHierarchyST(test2, (byte)2);
		} catch (Exception e) {
			fail("Es gab einen ehler beim ausfuehren der comuteH.: "+e.getLocalizedMessage());
		}
		//Teste nun korrekte Menge der Wege auf Level 2 - leere Mengen
		korrekt[0] = test2.getNode(1001).getIncomingEdgesByHierarchy((byte)2).isEmpty();
		korrekt[1] = test2.getNode(1).getIncomingEdgesByHierarchy((byte)2).isEmpty();
		korrekt[2] = test2.getNode(2).getIncomingEdgesByHierarchy((byte)2).isEmpty();
		korrekt[5] = test2.getNode(5).getIncomingEdgesByHierarchy((byte)2).isEmpty();
		
		//Jeweils eine Kante sollte hier sein - und zwar die Kante 3
		korrekt[3] = test2.getNode(3).getIncomingEdgesByHierarchy((byte)2).size()==1;
		korrekt[4] = test2.getNode(4).getIncomingEdgesByHierarchy((byte)2).size()==1;
		
		for(int i=1;i<5;i++)
			if(korrekt[i] == false)
				fail("Test 2: Knoten "+i+" hat soviele Eingehende Kanten auf Level 2: "+test2.getNode(i).getIncomingEdgesByHierarchy((byte)2).size());
		
		if(korrekt[0] == false)
			fail("Test 2: Knoten 1001 (t)  sollte auf Level 2 keine Kanten haben. Hat aber:  "+test2.getNode(1001).getIncomingEdgesByHierarchy((byte)2).size());
		
		kante = (test2.getNode(3).getIncomingEdgesByHierarchy((byte)2).get(0));
		if (kante.getUID() != 3)
			fail("Test 2: Die kante, die auf Level 2 exisitert hat die falsche ID (ab Knoten 3) - soll:3 Ist: "+kante.getUID());
		
		kante = (test2.getNode(4).getIncomingEdgesByHierarchy((byte)2).get(0));
		if (kante.getUID() != 3)
			fail("Test 2: Die kante, die auf Level 2 exisitert hat die falsche ID (ab Knoten 4) - soll:3 Ist: "+kante.getUID());
		
		logger.log("HHierarchyTest","Test 2 bestanden");
		
		/*
		 * Test 3 - mit H=2 sllte hier genau zwei HH rauskommen
		 */
		Config.H = 2;
		try {
			HHierarchyMT.computeHierarchyST(test3, (byte)2);
		} catch (Exception e) {
			fail("Es gab einen ehler beim ausfuehren der comuteH.: "+e.getLocalizedMessage());
		}
		//Teste nun korrekte Menge der Wege auf Level 2 - leere Mengen
		korrekt[0] = test3.getNode(1001).getIncomingEdgesByHierarchy((byte)2).isEmpty();
		korrekt[1] = test3.getNode(1).getIncomingEdgesByHierarchy((byte)2).isEmpty();
		korrekt[2] = test3.getNode(2).getIncomingEdgesByHierarchy((byte)2).isEmpty();
		korrekt[6] = test3.getNode(6).getIncomingEdgesByHierarchy((byte)2).isEmpty();
		//Hier sollen Kanten sein
		korrekt[3] = test3.getNode(3).getIncomingEdgesByHierarchy((byte)2).size()==1;
		korrekt[4] = test3.getNode(4).getIncomingEdgesByHierarchy((byte)2).size()==2;
		korrekt[5] = test3.getNode(5).getIncomingEdgesByHierarchy((byte)2).size()==1;
		
		for(int i=1;i<7;i++)
			if(korrekt[i] == false)
				fail("Test 3: Knoten "+i+" hat soviele Eingehende Kanten auf Level 2: "+test3.getNode(i).getIncomingEdgesByHierarchy((byte)2).size());
		
		if(korrekt[0] == false)
			fail("Test 3: Knoten 1001 (t)  sollte auf Level 2 keine Kanten haben. Hat aber:  "+test3.getNode(1001).getIncomingEdgesByHierarchy((byte)2).size());
		
		kante = (test3.getNode(3).getIncomingEdgesByHierarchy((byte)2).get(0));
		if (kante.getUID() != 3)
			fail("Test 3: Die kante, die auf Level 2 exisitert hat die falsche ID (ab Knoten 3) - soll:3 Ist: "+kante.getUID());
		
		kante = (test3.getNode(5).getIncomingEdgesByHierarchy((byte)2).get(0));
		if (kante.getUID() != 4)
			fail("Test 3: Die kante, die auf Level 2 exisitert hat die falsche ID (ab Knoten 5) - soll:4 Ist: "+kante.getUID());
		
		logger.log("HHierarchyTest","Test 3 bestanden");
		
		/*
		 * Test 4 - mit H=4 sllte hier genau eine HH rauskommen
		 */
		Config.H = 4;
		try {
			HHierarchyMT.computeHierarchyST(test4, (byte)2);
		} catch (Exception e) {
			fail("Es gab einen ehler beim ausfuehren der comuteH.: "+e.getLocalizedMessage());
		}
		//Teste nun korrekte Menge der Wege auf Level 2 - leere Mengen
		korrekt[0] = test4.getNode(1001).getIncomingEdgesByHierarchy((byte)2).isEmpty();
		korrekt[1] = test4.getNode(1).getIncomingEdgesByHierarchy((byte)2).isEmpty();
		korrekt[2] = test4.getNode(2).getIncomingEdgesByHierarchy((byte)2).isEmpty();
		korrekt[3] = test4.getNode(3).getIncomingEdgesByHierarchy((byte)2).isEmpty();
		korrekt[4] = test4.getNode(4).getIncomingEdgesByHierarchy((byte)2).isEmpty();
		korrekt[7] = test4.getNode(7).getIncomingEdgesByHierarchy((byte)2).isEmpty();
		korrekt[8] = test4.getNode(8).getIncomingEdgesByHierarchy((byte)2).isEmpty();
		korrekt[9] = test4.getNode(9).getIncomingEdgesByHierarchy((byte)2).isEmpty();
		//Hier sollen kantensein
		korrekt[5] = test4.getNode(5).getIncomingEdgesByHierarchy((byte)2).size()==1;
		korrekt[6] = test4.getNode(6).getIncomingEdgesByHierarchy((byte)2).size()==1;
		
		for(int i=1;i<10;i++)
			if(korrekt[i] == false)
				fail("Test 4: Knoten "+i+" hat soviele Eingehende Kanten auf Level 2: "+test4.getNode(i).getIncomingEdgesByHierarchy((byte)2).size());
		
		if(korrekt[0] == false)
			fail("Test 4: Knoten 1001 (t)  sollte auf Level 2 keine Kanten haben. Hat aber:  "+test4.getNode(1001).getIncomingEdgesByHierarchy((byte)2).size());
		
		kante = (test4.getNode(5).getIncomingEdgesByHierarchy((byte)2).get(0));
		if (kante.getUID() != 5)
			fail("Test 4: Die kante, die auf Level 2 exisitert hat die falsche ID (ab Knoten 5) - soll:5 Ist: "+kante.getUID());
		
		kante = (test4.getNode(6).getIncomingEdgesByHierarchy((byte)2).get(0));
		if (kante.getUID() != 5)
			fail("Test 4: Die kante, die auf Level 2 exisitert hat die falsche ID (ab Knoten 6) - soll:5 Ist: "+kante.getUID());
		
		logger.log("HHierarchyTest","Test 4 bestanden");
		
		/*
		 * Test 5 - mit H=2 sllte hier genau eine HH rauskommen
		 */
		Config.H = 2;
		try {
			HHierarchyMT.computeHierarchyST(test5, (byte)2);
		} catch (Exception e) {
			fail("Es gab einen ehler beim ausfuehren der comuteH.: "+e.getLocalizedMessage());
		}
		//Teste nun korrekte Menge der Wege auf Level 2 - leere Mengen
		korrekt[0] = test5.getNode(1001).getIncomingEdgesByHierarchy((byte)2).isEmpty();
		korrekt[1] = test5.getNode(1).getIncomingEdgesByHierarchy((byte)2).isEmpty();
		korrekt[2] = test5.getNode(2).getIncomingEdgesByHierarchy((byte)2).isEmpty();
		korrekt[3] = test5.getNode(3).getIncomingEdgesByHierarchy((byte)2).isEmpty();
		korrekt[5] = test5.getNode(5).getIncomingEdgesByHierarchy((byte)2).isEmpty();
		//Hier sollen Kanten sein
		korrekt[4] = test5.getNode(4).getIncomingEdgesByHierarchy((byte)2).size()==1;
		
		for(int i=1;i<6;i++)
			if(korrekt[i] == false)
				fail("Test 5: Knoten "+i+" hat soviele Eingehende Kanten auf Level 2: "+test5.getNode(i).getIncomingEdgesByHierarchy((byte)2).size());
		
		if(korrekt[0] == false)
			fail("Test 5: Knoten 1001 (t)  sollte auf Level 2 keine Kanten haben. Hat aber:  "+test5.getNode(1001).getIncomingEdgesByHierarchy((byte)2).size());
		
		kante = (test5.getNode(4).getIncomingEdgesByHierarchy((byte)2).get(0));
		if (kante.getUID() != 3)
			fail("Test 5: Die kante, die auf Level 2 exisitert hat die falsche ID (ab Knoten 4) - soll:3 Ist: "+kante.getUID());
		
		logger.log("HHierarchyTest","Test 5 bestanden");
		
		/*
		 * Test 6 - mit H=2 sllte hier genau 3 HH rauskommen
		 */
		Config.H = 2;
		try {
			HHierarchyMT.computeHierarchyST(test6, (byte)2);
		} catch (Exception e) {
			fail("Es gab einen ehler beim ausfuehren der comuteH.: "+e.getLocalizedMessage());
		}
		//Teste nun korrekte Menge der Wege auf Level 2 - leere Mengen
		korrekt[0] = test6.getNode(1001).getIncomingEdgesByHierarchy((byte)2).isEmpty();
		korrekt[4] = test6.getNode(4).getIncomingEdgesByHierarchy((byte)2).isEmpty();
		korrekt[6] = test6.getNode(6).getIncomingEdgesByHierarchy((byte)2).isEmpty();
		korrekt[7] = test6.getNode(7).getIncomingEdgesByHierarchy((byte)2).isEmpty();
		korrekt[8] = test6.getNode(8).getIncomingEdgesByHierarchy((byte)2).isEmpty();
		//Hier sollen kanten sein		
		korrekt[1] = test6.getNode(1).getIncomingEdgesByHierarchy((byte)2).size()==1;
		korrekt[2] = test6.getNode(2).getIncomingEdgesByHierarchy((byte)2).size()==1;
		korrekt[3] = test6.getNode(3).getIncomingEdgesByHierarchy((byte)2).size()==0;
		korrekt[5] = test6.getNode(5).getIncomingEdgesByHierarchy((byte)2).size()==3;
		korrekt[9] = test6.getNode(9).getIncomingEdgesByHierarchy((byte)2).size()==1;	
		
		for(int i=1;i<10;i++)
			if(korrekt[i] == false)
				fail("Test 6: Knoten "+i+" hat soviele Eingehende Kanten auf Level 2: "+test6.getNode(i).getIncomingEdgesByHierarchy((byte)2).size());
		
		if(korrekt[0] == false)
			fail("Test 6: Knoten 1001 (t)  sollte auf Level 2 keine Kanten haben. Hat aber:  "+test6.getNode(1001).getIncomingEdgesByHierarchy((byte)2).size());
		
		//Es sollten genau drei der Kanten sein, die an Knoten 5 haengen
		kante = (test6.getNode(5).getIncomingEdgesByHierarchy((byte)2).get(0));
		if (!((kante.getUID() == 3) || (kante.getUID() == 5) || (kante.getUID() == 6)))
			fail("Test 6: Es gibt eine Kante auf Level 2, die es nciht geben duerfte: "+kante.getUID());
		
		kante = (test6.getNode(5).getIncomingEdgesByHierarchy((byte)2).get(1));
		if (!((kante.getUID() == 3) || (kante.getUID() == 5) || (kante.getUID() == 6)))
			fail("Test 6: Es gibt eine Kante auf Level 2, die es nciht geben duerfte: "+kante.getUID());
		
		kante = (test6.getNode(5).getIncomingEdgesByHierarchy((byte)2).get(2));
		if (!((kante.getUID() == 3) || (kante.getUID() == 5) || (kante.getUID() == 6)))
			fail("Test 6: Es gibt eine Kante auf Level 2, die es nciht geben duerfte: "+kante.getUID());
		
		
		logger.log("HHierarchyTest","Test 6 bestanden");
		
		/*
		 * Test 7 - mit H=3 sllte hier genau 1 HH rauskommen
		 */
		Config.H = 3;
		try {
			HHierarchyMT.computeHierarchyST(test7, (byte)2);
		} catch (Exception e) {
			fail("Es gab einen ehler beim ausfuehren der comuteH.: "+e.getLocalizedMessage());
		}
		//Teste nun korrekte Menge der Wege auf Level 2 - leere Mengen
		korrekt[0] = test7.getNode(1001).getIncomingEdgesByHierarchy((byte)2).isEmpty();
		korrekt[2] = test7.getNode(2).getIncomingEdgesByHierarchy((byte)2).isEmpty();
		korrekt[3] = test7.getNode(3).getIncomingEdgesByHierarchy((byte)2).isEmpty();
		korrekt[4] = test7.getNode(4).getIncomingEdgesByHierarchy((byte)2).isEmpty();
		korrekt[6] = test7.getNode(6).getIncomingEdgesByHierarchy((byte)2).isEmpty();
		korrekt[7] = test7.getNode(7).getIncomingEdgesByHierarchy((byte)2).isEmpty();
		korrekt[8] = test7.getNode(8).getIncomingEdgesByHierarchy((byte)2).isEmpty();
		korrekt[9] = test7.getNode(9).getIncomingEdgesByHierarchy((byte)2).isEmpty();
		//Hier sollen kanten sein		
		korrekt[1] = test7.getNode(1).getIncomingEdgesByHierarchy((byte)2).size()==1;
		korrekt[5] = test7.getNode(5).getIncomingEdgesByHierarchy((byte)2).size()==1;	
		
		for(int i=1;i<10;i++)
			if(korrekt[i] == false)
				fail("Test 7: Knoten "+i+" hat soviele Eingehende Kanten auf Level 2: "+test7.getNode(i).getIncomingEdgesByHierarchy((byte)2).size());
		
		if(korrekt[0] == false)
			fail("Test 7: Knoten 1001 (t)  sollte auf Level 2 keine Kanten haben. Hat aber:  "+test7.getNode(1001).getIncomingEdgesByHierarchy((byte)2).size());
		
		//es sollte nur Kante 3 exisiteren
		kante = (test7.getNode(1).getIncomingEdgesByHierarchy((byte)2).get(0));
		if (kante.getUID() != 3)
			fail("Test 7: Die kante, die auf Level 2 exisitert hat die falsche ID (ab Knoten 1) - soll:3 Ist: "+kante.getUID());
		
		kante = (test7.getNode(5).getIncomingEdgesByHierarchy((byte)2).get(0));
		if (kante.getUID() != 3)
			fail("Test 7: Die kante, die auf Level 2 exisitert hat die falsche ID (ab Knoten 5) - soll:3 Ist: "+kante.getUID());
		
		logger.log("HHierarchyTest","Test 7 bestanden");
		
		/*
		 * Test 8 - mit H=4 sllte hier genau keine HH rauskommen
		 */
		Config.H = 4;
		try {
			HHierarchyMT.computeHierarchyST(test8, (byte)2);
		} catch (Exception e) {
			fail("Es gab einen ehler beim ausfuehren der comuteH.: "+e.getLocalizedMessage());
		}
		//Teste nun korrekte Menge der Wege auf Level 2 - leere Mengen
		korrekt[0] = test8.getNode(1001).getIncomingEdgesByHierarchy((byte)2).isEmpty();
		korrekt[1] = test8.getNode(1).getIncomingEdgesByHierarchy((byte)2).isEmpty();
		korrekt[2] = test8.getNode(2).getIncomingEdgesByHierarchy((byte)2).isEmpty();
		korrekt[3] = test8.getNode(3).getIncomingEdgesByHierarchy((byte)2).isEmpty();
		korrekt[4] = test8.getNode(4).getIncomingEdgesByHierarchy((byte)2).isEmpty();
		korrekt[5] = test8.getNode(5).getIncomingEdgesByHierarchy((byte)2).isEmpty();
		korrekt[6] = test8.getNode(6).getIncomingEdgesByHierarchy((byte)2).isEmpty();
		korrekt[7] = test8.getNode(7).getIncomingEdgesByHierarchy((byte)2).isEmpty();
		korrekt[8] = test8.getNode(8).getIncomingEdgesByHierarchy((byte)2).isEmpty();
		korrekt[9] = test8.getNode(9).getIncomingEdgesByHierarchy((byte)2).isEmpty();	
		
		for(int i=1;i<10;i++)
			if(korrekt[i] == false)
				fail("Test 8: Knoten "+i+" hat soviele Eingehende Kanten auf Level 2 (0 erwartet): "+test8.getNode(i).getIncomingEdgesByHierarchy((byte)2).size());
		
		if(korrekt[0] == false)
			fail("Test 8: Knoten 1001 (t)  sollte auf Level 2 keine Kanten haben. Hat aber:  "+test8.getNode(1001).getIncomingEdgesByHierarchy((byte)2).size());
		
		logger.log("HHierarchyTest","Test 8 bestanden");
		
		/*
		 * Test 9 - mit H=2 sllte hier genau 4 HH rauskommen
		 */
		Config.H = 2;
		try {
			HHierarchyMT.computeHierarchyST(test9, (byte)2);
		} catch (Exception e) {
			fail("Es gab einen ehler beim ausfuehren der comuteH.: "+e.getLocalizedMessage());
		}
		//Teste nun korrekte Menge der Wege auf Level 2 - leere Mengen
		korrekt[0] = test9.getNode(1001).getIncomingEdgesByHierarchy((byte)2).isEmpty();
		korrekt[4] = test9.getNode(4).getIncomingEdgesByHierarchy((byte)2).isEmpty();
		korrekt[6] = test9.getNode(6).getIncomingEdgesByHierarchy((byte)2).isEmpty();
		korrekt[7] = test9.getNode(7).getIncomingEdgesByHierarchy((byte)2).isEmpty();
		korrekt[8] = test9.getNode(8).getIncomingEdgesByHierarchy((byte)2).isEmpty();
		//Hier sollen kanten sein		
		korrekt[1] = test9.getNode(1).getIncomingEdgesByHierarchy((byte)2).size()==1;
		korrekt[2] = test9.getNode(2).getIncomingEdgesByHierarchy((byte)2).size()==1;
		korrekt[3] = test9.getNode(3).getIncomingEdgesByHierarchy((byte)2).size()==1;
		korrekt[5] = test9.getNode(5).getIncomingEdgesByHierarchy((byte)2).size()==4;
		korrekt[9] = test9.getNode(9).getIncomingEdgesByHierarchy((byte)2).size()==1;	
		
		for(int i=1;i<10;i++)
			if(korrekt[i] == false)
				fail("Test 9: Knoten "+i+" hat soviele Eingehende Kanten auf Level 2: "+test9.getNode(i).getIncomingEdgesByHierarchy((byte)2).size());
		
		if(korrekt[0] == false)
			fail("Test 9: Knoten 1001 (t)  sollte auf Level 2 keine Kanten haben. Hat aber:  "+test9.getNode(1001).getIncomingEdgesByHierarchy((byte)2).size());
		
		//Hat Knoten 5 vier Kanten auf Level 2 muessen es die richtigen sein
		
		logger.log("HHierarchyTest","Test 9 bestanden");
		
		/*
		 * Test 10 - mit H=1 sllte hier genau 2 HH rauskommen
		 */
		Config.H = 1;
		try {
			HHierarchyMT.computeHierarchyST(test10, (byte)2);
		} catch (Exception e) {
			fail("Es gab einen ehler beim ausfuehren der comuteH.: "+e.getLocalizedMessage());
		}
		//Teste nun korrekte Menge der Wege auf Level 2 - leere Mengen
		korrekt[0] = test10.getNode(1001).getIncomingEdgesByHierarchy((byte)2).isEmpty();
		korrekt[1] = test10.getNode(1).getIncomingEdgesByHierarchy((byte)2).isEmpty();
		//Hier sollen kanten sein		
		korrekt[2] = test10.getNode(2).getIncomingEdgesByHierarchy((byte)2).size()==1;
		korrekt[3] = test10.getNode(3).getIncomingEdgesByHierarchy((byte)2).size()==2;
		korrekt[4] = test10.getNode(4).getIncomingEdgesByHierarchy((byte)2).size()==1;
	
		
		for(int i=1;i<5;i++)
			if(korrekt[i] == false)
				fail("Test 10: Knoten "+i+" hat soviele Eingehende Kanten auf Level 2: "+test10.getNode(i).getIncomingEdgesByHierarchy((byte)2).size());
		
		if(korrekt[0] == false)
			fail("Test 10: Knoten 1001 (t)  sollte auf Level 2 keine Kanten haben. Hat aber:  "+test10.getNode(1001).getIncomingEdgesByHierarchy((byte)2).size());
		
		//Es sollten die Kanten 2,3 enthalten sein
		kante = (test10.getNode(3).getIncomingEdgesByHierarchy((byte)2).get(0));
		if (!((kante.getUID() == 2) || (kante.getUID() == 3)))
			fail("Test 10: Es gibt eine Kante auf Level 2, die es nciht geben duerfte: "+kante.getUID());
		
		kante = (test10.getNode(3).getIncomingEdgesByHierarchy((byte)2).get(1));
		if (!((kante.getUID() == 3) || (kante.getUID() == 2)))
			fail("Test 10: Es gibt eine Kante auf Level 2, die es nciht geben duerfte: "+kante.getUID());
		
		logger.log("HHierarchyTest","Test 10 bestanden");
		
		/*
		 * Test 10b - mit H=1 sllte hier genau 2 HH rauskommen - Knsitenz-Test
		 */
		Config.H = 1;
		try {
			HHierarchyMT.computeHierarchyST(test10, (byte)2);
		} catch (Exception e) {
			fail("Es gab einen ehler beim ausfuehren der comuteH.: "+e.getLocalizedMessage());
		}
		//Teste nun korrekte Menge der Wege auf Level 2 - leere Mengen
		korrekt[0] = test10.getNode(1001).getIncomingEdgesByHierarchy((byte)2).isEmpty();
		korrekt[1] = test10.getNode(1).getIncomingEdgesByHierarchy((byte)2).isEmpty();
		//Hier sollen kanten sein		
		korrekt[2] = test10.getNode(2).getIncomingEdgesByHierarchy((byte)2).size()==1;
		korrekt[3] = test10.getNode(3).getIncomingEdgesByHierarchy((byte)2).size()==2;
		korrekt[4] = test10.getNode(4).getIncomingEdgesByHierarchy((byte)2).size()==1;
	
		
		for(int i=1;i<5;i++)
			if(korrekt[i] == false)
				fail("Test 10b: Knoten "+i+" hat soviele Eingehende Kanten auf Level 2: "+test10.getNode(i).getIncomingEdgesByHierarchy((byte)2).size());
		
		if(korrekt[0] == false)
			fail("Test 10b: Knoten 1001 (t)  sollte auf Level 2 keine Kanten haben. Hat aber:  "+test10.getNode(1001).getIncomingEdgesByHierarchy((byte)2).size());
		
		//Es sollten die Kanten 2,3 enthalten sein
		kante = (test10.getNode(3).getIncomingEdgesByHierarchy((byte)2).get(0));
		if (!((kante.getUID() == 2) || (kante.getUID() == 3)))
			fail("Test 10b: Es gibt eine Kante auf Level 2, die es nciht geben duerfte: "+kante.getUID());
		
		kante = (test10.getNode(3).getIncomingEdgesByHierarchy((byte)2).get(1));
		if (!((kante.getUID() == 3) || (kante.getUID() == 2)))
			fail("Test 10b: Es gibt eine Kante auf Level 2, die es nciht geben duerfte: "+kante.getUID());
		
		logger.log("HHierarchyTest","Test 10b bestanden");
		
		
		/*
		 * TEst 11b - mit H=1 sllte hier genau keine HH rauskommen - Knsitenz-Test
		 */
		Config.H = 1;
		try {
			HHierarchyMT.computeHierarchyST(test11, (byte)6);
		} catch (Exception e) {
			fail("Es gab einen ehler beim ausfuehren der comuteH.: "+e.getLocalizedMessage());
		}
		//Teste nun korrekte Menge der Wege auf Level 2 - leere Mengen
		korrekt[0] = test11.getNode(1001).getIncomingEdgesByHierarchy((byte)2).isEmpty();
		korrekt[1] = test11.getNode(1).getIncomingEdgesByHierarchy((byte)2).isEmpty();
		korrekt[2] = test11.getNode(2).getIncomingEdgesByHierarchy((byte)2).isEmpty();
		korrekt[3] = test11.getNode(3).getIncomingEdgesByHierarchy((byte)2).isEmpty();
		korrekt[4] = test11.getNode(4).getIncomingEdgesByHierarchy((byte)2).isEmpty();
	
		
		for(int i=1;i<5;i++)
			if(korrekt[i] == false)
				fail("TEst 11: Knoten "+i+" hat soviele Eingehende Kanten auf Level 2: "+test11.getNode(i).getIncomingEdgesByHierarchy((byte)2).size());
		
		if(korrekt[0] == false)
			fail("TEst 11: Knoten 1001 (t)  sollte auf Level 2 keine Kanten haben. Hat aber:  "+test11.getNode(1001).getIncomingEdgesByHierarchy((byte)2).size());
		
		logger.log("HHierarchyTest","Test 11 bestanden");
			
		logger.log("HHierarchyTest","-------------------");
			
			/* Einkommentieren fuer etwas belastendere Tests
			 
		logger.log("HHierarchyTest","Starte einen Belastungstest zum Abschaetzen der Laufzeit");
		
		HierarchyMapGraph test = null;
		long start;
		long ende;
		
		logger.log("HHierarchyTest","Erstelle Zufallsgraphen mit 10.000 Knoten");
		try {
			 test = randomGraph(10000,0.7f,5);
		} catch (Exception e) {
			fail("Der RandomGraph konte nciht erstellt wrden: "+e.getLocalizedMessage());
		}
		logger.log("HHierarchyTest","Teste nun die cmputeH. (setze H auf 50)");
		Dijkstra.H = 50;
		start = System.currentTimeMillis();
		try {
			HHierarchyMT.computeHierarchyST(test, (byte)2);
		} catch (Exception e) {
			fail("Auf dem RandomGraph ist der computeH gescheitert: "+e.getLocalizedMessage());
		}
		ende = System.currentTimeMillis();
		
		logger.log("HHierarchyTest","Algo ist terminiert - Laufzeit: "+(ende-start) +"ms");
		
		logger.log("HHierarchyTest","Erstelle Zufallsgraphen mit 50.000 Knoten");
		try {
			 test = randomGraph(50000,0.7f,5);
		} catch (Exception e) {
			fail("Der RandomGraph konte nciht erstellt wrden: "+e.getLocalizedMessage());
		}
		logger.log("HHierarchyTest","Teste nun die cmputeH. (setze H auf 50)");
		Dijkstra.H = 50;
		start = System.currentTimeMillis();
		try {
			HHierarchyMT.computeHierarchyST(test, (byte)2);
		} catch (Exception e) {
			fail("Auf dem RandomGraph ist der computeH gescheitert: "+e.getLocalizedMessage());
		}
		ende = System.currentTimeMillis();
		
		logger.log("HHierarchyTest","Algo ist terminiert - Laufzeit: "+(ende-start)+"ms");
			
			
		
		logger.log("HHierarchyTest","Erstelle Zufallsgraphen mit 100.000 Knoten");
		try {
			 test = randomGraph(100000,0.7f,5);
		} catch (Exception e) {
			fail("Der RandomGraph konte nciht erstellt wrden: "+e.getLocalizedMessage());
		}
		logger.log("HHierarchyTest","Teste nun die cmputeH. (setze H auf 50)");
		Dijkstra.H = 50;
		start = System.currentTimeMillis();
		try {
			HHierarchyMT.computeHierarchyST(test, (byte)2);
		} catch (Exception e) {
			fail("Auf dem RandomGraph ist der computeH gescheitert: "+e.getLocalizedMessage());
		}
		ende = System.currentTimeMillis();
		
		logger.log("HHierarchyTest","Algo ist terminiert - Laufzeit: "+(ende-start)+"ms");
		
		logger.log("HHierarchyTest","Erstelle Zufallsgraphen mit 500.000 Knoten");
		try {
			 test = randomGraph(500000,0.7f,5);
		} catch (Exception e) {
			fail("Der RandomGraph konte nciht erstellt wrden: "+e.getLocalizedMessage());
		}
		logger.log("HHierarchyTest","Teste nun die cmputeH. (setze H auf 50)");
		Dijkstra.H = 50;
		start = System.currentTimeMillis();
		try {
			HHierarchyMT.computeHierarchyST(test, (byte)2);
		} catch (Exception e) {
			fail("Auf dem RandomGraph ist der computeH gescheitert: "+e.getLocalizedMessage());
		}
		ende = System.currentTimeMillis();
		
		logger.log("HHierarchyTest","Algo ist terminiert - Laufzeit: "+(ende-start)+"ms");
		
		
		logger.log("HHierarchyTest","Erstelle Zufallsgraphen mit 1.00.000 Knoten");
		try {
			 test = randomGraph(1000000,0.7f,5);
		} catch (Exception e) {
			fail("Der RandomGraph konte nciht erstellt wrden: "+e.getLocalizedMessage());
		}
		logger.log("HHierarchyTest","Teste nun die cmputeH. (setze H auf 50)");
		Dijkstra.H = 50;
		start = System.currentTimeMillis();
		try {
			HHierarchyMT.computeHierarchyST(test, (byte)2);
		} catch (Exception e) {
			fail("Auf dem RandomGraph ist der computeH gescheitert: "+e.getLocalizedMessage());
		}
		ende = System.currentTimeMillis();
		
		logger.log("HHierarchyTest","Algo ist terminiert - Laufzeit: "+(ende-start)+"ms");
			
			*/
		
		logger.log("HHierarchyTest","Alle Tests bestanden - Besser is das");
	}
	

	@Test
	public void testMT() {
		boolean[] korrekt = new boolean[10];
		HierarchyMapEdge kante;
		
		logger.log("HHierarchyMTTest","Starte Tests:");
		
		
		/*
		 * Test 1 - mit H=2 sllte hier keine HH rauskommen
		 */
		Config.H=2;
		try {
			HHierarchyMT.computeHierarchy(test1, (byte)2);
		} catch (Exception e) {
			e.printStackTrace();
			fail("Es gab einen ehler beim ausfuehren der comuteH.: "+e.getLocalizedMessage());
		}
		//Teste nun korrekte Menge der Wege auf Level 2
		for(int i = 1;i<5;i++)
			korrekt[i] = test1.getNode(i).getIncomingEdgesByHierarchy((byte)2).isEmpty();
		korrekt[0] = test1.getNode(1001).getIncomingEdgesByHierarchy((byte)2).isEmpty();
		
		for(int i=1;i<5;i++)
			if(korrekt[i] == false)
				fail("Test 1: Es sollten keine Edges auf Level 2 exisiteren - aber bei Knoten "+i+" gibt es "+test1.getNode(i).getIncomingEdgesByHierarchy((byte)2).size());
		
		if(korrekt[0] == false)
			fail("Test 1: Knoten 1001 (t)  sollte auf Level 2 keine Kanten haben. Hat aber:  "+test1.getNode(1001).getIncomingEdgesByHierarchy((byte)2).size());
		
		logger.log("HHierarchyMTTest","Test 1 bestanden");
		
		/*
		 * Test 2 - mit H=2 sllte hier genau eine HH rauskommen
		 */
		Config.H = 2;
		try {
			HHierarchyMT.computeHierarchy(test2, (byte)2);
		} catch (Exception e) {
			fail("Es gab einen ehler beim ausfuehren der comuteH.: "+e.getLocalizedMessage());
		}
		//Teste nun korrekte Menge der Wege auf Level 2 - leere Mengen
		korrekt[0] = test2.getNode(1001).getIncomingEdgesByHierarchy((byte)2).isEmpty();
		korrekt[1] = test2.getNode(1).getIncomingEdgesByHierarchy((byte)2).isEmpty();
		korrekt[2] = test2.getNode(2).getIncomingEdgesByHierarchy((byte)2).isEmpty();
		korrekt[5] = test2.getNode(5).getIncomingEdgesByHierarchy((byte)2).isEmpty();
		
		//Jeweils eine Kante sollte hier sein - und zwar die Kante 3
		korrekt[3] = test2.getNode(3).getIncomingEdgesByHierarchy((byte)2).size()==1;
		korrekt[4] = test2.getNode(4).getIncomingEdgesByHierarchy((byte)2).size()==1;
		
		for(int i=1;i<5;i++)
			if(korrekt[i] == false)
				fail("Test 2: Knoten "+i+" hat soviele Eingehende Kanten auf Level 2: "+test2.getNode(i).getIncomingEdgesByHierarchy((byte)2).size());
		
		if(korrekt[0] == false)
			fail("Test 2: Knoten 1001 (t)  sollte auf Level 2 keine Kanten haben. Hat aber:  "+test2.getNode(1001).getIncomingEdgesByHierarchy((byte)2).size());
		
		kante = (test2.getNode(3).getIncomingEdgesByHierarchy((byte)2).get(0));
		if (kante.getUID() != 3)
			fail("Test 2: Die kante, die auf Level 2 exisitert hat die falsche ID (ab Knoten 3) - soll:3 Ist: "+kante.getUID());
		
		kante = (test2.getNode(4).getIncomingEdgesByHierarchy((byte)2).get(0));
		if (kante.getUID() != 3)
			fail("Test 2: Die kante, die auf Level 2 exisitert hat die falsche ID (ab Knoten 4) - soll:3 Ist: "+kante.getUID());
		
		logger.log("HHierarchyMTTest","Test 2 bestanden");
		
		/*
		 * Test 3 - mit H=2 sllte hier genau zwei HH rauskommen
		 */
		Config.H = 2;
		try {
			HHierarchyMT.computeHierarchy(test3, (byte)2);
		} catch (Exception e) {
			fail("Es gab einen ehler beim ausfuehren der comuteH.: "+e.getLocalizedMessage());
		}
		//Teste nun korrekte Menge der Wege auf Level 2 - leere Mengen
		korrekt[0] = test3.getNode(1001).getIncomingEdgesByHierarchy((byte)2).isEmpty();
		korrekt[1] = test3.getNode(1).getIncomingEdgesByHierarchy((byte)2).isEmpty();
		korrekt[2] = test3.getNode(2).getIncomingEdgesByHierarchy((byte)2).isEmpty();
		korrekt[6] = test3.getNode(6).getIncomingEdgesByHierarchy((byte)2).isEmpty();
		//Hier sollen Kanten sein
		korrekt[3] = test3.getNode(3).getIncomingEdgesByHierarchy((byte)2).size()==1;
		korrekt[4] = test3.getNode(4).getIncomingEdgesByHierarchy((byte)2).size()==2;
		korrekt[5] = test3.getNode(5).getIncomingEdgesByHierarchy((byte)2).size()==1;
		
		for(int i=1;i<7;i++)
			if(korrekt[i] == false)
				fail("Test 3: Knoten "+i+" hat soviele Eingehende Kanten auf Level 2: "+test3.getNode(i).getIncomingEdgesByHierarchy((byte)2).size());
		
		if(korrekt[0] == false)
			fail("Test 3: Knoten 1001 (t)  sollte auf Level 2 keine Kanten haben. Hat aber:  "+test3.getNode(1001).getIncomingEdgesByHierarchy((byte)2).size());
		
		kante = (test3.getNode(3).getIncomingEdgesByHierarchy((byte)2).get(0));
		if (kante.getUID() != 3)
			fail("Test 3: Die kante, die auf Level 2 exisitert hat die falsche ID (ab Knoten 3) - soll:3 Ist: "+kante.getUID());
		
		kante = (test3.getNode(5).getIncomingEdgesByHierarchy((byte)2).get(0));
		if (kante.getUID() != 4)
			fail("Test 3: Die kante, die auf Level 2 exisitert hat die falsche ID (ab Knoten 5) - soll:4 Ist: "+kante.getUID());
		
		logger.log("HHierarchyMTTest","Test 3 bestanden");
		
		/*
		 * Test 4 - mit H=4 sllte hier genau eine HH rauskommen
		 */
		Config.H = 4;
		try {
			HHierarchyMT.computeHierarchy(test4, (byte)2);
		} catch (Exception e) {
			fail("Es gab einen ehler beim ausfuehren der comuteH.: "+e.getLocalizedMessage());
		}
		//Teste nun korrekte Menge der Wege auf Level 2 - leere Mengen
		korrekt[0] = test4.getNode(1001).getIncomingEdgesByHierarchy((byte)2).isEmpty();
		korrekt[1] = test4.getNode(1).getIncomingEdgesByHierarchy((byte)2).isEmpty();
		korrekt[2] = test4.getNode(2).getIncomingEdgesByHierarchy((byte)2).isEmpty();
		korrekt[3] = test4.getNode(3).getIncomingEdgesByHierarchy((byte)2).isEmpty();
		korrekt[4] = test4.getNode(4).getIncomingEdgesByHierarchy((byte)2).isEmpty();
		korrekt[7] = test4.getNode(7).getIncomingEdgesByHierarchy((byte)2).isEmpty();
		korrekt[8] = test4.getNode(8).getIncomingEdgesByHierarchy((byte)2).isEmpty();
		korrekt[9] = test4.getNode(9).getIncomingEdgesByHierarchy((byte)2).isEmpty();
		//Hier sollen kantensein
		korrekt[5] = test4.getNode(5).getIncomingEdgesByHierarchy((byte)2).size()==1;
		korrekt[6] = test4.getNode(6).getIncomingEdgesByHierarchy((byte)2).size()==1;
		
		for(int i=1;i<10;i++)
			if(korrekt[i] == false)
				fail("Test 4: Knoten "+i+" hat soviele Eingehende Kanten auf Level 2: "+test4.getNode(i).getIncomingEdgesByHierarchy((byte)2).size());
		
		if(korrekt[0] == false)
			fail("Test 4: Knoten 1001 (t)  sollte auf Level 2 keine Kanten haben. Hat aber:  "+test4.getNode(1001).getIncomingEdgesByHierarchy((byte)2).size());
		
		kante = (test4.getNode(5).getIncomingEdgesByHierarchy((byte)2).get(0));
		if (kante.getUID() != 5)
			fail("Test 4: Die kante, die auf Level 2 exisitert hat die falsche ID (ab Knoten 5) - soll:5 Ist: "+kante.getUID());
		
		kante = (test4.getNode(6).getIncomingEdgesByHierarchy((byte)2).get(0));
		if (kante.getUID() != 5)
			fail("Test 4: Die kante, die auf Level 2 exisitert hat die falsche ID (ab Knoten 6) - soll:5 Ist: "+kante.getUID());
		
		logger.log("HHierarchyMTTest","Test 4 bestanden");
		
		/*
		 * Test 5 - mit H=2 sllte hier genau eine HH rauskommen
		 */
		Config.H = 2;
		try {
			HHierarchyMT.computeHierarchy(test5, (byte)2);
		} catch (Exception e) {
			fail("Es gab einen ehler beim ausfuehren der comuteH.: "+e.getLocalizedMessage());
		}
		//Teste nun korrekte Menge der Wege auf Level 2 - leere Mengen
		korrekt[0] = test5.getNode(1001).getIncomingEdgesByHierarchy((byte)2).isEmpty();
		korrekt[1] = test5.getNode(1).getIncomingEdgesByHierarchy((byte)2).isEmpty();
		korrekt[2] = test5.getNode(2).getIncomingEdgesByHierarchy((byte)2).isEmpty();
		korrekt[3] = test5.getNode(3).getIncomingEdgesByHierarchy((byte)2).isEmpty();
		korrekt[5] = test5.getNode(5).getIncomingEdgesByHierarchy((byte)2).isEmpty();
		//Hier sollen Kanten sein
		korrekt[4] = test5.getNode(4).getIncomingEdgesByHierarchy((byte)2).size()==1;
		
		for(int i=1;i<6;i++)
			if(korrekt[i] == false)
				fail("Test 5: Knoten "+i+" hat soviele Eingehende Kanten auf Level 2: "+test5.getNode(i).getIncomingEdgesByHierarchy((byte)2).size());
		
		if(korrekt[0] == false)
			fail("Test 5: Knoten 1001 (t)  sollte auf Level 2 keine Kanten haben. Hat aber:  "+test5.getNode(1001).getIncomingEdgesByHierarchy((byte)2).size());
		
		kante = (test5.getNode(4).getIncomingEdgesByHierarchy((byte)2).get(0));
		if (kante.getUID() != 3)
			fail("Test 5: Die kante, die auf Level 2 exisitert hat die falsche ID (ab Knoten 4) - soll:3 Ist: "+kante.getUID());
		
		logger.log("HHierarchyMTTest","Test 5 bestanden");
		
		/*
		 * Test 6 - mit H=2 sllte hier genau 3 HH rauskommen
		 */
		Config.H = 2;
		try {
			HHierarchyMT.computeHierarchy(test6, (byte)2);
		} catch (Exception e) {
			fail("Es gab einen ehler beim ausfuehren der comuteH.: "+e.getLocalizedMessage());
		}
		//Teste nun korrekte Menge der Wege auf Level 2 - leere Mengen
		korrekt[0] = test6.getNode(1001).getIncomingEdgesByHierarchy((byte)2).isEmpty();
		korrekt[4] = test6.getNode(4).getIncomingEdgesByHierarchy((byte)2).isEmpty();
		korrekt[6] = test6.getNode(6).getIncomingEdgesByHierarchy((byte)2).isEmpty();
		korrekt[7] = test6.getNode(7).getIncomingEdgesByHierarchy((byte)2).isEmpty();
		korrekt[8] = test6.getNode(8).getIncomingEdgesByHierarchy((byte)2).isEmpty();
		//Hier sollen kanten sein		
		korrekt[1] = test6.getNode(1).getIncomingEdgesByHierarchy((byte)2).size()==1;
		korrekt[2] = test6.getNode(2).getIncomingEdgesByHierarchy((byte)2).size()==1;
		korrekt[3] = test6.getNode(3).getIncomingEdgesByHierarchy((byte)2).size()==0;
		korrekt[5] = test6.getNode(5).getIncomingEdgesByHierarchy((byte)2).size()==3;
		korrekt[9] = test6.getNode(9).getIncomingEdgesByHierarchy((byte)2).size()==1;	
		
		for(int i=1;i<10;i++)
			if(korrekt[i] == false)
				fail("Test 6: Knoten "+i+" hat soviele Eingehende Kanten auf Level 2: "+test6.getNode(i).getIncomingEdgesByHierarchy((byte)2).size());
		
		if(korrekt[0] == false)
			fail("Test 6: Knoten 1001 (t)  sollte auf Level 2 keine Kanten haben. Hat aber:  "+test6.getNode(1001).getIncomingEdgesByHierarchy((byte)2).size());
		
		//Es sollten genau drei der Kanten sein, die an Knoten 5 haengen
		kante = (test6.getNode(5).getIncomingEdgesByHierarchy((byte)2).get(0));
		if (!((kante.getUID() == 3) || (kante.getUID() == 5) || (kante.getUID() == 6)))
			fail("Test 6: Es gibt eine Kante auf Level 2, die es nciht geben duerfte: "+kante.getUID());
		
		kante = (test6.getNode(5).getIncomingEdgesByHierarchy((byte)2).get(1));
		if (!((kante.getUID() == 3) || (kante.getUID() == 5) || (kante.getUID() == 6)))
			fail("Test 6: Es gibt eine Kante auf Level 2, die es nciht geben duerfte: "+kante.getUID());
		
		kante = (test6.getNode(5).getIncomingEdgesByHierarchy((byte)2).get(2));
		if (!((kante.getUID() == 3) || (kante.getUID() == 5) || (kante.getUID() == 6)))
			fail("Test 6: Es gibt eine Kante auf Level 2, die es nciht geben duerfte: "+kante.getUID());
		
		
		logger.log("HHierarchyMTTest","Test 6 bestanden");
		
		/*
		 * Test 7 - mit H=3 sllte hier genau 1 HH rauskommen
		 */
		Config.H = 3;
		try {
			HHierarchyMT.computeHierarchy(test7, (byte)2);
		} catch (Exception e) {
			fail("Es gab einen ehler beim ausfuehren der comuteH.: "+e.getLocalizedMessage());
		}
		//Teste nun korrekte Menge der Wege auf Level 2 - leere Mengen
		korrekt[0] = test7.getNode(1001).getIncomingEdgesByHierarchy((byte)2).isEmpty();
		korrekt[2] = test7.getNode(2).getIncomingEdgesByHierarchy((byte)2).isEmpty();
		korrekt[3] = test7.getNode(3).getIncomingEdgesByHierarchy((byte)2).isEmpty();
		korrekt[4] = test7.getNode(4).getIncomingEdgesByHierarchy((byte)2).isEmpty();
		korrekt[6] = test7.getNode(6).getIncomingEdgesByHierarchy((byte)2).isEmpty();
		korrekt[7] = test7.getNode(7).getIncomingEdgesByHierarchy((byte)2).isEmpty();
		korrekt[8] = test7.getNode(8).getIncomingEdgesByHierarchy((byte)2).isEmpty();
		korrekt[9] = test7.getNode(9).getIncomingEdgesByHierarchy((byte)2).isEmpty();
		//Hier sollen kanten sein		
		korrekt[1] = test7.getNode(1).getIncomingEdgesByHierarchy((byte)2).size()==1;
		korrekt[5] = test7.getNode(5).getIncomingEdgesByHierarchy((byte)2).size()==1;	
		
		for(int i=1;i<10;i++)
			if(korrekt[i] == false)
				fail("Test 7: Knoten "+i+" hat soviele Eingehende Kanten auf Level 2: "+test7.getNode(i).getIncomingEdgesByHierarchy((byte)2).size());
		
		if(korrekt[0] == false)
			fail("Test 7: Knoten 1001 (t)  sollte auf Level 2 keine Kanten haben. Hat aber:  "+test7.getNode(1001).getIncomingEdgesByHierarchy((byte)2).size());
		
		//es sollte nur Kante 3 exisiteren
		kante = (test7.getNode(1).getIncomingEdgesByHierarchy((byte)2).get(0));
		if (kante.getUID() != 3)
			fail("Test 7: Die kante, die auf Level 2 exisitert hat die falsche ID (ab Knoten 1) - soll:3 Ist: "+kante.getUID());
		
		kante = (test7.getNode(5).getIncomingEdgesByHierarchy((byte)2).get(0));
		if (kante.getUID() != 3)
			fail("Test 7: Die kante, die auf Level 2 exisitert hat die falsche ID (ab Knoten 5) - soll:3 Ist: "+kante.getUID());
		
		logger.log("HHierarchyMTTest","Test 7 bestanden");
		
		/*
		 * Test 8 - mit H=4 sllte hier genau keine HH rauskommen
		 */
		Config.H = 4;
		try {
			HHierarchyMT.computeHierarchy(test8, (byte)2);
		} catch (Exception e) {
			fail("Es gab einen ehler beim ausfuehren der comuteH.: "+e.getLocalizedMessage());
		}
		//Teste nun korrekte Menge der Wege auf Level 2 - leere Mengen
		korrekt[0] = test8.getNode(1001).getIncomingEdgesByHierarchy((byte)2).isEmpty();
		korrekt[1] = test8.getNode(1).getIncomingEdgesByHierarchy((byte)2).isEmpty();
		korrekt[2] = test8.getNode(2).getIncomingEdgesByHierarchy((byte)2).isEmpty();
		korrekt[3] = test8.getNode(3).getIncomingEdgesByHierarchy((byte)2).isEmpty();
		korrekt[4] = test8.getNode(4).getIncomingEdgesByHierarchy((byte)2).isEmpty();
		korrekt[5] = test8.getNode(5).getIncomingEdgesByHierarchy((byte)2).isEmpty();
		korrekt[6] = test8.getNode(6).getIncomingEdgesByHierarchy((byte)2).isEmpty();
		korrekt[7] = test8.getNode(7).getIncomingEdgesByHierarchy((byte)2).isEmpty();
		korrekt[8] = test8.getNode(8).getIncomingEdgesByHierarchy((byte)2).isEmpty();
		korrekt[9] = test8.getNode(9).getIncomingEdgesByHierarchy((byte)2).isEmpty();	
		
		for(int i=1;i<10;i++)
			if(korrekt[i] == false)
				fail("Test 8: Knoten "+i+" hat soviele Eingehende Kanten auf Level 2 (0 erwartet): "+test8.getNode(i).getIncomingEdgesByHierarchy((byte)2).size());
		
		if(korrekt[0] == false)
			fail("Test 8: Knoten 1001 (t)  sollte auf Level 2 keine Kanten haben. Hat aber:  "+test8.getNode(1001).getIncomingEdgesByHierarchy((byte)2).size());
		
		logger.log("HHierarchyMTTest","Test 8 bestanden");
		
		/*
		 * Test 9 - mit H=2 sllte hier genau 4 HH rauskommen
		 */
		Config.H = 2;
		try {
			HHierarchyMT.computeHierarchy(test9, (byte)2);
		} catch (Exception e) {
			fail("Es gab einen ehler beim ausfuehren der comuteH.: "+e.getLocalizedMessage());
		}
		//Teste nun korrekte Menge der Wege auf Level 2 - leere Mengen
		korrekt[0] = test9.getNode(1001).getIncomingEdgesByHierarchy((byte)2).isEmpty();
		korrekt[4] = test9.getNode(4).getIncomingEdgesByHierarchy((byte)2).isEmpty();
		korrekt[6] = test9.getNode(6).getIncomingEdgesByHierarchy((byte)2).isEmpty();
		korrekt[7] = test9.getNode(7).getIncomingEdgesByHierarchy((byte)2).isEmpty();
		korrekt[8] = test9.getNode(8).getIncomingEdgesByHierarchy((byte)2).isEmpty();
		//Hier sollen kanten sein		
		korrekt[1] = test9.getNode(1).getIncomingEdgesByHierarchy((byte)2).size()==1;
		korrekt[2] = test9.getNode(2).getIncomingEdgesByHierarchy((byte)2).size()==1;
		korrekt[3] = test9.getNode(3).getIncomingEdgesByHierarchy((byte)2).size()==1;
		korrekt[5] = test9.getNode(5).getIncomingEdgesByHierarchy((byte)2).size()==4;
		korrekt[9] = test9.getNode(9).getIncomingEdgesByHierarchy((byte)2).size()==1;	
		
		for(int i=1;i<10;i++)
			if(korrekt[i] == false)
				fail("Test 9: Knoten "+i+" hat soviele Eingehende Kanten auf Level 2: "+test9.getNode(i).getIncomingEdgesByHierarchy((byte)2).size());
		
		if(korrekt[0] == false)
			fail("Test 9: Knoten 1001 (t)  sollte auf Level 2 keine Kanten haben. Hat aber:  "+test9.getNode(1001).getIncomingEdgesByHierarchy((byte)2).size());
		
		//Hat Knoten 5 vier Kanten auf Level 2 muessen es die richtigen sein
		
		logger.log("HHierarchyMTTest","Test 9 bestanden");
		
		/*
		 * Test 10 - mit H=1 sllte hier genau 2 HH rauskommen
		 */
		Config.H = 1;
		try {
			HHierarchyMT.computeHierarchy(test10, (byte)2);
		} catch (Exception e) {
			fail("Es gab einen ehler beim ausfuehren der comuteH.: "+e.getLocalizedMessage());
		}
		//Teste nun korrekte Menge der Wege auf Level 2 - leere Mengen
		korrekt[0] = test10.getNode(1001).getIncomingEdgesByHierarchy((byte)2).isEmpty();
		korrekt[1] = test10.getNode(1).getIncomingEdgesByHierarchy((byte)2).isEmpty();
		//Hier sollen kanten sein		
		korrekt[2] = test10.getNode(2).getIncomingEdgesByHierarchy((byte)2).size()==1;
		korrekt[3] = test10.getNode(3).getIncomingEdgesByHierarchy((byte)2).size()==2;
		korrekt[4] = test10.getNode(4).getIncomingEdgesByHierarchy((byte)2).size()==1;
	
		
		for(int i=1;i<5;i++)
			if(korrekt[i] == false)
				fail("Test 10: Knoten "+i+" hat soviele Eingehende Kanten auf Level 2: "+test10.getNode(i).getIncomingEdgesByHierarchy((byte)2).size());
		
		if(korrekt[0] == false)
			fail("Test 10: Knoten 1001 (t)  sollte auf Level 2 keine Kanten haben. Hat aber:  "+test10.getNode(1001).getIncomingEdgesByHierarchy((byte)2).size());
		
		//Es sollten die Kanten 2,3 enthalten sein
		kante = (test10.getNode(3).getIncomingEdgesByHierarchy((byte)2).get(0));
		if (!((kante.getUID() == 2) || (kante.getUID() == 3)))
			fail("Test 10: Es gibt eine Kante auf Level 2, die es nciht geben duerfte: "+kante.getUID());
		
		kante = (test10.getNode(3).getIncomingEdgesByHierarchy((byte)2).get(1));
		if (!((kante.getUID() == 3) || (kante.getUID() == 2)))
			fail("Test 10: Es gibt eine Kante auf Level 2, die es nciht geben duerfte: "+kante.getUID());
		
		logger.log("HHierarchyMTTest","Test 10 bestanden");
		
		/*
		 * Test 10b - mit H=1 sllte hier genau 2 HH rauskommen - Knsitenz-Test
		 */
		Config.H = 1;
		try {
			HHierarchyMT.computeHierarchy(test10, (byte)2);
		} catch (Exception e) {
			fail("Es gab einen ehler beim ausfuehren der comuteH.: "+e.getLocalizedMessage());
		}
		//Teste nun korrekte Menge der Wege auf Level 2 - leere Mengen
		korrekt[0] = test10.getNode(1001).getIncomingEdgesByHierarchy((byte)2).isEmpty();
		korrekt[1] = test10.getNode(1).getIncomingEdgesByHierarchy((byte)2).isEmpty();
		//Hier sollen kanten sein		
		korrekt[2] = test10.getNode(2).getIncomingEdgesByHierarchy((byte)2).size()==1;
		korrekt[3] = test10.getNode(3).getIncomingEdgesByHierarchy((byte)2).size()==2;
		korrekt[4] = test10.getNode(4).getIncomingEdgesByHierarchy((byte)2).size()==1;
	
		
		for(int i=1;i<5;i++)
			if(korrekt[i] == false)
				fail("Test 10b: Knoten "+i+" hat soviele Eingehende Kanten auf Level 2: "+test10.getNode(i).getIncomingEdgesByHierarchy((byte)2).size());
		
		if(korrekt[0] == false)
			fail("Test 10b: Knoten 1001 (t)  sollte auf Level 2 keine Kanten haben. Hat aber:  "+test10.getNode(1001).getIncomingEdgesByHierarchy((byte)2).size());
		
		//Es sollten die Kanten 2,3 enthalten sein
		kante = (test10.getNode(3).getIncomingEdgesByHierarchy((byte)2).get(0));
		if (!((kante.getUID() == 2) || (kante.getUID() == 3)))
			fail("Test 10b: Es gibt eine Kante auf Level 2, die es nciht geben duerfte: "+kante.getUID());
		
		kante = (test10.getNode(3).getIncomingEdgesByHierarchy((byte)2).get(1));
		if (!((kante.getUID() == 3) || (kante.getUID() == 2)))
			fail("Test 10b: Es gibt eine Kante auf Level 2, die es nciht geben duerfte: "+kante.getUID());
		
		logger.log("HHierarchyMTTest","Test 10b bestanden");
		
		
		/*
		 * TEst 11b - mit H=1 sllte hier genau keine HH rauskommen - Knsitenz-Test
		 */
		Config.H = 1;
		try {
			HHierarchyMT.computeHierarchy(test11, (byte)6);
		} catch (Exception e) {
			fail("Es gab einen ehler beim ausfuehren der comuteH.: "+e.getLocalizedMessage());
		}
		//Teste nun korrekte Menge der Wege auf Level 2 - leere Mengen
		korrekt[0] = test11.getNode(1001).getIncomingEdgesByHierarchy((byte)2).isEmpty();
		korrekt[1] = test11.getNode(1).getIncomingEdgesByHierarchy((byte)2).isEmpty();
		korrekt[2] = test11.getNode(2).getIncomingEdgesByHierarchy((byte)2).isEmpty();
		korrekt[3] = test11.getNode(3).getIncomingEdgesByHierarchy((byte)2).isEmpty();
		korrekt[4] = test11.getNode(4).getIncomingEdgesByHierarchy((byte)2).isEmpty();
	
		
		for(int i=1;i<5;i++)
			if(korrekt[i] == false)
				fail("TEst 11: Knoten "+i+" hat soviele Eingehende Kanten auf Level 2: "+test11.getNode(i).getIncomingEdgesByHierarchy((byte)2).size());
		
		if(korrekt[0] == false)
			fail("TEst 11: Knoten 1001 (t)  sollte auf Level 2 keine Kanten haben. Hat aber:  "+test11.getNode(1001).getIncomingEdgesByHierarchy((byte)2).size());
		
		logger.log("HHierarchyMTTest","Test 11 bestanden");
			
		logger.log("HHierarchyMTTest","-------------------");
	}
	
	// Vergleichstest - einkmmentieren fuer Abschaetzung des Leistungszuwachses bei MT
//	@Test
//	public void testMTVergleichstest() {
//		//int gr = 1000;
//		
//		
//		logger.log("HHierarchyMTTest","Alle Tests bestanden - Besser is das");
//		
//		int testgr = 50;
//		
//		//Setze auf optimale Groesse
//		HHierarchyMT.setThNum(Math.round(Runtime.getRuntime().availableProcessors()*2));
//		
//		logger.log("HHierarchyMTTest", "Teste mit n="+testgr+",M="+HHierarchyMT.getThNum());
//		//System.out.println("Teste mit n="+testgr+",M="+HHierarchyMT.getThNum()+")");
//
//		this.vergleichstest(testgr, true);
//		
//		
//	}
	

}
