package se.liu.ida.freis685poniv820.tddd78.tvseries.gui;

import se.liu.ida.freis685poniv820.tddd78.tvseries.database.QueryType;
import se.liu.ida.freis685poniv820.tddd78.tvseries.episodedao.Episode;
import se.liu.ida.freis685poniv820.tddd78.tvseries.episodedao.EpisodeDao;
import se.liu.ida.freis685poniv820.tddd78.tvseries.episodedao.EpisodeDaoSQLite;
import net.miginfocom.swing.MigLayout;
import se.liu.ida.freis685poniv820.tddd78.tvseries.parser.ParseType;
import se.liu.ida.freis685poniv820.tddd78.tvseries.parser.XMLParser;
import se.liu.ida.freis685poniv820.tddd78.tvseries.seriesdao.Series;
import se.liu.ida.freis685poniv820.tddd78.tvseries.seriesdao.SeriesDao;
import se.liu.ida.freis685poniv820.tddd78.tvseries.seriesdao.SeriesDaoSQLite;
import se.liu.ida.freis685poniv820.tddd78.tvseries.tvseries.FileHandler;
import se.liu.ida.freis685poniv820.tddd78.tvseries.parser.UrlXMLReader;

import javax.swing.*;
import javax.swing.border.Border;

import java.awt.*;

import java.util.*;
import java.util.List;

/**
 * SeriesFrame is the main frame of the application. It is launched at the start of the program and can display two different viewa,
 * MultipleSeriesView and SingleSeriesView. The MultipleSeriesView is the one displayed at launch.
 */
public class SeriesFrame extends JFrame implements ViewListener {

    private final static int PREFERRED_FRAME_WIDTH = 1280;
    private final static int PREFERRED_FRAM_HEIGHT = 720;
    private final static int MIN_FRAME_WIDTH = 800;
    private final static int MIN_FRAME_HEIGHT = 600;
    private final static int MENU_WIDTH = 200;
    private static final int MINUTES_MONTH = 43829;
    private static final int MINUTES_DAY = 1440;
    private static final int MINUTES_HOUR = 60;

    private Map<String, String> searchResults = new HashMap<>();


    // Custom borders and colors
    private Border darkBorder = BorderFactory.createLineBorder(Color.decode("#444444"), 1);
    private JList<String> resultList = null;


    private JScrollPane mySeries;
    private JTextArea watchTimeDisplay;

    // views
    private MultipleSeriesView msv;
    private SingleSeriesView ssv = null;


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


    private void setStyle(JComponent component) {
	if (JPanel.class.isInstance(component)) {
	    component.setBorder(darkBorder);
	    component.setBackground(Color.decode("#222222"));
	}else if (JLabel.class.isInstance(component)) {
	    component.setForeground(Color.decode("#999999"));
	} else if (JTextField.class.isInstance(component) || JTextArea.class.isInstance(component) || JScrollPane.class.isInstance(component) ||
		   JButton.class.isInstance(component)) {
	    component.setBorder(darkBorder);
	    component.setBackground(Color.decode("#191919"));
	    component.setForeground(Color.decode("#999999"));
	}
    }


    /**
     * Creates the left bar containing the search window
     * @return JPanel containing the left menu
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

	setStyle(menuPane);
	setStyle(searchLabel);
	setStyle(searchField);
	setStyle(timeLabel);
	setStyle(watchTimeDisplay);
	watchTimeDisplay.setText(getTotalWatchTime());
	setStyle(resultScroll);
	resultScroll.getViewport().setBackground(Color.BLACK);
	setStyle(addBtn);

	// add everything to the menu pane
	menuPane.add(searchLabel, "left, wrap");
	menuPane.add(searchField, "left, pushx, growx, wrap");
	menuPane.add(resultScroll, "left, pushx, growx, wrap");
	menuPane.add(addBtn, "right, wrap");
	menuPane.add(timeLabel, "wrap");
	menuPane.add(watchTimeDisplay, "grow");

	// adds action to search on enter
	searchField.addActionListener(e -> {
	    String searchString = searchField.getText();

	    if (!searchString.isEmpty()) {
		updateResultScroll(searchString);
		resultScroll.setViewportView(resultList);
		resultScroll.revalidate();
		resultScroll.setVisible(true);

		addBtn.setVisible(true);
		searchField.setText("");
	    }


	});

	// Adds a new show
	addBtn.addActionListener(e -> {
	    // Hides the resultpane and button
	    resultScroll.setVisible(false);
	    addBtn.setVisible(false);

	    new SwingWorker<Void, Void>()
	    {
		@Override public Void doInBackground() {
		    // get series id
		    String name = resultList.getSelectedValue();
		    int id = Integer.parseInt(searchResults.get(name));

		    SeriesDao seriesDb = new SeriesDaoSQLite();
		    List<String> seriesInDb = seriesDb.selectAllIds();


		    if (!seriesInDb.contains(Integer.toString(id))) {
			addShow(id);
		    } else {
			System.out.println("The series " + "'" + name + "'" + " is already in the database.");
		    }
		    return null;
		}

		@Override public void done() {
		    super.done();
		    System.out.println("LOG: (SeriesFrame) Done saving changes.");

		    resultList = null;
		    searchResults.clear();
		    searchField.setEditable(true);


		}
	    }.execute();
	    mySeries.setPreferredSize(msv.getPreferredSize());
	    mySeries.getViewport().revalidate();
	    mySeries.getViewport().repaint();
	});
	return menuPane;
    }

    private void addShow(int id) {
	    FileHandler.fetchZip(id);

	    // parse show
	    XMLParser xmlParser = new XMLParser();
	    Map<ParseType, String> imagePaths = xmlParser.getImageURLs(id);


	    Series series = xmlParser.getSeries(id);


	    // fetch showart
	if (!imagePaths.get(ParseType.POSTER).isEmpty()) {
	    FileHandler.fetchPoster(imagePaths.get(ParseType.POSTER), id);
	}
	if (!imagePaths.get(ParseType.FANART).isEmpty()) {
	    FileHandler.fetchFanart(imagePaths.get(ParseType.FANART), id);
	}


	    // write Series to db
	    SeriesDao seriesDb = new SeriesDaoSQLite();
	    seriesDb.updateSeries(series, QueryType.INSERT);

	    // write episodes to db

	    List<Episode> parsedEpisodes = xmlParser.getEpisodes(id);

	    EpisodeDao episodeDb = new EpisodeDaoSQLite();
	    episodeDb.updateMultipleEpisodes(parsedEpisodes, QueryType.INSERT);

	    //update view
	    msv.addSeriesToView(series);
	    msv.updateView();
    }


    private void updateResultScroll(String searchString) {
	System.out.println("LOG: (SeriesFrame) Searching THETVDB for: " + searchString);

	UrlXMLReader xmlReader = new UrlXMLReader(searchString.replaceAll(" ", "%20"));
	searchResults = xmlReader.getResult();
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


	EpisodeDao episodeDb = new EpisodeDaoSQLite();
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
	builder.append(String.format("Episodes: %d \n",episodesWatched));

	int monthsWatched = minutesWatched / MINUTES_MONTH;
	minutesWatched %= MINUTES_MONTH;
	builder.append(String.format("Months: %d \n", monthsWatched));


	int daysWatched = minutesWatched / MINUTES_DAY;
	minutesWatched %= MINUTES_DAY;
	builder.append(String.format("Days: %d \n", daysWatched));


	int hoursWatched = minutesWatched / MINUTES_HOUR;
	minutesWatched %= MINUTES_HOUR;
	builder.append(String.format("Hours: %d \n", hoursWatched));
	builder.append(String.format("Minutes: %d", minutesWatched));


	return builder.toString();
    }

}

