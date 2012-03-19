package Import;

import graphenbib.GPS;
import graphenbib.GPSCoordinate;
import graphenbib.GPSRectangle;
import graphenbib.HierarchyMapGraph;
import graphenbib.MapGraph;
import graphenbib.MapNode;
import graphexceptions.EmptyInputException;
import graphexceptions.EmptyMapGraph;
import graphexceptions.InvalidGPSCoordinateException;
import graphexceptions.InvalidInputException;
import graphexceptions.NodeNotInGraphException;

import java.io.File;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import javax.xml.stream.XMLStreamException;
import javax.xml.transform.stream.StreamSource;

import main.Config;
import main.Logger;

/**
 * Diese Klasse kann in der Main instanziert werden, um eine *.osm Datei
 * einzulesen. Die eigelesene Datei wird in einer ArrayList als Tile
 * gespeichert.
 * 
 */
public class OSMImporter {
	private String mappath, workfolder, configpath;
	private boolean folderflag, nomapflag = false;
	private ArrayList<Tile> tiles = new ArrayList<Tile>();
	private OSMReader osmreader = new OSMReader();

	/**
	 * Initialisiert einen OSMImporter je nach dem ob eine .osm Datei oder eine
	 * ProcessedTilesConfig.tiles geoeffnet wurde. Je nach dem was zutrifft,
	 * werden schon fertig preprocessed Tiles geladen oder die map durchlaeuft
	 * das PreProcessing.
	 * 
	 * @param map
	 *            .osm oder .tiles Datei.
	 * @throws InvalidInputException
	 *             Falls keine gueltige Datei ausgewaehlt wird.
	 */
	public OSMImporter(File map) throws InvalidInputException {

		if (map.getName().endsWith(".osm")) {
			this.mappath = map.getPath();
			this.workfolder = map.getPath() + ".mapfiles" + File.separatorChar;
			new File(workfolder).mkdir();
			this.configpath = this.workfolder + "ProcessedTilesConfig.tiles";
			this.workfolder += "tiles" + File.separatorChar;
			new File(workfolder).mkdir();
			try {
				preProcess();
			} catch (Exception e) {
				Logger.getInstance()
						.log("OSMImporter", e.getLocalizedMessage());
			}

		} else if (map.getName().endsWith(".tiles")) {
			this.workfolder = map.getParent() + File.separatorChar + "tiles"
					+ File.separatorChar;
			this.configpath = map.getPath();
			this.nomapflag = true;
			try {
				preProcess();
			} catch (Exception e) {
				Logger.getInstance()
						.log("OSMImporter", e.getLocalizedMessage());
			}
		} else {
			throw new InvalidInputException(
					"Bitte .osm oder .tiles Datei auswaehlen!");
		}
	}

	/**
	 * Je nach dem ob eine .tiles oder .osm Datei ausgewaehlt wurde werden tiles
	 * geladen oder das PreProcessing durchegfuehrt. Liest die beim instanzieren
	 * uebergebene .osm Datei ein. Falls notwendig wird die Datei in Tiles
	 * unterteilt um besser verarbeitet werden zu koennen. Dabei wird eine
	 * ArrayList<Tile> angelegt in der die Dateinamen und Bounds der
	 * Tiles(MapGraph) gespeichert werden. Diese wird fuer erneutes Einladen der
	 * tiles in der ProcessedTilesConfig.tiles gespeichert.
	 * 
	 * 
	 * @throws IOException
	 *             Wenn ein unerwarteter I/O Fehler auftritt
	 * @throws XMLStreamException
	 *             Wenn ein Fehler bei Streamen der Datei auftritt.
	 * @throws InvalidGPSCoordinateException
	 * @throws InvalidInputException
	 * @throws EmptyInputException
	 * @throws NodeNotInGraphException
	 * 
	 */

	private synchronized void preProcess() throws IOException,
			XMLStreamException, EmptyInputException, InvalidInputException,
			InvalidGPSCoordinateException, NodeNotInGraphException {
		if (nomapflag) {
			loadTiles();
			return;
		}
		File map = new File(mappath);
		if (map.length() < Config.maxSingleFileSize) {
			folderflag = false;
			osmreader.setFolderflag(folderflag);
			MapGraph graph = osmreader.readOSM(new StreamSource(map));
			String filename = "tile.graph";
			graph.setFilename(filename);

			tiles.add(new Tile(filename, graph.getUpperLeft(), graph
					.getLowerRight(), graph.getSize()));
			LoadSaveObject.saveObject(new File(this.workfolder + filename + 1),
					ZoomLevelExtractor.extractZoomLevel(graph, 1));
			LoadSaveObject.saveObject(new File(this.workfolder + filename + 2),
					ZoomLevelExtractor.extractZoomLevel(graph, 2));
			graph.setFilename(filename + 0);
			LoadSaveObject.saveObject(new File(this.workfolder + filename + 0),
					graph);

			graph = null;
			LoadSaveObject.saveObject(new File(configpath), tiles);
			HierarchyMapGraph hgraph = exportToHierarchyMapGraph();
			saveHGraph(hgraph);
			return;
		}

		folderflag = true;

		FileTiler tiler = new FileTiler();
		tiler.TileFile(this.mappath, this.workfolder);
		WayReader wayreader = new WayReader();
		WayFile ways = wayreader.readWays(new StreamSource(this.workfolder
				+ "wayfile.osm"));
		osmreader.setWayFile(ways);

		File file = new File(this.workfolder);
		File[] filearray = file.listFiles();

		MapGraph graph;
		String[] name;
		String filename;
		Iterator<MapNode> it;
		Tile tile;
		for (File i : filearray) {
			if (i.getName().endsWith(".tile")) {
				name = i.getName().split("t");
				filename = name[0].replaceFirst("f", "t") + "graph";
				// Logger.getInstance().log("OSMImporter",filename+0);
				graph = osmreader.readOSM(new StreamSource(i.getPath()));

				tile = new Tile(filename, graph.getUpperLeft(),
						graph.getLowerRight(), graph.getSize());
				it = graph.getNodeIt();
				MapNode node;
				while (it.hasNext()) {
					node = it.next();
					tile.addNode(node.getUID());
				}
				tiles.add(tile);
				graph.setFilename(filename);

				LoadSaveObject.saveObject(new File(this.workfolder + filename
						+ 0), graph);
				Logger.getInstance().log("OSM Importer",
						"Tile: " + tile.getName() + " Verarbeitet.");
				graph = null;
			}
		}
		reconstructNodesWithoutGPSAndExtractAllZoomLevels(osmreader.getNodes());
		LoadSaveObject.saveObject(new File(configpath), tiles);
		HierarchyMapGraph hgraph = exportToHierarchyMapGraph();
		saveHGraph(hgraph);

	}

	/**
	 * Laedt Tile Informationen aus der ProcessedTilesConfig.tiles ein.
	 */

	@SuppressWarnings("unchecked")
	private void loadTiles() {
		this.tiles = (ArrayList<Tile>) LoadSaveObject.loadObject(new File(
				configpath));
	}

	@Deprecated
	/**
	 * Laedt bereits fertig importierte .graph Dateien zur benutzung wieder in erinnerung.
	 * Dazu bitte OSM Importer mit dem Verzeichnis in dem die Dateien liegen initialisieren.
	 */
	public void loadPreProcessedTiles() {

		File file = new File(workfolder);
		File[] filearray = file.listFiles();
		MapGraph graph;
		tiles.clear();

		for (File i : filearray) {
			String filename = i.getName();
			if (filename.endsWith(".graph0")) {

				graph = LoadSaveObject.loadMGraph(i);

				String[] filenames = filename.split("g");
				// logger.log("OSMImp", filenames[0]);
				filename = filenames[0] + "graph";

				Tile tile = new Tile(filename, graph.getUpperLeft(),
						graph.getLowerRight(), graph.getSize());
				Iterator<MapNode> it = graph.getNodeIt();
				MapNode node;
				while (it.hasNext()) {
					node = it.next();
					tile.addNode(node.getUID());
				}
				tiles.add(tile);
				graph = null;
			}
		}
		LoadSaveObject.saveObject(new File(configpath), tiles);
	}

	/**
	 * Versucht fehlende GPS Koordinaten nachtraeglich einzufuegen.
	 * 
	 * @param hgraph
	 * @throws InvalidInputException
	 * @throws EmptyInputException
	 * @throws NodeNotInGraphException
	 */
	private void reconstructNodesWithoutGPSAndExtractAllZoomLevels(
			HashMap<Integer, GPSCoordinate> Nodes)
			throws InvalidInputException, EmptyInputException,
			NodeNotInGraphException {
		Iterator<Tile> tiles = this.tiles.iterator();

		Logger.getInstance()
				.log("OSMImporter.reconstructGPS",
						"Beginne Rekonstruktion der GPS Koordinaten von Knoten an Tilegrenzen und Extrahiere Zoomlevel.");
		while (tiles.hasNext()) {
			Tile tile = tiles.next();

			MapGraph mg = LoadSaveObject.loadMGraph(new File(this.workfolder
					+ tile.getName() + 0));

			Iterator<MapNode> nodes = mg.getNodeIt();
			MapNode node;
			while (nodes.hasNext()) {
				node = nodes.next();
				if (!node.hasGPS()) {
					node.setGPS(Nodes.get(node.getUID()));
				}
			}
			mg.correctLength();
			LoadSaveObject.saveObject(
					new File(this.workfolder + mg.getFilename() + 1),
					ZoomLevelExtractor.extractZoomLevel(mg, 1));
			LoadSaveObject.saveObject(
					new File(this.workfolder + mg.getFilename() + 2),
					ZoomLevelExtractor.extractZoomLevel(mg, 2));
			mg.setFilename(mg.getFilename() + 0);
			LoadSaveObject.saveObject(
					new File(this.workfolder + mg.getFilename()), mg);

		}
		Logger.getInstance().log("OSMImporter.reconstructGPS",
				"Rekonstruktion und Extraktion beendet.");
	}

	/**
	 * Ermittelt den Mittelpunkt der gesamten Karte.
	 * 
	 * @return Mittelpunkt als GPS Coordinate.
	 * @throws InvalidGPSCoordinateException
	 * @throws EmptyInputException
	 * @throws InvalidInputException
	 */
	public GPSCoordinate getMapCenter() throws InvalidGPSCoordinateException,
			EmptyInputException, InvalidInputException {
		GPSRectangle gpsr = getMapBounds();
		float lat, lon;

		lat = gpsr.getLowerRight().getLatitude()
				+ ((gpsr.getUpperLeft().getLatitude() - gpsr.getLowerRight()
						.getLatitude()) / 2);
		lon = gpsr.getUpperLeft().getLongitude()
				+ ((gpsr.getLowerRight().getLongitude() - gpsr.getUpperLeft()
						.getLongitude()) / 2);

		return (new GPSCoordinate(lat, lon));
	}

	/**
	 * Ermittelt den Punkt der Karte an dem die hoechste Dichte sein sollte. Der
	 * gefundene Punkt liegt auf jedenfall auf einer Strasse. Es kann in
	 * unguenstigen Faellen vorkommen, dass trotzdem eine Strasse in der Pampa
	 * gewaehlt wird.
	 * 
	 * @return GPSCoordinate auf einer Strasse
	 * @throws InvalidGPSCoordinateException
	 * @throws EmptyMapGraph
	 */
	public GPSCoordinate getPOI() throws InvalidGPSCoordinateException,
			EmptyMapGraph {
		Iterator<Tile> it = tiles.iterator();
		Tile acttile = it.next();
		Tile tile = acttile;

		while (it.hasNext()) {
			acttile = it.next();
			if (acttile.getSize() > tile.getSize())
				tile = acttile;
		}
		MapGraph graph = LoadSaveObject.loadMGraph(new File(workfolder
				+ tile.getName() + 0));

		Iterator<MapNode> git = graph.getNodeIt();
		int size = 0;
		float lon = 0, lat = 0;
		MapNode node;
		while (git.hasNext()) {
			node = git.next();
			if (node.hasGPS()) {
				lat += node.getGPS().getLatitude();
				lon += node.getGPS().getLongitude();
				size++;
			}
		}

		return (graph.getClosestNode(new GPSCoordinate(lat / size, lon / size))
				.getGPS());
	}

	/**
	 * Ermittelt die Grenzen der gesamten Karte.
	 * 
	 * @return Grenzen der Karte als GPS Rectangle
	 * @throws EmptyInputException
	 * @throws InvalidInputException
	 * @throws InvalidGPSCoordinateException
	 */
	public GPSRectangle getMapBounds() throws EmptyInputException,
			InvalidInputException, InvalidGPSCoordinateException {
		if (tiles.size() == 1) {
			return (new GPSRectangle(tiles.get(0).getUpperleft(), tiles.get(0)
					.getLowerright()));
		}

		Iterator<Tile> it = tiles.iterator();
		float ulat = -90, ulon = 180, llat = 90, llon = -180;
		while (it.hasNext()) {
			Tile tile = it.next();
			if (tile.getUpperleft().getLatitude() > ulat)
				ulat = tile.getUpperleft().getLatitude();
			if (tile.getUpperleft().getLongitude() < ulon)
				ulon = tile.getUpperleft().getLongitude();

			if (tile.getLowerright().getLatitude() < llat)
				llat = tile.getLowerright().getLatitude();
			if (tile.getLowerright().getLongitude() > llon)
				llon = tile.getLowerright().getLongitude();
		}
		return (new GPSRectangle(ulat, llon, llat, ulon));
	}

	/**
	 * Gibt Tiles die den angegebenen Teil der Karte abdecken zurueck. Dabei
	 * wreden aus der ArrayList die alten tiles geloescht und die neuen
	 * hinzugefuegt.
	 * 
	 * @param upperleft
	 *            Linke obere Ecke des Anzuzeigenden Bereichs.
	 * @param loweright
	 *            Rechte untere Ecke des Anzuzeigenden Bereichs.
	 * @param ZoomLevel
	 *            Nach Streettype sortierter Zoomgrad 0= Alles, 1=
	 *            Bundesstrassen,Landstrassen, 2= Autobahnen.
	 * @throws InvalidObjectException
	 *             Wenn das Zoomlevel nicht korrekt ist wird ein Fehler
	 *             geworfen.
	 * @throws InvalidInputException
	 */
	public void getTiles(GPS upperleft, GPS loweright, int ZoomLevel,
			ArrayList<MapGraph> graphs) throws InvalidInputException {
		if (ZoomLevel < 0 || ZoomLevel > 2)
			throw new InvalidInputException(
					"Das Zoomlevel liegt ausserhalb des moeglichen bereichs!");
		ArrayList<String> gnames = new ArrayList<String>();
		// ArrayList<MapGraph> graphs = (ArrayList<MapGraph>) oldgraphs.clone();
		if (graphs == null)
			graphs = new ArrayList<MapGraph>();

		// Benoetigte Tiles suchen
		Iterator<Tile> it = this.tiles.iterator();
		while (it.hasNext()) {
			Tile tile = it.next();
			if (tile.getLowerright().getLatitude() <= upperleft.getLatitude()) {
				if (tile.getLowerright().getLongitude() >= upperleft
						.getLongitude()) {
					if (tile.getUpperleft().getLatitude() >= loweright
							.getLatitude()) {
						if (tile.getUpperleft().getLongitude() <= loweright
								.getLongitude()) {
							gnames.add(tile.getName() + ZoomLevel);
						}
					}
				}
			}
		}

		// Die alten tiles loeschen
		Iterator<MapGraph> mgs = graphs.iterator();
		while (mgs.hasNext()) {
			MapGraph mg = mgs.next();
			if (!gnames.contains(mg.getFilename()))
				mgs.remove();
		}

		// Die neuen Tiles einfuegen
		boolean notinflag = true;
		Iterator<String> names = gnames.iterator();
		while (names.hasNext()) {
			String name = names.next();
			mgs = graphs.iterator();
			while (mgs.hasNext()) {
				MapGraph mg = mgs.next();
				if (name.equalsIgnoreCase(mg.getFilename()))
					notinflag = false;
			}
			if (notinflag)
				graphs.add(LoadSaveObject
						.loadMGraph(new File(workfolder + name)));
			notinflag = true;
		}
	}

	/**
	 * Sucht das Tile in dem der angegebene Knoten liegt
	 * 
	 * @param node
	 *            GPS Koordinaten des Knotens.
	 * @return MapGraph
	 * @throws InvalidInputException
	 *             Wird geworfen wenn der Knoten nicht auf der Karte liegt.
	 * @param ZoomLevel
	 *            Nach Streettype sortierter Zoomgrad 0= Alles, 1=
	 *            Bundesstrassen,Landstrassen, 2= Autobahnen.
	 */

	public MapGraph getTile(GPS node, int ZoomLevel)
			throws InvalidInputException {
		if (ZoomLevel < 0 || ZoomLevel > 2)
			throw new InvalidInputException(
					"Das Zoomlevel liegt ausserhalb des moeglichen bereichs!");

		Iterator<Tile> it = this.tiles.iterator();
		while (it.hasNext()) {
			Tile tile = it.next();
			if (tile.getSize() == 0) continue;
			if (tile.getUpperleft().getLatitude() >= node.getLatitude()
					&& tile.getLowerright().getLatitude() <= node.getLatitude()
					&& tile.getUpperleft().getLongitude() <= node
							.getLongitude()
					&& tile.getLowerright().getLongitude() >= node
							.getLongitude()) {
				// Logger.getInstance().log("OSMImporter.getTile",tile.getName()+ZoomLevel);
				return (MapGraph) LoadSaveObject.loadObject(new File(workfolder
						+ tile.getName() + ZoomLevel));
			}
		}
		throw new InvalidInputException(
				"Der angegebene Knoten liegt nicht innerhalb der Karte!");
	}

	/**
	 * Sucht das Tile in dem der angegebene Knoten liegt.
	 * 
	 * @param node
	 *            ID des Knotens
	 * @return MapGraph
	 * @throws InvalidInputException
	 *             Wird geworfen wenn der Knoten nicht auf der Karte liegt.
	 * @param ZoomLevel
	 *            Nach Streettype sortierter Zoomgrad 0= Alles, 1=
	 *            Bundesstrassen,Landstrassen, 2= Autobahnen.
	 */
	public String getTileName(int node, String tilename, int ZoomLevel)
			throws InvalidInputException {
		if (ZoomLevel < 0 || ZoomLevel > 2)
			throw new InvalidInputException(
					"Das Zoomlevel liegt ausserhalb des moeglichen bereichs!");
		Iterator<Tile> it = this.tiles.iterator();
		while (it.hasNext()) {
			Tile tile = it.next();
			if (!tilename.equalsIgnoreCase((tile.getName() + ZoomLevel))
					&& tile.nodeExists(node)) {
				// Logger.getInstance().log("OSMImporter.getTile",tile.getName()+ZoomLevel);
				return tile.getName() + ZoomLevel;
			}
		}
		return null;
	}

	/**
	 * Sucht das Tile in dem der angegebene Knoten liegt.
	 * 
	 * @param node
	 *            ID des Knotens
	 * @return MapGraph
	 * @throws InvalidInputException
	 *             Wird geworfen wenn der Knoten nicht auf der Karte liegt.
	 * @param ZoomLevel
	 *            Nach Streettype sortierter Zoomgrad 0= Alles, 1=
	 *            Bundesstrassen,Landstrassen, 2= Autobahnen.
	 */
	public MapGraph getTile(int node, int ZoomLevel)
			throws InvalidInputException {
		if (ZoomLevel < 0 || ZoomLevel > 2)
			throw new InvalidInputException(
					"Das Zoomlevel liegt ausserhalb des moeglichen bereichs!");
		Iterator<Tile> it = this.tiles.iterator();
		while (it.hasNext()) {
			Tile tile = it.next();
			if (tile.nodeExists(node)) {
				// Logger.getInstance().log("OSMImporter.getTile",tile.getName()+ZoomLevel);
				return LoadSaveObject.loadMGraph(new File(workfolder
						+ tile.getName() + ZoomLevel));
			}
		}
		throw new InvalidInputException(
				"Der angegebene Knoten liegt nicht innerhalb der Karte!");
	}

	/**
	 * Gibt eine ArrayList<MapGraph> mit allen Tiles der Zoomstufe zurueck .
	 * 
	 * @return ArrayList<MapGraph>
	 * @throws InvalidInputException
	 * @param ZoomLevel
	 *            Nach Streettype sortierter Zoomgrad 0= Alles, 1=
	 *            Bundesstrassen,Landstrassen, 2= Autobahnen.
	 */
	public ArrayList<MapGraph> getAllTiles(int ZoomLevel)
			throws InvalidInputException {
		if (ZoomLevel < 0 || ZoomLevel > 2)
			throw new InvalidInputException(
					"Das Zoomlevel liegt ausserhalb des moeglichen bereichs!");

		ArrayList<MapGraph> graphs = new ArrayList<MapGraph>();

		Iterator<Tile> it = this.tiles.iterator();
		while (it.hasNext()) {
			Tile tile = it.next();
			// Logger.getInstance().log("OSMImporter.getAllTiles",tile.getName()+ZoomLevel);
			graphs.add(LoadSaveObject.loadMGraph(new File(workfolder
					+ tile.getName() + ZoomLevel)));
		}
		return graphs;
	}

	/**
	 * 
	 * @param filename
	 *            Name des Tiles.
	 * @return MapGraph zum angeforderten Tile.
	 */
	public MapGraph getTile(String filename) {
		return LoadSaveObject.loadMGraph(new File(workfolder + filename));
	}

	/**
	 * Erstellt einen HierarchyMapGraphen aus allen Tiles die mit dem
	 * OSMImporter eingelesen wurden.
	 * 
	 * @return HierarchyMapGraph aller Tiles.
	 */
	public HierarchyMapGraph exportToHierarchyMapGraph() {
		HierarchyMapGraph hMapGraph = new HierarchyMapGraph();
		Iterator<Tile> it = this.tiles.iterator();
		while (it.hasNext()) {
			Tile tile = it.next();
			Logger.getInstance().log(
					"OSMImporter",
					"Exportiere zu HierarchyMapGraph Tile: " + tile.getName()
							+ 0);
			LoadSaveObject
					.loadMGraph(new File(workfolder + tile.getName() + 0))
					.exportToHierachyGraph(hMapGraph);
		}

		hMapGraph.correctLength();
		return hMapGraph;
	}

	@Deprecated
	/**
	 * Nicht mehr benutzen...ausser es ist explizit eine Datei einzulesen. Bitte PreProcess benutzen!
	 * Liest eine .osm Datei ein und gibt den zugehoerigen MapGraph zurueck.
	 * @return Der MapGraph zur einzulesenden .osm Datei.
	 * 
	 * @throws IOException
	 * @throws XMLStreamException
	 * @throws EmptyInputException
	 * @throws InvalidInputException
	 * @throws InvalidGPSCoordinateException
	 * @throws NodeNotInGraphException
	 */
	public MapGraph readFile() throws IOException, XMLStreamException,
			EmptyInputException, InvalidInputException,
			InvalidGPSCoordinateException, NodeNotInGraphException {
		folderflag = false;
		if (nomapflag)
			throw new EmptyInputException(
					"Ohne Map kann das readFile nicht durchgefuehrt werden, bitte OSMImporter mit .osm Datei initialisieren!");
		MapGraph mg = osmreader.readOSM(new StreamSource(mappath));
		return mg;

	}

	/**
	 * Speichert den HGraph unter workfolder/hGraph.hgraph
	 * 
	 * @param hgraph
	 *            Zu speichernder Graph
	 */
	public void saveHGraph(HierarchyMapGraph hgraph) {
		LoadSaveObject.saveObject(new File(workfolder + "hGraph.hgraph"),
				hgraph);
	}

	/**
	 * Laedt den hGraph aus workfolder/hGraph.hgraph
	 * 
	 * @return Gibt den geladenen Graphen zurueck.
	 */
	public HierarchyMapGraph loadHGraph() {
		return LoadSaveObject
				.loadHGraph(new File(workfolder + "hGraph.hgraph"));
	}

}
