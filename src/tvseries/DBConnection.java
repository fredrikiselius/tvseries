package tvseries;

import java.sql.*;

public class DBConnection
{
    private String database;
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

    public static boolean close() {
	try {
	    connection.close();
	    return true;
	} catch (SQLException e) {
	    System.out.println(e);
	    return false;
	}

    }

    public static Connection getConnection() {
	return connection;
    }

    public static Statement getStatement() {
	return statement;
    }

    public static void setConnection(final Connection connection) {
	DBConnection.connection = connection;
    }

    public static void setStatement(final Statement statement) {
	DBConnection.statement = statement;
    }
}


