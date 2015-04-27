package parser;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class UrlXMLReader
{
    private String xmlSource;
    public static NodeList nodeList = null;
    public Map<String, String> result = new HashMap<String, String>();


    public UrlXMLReader(String xmlSource) {
	this.xmlSource = xmlSource;
	fetchData();
    }


    private void createNodeList() {
	Document document;
	try {
	    DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
	    DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();

	    document = docBuilder.parse(new URL("http://thetvdb.com/api/GetSeries.php?seriesname="+xmlSource).openStream());

	    if (document != null) {
		document.getDocumentElement().normalize();
		nodeList = document.getElementsByTagName("Series");
	    }
	} catch (ParserConfigurationException | IOException | SAXException e) {
	    e.printStackTrace();
	}
    }

    private void fetchData() {
	createNodeList();
	if (nodeList.getLength() != 0) {
	    for (int i = 0; i < nodeList.getLength(); i++) {
		Node node = nodeList.item(i);
		if (node.getNodeType() == Node.ELEMENT_NODE) {
		    Element element = (Element) node;

		    String name = element.getElementsByTagName("SeriesName").item(0).getTextContent();
		    String id = element.getElementsByTagName("seriesid").item(0).getTextContent();

		    result.put(name, id);
		}

	    }
	    System.out.println("LOG: (XMLReader) Found " + result.keySet().size() + " matches.");
	} else {
	    System.out.println("LOG: (XMLReader) Search gave no results.");
	}
    }
}
