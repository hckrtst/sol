package com.hellosanket.sol;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by sanket on 6/1/16.
 */
public class CalendarDataHelper {
    private Map<String, Calendar> data = new HashMap<>();

    private static CalendarDataHelper ourInstance = new CalendarDataHelper();

    public static CalendarDataHelper getInstance() {
        return ourInstance;
    }

    private CalendarDataHelper() {
    }

    Calendar getCalFor(String key) {
        return data.get(key);
    }

    void setCalFor(String key, Calendar value) {
        data.put(key, value);
    }
}
