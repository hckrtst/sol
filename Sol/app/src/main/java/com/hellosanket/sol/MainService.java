package com.hellosanket.sol;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;


import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class MainService extends Service {
    private final static String TAG = "MainService";
    private GClient mGClient;
    public final static String ACTION_GET_SOLAR_TIMES = "sol.mainservice.get.solar.times";

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
        L.d(TAG, "onStartCommand");
        if (intent != null) {
            L.d(TAG, "Got intent = " + intent.getAction());
            if (ACTION_GET_SOLAR_TIMES.equals(intent.getAction())) {
                // fetch the solar times based on location

                // TEST - will do the calculation in an intentservice
                /*
                MyLocation location = new MyLocation("", "");
                SunriseSunsetCalculator calculator = new SunriseSunsetCalculator(location, "America/Los_Angeles");

                L.d(TAG, "Sunrise at " + calculator.getOfficialSunriseForDate(Calendar.getInstance()));
                L.d(TAG, "Sunset at " + calculator.getOfficialSunsetForDate(Calendar.getInstance()));
                */
                if (mGClient.getLocation() != null) {
                    SolarDataIntentService.startComputeService(getApplicationContext(),
                            mGClient.getLocation());
                } else {
                    L.w(TAG, "no location yet");
                }


            }
        }
        return Service.START_NOT_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mGClient.build();
    }

    /** Public methods **/
    public static void getSolarTimes(Context context) {
        Intent intent = new Intent(context, MainService.class);
        intent.setAction(MainService.ACTION_GET_SOLAR_TIMES);
        context.startService(intent);
    }

    /** Inner Classes **/
    private class GClient implements GoogleApiClient.OnConnectionFailedListener,
            GoogleApiClient.ConnectionCallbacks {
        private final static String TAG = "GClient";
        // Provides the entry point to Google Play services.
        private GoogleApiClient mGoogleApiClient;
        private Location mLocation;

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
            if ((ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) /*||
                (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)*/) {
                L.w(TAG, "Bailed due to no permission");
                return;
            }
            mLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if (mLocation != null ) L.d(TAG, "Location: " + mLocation.toString());
            MainService.getSolarTimes(getApplicationContext());

        }

        @Override
        public void onConnectionSuspended(int i) {
            L.d(TAG, "google api service suspended");
            if (mGoogleApiClient != null) mGoogleApiClient.connect();
        }

        @Override
        public void onConnectionFailed(ConnectionResult connectionResult) {
            L.d(TAG, "Failed to connect to google api service");
        }

        public boolean isConnected() {
            return mGoogleApiClient.isConnected();
        }

        public synchronized Location getLocation() { return mLocation; }
    }
    /*******************/
}
