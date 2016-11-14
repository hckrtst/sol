package com.hellosanket.sol;

/**
 * Created by sanket on 5/9/16.
 */
public final class Constants {
    public static final String PACKAGE_NAME =
            "com.hellosanket.sol";
    public static final int PERMISSIONS_REQUEST_LOCATION = 223;
    public static final int SUNRISE_ALARM_TOKEN = 23908;
    public static final int SUNSET_ALARM_TOKEN = 24569;
    public static final int NOTIF_TOKEN = 39893;

    public static final String SOLAR_DATA_INTENT_LOC_EXTRA = "loc";
    public static final String SOLAR_DATA_INTENT_CAL_EXTRA = "cal";

    public static final String SOL_DB = "sol.data";
    public static final String SUNRISE_TIME_TEXT_KEY = "sunsrise.time.text";
    public static final String SUNSET_TIME_TEXT_KEY = "sunset.time.text";
    public static final String SUNRISE_ALARM_OFFSET_KEY = "sunrise.alarm.offset";
    public static final String SUNSET_ALARM_OFFSET_KEY = "sunset.alarm.offset";

    public enum SolarEvents{
      SUNRISE,
      SUNSET
    };

    public static final boolean DBG = false;

}
