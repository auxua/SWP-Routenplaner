package main;

import graphenbib.GPSCoordinate;
import graphenbib.MapNode;
import graphexceptions.InvalidInputException;

import java.util.ArrayList;
import java.util.Iterator;

public class Path2Draw {
	private ArrayList<Integer> pathIDs;
	private ArrayList<GPSCoordinate> pathGPS;
	private int totalDistance;
	private long totalTime;
	private GPSCoordinate startPos = null;
	private GPSCoordinate endPos = null;
	
	public final GPSCoordinate getStartPos() {
		return startPos;
	}


	public final GPSCoordinate getEndPos() {
		return endPos;
	}


	/**
	 * Konstruktor, der eine ArrayList von KnotenIDs uebergeben bekommt und eine ArrayList gleicher Laenge
	 * von GPSKoordinaten, die entweder null sind, falls die GPS-Koordinate nicht bekannt ist, oder eine gueltige
	 * GPS Koordinate, falls diese schon bekannt ist.
	 * @param pathIDs ArrayList von KnotenIDs
	 * @param pathGPS ArrayList von GPS-Koordinaten gleicher Laenge.
	 * @param distance Pfadlaenge in Dezimetern
	 * @param time Zeit um Pfad zurueck zu legen in Sekunden
	 * @throws InvalidInputException Falls gegebene Arrayslists ungleiche Laenge besitzen.
	 */
	public Path2Draw(ArrayList<Integer> pathIDs,ArrayList<GPSCoordinate> pathGPS, 
			int distance, long time) throws InvalidInputException {
		if(pathGPS.size()!=pathIDs.size()) {
			throw new InvalidInputException("Path2Draw mit ungleichen ArrayList laengen initilialisiert.");
		}
		this.pathGPS = pathGPS;
		this.pathIDs = pathIDs;
		this.totalDistance=distance;
		this.totalTime=time;
		//setze start und ende
		if (pathGPS != null && pathGPS.size()>0) {
			this.endPos = pathGPS.get(pathGPS.size()-1);
			this.startPos = pathGPS.get(0);
		}
	}

	
	/**
	 * Der Pfad kann mittels dieser Methode gezeichnet werden. Als zusaetzliches Argument ist ein
	 * Viewport Objekt noetig, aus dem sich das Pfad Objekt gegebenenfalls zusaetzliche GPS Koordinaten
	 * holt und diese speichert.
	 * @param graph2Draw In dieser Karte soll der Pfad gezeichnt werden.
	 * @return Einen Street2Draw Iterator, der alle Strassen, die den Pfad repraesentieren nacheinander zurueckgibt.
	 */
	public Iterator<Street2Draw> getPathIt(Viewport graph2Draw) {
		if(this.pathIDs.size()<2) {
			return null;
		}
		//Falls neuer Knoten mit GPS Koordinate hinzugekommen ist, fuege seine GPS in das Array pathGPS ein
		for (int i = 0; i < pathIDs.size(); i++) {
			if(pathGPS.get(i)==null) {
				MapNode tempNode=graph2Draw.getNodeWithGPS(pathIDs.get(i));
				if(tempNode!=null) {
					pathGPS.set(i, tempNode.getGPS());
				}
			}
		}
		//Nun fuege alle zeichenbaren Abschnitte in die ArrayList tempStreets ein
		//Wichtig ist hierfuer, dass die GPS der ersten und letzten MapNode gespeichert ist.
		ArrayList<Street2Draw> tempStreets=new ArrayList<Street2Draw>();
		int positionStreetEnd,positionStreetStart=0;
		while(positionStreetStart<pathIDs.size()) {
			positionStreetEnd=positionStreetStart+1;
			while(positionStreetEnd<pathIDs.size()) {
				if(pathGPS.get(positionStreetEnd)==null) {
					positionStreetEnd++;
				} else {
					break;
				}
			}
			if(positionStreetEnd<pathIDs.size()) {
				tempStreets.add(new Street2Draw(pathGPS.get(positionStreetStart),
						pathGPS.get(positionStreetEnd), true, "", null));
			}
			positionStreetStart=positionStreetEnd;
		}
		return tempStreets.iterator();
	}


	/**
	 * Gibt die Pfadlaenge zurueck.
	 * @return Laenge des Pfades in Dezimetern.
	 */
	public int getTotalDistance() {
		return totalDistance;
	}


	/**
	 * Gibt die Zeit zurueck, die man benoetig den Pfad zurueckzulegen.
	 * @return Zeit in Sekunden.
	 */
	public long getTotalTime() {
		return totalTime;
	}
}
