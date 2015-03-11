package tvseries;


import net.miginfocom.swing.MigLayout;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.MouseInputAdapter;
import javax.swing.text.Document;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.*;
import java.util.List;

public class SeriesFrame extends JFrame
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
    ;


    public SeriesFrame() {
	super("Omega");
	initGUI();
    }

    private void initGUI() {
	fetchSeries();

	setExtendedState(Frame.MAXIMIZED_BOTH);
	setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
	setPreferredSize(new Dimension(PREFERRED_FRAME_WIDTH, PREFERRED_FRAM_HEIGHT));
	setMinimumSize(new Dimension(MIN_FRAME_WIDTH, MIN_FRAME_HEIGHT));

	contentPane = new JPanel();
	contentPane.setLayout(new MigLayout("fill", "[200px][grow]", "[grow]"));

	mySeries = new JScrollPane(createMySeries());
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

    private JPanel createStatusBar() {
	JPanel statusBar = new JPanel(new MigLayout("left, center"));

	statusBar.add(statusText, "left");
	statusBar.setBorder(darkBorder);
	statusBar.setBackground(Color.decode("#222222"));
	statusText.setForeground(Color.decode("#999999"));

	return statusBar;
    }

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


			return null;
		    }

		    @Override public void done() {
			System.out.println("Done");
			updateMySeries();


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

    private JPanel createMySeries() {
	mySeriesHolder = new JPanel(); // Holder for all the posters
	mySeriesHolder.setLayout(new MigLayout("insets 0, gap 0, wrap " + NUMBER_OF_POSTERS_ROW, "", ""));
	addPosterPanels();
	return mySeriesHolder;
    }

    private void addPosterPanels() {
	for (int i = 0; i < loadedSeries.size(); i++) {
	    mySeriesHolder.add(createSeriesPanel(loadedSeries.get(i)),
			       "top, w " + POSTER_PANEL_WIDTH + "!, h " + POSTER_PANEL_HEIGHT + "!");
	}
	mySeriesHolder.repaint();
	mySeriesHolder.revalidate();
	contentPane.revalidate();
	contentPane.repaint();
    }

    // Creates the frames seen when a new series is added
    // Contains a poster, the show name and the network name
    private JPanel createSeriesPanel(Series show) {
	final String id = show.getTvDbId();
	final JPanel seriesPanel = new JPanel(new MigLayout(""));


	JLabel picHolder = setPosterPicture(show.getTvDbId());
	JLabel serName = new JLabel(show.getShowName());
	JLabel serNetwork = new JLabel(show.getNetwork());
	JLabel airday = new JLabel("Airdays: " + show.getAirday());
	JLabel airtime = new JLabel("Airtime: " + show.getAirtime());
	JLabel runtime = new JLabel("Runtime: " + show.getRuntime() + " min");
	JLabel status = new JLabel("Status: " + show.getStatus());

	JTextPane overview = new JTextPane();
	overview.setEditable(false);
	overview.setText(show.getOverview());
	JLabel removeSer = new JLabel("X");


	seriesPanel.setBackground(setColor("black"));
	seriesPanel.setBorder(darkBorder);

	serName.setForeground(Color.decode("#33CC33"));
	serNetwork.setForeground(Color.decode("#33CC33"));
	removeSer.setForeground(Color.decode("#FF3300"));


	seriesPanel.add(picHolder, "wrap");
	seriesPanel.add(serName, "width ::" + POSTER_WIDTH + ", wrap");
	seriesPanel.add(serNetwork, "left, pushx, growx, split 2");
	seriesPanel.add(removeSer, "right, wrap");


	// Adds the functionality to remove on click
	removeSer.addMouseListener(new MouseInputAdapter()
	{
	    @Override public void mousePressed(final MouseEvent e) {
		super.mousePressed(e);
		System.out.println("LOG: Removing " + id);
		TVDBDataMapper.delete(id);
		updateMySeries();
	    }
	});

	// Opens a new JPanel with information about the clicked series
	picHolder.addMouseListener(new MouseInputAdapter()
	{
	    @Override public void mousePressed(final MouseEvent e) {
		super.mousePressed(e);
		System.out.println("LOG: Opening " + show.getShowName());
		JPanel seriesPage = createSeriesPage(show);

		/*JPanel seriesHolder = new JPanel(new MigLayout("fill, gap 0, insets 0, top", "", "[][]"));
		JPanel infoHolder = new JPanel(new MigLayout("wrap"));
		JButton backBtn = new JButton("<< Back");

		infoHolder.add(serName);
		infoHolder.add(serNetwork);
		infoHolder.add(airday);
		infoHolder.add(airtime);
		infoHolder.add(runtime);
		infoHolder.add(status);


		JPanel episodeListPanel = new JPanel(new MigLayout("wrap"));
		List<Episode> episodes = TVDBDataMapper.findByShowId(show.getTvDbId());
		List<String> seasons = new ArrayList<String>();

		for (Episode episode : episodes) {
		    if (!seasons.contains(episode.getSeNumb())) {
			seasons.add(episode.getSeNumb());
		    }
		}

		List<JPanel> episodePanels = new ArrayList<JPanel>();
		//JPanel episodeList = new JPanel();
		//episodeList.setVisible(true);
		for (String season : seasons) {
		    episodePanels.add(new JPanel(new MigLayout("debug, fill, wrap")));
		    JLabel seasonLabel = new JLabel("Season " + season + " +");
		    episodeListPanel.add(seasonLabel);
		    episodeListPanel.add(episodePanels.get(Integer.parseInt(season)), "");
		    System.out.println(episodePanels.get(Integer.parseInt(season)));


		    seasonLabel.addMouseListener(new MouseInputAdapter()
		    {
			@Override public void mousePressed(final MouseEvent e) {
			    super.mousePressed(e);
			    if (seasonLabel.getText().endsWith("+")) {
				System.out.println("+");
				for (Episode episode : episodes) {

				    if (episode.getSeNumb().equals(season)) {
					episodePanels.get(Integer.parseInt(season)).add(new JLabel(episode.getName()));

				    }
				}
				seasonLabel.setText("Season " + season + " -");
				episodePanels.get(Integer.parseInt(season)).revalidate();
				episodePanels.get(Integer.parseInt(season)).repaint();
			    } else if (seasonLabel.getText().endsWith("-")) {
				System.out.println("-");
				System.out.println("shrink");
				int components = episodePanels.get(Integer.parseInt(season)).getComponentCount();
				List<JLabel> labelsToRemove = new ArrayList<JLabel>();
				for (int c = 0; c < components; c++) {
				    labelsToRemove.add((JLabel) episodePanels.get(Integer.parseInt(season)).getComponent(c));
				}
				for (JLabel label : labelsToRemove) {
				    episodePanels.get(Integer.parseInt(season)).remove(label);
				}
				seasonLabel.setText("Season " + season + " +");
				episodePanels.get(Integer.parseInt(season)).revalidate();
				episodePanels.get(Integer.parseInt(season)).repaint();
			    }
			}
		    });


		}


		seriesHolder.add(picHolder, "split 3"); // TODO use another picture without action
		seriesHolder.add(infoHolder, "h " + POSTER_HEIGHT + "!");
		seriesHolder.add(overview, "w 400!, wrap, top");
		seriesHolder.add(backBtn, "wrap");
		seriesHolder.add(episodeListPanel);

		backBtn.addActionListener(new ActionListener()
		{
		    @Override public void actionPerformed(final ActionEvent e) {
			contentPane.remove(seriesHolder);
			contentPane.add(mySeries,
					"north, w " + ((POSTER_PANEL_WIDTH) * NUMBER_OF_POSTERS_ROW + POSTER_PANEL_WIDTH_FIX) +
					"!, push, grow, wrap");
			mySeries.revalidate();
			mySeries.repaint();
			updateMySeries();
			contentPane.revalidate();
			contentPane.repaint();
		    }
		});


		contentPane.remove(mySeries);
		contentPane.add(seriesHolder, "gapleft 10pt, gaptop 10pt, top");
		contentPane.revalidate();
		contentPane.repaint();

	    }
	});*/
		contentPane.remove(mySeries);
		contentPane.add(seriesPage, "gapleft 10pt, gaptop 10pt, top");
		contentPane.revalidate();
		contentPane.repaint();

	    }
	});
	showPanels.add(seriesPanel);
	return seriesPanel;
    }


    // Creates a JLabel and adds a poster based on the tvDbId
    private JLabel setPosterPicture(String tvDbId) {
	JLabel posterHolder = new JLabel();
	posterHolder.setBorder(darkBorder);

	File tvDbPoster = new File("img/" + tvDbId + ".jpg");
	File noPoster = new File("img/no.jpg");

	// Use a 'non-avalible' poster if one cannot be found on thetvdb
	File poster;
	if (tvDbPoster.exists()) {
	    poster = tvDbPoster;
	} else {
	    poster = noPoster;
	}

	Image img = null;
	try {
	    img = ImageIO.read(poster);
	} catch (IOException ioe) {
	    ioe.printStackTrace();
	} finally {
	    if (img != null) {
		posterHolder.setIcon(new ImageIcon(img.getScaledInstance(POSTER_WIDTH, POSTER_HEIGHT, Image.SCALE_DEFAULT)));
	    }
	}
	return posterHolder;
    }


    private JPanel createSeriesPage(Series series) {
	String[] seriesInfo = series.getEverything();

	JPanel pageHolder = new JPanel(new MigLayout("fill, gap 0, insets 0, top", "", "[][]"));
	JPanel basicInfoHolder = new JPanel(new MigLayout("wrap"));
	JButton backButton = new JButton("<< Back");

	// Adds labels with all the show information
	// Begins on 1 to skip the id
	for (int infoIndex = 1; infoIndex < seriesInfo.length; infoIndex++) {
	    basicInfoHolder.add(new JLabel(seriesInfo[infoIndex]));
	}

	pageHolder.add(setPosterPicture(seriesInfo[0]), "split 2");
	pageHolder.add(basicInfoHolder, "wrap");
	pageHolder.add(backButton, "wrap");
	pageHolder.add(createEpisodeList(series.getTvDbId()));


	backButton.addActionListener(new ActionListener()
	{
	    @Override public void actionPerformed(final ActionEvent e) {
		contentPane.remove(pageHolder);
		contentPane
			.add(mySeries, "north, w " + ((POSTER_PANEL_WIDTH) * NUMBER_OF_POSTERS_ROW + POSTER_PANEL_WIDTH_FIX) +
				       "!, push, grow, wrap");
		mySeries.revalidate();
		mySeries.repaint();
		updateMySeries();
		contentPane.revalidate();
		contentPane.repaint();
	    }
	});

	contentPane.remove(mySeries);
	contentPane.add(pageHolder, "gapleft 10pt, gaptop 10pt, top");
	contentPane.revalidate();
	contentPane.repaint();

	return pageHolder;
    }

    private JPanel createEpisodeList(String tvDbId) {
	List<Episode> episodes = TVDBDataMapper.findByShowId(tvDbId);
	int numberOfSeasons = -1;

	// Get number of seasons
	for ( Episode episode : episodes) {
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
	    }
	}

	return episodeHolder;
    }


    private Color setColor(String input) {
	switch (input.toLowerCase()) {
	    case "black":
		return Color.decode("#222222");
	    default:
		return Color.WHITE;
	}
    }

    private void updateMySeries() {
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

}

