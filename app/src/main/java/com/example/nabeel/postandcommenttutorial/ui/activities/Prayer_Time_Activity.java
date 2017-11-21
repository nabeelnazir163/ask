package com.example.nabeel.postandcommenttutorial.ui.activities;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import android.Manifest;
import com.example.nabeel.postandcommenttutorial.utils.GPSTracker;
import com.example.nabeel.postandcommenttutorial.utils.PrayTime;
import com.example.nabeel.postandcommenttutorial.R;

public class Prayer_Time_Activity extends AppCompatActivity {
    private TextView txtPrayerNames;
    private TextView txtPrayerTimes;
    GPSTracker gps;
    double latitude;
    double longitude;
//    private Button mGetTime;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prayer__time_);

//        mGetTime = (Button) findViewById(R.id.gettimebtn);

        if(getSupportActionBar() != null){

            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);

        }

        txtPrayerNames = (TextView) findViewById(R.id.txtPrayerNames);
        txtPrayerTimes = (TextView) findViewById(R.id.txtPrayerTimes);

        gps = new GPSTracker(Prayer_Time_Activity.this);

        if(gps.canGetLocation()) {
            latitude = gps.getLatitude();
            longitude = gps.getLongitude();
//            init();

        } else {
            gps.showSettingsAlert();
        }

    }
    
    @Override
    protected void onStop() {
        super.onStop();
        txtPrayerNames.setText("");
        txtPrayerTimes.setText("");
    }

    public void init(){

        if(latitude != 0 && longitude != 0 ){

            double m_latitude = latitude;
            double m_longitude = longitude;
            double timezone = (Calendar.getInstance().getTimeZone()
                    .getOffset(Calendar.getInstance().getTimeInMillis()))
                    / (1000 * 60 * 60);
            PrayTime prayers = new PrayTime();

            prayers.setTimeFormat(prayers.Time12);
            prayers.setCalcMethod(prayers.Makkah);
            prayers.setAsrJuristic(prayers.Shafii);
            prayers.setAdjustHighLats(prayers.AngleBased);
            int[] offsets = { 0, 0, 0, 0, 0, 0, 0 }; // {Fajr,Sunrise,Dhuhr,Asr,Sunset,Maghrib,Isha}
            prayers.tune(offsets);

            Date now = new Date();
            Calendar cal = Calendar.getInstance();
            cal.setTime(now);

            ArrayList prayerTimes = prayers.getPrayerTimes(cal, m_latitude,
                    m_longitude, timezone);
            ArrayList prayerNames = prayers.getTimeNames();

            for (int i = 0; i < prayerTimes.size(); i++) {
//            txtPrayerTimes.append("\n" + prayerNames.get(i) + " - "
//                    + prayerTimes.get(i));

                txtPrayerNames.append("\n" +prayerNames.get(i));
                txtPrayerTimes.append("\n" +prayerTimes.get(i));
            }

        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        init();
    }



    /*public void getTime(View v) {
        Toast.makeText(
                getApplicationContext(),
                "Your Location is -\nLat: " + latitude + "\nLong: "
                        + longitude, Toast.LENGTH_LONG).show();

        double m_latitude = latitude;
        double m_longitude = longitude;
        double timezone = (Calendar.getInstance().getTimeZone()
                .getOffset(Calendar.getInstance().getTimeInMillis()))
                / (1000 * 60 * 60);
        PrayTime prayers = new PrayTime();

        prayers.setTimeFormat(prayers.Time12);
        prayers.setCalcMethod(prayers.Makkah);
        prayers.setAsrJuristic(prayers.Shafii);
        prayers.setAdjustHighLats(prayers.AngleBased);
        int[] offsets = { 0, 0, 0, 0, 0, 0, 0 }; // {Fajr,Sunrise,Dhuhr,Asr,Sunset,Maghrib,Isha}
        prayers.tune(offsets);

        Date now = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(now);

        ArrayList prayerTimes = prayers.getPrayerTimes(cal, m_latitude,
                m_longitude, timezone);
        ArrayList prayerNames = prayers.getTimeNames();

        for (int i = 0; i < prayerTimes.size(); i++) {
//            txtPrayerTimes.append("\n" + prayerNames.get(i) + " - "
//                    + prayerTimes.get(i));

            txtPrayerNames.append("\n" +prayerNames.get(i));
            txtPrayerTimes.append("\n" +prayerTimes.get(i));
        }

        mGetTime.setVisibility(View.GONE);
    }*/
}