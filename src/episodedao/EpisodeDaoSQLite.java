package episodedao;

import database.DBHandler;
import database.QueryType;
import tvseries.DateHandler;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
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

    private static final String DELETE_STATEMENT = "DELETE FROM episodes where tvdb_id=%s";

    private static final String SELECT_ONE_STATEMENT = "SELECT show_id, episode_name, first_aired, episodenumber, " +
						       "seasonnumber, absolutenumber, overview, watch_count " +
						       "FROM episodes WHERE tvdb_id=%d";

    private static final String SELECT_ALL_STATEMENT = "SELECT tvdb_id, episode_name, first_aired, episodenumber, " +
						       "seasonnumber, absolutenumber, overview, watch_count " +
						       "FROM episodes WHERE show_id=%d";

    private static final String ADD_TO_HISTORY = "INSERT INTO history " +
    						"(episode_id, watch_date) " +
    						"VALUES ('%s', '%s');";

    private static final String SELECT_ALL_IDS = "SELECT tvdb_id FROM episodes";


    /**
     * Preforms a database update on the episode
     * @param episode
     * @param queryType
     */
    public void updateEpisode(Episode episode, QueryType queryType) {
	String statement = createStatement(episode, queryType);
	executeUpdate(statement);
    }

    public void updateMultipleEpisodes(List<Episode> episodes, QueryType queryType) {
	List<String> updatestatements = new ArrayList<>();

	for (Episode episode : episodes) {
	    updatestatements.add(createStatement(episode, queryType));
	}
	executeMultipleUpdates(updatestatements);
    }

    private String createStatement(Episode episode, QueryType queryType) {
	String statement = "";
	switch (queryType) {
	    case UPDATE:
		statement = String.format(UPDATE_STATEMENT, episode.getName(), episode.getFirstAiredString(),
						episode.getEpisodeNumber(), episode.getSeasonNumber(),
						episode.getAbsoluteNumber(), episode.getOverview(), episode.getTvDbId());
		break;
	    case INSERT:
		statement = String.format(INSERT_STATEMENT, episode.getShowId(), episode.getTvDbId(), episode.getName(),
						episode.getFirstAiredString(), episode.getEpisodeNumber(),
						episode.getSeasonNumber(), episode.getAbsoluteNumber(), episode.getOverview());
		break;
	    case DELETE:
		statement = String.format(DELETE_STATEMENT, episode.getTvDbId());
		break;
	}
	return statement;
    }

    @Override public Episode getEpisode(int episodeId) {
	createConnection();
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
		String firstAired = resultSet.getString("first_aired");

		episode =
			new Episode(name, overview, showId, episodeId, episodeNumber, seasonNumber, absoluteNumber, watchCount);
		episode.deciedWatchedStatus(watchCount);
		episode.setFirstAired(firstAired);
	    }

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
	return episode;
    }

    @Override public List<Episode> getAllEpisodes(int seriesId) {
	createConnection();
	List<Episode> episodes = new ArrayList<>();
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
		String firstAired = resultSet.getString("first_aired");

		Episode episode = new Episode(name, overview, seriesId, episodeId, episodeNumber, seasonNumber, absoluteNumber,
					      watchCount);
		episode.deciedWatchedStatus(watchCount);
		episode.setFirstAired(firstAired);
		episodes.add(episode);
	    }

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
	return episodes;
    }




    public void updateWatchCount(Episode episode) {
	List<String> statements = new ArrayList<>();

	String currentDate = DateHandler.dateToString(new Date());
	String updateStatement = String.format("UPDATE episodes SET watch_count=%d WHERE tvdb_id=%d", episode.getWatchCount(),
					       episode.getTvDbId());
	String historyStatement = String.format(ADD_TO_HISTORY, episode.getTvDbId(), currentDate);

	statements.add(updateStatement);
	statements.add(historyStatement);

	executeMultipleUpdates(statements);
    }

    public void updateWatchCountMultipleEpisodes(List<Episode> episodes) {
	List<String> updateStatements = new ArrayList<>();
	String currentDate = DateHandler.dateToString(new Date());
	for (Episode episode : episodes) {
	    System.out.println("LOG: (EpisodeDaoSQLite) Updating: " + episode.getName());
	    String updateStatement =
		    String.format("UPDATE episodes SET watch_count=%d WHERE tvdb_id=%d", episode.getWatchCount(),
				  episode.getTvDbId());
	    String historyStatement = String.format(ADD_TO_HISTORY, episode.getTvDbId(), currentDate);
	    updateStatements.add(updateStatement);
	    updateStatements.add(historyStatement);
	}
	executeMultipleUpdates(updateStatements);
    }

    public List<Date> getWatchHistoryForEpisode(Episode episode) {
	createConnection();
	List<Date> dates = new ArrayList<>();
	ResultSet resultSet;
	try {
	    resultSet = statement.executeQuery(String.format("SELECT watch_date from history WHERE episode_id=%d", episode.getTvDbId()));
	    while (resultSet.next()) {
		Date date = DateHandler.stringToDate(resultSet.getString("watch_date"));
		dates.add(date);
	    }
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
	return dates;
    }

    public void removeHistoryEntry(Date date) {
	String dateString = DateHandler.dateToString(date);
	String statement = String.format("DELETE from history WHERE watch_date='%s'", dateString);
	executeUpdate(statement);
    }
}
