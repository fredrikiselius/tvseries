package tvseries;

import java.io.IOException;
import java.sql.SQLException;

public class CreateDatabase
{
    private String database;
    private DBConnection dbConnection;

    public CreateDatabase(String database) throws SQLException, IOException {
	this.database = database;
	this.dbConnection = new DBConnection(this.database);
	createTables();
	dbConnection.close();
    }

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
			       " episodenumber INTEGER NOT NULL," +
			       " seasonnumber INTEGER NOT NULL," +
			       " absolutenumber INTEGER NOT NULL," +
			       " overview TEXT NOT NULL)");

	// TODO log
	dbConnection.getStatement().executeUpdate(seriesTable);
	dbConnection.getStatement().executeUpdate(episodeTable);



    }
}
