package ui;

import graphenbib.GPSCoordinate;
import graphenbib.MapNode;

import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import main.Logger;
import main.Path2Draw;
import main.Viewport;

/**
 * JPanel, in dem alle Informationen zur gesamten Route sind
 */

public class InfoPanel extends JPanel{

	private static final long serialVersionUID = 4600016410648638443L;
	private JButton selectStart; //wenn dieser Button gedrueckt wird, dann kann Benutzer Startkoordinate der Route waehlen
	private JButton selectDest;  // Zielkoordinate

	private JLabel routeLabel;   	// Anzeigetext fuer Routeninformationen
	private JLabel routeDestLabel;
	private JLabel distLabel;		//Label, das die Laenge der Route ausgibt
	private JLabel estTimeLabel; 	//Label, das die ungefaehr benoetigte Zeit der Route ausgibt
	private String startLabel ="<X>";	//Label, das die Startkoordinaten anzeigt
	private String destLabel ="<Y>";	//Label, das die Endkoordinaten anzeigt
	private String dist ="0";			//Routenlaenge als String
	private String estTime ="0";		//ben. Zeit als String

	private final ButtonEventListener bel; // um auf Benutzereingabe zu reagieren
	private final MainFrame parentFrame;	//Referenz auf Hauptfenster
	private float [] start;			//Start-,Ziel- Koord. in float
	private float [] dest;
	
	/**
	 * 
	 * @param parentFrame Referenz auf Hauptfenster
	 */
	public InfoPanel(MainFrame parentFrame){
		setLayout(new GridLayout(6,1));
		bel = new ButtonEventListener(this,parentFrame);
		this.parentFrame = parentFrame;

		buildButtons();
		buildLabels();
	}

	/**
	 * initialisiert die Buttons
	 */
	private void buildButtons(){
		selectStart = new JButton("Startpunkt auswaehlen");
		selectDest = new JButton("Zielpunkt auswaehlen");

		selectStart.addActionListener(bel);
		selectDest.addActionListener(bel);

		this.add(selectStart);
		this.add(selectDest);
	}

	/**
	 * initialisiert die Labels
	 */
	private void buildLabels(){
		routeLabel   = new JLabel("Route von:  "+startLabel);
		routeDestLabel = new JLabel("nach:  " +destLabel);
		distLabel    = new JLabel("Entfernung: "+dist+" Km");
		estTimeLabel = new JLabel("Fahrtzeit:  ~"+estTime+" h");

		this.add(routeLabel);
		this.add(routeDestLabel);
		this.add(distLabel);
		this.add(estTimeLabel);
	}

	/**
	 * Falls es durch den Benutzer zu Aenderungen kam, so werden alle Aenderungen, fuer die ein Objekt dieser Klasse
	 * zustaendig ist, vermerkt.
	 * Insbesondere wird, wenn Start- und Zielpounkt ausgewaehlt wurden, die Routenberechnung gestartet, 
	 * sowie die Zeit und Laenge der Route angezeigt. Wenn keine Route ex. wird eine Nachricht angezeigt
	 */
	public void updateLabels(){
		routeLabel.setText("Route von:  "+startLabel);
		routeDestLabel.setText("nach:  " +destLabel);
		if(!startLabel.equals("<X>")&&!destLabel.equals("<Y>"))
			try{
				final GPSCoordinate startCoord = new GPSCoordinate(start[1],start[0]);
				final GPSCoordinate endCoord   = new GPSCoordinate(dest[1],dest[0]);
				final Viewport vp = parentFrame.getMapPanel().getViewport();
				final MapNode startNode = vp.getClosestNode(startCoord);
				final MapNode endNode    = vp.getClosestNode(endCoord);

				vp.computeShortestPath( //berechne neuen kuerzesten Pfad
						startNode,
						endNode );
				final Path2Draw shortestPath = parentFrame.getMapPanel().getViewport().getShortestPath(); //Zeiger auf kuerzesten Pfad aus viewport


				if(vp.getShortestPath().getPathIt(vp)==null)	//Wenn kein Pfad exisitert
					JOptionPane.showMessageDialog(parentFrame,"Bitte waehlen Sie einen anderen Start -oder Zielpunkt", "Es existiert kein Pfad.",JOptionPane.WARNING_MESSAGE);
				else{
					calcDist(shortestPath.getTotalDistance()/10); //in metern
					calcEstTime(shortestPath.getTotalTime()/10000); //in sekunden
					distLabel.setText("Entfernung: "+dist);
					estTimeLabel.setText("Fahrtzeit:  ~"+estTime);
					parentFrame.getMapPanel().repaintAllStuff();
				}
			}catch(final Exception e){e.printStackTrace();}
			Logger.getInstance().log("InfoPanel","updated labels: "+routeLabel.getText());
	}

	/**
	 * Laenge des Kuerzesten Weges in schoeneres Format bringen
	 * @param i Laenge in Metern
	 */
	private void calcDist(int i) { 
		//System.out.println(i+"");
		dist = (i>1000)?(i/1000)+"km "+(i%1000)+"m":i+"m";
	}

	/**
	 * Zeit, die fuer kuerzesten Weg benoetigt wird in schoeneres Format bringen
	 * @param d Zeit in Sekunden
	 */
	private void calcEstTime(long d){
		//System.out.println(d+"");
		estTime = (int)(d/3600)+"h "+(int)((d/60)%60)+"m "+((int)d%60)+"s";

	}

	//Getter und setter -Methoden:
	public JButton getSelectStart() {
		return selectStart;
	}

	public JButton getSelectDest() {
		return selectDest;
	}

	public JLabel getRouteLabel() {
		return routeLabel;
	}

	public JLabel getDistLabel() {
		return distLabel;
	}

	public JLabel getEstTimeLabel() {
		return estTimeLabel;
	}

	public void setStartLabel(String start) {
		startLabel = start;
		updateLabels();
	}

	public void setDestLabel(String dest) {
		destLabel = dest;
		updateLabels();
	}

	public float[] getDest() {
		return dest;
	}

	public void setDest(float[] dest) {
		this.dest = dest;
	}

	public float[] getStart() {
		return start;
	}

	public void setStart(float[] start) {
		this.start = start;
	}

}
