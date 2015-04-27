package seriesdao;

import episodedao.Episode;
import episodedao.EpisodeComparator;
import episodedao.EpisodeDaoSQLite;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * The Series class contains all the information about a specific show. Only the tvDbId should be added in the constructor.
 */
public class Series
{
    private int tvDbId;
    private String showName;
    private String network;
    private String airday;
    private String airtime;
    private String overview;
    private String status;
    private int runtime;
    private String nextAirDate;
    private Date firstAired;


    public Series() {

    }

    /**
     * Create a Series with just the tvdb id
     * @param tvDbId The tvdb id for the Series.
     */
    public Series(int tvDbId) {
        this.tvDbId = tvDbId;
    }

    /**
     * Create a Series with all its data.
     * @param tvDbId     The tvdb id for the Series.
     * @param showName   The name of the Series.
     * @param network    The network where the Series airs.
     * @param airday     Day of the week that the Series airs.
     * @param airtime    The time when the Series airs (The timezone is that of where the network resides)
     * @param overview   Overview of the Series
     * @param status     The Series status, ex: continueing, ended
     * @param runtime    The duration of each episode
     * @param firstAired The date that the Series was first aired
     */
    public Series(int tvDbId, String showName, String network, String airday, String airtime, String overview, String status,
                  int runtime, String firstAired)
    {
        this.tvDbId = tvDbId;
        this.showName = showName;
        this.network = network;
        this.airday = airday;
        this.airtime = airtime;
        this.overview = overview;
        this.status = status;
        this.runtime = runtime;

        SimpleDateFormat originalFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            this.firstAired = originalFormat.parse(firstAired);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }


    /**
     * Decides which episode that will air next. If the latest aired episode was aired prior to todays date, then nextAirDate is
     * set to the shows status..
     */
    private void calculateNextEp() {
        EpisodeDaoSQLite episodeDb = new EpisodeDaoSQLite();
        List<Episode> episodes = episodeDb.getAllEpisodes(this.tvDbId);
        Collections.sort(episodes, new EpisodeComparator());
        Date currentDate = new Date(); // must be of Date type to be able to compare

        DateFormat df = new SimpleDateFormat("dd MMMM");

        Episode nextEp = null;
        // Make sure the database returns any episodes
        if (episodes != null) {
            for (Episode episode : episodes) {
                if (episode.getWatchCount() == 0 && episode.getSeasonNumber() > 0) {
                    nextEp = episode;
                    break;
                }
                // Make sure that there is first aired datee

            }
        }

        if (nextEp != null) {
            Date firstAired = nextEp.getFirstAired();
            if (firstAired != null && currentDate.compareTo(firstAired) < 0) {
                nextAirDate = "Next ep: " + df.format(firstAired);
                return;
            } else if (firstAired != null && currentDate.compareTo(firstAired) > 0) {
                nextAirDate = "Next: " + nextEp.getSeasonNumber() + "x" + nextEp.getEpisodeNumber();
                return;
            }
        }

        nextAirDate = status;
    }

    public String getNextAirDate() {
        calculateNextEp();
        if (nextAirDate != null) {
            return nextAirDate;
        }
        return "TBA";
    }

    public void setTvDbId(final int tvDbId) {
        this.tvDbId = tvDbId;
    }

    public void setFirstAired(String firstAiredString) {
        SimpleDateFormat originalFormat = new SimpleDateFormat("yyyy-MM-dd");
        if (!firstAiredString.isEmpty()) {
            try {
                this.firstAired = originalFormat.parse(firstAiredString);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }

    public int getTvDbId() {
        return tvDbId;
    }

    public String getShowName() {
        return showName;
    }

    public void setShowName(final String showName) {
        this.showName = showName;
    }

    public String getNetwork() {
        return network;
    }

    public void setNetwork(final String network) {
        this.network = network;
    }

    public String getAirday() {
        return airday;
    }

    public void setAirday(final String airday) {
        this.airday = airday;
    }

    public String getAirtime() {
        return airtime;
    }

    public void setAirtime(final String airtime) {
        this.airtime = airtime;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(final String overview) {
        this.overview = overview;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(final String status) {
        this.status = status;
    }

    public void setRuntime(int runtime) {
        this.runtime = runtime;
    }

    public int getRuntime() {
        return runtime;
    }

    @Override public String toString() {
        return "Series{" +
                "tvDbId=" + tvDbId +
                ", showName='" + showName + '\'' +
                ", network='" + network + '\'' +
                ", airday='" + airday + '\'' +
                ", airtime='" + airtime + '\'' +
                ", overview='" + overview + '\'' +
                ", status='" + status + '\'' +
                ", runtime='" + runtime + '\'' +
                ", nextAirDate='" + nextAirDate + '\'' +
                ", firstAired=" + firstAired +
                '}';
    }
}
