package ui;

import java.awt.GridLayout;

import javax.swing.JPanel;

/**
 * JPanel, in dem Routenanzeige, Konsolenausgabe und Kartennavigation enthalten sind
 */
public class SidePanel extends JPanel{

	private static final long serialVersionUID = -6631880099992721524L;
	private final InfoPanel infoPanel;
	private final MapControlPanel mapControl;
	private final ConsolePanel conPanel;

	/**
	 * 
	 * @param parentFrame Referenz auf Hauptfenster
	 */
	public SidePanel(MainFrame parentFrame){ //uebliche initialisierung von JPanels
		setLayout(new GridLayout(3,1));

		infoPanel = new InfoPanel(parentFrame);
		mapControl = new MapControlPanel(parentFrame);
		conPanel = new ConsolePanel(parentFrame);

		this.add(infoPanel);
		this.add(conPanel);
		this.add(mapControl);
	}

	//Getter- Methoden:
	public InfoPanel getInfoPanel() {
		return infoPanel;
	}

	public MapControlPanel getMapControl() {
		return mapControl;
	}

	public ConsolePanel getConPanel() {
		return conPanel;
	}
}
