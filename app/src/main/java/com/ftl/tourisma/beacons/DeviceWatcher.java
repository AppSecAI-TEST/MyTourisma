package com.ftl.tourisma.beacons;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by C162 on 04/11/16.
 */

public class DeviceWatcher extends BroadcastReceiver {
    private MyBeaconsService myBeaconsService;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e("DeviceWatcher", "onReceive");
        // do anything with this information
        myBeaconsService = new MyBeaconsService();
        String action = intent.getAction();

        if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
            if (intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1)
                    == BluetoothAdapter.STATE_OFF) {
                myBeaconsService.onDestroy();
            } else {
                if (!myBeaconsService.isServiceRunning()) {
                    Log.e("DeviceWatcher", "Started");
                } else {
                    Log.e("DeviceWatcher", "Running");

                }
            }
            // Bluetooth is disconnected, do handling here
        }
    }
}