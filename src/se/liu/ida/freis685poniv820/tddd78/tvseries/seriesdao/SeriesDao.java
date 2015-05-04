package se.liu.ida.freis685poniv820.tddd78.tvseries.seriesdao;


import se.liu.ida.freis685poniv820.tddd78.tvseries.database.QueryType;

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
