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
	String seriesTable = ("CREATE TABLE IF NOT EXISTS series " +
			      "(tvdb_id INTEGER NOT NULL PRIMARY KEY UNIQUE," +
			      " show_name TEXT NOT NULL," +
			      " network TEXT," +
			      " airday TEXT," +
			      " airtime TEXT," +
			      " overview TEXT," +
			      " status TEXT," +
			      " runtime TEXT)");

	String episodeTable = ("CREATE TABLE IF NOT EXISTS episodes " +
			       "(id INTEGER NOT NULL PRIMARY KEY UNIQUE," +
			       " show_id INTEGER NOT NULL," +
			       " tvdb_id INTEGER NOT NULL," +
			       " episode_name TEXT NOT NULL," +
			       " episode INTEGER NOT NULL," +
			       " season INTEGER NOT NULL," +
			       " overview TEXT NOT NULL)");

	// TODO log
	System.out.println("Creating tables if necessary");
	dbConnection.getStatement().executeUpdate(seriesTable);
	dbConnection.getStatement().executeUpdate(episodeTable);



    }
}