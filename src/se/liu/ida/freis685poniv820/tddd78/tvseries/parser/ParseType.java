package se.liu.ida.freis685poniv820.tddd78.tvseries.parser;

/**
 * The ParseType enums is used in other classes to decide which information to parse
 */
public enum ParseType
{
    /**
     * Used when information about a whole series is wanted
     */
    SERIES,
    /**
     * Used when information about each episode is wanted
     */
    EPISODE,
    /**
     * Used when parsing the images path
     */
    IMAGE,
    /**
     * Fanart is one type of images that can be parsed
     */
    FANART,
    /**
     * Poster is another type of image that can be parsed.
     */
    POSTER
}
