package com.hellosanket.sol;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.location.Location;

import com.luckycatlabs.sunrisesunset.SunriseSunsetCalculator;
import com.luckycatlabs.sunrisesunset.dto.MyLocation;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.SimpleTimeZone;

/**
 *
 */
public class SolarDataIntentService extends IntentService {
    private static final String ACTION_COMPUTE = "com.hellosanket.sol.action.compute";
    private SunriseSunsetCalculator mCalculator;
    private static final String TAG = "SolarDataIntentService";

    public SolarDataIntentService() {
        super("SolarDataIntentService");
    }

    /**
     * Starts this service to perform action
     *
     * @see IntentService
     */

    public static void startComputeService(final Context context, final Location location) {
        Intent intent = new Intent(context, SolarDataIntentService.class);
        intent.setAction(ACTION_COMPUTE);
        intent.putExtra(Constants.SOLAR_DATA_INTENT_LOC_EXTRA, location);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_COMPUTE.equals(action)) {
                Location location = intent.getParcelableExtra(Constants.SOLAR_DATA_INTENT_LOC_EXTRA);
                try {
                    handleActionCompute(location);
                } catch (NullPointerException e) {
                    L.e(TAG, "failed to get location");
                }
            }
        }
    }

    /**
     * Handle action Compute in the provided background thread with the provided
     * parameters.
     */
    private void handleActionCompute(final Location location) {
        MyLocation loc;
        try {
            loc = new MyLocation(new Double(location.getLatitude()).toString(),
                    new Double(location.getLongitude()).toString());
            String timeZone = SimpleTimeZone.getDefault().getID();
            SunriseSunsetCalculator calculator = new SunriseSunsetCalculator(loc, timeZone);
            Calendar now = new GregorianCalendar();
            Calendar sunriseCal = calculator.getOfficialSunriseCalendarForDate(now);
            Calendar sunsetCal = calculator.getOfficialSunsetCalendarForDate(now);

            // if now is past sunrise then we
            // need next sunrise
            if (now.compareTo(sunriseCal) == 1) {
                Calendar cal = new GregorianCalendar();
                cal.add(Calendar.DAY_OF_WEEK, 1);
                sunriseCal = calculator.getOfficialSunriseCalendarForDate(cal);
            }

            // if now is past sunset then we need next sunset
            if (now.compareTo(sunsetCal) == 1) {
                Calendar cal = new GregorianCalendar();
                cal.add(Calendar.DAY_OF_WEEK, 1);
                sunsetCal = calculator.getOfficialSunsetCalendarForDate(cal);
            }

            String sunrise = getPrettyTime(sunriseCal);
            String sunset = getPrettyTime(sunsetCal);

            DataWrapper.saveString(getApplicationContext(),
                    Constants.SOL_DB, Constants.SUNRISE_TIME_TEXT_KEY, sunrise);
            DataWrapper.saveString(getApplicationContext(),
                    Constants.SOL_DB, Constants.SUNSET_TIME_TEXT_KEY, sunset);

            CalendarDataHelper dataHelper = CalendarDataHelper.getInstance();
            dataHelper.setCalFor(CalendarDataHelper.sunrise_key, sunriseCal);
            dataHelper.setCalFor(CalendarDataHelper.sunset_key, sunsetCal);

        } catch (NullPointerException e) {
            L.e(TAG, "Null location");
        }
    }

    private String getPrettyTime(Calendar cal) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("LLL dd hh:mm a z");
        return simpleDateFormat.format(cal.getTime());
    }

}
