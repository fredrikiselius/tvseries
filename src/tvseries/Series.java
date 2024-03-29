package tvseries;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * The Series class contains all the information about a specific show.
 * Only the tvDbId should be added in the constructor.
 */
public class Series {
    private String tvDbId;
    private String showName;
    private String network;
    private String airday;
    private String airtime;
    private String overview;
    private String status;
    private String runtime;
    private String nextEpisode;

    public Series(String tvDbId) {
        this.tvDbId = tvDbId;
    }

    /**
     * Decides which episode that will air next.
     * If the latest aired episode was aired prior to todays date,
     * then nextEpisode is set to the shows status.
     */
    private void calculateNextEp() {
        Date currentDate = new Date(); // must be of Date type to be able to compare
        List<Episode> episodes = TVDBDataMapper.findByShowId(this.tvDbId);
        DateFormat df = new SimpleDateFormat("dd MMMM");

        Episode nextEp = null;
        // Make sure the database returns any episodes
        if (episodes != null) {
            for (Episode episode : episodes) {
                if (episode.getWatchCount() == 0 && episode.getSeNumb() > 0) {
           		nextEp = episode;
           		break;
           	    }
                // Make sure that there is first aired date

            }
        }

        if (nextEp != null) {
            Date firstAired = nextEp.getFirstAired();
            if (firstAired != null && currentDate.compareTo(firstAired) < 0) {
                nextEpisode = "Next ep: " + df.format(firstAired);
                return;
            } else if (firstAired != null && currentDate.compareTo(firstAired) > 0) {
                nextEpisode = "Next: " + nextEp.getSeNumb() + "x" + nextEp.getEpNumb();
                return;
            }
        }

        nextEpisode = status;
    }

    public String getNextEpisode() {
        calculateNextEp();
        if (nextEpisode != null) {
            return nextEpisode;
        }
        return "TBA";
    }

    @Override public String toString() {
        return "Series{"+tvDbId+", "+showName+"}";
    }

    public String getTvDbId() {
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

    public void setRuntime(final String runtime) {
        this.runtime = runtime;
    }
}
