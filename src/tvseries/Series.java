package tvseries;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

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
     * Decides which episode that will air next
     */
    private void calculateNextEp() {
        Date currentDate = new Date();
        List<Episode> episodes = TVDBDataMapper.findByShowId(this.tvDbId);
        DateFormat df = new SimpleDateFormat("MM-dd");

        if (episodes != null) {
            System.out.println("pooooo");


            for (Episode episode : episodes) {
                if (currentDate.compareTo(episode.getFirstAired()) < 0) {
                    nextEpisode = df.format(episode.getFirstAired());
                    System.out.println(nextEpisode);
                    return;
                }
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

    public void setTvDbId(final String tvDbId) {
        this.tvDbId = tvDbId;
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

    public String getRuntime() {
        return runtime;
    }

    public void setRuntime(final String runtime) {
        this.runtime = runtime;
    }

    public String[] getEverything() {
        String[] allInfo = {tvDbId, showName, network, airday, airtime, overview, status, runtime};
        return allInfo;
    }
}
