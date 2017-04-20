package com.ftl.tourisma.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.estimote.sdk.repackaged.gson_v2_3_1.com.google.gson.Gson;

import java.util.Set;

/**
 * Created by C162 on 05/11/16.
 */

public class Preference {
    public static final String PREFS = "SharedPreferenceMtTourisma";
    public static String Pref_City="mCity";
    public static String Pref_Country="mCountry";
    public static String Pref_State="mState";
    public static String Pref_Country_code = "mCountryCode";

    public static void setBooleanPrefs(String prefKey, Context context, boolean value) {
        SharedPreferences.Editor editor = context.getSharedPreferences(PREFS, 0).edit();
        editor.putBoolean(prefKey, value);
        editor.commit();
    }

    public static boolean getBooleanPrefs(String prefKey, Context context) {
        return context.getSharedPreferences(PREFS, 0).getBoolean(prefKey, false);
    }

    public static boolean getBooleanPrefs(String prefKey, Context context, boolean defaultValue) {
        return context.getSharedPreferences(PREFS, 0).getBoolean(prefKey, defaultValue);
    }

    public static void setStringPrefs(String prefKey, Context context, String Value) {
        SharedPreferences.Editor editor = context.getSharedPreferences(PREFS, 0).edit();
        editor.putString(prefKey, Value);
        editor.commit();
    }

    public static String getStringPrefs(String prefKey, Context context) {
        return context.getSharedPreferences(PREFS, 0).getString(prefKey, null);
    }

    public static String getStringPrefs(String prefKey, Context context, String defaultValue) {
        return context.getSharedPreferences(PREFS, 0).getString(prefKey, defaultValue);
    }

    public static void setIntPrefs(String prefKey, Context context, int value) {
        SharedPreferences.Editor editor = context.getSharedPreferences(PREFS, 0).edit();
        editor.putInt(prefKey, value);
        editor.commit();
    }

    public static void setStringSetPrefs(String prefKey, Context context, Set<String> value) {
        SharedPreferences.Editor editor = context.getSharedPreferences(PREFS, 0).edit();
        editor.putStringSet(prefKey, value);
        editor.commit();
    }

    public static Set<String> getStringSetPrefs(String prefKey, Context context) {
        return context.getSharedPreferences(PREFS, 0).getStringSet(prefKey, null);
    }

    public static int getIntPrefs(String prefKey, Context context) {
        return context.getSharedPreferences(PREFS, 0).getInt(prefKey, 0);
    }

    public static int getIntPrefs(String prefKey, Context context, int defaultValue) {
        return context.getSharedPreferences(PREFS, 0).getInt(prefKey, defaultValue);
    }

    public static void setLongPrefs(String prefKey, Context context, long value) {
        SharedPreferences.Editor editor = context.getSharedPreferences(PREFS, 0).edit();
        editor.putLong(prefKey, value);
        editor.commit();
    }

    public static long getLongPrefs(String prefKey, Context context) {
        return context.getSharedPreferences(PREFS, 0).getLong(prefKey, 0);
    }

    public static long getLongPrefs(String prefKey, Context context, long defaultValue) {
        return context.getSharedPreferences(PREFS, 0).getLong(prefKey, defaultValue);
    }

    public static void setFloatPrefs(String prefKey, Context context, float value) {
        SharedPreferences.Editor editor = context.getSharedPreferences(PREFS, 0).edit();
        editor.putFloat(prefKey, value);
        editor.commit();
    }

    public static float getFloatPrefs(String prefKey, Context context) {
        return context.getSharedPreferences(PREFS, 0).getFloat(prefKey, 0);
    }

    public static float getFloatPrefs(String prefKey, Context context, long defaultValue) {
        return context.getSharedPreferences(PREFS, 0).getFloat(prefKey, defaultValue);
    }

    /**
     * Clear all data in SharedPreference
     *
     * @param context
     */
    public static void clearWholePreference(Context context) {
        SharedPreferences.Editor editor = context.getSharedPreferences(PREFS, 0).edit();
        editor.clear();
        editor.commit();
    }

    /**
     * Clear single key value
     *
     * @param prefKey
     * @param context
     */
    public static void remove(String prefKey, Context context) {
        SharedPreferences.Editor editor = context.getSharedPreferences(PREFS, 0).edit();
        editor.remove(prefKey);
        editor.commit();
    }

    /**
     * save object in share preference
     *
     * @param prefKey
     * @param context
     * @param object
     */
    public static void setPreferenceObject(String prefKey, Context context, Object object) {
        SharedPreferences appSharedPrefs = PreferenceManager.getDefaultSharedPreferences(
                context);
        SharedPreferences.Editor prefsEditor = appSharedPrefs.edit();
        Gson gson = new Gson();

        String json = gson.toJson(object); // myObject - instance of MyObject
        prefsEditor.putString(prefKey, json);
        prefsEditor.commit();
        ;
    }

    /**
     * get object of shared preference
     *
     * @param prefKey
     * @param context
     * @param object
     * @return
     */
    public static Object getPreferenceJsonObject(String prefKey, Context context, Class object) {
        SharedPreferences appSharedPrefs = PreferenceManager.getDefaultSharedPreferences(
                context.getApplicationContext());
        Gson gson = new Gson();
        String json = appSharedPrefs.getString(prefKey, "");
        Object obj = gson.fromJson(json, object);
        return obj;
    }


}
