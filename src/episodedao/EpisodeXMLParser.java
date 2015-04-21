package episodedao;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import seriesdao.Series;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class EpisodeXMLParser
{
    private static final String XML_FILE_PATH = "showdata/%d/en.xml";

    /**
     * Parses the xml document for information about the specified series
     * @param seriesID the tvdb id of the series to be parsed
     * @return the parsed Series object
     */
    public List<Episode> getEpisodes(int seriesID) {
	File xmlFile = new File(String.format(XML_FILE_PATH, seriesID));
	DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

	List<Episode> episodes = new ArrayList<>();
	if (xmlFile.exists()) {
	    try {
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document document = builder.parse(new File(String.format(XML_FILE_PATH, seriesID)));
		document.getDocumentElement().normalize();

		NodeList seriesNodeList = document.getElementsByTagName("Episode");
		for (int i = 0; i < seriesNodeList.getLength(); i++) {
		    Episode episode = new Episode();
		    NodeList dataNodeList = seriesNodeList.item(i).getChildNodes();
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


	    } catch (ParserConfigurationException | SAXException | IOException e) {
		e.printStackTrace();
	    }
	} else {
	    System.out.println("Could not locate " + String.format(XML_FILE_PATH, seriesID));
	}
	return episodes;
    }

    public static void main(String[] args) {
	EpisodeXMLParser episodeParser = new EpisodeXMLParser();
	episodeParser.getEpisodes(257655);
    }
}

