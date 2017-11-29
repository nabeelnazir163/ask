package com.example.nabeel.postandcommenttutorial.ui.activities;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import android.Manifest;
import com.example.nabeel.postandcommenttutorial.utils.PrayTime;
import com.example.nabeel.postandcommenttutorial.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderApi;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;


public class Prayer_Time_Activity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {

    /**
     * Stuff for LOCATIONAPI
     */
    private static final int MY_PERMISSION_ACCESS_FINE_LOCATION = 0;
    private static final int MY_PERMISSION_ACCESS_COARSE_LOCATION = 0;
    private static final String TAG = MainActivity.class.getSimpleName();
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;

    boolean isGPSEnabled = false;
    private Location mLastLocation;
    private GoogleApiClient mGoogleApiClient;

    private boolean mRequestLocationUpdates = false;

    private LocationRequest mLocationRequest;

    private static int UPDATE_INTERVAL = 1000;
    private static int FATEST_INTERVAL = 1000;
    private static int DISPLACEMENT = 10;

    private Button btnShowLocation;
    //THAT's IT

    private TextView txtPrayerNames;
    private TextView txtPrayerTimes;
    private TextView partitionLine;
    double latitude;
    double longitude;
//    Button mGetTime;

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
        if (checkPlayServices()) {
            buildGoogleApiClient();
            createLocationRequest();
        }
        txtPrayerNames = (TextView) findViewById(R.id.txtPrayerNames);
        txtPrayerTimes = (TextView) findViewById(R.id.txtPrayerTimes);
        partitionLine = (TextView) findViewById(R.id.partitionline);

        displayLocation();

//        mGetTime.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                displayLocation();
//            }
//        });
    }
    
    @Override
    protected void onStop() {
        super.onStop();
        txtPrayerNames.setText("");
        txtPrayerTimes.setText("");
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkPlayServices();
        if (mGoogleApiClient.isConnected() && mRequestLocationUpdates) {
            startLocationUpdates();
        }
    }

    public void displayLocation() {
        LocationManager locationManager =(LocationManager)getSystemService(LOCATION_SERVICE);
        isGPSEnabled = locationManager.isProviderEnabled(locationManager.GPS_PROVIDER);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED && (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_DENIED)) {
            getFinePermission();
            getCoarsePermission();
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if (mLastLocation != null) {
                latitude = mLastLocation.getLatitude();
                longitude = mLastLocation.getLongitude();
                init();
            }
            else{
                if(isGPSEnabled == false)
                    showSettingsAlert();
                //Toast.makeText(this, "COULD NOT ACCESS LOCATION", Toast.LENGTH_SHORT).show();
            }
        }
    }
    public void showSettingsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

        alertDialog.setTitle("GPS is settings");

        alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?");

        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        });

        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        alertDialog.show();
    }
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();
    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FATEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setSmallestDisplacement(DISPLACEMENT);
    }

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this, PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Toast.makeText(getApplicationContext(), "This device is not supported", Toast.LENGTH_LONG)
                        .show();
                finish();
            }
            return false;
        }
        return true;
    }

    protected void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            getFinePermission();
            getCoarsePermission();
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);

        }
    }

    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
    }

    @Override
    public void onConnected(Bundle bundle) {
        displayLocation();

        if(mRequestLocationUpdates) {
            startLocationUpdates();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;

        Toast.makeText(getApplicationContext(), "Location changed!", Toast.LENGTH_SHORT).show();

        displayLocation();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.i(TAG, "Connection failed: " + connectionResult.getErrorCode());
    }

    public void getCoarsePermission(){
        //check if permission is granted
        if(ContextCompat.checkSelfPermission(Prayer_Time_Activity.this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED){

            //should we show description?
            if(ActivityCompat.shouldShowRequestPermissionRationale(Prayer_Time_Activity.this, Manifest.permission.ACCESS_COARSE_LOCATION)){
                /*
                DESCRIBE WHY THE PERMISSION IS REQUIRED
                 */
            }else{
                //NO EXPLANATION NEEDED

                ActivityCompat.requestPermissions(Prayer_Time_Activity.this,new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},MY_PERMISSION_ACCESS_COARSE_LOCATION);
            }
        }
    }
    public void getFinePermission(){
        //check if permission is granted
        if(ContextCompat.checkSelfPermission(Prayer_Time_Activity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                !=PackageManager.PERMISSION_GRANTED){

            //should we show description?
            if(ActivityCompat.shouldShowRequestPermissionRationale(Prayer_Time_Activity.this, Manifest.permission.ACCESS_FINE_LOCATION)){
                /*
                DESCRIBE WHY THE PERMISSION IS REQUIRED
                 */
            }else{
                //NO EXPLANATION NEEDED
                ActivityCompat.requestPermissions(Prayer_Time_Activity.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},MY_PERMISSION_ACCESS_FINE_LOCATION);
            }
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {

        if (requestCode == MY_PERMISSION_ACCESS_COARSE_LOCATION) {
            // If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
                // permission was granted, yay! Do the
                // contacts-related task you need to do.

            } else {

                // permission denied, boo! Disable the
                // functionality that depends on this permission.
            }
            return;
        }
        if (requestCode == MY_PERMISSION_ACCESS_FINE_LOCATION) {
            // If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
                // permission was granted, yay! Do the
                // contacts-related task you need to do.

            } else {

                // permission denied, boo! Disable the
                // functionality that depends on this permission.
            }
            return;
        }
        // other 'case' lines to check for other
        // permissions this app might request
    }

    public void init(){

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

                txtPrayerNames.append("\n" +prayerNames.get(i) + "\n" );
                partitionLine.append("\n" + "_________" + "\n");
                txtPrayerTimes.append("\n" +prayerTimes.get(i) + "\n" );

            }

        }

    @Override
    protected void onStart() {
        super.onStart();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
        displayLocation();
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