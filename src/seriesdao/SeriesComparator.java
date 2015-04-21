package seriesdao;

import java.util.Comparator;

/**
 * Comparator for Series objects.
 */
public class SeriesComparator implements Comparator<Series> {
    @Override
    public int compare(Series s1, Series s2) {
	return s1.getShowName().compareTo(s2.getShowName());
    }
}
