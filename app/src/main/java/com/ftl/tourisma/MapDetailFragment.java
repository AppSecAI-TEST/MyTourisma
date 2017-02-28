package com.ftl.tourisma;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableStringBuilder;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.ftl.tourisma.activity.NoInternet;
import com.ftl.tourisma.adapters.TimingAdapter;
import com.ftl.tourisma.custom_views.NormalTextView;
import com.ftl.tourisma.database.Nearby;
import com.ftl.tourisma.models.HourDetails;
import com.ftl.tourisma.models.WeekDaysModel;
import com.ftl.tourisma.postsync.PostSync;
import com.ftl.tourisma.postsync.post_sync;
import com.ftl.tourisma.utils.CommonClass;
import com.ftl.tourisma.utils.Constants;
import com.ftl.tourisma.utils.GPSTracker;
import com.ftl.tourisma.utils.Preference;
import com.ftl.tourisma.utils.TimingFunction;
import com.ftl.tourisma.utils.Utils;
import com.github.clans.fab.FloatingActionButton;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.SnackbarManager;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by fipl11111 on 16-Mar-16.
 */
public class MapDetailFragment extends FragmentActivity implements View.OnClickListener, post_sync.ResponseHandler {

    private static final String TAG = "MapDetailsFragment";
    private FloatingActionButton fab_gps, fab_list;
    private GoogleMap map_detail;
    private NormalTextView tv_login_snack, tv_sign_up_snack, tv_snack_msg, tv_your_location_header5, tv_close_map_main, txtPlacename, txtLocation, txtDailyWorkingHours, tv_opening_map_main, tv_detail_map_main;
    private ImageView iv_down_header5, iv_map_direction_main, iv_map_like_main, tv_map_place_share_main, iv_back_main, iv_search_map, iv_back5;
    //    private com.ftl.tourisma.gallery1.Gallery gv_detail_map_main;
    private Double latitude, longitude;
    private ArrayList<Nearby> nearbies = new ArrayList<>();
    private Intent mIntent;
    private Marker marker, marker1;
    private DisplayImageOptions options;
    private ImageLoader imageLoader = ImageLoader.getInstance();
    private String[] strImg1;
    private SharedPreferences mPreferences;
    private int width, height;
    private NormalTextView tv_map_location;
    private LinearLayout ll_header5_location;
    private SimpleDateFormat _24HourSDF, _12HourSDF;
    private String _24HourTime, _24HourTime1, latitude1, longitude1, title;
    private Date _24HourDt, _24HourDt1;
    private int like;
    private GPSTracker gpsTracker;
    private Handler handler = new Handler();
    private Runnable runnable;
    private LinearLayout ll_login_snack, ll_sign_up_snack, llMapLocationToast, ll_map_detail_main;
    private Dialog dialog;
    private ArrayList<HourDetails> hourDetailses;
    private RecyclerView mRecyclerView;
    private int mId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.map_locations);

        try {
            DisplayMetrics displaymetrics = new DisplayMetrics();
            this.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
            height = displaymetrics.heightPixels;
            width = displaymetrics.widthPixels;
        } catch (Exception e) {
            e.printStackTrace();
        }

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this)
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .denyCacheImageMultipleSizesInMemory()
                .diskCacheFileNameGenerator(new Md5FileNameGenerator())
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                .build();

        imageLoader.init(ImageLoaderConfiguration.createDefault(this));
        options = new DisplayImageOptions.Builder()
                .showImageForEmptyUri(null)
                .displayer(new SimpleBitmapDisplayer())
                .showImageOnFail(null).cacheInMemory(true)
                .cacheOnDisk(true).build();
        ImageLoader.getInstance().init(config);

        mPreferences = getSharedPreferences(Constants.mPref, 0);

        mIntent = getIntent();
        nearbies = (ArrayList<Nearby>) mIntent.getSerializableExtra("nearbies");
        title = mIntent.getStringExtra("title");

        gpsTracker = new GPSTracker(MapDetailFragment.this);
        if (mPreferences.getString("latitude1", "").equalsIgnoreCase("0.0") || mPreferences.getString("longitude1", "").equalsIgnoreCase("0.0") || mPreferences.getString("latitude1", "").equalsIgnoreCase("") || mPreferences.getString("longitude1", "").equalsIgnoreCase("")) {
            if (gpsTracker.canGetLocation()) {
                gpsTracker.getLocation();
                mPreferences.edit().putString("latitude1", String.valueOf(gpsTracker.getLatitude())).commit();
                mPreferences.edit().putString("longitude1", String.valueOf(gpsTracker.getLongitude())).commit();

            } else {
                gpsTracker.showSettingsAlert();
            }
        }

        init();

        setMapPin();
    }

    private void init() {
        llMapLocationToast = (LinearLayout) findViewById(R.id.llMapLocationToast);
        ll_login_snack = (LinearLayout) findViewById(R.id.ll_login_snack);
        ll_sign_up_snack = (LinearLayout) findViewById(R.id.ll_sign_up_snack);
        tv_login_snack = (NormalTextView) findViewById(R.id.tv_login_snack);
        tv_sign_up_snack = (NormalTextView) findViewById(R.id.tv_sign_up_snack);
        tv_snack_msg = (NormalTextView) findViewById(R.id.tv_snack_msg);
        ll_sign_up_snack.setOnClickListener(this);
        ll_login_snack.setOnClickListener(this);

        iv_down_header5 = (ImageView) findViewById(R.id.iv_down_header5);
        iv_down_header5.setVisibility(View.GONE);

        latitude1 = mIntent.getStringExtra("latitude");
        longitude1 = mIntent.getStringExtra("longitude");

        ll_header5_location = (LinearLayout) findViewById(R.id.ll_header5_location);
//        ll_header5_location.setOnClickListener(this);

        tv_your_location_header5 = (NormalTextView) findViewById(R.id.tv_your_location_header5);
        tv_your_location_header5.setText(Constants.showMessage(MapDetailFragment.this, mPreferences.getString("Lan_Id", ""), "locationtitle"));

        iv_search_map = (ImageView) findViewById(R.id.iv_search_map);
        iv_search_map.setVisibility(View.GONE);

        iv_back5 = (ImageView) findViewById(R.id.iv_back5);
        iv_back5.setOnClickListener(this);

        iv_map_direction_main = (ImageView) findViewById(R.id.iv_map_direction_main);
        iv_map_like_main = (ImageView) findViewById(R.id.iv_map_like_main);
        txtPlacename = (NormalTextView) findViewById(R.id.tv_map_place_name_main);
        txtLocation = (NormalTextView) findViewById(R.id.tv_map_place_location_main);
        tv_opening_map_main = (NormalTextView) findViewById(R.id.txtOpenNowVal);

        txtDailyWorkingHours = (NormalTextView) findViewById(R.id.txtDailyWorkingHours);
        txtDailyWorkingHours.setText(Constants.showMessage(this, mPreferences.getString("Lan_Id", ""), "View Daily Working Hours"));
        txtDailyWorkingHours.setOnClickListener(this);

        tv_detail_map_main = (NormalTextView) findViewById(R.id.tv_detail_map_main);
        tv_map_place_share_main = (ImageView) findViewById(R.id.tv_map_place_share_main);
//        tv_map_place_share_main.setOnClickListener(this);
//        iv_back_main = (ImageView) findViewById(R.id.iv_back_main);
//        iv_back_main.setOnClickListener(this);

//        gv_detail_map_main = (com.ftl.tourisma.gallery1.Gallery) findViewById(R.id.gv_detail_map_main);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(layoutManager);

        tv_close_map_main = (NormalTextView) findViewById(R.id.tv_close_map_main);
        tv_close_map_main.setText(Constants.showMessage(MapDetailFragment.this, mPreferences.getString("Lan_Id", ""), "close"));
        tv_close_map_main.setOnClickListener(this);

        ll_map_detail_main = (LinearLayout) findViewById(R.id.ll_map_detail_main);

        map_detail = ((MapFragment) this.getFragmentManager().findFragmentById(R.id.map_detail)).getMap();

        fab_list = (FloatingActionButton) findViewById(R.id.fab_list);
        fab_list.setOnClickListener(this);
        fab_gps = (FloatingActionButton) findViewById(R.id.fab_gps);
        fab_gps.setOnClickListener(this);

        tv_map_location = (NormalTextView) findViewById(R.id.tv_map_location);

        if (title.length() != 0) {
            tv_map_location.setText("\"" + title + "\"");
            tv_your_location_header5.setText(mPreferences.getString(Preference.Pref_City, ""));
        } else {
            tv_map_location.setText(mPreferences.getString(Preference.Pref_City, ""));
            tv_map_location.setText("");
        }

        tv_your_location_header5.setText(mPreferences.getString(Preference.Pref_City, ""));

//        if (nearbies.size() == 1) {
//            title = nearbies.get(0).getPlace_Name();
//            title = "Spot";
           // tv_map_location.setText("\"" + title + "\"");
//        } else {
//            title = Constants.showMessage(MapDetailFragment.this, mPreferences.getString("Lan_Id", ""), "spotmultipale");
           // tv_map_location.setText("\"" + title + "\"");
//        }

    }

    @Override
    public void onClick(View v) {
        if (v == fab_list || v == iv_back5) {
            Intent intent=new Intent();
            Bundle bundle=new Bundle();
            intent.putExtra("nearbies", nearbies);
            setResult(RESULT_OK, intent);
            finish();
        } else if (v == tv_close_map_main) {
            ll_map_detail_main.setVisibility(View.GONE);
        } else if (v == ll_header5_location) {
           /* Intent mIntent = new Intent(MapDetailFragment.this, SelectLocationFragmentActivity.class);
            mIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(mIntent);
            finish();*/
        } else if (v == fab_gps) {
            fab_gps.setImageResource(R.drawable.gps_icon_map_selected);
            setMapPin1();
        } else if (v == txtDailyWorkingHours) {
            openWeekDaysPopup();
        }else if (v == ll_login_snack) {
            mPreferences.edit().putString("User_id", "").commit();
            Intent mIntent = new Intent(MapDetailFragment.this, LoginFragmentActivity.class);
            startActivity(mIntent);
            finish();
        } else if (v == ll_sign_up_snack) {
            mPreferences.edit().putString("User_id", "").commit();
            Intent mIntent = new Intent(MapDetailFragment.this, SignupFragmentActivity.class);
            startActivity(mIntent);
            finish();
        }
    }

    private void setMapPin() {
        try {
            map_detail.clear();
            LatLngBounds.Builder b = new LatLngBounds.Builder();

            map_detail.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Double.parseDouble(mPreferences.getString("latitude1", "")), Double.parseDouble(mPreferences.getString("longitude1", ""))), 8));


            for (int i = 0; i < nearbies.size(); i++) {

                try {
                    latitude = Double.parseDouble(nearbies.get(i).getPlace_Latitude());
                    longitude = Double.parseDouble(nearbies.get(i).getPlace_Longi());

//                    Log.d("System out", "At mgoogle :" + "" + "Lat" + latitude + ""
//                            + "Long" + longitude);

                } catch (NumberFormatException e) {
                    Log.e("System out", e.getMessage());
                }
                try {
                    map_detail.setMyLocationEnabled(false);
                    map_detail.getUiSettings().setZoomControlsEnabled(true);
                    map_detail.getUiSettings().setCompassEnabled(false);
                    map_detail.getUiSettings().setMyLocationButtonEnabled(false);
                    map_detail.getUiSettings().setAllGesturesEnabled(true);
                    map_detail.setTrafficEnabled(true);

                    View marker1 = ((LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.pin_popup, null);
//                    final NormalTextView pin_text = (NormalTextView) marker1.findViewById(R.id.pin_text);
//                    final NormalTextView pin_text1 = (NormalTextView) marker1.findViewById(R.id.pin_text1);
                    final ImageView pin_image = (ImageView) marker1.findViewById(R.id.pin_image);
//                    pin_text1.setId(i);
//                    pin_text.setText(Constants.showMessage(MapDetailFragment.this, mPreferences.getString("Lan_Id", ""), "KM"));
//                    pin_text1.setText("" + Math.round(Float.parseFloat(nearbies.get(i).getDist())));

                    String imageUrl = Constants.IMAGE_URL2 + nearbies.get(i).getCategory_Map_Icon() + "&h=100";
                    Log.i("System out", imageUrl);
//                    Picasso.with(MapDetailFragment.this) //
//                            .load(imageUrl) //
                    //  .placeholder(R.drawable.map_pin1)
                    // .error(R.drawable.map_pin1)
//                            .into(pin_image);

                    map_detail.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                        @Override
                        public boolean onMarkerClick(Marker marker) {
//                            Log.d("System out", "map detail visible...");
                            ll_map_detail_main.setVisibility(View.VISIBLE);
                            setMapDetail(marker.getSnippet(), nearbies.get(Integer.parseInt(marker.getSnippet())));
                            return false;
                        }
                    });

                    marker = map_detail.addMarker(new MarkerOptions()
                            .position(new LatLng(latitude, longitude))
                            .snippet("" + i)
                            .icon(BitmapDescriptorFactory.fromBitmap(createDrawableFromView(this, marker1))));
                    b.include(marker.getPosition());

                } catch (Exception e) {
                    Log.e("System out", e.getMessage());
                }
            }
            LatLngBounds bounds = b.build();
            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 150, 150, 5);
            map_detail.animateCamera(cu);
        } catch (Exception e) {
            Log.e("System out", "IllegalStateException " + e.getMessage());
        }
    }

    private void setMapPin1() {
        try {
            map_detail.clear();
            LatLngBounds.Builder b = new LatLngBounds.Builder();

            map_detail.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Double.parseDouble(mPreferences.getString("latitude1", "")), Double.parseDouble(mPreferences.getString("longitude1", ""))), 8));

            for (int i = 0; i < nearbies.size(); i++) {

                try {
                    latitude = Double.parseDouble(nearbies.get(i).getPlace_Latitude());
                    longitude = Double.parseDouble(nearbies.get(i).getPlace_Longi());

                } catch (NumberFormatException e) {
                    Log.e("System out", e.getMessage());
                }
                try {
                    map_detail.setMyLocationEnabled(false);
                    map_detail.getUiSettings().setZoomControlsEnabled(true);
                    map_detail.getUiSettings().setCompassEnabled(false);
                    map_detail.getUiSettings().setMyLocationButtonEnabled(false);
                    map_detail.getUiSettings().setAllGesturesEnabled(true);
                    map_detail.setTrafficEnabled(true);

                    View marker1 = ((LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.pin_popup, null);

                    map_detail.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                        @Override
                        public boolean onMarkerClick(Marker marker) {
//                            Log.d("System out", "map detail visible...");
                            ll_map_detail_main.setVisibility(View.VISIBLE);
                            setMapDetail(marker.getSnippet(), nearbies.get(Integer.parseInt(marker.getSnippet())));
                            return false;
                        }
                    });

                    marker = map_detail.addMarker(new MarkerOptions()
                            .position(new LatLng(latitude, longitude))
                            .snippet("" + i)
                            .icon(BitmapDescriptorFactory.fromBitmap(createDrawableFromView(this, marker1))));
                    b.include(marker.getPosition());

                } catch (Exception e) {
                    Log.e("System out", e.getMessage());
                }
            }

            marker1 = map_detail.addMarker(new MarkerOptions()
                    .position(new LatLng(Double.parseDouble(mPreferences.getString("latitude1", "")), Double.parseDouble(mPreferences.getString("longitude1", ""))))
                    .snippet("" + 0)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));

            b.include(marker1.getPosition());

            LatLngBounds bounds = b.build();
            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 150, 150, 5);
            map_detail.animateCamera(cu);
        } catch (Exception e) {
            Log.e("System out", "IllegalStateException " + e.getMessage());
        }
    }

    private void setMapDetail(String id, final Nearby nearby) {
        mId = Integer.parseInt(id);
        hourDetailses = nearby.getHourDetailsArrayList();
        txtPlacename.setText(nearbies.get(mId).getPlace_Name());
        txtPlacename.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MapDetailFragment.this, SearchResultPlaceDetailsActivity.class);
                intent.putExtra("placeId", nearbies.get(mId).getPlace_Id());
                startActivity(intent);
            }
        });
        txtLocation.setText(nearbies.get(mId).getPlace_Address());

      /*  try {
            _24HourTime = nearbies.get(mId).getHoursOfOperations().get(0).getPOH_Start_Time();
            _24HourTime1 = nearbies.get(mId).getHoursOfOperations().get(0).getPOH_End_Time();
            _24HourSDF = new SimpleDateFormat("HH:mm");
            _12HourSDF = new SimpleDateFormat("hh:mma");
            _24HourDt = _24HourSDF.parse(_24HourTime);
            _24HourDt1 = _24HourSDF.parse(_24HourTime1);
                    *//*System.out.println(_24HourDt);
                    System.out.println(_12HourSDF.format(_24HourDt));*//*
        } catch (Exception e) {
            e.printStackTrace();
        }*/
       /* int dayFoundStatus = 0; //0 -> no day found 1-> found but closed - 2-> open but time not found 3 -> done
        if (nearby.getHourDetailsArrayList() != null && nearby.getHourDetailsArrayList().size() > 0) {
//            hourDetails = nearby.getHourDetailsArrayList();
            for (HourDetails hourDetails : nearby.getHourDetailsArrayList()) {
                if (hourDetails.getPOHKey().equals(Utils.getCurrentDay())) {
                    SpannableStringBuilder time = TimingFunction.getPlaceTiming(this,hourDetails,mPreferences);
                    if (hourDetails.getPOHIsOpen().equals(PlaceOpenWithAnyTime)) {
                        if (time != null) {
                            dayFoundStatus = 3;
                            tv_opening_map_main.setText(time);
                        } else {
                            dayFoundStatus = 2;
                        }

                    } else if (hourDetails.getPOHIsOpen().equals(PlaceOpenFor24Hours)) {
                        dayFoundStatus = 2;
                    } else {

                        dayFoundStatus = 1;

                    }

                }
            }
        }
        if (dayFoundStatus == 2) {
            tv_opening_map_main.setText(Utils.getSpannableString(Constants.showMessage(this, mPreferences.getString("Lan_Id", ""), "Open Now"), Utils.getColor(this, R.color.mGreen), true, 0));

            // txtOpenNowVal.setText("");
        } else if (dayFoundStatus == 0 || dayFoundStatus == 1) {

            tv_opening_map_main.setText(Utils.getSpannableString(Constants.showMessage(this, mPreferences.getString("Lan_Id", ""), "Closed"), Utils.getColor(this, android.R.color.holo_red_dark), true, 0));
//                    txtOpenNowVal.setText("");
        }
      *//*  try {
            String str = _12HourSDF.format(_24HourDt).toString().replace("AM", "am").replace("PM", "pm") + " " + Constants.showMessage(MapDetailFragment.this, mPreferences.getString("Lan_Id", ""), "TO") + " " + _12HourSDF.format(_24HourDt1).toString().replace("AM", "am").replace("PM", "pm");
            tv_opening_map_main.setText("Opening " + str);
        } catch (Exception e) {
            e.printStackTrace();
        }*//**/

        tv_detail_map_main.setText(nearbies.get(mId).getPlace_Description());
        tv_detail_map_main.setVisibility(View.GONE);
        String string = nearbies.get(mId).getPlace_MainImage();
        if (nearbies.get(mId).getOtherimages().length() != 0 && nearbies.get(mId).getOtherimages() != null) {
            string += "," + nearbies.get(mId).getOtherimages();
        }

        iv_map_direction_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

/*
                Intent mIntent = new Intent(MapDetailFragment.this, com.ftl.tourisma.MapLocationFragment.class);
                mIntent.putExtra("placeName", nearby.getPlace_Name());
                mIntent.putExtra("dist", nearby.getDist());
                mIntent.putExtra("latitude", nearby.getPlace_Latitude());
                mIntent.putExtra("longitude", nearby.getPlace_Longi());
                mIntent.putExtra("mDirection", "yes");
                mIntent.putExtra("address", nearby.getPlace_Address());
                startActivity(mIntent);
*/

                String uri = String.format(Locale.ENGLISH, "http://maps.google.com/maps?saddr=%f,%f&daddr=%f,%f", Double.parseDouble(mPreferences.getString("latitude1", "")), Double.parseDouble(mPreferences.getString("longitude1", "")), Double.parseDouble(nearby.getPlace_Latitude()), Double.parseDouble(nearby.getPlace_Longi()));
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
                startActivity(intent);

            }
        });

        if (nearby.getFav_Id().equalsIgnoreCase("0")) {
            iv_map_like_main.setImageResource(R.drawable.like_map_popup);
            like = 0;
        } else {
            iv_map_like_main.setImageResource(R.drawable.like_map_popup1);
            like = 1;
        }

//        Log.d("System out", "like is 0 for add and 1 for remove " + like);

        iv_map_like_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPreferences.getString("User_Id", "").equalsIgnoreCase("0")) {
                    guestSnackToast();
                } else {
                    if (like == 0) {
                        iv_map_like_main.setImageResource(R.drawable.like_map_popup1);
                        addFavoriteCall(nearby.getPlace_Id());
//                    mFlag = mId;
                    } else if (like == 1) {
                        iv_map_like_main.setImageResource(R.drawable.like_map_popup);
                        deleteFavoriteCall(nearby.getFav_Id());
//                    mFlag = mId;
                    }
                }
            }
        });

        tv_map_place_share_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mIntent = new Intent(MapDetailFragment.this, ShareFragmentActivity.class);
                String share1 = Constants.showMessage(MapDetailFragment.this, mPreferences.getString("Lan_Id", ""), "share1");
                String share2 = Constants.showMessage(MapDetailFragment.this, mPreferences.getString("Lan_Id", ""), "share2");
                String share3 = Constants.showMessage(MapDetailFragment.this, mPreferences.getString("Lan_Id", ""), "share3");

                mIntent.putExtra("myMsg", share1 + " \"" + mPreferences.getString("User_Name", "") + "\" " + share2 + " \"" + nearby.getPlace_Name() + "\" " + share3);
                startActivity(mIntent);

            }
        });

        strImg1 = string.split(",");
        if (strImg1.length > 0) {

            PlacesImagesAdapter placesImagesAdapter = new PlacesImagesAdapter(this);
            mRecyclerView.setAdapter(null);
            mRecyclerView.setAdapter(placesImagesAdapter);
        }
    }

    private void openWeekDaysPopup() {
        try {

            // Inflate the custom layout/view
            WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
            Display display = wm.getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            int width = size.x;
            int height = size.y;
            dialog = new Dialog(this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(false);
            dialog.getWindow()
                    .getAttributes().windowAnimations = R.style.DialogAnimation;
            dialog.setContentView(R.layout.popup_weekdays);
            Window window = dialog.getWindow();
            window.setLayout((width * 90) / 100, LinearLayout.LayoutParams.WRAP_CONTENT);
//            View view = inflater.inflate(R.layout.popup_weekdays, null, false);
//            final PopupWindow popupWindow = new PopupWindow(view, (80 * width) / 100, LinearLayout.LayoutParams.WRAP_CONTENT);
//            if (Build.VERSION.SDK_INT >= 21) {
//                popupWindow.setElevation(5.0f);
//            }
//            popupWindow.setBackgroundDrawable(new BitmapDrawable(getResources(),
//                    ""));
//            popupWindow.setBackgroundDrawable(new ColorDrawable(Utils.getColor(this, R.color.mTrans1)));
//            popupWindow.setOutsideTouchable(false);
            ArrayList<WeekDaysModel> stringArrayList = new ArrayList<>();
            stringArrayList = getTimingArrayList();
            ListView listView = (ListView) dialog.findViewById(R.id.listview_week);
            NormalTextView txtTitle = (NormalTextView) dialog.findViewById(R.id.txtTitle);
            txtTitle.setText(Constants.showMessage(this, mPreferences.getString("Lan_Id", ""), "Time Details Header"));
            ImageView iv_menu_close = (ImageView) dialog.findViewById(R.id.iv_menu_close);
            iv_menu_close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            listView.setAdapter(new TimingAdapter(stringArrayList, this));

            dialog.show();
            //popupWindow.showAtLocation(txtOpenNowVal, Gravity.CENTER, 0, 0);


        } catch (Exception e) {
            Utils.Log(TAG, "openWeekDaysPopup Exception: " + e.getLocalizedMessage());
        }

    }


    public ArrayList<WeekDaysModel> getTimingArrayList() {
        ArrayList<WeekDaysModel> timingArrayList = new ArrayList<>();
        if (hourDetailses != null) {
            for (HourDetails hourDetails : hourDetailses) {
                WeekDaysModel weekDaysModel = new WeekDaysModel();
                weekDaysModel.setTime(TimingFunction.getTimingWeekDayFormat(this, hourDetails, mPreferences, hourDetailses));
                weekDaysModel.setDay(Constants.showMessage(this, mPreferences.getString("Lan_Id", ""), hourDetails.getPOHKey()));

                timingArrayList.add(weekDaysModel);
//                if (hourDetails.getPOHIsOpen().equals(PlaceOpenFor24Hours)) {
////                    weekDaysModel.setCurrentDay(hourDetails.getPOHKey().equals(Utils.getCurrentDay()));
//                    timingArrayList.add(weekDaysModel);
//                    // getTimingWeekDayFormat(hourDetails);
//
//                } else {
//                    timingArrayList.add(hourDetails.getPOHDay() + ": " + "Closed");
//                }
            }
        }

        return timingArrayList;
    }

    private SpannableStringBuilder getPlaceTiming(HourDetails hourDetails) {
        SpannableStringBuilder spannableStringBuilder = null;

        if (hourDetails.getPOHStartTime().equalsIgnoreCase("null") && hourDetails.getPOHEndTime().equalsIgnoreCase("null")) {
            //Open status only
            return null;
        } else if (hourDetails.getPOHStartTime().equals("00:00:00") && hourDetails.getPOHEndTime().equals("00:00:00")) {
            //Open status only
            return null;
        } else if (hourDetails.getPOHEndTime().equals(hourDetails.getPOHStartTime())) {
            //Open status only beacuse both times are same
            return null;

        } else {
            _24HourTime = hourDetails.getPOHStartTime();
            _24HourTime1 = hourDetails.getPOHEndTime();
            try {
                _24HourDt = _24HourSDF.parse(_24HourTime);
                _24HourDt1 = _24HourSDF.parse(_24HourTime1);
                Calendar calendar = Calendar.getInstance();
                Date c = calendar.getTime();
                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                int minute = calendar.get(Calendar.MINUTE);
                android.text.format.DateFormat dateFormat = new android.text.format.DateFormat();
                boolean is24HourFormat = dateFormat.is24HourFormat(this);
                Date date;
                if (is24HourFormat) {
                    c = _24HourSDF.parse(hour + ":" + minute);
                } else {
                    String formattedDate = _24HourSDF.format(c.getTime());
                    c = _24HourSDF.parse(formattedDate);
                }
                long timeInMilliseconds = c.getTime();
                long timeInMilliseconds1 = _24HourDt.getTime();
                long timeInMilliseconds2 = _24HourDt1.getTime();
                Log.e(TAG, "timeInMilliseconds : " + timeInMilliseconds);
                Log.e(TAG, "timeInMilliseconds1 : " + timeInMilliseconds1);
                Log.e(TAG, "timeInMilliseconds2 : " + timeInMilliseconds2);
                if (timeInMilliseconds1 <= timeInMilliseconds && timeInMilliseconds2 >= timeInMilliseconds) {
                    //Utils.toast("11 true"+date);
                    //TODO here need to manage break time
                    spannableStringBuilder = Utils.getSpannableString(Constants.showMessage(this, mPreferences.getString("Lan_Id", ""), "Open Now") + ": ", Utils.getColor(this, R.color.mGreen), true, 0);
                } else {
                    spannableStringBuilder = Utils.getSpannableString(Constants.showMessage(this, mPreferences.getString("Lan_Id", ""), "Closed") + ": ", Utils.getColor(this, android.R.color.holo_red_dark), true, 0);
                    // Utils.toast("22 false"+date);
                }

            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        spannableStringBuilder.append(_24HourSDF.format(_24HourDt) + " " + Constants.showMessage(this, mPreferences.getString("Lan_Id", ""), "TO") + " " + _24HourSDF.format(_24HourDt1));
//        spannableStringBuilder.append(_12HourSDF.format(_24HourDt).replace("AM", Constants.showMessage(getActivity(), mPreferences.getString("Lan_Id", ""), "am")).replace("PM", Constants.showMessage(getActivity(), mPreferences.getString("Lan_Id", ""), "pm")) + " " + Constants.showMessage(getActivity(), mPreferences.getString("Lan_Id", ""), "TO") + " " + _12HourSDF.format(_24HourDt1).replace("AM", Constants.showMessage(getActivity(), mPreferences.getString("Lan_Id", ""), "am")).replace("PM", Constants.showMessage(getActivity(), mPreferences.getString("Lan_Id", ""), "pm")));
        return spannableStringBuilder;


    }

    @Override
    public void onResponse(String response, String action) {
        try {
            if (action.equalsIgnoreCase("DeleteFavorite")) {
                setResult(RESULT_OK,new Intent());
                deleteFavoriteResponse(response);
            }else if (action.equalsIgnoreCase("AddFavorite")) {
                setResult(RESULT_OK,new Intent());
                addFavoriteResponse(response);
            }
        } catch (Exception e) {
            Log.e(TAG, "onResponse Exception " + e.getLocalizedMessage());
        }
    }

    private class PlacesImagesAdapter extends RecyclerView.Adapter<PlacesImagesAdapter.ViewHolder> {

        class ViewHolder extends RecyclerView.ViewHolder {

            private LinearLayout transparentlayout;
            private NormalTextView tv_name, tv_km;
            private ImageView iv_detail;

            public ViewHolder(View itemView) {
                super(itemView);
                iv_detail = (ImageView) itemView.findViewById(R.id.iv_detail);
                tv_name = (NormalTextView) itemView.findViewById(R.id.tv_name);
                tv_km = (NormalTextView) itemView.findViewById(R.id.tv_km);
                transparentlayout = (LinearLayout) itemView.findViewById(R.id.transparentlayout);

                transparentlayout.setVisibility(View.GONE);
                tv_name.setVisibility(View.GONE);
                tv_km.setVisibility(View.GONE);
            }
        }

        Context context;

        public PlacesImagesAdapter(Context context) {
            this.context = context;
        }

//        @Override
//        public int getCount() {
//            return strImg1.length;
//        }
//
//        @Override
//        public Object getItem(int position) {
//            return null;
//        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.detail_gallery, parent, false);
            ViewHolder viewHolder = new ViewHolder(view);

            return viewHolder;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            String imageUrl = Constants.IMAGE_URL + strImg1[position] + "&w=" + (width);
            imageLoader.displayImage(imageUrl, holder.iv_detail, options);
            holder.iv_detail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MapDetailFragment.this, SearchResultPlaceDetailsActivity.class);
                    intent.putExtra("placeId", nearbies.get(mId).getPlace_Id());
                    startActivity(intent);
                }
            });
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public int getItemCount() {
            return strImg1.length;
        }

//        @Override
//        public View getView(int position, View convertView, ViewGroup parent) {
//            LayoutInflater mLayoutInflater;
//            mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//            convertView = mLayoutInflater.inflate(R.layout.detail_gallery, null);
//            final ImageView iv_detail = (ImageView) convertView.findViewById(R.id.iv_detail);
//            final NormalTextView tv_name = (NormalTextView) convertView.findViewById(R.id.tv_name);
//            final NormalTextView tv_km = (NormalTextView) convertView.findViewById(R.id.tv_km);
//            final LinearLayout transparentlayout = (LinearLayout) convertView.findViewById(R.id.transparentlayout);
//
//            transparentlayout.setVisibility(View.GONE);
//            tv_name.setVisibility(View.GONE);
//            tv_km.setVisibility(View.GONE);
//            String imageUrl = Constants.IMAGE_URL + strImg1[position] + "&w=" + (width);
////            Picasso.with(this) //
////                    .load(imageUrl) //
////                    .into(iv_detail);
//
////            mRecyclerView.seto(new com.ftl.tourisma.gallery1.AdapterView.OnItemClickListener() {
////                @Override
////                public void onItemClick(com.ftl.tourisma.gallery1.AdapterView<?> parent, View view, int position, long id) {
////                    id = position;
//////                    setDetailInfo1(position);
////                }
////            });
//
//            Log.i("System out", imageUrl);
//            imageLoader.displayImage(imageUrl, iv_detail, options);
//
//            return convertView;
//        }
    }

    // Convert a view to bitmap
    public Bitmap createDrawableFromView(Context context, View view) {

        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        view.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        view.measure(displayMetrics.widthPixels, displayMetrics.heightPixels);
        view.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels);
        view.buildDrawingCache();

//        Log.d("System out", "width :" + view.getMeasuredWidth());

        Bitmap bitmap = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);

        return bitmap;
    }

    private void addFavoriteCall(String Place_Id) {
        if (CommonClass.hasInternetConnection(this)) {
            String url = Constants.SERVER_URL + "json.php?action=AddFavorite";
            String json = "[{\"User_Id\":\"" + mPreferences.getString("User_Id", "") + "\",\"Place_Id\":\"" + Place_Id + "\"}]";
//            Log.d("System out", "AddFavorite " + json);
            new PostSync(MapDetailFragment.this, "AddFavorite",MapDetailFragment.this).execute(url, json);
        } else {
            Intent intent = new Intent(getApplicationContext(), NoInternet.class);
            startActivity(intent);
            //SnackbarManager.show(Snackbar.with(this).color(Utils.getColor(this,R.color.mBlue)).text(Constants.showMessage(MapDetailFragment.this, mPreferences.getString("Lan_Id", ""), "NOINTERNET")));
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
                        nearbies.get(mId).setFav_Id(jsonObject.optString("Fav_Id"));
//                        dbAdapter.open();
//                        dbAdapter.updateNearBy(nearbies.get(mFlag).getPlace_Id(), jsonObject.optString("Fav_Id"));
//                        dbAdapter.close();
//                        mFlag = 0;
                        Constants.mStaticFavCall = 0;
                        Constants.mStaticNearCall = 0;
                        like = 1;
                        setResult(RESULT_OK);
                        SnackbarManager.show(Snackbar.with(this).color(Utils.getColor(this,R.color.mBlue)).text(Constants.showMessage(MapDetailFragment.this, mPreferences.getString("Lan_Id", ""), "AddFavourite")));
                    } else {

                    }
                } else {
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void deleteFavoriteCall(String Fav_Id) {
        if (CommonClass.hasInternetConnection(this)) {
            String url = Constants.SERVER_URL + "json.php?action=DeleteFavorite";
            String json = "[{\"User_Id\":\"" + mPreferences.getString("User_Id", "") + "\",\"Fav_Id\":\"" + Fav_Id + "\"}]";
//            Log.d("System out", "DeleteFavorite " + json);
            new PostSync(MapDetailFragment.this, "DeleteFavorite",MapDetailFragment.this).execute(url, json);
        } else {
            Intent intent = new Intent(getApplicationContext(), NoInternet.class);
            startActivity(intent);
            //SnackbarManager.show(Snackbar.with(this).color(Utils.getColor(this,R.color.mBlue)).text(Constants.showMessage(MapDetailFragment.this, mPreferences.getString("Lan_Id", ""), "NOINTERNET")));
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
                        nearbies.get(mId).setFav_Id("0");

//                        nearbies.get(mFlag).setFav_Id("0");
//                        dbAdapter.open();
//                        dbAdapter.updateNearBy(nearbies.get(mFlag).getPlace_Id(), "0");
//                        dbAdapter.close();
//                        mFlag = 0;
                        Constants.mStaticFavCall = 0;
                        Constants.mStaticNearCall = 0;
                        like = 0;
                        setResult(RESULT_OK);
                        SnackbarManager.show(Snackbar.with(this).color(Utils.getColor(this,R.color.mBlue)).text(Constants.showMessage(MapDetailFragment.this, mPreferences.getString("Lan_Id", ""), "Removefavorite")));
                    }
                }
            } catch (JSONException e) {

            }
        }
    }

    private void guestSnackToast() {

        tv_login_snack.setText(Constants.showMessage(MapDetailFragment.this, mPreferences.getString("Lan_Id", ""), "Login"));
        tv_sign_up_snack.setText(Constants.showMessage(MapDetailFragment.this, mPreferences.getString("Lan_Id", ""), "SignUp"));
        tv_snack_msg.setText(Constants.showMessage(MapDetailFragment.this, mPreferences.getString("Lan_Id", ""), "GetStarted"));

        runnable = new Runnable() {
            @Override
            public void run() {
                llMapLocationToast.setVisibility(View.GONE);
            }
        };
        llMapLocationToast.setVisibility(View.VISIBLE);
        handler.postDelayed(runnable, 4000);
    }

}
