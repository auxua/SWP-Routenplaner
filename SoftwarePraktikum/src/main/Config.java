package main;
import graphenbib.StreetType;

import java.awt.Color;
import java.io.File;

public class Config {
	/**
	 * Groesse der Nachbarschaft, wie im Paper
	 * TODO: Final machen
	 */
	public static int H=50; 
	//public static int H=2;
	
	/**
	 * Das DefaultFile beim Oeffnen
	 */
	public final static String defaultFileString = "testdateien"+File.separatorChar+"aachen.osm";
	
	/**
	 * Wird eine Kante eingefuegt, wobei von einem der beteiligten Knoten die Kantenlaenge nicht bekannt ist,
	 * wird hierfuer standardmaessig diese Laenge verwendet. Diese wird dann in der HierarchyMapGraph mittels
	 * der correctLength() Methode neu berechnet.
	 */
	public final static int initialTemporaryLengthForEdges=999999;
	
	/**
	 * 
	 */
	public static int maxHierarchyLayer=11;
	
	/**
	 * Erdumfang bei Programmstart initialisieren falls besonderer Ort
	 */
	//public static float erdRadius = 6378.137f;
	public final static int erdRadius = 63781370; //Radius is dm
	
	/**
	 * Seitenlaenge der Tiles
	 */
	public static int TileGroesse = 250000; //Length is dm
	//public static int TileGroesse = 2500; //Length is dm
	
	/**
	 * Maximal Groesse bis zu der eine Datei in eins eingelesen wird.
	 */
	public static int maxSingleFileSize = 100000000; //in Byte im MOment 100MB
	//public static int maxSingleFileSize = 4; //in Byte
	
	
	/**
	 *Mittels des Zoomlevels soll bestimmt werden, welche Strassen angezeigt werden.
	 *Die GUI soll anhand Fenstergroesse und Skalierungsfaktor entscheiden, welches Zoomlevel aktuell ist.
	 *Es gilt dabei folgende Konvention:
	 *zoomLevel==0: Alle Strassen werden angezeigt.
	 *zoomLevel==1: Nur Autobahnen, Trunk, Primary und Secondary werden angezeigt.
	 *zoomLevel==2: Nur Autobahnen, Trunk werden angezeigt
	 */
	public static int maxZoomLevel=2;
	
	/**
	 * Verschiebekonstante des Bildschirmausschnitts beim Klicken auf Translationsbuttons
	 */
	public static final int shiftConstant = 10;
	
	/**
	 * Zoomkonstante beim herein- oder herauszoomen in das MapPanel
	 */
	public static final float zoomConst = 0.9f;

	/**
	 * fuer RepaintWorker,
	 * Anzahl an Strassen die in einem Zug gezeichnet werden
	 */
	public static final int waitCount = 2000;

	/**
	 * fuer RepaintWorker
	 * Zeit in ms, die RepaintWorker anderen Threads zum Arbeiten laesst
	 */
	public static final long time = 10;
	
	
	/**
	 * Name der gewuenschten Schriftart bei Strassenlabels und dazugeoerige Schriftgroesse
	 */
	public static String fontName = "arial narrow";
	public static final int fontSize = 20;
	
	
	/**
	 * Dieser Boolean gibt an, ob kuerzeste Wege oder schnellste Wege berechnet werden sollen.
	 * Ist fastestPathMode true, dann werden mittels der unten angegeben Durchschnittsgeschwindigkeiten
	 * fuer jede Kante eine Zeit berechnet, die dann bei der Funktion getWeight() zurueckgegeben
	 * wird. Ansonsten gibt die Methode getWeight() nur die Laenge zurueck und damit wird
	 * der kuerzeste Weg berechnet.
	 */
	public static boolean fastestPathMode=true;
	
	/**
	 * Durchschnittsgeschwindigkeiten in km/h fuer die verschiedenen Strassentypen. Um
	 * Typecasts nach long bei der Berechnung des Berichts zu vermeiden, werden sie direkt als long
	 * angegeben.
	 * TODO: Richtig setzen.
	 */
	public static long averageSpeedMotorway=100;
	public static long averageSpeedTrunk=100;
	public static long averageSpeedPrimary=80;
	public static long averageSpeedSecondary=50;
	public static long averageSpeedTertiary=30;
	public static long averageSpeedResidential=30;
	public static long averageSpeedLivingStreet=10;
	public static long averageSpeedRoad=50;
	public static long averageSpeedUnknown=50;
	
	
	public static long getAverageSpeed(StreetType streetType) {
		long averageSpeed;
		switch (streetType) {
			case MOTORWAY:
				averageSpeed=Config.averageSpeedMotorway;
				break;
			case TRUNK:
				averageSpeed=Config.averageSpeedTrunk;
				break;
			case PRIMARY:
				averageSpeed=Config.averageSpeedPrimary;
				break;
			case SECONDARY:
				averageSpeed=Config.averageSpeedSecondary;
				break;
			case RESIDENTIAL:
				averageSpeed=Config.averageSpeedResidential;
				break;
			case LIVING_STREET:
				averageSpeed=Config.averageSpeedLivingStreet;
				break;
			case ROAD:
				averageSpeed=Config.averageSpeedRoad;
				break;
			case UNKNOWN:
				averageSpeed=Config.averageSpeedUnknown;
				break;
			default: 
				averageSpeed=Config.averageSpeedUnknown;
				break;
		}
		return averageSpeed;
	}
	
	/**
	 * Methode um den Zeit-Strecke-Faktor fuer einen gegebenen Strassentyp zu erhalten.
	 * Multipliziert man dann einen Weglaenge in Dezimetern mit diesem Faktor, so erhaelt
	 * man die benoetige Zeit in Sekunden fuer diesen Wegabschnitt.
	 * @param streetType Der Strassentyp des betrachteten Wegabschnitt.
	 * @return Der Faktor in Sekunden/Dezimeter fuer Wegabschnitt.
	 */
	public static long getSpeedFactor(StreetType streetType) {
		long averageSpeed=Config.getAverageSpeed(streetType);
		//avgSpeed * 10 000 ist die Geschwindikeit in dezimetern/Stunden
		//(avgSpeed * 10 000)/3600 ist die Geschwindikeit in dezimetern/Sekunde
		//Unterer Wert ist gerade der Kehrwert hiervon.
		return Math.round(3600./(averageSpeed));
	}
	
	/**
	 * Parameter für die farbliche Darstellung der Straßentypen
	 */
	public static final Color motorwayColor 	= new Color( 10, 10,200);
	public static final Color trunkColor		= new Color( 20, 20,200);
	public static final Color primaryColor 		= new Color(230,150,  0);
	public static final Color secondaryColor 	= new Color(240,160, 30);
	public static final Color tertiaryColor 	= new Color(240,240,140);
	public static final Color roadColor		 	= new Color(255,255,255);
	public static final Color residentialColor 	= new Color(230,230,230);
	public static final Color livingStreetColor = new Color(220,220,220);
	public static final Color shortestPathColor = new Color(255,  0,  0);
	public static final Color unknownColor 		= new Color(  0,  0,  0);
	
}
