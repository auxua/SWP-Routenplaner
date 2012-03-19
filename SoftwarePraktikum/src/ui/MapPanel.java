package ui;

import graphenbib.GPSCoordinate;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Iterator;

import javax.swing.JPanel;

import main.Config;
import main.Logger;
import main.Path2Draw;
import main.Street2Draw;
import main.Viewport;
import main.ViewportCallback;

/**
 * JPanel, in dem die Karte und darauf berechnete Routen angezeigt werden
 *
 */

@SuppressWarnings("serial")
public class MapPanel extends JPanel{
	private float worldX=0;	//lon	 (0/0) des Panels gemapped auf "GPS" koordiante
	private float worldY=0;	//lat

	private int zoomCount; 	//Counter, der zaehlt, wie oft herausgezoomt wurde. ++bei zoomOut(), -- bei zoomIn()
	
//	private Image startFlag = Toolkit.getDefaultToolkit().getImage("img"+File.separatorChar+"start.gif");
//	private Image destFlag  = Toolkit.getDefaultToolkit().getImage("img"+File.pathSeparatorChar+"ziel.gif");
//	private int imgSize = 75;   		//Startgroesse der Flaggen

	private final TransformationManager trans = new TransformationManager(); //Transformation fuer das Grafikobjekt
	private Viewport viewport;	//viewport, zum updaten der Tiles

	private final MainFrame parentFrame; // Frame, das die GUI verwaltet
	final float worldLocalFactor = 1000000f; //Faktor zum Mappen von GLobalen zu Lokalen Koordinaten (oder umgekehrt)
	private static Logger logger = Logger.getInstance();
	private Path2Draw path= null;    // Schnellster Weg, der zuletzt berechnet werden sollte
	private int fontSize = Config.fontSize;      //Schriftgroesse der Labels
	private Font font = new Font(Config.fontName, Font.BOLD, fontSize); //Schriftart der Labels

	private RepaintWorker repWork = null;   //Zeichnet aktuellen Kartenausschnitt
	private BufferedImage buffer;			//Verweis auf den Buffer, auf dem gezeichnet wird

	/**
	 * 
	 * @param par Referenz auf das Hauptfenster
	 */
	public MapPanel(MainFrame par){
		parentFrame = par;
		zoomCount = 0;
		addComponentListener(new ComponentAdapter() {			//Saemtliche Listener hinzufuegen
			@Override
			public void componentResized(ComponentEvent e) {
				createBuffer();
			}
			@Override
			public void componentShown(ComponentEvent e) {
				createBuffer();
			}
		});
		final ClickNDragListener cnd = new ClickNDragListener(parentFrame);
		addMouseListener(cnd);
		addMouseWheelListener(cnd);
		addMouseMotionListener(cnd);

		//final File defaultFile = new File("testdateien/aachen.osm");		//Defaultfile preprocessen und oeffnen. 
		final File defaultFile = new File(main.Config.defaultFileString);		//Defaultfile preprocessen und oeffnen.
		
		 //Falls sich am Preprocessing etwas aendert, ist so zumindest diese Datei immer up to date:
				try {
					main.MainPreProc.main(defaultFile);
				} catch (Exception e1) {
					e1.printStackTrace();
				}
		final String dir = defaultFile.getPath()+".mapfiles"+File.separatorChar+"ProcessedTilesConfig.tiles"; //ermittle aus .osm die preprocesste Datei
		final File deftiles = new File(dir);
		openMapFile(deftiles); //oeffne preprocesste Datei des defaultFiles
	}

	/**
	 * erstelle neuen Buffer
	 */
	private void createBuffer() {
		if (getWidth() > 0 && getHeight() > 0) {
			buffer = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
			repaintAllStuff();
		}
	}

	/**
	 * Erstellt ein neues Viewport Objekt mit einer Datei als Parameter. Die Datei ist eine der Dateien im Zielverzeichnis
	 * @param f Dateiname der .tiles Datei vom Ordner, der geoeffnet werden soll
	 */
	public void openMapFile(File f){
		try{
			viewport = new Viewport(f);
			parentFrame.setTitle("SWP - Routenplaner - "+f.getAbsolutePath());
			worldX =  viewport.getPOI().getLongitude();		//Setze Startpunkt des angezeigten Kartenbereichs
			worldY =  viewport.getPOI().getLatitude();
			initZoom();	
			createBuffer();
			logger.log("MapPanel",worldX+" "+worldY);
		}catch(final Exception e){e.printStackTrace();}
	}

	/**
	 * Zoomt zu einer bestimmten entfernung heraus. 
	 * Sonst wuerde bei jedem Laden einer Karte die Zoomstufe der Karte davror angezeigt
	 */
	private void initZoom() {
		while(zoomCount!=15){
			if(zoomCount<15) zoomOut();
			if(zoomCount>15) zoomIn();
		}
	}

	/**
	 * Verschiebe Karte um amount nach oben
	 * @param amount
	 */
	public void goUp(float amount){
		trans.translate(0.0, amount);
		repaintAllStuff();
	}

	/**
	 * Verschiebe Karte um amount nach rechts
	 * @param amount
	 */
	public void goRight(float amount){
		trans.translate(-amount,0.0);
		repaintAllStuff();
	}

	/**
	 * Verschiebe Karte um amount nach unten
	 * @param amount
	 */
	public void goDown(float amount){
		trans.translate(0.0, -amount);
		repaintAllStuff();
	}

	/**
	 * Verschiebe Karte um amount nach links
	 * @param amount
	 */
	public void goLeft(float amount){
		trans.translate(amount,0.0);
		repaintAllStuff();
	}

	/**
	 * zoome heraus
	 */
	public void zoomOut(){
		if(zoomCount<110){			//Gegen Exception: InvalidGPSCoordinate
			trans.scaleInMid(this.getBounds().width,this.getBounds().height,main.Config.zoomConst);
			zoomCount++;
			main.Logger.getInstance().log("MapPanel", zoomCount);
			fontSize = new Integer((int)(fontSize * (1/main.Config.zoomConst))); //Passe Scrhiftgroesseso an, dass Labels immer gleich gross sind
			font = new Font("arial narrow", Font.BOLD, fontSize);
			changeZoomLevel();
			repaintAllStuff();
		}
	}

	/**
	 * zoome herein
	 */
	public void zoomIn(){
		if(zoomCount>0){		//Vermeiden von Fehlern
			trans.scaleInMid(this.getBounds().width ,this.getBounds().height,1/main.Config.zoomConst); 
			zoomCount--;
			fontSize = ((int)(fontSize * main.Config.zoomConst)) + 1; //Passe Scrhiftgroesseso an, dass Labels immer gleich gross sind
			font = new Font("arial narrow", Font.BOLD, fontSize);
			changeZoomLevel();
			repaintAllStuff();
		}
	}

	/**
	 * Methode zum Anpassen des ZoomLvls im {@link Viewport}, damit richtige Tiles geladen werden
	 */
	private void changeZoomLevel(){
		if(40>=zoomCount)
			viewport.setZoomLevel(0);
		else if(60>=zoomCount&&zoomCount>40)
			viewport.setZoomLevel(1);
		else
			viewport.setZoomLevel(2);
	}

	/**
	 * Zeichne das BufferImage
	 * @param g Graphics Objekt des MapPanels
	 */
	@Override
	public void paintComponent(Graphics g){
		if(buffer == null) return;

		final Graphics2D g2d = (Graphics2D) g;
		synchronized (buffer) {
			assert(buffer != null);
			g2d.drawImage(buffer, 0, 0, null);
		}
	}

	/**
	 * aktualisiere die zu zeichnenden Strassen, zeichne neu
	 */
	public void repaintAllStuff(){
		final MapPanel self = this;
		final float [] topLeft = localToWorld(new int[] {0,0});	//linkere Obere Bildschirmecke in World- Coord
		final float [] lowerRight = localToWorld(new int[] {this.getBounds().width,this.getBounds().height}); //rechte untere Bildschirmecke in World- Coord
		try{
			viewport.update(	new GPSCoordinate(topLeft[1],topLeft[0]),
					new GPSCoordinate(lowerRight[1],lowerRight[0]),
					new ViewportCallback(){
				@Override
				public void updateComplete(Iterator<Street2Draw> it, Iterator<Street2Draw> shortestPathIt) {
					if (repWork != null)
						repWork.cancel();

					repWork = new RepaintWorker(buffer, it, viewport.getShortestPath(), shortestPathIt, self, viewport.getZoomLevel());
					repWork.start();
				}

			}
			);
			path = viewport.getShortestPath();
			
		} catch(final Exception e){ e.printStackTrace();}
	}
	
	

	/**
	 * wandelt den Punkt p von screen coordinates in world coordinates um
	 * @param p ScreenCoordinates
	 * @return WorldCoordinates
	 */
	public float[] localToWorld(int [] p){
		final float [] res = new float[2];
		final AffineTransform inverse = trans.getInverse();
		res[0] = worldX + ((float) inverse.getScaleX() * p[0] + (float) inverse.getTranslateX()) / worldLocalFactor;
		res[1] = worldY - ((float) inverse.getScaleY() * p[1] + (float) inverse.getTranslateY()) / worldLocalFactor;
		return res;
	}

	//Getter-Methoden:
	public Viewport getViewport() {
		return viewport;
	}

	public Path2Draw getPath() {
		return path;
	}

	public TransformationManager getTrans() {
		return trans;
	}

	public float getWorldX() {
		return worldX;
	}

	public float getWorldY() {
		return worldY;
	}

	public Font getFont(){
		return font;
	}

}
