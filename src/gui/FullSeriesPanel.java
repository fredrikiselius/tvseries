package gui;

import net.miginfocom.swing.MigLayout;
import tvseries.Series;

import javax.swing.*;

/**
 *
 */
public class FullSeriesPanel extends JPanel {
    private Series series;

    public FullSeriesPanel(Series series) {
	this.series = series;
	this.setLayout(new MigLayout("fill, gap 0, insets 0, top", "", "[][]"));
    }

    private void addSeriesContent() {
	JPanel infoPanel = new JPanel();

	JLabel name = new JLabel(series.getShowName());
	JLabel network = new JLabel(series.getNetwork());
	JLabel airday = new JLabel(series.getAirday());
	JLabel airtime = new JLabel("Airtime: " + series.getAirtime());
	JLabel runtime = new JLabel("Runtime: " + series.getRuntime() + " min");
	JLabel status = new JLabel("Status: " + series.getStatus());

	JTextPane overview = new JTextPane();
	overview.setEditable(false);
	overview.setText(series.getOverview());

	infoPanel.add(name);
	infoPanel.add(network);
	infoPanel.add(airday);
	infoPanel.add(airtime);
	infoPanel.add(runtime);
	infoPanel.add(status);

	//this.add(); picture here
	this.add(infoPanel, "");
	this.add(overview);
	//this.add();
    }

    private void addEpisodeContent(){

    }
}
