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

    /**
     * @param tvDbId
     * @return ImageIcon
     */
    public static ImageIcon loadPoster(String tvDbId) {
	File tvDbPoster = new File("img/" + tvDbId + ".jpg");
	File noPoster = new File("img/no.jpg");

	// Use a 'non-avalible' poster if one cannot be found on thetvdb
	File poster;
	if (tvDbPoster.exists()) {
	    poster = tvDbPoster;
	} else {
	    poster = noPoster;
	}

	Image img = null;
	try {
	    img = ImageIO.read(poster);
	} catch (IOException ioe) {
	    ioe.printStackTrace();
	} finally {
	    if (img != null) {
		return new ImageIcon(img.getScaledInstance(POSTER_WIDTH, POSTER_HEIGHT, Image.SCALE_DEFAULT));
	    }
	}
	return null;
    }
}
