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
    private List<ViewListener> viewListeners;
    private Series s;

    private Border darkBorder = BorderFactory.createLineBorder(Color.decode("#444444"), 1);

    public SingleSeriesView(Series s) {
	this.s = s;
	this.viewListeners = new ArrayList<>();

	this.setLayout(new MigLayout("fill, gap 0, insets 0, top", "", ""));
	createFanartContainer();
	this.add(new JLabel(PictureLoader.loadPoster(s.getTvDbId())), "split 2");
	addBasicInfo();
	JButton backButton = new JButton("<< Back");
	this.add(backButton, "wrap");
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

    private void createFanartContainer() {
	JPanel fanartContainer = new JPanel(new MigLayout("debug, gap 0, insets 0,fillx, wrap 1", "[grow, fill]"));
	fanartContainer.setBackground(Color.decode("#111111"));
	fanartContainer.setBorder(darkBorder);
	JLabel fanart = new JLabel(PictureLoader.loadFanart(s.getTvDbId()));
	fanartContainer.add(fanart, "growx, pushx");
	this.add(fanartContainer, "h 400!, wrap, growx, pushx");
    }

    private void addBasicInfo() {
	String[] seriesInfo = this.s.getEverything();
	JPanel basicInfoHolder = new JPanel(new MigLayout("wrap"));

	// Adds labels with all the show information
	// Begins on 1 to skip the id
	for (int infoIndex = 1; infoIndex < seriesInfo.length; infoIndex++) {
	    if (infoIndex == 5) {
		JTextArea jtf = new JTextArea(seriesInfo[infoIndex]);
		jtf.setEditable(false);
		jtf.setLineWrap(true);
		basicInfoHolder.add(jtf, "w 400!");
	    } else {
		basicInfoHolder.add(new JLabel(seriesInfo[infoIndex]));
	    }
	}

	this.add(basicInfoHolder, "wrap");
    }

    private void createEpisodeList(String tvDbId) {
	List<Episode> loadedEpisodes = TVDBDataMapper.findByShowId(tvDbId);
	int numberOfSeasons = -1;

	// Get number of seasons
	for (Episode episode : loadedEpisodes) {
	    if (episode.getSeNumb() > numberOfSeasons) {
		numberOfSeasons = episode.getEpNumb();
	    }
	}
	System.out
		.println("LOG: Found " + numberOfSeasons + " season(s) with a total of " + loadedEpisodes.size() + " episodes");

	JPanel episodeContainer =
		new JPanel(new MigLayout("wrap")); // The container for all seasons and episodes of the entire series
	List<JPanel> seasonPanels = new ArrayList<JPanel>(); // Will contain one panel for each season

	// Make sure at least one season is found
	if (numberOfSeasons >= 0) {
	    for (int season = 0; season <= numberOfSeasons; season++) {
		JLabel seasonNumber = new JLabel("Season " + season + "+");
		seasonPanels.add(new JPanel(new MigLayout("wrap")));
		episodeContainer.add(seasonNumber);
		episodeContainer.add(seasonPanels.get(season));

		final int currentSeason = season;
		seasonNumber.addMouseListener(new MouseInputAdapter()
		{
		    @Override public void mousePressed(final MouseEvent e) {
			super.mousePressed(e);
			if (seasonNumber.getText().endsWith("+")) {
			    seasonNumber.setText("Season " + currentSeason + "-");
			    for (Episode episode : loadedEpisodes) {
				if (episode.getSeNumb() == currentSeason) {
				    seasonPanels.get(currentSeason).add(new JLabel(episode.getName()));
				}
			    }
			} else if (seasonNumber.getText().endsWith("-")) {
			    seasonNumber.setText("Season " + currentSeason + "+");
			    for (Episode episode : loadedEpisodes) {
				if (episode.getSeNumb() == currentSeason) {
				    System.out.println("Removing " + episode.getName());
				    seasonPanels.get(currentSeason).removeAll();
				}
			    }
			}
			revalidate();
			repaint();
		    }
		});
	    }
	}
	this.add(episodeContainer);
    }


}
