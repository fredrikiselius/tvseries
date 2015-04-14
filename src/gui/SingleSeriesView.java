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
    private List<Episode> episodes;
    private Series s;

    private Border darkBorder = BorderFactory.createLineBorder(Color.decode("#444444"), 1);

    public SingleSeriesView(Series s) {
	this.s = s;
	this.viewListeners = new ArrayList<>();
	this.episodes = TVDBDataMapper.findByShowId(s.getTvDbId());

	this.setLayout(new MigLayout("debug, fill, gap 0, insets 0, top", "", ""));
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
	JPanel fanartContainer = new JPanel(new MigLayout("gap 0, insets 0,fillx, wrap 1", "[grow, fill]"));
	fanartContainer.setBackground(Color.decode("#111111"));
	fanartContainer.setBorder(darkBorder);
	JLabel fanart = new JLabel(PictureLoader.loadFanart(s.getTvDbId()));
	fanartContainer.add(fanart, "growx, pushx");
	//this.add(fanartContainer, "h 200!, wrap, growx, pushx");

	JPanel swosh = new SingleSeriesPanel(s);
	this.add(swosh, "wrap, w " + swosh.getPreferredSize().width + "!");
    }

    private void addBasicInfo() {
	JPanel basicInfoHolder = new JPanel(new MigLayout("wrap"));

	// Add basic info
	// Format: show name, day at time, network, status
	String info = this.s.getShowName() + "  -  " + this.s.getAirday() + " at " +
		      this.s.getAirtime() + "  -  " + this.s.getNetwork() + "  -  " + this.s.getStatus();
	basicInfoHolder.add(new JLabel(info), "TOP");

	// Add overview
	JTextArea jta =  new JTextArea(this.s.getOverview());
	jta.setEditable(false);
	jta.setLineWrap(true);
	basicInfoHolder.add(jta, "w 400!, TOP");

	this.add(basicInfoHolder, "wrap");
    }

    private void createEpisodeList(String tvDbId) {
	int numberOfSeasons = -1;

	// Get number of seasons
	for (Episode episode : episodes) {
	    if (episode.getSeNumb() > numberOfSeasons) {
		numberOfSeasons = episode.getEpNumb();
	    }
	}
	System.out
		.println("LOG: Found " + numberOfSeasons + " season(s) with a total of " + episodes.size() + " episodes");

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
			    for (Episode episode : episodes) {
				if (episode.getSeNumb() == currentSeason) {
				    seasonPanels.get(currentSeason).add(new JLabel(episode.getName()));
				}
			    }
			} else if (seasonNumber.getText().endsWith("-")) {
			    seasonNumber.setText("Season " + currentSeason + "+");
			    for (Episode episode : episodes) {
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
