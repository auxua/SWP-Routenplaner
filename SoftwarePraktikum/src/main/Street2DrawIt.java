package main;


import graphenbib.GPSCoordinate;
import graphenbib.MapEdge;
import graphenbib.MapGraph;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.Iterator;

/**
 * Klasse, welche die Iteratorfunktionalitaet fuer die getStreetIt Methoden implementiert.
 */
public class Street2DrawIt implements Iterator<Street2Draw> {
	
	private ArrayList<MapGraph> graphs;
	private Iterator<MapGraph> mapIt;
	private Iterator<MapEdge> edgeIt;
	private Street2Draw nextStreet;
	boolean oneIteratorNull;
	
	public Street2DrawIt(ArrayList<MapGraph> tiles) {
		//mapIt = tiles.iterator();
		
		graphs = tiles;
		mapIt=new MapIterator();
		
		if(mapIt.hasNext()) {
			edgeIt=mapIt.next().getEdgeIt();
			oneIteratorNull=false;
		} else {
			edgeIt=null;
			oneIteratorNull=true;
		}
		//Speichere die naechste Strasse zwischen. Dieses Konstrukt ist noetig, da es sein kann, dass
		//es noch weitere Strassen gibt, die aber alle nicht gezeichnet werden koennen, da Start- und Endknoten
		//keine gueltigen GPS-Koordinaten besitzen.
		nextStreet=this.getNextValidStreet();
		
	}
	
	public void remove() {
	}
	
	public boolean hasNext() {
		if(nextStreet==null) {
			return false;
		} else {
			return true;
		}
	}

	public Street2Draw next() {
		Street2Draw result=nextStreet;
		nextStreet=this.getNextValidStreet();
		return result;
	}
	
	private boolean oneIteratorHasNext() {
		if(oneIteratorNull) {
			return false;
		} else {
			return (mapIt.hasNext() || edgeIt.hasNext());
		}
	}
	
	/**
	 * Mit dieser Methode wird die naechste zeichenbare Strasse zurueckgegeben, dass heisst mit gueltigen
	 * GPSKoordinaten des Start- und Zielknotens.
	 * @return Gueltige zeichenbare Strasse
	 */
	private Street2Draw getNextValidStreet() {
		if(!this.oneIteratorHasNext()) {
			return null;
		} else {
			if(edgeIt.hasNext()) {
				MapEdge curEdge=edgeIt.next();
				if(curEdge != null && curEdge.getNodeStart().hasGPS() && curEdge.getNodeEnd().hasGPS() && curEdge.drawIt()) {
					return convertMapEdge(curEdge);
				} else {
					return getNextValidStreet();
				}
			} else {
				MapGraph graph = mapIt.next();
				edgeIt=graph.getEdgeIt();
				return getNextValidStreet();
			}
		}
	}
	
	
	/**
	 * Methode, welche eine MapEdge in den Typ Street2Draw konvertiert, der die zum Zeichnen
	 * wichtigen Informationen kapselt.
	 * @param edge Zu konvertierende MapEdge.
	 * @return Zu zeichnendes Street2Draw Objekt
	 */
	private Street2Draw convertMapEdge(MapEdge edge) {
		Street2Draw street2Draw=new Street2Draw(edge.getNodeStart().getGPS(), 
				edge.getNodeEnd().getGPS(), edge.isOneWay(), edge.getName(), edge.getType());
		return street2Draw;
	}
	
	/**
	 * An optimized version of AbstractList.Itr
	 */
	private class MapIterator implements Iterator<MapGraph> {
		 
		int cursor;       // index of next element to return
		int lastRet = -1; // index of last element returned; -1 if no such
		
		public boolean hasNext() {
			return cursor != graphs.size();
		}
		
		public MapGraph next() {
			int i = cursor;
			if (i >= graphs.size()){
				cursor = 0;
				try {
					MapGraph newGraph = new MapGraph( new GPSCoordinate( 90,-180) ,  new GPSCoordinate( -90,180) );
					edgeIt = newGraph.getEdgeIt();
					return newGraph;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}  
			cursor = i + 1;
			return graphs.get( lastRet = i);
		}
		
		public void remove() {
			if (lastRet < 0)
				throw new IllegalStateException();
			try {
				graphs.remove(lastRet);
				cursor = lastRet;
				lastRet = -1;
			} catch (IndexOutOfBoundsException ex) {
				throw new ConcurrentModificationException();
			}
		}
	}
}
