package com.ftl.tourisma.utils;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.ftl.tourisma.database.Nearby;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by C162 on 11/10/16.
 */

public class Utilities {
    private static final Lock lock = new ReentrantLock();
    private static final String LOG_TAG = "Utilities";
    private static ObjectMapper mapper = null;
    private static ProgressDialog progressDialog;

    public static double GetRoutDistane(double startLat, double startLong, double endLat, double endLong, String distApi) {
        try {
            Location startPoint = new Location("locationA");
            startPoint.setLatitude(startLat);
            startPoint.setLongitude(startLong);
            Location endPoint = new Location("locationA");
            endPoint.setLatitude(endLat);
            endPoint.setLongitude(endLong);
            DecimalFormat df = new DecimalFormat("#.##");
            Double time = Double.valueOf(df.format(startPoint.distanceTo(endPoint) / 1000));
            return time;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    public static ArrayList<Nearby> sortLocations(ArrayList<Nearby> locations) {
        Comparator comp = new Comparator<Nearby>() {
            @Override
            public int compare(Nearby o, Nearby o2) {
                double distance1 = o.getDistance();
                double distance2 = o2.getDistance();
                return Double.compare(distance1, distance2);
            }
        };
        Collections.sort(locations, comp);
        return locations;
    }

    public static synchronized ObjectMapper getMapper() {
        if (mapper != null) {
            return mapper;
        }
        try {
            lock.lock();
            if (mapper == null) {
                mapper = new ObjectMapper();
                mapper.configure(SerializationFeature.WRITE_NULL_MAP_VALUES, false);
            }
            lock.unlock();
        } catch (Exception ex) {
            if (ex != null)
                Log.e(LOG_TAG, "Mapper Initialization Failed.", ex);
        }
        return mapper;
    }


    public static void showProgressDialog(Context context, String message) {
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage(message);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    public static void hideProgressDialog() {
        if (progressDialog != null)
            progressDialog.cancel();
    }

    /**
     * Common dialog to show for alerts.
     *
     * @param context
     * @param title
     * @param message
     * @return
     */
    public static AlertDialog alert(Context context, String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        if (title != null) {
            builder.setTitle(title);
        }
        if (message != null) {
            builder.setMessage(message);
        }
        AlertDialog dialog = builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        }).create();
        dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        dialog.show();
        return dialog;
    }

    public static void toast(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    public static int getColor(Context context, int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return context.getColor(color);
        } else {
            return context.getResources().getColor(color);
        }
    }

    public static Drawable getDrawable(Context context, int drawable) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return context.getResources().getDrawable(drawable, context.getTheme());
        } else {
            return context.getResources().getDrawable(drawable);
        }
    }

    /**
     * To check for the connectivity.
     *
     * @param context
     * @return
     */
    public static boolean isConnected(Context context) {
        NetworkInfo networkInfo = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnectedOrConnecting();
    }

    public static void Log(String log_tag, String message, Exception e) {
        if (Constants.Dev_mode)
            Log.e(log_tag, message + " -> Exception : " + e.getLocalizedMessage());
    }

    public static void Log(String log_tag, String message) {
        if (Constants.Dev_mode)
            Log.e(log_tag, message);
    }

    public static JSONObject getJSONfromURL(String url) {

        //initialize
        InputStream is = null;
        String result = "";
        JSONObject jArray = null;

        //http post
        try {
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(url);
            HttpResponse response = httpclient.execute(httppost);
            HttpEntity entity = response.getEntity();
            is = entity.getContent();

        } catch (Exception e) {
            Log.e("log_tag", "Error in http connection " + e.toString());
        }

        //convert response to string
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            is.close();
            result = sb.toString();
        } catch (Exception e) {
            Log.e("log_tag", "Error converting result " + e.toString());
        }

        //try parse the string to a JSON object
        try {
            jArray = new JSONObject(result);
        } catch (JSONException e) {
            Log.e("log_tag", "Error parsing data " + e.toString());
        }
        return jArray;
    }
}
