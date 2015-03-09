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
import java.util.List;

public class TVDBDataMapper
{
    static String dbName = PropHandler.getDatabaseName();

    /**
     * The initial insert of a series into the database. Only inserts the show name and the show id.
     *
     * @param showName
     * @param tvDbId
     */

    //hej:)
    public static void initialData(String showName, String tvDbId) {
	try {
	    DBConnection dbc = new DBConnection(dbName);
	    String statement = String.format("INSERT INTO series (tvdb_id, show_name) VALUES (%s, \'%s\');", tvDbId,
					     showName.replaceAll("'", "\'\'"));
	    dbc.getStatement().executeUpdate(statement);
	    System.out.println("LOG: (TVDBDataMapper) Added " + showName + "(" + tvDbId + ")");
	    dbc.close();
	} catch (SQLException e) {
	    e.printStackTrace();
	}
    }

    public static void Update(String tvDbId) throws SQLException {
	try {
	    DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
	    DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
	    Document doc = docBuilder.parse(new File("showdata/en.xml"));
	    doc.getDocumentElement().normalize();

	    NodeList nList = doc.getElementsByTagName("Series");
	    NodeList episodeList = doc.getElementsByTagName("Episode");

	    System.out.println("Episodes: " + episodeList.getLength());

	    Connection db = DriverManager.getConnection("jdbc:sqlite:tvseries.db");
	    String statementSeries = "UPDATE series " +
				     "SET network=?, airday=?, airtime=?, status=?, runtime=?, overview=? " +
				     "WHERE tvdb_id=" + tvDbId;
	    String statementsEpisodes = "INSERT INTO episodes " +
					"(show_id, tvdb_id, episode_name, episode, season, overview) " +
					"VALUES (" + tvDbId + ", ?, ?, ?, ?, ?);";

	    PreparedStatement dbStatement = db.prepareStatement(statementSeries);

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
		    System.out.println(eElement.getElementsByTagName("poster").item(0).getTextContent());
		    DownloadFile.fetchPoster(eElement.getElementsByTagName("poster").item(0).getTextContent(), tvDbId);


		}
	    }
	    dbStatement.executeUpdate();
	    dbStatement.close();

	    PreparedStatement epStatement = db.prepareStatement(statementsEpisodes);


	    for (int temp = 0; temp < episodeList.getLength(); temp++) {
		Node epNode = episodeList.item(temp);

		if (epNode.getNodeType() == Node.ELEMENT_NODE) {
		    Element epElement = (Element) epNode;

		    epStatement.setString(1, epElement.getElementsByTagName("id").item(0).getTextContent());
		    epStatement.setString(2, epElement.getElementsByTagName("EpisodeName").item(0).getTextContent());
		    epStatement.setString(3, epElement.getElementsByTagName("EpisodeNumber").item(0).getTextContent());
		    epStatement.setString(4, epElement.getElementsByTagName("SeasonNumber").item(0).getTextContent());
		    epStatement.setString(5, epElement.getElementsByTagName("Overview").item(0).getTextContent());
		    epStatement.executeUpdate();
		}

	    }


	    epStatement.close();

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
     *
     * @param tvDbId
     *
     * @return
     */
    public static synchronized Series findByTvDbId(String tvDbId) {
	ResultSet resultSet = null;
	try {
	    DBConnection dbc = new DBConnection(dbName);
	    String query = "SELECT tvdb_id, show_name, network, airday, airtime, overview, status, runtime " +
			   "FROM series WHERE tvdb_id =" + tvDbId + " ORDER BY show_name ASC";
	    resultSet = dbc.getStatement().executeQuery(query);

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
	} finally {
	    if (resultSet != null) {
		try {
		    resultSet.close();
		} catch (SQLException e) {
		    e.printStackTrace();
		}
	    }
	}
    }


    /**
     * Selects all the tvdb ids from the database and returns them in an ArrayList
     *
     * @return
     */
    public static synchronized List<String> selectAllIds() {
	ResultSet resultSet = null;
	System.out.println(resultSet);
	try {
	    DBConnection dbc = new DBConnection(dbName);
	    List<String> arrayList = new ArrayList<String>();
	    String query = "SELECT tvdb_id FROM series ORDER BY show_name ASC";
	    resultSet = dbc.getStatement().executeQuery(query);
	    while (resultSet.next()) {
		arrayList.add(resultSet.getString("tvdb_id"));
	    }

	    dbc.close();
	    return arrayList;
	} catch (SQLException e) {
	    return null;
	} finally {
	    if (resultSet != null) {
		try {
		    resultSet.close();
		} catch (SQLException e) {
		    e.printStackTrace();
		}
	    }
	}
    }

    public static synchronized void delete(String tvDbId) {
	try {
	    DBConnection dbc = new DBConnection(dbName);
	    String statement = String.format("DELETE FROM series where tvdb_id=%s", tvDbId);

	    dbc.getStatement().executeUpdate(statement);
	    dbc.close();
	    System.out.println("LOG: (TVDBDataMapper) Removed show with id: " + tvDbId);

	} catch (SQLException e) {
	    e.getStackTrace();
	}

    }
}
