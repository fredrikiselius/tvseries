package tvseries;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class XMLReader
{
    private String xmlSource;
    private String sourceType;
    public static NodeList nodeList = null;
    public Map<String, String> result = new HashMap<String, String>();


    public XMLReader(String xmlSource, String sourceType) {
	this.xmlSource = xmlSource;
	this.sourceType = sourceType;
	fetchData();
    }


    private void createNodeList() {
	try {
	    DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
	    DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
	    Document doc = null;

	    if (sourceType == "file") {
		doc = docBuilder.parse(new File(xmlSource));
	    } else if (sourceType == "url") {
		    doc = docBuilder.parse(new URL("http://thetvdb.com/api/GetSeries.php?seriesname="+xmlSource).openStream());
	    } else {
		    System.out.println("ERROR");
	    }

	    if (doc != null) {
		doc.getDocumentElement().normalize();
		nodeList = doc.getElementsByTagName("Series");
	    }
	} catch (ParserConfigurationException e) {
	    e.printStackTrace();
	} catch (IOException e) {
	    e.printStackTrace();
	} catch (SAXException e) {
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
	    System.out.println("LOG: Found "+result.keySet().size()+" matches");
	} else {
	    System.out.println("LOG: (XMLReader) search gave no results");
	}
    }
}
