/*
 * Copyright 2015 Google Inc. All Rights Reserved.

 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ftl.tourisma;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.Pair;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.ftl.tourisma.custom_views.NormalTextView;
import com.ftl.tourisma.utils.Constants;
import com.ftl.tourisma.utils.Utilities;
import com.google.vr.sdk.widgets.pano.VrPanoramaEventListener;
import com.google.vr.sdk.widgets.pano.VrPanoramaView;
import com.google.vr.sdk.widgets.pano.VrPanoramaView.Options;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static com.ftl.tourisma.beacons.MyBeaconsService.BEACON_ENTERED;
import static com.ftl.tourisma.beacons.MyBeaconsService.BEACON_ENTRY_TEXT;
import static com.ftl.tourisma.beacons.MyBeaconsService.BEACON_EXITED;
import static com.ftl.tourisma.beacons.MyBeaconsService.BEACON_EXIT_TEXT;
import static com.ftl.tourisma.beacons.MyBeaconsService.BEACON_IS_CLOSE_PROMO;
import static com.ftl.tourisma.beacons.MyBeaconsService.BEACON_MESSAGE;
import static com.ftl.tourisma.beacons.MyBeaconsService.BEACON_NEAR_BY;
import static com.ftl.tourisma.beacons.MyBeaconsService.BEACON_NEAR_BY_TEXT;
import static com.ftl.tourisma.beacons.MyBeaconsService.BROADCAST_BEACON;
import static com.ftl.tourisma.beacons.MyBeaconsService.PLACE_ID;
import static com.ftl.tourisma.beacons.MyBeaconsService.PLACE_IMAGE;

/**
 * A basic PanoWidget Activity to load panorama images from disk. It will load a test image by
 * default. It can also load an arbitrary image from disk using:
 * adb shell am start -a "android.intent.action.VIEW" \
 * -n "com.google.vr.sdk.samples.simplepanowidget/.SimpleVrPanoramaActivity" \
 * -d "/sdcard/FILENAME.JPG"
 * <p>
 * To load stereo images, "--ei inputType 2" can be used to pass in an integer extra which will set
 * VrPanoramaView.Options.inputType.
 */
public class SimpleVrPanoramaActivity extends Activity {
    private static final String TAG = SimpleVrPanoramaActivity.class.getSimpleName();
    /**
     * Actual panorama widget.
     **/
//    private VrPanoramaView panoWidgetView;
    /**
     * Arbitrary variable to track load status. In this example, this variable should only be accessed
     * on the UI thread. In a real app, this variable would be code that performs some UI actions when
     * the panorama is fully loaded.
     */
    public boolean loadImageSuccessful;
    /**
     * Tracks the file to be loaded across the lifetime of this app.
     **/
    private Uri fileUri;
    /**
     * Configuration information for the panorama.
     **/
    private Options panoOptions = new Options();
    private DownloadImagesTask downloadImageLoader;
    private String path, path_1;
    private ViewPager viewPager;
    private NormalTextView txtPrev, txtNext, txtTitle;
    private List<VRImages> mResources = new ArrayList<>();
    private List<String> vrImagesLoaded = new ArrayList<>();
    private ImageView imgBack;
    private RecyclerView listview;
    private CustomPagerAdapter customPagerAdapter;
    private DisplayImageOptions displayImageOptions;
    private ImageLoader imageLoader;
    private SharedPreferences mPreferences;
    private SharedPreferences.Editor mEditor;
    private Runnable runnable;
    private Handler handler = new Handler();
    private LinearLayout llBeaconToast;
    private NormalTextView txt_snack_msg;
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null) {
                Bundle bundle = intent.getExtras();
                String type = bundle.getString("type");
                if (type.equals(BEACON_ENTERED)) {
                    beaconsToast(bundle.getString(BEACON_ENTRY_TEXT), bundle.getString(BEACON_MESSAGE), bundle.getString(PLACE_IMAGE), bundle.getString(PLACE_ID), 0, bundle.getString(BEACON_IS_CLOSE_PROMO));

                } else if (type.equals(BEACON_EXITED)) {
                    beaconsToast(bundle.getString(BEACON_EXIT_TEXT), bundle.getString(BEACON_MESSAGE), bundle.getString(PLACE_IMAGE), bundle.getString(PLACE_ID), 0, bundle.getString(BEACON_IS_CLOSE_PROMO));

                } else if (type.equals(BEACON_NEAR_BY)) {
                    beaconsToast(bundle.getString(BEACON_NEAR_BY_TEXT), bundle.getString(BEACON_MESSAGE), bundle.getString(PLACE_IMAGE), bundle.getString(PLACE_ID), 1, bundle.getString(BEACON_IS_CLOSE_PROMO));

                }
            }
        }
    };

    /**
     * Called when the app is launched via the app icon or an intent using the adb command above. This
     * initializes the app and loads the image to render.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);

        // Make the source link clickable.
        displayImageOptions = new DisplayImageOptions.Builder()
                .resetViewBeforeLoading(true)  // default
                .cacheOnDisk(true).considerExifParams(true)
                .cacheInMemory(true)
                .imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2) // default
                .displayer(new SimpleBitmapDisplayer()) // default
                .bitmapConfig(Bitmap.Config.RGB_565).build();

        imageLoader = ImageLoader.getInstance();

        imageLoader.init(ImageLoaderConfiguration.createDefault(this));
        Bundle bundle = getIntent().getExtras();
        path = bundle.getString("path");
        path_1 = bundle.getString("path_1");
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        listview = (RecyclerView) findViewById(R.id.listview);
        llBeaconToast = (LinearLayout) findViewById(R.id.llBeaconToast);
        txt_snack_msg = (NormalTextView) findViewById(R.id.txt_snack_msg);
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        listview.setLayoutManager(layoutManager);

        mResources.add(getObjectVrImage(path, 0, 1));
        if (path_1 != null && !path_1.equals("")) {
            String[] strings = path_1.split(",");
            for (String path : strings)
                mResources.add(getObjectVrImage(path, 0, 0));
        }
        vrImagesLoaded.add(path);
        customPagerAdapter = new CustomPagerAdapter(this);
        viewPager.setAdapter(customPagerAdapter);
        downloadImageLoader = null;
        initView();
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                txtPrev.setEnabled(viewPager.getCurrentItem() != 0);
                txtNext.setEnabled(viewPager.getCurrentItem() != mResources.size() - 1);
                updateUI();
                Log.e(TAG, "Current View : " + position + "// " + viewPager.getCurrentItem());
                if (mResources.get(viewPager.getCurrentItem()).getLoaded() == 0) {
                    Log.e(TAG, "View Notified: " + position + "// " + viewPager.getCurrentItem());
                    mResources.get(viewPager.getCurrentItem()).setNotLoaded(1);
                    customPagerAdapter.updateCurrentUI();
                }
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        // Initial launch of the app or an Activity recreation due to rotation.
        // handleIntent(getIntent());
    }

    private VRImages getObjectVrImage(String path, int i, int i1) {
        VRImages vrImages = new VRImages();
        vrImages.setName(path);
        vrImages.setLoaded(i);
        vrImages.setNotLoaded(i1);
        return vrImages;
    }

    private void initView() {
        try {
            mPreferences = getSharedPreferences(Constants.mPref, 0);
            mEditor = mPreferences.edit();
            imgBack = (ImageView) findViewById(R.id.imgBack);
            txtPrev = (NormalTextView) findViewById(R.id.txtPrev);
            txtTitle = (NormalTextView) findViewById(R.id.txtTitle);
            txtTitle.setText(Constants.showMessage(this, mPreferences.getString("Lan_Id", ""), "virtualreality"));
            txtPrev.setEnabled(false);
            txtNext = (NormalTextView) findViewById(R.id.txtNext);
            updateUI();
            txtPrev.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (viewPager.getCurrentItem() != 0) {
                        viewPager.setCurrentItem(viewPager.getCurrentItem() - 1, true);
                    }
                    txtPrev.setEnabled(viewPager.getCurrentItem() != 0);
                    txtNext.setEnabled(viewPager.getCurrentItem() != mResources.size() - 1);
                    updateUI();
                }
            });
            txtNext.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (viewPager.getCurrentItem() != mResources.size() - 1) {
                        viewPager.setCurrentItem(viewPager.getCurrentItem() + 1, true);
                    }
                    txtPrev.setEnabled(viewPager.getCurrentItem() != 0);
                    txtNext.setEnabled(viewPager.getCurrentItem() != mResources.size() - 1);
                    updateUI();

                }
            });
            imgBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        } catch (Exception e) {
            // Tracking exception
            MyTorismaApplication.getInstance().trackException(e);
            e.printStackTrace();
        }
    }

    private void updateUI() {
        if (txtPrev.isEnabled())
            txtPrev.setTextColor(Utilities.getColor(this, R.color.selector_white_blue));
        else txtPrev.setTextColor(Utilities.getColor(this, R.color.mGray));
        if (txtNext.isEnabled())
            txtNext.setTextColor(Utilities.getColor(this, R.color.selector_white_blue));
        else txtNext.setTextColor(Utilities.getColor(this, R.color.mGray));

    }

    /**
     * Called when the Activity is already running and it's given a new intent.
     */
    @Override
    protected void onNewIntent(Intent intent) {
        Log.i(TAG, this.hashCode() + ".onNewIntent()");
        // Save the intent. This allows the getIntent() call in onCreate() to use this new Intent during
        // future invocations.
        setIntent(intent);
        // Load the new image.
        //   handleIntent(mResources[0]);
    }

    /**
     * Load custom images based on the Intent or load the default image. See the Javadoc for this
     * class for information on generating a custom intent via adb.
     *
     * @param mResource
     */
    private void handleIntent(String mResource, VrPanoramaView panoWidgetView) {
        if (downloadImageLoader != null) {
            // Cancel any task from a previous intent sent to this activity.
            downloadImageLoader.cancel(true);
        }
        downloadImageLoader = new DownloadImagesTask(mResource);
        downloadImageLoader.execute(panoWidgetView);
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            unregisterReceiver(broadcastReceiver);
        } catch (Exception e) {
            // Tracking exception
            MyTorismaApplication.getInstance().trackException(e);
            e.printStackTrace();
        }

    }

    private void beaconsToast(final String msg, final String msgBeacon, final String img, final String placeId, final int isCloseApproach, final String isClosePromo) {
        if (msg != null && !msg.equals("")) {

            txt_snack_msg.setText(msg);
            txt_snack_msg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isClosePromo.equals("1")) {
                        Intent intent = new Intent(SimpleVrPanoramaActivity.this, BeaconsActivity.class);
                        intent.putExtra(PLACE_ID, placeId);
                        intent.putExtra(PLACE_IMAGE, img);
                        intent.putExtra(BEACON_MESSAGE, msgBeacon);
                        startActivity(intent);
                    } else {
                        Intent intent = new Intent(SimpleVrPanoramaActivity.this, SearchResultPlaceDetailsActivity.class);
                        intent.putExtra("placeId", placeId);
                        startActivity(intent);
                    }


                }
            });


            runnable = new Runnable() {
                @Override
                public void run() {
                    llBeaconToast.setVisibility(View.GONE);
                }
            };
            llBeaconToast.setVisibility(View.VISIBLE);
            handler.postDelayed(runnable, 4000);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // panoWidgetView.resumeRendering();
        LocalBroadcastManager.getInstance(this).registerReceiver(
                broadcastReceiver,
                new IntentFilter(BROADCAST_BEACON));
    }

    @Override
    protected void onDestroy() {
        // Destroy the widget and free memory.
        // The background task has a 5 second timeout so it can potentially stay alive for 5 seconds
        // after the activity is destroyed unless it is explicitly cancelled.
        if (downloadImageLoader != null) {
            downloadImageLoader.cancel(true);
        }
        super.onDestroy();
    }

    class VRImages {
        String name;
        int loaded;
        int notLoaded;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getLoaded() {
            return loaded;
        }

        public void setLoaded(int loaded) {
            this.loaded = loaded;
        }

        public int getNotLoaded() {
            return notLoaded;
        }

        public void setNotLoaded(int notLoaded) {
            this.notLoaded = notLoaded;
        }
    }

    class MyVRImagesAdapter extends RecyclerView.Adapter<MyVRImagesAdapter.MyViewHolder> {

        private Context context;

        public MyVRImagesAdapter(Context context) {
            this.context = context;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(context).inflate(R.layout.row_item_virtual_tour, parent, false);
            MyViewHolder viewHolder = new MyViewHolder(itemView);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {

            holder.panoWidgetView.setEventListener(new ActivityEventListener());
            holder.panoWidgetView.setMotionEventSplittingEnabled(true);
            holder.panoWidgetView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            break;
                        case MotionEvent.ACTION_MOVE:
                            break;
                        case MotionEvent.ACTION_UP:
                            break;
                        case MotionEvent.ACTION_POINTER_UP:

                            break;
                    }
                    return true;
                }
            });
            holder.panoWidgetView.setStereoModeButtonEnabled(true);
            DownloadImagesTask downloadImageLoader = new DownloadImagesTask(mResources.get(position).getName());
            downloadImageLoader.execute(holder.panoWidgetView);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public int getItemCount() {
            return mResources.size();
        }

        class MyViewHolder extends RecyclerView.ViewHolder {

            private VrPanoramaView panoWidgetView;

            public MyViewHolder(View itemView) {
                super(itemView);
                panoWidgetView = (VrPanoramaView) itemView.findViewById(R.id.pano_view);
            }
        }
    }

    class CustomPagerAdapter extends PagerAdapter {

        Context mContext;
        LayoutInflater mLayoutInflater;
        Context context;
        SparseArray<View> views = new SparseArray<View>();

        public CustomPagerAdapter(Context context) {
            mContext = context;
            mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return mResources.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == ((RelativeLayout) object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View itemView = mLayoutInflater.inflate(R.layout.row_item_virtual_tour, container, false);
            final VrPanoramaView panoWidgetView = (VrPanoramaView) itemView.findViewById(R.id.pano_view);
            final RelativeLayout progressbar = (RelativeLayout) itemView.findViewById(R.id.progressbar);
            panoWidgetView.setEventListener(new ActivityEventListener());
            panoWidgetView.setMotionEventSplittingEnabled(true);
            panoWidgetView.setStereoModeButtonEnabled(true);
            final Options panoOptions = new Options();
            panoOptions.inputType = Options.TYPE_MONO;
            imageLoader.loadImage(Constants.VR_IMAGE + mResources.get(position).getName(), displayImageOptions, new ImageLoadingListener() {
                @Override
                public void onLoadingStarted(String s, View view) {
                    progressbar.setVisibility(View.VISIBLE);
                }

                @Override
                public void onLoadingFailed(String s, View view, FailReason failReason) {
                    progressbar.setVisibility(View.GONE);
                }

                @Override
                public void onLoadingComplete(String s, View view, Bitmap bitmap) {
                    panoWidgetView.loadImageFromBitmap(bitmap, panoOptions);
                    progressbar.setVisibility(View.GONE);
                }

                @Override
                public void onLoadingCancelled(String s, View view) {
                    progressbar.setVisibility(View.GONE);
                }
            });
            Log.e(TAG, "Downloading..." + position);

            mResources.get(position).setLoaded(1);
            itemView.setTag(mResources.get(position));
            container.addView(itemView);
            return itemView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((RelativeLayout) object);
        }

        public void updateCurrentUI() {

        }
    }

    /**
     * Helper class to manage threading.
     */
    class ImageLoaderTask extends AsyncTask<Pair<Uri, Options>, Void, Boolean> {

        /**
         * Reads the bitmap from disk in the background and waits until it's loaded by pano widget.
         */
        @Override
        protected Boolean doInBackground(Pair<Uri, Options>... fileInformation) {
            Options panoOptions = null;  // It's safe to use null VrPanoramaView.Options.
            InputStream istr = null;
            if (fileInformation == null || fileInformation.length < 1
                    || fileInformation[0] == null || fileInformation[0].first == null) {
                AssetManager assetManager = getAssets();
                try {
                    istr = assetManager.open("andes.jpg");
                    panoOptions = new Options();
                    panoOptions.inputType = Options.TYPE_STEREO_OVER_UNDER;
                } catch (IOException e) {
                    Log.e(TAG, "Could not decode default bitmap: " + e);
                    return false;
                }
            } else {
                try {
                    istr = new FileInputStream(new File(fileInformation[0].first.getPath()));
                    panoOptions = fileInformation[0].second;
                } catch (IOException e) {
                    Log.e(TAG, "Could not load file: " + e);
                    return false;
                }
            }
            try {
                istr.close();
            } catch (IOException e) {
                Log.e(TAG, "Could not close input stream: " + e);
            }
            return true;
        }
    }

    public class DownloadImagesTask extends AsyncTask<VrPanoramaView, Void, Bitmap> {

        VrPanoramaView imageView = null;
        String path;
        private ProgressBar progressbar;

        public DownloadImagesTask(String path) {
            this.path = path;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Bitmap doInBackground(VrPanoramaView... imageViews) {
            this.imageView = imageViews[0];
            return download_Image(Constants.VR_IMAGE + path);
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            panoOptions = new Options();
            panoOptions.inputType = Options.TYPE_MONO;
            imageView.loadImageFromBitmap(result, panoOptions);
        }

        private Bitmap download_Image(String url) {

            Bitmap bmp = null;
            try {
                URL ulrn = new URL(url);
                HttpURLConnection con = (HttpURLConnection) ulrn.openConnection();
                InputStream is = con.getInputStream();
                bmp = BitmapFactory.decodeStream(is);
                if (null != bmp)
                    return bmp;
            } catch (Exception e) {
                // Tracking exception
                MyTorismaApplication.getInstance().trackException(e);
            }
            return bmp;
        }
    }

    /**
     * Listen to the important events from widget.
     */
    private class ActivityEventListener extends VrPanoramaEventListener {
        /**
         * Called by pano widget on the UI thread when it's done loading the image.
         */
        @Override
        public void onLoadSuccess() {
            loadImageSuccessful = true;
        }

        /**
         * Called by pano widget on the UI thread on any asynchronous error.
         */
        @Override
        public void onLoadError(String errorMessage) {
            loadImageSuccessful = false;
            Toast.makeText(SimpleVrPanoramaActivity.this, "Error loading pano: " + errorMessage, Toast.LENGTH_LONG).show();
            Log.e(TAG, "Error loading pano: " + errorMessage);
        }
    }
}
