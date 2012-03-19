package ui;


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

		final MainFrame mf = new MainFrame(800,600);

		//		try {
		//			MainPreProc.main(new File("duesseldorf.osm"));
		//		} catch (Exception e) {
		//			// TODO Auto-generated catch block
		//			e.printStackTrace();
		//		}


	}

}
