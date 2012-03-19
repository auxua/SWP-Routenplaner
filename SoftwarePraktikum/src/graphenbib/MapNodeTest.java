package graphenbib;
import static org.junit.Assert.fail;

import org.junit.BeforeClass;
import org.junit.Test;

/**
 * 
 */

public class MapNodeTest {

	/**
	 * Variablendefinition
	 */
	
	private static MapNode[] nodes = new MapNode[11];
	private static MapEdge[] edges = new MapEdge[9];
	//private static String[] namen = new String[11];
	private static GPSCoordinate[] koord = new GPSCoordinate[11];
	
	private static int randomint(int min, int max) {
		return (int) (Math.random() * (max - min + 1)) + min;
	}
	

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		//bereite Variablen fuer Test vor
		int i = 0;
		//Koordinaten - zufaellige Werte
		for (i=0;i<11;i++) {
			try {
				koord[i] = new GPSCoordinate(randomint(-90,90),randomint(-180,180));
			} catch (Exception e) {
				fail("Fehler bei ANlegen von GPS-Koordinaten (zufall)"+e.getLocalizedMessage());
			}
		}
		//Nodes
		for (i=0;i<11;i++) {
			nodes[i] = new MapNode(i,koord[i]);
		}
		//Edges
		edges[0] = new MapEdge(nodes[0],nodes[5],0,(int)Math.random(),StreetType.LIVING_STREET);
		edges[1] = new MapEdge(nodes[5],nodes[1],1,(int)Math.random(),StreetType.PRIMARY);
		edges[2] = new MapEdge(nodes[6],nodes[2],2,(int)Math.random(),StreetType.MOTORWAY);
		edges[3] = new MapEdge(nodes[2],nodes[6],3,(int)Math.random(),StreetType.RESIDENTIAL);
		edges[4] = new MapEdge(nodes[3],nodes[7],4,(int)Math.random(),StreetType.ROAD);
		edges[5] = new MapEdge(nodes[3],nodes[8],5,(int)Math.random(),StreetType.SECONDARY);
		edges[6] = new MapEdge(nodes[8],nodes[4],6,(int)Math.random(),StreetType.TERTIARY);
		edges[7] = new MapEdge(nodes[9],nodes[4],7,(int)Math.random(),StreetType.TRUNK);
		edges[8] = new MapEdge(nodes[0],nodes[5],8,(int)Math.random(),StreetType.LIVING_STREET);
		//Fuege diese hinzu
		nodes[0].addOutgoingEdge(edges[0]);
		nodes[5].addOutgoingEdge(edges[1]);
		nodes[6].addOutgoingEdge(edges[2]);
		nodes[2].addOutgoingEdge(edges[3]);
		nodes[3].addOutgoingEdge(edges[4]);
		nodes[3].addOutgoingEdge(edges[5]);
		nodes[8].addOutgoingEdge(edges[6]);
		nodes[9].addOutgoingEdge(edges[7]);
		nodes[0].addOutgoingEdge(edges[8]);
				
		nodes[5].addIncomingEdge(edges[0]);
		nodes[1].addIncomingEdge(edges[1]);
		nodes[2].addIncomingEdge(edges[2]);
		nodes[6].addIncomingEdge(edges[3]);
		nodes[7].addIncomingEdge(edges[4]);
		nodes[8].addIncomingEdge(edges[5]);
		nodes[4].addIncomingEdge(edges[6]);
		nodes[4].addIncomingEdge(edges[7]);
		nodes[5].addIncomingEdge(edges[8]);
		
		
	}

	/**
	 * Test method for {@link MapNode#getIncomingEdges()}.
	 */
	@Test
	public void testGetIncomingEdges() {


		boolean[] korrekt = new boolean[11];
		//Daten abholen
		MapEdge[] liste0 = nodes[0].getIncomingEdges();
		MapEdge[] liste1 = nodes[1].getIncomingEdges();
		MapEdge[] liste2 = nodes[2].getIncomingEdges();
		MapEdge[] liste3 = nodes[3].getIncomingEdges();
		MapEdge[] liste4 = nodes[4].getIncomingEdges();
		MapEdge[] liste5 = nodes[5].getIncomingEdges();
		MapEdge[] liste6 = nodes[6].getIncomingEdges();
		MapEdge[] liste7 = nodes[7].getIncomingEdges();
		MapEdge[] liste8 = nodes[8].getIncomingEdges();
		MapEdge[] liste9 = nodes[9].getIncomingEdges();
		MapEdge[] liste10 = nodes[10].getIncomingEdges();
		
		//Vergleiche Daten der leeren Listen
		korrekt[0] = (liste0.length == 0);
		korrekt[3] = (liste3.length == 0);
		korrekt[9] = (liste9.length == 0);
		korrekt[10] = (liste10.length == 0);
		//vergleiche andere Nodes (nciht trivial)
		//Node 1 - nur Kante 1
		korrekt[1] = ((liste1.length==1) && (liste1[0] == edges[1]));
		//Node 2 - nur Kante 2
		korrekt[2] = ((liste2.length==1) && (liste2[0] == edges[2]));
		//Node 4 - nur kanten 6,7 
		korrekt[4] = ((liste4.length==2) && ((liste4[0] == edges[6]) || (liste4[0] == edges[7])) && ((liste4[1] == edges[6]) || (liste4[1] == edges[7])));
		//Node 5 - nur kanten 0,8
		korrekt[5] = ((liste4.length==2) && ((liste5[0] == edges[0]) || (liste5[0] == edges[8])) && ((liste5[1] == edges[0]) || (liste5[1] == edges[8])));
		//Node 6 - nur Kante 3
		korrekt[6] = ((liste6.length==1) && (liste6[0] == edges[3]));
		//Node 7 - nur Kante 4
		korrekt[7] = ((liste7.length==1) && (liste7[0] == edges[4]));
		//Node 8 - nur Kante 5
		korrekt[8] = ((liste8.length==1) && (liste8[0] == edges[5]));
		
		for (int i=0;i<9;i++) {
			if (korrekt[i] == false) fail("Fehler bei IncomingEdges-Test "+i);
		}
	}

	/**
	 * Test method for {@link MapNode#getOutgoingEdges()}.
	 */
	@Test
	public void testGetOutgoingEdges() {

		boolean[] korrekt = new boolean[11];
		//Daten abholen
		MapEdge[] liste0 = nodes[0].getOutgoingEdges();
		MapEdge[] liste1 = nodes[1].getOutgoingEdges();
		MapEdge[] liste2 = nodes[2].getOutgoingEdges();
		MapEdge[] liste3 = nodes[3].getOutgoingEdges();
		MapEdge[] liste4 = nodes[4].getOutgoingEdges();
		MapEdge[] liste5 = nodes[5].getOutgoingEdges();
		MapEdge[] liste6 = nodes[6].getOutgoingEdges();
		MapEdge[] liste7 = nodes[7].getOutgoingEdges();
		MapEdge[] liste8 = nodes[8].getOutgoingEdges();
		MapEdge[] liste9 = nodes[9].getOutgoingEdges();
		MapEdge[] liste10 = nodes[10].getOutgoingEdges();
		
		/*
		for(int i=0; i<=10; i++) 
		{
			logger.log("MapNodeTest",nodes[i]);
		}
		*/
		//Vergleiche Daten der leeren Listen
		korrekt[1] = (liste1.length == 0);
		korrekt[4] = (liste4.length == 0);
		korrekt[7] = (liste7.length == 0);
		korrekt[10] = (liste10.length == 0);
		
		//Vergleiche andere Nodes
		//Node 0 - Nur Kanten 0,8
		korrekt[0] = ((liste0.length==2) && ((liste0[0] == edges[0]) || (liste0[0] == edges[8])) && ((liste0[1] == edges[0]) || (liste0[1] == edges[8])));
		//Node 2 - Nur Kante 3
		korrekt[2] = ((liste2.length==1) && (liste2[0] == edges[3]));
		//Node 3 - Nur Kanten 4,5
		korrekt[3] = ((liste3.length==2) && ((liste3[0] == edges[4]) || (liste3[0] == edges[5])) && ((liste3[1] == edges[4]) || (liste3[1] == edges[5])));
		//Node 5 - Nur Kante 1
		korrekt[5] = ((liste5.length==1) && (liste5[0] == edges[1]));
		//Node 6 - Nur Kante 2
		korrekt[6] = ((liste6.length==1) && (liste6[0] == edges[2]));
		//Node 8 - Nur Kante 6
		korrekt[8] = ((liste8.length==1) && (liste8[0] == edges[6]));
		//Node 9 - Nur Kante 7
		korrekt[9] = ((liste9.length==1) && (liste9[0] == edges[7]));
		
		for (int i=0;i<9;i++) {
			if (korrekt[i] == false) fail("Fehler bei OutgingEdges-Test "+i);
		}
		
	}
	
	/**
	 * test fuer das Loeschen von Kanten
	 */
	@Test
	public void testDeleteEdges() {
		/*
		 * Erstelle einen Graph mit Zwei kreuzungen und dazwischen zwe Verbindungsknoten
		 */
		MapNode[] nodes = new MapNode[9];
		for (int i=1; i<9;i++) {
			nodes[i] = new MapNode(i);
		}
		MapEdge[] edges = new MapEdge[14];
		int id = 0;
		edges[id] = new MapEdge(nodes[1], nodes[3], id++, 1, StreetType.MOTORWAY);
		edges[id] = new MapEdge(nodes[2], nodes[3], id++, 1, StreetType.MOTORWAY);
		edges[id] = new MapEdge(nodes[3], nodes[4], id++, 1, StreetType.MOTORWAY);
		edges[id] = new MapEdge(nodes[4], nodes[5], id++, 1, StreetType.MOTORWAY);
		edges[id] = new MapEdge(nodes[5], nodes[7], id++, 1, StreetType.MOTORWAY);
		
		edges[id] = new MapEdge(nodes[7], nodes[6], id++, 1, StreetType.MOTORWAY);
		edges[id] = new MapEdge(nodes[7], nodes[8], id++, 1, StreetType.MOTORWAY);
		
		//andere Richtung
		edges[id] = new MapEdge(nodes[3], nodes[1], id++, 1, StreetType.MOTORWAY);
		edges[id] = new MapEdge(nodes[3], nodes[2], id++, 1, StreetType.MOTORWAY);
		edges[id] = new MapEdge(nodes[4], nodes[3], id++, 1, StreetType.MOTORWAY);
		edges[id] = new MapEdge(nodes[5], nodes[4], id++, 1, StreetType.MOTORWAY);
		edges[id] = new MapEdge(nodes[7], nodes[5], id++, 1, StreetType.MOTORWAY);
		
		edges[id] = new MapEdge(nodes[6], nodes[7], id++, 1, StreetType.MOTORWAY);
		edges[id] = new MapEdge(nodes[8], nodes[7], id++, 1, StreetType.MOTORWAY);
		
		//Fuege zuerst nur in eine Richtung ein
		//Fuege die outgoing ein
		nodes[1].addOutgoingEdge(edges[0]);
		nodes[2].addOutgoingEdge(edges[1]);
		nodes[3].addOutgoingEdge(edges[2]);
		nodes[4].addOutgoingEdge(edges[3]);
		nodes[5].addOutgoingEdge(edges[4]);
		nodes[7].addOutgoingEdge(edges[5]);
		nodes[7].addOutgoingEdge(edges[6]);
		
		//Fuege nun die inc. ein
		nodes[3].addIncomingEdge(edges[0]);
		nodes[3].addIncomingEdge(edges[1]);
		nodes[4].addIncomingEdge(edges[2]);
		nodes[5].addIncomingEdge(edges[3]);
		nodes[6].addIncomingEdge(edges[5]);
		nodes[8].addIncomingEdge(edges[6]);
		nodes[7].addIncomingEdge(edges[4]);
		
//		/*
//		 * Erste Testreihe! - getnextCrossings wurde ersatzlos gestrichen
//		 */
//		
//		Vertex[] testArray = null;
//		testArray = nodes[6].getNextCrossings(null,true);
//		if (testArray != null)
//			fail("Es wurde eine Rueckgabe ausgegeben auf einer Sackgasse (ger)");
//		
//		testArray = nodes[7].getNextCrossings(null,true);
//		if (testArray.length != 1 || testArray[0].getID()!=nodes[7].getUID())
//			fail("Auf einer Kreuzung wurde eine falsche Rueckgabe geliefert (ger)");
//		
//		testArray = nodes[4].getNextCrossings(null,true);
//		if (testArray.length != 1 || testArray[0].getID()!=nodes[7].getUID())
//			fail("Auf einer Einbahnstrasse wurde eine falsche Rueckgabe geliefert (ger)");
//		
//		/*
//		 * der gleiche Spass nun aber auch mal andersrum - soll heissen, jetzt ist die booelan fuer die richtung mal negativ
//		 */
//		
//		testArray = nodes[4].getNextCrossings(null,false);
//		if (testArray.length != 1 || testArray[0].getID()!=nodes[3].getUID())
//			fail("Auf einer Einbahnstrasse wurde eine falsche Rueckgabe geliefert (ger,rueck)");
//		
//		testArray = nodes[1].getNextCrossings(null,false);
//		if (testArray != null)
//			fail("Auf einer Sackgasse wurde eine falsche Rueckgabe geliefert (ger,rueck)");
//		
//		testArray = nodes[3].getNextCrossings(null,false);
//		if (testArray.length != 1 || testArray[0].getID()!=nodes[3].getUID())
//			fail("Auf einer Kreuzung wurde eine falsche Rueckgabe geliefert (ger,rueck)");
		
		/*
		 * jetzt mach das ganze al ungerichtet
		 */
		//Fuege die outgoing ein
		nodes[3].addOutgoingEdge(edges[7]);
		nodes[3].addOutgoingEdge(edges[8]);
		nodes[4].addOutgoingEdge(edges[9]);
		nodes[5].addOutgoingEdge(edges[10]);
		nodes[7].addOutgoingEdge(edges[11]);
		nodes[6].addOutgoingEdge(edges[12]);
		nodes[8].addOutgoingEdge(edges[13]);
		
		//Fuege nun die inc. ein
		nodes[1].addIncomingEdge(edges[7]);
		nodes[2].addIncomingEdge(edges[8]);
		nodes[3].addIncomingEdge(edges[9]);
		nodes[4].addIncomingEdge(edges[10]);
		nodes[5].addIncomingEdge(edges[11]);
		nodes[7].addIncomingEdge(edges[12]);
		nodes[7].addIncomingEdge(edges[13]);
		
//		/*
//		 * Zweite Testreihe - getNextCrssings ersatzlos gestrihen
//		 */
//		
//		testArray = nodes[6].getNextCrossings(null,true);
//		if (testArray.length != 1 || testArray[0].getID()!=nodes[7].getUID())
//			fail("Auf einer Sackgasse wurde eine falsche Rueckgabe geliefert");
//		
//		testArray = nodes[7].getNextCrossings(null,true);
//		if (testArray.length != 1 || testArray[0].getID()!=nodes[7].getUID())
//			fail("Auf einer Kreuzung wurde eine falsche Rueckgabe geliefert");
//		
//		
//		testArray = nodes[4].getNextCrossings(null,true);
//		
//		if (testArray.length != 2)
//			fail("Auf einer Strasse wurde eine falsche Rueckgabe geliefert");
//		
//		if (testArray[0].getID() != nodes[7].getUID() && testArray[1].getID() != nodes[7].getUID())
//			fail("Die Ausgabe beinhaltet Kreuzung 7 nicht");
//		if (testArray[0].getID() != nodes[3].getUID() && testArray[1].getID() != nodes[3].getUID())
//			fail("Die Ausgabe beinhaltet Kreuzung 3 nicht");
//		
//		/*
//		 * Und auch diese Testreihe bitte in die andere Richtung (erwarte aber gleiches ergebnis, da ungerichtet)
//		 */
//		
//		testArray = nodes[6].getNextCrossings(null,false);
//		if (testArray.length != 1 || testArray[0].getID()!=nodes[7].getUID())
//			fail("Auf einer Sackgasse wurde eine falsche Rueckgabe geliefert");
//		
//		testArray = nodes[7].getNextCrossings(null,false);
//		if (testArray.length != 1 || testArray[0].getID()!=nodes[7].getUID())
//			fail("Auf einer Kreuzung wurde eine falsche Rueckgabe geliefert");
//		
//		
//		testArray = nodes[4].getNextCrossings(null,false);
//		
//		if (testArray.length != 2)
//			fail("Auf einer Strasse wurde eine falsche Rueckgabe geliefert");
//		
//		if (testArray[0].getID() != nodes[7].getUID() && testArray[1].getID() != nodes[7].getUID())
//			fail("Die Ausgabe beinhaltet Kreuzung 7 nicht");
//		if (testArray[0].getID() != nodes[3].getUID() && testArray[1].getID() != nodes[3].getUID())
//			fail("Die Ausgabe beinhaltet Kreuzung 3 nicht");
		
		/*
		 * Testroutine fuer das loeschen von Kanten
		 */
		if (nodes[7].deleteIncomingEdge(edges[10]))
			fail("Es wurde eine positive Rueckmeldung gegeben beim versuch eine falsche kante zu entfernen (Incioming)");
		if (nodes[7].deleteOutgoingEdge(edges[7]))
			fail("Es wurde euine positive Rueckmeldung gegeben beim Versuch eine falsche Kante zu entfernen (Outgoing)");
		
		if (!nodes[4].deleteOutgoingEdge(edges[3]))
			fail("Es gab einen Fehler beim loeschen einer gueltigen Kante (outgoing)");
		try {
			if (nodes[4].getOutgoingEdges().length != 1 || nodes[4].getOutgoingEdges()[0]!=edges[9])
				fail("Scheinbar wurde eine falsche Kante geloescht stat der gewuenschten (outgoing)");
		} catch (Exception e) {
			fail("Es gab eine Exception beim Zugriff auf bearbeiteten Knoten (Outgoing - aufscheiben des Arrays vergessen?): "+e.getLocalizedMessage());
		}
		
		if (!nodes[4].deleteIncomingEdge(edges[2]))
			fail("Es gab einen Fehler beim loeschen einer gueltigen Kante (incoming)");
		try {
			if (nodes[4].getIncomingEdges().length != 1 || nodes[4].getIncomingEdges()[0]!=edges[10])
				fail("Scheinbar wurde eine falsche Kante geloescht stat der gewuenschten (incoming)");
		} catch (Exception e) {
			fail("Es gab eine Exception beim Zugriff auf bearbeiteten Knoten (incoming - aufscheiben des Arrays vergessen?): "+e.getLocalizedMessage());
		}
	}
	
	

}
