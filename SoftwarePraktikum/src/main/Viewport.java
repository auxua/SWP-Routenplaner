package main;

import graphenbib.GPSCoordinate;
import graphenbib.GPSRectangle;
import graphenbib.HierarchyMapGraph;
import graphenbib.HierarchyMapNode;
import graphenbib.MapGraph;
import graphenbib.MapNode;
import graphenbib.Path;
import graphexceptions.EmptyInputException;
import graphexceptions.EmptyMapGraph;
import graphexceptions.InvalidGPSCoordinateException;
import graphexceptions.InvalidInputException;

import java.io.File;
import java.util.ArrayList;

import Import.OSMImporter;
import algorithmen.Query;

public class Viewport {
	private OSMImporter osmImp;
	private ArrayList<MapGraph> mapGraphTiles= new ArrayList<MapGraph>();
	private HierarchyMapGraph hGraph;
	private Path2Draw shortestPath = null;
	
	private int zoomLevel=0; //Zoomlevel. Genaue Definition in Main.Constants

	/**
	 * Mit diesem Konstruktor werden die Daten, die schon preprocessed sind, geoeffnet.
	 */
	public Viewport(File map) throws InvalidInputException
	{
		this.osmImp = new OSMImporter(map); 
		this.hGraph=this.osmImp.loadHGraph();
		try {
			this.shortestPath= new Path2Draw(new ArrayList<Integer>(), 
					new ArrayList<GPSCoordinate>(), 0, 0);
		} catch (InvalidInputException e) {
			//Diese Exception kann nicht auftreten, da beide ArrayLists gleich langs sind.
			e.printStackTrace();
		}
		this.zoomLevel=0;
	}
		
	/**
	 * Diese Methode wird von der UI aufgerufen, nachdem sich der Bildausschnitt durch Zoomen oder Scrollen
	 * geaendert hat. Es werden dann automatisch entsprechende Tiles nachgeladen, sodass mittels der
	 * getAllStreetIterator danach alle Strassen des neuen Bildausschnitts zurueckgegeben werden.
	 * @param lowerright Rechte untere Koordinate.
	 */
	public synchronized void update(GPSCoordinate upperleft, GPSCoordinate lowerright, ViewportCallback callback) {
		synchronized (this.mapGraphTiles) {
			try {
				osmImp.getTiles(upperleft, lowerright, this.zoomLevel, this.mapGraphTiles);
			} catch (InvalidInputException e) {
				// Diese Exception sollte nicht auftretten, da sicher gestellt wird, dass das Zoomlevel
				//im korrekten Intervall liegt.
				e.printStackTrace();
			}	
		}
		Logger.getInstance().log("Viewport.update", "Zahl geladener Tiles: "+this.mapGraphTiles.size());
		callback.updateComplete(new Street2DrawIt(this.mapGraphTiles), this.shortestPath.getPathIt(this));
	}
	
	
	/**
	 * Methode um den Gesamtausschnitt der geladenen Karte zu bekommen um zum Beispiel
	 * den initialen Kartenausschnitt zu bestimmten.
	 * @return Rechteck, in dem die Kartendaten liegen.
	 */
	public GPSRectangle getMapRectangle() {
		GPSRectangle gpr=null;
		try {
			gpr=this.osmImp.getMapBounds();
		} catch (EmptyInputException e) {
			// Die folgenden Fehler sollten nicht auftreten.
			e.printStackTrace();
		} catch (InvalidInputException e) {
			e.printStackTrace();
		} catch (InvalidGPSCoordinateException e) {
			e.printStackTrace();
		}
		return gpr;
	}
	
	/**
	 * Methode um die Mitte der gegebenen Kartendaten abzufragen.
	 * @return Mitte des Rechtecks, in dem die Kartendaten liegen.
	 */
	public GPSCoordinate getMapCenter() {
		GPSCoordinate centerGPS=null;
		try {
			centerGPS=this.osmImp.getMapCenter();
		} catch (InvalidGPSCoordinateException e) {
			// Die folgenden Fehler sollten nicht auftreten.
			e.printStackTrace();
		} catch (EmptyInputException e) {
			e.printStackTrace();
		} catch (InvalidInputException e) {
			e.printStackTrace();
		}
		return centerGPS;
	}
	
	/**
	 * Mit dieser Methode kann der zur uid zugehoerige MapNode abgefragt werden, falls ein solcher mit gueltiger
	 * GPSKoordinaten in der gerade betrachteten Knotenmenge existiert.
	 * Dies wir insbesondere beim Rekonstruieren eines Pfades benoetigt.
	 * @param uid Knoten UID des gegebenen Knotens n.
	 * @return Gibt MapNode mit der uebergebenen UID zurueck, falls dieser sich im aktuellen Ausschnitt befindet
	 * 			und im derzeitigen Zoomlevel angezeigt wird und ansonsten null.
	 * 
	 */
	public MapNode getNodeWithGPS(int uid) {
		synchronized (this.mapGraphTiles) {
			for(MapGraph mapGraph : this.mapGraphTiles) {
				if(mapGraph.getNode(uid)!=null) {
					if(mapGraph.getNode(uid).hasGPS()) {
						return mapGraph.getNode(uid);
					}
				}
			}
		}
		return null;
	}
		
	/**
	 * @return Gibt das aktuelle Zoomlevel zurueck. Mehr ueber Zoomlevels gibt es in der mainConstants.
	 */
	public int getZoomLevel() {
		return this.zoomLevel;
	}
	
	/**
	 * Setzt das aktuelle Zoomlevel auf den uebergebenen Wert, falls dieser die Bedingung nichtnegativ ist
	 * und kleiner oder gleich dem main.maxZoomLevel. Ansonsten tut sie Methode nichts.  
	 * Mehr ueber Zoomlevels gibt es in der mainConstants.maxZoomLevel
	 * @param zoomLevel Das zu setzende Zoomlevel.
	 */
	public void setZoomLevel(int zoomLevel) {
		if(zoomLevel>=0 || zoomLevel<=main.Config.maxZoomLevel) {
			this.zoomLevel=zoomLevel;
		}
	}

	/**
	 * Diese Methode gibt einen Knotens zurueck, der am naechsten zur gegeben GPS-Koordinate liegt.
	 * @param gps Betrachtete GPS-Koordinate
	 */
	public MapNode getClosestNode(GPSCoordinate gps) throws InvalidGPSCoordinateException,EmptyMapGraph {
		int minDistance=Integer.MAX_VALUE;
		MapNode closestNode=null;
		MapNode copiedClosestNode=null;
		synchronized (this.mapGraphTiles) {
			try {
				MapGraph graph = this.osmImp.getTile(gps, 0);
				closestNode = graph.getClosestNode(gps);				
			} catch (InvalidInputException e1){
				for(MapGraph m: this.mapGraphTiles) {
					try {
						MapNode tempNode=m.getClosestNode(gps);
						if(tempNode.getGPS().distanceTo(gps)<minDistance) {
							closestNode=tempNode;
							minDistance=tempNode.getGPS().distanceTo(gps);
						}	
					}
					catch (EmptyMapGraph e) {
					}
				}
			}		
			
			if(closestNode==null && minDistance==Integer.MAX_VALUE) {
				throw new EmptyMapGraph("Gegebener Kartenausschnitt leer. Keine nichtleeren MapGraphen.");
			}
			
			//Kopiere den closestNode, sodass nicht wegen der Referenzen im closestNode der ganze dazugehoerige MapGraph
			//im Speicher gehalten werden muss.
			try {
				copiedClosestNode=new MapNode(closestNode.getUID(), closestNode.getGPS());
			} catch (EmptyInputException e) {
				//Dieser Fehler kann nicht auftreten, da closestNode immer eine gueltige GPS besitzt.
				e.printStackTrace();
			}
		}
		return copiedClosestNode;
	}
	
	/**
	 * Methode zum berechnen eines kuerzesten Weges von der MapNode mit der startID zu der MapNode mit der
	 * endID.
	 * @param start
	 * @param end
	 */
	public void computeShortestPath(MapNode start,MapNode end) {
		try {
			//Frage den besten Pfad ab und erhalte diesen als ein Pfadobjekt
			Path shortestPath = Query.computeShortestPath(start.getUID(), end.getUID(), this.hGraph);
			ArrayList<Integer> bestPathIDs=shortestPath.getPathNodeIDs();
			Logger.getInstance().log("Viewport.computeShortestPath","Query + reconstruct abgeschlossen: Ermittle GPS zu jedem Knoten.");
			//Erstelle, die zu den IDs dazugehoerigen GPS Koordinaten in einer zusaetzlichen Arraylist
			ArrayList<GPSCoordinate> bestPathGPS=new ArrayList<GPSCoordinate>();
			for (int i = 0; i < bestPathIDs.size(); i++) { 
				//Hole zugehoerige GPS Koordinaten, falls diese bekannt sind
				HierarchyMapNode hNode=this.hGraph.getNode(bestPathIDs.get(i));
				if(hNode!=null) {
					bestPathGPS.add(i, hNode.getGPS());
				} else {//bestPathIDs.get(i) ist nur im MapGraph enthalten
					bestPathGPS.add(i, null);
				}
			}
			//Wenn ein gueltiger Pfad existiert, aktualisiere die GPS von Start und Ziel
			if(bestPathIDs.size()>=2) { 
				bestPathGPS.set(0,start.getGPS());
				bestPathGPS.set(bestPathGPS.size()-1,end.getGPS());
			}
			Logger.getInstance().log("Viewport.computeShortestPath","Folgender Pfad berechnet: "+ shortestPath);
			this.shortestPath=new Path2Draw(bestPathIDs,bestPathGPS,shortestPath.getPathLength(),shortestPath.getPathTime()); 
		} catch (Exception e) {
			//Hierhin sollte er nicht kommen, 
			//denn sonst muss in der Berechnung ein unerwarteter Fehler aufgetreten sein.
			e.printStackTrace();
		}
	}
	
	public Path2Draw getShortestPath() {
		return this.shortestPath;
	}
	
	/**
	 * 
	 * @return Startpunkt fuer MapPanel
	 */
	public GPSCoordinate getPOI(){
		try{
			if(osmImp!=null){
				return osmImp.getPOI();
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}
}
