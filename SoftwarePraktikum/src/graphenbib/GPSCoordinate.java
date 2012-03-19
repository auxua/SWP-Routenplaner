package graphenbib;
import graphexceptions.InvalidGPSCoordinateException;

import java.io.Serializable;

import main.Config;


public class GPSCoordinate implements GPS,Serializable
{
	private static final long serialVersionUID = 5563090485913454340L;
	private float longitude;
	private float latitude;

	/**
	 * Konstruktor zum Erzeugen einer GPS-Koordinate.
	 * @param latitude Breitengrad der Koordinate.
	 * @param longitude Laengengrad der Koordinate.
	 * @throws InvalidGPSCoordinateException Falls die uebergebene Longitude ausserhalb
	 * vom Intervall [-180,180] oder die Latitude ausserhalb vom Intervall [-90,90] liegt.
	 */
	public GPSCoordinate(float latitude, float longitude) throws InvalidGPSCoordinateException
	{
		if ((longitude<=180) && (longitude>=-180) && (latitude >=-90) && (latitude<=90)) {
			this.longitude = longitude;
			this.latitude = latitude;
		} else {
			throw(new InvalidGPSCoordinateException("GPS Koordinate nicht im gueltigen Wertebereich. ("+longitude+","+latitude+")"));
		}
	}
	
	/** Gibt die geographische Laenge zurueck. Sie liegt im Intervall [-180,180].
	 * 
	 */
	public float getLongitude()
	{
		return longitude;
	}
	/** Gibt die geographische Breite zurueck. Sie liegt im Intervall [-90,90].
	 */
	public float getLatitude()
	{
		return latitude;
	}
	
	
	public String toString(){
		return("GPS: Longitude "+longitude+" Latitude "+latitude);
	}
	
	/**
	 * Funktion zur Berechnung der Distanz in Dezimetern zwischen zwei GPS-Koordinaten.
	 * @param b GPS-Koordinate zu der die Distanz berechnet werden soll.
	 * @return Die Distanz als Integer in Dezimetern 
	 *   zwischen der gegebenen GPS-Koordinate und der GPS-Koordinate b.
	 * @throws InvalidGPSCoordinateException Falls uebergebene GPS-Koordinate Null.
	 */
	public int distanceTo(GPSCoordinate b) throws InvalidGPSCoordinateException{
		if(b==null) {
			throw new InvalidGPSCoordinateException("Fehler in Methode distanceTo: " +
					"Ubergebene GPS-Koordinate ist Null");
		}
		GPSCoordinate a =this;
		return (int) (Math.acos(Math.sin(b.getLatitude()/180*Math.PI)*Math.sin(a.getLatitude()/180*Math.PI) + Math.cos(b.getLatitude()/180*Math.PI)*Math.cos(a.getLatitude()/180*Math.PI)*Math.cos(b.getLongitude()/180*Math.PI-a.getLongitude()/180*Math.PI) ) * Config.erdRadius);
	}
}