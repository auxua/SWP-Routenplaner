package graphenbib;

import graphexceptions.EmptyInputException;
import graphexceptions.InvalidGPSCoordinateException;
import graphexceptions.InvalidInputException;

import java.io.Serializable;

public class GPSRectangle implements Serializable{
	
	private static final long serialVersionUID = -1681037706953626396L;
	private GPS upperLeft;
	private GPS lowerRight;
	
	
	/**
	 * Dieser Konstruktor konstruiert ein GPSRectangle auf Basis existierender GPSCoordinates
	 */
	public GPSRectangle(GPS upperLeft, GPS lowerRight) throws EmptyInputException, InvalidInputException {
		//GPSCoordinate implementiert bereits einen Test, ob Koordinaten auf der Erde liegen
		//Pruefe nun, ob diese Ecken existieren
		if (upperLeft == null || lowerRight == null) {
			throw new EmptyInputException("Mindestens eine der GPS-Parameter war null");
		}
		
		//Pruefe, nun, ob diese Ecken ein Rechteck bezeichnen
		if ((upperLeft.getLatitude() <= lowerRight.getLatitude()) || (upperLeft.getLongitude() >= lowerRight.getLongitude())) {
			throw new InvalidInputException("Die uebergebenen Parameter entsprechen nciht einem zweidimensionalem Rechteck");
		}
		this.upperLeft = upperLeft;
		this.lowerRight = lowerRight;
	}
	
	/**
	 * Dieser Konstruktor konstruiert ein GPSRectangle auf Basis der Randkoordinaten die oben angefangen im Uhrzeigersinn angegeben werden
	 * Es werden Fehler aus dem den GPSCoordinate und dem anderen Konstruktor weitergeleitet
	 */
	public GPSRectangle(float top, float right, float bottom, float left) throws EmptyInputException, InvalidInputException, InvalidGPSCoordinateException {
		GPSCoordinate upLeft = new GPSCoordinate(top,left);
		GPSCoordinate lowRight = new GPSCoordinate(bottom,right);
		
		//Pruefe, nun, ob diese Ecken ein Rechteck bezeichnen
		if ((upLeft.getLatitude() <= lowRight.getLatitude()) || (upLeft.getLongitude() >= lowRight.getLongitude())) {
			throw new InvalidInputException("Die uebergebenen Parameter entsprechen nciht einem zweidimensionalem Rechteck");
		}
		this.upperLeft = upLeft;
		this.lowerRight = lowRight;
	}
	
	/**
	 * getter fuer oben-links - beachte: es wird das Objekt uebergeben und keine Konstante 
	 */
	public GPS getUpperLeft() {
		return this.upperLeft;
	}
	
	/**
	 * getter fuer unten-rechts - beachte: es wird das Objekt uebergeben und keine Konstante 
	 */
	public GPS getLowerRight() {
		return this.lowerRight;
	}
	
	/**
	 * Diese Methode gibt true zurueck, wenn Parameter in dem Rechteck liegt
	 */
	public boolean GPSInside(GPS gps) {
		return ((gps.getLongitude() >= this.upperLeft.getLongitude()) &&
				(gps.getLatitude() <= this.upperLeft.getLatitude()) &&
				(gps.getLongitude() <= this.lowerRight.getLongitude()) &&
				(gps.getLatitude() >= this.lowerRight.getLatitude()));
	}
	
	/**
	 * getter fuer die minimale Latituede
	 */
	public float getMinLat() {
		return lowerRight.getLatitude();
	}
	
	/**
	 * getter fuer die maximale Latituede
	 */
	public float getMaxLat() {
		return upperLeft.getLatitude();
	}
	
	/**
	 * getter fuer die minimale Longitude
	 */
	public float getMinLon() {
		return upperLeft.getLongitude();
	}
	
	/**
	 * getter fuer die maximale Longituede
	 */
	public float getMaxLon() {
		return lowerRight.getLongitude();
	}
}
