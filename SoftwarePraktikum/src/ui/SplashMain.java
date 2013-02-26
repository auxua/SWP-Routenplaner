package ui;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;

import javax.swing.JFrame;



public class SplashMain extends JFrame implements Runnable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 9067640161350394294L;
	/**
	 * Pseudo Mainklasse zum Starten des MainFrame
	 */
	private Image splash;
	private long duration;
	
	public SplashMain(Image splash, long duration){
		this.splash = splash;
		this.duration = duration;
		this.setResizable(false);
		this.setSize(800,600);
		this.setAlwaysOnTop(true);
		this.setUndecorated(true);
		this.setVisible(true);
		this.setLocationRelativeTo(null);
	}
	public static void main(String[] args) {
		try {
			new SplashMain(Toolkit.getDefaultToolkit().getImage("img/splash2.gif"),30).showSplash();
		} catch (StackOverflowError stErr) {
			System.err.println("Fehler: Zu kleiner Stack. Bitte starten mit der -Xss-Option");
		} catch (OutOfMemoryError memErr) {
			System.err.println("Fehler: Zu kleiner Speicher. Moeglicher Fix: -Xmx / -Xms Optionen");
		}
	}
	
	public void showSplash(){
		repaint();
		new Thread(this).start();
	}

	@Override
	public void run(){
		new MainFrame(800,600);
		try {
			Thread.sleep(duration);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		this.setVisible(false);
	}
	

	@Override
	public void paint(Graphics g){
		g.drawImage(splash,0,0,this);
	}

}
