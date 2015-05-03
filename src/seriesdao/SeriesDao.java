package seriesdao;


import database.QueryType;

import java.util.List;

/**
 * The SeriesDao
 */
public interface SeriesDao {

    void updateSeries(Series series, QueryType queryType);
    void updateMultipleSeries(List<Series> seriesList, QueryType queryType);
    List<Series> getAllSeries();
    List<String> selectAllIds();


}
