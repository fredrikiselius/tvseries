package episodedao;

import database.DBHandler;
import database.QueryType;
import tvseries.DateHandler;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This class is used to both fetch and store information in the episode table in the database.
 */
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


    private static final String SELECT_ALL_STATEMENT = "SELECT tvdb_id, episode_name, first_aired, episodenumber, " +
						       "seasonnumber, absolutenumber, overview, watch_count " +
						       "FROM episodes WHERE show_id=%d";

    private static final String ADD_TO_HISTORY = "INSERT INTO history " +
    						"(episode_id, watch_date) " +
    						"VALUES ('%s', '%s');";



    public void updateMultipleEpisodes(List<Episode> episodes, QueryType queryType) {
	Collection<String> updateStatements =
		episodes.stream().map(episode -> createStatement(episode, queryType)).collect(Collectors.toList());

	executeMultipleUpdates(updateStatements);
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

    @Override public List<Episode> getAllEpisodes(int seriesId) {
	createConnection();
	List<Episode> episodes = new ArrayList<>();
	// The database is local thus there is no need to worry about security
	//noinspection JDBCExecuteWithNonConstantString
	try (ResultSet resultSet = statement.executeQuery(String.format(SELECT_ALL_STATEMENT, seriesId))) {
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
	String updateStatement = String.format("UPDATE episodes SET watch_count=%d WHERE tvdb_id=%d", episode.getWatchCount(),
					       episode.getTvDbId());
	executeUpdate(updateStatement);
    }

    public void updateWatchCountMultipleEpisodes(Iterable<Episode> episodes) {
	Collection<String> updateStatements = new ArrayList<>();
	String currentDate = DateHandler.dateToString(new Date());
	for (Episode episode : episodes) {
	    String updateStatement =
		    String.format("UPDATE episodes SET watch_count=%d WHERE tvdb_id=%d", episode.getWatchCount(),
				  episode.getTvDbId());
	    String historyStatement = String.format(ADD_TO_HISTORY, episode.getTvDbId(), currentDate);
	    updateStatements.add(updateStatement);
	    updateStatements.add(historyStatement);
	}
	executeMultipleUpdates(updateStatements);
    }


    /**
     * Fetches the watch history for a specified episode
     * @param episode The episode for which to get the watch history
     * @return List<Dates> dates, containing the watch history
     */
    public List<Date> getWatchHistoryForEpisode(Episode episode) { //We decided to use date since it seemed simple to implement
	createConnection();
	List<Date> dates = new ArrayList<>();
	// The database is local thus there is no need to worry about security
	//noinspection JDBCExecuteWithNonConstantString
	try (ResultSet resultSet = statement.executeQuery(String.format("SELECT watch_date from history WHERE episode_id=%d", episode.getTvDbId()))) {
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

    /**
     * Removes an entry in the database
     * @param date Date entry to be matched
     * @param episode Episode id to be matched
     */
    public void removeHistoryEntry(Date date, Episode episode) { //We decided to use date since it seemed simple to implement
	String dateString = DateHandler.dateToString(date);
	System.out.println("Remove " + dateString);
	String statement = String.format("DELETE from history WHERE watch_date='%s' AND episode_id=%d", dateString, episode.getTvDbId());
	executeUpdate(statement);
    }

    /**
     * Add an entry in the history table
     * @param episode the episode to be added
     */
    public void addHistoryEntry(Episode episode) {
	String currentDate = DateHandler.dateToString(new Date());
	String statement = String.format(ADD_TO_HISTORY, episode.getTvDbId(), currentDate);
	executeUpdate(statement);
    }
}
