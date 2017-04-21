package com.ftl.tourisma;

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
import com.google.android.gms.maps.model.Marker;
import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.SnackbarManager;
import com.squareup.picasso.Picasso;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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
import static com.ftl.tourisma.utils.Constants.PlaceOpenFor24Hours;
import static com.ftl.tourisma.utils.Constants.PlaceOpenWithAnyTime;

/**
 * Created by fipl11111 on 02-Mar-16.
 */
public class SearchResultPlaceDetailsActivity extends FragmentActivity implements View.OnClickListener, post_sync.ResponseHandler {

    private static final int PLACE_LIKE_SEARCH = 1003;
    private static final String TAG = "SearchResultFragment";
    static int mCounter = -1;
    GalleryAdapter2 galleryAdapter2;
    private ArrayList<Nearby> nearbies1 = new ArrayList<>();
    private Intent mIntent;
    //    private ImageLoader imageLoader = ImageLoader.getInstance();
//    private ImageLoader imageLoader2 = ImageLoader.getInstance();
    private int width, height;
    private SharedPreferences mPreferences;
    private SharedPreferences.Editor mEditor;
    private SimpleDateFormat _24HourSDF, _12HourSDF;
    private String _24HourTime, _24HourTime1;
    private Date _24HourDt, _24HourDt1;
    private NormalBoldTextView tv_full_name_search_result2;
    private NormalTextView txtDailyWorkingHours, txtOpenNowVal, txt_add_to_fav, tv_distance1_search_result2, tv_info_search_result2, tv_discription_search_result2;
    private com.ftl.tourisma.gallery1.Gallery gv_detail1_search_result2;
    private ImageView iv_back5;
    private String[] strImg1;
    private int id1;
    private LinearLayout listView_fees, ll_search_result2, ll_see_all;
    private Double latitude, longitude;
    private int pos = 0;
    private Marker marker;
    private String placeId;
    private NormalTextView tv_map_location;
    private int mFlag = 0;
    private ImageView iv_search_map;
    private LinearLayout dotLayout_detail_search_result2, ll_change_city;
    private Handler mHandler;
    private Runnable mRunnable;
    private ImageView[] mDotsText1;
    private NormalTextView tv_fee_search_result;
    private NormalTextView tv_about_place_search_result;
    private NormalTextView tv_similar_search;
    private NormalTextView tv_see_all_search_result;
    private Nearby mNearby = new Nearby();
    private int like;
    private Dialog dialog;
    private ArrayList<HourDetails> hourDetailses;
    private SliderLayout sliderPlaceImages;
    private PagerIndicator custom_indicator1;
    private ImageView imgSharePlace;
    private RelativeLayout rlVirtualTour;
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
    private String locationName;
    private boolean isForSimilarPlaces = false;

    private void beaconsToast(final String msg, final String msgBeacon, final String img, final String placeId, final int isCloseApproach, final String isClosePromo) {
        if (msg != null && !msg.equals("")) {

            txt_snack_msg.setText(msg);
            txt_snack_msg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isClosePromo.equals("1")) {
                        Intent intent = new Intent(SearchResultPlaceDetailsActivity.this, BeaconsActivity.class);
                        intent.putExtra(PLACE_ID, placeId);
                        intent.putExtra(PLACE_IMAGE, img);
                        intent.putExtra(BEACON_MESSAGE, msgBeacon);
                        startActivity(intent);
                    } else {
                        Intent intent = new Intent(SearchResultPlaceDetailsActivity.this, SearchResultPlaceDetailsActivity.class);
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

    private void beaconsToast(final String msg, final String msgBeacon, final String img, final String placeId, boolean isClickable, final int isCloseApproach) {
        if (msg != null && !msg.equals("")) {

            txt_snack_msg.setText(msg);
            if (isClickable) {
                txt_snack_msg.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (isCloseApproach == 1) {
                            Intent intent = new Intent(SearchResultPlaceDetailsActivity.this, BeaconsActivity.class);
                            intent.putExtra(PLACE_ID, placeId);
                            intent.putExtra(PLACE_IMAGE, img);
                            intent.putExtra(BEACON_MESSAGE, msgBeacon);
                            startActivity(intent);
                        } else {
                            Intent intent = new Intent(SearchResultPlaceDetailsActivity.this, SearchResultPlaceDetailsActivity.class);
                            intent.putExtra("placeId", placeId);
                            startActivity(intent);
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

        setContentView(R.layout.activity_search_result_place_details);

        mIntent = getIntent();
//        nearbies = (ArrayList<Nearby>) mIntent.getSerializableExtra("nearbies");
        placeId = mIntent.getStringExtra("placeId");
        locationName = mIntent.getStringExtra("location");

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

      /*   ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this)
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
*/
        initialisation();


        searchCall(placeId);

    }

    private void initialisation() {

        imgSharePlace = (ImageView) findViewById(R.id.imgSharePlace);
        imgSharePlace.setOnClickListener(this);

        rlVirtualTour = (RelativeLayout) findViewById(R.id.rlVirtualTour);
        rlVirtualTour.setOnClickListener(this);
        custom_indicator1 = (PagerIndicator) findViewById(R.id.custom_indicator1);
        custom_indicator1.setIndicatorStyleResource(R.drawable.shape_cirlce_fill, R.drawable.shape_cirlce_unfill);

        llBeaconToast = (LinearLayout) findViewById(R.id.llBeaconToast);
        txt_snack_msg = (NormalTextView) findViewById(R.id.txt_snack_msg);

        ll_see_all = (LinearLayout) findViewById(R.id.ll_see_all);
        llSearchResultToast = (LinearLayout) findViewById(R.id.llSearchResultToast);
        ll_login_snack = (LinearLayout) findViewById(R.id.ll_login_snack);
        ll_sign_up_snack = (LinearLayout) findViewById(R.id.ll_sign_up_snack);
        tv_login_snack = (NormalTextView) findViewById(R.id.tv_login_snack);
        tv_sign_up_snack = (NormalTextView) findViewById(R.id.tv_sign_up_snack);
        tv_snack_msg = (NormalTextView) findViewById(R.id.tv_snack_msg);
        ll_sign_up_snack.setOnClickListener(this);
        ll_login_snack.setOnClickListener(this);

        tv_fee_search_result = (NormalTextView) findViewById(R.id.tv_fee_explore);
        tv_fee_search_result.setText(Constants.showMessage(SearchResultPlaceDetailsActivity.this, mPreferences.getString("Lan_Id", ""), "Fee"));
        txtStartNavigating = (NormalTextView) findViewById(R.id.txtStartNavigating);
        txtStartNavigating.setText(Constants.showMessage(SearchResultPlaceDetailsActivity.this, mPreferences.getString("Lan_Id", ""), "StartNavigation"));

        tv_about_place_search_result = (NormalTextView) findViewById(R.id.tv_about_place_explore);
        tv_about_place_search_result.setText(Constants.showMessage(SearchResultPlaceDetailsActivity.this, mPreferences.getString("Lan_Id", ""), "aboutplace"));

        tv_similar_explore = (NormalTextView) findViewById(R.id.tv_similar_explore);
        tv_similar_explore.setText(Constants.showMessage(SearchResultPlaceDetailsActivity.this, mPreferences.getString("Lan_Id", ""), "similarplace"));

        tv_see_all_search_result = (NormalTextView) findViewById(R.id.tv_see_all_explore);
        tv_see_all_search_result.setText(Constants.showMessage(SearchResultPlaceDetailsActivity.this, mPreferences.getString("Lan_Id", ""), "seeall"));
        tv_see_all_search_result.setOnClickListener(this);

        ll_change_city = (LinearLayout) findViewById(R.id.ll_change_city);
        dotLayout_detail_search_result2 = (LinearLayout) findViewById(R.id.dotLayout_detail);
//        iv_location_search_result2 = (ImageView) findViewById(R.id.iv_location);

        iv_search_map = (ImageView) findViewById(R.id.iv_search_map);
        iv_search_map.setVisibility(View.GONE);

        ll_change_city.setOnClickListener(this);

        ll_search_result2 = (LinearLayout) findViewById(R.id.ll_search_result2);

        tv_map_location = (NormalTextView) findViewById(R.id.tv_map_location);

//        tv_map_location.setText(mPreferences.getString(Preference.Pref_City, ""));
        if (locationName == null) {
            if (mPreferences.getString(Preference.Pref_City, "").equalsIgnoreCase("")) {
                tv_map_location.setText(mPreferences.getString(Preference.Pref_Country, ""));
            } else {
            }
        } else {
            tv_map_location.setText(locationName);
        }

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
        iv_back5.setOnClickListener(this);

        txt_add_to_fav = (NormalTextView) findViewById(R.id.txt_add_to_fav);

        listView_fees = (LinearLayout) findViewById(R.id.listView_fees);
    }

    @Override
    public void onClick(View v) {
        if (v == ll_change_city) {
            Intent mIntent = new Intent(this, SelectLocationFragmentActivity.class);
//            mIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(mIntent);
            // this.finish();
        } else if (v == iv_back5) {
//            ll_search_result1.setVisibility(View.VISIBLE);
            finish();
        } else if (v == iv_search_map) {
            Intent mIntent = new Intent(this, SearchActivity.class);
            startActivity(mIntent);
            //finish();
        } else if (v == tv_see_all_search_result) {
            Intent mIntent = new Intent(SearchResultPlaceDetailsActivity.this, SearchResultFragmentActivity.class);
            mIntent.putExtra("nearbies", nearbies1);
            mIntent.putExtra("search", "Similar Places");
            startActivity(mIntent);
        } else if (v == txtSuggestPlace) {
            suggestPlace();
        } else if (v == txtDailyWorkingHours) {
            openWeekDaysPopup();
        } else if (v == rlVirtualTour) {
            Intent intent = new Intent(SearchResultPlaceDetailsActivity.this, SimpleVrPanoramaActivity.class);
            intent.putExtra("path", mNearby.getPlaceVRMainImage());
            intent.putExtra("path_1", mNearby.getVrimages());
            startActivity(intent);
        } else if (v == imgSharePlace) {
            Intent mIntent = new Intent(SearchResultPlaceDetailsActivity.this, ShareFragmentActivity.class);
            String share1 = Constants.showMessage(SearchResultPlaceDetailsActivity.this, mPreferences.getString("Lan_Id", ""), "share1");
            String share2 = Constants.showMessage(SearchResultPlaceDetailsActivity.this, mPreferences.getString("Lan_Id", ""), "share2");
            String share3 = Constants.showMessage(SearchResultPlaceDetailsActivity.this, mPreferences.getString("Lan_Id", ""), "share3");

            mIntent.putExtra("myMsg", share1 + " \"" + mPreferences.getString("User_Name", "") + "\" " + share2 + " \"" + mNearby.getPlace_Name() + "\" " + share3);
            startActivity(mIntent);

        } else if (v == ll_login_snack) {
            mPreferences.edit().putString("User_id", "").commit();
            Intent mIntent = new Intent(SearchResultPlaceDetailsActivity.this, LoginFragmentActivity.class);
            startActivity(mIntent);
            finish();
        } else if (v == ll_sign_up_snack) {
            mPreferences.edit().putString("User_id", "").commit();
            Intent mIntent = new Intent(SearchResultPlaceDetailsActivity.this, SignupFragmentActivity.class);
            startActivity(mIntent);
            finish();
        }
    }

    public void suggestPlace() {
        Intent sendIntent = new Intent(Intent.ACTION_VIEW);
        sendIntent.setType("text/plain");
        sendIntent.setData(Uri.parse("info@mytourisma.com"));
        sendIntent.setClassName("com.google.android.gm", "com.google.android.gm.ComposeActivityGmail");
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
                    spannableStringBuilder = Utils.getSpannableString(Constants.showMessage(SearchResultPlaceDetailsActivity.this, mPreferences.getString("Lan_Id", ""), "Open Now") + ": ", Utils.getColor(SearchResultPlaceDetailsActivity.this, R.color.mGreen), true, 0);
                } else {
                    spannableStringBuilder = Utils.getSpannableString(Constants.showMessage(SearchResultPlaceDetailsActivity.this, mPreferences.getString("Lan_Id", ""), "Closed") + ": ", Utils.getColor(SearchResultPlaceDetailsActivity.this, android.R.color.holo_red_dark), true, 0);
                    // Utils.toast("22 false"+date);
                }

            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        spannableStringBuilder.append(_24HourSDF.format(_24HourDt) + " " + Constants.showMessage(SearchResultPlaceDetailsActivity.this, mPreferences.getString("Lan_Id", ""), "TO") + " " + _24HourSDF.format(_24HourDt1));
//        spannableStringBuilder.append(_12HourSDF.format(_24HourDt).replace("AM", Constants.showMessage(getActivity(), mPreferences.getString("Lan_Id", ""), "am")).replace("PM", Constants.showMessage(getActivity(), mPreferences.getString("Lan_Id", ""), "pm")) + " " + Constants.showMessage(getActivity(), mPreferences.getString("Lan_Id", ""), "TO") + " " + _12HourSDF.format(_24HourDt1).replace("AM", Constants.showMessage(getActivity(), mPreferences.getString("Lan_Id", ""), "am")).replace("PM", Constants.showMessage(getActivity(), mPreferences.getString("Lan_Id", ""), "pm")));
        return spannableStringBuilder;


    }

    public void setDetailInfo(final Nearby mNearby) {

//        Log.d("System out", "selected id " + id1);
//        Log.d("System out", "mNearby size " + nearbies.size());
//        Log.d("System out", "mNearby getHoursOfOperations size " + mNearby.getHoursOfOperations().size());

        hourDetailses = new ArrayList<>();
        int dayFoundStatus = 0; //0 -> no day found 1-> found but closed - 2-> open but time not found 3 -> done
        if (mNearby.getHourDetailsArrayList() != null && mNearby.getHourDetailsArrayList().size() > 0) {
            hourDetailses = mNearby.getHourDetailsArrayList();
            for (HourDetails hourDetails : mNearby.getHourDetailsArrayList()) {
                if (hourDetails.getPOHKey().equals(Utils.getCurrentDay())) {

                    //Checking for if yeasterday's timing is open for place
                    for (HourDetails hourDetailsYesterday : mNearby.getHourDetailsArrayList()) {
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
                            if (dayFoundStatus == 0)
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

       /* if (mNearby.getHoursOfOperations().get(0).getPOH_Start_Time().length() != 0) {
            _24HourTime = mNearby.getHoursOfOperations().get(0).getPOH_Start_Time();
            _24HourTime1 = mNearby.getHoursOfOperations().get(0).getPOH_End_Time();
            _24HourSDF = new SimpleDateFormat("H:mm");
            _12HourSDF = new SimpleDateFormat("h:mma");
            try {
                _24HourDt = _24HourSDF.parse(_24HourTime);
                _24HourDt1 = _24HourSDF.parse(_24HourTime1);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            txtOpenNowVal.setText(_12HourSDF.format(_24HourDt).toString().replace("AM", Constants.showMessage(SearchResultFragmentActivity.this, mPreferences.getString("Lan_Id", ""), "am")).replace("PM", Constants.showMessage(SearchResultFragmentActivity.this, mPreferences.getString("Lan_Id", ""), "pm")) + " " + Constants.showMessage(SearchResultFragmentActivity.this, mPreferences.getString("Lan_Id", ""), "TO") + " " + _12HourSDF.format(_24HourDt1).toString().replace("AM", Constants.showMessage(SearchResultFragmentActivity.this, mPreferences.getString("Lan_Id", ""), "am")).replace("PM", Constants.showMessage(SearchResultFragmentActivity.this, mPreferences.getString("Lan_Id", ""), "pm")));
        }

        try {
            _24HourTime = mNearby.getHoursOfOperations().get(0).getPOH_Start_Time();
            _24HourTime1 = mNearby.getHoursOfOperations().get(0).getPOH_End_Time();
            _24HourSDF = new SimpleDateFormat("HH:mm");
            _12HourSDF = new SimpleDateFormat("hh:mma");
            _24HourDt = _24HourSDF.parse(_24HourTime);
            _24HourDt1 = _24HourSDF.parse(_24HourTime1);
                    *//*System.out.println(_24HourDt);
                    System.out.println(_12HourSDF.format(_24HourDt));*//*
        } catch (Exception e) {
            e.printStackTrace();
        }*/

        final int mId;

        mId = id1;
        if (mNearby.getFav_Id().equalsIgnoreCase("0")) {
            txt_add_to_fav.setCompoundDrawablesWithIntrinsicBounds(Utilities.getDrawable(this, R.drawable.ic_favourite_default), null, null, null);
            txt_add_to_fav.setText(Constants.showMessage(SearchResultPlaceDetailsActivity.this, mPreferences.getString("Lan_Id", ""), "Add to Favourites"));
            like = 0;
        } else {
            txt_add_to_fav.setText(Constants.showMessage(SearchResultPlaceDetailsActivity.this, mPreferences.getString("Lan_Id", ""), "Remove Favourite"));

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
                        txt_add_to_fav.setText(Constants.showMessage(SearchResultPlaceDetailsActivity.this, mPreferences.getString("Lan_Id", ""), "Remove Favourite"));

                        txt_add_to_fav.setCompoundDrawablesWithIntrinsicBounds(Utilities.getDrawable(SearchResultPlaceDetailsActivity.this, R.drawable.ic_favourite_active), null, null, null);

                        addFavoriteCall(mNearby.getPlace_Id());
                        mFlag = mId;
                    } else if (like == 1) {
                        txt_add_to_fav.setText(Constants.showMessage(SearchResultPlaceDetailsActivity.this, mPreferences.getString("Lan_Id", ""), "Add to Favourites"));
                        txt_add_to_fav.setCompoundDrawablesWithIntrinsicBounds(Utilities.getDrawable(SearchResultPlaceDetailsActivity.this, R.drawable.ic_favourite_default), null, null, null);

                        deleteFavoriteCall(mNearby.getFav_Id());
                        mFlag = mId;
                    }
                    mCounter = 1;
                }
            }
        });

//        tv_distance1_search_result2.setText(mNearby.getDist() + Constants.showMessage(SearchResultPlaceDetailsActivity.this, mPreferences.getString("Lan_Id", ""), "KM"));
        tv_distance1_search_result2.setText(GetRoutDistane(Double.parseDouble(mPreferences.getString("latitude2", "")), Double.parseDouble(mPreferences.getString("longitude2", "")), Double.parseDouble(mNearby.getPlace_Latitude()), Double.parseDouble(mNearby.getPlace_Longi()), mNearby.getDist()) + Constants.showMessage(SearchResultPlaceDetailsActivity.this, mPreferences.getString("Lan_Id", ""), "KM"));

        //  txtOpenNow.setText(Constants.showMessage(SearchResultFragmentActivity.this, mPreferences.getString("Lan_Id", ""), "Timing") + "(" + mNearby.getPlace_Close_Note() + ")");

        tv_full_name_search_result2.setText(mNearby.getPlace_Name());
        tv_info_search_result2.setText(mNearby.getPlace_Address());
        tv_discription_search_result2.setText(Html.fromHtml(Html.fromHtml("<html><body><p style=\"text-align:justify\">" + mNearby.getPlace_Description() + "</p></body></html>").toString()));
        String string = mNearby.getPlace_MainImage();
        if (mNearby.getOtherimages().length() != 0 && !mNearby.getOtherimages().equalsIgnoreCase("null")) {
            string += "," + mNearby.getOtherimages();
        }
        listView_fees.setVisibility(View.VISIBLE);
        boolean isCurrentDayFound = false;
        if (mNearby.getHourDetailsArrayList() != null && mNearby.getHourDetailsArrayList().size() > 0) {
            addFeesCustomViews(mNearby.getHourDetailsArrayList().get(0).getPOHCharges(), mNearby.getPrice_Description());

        } else {
            addFeesCustomViews("-", mNearby.getPrice_Description());
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
                                Intent intent = new Intent(SearchResultPlaceDetailsActivity.this, FullPlaceImageViewActivity.class);
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
        if (isForSimilarPlaces)
            searchCall(mNearby.getPlace_Id());
        txtStartNavigating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Log.d("System out", "placeName " + mNearby.getPlace_Name());
//                Log.d("System out", "dist " + mNearby.getDist());
//                Log.d("System out", "latitude " + mNearby.getPlace_Latitude());
//                Log.d("System out", "longitude " + mNearby.getPlace_Longi());

/*
                Intent mIntent = new Intent(SearchResultFragmentActivity.this, MapLocationFragment.class);
                mIntent.putExtra("placeName", mNearby.getPlace_Name());
                mIntent.putExtra("dist", mNearby.getDist());
                mIntent.putExtra("latitude", mNearby.getPlace_Latitude());
                mIntent.putExtra("longitude", mNearby.getPlace_Longi());
                mIntent.putExtra("mDirection", "yes");
                mIntent.putExtra("address", mNearby.getPlace_Address());
                startActivity(mIntent);
*/

                String uri = String.format(Locale.ENGLISH, "http://maps.google.com/maps?saddr=%f,%f&daddr=%f,%f", Double.parseDouble(mPreferences.getString("latitude1", "")), Double.parseDouble(mPreferences.getString("longitude1", "")), Double.parseDouble(mNearby.getPlace_Latitude()), Double.parseDouble(mNearby.getPlace_Longi()));
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
                startActivity(intent);
            }
        });

    }

    public String GetRoutDistane(double startLat, double startLong, double endLat, double endLong, String distApi) {
        String Distance = "error";
        String Status = "error";
        try {
            Log.e("Distance Link : ", "http://maps.googleapis.com/maps/api/directions/json?origin=" + startLat + "," + startLong + "&destination=" + endLat + "," + endLong + "&sensor=false");
            JSONObject jsonObj = Utilities.getJSONfromURL("http://maps.googleapis.com/maps/api/directions/json?origin=" + startLat + "," + startLong + "&destination=" + endLat + "," + endLong + "&sensor=false");
            Status = jsonObj.getString("status");
            if (Status.equalsIgnoreCase("OK")) {
                JSONArray routes = jsonObj.getJSONArray("routes");
                JSONObject zero = routes.getJSONObject(0);
                JSONArray legs = zero.getJSONArray("legs");
                JSONObject zero2 = legs.getJSONObject(0);
                JSONObject dist = zero2.getJSONObject("distance");
                Distance = dist.getString("text").replace("km", "").replace(" ", "");
            } else {
                Distance = distApi;
            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return Distance;


    }


    public InputStream retrieveStream(String url) {

        DefaultHttpClient client = new DefaultHttpClient();

        HttpGet getRequest = new HttpGet(url);

        try {

            HttpResponse getResponse = client.execute(getRequest);
            final int statusCode = getResponse.getStatusLine().getStatusCode();

            if (statusCode != HttpStatus.SC_OK) {

                return null;
            }

            HttpEntity getResponseEntity = getResponse.getEntity();
            return getResponseEntity.getContent();

        } catch (IOException e) {
            getRequest.abort();

        }

        return null;

    }


    private void addFeesCustomViews(String pohCharges, String price_description) {
        listView_fees.removeAllViews();
        if (price_description == null || price_description.equals("") || price_description.equalsIgnoreCase("null") || price_description.trim().length() == 0) {


            price_description = Constants.showMessage(SearchResultPlaceDetailsActivity.this, mPreferences.getString("Lan_Id", ""), "No Fees data available");
        }
        LayoutInflater inflater = (LayoutInflater) SearchResultPlaceDetailsActivity.this.getSystemService(LAYOUT_INFLATER_SERVICE);
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
        //String text=s.replace("\\n", System.getProperty("line.separator")).replace("\\r", "");
        SpannableStringBuilder spannablecontent = new SpannableStringBuilder(s);

        if (isBold)
            spannablecontent.setSpan(new CustomTypefaceSpan("", MyTorismaApplication.typeFace.getBoldFonts()),
                    0, spannablecontent.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        else
            spannablecontent.setSpan(new CustomTypefaceSpan("", MyTorismaApplication.typeFace.getNormalFonts()),
                    0, spannablecontent.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        return spannablecontent;
    }

    @Override
    public void onResponse(String response, String action) {
        try {
            if (action.equalsIgnoreCase("AddFavorite")) {
                addFavoriteResponse(response);
            } else if (action.equalsIgnoreCase("DeleteFavorite")) {
                deleteFavoriteResponse(response);
            } else if (action.equalsIgnoreCase("PlaceDetails")) {
                searchResponse(response);
            }
        } catch (Exception e) {
            Log.e(TAG, "onResponse Exception " + e.getLocalizedMessage());
        }
    }


    private void searchCall(String Place_Id) {
        if (!isForSimilarPlaces)
            ll_search_result2.setVisibility(View.GONE);
        if (CommonClass.hasInternetConnection(SearchResultPlaceDetailsActivity.this)) {
            String url = Constants.SERVER_URL + "json.php?action=PlaceDetails";
            String json = "[{\"Lan_Id\":\"" + mPreferences.getString("Lan_Id", "") + "\",\"User_Id\":\"" + mPreferences.getString("User_Id", "") + "\",\"Current_Latitude\":\"" + mPreferences.getString("latitude2", "") + "\",\"Current_Longitude\":\"" + mPreferences.getString("longitude2", "") + "\",\"Place_Id\":\"" + Place_Id + "\"}]";
//            Log.d("System out", "PlaceDetails " + json);
            new PostSync(SearchResultPlaceDetailsActivity.this, "PlaceDetails", SearchResultPlaceDetailsActivity.this).execute(url, json);
        } else {
            Intent intent = new Intent(getApplicationContext(), NoInternet.class);
            startActivity(intent);
            //SnackbarManager.show(Snackbar.with(SearchResultPlaceDetailsActivity.this).color(Utils.getColor(this, R.color.mBlue)).text(Constants.showMessage(SearchResultPlaceDetailsActivity.this, mPreferences.getString("Lan_Id", ""), "NOINTERNET")));
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
    }

    public void searchResponse(String resultString) {
//        Log.d("System out", resultString);
        ll_search_result2.setVisibility(View.VISIBLE);
        try {
            String str = resultString.replaceAll("\\\\", "");
            JSONArray jsonArray = new JSONArray(resultString);
//            JSONArray jsonArray = new JSONArray(resultString);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.optJSONObject(i);

                mNearby = new Nearby();
                mNearby.setPlace_Id(jsonObject.optString("Place_Id"));
                mNearby.setCategory_Name(jsonObject.optString("Category_Name"));
                mNearby.setPlace_Name(jsonObject.optString("Place_Name"));
                mNearby.setPlace_ShortInfo(jsonObject.optString("Place_ShortInfo"));
                mNearby.setPlace_MainImage(jsonObject.optString("Place_MainImage"));
//                mNearby.setPlace_Description(jsonObject.optString("Place_Description"));
                if (jsonObject.optString("Place_Description") != null && !jsonObject.optString("Place_Description").equalsIgnoreCase("")) {

                    String price = jsonObject.optString("Place_Description");
                    //String a = price.replace("\\*", "");
//                    String b = price.replaceAll("\r", "");
//                    String c = b.replaceAll("\n", System.getProperty("line.separator"));
                    mNearby.setPlace_Description(price);

                } else {
                    mNearby.setPlace_Description(jsonObject.optString("Place_Description"));
                }

                mNearby.setPlace_Address(jsonObject.optString("Place_Address"));
                mNearby.setPlace_Latitude(jsonObject.optString("Place_Latitude"));

                if (jsonObject.optString("Price_Description") != null && !jsonObject.optString("Price_Description").equalsIgnoreCase("")) {
                    String price = jsonObject.optString("Price_Description");
                    //String a = price.replaceAll("\r", "");
//                    String b = a.replaceAll("\n", System.getProperty("line.separator"));
                    mNearby.setPrice_Description(price);

                } else {
                    mNearby.setPrice_Description(jsonObject.optString("Price_Description"));
                }
                mNearby.setPlace_Longi(jsonObject.optString("Place_Longi"));
                mNearby.setOtherimages(jsonObject.optString("otherimages"));
                mNearby.setDist(jsonObject.optString("dist"));
                mNearby.setFav_Id(jsonObject.optString("Fav_Id"));
                mNearby.setPlaceVRMainImage(jsonObject.optString("Place_VRMainImage"));
                mNearby.setVrimages(jsonObject.optString("vrimages"));
                mNearby.setFree_entry(jsonObject.optString("free_entry"));

                JSONArray operation1 = jsonObject.getJSONArray("HourDetails");
                ArrayList<HourDetails> detailsArrayList = new ArrayList<>();
                for (int j = 0; j < operation1.length(); j++) {

                    HourDetails hourDetails = new HourDetails();
                    JSONObject jsonObject2 = operation1.getJSONObject(j);
                    hourDetails.setPlaceId(jsonObject2.getString("Place_Id"));
                    hourDetails.setPOHIsOpen(jsonObject2.getString("POH_Is_Open"));
                    hourDetails.setPOHKey(jsonObject2.getString("POH_Key"));
                    hourDetails.setPOHDay(jsonObject2.getString("POH_Day"));
                    hourDetails.setPOHStartTime(jsonObject2.getString("POH_Start_Time"));
                    hourDetails.setPOHEndTime(jsonObject2.getString("POH_End_Time"));
                    hourDetails.setPOHId(jsonObject2.getString("POH_Id"));
                    hourDetails.setFeesDetails(getFeesObject(jsonObject2));
                    detailsArrayList.add(hourDetails);
                }
                mNearby.setHourDetailsArrayList(detailsArrayList);

                nearbies1.clear();
                if (jsonObject.has("similar")) {
                    JSONArray jsonArray1 = jsonObject.optJSONArray("similar");

                    for (int intSimilar = 0; intSimilar < jsonArray1.length(); intSimilar++) {
                        Nearby nearby1 = jsonObjConverter.convertJsonToNearByObj(jsonArray1.optJSONObject(intSimilar));
                        nearbies1.add(nearby1);
                    }
                }
                if (!isForSimilarPlaces)
                    setDetailInfo(mNearby);
                galleryAdapter2 = new GalleryAdapter2(SearchResultPlaceDetailsActivity.this);
                gv_detail1_search_result2.setAdapter(null);
                gv_detail1_search_result2.setAdapter(galleryAdapter2);

                if (nearbies1.size() == 0) {
                    ll_see_all.setVisibility(View.GONE);
                } else {
                    ll_see_all.setVisibility(View.VISIBLE);
                }
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

    public String GetRoutDistane(NormalTextView txtDistance, double startLat, double startLong, double endLat, double endLong, String distApi) {
        String Distance = "error";
        String Status = "error";
        try {
            Log.e("Distance Link : ", "http://maps.googleapis.com/maps/api/directions/json?origin=" + startLat + "," + startLong + "&destination=" + endLat + "," + endLong + "&sensor=false");

            new DistanceAsync(txtDistance, distApi).execute("http://maps.googleapis.com/maps/api/directions/json?origin=" + startLat + "," + startLong + "&destination=" + endLat + "," + endLong + "&sensor=false");
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

    private void addFavoriteCall(String Place_Id) {
        if (CommonClass.hasInternetConnection(this)) {
            String url = Constants.SERVER_URL + "json.php?action=AddFavorite";
            String json = "[{\"User_Id\":\"" + mPreferences.getString("User_Id", "") + "\",\"Place_Id\":\"" + Place_Id + "\"}]";
//            Log.d("System out", "AddFavorite " + json);
            new PostSync(SearchResultPlaceDetailsActivity.this, "AddFavorite", SearchResultPlaceDetailsActivity.this).execute(url, json);
        } else {
            Intent intent = new Intent(getApplicationContext(), NoInternet.class);
            startActivity(intent);
            //SnackbarManager.show((Snackbar) Snackbar.with(SearchResultPlaceDetailsActivity.this).color(getResources().getColor(R.color.mBlue)).text(Constants.showMessage(SearchResultPlaceDetailsActivity.this, mPreferences.getString("Lan_Id", ""), "NOINTERNET")));
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
//                        if (mCounter == -1) {
//                            nearbies.get(mFlag).setFav_Id(jsonObject.optString("Fav_Id"));
//                        }
//                        mCounter = -1;
                        mFlag = 0;
                        like = 1;
                        Constants.mStaticFavCall = 0;
                        SnackbarManager.show(Snackbar.with(this).color(getResources().getColor(R.color.mBlue)).text(Constants.showMessage(SearchResultPlaceDetailsActivity.this
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
            new PostSync(SearchResultPlaceDetailsActivity.this, "DeleteFavorite", SearchResultPlaceDetailsActivity.this).execute(url, json);
        } else {
            Intent intent = new Intent(getApplicationContext(), NoInternet.class);
            startActivity(intent);
            //SnackbarManager.show((Snackbar) Snackbar.with(SearchResultPlaceDetailsActivity.this).color(getResources().getColor(R.color.mBlue)).text(Constants.showMessage(SearchResultPlaceDetailsActivity.this, mPreferences.getString("Lan_Id", ""), "NOINTERNET")));
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
//                        if (mCounter == -1) {
//                            nearbies.get(mFlag).setFav_Id("0");
//                        }
                        mCounter = -1;
                        mFlag = 0;
                        like = 0;
                        Constants.mStaticFavCall = 0;
                        SnackbarManager.show(Snackbar.with(this).color(getResources().getColor(R.color.mBlue)).text(Constants.showMessage(SearchResultPlaceDetailsActivity.this, mPreferences.getString("Lan_Id", ""), "Removefavorite")));
                    }
                }
            } catch (JSONException e) {

            }
        }
    }

    private void guestSnackToast() {

        tv_login_snack.setText(Constants.showMessage(SearchResultPlaceDetailsActivity.this, mPreferences.getString("Lan_Id", ""), "Login"));
        tv_sign_up_snack.setText(Constants.showMessage(SearchResultPlaceDetailsActivity.this, mPreferences.getString("Lan_Id", ""), "SignUp"));
        tv_snack_msg.setText(Constants.showMessage(SearchResultPlaceDetailsActivity.this, mPreferences.getString("Lan_Id", ""), "GetStarted"));

        runnable = new Runnable() {
            @Override
            public void run() {
                llSearchResultToast.setVisibility(View.GONE);
            }
        };
        llSearchResultToast.setVisibility(View.VISIBLE);
        handler.postDelayed(runnable, 4000);
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
            layoutParams.width = (width * 50) / 100;
            layoutParams.height = (width * 50) / 100;


            rlMain.setLayoutParams(layoutParams);

            tv_name.setText(nearbies1.get(position).getPlace_Name());
            tv_km.setText(nearbies1.get(position).getDist() + Constants.showMessage(SearchResultPlaceDetailsActivity.this, mPreferences.getString("Lan_Id", ""), "KM"));
            GetRoutDistane(tv_km, Double.parseDouble(mPreferences.getString("latitude2", "")), Double.parseDouble(mPreferences.getString("longitude2", "")), Double.parseDouble(nearbies1.get(position).getPlace_Latitude()), Double.parseDouble(nearbies1.get(position).getPlace_Longi()), nearbies1.get(position).getDist());
            String imageUrl = Constants.IMAGE_URL + nearbies1.get(position).getPlace_MainImage() + "&w=" + (width);

            Picasso.with(SearchResultPlaceDetailsActivity.this) //
                    .load(imageUrl) //
                    .resize(width, width)
                    .into(iv_detail);

            gv_detail1_search_result2.setOnItemClickListener(new com.ftl.tourisma.gallery1.AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(com.ftl.tourisma.gallery1.AdapterView<?> parent, View view, int position, long id) {
                    id1 = position;
                    if (mHandler != null) {
                        mHandler.removeCallbacks(mRunnable);
                    }
                    isForSimilarPlaces = true;
                    setDetailInfo(nearbies1.get(position));
                    mNearby = nearbies1.get(position);
                }
            });

            Log.i("System out", imageUrl);
            //  imageLoader2.displayImage(imageUrl, iv_detail, optionsSimple);

            return convertView;
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
                txtDistance.setText(Distance + Constants.showMessage(SearchResultPlaceDetailsActivity.this, mPreferences.getString("Lan_Id", ""), "KM"));
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }


        }
    }

}
