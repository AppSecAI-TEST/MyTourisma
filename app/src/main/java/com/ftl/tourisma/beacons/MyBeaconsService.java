package com.ftl.tourisma.beacons;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanFilter;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.Region;
import com.ftl.tourisma.MyTorismaApplication;
import com.ftl.tourisma.R;
import com.ftl.tourisma.SplashFragmentActivity;
import com.ftl.tourisma.utils.Preference;
import com.ftl.tourisma.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Created by C162 on 03/11/16.
 */

public class MyBeaconsService extends Service {
    public static final String BROADCAST_BEACON = "beacon";
    public static final String BEACON_EXITED = "beacon_exited";
    public static final String BEACON_ENTERED = "beacon_entered";
    public static final String BEACON_NEAR_BY = "beacon_near_by";
    public static final String BEACON_RANGE = "beacon_range";
    public static final String BEACON_MINOR = "beacon_minor";
    public static final String BEACON_MAJOR = "beacon_major";
    public static final String BEACON_TYPE = "is_close_approach";//is_close_approach =0 for enter nd exit is_close_approach=1 for nearby
    public static final String BEACON_IMAGE = "beacon_image";
    public static final String BEACON_ID = "beacon_id";
    public static final String BEACON_NAME = "beacon_name";
    public static final String PLACE_ID = "place_id";
    public static final String BEACON_MESSAGE = "beacon_message";
    public static final String PLACE_IMAGE = "place_image";
    public static final String BEACON_ENTRY_TEXT = "entry_text";
    public static final String BEACON_EXIT_TEXT = "exit_text";
    public static final String BEACON_NEAR_BY_TEXT = "nearby_text";
    public static final String BEACON_IS_CLOSE_PROMO = "is_close_promo";
    public static final String BEACON_UUDI = "beacon_uuid";
    private static final String TAG = MyBeaconsService.class.getSimpleName();
    private static final int TYPE_ENTER = 0, TYPE_EIXT = 1, TYPE_RANGE = 3;
    public BeaconManager beaconManager;
    List<ScanFilter> filters = new ArrayList<ScanFilter>();
    private UUID mMyUuid = UUID.fromString("32769A6A-E884-4CCE-8D86-9A979E1B5ED5");
    private BluetoothLeScanner mBluetoothLeScanner;
    private boolean isServiceRunning;
    private int intentId = 0;
    private PendingIntent pendingIntent;

    protected static double calculateDistance(int txPower, double rssi) {
        if (rssi == 0) {
            return -1.0; // if we cannot determine distance, return -1.
        }

        double ratio = rssi * 1.0 / txPower;
        if (ratio < 1.0) {
            return Math.pow(ratio, 10);
        } else {
            double accuracy = (0.89976) * Math.pow(ratio, 7.7095) + 0.111;
            return accuracy;
        }
    }

    public boolean isServiceRunning() {
        return isServiceRunning;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e(TAG, "onCreate");
        isServiceRunning = true;
        beaconManager = new BeaconManager(getApplicationContext());
        beaconManager.setMonitoringListener(new BeaconManager.MonitoringListener() {
            @Override
            public void onEnteredRegion(Region region, List<Beacon> list) {
                Log.e(TAG, "onEnteredRegion " + region.getProximityUUID());
                getBeaconNotification(TYPE_ENTER, region, 0);
            }

            @Override
            public void onExitedRegion(Region region) {
                Log.e(TAG, "onExitedRegion " + region.getProximityUUID());
                getBeaconNotification(TYPE_EIXT, region, 0);
            }
        });

        beaconManager.setRangingListener(new BeaconManager.RangingListener() {
            @Override
            public void onBeaconsDiscovered(Region region, List<Beacon> list) {
                if (list != null) {
                    if (list.size() > 0) {
                        for (Beacon beacon : list) {

                            Beacon selectedBeacon = beacon;
                            double v = calculateDistance(selectedBeacon.getMeasuredPower(), selectedBeacon.getRssi());
                            double range = 0;
                            String type = "";
                            Set<String> stringSet = Preference.getStringSetPrefs("keyBeacons", getApplicationContext());
                            if (stringSet != null) {
                                for (String str : stringSet) {
                                    try {
                                        JSONObject jsonObject = new JSONObject(str);
                                        if (region.getIdentifier().equals(jsonObject.getString("uuid") + ":" + jsonObject.getInt("major") + ":" + jsonObject.getInt("minor"))) {
                                            type = jsonObject.getString(BEACON_TYPE);
                                            range = jsonObject.getInt("range");
                                        }
                                    } catch (JSONException e) {
                                        // Tracking exception
                                        MyTorismaApplication.getInstance().trackException(e);
                                        e.printStackTrace();
                                    }
                                }
                            }
                            if (type.equals("1")) {
                                if (v <= range) {
                                    if (Preference.getBooleanPrefs(region.getIdentifier() + "nearby", getApplicationContext(), false)) {
                                        Preference.setBooleanPrefs(region.getIdentifier() + "nearby", getApplicationContext(), false);
                                        String enterMessage = getEnteredMessage(region);
                                        if (enterMessage != null && !enterMessage.equals("")) {
                                            showNotification(getApplicationContext().getResources().getString(R.string.app_name), getEnteredMessage(region), BEACON_ENTERED, region.getIdentifier());
                                        }
                                    }
                                } else if (v > range) {
                                    if (!Preference.getBooleanPrefs(region.getIdentifier() + "nearby", getApplicationContext(), false)) {
                                        Preference.setBooleanPrefs(region.getIdentifier() + "nearby", getApplicationContext(), true);
                                        String exitMessage = getExitedRegionMessage(region);
                                        if (exitMessage != null && !exitMessage.equals("")) {
                                            showNotification(getApplicationContext().getResources().getString(R.string.app_name), exitMessage, BEACON_EXITED, region.getIdentifier());
                                        }
                                    }
                                }
                            } else {

                            }
                        }
                    }
                }
            }
        });

        beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
                                  @Override
                                  public void onServiceReady() {
                                      Set<String> stringSet = Preference.getStringSetPrefs("keyBeacons", getApplicationContext());
                                      if (stringSet != null) {
                                          for (String str : stringSet) {
                                              try {
                                                  JSONObject jsonObject = new JSONObject(str);
                                                  beaconManager.startMonitoring(new Region(jsonObject.getString("uuid") + ":" + jsonObject.getInt("major") + ":" + jsonObject.getInt("minor"),
                                                          UUID.fromString(jsonObject.getString("uuid")), jsonObject.getInt("major"), jsonObject.getInt("minor")));
                                                  beaconManager.startRanging(new Region(jsonObject.getString("uuid") + ":" + jsonObject.getInt("major") + ":" + jsonObject.getInt("minor"),
                                                          UUID.fromString(jsonObject.getString("uuid")), jsonObject.getInt("major"), jsonObject.getInt("minor")));
                                              } catch (JSONException e) {
                                                  // Tracking exception
                                                  MyTorismaApplication.getInstance().trackException(e);
                                                  e.printStackTrace();
                                              }
                                          }
                                      }
                                  }
                              }
        );
    }

    private void getBeaconNotification(int type, Region region, int range) {
        try {
            JSONObject jsonObject = checkRegionFound(region);
            if (jsonObject != null) {
                switch (type) {
                    case TYPE_ENTER:
                        try {
                            if (jsonObject.getString(BEACON_TYPE).equalsIgnoreCase("1")) {
                            } else {
                                String enterMesg = jsonObject.getString(BEACON_ENTRY_TEXT);
                                if (enterMesg != null && !enterMesg.equals("")) {
                                    showNotification(getApplicationContext().getResources().getString(R.string.app_name), jsonObject.getString(BEACON_ENTRY_TEXT), BEACON_ENTERED, region.getIdentifier());
                                }
                                Preference.setBooleanPrefs(region.getIdentifier() + "enter", getApplicationContext(), true);
                            }
                        } catch (JSONException e) {
                            // Tracking exception
                            MyTorismaApplication.getInstance().trackException(e);
                            e.printStackTrace();
                        }
                        break;
                    case TYPE_EIXT:
                        try {
                            Log.e(TAG, "onExitedRegion " + jsonObject.getString(BEACON_EXIT_TEXT));
                            if (!jsonObject.getString(BEACON_TYPE).equalsIgnoreCase("1")) {
                                Preference.remove(region.getIdentifier() + "enter", getApplicationContext());
                                String exitMsg = jsonObject.getString(BEACON_EXIT_TEXT);
                                if (exitMsg != null && !exitMsg.equals("")) {
                                    showNotification(getApplicationContext().getResources().getString(R.string.app_name), jsonObject.getString(BEACON_EXIT_TEXT), BEACON_EXITED, region.getIdentifier());
                                }
                            } else {
                            }
                        } catch (JSONException e) {
                            // Tracking exception
                            MyTorismaApplication.getInstance().trackException(e);
                            e.printStackTrace();
                        }
                        break;
                    case TYPE_RANGE:
                        break;
                }
            }
        } catch (Exception e) {
            // Tracking exception
            MyTorismaApplication.getInstance().trackException(e);
            Utils.Log(TAG, " getBeaconNotification Exception : " + e.getLocalizedMessage());
        }
    }

    private JSONObject checkRegionFound(Region region) {
        Set<String> stringSet = Preference.getStringSetPrefs("keyBeacons", getApplicationContext());
        if (stringSet != null) {
            for (String str : stringSet) {
                try {
                    JSONObject jsonObject = new JSONObject(str);
                    if (region.getIdentifier().equals(jsonObject.getString("uuid") + ":" + jsonObject.getInt("major") + ":" + jsonObject.getInt("minor")))
                        return jsonObject;
                } catch (JSONException e) {
                    // Tracking exception
                    MyTorismaApplication.getInstance().trackException(e);
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    private double getRangeofRegion(Region region) {
        Set<String> stringSet = Preference.getStringSetPrefs("keyBeacons", getApplicationContext());
        if (stringSet != null) {
            for (String str : stringSet) {
                try {
                    JSONObject jsonObject = new JSONObject(str);
                    if (region.getIdentifier().equals(jsonObject.getString("uuid") + ":" + jsonObject.getInt("major") + ":" + jsonObject.getInt("minor")))
                        return jsonObject.getInt("range");
                } catch (JSONException e) {
                    // Tracking exception
                    MyTorismaApplication.getInstance().trackException(e);
                    e.printStackTrace();
                }
            }
        }
        return 1;
    }

    double getDistance(int rssi, int txPower) {
    /*
     * RSSI = TxPower - 10 * n * lg(d)
     * n = 2 (in free space)
     *
     * d = 10 ^ ((TxPower - RSSI) / (10 * n))
     */
        return Math.pow(10d, ((double) txPower - rssi) / (10 * 2));
    }

    private String getExitedRegionMessage(Region region) {
        Set<String> stringSet = Preference.getStringSetPrefs("keyBeacons", getApplicationContext());
        if (stringSet != null) {
            for (String str : stringSet) {
                try {
                    JSONObject jsonObject = new JSONObject(str);
                    if (region.getIdentifier().equals(jsonObject.getString("uuid") + ":" + jsonObject.getInt("major") + ":" + jsonObject.getInt("minor")))
                        return jsonObject.getString("exit_text");
                } catch (JSONException e) {
                    // Tracking exception
                    MyTorismaApplication.getInstance().trackException(e);
                    e.printStackTrace();
                }
            }
        }
        return "";
    }

    private String getNearByRegionMessage(Region region) {
        Set<String> stringSet = Preference.getStringSetPrefs("keyBeacons", getApplicationContext());
        if (stringSet != null) {
            for (String str : stringSet) {
                try {
                    JSONObject jsonObject = new JSONObject(str);
                    if (region.getIdentifier().equals(jsonObject.getString("uuid") + ":" + jsonObject.getInt("major") + ":" + jsonObject.getInt("minor")))
                        return jsonObject.getString("nearby_text");
                } catch (JSONException e) {
                    // Tracking exception
                    MyTorismaApplication.getInstance().trackException(e);
                    e.printStackTrace();
                }
            }
        }
        return "";
    }


    private String getEnteredMessage(Region region) {
        Set<String> stringSet = Preference.getStringSetPrefs("keyBeacons", getApplicationContext());
        if (stringSet != null) {
            for (String str : stringSet) {
                try {
                    JSONObject jsonObject = new JSONObject(str);
                    if (region.getIdentifier().equals(jsonObject.getString("uuid") + ":" + jsonObject.getInt("major") + ":" + jsonObject.getInt("minor")))
                        return jsonObject.getString("entry_text");
                } catch (JSONException e) {
                    // Tracking exception
                    MyTorismaApplication.getInstance().trackException(e);
                    e.printStackTrace();
                }
            }
        }
        return "";
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isServiceRunning = false;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.e(TAG, "In Background");
        return null;
    }

    private boolean isAppIsInBackground(Context context) {
        boolean isInBackground = true;
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
            List<ActivityManager.RunningAppProcessInfo> runningProcesses = am.getRunningAppProcesses();
            for (ActivityManager.RunningAppProcessInfo processInfo : runningProcesses) {
                if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    for (String activeProcess : processInfo.pkgList) {
                        if (activeProcess.equals(context.getPackageName())) {
                            isInBackground = false;
                        }
                    }
                }
            }
        } else {
            List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
            ComponentName componentInfo = taskInfo.get(0).topActivity;
            if (componentInfo.getPackageName().equals(context.getPackageName())) {
                isInBackground = false;
            }
        }
        return isInBackground;
    }

    public void showNotification(String title, String message, String type, String beacon) {
        JSONObject jsonObject;
        String placeid = null, placeImage = null, messageBeacon = null, message1, is_close_promo = null, entered_message = null, exit_message = null, near_by_message = null, beaconType = null;
        Set<String> stringSet = Preference.getStringSetPrefs("keyBeacons", getApplicationContext());
        if (stringSet != null) {
            for (String str : stringSet) {
                try {
                    jsonObject = new JSONObject(str);
                    if (beacon.equals(jsonObject.getString("uuid") + ":" + jsonObject.getInt("major") + ":" + jsonObject.getInt("minor"))) {
                        placeImage = jsonObject.getString("image_path");
                        messageBeacon = jsonObject.getString("message");
                        placeid = jsonObject.getString("place_id");
                        entered_message = jsonObject.getString("entry_text");
                        exit_message = jsonObject.getString("exit_text");
                        near_by_message = jsonObject.getString("nearby_text");
                        beaconType = jsonObject.getString(BEACON_TYPE);
                        is_close_promo = jsonObject.getString(BEACON_IS_CLOSE_PROMO);
                        break;
                    }
                } catch (JSONException e) {
                    // Tracking exception
                    MyTorismaApplication.getInstance().trackException(e);
                    e.printStackTrace();
                }
            }
        }
        int nid = Preference.getIntPrefs("NOTIFICATION_ID", this, 0);

        if (isAppIsInBackground(getApplicationContext())) {

            Intent notifyIntent = new Intent(this, SplashFragmentActivity.class);
            notifyIntent.putExtra(BEACON_ENTRY_TEXT, entered_message);
            notifyIntent.putExtra(BEACON_EXIT_TEXT, exit_message);
            notifyIntent.putExtra(BEACON_NEAR_BY_TEXT, near_by_message);
            notifyIntent.putExtra(BEACON_MESSAGE, messageBeacon);
            notifyIntent.putExtra(PLACE_ID, placeid);
            notifyIntent.putExtra("type", type);
            notifyIntent.putExtra(BEACON_NAME, beacon);
            notifyIntent.putExtra(PLACE_IMAGE, placeImage);
            notifyIntent.putExtra(BEACON_TYPE, beaconType);
            notifyIntent.putExtra(BEACON_IS_CLOSE_PROMO, is_close_promo);
            notifyIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
//            pendingIntent = PendingIntent.getActivity(this, nid++, notifyIntent, 0);
            PendingIntent pendingIntent = PendingIntent.getActivities(this, 0, new Intent[]{notifyIntent}, PendingIntent.FLAG_UPDATE_CURRENT);
            Notification notification = new Notification.Builder(this)
                    .setSmallIcon(R.drawable.appicon1)
                    .setContentTitle(title)
                    .setContentText(message)
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntent)
                    .build();
            notification.defaults |= Notification.DEFAULT_SOUND;
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//            notificationManager.notify(nid, notification);
            notificationManager.notify(1, notification);
            Preference.setIntPrefs("NOTIFICATION_ID", this, nid);
        } else {
            Intent broadCastIntent = new Intent(BROADCAST_BEACON);
            broadCastIntent.putExtra(BEACON_ENTRY_TEXT, entered_message);
            broadCastIntent.putExtra(BEACON_EXIT_TEXT, exit_message);
            broadCastIntent.putExtra(BEACON_NEAR_BY_TEXT, near_by_message);
            broadCastIntent.putExtra(PLACE_ID, placeid);
            broadCastIntent.putExtra("type", type);
            broadCastIntent.putExtra(BEACON_NAME, beacon);
            broadCastIntent.putExtra(BEACON_MESSAGE, messageBeacon);
            broadCastIntent.putExtra(PLACE_IMAGE, placeImage);
            broadCastIntent.putExtra(BEACON_IS_CLOSE_PROMO, is_close_promo);
            LocalBroadcastManager.getInstance(this).sendBroadcast(broadCastIntent);
        }
    }
}
