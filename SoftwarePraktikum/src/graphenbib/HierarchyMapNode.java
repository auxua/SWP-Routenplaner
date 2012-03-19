package graphenbib;

import graphexceptions.EmptyInputException;

import java.util.ArrayList;
import java.util.HashSet;

import main.Config;

public class HierarchyMapNode extends AbstractNode<HierarchyMapEdge>
{
	private static final long serialVersionUID = 2729702305637430373L;
	private long[] dHforward = new long[Config.maxHierarchyLayer+1]; //Die Distanz zum H-naechsten Knoten vorwaerts
	private long[] dHbackward = new long[Config.maxHierarchyLayer+1]; //Die Distanz zum H-naechsten Knoten ruckwaerts
	private byte level=0;
	
	/**
	 * Konstruktor der Klasse MapNode wird einfach aufgerufen.
	 * @param uid
	 * @throws EmptyInputException
	 */
	public HierarchyMapNode(int uid,int numberIncomingEdges,int numberOutgoingEdges)
	{
		this(uid,numberIncomingEdges,numberOutgoingEdges,false,0f,0f);
	}
	
	public HierarchyMapNode(int uid,int numberIncomingEdges,int numberOutgoingEdges,boolean hasGPS, float latitude, float longitude)
	{
		super(uid,new HierarchyMapEdge[numberOutgoingEdges],new HierarchyMapEdge[numberIncomingEdges],hasGPS,latitude,longitude);
		for (int i=0;i<Config.maxHierarchyLayer;i++){
			dHforward[i] = 0;
			dHbackward[i] = 0;
		}
	}
	
	/**
	 * 
	 * @param l Das Level der Kanten, die wir zurueckgegeben bekommen wollen
	 * @return Eine Liste ausgehender Kanten, die MINDESTENS das Level l haben.
	 */
	public ArrayList<HierarchyMapEdge> getOutgoingEdgesByHierarchy(byte l)
	{
		if (l < 0) return null;
		ArrayList<HierarchyMapEdge> result = new ArrayList<HierarchyMapEdge>();
		for (HierarchyMapEdge e : this.getOutgoingEdges())
		{
			if (e == null) break;
			if (e.getLevel() < l) continue;
			result.add(e);		
		}
		return result;
	}
	
	/**
	 * 
	 * @param l Das Level der Kanten, die wir zurueckgegeben bekommen wollen
	 * @return Eine Liste hineinfuerender Kanten, die mindestens das Level l haben.
	 */
	public ArrayList<HierarchyMapEdge> getIncomingEdgesByHierarchy(byte l)
	{
		if (l < 0) return null;
		ArrayList<HierarchyMapEdge> result = new ArrayList<HierarchyMapEdge>();
		for (HierarchyMapEdge e : this.getIncomingEdges())
		{
			if (e == null) break;
			if (e.getLevel() < l) continue;
			result.add(e);	
		}
		return result;
	}

	/**
	 * Funktion um eine die Menge von Nachbarn zu erhalten, die ueber eine Kante von 
	 * mindestens einem bestimmten Level erreichbar sind. Hierfuer werden Mengen verwendet,
	 * sodass keine Nachbarn doppelt vorkommen.
	 * @param l Das Level der Kanten, ueber die wir Nachbarn erreichen wollen
	 * @return Eine Menge von Nachbarn, die ueber eine Kante von mindestens Level
	 * 		l erreicht werden.
	 */
	public HashSet<HierarchyMapNode> getNeighbours(byte l) {
		if (l < 0) return null;
		HashSet<HierarchyMapNode> neighbours = new HashSet<HierarchyMapNode>();
		for (HierarchyMapEdge e : this.getOutgoingEdges())
		{
			if (e == null) break;
			if (e.getLevel() < l) continue;
			neighbours.add(e.getNodeEnd());	
		}
		return neighbours;
	}
	
	/**
	 * Funktion um eine die Menge von Vorgaengern zu erhalten, die ueber eine Kante von 
	 * mindestens einem bestimmten Level erreichbar sind. Hierfuer werden Mengen verwendet,
	 * sodass keine Vorgaenger doppelt vorkommen.
	 * @param l Das Level der Kanten, ueber die wir Vorgaenger erreichen wollen
	 * @return Eine Menge von Vorgaengern, die ueber eine Kante von mindestens Level
	 * 		l erreicht werden.
	 */
	public HashSet<HierarchyMapNode> getPredecessors(byte l) {
		if (l < 0) return null;
		HashSet<HierarchyMapNode> predecessors = new HashSet<HierarchyMapNode>();
		for (HierarchyMapEdge e : this.getIncomingEdges())
		{
			if (e == null) break;
			if (e.getLevel() < l) continue;
			predecessors.add(e.getNodeStart());	
		}
		return predecessors;
	}
	
	/**
	 * Setzt die Distanz zu dem H-naechsten Knoten von dem aktuellen Knoten
	 * @param dH Die uebergebene Distanz
	 */
	public void setdH(long dH, byte level, boolean forward)
	{
		if (forward){
			this.dHforward[level] = dH;
		}else{
			this.dHbackward[level] = dH;
		}
	}
	
	/**
	 * Gibt die Distanz zum H-naechsten Knoten des aktuellen Knotens an
	 * @return die Distanz zum h-naechsten Knoten
	 */
	public long getdH(byte level, boolean forward)
	{
		if (forward)
			return this.dHforward[level];
		return this.dHbackward[level];
		
	}
	
	/**
	 * Gibt den Level des Knotens an, auf dem er sich befindet.
	 * @return das Level
	 */
	public byte getLevel()
	{
		return this.level;
	}
	
	/**
	 * Setzt den Level der Knoten 
	 * @param l
	 */
	public void setLevel(byte l)
	{
		if (l>=0)
			this.level= l;
	}
	
	/**
	 * Fuegt eine eingehende Kante hinzu. Ist der Level der hinzuzufuegenden Kante groesser als
	 * der aktuell in der Kante gespeicherte Level, dann wird der aktuelle Level entsprechend angepasst
	 * @param e Die hinzuzufuegende Kante
	 */
	@Override
	protected void addIncomingEdge(HierarchyMapEdge e)
	{
		if (e.getLevel()>this.level) {
			this.level = e.getLevel();
		}
		super.addIncomingEdge(e);
	}
	
	/**
	 * Fuegt eine ausgehende Kante hinzu. Ist der Level der hinzuzufuegenden Kante groesser als
	 * der aktuell in der Kante gespeicherte Level, dann wird der aktuelle Level entsprechend angepasst
	 * @param e
	 */
	@Override
	protected void addOutgoingEdge(HierarchyMapEdge e)
	{	
		if (e.getLevel()>this.level) {
			this.level = e.getLevel();
		}
		super.addOutgoingEdge(e);
	}
	
	/**
	 * Wenn der Knoten eine Kante zum Knoten mit der UID nUID besitzt (Incoming oder Outgoing), wird diese
	 * zurueckgegeben. Ansonsten wird null zurueckgegeben. 
	 * @param nUID
	 * @return Die Kante, wenn die Knoten benachbart sind, ansonsten null
	 */
	public HierarchyMapEdge getEdgeToNeighbour(int nUID)
	{//getEdgeToNeighbour
		HierarchyMapEdge incomingEdges[]=this.getIncomingEdges();
		HierarchyMapEdge outgoingEdges[]=this.getOutgoingEdges();
		for (int i = 0; i < outgoingEdges.length; i++)
        {
	        if (outgoingEdges[i].getNodeEnd().getUID()==nUID)
	        	return outgoingEdges[i];
        }
		for (int i = 0; i < incomingEdges.length; i++)
        {
	        if (incomingEdges[i].getNodeStart().getUID()==nUID)
	        	return incomingEdges[i];
        }
		return null;
	}//getEdgeToNeighbour
	
	
	public void updateLevel() {
		byte maxLevel=0;
		HierarchyMapEdge incomingEdges[]=this.getIncomingEdges();
		HierarchyMapEdge outgoingEdges[]=this.getOutgoingEdges();
		for (int i = 0; i < incomingEdges.length; i++) {
			if ((incomingEdges[i]!=null) && (incomingEdges[i].getLevel()>maxLevel)) {
				maxLevel=incomingEdges[i].getLevel();
			}
		}
		for (int i = 0; i < outgoingEdges.length; i++) {
			if ((outgoingEdges[i]!=null) && (outgoingEdges[i].getLevel()>maxLevel)) {
				maxLevel=outgoingEdges[i].getLevel();
			}
		}
		this.level=maxLevel;
	}
	
	
	/**
	 * Diese Methode gibt die Zahl der eingehenden Kanten zurueck, deren Level groesser oder
	 * gleich dem angegebenen Level ist. Fuer Level 0 gibt sie also die Zahl der eingehenden Kanten zurueck.
	 * @param level Betrachte nur Kanten mit Level groesser oder gleich diesem Level.
	 * @return Zahl der Kanten mit Level groesser oder gleich dem angegebenen Level.
	 */
	public int getNumberOfIncomingEdges(byte level) {
		if(level==0) {
			return this.getIncomingEdges().length; //Alle Edges sind auf Level 0
		} else {
			int count=0;
			for (HierarchyMapEdge inEdge :this.getIncomingEdges()) {
				if(inEdge.getLevel()>=level) {
					count++;
				}
			}
			return count;
		}
	}
	
	/**
	 * Diese Methode gibt die Zahl der ausgehenden Kanten zurueck, deren Level groesser oder
	 * gleich dem angegebenen Level ist. Fuer Level 0 gibt sie also die Zahl der ausgehenden Kanten zurueck.
	 * @param level Betrachte nur Kanten mit Level groesser oder gleich diesem Level.
	 * @return Zahl der Kanten mit Level groesser oder gleich dem angegebenen Level.
	 */
	public int getNumberOfOutgoingEdges(byte level) {
		if(level==0) {
			return this.getOutgoingEdges().length; //Alle Edges sind auf Level 0
		} else {
			int count=0;
			for (HierarchyMapEdge outEdge : this.getOutgoingEdges()){
				if(outEdge.getLevel()>=level) {
					count++;
				}
			}
			return count;
		}
	}
	
	
	/**
	 * Erhoeht des Level des Knotens um 1.
	 */
	protected void increaseLevel()
	{
		level++;
	}
	
	/**
	 * Verringert das Level des Knotens um 1. Uberlaeufe werden nicht abgefangen.
	 */
	protected void decreaseLevel()
	{
		level--;
	}
	
	/**
	 * Gibt an, ob der gegebene Knoten nicht mindestens eine ausgehende oder eingehende Kante
	 * mit mindestens dem gegebenen Level hat.
	 * @param level Level, ueber das der Knoten mindestens verbunden sein soll.
	 * @return True, falls der Knoten mit Kante mit mindestens dem gegebenen Level verbunden ist.
	 */
	public boolean isIsolated(byte level) {
		if(this.getNumberOfIncomingEdges(level)==0 &&
				this.getNumberOfOutgoingEdges(level)==0) {
			return true;
		} else {
			return false;
		}
	}
	
	@Override
	public String toString(){
		String nodeString="HierarchyMapNode: "+super.toString() +
				" Level: "+this.level+"\n"; 
		nodeString+="dHforward:\n";
		for (int i = 0; i < dHforward.length; i++) {
			nodeString+=dHforward[i]+", ";
		}
		nodeString+="\n dHbackward:\n";
		for (int i = 0; i < dHbackward.length; i++) {
			nodeString+=dHbackward[i]+", ";
		}
		return nodeString;		
	}
	
	
}
