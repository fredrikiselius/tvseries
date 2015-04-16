package gui;

import net.miginfocom.swing.MigLayout;
import tvseries.Episode;
import tvseries.Series;
import tvseries.TVDBDataMapper;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.MouseInputAdapter;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class SingleSeriesView extends JPanel
{
    //
    private List<ViewListener> viewListeners;
    private List<Episode> episodes;
    private JPanel episodePanel = new JPanel(new MigLayout());
    private int selectedSeason;

    public SingleSeriesView(Series s) {
	this.viewListeners = new ArrayList<>();
	this.episodes = TVDBDataMapper.findByShowId(s.getTvDbId());
	this.selectedSeason = 1;

	this.setLayout(new MigLayout("debug, fill, gap 0, insets 0, top", "", ""));
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

    private void createEpisodeList(String tvDbId) {

	int numberOfSeasons = -1;

	// Get number of seasons
	for (Episode episode : episodes) {
	    if (episode.getSeNumb() > numberOfSeasons) {
		numberOfSeasons = episode.getEpNumb();

		//create a panel to contain all the episodes
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
	seasonList.addActionListener(new ActionListener()
	{
	    @Override public void actionPerformed(final ActionEvent e) {
		episodePanel.removeAll();
		String selectedSeasonString = (String) seasonList.getSelectedItem();
		selectedSeason = Integer.parseInt(selectedSeasonString.replace("Season ", ""));
		System.out.println("Selected season: " + selectedSeasonString);
		createEpisodePanel(selectedSeason);
	    }
	});
	this.add(seasonList, "top, gap 8, split 2");

	// mark all episodes as watched button
	JButton markAll = new JButton("Watched");
	this.add(markAll, "wrap");
	markAll.addActionListener(new ActionListener()
	{
	    @Override public void actionPerformed(final ActionEvent e) {
		String selectedSeason = (String) seasonList.getSelectedItem();
		int season = Integer.parseInt(selectedSeason.replace("Season ", ""));
		markSeasonWatched(season);
	    }
	});


	this.add(episodePanel);
	createEpisodePanel(1);


	System.out.println("LOG: Found " + numberOfSeasons + " season(s) with a total of " + episodes.size() + " episodes");

    }

    private void markSeasonWatched(int season) {
	List<Episode> episodesWithSeason = new ArrayList<>();
	for (Episode episode : episodes) {
	    if (episode.getSeNumb() == season) {
		episode.setWatchedStatus(true);
		episode.setWatchCount(episode.getWatchCount() + 1); // increment watch status with one
		episodesWithSeason.add(episode);
	    }
	}

	new SwingWorker<Void, Void>() {
	    @Override public Void doInBackground() {
		TVDBDataMapper.addMultipleWatched(episodesWithSeason);
		return null;
	    }
	    @Override public void done() {
		episodePanel.removeAll();
		createEpisodePanel(selectedSeason);
	    }
	}.execute();
    }

    private void createEpisodePanel(int seasonNumber) {
	for (Episode episode : episodes) {
	    if (episode.getSeNumb() == seasonNumber) {
		String watched;
		episodePanel.add(new JLabel(episode.getEpNumb() + ""));
		episodePanel.add(new JLabel(episode.getName()), "gapleft 10");
		JLabel incrementWatchCount = new JLabel("+");
		JLabel decreaseWatchCount = new JLabel("-");
		JLabel watchCount = new JLabel(episode.getWatchCount() + "");
		episodePanel.add(incrementWatchCount, "gapleft 10");
		episodePanel.add(decreaseWatchCount, "gapleft 5");
		episodePanel.add(watchCount, "wrap");

		incrementWatchCount.addMouseListener(new MouseInputAdapter()
		{
		    @Override public void mousePressed(final MouseEvent e) {
			super.mousePressed(e);
			episode.markAsWatched();
			watchCount.setText(episode.getWatchCount() + "");
		    }
		});

		decreaseWatchCount.addMouseListener(new MouseInputAdapter()
		{
		    @Override public void mousePressed(final MouseEvent e) {
			super.mousePressed(e);
			episode.removeMostRecentWatched();
			watchCount.setText(episode.getWatchCount() + "");
		    }
		});
	    }
	}
	episodePanel.repaint();
	episodePanel.revalidate();
    }
}
