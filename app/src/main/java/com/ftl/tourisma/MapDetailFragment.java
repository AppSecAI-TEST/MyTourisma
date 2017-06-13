package com.ftl.tourisma;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
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
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
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
public class MapDetailFragment extends FragmentActivity implements OnMapReadyCallback, View.OnClickListener, post_sync.ResponseHandler {

    private static final String TAG = "MapDetailsFragment";
    private FloatingActionButton fab_gps, fab_list;
    private SupportMapFragment map_detail;
    private GoogleMap map;
    private NormalTextView tv_login_snack, tv_sign_up_snack, tv_snack_msg, tv_your_location_header5, tv_close_map_main, txtPlacename, txtLocation, txtDailyWorkingHours, tv_opening_map_main, tv_detail_map_main;
    private ImageView iv_down_header5, iv_map_direction_main, iv_map_like_main, tv_map_place_share_main, iv_back_main, iv_search_map, iv_back5;
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
            // Tracking exception
            MyTorismaApplication.getInstance().trackException(e);
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
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(layoutManager);
        tv_close_map_main = (NormalTextView) findViewById(R.id.tv_close_map_main);
        tv_close_map_main.setText(Constants.showMessage(MapDetailFragment.this, mPreferences.getString("Lan_Id", ""), "close"));
        tv_close_map_main.setOnClickListener(this);
        ll_map_detail_main = (LinearLayout) findViewById(R.id.ll_map_detail_main);
        map_detail = (SupportMapFragment) this.getSupportFragmentManager().findFragmentById(R.id.map_detail);
        map_detail.getMapAsync(this);
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
    }

    @Override
    public void onClick(View v) {
        if (v == fab_list || v == iv_back5) {
            Intent intent = new Intent();
            Bundle bundle = new Bundle();
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
        } else if (v == ll_login_snack) {
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
            map.clear();
            LatLngBounds.Builder b = new LatLngBounds.Builder();
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Double.parseDouble(mPreferences.getString("latitude1", "")), Double.parseDouble(mPreferences.getString("longitude1", ""))), 8));
            for (int i = 0; i < nearbies.size(); i++) {

                try {
                    latitude = Double.parseDouble(nearbies.get(i).getPlace_Latitude());
                    longitude = Double.parseDouble(nearbies.get(i).getPlace_Longi());
                } catch (NumberFormatException e) {
                    Log.e("System out", e.getMessage());
                }
                try {
                    if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }
                    map.setMyLocationEnabled(false);
                    map.getUiSettings().setZoomControlsEnabled(true);
                    map.getUiSettings().setCompassEnabled(false);
                    map.getUiSettings().setMyLocationButtonEnabled(false);
                    map.getUiSettings().setAllGesturesEnabled(true);
                    map.setTrafficEnabled(true);
                    View marker1 = ((LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.pin_popup, null);
                    final ImageView pin_image = (ImageView) marker1.findViewById(R.id.pin_image);
                    String imageUrl = Constants.IMAGE_URL2 + nearbies.get(i).getCategory_Map_Icon() + "&h=100";
                    Log.i("System out", imageUrl);

                    map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                        @Override
                        public boolean onMarkerClick(Marker marker) {
//                            Log.d("System out", "map detail visible...");
                            ll_map_detail_main.setVisibility(View.VISIBLE);
                            setMapDetail(marker.getSnippet(), nearbies.get(Integer.parseInt(marker.getSnippet())));
                            return false;
                        }
                    });

                    marker = map.addMarker(new MarkerOptions()
                            .position(new LatLng(latitude, longitude))
                            .snippet("" + i)
                            .icon(BitmapDescriptorFactory.fromBitmap(createDrawableFromView(this, marker1))));
                    b.include(marker.getPosition());

                } catch (Exception e) {
                    // Tracking exception
                    MyTorismaApplication.getInstance().trackException(e);
                    Log.e("System out", e.getMessage());
                }
            }
            LatLngBounds bounds = b.build();
            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 150, 150, 5);
            map.animateCamera(cu);
        } catch (Exception e) {
            // Tracking exception
            MyTorismaApplication.getInstance().trackException(e);
            Log.e("System out", "IllegalStateException " + e.getMessage());
        }
    }

    private void setMapPin1() {
        try {
            map.clear();
            LatLngBounds.Builder b = new LatLngBounds.Builder();
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Double.parseDouble(mPreferences.getString("latitude1", "")), Double.parseDouble(mPreferences.getString("longitude1", ""))), 8));
            for (int i = 0; i < nearbies.size(); i++) {

                try {
                    latitude = Double.parseDouble(nearbies.get(i).getPlace_Latitude());
                    longitude = Double.parseDouble(nearbies.get(i).getPlace_Longi());

                } catch (NumberFormatException e) {
                    // Tracking exception
                    MyTorismaApplication.getInstance().trackException(e);
                    Log.e("System out", e.getMessage());
                }
                try {
                    if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }
                    map.setMyLocationEnabled(false);
                    map.getUiSettings().setZoomControlsEnabled(true);
                    map.getUiSettings().setCompassEnabled(false);
                    map.getUiSettings().setMyLocationButtonEnabled(false);
                    map.getUiSettings().setAllGesturesEnabled(true);
                    map.setTrafficEnabled(true);
                    View marker1 = ((LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.pin_popup, null);

                    map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                        @Override
                        public boolean onMarkerClick(Marker marker) {
//                            Log.d("System out", "map detail visible...");
                            ll_map_detail_main.setVisibility(View.VISIBLE);
                            setMapDetail(marker.getSnippet(), nearbies.get(Integer.parseInt(marker.getSnippet())));
                            return false;
                        }
                    });

                    marker = map.addMarker(new MarkerOptions()
                            .position(new LatLng(latitude, longitude))
                            .snippet("" + i)
                            .icon(BitmapDescriptorFactory.fromBitmap(createDrawableFromView(this, marker1))));
                    b.include(marker.getPosition());

                } catch (Exception e) {
                    // Tracking exception
                    MyTorismaApplication.getInstance().trackException(e);
                    Log.e("System out", e.getMessage());
                }
            }

            marker1 = map.addMarker(new MarkerOptions()
                    .position(new LatLng(Double.parseDouble(mPreferences.getString("latitude1", "")), Double.parseDouble(mPreferences.getString("longitude1", ""))))
                    .snippet("" + 0)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
            b.include(marker1.getPosition());
            LatLngBounds bounds = b.build();
            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 150, 150, 5);
            map.animateCamera(cu);
        } catch (Exception e) {
            // Tracking exception
            MyTorismaApplication.getInstance().trackException(e);
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
        tv_detail_map_main.setText(nearbies.get(mId).getPlace_Description());
        tv_detail_map_main.setVisibility(View.GONE);
        String string = nearbies.get(mId).getPlace_MainImage();
        if (nearbies.get(mId).getOtherimages().length() != 0 && nearbies.get(mId).getOtherimages() != null) {
            string += "," + nearbies.get(mId).getOtherimages();
        }

        iv_map_direction_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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

        iv_map_like_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPreferences.getString("User_Id", "").equalsIgnoreCase("0")) {
                    guestSnackToast();
                } else {
                    if (like == 0) {
                        iv_map_like_main.setImageResource(R.drawable.like_map_popup1);
                        addFavoriteCall(nearby.getPlace_Id());
                    } else if (like == 1) {
                        iv_map_like_main.setImageResource(R.drawable.like_map_popup);
                        deleteFavoriteCall(nearby.getFav_Id());
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
            dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
            dialog.setContentView(R.layout.popup_weekdays);
            Window window = dialog.getWindow();
            window.setLayout((width * 90) / 100, LinearLayout.LayoutParams.WRAP_CONTENT);
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
        } catch (Exception e) {
            // Tracking exception
            MyTorismaApplication.getInstance().trackException(e);
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
                    //TODO here need to manage break time
                    spannableStringBuilder = Utils.getSpannableString(Constants.showMessage(this, mPreferences.getString("Lan_Id", ""), "Open Now") + ": ", Utils.getColor(this, R.color.mGreen), true, 0);
                } else {
                    spannableStringBuilder = Utils.getSpannableString(Constants.showMessage(this, mPreferences.getString("Lan_Id", ""), "Closed") + ": ", Utils.getColor(this, android.R.color.holo_red_dark), true, 0);
                }

            } catch (ParseException e) {
                // Tracking exception
                MyTorismaApplication.getInstance().trackException(e);
                e.printStackTrace();
            }
        }
        spannableStringBuilder.append(_24HourSDF.format(_24HourDt) + " " + Constants.showMessage(this, mPreferences.getString("Lan_Id", ""), "TO") + " " + _24HourSDF.format(_24HourDt1));
        return spannableStringBuilder;
    }

    @Override
    public void onResponse(String response, String action) {
        try {
            if (action.equalsIgnoreCase("DeleteFavorite")) {
                setResult(RESULT_OK, new Intent());
                deleteFavoriteResponse(response);
            } else if (action.equalsIgnoreCase("AddFavorite")) {
                setResult(RESULT_OK, new Intent());
                addFavoriteResponse(response);
            }
        } catch (Exception e) {
            // Tracking exception
            MyTorismaApplication.getInstance().trackException(e);
            Log.e(TAG, "onResponse Exception " + e.getLocalizedMessage());
        }
    }

    // Convert a view to bitmap
    public Bitmap createDrawableFromView(Context context, View view) {

        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        view.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        view.measure(displayMetrics.widthPixels, displayMetrics.heightPixels);
        view.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels);
        view.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        return bitmap;
    }

    private void addFavoriteCall(String Place_Id) {
        if (CommonClass.hasInternetConnection(this)) {
            String url = Constants.SERVER_URL + "json.php?action=AddFavorite";
            String json = "[{\"User_Id\":\"" + mPreferences.getString("User_Id", "") + "\",\"Place_Id\":\"" + Place_Id + "\"}]";
            new PostSync(MapDetailFragment.this, "AddFavorite", MapDetailFragment.this).execute(url, json);
        } else {
            Intent intent = new Intent(getApplicationContext(), NoInternet.class);
            startActivity(intent);
            //SnackbarManager.show(Snackbar.with(this).color(Utils.getColor(this,R.color.mBlue)).text(Constants.showMessage(MapDetailFragment.this, mPreferences.getString("Lan_Id", ""), "NOINTERNET")));
        }
    }

    public void addFavoriteResponse(String resultString) {
        if (resultString.length() > 2) {
            try {
                JSONArray jsonArray = new JSONArray(resultString);
                JSONObject jsonObject = jsonArray.optJSONObject(0);
                if (jsonObject.has("status")) {
                    String str = jsonObject.optString("status");
                    if (str.equalsIgnoreCase("true")) {
                        nearbies.get(mId).setFav_Id(jsonObject.optString("Fav_Id"));
                        Constants.mStaticFavCall = 0;
                        Constants.mStaticNearCall = 0;
                        like = 1;
                        setResult(RESULT_OK);
                        SnackbarManager.show(Snackbar.with(this).color(Utils.getColor(this, R.color.mBlue)).text(Constants.showMessage(MapDetailFragment.this, mPreferences.getString("Lan_Id", ""), "AddFavourite")));
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
            new PostSync(MapDetailFragment.this, "DeleteFavorite", MapDetailFragment.this).execute(url, json);
        } else {
            Intent intent = new Intent(getApplicationContext(), NoInternet.class);
            startActivity(intent);
            //SnackbarManager.show(Snackbar.with(this).color(Utils.getColor(this,R.color.mBlue)).text(Constants.showMessage(MapDetailFragment.this, mPreferences.getString("Lan_Id", ""), "NOINTERNET")));
        }
    }

    public void deleteFavoriteResponse(String resultString) {
        if (resultString.length() > 2) {
            try {
                JSONArray jsonArray = new JSONArray(resultString);
                JSONObject jsonObject = jsonArray.optJSONObject(0);
                if (jsonObject.has("status")) {
                    String str = jsonObject.optString("status");
                    if (str.equalsIgnoreCase("true")) {
                        nearbies.get(mId).setFav_Id("0");
                        Constants.mStaticFavCall = 0;
                        Constants.mStaticNearCall = 0;
                        like = 0;
                        setResult(RESULT_OK);
                        SnackbarManager.show(Snackbar.with(this).color(Utils.getColor(this, R.color.mBlue)).text(Constants.showMessage(MapDetailFragment.this, mPreferences.getString("Lan_Id", ""), "Removefavorite")));
                    }
                }
            } catch (JSONException e) {
                // Tracking exception
                MyTorismaApplication.getInstance().trackException(e);
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

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        setMapPin();
    }

    private class PlacesImagesAdapter extends RecyclerView.Adapter<PlacesImagesAdapter.ViewHolder> {

        Context context;

        public PlacesImagesAdapter(Context context) {
            this.context = context;
        }

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
    }
}
