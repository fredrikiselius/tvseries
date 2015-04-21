package episodedao;

import database.DBHandler;
import seriesdao.Series;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class EpisodeDaoSQLite extends DBHandler implements EpisodeDao
{
    private static final String UPDATE_STATEMENT = "UPDATE episodes SET " +
						   "episode_name='%s', first_aired='%s', episodenumber=%d, " +
						   "seasonnumber=%d, absolutenumber=%d, overview='%s'" +
						   "WHERE tvdb_id=%d";

    private static final String INSERT_STATEMENT = "INSERT INTO episodes " +
                                                   "(show_id, tvdb_id, episode_name, first_aired, episodenumber, seasonnumber, " +
                                                   "absolutenumber, overview) " +
                                                   "VALUES (%d, %d, '%s', '%s', %d, %d, %d, '%s');";

    private static final String SELECT_ONE_STATEMENT = "SELECT show_id, episode_name, first_aired, episodenumber, " +
                                                       "seasonnumber, absolutenumber, overview, watch_count " +
                                                       "FROM episodes WHERE tvdb_id=%d";

    private static final String SELECT_ALL_STATEMENT = "SELECT tvdb_id, episode_name, first_aired, episodenumber, " +
                                                       "seasonnumber, absolutenumber, overview, watch_count " +
                                                       "FROM episodes WHERE show_id=%d";

    private static final String SELECT_ALL_IDS = "SELECT tvdb_id FROM episodes";


    @Override public void insertMultipleEpisodes(List<Episode> episodes) {
        createConnection();
       	isExecuting = true;

       	try {
            statement.executeUpdate("BEGIN");
            for (Episode episode : episodes) {
		String insertStatement = String.format(INSERT_STATEMENT,
                                                       episode.getShowId(), episode.getTvDbId(), episode.getName(),
                                                       episode.getFirstAiredString(), episode.getEpisodeNumber(), episode.getSeasonNumber(),
                                                       episode.getAbsoluteNumber(), episode.getOverview());
                statement.executeUpdate(insertStatement);
            }
            statement.executeUpdate("COMMIT");
       	} catch (SQLException e) {
       	    e.printStackTrace();
       	} finally {
       	    try {
       		statement.close();
       		connection.close();
       		isExecuting = false;
       	    } catch (SQLException e) {
       		e.printStackTrace();
       	    }

       	}
    }

    /**
     * Inserts an episode and all its info into the database
     * @param episode the episode to be inserted
     */
    @Override public void insertEpisode(Episode episode) {
        createConnection();
       	isExecuting = true;
       	String insertStatement = String.format(INSERT_STATEMENT,
                                               episode.getShowId(), episode.getTvDbId(), episode.getName(),
                                               episode.getFirstAired(), episode.getEpisodeNumber(), episode.getSeasonNumber(),
                                               episode.getAbsoluteNumber(), episode.getOverview());
       	try {
       	     statement.executeUpdate(insertStatement);
       	} catch (SQLException e) {
       	    e.printStackTrace();
       	} finally {
       	    try {
       		statement.close();
       		connection.close();
       		isExecuting = false;
       	    } catch (SQLException e) {
       		e.printStackTrace();
       	    }

       	}
    }

    @Override public Episode getEpisode(int episodeId) {
        createConnection();
       	isExecuting = true;
       	ResultSet resultSet;
       	Episode episode = null;
       	try {
       	    resultSet = statement.executeQuery(String.format(SELECT_ONE_STATEMENT, episodeId));
       	    while (resultSet.next()) {
       		int showId = resultSet.getInt("show_id");
       		int episodeNumber = resultSet.getInt("episodenumber");
                int seasonNumber = resultSet.getInt("seasonnumber");
                int absoluteNumber = resultSet.getInt("absolutenumber");
       		int watchCount = resultSet.getInt("watch_count");
                String name = resultSet.getString("episode_name");
                String overview = resultSet.getString("overview");
                String firstAried = resultSet.getString("first_aired");

       		episode = new Episode(name, overview, showId, episodeId, episodeNumber,
                                      seasonNumber, absoluteNumber, watchCount);
                episode.deciedWatchedStatus(watchCount);
                episode.setFirstAired(firstAried);
       	    }

       	} catch (SQLException e) {
       	    e.printStackTrace();
       	} finally {
       	    try {
       		statement.close();
       		connection.close();
       		isExecuting = false;
       	    } catch (SQLException e) {
       		e.printStackTrace();
       	    }
       	}
       	return episode;
    }

    @Override public List<Episode> getAllEpisodes(int seriesId) {
        createConnection();
        List<Episode> episodes = new ArrayList<>();
       	isExecuting = true;
       	ResultSet resultSet;
       	try {
       	    resultSet = statement.executeQuery(String.format(SELECT_ALL_STATEMENT, seriesId));
       	    while (resultSet.next()) {
       		int episodeId = resultSet.getInt("tvdb_id");
       		int episodeNumber = resultSet.getInt("episodenumber");
                int seasonNumber = resultSet.getInt("seasonnumber");
                int absoluteNumber = resultSet.getInt("absolutenumber");
       		int watchCount = resultSet.getInt("watch_count");
                String name = resultSet.getString("episode_name");
                String overview = resultSet.getString("overview");
                String firstAried = resultSet.getString("first_aired");

       		Episode episode = new Episode(name, overview, seriesId, episodeId, episodeNumber,
                                      seasonNumber, absoluteNumber, watchCount);
                episode.deciedWatchedStatus(watchCount);
                episode.setFirstAired(firstAried);
                episodes.add(episode);
       	    }

       	} catch (SQLException e) {
       	    e.printStackTrace();
       	} finally {
       	    try {
       		statement.close();
       		connection.close();
       		isExecuting = false;
       	    } catch (SQLException e) {
       		e.printStackTrace();
       	    }
       	}
       	return episodes;
    }


    @Override public void updateEpisode(Episode episode) {

    }

    @Override public void updateMultipleEpisodes(List<Episode> episodes) {
	createConnection();
	isExecuting = true;

	try {
	    statement.executeUpdate("BEGIN");
	    for (Episode episode : episodes) {
		System.out.println("LOG: (EpisodeDaoSQLite) Updating: " + episode.getName());
		String insertStatement = String.format(UPDATE_STATEMENT,
						       episode.getName(),
						       episode.getFirstAiredString(), episode.getEpisodeNumber(), episode.getSeasonNumber(),
						       episode.getAbsoluteNumber(), episode.getOverview(), episode.getTvDbId());
		statement.executeUpdate(insertStatement);
	    }
	    statement.executeUpdate("COMMIT");
	} catch (SQLException e) {
	    e.printStackTrace();
	} finally {
	    try {
		statement.close();
		connection.close();
		isExecuting = false;
	    } catch (SQLException e) {
		e.printStackTrace();
	    }

	}
    }

    public void updateWatchCount(Episode episode) {
	createConnection();
	String updateStatement = String.format("UPDATE episodes SET watch_count=%d WHERE tvdb_id=%d",
					       episode.getWatchCount(), episode.getTvDbId()) ;
	try {
	    statement.executeUpdate(updateStatement);
	} catch (SQLException e) {
	    e.printStackTrace();
	} finally {
	    try {
		statement.close();
		connection.close();
	    } catch (SQLException e) {
		e.printStackTrace();
	    }
	}
    }

    public void updateWatchCountMultipleEpisodes(List<Episode> episodes) {
	createConnection();

	try {
	    statement.executeUpdate("BEGIN");
	    for (Episode episode : episodes) {
		System.out.println("LOG: (EpisodeDaoSQLite) Updating: " + episode.getName());
		String updateStatement = String.format("UPDATE episodes SET watch_count=%d WHERE tvdb_id=%d", episode.getWatchCount(),
						       episode.getTvDbId()) ;
		statement.executeUpdate(updateStatement);
	    }
	    statement.executeUpdate("COMMIT");
	} catch (SQLException e) {
	    e.printStackTrace();
	} finally {
	    try {
		statement.close();
		connection.close();
		isExecuting = false;
	    } catch (SQLException e) {
		e.printStackTrace();
	    }

	}
    }

    @Override public void deleteEpisode(final Episode episode) {

    }

    @Override public void deleteMultipleEpisodes(final List<Episode> episodes) {

    }
}
