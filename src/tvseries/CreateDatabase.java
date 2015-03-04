package tvseries;

import java.io.IOException;
import java.sql.SQLException;

public class CreateDatabase {
    public String database;
    public CreateDatabase(String database) throws SQLException, IOException{
	this.database = database;
	DBConnection dbConnection = new DBConnection(this.database);
	createTables();
    }
    public void createTables() throws SQLException, IOException {
	DBConnection.connection.createStatement().executeUpdate("CREATE TABLE IF NOT EXISTS series " +
								"(tvdb_id INTEGER NOT NULL PRIMARY KEY UNIQUE," +
								" show_name TEXT NOT NULL," +
								" network TEXT," +
								" airday TEXT," +
								" airtime TEXT," +
								" overview TEXT," +
								" status TEXT," +
								" runtime TEXT)");

	DBConnection.connection.createStatement().executeUpdate("CREATE TABLE IF NOT EXISTS episodes " +
								"(id INTEGER NOT NULL PRIMARY KEY UNIQUE," +
								" show_id INTEGER NOT NULL," +
								" tvdb_id INTEGER NOT NULL," +
								" episode_name TEXT NOT NULL," +
								" episode INTEGER NOT NULL," +
								" season INTEGER NOT NULL," +
								" overview TEXT NOT NULL)");

    }
}
