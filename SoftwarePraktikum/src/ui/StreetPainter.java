package ui;

import graphenbib.StreetType;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.util.HashMap;

import main.Config;

/**
 * Dient zum Zeichnen einer Strassenkante in einem bestimmten Stil
 * (z.B. Motorway sieht anders aus als Living Street)
 */
public class StreetPainter {

	private static final int streetThickness = 80;	//Faktor, mit dem relative Strassendicke eines Strassentyps multipliziert wird
	public static final int getStreetthickness() {
		return streetThickness;
	}

	private final HashMap<StreetType,StreetPaintArgs> args = 
		new HashMap<StreetType,StreetPaintArgs>(); //Speichert Zeichenargumente fuer jeden Strassentyp

	/**
	 * 
	 */
	public StreetPainter(){
		initArgs();
	}

	/**
	 * @param p1 Startpunkt der Strassenkante
	 * @param p2 Endpunkt der Strassenkante
	 * @param streetType Strassentyp der Strassenkante
	 * @param g Graphics-Objekt, welches die Strassenkante zeichnet
	 * @param zoomLvl ZoomLvl(element{0,1,2}), in dem die Karte aktuell angezeigt wird
	 * 
	 * Methode, die eine Strassenkante zeichnet
	 */
	public void paintStreet(int[] p1, int[] p2, StreetType streetType, Graphics2D g,int zoomLvl){
		final StreetPaintArgs tmp = args.get(streetType);
		g.setColor(tmp.color);
		final float thick = tmp.width*streetThickness+zoomLvl*500;
		g.setStroke(new BasicStroke(thick,BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND));
		g.drawLine(p1[0],p1[1],p2[0],p2[1]);
		g.fillOval((int)(p1[0]-thick/2), (int)(p1[1]-thick/2), (int)thick, (int)thick);
		g.fillOval((int)(p2[0]-thick/2), (int)(p2[1]-thick/2), (int)thick, (int)thick);
	}

	/**
	 * initialisiert Die HashMap args, welche zu jedem Strassentyp die Zeichenvorgaben enthaelt
	 */
	private void initArgs(){
		args.put(StreetType.MOTORWAY,		new StreetPaintArgs(1.8f,Config.motorwayColor));
		args.put(StreetType.TRUNK,			new StreetPaintArgs(1.6f,Config.trunkColor));
		args.put(StreetType.PRIMARY,		new StreetPaintArgs(1.6f,Config.primaryColor));
		args.put(StreetType.SECONDARY,		new StreetPaintArgs(1.5f,Config.secondaryColor));
		args.put(StreetType.TERTIARY,		new StreetPaintArgs(1.4f,Config.tertiaryColor));
		args.put(StreetType.RESIDENTIAL,	new StreetPaintArgs(1.3f,Config.residentialColor));
		args.put(StreetType.LIVING_STREET,	new StreetPaintArgs(1.3f,Config.livingStreetColor));
		args.put(StreetType.ROAD,			new StreetPaintArgs(1.3f,Config.roadColor));
		args.put(StreetType.SHORTESTPATH,	new StreetPaintArgs(2.0f,Config.shortestPathColor));
		args.put(StreetType.UNKNOWN,		new StreetPaintArgs(0.1f,Config.unknownColor));
	}

	/**
	 * Interne Klasse, dient zum Speichern der Farbe und Breite eines jeweiligen StreetTypes
	 */
	class StreetPaintArgs{
		protected float width;	// relative Breite
		protected Color color;	// Farbe

		/**
		 * 
		 * @param w Breite einer Strasse
		 * @param col Farbe einer Strasse
		 */
		public StreetPaintArgs(float w, Color col) {
			width = w;
			color = col;
		}
	}
}
