package ui;

import java.awt.BorderLayout;
import java.io.PrintStream;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 * Klasse, die das Infofenster repraesentiert. Aktionen, die vom Programm durchgefuehrt werden, werden in dem Fenster
 * angezeigt. Dies geschieht durch Ueberschreiben einiger System.out.println Methoden, so dass diese in dieses Panel umgeleitet werden
 */
public class ConsolePanel extends JPanel{

	private static final long serialVersionUID = -1027448727362889770L;
	private final JTextArea console;	//Ausgabetextfeld fuer Ausgabe

	/**
	 * 
	 * @param parent Referenz auf Hauptfenster
	 */
	public ConsolePanel(MainFrame parent){
		setLayout(new BorderLayout());		//Uebliche initialisierung von JPanels.
		console = new JTextArea();
		console.setEditable(false);
		console.setRows(10);
		final JScrollPane scrollPane = new JScrollPane(console,JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		final PrintStream output = new PrintStream(System.out){		//Hier wird der System.out Stream abgefangen und
			@Override												//mit eigenen Methoden ueberschirbene, die die Ausgabe
			public void println(String s){							//in das Konsolen Textfeld schreiben
				console.append(s+"\n");
			}
			@Override
			public void println(int s){
				console.append(s+"\n");
			}
			@Override
			public void println(float s){
				console.append(s+"\n");
			}
			@Override
			public void print(String s){
				console.append(s);
			}
		};
		System.setOut(output);		
		this.add(scrollPane,BorderLayout.CENTER);
	}
}
