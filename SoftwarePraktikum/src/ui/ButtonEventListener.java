package ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JPanel;

/**
 * Wartet auf Events durch Klicken auf einen Button, 
 * wird sowohl im {@link InfoPanel} als auch im {@link MapControlPanel} benoetigt
 */
public class ButtonEventListener implements ActionListener{

	private final JPanel parentPan;		//JPanel, zu dem der ButtonEventListener gehoert
	private final MainFrame parentFrame; // Referenz auf Hauptfenster

	/**
	 * 
	 * @param parentPan Referenz auf JPanel, zu dem der ButtonEventListener gehoert
	 * @param parentFrame Referenz auf Hauptfenster
	 */
	public ButtonEventListener(JPanel parentPan,MainFrame parentFrame){
		this.parentPan = parentPan;	
		this.parentFrame =parentFrame;
	}

	/**
	 * Ueberpruefung und Behandlung, falls User Start-, Ziel- Koordinate ausgewaehlt hat oder die Karte im
	 * MapPanel verschieben moechte
	 */
	@Override
	public void actionPerformed(ActionEvent ae){
		if(parentPan instanceof InfoPanel){
			final InfoPanel parent2 = (InfoPanel)parentPan;			
			if(ae.getSource().equals(parent2.getSelectStart())){  //wenn User aus Start auswaelheln geklickt hat
				parentFrame.setSelectDest(false);					// -> kann Ziel nicht mehr ausgewaehlt werden
				parentFrame.setSelectStart(true);					// aber dafuer der Startpunkt
			}
			if(ae.getSource().equals(parent2.getSelectDest())){  //s.o., nur Start und Ziel vertauscht
				parentFrame.setSelectStart(false);
				parentFrame.setSelectDest(true);
			}
		}
		if(parentPan instanceof MapControlPanel){
			final MapControlPanel parent2 = (MapControlPanel)parentPan;
			final MapPanel mapPan = parentFrame.getMapPanel();
			if(ae.getSource().equals(parent2.getUp())) //Fuehre Translation bzw. Zoom durch, wenn auf jeweilige Buttons geklickt wurde
				mapPan.goUp(main.Config.shiftConstant);
			if(ae.getSource().equals(parent2.getDown()))
				mapPan.goDown(main.Config.shiftConstant);
			if(ae.getSource().equals(parent2.getLeft()))
				mapPan.goLeft(main.Config.shiftConstant);
			if(ae.getSource().equals(parent2.getRight()))
				mapPan.goRight(main.Config.shiftConstant);
			if(ae.getSource().equals(parent2.getZoomIn()))
				mapPan.zoomIn();
			if(ae.getSource().equals(parent2.getZoomOut()))
				mapPan.zoomOut();
		}
	}
}
