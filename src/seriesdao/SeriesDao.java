package seriesdao;


import database.QueryType;

import java.util.List;

public interface SeriesDao {
    public Series getSeries(int seriesID); // where seriesID is the tvdb id..
    public List<Series> getAllSeries();

    public void updateSeries(Series series, QueryType queryType);
    public void updateMultipleSeries(List<Series> series, QueryType queryType);
}
