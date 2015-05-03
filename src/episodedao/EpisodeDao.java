package episodedao;

import database.QueryType;

import java.util.List;

/**
 * DAO interface for Episodes, we decided to use an interface here partly because it is part of the pattern,
 * but also to be able to implement another storage source for the information later on.
 */
public interface EpisodeDao {
    void updateEpisode(Episode episode, QueryType queryType);
    void updateMultipleEpisodes(List<Episode> episodes, QueryType queryType);

    Episode getEpisode(int episodeId);
    List<Episode> getAllEpisodes(int seriesId);
}
