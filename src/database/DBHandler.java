package database;

import tvseries.PropHandler;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;


/**
 * DBHandler is used to extend other dataase classes.
 * It has methods for updating and querieing a database.
 */
public class DBHandler  {
    protected static final String DATABASE_NAME = PropHandler.getDatabaseName();

    protected Connection connection = null;
    protected Statement statement = null;

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

    /**
     * Executes a update to the database.
     * It can either be a UPDATE, INSERT or a DELETE statement.
     *
     * @param updateStatement The update statement.
     */
    protected void executeUpdate(String updateStatement) {
	createConnection();
	try {
	    if (updateStatement.startsWith("UPDATE") || updateStatement.startsWith("INSERT") ||
		updateStatement.startsWith("DELETE") || updateStatement.startsWith("CREATE")) {
		//noinspection JDBCExecuteWithNonConstantString
		statement.executeUpdate(updateStatement); // The database is local thus there is no need to worry about security
	    } else {
		System.out.println("Unknown query type: ");
		System.out.println(updateStatement);
	    }

	} catch (SQLException e) {
	    e.printStackTrace();
	} finally {
	    try {
		statement.close();
		connection.close();
	    } catch (SQLException e) {
		e.printStackTrace();
	    }
	}
    }

    /**
     * Executes multiple updates to the database.
     * It can either be UPDATE, INSERT or DELETE statements.
     * @param updateStatements The statements to be updated.
     */
    protected void executeMultipleUpdates(Iterable<String> updateStatements) {
	createConnection();
	try {
	    statement.executeUpdate("BEGIN");
	    for (String updateStatement : updateStatements) {
		if (updateStatement.startsWith("UPDATE") || updateStatement.startsWith("INSERT") ||
		    updateStatement.startsWith("DELETE") || updateStatement.startsWith("CREATE")) {

		    //noinspection JDBCExecuteWithNonConstantString
		    statement.executeUpdate(updateStatement);
		} else {
		    System.out.println("Unknown query type.");
		    System.out.println(updateStatement);
		}
	    }
	    statement.executeUpdate("COMMIT");
	} catch (SQLException e) {
	    e.printStackTrace();
	} finally {
	    try {
		statement.close();
		connection.close();
	    } catch (SQLException e) {
		e.printStackTrace();
	    }
	}
    }
}
