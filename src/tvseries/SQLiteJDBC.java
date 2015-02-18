package tvseries;

import java.sql.*;

public class SQLiteJDBC
{
    private String DBNAME;

    public static void main(String args[])
    {
	Connection c = null;
	Statement stmt = null;
	try {
	    Class.forName("org.sqlite.JDBC");
	    c = DriverManager.getConnection("jdbc:sqlite:tvseries.db");
	    System.out.println("Opened database successfully");

	    stmt = c.createStatement();
	    /*String sql = "CREATE TABLE SERIES " +
			 "(THETVDBID INT PRIMARY KEY     NOT NULL," +
			 " NAME           TEXT    NOT NULL)";
	    String sql = "INSERT INTO SERIES (THETVDBID, NAME) " +
			 "VALUES (257655, 'Arrow');";
	    stmt.executeUpdate(sql);
	    stmt.close();
	    c.close();*/

	   ResultSet rs = stmt.executeQuery( "SELECT * FROM SERIES;");
	    while ( rs.next() ) {
		int tid = rs.getInt("thetvdbid");
		String name = rs.getString("name");
		System.out.println(tid + " " + name);
	    }
	    rs.close();
	    stmt.close();
	    c.close();

	} catch (Exception e) {
	    System.err.println(e.getClass().getName() + ": " + e.getMessage());
	    System.exit(0);
	}
	System.out.println("Records created successfully");
    }

    public void addSeries(String name, int id) {

    }

    public String getSeries(String name) {
	return "not done";
    }
}
