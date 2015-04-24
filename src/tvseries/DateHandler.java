package tvseries;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class DateHandler{

    // 2014-09-01T09:10:11.000Z
    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.XX");

    public static Date stringToDate(String dateString) {
	Date date = null;
	try {
	    date = sdf.parse(dateString);
	} catch (ParseException e) {
	    e.printStackTrace();
	}
	return date;
    }

    public static String dateToString(Date date){
	return sdf.format(date);
    }

    public static Date getNewestDate(List<Date> dates) {
	Collections.sort(dates);
	for (Date date : dates) {
	    System.out.println(dateToString(date));
	}
	return dates.get(0);
    }
}
