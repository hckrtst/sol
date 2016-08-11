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
import android.support.v7.app.NotificationCompat;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 */
public class AlarmIntentService extends IntentService {
    private static final String ACTION_ADD = "com.hellosanket.sol.alarmsv.ADD";
    private static final String ACTION_CLEAR = "com.hellosanket.sol.alarmsvc.CLEAR";
    private static final String ACTION_SHOW = "com.hellosanket.sol.alarmsvc.SHOW";
    private static final String EXTRA_OFFSET = "com.hellosanket.sol.extra.OFFSET";
    private static final String EXTRA_ALARM_TYPE = "com.hellosanket.sol.extra.ALARM_TYPE";
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
                                      int alarmType,
                                      int offset) {
        Intent intent = new Intent(context, AlarmIntentService.class);
        intent.setAction(ACTION_ADD);
        intent.putExtra(EXTRA_OFFSET, offset);
        intent.putExtra(EXTRA_ALARM_TYPE, alarmType);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_ADD.equals(action)) {
                int offset = intent.getIntExtra(EXTRA_OFFSET, -1);
                int alarmType = intent.getIntExtra(EXTRA_ALARM_TYPE, -1);
                handleActionAdd(alarmType, offset);
            } else if (ACTION_SHOW.equals(action)) {
                L.d(TAG, "yay alarm");

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
                        .setContentText("Sunrise soon!")
                        .setCategory(NotificationCompat.CATEGORY_ALARM)
                        .setColor(Color.GRAY)
                        .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM))
                        .setContentIntent(pendingIntent);

                NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.notify(1234, notifBuilder.build());

                // set tomorrow's alarm immediately
                /*
                Date date = new Date();
                Calendar calendar = new GregorianCalendar();
                calendar.setTime(date);
                calendar.add(Calendar.DAY_OF_WEEK, 1);
                */
                //L.d(TAG, "next alarm on " + calendar.getTime());
            }
        }
    }

    private void handleActionAdd(int alarmType, int offset) {
        CalendarDataHelper dataHelper = CalendarDataHelper.getInstance();
        Calendar now = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("LLL dd hh:mm a z");
        L.d(TAG, "Now is " + sdf.format(now.getTime()));
        switch (alarmType) {
            case Constants.ALARM_TYPE_SUNRISE: {
                Calendar cal = dataHelper.getCalFor("sunrise");
                if (cal != null) {
                    L.d(TAG, "Sunrise is at " + sdf.format(cal.getTime()));

                    // subtract minutes
                    cal.add(Calendar.MINUTE, -1*offset);

                    // TODO refactor into common func
                    AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                    Intent myIntent = new Intent(ACTION_SHOW);
                    PendingIntent pendingIntent = PendingIntent.getService(getApplicationContext(),
                            Constants.ALARM_TOKEN+2, myIntent, PendingIntent.FLAG_CANCEL_CURRENT);

                    // We need to ensure alarm fires even if device not awake
                    alarmManager.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pendingIntent);

                    L.d(TAG, "Set sunrise reminder for " + sdf.format(cal.getTime()));

                } else {
                    L.e(TAG, "Failed to get sunrise cal object, no alarm set");
                }

                break;
            }
            case Constants.ALARM_TYPE_SUNSET: {
                Calendar cal = dataHelper.getCalFor("sunset");
                if (cal != null) {
                    L.d(TAG, "Sunset at " + sdf.format(cal.getTime()));

                    // subtract minutes
                    cal.add(Calendar.MINUTE, -1*offset);

                    // TODO refactor into common func
                    AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                    Intent myIntent = new Intent(ACTION_SHOW);
                    PendingIntent pendingIntent = PendingIntent.getService(getApplicationContext(),
                            Constants.ALARM_TOKEN, myIntent, PendingIntent.FLAG_CANCEL_CURRENT);

                    // We need to ensure alarm fires even if device not awake
                    alarmManager.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pendingIntent);

                    L.d(TAG, "Set sunset reminder for " + sdf.format(cal.getTime()));


                } else {
                    L.e(TAG, "Failed to get sunset cal object, no alarm set");
                }
                break;
            }
            default:
                L.e(TAG, "Invalid alarm type!");

        }

    }
}
