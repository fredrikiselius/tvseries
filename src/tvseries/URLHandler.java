package tvseries;

import java.net.URL;

public class URLHandler
{
    static String TVDB_API = "6A988698B3E59C3C";
    static String BASE_URL = "http://thetvdb.com";
    static String SEARCH_SERIES_URL = "http://thetvdb.com/api/GetSeries.php?seriesname=";

    public static URL searchURL(String seriesName) throws Exception {
	String BASE_URL = "http://thetvdb.com";
	String SEARCH_SERIES_URL = "http://thetvdb.com/api/GetSeries.php?seriesname=";
	return new URL(BASE_URL + SEARCH_SERIES_URL + seriesName.replaceAll(" ", "%20"));
    }
}
