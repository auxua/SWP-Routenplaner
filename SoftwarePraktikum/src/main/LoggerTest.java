/**
 * 
 */
package main;

import static org.junit.Assert.fail;

import org.junit.Test;

public class LoggerTest {

	@Test
	public void test() {
		//Ein kleiner Test um zu gucken, ob der Logger funktioniert
		Logger temp = null;
		
		/*
		 * Test 1 - kann ich mal ne Instanz haben?
		 */
		
		try {
			temp = Logger.getInstance();
		} catch (Exception e) {
			fail("Es gabe einen Fehler beim Abholen einer Instanz: "+e.getLocalizedMessage());
		}
		
		/*
		 * Test 2 - Logge doch mal bitte was in die Konsole
		 */
		Logger.setOutput(Output.CONSOLE);
		try {
			temp.log("LoggerTest", "Dies ist ein Test-Log - na, sieht man mich?");
		} catch (Exception e) {
			fail("Es gabe einen Fehler beim Loggen (Console): "+e.getLocalizedMessage());
		}
		
		/*
		 * Test 3 - Logge doch mal bitte was in eine nachricht
		 */
		Logger.setOutput(Output.MESSAGE);
		try {
			temp.log("LoggerTest", "Dies ist ein Test-Log - na, sieht man mich?");
		} catch (Exception e) {
			fail("Es gabe einen Fehler beim Loggen (Box): "+e.getLocalizedMessage());
		}
		
		/*
		 * Test 4 - Logge doch mal bitte was in eine Datei
		 */
		Logger.setOutput(Output.FILE);
		try {
			temp.log("LoggerTest", "Dies ist ein Test-Log - na, sieht man mich?");
		} catch (Exception e) {
			fail("Es gabe einen Fehler beim Loggen (File): "+e.getLocalizedMessage());
		}
		
		
	}

}
