package episodedao;

import java.util.Comparator;

public class EpisodeComparator implements Comparator<Episode> {

    @Override
    public int compare(Episode episode1, Episode episode2) {

	Integer season1 = episode1.getSeasonNumber();
	Integer season2 = episode2.getSeasonNumber();
	int seasonCompare = season1.compareTo(season2);

	if (seasonCompare != 0) {
	    return seasonCompare;
	} else {
	    Integer episodeNo1 = episode1.getEpisodeNumber();
	    Integer episodeNo2 = episode2.getEpisodeNumber();
	    return episodeNo1.compareTo(episodeNo2);
	}
    }
}
