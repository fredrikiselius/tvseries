package seriesdao;

import org.w3c.dom.Document;

import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;

public class SeriesXMLParser
{
    private static final String XML_FILE_PATH = "showdata/%d/en.xml";

    /**
     * Parses the xml document for information about the specified series
     * @param seriesID the tvdb id of the series to be parsed
     * @return the parsed series object.
     */
    public series getSeries(int seriesID) {
	File xmlFile = new File(String.format(XML_FILE_PATH, seriesID));
	DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

	series series = new series();
	if (xmlFile.exists()) {
	    try {
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document document = builder.parse(new File(String.format(XML_FILE_PATH, seriesID)));
		document.getDocumentElement().normalize();

		NodeList seriesNodeList = document.getElementsByTagName("series");
		for (int i = 0; i < seriesNodeList.getLength(); i++) {
		    NodeList dataNodeList = seriesNodeList.item(i).getChildNodes();
		    for (int j = 0; j < dataNodeList.getLength(); j++) {
			String data = dataNodeList.item(j).getTextContent().replaceAll("'", "");
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
				series.setRuntime(data);
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


	    } catch (ParserConfigurationException | SAXException | IOException e) {
		e.printStackTrace();
	    }
	} else {
	    System.out.println("Could not locate " + String.format(XML_FILE_PATH, seriesID));
	}
	return series;
    }
}
