package seriesdao;

import database.DBHandler;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * This class handles all the querys to the Series table in the database.
 * There are methods for adding, updateing and deleting..
 */
public class SeriesDaoSQLite extends DBHandler implements SeriesDao{

    private static final String UPDATE_STATEMENT = "UPDATE Series SET " +
						  "network='%s', airday='%s', airtime='%s', overview='%s', " +
						  "status='%s' " +
						  "WHERE tvdb_id=%s";

    private static final String INSERT_STATEMENT = "INSERT INTO Series " +
						   "(tvdb_id, show_name, network, airday," +
						   "airtime, overview, status) " +
						   "VALUES ('%s', '%s', '%s', '%s', '%s', " +
						   "'%s', '%s')";

    private static final String SELECT_STATEMENT = "SELECT tvdb_id, show_name, network, airday, " +
						   "airtime, overview, status, runtime " +
						   "FROM Series";

    private static final String DELETE_STATEMENT = "DELETE FROM Series where tvdb_id=%s";

    private static final String SELECT_ALL_IDS = "SELECT tvdb_id FROM Series";


    private boolean isExecuting;

    public SeriesDaoSQLite() {
	isExecuting = false;
    }

    /**
     * Inserts a new Series into the database
     * @param series the Series to be inserted.
     */
    @Override
    public void insertSeries(Series series) {
	createConnection();
	isExecuting = true;
	String insertStatement = String.format(INSERT_STATEMENT,
		series.getTvDbId(), series.getShowName(),series.getNetwork(), series.getAirday(),
		series.getAirtime(), series.getOverview(), series.getStatus());

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

    /**
     * Updates a Series already in the databse
     * @param series the Series to be updated
     */
    @Override
    public void updateSeries(Series series) {
	createConnection();
	isExecuting = true;
	String updateStatement = String.format(UPDATE_STATEMENT,
		series.getNetwork(), series.getAirday(), series.getAirtime(),
		series.getOverview(), series.getStatus(), series.getTvDbId()
	);

	try {
	     statement.executeUpdate(updateStatement);
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
     * Updates multiple Series already in the database
     * @param series List containing the Series to be updated
     */
    @Override
    public void updateMultipleSeries(List<Series> series) {
	createConnection();
	isExecuting = true;
	try {
	    statement.executeUpdate("BEGIN");
	    for (Series show : series) {
		System.out.println("LOG: (SeriesDaoSQLite) Updating " + show.getShowName());
		String updateStatement = String.format(UPDATE_STATEMENT,
				show.getNetwork(), show.getAirday(), show.getAirtime(),
				show.getOverview(), show.getStatus(), show.getTvDbId());

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

    /**
     * Fetches all information about a Series from the database
     * @param seriesID the tvdb id for the Series to fetched
     * @return new Series object with the fetched information
     */
    @Override
    public Series getSeries(int seriesID) {
	createConnection();
	isExecuting = true;
	ResultSet resultSet;
	Series series = null;
	try {
	    resultSet = statement.executeQuery(SELECT_STATEMENT + " WHERE tvdb_id=" + seriesID);
	    while (resultSet.next()) {
		String name = resultSet.getString("show_name");
		String network = resultSet.getString("network");
		String airday = resultSet.getString("airday");
		String airtime = resultSet.getString("airtime");
		String overview = resultSet.getString("overview");
		String status = resultSet.getString("status");
		String runtime = resultSet.getString("runtime");

		series = new Series(seriesID);
		series.setShowName(name);
		series.setNetwork(network);
		series.setAirday(airday);
		series.setAirtime(airtime);
		series.setOverview(overview);
		series.setStatus(status);
		series.setRuntime(runtime);
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
	return series;
    }

    /**
     * Fetches all Series currently in the database
     * @return List<Series> containing Series objects from all the Series
     */
    @Override
    public List<Series> getAllSeries() {
	createConnection();
	isExecuting = true;
	List<Series> allSeries = new ArrayList<>();
	ResultSet resultSet;
	try {
	    resultSet = statement.executeQuery(SELECT_STATEMENT);
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

		allSeries.add(series);
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
	return allSeries;
    }

    /**
     * Deletes a Series from the database
     * @param series the Series to be deleted
     */
    @Override
    public void deleteSeries(Series series) {
	createConnection();
	try {
	    statement.executeUpdate(String.format(DELETE_STATEMENT, series.getTvDbId()));
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

    /**
     * Fetches all the Series ids from the database
     * @return List<String> contaning all the ids
     */
    public List<String> selectAllIds() {
	createConnection();
	ResultSet queriedIds;
	List<String> loadedIds = new ArrayList<>();
	try {
	    queriedIds = statement.executeQuery(SELECT_ALL_IDS);
	    while (queriedIds.next()) {
		loadedIds.add(queriedIds.getString("tvdb_id"));
	    }
	} catch ( SQLException e) {
	    e.printStackTrace();
	}
	return loadedIds;
    }
}
