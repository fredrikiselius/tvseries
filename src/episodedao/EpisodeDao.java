package episodedao;

import java.util.List;

public interface EpisodeDao {
    public void insertMultipleEpisodes(List<Episode> episodes);
    public void insertEpisode(Episode episode);

    public Episode getEpisode(int episodeId);
    public List<Episode> getAllEpisodes(int seriesId);

    public void updateEpisode(Episode episode);
    public void updateMultipleEpisodes(List<Episode> episodes);

    public void deleteEpisode(Episode episode);
    public void deleteMultipleEpisodes(List<Episode> episodes);
}
