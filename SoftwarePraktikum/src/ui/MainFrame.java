package ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.BorderFactory;
import javax.swing.JFrame;

import main.Logger;

/**
 * Hier wird das Fenster erzeugt, beinhaltet saemtliche JPanels
 */
public class MainFrame extends JFrame{

	private static final long serialVersionUID = 1L; 
	private MapPanel mapPanel;		//Bereich, in dem die Karte angezeigt wird
	private SidePanel sidePanel;	//Bereich, in dem Kartennavigation und Routenberechnung durchgefuehrt werden
	private MenuHandler mHandler;	//Menueleiste
	private int width,height;		//initialwerte der Fenstergroesse
	private boolean selectStart,selectDest;	//true, wenn gerade die Option besteht durch Klicken auf die Karte Start oder Ziel einer Route festzulegen

	/**
	 * 
	 * @param width Initiale Fensterbreite
	 * @param height Initiale Fensterhoehe
	 */
	public MainFrame(int width, int height){
		this.width = width;
		this.height = height;

		initJFrame();
		buildPanels();

		setVisible(true); 	  //fenster anzeigen
		repaint();
	}

	/**
	 * initialisiert das JFrame
	 */
	private void initJFrame(){
		this.setTitle("SWP- Routenplaner");		//Titel setzen
		this.setSize(new Dimension(width,height));	//Groesse setzen
		this.setLocationRelativeTo(null);   //Fenster in der Mitte des Bildschirms anzeigen											//Beim klick auf schliessen Programm beenden
		this.setLayout(new BorderLayout());		//Layout: Karte in der Mitte, rest rechts am Rand
		this.setIconImage(Toolkit.getDefaultToolkit().getImage("img/icon.gif"));	//Icon des Programms setzen
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);	//Beim Klick auf schliessen das Programm beenden
		mHandler = new MenuHandler(this);				//Menue an Fenster binden
		this.setJMenuBar(mHandler.buildMenu());
	}

	/**
	 * erstellt map- und sidePanel
	 */
	private void buildPanels(){
		mapPanel = new MapPanel(this);
		sidePanel = new SidePanel(this);

		mapPanel.addMouseListener(new MouseEventListener(this));

		mapPanel.setPreferredSize(new Dimension(2*width/3,height));
		sidePanel.setPreferredSize(new Dimension(width/3,height));
		mapPanel.setBorder(BorderFactory.createLineBorder (Color.BLACK, 1));	//kleiner schwarzer Rand zwischen den Panels
		sidePanel.setBorder(BorderFactory.createLineBorder (Color.BLACK, 1));

		this.add(mapPanel,BorderLayout.CENTER);
		this.add(sidePanel,BorderLayout.EAST);
	}

	/**
	 * Wenn man Start auswahelen kann, zeige einen Handcursor
	 * @param selectStart Flag, das gesetzt wird
	 */
	public void setSelectStart(boolean selectStart) {
		this.selectStart = selectStart;
		final Cursor cursor = selectStart?new Cursor(Cursor.HAND_CURSOR):new Cursor(Cursor.DEFAULT_CURSOR);
		mapPanel.setCursor(cursor);
		if(selectStart) 
			Logger.getInstance().log("MainFrame","Waehlen Sie einen Startpunkt aus!");
	}

	/**
	 * Wenn man Ziel auswahelen kann, zeige einen Handcursor
	 * @param selectDest Flag, das gesetzt wird
	 */
	public void setSelectDest(boolean selectDest) {
		this.selectDest = selectDest;
		final Cursor cursor = selectDest?new Cursor(Cursor.HAND_CURSOR):new Cursor(Cursor.DEFAULT_CURSOR);
		mapPanel.setCursor(cursor);
		if(selectDest)
			Logger.getInstance().log("MainFrame","Waehlen Sie einen Endpunkt aus!");
	}

	//Getter- Methoden:
	public MapPanel getMapPanel() {
		return mapPanel;
	}

	public SidePanel getSidePanel() {
		return sidePanel;
	}
	
	public boolean isSelectStart() {
		return selectStart;
	}
	
	public boolean isSelectDest() {
		return selectDest;
	}
}
