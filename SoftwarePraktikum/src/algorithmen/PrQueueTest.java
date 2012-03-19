/**
 * 
 */
package algorithmen;

import static org.junit.Assert.fail;
import graphenbib.GPSCoordinate;
import graphenbib.MapNode;

import org.junit.Test;

public class PrQueueTest {

	private PrQueue testQueue = null;
	
	private MapNode node(int ID) throws Exception{
		return new MapNode(ID, new GPSCoordinate(1,1));
	}
	
	@Test
	public void test() throws Exception{
		Vertex testVertex = null;
		boolean fehler = false;
		
		/*
		 * Test 1: versuche aus einer leere Queue zu extrahieren -> erwarte Exception
		 */
		clean();
		fehler = false;
		try {
			testVertex = testQueue.extractMin();
		} catch (Exception e) {
			fehler = true;
		}
		if (fehler ==false)
			fail("Test 1: Es wrde ein Element ohne Kommentar/Exception als minimales Element aus einer leeren Queue ausgegeben");
		
		//Aufraeumen
		clean();
		
		/*
		 * Test 2: versuche einen Nullpointer einzufuegen
		 */
		fehler = false;
		try {
			testQueue.insert(null);
		} catch (Exception e) {
			fehler = true;
		}
		if (fehler ==false)
			fail("Test 2: Es wurde ohne Fehler/Exception ein Nullpointer in der Queue hinzugefuegt");
		
		//Aufraeumen
		clean();
		
		/*
		 * Test 3: Fuege einen gueltigen Vertex alleine ein - sollte funktionieren
		 */
		Vertex validVertex = new Vertex(node(3),585247);
		
		try {
			testQueue.insert(validVertex);
		} catch (Exception e) {
			fail("Test 3: Fehler beim einfuegen eines gueltigen Vertices. Fehler: "+e.getLocalizedMessage());
		}
		
		//Aufraumen
		clean();
		
		/*
		 * Test 4: versuche zwei Vertices mit gleicher UID einzufuegen
		 */
		Vertex failVertex1 = new Vertex(node(5),5);
		Vertex failVertex2 = new Vertex(node(5),7);
		
		fehler = false;
		try {
			testQueue.insert(failVertex1);
			testQueue.insert(failVertex2);
		} catch (Exception e) {
			fehler = true;
		}
		if (fehler ==false)
			fail("Test 4: Es wurde ohne Fehler/Exception Vertices gleicher UID eingefuegt");
		
		//Aufraumen
		clean();
		
		/*
		 * Test 5: Versuche Vertex mit neg./null Kantengewicht anzulegen und einzufuegen
		 */
		//teste neg. Vertex
		fehler = false;
		try {
			failVertex1 = new Vertex(node(7),-89258);
			testQueue.insert(failVertex1);
		} catch (Exception e) {
			fehler = true;
		}
		if (fehler ==false)
			fail("Test 5: Es wurde ohne Fehler/Exception ein Vertex eingefuegt mit negativer Distanz");
		
		//Der folgende Test wurde entfernt, da zum Beispiel der Startknoten Distanz 0 besitzt
		
		//Aufraumen
		//clean();
		//teste null-Distanz
		//fehler = false;
		//try {
		//	failVertex2 = new Vertex(1587,0);
		//	testQueue.insert(failVertex2);
		//} catch (Exception e) {
		//	fehler = true;
		//}
		//if (fehler ==false)
		//	fail("Test 5: Es wurde ohne Fehler/Exception ein Vertex eingefuegt mit null-Distanz");
		
		//Aufraumen
		clean();
		
		/*
		 * Test 6: teste 3 Vertices gleicher Distanz - erwartete Ausgabe (UID): 1,2,3
		 */
		
		//Lege Vertices an
		Vertex v1 = new Vertex(node(1) ,10);
		Vertex v2 = new Vertex(node(2),10);
		Vertex v3 = new Vertex(node(3),10);
		
		//Fuege ein
		try {
			testQueue.insert(v1);
			testQueue.insert(v2);
			testQueue.insert(v3);
		} catch (Exception e) {
			fail("Test 6: Das einfuegen der Vertices hat nciht funktioniert. Fehler: "+e.getLocalizedMessage());
		}
		
		//teste auf Korrekte Ausgabenreihenfolge
		testVertex = testQueue.extractMin();
		if (testVertex != v1)
			fail("Test 6: Der erste ausgegebene Vertex ist falsch. Soll: UID=1 Ist="+testVertex.node.getUID());
		
		testVertex = testQueue.extractMin();
		if (testVertex != v2)
			fail("Test 6: Der zweite ausgegebene Vertex ist falsch. Soll: UID=2 Ist="+testVertex.node.getUID());
		
		testVertex = testQueue.extractMin();
		if (testVertex != v3)
			fail("Test 6: Der dritte ausgegebene Vertex ist falsch. Soll: UID=3 Ist="+testVertex.node.getUID());
		
		//Aufraeumen
		clean();
		
		/*
		 * Test 7: test 3 Vertices und nutze Updates -> erwarte 1,2,3
		 */
		
		//Lege Vertices an
		v1 = new Vertex(node(1),10);
		v2 = new Vertex(node(2),10);
		v3 = new Vertex(node(3),10);
		
		//Fuege ein
		try {
			testQueue.insert(v1);
			testQueue.insert(v2);
			testQueue.insert(v3);
		} catch (Exception e) {
			fail("Test 7: Das einfuegen der Vertices hat nciht funktioniert. Fehler: "+e.getLocalizedMessage());
		}
		
		//Versuche nun Updates
		try {
			v1.setDist(8);
			testQueue.update(v1);
			v2.setDist(9);
			testQueue.update(v2);
		} catch (Exception e) {
			fail("Test 7: Das updaten hat cniht funktioniert. Fehler: "+e.getLocalizedMessage());
		}
		
		//Teste nun Reihenfolge
		testVertex = testQueue.extractMin();
		if (testVertex != v1)
			fail("Test 7: Der erste ausgegebene Vertex ist falsch. Soll: UID=1 Ist="+testVertex.node.getUID());
		
		testVertex = testQueue.extractMin();
		if (testVertex != v2)
			fail("Test 7: Der zweite ausgegebene Vertex ist falsch. Soll: UID=2 Ist="+testVertex.node.getUID());
		
		testVertex = testQueue.extractMin();
		if (testVertex != v3)
			fail("Test 7: Der dritte ausgegebene Vertex ist falsch. Soll: UID=3 Ist="+testVertex.node.getUID());
		
		//Aufraeumen
		clean();
		
		/*
		 * Test 8: Update Vertices auf gleiche Distanz. Die Reihenfolge der Updates muss eingehalten werden -> erwarte 2,1,3
		 */
		
		//Lege Vertices an
		v1 = new Vertex(node(1),10);
		v2 = new Vertex(node(2),10);
		v3 = new Vertex(node(3),10);
				
		//Fuege ein
		try {
			testQueue.insert(v1);
			testQueue.insert(v2);
			testQueue.insert(v3);
		} catch (Exception e) {
			fail("Test 8: Das einfuegen der Vertices hat nciht funktioniert. Fehler: "+e.getLocalizedMessage());
		}
			
		//Versuche nun Updates
		try {
			v2.setDist(5);
			testQueue.update(v2);
			v1.setDist(5);
			testQueue.update(v1);
			v3.setDist(5);
			testQueue.update(v3);
		} catch (Exception e) {
			fail("Test 8: Das updaten hat cniht funktioniert. Fehler: "+e.getLocalizedMessage());
		}
				
		//Teste nun Reihenfolge
		testVertex = testQueue.extractMin();
		if (testVertex != v2)
			fail("Test 8: Der erste ausgegebene Vertex ist falsch. Soll: UID=2 Ist="+testVertex.node.getUID());
		
		testVertex = testQueue.extractMin();
		if (testVertex != v1)
			fail("Test 8: Der zweite ausgegebene Vertex ist falsch. Soll: UID=1 Ist="+testVertex.node.getUID());
		
		testVertex = testQueue.extractMin();
		if (testVertex != v3)
			fail("Test 8: Der dritte ausgegebene Vertex ist falsch. Soll: UID=3 Ist="+testVertex.node.getUID());
		
		//Aufraeumen
		clean();
		
		/*
		 * Test 9: Weiterer Update-Test -> erwarte 1,2,3
		 */
		
		//Lege Vertices an
		v1 = new Vertex(node(1),9);
		v2 = new Vertex(node(2),10);
		v3 = new Vertex(node(3),11);
				
		//Fuege ein
		try {
			testQueue.insert(v1);
			testQueue.insert(v2);
			testQueue.insert(v3);
		} catch (Exception e) {
			fail("Test 9: Das einfuegen der Vertices hat nciht funktioniert. Fehler: "+e.getLocalizedMessage());
		}
			
		//Versuche nun Updates
		try {
			v3.setDist(10);
			testQueue.update(v3);
			v2.setDist(9);
			testQueue.update(v2);
		} catch (Exception e) {
			fail("Test 9: Das updaten hat cniht funktioniert. Fehler: "+e.getLocalizedMessage());
		}
				
		//Teste nun Reihenfolge
		testVertex = testQueue.extractMin();
		if (testVertex != v1)
			fail("Test 9: Der erste ausgegebene Vertex ist falsch. Soll: UID=1 Ist="+testVertex.node.getUID());
		
		testVertex = testQueue.extractMin();
		if (testVertex != v2)
			fail("Test 9: Der zweite ausgegebene Vertex ist falsch. Soll: UID=2 Ist="+testVertex.node.getUID());
		
		testVertex = testQueue.extractMin();
		if (testVertex != v3)
			fail("Test 9: Der dritte ausgegebene Vertex ist falsch. Soll: UID=3 Ist="+testVertex.node.getUID());
		
		//Aufraeumen
		clean();
		
		
		/*
		 * Test 10: Weiterer Update-Test -> erwarte 3,2,1
		 */
		
		//Lege Vertices an
		v1 = new Vertex(node(1),11);
		v2 = new Vertex(node(2),10);
		v3 = new Vertex(node(3),9);
				
		//Fuege ein
		try {
			testQueue.insert(v1);
			testQueue.insert(v2);
			testQueue.insert(v3);
		} catch (Exception e) {
			fail("Test 10: Das einfuegen der Vertices hat nciht funktioniert. Fehler: "+e.getLocalizedMessage());
		}
			
		//Versuche nun Updates
		try {
			v2.setDist(9);
			testQueue.update(v2);
			v1.setDist(10);
			testQueue.update(v1);
		} catch (Exception e) {
			fail("Test 10: Das updaten hat cniht funktioniert. Fehler: "+e.getLocalizedMessage());
		}
				
		//Teste nun Reihenfolge
		testVertex = testQueue.extractMin();
		if (testVertex != v3)
			fail("Test 10: Der erste ausgegebene Vertex ist falsch. Soll: UID=3 Ist="+testVertex.node.getUID());
		
		testVertex = testQueue.extractMin();
		if (testVertex != v2)
			fail("Test 10: Der zweite ausgegebene Vertex ist falsch. Soll: UID=2 Ist="+testVertex.node.getUID());
		
		testVertex = testQueue.extractMin();
		if (testVertex != v1)
			fail("Test 10: Der dritte ausgegebene Vertex ist falsch. Soll: UID=1 Ist="+testVertex.node.getUID());
		
		//Aufraeumen
		clean();
		
		/*
		 * Test 11: Versuche nciht-existierende Node upzudaten. Erwartet: keine Aenderung der Daten und der Menge
		 */
		
		//Lege Vertices an
		v1 = new Vertex(node(1),10);
		v2 = new Vertex(node(2),10);
		v3 = new Vertex(node(3),10);
		testVertex = new Vertex(node(5),2);
				
		//Fuege ein
		try {
			testQueue.insert(v1);
			testQueue.insert(v2);
			testQueue.insert(v3);
		} catch (Exception e) {
			fail("Test 11: Das einfuegen der Vertices hat nciht funktioniert. Fehler: "+e.getLocalizedMessage());
		}
			
		//Versuche nun Updates
		try {
			v2.setDist(5);
			testQueue.update(v2);
			v1.setDist(5);
			testQueue.update(v1);
			v3.setDist(5);
			testQueue.update(v3);
			testQueue.update(testVertex);
		} catch (Exception e) {
			fail("Test 11: Das updaten hat cniht funktioniert. Fehler: "+e.getLocalizedMessage());
		}
				
		//Teste nun Reihenfolge
		testVertex = testQueue.extractMin();
		if (testVertex != v2)
			fail("Test 11: Der erste ausgegebene Vertex ist falsch. Soll: UID=2 Ist="+testVertex.node.getUID());
		
		testVertex = testQueue.extractMin();
		if (testVertex != v1)
			fail("Test 11: Der zweite ausgegebene Vertex ist falsch. Soll: UID=1 Ist="+testVertex.node.getUID());
		
		testVertex = testQueue.extractMin();
		if (testVertex != v3)
			fail("Test 11: Der dritte ausgegebene Vertex ist falsch. Soll: UID=3 Ist="+testVertex.node.getUID());
		
		//Aufraeumen
		clean();
		
		/*
		 * Test 12: Versuche null-Node upzudaten -> erwarte Exception
		 */
		
		//Versuche nun Update
		fehler = false;
		try {
			testQueue.update(null);
		} catch (Exception e) {
			fehler = true;
		}
		if (fehler == false)
			fail("Test 12: Das updaten mit Nullpointer hat keine Exception verursascht");

		//Aufraeumen
		clean();
		
		/*
		 * Test 13: Teste nun eine Reihe von orrekten, unsortierten Vertices
		 */
		
		//Lege Vertices an
		Vertex v5 = new Vertex(node(10),1558);
		Vertex v4 = new Vertex(node(20),3203);
		v1 = new Vertex(node(30),7358);
		v2 = new Vertex(node(40),9725);
		v3 = new Vertex(node(50),10111);
		Vertex v6 = new Vertex(node(60),12453);
		Vertex v7 = new Vertex(node(70),27225);
				
		//Fuege ein
		try {
			testQueue.insert(v1);
			testQueue.insert(v2);
			testQueue.insert(v3);
			testQueue.insert(v4);
			testQueue.insert(v5);
			testQueue.insert(v6);
			testQueue.insert(v7);
		} catch (Exception e) {
			fail("Test 13: Das einfuegen der Vertices hat nciht funktioniert. Fehler: "+e.getLocalizedMessage());
		}
				
		//Teste nun Reihenfolge
		testVertex = testQueue.extractMin();
		if (testVertex != v5)
			fail("Test 13: Der erste ausgegebene Vertex ist falsch. Soll: UID=10 Ist="+testVertex.node.getUID());
		
		testVertex = testQueue.extractMin();
		if (testVertex != v4)
			fail("Test 13: Der zweite ausgegebene Vertex ist falsch. Soll: UID=20 Ist="+testVertex.node.getUID());
		
		testVertex = testQueue.extractMin();
		if (testVertex != v1)
			fail("Test 13: Der dritte ausgegebene Vertex ist falsch. Soll: UID=30 Ist="+testVertex.node.getUID());
		
		testVertex = testQueue.extractMin();
		if (testVertex != v2)
			fail("Test 13: Der vierte ausgegebene Vertex ist falsch. Soll: UID=40 Ist="+testVertex.node.getUID());
		
		testVertex = testQueue.extractMin();
		if (testVertex != v3)
			fail("Test 13: Der fuenfte ausgegebene Vertex ist falsch. Soll: UID=50 Ist="+testVertex.node.getUID());
		
		testVertex = testQueue.extractMin();
		if (testVertex != v6)
			fail("Test 13: Der sechste ausgegebene Vertex ist falsch. Soll: UID=60 Ist="+testVertex.node.getUID());
		
		testVertex = testQueue.extractMin();
		if (testVertex != v7)
			fail("Test 13: Der siebte ausgegebene Vertex ist falsch. Soll: UID=70 Ist="+testVertex.node.getUID());
		
		
		
		//Aufraeumen
		clean();
		
		/*
		 * Test 14: Teste nun Korrektheit der getSize-Methode
		 */
		
		//Lege Vertices an
		v1 = new Vertex(node(1),11);
		v2 = new Vertex(node(2),10);
		v3 = new Vertex(node(3),9);
		
		//Jetzt sollten 0 Elemente existieren
		if (testQueue.getSize() != 0)
			fail("Test 14: Leere Queue hat get-Size Rueckgabe: "+testQueue.getSize());
		
		//Fuege ein
		try {
			testQueue.insert(v1);
			testQueue.insert(v2);
		} catch (Exception e) {
			fail("Test 14: Das einfuegen der Vertices hat nciht funktioniert. Fehler: "+e.getLocalizedMessage());
		}
		
		//Jetzt sollte es 2 Elemente geben
		if (testQueue.getSize() != 2)
			fail("Test 14: 2-Element-Queue hat get-Size Rueckgabe: "+testQueue.getSize());
		
		//Versuche nun Updates
		try {
			v1.setDist(10);
			testQueue.update(v1);
			v2.setDist(9);
			testQueue.update(v2);
			
		} catch (Exception e) {
			fail("Test 14: Das updaten hat cniht funktioniert. Fehler: "+e.getLocalizedMessage());
		}
		
		//Jetzt sollte es 2 Elemente geben
		if (testQueue.getSize() != 2)
			fail("Test 14: 2-Element-Queue hat get-Size Rueckgabe: "+testQueue.getSize());
		
		testVertex = testQueue.extractMin();
		
		//Jetzt sollte es 1 Element geben
		if (testQueue.getSize() != 1)
			fail("Test 14: 1-Element-Queue hat get-Size Rueckgabe: "+testQueue.getSize());
		
		try {
			testQueue.insert(v3);
		} catch (Exception e) {
			fail("Test 14: Das einfuegen der Vertices hat nciht funktioniert. Fehler: "+e.getLocalizedMessage());
		}
		
		//Jetzt sollte es 2 Elemente geben
		if (testQueue.getSize() != 2)
			fail("Test 14: 2-Element-Queue hat get-Size Rueckgabe: "+testQueue.getSize());
		
		testVertex = testQueue.extractMin();
		testVertex = testQueue.extractMin();
		
		//Jetzt sollten 0 Elemente existieren
		if (testQueue.getSize() != 0)
			fail("Test 14: Leere Queue hat get-Size Rueckgabe: "+testQueue.getSize());
		
		//Aufraumen
		clean();
	}
	
	//aufraeumen
	private void clean() {
		testQueue = new PrQueue();
	}

}
