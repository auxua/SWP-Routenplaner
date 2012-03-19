package Import;

import graphexceptions.EmptyInputException;
import graphexceptions.InvalidGPSCoordinateException;
import graphexceptions.InvalidInputException;

import java.io.IOException;

import javax.xml.stream.XMLStreamException;

public class FileTilerTest {
	public static void main(String[] args){
		FileTiler tiler = new FileTiler();
		try {
			tiler.TileFile("testdateien/aachen.osm", "tmp");
		} catch (XMLStreamException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (EmptyInputException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidInputException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidGPSCoordinateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
