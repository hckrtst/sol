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
    AlarmIntentService service;
    CalendarDataHelper dataHelper;
    SimpleDateFormat sdf;
    Calendar sunriseCal;
    Calendar sunsetCal;


    @Before
    public void setUp() {
        context = RuntimeEnvironment.application.getApplicationContext();


    }

    @Test
    public void computeSunrise() {
        Location location = new Location("New")
        SolarDataIntentService.startComputeService(context, )

    }
}
