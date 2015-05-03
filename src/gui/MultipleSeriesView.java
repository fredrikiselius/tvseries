package gui;

import database.QueryType;
import episodedao.Episode;
import episodedao.EpisodeDao;
import episodedao.EpisodeDaoSQLite;
import net.miginfocom.swing.MigLayout;
import seriesdao.Series;
import seriesdao.SeriesDao;
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

    private final static int LEFT_MENU = 200;
    final static int POSTER_WIDTH = 180;
    final static int POSTER_PANEL_WIDTH = 200; // Size for the panel containing the show poster
    final static int POSTER_PANEL_HEIGHT = 320; // and name in myseries

    private List<Series> series; // contains all loaded shows from the database

    private SeriesDao seriesDb;
    private SingleSeriesView ssv = null;

    private int screenWidth;

    private List<ViewListener> viewListeners;

    public MultipleSeriesView() {
        GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
       	this.screenWidth = gd.getDisplayMode().getWidth();

        seriesDb = new SeriesDaoSQLite();
        this.series = new ArrayList<>();
        this.setLayout(new MigLayout("insets 0, gap 0, wrap " + numberOfPostersRow(), "", ""));
        this.viewListeners = new ArrayList<>();
        fetchSeries();
        updateView();
        this.setVisible(true);
    }

    private int numberOfPostersRow() {
        return (this.screenWidth - LEFT_MENU) / POSTER_PANEL_WIDTH;

    }

    public void addViewListener(ViewListener vl) {
        this.viewListeners.add(vl);
    }

    private void notifyViewListeners(SingleSeriesView ssv) {
        for (ViewListener viewListener : viewListeners) {
            viewListener.multipleViewChanged(ssv);
        }
    }

    private void notifyViewListeners() {
        viewListeners.forEach(ViewListener::totalTimeChanged);
    }

    private void createSeriesPanel(Series series) {
        JPanel seriesPanel = new JPanel(new MigLayout("wrap"));

        JLabel poster = new JLabel(PictureLoader.loadPoster(series.getTvDbId()));
        JLabel name = new JLabel(series.getShowName());
        JLabel next = new JLabel(series.getNextAirDate());
        JLabel removeSeries = new JLabel("X");

        poster.setBorder(darkBorder);

        seriesPanel.add(poster, "al center");
        seriesPanel.add(name, "width ::" + POSTER_WIDTH);
        seriesPanel.add(next, "left, pushx, growx, split 2");
        seriesPanel.add(removeSeries, "");

        seriesPanel.setBackground(Color.decode("#222222"));
        seriesPanel.setBorder(darkBorder);

        name.setForeground(Color.WHITE);
        next.setForeground(Color.WHITE);
        removeSeries.setForeground(Color.decode("#FF3300"));


        removeSeries.addMouseListener(new MouseInputAdapter() {
            @Override
            public void mousePressed(final MouseEvent e) {
                super.mousePressed(e);
                // Remove episodes
                EpisodeDao episodeDb = new EpisodeDaoSQLite();
                List<Episode> episodesToRemove = episodeDb.getAllEpisodes(series.getTvDbId());
                episodeDb.updateMultipleEpisodes(episodesToRemove, QueryType.DELETE);

                MultipleSeriesView.this.series.remove(series);
                updateView();
                seriesDb.updateSeries(series, QueryType.DELETE);
                FileHandler.deleteShowDir(series.getTvDbId());
                notifyViewListeners();
            }

            @Override public void mouseEntered(final MouseEvent e) {
                super.mouseEntered(e);
                removeSeries.setForeground(Color.decode("#B22400"));
            }

            @Override public void mouseExited(final MouseEvent e) {
                super.mouseExited(e);
                removeSeries.setForeground(Color.decode("#FF3300"));
            }
        });

        poster.addMouseListener(new MouseInputAdapter() {
            @Override
            public void mousePressed(final MouseEvent e) {
                super.mousePressed(e);
                System.out.println("LOG: (MultipleSeriesView) Opening " + series.getShowName() + ".");
                ssv = new SingleSeriesView(series);
                notifyViewListeners(ssv);

            }

            @Override public void mouseEntered(final MouseEvent e) {
                super.mouseEntered(e);
                name.setForeground(Color.decode("#FF9900"));
            }

            @Override public void mouseExited(final MouseEvent e) {
                super.mouseExited(e);
                name.setForeground(Color.WHITE);
            }
        });

        name.addMouseListener(new MouseInputAdapter()
        {
            @Override public void mousePressed(final MouseEvent e) {
                super.mousePressed(e);
                System.out.println("LOG: (MultipleSeriesView) Opening " + series.getShowName() + ".");
                ssv = new SingleSeriesView(series);
                notifyViewListeners(ssv);

            }

            @Override public void mouseEntered(final MouseEvent e) {
                super.mouseEntered(e);
                name.setForeground(Color.decode("#FF9900"));
            }

            @Override public void mouseExited(final MouseEvent e) {
                super.mouseExited(e);
                name.setForeground(Color.WHITE);
            }
        });

        this.add(seriesPanel, "top, w " + POSTER_PANEL_WIDTH + "!, h " + POSTER_PANEL_HEIGHT + "!");
    }

    public void updateView() {
        this.removeAll();
        series.forEach(this::createSeriesPanel);
        this.repaint();
        this.revalidate();
    }

    public void addSeriesToView(Series series) {
        this.series.add(series);
        Collections.sort(this.series, new SeriesComparator());
    }

    @Override
    public Dimension getPreferredSize() {
        int height = ((series.size()+numberOfPostersRow())/numberOfPostersRow())*POSTER_PANEL_HEIGHT;
        setPreferredSize(new Dimension(screenWidth - LEFT_MENU, height));
        return super.getPreferredSize();
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
    }

    public Iterable<Series> getLoadedSeries() {
        return series;
    }



}
