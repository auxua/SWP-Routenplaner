/**
 * 
 */
package graphenbib;

import static org.junit.Assert.fail;
import graphexceptions.InvalidInputException;
import graphexceptions.NodeNotNeighbourOfPreviousElementInPathException;
import graphexceptions.PathNotFullyInitialized;

import java.util.ArrayList;
import java.util.HashSet;

import org.junit.BeforeClass;
import org.junit.Test;


public class HierarchyMapGraphNodeTest {

	private HierarchyMapGraph testGraph = null;
	
	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {

		
	}
	/**
	 * Dieser Test testes das korrekte Loeschen der Incomin Kanten aus einer MapNode
	 */
	@Test
	public void DeleteTest() {
		//Notiz: Dieser Test ist angepasst und erweitert worden.
		//Es werden nun auch ausgehende Kanten geprueft und die kanten-Konstruktoren wurden korrigiert
		HierarchyMapNode testNode1=new HierarchyMapNode(1, 2, 2);
		HierarchyMapNode testNode2=new HierarchyMapNode(2, 100, 10);
		HierarchyMapNode testNode3=new HierarchyMapNode(3,0, 0);
		HierarchyMapEdge testEdge1=new HierarchyMapEdge(testNode2, testNode1, 1, 10, StreetType.MOTORWAY, (byte)0);
		HierarchyMapEdge testEdge2=new HierarchyMapEdge(testNode3, testNode1, 2, 10, StreetType.MOTORWAY, (byte)0);
		HierarchyMapEdge testEdge3=new HierarchyMapEdge(testNode3, testNode2, 3, 10, StreetType.MOTORWAY, (byte)0);
		
		testNode1.addIncomingEdge(testEdge1);
		testNode1.addIncomingEdge(testEdge2);
		if(testNode1.getNumberOfIncomingEdges((byte)0)!=2) {
			fail("Fehler in testDeleteEdges (Inc): Edges konnten nicht hinzugefuegt werden.");
		}
		boolean deleteSucess=testNode1.deleteIncomingEdge(testEdge1);
		if(testNode1.getNumberOfIncomingEdges((byte)0)!=1 || deleteSucess==false || 
				testNode1.getIncomingEdgesByHierarchy((byte)0).contains(testEdge1)) {
			fail("Fehler in testDeleteEdges (Inc): Edges nicht erfolgreich geloescht.");
		}
		if(testNode1.deleteIncomingEdge(testEdge1)){
			fail("Fehler in testDeleteEdges (Inc): deleteIncomingEdges sollte nun false zuerueckgeben, da Edge nicht mehr vorhanden ist.");
		}
		testNode1.deleteIncomingEdge(testEdge2);
		if(!testNode1.getIncomingEdgesByHierarchy((byte)0).isEmpty()) {
			fail("Fehler in testDeleteEdges (Inc): Nun sollten keine Kanten mehr vorhanden sein.");
		}
		
		//Teste nun mit ausgehende Kanten (Nutze aus Gruenden der Konsistenz gleichen Ausbau des Testablaufes
		testNode3.addOutgoingEdge(testEdge2);
		testNode3.addOutgoingEdge(testEdge3);
		
		if(testNode3.getNumberOfOutgoingEdges((byte)0)!=2) {
			fail("Fehler in testDeleteEdges (Outg.): Edges konnten nicht hinzugefuegt werden.");
		}
		
		deleteSucess=testNode3.deleteOutgoingEdge(testEdge2);
		if(testNode3.getNumberOfOutgoingEdges((byte)0)!=1 || deleteSucess==false || 
				testNode3.getOutgoingEdgesByHierarchy((byte)0).contains(testEdge2)) {
			fail("Fehler in testDeleteEdges (Outg.): Edges nicht erfolgreich geloescht.");
		}
		
		if(testNode3.deleteOutgoingEdge(testEdge1)){
			fail("Fehler in testDeleteEdges (Outg.): deleteOutgoingEdges sollte nun false zuerueckgeben, da Edge nicht mehr vorhanden ist.");
		}
		
		testNode3.deleteOutgoingEdge(testEdge3);
		if(!testNode3.getOutgoingEdgesByHierarchy((byte)0).isEmpty()) {
			fail("Fehler in testDeleteEdges (Outg.): Nun sollten keine Kanten mehr vorhanden sein.");
		}
	}
	
	@Test
	public void NeighbourTest() {
		HierarchyMapNode[] testNodes = new HierarchyMapNode[7];
		HierarchyMapEdge[] testEdges = new HierarchyMapEdge[6];
		
		for(int i=1; i<7; i++) {
			testNodes[i] = new HierarchyMapNode(i, 1, 1);
		}
		for(int i=1; i<6; i++) {
			testEdges[i] = new HierarchyMapEdge(testNodes[1], testNodes[i+1], i, i, StreetType.LIVING_STREET, (byte)i);
			testNodes[1].addOutgoingEdge(testEdges[i]);
		}
		
		HashSet<HierarchyMapNode> set = testNodes[1].getNeighbours((byte)1);
		if (set.size() != 5)
			fail("Die Nachbarschaft wird nciht korrekt ausgegeben. Soll: 5 Ist: "+set.size());
		
		set = testNodes[1].getNeighbours((byte)2);
		if (set.size() != 4)
			fail("Die Nachbarschaft wird nciht korrekt ausgegeben. Soll: 4 Ist: "+set.size());
		
		set = testNodes[1].getNeighbours((byte)3);
		if (set.size() != 3)
			fail("Die Nachbarschaft wird nciht korrekt ausgegeben. Soll: 3 Ist: "+set.size());
		
		set = testNodes[1].getNeighbours((byte)4);
		if (set.size() != 2)
			fail("Die Nachbarschaft wird nciht korrekt ausgegeben. Soll: 2 Ist: "+set.size());
		
		set = testNodes[1].getNeighbours((byte)5);
		if ((set.size() != 1) || !(set.contains(testNodes[6])))
			fail("Die Nachbarschaft wird nciht korrekt ausgegeben. Soll: 1 Ist: "+set.size());
		
		
	}
	
	@Test
	public void PredecessorTest() {
		
		HierarchyMapNode[] testNodes = new HierarchyMapNode[7];
		HierarchyMapEdge[] testEdges = new HierarchyMapEdge[6];
		
		for(int i=1; i<7; i++) {
			testNodes[i] = new HierarchyMapNode(i, 1, 1);
		}
		for(int i=1; i<6; i++) {
			testEdges[i] = new HierarchyMapEdge(testNodes[i+1], testNodes[1], i, i, StreetType.LIVING_STREET, (byte)i);
			testNodes[1].addIncomingEdge(testEdges[i]);
		}
		
		HashSet<HierarchyMapNode> set = testNodes[1].getPredecessors((byte)1);
		if (set.size() != 5)
			fail("Die Nachbarschaft wird nciht korrekt ausgegeben. Soll: 5 Ist: "+set.size());
		
		set = testNodes[1].getPredecessors((byte)2);
		if (set.size() != 4)
			fail("Die Nachbarschaft wird nciht korrekt ausgegeben. Soll: 4 Ist: "+set.size());
		
		set = testNodes[1].getPredecessors((byte)3);
		if (set.size() != 3)
			fail("Die Nachbarschaft wird nciht korrekt ausgegeben. Soll: 3 Ist: "+set.size());
		
		set = testNodes[1].getPredecessors((byte)4);
		if (set.size() != 2)
			fail("Die Nachbarschaft wird nciht korrekt ausgegeben. Soll: 2 Ist: "+set.size());
		
		set = testNodes[1].getPredecessors((byte)5);
		if ((set.size() != 1) || !(set.contains(testNodes[6])))
			fail("Die Nachbarschaft wird nciht korrekt ausgegeben. Soll: 1 Ist: "+set.size());
	}
	
	/**
	 * Dieser Test prueft ob das Anlegen und befuellen korrekt funzt
	 */
	@Test
	public void CreateTest() {
		
		/*
		 * Erstelle den Graphen
		 */
		try {
			testGraph = new HierarchyMapGraph();
		} catch (Exception e) {
			fail("Fehler beim erstellen des Graphen: "+e.getLocalizedMessage());
		}
		
		/*
		 * Fuege Knoten ein
		 */
		try {
			for (int i=0; i<7; i++)
				testGraph.insertNode(i,1,1);
		} catch (Exception e) {
			fail("Fehler beim einfuegen der Nodes in den Graphen: "+e.getLocalizedMessage());
		}
		
		/*
		 * Fuege Kanten ein mit Levels
		 */
		try {
			testGraph.insertEdge(1, 2, 1, 10, StreetType.MOTORWAY, (byte) 5);
			testGraph.insertEdge(2, 3, 2, 10, StreetType.MOTORWAY, (byte) 4);
			testGraph.insertEdge(3, 4, 3, 10, StreetType.MOTORWAY, (byte) 4);
			testGraph.insertEdge(4, 5, 4, 10, StreetType.MOTORWAY, (byte) 2);
			testGraph.insertEdge(5, 6, 5, 10, StreetType.MOTORWAY, (byte) 1);
			
			testGraph.insertEdge(5, 4, 6, 10, StreetType.MOTORWAY, (byte) 2);
			testGraph.insertEdge(4, 3, 7, 10, StreetType.MOTORWAY, (byte) 4);
			testGraph.insertEdge(3, 2, 8, 10, StreetType.MOTORWAY, (byte) 4);
			testGraph.insertEdge(2, 1, 9, 10, StreetType.MOTORWAY, (byte) 5);
			testGraph.insertEdge(6, 5, 10, 10, StreetType.MOTORWAY, (byte) 1);
		} catch (Exception e) {
			fail("Fehler beim einfuegen der Nodes: "+e.getLocalizedMessage());
		}
		
		/*
		 * pruefe nun korrekte eingehenden Kantenausgaben bzgl. Level 1
		 */
		HierarchyMapNode testNode = testGraph.getNode(1);
		if (testNode.getIncomingEdgesByHierarchy((byte) 1).size() !=1)
			fail("Node 1 sollte auf Level eins genau 1 eingehende Kanten haben. Statdessen: "+testNode.getIncomingEdgesByHierarchy((byte) 1).size());
		
		testNode = testGraph.getNode(2);
		if (testNode.getIncomingEdgesByHierarchy((byte) 1).size() !=2)
			fail("Node 2 sollte auf Level eins genau 2 eingehende Kanten haben. Statdessen: "+testNode.getIncomingEdgesByHierarchy((byte) 1).size());
		
		testNode = testGraph.getNode(3);
		if (testNode.getIncomingEdgesByHierarchy((byte) 1).size() !=2)
			fail("Node 3 sollte auf Level eins genau 2 eingehende Kanten haben. Statdessen: "+testNode.getIncomingEdgesByHierarchy((byte) 1).size());
		
		testNode = testGraph.getNode(4);
		if (testNode.getIncomingEdgesByHierarchy((byte) 1).size() !=2)
			fail("Node 4 sollte auf Level eins genau 2 eingehende Kanten haben. Statdessen: "+testNode.getIncomingEdgesByHierarchy((byte) 1).size());
		
		testNode = testGraph.getNode(5);
		if (testNode.getIncomingEdgesByHierarchy((byte) 1).size() !=2)
			fail("Node 5 sollte auf Level eins genau 2 eingehende Kanten haben. Statdessen: "+testNode.getIncomingEdgesByHierarchy((byte) 1).size());
		
		testNode = testGraph.getNode(6);
		if (testNode.getIncomingEdgesByHierarchy((byte) 1).size() !=1)
			fail("Node 6 sollte auf Level eins genau 1 eingehende Kanten haben. Statdessen: "+testNode.getIncomingEdgesByHierarchy((byte) 1).size());
		
		/*
		 * pruefe nun korrekte ausgehenden Kantenausgaben bzgl. Level 1
		 */
		testNode = testGraph.getNode(1);
		if (testNode.getOutgoingEdgesByHierarchy((byte) 1).size() !=1)
			fail("Node 1 sollte auf Level eins genau 1 eingehende Kanten haben. Statdessen: "+testNode.getOutgoingEdgesByHierarchy((byte) 1).size());
		
		testNode = testGraph.getNode(2);
		if (testNode.getOutgoingEdgesByHierarchy((byte) 1).size() !=2)
			fail("Node 2 sollte auf Level eins genau 2 eingehende Kanten haben. Statdessen: "+testNode.getOutgoingEdgesByHierarchy((byte) 1).size());
		
		testNode = testGraph.getNode(3);
		if (testNode.getOutgoingEdgesByHierarchy((byte) 1).size() !=2)
			fail("Node 3 sollte auf Level eins genau 2 eingehende Kanten haben. Statdessen: "+testNode.getOutgoingEdgesByHierarchy((byte) 1).size());
		
		testNode = testGraph.getNode(4);
		if (testNode.getOutgoingEdgesByHierarchy((byte) 1).size() !=2)
			fail("Node 4 sollte auf Level eins genau 2 eingehende Kanten haben. Statdessen: "+testNode.getOutgoingEdgesByHierarchy((byte) 1).size());
		
		testNode = testGraph.getNode(5);
		if (testNode.getOutgoingEdgesByHierarchy((byte) 1).size() !=2)
			fail("Node 5 sollte auf Level eins genau 2 eingehende Kanten haben. Statdessen: "+testNode.getOutgoingEdgesByHierarchy((byte) 1).size());
		
		testNode = testGraph.getNode(6);
		if (testNode.getOutgoingEdgesByHierarchy((byte) 1).size() !=1)
			fail("Node 6 sollte auf Level eins genau 1 eingehende Kanten haben. Statdessen: "+testNode.getOutgoingEdgesByHierarchy((byte) 1).size());
		
		
		
		/*
		 * pruefe nun korrekte ausgehenden Kantenausgaben bzgl. Level 2
		 */
		testNode = testGraph.getNode(1);
		if (testNode.getOutgoingEdgesByHierarchy((byte) 2).size() !=1)
			fail("Node 1 sollte auf Level zwei genau 1 eingehende Kanten haben. Statdessen: "+testNode.getOutgoingEdgesByHierarchy((byte) 2).size());
		
		testNode = testGraph.getNode(2);
		if (testNode.getOutgoingEdgesByHierarchy((byte) 2).size() !=2)
			fail("Node 2 sollte auf Level zwei genau 2 eingehende Kanten haben. Statdessen: "+testNode.getOutgoingEdgesByHierarchy((byte) 2).size());
		
		testNode = testGraph.getNode(3);
		if (testNode.getOutgoingEdgesByHierarchy((byte) 2).size() !=2)
			fail("Node 3 sollte auf Level zwei genau 2 eingehende Kanten haben. Statdessen: "+testNode.getOutgoingEdgesByHierarchy((byte) 2).size());
		
		testNode = testGraph.getNode(4);
		if (testNode.getOutgoingEdgesByHierarchy((byte) 2).size() !=2)
			fail("Node 4 sollte auf Level zwei genau 2 eingehende Kanten haben. Statdessen: "+testNode.getOutgoingEdgesByHierarchy((byte) 2).size());
		
		testNode = testGraph.getNode(5);
		if (testNode.getOutgoingEdgesByHierarchy((byte) 2).size() !=1)
			fail("Node 5 sollte auf Level zwei genau 1 eingehende Kanten haben. Statdessen: "+testNode.getOutgoingEdgesByHierarchy((byte) 2).size());
		
		testNode = testGraph.getNode(6);
		if (testNode.getOutgoingEdgesByHierarchy((byte) 2).size() !=0)
			fail("Node 6 sollte auf Level zwei genau 0 eingehende Kanten haben. Statdessen: "+testNode.getOutgoingEdgesByHierarchy((byte) 2).size());
		
		/*
		 * pruefe nun korrekte eingehenden Kantenausgaben bzgl. Level 2
		 */
		testNode = testGraph.getNode(1);
		if (testNode.getIncomingEdgesByHierarchy((byte) 2).size() !=1)
			fail("Node 1 sollte auf Level zwei genau 1 eingehende Kanten haben. Statdessen: "+testNode.getIncomingEdgesByHierarchy((byte) 2).size());
		
		testNode = testGraph.getNode(2);
		if (testNode.getIncomingEdgesByHierarchy((byte) 2).size() !=2)
			fail("Node 2 sollte auf Level zwei genau 2 eingehende Kanten haben. Statdessen: "+testNode.getIncomingEdgesByHierarchy((byte) 2).size());
		
		testNode = testGraph.getNode(3);
		if (testNode.getIncomingEdgesByHierarchy((byte) 2).size() !=2)
			fail("Node 3 sollte auf Level zwei genau 2 eingehende Kanten haben. Statdessen: "+testNode.getIncomingEdgesByHierarchy((byte) 2).size());
		
		testNode = testGraph.getNode(4);
		if (testNode.getIncomingEdgesByHierarchy((byte) 2).size() !=2)
			fail("Node 4 sollte auf Level zwei genau 2 eingehende Kanten haben. Statdessen: "+testNode.getIncomingEdgesByHierarchy((byte) 2).size());
		
		testNode = testGraph.getNode(5);
		if (testNode.getIncomingEdgesByHierarchy((byte) 2).size() !=1)
			fail("Node 5 sollte auf Level zwei genau 1 eingehende Kanten haben. Statdessen: "+testNode.getIncomingEdgesByHierarchy((byte) 2).size());
		
		testNode = testGraph.getNode(6);
		if (testNode.getIncomingEdgesByHierarchy((byte) 2).size() !=0)
			fail("Node 6 sollte auf Level zwei genau 0 eingehende Kanten haben. Statdessen: "+testNode.getIncomingEdgesByHierarchy((byte) 2).size());
		
		
		
		/*
		 * pruefe nun korrekte ausgehenden Kantenausgaben bzgl. Level 3
		 */
		testNode = testGraph.getNode(1);
		if (testNode.getOutgoingEdgesByHierarchy((byte) 3).size() !=1)
			fail("Node 1 sollte auf Level drei genau 1 eingehende Kanten haben. Statdessen: "+testNode.getOutgoingEdgesByHierarchy((byte) 3).size());
		
		testNode = testGraph.getNode(2);
		if (testNode.getOutgoingEdgesByHierarchy((byte) 3).size() !=2)
			fail("Node 2 sollte auf Level drei genau 2 eingehende Kanten haben. Statdessen: "+testNode.getOutgoingEdgesByHierarchy((byte) 3).size());
		
		testNode = testGraph.getNode(3);
		if (testNode.getOutgoingEdgesByHierarchy((byte) 3).size() !=2)
			fail("Node 3 sollte auf Level drei genau 2 eingehende Kanten haben. Statdessen: "+testNode.getOutgoingEdgesByHierarchy((byte) 3).size());
		
		testNode = testGraph.getNode(4);
		if (testNode.getOutgoingEdgesByHierarchy((byte) 3).size() !=1)
			fail("Node 4 sollte auf Level drei genau 1 eingehende Kanten haben. Statdessen: "+testNode.getOutgoingEdgesByHierarchy((byte) 3).size());
		
		testNode = testGraph.getNode(5);
		if (testNode.getOutgoingEdgesByHierarchy((byte) 3).size() !=0)
			fail("Node 5 sollte auf Level drei genau 0 eingehende Kanten haben. Statdessen: "+testNode.getOutgoingEdgesByHierarchy((byte) 3).size());
		
		testNode = testGraph.getNode(6);
		if (testNode.getOutgoingEdgesByHierarchy((byte) 3).size() !=0)
			fail("Node 6 sollte auf Level drei genau 0 eingehende Kanten haben. Statdessen: "+testNode.getOutgoingEdgesByHierarchy((byte) 3).size());
		
		/*
		 * pruefe nun korrekte eingehenden Kantenausgaben bzgl. Level 3
		 */
		testNode = testGraph.getNode(1);
		if (testNode.getIncomingEdgesByHierarchy((byte) 3).size() !=1)
			fail("Node 1 sollte auf Level drei genau 1 eingehende Kanten haben. Statdessen: "+testNode.getIncomingEdgesByHierarchy((byte) 3).size());
		
		testNode = testGraph.getNode(2);
		if (testNode.getIncomingEdgesByHierarchy((byte) 3).size() !=2)
			fail("Node 2 sollte auf Level drei genau 2 eingehende Kanten haben. Statdessen: "+testNode.getIncomingEdgesByHierarchy((byte) 3).size());
		
		testNode = testGraph.getNode(3);
		if (testNode.getIncomingEdgesByHierarchy((byte) 3).size() !=2)
			fail("Node 3 sollte auf Level drei genau 2 eingehende Kanten haben. Statdessen: "+testNode.getIncomingEdgesByHierarchy((byte) 3).size());
		
		testNode = testGraph.getNode(4);
		if (testNode.getIncomingEdgesByHierarchy((byte) 3).size() !=1)
			fail("Node 4 sollte auf Level drei genau 1 eingehende Kanten haben. Statdessen: "+testNode.getIncomingEdgesByHierarchy((byte) 3).size());
		
		testNode = testGraph.getNode(5);
		if (testNode.getIncomingEdgesByHierarchy((byte) 3).size() !=0)
			fail("Node 5 sollte auf Level drei genau 0 eingehende Kanten haben. Statdessen: "+testNode.getIncomingEdgesByHierarchy((byte) 3).size());
		
		testNode = testGraph.getNode(6);
		if (testNode.getIncomingEdgesByHierarchy((byte) 3).size() !=0)
			fail("Node 6 sollte auf Level drei genau 0 eingehende Kanten haben. Statdessen: "+testNode.getIncomingEdgesByHierarchy((byte) 3).size());
		
		
		/*
		 * pruefe nun korrekte ausgehenden Kantenausgaben bzgl. Level 5
		 */
		testNode = testGraph.getNode(1);
		if (testNode.getOutgoingEdgesByHierarchy((byte) 5).size() !=1)
			fail("Node 1 sollte auf Level fuenf genau 1 eingehende Kanten haben. Statdessen: "+testNode.getOutgoingEdgesByHierarchy((byte) 5).size());
		
		testNode = testGraph.getNode(2);
		if (testNode.getOutgoingEdgesByHierarchy((byte) 5).size() !=1)
			fail("Node 2 sollte auf Level fuenf genau 1 eingehende Kanten haben. Statdessen: "+testNode.getOutgoingEdgesByHierarchy((byte) 5).size());
		
		testNode = testGraph.getNode(3);
		if (testNode.getOutgoingEdgesByHierarchy((byte) 5).size() !=0)
			fail("Node 3 sollte auf Level fuenf genau 0 eingehende Kanten haben. Statdessen: "+testNode.getOutgoingEdgesByHierarchy((byte) 5).size());
		
		testNode = testGraph.getNode(4);
		if (testNode.getOutgoingEdgesByHierarchy((byte) 5).size() !=0)
			fail("Node 4 sollte auf Level fuenf genau 0 eingehende Kanten haben. Statdessen: "+testNode.getOutgoingEdgesByHierarchy((byte) 5).size());
		
		testNode = testGraph.getNode(5);
		if (testNode.getOutgoingEdgesByHierarchy((byte) 5).size() !=0)
			fail("Node 5 sollte auf Level fuenf genau 0 eingehende Kanten haben. Statdessen: "+testNode.getOutgoingEdgesByHierarchy((byte) 5).size());
		
		testNode = testGraph.getNode(6);
		if (testNode.getOutgoingEdgesByHierarchy((byte) 5).size() !=0)
			fail("Node 6 sollte auf Level fuenf genau 0 eingehende Kanten haben. Statdessen: "+testNode.getOutgoingEdgesByHierarchy((byte) 5).size());
		
		/*
		 * pruefe nun korrekte eingehenden Kantenausgaben bzgl. Level 5
		 */
		testNode = testGraph.getNode(1);
		if (testNode.getIncomingEdgesByHierarchy((byte) 5).size() !=1)
			fail("Node 1 sollte auf Level fuenf genau 1 eingehende Kanten haben. Statdessen: "+testNode.getIncomingEdgesByHierarchy((byte) 5).size());
		
		testNode = testGraph.getNode(2);
		if (testNode.getIncomingEdgesByHierarchy((byte) 5).size() !=1)
			fail("Node 2 sollte auf Level fuenf genau 1 eingehende Kanten haben. Statdessen: "+testNode.getIncomingEdgesByHierarchy((byte) 5).size());
		
		testNode = testGraph.getNode(3);
		if (testNode.getIncomingEdgesByHierarchy((byte) 5).size() !=0)
			fail("Node 3 sollte auf Level fuenf genau 0 eingehende Kanten haben. Statdessen: "+testNode.getIncomingEdgesByHierarchy((byte) 5).size());
		
		testNode = testGraph.getNode(4);
		if (testNode.getIncomingEdgesByHierarchy((byte) 5).size() !=0)
			fail("Node 4 sollte auf Level fuenf genau 0 eingehende Kanten haben. Statdessen: "+testNode.getIncomingEdgesByHierarchy((byte) 5).size());
		
		testNode = testGraph.getNode(5);
		if (testNode.getIncomingEdgesByHierarchy((byte) 5).size() !=0)
			fail("Node 5 sollte auf Level fuenf genau 0 eingehende Kanten haben. Statdessen: "+testNode.getIncomingEdgesByHierarchy((byte) 5).size());
		
		testNode = testGraph.getNode(6);
		if (testNode.getIncomingEdgesByHierarchy((byte) 5).size() !=0)
			fail("Node 6 sollte auf Level fuenf genau 0 eingehende Kanten haben. Statdessen: "+testNode.getIncomingEdgesByHierarchy((byte) 5).size());
		
		
		
		/*
		 * pruefe nun korrekte ausgehenden Kantenausgaben bzgl. Level 6
		 */
		testNode = testGraph.getNode(1);
		if (testNode.getOutgoingEdgesByHierarchy((byte) 6).size() !=0)
			fail("Node 1 sollte auf Level sechs genau 0 eingehende Kanten haben. Statdessen: "+testNode.getOutgoingEdgesByHierarchy((byte) 6).size());
		
		testNode = testGraph.getNode(2);
		if (testNode.getOutgoingEdgesByHierarchy((byte) 6).size() !=0)
			fail("Node 2 sollte auf Level sechs genau 0 eingehende Kanten haben. Statdessen: "+testNode.getOutgoingEdgesByHierarchy((byte) 6).size());
		
		testNode = testGraph.getNode(3);
		if (testNode.getOutgoingEdgesByHierarchy((byte) 6).size() !=0)
			fail("Node 3 sollte auf Level sechs genau 0 eingehende Kanten haben. Statdessen: "+testNode.getOutgoingEdgesByHierarchy((byte) 6).size());
		
		testNode = testGraph.getNode(4);
		if (testNode.getOutgoingEdgesByHierarchy((byte) 6).size() !=0)
			fail("Node 4 sollte auf Level sechs genau 0 eingehende Kanten haben. Statdessen: "+testNode.getOutgoingEdgesByHierarchy((byte) 6).size());
		
		testNode = testGraph.getNode(5);
		if (testNode.getOutgoingEdgesByHierarchy((byte) 6).size() !=0)
			fail("Node 5 sollte auf Level sechs genau 0 eingehende Kanten haben. Statdessen: "+testNode.getOutgoingEdgesByHierarchy((byte) 6).size());
		
		testNode = testGraph.getNode(6);
		if (testNode.getOutgoingEdgesByHierarchy((byte) 6).size() !=0)
			fail("Node 6 sollte auf Level sechs genau 0 eingehende Kanten haben. Statdessen: "+testNode.getOutgoingEdgesByHierarchy((byte) 6).size());
		
		/*
		 * pruefe nun korrekte eingehenden Kantenausgaben bzgl. Level 6
		 */
		testNode = testGraph.getNode(1);
		if (testNode.getIncomingEdgesByHierarchy((byte) 6).size() !=0)
			fail("Node 1 sollte auf Level sechs genau 0 eingehende Kanten haben. Statdessen: "+testNode.getIncomingEdgesByHierarchy((byte) 6).size());
		
		testNode = testGraph.getNode(2);
		if (testNode.getIncomingEdgesByHierarchy((byte) 6).size() !=0)
			fail("Node 2 sollte auf Level sechs genau 0 eingehende Kanten haben. Statdessen: "+testNode.getIncomingEdgesByHierarchy((byte) 6).size());
		
		testNode = testGraph.getNode(3);
		if (testNode.getIncomingEdgesByHierarchy((byte) 6).size() !=0)
			fail("Node 3 sollte auf Level sechs genau 0 eingehende Kanten haben. Statdessen: "+testNode.getIncomingEdgesByHierarchy((byte) 6).size());
		
		testNode = testGraph.getNode(4);
		if (testNode.getIncomingEdgesByHierarchy((byte) 6).size() !=0)
			fail("Node 4 sollte auf Level sechs genau 0 eingehende Kanten haben. Statdessen: "+testNode.getIncomingEdgesByHierarchy((byte) 6).size());
		
		testNode = testGraph.getNode(5);
		if (testNode.getIncomingEdgesByHierarchy((byte) 6).size() !=0)
			fail("Node 5 sollte auf Level sechs genau 0 eingehende Kanten haben. Statdessen: "+testNode.getIncomingEdgesByHierarchy((byte) 6).size());
		
		testNode = testGraph.getNode(6);
		if (testNode.getIncomingEdgesByHierarchy((byte) 6).size() !=0)
			fail("Node 6 sollte auf Level sechs genau 0 eingehende Kanten haben. Statdessen: "+testNode.getIncomingEdgesByHierarchy((byte) 6).size());
		
		/*
		 * Einfuegen einer Edge ueber die alte Methode
		 * Logisch zu erwarten ist ein Level=1
		 */
		
		/*try {
			testGraph.insertNode(20);
			testGraph.insertNode(30);
			testGraph.insertEdge(20, 30, 500, 10f, StreetType.MOTORWAY,3);
		} catch (Exception e) {
			fail("Das Einfuegen einer Edge funktioniert nciht ohne Level-angabe, wird aber dennoch angeboten. Fehler: "+e.getLocalizedMessage());
		}
		if ((testGraph.getNode(30).getIncomingEdgesByHierarchy((byte) 1).size()) <= (testGraph.getNode(30).getIncomingEdgesByHierarchy((byte) 2).size()))
			fail("Das Einfuegen einer Edge ohne Levelangabe geschieht nciht wie erwartet als Level 1");
	*/
		/*
		 * Teste nun fuer die mapNode die isIsolated-Methode
		 */
	}
	
	/**
	 * Dieser Test testet korrekte Isolierte/geloeschte Nodes und das korrekte Update
	 */
	@Test
	public void IsolatedTest() {
	
		testGraph = new HierarchyMapGraph();
		testGraph.insertNode(1, 0, 0);
		if (!testGraph.getNode(1).isIsolated((byte)1))
				fail("Eine isolierte Node wurde nciht als solche erkannt");
		
		//Teste nun das korrekte Loeschen und ob danach noch iommer die isolierte methode funzt
		testGraph.insertNode(2, 1, 1);
		testGraph.insertEdgeBothWays(1, 2, 5, 1, StreetType.MOTORWAY, (byte)1);
		if (testGraph.getNode(1).isIsolated((byte)1))
			fail("Eine nciht isolierte Node wurde nciht als isoliert emeldet");
		if (testGraph.getNode(1).getNumberOfIncomingEdges((byte)1) != 1)
			fail("Die Methode zum hinzuuegen einer ungerichteten kante funktioniert niht korrekt");
		if (testGraph.getNode(1).getIncomingEdgesByHierarchy((byte)1).size() != 1)
			fail("Das Hinzufuegen einer ungerichteten Kante funzt nciht, aber laesst die Methode der getNumberOf.. den falschen Wert ausgeben");
		if (testGraph.getNode(1).getIncomingEdgesByHierarchy((byte)1).get(0)==null)
			fail("Die eingefuegte ungerichtete Kante ist in wirklichkeit ein Null-Wert");
		if (testGraph.getNode(1).getNumberOfOutgoingEdges((byte)1) != 1)
			fail("Die Methode zum hinzuuegen einer ungerichteten kante funktioniert niht korrekt");
		if (testGraph.getNode(1).getOutgoingEdgesByHierarchy((byte)1).size() != 1)
			fail("Das Hinzufuegen einer ungerichteten Kante funzt nciht, aber laesst die Methode der getNumberOf.. den falschen Wert ausgeben");
		if (testGraph.getNode(1).getOutgoingEdgesByHierarchy((byte)1).get(0)==null)
			fail("Die eingefuegte ungerichtete Kante ist in wirklichkeit ein Null-Wert");
		
		HierarchyMapNode node = testGraph.getNode(1);
		HierarchyMapEdge edge1 = node.getEdgeToNeighbour(2);
		
		HierarchyMapEdge edge = node.getOutgoingEdgesByHierarchy((byte)1).get(0);
		//Teste nebenbei diese tolle Methode:
		if (edge != edge1)
			fail("Die \"getEdgeToNeighbour\"-Methode liefert nicht die korrekte Kante");
		
		node.deleteOutgoingEdge(edge);
		
		if (testGraph.getNode(1).isIsolated((byte)1))
			fail("Scheinbar hat das Loeschen einer Kante auch die anderen Kanten geloescht");

		edge = node.getIncomingEdgesByHierarchy((byte)1).get(0);
		node.deleteIncomingEdge(edge);
		
		if (node.getNumberOfIncomingEdges((byte)1) !=0)
			fail("Eingehende Kante wurde nciht geloescht");
		if (node.getNumberOfOutgoingEdges((byte)1) !=0)
			fail("Ausgehende Kante wurde nciht geloescht");
		
		if (!testGraph.getNode(1).isIsolated((byte)1))
			fail("Eine isolierte Node wurde nciht als solche erkannt: ");
		
		
		/*
		 * Teste nun die Update-Methode
		 */
		
		testGraph.insertNode(3, 2, 2);
		testGraph.insertNode(4, 2, 2);
		
		testGraph.insertEdgeBothWays(1, 2, 5, 1, StreetType.MOTORWAY, (byte)1);
		testGraph.insertEdgeBothWays(2, 3, 6, 1, StreetType.MOTORWAY, (byte)5);
		testGraph.insertEdgeBothWays(3, 4, 7, 1, StreetType.MOTORWAY, (byte)3);
		testGraph.insertEdgeBothWays(2, 4, 1000, 5, StreetType.PRIMARY, (byte)2);
		
		for (int i = 1; i<5; i++)
			testGraph.getNode(i).updateLevel();
		
		if (testGraph.getNode(1).getLevel() != (byte)1)
			fail("Node 1 hat das falsche Level. Soll:1 Ist:"+testGraph.getNode(1).getLevel());
		
		if (testGraph.getNode(2).getLevel() != (byte)5)
			fail("Node 2 hat das falsche Level. Soll:5 Ist:"+testGraph.getNode(2).getLevel());
		
		if (testGraph.getNode(3).getLevel() != (byte)5)
			fail("Node 3 hat das falsche Level. Soll:5 Ist:"+testGraph.getNode(3).getLevel());
		
		if (testGraph.getNode(4).getLevel() != (byte)3)
			fail("Node 4 hat das falsche Level. Soll:3 Ist:"+testGraph.getNode(4).getLevel());
		
		//Entferne nun die Kante  - es aendert sich ein Level!
		//Fuege Kante von 1 nach 3 hinzu mit Level 3 - Es aendert sich ein Level!
		
		edge = testGraph.getNode(4).getIncomingEdgesByHierarchy((byte)3).get(0);
		testGraph.getNode(4).deleteIncomingEdge(edge);
		testGraph.getNode(3).deleteOutgoingEdge(edge);
		
		testGraph.insertEdgeBothWays(1, 3, 500, 500, StreetType.SECONDARY, (byte)3);
		
		for (int i = 1; i<5; i++)
			testGraph.getNode(i).updateLevel();
		
		if (testGraph.getNode(1).getLevel() != (byte)3)
			fail("Node 1 hat das falsche Level. Soll:3 Ist:"+testGraph.getNode(1).getLevel());
		
		if (testGraph.getNode(2).getLevel() != (byte)5)
			fail("Node 2 hat das falsche Level. Soll:5 Ist:"+testGraph.getNode(2).getLevel());
		
		if (testGraph.getNode(3).getLevel() != (byte)5)
			fail("Node 3 hat das falsche Level. Soll:5 Ist:"+testGraph.getNode(3).getLevel());
		
		//if (testGraph.getNode(4).getLevel() != (byte)2)
		if (testGraph.getNode(4).getLevel() != (byte)3)
			fail("Node 4 hat das falsche Level. (Ver Soll:3 Ist:"+testGraph.getNode(4).getLevel());
	}
	
	/**
	 * Hier beginnt der Verantwortungsbereich von Jan!
	 * VERANTWORTLICHER Jan
	 */
	
	@Test
	public void testTwoCore() {
		
		/*
		 * Erstelle HierarchyMapGraph wie in der Zeichnung 1 in der Dokumentation zu entnehmen ist.
		 */
		try {
			testGraph = new HierarchyMapGraph();
		} catch (Exception e) {
			fail("Fehler beim erstellen des Graphen: "+e.getLocalizedMessage());
		}
		
		try {
			for (int i=1; i<23; i++) {
				testGraph.insertNode(i,0,0);
				testGraph.getNode(i).setLevel((byte)2);
			}
		} catch (Exception e) {
			fail("Fehler beim einfuegen der Nodes in den Graphen: "+e.getLocalizedMessage());
		}
		
		try {
			testGraph.insertEdgeBothWays(2, 3, 1, 10, StreetType.MOTORWAY, (byte) 2);
			testGraph.insertEdgeBothWays(4, 5, 2, 10, StreetType.MOTORWAY, (byte) 2);
			testGraph.insertEdgeBothWays(5, 6, 3, 10, StreetType.MOTORWAY, (byte) 2);
			testGraph.insertEdgeBothWays(7, 9, 4, 10, StreetType.MOTORWAY, (byte) 2);
			testGraph.insertEdgeBothWays(8, 9, 5, 10, StreetType.MOTORWAY, (byte) 2);
			testGraph.insertEdgeBothWays(9, 11, 6, 10, StreetType.MOTORWAY, (byte) 2);
			testGraph.insertEdgeBothWays(10, 11, 7, 10, StreetType.MOTORWAY, (byte) 2);
			testGraph.insertEdgeBothWays(12, 11, 8, 10, StreetType.MOTORWAY, (byte) 2);
			testGraph.insertEdgeBothWays(22, 13, 9, 10, StreetType.MOTORWAY, (byte) 2);
			testGraph.insertEdgeBothWays(21, 13, 10, 10, StreetType.MOTORWAY, (byte) 2);
			testGraph.insertEdgeBothWays(13, 11, 11, 10, StreetType.MOTORWAY, (byte) 2);
			testGraph.insertEdgeBothWays(11, 14, 12, 10, StreetType.MOTORWAY, (byte) 2);
			testGraph.insertEdgeBothWays(14, 15, 13, 10, StreetType.MOTORWAY, (byte) 2);
			testGraph.insertEdgeBothWays(15, 16, 14, 10, StreetType.MOTORWAY, (byte) 2);
			testGraph.insertEdgeBothWays(16, 17, 15, 10, StreetType.MOTORWAY, (byte) 2);
			testGraph.insertEdgeBothWays(17, 14, 16, 10, StreetType.MOTORWAY, (byte) 2);
			testGraph.insertEdgeBothWays(16, 18, 16, 10, StreetType.MOTORWAY, (byte) 2);
			testGraph.insertEdgeBothWays(17, 19, 16, 10, StreetType.MOTORWAY, (byte) 2);
			testGraph.insertEdgeBothWays(19, 20, 16, 10, StreetType.MOTORWAY, (byte) 2);

		} catch (Exception e) {
			fail("Fehler beim Einfuegen der Kanten: "+e.getLocalizedMessage());
		}
		
		testGraph.computeTwoCore((byte)2);
		
		String fehlerausgabe="";
		boolean passed=true;
		for (int i = 1; i < 14; i++) {
			if(testGraph.getNode(i).getLevel()!=(byte)2) {
				fehlerausgabe+="Knoten: "+i+" in 2-Core Test hat falsches Level \n";
				passed=false;
			}	
		}
		for (int i = 14; i < 18; i++) {
			if(testGraph.getNode(i).getLevel()!=(byte)3) {
				fehlerausgabe+="Knoten: "+i+" in 2-Core Test hat falsches Level \n";
				passed=false;
			}	
		}
		for (int i = 19; i < 22; i++) {
			if(testGraph.getNode(i).getLevel()!=(byte)2) {
				fehlerausgabe+="Knoten: "+i+" in 2-Core Test hat falsches Level \n";
				passed=false;
			}	
		}
		if(!passed){
			fail(fehlerausgabe);
		}
	}
	

	@Test
	public void testTwoCoreDirected() {
		
		/*
		 * Erstelle HierarchyMapGraph wie in der Zeichnung 2 in der Dokumentation zu entnehmen ist.
		 */
		try {
			testGraph = new HierarchyMapGraph();
		} catch (Exception e) {
			fail("Fehler beim erstellen des Graphen: "+e.getLocalizedMessage());
		}
		
		try {
			for (int i=1; i<21; i++) {
				testGraph.insertNode(i,0,0);
				testGraph.getNode(i).setLevel((byte)5);
			}
		} catch (Exception e) {
			fail("Fehler beim einfuegen der Nodes in den Graphen: "+e.getLocalizedMessage());
		}
		
		try {
			testGraph.insertEdge(2, 3, 1, 10, StreetType.MOTORWAY, (byte) 5);
			testGraph.insertEdge(4, 5, 2, 10, StreetType.MOTORWAY, (byte) 5);
			testGraph.insertEdge(5, 6, 3, 10, StreetType.MOTORWAY, (byte) 5);
			testGraph.insertEdge(7, 8, 4, 10, StreetType.MOTORWAY, (byte) 5);
			testGraph.insertEdge(9, 8, 5, 10, StreetType.MOTORWAY, (byte) 5);
			testGraph.insertEdge(12, 11, 6, 10, StreetType.MOTORWAY, (byte) 5);
			testGraph.insertEdge(11, 10, 7, 10, StreetType.MOTORWAY, (byte) 5);
			testGraph.insertEdge(11, 13, 8, 10, StreetType.MOTORWAY, (byte) 5);
			testGraph.insertEdge(13, 12, 9, 10, StreetType.MOTORWAY, (byte) 5);
			testGraph.insertEdge(17, 13, 10, 10, StreetType.MOTORWAY, (byte) 5);
			testGraph.insertEdge(17, 10, 11, 10, StreetType.MOTORWAY, (byte) 5);
			testGraph.insertEdge(15, 17, 12, 10, StreetType.MOTORWAY, (byte) 5);
			testGraph.insertEdge(14, 15, 13, 10, StreetType.MOTORWAY, (byte) 5);
			testGraph.insertEdge(16, 15, 14, 10, StreetType.MOTORWAY, (byte) 5);
			testGraph.insertEdge(15, 18, 15, 10, StreetType.MOTORWAY, (byte) 5);
			testGraph.insertEdge(18, 20, 16, 10, StreetType.MOTORWAY, (byte) 5);
			testGraph.insertEdge(19, 18, 17, 10, StreetType.MOTORWAY, (byte) 5);


		} catch (Exception e) {
			fail("Fehler beim Einfuegen der Kanten: "+e.getLocalizedMessage());
		}
		
		testGraph.computeTwoCore((byte) 5);
		
		String fehlerausgabe="";
		boolean passed=true;
		//Die Knoten {1,2,3,4,5,6,7,8,9,14,15,16,18,19,2} sollten bei der
		//korrekten Anwenung des Algorithmus Level 5 haben
		for (int i = 1; i < 9; i++) {
			if(testGraph.getNode(i).getLevel()!=(byte)5) {
				fehlerausgabe+="Knoten: "+i+" in 2-Core TestDirected hat falsches Level \n";
				passed=false;
			}	
		}
		
		for (int i = 14; i < 21; i++) {
			if(testGraph.getNode(i).getLevel()!=(byte)5 && i!=17) {
				fehlerausgabe+="Knoten: "+i+" in 2-Core TestDirected hat falsches Level \n";
				passed=false;
			}	
		}
		//Die Knoten {10,11,12,13,17} sollten bei korrekter Anwendung des Algorithmus
		//Level 6 haben
		for (int i = 10; i < 14; i++) {
			if(testGraph.getNode(i).getLevel()!=(byte)6) {
				fehlerausgabe+="Knoten: "+i+" in 2-Core TestDirected hat falsches Level \n";
				passed=false;
			}	
		}
		if(testGraph.getNode(17).getLevel()!=(byte)6) {
			fehlerausgabe+="Knoten: 17 in 2-Core TestDirected hat falsches Level \n";
			passed=false;
		}
		
		if(!passed){
			fail(fehlerausgabe);
		}
	}
	
	private HierarchyMapGraph constructTestGraph(byte level) {
		/*
		 * Erstelle HierarchyMapGraph wie in der Zeichnung 1 in der Dokumentation zu entnehmen ist.
		 */
		HierarchyMapGraph tempTestGraph=null;
		try {
			tempTestGraph = new HierarchyMapGraph();
		} catch (Exception e) {
			fail("Fehler beim erstellen des Graphen: "+e.getLocalizedMessage());
		}
		
		try {
			for (int i=1; i<23; i++) {
				tempTestGraph.insertNode(i,0,0);
				tempTestGraph.getNode(i).setLevel(level);
			}
		} catch (Exception e) {
			fail("Fehler beim einfuegen der Nodes in den Graphen: "+e.getLocalizedMessage());
		}
		
		try {
			tempTestGraph.insertEdgeBothWays( 2,  3,  1, 10, StreetType.MOTORWAY, level);
			tempTestGraph.insertEdgeBothWays( 4, 11,  2, 10, StreetType.MOTORWAY, level);
			tempTestGraph.insertEdgeBothWays(11,  5,  3, 10, StreetType.MOTORWAY, level);
			tempTestGraph.insertEdgeBothWays(18, 19,  4, 10, StreetType.MOTORWAY, level);
			tempTestGraph.insertEdgeBothWays(19, 20,  5, 10, StreetType.MOTORWAY, level);
			tempTestGraph.insertEdgeBothWays(20,  9,  6, 10, StreetType.MOTORWAY, level);
			tempTestGraph.insertEdgeBothWays( 6, 18,  7, 10, StreetType.MOTORWAY, level);
			tempTestGraph.insertEdgeBothWays( 6, 12,  8, 10, StreetType.MOTORWAY, level);
			tempTestGraph.insertEdgeBothWays(12, 13,  9, 10, StreetType.MOTORWAY, level);
			tempTestGraph.insertEdgeBothWays(13, 14, 10, 10, StreetType.MOTORWAY, level);
			tempTestGraph.insertEdgeBothWays(14, 15, 11, 10, StreetType.MOTORWAY, level);
			tempTestGraph.insertEdgeBothWays(15,  9, 12, 10, StreetType.MOTORWAY, level);
			tempTestGraph.insertEdgeBothWays( 6, 16, 14, 10, StreetType.MOTORWAY, level);
			tempTestGraph.insertEdgeBothWays(16, 10, 15, 10, StreetType.MOTORWAY, level);
			tempTestGraph.insertEdgeBothWays(10,  9, 16, 10, StreetType.MOTORWAY, level);
			tempTestGraph.insertEdgeBothWays(10,  6, 17, 10, StreetType.MOTORWAY, level);
			tempTestGraph.insertEdgeBothWays( 7, 17, 18, 10, StreetType.MOTORWAY, level);
			tempTestGraph.insertEdgeBothWays(17,  8, 19, 10, StreetType.MOTORWAY, level);
			tempTestGraph.insertEdgeBothWays( 8, 21, 20, 10, StreetType.MOTORWAY, level);
			tempTestGraph.insertEdgeBothWays( 8, 22, 21, 10, StreetType.MOTORWAY, level);
			tempTestGraph.insertEdgeBothWays( 8,  9, 23, 10, StreetType.MOTORWAY, level);
			tempTestGraph.insertEdgeBothWays( 9, 21, 24, 10, StreetType.MOTORWAY, level);
			tempTestGraph.insertEdgeBothWays( 9, 22, 25, 10, StreetType.MOTORWAY, level);
		} catch (Exception e) {
			fail("Fehler beim Einfuegen der Kanten: "+e.getLocalizedMessage());
		}
		return tempTestGraph;
	}
	
	@Test
	public void testContractEdges() {
		testGraph=constructTestGraph((byte)0);
		testGraph.contractEdges((byte)0);
		//System.out.println(testGraph);
		String fehlerausgabe="";
		boolean passed=true;
		//Test Level 0. Hier sollen Kanten und Knoten geloescht werden bei der Kontraktion.
		for (int i = 1; i < 11; i++) {
			if(testGraph.getNode(i)==null) {
				fehlerausgabe+="Fehler in testContractEdges Teil 1: Knoten mit ID "+i+" faelschlicherweise geloescht.\n";
				passed=false;
			} else if (testGraph.getNode(i).getLevel()!=0) {
				fehlerausgabe+="Fehler in testContractEdges Teil 1: Knoten mit ID "+i+" besitzt falsches Level geloescht.\n";
				passed=false;
			}
		}
		for (int i = 11; i < 23; i++) {
			if(testGraph.getNode(i)!=null) {
				fehlerausgabe+="Fehler in testContractEdges Teil 1: Knoten mit ID "+i+" sollte geloescht sein.\n";
				passed=false;
			}
		}
		if(!passed) {
			fail(fehlerausgabe);
		}
		
		//Test Level 2: Hier sollen nur die Levels der betreffenden Kanten und Knoten verringert werden bei der Kontraktion
		testGraph=constructTestGraph((byte)2);
		testGraph.contractEdges((byte)2);
		for (int i = 1; i < 23; i++) {
			if(testGraph.getNode(i)==null) {
				fehlerausgabe+="Fehler in testContractEdges Teil 2: Knoten mit ID "+i+" faelschlicherweise geloescht.\n";
				passed=false;
			}
		}
		if(passed) {
			for (int i = 1; i < 11; i++) {
				if(testGraph.getNode(i).getLevel()!=2) {
					fehlerausgabe+="Fehler in testContractEdges Teil 2: Knoten mit ID "+i+" besitzt falsches Level.\n";
					passed=false;
				}
			}
			for (int i = 11; i < 23; i++) {
				if(testGraph.getNode(i).getLevel()!=1) {
					fehlerausgabe+="Fehler in testContractEdges Teil 2: Knoten mit ID "+i+" besitzt falsches Level.\n";
					passed=false;
				}
			}
			
		}
		if(!passed) {
			fail(fehlerausgabe);
		}	
		
		/*
		 *	Neuer Test-Bereich 
		 */
			
		
		HierarchyMapGraph hGraph = new HierarchyMapGraph();
		
		//Knoten -> spaeter sollen 5,6,7 kontrahiert werden
		hGraph.insertNode(1, 1, 1);
		hGraph.insertNode(2, 1, 1);
		hGraph.insertNode(3, 1, 1);
		hGraph.insertNode(4, 1, 1);
		hGraph.insertNode(10, 1, 1);
		
		hGraph.insertNode(11, 1, 1);
		hGraph.insertNode(20, 1, 1);
		hGraph.insertNode(21, 1, 1);
		hGraph.insertNode(30, 1, 1);
		hGraph.insertNode(31, 1, 1);
		
		hGraph.insertNode(40, 1, 1);
		hGraph.insertNode(41, 1, 1);
		hGraph.insertNode(5, 1, 1);
		hGraph.insertNode(6, 1, 1);
		hGraph.insertNode(7, 1, 1);
		
		//Kanten
		hGraph.insertEdgeBothWays(1, 10, 10, 1, StreetType.MOTORWAY, (byte) 1);
		hGraph.insertEdgeBothWays(1, 11, 11, 1, StreetType.MOTORWAY, (byte) 1);
		hGraph.insertEdgeBothWays(1, 2, 1, 1, StreetType.MOTORWAY, (byte) 1);
		hGraph.insertEdgeBothWays(2, 20, 20, 1, StreetType.MOTORWAY, (byte) 1);
		hGraph.insertEdgeBothWays(2, 21, 21, 1, StreetType.MOTORWAY, (byte) 1);
		
		hGraph.insertEdgeBothWays(2, 5, 2, 1, StreetType.MOTORWAY, (byte) 1);
		hGraph.insertEdgeBothWays(5, 6, 5, 1, StreetType.MOTORWAY, (byte) 1);
		hGraph.insertEdgeBothWays(6, 7, 6, 1, StreetType.MOTORWAY, (byte) 1);
		hGraph.insertEdgeBothWays(7, 3, 7, 1, StreetType.MOTORWAY, (byte) 1);
		hGraph.insertEdgeBothWays(3, 4, 3, 1, StreetType.MOTORWAY, (byte) 1);
		
		hGraph.insertEdgeBothWays(3, 30, 30, 1, StreetType.MOTORWAY, (byte) 1);
		hGraph.insertEdgeBothWays(3, 31, 31, 1, StreetType.MOTORWAY, (byte) 1);
		hGraph.insertEdgeBothWays(4, 40, 40, 1, StreetType.MOTORWAY, (byte) 1);
		hGraph.insertEdgeBothWays(4, 41, 41, 1, StreetType.MOTORWAY, (byte) 1);
		
		//Jetzt der Aufruf
		hGraph.contractEdges((byte)1);
		
		if (hGraph.getNode(2)==null) {
			fail("Es wurde eine Node geloescht, die eine Kreuzung darstellt");
		}
		if (hGraph.getNode(5).getLevel()!=0) {
			fail("Node 5 hat das falcshe Level. Soll: 0, Ist: "+hGraph.getNode(5).getLevel());
		}
		if (hGraph.getNode(6).getLevel()!=0) {
			fail("Node 6 hat das falcshe Level. Soll: 0, Ist: "+hGraph.getNode(6).getLevel());
		}
		if (hGraph.getNode(7).getLevel()!=0) {
			fail("Node 7 hat das falcshe Level. Soll: 0, Ist: "+hGraph.getNode(7).getLevel());
		}
		
		HierarchyMapNode node = hGraph.getNode(2);
		HierarchyMapEdge[] treffer = new HierarchyMapEdge[22];
		
		for (int i=0; i<node.getNumberOfOutgoingEdges((byte)1); i++) {
			treffer[node.getOutgoingEdgesByHierarchy((byte)1).get(i).getUID()] = node.getOutgoingEdgesByHierarchy((byte)1).get(i); 
		}
		
		for (int i=0; i<treffer.length; i++) {
			if (i==1 || i==2 || i==20 || i==21) {
				if (treffer[i] == null)
					fail("Kante "+i+" ist nicht mehr vorhanden auf Level 1");
			} else if (treffer[i] != null)
				fail("Es gibt eine Kante mit ID: "+i+" die es nciht geben duerfte: "+treffer[i]);
		}
		
		HierarchyMapEdge kante = treffer[2];
		
		//Teste nun die tolle neue Kante!
		if (kante.getLength()!=4)
			fail("Die neue Kante hat die falsche Laenge. Soll:4, Ist:"+kante.getLength());
		if (kante.getMinLevel()!=1)
			fail("Die neue Kante hat das falsche MinLevel. Soll:1, Ist:"+kante.getMinLevel());
		if (kante.getNodeStart().getUID()!= 2)
			fail("Die neue Kante hat dden falschen Startknoten (Fehler beim erstellen?). Soll:2, Ist:"+kante.getNodeStart().getUID());
		if (kante.getNodeEnd().getUID()!= 3)
			fail("Die neue Kante hat dden falschen Startknoten (Fehler beim erstellen?). Soll:3, Ist:"+kante.getNodeEnd().getUID());
		if (kante.getType() != StreetType.MOTORWAY)
			fail("Der Typ der neuen kante stimmt nicht. Soll: Motorway, Ist:"+kante.getType());
		
		//Kommt der Test bis hier, sieht das erstmal gut aus
			
		//Teste korrekte kontrahierte Nodes
		int[] IDs = kante.getContractedNodeIDs();
		if (IDs.length!=3)
			fail("DieAnzahl der kontraierten Knoten stimmt nciht. Soll:3, Ist:"+IDs.length);
		
		//nach der Definition der Methodik erwarte korrekte Reihenfolge der Nodes!
		if (IDs[0] != 5)
			fail("Der erste Knoten der Kntraktion ist falsch. Soll:5, Ist:"+IDs[0]);
		if (IDs[1] != 6)
			fail("Der zweite Knoten der Kntraktion ist falsch. Soll:6, Ist:"+IDs[1]);
		if (IDs[2] != 7)
			fail("Der dritte Knoten der Kntraktion ist falsch. Soll:7, Ist:"+IDs[2]);
			
		
		//Teste korrekte Entfernungen
		try {
			if (kante.getContractedNodeDistance(5)!=1)
				fail("Die Entfernung von Knoten 2 zu 5 wurde falsch ausgegeben. Soll:1, Ist:"+kante.getContractedNodeDistance(5));
			if (kante.getContractedNodeDistance(6)!=2)
				fail("Die Entfernung von Knoten 2 zu 6 wurde falsch ausgegeben. Soll:2, Ist:"+kante.getContractedNodeDistance(6));
			if (kante.getContractedNodeDistance(7)!=3)
				fail("Die Entfernung von Knoten 2 zu 7 wurde falsch ausgegeben. Soll:3, Ist:"+kante.getContractedNodeDistance(7));
		} catch (Exception e) {
			fail("Der Zugriff auf die getContractedNodeDistance(int) hat eine Exception geworfen trotz korrekter abfrage: "+e.getLocalizedMessage());
		}
			
		//Teste korrekten Wurf einer Exception bei illegalen Daten
		boolean fehler = false;
		try {
			kante.getContractedNodeDistance(4);
		} catch (InvalidInputException e) {
			fehler = true;
		} finally {
			if (!fehler) {
				fail("Es wurde bei einer illegalen Eingabe keine Exception geworfen");
			}
		}
		
		HashSet<HierarchyMapNode> set =  hGraph.getNode(2).getNeighbours((byte) 1);
		if (!set.contains(hGraph.getNode(3)))
			fail("Laut getNeighbous-Methode gibt es keinen Weg von 2 nach 3(ueber die kontrahierte Kante)");
		
		set =  hGraph.getNode(3).getNeighbours((byte) 1);
		if (!set.contains(hGraph.getNode(2)))
			fail("Laut getNeighbous-Methode gibt es keinen Weg von 3 nach 2 (ueber die kontrahierte Kante)");
			
			/*
		HierarchyMapGraph hGraph;
		HierarchyMapNode node;
		HierarchyMapEdge kante;
		HierarchyMapEdge[] treffer;
		int[] IDs;
		boolean fehler;
		HashSet<HierarchyMapNode> set;
			*/
		
		//Teste nun mit Level 0
		hGraph = new HierarchyMapGraph();
		
		//Knoten -> spaeter sollen 5,6,7 kontrahiert werden
		hGraph.insertNode(1, 1, 1);
		hGraph.insertNode(2, 1, 1);
		hGraph.insertNode(3, 1, 1);
		hGraph.insertNode(4, 1, 1);
		hGraph.insertNode(10, 1, 1);
		
		hGraph.insertNode(11, 1, 1);
		hGraph.insertNode(20, 1, 1);
		hGraph.insertNode(21, 1, 1);
		hGraph.insertNode(30, 1, 1);
		hGraph.insertNode(31, 1, 1);
		
		hGraph.insertNode(40, 1, 1);
		hGraph.insertNode(41, 1, 1);
		hGraph.insertNode(5, 1, 1);
		hGraph.insertNode(6, 1, 1);
		hGraph.insertNode(7, 1, 1);
		
		//Kanten
		hGraph.insertEdgeBothWays(1, 10, 10, 1, StreetType.MOTORWAY, (byte) 0);
		hGraph.insertEdgeBothWays(1, 11, 11, 1, StreetType.MOTORWAY, (byte) 0);
		hGraph.insertEdgeBothWays(1, 2, 1, 1, StreetType.MOTORWAY, (byte) 0);
		hGraph.insertEdgeBothWays(2, 20, 20, 1, StreetType.MOTORWAY, (byte) 0);
		hGraph.insertEdgeBothWays(2, 21, 21, 1, StreetType.MOTORWAY, (byte) 0);
		
		hGraph.insertEdgeBothWays(2, 5, 2, 1, StreetType.MOTORWAY, (byte) 0);
		hGraph.insertEdgeBothWays(5, 6, 5, 1, StreetType.MOTORWAY, (byte) 0);
		hGraph.insertEdgeBothWays(6, 7, 6, 1, StreetType.MOTORWAY, (byte) 0);
		hGraph.insertEdgeBothWays(7, 3, 7, 1, StreetType.MOTORWAY, (byte) 0);
		hGraph.insertEdgeBothWays(3, 4, 3, 1, StreetType.MOTORWAY, (byte) 0);
		
		hGraph.insertEdgeBothWays(3, 30, 30, 1, StreetType.MOTORWAY, (byte) 0);
		hGraph.insertEdgeBothWays(3, 31, 31, 1, StreetType.MOTORWAY, (byte) 0);
		hGraph.insertEdgeBothWays(4, 40, 40, 1, StreetType.MOTORWAY, (byte) 0);
		hGraph.insertEdgeBothWays(4, 41, 41, 1, StreetType.MOTORWAY, (byte) 0);
		
		//Jetzt der Aufruf
		hGraph.contractEdges((byte)0);
		
		if (hGraph.getNode(2)==null) {
			fail("Es wurde eine Node geloescht, die eine Kreuzung darstellt");
		}
		if (hGraph.getNode(5)!=null) {
			fail("Node 5 exisitert noch - sollte geloscht sein");
		}
		if (hGraph.getNode(6)!=null) {
			fail("Node 6 exisitert noch - sollte geloscht sein");
		}
		if (hGraph.getNode(7)!=null) {
			fail("Node 7 exisitert noch - sollte geloscht sein");
		}
		
		node = hGraph.getNode(2);
		treffer = new HierarchyMapEdge[22];
		
		for (int i=0; i<node.getNumberOfOutgoingEdges((byte)0); i++) {
			treffer[node.getOutgoingEdgesByHierarchy((byte)0).get(i).getUID()] = node.getOutgoingEdgesByHierarchy((byte)0).get(i); 
		}
		
		for (int i=0; i<treffer.length; i++) {
			if (i==1 || i==2 || i==20 || i==21) {
				if (treffer[i] == null)
					fail("Kante "+i+" ist nicht mehr vorhanden auf Level 0");
			} else if (treffer[i] != null)
				fail("Es gibt eine Kante mit ID: "+i+" die es nciht geben duerfte: "+treffer[i]);
		}
		
		kante = treffer[2];
		
		//Teste nun die tolle neue Kante!
		if (kante.getLength()!=4)
			fail("Die neue Kante hat die falsche Laenge. Soll:4, Ist:"+kante.getLength());
		if (kante.getMinLevel()!=0)
			fail("Die neue Kante hat das falsche MinLevel. Soll:0, Ist:"+kante.getMinLevel());
		if (kante.getNodeStart().getUID()!= 2)
			fail("Die neue Kante hat dden falschen Startknoten (Fehler beim erstellen?). Soll:2, Ist:"+kante.getNodeStart().getUID());
		if (kante.getNodeEnd().getUID()!= 3)
			fail("Die neue Kante hat dden falschen Startknoten (Fehler beim erstellen?). Soll:3, Ist:"+kante.getNodeEnd().getUID());
		if (kante.getType() != StreetType.MOTORWAY)
			fail("Der Typ der neuen kante stimmt nicht. Soll: Motorway, Ist:"+kante.getType());
		
		//Kommt der Test bis hier, sieht das erstmal gut aus
			
		//Teste korrekte kontrahierte Nodes
		IDs = kante.getContractedNodeIDs();
		if (IDs.length!=3)
			fail("DieAnzahl der kontraierten Knoten stimmt nciht. Soll:3, Ist:"+IDs.length);
		
		//nach der Definition der Methodik erwarte korrekte Reihenfolge der Nodes!
		if (IDs[0] != 5)
			fail("Der erste Knoten der Kntraktion ist falsch. Soll:5, Ist:"+IDs[0]);
		if (IDs[1] != 6)
			fail("Der zweite Knoten der Kntraktion ist falsch. Soll:6, Ist:"+IDs[1]);
		if (IDs[2] != 7)
			fail("Der dritte Knoten der Kntraktion ist falsch. Soll:7, Ist:"+IDs[2]);
			
		
		//Teste korrekte Entfernungen
		try {
			if (kante.getContractedNodeDistance(5)!=1)
				fail("Die Entfernung von Knoten 2 zu 5 wurde falsch ausgegeben. Soll:1, Ist:"+kante.getContractedNodeDistance(5));
			if (kante.getContractedNodeDistance(6)!=2)
				fail("Die Entfernung von Knoten 2 zu 6 wurde falsch ausgegeben. Soll:2, Ist:"+kante.getContractedNodeDistance(6));
			if (kante.getContractedNodeDistance(7)!=3)
				fail("Die Entfernung von Knoten 2 zu 7 wurde falsch ausgegeben. Soll:3, Ist:"+kante.getContractedNodeDistance(7));
		} catch (Exception e) {
			fail("Der Zugriff auf die getContractedNodeDistance(int) hat eine Exception geworfen trotz korrekter abfrage: "+e.getLocalizedMessage());
		}
			
		//Teste korrekten Wurf einer Exception bei illegalen Daten
		fehler = false;
		try {
			kante.getContractedNodeDistance(4);
		} catch (InvalidInputException e) {
			fehler = true;
		} finally {
			if (!fehler) {
				fail("Es wurde bei einer illegalen Eingabe keine Exception geworfen");
			}
		}
		
		set =  hGraph.getNode(2).getNeighbours((byte) 0);
		if (!set.contains(hGraph.getNode(3)))
			fail("Laut getNeighbous-Methode gibt es keinen Weg von 2 nach 3(ueber die kontrahierte Kante)");
		
		set =  hGraph.getNode(3).getNeighbours((byte) 0);
		if (!set.contains(hGraph.getNode(2)))
			fail("Laut getNeighbous-Methode gibt es keinen Weg von 3 nach 2 (ueber die kontrahierte Kante)");
		
		
		
		
		//Teste nun ein paar nette Sonderfaelle
		//Sonderfall 1: verschiedene StreetTypes
		
		hGraph = new HierarchyMapGraph();
		
		//Knoten -> spaeter sollen 5,6,7 eben nciht kontrahiert werden
		hGraph.insertNode(1, 1, 1);
		hGraph.insertNode(2, 1, 1);
		hGraph.insertNode(3, 1, 1);
		hGraph.insertNode(4, 1, 1);
		hGraph.insertNode(10, 1, 1);
		
		hGraph.insertNode(11, 1, 1);
		hGraph.insertNode(20, 1, 1);
		hGraph.insertNode(21, 1, 1);
		hGraph.insertNode(30, 1, 1);
		hGraph.insertNode(31, 1, 1);
		
		hGraph.insertNode(40, 1, 1);
		hGraph.insertNode(41, 1, 1);
		hGraph.insertNode(5, 1, 1);
		hGraph.insertNode(6, 1, 1);
		hGraph.insertNode(7, 1, 1);
		
		//Kanten
		hGraph.insertEdgeBothWays(1, 10, 10, 1, StreetType.MOTORWAY, (byte) 1);
		hGraph.insertEdgeBothWays(1, 11, 11, 1, StreetType.MOTORWAY, (byte) 1);
		hGraph.insertEdgeBothWays(1, 2, 1, 1, StreetType.MOTORWAY, (byte) 1);
		hGraph.insertEdgeBothWays(2, 20, 20, 1, StreetType.MOTORWAY, (byte) 1);
		hGraph.insertEdgeBothWays(2, 21, 21, 1, StreetType.MOTORWAY, (byte) 1);
		
		hGraph.insertEdgeBothWays(2, 5, 2, 1, StreetType.MOTORWAY, (byte) 1);
		hGraph.insertEdgeBothWays(5, 6, 5, 1, StreetType.LIVING_STREET, (byte) 1);
		hGraph.insertEdgeBothWays(6, 7, 6, 1, StreetType.PRIMARY, (byte) 1);
		hGraph.insertEdgeBothWays(7, 3, 7, 1, StreetType.MOTORWAY, (byte) 1);
		hGraph.insertEdgeBothWays(3, 4, 3, 1, StreetType.MOTORWAY, (byte) 1);
		
		hGraph.insertEdgeBothWays(3, 30, 30, 1, StreetType.MOTORWAY, (byte) 1);
		hGraph.insertEdgeBothWays(3, 31, 31, 1, StreetType.MOTORWAY, (byte) 1);
		hGraph.insertEdgeBothWays(4, 40, 40, 1, StreetType.MOTORWAY, (byte) 1);
		hGraph.insertEdgeBothWays(4, 41, 41, 1, StreetType.MOTORWAY, (byte) 1);
		
		//Jetzt der Aufruf
		hGraph.contractEdges((byte)1);
		
		if (hGraph.getNode(6).getLevel()!=1)
			fail("Sonderfall1: Es wurde kontrahiert auf verschiedenen Strassentypen - das darf nciht passieren!");
		
		//Sonderfall2: In eine Richtung duerfte kontrahiert werden, in die andere nciht -> die Kniten dazwischen muessen auf dem Level bleiben!
		hGraph = new HierarchyMapGraph();
		
		//Knoten -> spaeter sollen 5,6,7 kontrahiert werden
		hGraph.insertNode(1, 1, 1);
		hGraph.insertNode(2, 1, 1);
		hGraph.insertNode(3, 1, 1);
		hGraph.insertNode(4, 1, 1);
		hGraph.insertNode(10, 1, 1);
		
		hGraph.insertNode(11, 1, 1);
		hGraph.insertNode(20, 1, 1);
		hGraph.insertNode(21, 1, 1);
		hGraph.insertNode(30, 1, 1);
		hGraph.insertNode(31, 1, 1);
		
		hGraph.insertNode(40, 1, 1);
		hGraph.insertNode(41, 1, 1);
		hGraph.insertNode(5, 1, 1);
		hGraph.insertNode(6, 1, 1);
		hGraph.insertNode(7, 1, 1);
		
		//Kanten
		hGraph.insertEdgeBothWays(1, 10, 10, 1, StreetType.MOTORWAY, (byte) 1);
		hGraph.insertEdgeBothWays(1, 11, 11, 1, StreetType.MOTORWAY, (byte) 1);
		hGraph.insertEdgeBothWays(1, 2, 1, 1, StreetType.MOTORWAY, (byte) 1);
		hGraph.insertEdgeBothWays(2, 20, 20, 1, StreetType.MOTORWAY, (byte) 1);
		hGraph.insertEdgeBothWays(2, 21, 21, 1, StreetType.MOTORWAY, (byte) 1);
		//Hinweg darf nciht kontrahiert werden
		hGraph.insertEdge(5, 2, 2, 1, StreetType.PRIMARY, (byte) 1);
		hGraph.insertEdge(6, 5, 5, 1, StreetType.MOTORWAY, (byte) 1);
		hGraph.insertEdge(7, 6, 6, 1, StreetType.LIVING_STREET, (byte) 1);
		hGraph.insertEdge(3, 7, 7, 1, StreetType.PRIMARY, (byte) 1);
		//Rueckweg duerfte kontrahiert werden
		hGraph.insertEdge(2, 5, 2, 1, StreetType.MOTORWAY, (byte) 1);
		hGraph.insertEdge(5, 6, 5, 1, StreetType.MOTORWAY, (byte) 1);
		hGraph.insertEdge(6, 7, 6, 1, StreetType.MOTORWAY, (byte) 1);
		hGraph.insertEdge(7, 3, 7, 1, StreetType.MOTORWAY, (byte) 1);
		
		hGraph.insertEdgeBothWays(3, 4, 3, 1, StreetType.MOTORWAY, (byte) 1);
		
		hGraph.insertEdgeBothWays(3, 30, 30, 1, StreetType.MOTORWAY, (byte) 1);
		hGraph.insertEdgeBothWays(3, 31, 31, 1, StreetType.MOTORWAY, (byte) 1);
		hGraph.insertEdgeBothWays(4, 40, 40, 1, StreetType.MOTORWAY, (byte) 1);
		hGraph.insertEdgeBothWays(4, 41, 41, 1, StreetType.MOTORWAY, (byte) 1);
		
		//Jetzt der Aufruf
		hGraph.contractEdges((byte)1);
		
		if (hGraph.getNode(6).getLevel()!=1)
			fail("Sonderfall2: Es wurde kontrahiert auf verschiedenen Strassentypen - das darf nciht passieren!");
		
		
		
		//Sonderfall 3: Einbahnstrassen
		
		hGraph = new HierarchyMapGraph();
		
		//Knoten -> spaeter sollen 5,6,7 kontrahiert werden
		hGraph.insertNode(1, 1, 1);
		hGraph.insertNode(2, 1, 1);
		hGraph.insertNode(3, 1, 1);
		hGraph.insertNode(4, 1, 1);
		hGraph.insertNode(10, 1, 1);
		
		hGraph.insertNode(11, 1, 1);
		hGraph.insertNode(20, 1, 1);
		hGraph.insertNode(21, 1, 1);
		hGraph.insertNode(30, 1, 1);
		hGraph.insertNode(31, 1, 1);
		
		hGraph.insertNode(40, 1, 1);
		hGraph.insertNode(41, 1, 1);
		hGraph.insertNode(5, 1, 1);
		hGraph.insertNode(6, 1, 1);
		hGraph.insertNode(7, 1, 1);
		
		//Kanten
		hGraph.insertEdgeBothWays(1, 10, 10, 1, StreetType.MOTORWAY, (byte) 1);
		hGraph.insertEdgeBothWays(1, 11, 11, 1, StreetType.MOTORWAY, (byte) 1);
		hGraph.insertEdgeBothWays(1, 2, 1, 1, StreetType.MOTORWAY, (byte) 1);
		hGraph.insertEdgeBothWays(2, 20, 20, 1, StreetType.MOTORWAY, (byte) 1);
		hGraph.insertEdgeBothWays(2, 21, 21, 1, StreetType.MOTORWAY, (byte) 1);
		
		//Rueckweg duerfte kontrahiert werden
		hGraph.insertEdge(2, 5, 2, 1, StreetType.MOTORWAY, (byte) 1);
		hGraph.insertEdge(5, 6, 5, 1, StreetType.MOTORWAY, (byte) 1);
		hGraph.insertEdge(6, 7, 6, 1, StreetType.MOTORWAY, (byte) 1);
		hGraph.insertEdge(7, 3, 7, 1, StreetType.MOTORWAY, (byte) 1);
		
		hGraph.insertEdgeBothWays(3, 4, 3, 1, StreetType.MOTORWAY, (byte) 1);
		
		hGraph.insertEdgeBothWays(3, 30, 30, 1, StreetType.MOTORWAY, (byte) 1);
		hGraph.insertEdgeBothWays(3, 31, 31, 1, StreetType.MOTORWAY, (byte) 1);
		hGraph.insertEdgeBothWays(4, 40, 40, 1, StreetType.MOTORWAY, (byte) 1);
		hGraph.insertEdgeBothWays(4, 41, 41, 1, StreetType.MOTORWAY, (byte) 1);
		
		//Jetzt der Aufruf
		hGraph.contractEdges((byte)1);
		
		if (hGraph.getNode(6).getLevel()!=0)
			fail("Sonderfall3: Auf einbahnstrassen wurden nciht kontrahiert");
		
		if (!hGraph.getNode(2).getNeighbours((byte)1).contains(hGraph.getNode(3)))
			fail("Es gibt laut getNeighbours-Methode keinen Weg von 2->3 - diesen sllte es aber geben!");
		if (hGraph.getNode(3).getNeighbours((byte)1).contains(hGraph.getNode(2)))
			fail("Es gibt laut getNeighbours-Methode einen Weg von 3->2 - diesen sollte es aber nciht geben!");
		
		
	}
	
	/**
	 * Dieser Test testet die getNextCrossingIDs
	 */
	@Test
	public void getNextCrossingIDsTest() {
		
		HierarchyMapGraph hGraph = new HierarchyMapGraph();
		
		//Knoten -> spaeter sollen 5,6,7 kontrahiert werden
		hGraph.insertNode(1, 1, 1);
		hGraph.insertNode(2, 1, 1);
		hGraph.insertNode(3, 1, 1);
		hGraph.insertNode(4, 1, 1);
		hGraph.insertNode(10, 1, 1);
		
		hGraph.insertNode(11, 1, 1);
		hGraph.insertNode(20, 1, 1);
		hGraph.insertNode(21, 1, 1);
		hGraph.insertNode(30, 1, 1);
		hGraph.insertNode(31, 1, 1);
		
		hGraph.insertNode(40, 1, 1);
		hGraph.insertNode(41, 1, 1);
		hGraph.insertNode(5, 1, 1);
		hGraph.insertNode(6, 1, 1);
		hGraph.insertNode(7, 1, 1);
		
		//Kanten
		hGraph.insertEdgeBothWays(1, 10, 10, 1, StreetType.MOTORWAY, (byte) 0);
		hGraph.insertEdgeBothWays(1, 11, 11, 1, StreetType.MOTORWAY, (byte) 0);
		hGraph.insertEdgeBothWays(1, 2, 1, 1, StreetType.MOTORWAY, (byte) 0);
		hGraph.insertEdgeBothWays(2, 20, 20, 1, StreetType.MOTORWAY, (byte) 0);
		hGraph.insertEdgeBothWays(2, 21, 21, 1, StreetType.MOTORWAY, (byte) 0);
		
		hGraph.insertEdgeBothWays(2, 5, 2, 1, StreetType.MOTORWAY, (byte) 0);
		hGraph.insertEdgeBothWays(5, 6, 5, 1, StreetType.MOTORWAY, (byte) 0);
		hGraph.insertEdgeBothWays(6, 7, 6, 1, StreetType.MOTORWAY, (byte) 0);
		hGraph.insertEdgeBothWays(7, 3, 7, 1, StreetType.MOTORWAY, (byte) 0);
		hGraph.insertEdgeBothWays(3, 4, 3, 1, StreetType.MOTORWAY, (byte) 0);
		
		hGraph.insertEdgeBothWays(3, 30, 30, 1, StreetType.MOTORWAY, (byte) 0);
		hGraph.insertEdgeBothWays(3, 31, 31, 1, StreetType.MOTORWAY, (byte) 0);
		hGraph.insertEdgeBothWays(4, 40, 40, 1, StreetType.MOTORWAY, (byte) 0);
		hGraph.insertEdgeBothWays(4, 41, 41, 1, StreetType.MOTORWAY, (byte) 0);
		
		//Jetzt der Aufruf zur Kontraktion
		hGraph.contractEdges((byte)0);
		
		//ArrayList<Integer> nextCrossingIDs = new  ArrayList<Integer>();
		//ArrayList<Double> weights = new ArrayList<Double>();
		
		ArrayList<Path> nextCrossings = null;
		
		//Test1: Rufe die Methode auf einem Knoten auf, der eine Kreuzung darstellt
		try {
			//hGraph.getNextCrossingIDs(2, true, nextCrossingIDs, weights);
			nextCrossings = hGraph.getNextCrossingIDs(2, true);
		} catch (Exception e) {
			fail("Es wurde eine Exception geworfen auf korrekten Argumenten der getNextCrossingIDs: "+e.getLocalizedMessage());
		}
		
		if (nextCrossings.size()!=1)
			fail("Die ArrayList der Kreuzungen hat auf einer Kreuzung die falsche Groesse. Soll:1, Ist:"+nextCrossings.size());
		
		try {
			if (nextCrossings.get(0).getEndNodeID()!=2)
				fail("Die ArrayList der Kreuzungen hat das falsche Element auf einer Kreuzung. Soll:2, Ist:"+nextCrossings.get(0).getEndNodeID());
		
			//if (weights.get(0) !=0)
			if (nextCrossings.get(0).getPathWeight(hGraph) !=0)
				fail("Die ArrayList der Gewichte gibt auf einer Kreuzung eine falsche Entfernung zurueck. Soll: 0, Ist:"+nextCrossings.get(0).getPathWeight(hGraph));
			
			//Test 2 Rufe den Spass nun auf einer kontrahierten Node auf (auf Knten 5)
			
			
			try {
				//hGraph.getNextCrossingIDs(5, true, nextCrossingIDs, weights);
				nextCrossings = hGraph.getNextCrossingIDs(5, true);
			} catch (Exception e) {
				fail("Es wurde eine Exception geworfen auf korrekten Argumenten der getNextCrossingIDs: "+e.getLocalizedMessage());
			}
			
			if (nextCrossings.size()!=2)
				fail("Die ArrayList der Kreuzungen hat auf einer Kreuzung die falsche Groesse. Soll:2, Ist:"+nextCrossings.size());
			
			if (nextCrossings.get(0) == nextCrossings.get(1))
				fail("Die ArrayList der Kreuzungen enthaelt zweimal den gleichen Eintrag: "+nextCrossings.get(0));
			if ((nextCrossings.get(0).getEndNodeID()!= 2) && (nextCrossings.get(0).getEndNodeID()!=3))
				fail("Die erste erkannte Kreuzung ist keine Kreuzung, die hier erwartet wurde. Soll:2||3, Ist:"+nextCrossings.get(0).getEndNodeID());
			if ((nextCrossings.get(1).getEndNodeID()!= 2) && (nextCrossings.get(1).getEndNodeID()!=3))
				fail("Die zweite erkannte Kreuzung ist keine Kreuzung, die hier erwartet wurde. Soll:2||3, Ist:"+nextCrossings.get(1).getEndNodeID());
			
			
			if ((nextCrossings.get(0).getEndNodeID()==2) && (nextCrossings.get(0).getPathLength()!=1))
				fail("Die ArrayList der Gewichte gibt fuer die Kreuzung 2 eine falsche Entfernung zurueck. Soll: 1, Ist:"+nextCrossings.get(0).getPathLength());
			if ((nextCrossings.get(0).getEndNodeID()==3) && (nextCrossings.get(0).getPathLength()!=3))
				fail("Die ArrayList der Gewichte gibt fuer die Kreuzung 3 eine falsche Entfernung zurueck. Soll: 3, Ist:"+nextCrossings.get(0).getPathLength());
			if ((nextCrossings.get(1).getEndNodeID()==2) && (nextCrossings.get(1).getPathLength()!=1))
				fail("Die ArrayList der Gewichte gibt fuer die Kreuzung 2 eine falsche Entfernung zurueck. Soll: 1, Ist:"+nextCrossings.get(1).getPathLength());
			if ((nextCrossings.get(1).getEndNodeID()==3) && (nextCrossings.get(1).getPathLength()!=3))
				fail("Die ArrayList der Gewichte gibt fuer die Kreuzung 3 eine falsche Entfernung zurueck. Soll: 3, Ist:"+nextCrossings.get(1).getPathLength());
		
		} catch (PathNotFullyInitialized e) {
			fail("Es wurde eine Exception geworfen auf korrekten Argumenten der getNextCrossingIDs: "+e.getLocalizedMessage());
		} catch(NodeNotNeighbourOfPreviousElementInPathException e2) {
			fail("Es wurde eine Exception geworfen auf korrekten Argumenten der getNextCrossingIDs: "+e2.getLocalizedMessage());
		}
		//Neuer Testfall: Teste mit Einbahnstrassen
		hGraph = new HierarchyMapGraph();
		
		//Knoten -> spaeter sollen 5,6,7 kontrahiert werden
		hGraph.insertNode(1, 1, 1);
		hGraph.insertNode(2, 1, 1);
		hGraph.insertNode(3, 1, 1);
		hGraph.insertNode(4, 1, 1);
		hGraph.insertNode(10, 1, 1);
		
		hGraph.insertNode(11, 1, 1);
		hGraph.insertNode(20, 1, 1);
		hGraph.insertNode(21, 1, 1);
		hGraph.insertNode(30, 1, 1);
		hGraph.insertNode(31, 1, 1);
		
		hGraph.insertNode(40, 1, 1);
		hGraph.insertNode(41, 1, 1);
		hGraph.insertNode(5, 1, 1);
		hGraph.insertNode(6, 1, 1);
		hGraph.insertNode(7, 1, 1);
		
		//Kanten
		hGraph.insertEdgeBothWays(1, 10, 10, 1, StreetType.MOTORWAY, (byte) 0);
		hGraph.insertEdgeBothWays(1, 11, 11, 1, StreetType.MOTORWAY, (byte) 0);
		hGraph.insertEdgeBothWays(1, 2, 1, 1, StreetType.MOTORWAY, (byte) 0);
		hGraph.insertEdgeBothWays(2, 20, 20, 1, StreetType.MOTORWAY, (byte) 0);
		hGraph.insertEdgeBothWays(2, 21, 21, 1, StreetType.MOTORWAY, (byte) 0);
		
		//Rueckweg duerfte kontrahiert werden
		hGraph.insertEdge(2, 5, 2, 1, StreetType.MOTORWAY, (byte) 0);
		hGraph.insertEdge(5, 6, 5, 1, StreetType.MOTORWAY, (byte) 0);
		hGraph.insertEdge(6, 7, 6, 1, StreetType.MOTORWAY, (byte) 0);
		hGraph.insertEdge(7, 3, 7, 1, StreetType.MOTORWAY, (byte) 0);
		
		hGraph.insertEdgeBothWays(3, 4, 3, 1, StreetType.MOTORWAY, (byte) 0);
		
		hGraph.insertEdgeBothWays(3, 30, 30, 1, StreetType.MOTORWAY, (byte) 0);
		hGraph.insertEdgeBothWays(3, 31, 31, 1, StreetType.MOTORWAY, (byte) 0);
		hGraph.insertEdgeBothWays(4, 40, 40, 1, StreetType.MOTORWAY, (byte) 0);
		hGraph.insertEdgeBothWays(4, 41, 41, 1, StreetType.MOTORWAY, (byte) 0);
		
		//Jetzt der Aufruf
		hGraph.contractEdges((byte)0);
		
		
		try {
			//hGraph.getNextCrossingIDs(5, true, nextCrossingIDs, weights);
			nextCrossings = hGraph.getNextCrossingIDs(5, true); 
		
		
			if (nextCrossings.size()!=1)
				fail("Die ArrayList der Kreuzungen hat auf Einbahnstrasse die falsche Groesse. Soll:1, Ist:"+nextCrossings.size());
			if (nextCrossings.get(0).getEndNodeID()!=3)
				fail("Die ArrayList der Kreuzungen hat das falsche Element auf einer Einbahnstrasse. Soll:3, Ist:"+nextCrossings.get(0).getEndNodeID());
			if (nextCrossings.get(0).getPathLength() !=3)
				fail("Die ArrayList der Gewichte gibt auf einer Einbahnstrasse eine falsche Entfernung zurueck. Soll: 3, Ist:"+nextCrossings.get(0).getPathLength());
		
		} catch (Exception e) {
			fail("Es wurde eine Exception geworfen auf korrekten Argumenten der getNextCrossingIDs: "+e.getLocalizedMessage());
		}
	}
	
	/**
	 * Dieser Test testet die deleteSelfLoopsAndParallelEdges-Methode
	 */
	@Test
	public void DeleteLoopsTest() {
		
		HierarchyMapGraph hGraph = new HierarchyMapGraph();
		
		//Test 1: parallele Kanten - wird die richtige entfernt?
		hGraph.insertNode(1, 1, 1);
		hGraph.insertNode(2, 1, 1);
		
		hGraph.insertEdge(1, 2, 1, 2, StreetType.PRIMARY, (byte)1);
		hGraph.insertEdge(1, 2, 2, 3, StreetType.PRIMARY, (byte)1);
		
		
		hGraph.deleteSelfLoopAndParallelEdges((byte)1);
		
		
		if (hGraph.getNode(1).getLevel()!=1)
			fail("Es wurde das Level der Node geandert, was cnit erlaubt war");
		if (hGraph.getNode(2).getLevel()!=1)
			fail("Es wurde das Level der Node geandert, was cnit erlaubt war");
		if (hGraph.getNode(1).getOutgoingEdgesByHierarchy((byte)1).size()!=1)
			fail("Es wurde die parallele kante nciht geloescht!");
		if (hGraph.getNode(1).getOutgoingEdgesByHierarchy((byte)1).get(0).getUID()!=1)
			fail("Es wurde die falsche (parallele) Kante geloescht!");
		
		//Test 2: self-Loop
		hGraph.insertEdgeBothWays(1, 1, 5, 5, StreetType.MOTORWAY, (byte)1);
		hGraph.deleteSelfLoopAndParallelEdges((byte)1);
		
		if (hGraph.getNode(1).getIncomingEdgesByHierarchy((byte)1).size()!=0)
			fail("Es wurden nciht alle Self-Loops entfernt!");
		if (hGraph.getNode(1).getOutgoingEdgesByHierarchy((byte)1).size()!=1)
			fail("Es wurden nciht alle Self-Loops entfernt!");
		
	}

}
