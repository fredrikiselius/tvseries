package tvseries;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Episode {
    private int showId;
    private int tvDbId;
    private String name;
    private int epNumb;
    private int seNumb;
    private String overview;
    private Date firstAired;

    public Episode() {

    }

    public int getShowId() {
	return showId;
    }

    public void setShowId(final int showId) {
	this.showId = showId;
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
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            this.firstAired = sdf.parse(firstAired);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}
