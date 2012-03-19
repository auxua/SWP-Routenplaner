package ui;

import graphenbib.StreetType;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Iterator;

import main.Config;
import main.Path2Draw;
import main.Street2Draw;

/**
 * Thread, in dem Karte, berechnete Route sowie Strassennamen auf einen Buffer gezeichnet werden
 */
public class RepaintWorker implements Runnable {
	final float worldLocalFactor = 1000000f;//Faktor zum Mappen von GLobalen zu Lokalen Koordinaten (oder umgekehrt)
	private int guiZoomLvl = 0;				// zoomLevel in der GUI, siehe Viewport
	final int flagFactor = 15;				//Groessenanpassung der Flaggen
	private int count;						//Anzahl bereits geyeichneter Strassen
	private volatile BufferedImage buffer;	//Buffer, auf den gezeichnet wird
	private final MapPanel mapP;			//Referenz auf Hauptfenster
	private final Iterator<Street2Draw> streets2DrawIt;	//Iterator fuer Strassen
	private final Iterator<Street2Draw> shortestPathIt;	//Iterator fuer berechnete Route
	private LabelRenderer labelRend;					//Zur Verwaltung der Labels
	private final StreetPainter streetP;				//Zum Zeichnen von Strassenkanten
	private final Path2Draw path; 
	
	private final Image startFlag = Toolkit.getDefaultToolkit().getImage("img"+File.separatorChar+"start.gif");
	private final Image destFlag  = Toolkit.getDefaultToolkit().getImage("img"+File.separatorChar+"ziel.gif");
	private final int imgSize = 75;   		//Startgroesse der Flaggen

	/**
	 * 
	 * @param buffer BufferedImage, auf das gezeichnet werden soll
	 * @param it Iterator ueber Strassen
	 * @param shortestPathIt Iterator ueber berechnete Route
	 * @param mapP	Referenz auf Hauptfenster
	 * @param guiZoomLvl zoomLevel, in dem die Karte momentan engezeigt wird
	 */
	public RepaintWorker(BufferedImage buffer, Iterator<Street2Draw> it, Path2Draw shortestPath, Iterator<Street2Draw> shortestPathIt, MapPanel mapP,int guiZoomLvl){
	//public RepaintWorker(BufferedImage buffer, Iterator<Street2Draw> it, Path2Draw shortestPath, MapPanel mapP,int guiZoomLvl){
		this.buffer = buffer;
		streets2DrawIt = it;
		this.mapP = mapP;
		this.path = shortestPath;
		this.shortestPathIt = shortestPathIt;
		this.guiZoomLvl = guiZoomLvl;
		streetP = new StreetPainter();
	}

	/**
	 * Startet den Thread
	 */
	public void start() {
		(new Thread(this)).start();
	}

	/**
	 * bricht den aktuellen Zeichenvorgang ab. Der GarbageCollector entfernt dann verwaiste Threads
	 */
	public void cancel() {
		if (buffer == null)
			return;
		synchronized (buffer) {
			buffer = null;
			assert( buffer == null );
		}
	}

	/**
	 * Methode, in dem der Zeichenvorgang stattfindet
	 */
	@Override
	public void run() {
		Graphics2D g2d = null;
		final AffineTransform trans = mapP.getTrans().getAffineTransform();

		if (buffer == null ) return;
		synchronized(buffer){
			if (buffer == null) return;

			g2d = (Graphics2D) buffer.getGraphics();
			g2d.setColor(Color.LIGHT_GRAY);
			g2d.fillRect(0, 0, mapP.getWidth(), mapP.getHeight());
			g2d.setFont(mapP.getFont());
			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
			g2d.transform(trans);
			labelRend = new LabelRenderer(g2d, this);
		}
		if (buffer == null) return;
		try {
			drawStreets(g2d); //Zeichne Strassen
		} catch (final Exception e) {
			e.printStackTrace();
		}
		drawPath(g2d);		//Zeichne Route
		labelRend.drawLabels(g2d);	//Zeichne Labels
	}

	//-------------------------RENDERING LOGIC------------------------------------------------------------------------------------
	private void drawStreets(Graphics2D g2d) throws Exception{
		count = Config.waitCount;
		while(streets2DrawIt.hasNext()){				//Iteriere ueber Strassen um diese zu zeichnen
			final Street2Draw temp = streets2DrawIt.next();	//temporaere Strasse, die in einer Iteration gezeichnet wird
			final float [] startCoord = {temp.getStartLongitude(), temp.getStartLatitude()}; //GPS Startkoordinaten der aktuellen Strasse
			final float [] endCoord   = {temp.getEndLongitude(), temp.getEndLatitude()}; //GPS Endkoordinaten der aktuellen Strasse

			final int [] localStartCoords = worldToLocal(startCoord);	//lokale Koordinaten vom Startpunkt der Strassenkante
			final int [] localEndCoords = worldToLocal(endCoord);  		//lokale Koordinaten vom Endpunkt der Strassenkante

			final int[] upperLeft = worldToLocal(mapP.localToWorld(new int[] {0,0}));  //lokale Koordinaten der oberen linken Bildschirmecke
			final int[] lowerRight = worldToLocal(mapP.localToWorld(new int[] {mapP.getWidth(),mapP.getHeight()})); //lokale Koordinaten der unteren rechten Bildschirmecke
			
			//Wenn Start oder Ziel innerhalb des angezeigten Kartenausschnitts
			if(( localStartCoords[0] >= upperLeft[0] && localStartCoords[1] >= upperLeft[1] && localStartCoords[0] <= lowerRight[0] && localStartCoords[1] <= lowerRight[1] ) 
					|| ( localEndCoords[0] >=upperLeft[0] && localEndCoords[1] >=  upperLeft[1] && localEndCoords[0] <=lowerRight[0] && localEndCoords[1] <= lowerRight[1])){
				
				labelRend.addLabel(new Label2Draw(temp.getLabel(), 		//Fuege Label fuer aktuelle Strassenkante hinzu
						new Point(localStartCoords[0],localStartCoords[1]), 
						new Point(localEndCoords[0],localEndCoords[1])), g2d, guiZoomLvl);

				if (buffer == null) return;
				synchronized (buffer){
					if (buffer == null) return;
					drawEdge(startCoord, endCoord , temp.getStreetType(), g2d);	//zeichne aktuelle Strasse
				}
			}

			//Jetzt ist die Chance fuer die anderen Threads gekommen
			count--;
			if ( count <= 0){
				synchronized (this) {
					mapP.repaint();
					try{
						wait(Config.time);
					} catch (final InterruptedException ex) {
						return;
					}
				}
				count = Config.waitCount;
			}
		}
		mapP.repaint();
	}

	/**
	 * Rechnet GPS Koordinaten in lokales Koordinatensystem um
	 * @param p GPS Koordinaten {long,lat}
	 * @return lokale Koordinaten {x,y}
	 */
	private int[] worldToLocal(float[] p){
		final float tempX = (worldLocalFactor * (p[0] - mapP.getWorldX()));
		final float tempY = (worldLocalFactor * (mapP.getWorldY() - p[1]));
		return new int[] {(int)tempX,(int)tempY};
	}

	/**
	 * Zeichnet eine Kante
	 * @param globalP1 GPS Koordinaten vom Startpunkt der Strassenkante
	 * @param globalP2 GPS Koordinaten vom Endpunkt der Strassenkante
	 * @param streetType Strassentyp der Kante
	 * @param g	Graphics2D Objekt, mit dem gezeichnet werden soll
	 */
	private void drawEdge(float[] globalP1, float[] globalP2,StreetType streetType, Graphics2D g){
		final int[] p1= worldToLocal(globalP1);
		final int[] p2 = worldToLocal(globalP2);
		streetP.paintStreet(p1, p2, streetType, g, guiZoomLvl);
	}

	/**
	 * Zeichne wenn eine Route berechnet wurde, sonst tue gar nichts
	 * @param g2d Graphics2D Objekt, mit dem gezeichnet werden soll
	 */
	private void drawPath(Graphics2D g2d){  
		if(shortestPathIt==null) 
			return;

		count = Config.waitCount;

		while(shortestPathIt.hasNext()){		//iteriere ueber Strassen der Route
			final Street2Draw temp = shortestPathIt.next();
			final float [] startCoord = {temp.getStartLongitude(), temp.getStartLatitude()}; // Startkoordinaten der aktuellen Strasse
			final float [] endCoord   = {temp.getEndLongitude(), temp.getEndLatitude()}; // Endkoordinaten der aktuellen Strasse

			if(buffer == null) return;
			synchronized (buffer) {
				if(buffer == null) return;
				drawEdge(startCoord, endCoord , graphenbib.StreetType.SHORTESTPATH, g2d);	//zeichne aktuelle Routenteil
			}

			//Chance fuer andere Threads
			if ( count <= 0){
				synchronized (this) {
					mapP.repaint();
					try{
						wait(Config.time);
					} catch (final InterruptedException ex) {
						return;
					}
				}
				count = Config.waitCount;
			}
			mapP.repaint();
		}
		if (path.getEndPos() !=null && path.getStartPos()!=null) {
			//if (path !=null) {
				final int thick = StreetPainter.getStreetthickness()+500*guiZoomLvl;
				//Male Fahnen - drecjkige Loesung, aber sollte schnell geug sein
				final float[] startCoord = { path.getStartPos().getLongitude(),	path.getStartPos().getLatitude() };
				final float[] endCoord = { path.getEndPos().getLongitude(),	path.getEndPos().getLatitude() };
				final int[] startPos = worldToLocal(startCoord);
				final int[] endPos = worldToLocal(endCoord);
				
				
				//g2d.drawImage(startFlag, startPos[0]-((flagFactor*imgSize)/2), startPos[1]-((flagFactor*imgSize)/2), imgSize*flagFactor, imgSize*flagFactor, null);
				g2d.drawImage(destFlag, endPos[0]-thick+50, endPos[1]-thick-50, imgSize*flagFactor, imgSize*(flagFactor), null);
				g2d.drawImage(startFlag, startPos[0]-thick+50, startPos[1]-thick-50, imgSize*flagFactor, imgSize*(flagFactor), null);
//				synchronized(this) {
//					mapP.repaint();
//					try{
//						wait(Constants.time);
//					} catch (final InterruptedException ex) {
//						return;
//					}
//				}
				mapP.repaint();
		}
		//mapP.repaint();
	}

	//Getter-Methoden:
	public BufferedImage getBuffer(){
		return buffer;
	}

	public MapPanel getMapP() {
		return mapP;
	}
}
