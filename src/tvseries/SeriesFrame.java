package tvseries;

import java.awt.*;

import javafx.event.ActionEvent;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SeriesFrame extends JFrame
{
    Map<String, Series> series = new HashMap<String, Series>();

    public SeriesFrame() {
	super("Omega");
	fetchSeries();

	this.setLayout(new MigLayout());
	this.add(mySeries(), "wrap");
	this.add(searchBox());
	this.pack();
	this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	this.setVisible(true);
	this.setSize(600, 400);
    }

    private JPanel mySeries() {
	ArrayList<String> idList = TVDBDataMapper.selectAllIds();
	JList showList;
	System.out.println(idList.size());
	if (idList.size() >= 1) {
	    String shows[] = new String[idList.size()];

	    int showIndex = 0;

	    for (Series series1 : series.values()) {
		System.out.println(series1);
	    }
	    for (String id : series.keySet()) {
		System.out.println(id);
		shows[showIndex] = series.get(id).getShowName();
		showIndex++;
	    }

	    showList = new JList(shows);
	} else {
	    showList = new JList(new String[]{null});
	}

	JScrollPane scrollPane = new JScrollPane(showList);
	scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

	JScrollBar scrollBar = scrollPane.getVerticalScrollBar();


	JPanel mySeriesPanel = new JPanel();
	mySeriesPanel.add(scrollPane);


	mySeriesPanel.setLayout(new MigLayout());
	mySeriesPanel.setSize(150, 400);
	return mySeriesPanel;
    }

    private void updateMySeries() {
	this.removeAll();
	System.out.println("ashd");
	this.revalidate();
	this.repaint();

    }

    private JPanel searchBox() {
	final JTextField searchField = new JTextField("Search");
	JButton searchButton = new JButton("Go");
	JPanel searchBox = new JPanel();

	searchBox.setLayout(new MigLayout());


	searchBox.add(searchField, "width :100, height 30:");
	searchBox.add(searchButton, "width :10:55, height 30:");

	searchButton.addActionListener(new ActionListener()
	{
	    @Override public void actionPerformed(final java.awt.event.ActionEvent e) {
		try {
		    String searchInput = searchField.getText().replaceAll(" ", "%20");

		    XMLReader xmlReader = new XMLReader(searchInput, "url");
		    int number = xmlReader.result.size();
		    String[] res = xmlReader.result.keySet().toArray(new String[number]);

		    String input = (String) JOptionPane
			    .showInputDialog(null, "Choose a series", "Search results", JOptionPane.QUESTION_MESSAGE, null, res,
					     // Array of choices
					     res[0]);


		    URLHandler urlHandler = new URLHandler(searchInput);
		    urlHandler.setZipUrl(xmlReader.result.get(input));

		    System.out.println(urlHandler.zipUrl);

		    TVDBDataMapper.initialData(input, xmlReader.result.get(input));

		    DownloadZip.downloadFile(urlHandler.zipUrl);

		    UnZip.unZipIt("/home/freis685/tvseries/seriesdata/en.zip");

		    File xmlFile = new File("/home/freis685/tvseries/seriesdata/en.xml");
		    TVDBDataMapper.Update(xmlFile, xmlReader.result.get(input));
		    updateMySeries();
		} catch (SQLException sqle) {
		    System.out.println(sqle);
		} catch (IOException ioe) {
		    System.out.println(ioe);
		}
	    }
	});

	return searchBox;

    }

    private void fetchSeries() {
	ArrayList<String> idList = TVDBDataMapper.selectAllIds();
	if (series.keySet().size() > 1) {
	    for (String id : idList) {
		System.out.println(id);
		series.put(id, TVDBDataMapper.findByTvDbId(id));
		System.out.println(id + " " + TVDBDataMapper.findByTvDbId(id));
	    }
	}


    }
}
