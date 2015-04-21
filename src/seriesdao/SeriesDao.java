package seriesdao;


import java.util.List;

public interface SeriesDao {
    public void insertSeries(series series);

    public series getSeries(int seriesID); // where seriesID is the tvdb id..
    public List<series> getAllSeries();

    public void updateSeries(series series);
    public void updateMultipleSeries(List<series> series);

    public void deleteSeries(series series);
}
