package tvseries;

import java.io.IOException;
import java.sql.SQLException;

/**
 * CreateDatabase is used to create the database if there is non.
 * It also creates the necessary tables
 */
public class CreateDatabase
{
    private DBConnection dbConnection;

    public CreateDatabase(String database) throws SQLException, IOException {
	this.dbConnection = new DBConnection(database);
	createTables();
	dbConnection.close();
    }

    /**
     * Creates the nessecary tables in the database if they are not already there.
     * @throws SQLException
     * @throws IOException
     */
    public void createTables() throws SQLException, IOException {
	System.out.println("LOG: (CreateDatabase) Creating tables if necessary");
	String seriesTable = ("CREATE TABLE IF NOT EXISTS series " +
			      "(tvdb_id INTEGER NOT NULL PRIMARY KEY UNIQUE," +
			      " show_name TEXT NOT NULL," +
			      " network TEXT," +
			      " airday TEXT," +
			      " airtime TEXT," +
			      " firstaired TEXT," +
			      " overview TEXT," +
			      " status TEXT," +
			      " runtime TEXT," +
			      " lastupdated INTEGER)");

	String episodeTable = ("CREATE TABLE IF NOT EXISTS episodes " +
			       "(tvdb_id INTEGER NOT NULL PRIMARY KEY UNIQUE," +
			       " show_id INTEGER NOT NULL," +
			       " episode_name TEXT NOT NULL," +
			       " first_aired TEXT NOT NULL," +
			       " episodenumber INTEGER NOT NULL," +
			       " seasonnumber INTEGER NOT NULL," +
			       " absolutenumber INTEGER NOT NULL," +
			       " overview TEXT NOT NULL)");

	String historyTable = "CREATE TABLE IF NOT EXISTS history " +
			      "(episode_id INTEGER NOT NULL, " +
			      "watch_date TEXT NOT NULL)";

	dbConnection.getStatement().executeUpdate(seriesTable);
	dbConnection.getStatement().executeUpdate(episodeTable);
	dbConnection.getStatement().executeUpdate(historyTable);



    }
}
