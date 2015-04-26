package parser;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;

public abstract class XMLParser{
    protected static final String XML_INFO_FILE_PATH = "showdata/%d/en.xml";
    protected static final String XML_IMAGES_FILE_PATH = "showdata/%d/banners.xml";

    protected NodeList parseSeriesInfo(int seriesID, ParseType parseType) {
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
}
