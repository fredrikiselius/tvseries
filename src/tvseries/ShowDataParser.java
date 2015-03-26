package tvseries;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShowDataParser
{
    private String tvDbId;
    private String folderPath;
    private String[] artPaths;
    private List<String> showInfo = new ArrayList<String>();
    private List<Map<String, String>> episodeInfo = new ArrayList<Map<String, String>>();

    private final static String PATH = "showdata/%s/";
    private final static String[] WANTED_SERIES_INFO = new String[]{
	    "Airs_DayOfWeek", "Airs_Time", "FirstAired", "Network",
	    "Overview", "Runtime", "Status", "lastupdated"
    };
    private final static String[] WANTED_EPISODE_INFO = new String[]{
	    "id", "EpisodeName", "EpisodeNumber", "SeasonNumber", "absolute_number",
	    "Overview",

    };

    public ShowDataParser(String tvDbId) {
	String folderPath = String.format(PATH, tvDbId);
	if ((new File(String.format(PATH, tvDbId))).exists()) {
	    System.out.println("LOG: Folder found");
	    this.tvDbId = tvDbId;
	    this.folderPath = folderPath;
	} else {
	    throw new IllegalArgumentException("Could not find the folder");
	}
    }

    // Looks for the poster and fanart with the highest rating
    public void parseBanners() {
	System.out.println("LOG: (ShowDataParser) Parsing: " + folderPath + "banners.xml");
	try {
	    DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
	    DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
	    Document doc = docBuilder.parse(new File(folderPath + "banners.xml"));

	    doc.getDocumentElement().normalize();

	    NodeList banners = doc.getElementsByTagName("Banner");

	    double fanartRating = 0;
	    double posterRating = 0;

	    String fanartBannerPath = "";
	    String posterBannerPath = "";

	    for (int bannerIndex = 0; bannerIndex < banners.getLength(); bannerIndex++) {
		Node bannerNode = banners.item(bannerIndex);
		if (bannerNode.getNodeType() == Node.ELEMENT_NODE) {
		    Element bannerElement = (Element) bannerNode;

		    String bannerType = bannerElement.getElementsByTagName("BannerType").item(0).getTextContent();
		    String rating = bannerElement.getElementsByTagName("Rating").item(0).getTextContent();
		    String language = bannerElement.getElementsByTagName("Language").item(0).getTextContent();
		    // Make sure there actually is a rating to compare
		    if (!rating.isEmpty()) {
			double fetchedRating = Double.parseDouble(rating);
			if (bannerType.equals("fanart") && language.equals("en") && fetchedRating > fanartRating) {
			    fanartRating = fetchedRating;
			    fanartBannerPath = bannerElement.getElementsByTagName("VignettePath").item(0).getTextContent();
			} else if (bannerType.equals("poster") && fetchedRating > posterRating) {
			    posterRating = fetchedRating;
			    posterBannerPath = bannerElement.getElementsByTagName("BannerPath").item(0).getTextContent();
			}
		    }
		}
	    }
	    this.artPaths = new String[] { fanartBannerPath, posterBannerPath };
	} catch (IOException ioe) {
	    ioe.printStackTrace();
	} catch (SAXException se) {
	    se.printStackTrace();
	} catch (ParserConfigurationException pce) {
	    pce.printStackTrace();
	}

    }

    public void parseShow() {
	try {
	    DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
	    DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
	    Document doc = docBuilder.parse(new File(folderPath + "en.xml"));

	    doc.getDocumentElement().normalize();

	    // Only returns one show
	    NodeList showList = doc.getElementsByTagName("Series"); // Only returns one show (each xml only contain one Series tag)
	    NodeList episodeList = doc.getElementsByTagName("Episode");


	    Element showElement = (Element) showList.item(0);
	    showInfo = new ArrayList<String>();
	    for (String info : WANTED_SERIES_INFO) {
		showInfo.add(showElement.getElementsByTagName(info).item(0).getTextContent());
	    }

	    for (int episodeIndex = 0; episodeIndex < episodeList.getLength(); episodeIndex++) {
		Node episodeNode = episodeList.item(episodeIndex);
		if (episodeNode.getNodeType() == Node.ELEMENT_NODE) {
		    Element episodeElement = (Element) episodeNode;
		    String[] completeEpisode = new String[WANTED_EPISODE_INFO.length];
		    Map<String, String> epTest = new HashMap<>();
		    int indexer = 0;
		    for (String info : WANTED_EPISODE_INFO) {
			epTest.put(info, episodeElement.getElementsByTagName(info).item(0).getTextContent().replaceAll("'", "''"));
			completeEpisode[indexer] = (episodeElement.getElementsByTagName(info).item(0).getTextContent());
			indexer++;
		    }
		    episodeInfo.add(epTest);
		}
	    }
	} catch (IOException ioe) {
	    ioe.printStackTrace();
	} catch (SAXException se) {
	    se.printStackTrace();
	} catch (ParserConfigurationException pce) {
	    pce.printStackTrace();
	}
    }

    public String[] getArtPaths() {
	return artPaths;
    }

    public String getFanart() {
	return artPaths[0];
    }

    public String getPoster() {
	return artPaths[1];
    }

    public List<Map<String, String>> getAllEpisodes() {
	return episodeInfo;
    }

    public int getNumberOfEpisodes() {
	return episodeInfo.size();
    }

    public List<String> getShowData() {
	return showInfo;
    }

    public String getAirday() {
    	return showInfo.get(0);
    }

    public String getAirtime() {
    	return showInfo.get(1);
    }

    public String getFirstAired() {
    	return showInfo.get(2);
    }

    public String getNetwork() {
	return showInfo.get(3);
    }

    public String getOverview() {
    	return showInfo.get(4).replaceAll("\'", "");
    }

    public String getRuntime() {
    	return showInfo.get(5);
    }

    public String getStatus() {
    	return showInfo.get(6);
    }

    public String getLastUpdated() {
    	return showInfo.get(7);
    }

    /**
     * Return format:
     * Airs_DayOfWeek, Airs_Time, FirstAired, Network,
     * Overview, Runtime, Status, lastupdated
     * @return all show data as one string
     */
    public String getShowDataAsString() {
	String allData = "";
	for (int i = 0; i < showInfo.size(); i++) {
	    if (i == showInfo.size()-1) {
		allData += showInfo.get(i);
	    } else {
		allData += (showInfo.get(i) + ", ");
	    }
	}
	return allData;
    }

}
