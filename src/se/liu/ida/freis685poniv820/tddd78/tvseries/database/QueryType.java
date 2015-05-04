package se.liu.ida.freis685poniv820.tddd78.tvseries.database;


/**
 * The QueryType enums is used as identifiers for which query to used when updateing the database.
 */
public enum QueryType {
    /**
     * Update is used when updating an already existing entry
     */
    UPDATE,
    /**
     * Insert is used whn creating a new entry
     */
    INSERT,
    /**
     * Delete is used when deleting a entry
     */
    DELETE
}
