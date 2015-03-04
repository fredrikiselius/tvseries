package tvseries;

import java.net.URL;

public class URLHandler
{
    private static String showName;
    private static String showId;

    public String searchUrl;
    public String zipUrl;

    final static String TVDB_API = "6A988698B3E59C3C";
    final static String BASE_URL = "http://thetvdb.com";
    final static String SEARCH_URL = "http://thetvdb.com/api/GetSeries.php?seriesname=";
    final static String ZIP_URL = "http://thetvdb.com/api/6A988698B3E59C3C/series/%s/all/en.zip";

    public URLHandler(String showName) {
        this.showName = showName;
        this.searchUrl = SEARCH_URL + showName.replaceAll(" ", "%20");
    }

    public void setZipUrl(String showId) {
        this.showId = showId;
        this.zipUrl = String.format(ZIP_URL, showId);
    }

    public static URL searchURL(String seriesName) throws Exception {
	String BASE_URL = "http://thetvdb.com";
	String SEARCH_SERIES_URL = "http://thetvdb.com/api/GetSeries.php?seriesname=";
	return new URL(BASE_URL + SEARCH_SERIES_URL + seriesName.replaceAll(" ", "%20"));
    }
}
