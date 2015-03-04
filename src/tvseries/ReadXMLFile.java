package tvseries;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.swing.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Tutorial:
 * http://www.mkyong.com/java/how-to-read-xml-file-in-java-dom-parser/
 */

// thetvdb api: 6A988698B3E59C3C

public class ReadXMLFile {
    public static void main(String[] args) throws Exception{
        // like python dictionary
        Map<String, String> nameAndId = new HashMap<String, String>();
        URLHandler.searchURL("game of thrones");
        String searchInput = JOptionPane.showInputDialog("Enter the name of the series");
        URL searchURL = new URL(" http://thetvdb.com/api/GetSeries.php?seriesname=" + searchInput.replaceAll(" ", "%20"));


        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();

            // can take data steam instead of file
            Document doc = dBuilder.parse(searchURL.openStream());

            doc.getDocumentElement().normalize();

            //System.out.println("Root element :" + doc.getDocumentElement().getNodeName();

            // creates? nodes at every <Series> tag, check the XML data
            NodeList nList = doc.getElementsByTagName("Series");
            System.out.println(nList.getLength());

            //System.out.println("-------------------------------------");

            for (int temp = 0; temp < nList.getLength(); temp++) {
                Node nNode = nList.item(temp);

                //System.out.println("\nCurrent Element :" + nNode.getNodeName());

                if (nNode.getNodeType() == Node.ELEMENT_NODE) {

                    Element eElement = (Element) nNode;

                    String name = eElement.getElementsByTagName("SeriesName").item(0).getTextContent();
                    String id = eElement.getElementsByTagName("seriesid").item(0).getTextContent();
                    nameAndId.put(name, id);
                    //name = name.replaceAll("\\'", "");
                    //System.out.println(name);
                    //DBConnection.openDB(DbType.INSERT, "(" + id + ", '" + name + "');");

                    //System.out.println("Series  : " + eElement.getElementsByTagName("SeriesName").item(0).getTextContent());
                    //System.out.println("Series id : " + eElement.getElementsByTagName("seriesid").item(0).getTextContent());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        /*for (String s : nameAndId.keySet()) {
                    System.out.println(s + " " + "(" + nameAndId.get(s) + ")");
        }*/
        int number = nameAndId.size();
        String[] res = nameAndId.keySet().toArray(new String[number]);

        String input = (String) JOptionPane
        		.showInputDialog(null, "Choose a series", "Search results", JOptionPane.QUESTION_MESSAGE, null,
                                         res, // Array of choices
                                         res[0]);

        String show = input.replaceAll("\\'", "");
        //DBConnection.openDB(DbType.INSERT, "(" + nameAndId.get(input) + ", '" + show + "');");


        /*String something = JOptionPane.showInputDialog("Pick one");
        if (valid.contains(something)) {
            System.out.println("yay!");
        }

        /*JComboBox resultList = new JComboBox(res);
        resultList.setSelectedIndex(0);
        resultList.addActionListener(this);
        System.out.println(Arrays.toString(res));*/

    }
}
