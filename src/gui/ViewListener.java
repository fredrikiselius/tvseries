package gui;

public interface ViewListener {

    // Go from multiple to single view
    public void multipleViewChanged(SingleSeriesView ssv);

    // Go from single to multiple
    public void singleViewChanged();
}
