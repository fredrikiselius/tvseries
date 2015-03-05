package tvseries;


import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.border.Border;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SeriesFrame extends JFrame
{
    final static int FRAME_WIDTH = 800;
    final static int FRAME_HEIGHT = 600;
    final static int MYSERIES_WIDTH = 150;
    Map<String, Series> series = new HashMap<String, Series>();
    Map<String, String> searchResults = new HashMap<String, String>();
    private int nSeries;
    JList<String> resultList = null;

    public SeriesFrame() {
	super("Omega");
	initializeUI();
	this.setVisible(true);
    }

    private void initializeUI() {
	fetchSeries();
	setLayout(new MigLayout("fillx", "[][]", "[grow][grow]"));
	setPreferredSize(new Dimension(FRAME_WIDTH, FRAME_HEIGHT));
	setLocationRelativeTo(null);
	setExtendedState(Frame.MAXIMIZED_BOTH);
	setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	add(createLeftMenu(), "spany, growy, west, width 200:");
	add(createMySeries(), "spany, growy, wmax 100%");
	pack();
    }

    private JPanel createLeftMenu() {
	Border darkBorder = BorderFactory.createLineBorder(Color.decode("#444444"), 1);


	JPanel panel = new JPanel();
	final JButton addBtn = new JButton("Add");
	final JTextField searchField = new JTextField();
	final JScrollPane resultScroll = new JScrollPane(resultList);
	resultScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
	JScrollBar scrollBar = resultScroll.getVerticalScrollBar();

	resultScroll.setVisible(false);
	addBtn.setVisible(false);


	panel.setLayout(new MigLayout("debug", "[center]", "[top]"));
	panel.setBackground(Color.decode("#222222"));

	searchField.setBorder(darkBorder);
	searchField.setBackground(Color.decode("#191919"));
	searchField.setForeground(Color.decode("#999999"));

	resultScroll.setBorder(darkBorder);
	resultScroll.setBackground(Color.decode("#191919"));
	resultScroll.setForeground(Color.decode("#999999"));
	resultScroll.getViewport().setBackground(Color.BLACK);

	addBtn.setBorder(darkBorder);
	addBtn.setBackground(Color.decode("#191919"));
	addBtn.setForeground(Color.decode("#999999"));


	panel.add(searchField, "pushx, width 200:, height 20:, wrap");
	panel.add(resultScroll, "pushx, width 200, height 100, wrap");
	panel.add(addBtn, "al right, width 40:, height 20:");


	searchField.addActionListener(new ActionListener()
	{
	    @Override public void actionPerformed(final ActionEvent e) {
		updateResultScroll(searchField.getText());
		resultScroll.setViewportView(resultList);


		resultList.setOpaque(true);
		resultScroll.revalidate();
		resultScroll.setVisible(true);
		addBtn.setVisible(true);
		searchField.setText("");

	    }
	});

	addBtn.addActionListener(new ActionListener()
	{
	    @Override public void actionPerformed(final ActionEvent e) {
		resultScroll.setVisible(false);
		addBtn.setVisible(false);
		String name = resultList.getSelectedValue();
		String id = searchResults.get(name);
		System.out.println("Added " + name + searchResults.get(name));
		try {
		    TVDBDataMapper.initialData(name, id);
		    DownloadZip.downloadFile(URLHandler.ZipUrl(id));
		    UnZip.unZipIt();
		    TVDBDataMapper.Update(id);
		} catch (SQLException sqlex) {
		    sqlex.printStackTrace();
		} catch (IOException ioex) {
		    ioex.printStackTrace();
		}

		resultList = null;
		searchResults.clear();

	    }
	});


	return panel;
    }

    private void updateResultScroll(String searchString) {
	if (searchString != null) {
	    XMLReader xmlReader = new XMLReader(searchString.replaceAll(" ", "%20"), "url");
	    searchResults = xmlReader.result;
	    String[] shows = new String[searchResults.keySet().size()];
	    int iterIndex = 0;
	    for (String showName : searchResults.keySet()) {
		shows[iterIndex] = showName;
		iterIndex++;
	    }

	    resultList = new JList<String>(shows);

	}
    }

    private JPanel createMySeries() {
	JPanel mySeries = new JPanel();
	mySeries.setLayout(new MigLayout(
		"debug, fillx",
		"",
		""));
	//number of series in db

	for (String id : series.keySet()) {
	    mySeries.add(createSeriesPanel(id), "gap 10px 10px 10px 10px, width 140::140 , height 200::200");
	}

	return mySeries;
    }

    private JPanel createSeriesPanel(String id) {
	Series ser = series.get(id);
	JPanel seriesPanel = new JPanel();
	JPanel picHolder = new JPanel();
	JLabel serName = new JLabel(ser.getShowName());
	JLabel serNetwork = new JLabel(ser.getNetwork());

	picHolder.setBackground(Color.BLUE);

	seriesPanel.setLayout(new MigLayout("", "[]", "[][][]"));
	seriesPanel.add(picHolder, "w 100%, h 80%, wrap");
	seriesPanel.add(serName, "al left, wrap");
	seriesPanel.add(serNetwork, "");
	return seriesPanel;
    }

    private JPanel mySeriesOld() {
	List<String> idList = null;
	idList = TVDBDataMapper.selectAllIds();
	JList showList;
	if (idList != null) {
	    String[] shows = new String[idList.size()];

	    int showIndex = 0;

	    for (Series series1 : series.values()) {
	    }
	    for (String id : series.keySet()) {
		shows[showIndex] = series.get(id).getShowName();
		showIndex++;
	    }

	    showList = new JList(shows);
	} else {
	    showList = new JList(new String[] { null });
	}

	JScrollPane scrollPane = new JScrollPane(showList);
	scrollPane.setSize(new Dimension(150, 700));
	scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

	JScrollBar scrollBar = scrollPane.getVerticalScrollBar();


	JPanel mySeriesPanel = new JPanel();
	mySeriesPanel.add(scrollPane);


	mySeriesPanel.setLayout(new MigLayout());
	mySeriesPanel.setSize(MYSERIES_WIDTH, FRAME_HEIGHT);
	mySeriesPanel.setBackground(Color.BLACK);
	return mySeriesPanel;
    }

    private void updateMySeries() {
	fetchSeries();
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
	    @Override public void actionPerformed(final ActionEvent e) {
		try {
		    String searchInput = searchField.getText().replaceAll(" ", "%20");
		    XMLReader xmlReader = new XMLReader(searchInput, "url");
		    int number = xmlReader.result.size();
		    String[] res = xmlReader.result.keySet().toArray(new String[number]);

		    String name = (String) JOptionPane
			    .showInputDialog(null, "Choose a series", "Search results", JOptionPane.QUESTION_MESSAGE, null, res,
					     res[0]);
		    String id = xmlReader.result.get(name);

		    if (name != null && id != null) {
			TVDBDataMapper.initialData(name, xmlReader.result.get(name));
			DownloadZip.downloadFile(URLHandler.ZipUrl(id));
			UnZip.unZipIt();
			TVDBDataMapper.Update(id);
			updateMySeries();
		    }

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
	System.out.println("Fetching ids from database");
	ArrayList<String> idList = null;
	idList = TVDBDataMapper.selectAllIds();
	if (idList != null) {
	    nSeries = idList.size();
	    for (String id : idList) {
		series.put(id, TVDBDataMapper.findByTvDbId(id));
	    }
	}
    }
}
