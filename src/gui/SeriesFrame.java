package gui;

import database.QueryType;
import episodedao.Episode;
import episodedao.EpisodeDaoSQLite;
import episodedao.EpisodeXMLParser;
import net.miginfocom.swing.MigLayout;
import seriesdao.Series;
import seriesdao.SeriesDaoSQLite;
import seriesdao.SeriesXMLParser;
import tvseries.FileHandler;
import parser.ShowDataParser;
import parser.XMLReader;

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
    /*
     * TODO setColor function
     */

    final static int PREFERRED_FRAME_WIDTH = 1280;
    final static int PREFERRED_FRAM_HEIGHT = 720;
    final static int MIN_FRAME_WIDTH = 800;
    final static int MIN_FRAME_HEIGHT = 600;

    final static int POSTER_PANEL_WIDTH = 200; // Size for the panel containing the Series poster
    final static int POSTER_PANEL_HEIGHT = 320; // and name in myseries

    private final static int MENU_WIDTH = 200;

    final static int NUMBER_OF_POSTERS_ROW = 5; // Number of posters that should be in a row in mySeries
    final static int POSTER_PANEL_WIDTH_FIX = 17; // Used to get the right width for the JScrollPane mySeries


    private Map<String, String> searchResults = new HashMap<String, String>();


    // Custom borders and colors
    private Border darkBorder = BorderFactory.createLineBorder(Color.decode("#444444"), 1);
    JList<String> resultList = null;


    private JScrollPane mySeries;
    private JLabel statusText = new JLabel("Idle");

    private MultipleSeriesView msv;
    private SingleSeriesView ssv;


    public SeriesFrame() {
	super("Omega");
	initGUI();
    }

    private void initGUI() {
	//fetchSeries();

	setExtendedState(Frame.MAXIMIZED_BOTH);
	setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
	setPreferredSize(new Dimension(PREFERRED_FRAME_WIDTH, PREFERRED_FRAM_HEIGHT));
	setMinimumSize(new Dimension(MIN_FRAME_WIDTH, MIN_FRAME_HEIGHT));

	final JPanel contentPane = new JPanel();
	contentPane.setLayout(new MigLayout("fill", "[" + MENU_WIDTH + "px][grow]", "[grow]"));

	msv = new MultipleSeriesView();
	msv.addViewListener(this);

	mySeries = new JScrollPane(msv);
	mySeries.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
	mySeries.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
	mySeries.setBorder(BorderFactory.createEmptyBorder());

	// ((POSTER_PANEL_WIDTH) * NUMBER_OF_POSTERS_ROW + POSTER_PANEL_WIDTH_FIX)
	contentPane.add(createStatusBar(), "south, h 20!, wrap");
	contentPane.add(createLeftMenu(), "west, w " + MENU_WIDTH + "!");
	contentPane.add(mySeries, "north, w " + (Toolkit.getDefaultToolkit().getScreenSize().width-200) +
				  "!, pushy, grow, wrap");

	GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
	System.out.println(gd.getDisplayMode().getWidth());
	setContentPane(contentPane);
	setVisible(true);
	pack();
    }

    /**
     * creates the bar at the bottom of the window
     * TODO remove?
     * @return
     */
    private JPanel createStatusBar() {
	JPanel statusBar = new JPanel(new MigLayout("left, center"));

	statusBar.add(statusText, "left");
	statusBar.setBorder(darkBorder);
	statusBar.setBackground(Color.decode("#222222"));
	statusText.setForeground(Color.decode("#999999"));

	return statusBar;
    }

    /**
     * Creates the left bar containing menu items and a search window
     * @return
     */
    private JPanel createLeftMenu() {
	JPanel menuPane = new JPanel(new MigLayout("", "[]", "[][::" + MENU_WIDTH + "px][]"));
	final JButton addBtn = new JButton("Add");
	JLabel searchLabel = new JLabel("Search:");
	final JTextField searchField = new JTextField();
	final JScrollPane resultScroll = new JScrollPane(resultList);
	resultScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

	resultScroll.setVisible(false);
	addBtn.setVisible(false);

	menuPane.setBorder(darkBorder);
	menuPane.setBackground(Color.decode("#222222"));

	searchLabel.setForeground(Color.decode("#999999"));

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


	menuPane.add(searchLabel, "left, wrap");
	menuPane.add(searchField, "left, pushx, growx, wrap");
	menuPane.add(resultScroll, "left, pushx, growx, wrap");
	menuPane.add(addBtn, "right, wrap");


	searchField.addActionListener(new ActionListener()
	{
	    @Override public void actionPerformed(final ActionEvent e) {
		String searchString = searchField.getText();

		if (!searchString.isEmpty()) {
		    statusText.setText("Searching for: " + searchString);
		    updateResultScroll(searchString);
		    resultScroll.setViewportView(resultList);
		    resultList.setOpaque(true);
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

		new SwingWorker<Void, Void>() {
		    @Override public Void doInBackground() {
			searchField.setEditable(false);

			String name = resultList.getSelectedValue();
			int id = Integer.parseInt(searchResults.get(name));
			String fixedName = name.replaceAll("'", "");


			try {
			    FileHandler.fetchZip(id);
			} catch (IOException e) {
			    e.printStackTrace();
			}

			// parse show
			ShowDataParser sdp = new ShowDataParser(id);
			sdp.parseBanners();

			SeriesXMLParser seriesParser = new SeriesXMLParser();
			Series series = seriesParser.getSeries(id);


			// fetch showart
			FileHandler.fetchPoster(sdp.getPoster(), id);
			FileHandler.fetchFanart(sdp.getFanart(), id);




			// write Series to db
			SeriesDaoSQLite seriesDb = new SeriesDaoSQLite();
			seriesDb.updateSeries(series, QueryType.INSERT);

			// write episodes to db
			EpisodeXMLParser episodeParser = new EpisodeXMLParser();
			List<Episode> parsedEpisodes = episodeParser.getEpisodes(id);

			EpisodeDaoSQLite episodeDb = new EpisodeDaoSQLite();
			episodeDb.updateMultipleEpisodes(parsedEpisodes, QueryType.INSERT);

			//update view
			msv.addSeriesToView(series);
			msv.updateView();
			return null;
		    }

		    @Override public void done() {
			System.out.println("LOG: (SeriesFrame) Done saving changes.");
			//updateMySeries();


			resultList = null;
			searchResults.clear();
			searchField.setEditable(true);

		    }
		}.execute();
		mySeries.setPreferredSize(msv.getPreferredSize());
		mySeries.getViewport().revalidate();
		mySeries.getViewport().repaint();


		// Update all posters in mySeries


	    }
	});


	return menuPane;
    }

    private void updateResultScroll(String searchString) {

	System.out.println("LOG: (SeriesFrame) Searching THETVDB for: " + searchString);
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

}

