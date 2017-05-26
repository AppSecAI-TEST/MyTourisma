package com.ftl.tourisma.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.provider.Settings;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.ftl.tourisma.MyTorismaApplication;
import com.ftl.tourisma.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Class containing common methods to use in the app.
 */
public class Utils {

    public static final String TAG = Utils.class.getSimpleName();
    private static final Lock lock = new ReentrantLock();
    /**
     * display image options for the universal image loader.
     * these remain common so define as static.
     */
    public static DisplayImageOptions
            options = new DisplayImageOptions.Builder()
            .cacheInMemory(true)
            .cacheOnDisk(true)
            .considerExifParams(true)
            .bitmapConfig(Bitmap.Config.RGB_565)
            .build();
    private static AlertDialog dialogOffline;
    private static AlertDialog dialogServerAlert;
    private static ProgressDialog progressDialog;
    private static ObjectMapper mapper;

    /**
     * common method to launch new activity.
     *
     * @param context
     * @param classToOpen
     */
    public static void launchIntent(Context context, Class classToOpen) {
        Intent intent = new Intent(context, classToOpen);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    /**
     * These is the method to show toast in android.
     *
     * @param message
     * @param context
     */
    public static void showToast(Context context, String message) {
        try {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log(TAG, "showToast ", e);
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

    /**
     * function to get somepart of string in color.just pass the string and color for that.
     *
     * @param spanString
     * @param color
     * @param fontSize
     * @return
     */
    public static SpannableStringBuilder getSpannableString(String spanString, Integer color, boolean isBold, float fontSize) {
        SpannableStringBuilder f = new SpannableStringBuilder(spanString);
        if (isBold)
            f.setSpan(new CustomTypefaceSpan("", MyTorismaApplication.typeFace.getBoldFonts()), 0, f.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        else
            f.setSpan(new CustomTypefaceSpan("", MyTorismaApplication.typeFace.getNormalFonts()), 0, f.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        f.setSpan(new ForegroundColorSpan(color), 0, f.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        if (fontSize != 0)
            f.setSpan(new RelativeSizeSpan(fontSize), 0, f.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        return f;
    }


    /**
     * check for the substring presence in string.
     *
     * @param original
     * @param tobeChecked
     * @param caseSensitive
     * @return
     */
    public static boolean containsString(String original, String tobeChecked, boolean caseSensitive) {
        if (caseSensitive) {
            return original.contains(tobeChecked);
        } else {
            return original.toLowerCase().contains(tobeChecked.toLowerCase());
        }
    }


    /**
     * Hide Keyboard
     *
     * @param mContext
     */
    public static void hideKeyboard(Context mContext) {
        try {
            InputMethodManager imm = (InputMethodManager) mContext
                    .getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(((Activity) mContext).getWindow()
                    .getCurrentFocus().getWindowToken(), 0);

        } catch (Exception e) {
            Log(TAG, "hideKeyboard", e);
        }
    }

    /**
     * Open keyboard programmatically.
     *
     * @param view
     * @param context
     */
    public static void showSoftKeyboard(View view, Context context) {
        if (view.requestFocus()) {
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
        }
    }

    /**
     * Get deviceId
     *
     * @param context
     * @return
     */
    public static String getDeviceTokenId(Context context) {
        return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    /**
     * @param string convert it to html formatted form.
     * @return
     */
    public static Spanned formatHtml(String string) {
        return Html.fromHtml(string);
    }

    /**
     * Format number to alphabetical index.
     *
     * @param i
     * @return
     */

    public static String getCharForNumber(int i) {
        return i > 0 && i < 27 ? String.valueOf((char) (i + 'A' - 1)) : null;
    }

    public static void showProgressDialog(Context context) {
        progressDialog = new ProgressDialog(context);
        progressDialog.setProgressStyle(android.R.style.Theme_Translucent_NoTitleBar);
        progressDialog.setIndeterminateDrawable(Utils.getDrawable(context, R.drawable.progress_background));
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    public static void showProgressDefaultDialog(Context context) {
        progressDialog = new ProgressDialog(context);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    public static void showProgressDialog(Context context, String message) {
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage(message);
        progressDialog.setIndeterminateDrawable(Utils.getDrawable(context, R.drawable.progress_background));
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    public static void hideProgressDialog() {
        if (progressDialog != null)
            progressDialog.cancel();
    }

    public static void hideProgressDialog1() {
        if (progressDialog != null)
            progressDialog.cancel();
    }

    public static void toast(String msg) {
        Toast.makeText(MyTorismaApplication.getContext(), msg, Toast.LENGTH_SHORT).show();
    }

    public static int getColor(Context context, int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return context.getColor(color);
        } else {
            //noinspection deprecation
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

    public static void setTextAppearance(Context context, int resid, View v) {

    }

    public static void hideView(View view, Context context) {
        view.setVisibility(View.GONE);
    }

    public static void showView(View view, Context context) {
        view.setVisibility(View.VISIBLE);
    }

    public static void invisibleView(View view, Context context) {
        view.setVisibility(View.INVISIBLE);
    }

    public static void displayAlert(Context context, String message) {

        try {
            new AlertDialog.Builder(context, AlertDialog.THEME_HOLO_LIGHT).setMessage(message)
                    .setPositiveButton(R.string.ok, null).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String objectToString(Object object) throws IOException {
        ObjectWriter writer = getMapper().writer();
        String json = writer.writeValueAsString(object);
        return json;
    }

    public static synchronized ObjectMapper getMapper() {
        if (mapper != null) {
            return mapper;
        }
        try {
            lock.lock();
            if (mapper == null) {
                mapper = new ObjectMapper();
                mapper.configure(SerializationFeature.WRITE_NULL_MAP_VALUES,
                        false);
            }
            lock.unlock();
        } catch (Exception ex) {
            if (ex != null)
                Log(TAG, "getMapper -> Mapper Initialization Failed."
                        , ex);
        }
        return mapper;
    }

    public static void Log(String log_tag, String message, Exception e) {
        Log.e(log_tag, message + " -> Exception : " + e.getLocalizedMessage());
    }

    public static void Log(String log_tag, String message) {
        Log.e(log_tag, message);
    }

    public static int getHeight(DisplayMetrics displayMetrics) {
        return displayMetrics.heightPixels;
    }

    public static int getWidth(DisplayMetrics displayMetrics) {
        return displayMetrics.widthPixels;
    }


    public static boolean isValidMobile(String phoneNumber) {
        return android.util.Patterns.PHONE.matcher(phoneNumber).matches();
    }

    public static String removeSpecialCharacters(String str) {
        return str.replaceAll("[|?*<\":>+\\[\\]/']", "");
    }

    public static String EncodeTextToBase64(String text) {
        byte[] data = new byte[0];
        String base64 = "";
        try {
            data = text.getBytes("UTF-8");
            base64 = new String(data, "ISO-8859-1");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return base64;
    }

    public static String DecodeTextToBase64(String text) {
        String sendtext = null;
        try {
            byte[] data = text.getBytes("ISO-8859-1");
            sendtext = new String(data, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return sendtext;
    }

    public static boolean isInternetAvailable() {
        try {
            InetAddress ipAddr = InetAddress.getByName("google.com"); //You can replace it with your name
            if (ipAddr.equals("")) {
                return false;
            } else {
                return true;
            }
        } catch (Exception e) {
            Log(TAG, "isInternetAvailable", e);
            return false;
        }
    }

    public static JSONObject StringToJson(String text) {
        try {
            JSONObject obj = new JSONObject(text);
            Log(TAG, obj.toString());
            return obj;
        } catch (JSONException t) {
            Log(TAG, "Could not parse malformed JSON: \"" + text + "\"", t);
        }
        return null;
    }

    public static String BitMapToString(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] b = baos.toByteArray();
        String temp = Base64.encodeToString(b, Base64.DEFAULT);
        return temp;
    }

    /*
    this method is used for get current day of week.
     */
    public static String getCurrentDay() {
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE");
        Date d = new Date();
        String dayOfTheWeek = sdf.format(d);
        return dayOfTheWeek;
    }

    public static String getYesterDayDay() {
        Date d = new Date(System.currentTimeMillis() - (1000 * 60 * 60 * 24));
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE");
        String dayOfTheWeek = sdf.format(d);
        return dayOfTheWeek;
    }
}
