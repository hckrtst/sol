package com.hellosanket.sol;

import android.content.Context;
import android.content.Intent;
import android.location.Location;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by sanket on 8/29/16.
 */
@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class SolarDataIntentServiceTest {
    Context context;
    SolarDataIntentService service;
    CalendarDataHelper dataHelper;
    SimpleDateFormat sdf = new SimpleDateFormat("MM dd hh:mm a z");
    Calendar sunriseCal;
    Calendar sunsetCal;


    @Before
    public void setUp() {
        context = RuntimeEnvironment.application.getApplicationContext();
        service = Robolectric.setupService(SolarDataIntentService.class);
    }

    @Test
    public void computeSunrise() {
        Location location = new Location("Fake");
        // Use lat-long from your local machine's timezone
        // Not sure how to auto-detect this yet
        location.setLatitude(32.566624);
        location.setLongitude(-117.387795);
        System.out.println(location);

        Intent intent = new Intent(context, SolarDataIntentService.class);
        intent.setAction(SolarDataIntentService.ACTION_COMPUTE);
        intent.putExtra(Constants.SOLAR_DATA_INTENT_LOC_EXTRA, location);
        intent.putExtra(Constants.SOLAR_DATA_INTENT_CAL_EXTRA, Calendar.getInstance());

        service.onHandleIntent(intent);

        dataHelper = CalendarDataHelper.getInstance();
        sunriseCal = dataHelper.getCalFor(CalendarDataHelper.sunrise_key);
        sunsetCal = dataHelper.getCalFor(CalendarDataHelper.sunset_key);
        System.out.println("Sunrise = " + sdf.format(sunriseCal.getTime()));
        System.out.println("Sunset = " + sdf.format(sunsetCal.getTime()));

        // TODO assert based on known events

    }
}
