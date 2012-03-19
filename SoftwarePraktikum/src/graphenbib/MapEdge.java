package graphenbib;
import java.io.Serializable;

public class MapEdge extends AbstractEdge<MapNode> implements Edge, Serializable
{
	private static final long serialVersionUID = 7489334110309326926L;
	private String name;
	boolean draw; //Damit bei Strassen in beide Richtungen nur eine gezeichnet wird,
					//wird dieses Flag verwendet. Bei der einen Strasse ist dies dann true, bei der anderen false.
	boolean oneWay;
	
	/**
	 * Methode zum Abfragen des Strassennames.
	 * @return Strassenname.
	 */
	public String getName() {
		return name;
	}
	
	public MapEdge(MapNode start, MapNode end, int wayID, int length, StreetType streetType, String name)
    {
		super(start,end,wayID,length,streetType);
		this.name=name;
    }
	
	public MapEdge(MapNode start, MapNode end, int wayID, int length, StreetType streetType, String name, boolean draw, boolean oneWay)
    {
		this(start,end,wayID,length,streetType,name);
		this.draw=draw;
		this.oneWay=oneWay;
    }
	
	/**
	 * @param start Knoten, vom dem die Kante ausgeht.
	 * @param end Knoten, 
	 * @param wayID Die UID aus den OSM-Daten fuer diese Strasse.
	 * @param length Laenge der Strasse als int in dm.
	 * @param streetType 
	 */
	public MapEdge(MapNode start, MapNode end, int wayID, int length, StreetType streetType)
    {
		super(start,end,wayID,length,streetType);
    }

	public String toString(){
		return("Edge: startUID "+this.getNodeStart().getUID()+", endUID "+this.getNodeEnd().getUID()+", " +
				"wayID "+this.getUID()+", Length "+this.getLength()+", Typ: "+this.getType()+", Gewicht: "+this.getWeight()+" .\n");
	}
	
	public boolean isOneWay() {
		return this.oneWay;
	}
	
	public boolean drawIt() {
		return this.draw;
	}
}
