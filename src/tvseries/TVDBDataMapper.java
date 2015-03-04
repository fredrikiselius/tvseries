package tvseries;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.ArrayList;

public class TVDBDataMapper
{

    public static void initialData(String name, String tvDbId) throws SQLException {
	System.out.println("INSERT INTO series (show_name, tvdb_id) " + "VALUES (" + name + ", " + tvDbId + ");");
	DBConnection.connection.createStatement().executeUpdate("INSERT INTO series (show_name, tvdb_id) " +
								"VALUES (\'" + name + "\'," + tvDbId + ");");
    }

    public static void Update(File xmlfile, String tvDbId) throws SQLException {
	try {
	    DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
	    DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
	    Document doc = docBuilder.parse(xmlfile);
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
	} catch (SQLException e) {
	    throw new SQLException("Something went wrong", e);
	} catch (ParserConfigurationException e) {
	    e.printStackTrace();
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }

    public static synchronized Series findByTvDbId(String tvDbId) {
	try {
	    String query = "SELECT tvdb_id, show_name, network, airday, airtime, overview, status, runtime " +
	    			   "FROM series WHERE tvdb_id ="+tvDbId;
	    ResultSet resultSet = DBConnection.connection.createStatement().executeQuery(query);

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

		return series;
	    }
	    return null;

	} catch (SQLException e) {
	    return null;
	}
    }

    public static synchronized ArrayList selectAllIds() {
	try {
	    DBConnection db = new DBConnection("tvseries");
	    db.statement = db.connection.createStatement();
	    ArrayList<String> arrayList = new ArrayList<String>();

	    //DBConnection.statement = DBConnection.connection.createStatement();

	    String query = "SELECT tvdb_id FROM series";
	    ResultSet resultSet = db.statement.executeQuery(query);

	    while (resultSet.next()) {
		arrayList.add(resultSet.getString("tvdb_id"));
	    }
	    System.out.println(DBConnection.close());
	    return arrayList;
	} catch (SQLException e) {
	    return null;


	}
    }


}
