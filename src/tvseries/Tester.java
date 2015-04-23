package tvseries;


import database.CreateDatabase;
import database.UpdateDatabase;
import gui.SeriesFrame;

import java.io.IOException;
import java.sql.SQLException;


/**
 * Tester is as the name suggests, a tester class to run the program from
 */
public class Tester
{
    public static void main(String[] args) throws SQLException, IOException {
	System.out.println(15/8);
	// create the local database
	CreateDatabase createDatabase = new CreateDatabase("tvseries");
	// create download folder
	FileHandler.checkShowDataFolder();
	// setup properties
	PropHandler propHandler = new PropHandler();
	// update db
	UpdateDatabase.update();
	// create the main window frame
	SeriesFrame sFrame = new SeriesFrame();
    }
}
