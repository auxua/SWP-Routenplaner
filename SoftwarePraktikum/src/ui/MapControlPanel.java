package ui;

import java.awt.BorderLayout;
import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JPanel;

/**
 * JPanel, in das alle Funktionen zum Anpassen der Kartenansicht gepackt werden
 */

public class MapControlPanel extends JPanel{

	private static final long serialVersionUID = 5208810179855159006L;
	private JButton up;			//nach oben Button
	private JButton down;		//nach unten Buttons
	private JButton left;		//nach links Button
	private JButton right;		//nach rechts Button
	private final Zooms zooms;	//Interne Klasse fuer Zooms
	private final ButtonEventListener bel;	//Listener fuer die Buttons

	/**
	 * 
	 * @param parentFrame Referenz aus Hauptfenster
	 */
	public MapControlPanel(MainFrame parentFrame){
		setLayout(new BorderLayout());
		bel = new ButtonEventListener(this,parentFrame);
		zooms = new Zooms();
		buildButtons();
	}

	/**
	 * Initialisiert die Buttons
	 */
	private void buildButtons(){
		up = new JButton("^");
		down = new JButton("v");
		left = new JButton("<");
		right = new JButton(">");

		up.addActionListener(bel);
		down.addActionListener(bel);
		left.addActionListener(bel);
		right.addActionListener(bel);

		this.add(up,BorderLayout.NORTH);
		this.add(down,BorderLayout.SOUTH);
		this.add(left,BorderLayout.WEST);
		this.add(right,BorderLayout.EAST);
		this.add(zooms,BorderLayout.CENTER);
	}

	/**
	 * Interne Klassen fuer Zooms (eigtl nicht benoetigt, aber einfacher wegen dem Layout)
	 *
	 */
	class Zooms extends JPanel{

		private static final long serialVersionUID = -7840001063397545843L;
		private final JButton zoomIn;		//Hereinzoomen Button
		private final JButton zoomOut;		//herauszoomen Button

		public Zooms(){
			setLayout(new GridLayout(2,1));
			zoomIn = new JButton("+");
			zoomOut = new JButton("-");

			zoomIn.addActionListener(bel);
			zoomOut.addActionListener(bel);

			this.add(zoomIn);
			this.add(zoomOut);
		}
	}

	//Getter-Methoden:

	public JButton getUp() {
		return up;
	}

	public JButton getDown() {
		return down;
	}

	public JButton getLeft() {
		return left;
	}

	public JButton getRight() {
		return right;
	}

	public JButton getZoomIn() {
		return zooms.zoomIn;
	}

	public JButton getZoomOut() {
		return zooms.zoomOut;
	}
}
