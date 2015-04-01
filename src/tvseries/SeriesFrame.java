package tvseries;


import gui.MultipleSeriesView;
import gui.SingleSeriesView;
import gui.ViewListener;
import net.miginfocom.swing.MigLayout;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.MouseInputAdapter;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;

public class SeriesFrame extends JFrame implements ViewListener
{
    /*
     * TODO setColor function
     */

    //hej :)

    final static int PREFERRED_FRAME_WIDTH = 1280;
    final static int PREFERRED_FRAM_HEIGHT = 720;
    final static int MIN_FRAME_WIDTH = 800;
    final static int MIN_FRAME_HEIGHT = 600;

    final static int POSTER_PANEL_WIDTH = 200; // Size for the panel containing the series poster
    final static int POSTER_PANEL_HEIGHT = 320; // and name in myseries

    final static int NUMBER_OF_POSTERS_ROW = 5; // Number of posters that should be in a row in mySeries
    final static int POSTER_PANEL_WIDTH_FIX = 17; // Used to get the right width for the JScrollPane mySeries

    final static int POSTER_WIDTH = 180;
    final static int POSTER_HEIGHT = 265;


    private Map<String, String> searchResults = new HashMap<String, String>();
    // Contains search results, emptied after a series is added
    private List<Series> loadedSeries = new ArrayList<Series>(); // Series loaded from the database TODO arraylist?
    private List<JPanel> showPanels = new ArrayList<JPanel>();
    //private Vector<JPanel> seriesPanels = new Vector<JPanel>(); // Contains the series panels shown under myseries

    // Custom borders and colors
    private Border darkBorder = BorderFactory.createLineBorder(Color.decode("#444444"), 1);
    JList<String> resultList = null;


    // GUI Components
    private JPanel contentPane = new JPanel(); // Holder for all other components
    private JPanel mySeriesHolder = new JPanel(); // Holder for all posters in mySeries
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

	contentPane = new JPanel();
	contentPane.setLayout(new MigLayout("fill", "[200px][grow]", "[grow]"));

	msv = new MultipleSeriesView();
	msv.addViewListener(this);

	mySeries = new JScrollPane(msv);
	mySeries.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
	mySeries.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
	mySeries.setBorder(BorderFactory.createEmptyBorder());


	contentPane.add(createStatusBar(), "south, h 20!, wrap");
	contentPane.add(createLeftMenu(), "west, w 200!");
	contentPane.add(mySeries, "north, w " + ((POSTER_PANEL_WIDTH) * NUMBER_OF_POSTERS_ROW + POSTER_PANEL_WIDTH_FIX) +
				  "!, pushy, growy, wrap");


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
	JPanel menuPane = new JPanel(new MigLayout("", "[]", "[][::200px][]"));
	final JButton addBtn = new JButton("Add");
	final JTextField searchField = new JTextField();
	final JScrollPane resultScroll = new JScrollPane(resultList);
	resultScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

	resultScroll.setVisible(false);
	addBtn.setVisible(false);

	menuPane.setBorder(darkBorder);
	menuPane.setBackground(Color.decode("#222222"));

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

		new SwingWorker<Void, Void>()
		{

		    @Override public Void doInBackground() {
			searchField.setEditable(false);

			String name = resultList.getSelectedValue();
			String id = searchResults.get(name);


			try {
			    DownloadFile.fetchZip(id);
			} catch (IOException e) {
			    e.printStackTrace();
			}
			    TVDBDataMapper.initialData(name.replaceAll("'", ""), id);
			    TVDBDataMapper.updateShow(id);
			TVDBDataMapper.updateEpisodes(id);
			msv.reloadShowPanels();
			pack();


			return null;
		    }

		    @Override public void done() {
			System.out.println("Done");
			//updateMySeries();


			resultList = null;
			searchResults.clear();
			searchField.setEditable(true);
		    }
		}.execute();


		// Update all posters in mySeries


	    }
	});


	return menuPane;
    }

    private void updateResultScroll(String searchString) {

	System.out.println("LOG: Searching THETVDB for: " + searchString);
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

    /**
    private JPanel createEpisodeList(String tvDbId) {
	List<Episode> episodes = TVDBDataMapper.findByShowId(tvDbId);
	int numberOfSeasons = -1;

	// Get number of seasons
	for ( Episode episode : episodes) {
	    System.out.println("AAAAAAAAAAAAAAAAAAAAAAA" + episode.getSeNumb());
	    if (Integer.parseInt(episode.getSeNumb()) > numberOfSeasons) {
		numberOfSeasons = Integer.parseInt(episode.getEpNumb());
	    }
	}
	System.out.println("LOG: Found " + numberOfSeasons + " season(s) with a total of " + episodes.size() + " episodes");

	JPanel episodeHolder = new JPanel(new MigLayout("wrap")); // The container for all episodes of the entire series
	List<JPanel> episodeLists = new ArrayList<JPanel>(); // Will contain one panel for each season

	// Make sure at least one season is found
	if (numberOfSeasons >= 0) {
	    for (int season = 0; season <= numberOfSeasons; season++) {
		JLabel seasonNumber = new JLabel("Season " + season + "+");
		episodeLists.add(new JPanel(new MigLayout("wrap")));
		episodeHolder.add(seasonNumber);
		episodeHolder.add(episodeLists.get(season));

		final String seasonNumberString = Integer.toString(season);
		final int seasonNumberInt = season;
		seasonNumber.addMouseListener(new MouseInputAdapter()
		{
		    @Override public void mousePressed(final MouseEvent e) {
			super.mousePressed(e);
			System.out.println(seasonNumber.getText());
			if (seasonNumber.getText().endsWith("+")) {
			    for (Episode episode : episodes) {
				if (episode.getSeNumb().equals(seasonNumberString)) {
					episodeLists.get(seasonNumberInt).add(new JLabel(episode.getName()));
				}
			    }
			} else if (seasonNumber.getText().endsWith("-")) {

			}
		    }
		});
	    }
	}

	return episodeHolder;
    }
     **/


    private Color setColor(String input) {
	switch (input.toLowerCase()) {
	    case "black":
		return Color.decode("#222222");
	    default:
		return Color.WHITE;
	}
    }

    /*private void updateMySeries() {
	loadedSeries.clear();
	fetchSeries();
	for (int seriesPanelIndex = 0; seriesPanelIndex < showPanels.size(); seriesPanelIndex++) {
	    mySeriesHolder.remove(showPanels.get(seriesPanelIndex));
	}
	addPosterPanels();
    }


    private void fetchSeries() {
	System.out.println("LOG: Fetching ids from database:");

	List<String> idList = TVDBDataMapper.selectAllIds();
	List<Episode> episodes = TVDBDataMapper.findByShowId("79168");

	if (!idList.isEmpty()) {
	    for (String id : idList) {
		loadedSeries.add(TVDBDataMapper.findByTvDbId(id));
	    }


	    System.out.println("LOG: loaded " + idList.size() + " series");
	} else {
	    System.out.println("LOG: The database is empty"); //TODO LOG
	}
    }
    */

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
	this.add(mySeries, "north, w " + ((POSTER_PANEL_WIDTH) * NUMBER_OF_POSTERS_ROW + POSTER_PANEL_WIDTH_FIX) +
					  "!, pushy, growy, wrap");
	this.revalidate();
	this.repaint();
    }

}

