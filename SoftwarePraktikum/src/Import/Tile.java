package Import;

import graphenbib.GPS;
import graphenbib.GPSCoordinate;
import graphexceptions.InvalidGPSCoordinateException;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Diese Klasse dient als Speicher der Randdaten von MapGraphen. Es werden
 * Dateiname (ohne Zoomlevel), Bounds, Node UIDs und Node Anzahl gespeichert.
 * 
 * 
 */

public class Tile implements Serializable {
	private static final long serialVersionUID = 7374700278258892853L;
	private String name;
	private GPS upperleft, lowerright;
	private HashMap<Integer, Boolean> nodes = new HashMap<Integer, Boolean>();
	private int size;

	public Tile(String name) {
		this.name = name;
	}

	public Tile(String name, float minlat, float maxlat, float minlon,
			float maxlon) throws InvalidGPSCoordinateException {
		this.name = name;
		this.upperleft = new GPSCoordinate(maxlat, minlon);
		this.lowerright = new GPSCoordinate(minlat, maxlon);
	}

	public Tile(String name, float minlat, float maxlat, float minlon,
			float maxlon, int size) throws InvalidGPSCoordinateException {
		this.name = name;
		this.upperleft = new GPSCoordinate(maxlat, minlon);
		this.lowerright = new GPSCoordinate(minlat, maxlon);
		this.size = size;
	}

	public Tile(String name, GPS upperleft, GPS lowerright) {
		this.name = name;
		this.upperleft = upperleft;
		this.lowerright = lowerright;
	}

	public Tile(String name, GPS upperleft, GPS lowerright, int size) {
		this.name = name;
		this.upperleft = upperleft;
		this.lowerright = lowerright;
		this.size = size;
	}

	public void addNode(int node) {
		nodes.put(node, true);
	}

	public boolean nodeExists(int node) {
		return nodes.containsKey(node);
	}

	public GPS getUpperleft() {
		return upperleft;
	}

	public GPS getLowerright() {
		return lowerright;
	}

	public String getName() {
		return name;
	}

	public int getSize() {
		return this.size;

	}

}
