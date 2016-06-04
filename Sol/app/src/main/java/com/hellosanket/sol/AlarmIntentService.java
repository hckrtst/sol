package com.hellosanket.sol;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 */
public class AlarmIntentService extends IntentService {
    private static final String ACTION_ADD = "com.hellosanket.sol.action.ADD";
    private static final String ACTION_CLEAR = "com.hellosanket.sol.action.CLEAR";
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
                    L.d(TAG, "Got calendar for sunrise " + cal);
                } else {
                    L.e(TAG, "Failed to get sunrise cal object, no alarm set");
                }

                break;
            }
            case Constants.ALARM_TYPE_SUNSET: {
                Calendar cal = dataHelper.getCalFor("sunset");
                if (cal != null) {
                    L.d(TAG, "Got calendar for sunset " + cal);
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
