package gui;

import net.miginfocom.swing.MigLayout;
import tvseries.Series;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.File;

/**
 * Custom JPanel used to draw series information
 */
public class SingleSeriesPanel extends JPanel
{
    private static final int FONT_SIZE = 12;
    private static final int MENU_WIDTH = 200;
    private static final int FANART_HEIGHT = 150;
    private static final int Y_OFFSET = 100;

    private BufferedImage fanart;
    private Series series;
    private JTextArea jta;
    private int width;
    private int height;

    public SingleSeriesPanel(Series series) {
	Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
	this.series = series;
	this.width = screenSize.width - MENU_WIDTH;

	File fanartUrl = new File("showdata/" + series.getTvDbId() + "/fanart.jpg");
	try {
	    fanart = ImageIO.read(fanartUrl);
	} catch (IOException e) {
	    e.printStackTrace();
	}

	setLayout(new MigLayout());
	addOverview();
	System.out.println("JTA rows: " + jta.getRows());
	this.height = (FANART_HEIGHT + 40 + 10 + 200);
	System.out.println("Total height: " + height);
	this.setPreferredSize(new Dimension(width, height));
    }

    private void addOverview() {
	jta = new JTextArea(series.getOverview());
	jta.setEditable(false);
	jta.setLineWrap(true);
	this.add(jta, "w 550!, gaptop " + (FANART_HEIGHT + (20 * 2)) + ", gapleft 8");
    }

    @Override public Dimension getPreferredSize() {
	return new Dimension(width, height);
    }

    @Override public void paintComponent(Graphics g) {
	super.paintComponent(g);
	final Graphics2D g2d = (Graphics2D) g;
	Font font = new Font("Arial", Font.BOLD, FONT_SIZE);

	String info = series.getShowName()+ " " + series.getAirday() + " at " + series.getAirtime() + " "
					   + series.getNetwork() + " " + series.getStatus();



	// draw header image, fanart
	g2d.setColor(Color.BLACK);
	g2d.fillRect(0, 0, width, FANART_HEIGHT);
	g2d.drawImage(fanart,
		      0, 0, Math.min(width, fanart.getWidth()), FANART_HEIGHT, // area to draw
		      0, Y_OFFSET, Math.min(width, fanart.getWidth()), Y_OFFSET + FANART_HEIGHT, // area to draw from
		      this);

	// draw series information
	g2d.setFont(font);


	g2d.drawString(info, 10, FANART_HEIGHT + 20);

    }
}
