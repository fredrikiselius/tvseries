package tvseries;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * This class is used to convert strings to dates and dates to string.
 * The date format used is as follows: 2014-09-01T09:10:11.000Z
 */
public final class DateHandler{
    private static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.XX");

    private DateHandler() {}

    /**
     * Converts a string contaning a date to a date object.
     * @param dateString String containing the date to be converted.
     * @return Date
     */
    public static Date stringToDate(String dateString) {
	Date date = null;
	try {
	    date = simpleDateFormat.parse(dateString);
	} catch (ParseException e) {
	    e.printStackTrace();
	}
	return date;
    }

    /**
     * Converts a date to a string.
     * @param date The date to be converted.
     * @return String containing the date.
     */
    public static String dateToString(Date date){
	return simpleDateFormat.format(date);
    }
}
