package com.ftl.tourisma;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.support.multidex.MultiDexApplication;

import com.crashlytics.android.Crashlytics;
import com.ftl.tourisma.app.AnalyticsTrackers;
import com.ftl.tourisma.beacons.MyBeaconsService;
import com.ftl.tourisma.utils.MyTypeFace;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.StandardExceptionParser;
import com.google.android.gms.analytics.Tracker;
import com.pixplicity.easyprefs.library.Prefs;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;

import java.util.concurrent.atomic.AtomicInteger;

import io.fabric.sdk.android.Fabric;

/**
 * Created by C162 on 10/10/16.
 */

public class MyTorismaApplication extends MultiDexApplication {
    //Google Analytics
    public static final String TAG = MyTorismaApplication.class.getSimpleName();
    public static Context context;
    public static MyTypeFace typeFace;
    public static AtomicInteger mAtomicInteger = new AtomicInteger();
    private static MyTorismaApplication mInstance;

    public static Context getContext() {
        return context;
    }

    public static synchronized MyTorismaApplication getInstance() {
        return mInstance;
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

        // Initializing the picasso
        Picasso.Builder builder = new Picasso.Builder(this);
        builder.downloader(new OkHttpDownloader(this, Integer.MAX_VALUE));
        Picasso built = builder.build();
        built.setIndicatorsEnabled(true);
        built.setLoggingEnabled(true);
        Picasso.setSingletonInstance(built);

        this.context = getApplicationContext();
        typeFace = new MyTypeFace(context);

        // Initializing the crashlytics
        Fabric.with(getApplicationContext(), new Crashlytics());

        // Starting the Beacons service
        startService(new Intent(this, MyBeaconsService.class));

        //Google analytics
        mInstance = this;
        AnalyticsTrackers.initialize(this);
        AnalyticsTrackers.getInstance().get(AnalyticsTrackers.Target.APP);
    }

    public synchronized Tracker getGoogleAnalyticsTracker() {
        AnalyticsTrackers analyticsTrackers = AnalyticsTrackers.getInstance();
        return analyticsTrackers.get(AnalyticsTrackers.Target.APP);
    }

    /***
     * Tracking screen view
     *
     * @param screenName screen name to be displayed on GA dashboard
     */
    public void trackScreenView(String screenName) {
        Tracker t = getGoogleAnalyticsTracker();

        // Set screen name.
        t.setScreenName(screenName);

        // Send a screen view.
        t.send(new HitBuilders.ScreenViewBuilder().build());

        GoogleAnalytics.getInstance(this).dispatchLocalHits();
    }

    /***
     * Tracking exception
     *
     * @param e exception to be tracked
     */
    public void trackException(Exception e) {
        if (e != null) {
            Tracker t = getGoogleAnalyticsTracker();

            t.send(new HitBuilders.ExceptionBuilder()
                    .setDescription(
                            new StandardExceptionParser(this, null)
                                    .getDescription(Thread.currentThread().getName(), e))
                    .setFatal(false)
                    .build()
            );
        }
    }

    /***
     * Tracking event
     *
     * @param category event category
     * @param action   action of the event
     * @param label    label
     */
    public void trackEvent(String category, String action, String label) {
        Tracker t = getGoogleAnalyticsTracker();

        // Build and send an Event.
        t.send(new HitBuilders.EventBuilder().setCategory(category).setAction(action).setLabel(label).build());
    }

}
