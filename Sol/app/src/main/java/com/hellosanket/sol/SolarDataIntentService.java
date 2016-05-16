package com.hellosanket.sol;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;

import com.luckycatlabs.sunrisesunset.SunriseSunsetCalculator;
import com.luckycatlabs.sunrisesunset.dto.MyLocation;

import java.util.Calendar;

/**
 *
 */
public class SolarDataIntentService extends IntentService {
    private static final String ACTION_COMPUTE = "com.hellosanket.sol.action.FOO";
    private SunriseSunsetCalculator sunsetCalculator;
    private static final String TAG = "SolarDataIntentService";

    public SolarDataIntentService() {
        super("SolarDataIntentService");
    }

    /**
     * Starts this service to perform action
     *
     * @see IntentService
     */

    public static void startComputeService(Context context) {
        Intent intent = new Intent(context, SolarDataIntentService.class);
        intent.setAction(ACTION_COMPUTE);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_COMPUTE.equals(action)) {
                handleActionCompute();
            }
        }
    }

    /**
     * Handle action Compute in the provided background thread with the provided
     * parameters.
     */
    private void handleActionCompute() {

        /*MyLocation location = new MyLocation("", "");
        SunriseSunsetCalculator calculator = new SunriseSunsetCalculator(location, "America/Los_Angeles");

        L.d(TAG, "Sunrise at " + calculator.getOfficialSunriseForDate(Calendar.getInstance()));
        L.d(TAG, "Sunset at " + calculator.getOfficialSunsetForDate(Calendar.getInstance()));*/
        L.d(TAG, "computing sunriseset");
    }

}
