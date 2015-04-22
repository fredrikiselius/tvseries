package gui;

import episodedao.Episode;
import episodedao.EpisodeDaoSQLite;
import net.miginfocom.swing.MigLayout;
import seriesdao.Series;
import seriesdao.SeriesDaoSQLite;
import seriesdao.SeriesComparator;
import tvseries.FileHandler;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.MouseInputAdapter;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import java.util.Collections;
import java.util.List;

/**
 * This class creates an overview for all the shows added to the program. The shows are displayed in seperate panels containing
 * a poster and the show name.
 * Each time a new Series is added the shows are reloaded from the database and all panels are recreated.
 */
public class MultipleSeriesView extends JPanel {

    private Border darkBorder = BorderFactory.createLineBorder(Color.decode("#444444"), 1);

    private final static int NUMBER_OF_POSTERS_ROW = 5;
    final static int POSTER_WIDTH = 180;
    final static int POSTER_HEIGHT = 265; //TODO remove?
    final static int POSTER_PANEL_WIDTH = 200; // Size for the panel containing the show poster
    final static int POSTER_PANEL_HEIGHT = 320; // and name in myseries

    private List<Series> series; // contains all loaded shows from the database

    private SeriesDaoSQLite seriesDb;
    private SingleSeriesView ssv;

    List<ViewListener> viewListeners;

    public MultipleSeriesView() {
        seriesDb = new SeriesDaoSQLite();
        this.series = new ArrayList<>();
        this.setLayout(new MigLayout("insets 0, gap 0, wrap " + NUMBER_OF_POSTERS_ROW, "", ""));
        this.viewListeners = new ArrayList<>();
        fetchSeries();
        updateView();
        this.setVisible(true);
    }

    public void addViewListener(ViewListener vl) {
        this.viewListeners.add(vl);
    }

    private void notifyViewListeners(SingleSeriesView ssv) {
        for (ViewListener viewListener : viewListeners) {
            viewListener.multipleViewChanged(ssv);
        }
    }

    private void createSeriesPanel(Series s) {
        JPanel seriesPanel = new JPanel(new MigLayout("wrap"));

        JLabel poster = new JLabel(PictureLoader.loadPoster(s.getTvDbId()));
        JLabel name = new JLabel(s.getShowName());
        JLabel network = new JLabel(s.getNextAirDate().toString());
        JLabel removeSeries = new JLabel("X");

        poster.setBorder(darkBorder);

        seriesPanel.add(poster, "al center");
        seriesPanel.add(name, "width ::" + POSTER_WIDTH);
        seriesPanel.add(network, "left, pushx, growx, split 2");
        seriesPanel.add(removeSeries, "");

        seriesPanel.setBackground(Color.decode("#222222"));
        seriesPanel.setBorder(darkBorder);

        name.setForeground(Color.WHITE);
        network.setForeground(Color.decode("#33CC33"));
        removeSeries.setForeground(Color.decode("#FF3300"));

        removeSeries.addMouseListener(new MouseInputAdapter() {
            @Override
            public void mousePressed(final MouseEvent e) {
                super.mousePressed(e);
                // Remove episodes
                EpisodeDaoSQLite episodeDb = new EpisodeDaoSQLite();
                List<Episode> episodesToRemove = episodeDb.getAllEpisodes(s.getTvDbId());
                episodeDb.deleteMultipleEpisodes(episodesToRemove);

                series.remove(s);
                updateView();
                seriesDb.deleteSeries(s);
                FileHandler.deleteShowDir(s.getTvDbId());
            }
        });

        poster.addMouseListener(new MouseInputAdapter() {
            @Override
            public void mousePressed(final MouseEvent e) {
                super.mousePressed(e);
                System.out.println("LOG: (MultipleSeriesView) Opening " + s.getShowName() + ".");
                ssv = new SingleSeriesView(s);
                notifyViewListeners(ssv);

            }
        });

        this.add(seriesPanel, "top, w " + POSTER_PANEL_WIDTH + "!, h " + POSTER_PANEL_HEIGHT + "!");
    }

    public void updateView() {
        this.removeAll();
        for (Series s : series) {
            createSeriesPanel(s);
        }
        this.repaint();
        this.revalidate();
    }

    public void addSeriesToView(Series s) {
        series.add(s);
        Collections.sort(series, new SeriesComparator());
    }

    /**
     * Fetches all Series in the database and sorts them alphabetically
     */
    private void fetchSeries() {
        System.out.println("LOG: (MultipleSeriesView) Fetching ids from database...");
        series = seriesDb.getAllSeries();

        if (!series.isEmpty()) {
            System.out.println("LOG: (MultipleSeriesView) Loaded " + series.size() + " Series.");
            Collections.sort(series, new SeriesComparator());
        } else {
            System.out.println("LOG: (MultipleSeriesView) The database is empty.");
        }

	/*List<String> idList = TVDBDataMapper.selectAllIds();
    if (!idList.isEmpty()) {
	    for (String id : idList) {
		Series.add(TVDBDataMapper.findByTvDbId(id));
	    }

	    System.out.println("LOG: (MultipleSeriesView) Loaded " + idList.size() + " Series.");
	} else {
	    System.out.println("LOG: (MultipleSeriesView) The database is empty."); //TODO LOG
	}*/
    }


}
