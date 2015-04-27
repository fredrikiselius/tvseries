package parser;

import episodedao.Episode;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import seriesdao.Series;


import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class XMLParser{
    private static final String XML_INFO_FILE_PATH = "showdata/%d/en.xml";
    private static final String XML_IMAGES_FILE_PATH = "showdata/%d/banners.xml";

    private NodeList parseSeriesInfo(int seriesID, ParseType parseType) {
	File xmlFile = new File(String.format(XML_INFO_FILE_PATH, seriesID));
	DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

	NodeList nodeList = null;
	if (xmlFile.exists()) {
	    Document document = null;
	    try {
		DocumentBuilder builder = factory.newDocumentBuilder();
		if (parseType == ParseType.IMAGE) {
		    document = builder.parse(new File(String.format(XML_IMAGES_FILE_PATH, seriesID)));
		} else {
		    document = builder.parse(new File(String.format(XML_INFO_FILE_PATH, seriesID)));
		}
		document.getDocumentElement().normalize();

		switch (parseType) {
		    case SERIES:
			nodeList = document.getElementsByTagName("Series");
			break;
		    case EPISODE:
			nodeList = document.getElementsByTagName("Episode");
			break;
		    case IMAGE:
			nodeList = document.getElementsByTagName("Banner");
			break;
		    default:
			System.out.println("Unknown parse type");
		}
	    } catch (ParserConfigurationException | SAXException | IOException e) {
		e.printStackTrace();
	    }
	} else {
	    System.out.println("Could not locate " + String.format(XML_INFO_FILE_PATH, seriesID));
	}
	return nodeList;
    }

    /**
     * Parses the xml document for information about the specified Series
     * @param seriesID the tvdb id of the Series to be parsed
     * @return the parsed Series object.
     */
    public Series getSeries(int seriesID) {
	NodeList nodeList = parseSeriesInfo(seriesID, ParseType.SERIES);

	Series series = new Series();

		for (int i = 0; i < nodeList.getLength(); i++) {
		    NodeList dataNodeList = nodeList.item(i).getChildNodes();
		    for (int j = 0; j < dataNodeList.getLength(); j++) {
			String data = dataNodeList.item(j).getTextContent().replaceAll("'", "");
			if (!data.isEmpty()) {
			    switch (dataNodeList.item(j).getNodeName()) {
				case "id":
				    series.setTvDbId(Integer.parseInt(data));
				    break;
				case "Airs_DayOfWeek":
				    series.setAirday(data);
				    break;
				case "Airs_Time":
				    series.setAirtime(data);
				    break;
				case "FirstAired": //TODO parse to date object
				    series.setFirstAired(data);
				    break;
				case "Network":
				    series.setNetwork(data);
				    break;
				case "Overview":
				    series.setOverview(data);
				    break;
				case "Runtime":
				    series.setRuntime(Integer.parseInt(data));
				    break;
				case "SeriesName":
				    series.setShowName(data);
				    break;
				case "Status":
				    series.setStatus(data);
				    break;
			    }
			}
		    }
		}
	return series;
    }

    /**
     * Parses the xml document for information about the specified Series
     * @param seriesID the tvdb id of the Series to be parsed
     * @return the parsed Series object
     */
    public List<Episode> getEpisodes(int seriesID) {
	NodeList nodeList = parseSeriesInfo(seriesID, ParseType.EPISODE);
	System.out.println(nodeList.getLength());
	List<Episode> episodes = new ArrayList<>();

	for (int i = 0; i < nodeList.getLength(); i++) {
	    Episode episode = new Episode();
	    NodeList dataNodeList = nodeList.item(i).getChildNodes();
	    for (int j = 0; j < dataNodeList.getLength(); j++) {
		String data = dataNodeList.item(j).getTextContent().replaceAll("'", "");
		switch (dataNodeList.item(j).getNodeName()) {
		    case "id":
			episode.setTvDbId(Integer.parseInt(data));
			break;
		    case "EpisodeName":
			if (!data.isEmpty()) {
			    episode.setName(data.replaceAll("'", ""));
			} else {
			    episode.setName("TBA");
			}
			break;
		    case "EpisodeNumber":
			episode.setEpisodeNumber(Integer.parseInt(data));
			break;
		    case "FirstAired": //TODO parse to date object
			episode.setFirstAired(data);
			break;
		    case "Overview":
			episode.setOverview(data.replaceAll("'", ""));
			break;
		    case "SeasonNumber":
			episode.setSeasonNumber(Integer.parseInt(data));
			break;
		    case "absolute_number":
			if (!data.isEmpty()) {
			    episode.setAbsoluteNumber(Integer.parseInt(data));
			}
			break;
		    case "seriesid":
			episode.setShowId(Integer.parseInt(data));
			break;
		}
	    }
	    episodes.add(episode);
	}
	return episodes;
    }

    public HashMap<ParseType, String> getImageURLs(int seriesID) {
	NodeList nodeList = parseSeriesInfo(seriesID, ParseType.IMAGE);

	double fanartRating = 0;
	double posterRating = 0;

	String fanartBannerPath = "";
	String posterBannerPath = "";

	HashMap<ParseType, String> imagePaths = new HashMap<>();

	for (int bannerIndex = 0; bannerIndex < nodeList.getLength(); bannerIndex++) {
	    Node bannerNode = nodeList.item(bannerIndex);
	    if (bannerNode.getNodeType() == Node.ELEMENT_NODE) {
		Element bannerElement = (Element) bannerNode;

		String bannerType = bannerElement.getElementsByTagName("BannerType").item(0).getTextContent();
		String rating = bannerElement.getElementsByTagName("Rating").item(0).getTextContent();
		// Make sure there actually is a rating to compare
		if (!rating.isEmpty()) {
		    double fetchedRating = Double.parseDouble(rating);
		    if (bannerType.equals("fanart") && fetchedRating > fanartRating) {
			System.out.println(fetchedRating);
			fanartRating = fetchedRating;
			fanartBannerPath = bannerElement.getElementsByTagName("VignettePath").item(0).getTextContent();
		    } else if (bannerType.equals("poster") && fetchedRating > posterRating) {
			posterRating = fetchedRating;
			posterBannerPath = bannerElement.getElementsByTagName("BannerPath").item(0).getTextContent();
		    }
		}
	    }
	}
	imagePaths.put(ParseType.FANART, fanartBannerPath);
	System.out.println(fanartBannerPath);
	imagePaths.put(ParseType.POSTER, posterBannerPath);
	return imagePaths;
    }
}
