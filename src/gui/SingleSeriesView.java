package gui;

import episodedao.EpisodeComparator;
import episodedao.EpisodeDaoSQLite;
import net.miginfocom.swing.MigLayout;
import episodedao.Episode;
import seriesdao.Series;


import javax.swing.*;

import javax.swing.event.MouseInputAdapter;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SingleSeriesView extends JPanel
{

    EpisodeDaoSQLite episodeDb;

    private List<ViewListener> viewListeners;
    private List<Episode> episodes;
    private JPanel episodePanel = new JPanel(new MigLayout(""));
	private JScrollPane episodeListScroller;

    private int selectedSeason;
    private Episode nextEpisode;

    public SingleSeriesView(Series s) {
	episodeDb = new EpisodeDaoSQLite();

	// episode list
	this.viewListeners = new ArrayList<>();
	this.episodes = episodeDb.getAllEpisodes(s.getTvDbId()); // get all episodes for the Series
	Collections.sort(episodes, new EpisodeComparator()); // sort the episode list

	getNextEpisode();
	if (nextEpisode != null) {
	    this.selectedSeason = nextEpisode.getSeasonNumber(); // season to view
	} else {
	    this.selectedSeason = 1; // default if everything is watched
	}


	this.setLayout(new MigLayout("fill, gap 0, insets 0, top", "", ""));
	JPanel headerContent = new SingleSeriesPanel(s);
	this.add(headerContent,
		 "wrap, w " + headerContent.getPreferredSize().width + "!, h " +
		 headerContent.getPreferredSize().height + "!");
	JButton backButton = new JButton("<< Back");
	this.add(backButton, "split 3, gapleft 8");
	createEpisodeList(s.getTvDbId());

	backButton.addActionListener(new ActionListener()
	{
	    @Override public void actionPerformed(final ActionEvent e) {
		notifyViewListeners();
	    }
	});

    }


    public void addViewListener(ViewListener vl) {
	this.viewListeners.add(vl);
    }

    private void notifyViewListeners() {
	for (ViewListener viewListener : viewListeners) {
	    viewListener.singleViewChanged();
	}
    }

    private void createEpisodeList(int tvDbId) {

	// Get number of seasons
	int numberOfSeasons = -1;
	for (Episode episode : episodes) {
	    if (episode.getSeasonNumber() > numberOfSeasons) {
		numberOfSeasons = episode.getSeasonNumber();
	    }
	}

	// create "lables" for the seasons combobox
	String[] seasons = new String[numberOfSeasons + 1];
	for (int i = 0; i <= numberOfSeasons ; i++) {
	    seasons[i] = "Season " + i;
	}

	// combobox to be able to chose which season to display
	JComboBox seasonList = new JComboBox(seasons);
	seasonList.setSelectedIndex(selectedSeason);
	seasonList.addActionListener(e -> {
	    // Get the selected season
	    String selectedSeasonString = (String) seasonList.getSelectedItem();
	    selectedSeason = Integer.parseInt(selectedSeasonString.replace("Season ", ""));

	    // Empty episodePanel and add new episodes
	    episodePanel.removeAll();
	    createEpisodePanel(selectedSeason);
	});
	this.add(seasonList, "top, gap 8, split 2");

	// mark season as watched
	JButton markSeasonBtn = new JButton("Watched");
	this.add(markSeasonBtn, "wrap");

	markSeasonBtn.addActionListener(e -> {
	    String selectedSeason1 = (String) seasonList.getSelectedItem();
	    int season = Integer.parseInt(selectedSeason1.replace("Season ", ""));
	    markSeasonWatched(season);
	});

	// create the episode list and add to scroller
	createEpisodePanel(selectedSeason);
	this.episodeListScroller = new JScrollPane(episodePanel);
	this.add(episodeListScroller, "grow");


	System.out.println("LOG: (SingleSeriesView) Found " + numberOfSeasons + " season(s) with a total of " + episodes.size() + " episodes.");
    }

    /**
     * Marks the season as watched
     * @param season The season to mark as watched
     */
    private void markSeasonWatched(int season) {
	List<Episode> episodesWithSeason = new ArrayList<>();
	for (Episode episode : episodes) {
	    if (episode.getSeasonNumber() == season) {
		episode.setWatchedStatus(true);
		episode.incrementWatchCount(1);
		episodesWithSeason.add(episode);
	    }
	}

	// update which episode to watch next
	getNextEpisode();

	new SwingWorker<Void, Void>() {
	    @Override public Void doInBackground() {
		episodeDb.updateWatchCountMultipleEpisodes(episodesWithSeason);
		return null;
	    }
	    @Override public void done() {
		episodePanel.removeAll();
		createEpisodePanel(selectedSeason);
	    }
	}.execute();
    }

    /**
     * Creates a list of jlabels containing all episodes for the specified season
     * @param seasonNumber The season to which the episode belongs
     */
    private void createEpisodePanel(int seasonNumber) {
	for (Episode episode : episodes) {
	    if (episode.getSeasonNumber() == seasonNumber) {
		JLabel episodeName = new JLabel(episode.getName());

		// check if episode is the next one to watch
		if (episode.equals(nextEpisode)) {
		    episodeName.setForeground(Color.decode("#FF9900"));
		}

		episodePanel.add(new JLabel(episode.getEpisodeNumber() + ""), "");
		episodePanel.add(episodeName, "gapleft 10, grow 2");
		JLabel incrementWatchCount = new JLabel("+");
		JLabel decreaseWatchCount = new JLabel("-");
		JLabel watchCount = new JLabel(episode.getWatchCount() + "");
		episodePanel.add(incrementWatchCount, "gapleft 30");
		episodePanel.add(decreaseWatchCount, "gapleft 10");
		episodePanel.add(watchCount, "gapleft 10, wrap");

		incrementWatchCount.addMouseListener(new MouseInputAdapter()
		{
		    @Override public void mousePressed(final MouseEvent e) {
			super.mousePressed(e);
			episode.setWatchCount(episode.getWatchCount() + 1);
			episodeDb.updateWatchCount(episode);
			getNextEpisode();

			episodePanel.removeAll();
			createEpisodePanel(seasonNumber);
		    }
		});

		decreaseWatchCount.addMouseListener(new MouseInputAdapter()
		{
		    @Override public void mousePressed(final MouseEvent e) {
			super.mousePressed(e);
			episode.setWatchCount(episode.getWatchCount() - 1);
			episodeDb.updateWatchCount(episode);
			getNextEpisode();

			episodePanel.removeAll();
			createEpisodePanel(seasonNumber);

		    }
		});
	    }
	}
	episodePanel.repaint();
	episodePanel.revalidate();
    }

    private void getNextEpisode() {
	Episode nextEpisode = null; // in case all episodes has been watched
	for (Episode episode : episodes) {
	    if (episode.getWatchCount() == 0 && episode.getSeasonNumber() > 0) {
		nextEpisode = episode;
		break;
	    }
	}
	this.nextEpisode = nextEpisode;
    }
}
