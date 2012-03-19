package ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Iterator;

import main.Config;

/**
 * Klasse, zum speichern von zu zeichnenden Labels. Wie zu benutzen?: Wird eine Strasse gezeichnet, so wird ein neues {@link ui.Label2Draw}
 * Objekt, mit den benoetigten und passenden Informationen der Strasse angelegt. Anschliessend wird die Methode {@link #addLabel}
 * mit dem Label2Draw Objekt aufgerufen, welche dann entscheidet, ob das Label gezeichnet werden soll.
 * Mithilfe der Methode {@link #drawLabels} koennen alle, im Labelrender gespeicherten Label2Draw, Objekte gezeichnet werden
 */
public class LabelRenderer {
	private final ArrayList<Label2Draw> labels = new ArrayList<Label2Draw>();	//zu zeichnende Label
	private final Font font; 													//font, in der die Label gezeichnet werden
	private final RepaintWorker repWorker; 										//Referenz auf das Repaintworker- Objekt, welches zum zeichnen zustaendig ist

	public LabelRenderer(Graphics2D g2d, RepaintWorker repWorker){
		font = g2d.getFont();
		this.repWorker = repWorker;
	}

	/**
	 * zeichnet alle Labels, die in der List "labels" enthalten sind
	 * @param g2d Graphics- Objekt, dass die labels zeichnen soll
	 */
	public void drawLabels(Graphics2D g2d){
		final Iterator<Label2Draw> it = labels.iterator(); //Iterator ueber alle zu zeichnenden Labels
		Label2Draw temp;
		while (it.hasNext()){	//zeichne alle Label
			temp = it.next();
			final double rotationDegree = Math.atan((temp.getDest().getY()-temp.getStart().getY())/(temp.getDest().getX()-temp.getStart().getX())) ; //Winkel zur Rotation des Strings
			final int shiftX = (int)(temp.getDist()/2-getStringLength(g2d.getFontRenderContext(), temp.getName())/2); //passe Labelposition auf Panel an
			final int shiftY = 30;

			if (temp.getStart().getX() > temp.getDest().getX()){ //4.Fall
				final Point help = temp.getStart();
				temp.setStart(temp.getDest());
				temp.setDest(help);
			}

			if(repWorker.getBuffer()!=null)
				synchronized(repWorker.getBuffer()){
					if(repWorker.getBuffer()!=null)
						g2d.translate(temp.getStart().getX(), temp.getStart().getY()); //Transliere Graphikobjekt zum Startpunkt
					g2d.rotate(rotationDegree); //rotiere, damit String auf Strasse

					g2d.setColor(Color.BLACK); //Farbe des Labels
					g2d.drawString(""+ temp.getName() , shiftX , shiftY );	//zeichne Label

					g2d.rotate(-rotationDegree); //rotiere zurueck
					g2d.translate(-temp.getStart().getX(), -temp.getStart().getY()); //transliere zurueck
				}

			//Warten, damit andere Threads arbeiten koennen:
			int count = Config.waitCount;
			if (count <= 0){
				synchronized (this) {
					repWorker.getMapP().repaint();
					try{
						wait(Config.time);
					} catch (final InterruptedException ex) {
						return;
					}
				}
				count = Config.waitCount;
			}
			repWorker.getMapP().repaint();
		}
	}

	/**
	 * Ueberprueft bei fuer ein gegebenes Label2Draw Objekt, ob der String, das es enthaelt zwischen die Grenzen( zwischen 2 Knoten) passt
	 * Dafuer wird auch die font des Grafik- Objekts gebracht um die font Eigenschaften zu erhalten
	 * @param toAdd das Label2Draw
	 * @param g2d das Grafikobjekt
	 */
	public void addLabel(Label2Draw toAdd, Graphics2D g2d, int lvl){
		if(toAdd.getDist() >= 1.2 * getStringLength(g2d.getFontRenderContext(), toAdd.getName()))
			labels.add(toAdd);
	}

	/**
	 * Berechnet die Laenge eines Texts
	 * @param context
	 * @param text
	 * @return Laenge von text mit dem benutzten font
	 */
	public double getStringLength(FontRenderContext context, String text){
		final Rectangle2D fontRectangle =  font.getStringBounds(text, context);
		return fontRectangle.getWidth();
	}
}
