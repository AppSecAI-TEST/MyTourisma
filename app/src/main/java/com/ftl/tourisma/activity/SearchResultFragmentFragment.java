package com.ftl.tourisma.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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
import com.ftl.tourisma.FullPlaceImageViewActivity;
import com.ftl.tourisma.MapDetailFragment;
import com.ftl.tourisma.MyTorismaApplication;
import com.ftl.tourisma.R;
import com.ftl.tourisma.ShareFragmentActivity;
import com.ftl.tourisma.SimpleVrPanoramaActivity;
import com.ftl.tourisma.adapters.TimingAdapter;
import com.ftl.tourisma.custom_views.NormalBoldTextView;
import com.ftl.tourisma.custom_views.NormalTextView;
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

import static com.ftl.tourisma.utils.Constants.PlaceClosed;
import static com.ftl.tourisma.utils.Constants.PlaceOpenFor24Hours;
import static com.ftl.tourisma.utils.Constants.PlaceOpenWithAnyTime;

/**
 * Created by fipl11111 on 02-Mar-16.
 */
public class SearchResultFragmentFragment extends Fragment implements View.OnClickListener, post_sync.ResponseHandler {

    private static final int PLACE_LIKE_SEARCH = 1003;
    private static final String TAG = "SearchResultFragment";
    static int mCounter = -1;
    private static ArrayList<Nearby> nearbies = new ArrayList<>();
    private static String search;
    GalleryAdapter2 galleryAdapter2;
    private ImageView iv_back_search_result;
    private ArrayList<Nearby> nearbies1 = new ArrayList<>();
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
    private NormalTextView tv_similar_explore, txtSuggestPlace, txtStartNavigating;

    private Nearby nearByDetails;

    private JSONObjConverter jsonObjConverter;

    private ImageView imgSharePlace;
    private RelativeLayout rlVirtualTour;
    private PagerIndicator custom_indicator1;
    private SliderLayout sliderPlaceImages;
    private int PLace_LIKE_SIMILAR = 203;
    private View view;
    private MainActivity mainActivity;
    private boolean isForCategory;

    public static SearchResultFragmentFragment NewInstance(ArrayList<Nearby> nearbies1, String search1, boolean isForCategory) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("nearbies", nearbies1);
        bundle.putSerializable("search", search1);
        bundle.putBoolean("isForCategory", isForCategory);
        //search=search1;
        //nearbies=nearbies1;
        SearchResultFragmentFragment searchResultFragmentFragment = new SearchResultFragmentFragment();
        searchResultFragmentFragment.setArguments(bundle);
        return searchResultFragmentFragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mainActivity = (MainActivity) context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        nearbies = (ArrayList<Nearby>) bundle.getSerializable("nearbies");
        search = bundle.getString("search");
        isForCategory = bundle.getBoolean("isForCategory");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        //LocalBroadcastManager to refresh home page
        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(getActivity());
        Intent intent = new Intent("TAG_REFRESH_HOMEPAGE");
        localBroadcastManager.sendBroadcast(intent);

        view = inflater.inflate(R.layout.search_result, container, false);
//        nearbies = (ArrayList<Nearby>) mIntent.getSerializableExtra("nearbies");
//        search = mIntent.getStringExtra("search");
        jsonObjConverter = new JSONObjConverter();
        _24HourSDF = new SimpleDateFormat("HH:mm");
        _12HourSDF = new SimpleDateFormat("hh:mma");
        try {
            DisplayMetrics displaymetrics = new DisplayMetrics();
            getActivity().getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
            height = displaymetrics.heightPixels;
            width = displaymetrics.widthPixels;
        } catch (Exception e) {
            e.printStackTrace();
        }

        mPreferences = getActivity().getSharedPreferences(Constants.mPref, 0);
        mEditor = mPreferences.edit();

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getActivity())
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .denyCacheImageMultipleSizesInMemory()
                .diskCacheFileNameGenerator(new Md5FileNameGenerator())
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                .build();

        imageLoader.init(ImageLoaderConfiguration.createDefault(getActivity()));
        imageLoader2.init(ImageLoaderConfiguration.createDefault(getActivity()));
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
//            SnackbarManager.show(Snackbar.with(getActivity()).color(getResources().getColor(R.color.mBlue)).text(Constants.showMessage(getActivity(), mPreferences.getString("Lan_Id", ""), "NORECORD")));
        }
        return view;
    }

    private void initialisation() {
        imgSharePlace = (ImageView) view.findViewById(R.id.imgSharePlace);
        imgSharePlace.setOnClickListener(this);

        rlVirtualTour = (RelativeLayout) view.findViewById(R.id.rlVirtualTour);
        rlVirtualTour.setOnClickListener(this);
        custom_indicator1 = (PagerIndicator) view.findViewById(R.id.custom_indicator1);
        custom_indicator1.setIndicatorStyleResource(R.drawable.shape_cirlce_fill, R.drawable.shape_cirlce_unfill);

        txtSuggestPlace = (NormalTextView) view.findViewById(R.id.txtSuggestPlace);
        txtSuggestPlace.setText(Constants.showMessage(getActivity(), mPreferences.getString("Lan_Id", ""), "Suggest Place"));
        txtSuggestPlace.setOnClickListener(this);

        tv_fee_search_result = (NormalTextView) view.findViewById(R.id.tv_fee_explore);
        tv_fee_search_result.setText(Constants.showMessage(getActivity(), mPreferences.getString("Lan_Id", ""), "Fee"));

        txtStartNavigating = (NormalTextView) view.findViewById(R.id.txtStartNavigating);
        txtStartNavigating.setText(Constants.showMessage(getActivity(), mPreferences.getString("Lan_Id", ""), "StartNavigation"));

        tv_about_place_search_result = (NormalTextView) view.findViewById(R.id.tv_about_place_explore);
        tv_about_place_search_result.setText(Constants.showMessage(getActivity(), mPreferences.getString("Lan_Id", ""), "aboutplace"));

        tv_similar_search = (NormalTextView) view.findViewById(R.id.tv_similar_explore);
        tv_similar_search.setText(Constants.showMessage(getActivity(), mPreferences.getString("Lan_Id", ""), "similarplace"));

        tv_see_all_search_result = (NormalTextView) view.findViewById(R.id.tv_see_all_explore);
        tv_see_all_search_result.setText(Constants.showMessage(getActivity(), mPreferences.getString("Lan_Id", ""), "seeall"));

        tv_your_location_header5 = (NormalTextView) view.findViewById(R.id.tv_your_location_header5);
        tv_your_location_search_result = (NormalTextView) view.findViewById(R.id.tv_your_location_search_result);
        tv_your_location_search_result.setText(Constants.showMessage(getActivity(), mPreferences.getString("Lan_Id", ""), "locationtitle"));

        tv_search_result = (NormalTextView) view.findViewById(R.id.tv_search_result);
        tv_search_result.setText(Constants.showMessage(getActivity(), mPreferences.getString("Lan_Id", ""), "searchresuilt"));
        txtMessage = (NormalTextView) view.findViewById(R.id.txtMessage);

        txtMessage.setText(Constants.showMessage(getActivity(), mPreferences.getString("Lan_Id", ""), "No records available for this search"));

        ll_change_city = (LinearLayout) view.findViewById(R.id.ll_change_city);
        ll_change_city.setOnClickListener(this);

        dotLayout_detail_search_result2 = (LinearLayout) view.findViewById(R.id.dotLayout_detail);
//        iv_location_search_result2 = (ImageView) findViewById(R.id.iv_location);

        iv_search_result = (ImageView) view.findViewById(R.id.iv_search_result);
        iv_search_result.setOnClickListener(this);
        iv_search_map = (ImageView) view.findViewById(R.id.iv_search_map);
        iv_search_map.setOnClickListener(this);

        imgDownArrow = (ImageView) view.findViewById(R.id.imgDownArrow);
        ImageView iv_down_header5 = (ImageView) view.findViewById(R.id.iv_down_header5);
        iv_down_header5.setVisibility(View.GONE);
        tv_see_all_search_result.setOnClickListener(this);

        fab_search_result = (FloatingActionButton) view.findViewById(R.id.fab_search_result);
        fab_search_result.setOnClickListener(this);

        ll_search_result = (LinearLayout) view.findViewById(R.id.ll_search_result);
        llEmptyLayout11 = (LinearLayout) view.findViewById(R.id.llEmptyLayout11);
        ll_search_result1 = (LinearLayout) view.findViewById(R.id.ll_search_result1);
        ll_search_result2 = (LinearLayout) view.findViewById(R.id.ll_search_result2);

        tv_place = (NormalTextView) view.findViewById(R.id.tv_place);
        tv_place.setOnClickListener(this);
        tv_map_location = (NormalTextView) view.findViewById(R.id.tv_map_location);

        if (search.equalsIgnoreCase("Similar Places")) {
            // tv_place.setPadding(0, 10, 0, 0);
            tv_place.setText(search);
            tv_place.setAllCaps(true);
            ll_search_result.setVisibility(View.GONE);
            imgDownArrow.setVisibility(View.GONE);
            tv_your_location_search_result.setVisibility(View.GONE);
            if (mPreferences.getString(Preference.Pref_City, "").equalsIgnoreCase("")) {
                tv_map_location.setText(mPreferences.getString(Preference.Pref_Country, ""));
                tv_your_location_header5.setText(Constants.showMessage(getActivity(), mPreferences.getString("Lan_Id", ""), "locationtitle"));
            } else {
                tv_map_location.setText(mPreferences.getString(Preference.Pref_City, ""));
                tv_your_location_header5.setText(Constants.showMessage(getActivity(), mPreferences.getString("Lan_Id", ""), "locationtitle"));
            }
        } else {
            tv_your_location_search_result.setVisibility(View.VISIBLE);

            tv_place.setAllCaps(false);
            if (search.length() != 0) {
                if (isForCategory)
                    tv_place.setText(search);
                else
                    tv_place.setText("\"" + search + "\"");


            } else {
                tv_place.setText("");
            }
            if (mPreferences.getString(Preference.Pref_City, "").equalsIgnoreCase("")) {
                tv_your_location_header5.setText(mPreferences.getString(Preference.Pref_Country, ""));
            } else {
                tv_your_location_header5.setText(mPreferences.getString(Preference.Pref_City, ""));
            }
            if (search.length() != 0) {
                tv_map_location.setText("\"" + search + "\"");
            } else {
                tv_map_location.setText("");
            }
        }


        iv_back_search_result = (ImageView) view.findViewById(R.id.iv_back_search_result);
        iv_back_search_result.setOnClickListener(this);

        rv_search_result = (RecyclerView) view.findViewById(R.id.rv_search_result);
        rv_search_result.setHasFixedSize(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        rv_search_result.setLayoutManager(layoutManager);

        recyclerAdapter = new RecyclerAdapter(getActivity());
        rv_search_result.setAdapter(recyclerAdapter);

        tv_full_name_search_result2 = (NormalBoldTextView) view.findViewById(R.id.tv_full_name);
        tv_info_search_result2 = (NormalTextView) view.findViewById(R.id.tv_info);
        txtOpenNowVal = (NormalTextView) view.findViewById(R.id.txtOpenNowVal);
        txtDailyWorkingHours = (NormalTextView) view.findViewById(R.id.txtDailyWorkingHours);
        txtDailyWorkingHours.setText(Constants.showMessage(getActivity(), mPreferences.getString("Lan_Id", ""), "View Daily Working Hours"));
        txtDailyWorkingHours.setOnClickListener(this);

        tv_distance1_search_result2 = (NormalTextView) view.findViewById(R.id.tv_distance1);
        tv_discription_search_result2 = (NormalTextView) view.findViewById(R.id.tv_discription);
        gv_detail1_search_result2 = (com.ftl.tourisma.gallery1.Gallery) view.findViewById(R.id.gv_detail1);
        iv_back5 = (ImageView) view.findViewById(R.id.iv_back5);
        txt_add_to_fav = (NormalTextView) view.findViewById(R.id.txt_add_to_fav);

        listView_fees = (LinearLayout) view.findViewById(R.id.listView_fees);

    }

    @Override
    public void onClick(View v) {
        if (v == iv_back_search_result) {
            mainActivity.onBackPressed();

        } else if (v == ll_change_city) {
            if (mainActivity.mainTabHostFragment.vpFragment.getCurrentItem() == 1) {
                mainActivity.favouriteFragment.replaceLocationFragment();
            } else {
                mainActivity.exploreNearbyFragment.replaceLocationFragment();
            }


        } else if (v == iv_back5) {
            ll_search_result1.setVisibility(View.VISIBLE);
            ll_search_result2.setVisibility(View.GONE);
            // mHandler.removeCallbacks(mRunnable);
        } else if (v == fab_search_result) {
            Intent mIntent = new Intent(getActivity(), MapDetailFragment.class);
            mIntent.putExtra("nearbies", nearbies);
            mIntent.putExtra("title", search);
            startActivityForResult(mIntent, PLace_LIKE_SIMILAR);
        } else if (v == iv_search_result || v == iv_search_map) {
//            mainActivity.replaceSearchFragment();
            if (mainActivity.mainTabHostFragment.vpFragment.getCurrentItem() == 1) {
                mainActivity.favouriteFragment.replaceSearchFragment();
            } else {
                mainActivity.exploreNearbyFragment.replaceSearchFragment();
            }
        } else if (v == tv_see_all_search_result) {

            if (mainActivity.mainTabHostFragment.vpFragment.getCurrentItem() == 1) {
                mainActivity.favouriteFragment.replaceSearchResultFragment(nearbies1, "Similar Places");
            } else {
                mainActivity.exploreNearbyFragment.replaceSearchResultFragment(nearbies1, "Similar Places", false);
            }
        } else if (v == txtSuggestPlace) {
            suggestPlace();
        } else if (v == txtDailyWorkingHours) {
            openWeekDaysPopup();
        } else if (v == rlVirtualTour) {
            Intent intent = new Intent(getActivity(), SimpleVrPanoramaActivity.class);
            intent.putExtra("path", mNearby.getPlaceVRMainImage());
            intent.putExtra("path_1", mNearby.getVrimages());
            startActivity(intent);
        } else if (v == imgSharePlace) {
            Intent mIntent = new Intent(getActivity(), ShareFragmentActivity.class);
            String share1 = Constants.showMessage(getActivity(), mPreferences.getString("Lan_Id", ""), "share1");
            String share2 = Constants.showMessage(getActivity(), mPreferences.getString("Lan_Id", ""), "share2");
            String share3 = Constants.showMessage(getActivity(), mPreferences.getString("Lan_Id", ""), "share3");

            mIntent.putExtra("myMsg", share1 + " \"" + mPreferences.getString("User_Name", "") + "\" " + share2 + " \"" + mNearby.getPlace_Name() + "\" " + share3);
            startActivity(mIntent);

        }
    }

    public void suggestPlace() {
        try {
            Intent emailIntent = new Intent(Intent.ACTION_SEND);
            emailIntent.setData(Uri.parse("info@mytourisma.com"));
//            sendIntent.setClassName("com.google.android.gm", "com.google.android.gm.ComposeActivityGmail");
            emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{"info@mytourisma.com"});
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "myTourisma - Suggest new location");
            emailIntent.putExtra(Intent.EXTRA_TEXT, "Hello,\n" +
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
            emailIntent.setType("text/plain");
            final PackageManager pm = getActivity().getPackageManager();
            final List<ResolveInfo> matches = pm.queryIntentActivities(emailIntent, 0);
            ResolveInfo best = null;
            for (final ResolveInfo info : matches)
                if (info.activityInfo.packageName.endsWith(".gm") || info.activityInfo.name.toLowerCase().contains("gmail"))
                    best = info;
            if (best != null)
                emailIntent.setClassName(best.activityInfo.packageName, best.activityInfo.name);

            getActivity().startActivity(emailIntent);
        } catch (Exception e) {
            Utils.Log(TAG, "suggestPlace Exception: " + e.getLocalizedMessage());
        }
    }

    @Override
    public void onResponse(String response, String action) {
        try {
            if (action.equalsIgnoreCase("PlaceDetails")) {
                searchResponse(response);
            } else if (action.equalsIgnoreCase("AddFavorite")) {
//                getActivity().setResult(Activity.RESULT_OK, new Intent());
                addFavoriteResponse(response);
            } else if (action.equalsIgnoreCase("DeleteFavorite")) {
//                getActivity().setResult(Activity.RESULT_OK, new Intent());
                deleteFavoriteResponse(response);
            }
        } catch (Exception e) {
            Log.e(TAG, "onResponse Exception " + e.getLocalizedMessage());
        }
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
                                SpannableStringBuilder spannableStringBuilder = TimingFunction.checkYesterDayTiming(getActivity(), hourDetails, hourDetailsYesterday, mPreferences);
                                if (spannableStringBuilder != null)
                                    dayFoundStatus = 2;
                            }
                            break;
                        }
                    }

                    SpannableStringBuilder time = TimingFunction.getPlaceTiming(getActivity(), hourDetails, mPreferences);
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
            txtOpenNowVal.setText(Utils.getSpannableString(Constants.showMessage(getActivity(), mPreferences.getString("Lan_Id", ""), "Open Now"), Utils.getColor(getActivity(), R.color.mGreen), true, 0));

            // txtOpenNowVal.setText("");
        } else if (dayFoundStatus == 0 || dayFoundStatus == 1) {

            txtOpenNowVal.setText(Utils.getSpannableString(Constants.showMessage(getActivity(), mPreferences.getString("Lan_Id", ""), "Closed"), Utils.getColor(getActivity(), android.R.color.holo_red_dark), true, 0));
//                    txtOpenNowVal.setText("");
        }


        final int mId;

        mId = id1;
        if (nearby.getFav_Id().equalsIgnoreCase("0")) {
            txt_add_to_fav.setCompoundDrawablesWithIntrinsicBounds(Utilities.getDrawable(getActivity(), R.drawable.ic_favourite_default), null, null, null);
            txt_add_to_fav.setText(Constants.showMessage(getActivity(), mPreferences.getString("Lan_Id", ""), "Add to Favourites"));
            like = 0;
        } else {
            txt_add_to_fav.setText(Constants.showMessage(getActivity(), mPreferences.getString("Lan_Id", ""), "Remove Favourite"));

            txt_add_to_fav.setCompoundDrawablesWithIntrinsicBounds(Utilities.getDrawable(getActivity(), R.drawable.ic_favourite_active), null, null, null);

            like = 1;
        }

        txt_add_to_fav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPreferences.getString("User_Id", "").equalsIgnoreCase("0")) {
                    ((MainActivity) getActivity()).guestSnackToast();
                } else {
                    if (like == 0) {
                        txt_add_to_fav.setText(Constants.showMessage(getActivity(), mPreferences.getString("Lan_Id", ""), "Remove Favourite"));

                        txt_add_to_fav.setCompoundDrawablesWithIntrinsicBounds(Utilities.getDrawable(getActivity(), R.drawable.ic_favourite_active), null, null, null);

                        addFavoriteCall(nearby.getPlace_Id());
                        mFlag = mId;
                    } else if (like == 1) {
                        txt_add_to_fav.setText(Constants.showMessage(getActivity(), mPreferences.getString("Lan_Id", ""), "Add to Favourites"));
                        txt_add_to_fav.setCompoundDrawablesWithIntrinsicBounds(Utilities.getDrawable(getActivity(), R.drawable.ic_favourite_default), null, null, null);

                        deleteFavoriteCall(nearby.getFav_Id());
                        mFlag = mId;
                    }
                    mCounter = 1;
                }
            }
        });

        tv_distance1_search_result2.setText(nearby.getDistance() + Constants.showMessage(getActivity(), mPreferences.getString("Lan_Id", ""), "KM"));
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

        strImg1 = string.split(",");
        if (strImg1.length >= 0) {
            sliderPlaceImages = (SliderLayout) view.findViewById(R.id.sliderPlaceImages);
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
                DefaultSliderView textSliderView = new DefaultSliderView(getActivity());
                // initialize a SliderLayout
                textSliderView
                        .description(mNearby.getPlace_Name())
                        .image(imageUrl)
                        .setScaleType(BaseSliderView.ScaleType.Fit)
                        .setOnSliderClickListener(new BaseSliderView.OnSliderClickListener() {
                            @Override
                            public void onSliderClick(BaseSliderView slider1) {
                                Intent intent = new Intent(getActivity(), FullPlaceImageViewActivity.class);
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
//

                String uri = String.format(Locale.ENGLISH, "http://maps.google.com/maps?saddr=%f,%f&daddr=%f,%f", Double.parseDouble(mPreferences.getString("latitude1", "")), Double.parseDouble(mPreferences.getString("longitude1", "")), Double.parseDouble(nearby.getPlace_Latitude()), Double.parseDouble(nearby.getPlace_Longi()));
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
                startActivity(intent);
            }
        });

    }

    private void addFeesCustomViews(String pohCharges, String price_description) {
        listView_fees.removeAllViews();
        if (price_description == null || price_description.equals("") || price_description.equalsIgnoreCase("null") || price_description.trim().length() == 0) {


            price_description = Constants.showMessage(getActivity(), mPreferences.getString("Lan_Id", ""), "No Fees data available");
        }
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.row_fees, null);
        NormalTextView txtFees = (NormalTextView) view.findViewById(R.id.txtFees);
        txtFees.setText("");
//            price_description=price_description.replace("//","/");getSpannableString(price_description, false);
//            price_description=price_description.replace("//","/")
        txtFees.append(getSpannableString(price_description, false));
        listView_fees.addView(view);

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
        if (CommonClass.hasInternetConnection(getActivity())) {
            String url = Constants.SERVER_URL + "json.php?action=PlaceDetails";
            String json = "[{\"Lan_Id\":\"" + mPreferences.getString("Lan_Id", "") + "\",\"User_Id\":\"" + mPreferences.getString("User_Id", "") + "\",\"Current_Latitude\":\"" + mPreferences.getString("latitude2", "") + "\",\"Current_Longitude\":\"" + mPreferences.getString("longitude2", "") + "\",\"Place_Id\":\"" + Place_Id + "\"}]";
//            Log.d("System out", "PlaceDetails " + json);
//            new PostSync(this, "PlaceDetails").execute(url, json);
            new PostSync(getActivity(), "PlaceDetails", SearchResultFragmentFragment.this).execute(url, json);
        } else {
            Intent intent = new Intent(getActivity(), NoInternet.class);
            startActivity(intent);
            //SnackbarManager.show(Snackbar.with(getActivity()).color(getResources().getColor(R.color.mBlue)).text(Constants.showMessage(getActivity(), mPreferences.getString("Lan_Id", ""), "NOINTERNET")));
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
            if (resultCode == getActivity().RESULT_OK) {
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
            if (resultCode == getActivity().RESULT_OK) {
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
                        nearby.setDistance(Utilities.GetRoutDistane(Double.parseDouble(mPreferences.getString("latitude2", "")), Double.parseDouble(mPreferences.getString("longitude2", "")), Double.parseDouble(nearby.getPlace_Latitude()), Double.parseDouble(nearby.getPlace_Longi()), nearby.getDist()));

                        nearbies1.add(nearby);
                    }
                }
                nearbies1 = Utilities.sortLocations(nearbies1);
            }


            galleryAdapter2 = new GalleryAdapter2(getActivity());
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
            WindowManager wm = (WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE);
            Display display = wm.getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            int width = size.x;
            int height = size.y;
            dialog = new Dialog(getActivity());
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
            txtTitle.setText(Constants.showMessage(getActivity(), mPreferences.getString("Lan_Id", ""), "Time Details Header"));
            ImageView iv_menu_close = (ImageView) dialog.findViewById(R.id.iv_menu_close);
            iv_menu_close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            listView.setAdapter(new TimingAdapter(stringArrayList, getActivity()));

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
                weekDaysModel.setTime(TimingFunction.getTimingWeekDayFormat(getActivity(), hourDetails, mPreferences, hourDetailses));
                weekDaysModel.setDay(Constants.showMessage(getActivity(), mPreferences.getString("Lan_Id", ""), hourDetails.getPOHKey()));

                timingArrayList.add(weekDaysModel);
            }
        }

        return timingArrayList;
    }

    private void addFavoriteCall(String Place_Id) {
        if (CommonClass.hasInternetConnection(getActivity())) {
            String url = Constants.SERVER_URL + "json.php?action=AddFavorite";
            String json = "[{\"User_Id\":\"" + mPreferences.getString("User_Id", "") + "\",\"Place_Id\":\"" + Place_Id + "\"}]";
//            Log.d("System out", "AddFavorite " + json);
            new PostSync(getActivity(), "AddFavorite", SearchResultFragmentFragment.this).execute(url, json);
        } else {
            Intent intent = new Intent(getActivity(), NoInternet.class);
            startActivity(intent);
            //SnackbarManager.show((Snackbar) Snackbar.with(getActivity()).color(getResources().getColor(R.color.mBlue)).text(Constants.showMessage(getActivity(), mPreferences.getString("Lan_Id", ""), "NOINTERNET")));
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
                        SnackbarManager.show(Snackbar.with(getActivity()).color(getResources().getColor(R.color.mBlue)).text(Constants.showMessage(getActivity()
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
        if (CommonClass.hasInternetConnection(getActivity())) {
            String url = Constants.SERVER_URL + "json.php?action=DeleteFavorite";
            String json = "[{\"User_Id\":\"" + mPreferences.getString("User_Id", "") + "\",\"Fav_Id\":\"" + Fav_Id + "\"}]";
//            Log.d("System out", "DeleteFavorite " + json);
            new PostSync(getActivity(), "DeleteFavorite", SearchResultFragmentFragment.this).execute(url, json);
        } else {
            Intent intent = new Intent(getActivity(), NoInternet.class);
            startActivity(intent);
            //SnackbarManager.show((Snackbar) Snackbar.with(getActivity()).color(getResources().getColor(R.color.mBlue)).text(Constants.showMessage(getActivity(), mPreferences.getString("Lan_Id", ""), "NOINTERNET")));
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
                        SnackbarManager.show(Snackbar.with(getActivity()).color(getResources().getColor(R.color.mBlue)).text(Constants.showMessage(getActivity(), mPreferences.getString("Lan_Id", ""), "Removefavorite")));
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
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
            viewHolder.txtShare.setText(Constants.showMessage(getActivity(), mPreferences.getString("Lan_Id", ""), "Share"));
            viewHolder.txtFav.setText(Constants.showMessage(getActivity(), mPreferences.getString("Lan_Id", ""), "Favourite"));
            String imageURL = Constants.IMAGE_URL + nearbies.get(position).getPlace_MainImage() + "&w=" + (width - 30);
//            Log.d("System out", imageURL);
            viewHolder.iv_nearby_explorer.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, (height * 60) / 100));
//            imageLoader.displayImage(imageURL, viewHolder.iv_nearby_explorer, options);
            Picasso.with(getActivity()).load(imageURL).
                    resize(width, (height * 60) / 100)
                    .into(viewHolder.iv_nearby_explorer);
            viewHolder.tv_near.setText(nearbies.get(position).getPlace_Name());
            if (nearbies.get(position).getFree_entry().equals("0")) {
                viewHolder.tv_ticket.setText(Constants.showMessage(getActivity(), mainActivity.getPreferences().getString("Lan_Id", ""), "Tickets") + ": " + Constants.showMessage(getActivity(), mainActivity.getPreferences().getString("Lan_Id", ""), "Check Details"));
            } else if (nearbies.get(position).getFree_entry().equals("1")) {
                viewHolder.tv_ticket.setText(Constants.showMessage(getActivity(), mainActivity.getPreferences().getString("Lan_Id", ""), "Tickets") + ": " + Constants.showMessage(getActivity(), mainActivity.getPreferences().getString("Lan_Id", ""), "Free Entry"));
            }
            viewHolder.txtCategory.setText(Constants.showMessage(getActivity(), mPreferences.getString("Lan_Id", ""), "Category") + ": " + nearbies.get(position).getCategory_Name());

            viewHolder.llView.setId(position);
            viewHolder.llView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    id1 = v.getId();
//
                    if (mainActivity.mainTabHostFragment.vpFragment.getCurrentItem() == 1) {
                        mainActivity.favouriteFragment.replacePlaceDetailsFragment(nearbies.get(id1).getPlace_Id(), tv_map_location.getText().toString());
                    } else {
                        mainActivity.exploreNearbyFragment.replacePlaceDetailsFragment(nearbies.get(id1).getPlace_Id(), tv_map_location.getText().toString());
                    }
                    mNearby = nearbies.get(v.getId());
                }
            });
            viewHolder.iv_nearby_explorer.setId(position);
            viewHolder.iv_nearby_explorer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    id1 = v.getId();

                    if (mainActivity.mainTabHostFragment.vpFragment.getCurrentItem() == 1) {
                        mainActivity.favouriteFragment.replacePlaceDetailsFragment(nearbies.get(id1).getPlace_Id(), tv_map_location.getText().toString());
                    } else {
                        mainActivity.exploreNearbyFragment.replacePlaceDetailsFragment(nearbies.get(id1).getPlace_Id(), tv_map_location.getText().toString());
                    }
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

                    Intent mIntent = new Intent(getActivity(), ShareFragmentActivity.class);
                    String share1 = Constants.showMessage(getActivity(), mPreferences.getString("Lan_Id", ""), "share1");
                    String share2 = Constants.showMessage(getActivity(), mPreferences.getString("Lan_Id", ""), "share2");
                    String share3 = Constants.showMessage(getActivity(), mPreferences.getString("Lan_Id", ""), "share3");

                    mIntent.putExtra("myMsg", share1 + share2 + " \"" + nearbies.get(v.getId()).getPlace_Name() + "\" " + share3);
                    startActivity(mIntent);
//                        Intent intent = new Intent(Intent.ACTION_SEND);
//                        intent.setType("text/plain");
//                        startActivity(Intent.createChooser(intent, "Share with"));
                }
            });

            viewHolder.rl_navigator.setId(position);

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
                        ((MainActivity) getActivity()).guestSnackToast();
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
                                viewHolder.tv_timing.setText(Constants.showMessage(getActivity(), mPreferences.getString("Lan_Id", ""), "Timing") + ": " + _24HourSDF.format(_24HourDt) + " " + Constants.showMessage(getActivity(), mPreferences.getString("Lan_Id", ""), "TO") + " " + _24HourSDF.format(_24HourDt1));
                            } else {
                                viewHolder.tv_timing.setText(Constants.showMessage(getActivity(), mPreferences.getString("Lan_Id", ""), "Timing") + ": -");
                                dayFoundStatus = 1;
                            }
                        }

                        if (hourDetails.getPOHCharges() != null && !hourDetails.getPOHCharges().equals("") && !hourDetails.getPOHCharges().equalsIgnoreCase("null")) {
                            isTicketSet = true;
//                            viewHolder.tv_ticket.setText(Constants.showMessage(getActivity(), mPreferences.getString("Lan_Id", ""), "Tickets") + ": " + hourDetails.getPOHCharges());
                        }
                        break;
                    } else {
                        viewHolder.tv_timing.setText(Constants.showMessage(getActivity(), mPreferences.getString("Lan_Id", ""), "Timing") + ": -");
                        dayFoundStatus = 1;
                    }
                }
            }
            if (dayFoundStatus == 2) {
                viewHolder.tv_timing.setText(Constants.showMessage(getActivity(), mPreferences.getString("Lan_Id", ""), "Timing") + ": " + Constants.showMessage(getActivity(), mPreferences.getString("Lan_Id", ""), "Open Now"));

                // txtOpenNowVal.setText("");
            } else if (dayFoundStatus == 0) {
                viewHolder.tv_timing.setText(Constants.showMessage(getActivity(), mPreferences.getString("Lan_Id", ""), "Timing") + ": " + Constants.showMessage(getActivity(), mPreferences.getString("Lan_Id", ""), "Closed"));
//                    txtOpenNowVal.setText("");
            }
            if (!isTicketSet) {
//                viewHolder.tv_ticket.setText(Constants.showMessage(getActivity(), mPreferences.getString("Lan_Id", ""), "Tickets") + ": -");
            }

//            viewHolder.tv_distance.setText(nearbies.get(position).getDistance() + Constants.showMessage(getActivity(), mPreferences.getString("Lan_Id", ""), "KM"));
            viewHolder.tv_distance.setText(Utilities.GetRoutDistane(Double.parseDouble(mPreferences.getString("latitude2", "")), Double.parseDouble(mPreferences.getString("longitude2", "")), Double.parseDouble(nearbies.get(position).getPlace_Latitude()), Double.parseDouble(nearbies.get(position).getPlace_Longi()), nearbies.get(position).getDist()) + Constants.showMessage(getActivity(), mPreferences.getString("Lan_Id", ""), "KM"));
        }

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
                txtShare = (NormalTextView) convertView.findViewById(R.id.txtShare);
                txtFav = (NormalTextView) convertView.findViewById(R.id.txtFav);
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
            tv_km.setText(nearbies1.get(position).getDistance() + Constants.showMessage(getActivity(), mPreferences.getString("Lan_Id", ""), "KM"));

           /* double d=Utilities.GetRoutDistane(Double.parseDouble(mPreferences.getString("latitude2", "")), Double.parseDouble(mPreferences.getString("longitude2", "")), Double.parseDouble(nearbies1.get(position).getPlace_Latitude()), Double.parseDouble(nearbies1.get(position).getPlace_Longi()),nearbies1.get(position).getDist());

            if(d!=-1){
                tv_km.setText(d + Constants.showMessage(getActivity(), mPreferences.getString("Lan_Id", ""), "KM"));

            }*/
            String imageUrl = Constants.IMAGE_URL + nearbies1.get(position).getPlace_MainImage() + "&w=" + (width - 30);

            Picasso.with(getActivity()) //
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
