package com.hellosanket.sol;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * Central store of computed
 * calendar objects for sunrise and sunset
 */
public class CalendarDataHelper {
    private Map<String, Calendar> data = new HashMap<>();
    private static CalendarDataHelper ourInstance = new CalendarDataHelper();
    public static String sunrise_key = "sunrise";
    public static String sunset_key = "sunset";
    public static CalendarDataHelper getInstance() {
        return ourInstance;
    }

    private CalendarDataHelper() {
    }

    // TODO
    // Does it make sense to always clone? It will prevent modification of the data
    // but will need to see if this causes too many GC runs
    synchronized Calendar getCalFor(String key) throws NullPointerException{
        return (Calendar)data.get(key).clone();
    }

    synchronized void setCalFor(String key, Calendar value) {
        data.put(key, value);
    }
}
