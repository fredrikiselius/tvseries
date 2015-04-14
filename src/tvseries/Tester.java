package tvseries;


import java.io.IOException;
import java.sql.SQLException;


/**
 * Tester is as the name suggests, a tester class to run the program from
 */
public class Tester
{
    public static void main(String[] args) throws SQLException, IOException {
	// create the local database
	CreateDatabase createDatabase = new CreateDatabase("tvseries");
	// setup properties
	PropHandler propHandler = new PropHandler();
	// create the main window frame
	SeriesFrame sFrame = new SeriesFrame();
    }
}
