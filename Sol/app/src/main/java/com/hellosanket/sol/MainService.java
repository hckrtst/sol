package com.hellosanket.sol;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.HandlerThread;
import android.os.IBinder;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

public class MainService extends Service {
    private final static String TAG = "MainService";
    private GClient mGClient;
    public final static String ACTION_GET_SOL_TIMES = "sol.mainservice.get.solar.times";

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
            if (ACTION_GET_SOL_TIMES.equals(intent.getAction())) {
                // fetch the solar times based on location
            }
        }
        return Service.START_NOT_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mGClient.build();
    }

    /** Inner Classes **/
    // TODO: can this be a static class?
    private class GClient implements GoogleApiClient.OnConnectionFailedListener,
            GoogleApiClient.ConnectionCallbacks {
        private final static String TAG = "GClient";
        // Provides the entry point to Google Play services.
        private GoogleApiClient mGoogleApiClient;
        private boolean mConnected = false;

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
            mConnected = true;
        }

        @Override
        public void onConnectionSuspended(int i) {
            L.d(TAG, "google api service suspended");
            if (mGoogleApiClient != null) mGoogleApiClient.connect();
        }

        @Override
        public void onConnectionFailed(ConnectionResult connectionResult) {
            L.d(TAG, "Failed to connect to google api service");
            mConnected = false;
        }

        public boolean isConnected() {
            return mConnected;
        }
    }
    /*******************/
}
