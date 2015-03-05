package tvseries;


import net.miginfocom.swing.MigLayout;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.Border;

import java.awt.*;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

public class SeriesFrame extends JFrame
{
    final static int FRAME_WIDTH = 800;
    final static int FRAME_HEIGHT = 600;
    final static int MYSERIES_WIDTH = 150;
    Map<String, Series> series = new HashMap<String, Series>();
    Map<String, String> searchResults = new HashMap<String, String>();
    Vector<Series> test2Series = new Vector<Series>();
    Border darkBorder = BorderFactory.createLineBorder(Color.decode("#444444"), 1);
    private int numberSeries;
    JList<String> resultList = null;

    public SeriesFrame() {
	super("Omega");
	initializeUI();
	this.setVisible(true);
    }

    private void initializeUI() {
	fetchSeries();

	setExtendedState(Frame.MAXIMIZED_BOTH);
	setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	setPreferredSize(new Dimension(FRAME_WIDTH, FRAME_HEIGHT));
	setMinimumSize(new Dimension(800, 600));

	JPanel contentPane = new JPanel();
	contentPane.setLayout(new MigLayout(", fill", "[200px][grow]", "[grow]"));

	JScrollPane mySeries = new JScrollPane(createMySeries());
	mySeries.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
	mySeries.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
	mySeries.setBorder(BorderFactory.createEmptyBorder());



	contentPane.add(createLeftMenu(), "west, width 200:");
	contentPane.add(mySeries, "pushx, growx, width 1050:");

	setContentPane(contentPane);
	pack();
	setLocationByPlatform(true);
	setVisible(true);


	//setLayout(new MigLayout("fillx", "[][]", "[grow][grow]"));
	/*setLayout(new GridBagLayout());
	GridBagConstraints gbc = new GridBagConstraints();

	setPreferredSize(new Dimension(FRAME_WIDTH, FRAME_HEIGHT));
	setMinimumSize(new Dimension(800, 600));
	setLocationRelativeTo(null);
	setExtendedState(Frame.MAXIMIZED_BOTH);
	setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


	gbc.anchor = GridBagConstraints.FIRST_LINE_START;
	gbc.fill = GridBagConstraints.VERTICAL;
	gbc.gridx = 0;
	gbc.gridy = 0;

	//add(createLeftMenu(), gbc);

	//add(createMySeries(), gbc);*/
	//pack();
    }

    private JPanel createLeftMenu() {
	Border darkBorder = BorderFactory.createLineBorder(Color.decode("#444444"), 1);


	JPanel menuPane = new JPanel(new MigLayout("","[200px]","[][::200px][]"));
	GridBagConstraints gbc = new GridBagConstraints();

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
		    DownloadFile.fetchZip(id);
		    //DownloadFile.downloadFile(URLHandler.ZipUrl(id)); TODO Not needed anymore
		    //UnZip.unZipIt();
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
	JPanel mySeries = new JPanel();
	mySeries.setLayout(new MigLayout("", "", ""));
	//number of series in db

	for (int i = 0; i < test2Series.size(); i ++) {

	    if ((i % 5) == 0 && (i != 0)) { // Make sure we get rows of five
		mySeries.add(createSeriesPanel(test2Series.get(i)), "top, wrap");
	    } else {
		mySeries.add(createSeriesPanel(test2Series.get(i)), "top");

	    }
	}

	mySeries.setPreferredSize(new Dimension(1000,1000));

	return mySeries;
    }

    private JPanel createSeriesPanel(Series show) {
	JPanel seriesPanel = new JPanel(new MigLayout(""));
	JLabel picHolder = new JLabel();
	JLabel serName = new JLabel(show.getShowName());
	JLabel serNetwork = new JLabel(show.getNetwork());
	JLabel removeSer = new JLabel("X");


	// Loading the series poster if there is one
	Image img = null;
	File imgFile = new File("img/" + show.getTvDbId() + ".jpg");
	if (imgFile.exists()) {
	    try {
		img = ImageIO.read(new File("img/" + show.getTvDbId() + ".jpg"));
	    } catch (IOException ioe) {
		ioe.printStackTrace();
	    } finally {
		if (img != null) {
		    picHolder.setIcon(new ImageIcon(img.getScaledInstance(180, 265, Image.SCALE_DEFAULT)));
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
	seriesPanel.add(serNetwork, "left, split 2");
	seriesPanel.add(removeSer, "right, pushx, growx, wrap");
	return seriesPanel;
    }

    /*private JPanel mySeriesOld() {
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
    }*/

    /*private void updateMySeries() {
	fetchSeries();
	this.revalidate();
	this.repaint();

    }*/

    /*private JPanel searchBox() {
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

    }*/

    private void fetchSeries() {
	System.out.println("Fetching ids from database");
	ArrayList<String> idList = null;
	idList = TVDBDataMapper.selectAllIds();

	if (idList != null) {
	    numberSeries = idList.size();
	    for (String id : idList) {
		series.put(id, TVDBDataMapper.findByTvDbId(id));
		System.out.println(series.get(id).getShowName());
		test2Series.addElement(TVDBDataMapper.findByTvDbId(id));
	    }

	    for	(int i = 0; i < test2Series.size(); i++) {
		System.out.println(test2Series.get(i));
	    }
	}
    }
}
