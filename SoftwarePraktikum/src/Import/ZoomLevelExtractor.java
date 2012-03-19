package Import;

import graphenbib.MapEdge;
import graphenbib.MapGraph;
import graphenbib.MapNode;
import graphenbib.StreetType;
import graphexceptions.EmptyInputException;
import graphexceptions.InvalidInputException;
import graphexceptions.NodeNotInGraphException;

import java.util.Iterator;

import main.Logger;

class ZoomLevelExtractor {

	/**
	 * Extrahiert aus einem MapGraph einen Graph der nur bestimmte Streettypes
	 * enthaelt. Diese werden durch das Zoomlevel bestimmt.
	 * 
	 * @param mg
	 *            MapGraph aus dem zu extrahieren ist.
	 * @param ZoomLevel
	 *            Nach Streettype sortierter Zoomgrad 0= Alles, 1=
	 *            Bundesstrassen,Landstrassen, 2= Autobahnen.
	 * @return Extrahierter MapGraph
	 * @throws EmptyInputException
	 * @throws InvalidInputException
	 * @throws NodeNotInGraphException
	 */
	protected static MapGraph extractZoomLevel(MapGraph mg, int ZoomLevel)
			throws EmptyInputException, InvalidInputException,
			NodeNotInGraphException {
		if (ZoomLevel < 0 || ZoomLevel > 2)
			throw new InvalidInputException(
					"Das Zoomlevel liegt ausserhalb des moeglichen bereichs!");
		int nrways = 0;
		MapGraph zoomgraph = new MapGraph(mg.getRect());
		zoomgraph.setFilename(mg.getFilename() + ZoomLevel);

		Iterator<MapNode> it = mg.getNodeIt();
		while (it.hasNext()) {
			MapNode node = it.next();

			zoomgraph.insertNodeWithoutGPS(node.getUID());
			zoomgraph.getNode(node.getUID()).setGPS(node.getGPS());
		}

		it = mg.getNodeIt();
		while (it.hasNext()) {
			MapNode node = it.next();
			MapEdge[] mapedges = node.getOutgoingEdges();
			for (int i = 0; i < mapedges.length; i++) {
				if (ZoomLevel == 1) {
					if (mapedges[i].getType() == StreetType.MOTORWAY
							|| mapedges[i].getType() == StreetType.TRUNK
							|| mapedges[i].getType() == StreetType.PRIMARY
							|| mapedges[i].getType() == StreetType.SECONDARY) {
						zoomgraph.insertEdge(mapedges[i].getNodeStart()
								.getUID(), mapedges[i].getNodeEnd().getUID(),
								mapedges[i].getUID(), mapedges[i].getLength(),
								mapedges[i].getType(), mapedges[i].getName());
						nrways++;
					}
				}
				if (ZoomLevel == 2) {
					if (mapedges[i].getType() == StreetType.MOTORWAY
							|| mapedges[i].getType() == StreetType.TRUNK) {
						zoomgraph.insertEdge(mapedges[i].getNodeStart()
								.getUID(), mapedges[i].getNodeEnd().getUID(),
								mapedges[i].getUID(), mapedges[i].getLength(),
								mapedges[i].getType(), mapedges[i].getName());
						nrways++;
					}
				}
			}
		}

		zoomgraph.deleteIsolatedNodes(); // Knoten entfehrnen die uebrig bleiben.
		Logger.getInstance().log(
				"OSMImporter",
				"Zoomlevel " + ZoomLevel
						+ " extrahiert.      Anzahl verbleibender Knoten: "
						+ zoomgraph.getSize()
						+ "         Verbleibende Kanten: " + nrways);
		return zoomgraph;
	}
}
