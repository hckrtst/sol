package com.hellosanket.sol;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 */
public class AlarmIntentService extends IntentService {
    private static final String ACTION_ADD = "com.hellosanket.sol.action.ADD";
    private static final String ACTION_CLEAR = "com.hellosanket.sol.action.CLEAR";
    private static final String EXTRA_OFFSET = "com.hellosanket.sol.extra.OFFSET";

    public AlarmIntentService() {
        super("AlarmIntentService");
    }

    /**
     * Starts this service to add alarm
     *
     * @see IntentService
     */
    public static void startActionAdd(final Context context,
                                      int offset) {
        Intent intent = new Intent(context, AlarmIntentService.class);
        intent.setAction(ACTION_ADD);
        intent.putExtra(EXTRA_OFFSET, offset);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_ADD.equals(action)) {
                final String param1 = intent.getStringExtra(EXTRA_OFFSET);
                handleActionAdd(param1);
            }
        }
    }

    private void handleActionAdd(String param1) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
