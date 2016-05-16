package com.hellosanket.sol;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener{
    private static final String TAG = "MainActivity";
    private boolean mServiceWarmedUp = false;

    /*---------------- LOCAL METHODS ----------------------------------*/
    private void getSolarTimes() {
        Intent intent = new Intent(MainActivity.this, MainService.class);
        intent.setAction(MainService.ACTION_GET_SOLAR_TIMES);
        startService(intent);

    }
    private boolean tryPermission() {
        if ((ActivityCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    Constants.PERMISSIONS_REQUEST_LOCATION);
            return false;
        }
        return true;
    }
    private void restoreOnCreate(Bundle savedInstanceState) {
        mServiceWarmedUp = savedInstanceState.getBoolean("mServiceWarmedUp");
    }


    /*-------------------------------- OVERRIDDEN METHODS ------------------------------------*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setBackgroundColor((getResources().getColor(R.color.main_toolbar)));
        setSupportActionBar(toolbar);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        if (savedInstanceState == null) {
            // start service in advance
            Intent intent = new Intent(MainActivity.this, MainService.class);
            startService(intent);
        } else {
            restoreOnCreate(savedInstanceState);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!mServiceWarmedUp){
            getSolarTimes();
            mServiceWarmedUp = true;
        }

        // TODO refresh UI
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        L.d(TAG, "onRequestPermissionsResult");

        switch (requestCode) {
            case Constants.PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    L.d(TAG,"Granted Permission");
                    getSolarTimes();
                } else {
                    Toast.makeText(this, "No Location, no reminder :'(", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putBoolean("mServiceWarmedUp", mServiceWarmedUp);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        // TODO handle
    }
}
