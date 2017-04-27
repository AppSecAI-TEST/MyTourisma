package com.ftl.tourisma;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.ftl.tourisma.activity.NoInternet;
import com.ftl.tourisma.custom_views.NormalTextView;
import com.ftl.tourisma.database.Nearby;
import com.ftl.tourisma.postsync.post_sync;
import com.ftl.tourisma.utils.CommonClass;
import com.ftl.tourisma.utils.Constants;
import com.ftl.tourisma.utils.Utilities;
import com.ftl.tourisma.utils.Utils;
import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.SnackbarManager;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;

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

// Created by C162 on 25/10/16.


public class FullPlaceImageViewActivity extends Activity implements post_sync.ResponseHandler {
    private static final String TAG = FullPlaceImageViewActivity.class.getSimpleName();
    private int height, width;
    private ImageLoader imageLoader;
    private DisplayImageOptions optionsSimple;
    private Bundle nearBy;
    private String place_id;
    private String place_long;
    private String place_lat;
    private String place_fav_id;
    private String nearBy_main_image, nearBy_other_images, nearBy_name;
    private SharedPreferences mPreferences;
    private SharedPreferences.Editor mEditor;
    private LinearLayout ll_login_snack, llBeaconToast, llYourLocationToast;
    private LinearLayout ll_sign_up_snack;
    private NormalTextView txt_snack_msg, tv_login_snack, tv_sign_up_snack, tv_snack_msg;
    private Runnable runnable;
    private Handler handler = new Handler();
    private Handler handlerBeaconToast = new Handler();
    private boolean isDataModified = false;
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

                }
                else if (type.equals(BEACON_NEAR_BY)) {
                    beaconsToast(bundle.getString(BEACON_NEAR_BY_TEXT), bundle.getString(BEACON_MESSAGE), bundle.getString(PLACE_IMAGE), bundle.getString(PLACE_ID), 1,bundle.getString(BEACON_IS_CLOSE_PROMO));

                }
            }
        }
    };

    private void beaconsToast(final String msg, final String msgBeacon, final String img, final String placeId, final int isCloseApproach, final String isClosePromo) {
        if (msg != null && !msg.equals("")) {

            txt_snack_msg.setText(msg);
            txt_snack_msg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isClosePromo.equals("1")) {
                        Intent intent = new Intent(FullPlaceImageViewActivity.this, BeaconsActivity.class);
                        intent.putExtra(PLACE_ID, placeId);
                        intent.putExtra(PLACE_IMAGE, img);
                        intent.putExtra(BEACON_MESSAGE, msgBeacon);
                        startActivity(intent);
                    } else {
                        Intent intent = new Intent(FullPlaceImageViewActivity.this, SearchResultPlaceDetailsActivity.class);
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
            handlerBeaconToast.postDelayed(runnable, 4000);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_full_screen);

        ll_login_snack = (LinearLayout) findViewById(R.id.ll_login_snack);
        ll_sign_up_snack = (LinearLayout) findViewById(R.id.ll_sign_up_snack);
        llYourLocationToast = (LinearLayout) findViewById(R.id.llYourLocationToast);

        tv_login_snack = (NormalTextView) findViewById(R.id.tv_login_snack);
        tv_sign_up_snack = (NormalTextView) findViewById(R.id.tv_sign_up_snack);
        tv_snack_msg = (NormalTextView) findViewById(R.id.tv_snack_msg);

        llBeaconToast = (LinearLayout) findViewById(R.id.llBeaconToast);
        txt_snack_msg = (NormalTextView) findViewById(R.id.txt_snack_msg);

        try {
            DisplayMetrics displaymetrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
            height = displaymetrics.heightPixels;
            width = displaymetrics.widthPixels;
        } catch (Exception e) {
            // Tracking exception
            MyTorismaApplication.getInstance().trackException(e);
            e.printStackTrace();
        }

        imageLoader = ImageLoader.getInstance();

        mPreferences = this.getSharedPreferences(Constants.mPref, 0);
        mEditor = mPreferences.edit();
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this)
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .denyCacheImageMultipleSizesInMemory()
                .diskCacheFileNameGenerator(new Md5FileNameGenerator())
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                .build();

        imageLoader.init(ImageLoaderConfiguration.createDefault(this));
        optionsSimple = new DisplayImageOptions.Builder()
                .showImageForEmptyUri(null)
                .displayer(new SimpleBitmapDisplayer())
                .showImageOnFail(null).cacheInMemory(true)
                .cacheOnDisk(true).build();
        ImageLoader.getInstance().init(config);
        place_id = getIntent().getStringExtra("nearBy_id");
        place_long = getIntent().getStringExtra("nearBy_longi");
        place_lat = getIntent().getStringExtra("nearBy_lati");
        place_fav_id = getIntent().getStringExtra("nearBy_Fav_id");
        nearBy_main_image = getIntent().getStringExtra("nearBy_main_image");
        nearBy_other_images = getIntent().getStringExtra("nearBy_other_images");
        nearBy_name = getIntent().getStringExtra("nearBy_name");
        setUI();
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        if (isDataModified) {
            Intent intent = new Intent();
            intent.putExtra("DATA", place_fav_id);
            setResult(RESULT_OK, intent);
        }
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(
                broadcastReceiver,
                new IntentFilter(BROADCAST_BEACON));
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

    private void setUI() {
        try {

            final ImageView imgFav = (ImageView) findViewById(R.id.imgFav);
            if (place_fav_id.equalsIgnoreCase("0")) {
                imgFav.setActivated(false);
            } else {
                imgFav.setActivated(true);
            }
            imgFav.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mPreferences.getString("User_Id", "").equalsIgnoreCase("0")) {
                        guestSnackToast();
                    } else {
                        if (place_fav_id.equalsIgnoreCase("0")) {
                            imgFav.setActivated(true);
                            addFavoriteCall(place_id);
                            // setDetailInfo(nearbies.get(placePosition));
//                            mFlag = placePosition;
                        } else {
                            imgFav.setActivated(false);
                            deleteFavoriteCall(place_fav_id);
                            // setDetailInfo(nearbies.get(placePosition));
//                            mFlag = placePosition;
                        }
                    }
                }
            });


            final ImageView imgStartNavigating = (ImageView) findViewById(R.id.imgStartNavigating);
            final ImageView imgShare = (ImageView) findViewById(R.id.imgShare);
            imgShare.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent mIntent = new Intent(FullPlaceImageViewActivity.this, ShareFragmentActivity.class);
                    String share1 = Constants.showMessage(FullPlaceImageViewActivity.this, mPreferences.getString("Lan_Id", ""), "share1");
                    String share2 = Constants.showMessage(FullPlaceImageViewActivity.this, mPreferences.getString("Lan_Id", ""), "share2");
                    String share3 = Constants.showMessage(FullPlaceImageViewActivity.this, mPreferences.getString("Lan_Id", ""), "share3");
                    mIntent.putExtra("myMsg", share1 + " \"" + mPreferences.getString("User_Name", "") + "\" " + share2 + " \"" + nearBy_name + "\" " + share3);
                    startActivity(mIntent);
                }
            });
            imgStartNavigating.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String uri = String.format(Locale.ENGLISH, "http://maps.google.com/maps?saddr=%f,%f&daddr=%f,%f", Double.parseDouble(mPreferences.getString("latitude1", "")), Double.parseDouble(mPreferences.getString("longitude1", "")), Double.parseDouble(place_lat), Double.parseDouble(place_long));
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                    intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
                    startActivity(intent);
                }
            });
            final ViewPager viewPager = (ViewPager) findViewById(R.id.viewPager);
            final NormalTextView txtTitle = (NormalTextView) findViewById(R.id.txtTitle);
            txtTitle.setText(nearBy_name);
            ArrayList<String> mResourcesImages = new ArrayList<>();
            mResourcesImages.add(nearBy_main_image);
            if (nearBy_other_images != null && !nearBy_other_images.equals("") && !nearBy_other_images.equals("null")) {
                String[] images = nearBy_other_images.split(",");
                if (images.length > 0) {
                    for (String i : images) {
                        mResourcesImages.add(i);
                    }
                }
            }
            PlaceImagesAdapter placeImagesAdapter = new PlaceImagesAdapter(FullPlaceImageViewActivity.this, mResourcesImages);

            viewPager.setAdapter(placeImagesAdapter);
            //  viewPager.setCurrentItem(position);
            final ImageView imgBack = (ImageView) findViewById(R.id.imgBack);
            final ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);
//        final Animation zoomin = AnimationUtils.loadAnimation(getActivity(), R.anim.zoom_in);
//        final Animation zoomout = AnimationUtils.loadAnimation(getActivity(), R.anim.zoom_out);
//        imgView.setAnimation(zoomin);
//        imgView.setAnimation(zoomout);


            imgBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();

//
                }
            });


//        viewCategory.setOnKeyListener(new View.OnKeyListener() {
//            @Override
//            public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
//                if (keyCode == KeyEvent.KEYCODE_BACK) popupWindow.dismiss();
//
//                return false;
//            }
//        });
        } catch (Exception e) {
            // Tracking exception
            MyTorismaApplication.getInstance().trackException(e);
            Utilities.Log(TAG, "ViewImageVideoPopUp", e);
        }

    }

    private void addFavoriteCall(String Place_Id) {
        if (CommonClass.hasInternetConnection(this)) {
            String url = Constants.SERVER_URL + "json.php?action=AddFavorite";
            String json = "[{\"User_Id\":\"" + mPreferences.getString("User_Id", "") + "\",\"Place_Id\":\"" + Place_Id + "\"}]";
//            Log.d("System out", "AddFavorite " + json);
//            new post_sync(FullPlaceImageViewActivity.this, "AddFavorite_Full").execute(url, json);
            new post_sync(this, "AddFavorite_Full", FullPlaceImageViewActivity.this, true).execute(url, json);

        } else {
            Intent intent = new Intent(getApplicationContext(), NoInternet.class);
            startActivity(intent);
            //SnackbarManager.show(Snackbar.with(this).color(Utils.getColor(this, R.color.mBlue)).text(Constants.showMessage(this, mPreferences.getString("Lan_Id", ""), "NOINTERNET")));
        }
    }

    public void addFavoriteResponse(String resultString) {
//        Log.d("System out", resultString);
//        [{"Fav_Id":23,"status":"true"}]
        if (resultString.length() > 2) {
            try {
                JSONArray jsonArray = new JSONArray(resultString);
                JSONObject jsonObject = jsonArray.optJSONObject(0);
                if (jsonObject.has("status")) {
                    String str = jsonObject.optString("status");
                    if (str.equalsIgnoreCase("true")) {
                        place_fav_id = jsonObject.optString("Fav_Id");
                        isDataModified = true;
                        SnackbarManager.show(Snackbar.with(this).color(Utils.getColor(this, R.color.mBlue)).text(Constants.showMessage(this, mPreferences.getString("Lan_Id", ""), "AddFavourite")));
                    } else {

                    }
                } else {
                }
            } catch (JSONException e) {
                // Tracking exception
                MyTorismaApplication.getInstance().trackException(e);
                e.printStackTrace();
            }
        }
    }

    private void deleteFavoriteCall(String Fav_Id) {
        if (CommonClass.hasInternetConnection(this)) {
            String url = Constants.SERVER_URL + "json.php?action=DeleteFavorite";
            String json = "[{\"User_Id\":\"" + mPreferences.getString("User_Id", "") + "\",\"Fav_Id\":\"" + Fav_Id + "\"}]";
//            Log.d("System out", "DeleteFavorite " + json);
//            new post_sync(FullPlaceImageViewActivity.this, "DeleteFavorite_Full").execute(url, json);
            new post_sync(this, "DeleteFavorite_Full", FullPlaceImageViewActivity.this, true).execute(url, json);

        } else {
            Intent intent = new Intent(getApplicationContext(), NoInternet.class);
            startActivity(intent);
            //SnackbarManager.show(Snackbar.with(this).color(Utils.getColor(this, R.color.mBlue)).text(Constants.showMessage(this, mPreferences.getString("Lan_Id", ""), "NOINTERNET")));
        }
    }

    public void deleteFavoriteResponse(String resultString) {
//        Log.d("System out", resultString);
        if (resultString.length() > 2) {
            try {
                JSONArray jsonArray = new JSONArray(resultString);
                JSONObject jsonObject = jsonArray.optJSONObject(0);
                if (jsonObject.has("status")) {
                    String str = jsonObject.optString("status");
                    if (str.equalsIgnoreCase("true")) {
                        place_fav_id = "0";
                        isDataModified = true;

                        SnackbarManager.show(Snackbar.with(this).color(Utils.getColor(this, R.color.mBlue)).text(Constants.showMessage(this, mPreferences.getString("Lan_Id", ""), "Removefavorite")));
                    }
                }
            } catch (JSONException e) {
                // Tracking exception
                MyTorismaApplication.getInstance().trackException(e);
            }
        }
    }

    @Override
    public void onResponse(String response, String action) {
        try {
            if (action.equalsIgnoreCase("AddFavorite_Full")) {
                addFavoriteResponse(response);
            } else if (action.equalsIgnoreCase("DeleteFavorite_Full")) {
                deleteFavoriteResponse(response);
            }
        } catch (Exception e) {
            // Tracking exception
            MyTorismaApplication.getInstance().trackException(e);
            Log.e(TAG, "onResponse Exception " + e.getLocalizedMessage());
        }
    }

    private void guestSnackToast() {

        tv_login_snack.setText(Constants.showMessage(this, mPreferences.getString("Lan_Id", ""), "Login"));
        tv_sign_up_snack.setText(Constants.showMessage(this, mPreferences.getString("Lan_Id", ""), "SignUp"));
        tv_snack_msg.setText(Constants.showMessage(this, mPreferences.getString("Lan_Id", ""), "GetStarted"));

        runnable = new Runnable() {
            @Override
            public void run() {
                llYourLocationToast.setVisibility(View.GONE);
            }
        };
        llYourLocationToast.setVisibility(View.VISIBLE);
        handler.postDelayed(runnable, 4000);
    }

    class PlaceImagesAdapter extends PagerAdapter {
        Context mContext;
        Nearby nearby;
        LayoutInflater mLayoutInflater;
        ArrayList<String> mResourcesImages;

        public PlaceImagesAdapter(Context context, ArrayList<String> mResourcesImages) {
            this.mContext = context;
            this.nearby = nearby;
            this.mResourcesImages = mResourcesImages;
            mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return mResourcesImages.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == ((RelativeLayout) object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View itemView = mLayoutInflater.inflate(R.layout.row_item, container, false);
            String imageUrl = Constants.IMAGE_URL + mResourcesImages.get(position) + "&w=" + (width);
            final ProgressBar progressBar = (ProgressBar) itemView.findViewById(R.id.progressBar);

            final ImageView imageView = (ImageView) itemView.findViewById(R.id.imageView);
//            imageView.setImageResource(mResourcesImages.get(position));
            Picasso.with(mContext).load(imageUrl).into(imageView);
           /* imageLoader.loadImage(imageUrl, optionsSimple, new ImageLoadingListener() {
                @Override
                public void onLoadingStarted(String imageUri, View view) {
                    progressBar.setVisibility(View.VISIBLE);

                }

                @Override
                public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                    progressBar.setVisibility(View.GONE);

                }

                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    imageView.setImageBitmap(loadedImage);
                    progressBar.setVisibility(View.GONE);


                }

                @Override
                public void onLoadingCancelled(String imageUri, View view) {
                    progressBar.setVisibility(View.GONE);


                }
            });*/
            container.addView(itemView);

            return itemView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((RelativeLayout) object);
        }

    }
}
