package tvseries;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;


public class Tester
{
    public static void main(String[] args) throws SQLException, IOException {
	SeriesFrame sFrame = new SeriesFrame();
	CreateDatabase createDatabase = new CreateDatabase("tvseries");

	/* fetches all ids from db
	ArrayList<String> idList = TVDBDataMapper.selectAllIds();

	String searchInput = JOptionPane.showInputDialog("Enter the name of the series");
	URLHandler urlHandler = new URLHandler(searchInput);

	XMLReader xmlReader = new XMLReader(searchInput, "url");
	int number = xmlReader.result.size();
	String[] res = xmlReader.result.keySet().toArray(new String[number]);

	String input = (String) JOptionPane
		.showInputDialog(null, "Choose a series", "Search results", JOptionPane.QUESTION_MESSAGE, null, res,
				 // Array of choices
				 res[0]);

	String show = input.replaceAll("\\'", "");

	urlHandler.setZipUrl(xmlReader.result.get(input));

	System.out.println(urlHandler.zipUrl);

	TVDBDataMapper.initialData(input, xmlReader.result.get(input));

	DownloadZip.downloadFile(urlHandler.zipUrl);

	UnZip.unZipIt("/home/freis685/tvseries/seriesdata/en.zip");

	File xmlFile = new File("/home/freis685/tvseries/seriesdata/en.xml");
	TVDBDataMapper.Update(xmlFile, xmlReader.result.get(input));

	Series s = TVDBDataMapper.findByTvDbId(xmlReader.result.get(input));
	System.out.println(s);*/





    }
}
