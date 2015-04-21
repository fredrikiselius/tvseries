package seriesdao;

import java.util.Comparator;

/**
 * Comparator for series objects.
 */
public class SeriesComparator implements Comparator<series> {
    @Override
    public int compare(series s1, series s2) {
	return s1.getShowName().compareTo(s2.getShowName());
    }
}
