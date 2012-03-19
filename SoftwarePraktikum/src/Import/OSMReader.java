package Import;

import graphenbib.GPSCoordinate;
import graphenbib.MapGraph;
import graphenbib.Node;
import graphenbib.StreetType;
import graphexceptions.EmptyInputException;
import graphexceptions.InvalidGPSCoordinateException;
import graphexceptions.InvalidInputException;
import graphexceptions.NodeNotInGraphException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.stream.StreamSource;

import main.Config;
import main.Logger;

/**
 * Diese Klasse verarbeitet OSM Dateien und fgt ihre Bestandteile in einen
 * MapGraph ein.
 * 
 * 
 */
class OSMReader {

	private boolean folderflag = false;
	private WayFile ways;
	private HashMap<Integer, GPSCoordinate> nodes = new HashMap<Integer, GPSCoordinate>();

	OSMReader() {
	}

	OSMReader(WayFile ways, boolean folderflag) {
		this.ways = ways;
		this.folderflag = folderflag;
	}

	void setFolderflag(boolean folderflag) {
		this.folderflag = folderflag;
	}

	void setWayFile(WayFile ways) {
		this.ways = ways;
		this.folderflag = true;
	}

	public HashMap<Integer, GPSCoordinate> getNodes() {
		return nodes;
	}

	/**
	 * Liest eine OSM Datei ein und baut daraus MapGraph Objekte.
	 * 
	 * @param source
	 *            StreamSource der OSM Datei.
	 * @return MapGraph zur OSM Datei.
	 * @throws IOException
	 *             Fall Fehler beim lesen/schreiben auftreten
	 * @throws XMLStreamException
	 *             Falls Fehler beim streamen/parsen auftreten
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
	MapGraph readOSM(StreamSource source) throws IOException,
			XMLStreamException, EmptyInputException, InvalidInputException,
			InvalidGPSCoordinateException, NodeNotInGraphException {
		float minlat = 0, minlon = 0, maxlat = 0.1f, maxlon = 0.1f;
		long timeStarted = System.currentTimeMillis();
		int event;
		MapGraph graph = null;

		XMLInputFactory factory = XMLInputFactory.newInstance();
		XMLStreamReader parser = factory.createXMLStreamReader(source);

		int ID = 0, ref = 0, refSafe = 0, index = 0;
		int nrNodes = 0, nrWays = 0;
		float lat = 0, lon = 0;
		boolean wayFlag = false, highway = false, oneway = false, allowcar = true, allowaccess = true, firstFlag = true;
		String name = "";

		ArrayList<ImportEdge> aList = new ArrayList<ImportEdge>();

		StreetType streetType = StreetType.UNKNOWN;

		// Bounds bestimmen falls keine Bounds existieren.
		while (true) {
			event = parser.next();
			if (event == XMLStreamConstants.END_DOCUMENT) {
				parser.close();
				graph = new MapGraph(new GPSCoordinate(maxlat, minlon),
						new GPSCoordinate(minlat, maxlon));
				parser = factory.createXMLStreamReader(source);
				break;
			}
			if (event == XMLStreamConstants.START_ELEMENT) {
				if (folderflag) {
					if (parser.getLocalName().equalsIgnoreCase("bounds")) {
						minlat = Float.parseFloat(parser.getAttributeValue(
								null, "minlat"));
						maxlat = Float.parseFloat(parser.getAttributeValue(
								null, "maxlat"));
						minlon = Float.parseFloat(parser.getAttributeValue(
								null, "minlon"));
						maxlon = Float.parseFloat(parser.getAttributeValue(
								null, "maxlon"));
						graph = new MapGraph(new GPSCoordinate(maxlat, minlon),
								new GPSCoordinate(minlat, maxlon));
						break;
					}
				} else {
					if (parser.getLocalName().equalsIgnoreCase("node")) {
						lat = Float.parseFloat(parser.getAttributeValue(null,
								"lat"));
						lon = Float.parseFloat(parser.getAttributeValue(null,
								"lon"));
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
		}

		while (true) {
			event = parser.next();
			if (event == XMLStreamConstants.END_DOCUMENT) {
				parser.close();
				break;
			}

			if (event == XMLStreamConstants.START_ELEMENT) {
				// Node
				if (parser.getLocalName().equalsIgnoreCase("node")) {
					ID = Integer.parseInt(parser.getAttributeValue(null, "id"));
					lat = Float.parseFloat(parser
							.getAttributeValue(null, "lat"));
					lon = Float.parseFloat(parser
							.getAttributeValue(null, "lon"));
					nrNodes++;
					graph.insertNode(ID, new GPSCoordinate(lat, lon));
					nodes.put(ID, new GPSCoordinate(lat, lon));
				}

				if (parser.getLocalName().equalsIgnoreCase("way")) {
					break;
				}
			}
		}
		if (folderflag) {
			Iterator<ImportEdge> edgeit = ways.getEdgeIt();
			Node node;
			int length;
			while (edgeit.hasNext()) {
				ImportEdge edge = edgeit.next();
				node = graph.getNode(edge.getStartNode());
				if (node != null && node.getGPS() != null) {
					node = graph.getNode(edge.getEndNode());
					if (node != null) {
						if (node.getGPS() != null) {
							length = graph.getNode(edge.getStartNode()).getGPS().distanceTo(graph.getNode(edge.getEndNode()).getGPS());
							if (edge.isOneway()) {
								graph.insertEdge(edge.getStartNode(),edge.getEndNode(), edge.getWayID(),length, edge.getStreetType(),edge.getName());
								nrWays++;
							} else {
								graph.insertEdgeBothDirections(edge.getStartNode(), edge.getEndNode(),edge.getWayID(), length,edge.getStreetType(), edge.getName());
								nrWays += 2;
							}

						} else {
							if (edge.isOneway()) {
								nrWays++;
								graph.insertEdge(edge.getStartNode(),edge.getEndNode(), edge.getWayID(),Config.initialTemporaryLengthForEdges,edge.getStreetType(), edge.getName());
							} else {
								nrWays++; nrWays++;
								graph.insertEdgeBothDirections(edge.getStartNode(),edge.getEndNode(), edge.getWayID(),Config.initialTemporaryLengthForEdges,edge.getStreetType(), edge.getName());
							}
						}
					} else {
						nrNodes++;
						graph.insertNodeWithoutGPS(edge.getEndNode());
						nrWays++;
						if (edge.isOneway()) {
							graph.insertEdge(edge.getStartNode(),edge.getEndNode(), edge.getWayID(),Config.initialTemporaryLengthForEdges,edge.getStreetType(), edge.getName());
							
						} else {
							nrWays++;
							graph.insertEdgeBothDirections(edge.getStartNode(),edge.getEndNode(), edge.getWayID(),Config.initialTemporaryLengthForEdges,edge.getStreetType(), edge.getName());
						}
					}
				}
			}
		} else {
			while (true) {
				if (event == XMLStreamConstants.END_DOCUMENT) {
					parser.close();
					break;
				}

				switch (event) {
				case XMLStreamConstants.START_ELEMENT:

					// Way
					if (parser.getLocalName().equalsIgnoreCase("way")) {
						ID = Integer.parseInt(parser.getAttributeValue(null,
								"id"));
						wayFlag = true;
						index = 0;
					}

					if (wayFlag && parser.getLocalName().equalsIgnoreCase("nd")) {
						refSafe = ref;
						ref = Integer.parseInt(parser.getAttributeValue(null,
								"ref"));

						if (index > 0) {
							aList.add(new ImportEdge(ID, refSafe, ref, 0, null));
						}
						index++;
					}
					if (wayFlag
							&& parser.getLocalName().equalsIgnoreCase("tag")) {
						if (parser.getAttributeValue(null, "k")
								.equalsIgnoreCase("highway")) {
							highway = true;
							streetType = getStreetType(parser
									.getAttributeValue(null, "v"));
						}
						if (parser.getAttributeValue(null, "k")
								.equalsIgnoreCase("oneway")) {
							oneway = (parser.getAttributeValue(null, "v")
									.equalsIgnoreCase("yes"));
						}
						// Strassen, die nciht fuer Autios erlaubt sind
						// (E-Brunnen z.B.)
						if (parser.getAttributeValue(null, "k")
								.equalsIgnoreCase("motorcar")) {
							allowcar = (parser.getAttributeValue(null, "v")
									.equalsIgnoreCase("yes"));
						}
						// Strassen mit beschraenkungen
						if (parser.getAttributeValue(null, "k")
								.equalsIgnoreCase("access")) {
							// gefundene Eigenschaften: yes, no, private,
							// permissive, destination
							allowaccess = (parser.getAttributeValue(null, "v")
									.equalsIgnoreCase("yes"));
						}

						if (parser.getAttributeValue(null, "k")
								.equalsIgnoreCase("name")) {
							name = parser.getAttributeValue(null, "v");
						}
					}
					break;

				case XMLStreamConstants.END_ELEMENT:
					// Way in Graphenbib
					if (wayFlag
							&& parser.getLocalName().equalsIgnoreCase("way")) {
						wayFlag = false;
						if (!highway || streetType == StreetType.UNKNOWN
								|| !allowcar || !allowaccess) { // Nicht
																// importieren
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
						Node node;
						ImportEdge edge;
						int length;
						while (itr.hasNext()) {
							edge = itr.next();
							node = graph.getNode(edge.getStartNode());
							if (node != null && node.getGPS() != null) {
								node = graph.getNode(edge.getEndNode());
								if (node != null) {
									if (node.getGPS() != null) {
										length = graph.getNode(edge.getStartNode()).getGPS().distanceTo(graph.getNode(edge.getEndNode()).getGPS());
										// graph.insertEdge(edge.getStartNode(),
										// edge.getEndNode(),
										// edge.getWayID(),length, streetType);
										if (oneway) {
											graph.insertOneWay(edge.getStartNode(),edge.getEndNode(),edge.getWayID(), length,streetType, name);
											//graph.insertEdgeBothDirections(edge.getStartNode(),edge.getEndNode(),edge.getWayID(), length,streetType, name);
											nrWays++;
										} else {
											// graph.insertEdge(edge.getEndNode(),edge.getStartNode()
											// , edge.getWayID(),length,
											// streetType);
											graph.insertEdgeBothDirections(edge.getStartNode(),edge.getEndNode(),edge.getWayID(), length,streetType, name);
											nrWays += 2;
										}
									} else {
										if (oneway) {
											nrWays++; 
											graph.insertOneWay(edge.getStartNode(),edge.getEndNode(),edge.getWayID(),	Config.initialTemporaryLengthForEdges,streetType, name);
										} else {
											nrWays++; nrWays++;
											graph.insertEdgeBothDirections(edge.getStartNode(),edge.getEndNode(),edge.getWayID(),	Config.initialTemporaryLengthForEdges,streetType, name);
										}
									}
								} else {
									nrNodes++;
									graph.insertNodeWithoutGPS(edge.getEndNode());
									
									if (oneway) {
										nrWays++;
										graph.insertOneWay(edge.getStartNode(),edge.getEndNode(),edge.getWayID(),Config.initialTemporaryLengthForEdges,streetType, name);
									} else {
										nrWays++; nrWays++;
										graph.insertEdgeBothDirections(edge.getStartNode(),edge.getEndNode(),edge.getWayID(),Config.initialTemporaryLengthForEdges,streetType, name);
									}

								}
							}
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

				event = parser.next();
			}
		}
		graph.deleteIsolatedNodes();
		long timeNeeded = System.currentTimeMillis() - timeStarted;
		Logger.getInstance().log(
				"OSMImporter",
				"Done! Anzahl importierter Daten: 	Knoten: " + nrNodes
						+ "	        Knoten nach loeschen von isolated Nodes: "
						+ graph.getSize() + "        	Wege: " + nrWays
						+ "	  Benoetigte Gesamtzeit: " + timeNeeded / 1000
						+ " sec.");

		return graph;
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
