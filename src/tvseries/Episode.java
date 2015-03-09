package tvseries;

public class Episode {
    private String showId;
    private String tvDbId;
    private String name;
    private String epNumb;
    private String seNumb;
    private String overview;

    public Episode() {

    }

    public String getShowId() {
	return showId;
    }

    public void setShowId(final String showId) {
	this.showId = showId;
    }

    public String getTvDbId() {
	return tvDbId;
    }

    public void setTvDbId(final String tvDbId) {
	this.tvDbId = tvDbId;
    }

    public String getName() {
	return name;
    }

    public void setName(final String name) {
	this.name = name;
    }

    public String getEpNumb() {
	return epNumb;
    }

    public void setEpNumb(final String epNumb) {
	this.epNumb = epNumb;
    }

    public String getSeNumb() {
	return seNumb;
    }

    public void setSeNumb(final String seNumb) {
	this.seNumb = seNumb;
    }

    public String getOverview() {
	return overview;
    }

    public void setOverview(final String overview) {
	this.overview = overview;
    }
}
