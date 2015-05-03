package database;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

/**
 * CreateDatabase is used to create the database if there is none.
 * It also creates the necessary tables
 */
public class CreateDatabase extends DBHandler
{


    public CreateDatabase() {
	createTables();
    }

    /**
     * Creates the nessecary tables in the database if they are not already there.
     */
    public void createTables() {
	createConnection();
	System.out.println("LOG: (CreateDatabase) Creating tables if necessary.");
	Collection<String> tables = new ArrayList<>();
	final String seriesTable = ("CREATE TABLE IF NOT EXISTS series " +
			      "(tvdb_id INTEGER NOT NULL PRIMARY KEY UNIQUE," +
			      " show_name TEXT NOT NULL," +
			      " network TEXT," +
			      " airday TEXT," +
			      " airtime TEXT," +
			      " firstaired TEXT," +
			      " overview TEXT," +
			      " status TEXT," +
			      " runtime INTEGER," +
			      " lastupdated INTEGER)");

	final String episodeTable = ("CREATE TABLE IF NOT EXISTS episodes " +
			       "(tvdb_id INTEGER NOT NULL PRIMARY KEY UNIQUE," +
			       " show_id INTEGER NOT NULL," +
			       " episode_name TEXT DEFAULT TBA," +
			       " first_aired TEXT DEFAULT TBA," +
			       " episodenumber INTEGER NOT NULL," +
			       " seasonnumber INTEGER NOT NULL," +
			       " absolutenumber INTEGER NOT NULL," +
			       " overview TEXT NOT NULL," +
			       " watch_count INTEGER DEFAULT 0)");

	final String historyTable = "CREATE TABLE IF NOT EXISTS history " +
			      "(episode_id INTEGER NOT NULL, " +
			      "watch_date TEXT NOT NULL)";

	tables.add(seriesTable);
	tables.add(episodeTable);
	tables.add(historyTable);

	executeMultipleUpdates(tables);
    }
}
