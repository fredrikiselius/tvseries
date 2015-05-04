package se.liu.ida.freis685poniv820.tddd78.tvseries.tvseries;


import se.liu.ida.freis685poniv820.tddd78.tvseries.database.CreateDatabase;
import se.liu.ida.freis685poniv820.tddd78.tvseries.database.UpdateDatabase;
import se.liu.ida.freis685poniv820.tddd78.tvseries.gui.SeriesFrame;


/**
 * Tester is as the name suggests, a tester class to run the program from
 */
public final class Tester
{
    private Tester() {}

    public static void main(String[] args) {
	// setup properties
	// Does never need to be used. Everything is handled in the constructor.
	//noinspection UnusedAssignment
	PropHandler propHandler = new PropHandler();

	// create the local database
	// Does never need to be used. Everything is handled in the constructor.
	//noinspection UnusedAssignment
	CreateDatabase createDatabase = new CreateDatabase();

	// create download folder
	FileHandler.checkShowDataFolder();

	// update db
	UpdateDatabase.update();

	// create the main window frame
	// Does never need to be used. Everything is handled in the constructor.
	//noinspection UnusedAssignment
	SeriesFrame sFrame = new SeriesFrame();
    }
}
