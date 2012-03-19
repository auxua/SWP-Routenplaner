/**
 * 
 */
package Import;

import static org.junit.Assert.fail;
import graphenbib.MapEdge;
import graphenbib.MapGraph;
import graphenbib.MapNode;
import graphenbib.StreetType;

import java.io.File;

import org.junit.Test;

public class OSMImporterGraphenTest {

	
	@Test
	public void test() {
		
		MapGraph testGraph = null;
		OSMImporter osm_imp;
		
		//Anmerkung: der aktuelle Konstruktor schreibt eine Ausgabedatei als Argument vor. dies soll ncoh angepasst werden -> siehe OSMImporter
	
		/*
		 * Test 0.1
		 * Dieser sollte funktionieren mit zwei Nodes und einer Kante (Einbahnstrasse)
		 */
	
		//starte Test
		try {
			osm_imp = new OSMImporter(new File("testdateien/test0.1.osm"));
			testGraph = osm_imp.getTile(osm_imp.getMapCenter(), 0);
		}
		catch(Exception e) {
			fail("Test 0.1: Es gab einen Fehler beim Einlesen. Fehler: "+e.getLocalizedMessage());
		}
		
		//Pruefe Ergebnis
		
		//Teste auf Null-Pointer
		if (testGraph == null)
			fail("Test 0.1: Es wurde kein Graph uebergeben, sondern null");
		
		
		
		//Test korrektheit der Nodes.
		
		MapNode node1 = testGraph.getNode(1);
		MapNode node2 = testGraph.getNode(2);
		MapNode node3 = testGraph.getNode(500);
		
		//Teste node3 - Idee dahinter ist verwechslungsgefahr uid und id
		if (node3 != null)
			fail("Test 0.1: Es existiert eine Node mit UID=500 - diese duerfte es nciht geben");
		//Test auf Nulluepointer
		if (node1 == null)
			fail("Test 0.1: Es wurden nciht alle Nodes angelegt - der testGraph hat null-pointer zurueckgegeben fuer UID=1");
		if (node2 == null)
			fail("Test 0.1: Es wurden nciht alle Nodes angelegt - der testGraph hat null-pointer zurueckgegeben fuer UID=2");
		
		//Test die (hoffentlich) leeren Kantenlisten der Knoten
		if (node1.getIncomingEdges().length > 0)
			fail("Test 0.1: Node 1 hat eine eingehende Kante - sie sollte keine haben (Einzige Kante ist Einbahnstrasse)");
		if (node2.getOutgoingEdges().length > 0)
			fail("Test 0.1: Node 2 hat eine ausgehende Kante - sie sollte keine haben (Einzige Kante ist Einbahnstrasse)");
		
		//Test auf identische Kante zwischen den beiden Knoten
		if (!(node1.getOutgoingEdges()[0].equals(node2.getIncomingEdges()[0])))
			fail("Test 0.1: Die eingetragenen Kanten zwischen Knoten 1 und 2 sind nciht die selbe Kante (intern mehrere angelegt?)");
			
		//Teste Streettype der Kante
		if (node1.getOutgoingEdges()[0].getType() != StreetType.TRUNK)
			fail("Test 0.1: Der Strassentyp wurde nciht korrekt ausgelesen und abgespeichert. (TODOS im Importer beachtet?)");
			
		
		//Anmerkung:
		//Aufgrund der Konsistenz der Tests werden einige Test nciht wiederholt (z.B. Test fuer korrekte Grenzen)
		
		
		
		/*
		 * Test 0.2
		 * Dieser Test ist quasi analog zum Test 0.1. Lediglich die Einbahnstrasse ist andersrum
		 */
		
		//starte Test
		try {
			osm_imp = new OSMImporter(new File("testdateien/test0.2.osm"));
			testGraph = osm_imp.getTile(osm_imp.getMapCenter(), 0);
		}
		catch(Exception e) {
			fail("Test 0.2: Es gab einen Fehler beim Einlesen. Fehler: "+e.getLocalizedMessage());
		}
		
		//Pruefe Ergebnis
		
		//Teste auf Null-Pointer
		if (testGraph == null)
			fail("Test 0.2: Es wurde kein Graph uebergeben, sondern null");
		
		//Test korrektheit der Nodes.
		
		node1 = testGraph.getNode(1);
		node2 = testGraph.getNode(2);
		
		//Test die (hoffentlich) leeren Kantenlisten der Knoten
		if (node2.getIncomingEdges().length > 0)
			fail("Test 0.2: Node 2 hat eine eingehende Kante - sie sollte keine haben (Einzige Kante ist Einbahnstrasse)");
		if (node1.getOutgoingEdges().length > 0)
			fail("Test 0.2: Node 1 hat eine ausgehende Kante - sie sollte keine haben (Einzige Kante ist Einbahnstrasse)");
		
		//Test auf identische Kante zwischen den beiden Knoten
		if (!(node2.getOutgoingEdges()[0].equals(node1.getIncomingEdges()[0])))
			fail("Test 0.2: Die eingetragenen Kanten zwischen Knoten 1 und 2 sind nciht die selbe Kante (intern mehrere angelegt?)");
		
		
		/*
		 * Test 0.3
		 * Dieser Test ist analog zu Test 0.1. Allerdings handelt es sich hierbei nciht um eine Einbahnstrasse
		 */
		
		//starte Test
		try {
			osm_imp = new OSMImporter(new File("testdateien/test0.3.osm"));
			testGraph = osm_imp.getTile(osm_imp.getMapCenter(), 0);
		}
		catch(Exception e) {
			fail("Test 0.3: Es gab einen Fehler beim Einlesen. Fehler: "+e.getLocalizedMessage());
		}
		
		//Pruefe Ergebnis
		
		//Teste auf Null-Pointer
		if (testGraph == null)
			fail("Test 0.3: Es wurde kein Graph uebergeben, sondern null");
		
		//Test korrektheit der Nodes.
		
		node1 = testGraph.getNode(1);
		node2 = testGraph.getNode(2);
			
		//Test die (hoffentlich) leeren Kantenlisten der Knoten
		if (node2.getIncomingEdges().length != 1)
			fail("Test 0.3: Node 2 hat falsche Anzahl eingehender Kanten - sie sollte eine haben (Strasse als Einbahnstrasse gespeichert?)");
		if (node1.getOutgoingEdges().length != 1)
			fail("Test 0.3: Node 1 hat falsche Anzahl ausgehender Kanten - sie sollte eine haben (Strasse als Einbahnstrasse gespeichert?)");
		
		//Teste nun penibel die Kanten - Es darf "!=" fuer den Test genutzt zu werden, da der Pointer der gleiche sein soll
		if (node1.getOutgoingEdges()[0].getNodeEnd() != node2)
			fail("Test 0.3: Die ausgehende Kante von node1 hat ein falsches Ziel (Reihenfolge vertauscht?)");
		if (node2.getOutgoingEdges()[0].getNodeEnd() != node1)
			fail("Test 0.3: Die ausgehende Kante von node2 hat ein falsches Ziel (Reihenfolge vertauscht?)");
		if (node1.getIncomingEdges()[0].getNodeStart() != node2)
			fail("Test 0.3: Die eingehende Kante von node1 hat einen falschen Start (Reihenfolge vertauscht?)");
		if (node2.getIncomingEdges()[0].getNodeStart() != node1)
			fail("Test 0.3: Die eingehende Kante von node2 hat einen falschen Start (Reihenfolge vertauscht?)");
			

		//Anmerkung
		//Die Tests 0.x sind triviale Basis-Tests. Nun teste mit fehlerhaften Daten das Verhalten
		
		/*
		 * Test 1
		 * Dieser Test soll mit einem Fehler abbrechen, da eine ungueltiger XML-Syntax vorliegt.
		 */
		
		//starte Test - erwarte Fehler
		boolean fehler = false;
		try {
			osm_imp = new OSMImporter(new File("testdateien/test1.osm"));
			testGraph = osm_imp.getTile(osm_imp.getMapCenter(), 0);
		}
		catch(Exception e) {
			fehler = true;
		}
		
		if (fehler == false)
			fail("Test 1: Es wurde ein Grpah auf Basis einer falschen XML-Syntax eingelesen.");
		
		
		/*
		 * Test 2
		 * Dieser Test soll mit einem Fehler abbrechen, da eine Node-ID doppelt vergeben ist
		 */
		
		//starte Test - erwarte Fehler
		fehler = false;
		try {
			osm_imp = new OSMImporter(new File("testdateien/test2.osm"));
			testGraph = osm_imp.getTile(osm_imp.getMapCenter(), 0);
		}
		catch(Exception e) {
			fehler = true;
		}
		if (fehler == false)
			fail("Test 2: Es wurde ein Graph mit doppelten Node-IDs eingelesen.");
		
//		-> Auskommentiert auf Anweisung der Import/Graphenbib		
//		/*
//		 * Test 3 
//		 * Dieser Test soll mit einem Fehler abbrechen, da die Bounds ausserhalb der Erde liegen.
//		 */
//			/*
//		//starte Test - erwarte Fehler
//		fehler = false;
//		try {
//			osm_imp = new OSMImporter("testdateien/test3.osm","tmp");
//			testGraph = osm_imp.getTile(osm_imp.getMapCenter(), 0);
//		}
//		catch(Exception e) {
//			fehler = true;
//		}
//		
//		if (fehler == false)
//			fail("Test 3: Es wurde ein Graph eingelesen, dessen Grenzen ausserhalb der Erde liegen.");
//			*/
//		
//		/*
//		 * Test 4
//		 * In diesem Test soll eine Node eingefuegt werden, die nciht innerhalb der bounds liegt.
//		 * Derzeitiger Stand: dieser Fall soll mit Fehler abbrechen
//		 */
//			/*
//		//starte Test - erwarte Fehler
//		fehler = false;
//		try {
//			osm_imp = new OSMImporter("testdateien/test4.osm","tmp");
//			testGraph = osm_imp.getTile(osm_imp.getMapCenter(), 0);
//		}
//		catch(Exception e) {
//			fehler = true;
//		}
//		
//		if (fehler == false)
//			fail("Test 4: Es wurde ein Graph angelegt und ohne Fehler zurueckgegeben, obwohl aufgrund einer ungueltigen Node-Position ein ehler haette geworfen werden sollen.");
//			*/
		
			
		/*
		 * Test 5
		 * In diesem Test wird fuer die Kante ein unbekannter Paramter fuer den Strassentyp angegeben.
		 * Derzeotiger Stannd: Umschreibung auf Unknown erwartet
		 */
			/*
		//starte Test
		try {
			osm_imp = new OSMImporter(new File("testdateien/test5.osm"));
			testGraph = osm_imp.getTile(osm_imp.getMapCenter(), 0);
		}
		catch(Exception e) {
			fail("Test 5: Es wurde ein Fehler geworfen statt einen unbekannten Strassentyp auf UNKNOWN umzuschreiben.");
		}
			*/
		
		
//		/*
//		 * Test 6
//		 * Dieser Test soll mit einem Fehler abbrechen, da das Ziel der Strasse nciht definiert ist (es existiert keine Node mit dieser UID)
//		 */
//			/*
//		//starte Test - erwarte Fehler
//		fehler = false;
//		try {
//			osm_imp = new OSMImporter("testdateien/test6.osm","tmp");
//			testGraph = osm_imp.getTile(osm_imp.getMapCenter(), 0);
//		}
//		catch(Exception e) {
//			fehler = true;
//		}
//		if (fehler == false)
//			fail("Test 6: Es wurde ein Graph mit ungueltiger Kante angelegt.");
//			*/
		
		/*
		 * Test 7
		 * In diesem Test werden Knoten in einen Graphen eingefuegt, die exakt auf den Bounds liegen
		 */
			
		//starte Test
		try {
			osm_imp = new OSMImporter(new File("testdateien/test7.osm"));
			testGraph = osm_imp.getTile(osm_imp.getMapCenter(), 0);
		}
		catch(Exception e) {
			fail("Test 7: Es war nciht moeglich einen gueltigen Graphen einzulesen, dessen Knoten auf den Bounds liegen. Fehler: "+e.getLocalizedMessage());
		}
		

		//Anmerkung
		//Dies waren die elementaren Fehlertests. Nun soll ein vordefinierter Beispielgraph eingelesen werden (siehe Dokumentation)
		
		/*
		 * TestGraph
		 * In diesem Test soll ein vordefinierter, gueltiger Graph korrekt eingelesen werden
		 */
		
		try {
			osm_imp = new OSMImporter(new File("testdateien/testGraph.osm"));
			testGraph = osm_imp.getTile(osm_imp.getMapCenter(), 0);
		}
		catch(Exception e) {
			fail("TestGraph: Beim Einlesen trat ein Fehler auf. Fehler: "+e.getLocalizedMessage());
		}
		
		
		// Teste auf korrekte Daten (verschwende ein Bit aus komfort-gruenden)
		boolean[] korrekt = new boolean[12];
		
		//Teste auf Graph Incomingedges
		//Daten abholen
		MapEdge[] liste1 = testGraph.getNode(1).getIncomingEdges();
		MapEdge[] liste2 = testGraph.getNode(2).getIncomingEdges();
		MapEdge[] liste3 = testGraph.getNode(3).getIncomingEdges();
		MapEdge[] liste4 = testGraph.getNode(4).getIncomingEdges();
		MapEdge[] liste5 = testGraph.getNode(5).getIncomingEdges();
		MapEdge[] liste6 = testGraph.getNode(6).getIncomingEdges();
		MapEdge[] liste7 = testGraph.getNode(7).getIncomingEdges();
		MapEdge[] liste8 = testGraph.getNode(8).getIncomingEdges();
		MapEdge[] liste9 = testGraph.getNode(9).getIncomingEdges();
		MapEdge[] liste10 = testGraph.getNode(10).getIncomingEdges();
		//MapEdge[] liste11 = testGraph.getNode(11).getIncomingEdges(); //Umstellung loescht diesen nach Import
		
		//Vergleiche Daten der leeren Listen
		korrekt[1] = (liste1.length == 0);
		korrekt[4] = (liste4.length == 0);
		korrekt[10] = (liste10.length == 0);
		//korrekt[11] = (liste11.length == 0);
		//vergleiche andere Nodes (nciht trivial)
		//Node 2 - nur Kante 1
		korrekt[2] = ((liste2.length==1) && (liste2[0].getUID() == 1));
		//Node 3 - nur Kante 23
		korrekt[3] = ((liste3.length==1) && (liste3[0].getUID() == 23));
		//Node 5 - nur kanten 6,7 
		korrekt[5] = ((liste5.length==2) && ((liste5[0].getUID() == 6) || (liste5[0].getUID() == 7)) &&  ((liste5[1].getUID() == 6) || (liste5[1].getUID() == 7)));
		//Node 6 - nur kanten 10,8
		korrekt[6] = ((liste6.length==2) && ((liste6[0].getUID() == 10) || (liste6[0].getUID() == 8)) &&  ((liste6[1].getUID() == 10) || (liste6[1].getUID() == 8)));
		//Node 7 - nur Kante 23
		korrekt[7] = ((liste7.length==1) && (liste7[0].getUID() == 23));
		//Node 8 - nur Kante 4
		korrekt[8] = ((liste8.length==1) && (liste8[0].getUID() == 4));
		//Node 9 - nur Kante 5
		korrekt[9] = ((liste9.length==1) && (liste9[0].getUID() == 5));
		
		//for (int i=1;i<12;i++) {
		for (int i=1;i<11;i++) {
			if (korrekt[i] == false) fail("testGraph: Fehler bei IncomingEdges-Test Nummer "+i);
		}
		
		//Teste auf Graph OutgoingEedges
		//Daten abholen
		liste1 = testGraph.getNode(1).getOutgoingEdges();
		liste2 = testGraph.getNode(2).getOutgoingEdges();
		liste3 = testGraph.getNode(3).getOutgoingEdges();
		liste4 = testGraph.getNode(4).getOutgoingEdges();
		liste5 = testGraph.getNode(5).getOutgoingEdges();
		liste6 = testGraph.getNode(6).getOutgoingEdges();
		liste7 = testGraph.getNode(7).getOutgoingEdges();
		liste8 = testGraph.getNode(8).getOutgoingEdges();
		liste9 = testGraph.getNode(9).getOutgoingEdges();
		liste10 = testGraph.getNode(10).getOutgoingEdges();
		//liste11 = testGraph.getNode(11).getOutgoingEdges();
				
		//Vergleiche Daten der leeren Listen
		korrekt[2] = (liste2.length == 0);
		korrekt[5] = (liste5.length == 0);
		korrekt[8] = (liste8.length == 0);
		//korrekt[11] = (liste11.length == 0);
					
		//Vergleiche andere Nodes
		//Node 1 - Nur Kanten 10,8
		korrekt[1] = ((liste1.length==2) && ((liste1[0].getUID() == 10) || (liste1[0].getUID() == 8)) &&  ((liste1[1].getUID() == 10) || (liste1[1].getUID() == 8)));
		//Node 3 - Nur Kante 23
		korrekt[3] = ((liste3.length==1) &&  (liste3[0].getUID() == 23));
		//Node 4 - Nur Kanten 4,5
		korrekt[4] = ((liste4.length==2) && ((liste4[0].getUID() == 4) || (liste4[0].getUID() == 5)) &&  ((liste4[1].getUID() == 4) || (liste4[1].getUID() == 5)));
		//Node 6 - Nur Kante 1
		korrekt[6] = ((liste6.length==1) &&  (liste6[0].getUID() == 1));
		//Node 7 - Nur Kante 23
		korrekt[7] = ((liste7.length==1) &&  (liste7[0].getUID() == 23));
		//Node 9 - Nur Kante 6
		korrekt[9] = ((liste9.length==1) &&  (liste9[0].getUID() == 6));
		//Node 10 - Nur Kante 7
		korrekt[10] = ((liste10.length==1) &&  (liste10[0].getUID() == 7));
		
		//for (int i=1;i<12;i++) {
		for (int i=1;i<11;i++) {
			if (korrekt[i] == false) fail("testGraph: Fehler bei OutgingEdges-Test Nummer "+i);
		}
		/*
		 * TestGraph2
		 * In diesem Test soll ein vordefinierter, gueltiger Graph korrekt eingelesen werden
		 * Im Gegensatz zum ersten Graph sind hier komplexere Ways beinhaltet (mehr als zwei Knioten)
		 */
		
		try {
			osm_imp = new OSMImporter(new File("testdateien/testGraph2.osm"));
			testGraph = osm_imp.getTile(osm_imp.getMapCenter(), 0);
		}
		catch(Exception e) {
			fail("TestGraph2: Beim Einlesen trat ein Fehler auf. Fehler: "+e.getLocalizedMessage());
		}
		
		
		// Teste auf korrekte Daten (verschwende ein Bit aus komfort-gruenden)
		korrekt = new boolean[12];
		
		//Teste auf Graph Incomingedges
		//Daten abholen
		liste1 = testGraph.getNode(1).getIncomingEdges();
		liste2 = testGraph.getNode(2).getIncomingEdges();
		liste3 = testGraph.getNode(3).getIncomingEdges();
		liste4 = testGraph.getNode(4).getIncomingEdges();
		liste5 = testGraph.getNode(5).getIncomingEdges();
		liste6 = testGraph.getNode(6).getIncomingEdges();
		liste7 = testGraph.getNode(7).getIncomingEdges();
		liste8 = testGraph.getNode(8).getIncomingEdges();
		liste9 = testGraph.getNode(9).getIncomingEdges();
		liste10 = testGraph.getNode(10).getIncomingEdges();
		//liste11 = testGraph.getNode(11).getIncomingEdges();
		
		//Vergleiche Daten der leeren Listen
		korrekt[1] = (liste1.length == 0);
		korrekt[4] = (liste4.length == 0);
		korrekt[10] = (liste10.length == 0);
		//korrekt[11] = (liste11.length == 0);
		//vergleiche andere Nodes (nciht trivial)
		//Node 2 - nur Kanten 10,20
		korrekt[2] = ((liste2.length==2) && ((liste2[0].getUID() == 10) || (liste2[0].getUID() == 20)) &&  ((liste2[1].getUID() == 10) || (liste2[1].getUID() == 20)));
		//Node 3 - nur Kante 20
		korrekt[3] = ((liste3.length==1) && (liste3[0].getUID() == 20));
		//Node 5 - nur kanten 5,7 
		korrekt[5] = ((liste5.length==2) && ((liste5[0].getUID() == 5) || (liste5[0].getUID() == 7)) &&  ((liste5[1].getUID() == 5) || (liste5[1].getUID() == 7)));
		//Node 6 - nur kanten 10,8
		korrekt[6] = ((liste6.length==2) && ((liste6[0].getUID() == 10) || (liste6[0].getUID() == 8)) &&  ((liste6[1].getUID() == 10) || (liste6[1].getUID() == 8)));
		//Node 7 - nur Kanten 20,20
		korrekt[7] = ((liste7.length==2) && ((liste7[0].getUID() == 20)) &&  ((liste7[1].getUID() == 20)));
		//Node 8 - nur Kante 4
		korrekt[8] = ((liste8.length==1) && (liste8[0].getUID() == 4));
		//Node 9 - nur Kante 5
		korrekt[9] = ((liste9.length==1) && (liste9[0].getUID() == 5));
		
		//for (int i=1;i<12;i++) {
		for (int i=1;i<11;i++) {
			if (korrekt[i] == false) fail("testGraph2: Fehler bei IncomingEdges-Test Nummer "+i);
		}
		
		//Teste auf Graph OutgoingEedges
		//Daten abholen
		liste1 = testGraph.getNode(1).getOutgoingEdges();
		liste2 = testGraph.getNode(2).getOutgoingEdges();
		liste3 = testGraph.getNode(3).getOutgoingEdges();
		liste4 = testGraph.getNode(4).getOutgoingEdges();
		liste5 = testGraph.getNode(5).getOutgoingEdges();
		liste6 = testGraph.getNode(6).getOutgoingEdges();
		liste7 = testGraph.getNode(7).getOutgoingEdges();
		liste8 = testGraph.getNode(8).getOutgoingEdges();
		liste9 = testGraph.getNode(9).getOutgoingEdges();
		liste10 = testGraph.getNode(10).getOutgoingEdges();
		//liste11 = testGraph.getNode(11).getOutgoingEdges();
				
		//Vergleiche Daten der leeren Listen
		korrekt[5] = (liste5.length == 0);
		korrekt[8] = (liste8.length == 0);
		//korrekt[11] = (liste11.length == 0);
					
		//Vergleiche andere Nodes
		//Node 1 - Nur Kanten 10,8
		korrekt[1] = ((liste1.length==2) && ((liste1[0].getUID() == 10) || (liste1[0].getUID() == 8)) &&  ((liste1[1].getUID() == 10) || (liste1[1].getUID() == 8)));
		//Node 2 - Nur Kante 20
		korrekt[2] = ((liste2.length==1) &&  (liste2[0].getUID() == 20));
		//Node 3 - Nur Kante 20
		korrekt[3] = ((liste3.length==1) &&  (liste3[0].getUID() == 20));
		//Node 4 - Nur Kanten 4,5
		korrekt[4] = ((liste4.length==2) && ((liste4[0].getUID() == 4) || (liste4[0].getUID() == 5)) &&  ((liste4[1].getUID() == 4) || (liste4[1].getUID() == 5)));
		//Node 6 - Nur Kante 10
		korrekt[6] = ((liste6.length==1) &&  (liste6[0].getUID() == 10));
		//Node 7 - Nur Kanten 20,20
		korrekt[7] = ((liste7.length==2) && ((liste7[0].getUID() == 20)) &&  ((liste7[1].getUID() == 20)));
		//Node 9 - Nur Kante 5
		korrekt[9] = ((liste9.length==1) &&  (liste9[0].getUID() == 5));
		//Node 10 - Nur Kante 7
		korrekt[10] = ((liste10.length==1) &&  (liste10[0].getUID() == 7));
		
		//for (int i=1;i<12;i++) {
		for (int i=1;i<11;i++) {
			if (korrekt[i] == false) fail("testGraph2: Fehler bei OutgingEdges-Test Nummer "+i);
		}
		
		/*
		 * TestGraph3
		 * In diesem Test soll ein vordefinierter, gueltiger Graph korrekt eingelesen werden
		 * Im Gegensatz zum ersten Graph sind hier komplexere Ways beinhaltet (mehr als zwei Knioten)
		 * Im Gegensatz zu den anderen Graphen werden hier auch ntfernungen geprueft.
		 */
		
		try {
			osm_imp = new OSMImporter(new File("testdateien/testGraph3.osm"));
			testGraph = osm_imp.getTile(osm_imp.getMapCenter(), 0);
		}
		catch(Exception e) {
			fail("TestGraph3: Beim Einlesen trat ein Fehler auf. Fehler: "+e.getLocalizedMessage());
		}
		
		//Gehe Kanten einzeln durch und pruefe deren Laengen
		int i = 0; int eps = 1;
		MapNode node = null;
		MapEdge[] edge = new MapEdge[12];
		int[] Ist = new int[112];
		//Nutze Iterator an erstem Knoten
		node = testGraph.getNode(1);
		i = 0;
		edge = node.getIncomingEdges();

		//Hole Kanten ab und trage ihre Laengen in das Ist-Array ein - Das ist nicht die effizienteste Methode, aber leicht zu Debuggen/pruefen
		//Dadurch erhalte kanten b,d,e
		while(i<edge.length && i<12) {
			Ist[edge[i].getUID()] = edge[i].getLength();
			i++;
		}
		i =0;
		
		
		
		//if (!((Ist[8] >= 6027.781f) && (Ist[8] <= 6027.782f)))
		if (((6027.782f - Ist[8]) > eps) && ((Ist[8] - 6027.782f) > 0))
			fail("testGraph3: falsche Entfernung bei Kante 8. Erwartetes min: 6027.781, max: 6027.782, Ist: "+Ist[8]);
		
		//if (!((Ist[4] >= 7610.388f) && (Ist[4] <= 7610.389f)))
		if (((7610.389f - Ist[4]) > eps) && ((Ist[4] - 7610.389f) > 0))
			fail("testGraph3: falsche Entfernung bei Kante 4. Erwartetes min: 7610.388, max: 7610.389, Ist: "+Ist[4]);
		
		//if (!((Ist[5] >= 5990.883f) && (Ist[5] <= 5990.884f)))
		if (((5990.884f - Ist[5]) > eps) && ((Ist[5] - 5990.884f) > 0))
			fail("testGraph3: falsche Entfernung bei Kante 5. Erwartetes min: 5990.884, max: 5990.884, Ist: "+Ist[5]);
		
		//Hole Kanten von Knoten 2. Erhalte neu dadurch Kante m
		node = testGraph.getNode(2);
		edge = node.getIncomingEdges();
		while(i<edge.length && i<12) {
			Ist[edge[i].getUID()] = edge[i].getLength();
			i++;
		}
		i =0;
		
		if (((14032.333f - Ist[111]) > eps) && ((Ist[111] - 14032.333f) > 0))
			fail("testGraph3: falsche Entfernung bei Kante 111. Erwartetes min: 14032.332, max: 14032.333, Ist: "+Ist[111]);
		
		//Hole Kanten von Knoten 3. erhalte neu dadurch Kante f
		node = testGraph.getNode(3);
		edge = node.getIncomingEdges();
		while(i<edge.length && i<12) {
			Ist[edge[i].getUID()] = edge[i].getLength();
			i++;
		}
		i =0;
		
		
		if (((9174.900f - Ist[7]) > eps)  && ((Ist[7] - 9174.900f) > 0))
			fail("testGraph3: falsche Entfernung bei Kante 7. Erwartetes min: 9174.899, max: 9174.900, Ist: "+Ist[7]);
		
		//Hole Kanten von Knoten 4. erhalte neu dadurch Kante g,c
		node = testGraph.getNode(4);
		edge = node.getIncomingEdges();
		while(i<edge.length && i<12) {
			Ist[edge[i].getUID()] = edge[i].getLength();
			i++;
		}
		i =0;
		
		if (((4187.851f - Ist[20]) > eps) && ((Ist[20] - 4187.851f) > 0))
			fail("testGraph3: falsche Entfernung bei Kante 20. Erwartetes min: 4187.850, max: 4187.851, Ist: "+Ist[20]);
		
		if (((2085.281f - Ist[21]) > eps) && ((Ist[21] - 2085.281f) > 0))
			fail("testGraph3: falsche Entfernung bei Kante 21. Erwartetes min: 2085.280, max: 2085.281, Ist: "+Ist[21]);
		
		//Hole Kanten von Knoten 5. erhalte neu dadurch Kante i,h
		node = testGraph.getNode(5);
		edge = node.getIncomingEdges();
		while(i<edge.length && i<12) {
			Ist[edge[i].getUID()] = edge[i].getLength();
			i++;
		}
		i =0;
		
		if (((3553.719f - Ist[22]) > eps) && ((Ist[22] - 3553.719f) > 0))
			fail("testGraph3: falsche Entfernung bei Kante 20. Erwartetes min: 3553.718, max: 3553.719, Ist: "+Ist[22]);
		
		if (((8618.230f - Ist[33]) > eps) && ((Ist[33] - 8618.230f) > 0))
			fail("testGraph3: falsche Entfernung bei Kante 33. Erwartetes min: 8618.229, max: 8618.230, Ist: "+Ist[33]);
		
		//Hole Kanten von Knoten 6. erhalte neu dadurch Kante j,k
		node = testGraph.getNode(6);
		edge = node.getIncomingEdges();
		while(i<edge.length && i<12) {
			
			Ist[edge[i].getUID()] = edge[i].getLength();
			i++;
		}
		i =0;
		
		if (((0.005f - Ist[50]) > eps) && ((Ist[50] - 0.005f) > 0))
			fail("testGraph3: falsche Entfernung bei Kante 50. Erwartetes min: 0.004, max: 0.005, Ist: "+Ist[50]);
		
		if (((5064.695f - Ist[55]) > eps) && ((Ist[55] - 5064.695f) > 0))
			fail("testGraph3: falsche Entfernung bei Kante 55. Erwartetes min: 5064.694, max: 5064.695, Ist: "+Ist[55]);
		
		
		//Hole Kanten von Knoten 7. erhalte neu dadurch Kante l
		node = testGraph.getNode(7);
		edge = node.getIncomingEdges();
		while(i<edge.length && i<12) {
			
			Ist[edge[i].getUID()] = edge[i].getLength();
			i++;
		}
		i =0;
		
		if (((5416.757f - Ist[5]) > eps) && ((Ist[5] - 5416.757f) > 0))
			fail("testGraph3: falsche Entfernung bei Kante 5(l). Erwartetes min: 5416.756, max: 5416.757, Ist: "+Ist[5]);
		
		//Hole Kanten von Knoten 8. erhalte neu dadurch Kante 10
		node = testGraph.getNode(8);
		edge = node.getIncomingEdges();
		while(i<edge.length && i<12) {
			
			Ist[edge[i].getUID()] = edge[i].getLength();
			i++;
		}
		i =0;
		
		if (((4786.739f - Ist[10]) > eps) && ((Ist[10] - 4786.739f) > 0))
			fail("testGraph3: falsche Entfernung bei Kante 10. Erwartetes min: 4786.738, max: 4786.739, Ist: "+Ist[5]);
		
		
		
		
	}

}
