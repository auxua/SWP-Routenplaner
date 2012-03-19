package graphenbib;
import java.io.Serializable;

import main.Config;

public abstract class AbstractEdge<NodeType> implements Serializable {
	
	private static final long serialVersionUID = 217758833904779794L;
	private int wayID;
	private int length;
	private StreetType streetType;
	private NodeType start, end;
	

	/**
	 * Standardkonstruktor, der in den konkreten Implementierung (MapEdge, HierarchyMapEdge) in
	 * den jeweiligen Konstruktoren aufgerufen wird.
	 * @param start Der Startknoten der Kante.
	 * @param end Der Endknoten der Kante.
	 * @param wayID Die ID der Kante, entspricht der ID aus den OSM Daten.
	 * @param length Die Laenge der Kante in Dezimetern als int.
	 * @param streetType Der Strassentyp der Kante, enspricht dem Strassentyp aus den OSM Daten.
	 */
	public AbstractEdge(NodeType start, NodeType end, int wayID, int length, StreetType streetType) {
		this.start=start;
		this.end=end;
		this.wayID = wayID;
		this.length = length;
		this.streetType = streetType;
	}

	/**
	 * Getter-Methode fuer den Strassentyp.
	 * @return Strassentyp der Kante.
	 */
	public StreetType getType()
	{
		return streetType;
	}
	
	/**
	 * Getter fuer die ID einer Kante.
	 * @return Die ID der Kante, entspricht der ID aus den OSM Daten.
	 */
	public int getUID()
	{
		return wayID;
	}
	
	/**
	 * Getter fuer die Laenge einer Kante.
	 * @return Die Laenge der Kante in Dezimetern als int.
	 */
	public int getLength()
	{
		return length;
	}
	
	
	/**
	 * Setter fuer die Laenge einer Kante.
	 * @param length Zu setzende Laenge.
	 */
	protected void setLength(int length) {
		this.length=length;
	}
	
	/**
	 * Das Gewicht einer Kante wird abhaengig von der Constants.fastestPathMode berechnet. Entweder sollen
	 * kuerzeste Wege berechnet werden, dann ist das Gewicht einfach die Laenge der Kante in Dezimetern.
	 * Ansonsten wird mittels der in der main.Constants festgelegten Durchschnittsgeschwindigkeiten die Zeit 
	 * in Sekunden berechnet, die man benoetig um den Weg, den die Kante beschreibt zurueckzulegen. Diese
	 * Zeit in Sekunden wird dann zurueckgegeben.
	 * @return Gewicht der Kante als int.
	 */
	public long getWeight()
	{
		if(Config.fastestPathMode){
			return this.getTime();
		} else {
			return this.getLength();
		}
	}
	
	/**
	 * Mittels der in der main.Constants festgelegten Durchschnittsgeschwindigkeiten wird die Zeit 
	 * in Sekunden berechnet, die man benoetig um den Weg, den die Kante beschreibt zurueckzulegen. 
	 * @return  Zeit in Sekunden
	 */
	public long getTime() {
		return (long) (this.length*Config.getSpeedFactor(this.streetType));
	}
	
	/**
	 * Getter fuer den Startknoten einer Kante.
	 * @return Den Startknoten der Kante.
	 */
	public NodeType getNodeStart()
	{
		return start;
	}

	/**
	 * Getter fuer den Endknoten einer Kante.
	 * @return Den Endknoten der Kante.
	 */
	public NodeType getNodeEnd()
	{
		return end;
	}
}
