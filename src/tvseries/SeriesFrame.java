package tvseries;


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


    private Map<String, String> searchResults = new HashMap<String, String>(); // Contains search results, emptied after a series is added
    private List<Series> loadedSeries = new ArrayList<Series>(); // Series loaded from the database TODO arraylist?
    private List<JPanel> showPanels = new ArrayList<JPanel>();
    //private Vector<JPanel> seriesPanels = new Vector<JPanel>(); // Contains the series panels shown under myseries

    // Custom borders and colors
    private Border darkBorder = BorderFactory.createLineBorder(Color.decode("#444444"), 1);
    JList<String> resultList = null;


    // GUI Components
    private JPanel contentPane = new JPanel(); // Holder for all other components
    private JPanel mySeriesHolder = new JPanel(); // Holder for all posters in mySeries


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
	contentPane.setLayout(new MigLayout(", fill", "[200px][grow]", "[grow]"));

	JScrollPane mySeries = new JScrollPane(createMySeries());
	mySeries.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
	mySeries.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
	mySeries.setBorder(BorderFactory.createEmptyBorder());


	contentPane.add(createLeftMenu(), "west, w 200!");
	contentPane.add(mySeries, "north, w "+((POSTER_PANEL_WIDTH)*NUMBER_OF_POSTERS_ROW+POSTER_PANEL_WIDTH_FIX)+
				  "!, pushy, growy, wrap");


	setContentPane(contentPane);
	setVisible(true);
	pack();
    }

    private JPanel createLeftMenu() {
	JPanel menuPane = new JPanel(new MigLayout("", "[]", "[][::200px][]"));
	final JButton addBtn = new JButton("Add");
	final JTextField searchField = new JTextField();
	final JScrollPane resultScroll = new JScrollPane(resultList);
	resultScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

	resultScroll.setVisible(false);
	addBtn.setVisible(false);


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

		String name = resultList.getSelectedValue();
		String id = searchResults.get(name);
		try {
		    TVDBDataMapper.initialData(name.replaceAll("'", ""), id);
		    DownloadFile.fetchZip(id);
		    TVDBDataMapper.Update(id);
		} catch (SQLException sqlex) {
		    sqlex.printStackTrace();
		} catch (IOException ioex) {
		    ioex.printStackTrace();
		}


		// Update all posters in mySeries
		updateMySeries();


		resultList = null;
		searchResults.clear();

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
		mySeriesHolder.add(createSeriesPanel(loadedSeries.get(i)), "top, w "+POSTER_PANEL_WIDTH+"!");
	}
	mySeriesHolder.repaint();
	mySeriesHolder.revalidate();
    }

    private JPanel createSeriesPanel(Series show) {
	final String id = show.getTvDbId();
	final JPanel seriesPanel = new JPanel(new MigLayout(""));

	seriesPanel.setPreferredSize(new Dimension(POSTER_PANEL_WIDTH, POSTER_PANEL_HEIGHT));
	seriesPanel.setMinimumSize(seriesPanel.getPreferredSize());
	seriesPanel.setMaximumSize(seriesPanel.getPreferredSize());

	JLabel picHolder = new JLabel();
	JLabel serName = new JLabel(show.getShowName());
	JLabel serNetwork = new JLabel(show.getNetwork());
	JLabel removeSer = new JLabel("X");

	// Loading the series poster if there is one
	Image img = null;
	File imgFile = new File("img/" + show.getTvDbId() + ".jpg");
	if (imgFile.exists()) {
	    try {
		img = ImageIO.read(imgFile);
	    } catch (IOException ioe) {
		ioe.printStackTrace();
	    } finally {
		if (img != null) {
		    picHolder.setIcon(new ImageIcon(img.getScaledInstance(POSTER_WIDTH, POSTER_HEIGHT, Image.SCALE_DEFAULT)));
		}
	    }
	} else {
	    try {
		img = ImageIO.read(new File("img/no.jpg"));
	    } catch (IOException ioe) {
		ioe.printStackTrace();
	    } finally {
		if (img != null) {
		    picHolder.setIcon(new ImageIcon(img.getScaledInstance(POSTER_WIDTH, POSTER_HEIGHT, Image.SCALE_DEFAULT)));
		}
	    }
	}


	seriesPanel.setBackground(Color.decode("#222222"));
	seriesPanel.setBorder(darkBorder);
	picHolder.setBorder(darkBorder);
	serName.setForeground(Color.decode("#33CC33"));
	serNetwork.setForeground(Color.decode("#33CC33"));
	removeSer.setForeground(Color.decode("#FF3300"));


	seriesPanel.add(picHolder, "wrap");
	seriesPanel.add(serName, "width ::"+ POSTER_WIDTH +", wrap");
	seriesPanel.add(serNetwork, "left, pushx, growx, split 2");
	seriesPanel.add(removeSer, "right, wrap");
	removeSer.addMouseListener(new MouseInputAdapter()
	{
	    @Override public void mousePressed(final MouseEvent e) {
		System.out.println("removing " + id);
		TVDBDataMapper.delete(id);
		updateMySeries();
	    }
	});

	showPanels.add(seriesPanel);
	return seriesPanel;
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

	if (!idList.isEmpty()) {
	    for (String id : idList) {
		loadedSeries.add(TVDBDataMapper.findByTvDbId(id));
	    }
	    System.out.println("LOG: loaded "+idList.size()+" series");
	} else {
	    System.out.println("LOG: The database is empty"); //TODO LOG
	}
    }

}

