package graphenbib;

public interface Edge
{
	/** Gibt den Knoten an, von dem die Kante ausgeht*/
	Node getNodeStart();
	
	/**Gibt den Knoten an, in den die Kante hineingeht*/
	Node getNodeEnd();
	
	/**Gibt die ID der Kante zurueck, welche der aus dem OSM Import entspricht*/
	int getUID();
	
	/**Gibt den Typ der Kante zurueck (z.B. "Autobahn")*/
	StreetType getType();
	
	/**Gibt die Laenge der Kante zurueck */
	int getLength();
}
