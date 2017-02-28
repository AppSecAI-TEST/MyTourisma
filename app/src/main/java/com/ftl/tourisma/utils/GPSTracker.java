package com.ftl.tourisma.utils;


import android.Manifest;
import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

public class GPSTracker extends Service implements LocationListener {
    private Context mContext;

    boolean isGPSEnabled = false;

    boolean isNetworkEnabled = false;

    boolean canGetLocation = false;

    Location location; // location
    double latitude; // latitude
    double longitude; // longitude

    String resultString;
    // The minimum distance to change Updates in meters
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 0; // 10 meters
//    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 50; // 10 meters

    // The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 0; // 1 minute
//    private static final long MIN_TIME_BW_UPDATES = 0; // 0 minute

    // Declaring a Location Manager
    protected LocationManager locationManager;
    protected boolean fromHome;

    public GPSTracker() {

    }

    public GPSTracker(Context context) {
        this.mContext = context;
        fromHome = false;
        getLocation();
    }

    public GPSTracker(Context context, boolean fromHome) {
        this.mContext = context;
        this.fromHome = fromHome;
        getLocation();
    }

    public Location getLocation() {
        try {
            locationManager = (LocationManager) mContext
                    .getSystemService(LOCATION_SERVICE);

            // getting GPS status
            isGPSEnabled = locationManager
                    .isProviderEnabled(LocationManager.GPS_PROVIDER);

            // getting network status
            isNetworkEnabled = locationManager
                    .isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!isGPSEnabled && !isNetworkEnabled) {
                // no network provider is enabled
                if (!fromHome)
                    showSettingsAlert();
            } else {
                this.canGetLocation = true;
                // First get location from Network Provider

                if (isNetworkEnabled) {

                    int permissionCheck1 = ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION);
                    Log.d("System out", "permission check fine location " + permissionCheck1);
                    if (permissionCheck1 == android.content.pm.PackageManager.PERMISSION_GRANTED) {
                        int permissionCheck2 = ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION);
                        if (permissionCheck2 == android.content.pm.PackageManager.PERMISSION_GRANTED) {
                            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                            Log.d("Network", "Network");
                            if (locationManager != null) {
                                location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                                if (location != null) {
                                    latitude = location.getLatitude();
                                    longitude = location.getLongitude();
                                }
                            }
                        }
                    } else {
                    }
                }// if GPS Enabled get lat/long using GPS Services
                 if (isGPSEnabled && location==null) {
                    int permissionCheck1 = ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION);
                    Log.d("System out", "permission check fine location " + permissionCheck1);
                    if (permissionCheck1 == android.content.pm.PackageManager.PERMISSION_GRANTED) {
                        int permissionCheck2 = ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION);
                        if (permissionCheck2 == android.content.pm.PackageManager.PERMISSION_GRANTED) {
                            if (location == null) {
                                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                                Log.d("GPS Enabled", "GPS Enabled");
                                if (locationManager != null) {
                                    location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                                    if (location != null) {
                                        latitude = location.getLatitude();
                                        longitude = location.getLongitude();
                                    }
                                }
                            }
                        } else {

                        }
                    } else {

                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return location;
    }

    /**
     * Stop using GPS listener
     * Calling this function will stop using GPS in your app
     */
    public void stopUsingGPS() {
        if (locationManager != null) {
//            locationManager.removeUpdates(GPSTracker.this);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopUsingGPS();
    }

    /**
     * Function to get latitude
     */
    public double getLatitude() {
        if (location != null) {
            latitude = location.getLatitude();
        }

        // return latitude
        return latitude;
    }

    /**
     * Function to get longitude
     */
    public double getLongitude() {
        if (location != null) {
            longitude = location.getLongitude();
        }

        // return longitude
        return longitude;
    }

    /**
     * Function to check GPS/wifi enabled
     *
     * @return boolean
     */
    public boolean canGetLocation() {
        return this.canGetLocation;
    }

    /**
     * Function to show settings alert dialog
     * On pressing Settings button will lauch Settings Options
     */
    public void showSettingsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);

        // Setting Dialog Title
        alertDialog.setTitle("GPS is settings");

        // Setting Dialog Message
        alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?");

        // On pressing Settings button
        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                mContext.startActivity(intent);
            }
        });

        // on pressing cancel button
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        // Showing Alert Message
        alertDialog.show();
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onProviderDisabled(String provider) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

}
