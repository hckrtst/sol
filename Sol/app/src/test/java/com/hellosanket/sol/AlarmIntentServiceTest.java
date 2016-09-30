package com.hellosanket.sol;

import android.app.AlarmManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;

import org.apache.tools.ant.Main;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.Shadows;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowAlarmManager;
import org.robolectric.shadows.ShadowIntent;
import org.robolectric.shadows.ShadowIntentService;
import org.robolectric.shadows.ShadowService;
import org.robolectric.shadows.ShadowSystemClock;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import static junit.framework.Assert.assertNull;
import static junit.framework.TestCase.assertEquals;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import static org.robolectric.Shadows.shadowOf;


/**
 * Created by sanket on 8/23/16.
 */

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class AlarmIntentServiceTest {
    ShadowAlarmManager shadowAlarmManager;
    Context context;
    AlarmIntentService service;
    CalendarDataHelper dataHelper;
    SimpleDateFormat sdf;
    Calendar sunriseCal;
    Calendar sunsetCal;


    @Before
    public void setUp() {
        context = RuntimeEnvironment.application.getApplicationContext();
        AlarmManager alarmManager = (AlarmManager) RuntimeEnvironment.application.getSystemService(Context.ALARM_SERVICE);
        shadowAlarmManager = shadowOf(alarmManager);
        service = Robolectric.setupService(AlarmIntentService.class);
        dataHelper = CalendarDataHelper.getInstance();
        sdf = new SimpleDateFormat("YYYY MM dd hh:mm a z");


        // TODO can we set shadow system clock?
        /*ShadowSystemClock.setCurrentTimeMillis(1424369871446L);
        System.out.println("1) Shadow time = " + sdf.format(new Date(ShadowSystemClock.currentThreadTimeMillis())));
        System.out.println("2) Calendar time = " + sdf.format((new GregorianCalendar().getTime())) );*/
    }

    @Test
    public void testSunriseAlarm(){
        int offset = 10;

        // initialize data with some future value
        sunriseCal = new GregorianCalendar();
        sunriseCal.add(Calendar.DATE, 1);
        sdf = new SimpleDateFormat("YYYY MM dd hh:mm a z");
        System.out.println("Set mock initial sunrise time = " + sdf.format(sunriseCal.getTime()));
        dataHelper.setCalFor(CalendarDataHelper.sunrise_key, sunriseCal);

        sunsetCal = new GregorianCalendar();
        sunsetCal.add(Calendar.DATE, 2);
        System.out.println("Set mock initial sunset time = " + sdf.format(sunsetCal.getTime()));
        dataHelper.setCalFor(CalendarDataHelper.sunset_key, sunsetCal);

        setAlarm(offset, Constants.SolarEvents.SUNRISE);
        ShadowAlarmManager.ScheduledAlarm scheduledAlarm = shadowAlarmManager.getNextScheduledAlarm();
        Calendar scheduledCal = new GregorianCalendar();
        scheduledCal.setTimeInMillis(scheduledAlarm.triggerAtTime);

        System.out.println("Set sunrise alarm for = " + sdf.format(scheduledCal.getTime()));

        Calendar expected = (Calendar) sunriseCal.clone();
        expected.add(Calendar.MINUTE, -1*offset);

        //ShadowPendingIntent shadowPendingIntent = shadowOf(scheduledAlarm.operation);
        assertEquals(scheduledCal.getTime(), expected.getTime());

    }

    @Test
    public void testSunsetAlarm(){
        int offset = 20;

        // initialize data with some future value
        sunriseCal = new GregorianCalendar();
        sunriseCal.add(Calendar.DATE, 1);

        System.out.println("Set mock initial sunrise time = " + sdf.format(sunriseCal.getTime()));
        dataHelper.setCalFor(CalendarDataHelper.sunrise_key, sunriseCal);

        sunsetCal = new GregorianCalendar();
        sunsetCal.add(Calendar.DATE, 2);
        System.out.println("Set mock initial sunset time = " + sdf.format(sunsetCal.getTime()));
        dataHelper.setCalFor(CalendarDataHelper.sunset_key, sunsetCal);

        setAlarm(offset, Constants.SolarEvents.SUNSET);
        ShadowAlarmManager.ScheduledAlarm scheduledAlarm = shadowAlarmManager.getNextScheduledAlarm();
        Calendar scheduledCal = new GregorianCalendar();
        scheduledCal.setTimeInMillis(scheduledAlarm.triggerAtTime);

        System.out.println("Set sunset alarm for = " + sdf.format(scheduledCal.getTime()));

        Calendar expected = (Calendar) sunsetCal.clone();
        expected.add(Calendar.MINUTE, -1*offset);

        //ShadowPendingIntent shadowPendingIntent = shadowOf(scheduledAlarm.operation);
        assertEquals(scheduledCal.getTime(), expected.getTime());
    }

    @Test
    public void testRepeatingSunriseAlarm() {
        System.out.println("--------- testRepeatingSunriseAlarm ----------");
        // initialize data with some past value
        sunriseCal = new GregorianCalendar();
        sunriseCal.add(Calendar.DATE, -1);
        sdf = new SimpleDateFormat("YYYY MM dd hh:mm a z");
        System.out.println("Set mock initial sunrise time = " + sdf.format(sunriseCal.getTime()));
        dataHelper.setCalFor(CalendarDataHelper.sunrise_key, sunriseCal);

        sunsetCal = new GregorianCalendar();
        sunsetCal.add(Calendar.DATE, -1);
        System.out.println("Set mock initial sunset time = " + sdf.format(sunsetCal.getTime()));
        dataHelper.setCalFor(CalendarDataHelper.sunset_key, sunsetCal);
        doNotification(Constants.SolarEvents.SUNRISE);

        // check that new intent sent for adding next alarm
        ShadowService shadowService = new ShadowService();
        Intent intent = shadowService.getNextStartedService();
        ShadowIntent shadowIntent = shadowOf(intent);
        System.out.println("action = " + intent.getAction());
        //MatcherAssert.assertThat(intent.getClass(), is(Matchers.equalTo(MainService.class)));
        assertEquals(shadowIntent.getIntentClass(), MainService.class);
        Bundle data = intent.getExtras();
        Calendar calendar = (Calendar) data.get(MainService.ACTION_GET_SOLAR_TIMES_EXTRA_CAL);
        System.out.println("Setting next alarm for " + sdf.format(calendar.getTime()));
        System.out.println();
        System.out.println(">" + data);
        Calendar nextCal = dataHelper.getCalFor(CalendarDataHelper.sunrise_key);
        System.out.println("Next sunrise set for " + sdf.format(nextCal.getTime()));
        System.out.println("------------------------------------------------");
    }

    @Test
    public void testRepeatingSunsetAlarm() {
        System.out.println("--------- testRepeatingSunsetAlarm ----------");
        // initialize data with some past value
        sunsetCal = new GregorianCalendar();
        sunsetCal.add(Calendar.DATE, -1);
        System.out.println("Set mock initial sunset time = " + sdf.format(sunsetCal.getTime()));
        dataHelper.setCalFor(CalendarDataHelper.sunset_key, sunsetCal);

        doNotification(Constants.SolarEvents.SUNSET);

        // check that new intent sent for adding next alarm
        ShadowService shadowService = new ShadowService();
        Intent intent = shadowService.getNextStartedService();
        ShadowIntent shadowIntent = shadowOf(intent);
        System.out.println("action = " + intent.getAction());
        //MatcherAssert.assertThat(intent.getClass(), is(Matchers.equalTo(MainService.class)));
        assertEquals(shadowIntent.getIntentClass(), MainService.class);
        Bundle data = intent.getExtras();
        Calendar calendar = (Calendar) data.get(MainService.ACTION_GET_SOLAR_TIMES_EXTRA_CAL);
        System.out.println("Setting next alarm for " + sdf.format(calendar.getTime()));
        System.out.println();
        System.out.println(">" + data);
        Calendar nextCal = dataHelper.getCalFor(CalendarDataHelper.sunset_key);
        System.out.println("Next sunset set for " + sdf.format(nextCal.getTime()));
        System.out.println("------------------------------------------------");
    }

    @Test
    public void testAlarmCancellation() {
        int offset = 20;

        // initialize data with some future value
        sunriseCal = new GregorianCalendar();
        sunriseCal.add(Calendar.DATE, 1);
        sdf = new SimpleDateFormat("YYYY MM dd hh:mm a z");
        System.out.println("Set mock initial sunrise time = " + sdf.format(sunriseCal.getTime()));
        dataHelper.setCalFor(CalendarDataHelper.sunrise_key, sunriseCal);

        sunsetCal = new GregorianCalendar();
        sunsetCal.add(Calendar.DATE, 2);
        System.out.println("Set mock initial sunset time = " + sdf.format(sunsetCal.getTime()));
        dataHelper.setCalFor(CalendarDataHelper.sunset_key, sunsetCal);

        setAlarm(offset, Constants.SolarEvents.SUNSET);
        // peek so as to to keep it
        ShadowAlarmManager.ScheduledAlarm scheduledAlarm = shadowAlarmManager.peekNextScheduledAlarm();
        Calendar scheduledCal = new GregorianCalendar();
        scheduledCal.setTimeInMillis(scheduledAlarm.triggerAtTime);

        System.out.println("Set sunset alarm for = " + sdf.format(scheduledCal.getTime()));

        Calendar expected = (Calendar) sunsetCal.clone();
        expected.add(Calendar.MINUTE, -1*offset);

        //ShadowPendingIntent shadowPendingIntent = shadowOf(scheduledAlarm.operation);
        assertEquals(scheduledCal.getTime(), expected.getTime());

        // now cancel it
        cancelAlarm(Constants.SolarEvents.SUNSET);

        // peek so as to to keep it
        scheduledAlarm = shadowAlarmManager.getNextScheduledAlarm();

        assertNull(scheduledAlarm);

    }


    private void setAlarm(int offset, Constants.SolarEvents event) {
        Intent intent = new Intent(context, AlarmIntentService.class);
        intent.setAction(AlarmIntentService.ACTION_ADD);
        intent.putExtra(AlarmIntentService.EXTRA_OFFSET, offset);
        intent.putExtra(AlarmIntentService.EXTRA_ALARM_TYPE, event);
        service.onHandleIntent(intent);
    }

    private void cancelAlarm(Constants.SolarEvents event) {
        Intent intent = new Intent(context, AlarmIntentService.class);
        intent.setAction(AlarmIntentService.ACTION_CLEAR);
        intent.putExtra(AlarmIntentService.EXTRA_ALARM_TYPE, event);
        service.onHandleIntent(intent);
    }

    private void doNotification(Constants.SolarEvents event) {
        Intent intent = new Intent(context, AlarmIntentService.class);
        intent.setAction(AlarmIntentService.ACTION_SHOW);
        intent.putExtra(AlarmIntentService.EXTRA_ALARM_TYPE, Constants.SolarEvents.SUNRISE);
        service.onHandleIntent(intent);
    }


}
