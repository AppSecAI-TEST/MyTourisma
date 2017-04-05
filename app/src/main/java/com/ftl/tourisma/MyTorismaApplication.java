package com.ftl.tourisma;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.support.multidex.MultiDexApplication;

import com.crashlytics.android.Crashlytics;
import com.ftl.tourisma.beacons.MyBeaconsService;
import com.ftl.tourisma.utils.MyTypeFace;
import com.pixplicity.easyprefs.library.Prefs;

import io.fabric.sdk.android.Fabric;

/**
 * Created by C162 on 10/10/16.
 */

public class MyTorismaApplication extends MultiDexApplication {
    public static Context context;
    public static MyTypeFace typeFace;

    public static Context getContext() {
        return context;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        // Initialize the Prefs class
        new Prefs.Builder()
                .setContext(this)
                .setMode(ContextWrapper.MODE_PRIVATE)
                .setPrefsName(getPackageName())
                .setUseDefaultSharedPreference(true)
                .build();

        this.context = getApplicationContext();
        typeFace = new MyTypeFace(context);
        Fabric.with(getApplicationContext(), new Crashlytics());
        startService(new Intent(this, MyBeaconsService.class));
    }

}
