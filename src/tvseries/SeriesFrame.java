package tvseries;


import javafx.scene.input.MouseEvent;
import net.miginfocom.swing.MigLayout;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.MouseInputAdapter;
import javax.swing.event.MouseInputListener;

import java.awt.*;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.awt.event.MouseListener;
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

    final static int PREFERRED_FRAME_WIDTH = 800;
    final static int PREFERRED_FRAM_HEIGHT = 600;

    final static int IMAGE_WIDTH = 180;
    final static int IMAGE_HEIGHT = 265;


    final static int WRAP_FIX = 1; // Fixes the layout in createMySeries

    final static int FRAME_WIDTH = 800;
    final static int FRAME_HEIGHT = 600;
    final static int MYSERIES_WIDTH = 150;

    private Map<String, String> searchResults = new HashMap<String, String>();
    private Vector<Series> test2Series = new Vector<Series>(); // The one that should be used TODO arraylist?
    private Vector<JPanel> seriesPanels = new Vector<JPanel>();

    // Custom borders and colors
    private Border darkBorder = BorderFactory.createLineBorder(Color.decode("#444444"), 1);
    private int numberSeries;
    JList<String> resultList = null;

    // GUI Components

    private JPanel contentPane = new JPanel(); // Holder for all other components
    private JPanel mySeriesHolder = new JPanel(); // Holder for all posters in mySeries


    public SeriesFrame() {
	super("Omega");
	initializeUI();
	this.setVisible(true);
    }

    private void initializeUI() {
	fetchSeries();

	setExtendedState(Frame.MAXIMIZED_BOTH);
	setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	setPreferredSize(new Dimension(PREFERRED_FRAME_WIDTH, PREFERRED_FRAM_HEIGHT));
	setMinimumSize(new Dimension(PREFERRED_FRAME_WIDTH, PREFERRED_FRAM_HEIGHT));

	contentPane = new JPanel();
	contentPane.setLayout(new MigLayout("debug, fill", "[200px][grow]", "[grow]"));

	JScrollPane mySeries = new JScrollPane(createMySeries());
	mySeries.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
	mySeries.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
	mySeries.setBorder(BorderFactory.createEmptyBorder());


	contentPane.add(createLeftMenu(), "west, width 200:");
	contentPane.add(new JPanel(), "east, pushy, growy, width 650:");
	contentPane.add(mySeries, "north, width 1050:, wrap");


	setContentPane(contentPane);
	pack();
	setLocationByPlatform(true);
	setVisible(true);
    }

    private JPanel createLeftMenu() {
	JPanel menuPane = new JPanel(new MigLayout("", "[200px]", "[][::200px][]"));
	final JButton addBtn = new JButton("Add");
	final JTextField searchField = new JTextField();
	final JScrollPane resultScroll = new JScrollPane(resultList);
	resultScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
	JScrollBar scrollBar = resultScroll.getVerticalScrollBar();

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
		updateResultScroll(searchField.getText());
		resultScroll.setViewportView(resultList);


		resultList.setOpaque(true);
		resultScroll.revalidate();
		resultScroll.setVisible(true);
		addBtn.setVisible(true);
		searchField.setText("");

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
		System.out.println("Added " + name + searchResults.get(name));
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
	mySeriesHolder = new JPanel(); // Holder for all the posters
	mySeriesHolder.setLayout(new MigLayout("", "", ""));
	addPosterPanels();
	return mySeriesHolder;
    }

    private void addPosterPanels() {
	System.out.println(test2Series.size());
	for (int i = 0; i < test2Series.size(); i++) {
	    if (((WRAP_FIX + i) % 5) == 0) { // Make sure we get rows of five
		mySeriesHolder.add(createSeriesPanel(test2Series.get(i)), "top, wrap");
	    } else {
		mySeriesHolder.add(createSeriesPanel(test2Series.get(i)), "top");

	    }
	}
	mySeriesHolder.repaint();
	mySeriesHolder.revalidate();

    }

    private JPanel createSeriesPanel(Series show) {

	final String id = show.getTvDbId();
	final JPanel seriesPanel = new JPanel(new MigLayout(""));
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
		    picHolder.setIcon(new ImageIcon(img.getScaledInstance(IMAGE_WIDTH, IMAGE_HEIGHT, Image.SCALE_DEFAULT)));
		}
	    }
	} else {
	    try {
		img = ImageIO.read(new File("img/no.jpg"));
	    } catch (IOException ioe) {
		ioe.printStackTrace();
	    } finally {
		if (img != null) {
		    picHolder.setIcon(new ImageIcon(img.getScaledInstance(IMAGE_WIDTH, IMAGE_HEIGHT, Image.SCALE_DEFAULT)));
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
	seriesPanel.add(serName, "wrap");
	seriesPanel.add(serNetwork, "left, pushx, growx, split 2");
	seriesPanel.add(removeSer, "right, wrap");
	removeSer.addMouseListener(new MouseInputAdapter()
	{
	    @Override public void mousePressed(final java.awt.event.MouseEvent e) {
		System.out.println("removing " + id);
		TVDBDataMapper.delete(id);
		updateMySeries();
	    }
	});

	seriesPanels.addElement(seriesPanel);

	return seriesPanel;
    }

    private void updateMySeries() {
	test2Series.removeAllElements();
	fetchSeries();
	for (int seriesPanelIndex = 0; seriesPanelIndex < seriesPanels.size(); seriesPanelIndex++) {
	    mySeriesHolder.remove(seriesPanels.get(seriesPanelIndex));
	}
	addPosterPanels();
    }

    private void fetchSeries() {
	System.out.println("Fetching ids from database:");
	List<String> idList = null;
	idList = TVDBDataMapper.selectAllIds();

	if (idList != null) {
	    numberSeries = idList.size();
	    for (String id : idList) {
		test2Series.addElement(TVDBDataMapper.findByTvDbId(id));
	    }

	    for (int i = 0; i < test2Series.size(); i++) {
		System.out.println(test2Series.get(i));
	    }
	}
    }

}

