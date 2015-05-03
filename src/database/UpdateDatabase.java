package database;

import episodedao.Episode;
import episodedao.EpisodeDao;
import episodedao.EpisodeDaoSQLite;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import parser.XMLParser;
import seriesdao.Series;
import seriesdao.SeriesDaoSQLite;
import tvseries.FileHandler;
import tvseries.PropHandler;


import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * UpdateDatabase is used to update the information of the series already in the database.
 */
public class UpdateDatabase extends DBHandler
{

    /**
     * Gets the current server time from thetvdb.com
     *
     * @return int Server time
     */
    private static int getCurrentServerTime() {
	DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	int currentServerTime = 0;

	try {
	    DocumentBuilder builder = factory.newDocumentBuilder();
	    Document document = builder.parse(new URL("http://thetvdb.com/api/Updates.php?type=none").openStream());

	    NodeList timeNodes = document.getElementsByTagName("Time"); // only contains one on that url
	    currentServerTime = Integer.parseInt(timeNodes.item(0).getTextContent());

	} catch (ParserConfigurationException | SAXException | IOException e) {
	    e.printStackTrace();
	}
	return currentServerTime;
    }

    /**
     * Fetech series ids to be updated
     *
     * @param lastUpdate Server time from last update
     *
     * @return List<String> Series ids
     */
    private static List<String> getSeriesToUpdate(int lastUpdate) {
	DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	List<String> seriesIds = new ArrayList<>();
	try {
	    DocumentBuilder builder = factory.newDocumentBuilder();
	    Document document = builder.parse(
		    new URL(String.format("http://thetvdb.com/api/Updates.php?type=all&time=%d", lastUpdate)).openStream());

	    NodeList seriesIdList = document.getElementsByTagName("Series");

	    for (int i = 0; i < seriesIdList.getLength(); i++) {
		seriesIds.add(seriesIdList.item(i).getTextContent());
	    }
	} catch (ParserConfigurationException | SAXException | IOException e) {
	    e.printStackTrace();
	}
	return seriesIds;
    }

    /**
     * Updates the series that are in need of updates
     */
    public static void update() {
	PropHandler pHandler = new PropHandler();
	SeriesDaoSQLite seriesDb = new SeriesDaoSQLite();
	EpisodeDao episodeDb = new EpisodeDaoSQLite();
	XMLParser xmlParser = new XMLParser();


	// fetch all ids in the db
	List<String> seriesInDb = seriesDb.selectAllIds();
	// fetch all ids updated since last update
	List<String> seriesUpdatedSinceLast = getSeriesToUpdate(pHandler.getLastUpdate());
	Collection<String> seriesToUpdate = new ArrayList<>();

	seriesUpdatedSinceLast.stream().filter(id -> seriesInDb.contains(id)).forEach(id -> {
	    seriesToUpdate.add(id);
	    System.out.println("Series ID TO UPDATE: " + id);
	});

	List<Series> series = new ArrayList<>();
	List<Episode> episodes = new ArrayList<>();
	for (String idString : seriesToUpdate) {
	    int id = Integer.parseInt(idString);
	    // fetch new data
	    try {
		FileHandler.fetchZip(id);
		// parse new data
		Series show = xmlParser.getSeries(id);
		List<Episode> parsedEpisodes = xmlParser.getEpisodes(id);

		series.add(show);
		episodes.addAll(parsedEpisodes);
	    } catch (IOException e) {
		e.printStackTrace();
	    }


	}

	episodeDb.updateMultipleEpisodes(episodes, QueryType.UPDATE);
	seriesDb.updateMultipleSeries(series, QueryType.UPDATE);
	pHandler.setLastUpdate(getCurrentServerTime());
    }


}
