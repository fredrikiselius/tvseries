package tvseries;

import java.sql.*;

public class DBConnection
{
    private String database;
    private Connection connection;
    private Statement statement;


    public DBConnection(String database) {
	this.database = database;
	createConnection();
    }

    private void createConnection() {
	try {
	    Class.forName("org.sqlite.JDBC");
	    connection = DriverManager.getConnection("jdbc:sqlite:" + database + ".db"); // Add options for database name
	    statement = connection.createStatement();
	    // testing TODO remove
	} catch (SQLException e) {
	    e.getStackTrace();
	} catch (ClassNotFoundException e) {
	    e.getStackTrace();
	}
    }

    public boolean close() {
	try {
	    statement.close();
	    connection.close();
	    // TODO log
	    return true;
	} catch (SQLException e) {
	    System.out.println("DBConnection: Could not close connection.\n");
	    e.getStackTrace();
	    return false;
	}

    }

    public Connection getConnection() {
	return connection;
    }

    public void setConnection(final Connection connection) {
	this.connection = connection;
    }

    public Statement getStatement() {
	return statement;
    }

    public void setStatement(final Statement statement) {
	this.statement = statement;
    }
}


