//package com.example.nabeel.postandcommenttutorial.utils;
//
//import android.Manifest;
//import android.app.Activity;
//import android.app.AlertDialog;
//import android.app.Service;
//import android.content.Context;
//import android.content.DialogInterface;
//import android.content.Intent;
//import android.content.pm.PackageManager;
//import android.location.Location;
//import android.location.LocationListener;
//import android.location.LocationManager;
//import android.os.Bundle;
//import android.os.IBinder;
//import android.provider.Settings;
//import android.support.v4.app.ActivityCompat;
//import android.support.v4.content.ContextCompat;
//import android.util.Log;
//import android.widget.Button;
//import android.widget.Toast;
//
//import com.example.nabeel.postandcommenttutorial.ui.activities.MainActivity;
//import com.google.android.gms.common.ConnectionResult;
//import com.google.android.gms.common.GooglePlayServicesUtil;
//import com.google.android.gms.common.api.GoogleApiClient;
//import com.google.android.gms.location.LocationRequest;
//import com.google.android.gms.location.LocationServices;
//
//public class GPSTracker extends Activity implements GoogleApiClient.ConnectionCallbacks,
//        GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {
//
//    private final Context context;
//    private static final int MY_PERMISSION_ACCESS_FINE_LOCATION = 0;
//    private static final int MY_PERMISSION_ACCESS_COARSE_LOCATION = 0;
//    private static final String TAG = MainActivity.class.getSimpleName();
//    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;
//    boolean isGPSEnabled = false;
//    private Location mLastLocation;
//    private GoogleApiClient mGoogleApiClient;
//    private boolean mRequestLocationUpdates = false;
//    private LocationRequest mLocationRequest;
//    private static int UPDATE_INTERVAL = 1000;
//    private static int FATEST_INTERVAL = 1000;
//    private static int DISPLACEMENT = 10;
//    double longtitude=0;
//    double latitude=0;
//
////
////    boolean isGPSEnabled = false;
////    boolean isNetworkEnabled = false;
////    boolean canGetLocation = false;
////    Location location;
////    double latitude;
////    double longitude;
////    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10;
////    private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1;
////    protected LocationManager locationManager;
//
//    public GPSTracker(Context context) {
//        this.context = context;
//        if (checkPlayServices()) {
//            buildGoogleApiClient();
//            createLocationRequest();
//        }
////        displayLocation();
//    }
//
//    private void displayLocation() {
//        LocationManager locationManager =(LocationManager)getSystemService(LOCATION_SERVICE);
//        isGPSEnabled = locationManager.isProviderEnabled(locationManager.GPS_PROVIDER);
//        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED && (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_DENIED)) {
//            getFinePermission();
//            getCoarsePermission();
//        }
//        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
//
//            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
//            if (mLastLocation != null) {
//                latitude = mLastLocation.getLatitude();
//                longtitude = mLastLocation.getLongitude();
//            } else {
//                if(!isGPSEnabled)
//                    showSettingsAlert();
//                Toast.makeText(context, "COULD NOT ACCESS LOCATION", Toast.LENGTH_SHORT).show();
//            }
//        }
//    }
//
//    public double getLongitude(){
//        displayLocation();
//        if(longtitude>0)
//            return longtitude;
//        else{
//            Toast.makeText(context, "LONGITUDE: "+longtitude, Toast.LENGTH_SHORT).show();
//            return longtitude;
//        }
//    }
//
//    public double getLatitude(){
//        displayLocation();
//        if(latitude>0)
//            return latitude;
//        else{
//            Toast.makeText(context, "LATITUDE: "+latitude, Toast.LENGTH_SHORT).show();
//            return latitude;
//        }
//    }
//
//    public void showSettingsAlert() {
//        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
//
//        alertDialog.setTitle("GPS is settings");
//
//        alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?");
//
//        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
//
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
//                startActivity(intent);
//            }
//        });
//
//        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                dialog.cancel();
//            }
//        });
//
//        alertDialog.show();
//    }
//
//
//    protected synchronized void buildGoogleApiClient() {
//        mGoogleApiClient = new GoogleApiClient.Builder(context)
//                .addConnectionCallbacks(this)
//                .addOnConnectionFailedListener(this)
//                .addApi(LocationServices.API).build();
//    }
//
//    protected void createLocationRequest() {
//        mLocationRequest = new LocationRequest();
//        mLocationRequest.setInterval(UPDATE_INTERVAL);
//        mLocationRequest.setFastestInterval(FATEST_INTERVAL);
//        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
//        mLocationRequest.setSmallestDisplacement(DISPLACEMENT);
//    }
//
//    private boolean checkPlayServices() {
//        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(context);
//        if (resultCode != ConnectionResult.SUCCESS) {
//            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
//                GooglePlayServicesUtil.getErrorDialog(resultCode, this, PLAY_SERVICES_RESOLUTION_REQUEST).show();
//            } else {
//                Toast.makeText(getApplicationContext(), "This device is not supported", Toast.LENGTH_LONG)
//                        .show();
////                finish();
//            }
//            return false;
//        }
//        return true;
//    }
//
//    protected void startLocationUpdates() {
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            getFinePermission();
//            getCoarsePermission();
//        }
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
//            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
//
//        }
//    }
//
//
//    @Override
//    public void onConnected(Bundle bundle) {
//        displayLocation();
//
//        if(mRequestLocationUpdates) {
//            startLocationUpdates();
//        }
//    }
//
//    @Override
//    public void onConnectionSuspended(int i) {
//        mGoogleApiClient.connect();
//    }
//
//    @Override
//    public void onLocationChanged(Location location) {
//        mLastLocation = location;
//
//        Toast.makeText(getApplicationContext(), "Location changed!", Toast.LENGTH_SHORT).show();
//
//        displayLocation();
//    }
//
//    @Override
//    public void onConnectionFailed(ConnectionResult connectionResult) {
//        Log.i(TAG, "Connection failed: " + connectionResult.getErrorCode());
//    }
//
//
//    public void getCoarsePermission(){
//        //check if permission is granted
//        if(ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
//                != PackageManager.PERMISSION_GRANTED){
//
//            //should we show description?
//            if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION)){
//                /*
//                DESCRIBE WHY THE PERMISSION IS REQUIRED
//                 */
//            }else{
//                //NO EXPLANATION NEEDED
//
//                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},MY_PERMISSION_ACCESS_COARSE_LOCATION);
//            }
//        }
//    }
//    public void getFinePermission(){
//        //check if permission is granted
//        if(ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
//                !=PackageManager.PERMISSION_GRANTED){
//
//            //should we show description?
//            if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)){
//                /*
//                DESCRIBE WHY THE PERMISSION IS REQUIRED
//                 */
//            }else{
//                //NO EXPLANATION NEEDED
//                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},MY_PERMISSION_ACCESS_FINE_LOCATION);
//            }
//        }
//    }
//    @Override
//    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
//
//        if (requestCode == MY_PERMISSION_ACCESS_COARSE_LOCATION) {
//            // If request is cancelled, the result arrays are empty.
//            if (grantResults.length > 0
//                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//
//                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
//                // permission was granted, yay! Do the
//                // contacts-related task you need to do.
//
//            } else {
//
//                // permission denied, boo! Disable the
//                // functionality that depends on this permission.
//            }
//            return;
//        }
//        if (requestCode == MY_PERMISSION_ACCESS_FINE_LOCATION) {
//            // If request is cancelled, the result arrays are empty.
//            if (grantResults.length > 0
//                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//
//                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
//                // permission was granted, yay! Do the
//                // contacts-related task you need to do.
//
//            } else {
//
//                // permission denied, boo! Disable the
//                // functionality that depends on this permission.
//            }
//            return;
//        }
//        // other 'case' lines to check for other
//        // permissions this app might request
//    }
//
//
//
//
////    public Location getLocation() {
////        try {
////            locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);
////
////            isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
////
////            isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
////
////            if (!isGPSEnabled && !isNetworkEnabled) {
////
////            } else {
////                this.canGetLocation = true;
////
////
////                if (isNetworkEnabled) {
////                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
////
////                    }
////                    locationManager.requestLocationUpdates(
////                            LocationManager.NETWORK_PROVIDER,
////                            MIN_TIME_BW_UPDATES,
////                            MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
////
////                        if (locationManager != null) {
////                            location = locationManager
////                                    .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
////
////                            if (location != null) {
////
////                                latitude = location.getLatitude();
////                                longitude = location.getLongitude();
////                            }
////                        }
////
////                    }
////
////                    if(isGPSEnabled) {
////                        if(location == null) {
////                            locationManager.requestLocationUpdates(
////                                    LocationManager.GPS_PROVIDER,
////                                    MIN_TIME_BW_UPDATES,
////                                    MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
////
////                            if(locationManager != null) {
////                                location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
////
////                                if(location != null) {
////                                    latitude = location.getLatitude();
////                                    longitude = location.getLongitude();
////                                }
////                            }
////                        }
////                    }
////            }
////
////        } catch (Exception e) {
////            e.printStackTrace();
////        }
////
////        return location;
////    }
////
////    public void stopUsingGPS() {
////        if(locationManager != null) {
////            locationManager.removeUpdates(GPSTracker.this);
////        }
////    }
////
////    public double getLatitude() {
////        if(location != null) {
////            latitude = location.getLatitude();
////        }
////        return latitude;
////    }
////
////    public double getLongitude() {
////        if(location != null) {
////            longitude = location.getLongitude();
////        }
////
////        return longitude;
////    }
////
////    public boolean canGetLocation() {
////        return this.canGetLocation;
////    }
////
////    public void showSettingsAlert() {
////        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
////
////        alertDialog.setTitle("GPS is settings");
////
////        alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?");
////
////        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
////
////            @Override
////            public void onClick(DialogInterface dialog, int which) {
////                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
////                context.startActivity(intent);
////            }
////        });
////
////        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
////
////            @Override
////            public void onClick(DialogInterface dialog, int which) {
////                dialog.cancel();
////            }
////        });
////
////        alertDialog.show();
////    }
////
////    @Override
////    public void onLocationChanged(Location arg0) {
////
////    }
////
////    @Override
////    public void onProviderDisabled(String arg0) {
////
////    }
////
////    @Override
////    public void onProviderEnabled(String arg0) {
////
////    }
////
////    @Override
////    public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
////
////    }
////
////    @Override
////    public IBinder onBind(Intent intent) {
////        return null;
////    }
//
//}
