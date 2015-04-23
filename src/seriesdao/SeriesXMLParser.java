package seriesdao;

import org.w3c.dom.Document;

import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import parser.ParseType;
import parser.XMLParser;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;

public class SeriesXMLParser extends XMLParser
{


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

	return series;
    }
}
