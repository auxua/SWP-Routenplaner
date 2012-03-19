package graphenbib;
import graphexceptions.InvalidInputException;
/**
 * Implementierung der HierarchyMapEdge, die gegenueber der AbstractEdge noch als weitere Datenfelder
 * das aktuelle Level (level), das Level auf dem sie eingefuegt wurde (minLevel) und eventuell 
 * kontrahierte Knoten und deren Position speichert.
 */

public class HierarchyMapEdge extends AbstractEdge<HierarchyMapNode>
{
	private static final long serialVersionUID = 2659408632831861986L;
	private int contractedNodeIDs[]=null;
	private int distOfNodes[]=null;
	/**
	 * Das Level der Kante. Bei den konstruierten Graphen G_0,G_1,... wissen wir dann, bis zu welcher
	 * Hoehe diese Kante noch bei der Hierarchie liegt.
	 */
	private byte level = 0;
	private byte minLevel =0; //Der Level, ab dem die Kante hinzugekommen ist
	
	
	/**
	 * Konstruktor einer HierarchyMapEdge mit dem alle Fehler bis auf die GPS-Koordinate gesetzt werden.
	 * @param start Startknoten
	 * @param end Zielknoten
	 * @param wayID Die ID der Kante entsprechend der OSM Daten.
	 * @param length Laenge der Kante in Dezimtern.
	 * @param streetType Strassentyp der Kante.
	 * @param level Level auf dem die Kante eingefuegt werden soll.
	 * @param contractedNodeIDs Kontrahierte Knoten in der Kante.
	 * @param distOfNodes Die Abstaender der kontrahierten Knoten zum Startknoten.
	 */
	public HierarchyMapEdge(HierarchyMapNode start, HierarchyMapNode end, int wayID,
	        int length, StreetType streetType, byte level,int contractedNodeIDs[],int distOfNodes[])
	{
		super(start,end,wayID,length,streetType);
		if (level>=0)
			this.level =this.minLevel= level;
		this.contractedNodeIDs=contractedNodeIDs;
		this.distOfNodes=distOfNodes;
	}
	
	/**
	 * Konstruktor einer HierarchyMapEdge mit dem alle Fehler bis auf die GPS-Koordinate gesetzt werden.
	 * @param start Startknoten
	 * @param end Zielknoten
	 * @param wayID Die ID der Kante entsprechend der OSM Daten.
	 * @param length Laenge der Kante in Dezimtern.
	 * @param streetType Strassentyp der Kante.
	 * @param level Level auf dem die Kante eingefuegt werden soll.
	 */
	public HierarchyMapEdge(HierarchyMapNode start, HierarchyMapNode end, int wayID,
	        int length, StreetType streetType, byte level)
	{
		this(start,end,wayID,length,streetType,level,new int[0],new int[0]);
	}
	
	/**
	 * @return Das minimale Level der Kante, das heisst, das Level, in dem die Kante in den HierarchyMapGraph
	 * eingefuegt wurde.
	 */
	public byte getMinLevel()
    {
    	return minLevel;
    }

	/**
	 * Setzt das minimale Level der Kante, das heisst, das Level, in dem die Kante in den HierarchyMapGraph
	 * eingefuegt wurde.
	 * @param minLevel Der Wert auf den das minimale Level gesetzt werden soll.
	 */
	public void setMinLevel(byte minLevel)
    {
    	this.minLevel = minLevel;
    }

	/**
	 * @return Den aktuellen Level der Kante
	 */
	public byte getLevel()
	{
		return this.level;
	}
	
	/**
	 * Setzt den Level der Kante neu auf den Wert l
	 * @param l Neuer Level der Kante
	 */
	public void setLevel(byte l)
	{
		if (l>=0)
			this.level = l;
	}
	
	/**
	 * Meistens wollen wir ja nur die Kante einen Level hoeher setzen.
	 * Diese Funktion inkrementiert Level um 1
	 */
	protected void increaseLevel()
	{
		level++;
	}
	
	/**
	 * Hiermit wird das Level der Kante um 1 dekrementiert. 
	 */
	protected void decreaseLevel()
	{
		level--;
	}
	
	/**
	 * Falls die betrachtete Kante aus der Kontraktion mehrere Kanten resultiert, werden die IDs
	 * der hierdurch geloeschten Knoten in der Kante gespeichert. Dabei entspricht die Reihenfolge
	 * gerade die Reihenfolge in der sie auf dem urspruenglichen Kantenzug auftraten. Mit dieser Methode
	 * koennen diese IDs abgefragt werden.
	 * @return Array der IDs der Knoten, die durch Kontraktion geloescht wurden.
	 */
	public int[] getContractedNodeIDs() {
		return this.contractedNodeIDs;
	}
	
	/**
	 * Um auch zu speichern, an welchen Positionen Knoten lagen bevor sie kontrahiert wurden, wird ein Array anleget,
	 * dass mit dieser Methode abgefragt werden kann. Dies speichert an der Stelle i die Distanz des Knoten contractedNodeIDs[i]
	 * von dem Startknoten der Kante.
	 * @return Array mit Distanzen der kontrahierten Knoten zum Startknoten.
	 */
	public int[] getContractedNodeDistances() {
		return this.distOfNodes;
	}
	
	public int getContractedNodeDistance(int nodeID) throws InvalidInputException{
		for (int i = 0; i < contractedNodeIDs.length; i++) {
			if(nodeID==contractedNodeIDs[i]) {
				return distOfNodes[i];
			}
		}
		throw new InvalidInputException("Kontrahierter Knoten mit ID "+nodeID +" nicht in Kante gespeichert.");
	}
	
	
	public String toString(){
		String mapEdgeString="Edge: startUID "+this.getNodeStart().getUID()+", endUID "+getNodeEnd().getUID()+
				", wayID "+this.getUID()+", " +"Length "+this.getLength()+", StreetType: "+this.getType()+", Gewicht: "+this.getWeight()+", " +
				"Level "+level+", MinLevel "+this.minLevel+".\n";
		mapEdgeString+="IDs von kontrahierten Knoten: ";
		for (int nodeID : this.contractedNodeIDs) {
			mapEdgeString+=nodeID+", ";
		}
		mapEdgeString+="\nDistanzen von kontrahierten Knoten: ";
		for (int nodeDist : this.distOfNodes) {
			mapEdgeString+=nodeDist+", ";
		}
		mapEdgeString+="\n";
		return mapEdgeString;
	}

}
