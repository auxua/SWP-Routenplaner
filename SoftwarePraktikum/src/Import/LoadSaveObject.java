package Import;

import graphenbib.HierarchyMapGraph;
import graphenbib.MapGraph;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import main.Logger;

/**
 * Klasse zum Lesen und Schreiben eines Objektes in eine Datei
 * 
 * 
 */
public class LoadSaveObject {

	private static Logger logger = Logger.getInstance();

	/**
	 * MapGraph in Datei f schreiben/seriaisieren
	 * 
	 * @param f
	 *            File
	 * @param mg
	 *            MapGraph
	 */
	public static void saveObject(File f, Object mg) {
		try {
			logger.log("LoadSaveObject", "Saving: " + f.getName());
			ObjectOutputStream oos = new ObjectOutputStream(
					new FileOutputStream(f));
			oos.writeObject(mg);
			oos.close();
			// logger.log("LoadSaveObject", "Saving done!");
		} catch (Exception e) {
			logger.log("LoadSaveObject.Save", e.getLocalizedMessage());
		}
	}

	/**
	 * MapGraph aus Datei f einlesen /deserialisieren
	 * 
	 * @param f
	 *            einzulesende Datei
	 * @return erzeugter MapGraph
	 */
	public static Object loadObject(File f) {
		Object mg = null;
		try {
			logger.log("LoadSaveObject", "Loading: " + f.getName());
			ObjectInputStream oos = new ObjectInputStream(
					new FileInputStream(f));
			mg = oos.readObject();
			oos.close();
			// logger.log("LoadSaveObject", "Loading done!");
		} catch (Exception e) {
			logger.log("LoadSaveObject.Load", e.getLocalizedMessage());
		}

		return mg;
	}

	/**
	 * Laedt den MapGraph aus der Datei.
	 * 
	 * @param f
	 *            Datei
	 * @return Gibt den gesuchten MapGraph zurueck.
	 */
	public static MapGraph loadMGraph(File f) {
		return (MapGraph) loadObject(f);
	}

	/**
	 * Laedt den HierarchyMapGraph aus der Datei.
	 * 
	 * @param f
	 *            Datei
	 * @return Gibt den gesuchten HierarchyMapGraphen zurueck.
	 */
	public static HierarchyMapGraph loadHGraph(File f) {
		return (HierarchyMapGraph) loadObject(f);
	}
}
