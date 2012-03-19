package ui;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Klasse zur Verarbeitung von Mausklicks im MainFrame, 
 * speziell Zum Auswaehlen von Start- und Zielpunkten der Route
 */
public class MouseEventListener extends MouseAdapter{

	private final MainFrame parentFrame; //Referenz aus Hauptfenster

	/**
	 * 
	 * @param parentFrame Referenz aus Hauptfenster
	 */
	public MouseEventListener(MainFrame parentFrame){
		this.parentFrame = parentFrame;
	}

	/**
	 * 
	 * @param me MouseEvent zur Bestimmung der Pos. in Bildschirmkoordinaten, auf den geklickt wurde
	 */
	@Override
	public void mouseClicked(MouseEvent me) {
		InfoPanel infoP = parentFrame.getSidePanel().getInfoPanel();
		if(parentFrame.isSelectStart()){  //Wenn Start ausgewahelt werden darf, speichere ihn und zeige ihn an
			final float [] selectedWorldCoordinates = parentFrame.getMapPanel().localToWorld(new int [] {me.getX(),me.getY()});
			infoP.setStart(selectedWorldCoordinates);
			infoP.setStartLabel("("+selectedWorldCoordinates[0]+","+selectedWorldCoordinates[1]+")");
			parentFrame.setSelectStart(false);	//danach darf er nicht mehr ausgewaehlt werde
		}
		if(parentFrame.isSelectDest()){ //Wenn Ziel ausgewahelt werden darf, speichere es und zeige es an
			final float [] selectedWorldCoordinates = parentFrame.getMapPanel().localToWorld(new int [] {me.getX(),me.getY()});
			infoP.setDest(selectedWorldCoordinates);
			infoP.setDestLabel("("+selectedWorldCoordinates[0]+","+selectedWorldCoordinates[1]+")");
			parentFrame.setSelectDest(false); //danach darf er nicht mehr ausgewaehlt werde
		}
	}

}
