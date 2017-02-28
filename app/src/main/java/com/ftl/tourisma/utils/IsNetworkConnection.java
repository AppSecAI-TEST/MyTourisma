package com.ftl.tourisma.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/*
 * IsNetworkConnection.java is static class to check network connection. return true is yes otherwise false.
 * */
public class IsNetworkConnection {
	/**
	 * Checks if the device has Internet connection.
	 * @return <code>true</code> if the phone is connected to the Internet.
	 */
	public static boolean checkNetworkConnection(Context context) {
		boolean connected = false;
		ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
		    if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
		            connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
		        //we are connected to a network
		        connected = true;
		    }
		    else
		        connected = false;
		    return connected;
	}
}
