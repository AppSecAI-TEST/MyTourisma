package com.ftl.tourisma;

import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Point;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.daimajia.slider.library.Indicators.PagerIndicator;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.DefaultSliderView;
import com.daimajia.slider.library.Transformers.BaseTransformer;
import com.ftl.tourisma.activity.NoInternet;
import com.ftl.tourisma.adapters.TimingAdapter;
import com.ftl.tourisma.custom_views.NormalBoldTextView;
import com.ftl.tourisma.custom_views.NormalTextView;
import com.ftl.tourisma.database.FeesDetails;
import com.ftl.tourisma.database.Nearby;
import com.ftl.tourisma.models.HourDetails;
import com.ftl.tourisma.models.WeekDaysModel;
import com.ftl.tourisma.postsync.PostSync;
import com.ftl.tourisma.postsync.post_sync;
import com.ftl.tourisma.utils.CommonClass;
import com.ftl.tourisma.utils.Constants;
import com.ftl.tourisma.utils.CustomTypefaceSpan;
import com.ftl.tourisma.utils.JSONObjConverter;
import com.ftl.tourisma.utils.Preference;
import com.ftl.tourisma.utils.TimingFunction;
import com.ftl.tourisma.utils.Utilities;
import com.ftl.tourisma.utils.Utils;
import com.github.clans.fab.FloatingActionButton;
import com.google.android.gms.maps.model.Marker;
import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.SnackbarManager;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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
import static com.ftl.tourisma.utils.Constants.PlaceClosed;
import static com.ftl.tourisma.utils.Constants.PlaceOpenFor24Hours;
import static com.ftl.tourisma.utils.Constants.PlaceOpenWithAnyTime;

/**
 * Created by fipl11111 on 02-Mar-16.
 */
public class SearchResultFragmentActivity extends FragmentActivity implements View.OnClickListener, post_sync.ResponseHandler {

    private static final int PLACE_LIKE_SEARCH = 1003;
    private static final String TAG = "SearchResultFragment";
    static int mCounter = -1;
    RelativeLayout noplace_found_relative;
    GalleryAdapter2 galleryAdapter2;
    private ImageView iv_back_search_result;
    private ArrayList<Nearby> nearbies = new ArrayList<>();
    private ArrayList<Nearby> nearbies1 = new ArrayList<>();
    private Intent mIntent;
    private DisplayImageOptions options, options2, optionsSimple;
    private ImageLoader imageLoader = ImageLoader.getInstance();
    private ImageLoader imageLoader2 = ImageLoader.getInstance();
    private int width, height;
    private SharedPreferences mPreferences;
    private SharedPreferences.Editor mEditor;
    private RecyclerAdapter recyclerAdapter;
    private RecyclerView rv_search_result;
    private NormalTextView tv_place;
    private SimpleDateFormat _24HourSDF, _12HourSDF;
    private String _24HourTime, _24HourTime1;
    private Date _24HourDt, _24HourDt1;
    private NormalBoldTextView tv_full_name_search_result2;
    private NormalTextView txtDailyWorkingHours, txtOpenNowVal, txt_add_to_fav, tv_distance1_search_result2, tv_info_search_result2, tv_discription_search_result2, tv_your_location_header5, tv_your_location_search_result, tv_search_result, txtMessage, tv_fee_search_result, tv_about_place_search_result, tv_similar_search, tv_see_all_search_result;
    private com.ftl.tourisma.gallery1.Gallery gv_detail1_search_result2;
    private ImageView iv_back5;
    private String[] strImg1;
    private int id1;
    private LinearLayout ll_search_result, llEmptyLayout11, listView_fees, ll_search_result1, ll_search_result2;
    private FloatingActionButton fab_search_result;
    private Double latitude, longitude;
    private int pos = 0;
    private Marker marker;
    private String search;
    private NormalTextView tv_map_location;
    private int mFlag = 0;
    private ImageView iv_search_result, imgDownArrow, iv_search_map;
    private LinearLayout dotLayout_detail_search_result2, ll_change_city;
    private Handler mHandler;
    private Runnable mRunnable;
    private ImageView[] mDotsText1;
    private Nearby mNearby = new Nearby();
    private int like;
    private Dialog dialog;
    private ArrayList<HourDetails> hourDetailses;
    private Handler handler = new Handler();

    private Handler handlerBeaconToast = new Handler();

    private Runnable runnable;

    private NormalTextView tv_similar_explore, txtSuggestPlace, txtStartNavigating, txt_snack_msg, tv_login_snack, tv_sign_up_snack, tv_snack_msg;

    private LinearLayout ll_login_snack, ll_sign_up_snack, llBeaconToast, llSearchResultToast;

    private Nearby nearByDetails;

    private JSONObjConverter jsonObjConverter;

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
    private ImageView imgSharePlace;
    private RelativeLayout rlVirtualTour;
    private PagerIndicator custom_indicator1;
    private SliderLayout sliderPlaceImages;
    private int PLace_LIKE_SIMILAR = 203;

    private void beaconsToast(final String msg, final String msgBeacon, final String img, final String placeId, final int isCloseApproach, final String isClosePromo) {
        if (msg != null && !msg.equals("")) {

            txt_snack_msg.setText(msg);
            txt_snack_msg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isClosePromo.equals("1")) {
                        Intent intent = new Intent(SearchResultFragmentActivity.this, BeaconsActivity.class);
                        intent.putExtra(PLACE_ID, placeId);
                        intent.putExtra(PLACE_IMAGE, img);
                        intent.putExtra(BEACON_MESSAGE, msgBeacon);
                        startActivity(intent);
                    } else {
                        Intent intent = new Intent(SearchResultFragmentActivity.this, SearchResultPlaceDetailsActivity.class);
                        intent.putExtra("placeId", placeId);
                        startActivityForResult(intent, 112);
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

    private void beaconsToast(final String msg, final String msgBeacon, final String img, final String placeId, boolean isClickable, final int isCloseApproach) {
        if (msg != null && !msg.equals("")) {

            txt_snack_msg.setText(msg);
            if (isClickable) {
                txt_snack_msg.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (isCloseApproach == 1) {
                            Intent intent = new Intent(SearchResultFragmentActivity.this, BeaconsActivity.class);
                            intent.putExtra(PLACE_ID, placeId);
                            intent.putExtra(PLACE_IMAGE, img);
                            intent.putExtra(BEACON_MESSAGE, msgBeacon);
                            startActivity(intent);
                        } else {
                            Intent intent = new Intent(SearchResultFragmentActivity.this, SearchResultPlaceDetailsActivity.class);
                            intent.putExtra("placeId", placeId);
                            startActivityForResult(intent, 112);
                        }


                    }
                });
            } else {
                txt_snack_msg.setOnClickListener(null);
            }

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
            e.printStackTrace();
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.search_result);

        mIntent = getIntent();
        nearbies = (ArrayList<Nearby>) mIntent.getSerializableExtra("nearbies");
        search = mIntent.getStringExtra("search");
        jsonObjConverter = new JSONObjConverter();
        _24HourSDF = new SimpleDateFormat("HH:mm");
        _12HourSDF = new SimpleDateFormat("hh:mma");
        try {
            DisplayMetrics displaymetrics = new DisplayMetrics();
            this.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
            height = displaymetrics.heightPixels;
            width = displaymetrics.widthPixels;
        } catch (Exception e) {
            e.printStackTrace();
        }

        mPreferences = getSharedPreferences(Constants.mPref, 0);
        mEditor = mPreferences.edit();

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this)
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .denyCacheImageMultipleSizesInMemory()
                .diskCacheFileNameGenerator(new Md5FileNameGenerator())
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                .build();

        imageLoader.init(ImageLoaderConfiguration.createDefault(this));
        imageLoader2.init(ImageLoaderConfiguration.createDefault(this));
        options = new DisplayImageOptions.Builder()
                .showImageForEmptyUri(null)
                .showImageOnFail(null).cacheInMemory(true)
                .cacheOnDisk(true).build();
        options2 = new DisplayImageOptions.Builder()
                .showImageForEmptyUri(null)
                .displayer(new RoundedBitmapDisplayer(10))
                .showImageOnFail(null).cacheInMemory(true)
                .cacheOnDisk(true).build();
        optionsSimple = new DisplayImageOptions.Builder()
                .showImageForEmptyUri(null)
                .displayer(new SimpleBitmapDisplayer())
                .showImageOnFail(null).cacheInMemory(true)
                .cacheOnDisk(true).build();
        ImageLoader.getInstance().init(config);
        initialisation();

        if (nearbies.size() > 0) {
            fab_search_result.setVisibility(View.VISIBLE);
        } else {
            rv_search_result.setVisibility(View.GONE);
            llEmptyLayout11.setVisibility(View.VISIBLE);
            noplace_found_relative.setVisibility(View.VISIBLE);
            SnackbarManager.show(Snackbar.with(SearchResultFragmentActivity.this).color(getResources().getColor(R.color.mBlue)).text(Constants.showMessage(SearchResultFragmentActivity.this, mPreferences.getString("Lan_Id", ""), "NORECORD")));
        }
    }

    private void initialisation() {
        imgSharePlace = (ImageView) findViewById(R.id.imgSharePlace);
        imgSharePlace.setOnClickListener(this);
        noplace_found_relative = (RelativeLayout) findViewById(R.id.noplace_found_relative);

        rlVirtualTour = (RelativeLayout) findViewById(R.id.rlVirtualTour);
        rlVirtualTour.setOnClickListener(this);
        custom_indicator1 = (PagerIndicator) findViewById(R.id.custom_indicator1);
        custom_indicator1.setIndicatorStyleResource(R.drawable.shape_cirlce_fill, R.drawable.shape_cirlce_unfill);
        llBeaconToast = (LinearLayout) findViewById(R.id.llBeaconToast);
        txt_snack_msg = (NormalTextView) findViewById(R.id.txt_snack_msg);

        llSearchResultToast = (LinearLayout) findViewById(R.id.llSearchResultToast);
        ll_login_snack = (LinearLayout) findViewById(R.id.ll_login_snack);
        ll_sign_up_snack = (LinearLayout) findViewById(R.id.ll_sign_up_snack);
        tv_login_snack = (NormalTextView) findViewById(R.id.tv_login_snack);
        tv_sign_up_snack = (NormalTextView) findViewById(R.id.tv_sign_up_snack);
        tv_snack_msg = (NormalTextView) findViewById(R.id.tv_snack_msg);
        ll_sign_up_snack.setOnClickListener(this);
        ll_login_snack.setOnClickListener(this);

        txtSuggestPlace = (NormalTextView) findViewById(R.id.txtSuggestPlace);
        txtSuggestPlace.setText(Constants.showMessage(SearchResultFragmentActivity.this, mPreferences.getString("Lan_Id", ""), "Suggest Place"));
        txtSuggestPlace.setOnClickListener(this);

        tv_fee_search_result = (NormalTextView) findViewById(R.id.tv_fee_explore);
        tv_fee_search_result.setText(Constants.showMessage(SearchResultFragmentActivity.this, mPreferences.getString("Lan_Id", ""), "Fee"));

        txtStartNavigating = (NormalTextView) findViewById(R.id.txtStartNavigating);
        txtStartNavigating.setText(Constants.showMessage(SearchResultFragmentActivity.this, mPreferences.getString("Lan_Id", ""), "StartNavigation"));

        tv_about_place_search_result = (NormalTextView) findViewById(R.id.tv_about_place_explore);
        tv_about_place_search_result.setText(Constants.showMessage(SearchResultFragmentActivity.this, mPreferences.getString("Lan_Id", ""), "aboutplace"));

        tv_similar_search = (NormalTextView) findViewById(R.id.tv_similar_explore);
        tv_similar_search.setText(Constants.showMessage(SearchResultFragmentActivity.this, mPreferences.getString("Lan_Id", ""), "similarplace"));

        tv_see_all_search_result = (NormalTextView) findViewById(R.id.tv_see_all_explore);
        tv_see_all_search_result.setText(Constants.showMessage(SearchResultFragmentActivity.this, mPreferences.getString("Lan_Id", ""), "seeall"));

        tv_your_location_header5 = (NormalTextView) findViewById(R.id.tv_your_location_header5);
        tv_your_location_search_result = (NormalTextView) findViewById(R.id.tv_your_location_search_result);
        tv_your_location_search_result.setText(Constants.showMessage(SearchResultFragmentActivity.this, mPreferences.getString("Lan_Id", ""), "locationtitle"));

        tv_search_result = (NormalTextView) findViewById(R.id.tv_search_result);
        tv_search_result.setText(Constants.showMessage(SearchResultFragmentActivity.this, mPreferences.getString("Lan_Id", ""), "searchresuilt"));
        txtMessage = (NormalTextView) findViewById(R.id.txtMessage);

        txtMessage.setText(Constants.showMessage(SearchResultFragmentActivity.this, mPreferences.getString("Lan_Id", ""), "No records available for this search"));

        ll_change_city = (LinearLayout) findViewById(R.id.ll_change_city);
        ll_change_city.setOnClickListener(this);

        dotLayout_detail_search_result2 = (LinearLayout) findViewById(R.id.dotLayout_detail);
//        iv_location_search_result2 = (ImageView) findViewById(R.id.iv_location);

        iv_search_result = (ImageView) findViewById(R.id.iv_search_result);
        iv_search_result.setOnClickListener(this);
        iv_search_map = (ImageView) findViewById(R.id.iv_search_map);
        iv_search_map.setOnClickListener(this);

        imgDownArrow = (ImageView) findViewById(R.id.imgDownArrow);
        ImageView iv_down_header5 = (ImageView) findViewById(R.id.iv_down_header5);
        iv_down_header5.setVisibility(View.GONE);
        tv_see_all_search_result.setOnClickListener(this);

        fab_search_result = (FloatingActionButton) findViewById(R.id.fab_search_result);
        fab_search_result.setOnClickListener(this);

        ll_search_result = (LinearLayout) findViewById(R.id.ll_search_result);
        llEmptyLayout11 = (LinearLayout) findViewById(R.id.llEmptyLayout11);
        ll_search_result1 = (LinearLayout) findViewById(R.id.ll_search_result1);
        ll_search_result2 = (LinearLayout) findViewById(R.id.ll_search_result2);

        tv_place = (NormalTextView) findViewById(R.id.tv_place);
        tv_place.setOnClickListener(this);
        tv_map_location = (NormalTextView) findViewById(R.id.tv_map_location);

        if (search.equalsIgnoreCase("Similar Places")) {
            // tv_place.setPadding(0, 10, 0, 0);
            tv_place.setText(search);
            tv_place.setAllCaps(true);
            ll_search_result.setVisibility(View.GONE);
            imgDownArrow.setVisibility(View.GONE);
            tv_your_location_search_result.setVisibility(View.GONE);
            // tv_map_location.setVisibility(View.GONE);
//            tv_map_location.setText(mPreferences.getString(Preference.Pref_City, ""));
            if (mPreferences.getString(Preference.Pref_City, "").equalsIgnoreCase("")) {
                tv_map_location.setText(mPreferences.getString(Preference.Pref_Country, ""));
                tv_your_location_header5.setText(Constants.showMessage(SearchResultFragmentActivity.this, mPreferences.getString("Lan_Id", ""), "locationtitle"));
            } else {
                tv_map_location.setText(mPreferences.getString(Preference.Pref_City, ""));
                tv_your_location_header5.setText(Constants.showMessage(SearchResultFragmentActivity.this, mPreferences.getString("Lan_Id", ""), "locationtitle"));
            }
        } else {
            tv_your_location_search_result.setVisibility(View.VISIBLE);

            tv_place.setAllCaps(false);
            if (search.length() != 0) {
                tv_place.setText("\"" + search + "\"");

//                tv_your_location_search_result.setText(mPreferences.getString(Preference.Pref_City, ""));

            } else {
                //tv_place.setText(mPreferences.getString(Preference.Pref_City, ""));
                tv_place.setText("");
            }
            if (mPreferences.getString(Preference.Pref_City, "").equalsIgnoreCase("")) {
                // tv_your_location_search_result.setText(mPreferences.getString(Preference.Pref_Country, ""));
                tv_your_location_header5.setText(mPreferences.getString(Preference.Pref_Country, ""));
            } else {
                tv_your_location_header5.setText(mPreferences.getString(Preference.Pref_City, ""));
            }
            if (search.length() != 0) {
                tv_map_location.setText("\"" + search + "\"");
//                tv_map_location.setText(Constants.showMessage(SearchResultFragmentActivity.this, mPreferences.getString("Lan_Id", ""), "searchresuilt") + "\"" + search + "\"");
                // tv_your_location_search_result.setText(mPreferences.getString(Preference.Pref_City, ""));
            } else {
                tv_map_location.setText("");
//                tv_map_location.setText(mPreferences.getString(Preference.Pref_City, ""));
            }
        }


        iv_back_search_result = (ImageView) findViewById(R.id.iv_back_search_result);
        iv_back_search_result.setOnClickListener(this);

        rv_search_result = (RecyclerView) findViewById(R.id.rv_search_result);
        rv_search_result.setHasFixedSize(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(SearchResultFragmentActivity.this);
        rv_search_result.setLayoutManager(layoutManager);

        recyclerAdapter = new RecyclerAdapter(this);
        rv_search_result.setAdapter(recyclerAdapter);

        tv_full_name_search_result2 = (NormalBoldTextView) findViewById(R.id.tv_full_name);
        tv_info_search_result2 = (NormalTextView) findViewById(R.id.tv_info);
        txtOpenNowVal = (NormalTextView) findViewById(R.id.txtOpenNowVal);
        txtDailyWorkingHours = (NormalTextView) findViewById(R.id.txtDailyWorkingHours);
        txtDailyWorkingHours.setText(Constants.showMessage(this, mPreferences.getString("Lan_Id", ""), "View Daily Working Hours"));
        txtDailyWorkingHours.setOnClickListener(this);

        tv_distance1_search_result2 = (NormalTextView) findViewById(R.id.tv_distance1);
        tv_discription_search_result2 = (NormalTextView) findViewById(R.id.tv_discription);
        gv_detail1_search_result2 = (com.ftl.tourisma.gallery1.Gallery) findViewById(R.id.gv_detail1);
        iv_back5 = (ImageView) findViewById(R.id.iv_back5);
        txt_add_to_fav = (NormalTextView) findViewById(R.id.txt_add_to_fav);

        listView_fees = (LinearLayout) findViewById(R.id.listView_fees);


    }

    @Override
    public void onClick(View v) {
        if (v == iv_back_search_result) {
            finish();
        } else if (v == ll_change_city) {
            Intent mIntent = new Intent(this, SelectLocationFragmentActivity.class);
//            mIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(mIntent);
            // this.finish();
        } else if (v == iv_back5) {
            ll_search_result1.setVisibility(View.VISIBLE);
            ll_search_result2.setVisibility(View.GONE);
            // mHandler.removeCallbacks(mRunnable);
        } else if (v == fab_search_result) {
            Intent mIntent = new Intent(SearchResultFragmentActivity.this, MapDetailFragment.class);
            mIntent.putExtra("nearbies", nearbies);
            mIntent.putExtra("title", search);
            startActivityForResult(mIntent, PLace_LIKE_SIMILAR);
//            ll_search_result1.setVisibility(View.GONE);
//            ll_search_result3.setVisibility(View.VISIBLE);
//            setMapPin();
//        }
//        else if (v == iv_back_search_result3 | v == fab_list_search_result3) {
//            ll_search_result1.setVisibility(View.VISIBLE);
//            ll_search_result3.setVisibility(View.GONE);
//        }
//        else if (v == tv_close_map) {
//            ll_map_detail.setVisibility(View.GONE);
        } else if (v == iv_search_result || v == iv_search_map) {
            Intent mIntent = new Intent(this, SearchActivity.class);
            startActivity(mIntent);
            //finish();
        } else if (v == tv_see_all_search_result) {
            Intent mIntent = new Intent(SearchResultFragmentActivity.this, SearchResultFragmentActivity.class);
            mIntent.putExtra("nearbies", nearbies1);
            mIntent.putExtra("search", "Similar Places");
            startActivity(mIntent);
        } else if (v == txtSuggestPlace) {
            suggestPlace();
        } else if (v == txtDailyWorkingHours) {
            openWeekDaysPopup();
        } else if (v == rlVirtualTour) {
            Intent intent = new Intent(SearchResultFragmentActivity.this, SimpleVrPanoramaActivity.class);
            intent.putExtra("path", mNearby.getPlaceVRMainImage());
            intent.putExtra("path_1", mNearby.getVrimages());
            startActivity(intent);
        } else if (v == imgSharePlace) {
            Intent mIntent = new Intent(SearchResultFragmentActivity.this, ShareFragmentActivity.class);
            String share1 = Constants.showMessage(SearchResultFragmentActivity.this, mPreferences.getString("Lan_Id", ""), "share1");
            String share2 = Constants.showMessage(SearchResultFragmentActivity.this, mPreferences.getString("Lan_Id", ""), "share2");
            String share3 = Constants.showMessage(SearchResultFragmentActivity.this, mPreferences.getString("Lan_Id", ""), "share3");

            mIntent.putExtra("myMsg", share1 + " \"" + mPreferences.getString("User_Name", "") + "\" " + share2 + " \"" + mNearby.getPlace_Name() + "\" " + share3);
            startActivity(mIntent);

        } else if (v == ll_login_snack) {
            mPreferences.edit().putString("User_id", "").commit();
            Intent mIntent = new Intent(SearchResultFragmentActivity.this, LoginFragmentActivity.class);
            startActivity(mIntent);
            finish();
        } else if (v == ll_sign_up_snack) {
            mPreferences.edit().putString("User_id", "").commit();
            Intent mIntent = new Intent(SearchResultFragmentActivity.this, SignupFragmentActivity.class);
            startActivity(mIntent);
            finish();
        }
    }

    public void suggestPlace() {
        Intent sendIntent = new Intent(Intent.ACTION_VIEW);
        sendIntent.setType("text/plain");
        sendIntent.setData(Uri.parse("info@mytourisma.com"));
//        sendIntent.setClassName("com.google.android.gm", "com.google.android.gm.ComposeActivityGmail");
        sendIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{"info@mytourisma.com"});
        sendIntent.putExtra(Intent.EXTRA_SUBJECT, "myTourisma - Suggest new location");
        sendIntent.putExtra(Intent.EXTRA_TEXT, "Hello,\n" +
                "\n" +
                "\n" +
                "I would like to suggest a new location for myTourisma app\n" +
                "\n" +
                "Location name:\n" +
                "Location:\n" +
                "\n" +
                "\n" +
                "\n" +
                "Thank you");
        final PackageManager pm = getPackageManager();
        final List<ResolveInfo> matches = pm.queryIntentActivities(sendIntent, 0);
        ResolveInfo best = null;
        for (final ResolveInfo info : matches)
            if (info.activityInfo.packageName.endsWith(".gm") || info.activityInfo.name.toLowerCase().contains("gmail"))
                best = info;
        if (best != null)
            sendIntent.setClassName(best.activityInfo.packageName, best.activityInfo.name);

        startActivity(sendIntent);
    }

    @Override
    public void onResponse(String response, String action) {
        try {
            if (action.equalsIgnoreCase("PlaceDetails")) {
                searchResponse(response);
            } else if (action.equalsIgnoreCase("AddFavorite")) {
                setResult(RESULT_OK, new Intent());
                addFavoriteResponse(response);
            } else if (action.equalsIgnoreCase("DeleteFavorite")) {
                setResult(RESULT_OK, new Intent());
                deleteFavoriteResponse(response);
            }
        } catch (Exception e) {
            Log.e(TAG, "onResponse Exception " + e.getLocalizedMessage());
        }
    }

    public String GetRoutDistane(NormalTextView txtDistance, double startLat, double startLong, double endLat, double endLong, String distApi) {
        String Distance = "error";
        String Status = "error";
        try {
            Log.e("Distance Link : ", "http://maps.googleapis.com/maps/api/directions/json?origin=" + startLat + "," + startLong + "&destination=" + endLat + "," + endLong + "&sensor=false");

            new DistanceAsync(txtDistance,distApi).execute("http://maps.googleapis.com/maps/api/directions/json?origin=" + startLat + "," + startLong + "&destination=" + endLat + "," + endLong + "&sensor=false");
/*
            JSONObject jsonObj =  Utilities.getJSONfromURL("http://maps.googleapis.com/maps/api/directions/json?origin=" + startLat + "," + startLong + "&destination=" + endLat + "," + endLong + "&sensor=false");
            Status = jsonObj.getString("status");
            if (Status.equalsIgnoreCase("OK")) {
                JSONArray routes = jsonObj.getJSONArray("routes");
                JSONObject zero = routes.getJSONObject(0);
                JSONArray legs = zero.getJSONArray("legs");
                JSONObject zero2 = legs.getJSONObject(0);
                JSONObject dist = zero2.getJSONObject("distance");
                Distance = dist.getString("text").replace("km","").replace(" ","");
            } else {
                Distance = distApi;
            }*/
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return Distance;


    }

    public void setDetailInfo(final Nearby nearby) {

        hourDetailses = new ArrayList<>();
        int dayFoundStatus = 0; //0 -> no day found 1-> found but closed - 2-> open but time not found 3 -> done
        if (nearby.getHourDetailsArrayList() != null && nearby.getHourDetailsArrayList().size() > 0) {
            hourDetailses = nearby.getHourDetailsArrayList();
            for (HourDetails hourDetails : nearby.getHourDetailsArrayList()) {
                if (hourDetails.getPOHKey().equals(Utils.getCurrentDay())) {
                    //Checking for if yeasterday's timing is open for place
                    for (HourDetails hourDetailsYesterday : nearby.getHourDetailsArrayList()) {
                        if (hourDetailsYesterday.getPOHDay().equalsIgnoreCase(Utils.getYesterDayDay())) {
                            if (hourDetailsYesterday.getPOHIsOpen().equals(PlaceOpenWithAnyTime)) {
                                SpannableStringBuilder spannableStringBuilder = TimingFunction.checkYesterDayTiming(this, hourDetails, hourDetailsYesterday, mPreferences);
                                if (spannableStringBuilder != null)
                                    dayFoundStatus = 2;
                            }
                            break;
                        }
                    }

                    SpannableStringBuilder time = TimingFunction.getPlaceTiming(this, hourDetails, mPreferences);
                    if (hourDetails.getPOHIsOpen().equals(PlaceOpenWithAnyTime)) {
                        if (time != null) {
                            dayFoundStatus = 3;
                            txtOpenNowVal.setText(time);
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
            txtOpenNowVal.setText(Utils.getSpannableString(Constants.showMessage(this, mPreferences.getString("Lan_Id", ""), "Open Now"), Utils.getColor(this, R.color.mGreen), true, 0));

            // txtOpenNowVal.setText("");
        } else if (dayFoundStatus == 0 || dayFoundStatus == 1) {

            txtOpenNowVal.setText(Utils.getSpannableString(Constants.showMessage(this, mPreferences.getString("Lan_Id", ""), "Closed"), Utils.getColor(this, android.R.color.holo_red_dark), true, 0));
//                    txtOpenNowVal.setText("");
        }


        final int mId;

        mId = id1;
        if (nearby.getFav_Id().equalsIgnoreCase("0")) {
            txt_add_to_fav.setCompoundDrawablesWithIntrinsicBounds(Utilities.getDrawable(this, R.drawable.ic_favourite_default), null, null, null);
            txt_add_to_fav.setText(Constants.showMessage(SearchResultFragmentActivity.this, mPreferences.getString("Lan_Id", ""), "Add to Favourites"));
            like = 0;
        } else {
            txt_add_to_fav.setText(Constants.showMessage(SearchResultFragmentActivity.this, mPreferences.getString("Lan_Id", ""), "Remove Favourite"));

            txt_add_to_fav.setCompoundDrawablesWithIntrinsicBounds(Utilities.getDrawable(this, R.drawable.ic_favourite_active), null, null, null);

            like = 1;
        }

        txt_add_to_fav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPreferences.getString("User_Id", "").equalsIgnoreCase("0")) {
                    guestSnackToast();
                } else {
                    if (like == 0) {
                        txt_add_to_fav.setText(Constants.showMessage(SearchResultFragmentActivity.this, mPreferences.getString("Lan_Id", ""), "Remove Favourite"));

                        txt_add_to_fav.setCompoundDrawablesWithIntrinsicBounds(Utilities.getDrawable(SearchResultFragmentActivity.this, R.drawable.ic_favourite_active), null, null, null);

                        addFavoriteCall(nearby.getPlace_Id());
                        mFlag = mId;
                    } else if (like == 1) {
                        txt_add_to_fav.setText(Constants.showMessage(SearchResultFragmentActivity.this, mPreferences.getString("Lan_Id", ""), "Add to Favourites"));
                        txt_add_to_fav.setCompoundDrawablesWithIntrinsicBounds(Utilities.getDrawable(SearchResultFragmentActivity.this, R.drawable.ic_favourite_default), null, null, null);

                        deleteFavoriteCall(nearby.getFav_Id());
                        mFlag = mId;
                    }
                    mCounter = 1;
                }
            }
        });

        tv_distance1_search_result2.setText(nearby.getDist() + Constants.showMessage(SearchResultFragmentActivity.this, mPreferences.getString("Lan_Id", ""), "KM"));
        GetRoutDistane(tv_distance1_search_result2,Double.parseDouble(mPreferences.getString("latitude2", "")), Double.parseDouble(mPreferences.getString("longitude2", "")), Double.parseDouble(nearby.getPlace_Latitude()), Double.parseDouble(nearby.getPlace_Longi()),nearby.getDist());
        //  txtOpenNow.setText(Constants.showMessage(SearchResultFragmentActivity.this, mPreferences.getString("Lan_Id", ""), "Timing") + "(" + nearby.getPlace_Close_Note() + ")");

        tv_full_name_search_result2.setText(nearby.getPlace_Name());
        tv_info_search_result2.setText(nearby.getPlace_Address());
        tv_discription_search_result2.setText(Html.fromHtml(Html.fromHtml("<html><body><p style=\"text-align:justify\">" + nearby.getPlace_Description() + "</p></body></html>").toString()));
        iv_back5.setOnClickListener(this);
        String string = nearby.getPlace_MainImage();
        if (nearby.getOtherimages().length() != 0 && !nearby.getOtherimages().equalsIgnoreCase("null")) {
            string += "," + nearby.getOtherimages();
        }
        listView_fees.setVisibility(View.VISIBLE);
        boolean isCurrentDayFound = false;
        if (nearby.getHourDetailsArrayList() != null && nearby.getHourDetailsArrayList().size() > 0) {
            addFeesCustomViews(nearby.getHourDetailsArrayList().get(0).getPOHCharges(), nearby.getPrice_Description());

        } else {
            addFeesCustomViews("-", nearby.getPrice_Description());
        }


//        Log.d("System out", "string " + string);

        strImg1 = string.split(",");
        if (strImg1.length >= 0) {
            sliderPlaceImages = (SliderLayout) findViewById(R.id.sliderPlaceImages);
            sliderPlaceImages.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (height * 60) / 100));
            if (strImg1.length > 1) {
                sliderPlaceImages.setDuration(4000);
            } else {
                sliderPlaceImages.stopAutoCycle();
                sliderPlaceImages.setPagerTransformer(false, new BaseTransformer() {
                    @Override
                    protected void onTransform(View view, float v) {
                    }
                });
            }
            sliderPlaceImages.removeAllSliders();
            for (String imgName : strImg1) {
                String imageUrl = Constants.IMAGE_URL + imgName + "&w=" + (width);
                DefaultSliderView textSliderView = new DefaultSliderView(this);
                // initialize a SliderLayout
                textSliderView
                        .description(mNearby.getPlace_Name())
                        .image(imageUrl)
                        .setScaleType(BaseSliderView.ScaleType.Fit)
                        .setOnSliderClickListener(new BaseSliderView.OnSliderClickListener() {
                            @Override
                            public void onSliderClick(BaseSliderView slider1) {
                                Intent intent = new Intent(SearchResultFragmentActivity.this, FullPlaceImageViewActivity.class);
                                intent.putExtra("nearBy_id", mNearby.getPlace_Id());
                                intent.putExtra("nearBy_Fav_id", mNearby.getFav_Id());
//                    intent.putExtra("nearBy_Fav_id",nearby.getFav_Id());
                                intent.putExtra("nearBy_name", mNearby.getPlace_Name());
                                intent.putExtra("nearBy_longi", mNearby.getPlace_Longi());
                                intent.putExtra("nearBy_lati", mNearby.getPlace_Latitude());
                                intent.putExtra("nearBy_main_image", mNearby.getPlace_MainImage());
                                intent.putExtra("nearBy_other_images", mNearby.getOtherimages());
                                nearByDetails = mNearby;
                                startActivityForResult(intent, PLACE_LIKE_SEARCH);
                            }
                        });

                sliderPlaceImages.addSlider(textSliderView);
            }
            sliderPlaceImages.setCustomIndicator(custom_indicator1);
//            sliderPlaceImages.addOnPageChangeListener(this);
            rlVirtualTour.setVisibility((mNearby.getPlaceVRMainImage() != null && !mNearby.getPlaceVRMainImage().equals("")) ? View.VISIBLE : View.GONE);


        }
        searchCall(nearby.getPlace_Id());

        txtStartNavigating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Log.d("System out", "placeName " + nearby.getPlace_Name());
//                Log.d("System out", "dist " + nearby.getDist());
//                Log.d("System out", "latitude " + nearby.getPlace_Latitude());
//                Log.d("System out", "longitude " + nearby.getPlace_Longi());

/*
                Intent mIntent = new Intent(SearchResultFragmentActivity.this, MapLocationFragment.class);
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
/*
        iv_location_search_result2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Log.d("System out", "placeName " + nearby.getPlace_Name());
//                Log.d("System out", "dist " + nearby.getDist());
//                Log.d("System out", "latitude " + nearby.getPlace_Latitude());
//                Log.d("System out", "longitude " + nearby.getPlace_Longi());

                Intent mIntent = new Intent(SearchResultFragmentActivity.this, MapLocationFragment.class);
                mIntent.putExtra("placeName", nearby.getPlace_Name());
                mIntent.putExtra("dist", nearby.getDist());
                mIntent.putExtra("latitude", nearby.getPlace_Latitude());
                mIntent.putExtra("longitude", nearby.getPlace_Longi());
                mIntent.putExtra("mDirection", "yes");
                mIntent.putExtra("address", nearby.getPlace_Address());
                startActivity(mIntent);
            }
        });*/
    }

    private void addFeesCustomViews(String pohCharges, String price_description) {
        listView_fees.removeAllViews();
        if (price_description == null || price_description.equals("") || price_description.equalsIgnoreCase("null") || price_description.trim().length() == 0) {


            price_description = Constants.showMessage(SearchResultFragmentActivity.this, mPreferences.getString("Lan_Id", ""), "No Fees data available");
        }
        LayoutInflater inflater = (LayoutInflater) SearchResultFragmentActivity.this.getSystemService(LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.row_fees, null);
        NormalTextView txtFees = (NormalTextView) view.findViewById(R.id.txtFees);
        txtFees.setText("");
//            price_description=price_description.replace("//","/");getSpannableString(price_description, false);
//            price_description=price_description.replace("//","/")
        txtFees.append(getSpannableString(price_description, false));
        listView_fees.addView(view);

    }

    private void addFeesCustomViews(ArrayList<FeesDetails> feesDetailsArrayList) {
//        if (convertView == null) {
        listView_fees.removeAllViews();
        if (feesDetailsArrayList.size() == 0) {
            LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.row_fees, null);
            NormalTextView txtFees = (NormalTextView) view.findViewById(R.id.txtFees);
            txtFees.setText("");
            txtFees.append(getSpannableString(Constants.showMessage(this, mPreferences.getString("Lan_Id", ""), "No Fees data available"), true));
            listView_fees.addView(view);
            return;
        }
        for (FeesDetails feesDetails : feesDetailsArrayList) {
            LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.row_fees, null);
            NormalTextView txtFees = (NormalTextView) view.findViewById(R.id.txtFees);
            txtFees.setText("");
            txtFees.append(getSpannableString(feesDetails.getFeesName() + ": ", true));
            txtFees.append(getSpannableString(feesDetails.getFeesValue() + "", false));
            listView_fees.addView(view);
//        }
        }
    }

    private SpannableStringBuilder getSpannableString(String s, boolean isBold) {
//        Typeface font = Typeface.createFromAsset(getAssets(), "Akshar.ttf");
        SpannableStringBuilder spannablecontent = new SpannableStringBuilder(s);
        if (isBold)
            spannablecontent.setSpan(new CustomTypefaceSpan("", MyTorismaApplication.typeFace.getBoldFonts()),
                    0, spannablecontent.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        else
            spannablecontent.setSpan(new CustomTypefaceSpan("", MyTorismaApplication.typeFace.getNormalFonts()),
                    0, spannablecontent.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        return spannablecontent;
    }

    private void searchCall(String Place_Id) {
        if (CommonClass.hasInternetConnection(SearchResultFragmentActivity.this)) {
            String url = Constants.SERVER_URL + "json.php?action=PlaceDetails";
            String json = "[{\"Lan_Id\":\"" + mPreferences.getString("Lan_Id", "") + "\",\"User_Id\":\"" + mPreferences.getString("User_Id", "") + "\",\"Current_Latitude\":\"" + mPreferences.getString("latitude2", "") + "\",\"Current_Longitude\":\"" + mPreferences.getString("longitude2", "") + "\",\"Place_Id\":\"" + Place_Id + "\"}]";
//            Log.d("System out", "PlaceDetails " + json);
//            new PostSync(this, "PlaceDetails").execute(url, json);
            new PostSync(this, "PlaceDetails", SearchResultFragmentActivity.this).execute(url, json);
        } else {
            Intent intent = new Intent(getApplicationContext(), NoInternet.class);
            startActivity(intent);
            //SnackbarManager.show(Snackbar.with(SearchResultFragmentActivity.this).color(getResources().getColor(R.color.mBlue)).text(Constants.showMessage(SearchResultFragmentActivity.this, mPreferences.getString("Lan_Id", ""), "NOINTERNET")));
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PLACE_LIKE_SEARCH) {
            if (data != null) {
                nearByDetails.setFav_Id(data.getStringExtra("DATA"));
                setDetailInfo(nearByDetails);
            }
        }
        if (requestCode == PLace_LIKE_SIMILAR) {
            if (resultCode == RESULT_OK) {
                if (data != null) {
                    nearbies = (ArrayList<Nearby>) data.getSerializableExtra("nearbies");
                    recyclerAdapter.notifyDataSetChanged();
                }
            }
            if (data != null) {
                //  nearByDetails.setFav_Id(data.getStringExtra("DATA"));
                //   setDetailInfo(nearByDetails);
            }
        }
        if (requestCode == 112) {
            if (resultCode == RESULT_OK) {
            }
        }
    }

    public void searchResponse(String resultString) {
//        Log.d("System out", resultString);

        try {
            JSONArray jsonArray = new JSONArray(resultString);
            JSONObject jsonObject = jsonArray.optJSONObject(0);
            if (jsonObject.has("similar")) {
                nearbies1.clear();
                JSONArray jsonArray1 = jsonObject.optJSONArray("similar");

                for (int i = 0; i < jsonArray1.length(); i++) {
                    Nearby nearby = jsonObjConverter.convertJsonToNearByObj(jsonArray1.optJSONObject(i));
                    if (!mNearby.getPlace_Id().equals(nearby.getPlace_Id())) {
                        nearbies1.add(nearby);
                    }
                }
            }


            galleryAdapter2 = new GalleryAdapter2(SearchResultFragmentActivity.this);
            gv_detail1_search_result2.setAdapter(null);
            gv_detail1_search_result2.setAdapter(galleryAdapter2);

            if (nearbies1.size() == 0) {
                tv_similar_explore.setVisibility(View.GONE);
            } else {
                tv_similar_explore.setVisibility(View.VISIBLE);
            }

        } catch (JSONException e) {
            e.printStackTrace();
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

    public ArrayList<FeesDetails> getFeesObject(JSONObject jsonObject) {
        JSONArray feesDetails = null;
        ArrayList<FeesDetails> feesArrayList = new ArrayList<>();
        try {
            feesDetails = jsonObject.getJSONArray("Fees_Details");
            if (feesDetails != null) {

                for (int k = 0; k < feesDetails.length(); k++) {

                    JSONObject jobjFees = feesDetails.getJSONObject(k);
                    FeesDetails objFees = new FeesDetails();
                    objFees.setFeesName(jobjFees.getString("Fee_Name"));
                    objFees.setFeesValue(jobjFees.getString("Fee_Value"));
                    feesArrayList.add(objFees);
                }
            }
            // hoursOfOperation.setFeesDetailses(feesArrayList);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return feesArrayList;
    }

    private void addFavoriteCall(String Place_Id) {
        if (CommonClass.hasInternetConnection(this)) {
            String url = Constants.SERVER_URL + "json.php?action=AddFavorite";
            String json = "[{\"User_Id\":\"" + mPreferences.getString("User_Id", "") + "\",\"Place_Id\":\"" + Place_Id + "\"}]";
//            Log.d("System out", "AddFavorite " + json);
            new PostSync(SearchResultFragmentActivity.this, "AddFavorite", SearchResultFragmentActivity.this).execute(url, json);
        } else {
            Intent intent = new Intent(getApplicationContext(), NoInternet.class);
            startActivity(intent);
            //SnackbarManager.show((Snackbar) Snackbar.with(SearchResultFragmentActivity.this).color(getResources().getColor(R.color.mBlue)).text(Constants.showMessage(SearchResultFragmentActivity.this, mPreferences.getString("Lan_Id", ""), "NOINTERNET")));
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
                        nearbies.get(mFlag).setFav_Id(jsonObject.optString("Fav_Id"));
                        mFlag = 0;
                        like = 1;
                        if (recyclerAdapter != null)
                            recyclerAdapter.notifyDataSetChanged();
                        Constants.mStaticFavCall = 0;
                        SnackbarManager.show(Snackbar.with(this).color(getResources().getColor(R.color.mBlue)).text(Constants.showMessage(SearchResultFragmentActivity.this
                                , mPreferences.getString("Lan_Id", ""), "AddFavourite")));
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
            new PostSync(SearchResultFragmentActivity.this, "DeleteFavorite", SearchResultFragmentActivity.this).execute(url, json);
        } else {
            Intent intent = new Intent(getApplicationContext(), NoInternet.class);
            startActivity(intent);
            //SnackbarManager.show((Snackbar) Snackbar.with(SearchResultFragmentActivity.this).color(getResources().getColor(R.color.mBlue)).text(Constants.showMessage(SearchResultFragmentActivity.this, mPreferences.getString("Lan_Id", ""), "NOINTERNET")));
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
                        nearbies.get(mFlag).setFav_Id("0");
                        if (recyclerAdapter != null)
                            recyclerAdapter.notifyDataSetChanged();
                        mFlag = 0;
                        like = 0;
                        Constants.mStaticFavCall = 0;
                        SnackbarManager.show(Snackbar.with(this).color(getResources().getColor(R.color.mBlue)).text(Constants.showMessage(SearchResultFragmentActivity.this, mPreferences.getString("Lan_Id", ""), "Removefavorite")));
                    }
                }
            } catch (JSONException e) {

            }
        }
    }

    private void guestSnackToast() {

        tv_login_snack.setText(Constants.showMessage(SearchResultFragmentActivity.this, mPreferences.getString("Lan_Id", ""), "Login"));
        tv_sign_up_snack.setText(Constants.showMessage(SearchResultFragmentActivity.this, mPreferences.getString("Lan_Id", ""), "SignUp"));
        tv_snack_msg.setText(Constants.showMessage(SearchResultFragmentActivity.this, mPreferences.getString("Lan_Id", ""), "GetStarted"));

        runnable = new Runnable() {
            @Override
            public void run() {
                llSearchResultToast.setVisibility(View.GONE);
            }
        };
        llSearchResultToast.setVisibility(View.VISIBLE);
        handler.postDelayed(runnable, 4000);
    }

    class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {
        public ViewHolder viewHolder;
        private Activity activity;
        private int lastPostion = -1;

        public RecyclerAdapter(Activity activity) {
            this.activity = activity;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            LayoutInflater inflater = activity.getLayoutInflater();
            View view = inflater.inflate(R.layout.search_result_adapter, viewGroup, false);
            viewHolder = new ViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(final ViewHolder viewHolder, final int position) {
            viewHolder.txtShare.setText(Constants.showMessage(SearchResultFragmentActivity.this, mPreferences.getString("Lan_Id", ""), "Share"));
            viewHolder.txtFav.setText(Constants.showMessage(SearchResultFragmentActivity.this, mPreferences.getString("Lan_Id", ""), "Favourite"));
            String imageURL = Constants.IMAGE_URL + nearbies.get(position).getPlace_MainImage() + "&w=" + (width - 30);
//            Log.d("System out", imageURL);
            viewHolder.iv_nearby_explorer.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, (height * 60) / 100));
//            imageLoader.displayImage(imageURL, viewHolder.iv_nearby_explorer, options);
            Picasso.with(SearchResultFragmentActivity.this).load(imageURL).
                    resize(width, (height * 60) / 100)
                    .into(viewHolder.iv_nearby_explorer);
            viewHolder.tv_near.setText(nearbies.get(position).getPlace_Name());
            viewHolder.txtCategory.setText(Constants.showMessage(SearchResultFragmentActivity.this, mPreferences.getString("Lan_Id", ""), "Category") + ": " + nearbies.get(position).getCategory_Name());

            viewHolder.llView.setId(position);
            viewHolder.llView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ll_search_result1.setVisibility(View.GONE);
                    ll_search_result2.setVisibility(View.VISIBLE);
                    id1 = v.getId();
                    if (mHandler != null) {
                        mHandler.removeCallbacks(mRunnable);
                    }
                    setDetailInfo(nearbies.get(id1));
                    mNearby = nearbies.get(v.getId());
                }
            });
            viewHolder.iv_nearby_explorer.setId(position);
            viewHolder.iv_nearby_explorer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ll_search_result1.setVisibility(View.GONE);
                    ll_search_result2.setVisibility(View.VISIBLE);
                    id1 = v.getId();
                    if (mHandler != null) {
                        mHandler.removeCallbacks(mRunnable);
                    }
                    setDetailInfo(nearbies.get(id1));
                    mNearby = nearbies.get(v.getId());
                }
            });

            if (nearbies.get(position).getFav_Id().equalsIgnoreCase("0")) {
                viewHolder.iv_favorite.setActivated(false);
            } else {
                viewHolder.iv_favorite.setActivated(true);
            }

            viewHolder.rl_share.setId(position);
            viewHolder.rl_share.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent mIntent = new Intent(SearchResultFragmentActivity.this, ShareFragmentActivity.class);
                    String share1 = Constants.showMessage(SearchResultFragmentActivity.this, mPreferences.getString("Lan_Id", ""), "share1");
                    String share2 = Constants.showMessage(SearchResultFragmentActivity.this, mPreferences.getString("Lan_Id", ""), "share2");
                    String share3 = Constants.showMessage(SearchResultFragmentActivity.this, mPreferences.getString("Lan_Id", ""), "share3");

                    mIntent.putExtra("myMsg", share1 + " \"" + mPreferences.getString("User_Name", "") + "\" " + share2 + " \"" + nearbies.get(v.getId()).getPlace_Name() + "\" " + share3);
                    startActivity(mIntent);
//                        Intent intent = new Intent(Intent.ACTION_SEND);
//                        intent.setType("text/plain");
//                        startActivity(Intent.createChooser(intent, "Share with"));
                }
            });

            viewHolder.rl_navigator.setId(position);
           /* viewHolder.rl_navigator.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent mIntent = new Intent(SearchResultFragmentActivity.this, MapLocationFragment.class);
                    mIntent.putExtra("placeName", nearbies.get(viewHolder.rl_navigator.getId()).getPlace_Name());
                    mIntent.putExtra("dist", nearbies.get(viewHolder.rl_navigator.getId()).getDist());
                    mIntent.putExtra("latitude", nearbies.get(viewHolder.rl_navigator.getId()).getPlace_Latitude());
                    mIntent.putExtra("longitude", nearbies.get(viewHolder.rl_navigator.getId()).getPlace_Longi());
                    mIntent.putExtra("address", nearbies.get(viewHolder.rl_navigator.getId()).getPlace_Address());
                    mIntent.putExtra("mDirection", "no");
                    startActivity(mIntent);
                }
            });*/
            //TOdo
            viewHolder.rl_navigator.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String uri = String.format(Locale.ENGLISH, "http://maps.google.com/maps?saddr=%f,%f&daddr=%f,%f", Double.parseDouble(mPreferences.getString("latitude1", "")), Double.parseDouble(mPreferences.getString("longitude1", "")), Double.parseDouble(nearbies.get(position).getPlace_Latitude()), Double.parseDouble(nearbies.get(position).getPlace_Longi()));
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                    intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
                    startActivity(intent);
                    // Utilities.toast(SearchResultFragmentActivity.this, "In-Progress -> It will Navaigate on google map");
                }
            });
            viewHolder.iv_favorite.setId(position);
            viewHolder.iv_favorite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mPreferences.getString("User_Id", "").equalsIgnoreCase("0")) {
                        guestSnackToast();
                    } else {
                        for (int j = 0; j < nearbies.size(); j++) {
                            if (v.getId() == j) {
                                if (nearbies.get(j).getFav_Id().equalsIgnoreCase("0")) {
                                    viewHolder.iv_favorite.setActivated(true);
                                    addFavoriteCall(nearbies.get(j).getPlace_Id());
                                    mFlag = j;
                                } else {
                                    viewHolder.iv_favorite.setActivated(false);
                                    deleteFavoriteCall(nearbies.get(j).getFav_Id());
                                    mFlag = j;
                                }
                            }
                        }
                    }
                }
            });


            int dayFoundStatus = 0; //0 -> closed - 2-> open for 24 hours  1-> updated
            boolean isTicketSet = false;
            if (nearbies.get(position).getHourDetailsArrayList() != null && nearbies.get(position).getHourDetailsArrayList().size() > 0) {
                for (HourDetails hourDetails : nearbies.get(position).getHourDetailsArrayList()) {
                    if (hourDetails.getPOHKey().equals(Utils.getCurrentDay())) {
                        if (hourDetails.getPOHIsOpen().equals(PlaceClosed)) {
                            dayFoundStatus = 0;
                        } else if (hourDetails.getPOHIsOpen().equals(PlaceOpenFor24Hours)) {
                            dayFoundStatus = 2;
                        } else if (hourDetails.getPOHIsOpen().equals(PlaceOpenWithAnyTime)) {
                            _24HourTime = hourDetails.getPOHStartTime();
                            _24HourTime1 = hourDetails.getPOHEndTime();
                            _24HourDt = null;
                            _24HourDt1 = null;
                            if (_24HourTime != null && !_24HourTime.equalsIgnoreCase("NULL")) {
                                try {
                                    _24HourDt = _24HourSDF.parse(_24HourTime);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                            }
                            if (_24HourTime1 != null && !_24HourTime1.equalsIgnoreCase("NULL")) {
                                try {
                                    _24HourDt1 = _24HourSDF.parse(_24HourTime1);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                            }
                            if (_24HourDt != null && _24HourDt1 != null) {
                                viewHolder.tv_timing.setText(Constants.showMessage(SearchResultFragmentActivity.this, mPreferences.getString("Lan_Id", ""), "Timing") + ": " + _24HourSDF.format(_24HourDt) + " " + Constants.showMessage(SearchResultFragmentActivity.this, mPreferences.getString("Lan_Id", ""), "TO") + " " + _24HourSDF.format(_24HourDt1));
                            } else {
                                viewHolder.tv_timing.setText(Constants.showMessage(SearchResultFragmentActivity.this, mPreferences.getString("Lan_Id", ""), "Timing") + ": -");
                                dayFoundStatus = 1;
                            }
                        }

                        if (hourDetails.getPOHCharges() != null && !hourDetails.getPOHCharges().equals("") && !hourDetails.getPOHCharges().equalsIgnoreCase("null")) {
                            isTicketSet = true;
                            viewHolder.tv_ticket.setText(Constants.showMessage(SearchResultFragmentActivity.this, mPreferences.getString("Lan_Id", ""), "Tickets") + ": " + hourDetails.getPOHCharges());
                        }
                        break;
                    } else {
                        viewHolder.tv_timing.setText(Constants.showMessage(SearchResultFragmentActivity.this, mPreferences.getString("Lan_Id", ""), "Timing") + ": -");
                        dayFoundStatus = 1;
                    }
                }
            }
            if (dayFoundStatus == 2) {
                viewHolder.tv_timing.setText(Constants.showMessage(SearchResultFragmentActivity.this, mPreferences.getString("Lan_Id", ""), "Timing") + ": " + Constants.showMessage(SearchResultFragmentActivity.this, mPreferences.getString("Lan_Id", ""), "Open Now"));

                // txtOpenNowVal.setText("");
            } else if (dayFoundStatus == 0) {
                viewHolder.tv_timing.setText(Constants.showMessage(SearchResultFragmentActivity.this, mPreferences.getString("Lan_Id", ""), "Timing") + ": " + Constants.showMessage(SearchResultFragmentActivity.this, mPreferences.getString("Lan_Id", ""), "Closed"));
//                    txtOpenNowVal.setText("");
            }
            if (!isTicketSet) {
                viewHolder.tv_ticket.setText(Constants.showMessage(SearchResultFragmentActivity.this, mPreferences.getString("Lan_Id", ""), "Tickets") + ": -");
            }

            viewHolder.tv_distance.setText(nearbies.get(position).getDist() + Constants.showMessage(SearchResultFragmentActivity.this, mPreferences.getString("Lan_Id", ""), "KM"));
            GetRoutDistane(viewHolder.tv_distance, Double.parseDouble(mPreferences.getString("latitude2", "")), Double.parseDouble(mPreferences.getString("longitude2", "")), Double.parseDouble(nearbies.get(position).getPlace_Latitude()), Double.parseDouble(nearbies.get(position).getPlace_Longi()), nearbies.get(position).getDist());
//            viewHolder.txtOpenNowVal.setText(nearbies.get(position).getHoursOfOperations().get(0).getPOH_Start_Time() + " to " + nearbies.get(position).getHoursOfOperations().get(0).getPOH_End_Time());
//            viewHolder.tv_ticket.setText(nearbies.get(position).getHoursOfOperations().get(0).getPOH_Charges() + "$");
//            viewHolder.tv_distance.setText(nearbies.get(position).getDist() + "km");

        }

        /*private void animate(final View view, final int position) {
//            Animation animation = AnimationUtils.loadAnimation(activity, (position > getLatestData.size()) ? R.anim.up_from_bottom : R.anim.down_from_top);
            Animation animation = AnimationUtils.loadAnimation(activity, R.anim.slidebottom);
            view.startAnimation(animation);
            lastPostion = position;
        }*/

        @Override
        public int getItemCount() {
            return nearbies.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            private ImageView iv_nearby_explorer;
            private ImageView iv_favorite;
            private NormalTextView tv_ticket, tv_distance, txtCategory, txtFav, txtDistance, tv_near, txtShare, tv_timing;
            private LinearLayout rl_share, rl_navigator, rl_fav;
            private LinearLayout llView;
            private View container;

            public ViewHolder(View convertView) {
                super(convertView);
                iv_nearby_explorer = (ImageView) convertView.findViewById(R.id.iv_nearby_explorer);
                iv_favorite = (ImageView) convertView.findViewById(R.id.imgFav);
                tv_near = (NormalTextView) convertView.findViewById(R.id.tv_near);
                txtCategory = (NormalTextView) convertView.findViewById(R.id.txtCategory);
                tv_timing = (NormalTextView) convertView.findViewById(R.id.tv_timing);
                tv_ticket = (NormalTextView) convertView.findViewById(R.id.tv_ticket);
                tv_distance = (NormalTextView) convertView.findViewById(R.id.txtDistance);
                llView = (LinearLayout) convertView.findViewById(R.id.llView);
                rl_share = (LinearLayout) convertView.findViewById(R.id.rl_share);
                rl_navigator = (LinearLayout) convertView.findViewById(R.id.rl_navigator);
                rl_fav = (LinearLayout) convertView.findViewById(R.id.rl_fav);
//                container = convertView.findViewById(R.id.cv_search_result_adapter);
                txtShare = (NormalTextView) convertView.findViewById(R.id.txtShare);
                txtFav = (NormalTextView) convertView.findViewById(R.id.txtFav);
            }
        }
    }

    class DistanceAsync extends AsyncTask<String, JSONObject, JSONObject> {
        String Distance = "error";
        String Status = "error";
        String distApi;
        private NormalTextView txtDistance;

        public DistanceAsync(NormalTextView txtDistance, String distApi) {
            this.txtDistance = txtDistance;
            this.distApi = distApi;
        }


        @Override
        protected JSONObject doInBackground(String... strings) {
            JSONObject jsonObj = Utilities.getJSONfromURL(strings[0]);
            return jsonObj;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);
            try {

                Status = jsonObject.getString("status");
                if (Status.equalsIgnoreCase("OK")) {
                    JSONArray routes = jsonObject.getJSONArray("routes");
                    JSONObject zero = routes.getJSONObject(0);
                    JSONArray legs = zero.getJSONArray("legs");
                    JSONObject zero2 = legs.getJSONObject(0);
                    JSONObject dist = zero2.getJSONObject("distance");
                    Distance = dist.getString("text").replace("km", "").replace(" ", "");
                } else {
                    Distance = distApi;
                }
                txtDistance.setText(Distance + Constants.showMessage(SearchResultFragmentActivity.this, mPreferences.getString("Lan_Id", ""), "KM"));
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }


        }
    }

    private class GalleryAdapter2 extends BaseAdapter {
        Context context;

        public GalleryAdapter2(Context context) {
            this.context = context;
        }

        @Override
        public int getCount() {
            return nearbies1.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            LayoutInflater mLayoutInflater;
            mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = mLayoutInflater.inflate(R.layout.detail_gallery, parent, false);
            final ImageView iv_detail = (ImageView) convertView.findViewById(R.id.iv_detail);
            final NormalTextView tv_name = (NormalTextView) convertView.findViewById(R.id.tv_name);
            final NormalTextView tv_km = (NormalTextView) convertView.findViewById(R.id.tv_km);
            final LinearLayout llView = (LinearLayout) convertView.findViewById(R.id.llView);
            final RelativeLayout rlMain = (RelativeLayout) convertView.findViewById(R.id.rlMain);
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) rlMain.getLayoutParams();
            int widthofImage = (width * 50) / 100;
            layoutParams.width = widthofImage;
            layoutParams.height = layoutParams.width;
            rlMain.setLayoutParams(layoutParams);

            tv_name.setText(nearbies1.get(position).getPlace_Name());
            tv_km.setText(nearbies1.get(position).getDist() + Constants.showMessage(SearchResultFragmentActivity.this, mPreferences.getString("Lan_Id", ""), "KM"));

            GetRoutDistane(tv_km, Double.parseDouble(mPreferences.getString("latitude2", "")), Double.parseDouble(mPreferences.getString("longitude2", "")), Double.parseDouble(nearbies1.get(position).getPlace_Latitude()), Double.parseDouble(nearbies1.get(position).getPlace_Longi()), nearbies1.get(position).getDist());
            String imageUrl = Constants.IMAGE_URL + nearbies1.get(position).getPlace_MainImage() + "&w=" + (width - 30);

            Picasso.with(SearchResultFragmentActivity.this) //
                    .load(imageUrl) //
                    .resize(widthofImage, widthofImage)
                    .into(iv_detail);

            gv_detail1_search_result2.setOnItemClickListener(new com.ftl.tourisma.gallery1.AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(com.ftl.tourisma.gallery1.AdapterView<?> parent, View view, int position, long id) {
                    id1 = position;
                    if (mHandler != null) {
                        mHandler.removeCallbacks(mRunnable);
                    }
                    setDetailInfo(nearbies1.get(position));
                    mNearby = nearbies1.get(position);
                }
            });

            Log.i("System out", imageUrl);
            //  imageLoader2.displayImage(imageUrl, iv_detail, optionsSimple);

            return convertView;
        }
    }

}
