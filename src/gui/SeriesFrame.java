package gui;

import database.QueryType;
import episodedao.Episode;
import episodedao.EpisodeDaoSQLite;
import net.miginfocom.swing.MigLayout;
import parser.ParseType;
import parser.XMLParser;
import seriesdao.Series;
import seriesdao.SeriesDaoSQLite;
import tvseries.FileHandler;
import parser.UrlXMLReader;

import javax.swing.*;
import javax.swing.border.Border;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.io.IOException;
import java.util.*;
import java.util.List;

public class SeriesFrame extends JFrame implements ViewListener
{

    private final static int PREFERRED_FRAME_WIDTH = 1280;
    private final static int PREFERRED_FRAM_HEIGHT = 720;
    private final static int MIN_FRAME_WIDTH = 800;
    private final static int MIN_FRAME_HEIGHT = 600;
    private final static int MENU_WIDTH = 200;

    private Map<String, String> searchResults = new HashMap<String, String>();


    // Custom borders and colors
    private Border darkBorder = BorderFactory.createLineBorder(Color.decode("#444444"), 1);
    JList<String> resultList = null;


    private JScrollPane mySeries;
    private JTextArea watchTimeDisplay;

    // views
    private MultipleSeriesView msv;
    private SingleSeriesView ssv;


    public SeriesFrame() {
	super("Omega");
	initGUI();
    }

    private void initGUI() {
	setExtendedState(Frame.MAXIMIZED_BOTH);
	setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
	setPreferredSize(new Dimension(PREFERRED_FRAME_WIDTH, PREFERRED_FRAM_HEIGHT));
	setMinimumSize(new Dimension(MIN_FRAME_WIDTH, MIN_FRAME_HEIGHT));

	final JPanel contentPane = new JPanel();
	contentPane.setLayout(new MigLayout("fill", "[" + MENU_WIDTH + "px][grow]", "[grow]"));

	// add multipleseriesview to a scrollpane
	msv = new MultipleSeriesView();
	msv.addViewListener(this);
	mySeries = new JScrollPane(msv);
	mySeries.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
	mySeries.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
	mySeries.setBorder(BorderFactory.createEmptyBorder());

	// adds everything to the contentpane
	GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
	contentPane.add(createLeftMenu(), "west, w " + MENU_WIDTH + "!");
	contentPane.add(mySeries, "north, w " + (gd.getDisplayMode().getWidth() - MENU_WIDTH) +
				  "!, pushy, grow, wrap");

	setContentPane(contentPane);
	setVisible(true);
	pack();
    }


    /**
     * Creates the left bar containing the search window
     *
     * @return
     */
    private JPanel createLeftMenu() {
	JPanel menuPane = new JPanel(new MigLayout("", "[]", "[][::" + MENU_WIDTH + "px][]"));
	JButton addBtn = new JButton("Add");
	JLabel searchLabel = new JLabel("Search:");
	JTextField searchField = new JTextField();
	JScrollPane resultScroll = new JScrollPane(resultList);
	resultScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
	JLabel timeLabel = new JLabel("Total watch time:");
	watchTimeDisplay = new JTextArea();

	resultScroll.setVisible(false);
	addBtn.setVisible(false);

	//set style to the menu pane
	menuPane.setBorder(darkBorder);
	menuPane.setBackground(Color.decode("#222222"));

	// set style to the searchlabel
	searchLabel.setForeground(Color.decode("#999999"));

	// set style to the searchfield
	searchField.setBorder(darkBorder);
	searchField.setBackground(Color.decode("#191919"));
	searchField.setForeground(Color.decode("#999999"));

	// set style to time label
	timeLabel.setForeground(Color.decode("#999999"));

	// set style to watch time field
	watchTimeDisplay.setBorder(darkBorder);
	watchTimeDisplay.setBackground(Color.decode("#191919"));
	watchTimeDisplay.setForeground(Color.decode("#999999"));
	watchTimeDisplay.setText(getTotalWatchTime() + "");

	// set style to the result window
	resultScroll.setBorder(darkBorder);
	resultScroll.setBackground(Color.decode("#191919"));
	resultScroll.setForeground(Color.decode("#999999"));
	resultScroll.getViewport().setBackground(Color.BLACK);

	// set style to the add button
	addBtn.setBorder(darkBorder);
	addBtn.setBackground(Color.decode("#191919"));
	addBtn.setForeground(Color.decode("#999999"));

	// add everything to the menu pane
	menuPane.add(searchLabel, "left, wrap");
	menuPane.add(searchField, "left, pushx, growx, wrap");
	menuPane.add(resultScroll, "left, pushx, growx, wrap");
	menuPane.add(addBtn, "right, wrap");
	menuPane.add(timeLabel, "wrap");
	menuPane.add(watchTimeDisplay, "grow");

	// adds action to search on enter
	searchField.addActionListener(new ActionListener()
	{
	    @Override public void actionPerformed(final ActionEvent e) {
		String searchString = searchField.getText();

		if (!searchString.isEmpty()) {
		    updateResultScroll(searchString);
		    resultScroll.setViewportView(resultList);
		    resultScroll.revalidate();
		    resultScroll.setVisible(true);

		    addBtn.setVisible(true);
		    searchField.setText("");
		}


	    }
	});

	// Adds a new show
	addBtn.addActionListener(new ActionListener()
	{
	    @Override public void actionPerformed(final ActionEvent e) {
		// Hides the resultpane and button
		resultScroll.setVisible(false);
		addBtn.setVisible(false);

		new SwingWorker<Void, Void>()
		{
		    @Override public Void doInBackground() {
			// get series id
			String name = resultList.getSelectedValue();
			int id = Integer.parseInt(searchResults.get(name));

			SeriesDaoSQLite seriesDb = new SeriesDaoSQLite();
			List<String> seriesInDb = seriesDb.selectAllIds();


			if (!seriesInDb.contains(id + "")) {
			    addShow(id);
			} else {
			    System.out.println("The series " + "'" + name + "'" + " is already in the database.");
			}
			return null;
		    }

		    @Override public void done() {
			System.out.println("LOG: (SeriesFrame) Done saving changes.");

			resultList = null;
			searchResults.clear();
			searchField.setEditable(true);

		    }
		}.execute();
		mySeries.setPreferredSize(msv.getPreferredSize());
		mySeries.getViewport().revalidate();
		mySeries.getViewport().repaint();
	    }
	});
	return menuPane;
    }

    private void addShow(int id) {
	try {
	    FileHandler.fetchZip(id);

	    // parse show
	    XMLParser xmlParser = new XMLParser();
	    HashMap<ParseType, String> imagePaths = xmlParser.getImageURLs(id);


	    Series series = xmlParser.getSeries(id);


	    // fetch showart
	    FileHandler.fetchPoster(imagePaths.get(ParseType.POSTER), id);
	    FileHandler.fetchFanart(imagePaths.get(ParseType.FANART), id);


	    // write Series to db
	    SeriesDaoSQLite seriesDb = new SeriesDaoSQLite();
	    seriesDb.updateSeries(series, QueryType.INSERT);

	    // write episodes to db

	    List<Episode> parsedEpisodes = xmlParser.getEpisodes(id);

	    EpisodeDaoSQLite episodeDb = new EpisodeDaoSQLite();
	    episodeDb.updateMultipleEpisodes(parsedEpisodes, QueryType.INSERT);

	    //update view
	    msv.addSeriesToView(series);
	    msv.updateView();
	} catch (IOException e) {
	    e.printStackTrace();
	}
    }


    private void updateResultScroll(String searchString) {
	System.out.println("LOG: (SeriesFrame) Searching THETVDB for: " + searchString);

	UrlXMLReader xmlReader = new UrlXMLReader(searchString.replaceAll(" ", "%20"));
	searchResults = xmlReader.result;
	String[] shows = new String[searchResults.keySet().size()];
	int iterIndex = 0;
	for (String showName : searchResults.keySet()) {
	    shows[iterIndex] = showName;
	    iterIndex++;
	}
	resultList = new JList<>(shows);


    }


    public void setSingleSeriesView(SingleSeriesView ssv) {
	this.ssv = ssv;
	this.ssv.addViewListener(this);
	this.remove(mySeries);
	this.add(ssv, "gap 0, top, growx, pushx");
	this.revalidate();
	this.repaint();
    }


    public void multipleViewChanged(SingleSeriesView ssv) {
	setSingleSeriesView(ssv);
    }


    public void singleViewChanged() {
	this.remove(ssv);
	this.add(mySeries, "north, w " + msv.getPreferredSize().width +
			   "!, h " + msv.getPreferredSize().height + ", pushy, growy, wrap");
	msv.updateView();
	this.revalidate();
	this.repaint();
    }

    public void totalTimeChanged() {
	watchTimeDisplay.setText(getTotalWatchTime());
	watchTimeDisplay.revalidate();
	watchTimeDisplay.repaint();
    }

    private String getTotalWatchTime() {
	int minutesWatched = 0; // in min
	int episodesWatched = 0;


	EpisodeDaoSQLite episodeDb = new EpisodeDaoSQLite();
	for (Series series : msv.getLoadedSeries()) {
	    List<Episode> episodes = episodeDb.getAllEpisodes(series.getTvDbId());
	    for (Episode episode : episodes) {
		int watchCount = episode.getWatchCount();
		if (watchCount > 0) {
		    episodesWatched += watchCount;
		    minutesWatched += watchCount * series.getRuntime();
		}
	    }
	}

	StringBuilder builder = new StringBuilder();
	builder.append("Episodes: " + episodesWatched + "\n");

	int monthsWatched = minutesWatched / 43829;
	minutesWatched %= 43829;
	builder.append("Months: " + monthsWatched + "\n");


	int daysWatched = minutesWatched / 1440;
	minutesWatched %= 1440;
	builder.append("Days: " + daysWatched + "\n");


	int hoursWatched = minutesWatched / 60;
	minutesWatched %= 60;
	builder.append("Hours: " + hoursWatched + "\n");
	builder.append("Minutes: " + minutesWatched);


	return builder.toString();
    }

}

