package graphenbib;

import static org.junit.Assert.fail;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

import main.Logger;

import org.junit.Test;


public class TestIO {
	
	
	/**
	 * Dieser Test erstellt einen Graphen, speichert ihn und liest ihn wieder aus
	 */
	@Test
	public void writeMapGraph() {
		OutputStream fos = null; 
		MapGraph mapTest=null;
		try {
			GPSRectangle gr=new GPSRectangle(90, 180, 0, 0);
			mapTest= new MapGraph(gr);
			GPSCoordinate gc=new GPSCoordinate(2f, 1f);
			mapTest.insertNode(0, gc);
			mapTest.insertNode(2, gc);
			mapTest.insertEdge(0, 2, 0, 1, StreetType.LIVING_STREET);
			mapTest.insertNode(1, gc);
		} catch (Exception e) {
			fail("Fehler bei Graph anlegen "+e.getLocalizedMessage());
		}
		Logger.getInstance().log("TestIO","MapGraph vor dem Speichern");
		Logger.getInstance().log("TestIO",mapTest.toString());
		//Schreiben testen
		try 
		{ 
		  fos = new FileOutputStream("test.ser"); 
		  ObjectOutputStream o = new ObjectOutputStream( fos ); 
		  o.writeObject(mapTest); 
		 
		} 
		catch ( IOException e ) { Logger.getInstance().log("ERROR",e.getLocalizedMessage()); } 
		finally { 
			try { 
				fos.close(); 
			} 
				catch ( Exception e ) {
					e.printStackTrace();
				} 
		}
		
		//Anschliessendes Auslesen testen
		InputStream fis = null; 
		MapGraph mapRead=null; 
		
		try 
		{ 
		  fis = new FileInputStream("test.ser"); 
		  ObjectInputStream o = new ObjectInputStream( fis ); 
		 mapRead= (MapGraph) o.readObject(); 
		} 
		catch ( IOException e ) { 
			Logger.getInstance().log("ERROR",e.getLocalizedMessage()); 
		} 
		catch ( ClassNotFoundException e ) { 
			Logger.getInstance().log("ERROR",e.getLocalizedMessage()); 
		} 
		finally { 
			try { 
				fis.close(); 
			} catch ( Exception e ) {
				e.printStackTrace();
			}
		}
		Logger.getInstance().log("TestIO","Wieder ausgelesener MapGraph");
		Logger.getInstance().log("TestIO",mapRead.toString());
	}
	
	@Test
	public void writeHGraph(){
		HierarchyMapGraph hGraph = new HierarchyMapGraph();
		hGraph.insertNode(0);
		hGraph.insertNode(1);
		hGraph.insertEdge(0, 1, 1, 1, StreetType.LIVING_STREET, (byte)0);
		//Anschliessendes Auslesen testen
		
		//Schreiben testen
		OutputStream fos = null; 
		try 
		{ 
		  fos = new FileOutputStream("test2.ser"); 
		  ObjectOutputStream o = new ObjectOutputStream( fos ); 
		  o.writeObject(hGraph); 
		 
		} 
		catch ( IOException e ) { Logger.getInstance().log("ERROR",e.getLocalizedMessage()); } 
		finally { 
			try { 
				fos.close(); 
			} 
				catch ( Exception e ) {
					e.printStackTrace();
				} 
		}
				
		
		InputStream fis = null; 
		HierarchyMapGraph hGraphRead=null; 
		
		try 
		{ 
		  fis = new FileInputStream("test2.ser"); 
		  ObjectInputStream o = new ObjectInputStream( fis ); 
		  hGraphRead= (HierarchyMapGraph) o.readObject(); 
		} 
		catch ( IOException e ) { 
			Logger.getInstance().log("ERROR",e.getLocalizedMessage()); 
		} 
		catch ( ClassNotFoundException e ) { 
			Logger.getInstance().log("ERROR",e.getLocalizedMessage()); 
		} 
		finally { 
			try { 
				fis.close(); 
			} catch ( Exception e ) {
				e.printStackTrace();
			}
		}
		Logger.getInstance().log("TestIO","Wieder ausgelesener HierarchyMapGraph");
		Logger.getInstance().log("TestIO",hGraphRead.toString());
		
	}
}
