package com.hellosanket.sol;

import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Debug;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

public class MainActivity extends AppCompatActivity implements
        SharedPreferences.OnSharedPreferenceChangeListener,
        ReminderDialogFragment.ReminderDialogListener{
    private static final String TAG = "MainActivity";
    private TextView mSunriseTimeTextView, mSunsetTimeTextView;
    private ToggleButton mSunriseRemBtn, mSunsetRemBtn;


    /*---------------- LOCAL METHODS ----------------------------------*/
    private boolean tryPermission() {
        if ((ActivityCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    Constants.PERMISSIONS_REQUEST_LOCATION);
            return false;
        }
        return true;
    }
    private void restoreOnCreate(Bundle savedInstanceState) {
        // Add anything we need to restore here
    }

    private void showReminderDialog(String type) {
        android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
        ReminderDialogFragment dialogFragment = ReminderDialogFragment.newInstance(type);
        dialogFragment.show(fragmentManager, "fragment_dialog_reminder");
        L.d(TAG, "showing reminder for " + type);
    }

    private void handleClick(final ToggleButton button, int resId) {
        if (button.isChecked()) {
            showReminderDialog(getString(resId));
        } else {
            button.setChecked(false);
        }
    }


    /*-------------------------------- OVERRIDDEN METHODS ------------------------------------*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        L.d(TAG, "onCreate");
        setContentView(R.layout.activity_main);

        mSunriseTimeTextView = (TextView) findViewById(R.id.sunrise_time_textview);
        mSunsetTimeTextView = (TextView) findViewById(R.id.sunset_time_textview);
        mSunriseRemBtn = (ToggleButton) findViewById(R.id.sunrise_reminder_toggle_btn);
        mSunsetRemBtn = (ToggleButton) findViewById(R.id.sunset_reminder_toggle_btn);

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

        // register to be notified of shared prefs
        DataWrapper.registerListener(getApplicationContext(), Constants.SOL_DB, this);

        String sunrise = DataWrapper.readString(getApplicationContext(),
                Constants.SOL_DB, Constants.SUNRISE_TIME_TEXT_KEY, "unset");
        String sunset = DataWrapper.readString(getApplicationContext(),
                Constants.SOL_DB, Constants.SUNSET_TIME_TEXT_KEY, "unset");

        if (!sunrise.equals("unset")) {
            mSunriseTimeTextView.setText( getString(R.string.main_activity_sunrise_time_leader) +
                    " " + sunrise);
        }
        if (!sunset.equals("unset")) {
            mSunsetTimeTextView.setText( getString(R.string.main_activity_sunset_time_leader) +
                    " " + sunset);
        }

        mSunriseRemBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleClick(mSunriseRemBtn, R.string.fragment_dialog_sunrise_title_text);
            }
        });
        mSunsetRemBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleClick(mSunsetRemBtn, R.string.fragment_dialog_sunset_title_text);
            }
        });

        if (savedInstanceState == null) {
            // init service in advance
            MainService.init(getApplicationContext());
        } else {
            restoreOnCreate(savedInstanceState);
        }
        tryPermission();
    }

    @Override
    protected void onResume() {
        super.onResume();
        L.d(TAG, "onResume");
        if (tryPermission()) {
            MainService.init(getApplicationContext());
            MainService.getSolarTimes(getApplicationContext());
        }
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
                    MainService.init(getApplicationContext());
                } else {
                    Toast.makeText(this, "No Location, no reminder :'(", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // save anything we need
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(Constants.SUNRISE_TIME_TEXT_KEY)) {
            L.d(TAG, "Sunrise updated to " + sharedPreferences.getString(key, "<default>"));
            mSunriseTimeTextView.setText( getString(R.string.main_activity_sunrise_time_leader) + " " +
                    sharedPreferences.getString(key, "<default>"));
        } else if (key.equals(Constants.SUNSET_TIME_TEXT_KEY)) {
            mSunsetTimeTextView.setText(getString(R.string.main_activity_sunset_time_leader) + " " +
                    sharedPreferences.getString(key, "<default>"));
        }

    }

    @Override
    public void onReminderSet(final String type, final boolean enabled, int offset) {
        L.d(TAG, "type = " + type + ", enabled = " + enabled);
        if (type.equals(getString(R.string.fragment_dialog_sunrise_title_text))) {
            mSunriseRemBtn.setChecked(enabled);
            if (enabled) {
                AlarmIntentService.startActionAdd(getApplicationContext(),
                        Constants.ALARM_TYPE_SUNRISE,
                        offset);
            }
        } else {
            mSunsetRemBtn.setChecked(enabled);
            if (enabled) {
                AlarmIntentService.startActionAdd(getApplicationContext(),
                        Constants.ALARM_TYPE_SUNSET,
                        offset);
            }
        }

    }

}
