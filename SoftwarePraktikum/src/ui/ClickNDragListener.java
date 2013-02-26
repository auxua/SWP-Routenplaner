package ui;

import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

/**
 * Diese Klasse handhabt das Verschieben, der Karte im MapPanel, welches durch Gedrueckthalten der linken Maustaste
 * herbeigefuehrt wird
 */
public class ClickNDragListener extends MouseAdapter implements MouseMotionListener{
	
	private final MainFrame parent;	//Referenz auf Hauptfenster
	private float[] vec = new float[2]; //beinhaltet den Vektor zum Verschieben

	/**
	 * 
	 * @param par Referenz auf Hauptfenster
	 */
	public ClickNDragListener(MainFrame par){
		parent = par;
	}

	/**
	 * Maus wurde gedrueckt, setze Startwert fuer Verschiebungsvektor
	 * @param me MouseEvent, das dafuer noetig ist
	 */
	@Override
	public void mousePressed(MouseEvent me) {
		vec[0] = me.getX();
		vec[1] = me.getY();
	}

	/**
	 * Methode um Karte, ueber ziehen auf dem Panel, zu verschieben.
	 * Veraendert auch sen Mauscursor 
	 * @param me MouseEvent, das dafuer noetig ist
	 */
	@Override
	public void mouseDragged(MouseEvent me) {
		parent.getMapPanel().setCursor(new Cursor(Cursor.MOVE_CURSOR));
		final float[] newPoint = {me.getX(),me.getY()};	//aktuelle Position der Maus

		vec[0] -= newPoint[0];		//erstelle Verschiebungsvektor
		vec[1] -= newPoint[1];

		//verschiebe Karte
		if(vec[0] < 0) parent.getMapPanel().goLeft (Math.abs(vec[0]));
		if(vec[0] > 0) parent.getMapPanel().goRight(Math.abs(vec[0]));
		if(vec[1] > 0) parent.getMapPanel().goDown (Math.abs(vec[1]));
		if(vec[1] < 0) parent.getMapPanel().goUp   (Math.abs(vec[1]));
		
		vec = newPoint;	//Endpunkt des alten Vektors ist Startpunkt des neuen Vektors
	}

	/**
	 * Maus wird nicht mehr gedrueckt, Verschieben abgeschlossen, also wieder normaler Cursor
	 * @param me MouseEvent, das dafuer noetig ist
	 */
	@Override
	public void mouseReleased(MouseEvent me){
		parent.getMapPanel().setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
	}


	/**
	 * Mausradbewegung zum zoomen
	 * @param mwe MouseWheelEvent, das dafuer noetig ist
	 */
	@Override
	public void mouseWheelMoved(MouseWheelEvent mwe) {
		if(mwe.getUnitsToScroll()>0)
			parent.getMapPanel().zoomOut();
		else
			parent.getMapPanel().zoomIn();
	}
	
	@Override
	public void mouseClicked(MouseEvent me) {
		if(me.getButton()==MouseEvent.BUTTON3){
			JPopupMenu popup = new JPopupMenu();
			JMenuItem selStart = new JMenuItem("Als Start setzen");
			JMenuItem selDest = new JMenuItem("Als Ziel setzen");
			selStart.addActionListener(new PopUpListener(parent, me.getX(), me.getY(), "Start"));
			selDest.addActionListener(new PopUpListener(parent, me.getX(), me.getY(), "Dest"));
			popup.add(selStart);
			popup.add(selDest);
			popup.show( me.getComponent(), me.getX(), me.getY() );
			}
	}
	
	class PopUpListener implements ActionListener{
		MainFrame parent;
		int xCoord, yCoord;
		String location;
		
		public PopUpListener(MainFrame parent, int xCoord, int yCoord, String location) {
			this.parent = parent;
			this.xCoord = xCoord;
			this.yCoord = yCoord;
			this.location = location;
		}

		@Override
		public void actionPerformed(ActionEvent ae) {
			InfoPanel infoP = parent.getSidePanel().getInfoPanel();
			if(location.equals("Start")){  //Wenn Start ausgewahelt werden darf, speichere ihn und zeige ihn an
				final float [] selectedWorldCoordinates = parent.getMapPanel().localToWorld(new int [] {xCoord,yCoord});
				infoP.setStart(selectedWorldCoordinates);
				infoP.setStartLabel("("+selectedWorldCoordinates[0]+","+selectedWorldCoordinates[1]+")");
			}
			if(location.equals("Dest")){ //Wenn Ziel ausgewahelt werden darf, speichere es und zeige es an
				final float [] selectedWorldCoordinates = parent.getMapPanel().localToWorld(new int [] {xCoord,yCoord});
				infoP.setDest(selectedWorldCoordinates);
				infoP.setDestLabel("("+selectedWorldCoordinates[0]+","+selectedWorldCoordinates[1]+")");
			}
			
		}
		
	}
}
