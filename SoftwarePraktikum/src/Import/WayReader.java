package Import;

import graphenbib.StreetType;
import graphexceptions.EmptyInputException;
import graphexceptions.InvalidGPSCoordinateException;
import graphexceptions.InvalidInputException;
import graphexceptions.NodeNotInGraphException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.stream.StreamSource;

import main.Config;
import main.Logger;

public class WayReader {

	WayReader() {
	}

	/**
	 * Liest eine OSM Datei ein und baut daraus ein WayFile Objekt, welches zum
	 * Einlesen der Wege in den eizelnen Tiles genutz wird.
	 * 
	 * @param source
	 *            StreamSource der OSM Datei.
	 * @return MapGraph zur OSM Datei.
	 * @throws IOException
	 *             Falls Fehler beim lesen/schreiben auftreten.
	 * @throws XMLStreamException
	 *             Falls Fehler beim streamen/parsen auftreten.
	 * @throws EmptyInputException
	 *             Falls ein GPSRectangle ohne Bounds angelegt wird.
	 * @throws InvalidInputException
	 *             Falls die bestimmten Bounds nicht korrekt sind.
	 * @throws InvalidGPSCoordinateException
	 *             Falls die bestimmten Bounds keine korrekten GPS Koordinaten
	 *             sind.
	 * @throws NodeNotInGraphException
	 *             Falls der Knoten der eingefgt wird nicht innerhalb der Bounds
	 *             liegt.
	 */
	public WayFile readWays(StreamSource source) throws IOException,
			XMLStreamException, EmptyInputException, InvalidInputException,
			InvalidGPSCoordinateException, NodeNotInGraphException {
		int event;
		long timeNeeded = System.currentTimeMillis();
		WayFile ways = new WayFile();

		XMLInputFactory factory = XMLInputFactory.newInstance();
		XMLStreamReader parser = factory.createXMLStreamReader(source);

		int ID = 0, ref = 0, refSafe = 0, index = 0, nrWays = 0;

		boolean wayFlag = false, highway = false, oneway = false, allowcar = true, allowaccess = true;
		String name = "";

		ArrayList<ImportEdge> aList = new ArrayList<ImportEdge>();

		StreetType streetType = StreetType.UNKNOWN;

		while (true) {
			event = parser.next();
			if (event == XMLStreamConstants.END_DOCUMENT) {
				parser.close();
				break;
			}

			switch (event) {
			case XMLStreamConstants.START_ELEMENT:

				// Way
				if (parser.getLocalName().equalsIgnoreCase("way")) {
					ID = Integer.parseInt(parser.getAttributeValue(null, "id"));
					wayFlag = true;
					index = 0;
				}

				if (wayFlag && parser.getLocalName().equalsIgnoreCase("nd")) {
					refSafe = ref;
					ref = Integer.parseInt(parser
							.getAttributeValue(null, "ref"));

					if (index > 0) {
						aList.add(new ImportEdge(ID, refSafe, ref, 0, null));
					}
					index++;
				}
				if (wayFlag && parser.getLocalName().equalsIgnoreCase("tag")) {
					if (parser.getAttributeValue(null, "k").equalsIgnoreCase(
							"highway")) {
						highway = true;
						streetType = getStreetType(parser.getAttributeValue(
								null, "v"));
					}
					if (parser.getAttributeValue(null, "k").equalsIgnoreCase(
							"oneway")) {
						oneway = (parser.getAttributeValue(null, "v")
								.equalsIgnoreCase("yes"));
					}
					// Strassen, die nciht fuer Autios erlaubt sind (E-Brunnen
					// z.B.)
					if (parser.getAttributeValue(null, "k").equalsIgnoreCase(
							"motorcar")) {
						allowcar = (parser.getAttributeValue(null, "v")
								.equalsIgnoreCase("yes"));
					}
					// Strassen mit beschraenkungen
					if (parser.getAttributeValue(null, "k").equalsIgnoreCase(
							"access")) {
						// gefundene Eigenschaften: yes, no, private,
						// permissive, destination
						allowaccess = (parser.getAttributeValue(null, "v")
								.equalsIgnoreCase("yes"));
					}

					if (parser.getAttributeValue(null, "k").equalsIgnoreCase(
							"name")) {
						name = parser.getAttributeValue(null, "v");
					}
				}
				break;

			case XMLStreamConstants.END_ELEMENT:
				// Way in Graphenbib
				if (wayFlag && parser.getLocalName().equalsIgnoreCase("way")) {
					wayFlag = false;
					if (!highway || streetType == StreetType.UNKNOWN
							|| !allowcar || !allowaccess) { // Nicht importieren
															// falls nicht
															// relevant
						oneway = false;
						allowcar = true;
						allowaccess = true;
						streetType = StreetType.UNKNOWN;
						aList.clear();
						name = "";
						break;
					}
					Iterator<ImportEdge> itr = aList.iterator();
					while (itr.hasNext()) {
						ImportEdge edge = itr.next();
						nrWays++;
						ImportEdge iedge = new ImportEdge(edge.getWayID(),
								edge.getStartNode(), edge.getEndNode(),
								Config.initialTemporaryLengthForEdges,
								oneway, name, streetType);
						ways.insertEdge(iedge);
						if (oneway)
							nrWays++;
					}
					oneway = false;
					highway = false;
					streetType = StreetType.UNKNOWN;
					aList.clear();
					name = "";
					allowcar = true;
					allowaccess = true;
				}
			}

		}
		timeNeeded -= System.currentTimeMillis();
		Logger.getInstance().log(
				"WayReader",
				"Done! Anzahl importierter Ways: " + nrWays
						+ "	  Benoetigte Gesamtzeit: " + timeNeeded / 1000
						+ " sec.");

		return ways;
	}

	private StreetType getStreetType(String v) {
		// MOTORWAY,TRUNK,PRIMARY,SECONDARY,TERTIARY,RESIDENTIAL,LIVING_STREET,ROAD
		if (v.equalsIgnoreCase("motorway"))
			return StreetType.MOTORWAY;
		if (v.equalsIgnoreCase("motorway_link"))
			return StreetType.MOTORWAY;
		if (v.equalsIgnoreCase("trunk"))
			return StreetType.TRUNK;
		if (v.equalsIgnoreCase("trunk_link"))
			return StreetType.TRUNK;
		if (v.equalsIgnoreCase("primary"))
			return StreetType.PRIMARY;
		if (v.equalsIgnoreCase("primary_link"))
			return StreetType.PRIMARY;
		if (v.equalsIgnoreCase("secondary"))
			return StreetType.SECONDARY;
		if (v.equalsIgnoreCase("secondary_link"))
			return StreetType.SECONDARY;
		if (v.equalsIgnoreCase("tertiary"))
			return StreetType.TERTIARY;
		if (v.equalsIgnoreCase("tertiary_link"))
			return StreetType.TERTIARY;
		if (v.equalsIgnoreCase("residential"))
			return StreetType.RESIDENTIAL;
		if (v.equalsIgnoreCase("living_street"))
			return StreetType.LIVING_STREET;
		if (v.equalsIgnoreCase("road") || v.equalsIgnoreCase("unclassified"))
			return StreetType.ROAD;
		return StreetType.UNKNOWN;

	}
}
