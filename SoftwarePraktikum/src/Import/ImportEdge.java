package Import;

import graphenbib.StreetType;

import java.io.Serializable;

/**
 * 
 * 
 * @param startNode
 *            Integer das Informationen ueber den Start der Kante enthaelt.
 * @param endNode
 *            Integer das Informationen ueber das Ziel der Kante enthaelt.
 * @param wayID
 *            Integer, der die ID der Kante enthaelt.
 * @param streetType
 *            StreetType, der den Strassentyp enthaelt.
 */
@SuppressWarnings("serial")
public class ImportEdge implements Serializable {

	private int startNode = 0;
	private int endNode = 0;
	private int wayID = 0;
	private int length;
	private boolean oneway;
	private String name;
	private StreetType streetType;

	/**
	 * Konstruiert ein Objekt vom Typ Edge
	 */
	public ImportEdge(int wayID, int startNode, int endNode, int distance,
			StreetType streetType) {
		this.startNode = startNode;
		this.endNode = endNode;
		this.wayID = wayID;
		this.length = distance;
		this.streetType = streetType;
	}

	/**
	 * Konstruiert ein Objekt vom Typ Edge
	 */
	public ImportEdge(int wayID, int startNode, int endNode, int distance,
			boolean oneway, String name, StreetType streetType) {
		this.startNode = startNode;
		this.endNode = endNode;
		this.wayID = wayID;
		this.length = distance;
		this.oneway = oneway;
		this.name = name;
		this.streetType = streetType;
	}

	/**
	 * Setzt den Start der Kante
	 */
	public void setStartNode(int node) {
		this.startNode = node;
	}

	/**
	 * Setzt das Ziel der Kante
	 */
	public void setEndNode(int node) {
		this.endNode = node;
	}

	/**
	 * Gibt den Start der Kante zurueck
	 */
	public int getStartNode() {
		return startNode;
	}

	/**
	 * Gibt das Ziel der Kante zurueck
	 */
	public int getEndNode() {
		return endNode;
	}

	/**
	 * Gibt die ID der Kante zurueck
	 */
	public int getWayID() {
		return wayID;
	}

	/**
	 * Setzt die ID der Kante (bei relationsalso die relationID)
	 */
	public void setWayID(int wayID) {
		this.wayID = wayID;
	}

	/**
	 * Gibt den Strassentyp der Kante zurueck
	 */
	public StreetType getStreetType() {
		return streetType;
	}

	public void setStreetType(StreetType streetType) {
		this.streetType = streetType;
	}

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}

	public boolean isOneway() {
		return oneway;
	}

	public void setOneway(boolean oneway) {
		this.oneway = oneway;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Wandelt die Kante in einen String um, ueberschreibt Object.toString()
	 */
	@Override
	public String toString() {
		return ("WayID: " + wayID + "	StartNodeID: " + startNode
				+ "	EndNodeID: " + endNode + " Name: " + name + " Oneway: "
				+ oneway + " length: " + length);
	}

	/**
	 * Wandelt die Kante in einen String um, der im CSV Format gespeichert
	 * werden kann
	 */
	public String toFileString() {
		return ("Edge;" + wayID + ";" + startNode + ";" + endNode);
	}

}
