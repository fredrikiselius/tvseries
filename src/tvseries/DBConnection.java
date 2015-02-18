package tvseries;

import java.sql.*;

public class DBConnection
{
    public static void openDB(DbType type, String values) {
	String INSERT_BASE = "INSERT INTO SERIES (THETVDBID, NAME) VALUES ";
	String SELECT_ALL = "SELECT * FROM SERIES;";
	String DELETE_BASE = "DELETE from SERIES where THETVDBID=";
	String CREATE_TABLE = "CREATE TABLE SERIES " +
			      "(THETVDBID INT PRIMARY KEY     NOT NULL," +
			      " NAME           TEXT    NOT NULL)";
	Connection connect = null;
	Statement stmt = null;
	try {
	    Class.forName("org.sqlite.JDBC");
	    connect = DriverManager.getConnection("jdbc:sqlite:tvseries.db");
	    System.out.println("Opened database successfully");
	    stmt = connect.createStatement();

	    switch (type) {
		case CREATE_TABLE:
		    stmt.executeUpdate(CREATE_TABLE);
		case INSERT:
		    // values ex: (257655, 'Arrow');
		    stmt.executeUpdate(INSERT_BASE + values);
		    break;
		case SELECT_ALL:
		    ResultSet rs = stmt.executeQuery(SELECT_ALL);
		    while (rs.next()) {
			int theTvDbId = rs.getInt("thetvdbid");
			String name = rs.getString("name");
			System.out.println(theTvDbId + " " + name);
		    }
		    rs.close();
		    break;
		case DELETE_SERIES:
		    stmt.executeUpdate(DELETE_BASE + values + ";");
		    System.out.println("Removed series with ID: " + values);
		    break;
	    }
	    stmt.close();
	    connect.close();


	} catch (SQLException s) {
	    System.out.println(s);
	} catch (Exception e) {
	    System.err.println(e.getClass().getName() + ": " + e.getMessage());
	    System.exit(0);

	}
    }

    public static void main(String[] args) {
	//DBConnection.openDB(DbType.INSERT, "(123123, 'something');");
	//DBConnection.openDB(DbType.CREATE_TABLE, "");
	//DBConnection.openDB(DbType.DELETE_SERIES, "107251");
	DBConnection.openDB(DbType.SELECT_ALL, "");
    }
}


