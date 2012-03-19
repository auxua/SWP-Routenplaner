/**
 * 
 */
package main;

import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JOptionPane;

public class Logger {
	
	/**
	 * ier wird die Instanz gespeichert
	 */
	private static Logger instance = null; 
	
	private static boolean timeOutput = false;
	
	private static String newline = System.getProperty("line.separator");
	
	public static void setTimeOutput(boolean timeOutput) {
		Logger.timeOutput = timeOutput;
	}

	/**
	 * Erlabt das stummschalten des Logger - keine Verschachtelung!
	 */
	public static void mute() {
		oldOutput = output;
		setOutput(Output.SILENT);
	}
	
	/**
	 * Diese Methde schaltet die Stummschaltung wieder aus
	 */
	public static void unmute() {
		setOutput(oldOutput);
	}
	
	/**
	 * Der Singleton-Instance-Getter 
	 * legt beim ersten Aufruf automatisch eine Instanz an
	 */
	public static Logger getInstance() {
		if (instance == null) 
			instance = new Logger();
		return instance;
	}
	
	/**
	 * Diese Variable bestimmt die Ausgabe
	 * Diese Moeglichkeiten richten sich dabei an dem Enum Output aus
	 * Es werden getter/Setter bereitgestellt, um bei Dateiausgabe diese sofort zu reservieren 
	 */
	private static Output output = Output.CONSOLE;
	private static Output oldOutput;
	
	/**
	 * Dies ist der Dateiname, der fuer diesen Logger reserviert werden soll
	 */
	private String fileName = "";
	
	/**
	 * Dies ist der Pfad, in dem die Datei liegen wird
	 */
	private String filePath = "logs";
	

	private File file = null;
	private FileWriter writer = null;
	
	/**
	 * Der private Konstruktor, um den Logger als Singleton zu realisieren
	 * Dadurhch werden unter anderem konkurierende Logger verhindert
	 */
	private Logger() {
		 //Der Konstruktor ist privat
	}
	
	/**
	 * Setter fuer den Output
	 * Beachte: Hier wird keine Datei erstellt - dies geschieht dann beim ersten loggen!
	 */
	public static void setOutput(Output op) {
		output = op;
	}
	
	/**
	 * Getter fuer den Output
	 */
	public static Output getOutput() {
		return output;
	}
	
	/**
	 * Getter fuer den Dateinamen (nur die Datei!)
	 */
	public String getFileName() {
		return fileName;
	}
	
	/**
	 * Getter fuer den Dateipfad (nur der Pfad!)
	 */
	public String getFilePath() {
		return filePath;
	}
	
	/**
	 * Getter fuer den vollstaendigen Dateipfad
	 */
	public String getFullFileName() {
		return filePath+File.separatorChar+fileName;
	}
	
	/**
	 * Hier wird eine Datei angelegtund der Filewriter zur Verfuegung gestellt
	 * Tritt dabei ein Fehler auf, stellt sich der Logger automatisch auf Consolenausgabe um und logt den Fehler
	 * @return true, wenn erfolgreich, sonst false
	 */
	private boolean assignFile() {
		//Der Dateiname wird aufgebaut: logxxxxxxx.log - wobei xxxxxx der Zeitstempel der erstellung des Files ist
		//Dies kann spaeter evtl. sinnvoller gemacht werden
		//Annahme: System kann mehr als 8+3 im Dateinamen!
		
		Date date = new Date();
		SimpleDateFormat format;
		
		format = new SimpleDateFormat("yyMMdd_HHmmss_S");
		
		String suffix = format.format(date);
		
		fileName = "log"+suffix+".log";
		
		//Erstelle Datei - bei Fehlern wird automatisch auf Console umgestellt und der Fehler ausgegeben!
		try {
			file = new File(filePath+File.separatorChar+fileName);
			//Sollte eine Datei existieren mit diesem Namen, ist sie offensichtlichnciht vion diesem Programm und wird ueberschrieben
			writer = new FileWriter(file);
			writer.write("Log-File von TimeStamp: "+suffix);
			writer.write(newline);
			writer.write(newline);
			writer.flush();
		} catch (Exception e) {
			output = Output.CONSOLE;
			log("Logger", "Fehler beim Anlegen der Datei in AssignFile - Fehler: "+e.getLocalizedMessage());
			return false;
		}
		return true;
	}
	
	/**
	 * Diese Methode erlaubt auch Ausgabe von Objekten
	 * @param sender Des Sender, welcher etwas loggen will (Klasse/Methode)
	 * @param o Das Object (verwendet toString-Methode)
	 */
	public void log(String sender, Object o) {
		this.log(sender, o.toString());
	}
	
	/**
	 * Diese Methode stellt das Logging zur Verfuegung
	 * Existiert noch keine Datei fuer diesen Logger, wird sie hier automatisch erstellt
	 * @param sender Der Sender, welcher etwas loggen moechte (Package, Klassenname, oder auch Methodenname)
	 * @param message Die Nachricht, die geloggt werden soll
	 */
	public void log(String sender, String message) {
		//Soll die zeit mit ausgegeben werden?
		if (timeOutput) {
			SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
			sender = sender+"@ "+sdf.format(new Date());
		}
		//Hier geschieht das Logging
		switch(output) {
			case SILENT: //keine Ausgabe
				break;
			case CONSOLE: //Ausgabe in die Console
				System.out.println("["+sender+"]: "+message);
				break;
			case MESSAGE: //Ausgabe als MessageBoxen
				JOptionPane.showMessageDialog(null,message,sender, JOptionPane.CANCEL_OPTION);
				break;
			case FILE: //Ausgabe in eine Datei leiten
				//Teste zuerst, ob die Datei bereits bereit
				if (writer == null) {
					boolean erfolg = assignFile();
					//Wenn es einen Fehler beim Anlegen der Datei gab, wurde der Output umgestellt. Leite die Nachricht also an logging-Methode weiter
					if (erfolg == false) {
						log(sender,message);
						break;
					}
				}	
				//Nun existiert auf jeden Fall die Datei und der writer
				try {
					writer.write("["+sender+"]: "+message+newline);
					writer.flush();
				} catch (Exception e) {
					output = Output.CONSOLE;
					log("Logger", "Fehler beim Anlegen der Datei in Log - Fehler: "+e.getLocalizedMessage());
				}
				break;
		}
	}
	
	/**
	 * Wird in eine Datei geloggt soll beim Vernichtung dieses Objektes das schliessen des Filewriters sichergestellt werden!
	 * Danach wird die vererbte finalize-Metode aufgerufen und der Logger ist Geschichte
	 */
	protected void finalize() throws Throwable {
		try {
			if (writer != null)
				writer.close();
		} catch (Exception e) {
			System.out.println("Fehler im Finalisierer - "+e.getLocalizedMessage());
		} finally {
			super.finalize();
		}
	}
}
