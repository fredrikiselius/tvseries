package tvseries;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.sql.*;
import java.util.ArrayList;

public class TVDBDataMapper
{
    static String dbName = PropHandler.getDatabaseName();

    /**
     *
     * The initial insert of a series into the database. Only inserts the show name and the show id.
     * @param showName
     * @param tvDbId
     */
    public static void initialData(String showName, String tvDbId) throws SQLException {
	try {
	    DBConnection dbc = new DBConnection(dbName);
	    String statement = String.format("INSERT INTO series (tvdb_id, show_name) VALUES (%s, \'%s\');", tvDbId, showName);
	    System.out.println(statement);
	    dbc.getStatement().executeUpdate(statement);
	    dbc.close();


	    // TODO LOG
	    System.out.println(String.format("Inserted tvdb_id: %s and show_name: %s", tvDbId, showName));
	} catch (SQLException e) {
	    if (e.getErrorCode() == 0) {
		System.out.println("ERROR: That show is already in the database");
		System.out.println(e);

	    }
	}


	/*DBConnection.connection.createStatement().executeUpdate("INSERT INTO series (show_name, tvdb_id) " +
								"VALUES (\'" + name + "\'," + tvDbId + ");");*/
    }

    public static void Update(String tvDbId) throws SQLException {
	try {
	    DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
	    DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
	    Document doc = docBuilder.parse(new File("showdata/en.xml"));
	    doc.getDocumentElement().normalize();
	    NodeList nList = doc.getElementsByTagName("Series");

	    Connection db = DriverManager.getConnection("jdbc:sqlite:tvseries.db");
	    String statement = "UPDATE series " +
			       "SET network=?, airday=?, airtime=?, status=?, runtime=?, overview=? " +
			       "WHERE tvdb_id=" + tvDbId;
	    PreparedStatement dbStatement = db.prepareStatement(statement);

	    for (int temp = 0; temp < nList.getLength(); temp++) {
		Node nNode = nList.item(temp);

		if (nNode.getNodeType() == Node.ELEMENT_NODE) {

		    Element eElement = (Element) nNode;

		    dbStatement.setString(1, eElement.getElementsByTagName("Network").item(0).getTextContent());
		    dbStatement.setString(2, eElement.getElementsByTagName("Airs_DayOfWeek").item(0).getTextContent());
		    dbStatement.setString(3, eElement.getElementsByTagName("Airs_Time").item(0).getTextContent());
		    dbStatement.setString(4, eElement.getElementsByTagName("Status").item(0).getTextContent());
		    dbStatement.setString(5, eElement.getElementsByTagName("Runtime").item(0).getTextContent());
		    dbStatement.setString(6, eElement.getElementsByTagName("Overview").item(0).getTextContent());


		}
	    }
	    dbStatement.executeUpdate();
	    dbStatement.close();
	    db.close();
	} catch (SQLException e) {
	    e.getStackTrace();
	} catch (ParserConfigurationException e) {
	    e.printStackTrace();
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }

    /**
     * Selects all data on a show based on tvdb id and creates a Series object.
     * @param tvDbId
     * @return
     */
    public static synchronized Series findByTvDbId(String tvDbId) {
	try {
	    DBConnection dbc = new DBConnection(dbName);
	    String query = "SELECT tvdb_id, show_name, network, airday, airtime, overview, status, runtime " +
	    			   "FROM series WHERE tvdb_id ="+tvDbId;
	    ResultSet resultSet = dbc.getStatement().executeQuery(query);

	    while (resultSet.next()) {
		String id = resultSet.getString("tvdb_id");
		String name = resultSet.getString("show_name");
		String network = resultSet.getString("network");
		String airday = resultSet.getString("airday");
		String airtime = resultSet.getString("airtime");
		String overview = resultSet.getString("overview");
		String status = resultSet.getString("status");
		String runtime = resultSet.getString("runtime");

		Series series = new Series(id);
		series.setShowName(name);
		series.setNetwork(network);
		series.setAirday(airday);
		series.setAirtime(airtime);
		series.setOverview(overview);
		series.setStatus(status);
		series.setRuntime(runtime);
		dbc.close();
		return series;
	    }
	    dbc.close();
	    return null;

	} catch (SQLException e) {
	    e.getStackTrace();
	    return null;
	}
    }


    /**
     * Selects all the tvdb ids from the database and returns them in an ArrayList
     * @return
     */
    public static synchronized ArrayList<String> selectAllIds() {
	try {
	    DBConnection dbc = new DBConnection(dbName);
	    ArrayList<String> arrayList = new ArrayList<String>();
	    String query = "SELECT tvdb_id FROM series";
	    ResultSet resultSet = dbc.getStatement().executeQuery(query);
	    while (resultSet.next()) {
		arrayList.add(resultSet.getString("tvdb_id"));
	    }

	    dbc.close();
	    return arrayList;
	} catch (SQLException e) {
	    return null;
	}
    }

    public static synchronized void delete(String tvDbId) {
	try {
	    DBConnection dbc = new DBConnection(dbName);
	    String statement = String.format("DELETE FROM series where tvdb_id=%s", tvDbId);

	    dbc.getStatement().executeUpdate(statement);
	    dbc.close();

	} catch (SQLException e) {
	    e.getStackTrace();
	}

    }
}
