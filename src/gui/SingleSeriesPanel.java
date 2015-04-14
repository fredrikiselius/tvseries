package gui;

import tvseries.Series;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

/**
 * Custom JPanel used to draw series information
 */
public class SingleSeriesPanel extends JPanel
{
    private static final int FONT_SIZE = 12;

    private BufferedImage fanart;
    private Series series;
    private int width;
    private int height;

    public SingleSeriesPanel(Series series) {
	Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
	this.series = series;
	this.width = screenSize.width - 200;
	this.height = 600;
	this.setPreferredSize(new Dimension(width, height));

	URL fanartUrl = getClass().getResource("/" + series.getTvDbId() + "/fanart.jpg");
	try {
	    fanart = ImageIO.read(fanartUrl);
	} catch (IOException e) {
	    e.printStackTrace();
	}


    }

    @Override public Dimension getPreferredSize() {
	return new Dimension(width, height);
    }

    @Override public void paintComponent(Graphics g) {
	super.paintComponent(g);
	final Graphics2D g2d = (Graphics2D) g;
	Font font = new Font("Arial", Font.BOLD, FONT_SIZE);
	String[] info = {
		series.getShowName(), series.getAirday() + " at " + series.getAirtime(),
	};


	g2d.drawImage(fanart,
		      0, 0, width, 150,
		      0, 100, width, 250,
		      this);

	g2d.setFont(font);

	int margin = 0;
	for (String s : info) {

	}
    }
}
