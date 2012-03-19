package main;


import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ Import.AllImportTests.class, graphenbib.AllGraphTests.class, algorithmen.AllAlgoTests.class, LoggerTest.class })
public class AllFunctionTests {
	
}
