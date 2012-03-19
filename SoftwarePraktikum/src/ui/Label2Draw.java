package ui;

import java.awt.Point;

/**
 * Klasse, die alle zum zeichnen benoetigten Informationen eines Labels enthaelt
 */
public class Label2Draw {

	private final String name;	//Strassenname
	private Point start;		//Startpunkt einer Strassenkante
	private Point dest;			//Endpunkt einer Strassenkante

	/**
	 * 
	 * @param name	Name
	 * @param start	Startpunkt einer Strassenkante
	 * @param dest 	Endpunkt einer Strassenkante
	 */
	public Label2Draw(String name, Point start, Point dest){
		this.name = name;
		this.start = start;
		this.dest = dest;
	}

	/**
	 * Setze Koordinaten des Startpunktes, der Strecke, auf der das Label gezeichnet werden soll
	 * @param start
	 */
	public void setStart(Point start) {
		this.start = start;
	}

	/**
	 * Setze Koordinaten des Endpunktes, der Strecke, auf der das Label gezeichnet werden soll
	 * @param dest
	 */
	public void setDest(Point dest) {
		this.dest = dest;
	}
	
	/**
	 * @return Euklidische Distanz der Strecke zwischen Start- und Endpunkt
	 */
	public double getDist(){
		return Math.sqrt(Math.pow(dest.getX()-start.getX(), 2) + Math.pow(dest.getY()-start.getY(), 2));
	}

	
	//Getter- Methoden:
	public Point getStart() {
		return start;
	}

	public Point getDest() {
		return dest;
	}
	
	public String getName() {
		return name;
	}
}
