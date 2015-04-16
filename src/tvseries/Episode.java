package tvseries;

import javax.swing.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Episode
{
    private int showId;
    private int tvDbId;
    private String name;
    private int epNumb;
    private int seNumb;
    private String overview;
    private Date firstAired;
    private boolean watchedStatus;
    private int watchCount;

    public Episode() {
	this.watchedStatus = false;
    }

    public int getTvDbId() {
	return tvDbId;
    }

    public void setTvDbId(final int tvDbId) {
	this.tvDbId = tvDbId;
    }

    public String getName() {
	return name;
    }

    public void setName(final String name) {
	this.name = name;
    }

    public int getEpNumb() {
	return epNumb;
    }

    public void setEpNumb(final int epNumb) {
	this.epNumb = epNumb;
    }

    public int getSeNumb() {
	return seNumb;
    }

    public void setSeNumb(final int seNumb) {
	this.seNumb = seNumb;
    }

    public String getOverview() {
	return overview;
    }

    public void setOverview(final String overview) {
	this.overview = overview;
    }

    public Date getFirstAired() {
	return firstAired;
    }

    public void setFirstAired(String firstAired) {
	if (!firstAired.isEmpty()) {
	    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	    try {
		this.firstAired = sdf.parse(firstAired);
	    } catch (ParseException e) {
		e.printStackTrace();
	    }
	}
    }

    /**
     * Marks the episode as watched. It also calls upon the addWatched method to make an entry in the database.
     */
    public void markAsWatched() {
	DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	Date currentDate = new Date();
	this.watchedStatus = true;
	this.watchCount++;

	new SwingWorker<Void, Void>()
	{

	    @Override protected Void doInBackground() throws Exception {
		TVDBDataMapper.addWatched(tvDbId, watchCount, dateFormat.format(currentDate));
		return null;
	    }
	}.execute();
    }

    public void removeMostRecentWatched() {
	if (watchCount > 0) {
            watchCount--;
        }

	new SwingWorker<Void, Void>() {
	    @Override protected Void doInBackground() throws Exception {
		TVDBDataMapper.removeWatched(tvDbId, watchCount);
		return null;
	    }
	}.execute();

    }

    public void setWatchedStatus(final boolean watchedStatus) {
	this.watchedStatus = watchedStatus;
    }

    public int getWatchCount() {
	return watchCount;
    }

    public void setWatchCount(final int watchCount) {
	if (watchCount >= 0) {
	    this.watchCount = watchCount;
	}

    }
}
