package ui;

import main.Logger;


@SuppressWarnings("unused")
public class TestMain {

	/**
	 * Pseudo Mainklasse zum Testen von MainFrame
	 * @param args
	 */
	public static void main(String[] args) {

		//		//Warteschleife fur das Profiling
		//		BufferedReader console = new BufferedReader(new InputStreamReader(System.in));
		//		System.out.print("Geben Sie etwas ein: ");
		//		String zeile = null;
		//		try {
		//			zeile = console.readLine();
		//		} catch (IOException e) {
		//			// Sollte eigentlich nie passieren
		//			e.printStackTrace();
		//		}

		main.Logger.setTimeOutput(true);
		try {
			final MainFrame mf = new MainFrame(800,600);
		} catch (StackOverflowError stErr) {
			System.err.println("Fehler: Zu kleiner Stack. Bitte starten mit der -Xss-Option");
		} catch (OutOfMemoryError memErr) {
			System.err.println("Fehler: Zu kleiner Speicher. Moeglicher Fix: -Xmx / -Xms Optionen");
		}

		//		try {
		//			MainPreProc.main(new File("duesseldorf.osm"));
		//		} catch (Exception e) {
		//			// TODO Auto-generated catch block
		//			e.printStackTrace();
		//		}


	}

}
