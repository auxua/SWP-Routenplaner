package graphenbib;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ HierarchyMapGraphNodeTest.class, MapGraphTest.class,
		MapNodeTest.class, PathTest.class, TestIO.class })
public class AllGraphTests {

}
