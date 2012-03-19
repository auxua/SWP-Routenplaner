package main;

import graphenbib.HierarchyMapGraph;
import graphexceptions.EmptyInputException;
import graphexceptions.InvalidGPSCoordinateException;
import graphexceptions.InvalidInputException;
import graphexceptions.NodeNotInGraphException;

import java.io.File;
import java.io.IOException;

import javax.xml.stream.XMLStreamException;

import Import.OSMImporter;
import algorithmen.HHierarchyMT;

public class MainPreProc {
	/**
	 * Statische Methode um das Preprocessing zu starten. Es wird eine OSM Datei geoeffnet, in Tiles
	 * geteilt und in einen HierarchyMapGraph exportiert. Auf diesen werden dann die Hierarchien berechnet
	 * und anschliessend wird der HierarchyMapGraph ebenfalls gespeichert.
	 * @throws IOException
	 * @throws XMLStreamException
	 * @throws EmptyInputException
	 * @throws InvalidInputException
	 * @throws InvalidGPSCoordinateException
	 * @throws NodeNotInGraphException
	 */
	public static void main(File map) throws IOException, XMLStreamException, EmptyInputException, InvalidInputException, InvalidGPSCoordinateException, NodeNotInGraphException
	{
		OSMImporter mainImporter = new OSMImporter(map); //Rufe Preprocessing an uebergebenem OSMFile automatisch auf
		HierarchyMapGraph hGraph = mainImporter.loadHGraph(); //Wird bereits im Import angelegt da er dort benoetigt wird.
		//hGraph.correctLength(); //Falls noch ungueltige Kantenlaengen existieren, wird dies hier behoben --> wird da der HGraph jetzt bereits im Import benoetigt wird dort ausgefuehrt
		HHierarchyMT.buildHierarchyGraph(hGraph); //Baut die Hierarchien im HierarchieGraphen auf
		mainImporter.saveHGraph(hGraph); //Speichert den hGraph unter workfolder/hGraph.hgraph
	}
}
