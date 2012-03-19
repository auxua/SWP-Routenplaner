/**
 * 
 */
package Import;

import static org.junit.Assert.fail;
import graphenbib.MapGraph;

import java.io.File;

import org.junit.BeforeClass;
import org.junit.Test;

public class OSMImporterFilterTest {

	private static MapGraph testGraph = null;
	private static OSMImporter osm_imp = null;
	
	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		try {
			osm_imp = new OSMImporter(new File("testdateien/FilterGraph1.osm"));
			testGraph = osm_imp.getTile(osm_imp.getMapCenter(), 0);
		}
		catch(Exception e) {
			fail("Fehler beim Importieren: "+e.getLocalizedMessage());
		}
	}

	@Test
	public void test() {
		int i = testGraph.getSize();
		if (i ==0)
			fail("Es wurden alle Nodes geloescht - also keine Kante importert, also weder motorway_link, noch trunk_link noch primary_link");
		
		if (testGraph.getNode(2)!=null)
			fail("Es wurde eine Kante mit hghway=path importiert");
		
		if (testGraph.getNode(3)!=null)
			fail("Es wurde eine Kante mit hghway=bridleway importiert");
		
		if (testGraph.getNode(4)!=null)
			fail("Es wurde eine Kante mit hghway=cycleway importiert");
		
		if (testGraph.getNode(5)!=null)
			fail("Es wurde eine Kante mit hghway=footway importiert");
		
		if (testGraph.getNode(6)!=null)
			fail("Es wurde eine Kante mit hghway=pedestrian importiert");
		
		if (testGraph.getNode(7)!=null)
			fail("Es wurde eine Kante mit hghway=track importiert");
		
		if (testGraph.getNode(8)!=null)
			fail("Es wurde eine Kante mit hghway=byway importiert");
		
		if (testGraph.getNode(9)!=null)
			fail("Es wurde eine Kante mit hghway=steps importiert");
		
		if (testGraph.getNode(10)!=null)
			fail("Es wurde eine Kante mit hghway=proposed importiert");
		
		//Die hier sollten aber drin sein:
		if (testGraph.getNode(11)==null)
			fail("Es wurde keine Kante mit hghway=motorway_link importiert");
		
		if (testGraph.getNode(12)==null)
			fail("Es wurde keine Kante mit hghway=trunk_link importiert");
		
		if (testGraph.getNode(13)==null)
			fail("Es wurde keine Kante mit hghway=primary_link importiert");
		
		
		//Nun die test fuer die motorcar-tags
		if (testGraph.getNode(14)!=null)
			fail("Es wurde eine Kante mit hghway=unclassified, motorcar=no importiert");
		
		if (testGraph.getNode(15)!=null)
			fail("Es wurde eine Kante mit hghway=unclassified, motorcar=delivery importiert");
	}

}
