package com.hellosanket.sol;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Intent;
import android.content.Context;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.support.annotation.NonNull;
import android.support.v7.app.NotificationCompat;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 */
public class AlarmIntentService extends IntentService {
    protected static final String ACTION_ADD = "com.hellosanket.sol.alarmsvc.ADD";
    protected static final String ACTION_ADD_FROM_UI = "sol.alarmsvc.add.from.ui";
    protected static final String ACTION_ADD_FROM_NOTIF = "sol.alarmsvc.add.from.notif";
    protected static final String ACTION_CLEAR = "com.hellosanket.sol.alarmsvc.CLEAR";
    protected static final String ACTION_SHOW = "com.hellosanket.sol.alarmsvc.SHOW";
    protected static final String EXTRA_OFFSET = "com.hellosanket.sol.extra.OFFSET";
    protected static final String EXTRA_ALARM_TYPE = "com.hellosanket.sol.extra.ALARM_TYPE";
    private final String TAG = "AlarmIntentSvc";

    public AlarmIntentService() {
        super("AlarmIntentService");
    }

    /**
     * Starts this service to add alarm
     *
     * @see IntentService
     */
    public static void startActionAdd(final Context context,
                                      Constants.SolarEvents alarmType,
                                      int offset) {
        Intent intent = new Intent(context, AlarmIntentService.class);
        intent.setAction(ACTION_ADD);
        intent.putExtra(EXTRA_OFFSET, offset);
        intent.putExtra(EXTRA_ALARM_TYPE, alarmType);
        context.startService(intent);
    }

    public static void startActionClear(final Context context,
                                        Constants.SolarEvents alarmType) {
        Intent intent = new Intent(context, AlarmIntentService.class);
        intent.setAction(ACTION_CLEAR);
        intent.putExtra(EXTRA_ALARM_TYPE, alarmType);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_ADD.equals(action)) {
                int offset = intent.getIntExtra(EXTRA_OFFSET, -1);
                handleActionAdd((Constants.SolarEvents) intent.getSerializableExtra(EXTRA_ALARM_TYPE), offset);
            } else if (ACTION_SHOW.equals(action)) {
                Constants.SolarEvents evt = (Constants.SolarEvents) intent.getSerializableExtra(EXTRA_ALARM_TYPE);
                switch (evt) {
                    case SUNRISE:
                        showNotification("Wakey wakey", evt);
                        // automatically set event for the next day
                        // TODO stop doing this when user canceled
                        if (isSunriseAlarmRepeating()) {
                            Calendar cal = new GregorianCalendar();
                            cal.add(Calendar.DAY_OF_WEEK, 1);
                            // FIXME call new API instead
                            //MainService.refreshSolarTimes(getApplicationContext(), cal);
                            // FIXME use stored offset
                            //handleActionAdd(Constants.SolarEvents.SUNRISE, 1);
                        }
                        break;
                    case SUNSET:
                        showNotification("Enjoy the sunset", evt);
                        if (isSunsetAlarmRepeating()) {
                            Calendar cal = new GregorianCalendar();
                            cal.add(Calendar.DAY_OF_WEEK, 1);
                            // FIXME call new API instead
                            //MainService.refreshSolarTimes(getApplicationContext(), cal);
                            //handleActionAdd(Constants.SolarEvents.SUNSET, 1);
                        }
                        break;
                    default:
                        L.w(TAG, "cannot display for unknown type");
                }
            } else if (ACTION_CLEAR.equals(action)) {
                handleActionClear((Constants.SolarEvents) intent.getSerializableExtra(EXTRA_ALARM_TYPE));
            }
        }
    }

    private void handleActionClear(Constants.SolarEvents alarmType) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        PendingIntent pendingIntent = getAlarmIntent(alarmType);
        alarmManager.cancel(pendingIntent);
        L.d(TAG, "Cleared type = " + alarmType);
        // TODO clear from storage
    }

    private void handleActionAdd(Constants.SolarEvents alarmType, int offset) {
        CalendarDataHelper dataHelper = CalendarDataHelper.getInstance();
        Calendar now = Calendar.getInstance();
        // FIXME need to check why roboelectric gets an exception when formatting with L
        //SimpleDateFormat sdf = new SimpleDateFormat("LLL dd hh:mm a z");
        SimpleDateFormat sdf = new SimpleDateFormat("MM dd hh:mm a z");
        try {
            L.d(TAG, "Now is " + sdf.format(now.getTime()));
            switch (alarmType) {
                case SUNRISE: {
                    Calendar cal = dataHelper.getCalFor(CalendarDataHelper.sunrise_key);
                    if (!scheduleAlarm(cal, offset, Constants.SolarEvents.SUNRISE)) {
                        L.e(TAG, "Failed to get sunrise cal object, no alarm set");
                    }
                    break;
                }
                case SUNSET: {
                    Calendar cal = dataHelper.getCalFor(CalendarDataHelper.sunset_key);
                    if (!scheduleAlarm(cal, offset, Constants.SolarEvents.SUNSET)) {
                        L.e(TAG, "Failed to get sunset cal object, no alarm set");
                    }
                    break;
                }
                default:
                    L.e(TAG, "Invalid alarm type!");
            }
        }catch (NullPointerException e) {
            L.e(TAG, "Whoops");
        }
    }

    /**
     *
     * @param msg
     * @throws IllegalArgumentException
     */
    private void showNotification(@NonNull final String msg,
                                  final Constants.SolarEvents evt) throws IllegalArgumentException{
        if (msg == null) {
            throw new IllegalArgumentException();
        }
        /* Allow notification to launch main activity */
        Intent mainIntent = new Intent(this, MainActivity.class);

        /* start a back stack to allow us to come back to home screen */
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(mainIntent);
        PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder notifBuilder = (NotificationCompat.Builder) new NotificationCompat.Builder(getApplicationContext())
                .setSmallIcon(R.drawable.ic_stat_action_alarm)
                .setContentTitle("Sol")
                .setContentText(msg)
                .setCategory(NotificationCompat.CATEGORY_ALARM)
                .setColor(Color.GRAY)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM))
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(Constants.NOTIF_TOKEN, notifBuilder.build());

        // set tomorrow's alarm immediately
        Calendar now = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd hh:mm a z");
        L.d(TAG, "Now is " + sdf.format(now.getTime()));
    }

    /**
     *
     * @param cal
     * @param offset in minutes
     * @param evt
     * @return
     * @throws IllegalArgumentException
     */
    private boolean scheduleAlarm(@NonNull Calendar cal, int offset,
                                  Constants.SolarEvents evt) throws IllegalArgumentException{
        if (cal == null) {
            throw new IllegalArgumentException();
        }
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        PendingIntent pendingIntent = getAlarmIntent(evt);
        if (pendingIntent != null) {
            // offset is subtracted to ensure alarm fires in advance
            cal.add(Calendar.MINUTE, -1*offset);
            L.d(TAG, "Set alarm for " + getPrettyTime(cal));
            // We need to ensure alarm fires even if device not awake
            alarmManager.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pendingIntent);
        }
        return true;
    }

    private String getPrettyTime(final Calendar cal) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd hh:mm a z");
        return simpleDateFormat.format(cal.getTime());
    }

    private boolean isSunriseAlarmRepeating() {
        return true;
    }

    private boolean isSunsetAlarmRepeating() {
        return true;
    }

    private final PendingIntent getAlarmIntent(Constants.SolarEvents evt) {
        PendingIntent pendingIntent = null;
        Intent myIntent = new Intent(ACTION_SHOW);
        myIntent.putExtra(EXTRA_ALARM_TYPE, evt);
        switch (evt) {
            case SUNRISE:
                pendingIntent = PendingIntent.getService(getApplicationContext(),
                        Constants.SUNRISE_ALARM_TOKEN, myIntent, PendingIntent.FLAG_CANCEL_CURRENT);
                break;
            case SUNSET:
                pendingIntent = PendingIntent.getService(getApplicationContext(),
                        Constants.SUNSET_ALARM_TOKEN, myIntent, PendingIntent.FLAG_CANCEL_CURRENT);
                break;
            default:
                L.w(TAG, "Cannot set alarm of unknown type");
        }
        return pendingIntent;
    }
}
