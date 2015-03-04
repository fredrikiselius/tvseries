package tvseries;

import java.sql.*;

public class DBConnection
{
    private static String database;
    public static Connection connection;
    public static Statement statement;
    private boolean isConnected;

    public DBConnection(String database) {
	this.database = database;
	try {
	    createConnection();
	} catch (Exception e) {

	}
    }

    private static void createConnection() {
	try {
	    Class.forName("org.sqlite.JDBC");
	    connection = DriverManager.getConnection("jdbc:sqlite:tvseries.db"); // Add options for database name
	    statement = connection.createStatement();
	} catch (SQLException e) {
	    System.out.println(e);
	} catch (ClassNotFoundException e) {
	    System.out.println(e);
	}
    }

    public boolean close() {
	try {
	    connection.close();
	    return true;
	} catch (SQLException e) {
	    System.out.println(e);
	    return false;
	}

    }
}


