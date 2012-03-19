package ui;

import java.awt.geom.AffineTransform;

/**
 * Ein Objekt dieser Klasse entspricht folgender affinen Transformationsmatrix:
 *	[ scale ,    0   , transX ]
 *	[ 0     , scale  , transY ]
 *  [ 0     ,    0   ,     1  ]
 */
public class TransformationManager {
	private double scale;
	private double transX;
	private double transY;

	/**
	 * Erstellt ein Objekt von {@link TransformationManager} mit der Identitaet
	 */
	public TransformationManager(){ //Erstelle Identity
		scale = 1.0;
		transX = 0.0;
		transY = 0.0;
	}

	/**
	 * Methode um entprechende Affine Transformation zu erhalten.
	 * @return Transformationsmatrix
	 */
	public AffineTransform getAffineTransform(){
		return new AffineTransform(scale, 0, 0, scale, transX, transY);
	}

	/**
	 * Methode, um das Inverse der entsprechenden affinen Transformation zu erhalten.
	 * @return das Inverse der diesem Objekt entsprechenden affinen Transformationsmatrix
	 */
	public AffineTransform getInverse(){
		return new AffineTransform(1/scale,0,0,1/scale,-transX/scale,-transY/scale);
	}

	/**
	 * Veraendere Matrix entsprechend einer Translation
	 * @param amountX Translation in X- Achse
	 * @param amountY Translation in Y- Achse
	 */
	public void translate(double amountX, double amountY){
		transX += amountX;
		transY += amountY;
	}

	/**
	 * Veraendere Matrix entsprechend eines Scalings. (Scaling zum/im Nullpunkt)
	 * @param scaleFactor Factor, mit dem gescaled werden soll
	 */
	public void scale(double scaleFactor){
		scale *= scaleFactor;
		transX *= scaleFactor;
		transY *= scaleFactor;
	}

	/**
	 * Veraendere Matrix entsprechend eines Scalings. (Scaling zum/im Mittelpunkt)
	 * @param width breite des Fensters
	 * @param height hoehe des Fensters
	 * @param scaleFactor Faktor, mit dem gescaled werden soll
	 */
	public void scaleInMid(int width, int height, double scaleFactor){
		translate(-((double)width)/2, -((double) height)/2);
		scale(scaleFactor);
		translate(((double)width)/2, ((double) height)/2);
	}
}
