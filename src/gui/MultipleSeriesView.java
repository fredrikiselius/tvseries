package gui;

import net.miginfocom.swing.MigLayout;
import tvseries.Series;
import tvseries.TVDBDataMapper;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.MouseInputAdapter;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * This class creates an overview for all the series added to the program the series are displayed in seperate panels containing
 * a poster and the series name
 */
public class MultipleSeriesView extends JPanel
{

    private Border darkBorder = BorderFactory.createLineBorder(Color.decode("#444444"), 1);

    private final static int NUMBER_OF_POSTERS_ROW = 5;
    final static int POSTER_WIDTH = 180;
    final static int POSTER_HEIGHT = 265;
    final static int POSTER_PANEL_WIDTH = 200; // Size for the panel containing the series poster
    final static int POSTER_PANEL_HEIGHT = 320; // and name in myseries

    List<Series> series; // contains all loaded series from the database

    public MultipleSeriesView() {
	this.series = new ArrayList<>();
	this.setLayout(new MigLayout("insets 0, gap 0, wrap " + NUMBER_OF_POSTERS_ROW));
	fetchSeries();
	updateView();
	this.setVisible(true);
    }

    private void createSeriesPanel(Series s) {
	JPanel seriesPanel = new JPanel(new MigLayout("debug, wrap"));

	JLabel poster = new JLabel(PictureLoader.loadPoster(s.getTvDbId()));
	JLabel name = new JLabel(s.getShowName());
	JLabel network = new JLabel(s.getNetwork());
	JLabel removeSeries = new JLabel("X");

	poster.setBorder(darkBorder);

	seriesPanel.add(poster, "al center");
	seriesPanel.add(name);
	seriesPanel.add(network, "left,  split 2");
	seriesPanel.add(removeSeries, "");

	removeSeries.addMouseListener(new MouseInputAdapter()
	{
	    @Override public void mousePressed(final MouseEvent e) {
		super.mousePressed(e);

		TVDBDataMapper.delete(s.getTvDbId());
		series.clear();
		fetchSeries();
		updateView();
	    }
	});

	poster.addMouseListener(new MouseInputAdapter()
	{
	    @Override public void mousePressed(final MouseEvent e) {
		super.mousePressed(e);
		System.out.println("MER KOD");
	    }
	});

	this.add(seriesPanel, "top, w " + POSTER_PANEL_WIDTH + "!, h " + POSTER_PANEL_HEIGHT + "!");
    }

    private void updateView() {
	this.removeAll();
	for (Series s : series) {
	    createSeriesPanel(s);
	}
	this.repaint();
	this.revalidate();
    }

    private void fetchSeries() {
	System.out.println("LOG: Fetching ids from database:");

	List<String> idList = TVDBDataMapper.selectAllIds();
	if (!idList.isEmpty()) {
	    for (String id : idList) {
		series.add(TVDBDataMapper.findByTvDbId(id));
	    }

	    System.out.println("LOG: loaded " + idList.size() + " series");
	} else {
	    System.out.println("LOG: The database is empty"); //TODO LOG
	}
    }


}
