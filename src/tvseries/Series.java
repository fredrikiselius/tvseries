package tvseries;

public class Series {
    private String tvDbId;
    private String showName;
    private String network;
    private String airday;
    private String airtime;
    private String overview;
    private String status;
    private String runtime;

    public Series(String tvDbId) {
        this.tvDbId = tvDbId;
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
}
