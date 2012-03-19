package graphenbib;

import graphexceptions.InvalidGPSCoordinateException;

import java.io.Serializable;
import java.util.Arrays;

public abstract class AbstractNode<EdgeType> implements Serializable{
	private static final long serialVersionUID = 3621909982628454283L;
	private int uid;
	private EdgeType[] outgoingEdges; 
	private EdgeType[] incomingEdges;
	private float longitude;
	private float latitude;
	private boolean hasGPS;
	
	/**
	 * Standardkonstruktor, der in den konkreten Implementierungen MapNode und HierarchyMapNode
	 * in den Konstruktoren aufgerufen wird. Die Arrays der eingehenden und ausgehenden Kanten
	 * werden mit Null-Werten initialisiert.
	 * @param uid Die UID des Knotens
	 * @param outgoingEdges Array der ausgehenden Kanten.
	 * @param incomingEdges Array der eingehenden Kanten.
	 */
	public AbstractNode(int uid, EdgeType[] outgoingEdges,
			EdgeType[] incomingEdges,boolean hasGPS, float latitude, float longitude) {
		this.uid = uid;
		this.outgoingEdges = outgoingEdges;
		this.incomingEdges = incomingEdges;
		//Null-Init wichtig!
		for (int i=0;i<this.incomingEdges.length;i++)
			incomingEdges[i] = null;
		for (int i=0;i<this.outgoingEdges.length;i++)
			outgoingEdges[i] = null;
		if(hasGPS) {
			this.hasGPS=true;
			this.latitude=latitude;
			this.longitude=longitude;
		} else {
			this.hasGPS=false;
			this.latitude=0;
			this.longitude=0;
		}
	}
	
	/**
	 * Methode um die UID eines Knotens abzufragen. Diese stimmt mit der UID aus den OSM Daten ueberein.
	 * @return UID des Knotens.
	 */
	public int getUID()
	{
		return uid;
	}
	
	/**
	 * Methode um Zahl der ausgehenden Kanten eines Knotens abzufragen.
	 * @return Zahl der ausgehenden Kanten.
	 */
	public int getNumberOfOutgoingEdges() {
		return outgoingEdges.length;
	}
	
	/**
	 * Methode um Zahl der eingehenden Kanten eines Knotens abzufragen.
	 * @return Zahl der eingehenden Kanten.
	 */
	public int getNumberOfIncomingEdges() {
		return incomingEdges.length;
	}
	
	/**
	 * Methode, mit der man die eingehenden Kanten eines Knotens erhaelt. Dieses Array darf nicht manipuliert
	 * werden, da sonst ungewollte Seiteneffekte auftreten.
	 * @return Array der eingehenden Kanten.
	 */
	public EdgeType[] getIncomingEdges() 
	{
		return incomingEdges;
	}
	
	/**
	 * Methode, mit der man die ausgehenden Kanten eines Knotens erhaelt. Dieses Array darf nicht manipuliert
	 * werden, da sonst ungewollte Seiteneffekte auftreten.
	 * @return Array der ausgehenden Kanten.
	 */
	public EdgeType[] getOutgoingEdges()
	{
		return outgoingEdges;
	}
	
	/**
	 * Loescht die uebergebene Kante aus dem Array der eingehenden Kanten des Knoten.
	 * @param e Zu loeschende Kante.
	 * @return True, falls Kante geloescht wurde. False, falls sich uebergebene Kante nicht
	 * im Array der eingehenden Kanten befindet.
	 */
	public boolean deleteIncomingEdge(EdgeType e)
	{
		boolean deleted=false;
		int pos;
		for (pos = 0; pos < incomingEdges.length; pos++) {
			if(this.incomingEdges[pos]==e) {
				deleted=true;
				break;
			}
		}
		//Fall: Kante nicht vorhanden, gebe also False zurueck
		if(!deleted) {
			return false;
		}
		//Fall: Kante vorhanden. Verschiebe Kanten im Array und kopiere es
		for (; pos < incomingEdges.length-1; pos++) {
			incomingEdges[pos]=incomingEdges[pos+1];
		}
		incomingEdges = Arrays.copyOf(incomingEdges,incomingEdges.length-1);
		return true;
	}
	
	/**
	 * Loescht die uebergebene Kante aus dem Array der ausgehenden Kanten des Knoten.
	 * @param e Zu loeschende Kante.
	 * @return True, falls Kante geloescht wurde. False, falls sich uebergebene Kante nicht
	 * im Array der ausgehenden Kanten befindet.
	 */
	public boolean deleteOutgoingEdge(EdgeType e)
	{
		boolean deleted=false;
		int pos;
		for (pos = 0; pos < outgoingEdges.length; pos++) {
			if(this.outgoingEdges[pos]==e) {
				deleted=true;
				break;
			}
		}
		//Fall: Kante nicht vorhanden, gebe also False zurueck
		if(!deleted) {
			return false;
		}
		//Fall: Kante vorhanden. Verschiebe Kanten im Array und kopiere es
		for (; pos < outgoingEdges.length-1; pos++) {
			outgoingEdges[pos]=outgoingEdges[pos+1];
		}
		outgoingEdges = Arrays.copyOf(outgoingEdges,outgoingEdges.length-1);
		return true;
	}
	
	/**
	 * Fuegt die uebergebende Kante zu den eingehenden Kanten hinzu.
	 * @param e Die hinzuzufuegende Kante
	 */
	protected void addIncomingEdge(EdgeType e)
	{
		int pos;
		for (pos = 0; pos < incomingEdges.length; pos++) {
			if(incomingEdges[pos]==null) {
				break;
			}
		}
		if(pos<incomingEdges.length) {
			incomingEdges[pos] = e;
			
		} else {
			//Array muss um eine Stelle verlaengert werden
			incomingEdges=Arrays.copyOf(incomingEdges,incomingEdges.length+1);
			incomingEdges[incomingEdges.length-1]=e;
		}
	}
	
	/**
	 * Fuegt die uebergebene Kante zu den ausgehenden Kante hinzu.
	 * @param e
	 */
	protected void addOutgoingEdge(EdgeType e)
	{	
		int pos;
		for (pos = 0; pos < outgoingEdges.length; pos++) {
			if(outgoingEdges[pos]==null) {
				break;
			}
		}
		if(pos<outgoingEdges.length) {
			outgoingEdges[pos] = e;
			
		} else {
			//Array muss um eine Stelle verlaengert werden
			outgoingEdges=Arrays.copyOf(outgoingEdges,outgoingEdges.length+1);
			outgoingEdges[outgoingEdges.length-1]=e;
		}
	}
	
	/**
	 * Methode zur Textrepraesentation fuer Debugging-Zwecke.
	 */
	public String toString() {
		String nodeString ="UID "+this.getUID()+", GPS: Longitude: "+longitude+" " +
			"Latitude "+latitude+"\n";
		nodeString+="Outgoing Edges: \n";
		for(EdgeType e: this.getOutgoingEdges()) {
			nodeString=nodeString+e.toString();
		}
		nodeString+="Incoming Edges: \n";
		for(EdgeType e: this.getIncomingEdges()) {
			nodeString=nodeString+e.toString();
		}
		nodeString+="\n";
		return nodeString;
	}
	
	/**
	 * return: Die GPS-Koordinate des MapNodes oder Null, falls der MapNode ohne GPS-Koordinate eingefuegt wurde.
	 */
	public GPSCoordinate getGPS()
	{
		GPSCoordinate gps=null;
		//Fuer einen Knoten ohne GPS Koordinaten wird hier null zurueckgegeben
		if(!hasGPS) {
			return gps;
		}
		try {
			gps = new GPSCoordinate(this.latitude, this.longitude);
		} catch (InvalidGPSCoordinateException e) {
			//dieser Fehler kann nicht auftreten, da nur gueltige GPS eingefuegt werden
			e.printStackTrace();
		}
		return gps;
	}
	
	/**
	 * Es ist erlaubt, Knoten ohne eine GPS-Koordinate in eine MapGraph einzufuegen,
	 * da diese Information nicht unbedingt in einem Tile vorhanden ist.
	 * @return True, falls der Knoten eine gueltige GPS-Koordinate besitzt, ansonsten false.
	 */
	public boolean hasGPS() {
		return this.hasGPS;
	}
	
	public void setGPS(GPS gps) {
		if(gps!=null) {
			this.hasGPS=true;
			this.latitude=gps.getLatitude();
			this.longitude=gps.getLongitude();
		}
	}
}
