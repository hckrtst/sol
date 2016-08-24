package com.hellosanket.sol;

import android.app.AlarmManager;
import android.content.Context;
import android.content.Intent;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowAlarmManager;
import org.robolectric.shadows.ShadowPendingIntent;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.robolectric.Shadows.shadowOf;

/**
 * Created by sanket on 8/23/16.
 */

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class)
public class SetAlarmTest {
    ShadowAlarmManager shadowAlarmManager;
    Context context;
    AlarmIntentService service;
    CalendarDataHelper dataHelper;
    SimpleDateFormat sdf;
    Calendar sunriseCal;


    @Before
    public void setUp() {
        context = RuntimeEnvironment.application.getApplicationContext();
        AlarmManager alarmManager = (AlarmManager) RuntimeEnvironment.application.getSystemService(Context.ALARM_SERVICE);
        shadowAlarmManager = shadowOf(alarmManager);
        service = Robolectric.setupService(AlarmIntentService.class);
        dataHelper = CalendarDataHelper.getInstance();

        // initialize data with some future value
        sunriseCal = new GregorianCalendar();
        sunriseCal.add(Calendar.DATE, 1);
        sdf = new SimpleDateFormat("MM dd hh:mm a z");
        System.out.println("Set mock initial sunrise time = " + sdf.format(sunriseCal.getTime()));
        dataHelper.setCalFor(CalendarDataHelper.sunrise_key, sunriseCal);

    }

    @Test
    public void setSunriseAlarm(){
        int offset = 10;
        Intent intent = new Intent(context, AlarmIntentService.class);
        intent.setAction(AlarmIntentService.ACTION_ADD);
        intent.putExtra(AlarmIntentService.EXTRA_OFFSET, offset);
        intent.putExtra(AlarmIntentService.EXTRA_ALARM_TYPE, Constants.SolarEvents.SUNRISE);

        service.onHandleIntent(intent);

        ShadowAlarmManager.ScheduledAlarm scheduledAlarm = shadowAlarmManager.getNextScheduledAlarm();
        Calendar scheduledCal = new GregorianCalendar();
        scheduledCal.setTimeInMillis(scheduledAlarm.triggerAtTime);

        System.out.println("Set sunrise alarm for = " + sdf.format(scheduledCal.getTime()));

        Calendar expected = (Calendar) sunriseCal.clone();
        expected.add(Calendar.MINUTE, -1*offset);

        //ShadowPendingIntent shadowPendingIntent = shadowOf(scheduledAlarm.operation);
        assertEquals(scheduledCal.getTime(), expected.getTime());
    }
}
