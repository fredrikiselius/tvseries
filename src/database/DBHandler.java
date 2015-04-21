package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public abstract class DBHandler {
    protected static final String DATABASE_NAME = "tvseries";

    protected Connection connection;
    protected Statement statement;
    protected boolean isExecuting;

    /**
     * Opens a connection to the database
     */
    protected void createConnection() {
	try {
	    Class.forName("org.sqlite.JDBC");
	    connection = DriverManager.getConnection("jdbc:sqlite:" + DATABASE_NAME + ".db");
	    statement = connection.createStatement();

	} catch (SQLException | ClassNotFoundException e) {
	    e.printStackTrace();
	}
    }

    protected void begin() {
	try {
	    statement.executeUpdate("BEGIN");
	} catch(SQLException e) {
	    e.printStackTrace();
	}
    }

    protected void commit() {
	try {
	    statement.executeUpdate("COMMIT");
	} catch(SQLException e) {
	    e.printStackTrace();
	}
    }

    protected void close() {
	try {
	    statement.close();
	    connection.close();
	} catch (SQLException e) {
	    e.printStackTrace();
	}
    }
}
