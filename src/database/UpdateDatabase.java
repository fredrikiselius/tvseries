package database;

import episodedao.Episode;
import episodedao.EpisodeDaoSQLite;
import episodedao.EpisodeXMLParser;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import seriesdao.Series;
import seriesdao.SeriesDaoSQLite;
import seriesdao.SeriesXMLParser;
import tvseries.FileHandler;
import tvseries.PropHandler;


import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class UpdateDatabase extends DBHandler {

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

    public static void update() {
	PropHandler pHandler = new PropHandler();
	SeriesDaoSQLite seriesDb = new SeriesDaoSQLite();
	EpisodeDaoSQLite episodeDb = new EpisodeDaoSQLite();
	SeriesXMLParser seriesParser = new SeriesXMLParser();
	EpisodeXMLParser episodeParser = new EpisodeXMLParser();

	UpdateDatabase dbUpdate = new UpdateDatabase();

	List<String> seriesInDb = seriesDb.selectAllIds();
	List<String> seriesUpdatedSinceLast = dbUpdate.getSeriesToUpdate(pHandler.getLastUpdate());
	List<String> seriesToUpdate = new ArrayList<>();

	for (String id : seriesUpdatedSinceLast) {
	    if (seriesInDb.contains(id)) {
		seriesToUpdate.add(id);
		System.out.println("Series ID TO UPDATE: " + id);
	    }
	}

	List<Series> series = new ArrayList<>();
	List<Episode> episodes = new ArrayList<>();
	for (String idString : seriesToUpdate) {
	    int id = Integer.parseInt(idString);
	    // fetch new data
	    try {
		FileHandler.fetchZip(id);
	    } catch (IOException e) {
		e.printStackTrace();
	    }

	    // parse new data
	    Series s = seriesParser.getSeries(id);
	    List<Episode> parsedEpisodes = episodeParser.getEpisodes(id);

	    series.add(s);
	    episodes.addAll(parsedEpisodes);
	}

	episodeDb.updateMultipleEpisodes(episodes);
	seriesDb.updateMultipleSeries(series);
	pHandler.setLastUpdate(getCurrentServerTime());
    }


}
