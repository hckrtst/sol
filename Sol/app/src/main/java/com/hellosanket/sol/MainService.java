package com.hellosanket.sol;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class MainService extends Service {
    private final static String TAG = "MainService";
    private GClient mGClient;
    public final static String ACTION_GET_SOLAR_TIMES = "sol.mainservice.get_solar_times";
    public final static String ACTION_LOC_PERM_GRANTED = "sol.mainservice.loc_perm_granted";
    public final static String ACTION_SHOW_ALARM = "com.hellosanket.sol.show_alarm";
    public final static String ACTION_SET_ALARM = "sol.mainservice.set_alarm";

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
        if (intent != null) {
            L.d(TAG, "Got intent = " + intent.getAction());
            if (ACTION_GET_SOLAR_TIMES.equals(intent.getAction())) {

                // fetch the solar times based on location
                if (mGClient.getLocation() != null) {
                    SolarDataIntentService.startComputeService(getApplicationContext(),
                            mGClient.getLocation());
                } else {
                    L.w(TAG, "no location yet");
                }
            } else if (ACTION_LOC_PERM_GRANTED.equals(intent.getAction())) {
                mGClient.build();
            } else if (ACTION_SET_ALARM.equals(intent.getAction())) {
                Intent myintent = new Intent(MainService.ACTION_SHOW_ALARM);
                PendingIntent pendingIntent = PendingIntent.getService(getApplicationContext(),
                        Constants.ALARM_TOKEN, myintent, PendingIntent.FLAG_CANCEL_CURRENT);

                // TEST
                AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                Calendar rightNow = new GregorianCalendar();

                rightNow.add(Calendar.SECOND, 40);

                // We need to ensure alarm fires even if device not awake
                alarmManager.set(AlarmManager.RTC_WAKEUP, rightNow.getTimeInMillis(), pendingIntent);

                L.d(TAG, "Set alarm for " + rightNow.getTime());

            } else if(ACTION_SHOW_ALARM.equals(intent.getAction())) {
                L.d(TAG, "Now showing: your alarm");
            }
        }
        return Service.START_NOT_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();

    }

    /** static public methods **/
    public static void getSolarTimes(Context context) {
        Intent intent = new Intent(context, MainService.class);
        intent.setAction(MainService.ACTION_GET_SOLAR_TIMES);
        context.startService(intent);
    }

    public static void init(Context context) {
        Intent intent = new Intent(context, MainService.class);
        intent.setAction(MainService.ACTION_LOC_PERM_GRANTED);
        context.startService(intent);
    }

    public static void setAlarm(Context context) {
        Intent myintent = new Intent(context, MainService.class);
        myintent.setAction(MainService.ACTION_SET_ALARM);
        context.startService(myintent);

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
            MainService.getSolarTimes(getApplicationContext());

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
            if ((ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) /*||
                (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)*/) {
                L.w(TAG, "Bailed due to no permission");
                return null;
            }
            Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if (location == null ) L.e(TAG, "Location is null");
            return location;
        }
    }
    /*******************/
}
