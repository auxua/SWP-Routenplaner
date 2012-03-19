package main;
import graphenbib.GPSCoordinate;
import graphenbib.StreetType;

/**
 * Der Zweck dieser Klasse ist die Information, die zum Zeichnen eines Strassenstueckes benoetigt
 * werden, zu kapseln.
 */
public class Street2Draw {
	private float startLongitude;
	private float startLatitude;
	private float endLongitude;
	private float endLatitude;
	boolean oneWay;
	String label;
	private StreetType streetType;
	
	public Street2Draw(GPSCoordinate gps1, GPSCoordinate gps2, boolean oneWay,
			String label, StreetType streetType) {
		this.startLongitude = gps1.getLongitude();
		this.startLatitude = gps1.getLatitude();
		this.endLongitude = gps2.getLongitude();
		this.endLatitude = gps2.getLatitude();
		this.oneWay = oneWay;
		this.label = label;
		this.streetType = streetType;
		
		markASCIIOneway();
	}

	/**
	 * @return the startLongitude
	 */
	public float getStartLongitude() {
		return startLongitude;
	}
	
	/**
	 * @return the startLatitude
	 */
	public float getStartLatitude() {
		return startLatitude;
	}
	
	/**
	 * @return the endLongitude
	 */
	public float getEndLongitude() {
		return endLongitude;
	}
	
	/**
	 * @return the endLatitude
	 */
	public float getEndLatitude() {
		return endLatitude;
	}
	
	/**
	 * @return the oneWay
	 */
	public boolean isOneWay() {
		return oneWay;
	}
	
	/**
	 * @return the label
	 */
	public String getLabel() {
		return label;
	}
	
	/**
	 * @return the streetType
	 */
	public StreetType getStreetType() {
		return streetType;
	}
	
	private void markASCIIOneway(){
		if(oneWay){
			if (startLongitude <= endLongitude){
				label = ">> "+label+" >>";
			}else{
				label = "<< "+label+" <<";
			}
		}
	}
}
