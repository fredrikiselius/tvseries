package seriesdao;

import database.DBHandler;
import database.QueryType;
import episodedao.Episode;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * This class handles all the querys to the Series table in the database. There are methods for adding, updateing and
 * deleting..
 */
public class SeriesDaoSQLite extends DBHandler implements SeriesDao
{

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


    public SeriesDaoSQLite() {

    }

    /**
     * Updated the database based on the query type. It can update, insert and delete.
     * @param series The series to be updated
     * @param queryType The query type to be used
     */
    public void updateSeries(Series series, QueryType queryType) {
	String statement = createStatement(series, queryType);
	executeUpdate(statement);
    }

    public void updateMultipleSeries(List<Series> seriesList, QueryType queryType) {
	List<String> updatestatements = new ArrayList<>();
	for (Series series : seriesList) {
	    updatestatements.add(createStatement(series, queryType));
	}
	executeMultipleUpdates(updatestatements);
    }

    /**
     * Creates a statement based on the query type.
     * @param series The series used to create the statement
     * @param queryType The type of statement to be created
     * @return A string containing the statement
     */
    private String createStatement(Series series, QueryType queryType) {
	String statement = "";
	switch (queryType) {
	    case UPDATE:
		statement = String.format(UPDATE_STATEMENT, series.getNetwork(), series.getAirday(), series.getAirtime(),
					  series.getOverview(), series.getStatus(), series.getTvDbId());
		break;
	    case INSERT:
		statement = String.format(INSERT_STATEMENT, series.getTvDbId(), series.getShowName(), series.getNetwork(),
					  series.getAirday(), series.getAirtime(), series.getOverview(), series.getStatus());
		break;
	    case DELETE:
		statement = String.format(DELETE_STATEMENT, series.getTvDbId());
		break;
	}
	return statement;
    }

    /**
     * Fetches all information about a Series from the database
     *
     * @param seriesID the tvdb id for the Series to fetched
     *
     * @return new Series object with the fetched information
     */
    @Override public Series getSeries(int seriesID) {
	createConnection();

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

	    } catch (SQLException e) {
		e.printStackTrace();
	    }
	}
	return series;
    }

    /**
     * Fetches all Series currently in the database
     *
     * @return List<Series> containing Series objects from all the Series
     */
    @Override public List<Series> getAllSeries() {
	createConnection();

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

	    } catch (SQLException e) {
		e.printStackTrace();
	    }
	}
	return allSeries;
    }

    /**
     * Fetches all the Series ids from the database
     *
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
	} catch (SQLException e) {
	    e.printStackTrace();
	}
	return loadedIds;
    }
}
