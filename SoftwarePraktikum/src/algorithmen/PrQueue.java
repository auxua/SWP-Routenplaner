/**
 * 
 */
package algorithmen;

import graphexceptions.EmptyInputException;
import graphexceptions.InvalidInputException;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Diese Warteschlange speichert verschiedene Objekte der Klasse Vertex,
 * In dieser Liste werden die Elemente nach dem Wert der in Vertes.distance gespeichert ist sortiert.
 * Im Gegensatz zu einer ArrayList kann aber von aussen nur auf das erste Element der Liste zugegriffen werden.
 * Falls 2 Elemente ueber den selben Wert in Vertex.distance verfuegen, wird nach dem FIFO-Prinzip sortiert.
 * <p>
 * Die Warteschlange wurde als Min-Heap realisiert, allerdings ist Einfuegen nicht in O(1) moeglich,
 * da fuer neue Elemente geprueft wird, ob sich das Element schon in der Liste befindet.
 * 
 */
public class PrQueue {
	
	/**
	 * Intern werden alle Elemente in einer gewoehnlichen ArrayList gespeichert
	 */
	private ArrayList<Vertex> nodes = new ArrayList<Vertex>();
	
	/**
	 * Vertauscht zwei Elemente innerhalb der Liste
	 * <p>
	 * Da diese methode private ist, wurde nicht getestet ob die Werte im gueltigen Bereich liegen. 
	 * 
	 * @param first Position des ersten Element
	 * @param second Position des zweiten Elements
	 */
	private void swap(int first, int second){
		Vertex help = nodes.get(first);
		
		nodes.set(first, nodes.get(second));
		nodes.set(second, help );
	}
	
	/** Setzt das Element mit der Position pos an die richtige Stelle
	 * Da die Methode nur von insert verwendet wird, kann zum Zeitpunkt des Aufrufs davon ausgegangen werden,
	 * dass die Warteschlange bis auf das Element an der uebergebenen Position korrekt sortiert ist.
	 * <p>
	 * Dazu tauscht dieses Element solange die Position mit seinem Vater (Element an Stelle (pos-1)/2)) getauscht wird,
	 * bis ein Vater gefunden wird dessen distance kleiner ist als die distance des neu eingefuegten Elements.
	 * @param pos
	 */
	private void sort(int pos){
		while(pos > 0){
			int parent = (pos - 1) / 2;
			if (nodes.get(pos).getDist() >= nodes.get(parent).getDist()){
				break;
			}
			swap(pos, parent);
			pos = parent;
		}
	}
	/**
	 * Wird verwendet nachdem das kleinste Element der Schlange geloescht wurde.
	 * Zu Beginn diesen Aufruf findet sich das (ehemals) letzte Element an erster Stelle, ansonsten sind die Heapeigenschaften aber erfuellt.
	 * <p>
	 * Nun wird dieses Element solange mit seinem groesseren Kind getauscht, bis ueberall die Heapeigenschaften wiede4rhergestellt sind.
	 */
	private void sortUp(int pos){
		while (pos < nodes.size()/2){
			int child = 2*pos+1;
			if ( (child < nodes.size()-1) && (nodes.get(child).getDist() > nodes.get(child+1).getDist() )){
				child++;
			}
			if (nodes.get(pos).getDist() < nodes.get(child).getDist()){
				break;
			}
			swap(pos, child);
			pos = child;
		}
	}
	
	/**
	 * Fuegt ein neues Element in die Warteschlange ein.
	 * Dazu wird zuerst geprueft, ob das Element in die Liste eingefuegt werden darf, ansonsten wird eine Exception geworfen.
	 * <p>
	 * Im Erfolgsfall wird der Vertex zunaechst ans Ende der Schloeange gehaengt und dann an die richtige Stelle sortiert.
	 * @param vertex Der Vertex der in die Schlange aaufgenommen werden soll.
	 * @throws Exception Eine Exception wird geworfen, falls eine der beiden folgenden berdingungen erfuellt ist:
	 * <p> sich bereits ein Vertex mit der selben ID in der Warteschlange befindet.
	 * <p> node.getDist() < 0
	 */
	public void insert(Vertex vertex) throws Exception {
		if (vertex != null){
			Iterator<Vertex> iter = nodes.iterator();
			while (iter.hasNext()){
				if (vertex.node.getUID() == iter.next().node.getUID()) throw new InvalidInputException("Vertex der bereits existiert uebergeben: "+vertex.node.getUID());
			}
			if (vertex.getDist() < 0) {
				throw new InvalidInputException("Vertex mit negativem Wert eingefuegt: "+vertex.getDist()+" Node:"+vertex.node.getUID());
			}
			else{
				nodes.add(vertex); //neuer Knoten wird hinten an die Liste drangehaengt
				sort(nodes.size()-1); //nun folgt richtiges einsortieren
			}
		}else{
			throw new EmptyInputException("Leerer Vertex uebergeben");
		}
	}


	/**
	 * Loescht das erste Element der Schlange und gibt es zurueck
	 * <p>
	 * Gemass der Definition von Min-Heaps wird hierbei das erste Element durch das letzte Element ersetzt.
	 * Diese wird anschliessend in die richtige Position nach hinten verschoben.
	 * 
	 * @return Das erste Element der Schlange.
	 */
	public Vertex extractMin() {
		Vertex min = nodes.get(0);
		Vertex last = nodes.remove(nodes.size()-1);
		
		if (!nodes.isEmpty()){
			nodes.set(0,last);
			sortUp(0);
		}
		return min;
	}
	
	/**
	 * Signalisiert der Warteschlange, dass das uebergebene Element aktualisiert wurde.
	 * Hier wird nicht das Element selber veraendert, da je nach Anwendung der PrQueue (Dijkstra.neighbourhood/Query/HHierarchy.computeTree)
	 * die unterschiedlichsten Eigenschaften veraendert werden.
	 * <p>
	 *  
	 * @param updatedNode Die Node die kuerzlich veraendert wurde und deren Position in der Warteschlange ueberprueft werden soll.
	 * @throws Exception Exception wird geworfen falls der Vertex leer ist. ANmerkung: Falls der Vertex nicht in der Schlange ist,
	 * wird keine Exception geworfen, aber die Reihenfolge in der Schlange aendert sich ebenfalls nicht.
	 */
	public void update(Vertex updatedNode) throws Exception{
		if (updatedNode == null) throw new EmptyInputException("Leerer Pointer");
		int i=0;
		Iterator<Vertex> iter = nodes.iterator();
		while (iter.hasNext()){
			if (updatedNode == iter.next()) break;
			i++;
		}
		if ( i == nodes.size()) return;
			
		sort(i);
		
	}
	
	/**
	 * Gibt die Anzahl der Eelemente zurueck
	 */
	public int getSize() {
		return nodes.size();
	}
	
	/**
	 * Gibt die kleinste Distanz zurueck, ohne das jeweilige Element zu loeschen.
	 * Falls die Schlange leer ist, wird Long.MAX_VALUE zurueckgegeben. 
	 */
	public long getMinValue() {
		if(nodes.size() == 0){
			return Long.MAX_VALUE;
		}
		return nodes.get(0).getDist();
	}
	
	/** Gibt true zurueck, falls es einen Vertex mit State Active gibt, sonst false.
	 */
	public boolean containsActive(){
		Iterator<Vertex> elements = nodes.iterator();
		while(elements.hasNext()){
			Vertex current = elements.next();
			if (current.getState() == State.Active){
				return true;
			}
		}
		return false;
	}
}
