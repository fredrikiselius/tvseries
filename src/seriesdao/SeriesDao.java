package seriesdao;


import java.util.List;

public interface SeriesDao {
    public void insertSeries(Series series);

    public Series getSeries(int seriesID); // where seriesID is the tvdb id..
    public List<Series> getAllSeries();

    public void updateSeries(Series series);
    public void updateMultipleSeries(List<Series> series);

    public void deleteSeries(Series series);
}
