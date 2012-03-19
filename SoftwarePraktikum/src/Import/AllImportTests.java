package Import;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ FileTilerImportTest.class, OSMImporterFilterTest.class,
		OSMImporterGraphenTest.class })
public class AllImportTests {

}
