package com.hellosanket.sol;

import android.Manifest;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class MainService extends Service {
    private final static String TAG = "MainService";
    private GClient mGClient;
    protected final static String ACTION_GET_SUNRISE_FOR_REMINDER = "sol.mainservice.sunrise_for_reminder";
    protected final static String ACTION_GET_SUNSET_FOR_REMINDER = "sol.mainservice.sunset_for_reminder";
    protected final static String ACTION_GET_SUNRISE_FOR_NOTIF = "sol.mainservice.sunrise_for_notif";
    protected final static String ACTION_GET_SUNSET_FOR_NOTIF = "sol.mainservice.sunset_for_notif";
    public final static String ACTION_GET_SOLAR_TIMES = "sol.mainservice.get_solar_times";
    public final static String ACTION_LOC_PERM_GRANTED = "sol.mainservice.loc_perm_granted";
    public final static String ACTION_GET_SOLAR_TIMES_EXTRA_CAL = "sol.mainservice.get_solar_times.cal";
    private SolarDataReceiver mSolarDataReceiver;

    /*** private methods ***/

    /***********************/

    /*** public methods ***/
    public MainService() {
        mGClient = new GClient();

    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (mSolarDataReceiver == null) {
            mSolarDataReceiver = new SolarDataReceiver();
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(SolarDataIntentService.RESULT_SUNRISE);
            intentFilter.addAction(SolarDataIntentService.RESULT_SUNSET);
            intentFilter.addAction(SolarDataIntentService.RESULT_SUNSET_FOR_REMINDER);
            intentFilter.addAction(SolarDataIntentService.RESULT_SUNRISE_FOR_REMINDER);
            LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(mSolarDataReceiver, intentFilter);
        }
        if (intent != null) {
            L.d(TAG, "Got intent = " + intent.getAction());
            if (ACTION_GET_SOLAR_TIMES.equals(intent.getAction())) {

                // fetch the solar times based on location and time
                if (mGClient.getLocation() != null) {
                    Bundle data = intent.getExtras();
                    Calendar calendar = (Calendar) data.get(MainService.ACTION_GET_SOLAR_TIMES_EXTRA_CAL);
                    SolarDataIntentService.startComputeService(getApplicationContext(),
                            mGClient.getLocation(), calendar);
                } else {
                    L.w(TAG, "no location yet");
                }
            } else if (ACTION_LOC_PERM_GRANTED.equals(intent.getAction())) {
                mGClient.build();
            } else if (ACTION_GET_SUNRISE_FOR_REMINDER.equals(intent.getAction())) {
                L.d(TAG, "sunrise for rem");
                handleSunriseForReminder();
            } else if (ACTION_GET_SUNSET_FOR_REMINDER.equals(intent.getAction())) {
                L.d(TAG, "sunset for rem");
                handleSunsetForReminder();
            } else if (ACTION_GET_SUNRISE_FOR_NOTIF.equals(intent.getAction())) {
                L.d(TAG, "sunrise for notif");
                handleSunriseForNotif();
            } else if (ACTION_GET_SUNSET_FOR_NOTIF.equals(intent.getAction())) {
                L.d(TAG, "sunset for notif");
                handleSunsetForNotif();
            }
        }
        return Service.START_NOT_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();

    }

    /** static public methods **/
    protected static void refreshSolarTimes(Context context, Calendar calendar) {
        if (calendar == null) {
            L.d(TAG, "refreshSolarTimes: Calendar was null, send current");
            calendar = new GregorianCalendar();
            // TODO better to enforce this and throw exception?
        }
        Intent intent = new Intent(context, MainService.class);
        intent.setAction(MainService.ACTION_GET_SOLAR_TIMES);
        intent.putExtra(ACTION_GET_SOLAR_TIMES_EXTRA_CAL, calendar);
        context.startService(intent);

    }

    protected static void addAlarmFromUi(Context context, Constants.SolarEvents event) {
        Intent intent = new Intent(context, MainService.class);

        switch (event) {
            case SUNRISE:
                intent.setAction(MainService.ACTION_GET_SUNRISE_FOR_REMINDER);
                break;
            case SUNSET:
                intent.setAction(MainService.ACTION_GET_SUNSET_FOR_REMINDER);
                break;
        }

        context.startService(intent);
    }

    protected static void addAlarmFromNotification(Context context, Constants.SolarEvents event) {
        Intent intent = new Intent(context, MainService.class);

        switch (event) {
            case SUNRISE:
                intent.setAction(MainService.ACTION_GET_SUNRISE_FOR_NOTIF);
                break;
            case SUNSET:
                intent.setAction(MainService.ACTION_GET_SUNSET_FOR_NOTIF);
                break;
        }

        context.startService(intent);
    }


    protected void handleSunriseForReminder() {
        Calendar cal = new GregorianCalendar();
        SolarDataIntentService.startComputeServiceForReminder(getApplicationContext(),
                mGClient.getLocation(), cal, Constants.SolarEvents.SUNRISE);
    }

    protected void handleSunsetForReminder() {
        Calendar cal = new GregorianCalendar();
        SolarDataIntentService.startComputeServiceForReminder(getApplicationContext(),
                mGClient.getLocation(), cal, Constants.SolarEvents.SUNSET);
    }

    protected void handleSunriseForNotif() {
        Calendar cal = new GregorianCalendar();

        SolarDataIntentService.startComputeServiceForNotif(getApplicationContext(),
                mGClient.getLocation(), cal, Constants.SolarEvents.SUNRISE);
    }

    protected void handleSunsetForNotif() {
        Calendar cal = new GregorianCalendar();
        SolarDataIntentService.startComputeServiceForNotif(getApplicationContext(),
                mGClient.getLocation(), cal, Constants.SolarEvents.SUNSET);
    }

    public static void init(Context context) {
        Intent intent = new Intent(context, MainService.class);
        intent.setAction(MainService.ACTION_LOC_PERM_GRANTED);
        context.startService(intent);

        L.d(TAG, "Google play Availability = " +
                GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(context));
    }

    /** Inner Classes **/
    private class GClient implements GoogleApiClient.OnConnectionFailedListener,
            GoogleApiClient.ConnectionCallbacks {
        private final static String TAG = "GClient";
        // Provides the entry point to Google Play services.
        private GoogleApiClient mGoogleApiClient;

        public GClient() {
        }
        public synchronized void build() {
            if (mGoogleApiClient != null) return;
            mGoogleApiClient = new GoogleApiClient.Builder(getApplicationContext())
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
            mGoogleApiClient.connect();

        }

        @Override
        public void onConnected(Bundle bundle) {
            L.d(TAG, "Connected to google api service");
            MainService.refreshSolarTimes(getApplicationContext(), new GregorianCalendar());

        }

        @Override
        public void onConnectionSuspended(int i) {
            L.w(TAG, "google api service suspended");
            if (mGoogleApiClient != null) mGoogleApiClient.connect();
        }

        @Override
        public void onConnectionFailed(ConnectionResult connectionResult) {
            L.e(TAG, "Failed to connect to google api service");
        }

        public boolean isConnected() {
            return mGoogleApiClient.isConnected();
        }

        public synchronized Location getLocation() {
            if ((ActivityCompat.checkSelfPermission(getApplicationContext(),
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {
                L.w(TAG, "Bailed due to no permission");
                return null;
            }
            Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if (location == null ) L.e(TAG, "Location is null");
            return location;
        }
    }

    private class SolarDataReceiver extends BroadcastReceiver {
        private final static String TAG = "SolarDataRecv";
        SolarDataReceiver() {
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null) {
                Bundle bundle = intent.getExtras();
                Calendar calendar = (Calendar) bundle.get("calendar");
                // TODO null check?
                String time = getPrettyTime(calendar);

                if (intent.getAction().equals(SolarDataIntentService.RESULT_SUNRISE)) {
                    L.d(TAG, "RESULT_SUNRISE: got sunrise time = " + time);
                    Calendar now = new GregorianCalendar();
                    L.d(TAG, "Now is = " + getPrettyTime(now));
                    if (now.compareTo(calendar) > 0) {
                        now.add(Calendar.DAY_OF_WEEK, 1);
                        L.d(TAG, "Now is updated to " + getPrettyTime(now));
                        //refreshSolarTimes(getApplicationContext(), now);
                        SolarDataIntentService.startComputeServiceByType(getApplicationContext(),
                                mGClient.getLocation(), now, Constants.SolarEvents.SUNRISE);
                    } else {
                        DataWrapper.saveString(getApplicationContext(),
                                Constants.SOL_DB, Constants.SUNRISE_TIME_TEXT_KEY, time);
                    }
                } else if (intent.getAction().equals(SolarDataIntentService.RESULT_SUNSET)) {
                    L.d(TAG, "RESULT_SUNSET: got sunset time = " + time);
                    Calendar now = new GregorianCalendar();
                    if (now.compareTo(calendar) > 0) {
                        now.add(Calendar.DAY_OF_WEEK, 1);
                        //refreshSolarTimes(getApplicationContext(), now);
                        SolarDataIntentService.startComputeServiceByType(getApplicationContext(),
                                mGClient.getLocation(), now, Constants.SolarEvents.SUNSET);
                    } else {
                        DataWrapper.saveString(getApplicationContext(),
                                Constants.SOL_DB, Constants.SUNSET_TIME_TEXT_KEY, time);
                    }
                } else if (intent.getAction().equals(SolarDataIntentService.RESULT_SUNRISE_FOR_REMINDER)) {
                    L.d(TAG, "RESULT_SUNRISE_FOR_REMINDER: got sunrise time = " + time);
                    Calendar now = new GregorianCalendar();
                    L.d(TAG, "Now is = " + getPrettyTime(now));
                    if (now.compareTo(calendar) > 0) {
                        now.add(Calendar.DAY_OF_WEEK, 1);
                        L.d(TAG, "Now is updated to " + getPrettyTime(now));
                        SolarDataIntentService.startComputeServiceForReminder(getApplicationContext(),
                                mGClient.getLocation(), now, Constants.SolarEvents.SUNRISE);
                    } else {
                        // Apply offset
                        int offset = DataWrapper.readInt(getApplicationContext(), Constants.SOL_DB,
                                Constants.SUNRISE_ALARM_OFFSET_KEY, -1);
                        if (offset > 0) calendar.add(Calendar.MINUTE, -1 * offset);
                        AlarmIntentService.startActionAdd(getApplicationContext(), Constants.SolarEvents.SUNRISE, calendar);
                    }

                } else if (intent.getAction().equals(SolarDataIntentService.RESULT_SUNSET_FOR_REMINDER)) {
                    L.d(TAG, "RESULT_SUNSET_FOR_REMINDER: got sunset time = " + time);
                    Calendar now = new GregorianCalendar();
                    L.d(TAG, "Now is = " + getPrettyTime(now));
                    if (now.compareTo(calendar) > 0) {
                        now.add(Calendar.DAY_OF_WEEK, 1);
                        L.d(TAG, "Now is updated to " + getPrettyTime(now));
                        SolarDataIntentService.startComputeServiceForReminder(getApplicationContext(),
                                mGClient.getLocation(), now, Constants.SolarEvents.SUNSET);
                    } else {
                        int offset = DataWrapper.readInt(getApplicationContext(), Constants.SOL_DB,
                                Constants.SUNSET_ALARM_OFFSET_KEY, -1);
                        if (offset > 0) calendar.add(Calendar.MINUTE, -1 * offset);
                        AlarmIntentService.startActionAdd(getApplicationContext(), Constants.SolarEvents.SUNSET, calendar);
                    }

                } else if (intent.getAction().equals(SolarDataIntentService.RESULT_SUNRISE_FOR_NOTIF)) {
                    L.d(TAG, "RESULT_SUNRISE_FOR_NOTIF: got sunrise time = " + time);
                    Calendar now = new GregorianCalendar();
                    L.d(TAG, "Now is = " + getPrettyTime(now));

                    // Apply offset
                    int offset = DataWrapper.readInt(getApplicationContext(), Constants.SOL_DB,
                               Constants.SUNRISE_ALARM_OFFSET_KEY, -1);
                    // if alarm is still active then repeat
                    if (offset >= 0) {
                        // always get for next day
                        now.add(Calendar.DAY_OF_WEEK, 1);
                        now.add(Calendar.MINUTE, -1 * offset);
                        AlarmIntentService.startActionAdd(getApplicationContext(), Constants.SolarEvents.SUNRISE, now);
                    }
                } else if (intent.getAction().equals(SolarDataIntentService.RESULT_SUNSET_FOR_NOTIF)) {
                    L.d(TAG, "RESULT_SUNSET_FOR_NOTIF: got sunset time = " + time);
                    Calendar now = new GregorianCalendar();
                    L.d(TAG, "Now is = " + getPrettyTime(now));

                    // Apply offset
                    int offset = DataWrapper.readInt(getApplicationContext(), Constants.SOL_DB,
                            Constants.SUNSET_ALARM_OFFSET_KEY, -1);
                    // if alarm is still active then repeat
                    if (offset >= 0) {
                        // always get for next day
                        now.add(Calendar.DAY_OF_WEEK, 1);
                        now.add(Calendar.MINUTE, -1 * offset);
                        AlarmIntentService.startActionAdd(getApplicationContext(), Constants.SolarEvents.SUNSET, now);
                    }
                }
            }
        }

        private String getPrettyTime(Calendar cal) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("LLL dd hh:mm a z");
            return simpleDateFormat.format(cal.getTime());
        }
    }


    /*******************/
}
