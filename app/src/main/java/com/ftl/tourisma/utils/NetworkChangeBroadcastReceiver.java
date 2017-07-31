/*
package com.ftl.tourisma.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.ftl.tourisma.activity.NoInternet;

*/
/**
 * Created by VirtualDusk on 3/23/2017.
 *//*


public class NetworkChangeBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(final Context context, Intent intent) {
        final String status = NetworkUtil.getConnectivityStatusString(context);
        if (status.equals("Not connected to Internet")) {
            intent = new Intent(context, NoInternet.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } else if (status.equals("Wifi enabled") || status.equals("Mobile data enabled")) {
            // check if the No Internet Activity context is visible
            intent = new Intent("closeNoInternetActivity");
            context.sendBroadcast(intent);
        }
    }
}
*/
