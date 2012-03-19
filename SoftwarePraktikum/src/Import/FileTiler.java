package Import;

/**
 * 
 * Der FileTiler wird im OSMImporter instantziert, um eine zu grosse .osm Datei in "Tiles" einer bestimmten Groesse zu unterteilen. 
 * Die Tiles werden aus der uebergebenen Datei eingelesen und numeriert wieder in den Arbeitsordner geschrieben. 
 * Die Einteilung findet dabei von oben links nach unten rechst statt.
 *Zusaetzlich wird ein wayfile angelegt in dem nur die Ways der .osm Datei gespeichert sind.
 */

import graphenbib.GPSCoordinate;
import graphenbib.GPSRectangle;
import graphexceptions.EmptyInputException;
import graphexceptions.InvalidGPSCoordinateException;
import graphexceptions.InvalidInputException;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Iterator;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.stream.StreamSource;

import main.Config;
import main.Logger;

public class FileTiler {
	private float minlat, minlon, maxlat, maxlon;
	private int erdradius = Config.erdRadius;

	private float latSize = (float) (Config.TileGroesse * 360 / (2 * Math.PI * erdradius)); // Seitenlaenge in Grad der Karte umrechnen
	private float lonSize = (float) (Config.TileGroesse * 360 / (2 * Math.PI * erdradius)); // Seitenlaenge in Grad der Karte umrechnen	
	private ArrayList<BufferedWriter> writers = new ArrayList<BufferedWriter>();
	private ArrayList<GPSRectangle> bounds = new ArrayList<GPSRectangle>();

	/**
	 * Teilt eine OSM Datei in kleine Stuecke.
	 * 
	 * @param readF
	 *            Dateipfad der zu teilenden .osm Datei
	 * @param destination
	 *            Dateipfad des Ordners in dem die Dateien gespeichert werden
	 *            sollen.
	 * @throws XMLStreamException
	 *             Streamfehler beim Parsen.
	 * @throws IOException
	 *             Fehler beim lesen/schreiben.
	 * @throws EmptyInputException
	 *             Falls ein GPSRectangle ohne Bounds angelegt wird.
	 * @throws InvalidInputException
	 *             Falls die bestimmten Bounds nicht korrekt sind.
	 * @throws InvalidGPSCoordinateException
	 *             Falls die bestimmten Bounds keine korrekten GPS Koordinaten
	 *             sind.
	 */
	public synchronized void TileFile(String readF, String destination)
			throws XMLStreamException, IOException, InvalidInputException,
			InvalidGPSCoordinateException, EmptyInputException {

		Logger.getInstance()
				.log("FileTiler",
						"Starte Tiling, dies kann je nach Kartengroesse, einige Minuten bis mehrere Stunden dauern.");
		long timeStarted = System.currentTimeMillis();
		File f = new File(destination);
		File[] filearray = f.listFiles();

		for (File i : filearray) {
			i.delete();
		}

		XMLInputFactory factory = XMLInputFactory.newInstance();
		XMLStreamReader parser = factory
				.createXMLStreamReader(new StreamSource(readF));

		int ID = 0, index = 0, event;
		float lat = 0, lon = 0;
		boolean wayFlag = false, firstFlag = true;

		File wayfile = new File(destination + File.separatorChar
				+ "wayfile.osm"); // Wayfile anlegen
		BufferedWriter waywriter = new BufferedWriter(new OutputStreamWriter(
				new FileOutputStream(wayfile), "UTF8"));
		xmlinit(waywriter);

		GPSRectangle actual;
		Logger.getInstance()
				.log("FileTiler", "Bestimung der Bounds gestartet.");

		while (true) { // Geht alle Knoten durch und bestimmt die Bounds der
						// Datei
			event = parser.next();
			if (event == XMLStreamConstants.END_DOCUMENT) {
				parser.close();
				break;
			}
			if (event == XMLStreamConstants.START_ELEMENT) {
				if (parser.getLocalName().equalsIgnoreCase("node")) {
					lat = Float.parseFloat(parser
							.getAttributeValue(null, "lat"));
					lon = Float.parseFloat(parser
							.getAttributeValue(null, "lon"));
					if (firstFlag) {
						firstFlag = false;
						minlat = maxlat = lat;
						minlon = maxlon = lon;
					}

					if (lat > maxlat)
						maxlat = lat;
					if (lat < minlat)
						minlat = lat;
					if (lon > maxlon)
						maxlon = lon;
					if (lon < minlon)
						minlon = lon;

				}

			}

		}
		Logger.getInstance().log("FileTiler",
				"Bounds wurden bestimmt. Beginne Tiling.");

		parser = factory.createXMLStreamReader(new StreamSource(readF));

		// Bereiche festlegen und files erstellen.
		float minlontemp = minlon;
		while (maxlat > minlat) {
			while (minlontemp < maxlon) {

				File file = new File(destination + File.separatorChar + "file"
						+ index + ".tile");
				BufferedWriter writer = new BufferedWriter(
						new OutputStreamWriter(new FileOutputStream(file),
								"UTF8"));
				writers.add(index, writer);
				bounds.add(index, new GPSRectangle(new GPSCoordinate(maxlat,
						minlontemp), new GPSCoordinate(maxlat - latSize,
						minlontemp + lonSize)));
				xmlinit(writers.get(index), bounds.get(index));
				minlontemp += lonSize;
				index++;
			}
			maxlat -= latSize;
			minlontemp = minlon;
		}

		// Parsen der Datei und verteilen auf die Tiles
		while (true) {
			event = parser.next();
			if (event == XMLStreamConstants.END_DOCUMENT) { // Abbruchbedingung
				parser.close();
				Iterator<BufferedWriter> it = writers.iterator();
				while (it.hasNext()) {
					xmldone(it.next()); // Files Schliessen
				}
				xmldone(waywriter);
				break;

			}

			switch (event) {
			case XMLStreamConstants.START_ELEMENT:

				// Node
				if (parser.getLocalName().equalsIgnoreCase("node")) {
					ID = Integer.parseInt(parser.getAttributeValue(null, "id"));
					lat = Float.parseFloat(parser
							.getAttributeValue(null, "lat"));
					lon = Float.parseFloat(parser
							.getAttributeValue(null, "lon"));

					Iterator<GPSRectangle> it = bounds.iterator();
					index = 0;
					while (it.hasNext()) {
						actual = it.next();
						if (lat >= actual.getMinLat()
								&& lat <= (actual.getMaxLat())
								&& lon >= actual.getMinLon()
								&& lon <= actual.getMaxLon()) {
							String str = "<node id=\"" + ID + "\" lat=\"" + lat
									+ "\" lon=\"" + lon + "\"/>";
							writeLine(writers.get(index), str);
						}
						index++;
					}
				}

				// Way
				if (parser.getLocalName().equalsIgnoreCase("way")) {
					wayFlag = true;
					String str = "<way id=\""
							+ parser.getAttributeValue(null, "id") + "\">";
					writeLine(waywriter, str);
				}
				if (wayFlag && parser.getLocalName().equalsIgnoreCase("nd")) {
					String str = "<nd ref=\""
							+ parser.getAttributeValue(null, "ref") + "\"/>";
					writeLine(waywriter, str);
				}
				if (wayFlag && parser.getLocalName().equalsIgnoreCase("tag")) {
					String str = parser.getAttributeValue(null, "k");
					if (str.equalsIgnoreCase("highway")
							|| str.equalsIgnoreCase("oneway")
							|| str.equalsIgnoreCase("name")) {
						String substring = parser.getAttributeValue(null, "v");

						// Sonderzeichen entfernen die Parsing probleme
						// verursachen
						substring = substring.replaceAll("\"", "");
						substring = substring.replaceAll("&", "und");
						substring = substring.replaceAll("/", ".");
						substring = substring.replaceAll("<", "");
						substring = substring.replaceAll(">", "");

						str = "<tag k=\"" + parser.getAttributeValue(null, "k")
								+ "\" v=\"" + substring + "\"/>";

						writeLine(waywriter, str);
					}
				}
				break;

			case XMLStreamConstants.END_ELEMENT:
				// Way ende erkennen
				if (wayFlag && parser.getLocalName().equalsIgnoreCase("way")) {
					wayFlag = false;
					writeLine(waywriter, "</way>");

				}
				break;
			}
		}
		long timeNeeded = System.currentTimeMillis() - timeStarted;
		Logger.getInstance().log("FileTiler",
				"Done! Benoetigte Gesamtzeit: " + timeNeeded / 1000 + " sec.");

		return;
	}

	/**
	 * Schreibt eine Zeile XML Code.
	 * 
	 * @param writer
	 *            Writer auf den geschrieben wird.
	 * @param line
	 *            Zu schreibender Text.
	 * @throws IOException
	 *             Falls ein Fehler beim schreiben auftritt.
	 */
	private void writeLine(BufferedWriter writer, String line)
			throws IOException {
		writer.write(line);
		writer.newLine();
		writer.flush();
	}

	/**
	 * Erzeugt einen XML Verion 1.0 Header fuer OSM Dateien.
	 * 
	 * @param writer
	 *            Writer auf den geschrieben wird.
	 * @throws IOException
	 *             Falls ein Fehler beim schreiben auftritt.
	 */
	private void xmlinit(BufferedWriter writer) throws IOException {
		String str = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
		writeLine(writer, str);
		str = "<osm version=\"0.6\" generator=\"CGImap 0.0.2\">";
		writeLine(writer, str);
	}

	/**
	 * Erzeugt einen XML Verion 1.0 Header fuer OSM Dateien, wobei die Bounds
	 * mit geschreiben werden.
	 * 
	 * @param writer
	 *            Writer auf den geschrieben wird.
	 * @param bounds
	 *            Grenzkoordinaten der Datei.
	 * @throws IOException
	 *             Falls ein Fehler beim schreiben auftritt.
	 */
	private void xmlinit(BufferedWriter writer, GPSRectangle bounds)
			throws IOException {
		String str = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
		writeLine(writer, str);
		str = "<osm version=\"0.6\" generator=\"CGImap 0.0.2\">";
		writeLine(writer, str);
		str = "<bounds minlat=\"" + bounds.getMinLat() + "\" minlon=\""
				+ bounds.getMinLon() + "\" maxlat=\"" + bounds.getMaxLat()
				+ "\" maxlon=\"" + bounds.getMaxLon() + "\"/>";
		writeLine(writer, str);
	}

	/**
	 * Erzeugt einen XML Verion 1.0 Footer fuer OSM Dateien.
	 * 
	 * @param writer
	 *            Writer auf den geschrieben wird.
	 * @throws IOException
	 *             Falls ein Fehler beim schreiben auftritt.
	 */
	private void xmldone(BufferedWriter writer) throws IOException {
		writeLine(writer, "</osm>");
		writer.close();
	}

}
