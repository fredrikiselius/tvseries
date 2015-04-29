package episodedao;

import database.QueryType;

import java.util.List;

public interface EpisodeDao {
    public List<Episode> getAllEpisodes(int seriesId);

    public void updateEpisode(Episode episode, QueryType queryType);
    public void updateMultipleEpisodes(List<Episode> episodes, QueryType queryType);
}
