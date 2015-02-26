package tvseries;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.*;

public class TVDBDataMapper {

    public static void Insert(File xmlfile) throws SQLException {
	try {
	    DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
	    DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
	    Document doc = docBuilder.parse(xmlfile);
	    doc.getDocumentElement().normalize();
	    NodeList nList = doc.getElementsByTagName("Series");

	    Connection db = DriverManager.getConnection("jdbc:sqlite:tvseries.db");
	    String statement = "UPDATE series " +
			       "SET network=?, airday=?, airtime=?, overview=? " +
			       "WHERE thetvdb_id=121361";
	    PreparedStatement dbStatement = db.prepareStatement(statement);

	    for (int temp = 0; temp < nList.getLength(); temp++) {
		Node nNode = nList.item(temp);

		if (nNode.getNodeType() == Node.ELEMENT_NODE) {

		    Element eElement = (Element) nNode;

		    dbStatement.setString(1, eElement.getElementsByTagName("Network").item(0).getTextContent());
		    dbStatement.setString(2, eElement.getElementsByTagName("Airs_DayOfWeek").item(0).getTextContent());
		    dbStatement.setString(3, eElement.getElementsByTagName("Airs_Time").item(0).getTextContent());
		    dbStatement.setString(4, eElement.getElementsByTagName("Overview").item(0).getTextContent());


		}
	    }
	    dbStatement.executeUpdate();
	} catch (SQLException e) {
		throw new SQLException("Something went wrong", e);
	} catch (ParserConfigurationException e) {
	    e.printStackTrace();
	} catch (Exception e) {
	    e.printStackTrace();
    }}

    public static void main(String[] args) throws Exception{
	File xml = new File("/home/freis685/tvseries/seriesdata/en.xml");
	Insert(xml);
    }

}
