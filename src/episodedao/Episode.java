package episodedao;

import tvseries.TVDBDataMapper;

import javax.swing.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Episode
{
    private String name;
    private String overview;

    private int showId;
    private int tvDbId;
    private int episodeNumber;
    private int seasonNumber;
    private int absoluteNumber;
    private int watchCount;

    private Date firstAired;
    private boolean watchedStatus;


    public Episode() {
	this.watchedStatus = false;
    }

    public Episode(String name, String overview, int showId, int tvDbId, int episodeNumber,
                   int seasonNumber, int absoluteNumber, int watchCount)
    {
        this.name = name;
        this.overview = overview;
        this.showId = showId;
        this.tvDbId = tvDbId;
        this.episodeNumber = episodeNumber;
        this.seasonNumber = seasonNumber;
        this.absoluteNumber = absoluteNumber;
        this.watchCount = watchCount;
    }

    public void setShowId(final int showId) {
        this.showId = showId;
    }

    public String getFirstAiredString() {
        if (firstAired != null) {
            SimpleDateFormat originalFormat = new SimpleDateFormat("yyyy-MM-dd");
            return originalFormat.format(firstAired);
        }
        return "";
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

    public int getEpisodeNumber() {
	return episodeNumber;
    }

    public void setEpisodeNumber(final int episodeNumber) {
	this.episodeNumber = episodeNumber;
    }

    public int getSeasonNumber() {
	return seasonNumber;
    }

    public void setSeasonNumber(final int seasonNumber) {
	this.seasonNumber = seasonNumber;
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

    public int getShowId() {
        return showId;
    }

    public int getAbsoluteNumber() {
        return absoluteNumber;
    }

    public void setAbsoluteNumber(final int absoluteNumber) {
        this.absoluteNumber = absoluteNumber;
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

    public void deciedWatchedStatus(int watchCount) {
        if (watchCount > 0) {
            watchedStatus = true;
        } else {
            watchedStatus = false;
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

	EpisodeDaoSQLite episodeDb = new EpisodeDaoSQLite();
        episodeDb.updateEpisode(this);
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

    public void setWatchCount(int watchCount) {
	if (watchCount >= 0) {
	    this.watchCount = watchCount;
	}

    }

    public void incrementWatchCount(int size) {
        if (size > 0) {
            this.watchCount += size;
        }
    }

    @Override public String toString() {
        return "Episode{" +
               "name='" + name + '\'' +
               ", seasonNumber=" + seasonNumber +
               ", episodeNumber=" + episodeNumber +
               '}';
    }
}
