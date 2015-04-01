package gui;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;

/**
 * loads the images needed
 */
public class PictureLoader
{
    private final static int POSTER_WIDTH = 180;
    private final static int POSTER_HEIGHT = 265;
    private final static int FANART_WIDTH = 600;
    private final static int FANART_HEIGHT = 300;

    /**
     * @param tvDbId
     * @return ImageIcon
     */

    public static ImageIcon loadPoster(String tvDbId) {
	File tvDbPoster = new File("showdata/" + tvDbId + "/poster.jpg");
	File noPoster = new File("img/no.jpg");
	return loadPicture(tvDbPoster, noPoster, POSTER_WIDTH, POSTER_HEIGHT);
    }

    public static ImageIcon loadFanart(String tvDbId) {
	File tvDbFanart = new File("showdata/" + tvDbId + "/fanart.jpg");
	File noFanart = new File("img/no.jpg");

		// Use a 'non-avalible' picture if one cannot be found
	File poster;
	if (tvDbFanart.exists()) {
	    poster = tvDbFanart;
	} else {
	    poster = noFanart;
	}

	Image img = null;
	try {
	    img = ImageIO.read(poster);
	} catch (IOException ioe) {
	    ioe.printStackTrace();
	} finally {
	    if (img != null) {
		return new ImageIcon(img);
	    }
	}
	return null;

    }



    private static ImageIcon loadPicture(File picture, File noPicture, int width, int height) {

	// Use a 'non-avalible' picture if one cannot be found
	File poster;
	if (picture.exists()) {
	    poster = picture;
	} else {
	    poster = noPicture;
	}

	Image img = null;
	try {
	    img = ImageIO.read(poster);
	} catch (IOException ioe) {
	    ioe.printStackTrace();
	} finally {
	    if (img != null) {
		return new ImageIcon(img.getScaledInstance(width, height, Image.SCALE_DEFAULT));
	    }
	}
	return null;
    }
}
