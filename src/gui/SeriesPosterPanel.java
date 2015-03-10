package gui;

import tvseries.Series;

import javax.swing.*;
import java.awt.*;

/**
 * Used in the GUI when viewing all the series that has been added.
 * Comes
 */
public class SeriesPosterPanel extends JPanel {
    private static final int POSTER_PANEL_WIDTH = 200;
    private static final int POSTER_PANEL_HEIGHT = 320;

    // All information about the series
    private Series series;

    public SeriesPosterPanel(Series series) {
	this.series = series;
	this.setPreferredSize(new Dimension(POSTER_PANEL_WIDTH, POSTER_PANEL_HEIGHT));
	this.setPreferredSize(new Dimension(POSTER_PANEL_WIDTH, POSTER_PANEL_HEIGHT));
	this.setMinimumSize(this.getPreferredSize());
	this.setMaximumSize(this.getPreferredSize());
    }

    // Adds all the fields to the SeriesPanel
    private void createContent() {
	JLabel name = new JLabel(series.getShowName());
	JLabel network = new JLabel(series.getNetwork());
	JLabel poster = new JLabel();
    }
}
