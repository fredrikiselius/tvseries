package tvseries;


import database.CreateDatabase;
import database.UpdateDatabase;
import gui.SeriesFrame;


/**
 * Tester is as the name suggests, a tester class to run the program from
 */
public final class Tester
{
    private Tester() {}

    public static void main(String[] args) {
		// setup properties
	PropHandler propHandler = new PropHandler();
	// create the local database
	CreateDatabase createDatabase = new CreateDatabase();
	// create download folder
	FileHandler.checkShowDataFolder();

	// update db
	UpdateDatabase.update();
	// create the main window frame
	SeriesFrame sFrame = new SeriesFrame();
    }
}
