package com.hellosanket.sol;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;

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
    protected static final String ACTION_COMPUTE = "com.hellosanket.sol.action.compute";
    protected static final String ACTION_COMPUTE_SUNRISE = "com.hellosanket.sol.action.compute.sunrise";
    protected static final String ACTION_COMPUTE_SUNSET = "com.hellosanket.sol.action.compute.sunset";
    protected static final String ACTION_SAVE = "com.hellosanket.sol.action.save";
    protected static final String RESULT_SUNSET = "com.hellosanket.sol.result.sunset";
    protected static final String RESULT_SUNRISE = "com.hellosanket.sol.result.sunrise";
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

    public static void startComputeService(final Context context, final Location location,
                                           final Calendar calendar) {
        Intent intent = new Intent(context, SolarDataIntentService.class);
        intent.setAction(ACTION_COMPUTE);
        intent.putExtra(Constants.SOLAR_DATA_INTENT_LOC_EXTRA, location);
        intent.putExtra(Constants.SOLAR_DATA_INTENT_CAL_EXTRA, calendar);
        context.startService(intent);
    }

    public static void startComputeServiceByType(final Context context, final Location location,
                                                 final Calendar calendar, Constants.SolarEvents event) {
        Intent intent = new Intent(context, SolarDataIntentService.class);
        if (event == Constants.SolarEvents.SUNRISE) {
            intent.setAction(ACTION_COMPUTE_SUNRISE);
        } else {
            intent.setAction(ACTION_COMPUTE_SUNSET);
        }
        intent.putExtra(Constants.SOLAR_DATA_INTENT_LOC_EXTRA, location);
        intent.putExtra(Constants.SOLAR_DATA_INTENT_CAL_EXTRA, calendar);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_COMPUTE.equals(action)) {
                Location location = intent.getParcelableExtra(Constants.SOLAR_DATA_INTENT_LOC_EXTRA);
                Bundle data = intent.getExtras();
                Calendar calendar = (Calendar) data.get(Constants.SOLAR_DATA_INTENT_CAL_EXTRA);
                L.d(TAG, "onHandleIntent ACTION_COMPUTE = " + getPrettyTime(calendar));
                try {
                    handleActionCompute(location, calendar, Constants.SolarEvents.SUNRISE);
                    handleActionCompute(location, calendar, Constants.SolarEvents.SUNSET);
                } catch (NullPointerException e) {
                    L.e(TAG, "failed to get location");
                }
            } else if (ACTION_COMPUTE_SUNRISE.equals(action)) {
                Location location = intent.getParcelableExtra(Constants.SOLAR_DATA_INTENT_LOC_EXTRA);
                Bundle data = intent.getExtras();
                Calendar calendar = (Calendar) data.get(Constants.SOLAR_DATA_INTENT_CAL_EXTRA);
                L.d(TAG, "onHandleIntent ACTION_COMPUTE_SUNRISE = " + getPrettyTime(calendar));
                try {
                    handleActionCompute(location, calendar, Constants.SolarEvents.SUNRISE);
                } catch (NullPointerException e) {
                    L.e(TAG, "failed to get location");
                }
            } else if (ACTION_COMPUTE_SUNSET.equals(action)) {
                Location location = intent.getParcelableExtra(Constants.SOLAR_DATA_INTENT_LOC_EXTRA);
                Bundle data = intent.getExtras();
                Calendar calendar = (Calendar) data.get(Constants.SOLAR_DATA_INTENT_CAL_EXTRA);
                L.d(TAG, "onHandleIntent ACTION_COMPUTE_SUNSET = " + getPrettyTime(calendar));
                try {
                    handleActionCompute(location, calendar, Constants.SolarEvents.SUNSET);
                } catch (NullPointerException e) {
                    L.e(TAG, "failed to get location");
                }
            }else if (ACTION_SAVE.equals(action)) {
                L.d(TAG, "saving");
            }
        }
    }

    /**
     * Handle action Compute in the provided background thread with the provided
     * parameters.
     */
    private void handleActionCompute(@NonNull final Location location,
                                     @Nullable Calendar eventCal,
                                     final Constants.SolarEvents event) {
        MyLocation loc;

        try {
            loc = new MyLocation(new Double(location.getLatitude()).toString(),
                    new Double(location.getLongitude()).toString());
            String timeZone = SimpleTimeZone.getDefault().getID();
            SunriseSunsetCalculator calculator = new SunriseSunsetCalculator(loc, timeZone);
            Calendar cal = eventCal;
            L.d(TAG, ">>>>>>>>> handleActionCompute for date = " + getPrettyTime(cal));
            if (cal == null) {
                L.w(TAG, "Cannot compute time for null calendar");
                return;
            }

            // FIXME this needs to be refactored
            if (event == Constants.SolarEvents.SUNRISE) {
                Calendar sunriseCal = calculator.getOfficialSunriseCalendarForDate(cal);
                // if current time is past sunrise time then re-compute next one
                /*if (cal.compareTo(sunriseCal) == 1) {
                    cal.add(Calendar.DAY_OF_WEEK, 1);
                    sunriseCal = calculator.getOfficialSunriseCalendarForDate(cal);
                }*/
                String sunrise = getPrettyTime(sunriseCal);
                /*DataWrapper.saveString(getApplicationContext(),
                        Constants.SOL_DB, Constants.SUNRISE_TIME_TEXT_KEY, sunrise);
                CalendarDataHelper dataHelper = CalendarDataHelper.getInstance();
                dataHelper.setCalFor(CalendarDataHelper.sunrise_key, sunriseCal);*/
                updateSunriseReceivers(sunriseCal);

            } else {
                Calendar sunsetCal = calculator.getOfficialSunsetCalendarForDate(cal);
                // if current time is past sunset time then re-compute next one
                /*if (cal.compareTo(sunsetCal) == 1) {
                    cal.add(Calendar.DAY_OF_WEEK, 1);
                    sunsetCal = calculator.getOfficialSunsetCalendarForDate(cal);
                }*/
                String sunset = getPrettyTime(sunsetCal);
                /*DataWrapper.saveString(getApplicationContext(),
                        Constants.SOL_DB, Constants.SUNSET_TIME_TEXT_KEY, sunset);
                CalendarDataHelper dataHelper = CalendarDataHelper.getInstance();
                dataHelper.setCalFor(CalendarDataHelper.sunset_key, sunsetCal);*/
                updateSunsetReceivers(sunsetCal);
            }
        } catch (NullPointerException e) {
            L.e(TAG, "Null location");
        }
    }

    private String getPrettyTime(Calendar cal) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM dd hh:mm a z");
        if (cal == null) return "null";
        return simpleDateFormat.format(cal.getTime());
    }

    private void updateSunsetReceivers(Calendar calendar) {
        Intent intent = new Intent(RESULT_SUNSET);
        intent.putExtra("calendar", calendar);
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
        L.d(TAG,"sent sunset time");
    }

    private void updateSunriseReceivers(Calendar calendar) {
        Intent intent = new Intent(RESULT_SUNRISE);
        intent.putExtra("calendar", calendar);
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
        L.d(TAG,"sent sunrise time");
    }

}
