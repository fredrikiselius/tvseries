package se.liu.ida.freis685poniv820.tddd78.tvseries.gui;

/**
 * Used to notify the SeriesFrame when there are changes in the different views.
 */
public interface ViewListener {
    // Go from multiple to single view
    void multipleViewChanged(SingleSeriesView ssv);

    // Go from single to multiple
    void singleViewChanged();

    void totalTimeChanged();
}
