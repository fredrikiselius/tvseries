package seriesdao;


import database.QueryType;

import java.util.List;

/**
 * The SeriesDao
 */
public interface SeriesDao {

    void updateSeries(Series series, QueryType queryType);
    void updateMultipleSeries(List<Series> seriesList, QueryType queryType);
    Series getSeries(int seriesID); // where seriesID is the tvdb id..
    List<Series> getAllSeries();

}
