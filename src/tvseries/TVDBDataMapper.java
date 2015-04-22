package tvseries;


import parser.ShowDataParser;
import seriesdao.Series;
import episodedao.Episode;

import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * TVDBDataMapper is used to interact with the database.
 * There is functionality for adding new shows and episodes as well as removing them.
 */
public class TVDBDataMapper
{
    static String dbName = PropHandler.getDatabaseName();


    /**
     * Puts the initial data from choosing a Series into the database
     * @param showName the name of the Series
     * @param tvDbId the tvdbid for the Series
     */
    public static void initialData(String showName, String tvDbId) {
	try {
	    DBConnection dbc = new DBConnection(dbName);
	    String statement = String.format("INSERT INTO Series (tvdb_id, show_name) VALUES (%s, \'%s\');", tvDbId,
					     showName.replaceAll("'", "\'\'"));
	    dbc.getStatement().executeUpdate(statement);
	    System.out.println("LOG: (TVDBDataMapper) Added " + showName + " (ID:" + tvDbId + ")");
	    dbc.close();
	} catch (SQLException e) {
	    e.printStackTrace();
	}
    }

    /**
     * Using the ShowDataParser this method completes the entry created by initialData with the remaining data.
     * @param tvDbId the tvdb id for the Series
     */
    public static void updateShow(int tvDbId) {
	ShowDataParser sdp = new ShowDataParser(tvDbId);
	DBConnection dbc = new DBConnection(dbName);
	sdp.parseShow();
	sdp.parseBanners();


	String seriesStatement = String.format(("UPDATE Series " +
						"SET network='%s', airday='%s', airtime='%s', firstaired='%s', " +
						"overview='%s', status='%s', runtime='%s', lastupdated=%s " +
						"WHERE tvdb_id=%s"), sdp.getNetwork(), sdp.getAirday(), sdp.getAirtime(),
					       sdp.getFirstAired(), sdp.getOverview(), sdp.getStatus(), sdp.getRuntime(),
					       sdp.getLastUpdated(), tvDbId);

	try {
	    dbc.getStatement().executeUpdate(seriesStatement);
	    dbc.close();
	    FileHandler.fetchPoster(sdp.getPoster(), tvDbId);
	    FileHandler.fetchFanart(sdp.getFanart(), tvDbId);
	} catch (SQLException e) {
	    e.printStackTrace();
	}
    }

    public static void updateEpisodes(int tvDbId) {
	ShowDataParser sdp = new ShowDataParser(tvDbId);
	DBConnection dbc = new DBConnection(dbName);
	sdp.parseShow();

	String statement = "INSERT INTO episodes " +
			   "(show_id, tvdb_id, episode_name, first_aired, episodenumber, seasonnumber, " +
			   "absolutenumber, overview) " +
			   "VALUES (" + tvDbId + ", %s, '%s', '%s', '%s', '%s', '%s', '%s');";

	//"id", "EpisodeName", "EpisodeNumber", "Overview", "SeasonNumber","absolute_number"

	try {
	    dbc.getStatement().executeUpdate("BEGIN");
	    for (Map<String, String> episode : sdp.getAllEpisodes()) {

		String completeStatement =
			String.format(statement, episode.get("id"), episode.get("EpisodeName"), episode.get("FirstAired"),
				      episode.get("EpisodeNumber"), episode.get("SeasonNumber"), episode.get("absolute_number"),
				      episode.get("Overview"));

		dbc.getStatement().executeUpdate(completeStatement);

	    }
	    dbc.getStatement().executeUpdate("COMMIT");
	    System.out.println("LOG: (TVDBDataMapper) Added " + sdp.getNumberOfEpisodes() + " episodes.");
	    dbc.close();
	} catch (SQLException e) {
	    e.printStackTrace();
	}
    }

    /**
     * Decreases the watch count with 1 in the database and also remove the most recent entry in the history table.
     * @param episodeID the tvdbid of the episode
     * @param watchCount total watch count of the episode
     */
    public static void removeWatched(int episodeID, int watchCount) {
	DBConnection dbc = new DBConnection(dbName);
	String episodeStatement = String.format("UPDATE episodes SET watched=%d WHERE tvdb_id=%d", watchCount, episodeID);

	try {
	    dbc.getStatement().executeUpdate(episodeStatement);
	    dbc.close();
	} catch (SQLException e) {
	    e.printStackTrace();
	}
    }

    /**
     * This method is used to add a single episode as watched.
     * @param episodeId   the TvDb id of the episode
     * @param currentDate String consisting of the current date on the format: yyyy-MM-dd hh:mm
     */
    public static void addWatched(int episodeId, int watchCount, String currentDate) {
	DBConnection dbc = new DBConnection(dbName);
	String historyStatement =
		String.format("INSERT INTO history (episode_id, watch_date) " + "VALUES (%s, '%s');", episodeId, currentDate);
	String episodeStatement = String.format("UPDATE episodes SET watched=%d WHERE tvdb_id=%s", watchCount, episodeId);

	try {
	    dbc.getStatement().executeUpdate("BEGIN");
	    dbc.getStatement().executeUpdate(historyStatement);
	    dbc.getStatement().executeUpdate(episodeStatement);
	    dbc.getStatement().executeUpdate("COMMIT");
	    dbc.close();
	} catch (SQLException e) {
	    e.printStackTrace();
	}
    }

    /**
     * This method is used to mark multiple episodes as watched in the database.
     * @param episodes List<Episode> containing all the episode to mark as watched
     */
    public static void addMultipleWatched(Iterable<Episode> episodes) {
	DBConnection dbc = new DBConnection(dbName);
	DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	java.util.Date currentDate = new java.util.Date();

	try {
	    dbc.getStatement().executeUpdate("BEGIN");
	    for (Episode episode : episodes) {
		String historyStatement =
			String.format("INSERT INTO history (episode_id, watch_date) " + "VALUES (%s, '%s');", episode.getTvDbId(),
				      dateFormat.format(currentDate));
		String episodeStatement =
			String.format("UPDATE episodes SET watched=%d WHERE tvdb_id=%s", episode.getWatchCount(), episode.getTvDbId());
		dbc.getStatement().executeUpdate(historyStatement);
		dbc.getStatement().executeUpdate(episodeStatement);
	    }
	    dbc.getStatement().executeUpdate("COMMIT");
	    dbc.close();
	} catch (SQLException e) {
	    e.printStackTrace();
	}
    }


    /**
     * Selects all data on a show based on tvdb id and creates a Series object.
     *
     * @param tvDbId
     *
     * @return
     */
    public static synchronized Series findByTvDbId(String tvDbId) {
	ResultSet resultSet = null;
	try {
	    DBConnection dbc = new DBConnection(dbName);
	    String query = "SELECT tvdb_id, show_name, network, airday, airtime, overview, status, runtime " +
			   "FROM Series WHERE tvdb_id=" + tvDbId + " ORDER BY show_name ASC";

	    resultSet = dbc.getStatement().executeQuery(query);

	    while (resultSet.next()) {
		int id = resultSet.getInt("tvdb_id");
		String name = resultSet.getString("show_name");
		String network = resultSet.getString("network");
		String airday = resultSet.getString("airday");
		String airtime = resultSet.getString("airtime");
		String overview = resultSet.getString("overview");
		String status = resultSet.getString("status");
		String runtime = resultSet.getString("runtime");

		Series series = new Series(id);
		series.setShowName(name);
		series.setNetwork(network);
		series.setAirday(airday);
		series.setAirtime(airtime);
		series.setOverview(overview);
		series.setStatus(status);
		series.setRuntime(runtime);
		dbc.close();
		return series;
	    }
	    dbc.close();
	    return null;

	} catch (SQLException e) {
	    e.getStackTrace();
	    return null;
	} finally {
	    if (resultSet != null) {
		try {
		    resultSet.close();
		} catch (SQLException e) {
		    e.printStackTrace();
		}
	    }
	}
    }


    public static synchronized List<Episode> findByShowId(int showId) {
	List<Episode> episodes = new ArrayList<Episode>();
	ResultSet rs = null;
	try {
	    DBConnection dbc = new DBConnection("tvseries");
	    String query = "SELECT tvdb_id, episode_name, first_aired, episodenumber, seasonnumber, overview, watched " +
			   "FROM episodes WHERE show_id=" + showId + " ORDER BY seasonnumber,episodenumber ASC";

	    //String query = "SELECT * FROM episodes";

	    rs = dbc.getStatement().executeQuery(query);


	    while (rs.next()) {
		int tvDbId = rs.getInt("tvdb_id");
		String name = rs.getString("episode_name");
		String firstAired = rs.getString("first_aired");
		String overview = rs.getString("overview");
		int season = rs.getInt("seasonnumber");
		int episode = rs.getInt("episodenumber");
		int watched = rs.getInt("watched");

		Episode ep = new Episode();
		ep.setTvDbId(tvDbId);
		ep.setName(name);
		ep.setFirstAired(firstAired);
		ep.setOverview(overview);
		ep.setSeasonNumber(season);
		ep.setEpisodeNumber(episode);
		ep.setWatchCount(watched);
		if (watched > 0) {
		    ep.setWatchedStatus(true);
		}

		episodes.add(ep);
	    }
	    dbc.close();

	    return episodes;

	} catch (SQLException e) {
	    e.getStackTrace();
	    return null;
	} finally {
	    if (rs != null) {
		try {
		    rs.close();
		} catch (SQLException e) {
		    e.printStackTrace();
		}
	    }
	}
    }


    /**
     * Selects all the tvdb ids from the database and returns them in an ArrayList
     *
     * @return
     */
    public static synchronized List<String> selectAllIds() {
	ResultSet resultSet = null;
	try {
	    DBConnection dbc = new DBConnection(dbName);
	    List<String> arrayList = new ArrayList<String>();
	    String query = "SELECT tvdb_id FROM Series ORDER BY show_name ASC";
	    resultSet = dbc.getStatement().executeQuery(query);
	    while (resultSet.next()) {
		arrayList.add(resultSet.getString("tvdb_id"));
	    }

	    dbc.close();
	    return arrayList;
	} catch (SQLException e) {
	    return null;
	} finally {
	    if (resultSet != null) {
		try {
		    resultSet.close();
		} catch (SQLException e) {
		    e.printStackTrace();
		}
	    }
	}
    }

    public static synchronized void delete(String tvDbId) {
	try {
	    DBConnection dbc = new DBConnection(dbName);
	    String sStatement = String.format("DELETE FROM Series where tvdb_id=%s", tvDbId);
	    String eStatement = String.format("DELETE FROM episodes where show_id=%s", tvDbId);

	    dbc.getStatement().executeUpdate("BEGIN");
	    dbc.getStatement().executeUpdate(sStatement);
	    dbc.getStatement().executeUpdate(eStatement);
	    dbc.getStatement().executeUpdate("COMMIT");
	    dbc.close();
	    System.out.println("LOG: (TVDBDataMapper) Removed show with id: " + tvDbId);

	} catch (SQLException e) {
	    e.getStackTrace();
	}

    }
}

