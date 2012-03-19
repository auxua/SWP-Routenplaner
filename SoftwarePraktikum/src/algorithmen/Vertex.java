package algorithmen;

import graphenbib.AbstractNode;

public class Vertex {

	final AbstractNode node; // Verweis auf den (Hierarchy)MapNode

	private long distance; //Distanz zum Startknoten der Suche
	private int predecessor; //direkter Vorgaenger des Knotens
	
	//zusaetzliche Werte fuer Berechnung des Trees
	private boolean leaf = false; //true => dieser Vertex ist das Blatt eines Baumes (sprich: er hat keine Nachfolger)
	private State state; // Falls Active ist die Abbruchbedingung des Baumes (siehe Paper) noch nicht erfuellt
	private long leafDistance; // Speichert die Entfernung eines Knoten zu einem Blatt das diesen Knoten als Vorgaenger hat

	//Abbruchkriterien fuer die Erstellung des Shortest Path Trees (siehe Paper)
	private long Criterion1; //Hier wird der Abstand von s1 gespeichert
	private long Criterion2; //Hier wird der Abstand zu p erfasst
	
	//Werte fuer die Berechnung der Query
	private byte level; //aktuelles Hierachy-Level
	private long gap; //Abstand der noch gegangen werden darf ohne das Level zu wechseln.
	
	/**
	 * Standard-Konstruktor, wird allerdings nur noch von Dijksta.bidirectional verwendet.
	 * @param node der Knoten
	 * @param dis Distanz zum Startknoten
	 * @param pre Id des direkten Vorgaengers
	 */
	public Vertex(AbstractNode node, long dis, int pre) {
		this.node = node;
		this.distance = dis;
		this.predecessor = pre;
	}
	
	/**
	 * Verwende diesen Konstruktor falls Vorgaengerknoten nicht gespeichert werden muss
	 * (Beispiel: Neighbourhood)
	 * @param node der Knoten
	 * @param dis Distanz zum Startknoten
	 */
	public Vertex(AbstractNode node, long dis) {
		this.node = node;
		this.distance = dis;
		this.predecessor = -1;
	}
	
	/**
	 * Verwende diesen Knoten bei Berechnung des Shortest Path Tree
	 * @param node
	 * @param dis
	 * @param pre
	 * @param state
	 * @param crit1
	 * @param crit2
	 */
	public Vertex(AbstractNode node, long dis, int pre, State state, long crit1, long crit2) {
		this.node = node;
		this.distance = dis;
		this.predecessor = pre;
		this.state = state;
		this.Criterion1 = crit1;
		this.Criterion2 = crit2;
	}
	
	/**
	 * Verwende diesen Konstruktor in der Query
	 * @param node der Knoten
	 * @param dis Distanz zum Startknoten
	 * @param pre Id des direkten Vorgaengers
	 */
	public Vertex(AbstractNode node, long dis, int pre, byte level, long gap) {
		this.node = node;
		this.distance = dis;
		this.predecessor = pre;
		this.level = level;
		this.gap = gap;
	}

	public int getPredecessor() {
		return predecessor;
	}

	public void setPredecessor(int pre) {
		this.predecessor = pre;
	}

	
	public long getDist(){
		return distance;
	}
	
	public void setDist( long dist){
		this.distance = dist;
	}
	
	public void noLeaf(){
		this.leaf = false;
	}
	
	public void setLeaf(){
		this.leaf = true;
	}

	public void setState(State state) {
		this.state = state;		
	}

	public State getState() {
		return this.state;
	}	

	public void setCrit1(long crit1) {
		this.Criterion1 = crit1;
	}

	public void setCrit2(long crit2) {
		this.Criterion2 = crit2;		
	}

	public long getCrit1() {
		return this.Criterion1;
	}

	public long getCrit2() {
		return this.Criterion2;		
	}

	public boolean isLeaf() {
		return leaf;
	}
	
	public long getLeafDistance(){
		return this.leafDistance;
	}
	
	public void setLeafDistance(long leafDistance){
		this.leafDistance = leafDistance;
	}
	
	public byte getLevel(){
		return this.level;
	}
	
	public void setLevel(byte level){
		this.level = level;
	}
	
	public long getGap(){
		return this.gap;
	}
	
	public void setGap(long gap){
		this.gap = gap;
	}
}