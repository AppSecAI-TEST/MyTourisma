package com.ftl.tourisma.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.daimajia.slider.library.Indicators.PagerIndicator;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.DefaultSliderView;
import com.daimajia.slider.library.Transformers.BaseTransformer;
import com.daimajia.slider.library.Tricks.ViewPagerEx;
import com.ftl.tourisma.FullPlaceImageViewActivity;
import com.ftl.tourisma.MapDetailFragment;
import com.ftl.tourisma.MyTorismaApplication;
import com.ftl.tourisma.OnInfoWindowElemTouchListener;
import com.ftl.tourisma.R;
import com.ftl.tourisma.SelectLocationFragmentActivity;
import com.ftl.tourisma.ShareFragmentActivity;
import com.ftl.tourisma.SimpleVrPanoramaActivity;
import com.ftl.tourisma.adapters.TimingAdapter;
import com.ftl.tourisma.custom_views.NormalBoldTextView;
import com.ftl.tourisma.custom_views.NormalTextView;
import com.ftl.tourisma.database.FeesDetails;
import com.ftl.tourisma.database.Nearby;
import com.ftl.tourisma.minterface.MapFragmentInterface;
import com.ftl.tourisma.minterface.Updatable;
import com.ftl.tourisma.models.HourDetails;
import com.ftl.tourisma.models.WeekDaysModel;
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
import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.SnackbarManager;
import com.nostra13.universalimageloader.cache.disc.naming.FileNameGenerator;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.display.BitmapDisplayer;
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
import java.util.Locale;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;
import static com.ftl.tourisma.utils.Constants.PlaceClosed;
import static com.ftl.tourisma.utils.Constants.PlaceOpenFor24Hours;
import static com.ftl.tourisma.utils.Constants.PlaceOpenWithAnyTime;

//import android.util.Log;

public class FavouriteFragment1 extends Fragment implements View.OnClickListener, Updatable, post_sync.ResponseHandler, ViewPagerEx.OnPageChangeListener {

    private static final int PLACE_LIKE_FAV = 1002;
    private static final String TAG = "FavouriteFragment";

    private FloatingActionButton fb_favorite;
    private RecyclerView rv_favorite;
    private MapFragmentInterface mapFragmentInterface;
    private SharedPreferences mPreferences;
    private SharedPreferences.Editor mEditor;
    private ArrayList<Nearby> nearbies = new ArrayList();
    private Nearby nearby;
    private DisplayImageOptions options, optionsSimple;
    private ImageLoader imageLoader = ImageLoader.getInstance();
    private int width, height, mFlag = 0;
    private RecyclerAdapter recyclerAdapter;
    private SimpleDateFormat _24HourSDF, _12HourSDF;
    private String _24HourTime, _24HourTime1;
    private Date _24HourDt, _24HourDt1;

    private LinearLayout dotLayout_detail_my_favorite3, llEmptyLayout, ll_see_all_my_favorite3, ll_my_favourite1, ll_my_favorite3;

    private int pos = 0;

    private OnInfoWindowElemTouchListener infoButtonListener;
    private ArrayList<Nearby> nearbies1 = new ArrayList();
    private NormalTextView tv_map_location, txtSuggest, tv_my_favorite_list, tv_your_location_header5, tv_about_place_favorite, txt_add_to_fav, tv_similar_favorite, tv_see_all_favorite, tv_total_favorite, txtDailyWorkingHours, txtOpenNowVal, tv_info_my_favorite3, tv_discription_my_favorite3, tv_distance1_my_favorite3, txtStartNavigating;
    private NormalBoldTextView tv_full_name_my_favorite3;
    private com.ftl.tourisma.gallery1.Gallery gv_detail1_my_favorite3;
    private String[] strImg1;
    private String Category_Name, Category_Id;
    private int id;
    private ImageView[] mDotsText1;

    private ImageView iv_back5, iv_search_favorite, iv_back_favorite, iv_search_map;

    MainActivity mainActivity;
    private Handler mHandler;
    private Runnable mRunnable;
    private Nearby mNearby = new Nearby();
    static int mCounter = -1;
    GalleryAdapter2 galleryAdapter2;
    private Nearby nearByDetails;
    private Dialog dialog;
    private ArrayList<HourDetails> hourDetailses;
    private View view;
    private JSONObjConverter jsonObjConverter;
    private SliderLayout sliderPlaceImages;
    private PagerIndicator custom_indicator1;
    //    private ImageView imgSharePlace;
//    private RelativeLayout rlVirtualTour;
    private ImageView iv_back3;

    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mPreferences = getActivity().getSharedPreferences(Constants.mPref, 0);
        mEditor = mPreferences.edit();
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

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getActivity()).threadPriority(3).denyCacheImageMultipleSizesInMemory().diskCacheFileNameGenerator((FileNameGenerator) new Md5FileNameGenerator()).tasksProcessingOrder(QueueProcessingType.LIFO).build();
        imageLoader.init(ImageLoaderConfiguration.createDefault(getActivity()));
        optionsSimple = new DisplayImageOptions.Builder()
                .showImageForEmptyUri(null)
                .displayer(new SimpleBitmapDisplayer())
                .showImageOnFail(null).cacheInMemory(true)
                .cacheOnDisk(true).build();
        options = new DisplayImageOptions.Builder().showImageForEmptyUri(null).displayer((BitmapDisplayer) new RoundedBitmapDisplayer(10)).showImageOnFail(null).cacheInMemory(true).cacheOnDisk(true).build();
        ImageLoader.getInstance().init(config);
//        Log.d("System out", "Favorite Fragment");
    }

    @Nullable
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.my_favourite, container, false);
//        iv_like_detail_my_favorite3 = (ImageView) view.findViewById(R.id.iv_like_detail_my_favorite3);
        return view;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initView();

        getFavoriteCall();
    }

    private void initView() {

//        imgSharePlace = (ImageView) view.findViewById(R.id.imgSharePlace);
//        imgSharePlace.setOnClickListener(this);

//        rlVirtualTour = (RelativeLayout) view.findViewById(R.id.rlVirtualTour);
//        rlVirtualTour.setOnClickListener(this);

        iv_search_map = (ImageView) view.findViewById(R.id.iv_search_map);
        iv_search_map.setOnClickListener(this);

//        ll_header5_location = (LinearLayout) view.findViewById(R.id.ll_header5_location);
//        ll_header5_location.setOnClickListener(this);

        txtSuggest = (NormalTextView) view.findViewById(R.id.txtSuggest);
        txtSuggest.setText(Constants.showMessage(getActivity(), mPreferences.getString("Lan_Id", ""), "Suggest Location"));

        tv_my_favorite_list = (NormalTextView) view.findViewById(R.id.tv_my_favorite_list);
        tv_my_favorite_list.setText(Constants.showMessage(getActivity(), mPreferences.getString("Lan_Id", ""), "myfavourittitle"));

        txtStartNavigating = (NormalTextView) view.findViewById(R.id.txtStartNavigating);
        txtStartNavigating.setText(Constants.showMessage(getActivity(), mPreferences.getString("Lan_Id", ""), "StartNavigation"));

        tv_about_place_favorite = (NormalTextView) view.findViewById(R.id.tv_about_place_explore);
        tv_about_place_favorite.setText(Constants.showMessage(getActivity(), mPreferences.getString("Lan_Id", ""), "aboutplace"));

        tv_similar_favorite = (NormalTextView) view.findViewById(R.id.tv_similar_explore);
        tv_similar_favorite.setText(Constants.showMessage(getActivity(), mPreferences.getString("Lan_Id", ""), "similarplace"));

        tv_see_all_favorite = (NormalTextView) view.findViewById(R.id.tv_see_all_explore);
        tv_see_all_favorite.setText(Constants.showMessage(getActivity(), mPreferences.getString("Lan_Id", ""), "seeall"));

        NormalTextView txtMessage = (NormalTextView) view.findViewById(R.id.txtMessage);
        NormalTextView txtOk = (NormalTextView) view.findViewById(R.id.txtOk);
        txtOk.setVisibility(View.GONE);
        //txtMessage.setText(Constants.showMessage(getActivity(), mPreferences.getString("Lan_Id", ""), "No records available for this place"));
        llEmptyLayout = (LinearLayout) view.findViewById(R.id.llEmptyLayout);

        txtMessage.setText(Constants.showMessage(getActivity(), mPreferences.getString("Lan_Id", ""), "No favourites found"));

        ll_see_all_my_favorite3 = (LinearLayout) view.findViewById(R.id.ll_see_all);
        ll_see_all_my_favorite3.setOnClickListener(this);

        iv_back_favorite = (ImageView) view.findViewById(R.id.iv_back_favorite);
        iv_back_favorite.setOnClickListener(this);

//        iv_location_my_favorite3 = (ImageView) view.findViewById(R.id.iv_location);

        dotLayout_detail_my_favorite3 = (LinearLayout) view.findViewById(R.id.dotLayout_detail);
        tv_total_favorite = (NormalTextView) view.findViewById(R.id.tv_total_favorite);

        tv_total_favorite.setText(Constants.showMessage(getActivity(), mPreferences.getString("Lan_Id", ""), "searchresuilt"));
        tv_your_location_header5 = (NormalTextView) view.findViewById(R.id.tv_your_location_header5);

        tv_your_location_header5.setText(Constants.showMessage(getActivity(), mPreferences.getString("Lan_Id", ""), "locationtitle"));

        iv_search_favorite = (ImageView) view.findViewById(R.id.iv_search_favorite);
        iv_search_favorite.setOnClickListener(this);

        tv_map_location = (NormalTextView) view.findViewById(R.id.tv_map_location);

//        tv_map_location.setText(mPreferences.getString(Preference.Pref_City, ""));

        if (mPreferences.getString(Preference.Pref_City, "").equalsIgnoreCase("")) {
            tv_map_location.setText(mPreferences.getString(Preference.Pref_Country, ""));
        } else {
            tv_map_location.setText(mPreferences.getString(Preference.Pref_City, ""));
        }

        fb_favorite = (FloatingActionButton) view.findViewById(R.id.fb_favorite);
        fb_favorite.setOnClickListener(this);
        rv_favorite = (RecyclerView) view.findViewById(R.id.rv_favorite);
        rv_favorite.setHasFixedSize(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        rv_favorite.setLayoutManager(layoutManager);

        ll_my_favourite1 = (LinearLayout) view.findViewById(R.id.ll_my_favorite1);
        ll_my_favorite3 = (LinearLayout) view.findViewById(R.id.ll_my_favorite3);
        tv_full_name_my_favorite3 = (NormalBoldTextView) view.findViewById(R.id.tv_full_name);
        tv_info_my_favorite3 = (NormalTextView) view.findViewById(R.id.tv_info);
        txtOpenNowVal = (NormalTextView) view.findViewById(R.id.txtOpenNowVal);

        txtDailyWorkingHours = (NormalTextView) view.findViewById(R.id.txtDailyWorkingHours);
        txtDailyWorkingHours.setText(Constants.showMessage(getActivity(), mPreferences.getString("Lan_Id", ""), "View Daily Working Hours"));
        txtDailyWorkingHours.setOnClickListener(this);
        tv_distance1_my_favorite3 = (NormalTextView) view.findViewById(R.id.tv_distance1);
        tv_discription_my_favorite3 = (NormalTextView) view.findViewById(R.id.tv_discription);
        txt_add_to_fav = (NormalTextView) view.findViewById(R.id.txt_add_to_fav);

        gv_detail1_my_favorite3 = (com.ftl.tourisma.gallery1.Gallery) view.findViewById(R.id.gv_detail1);
        iv_back5 = (ImageView) view.findViewById(R.id.iv_back5);
        iv_back5.setOnClickListener(this);

//        listView_fees = (LinearLayout) view.findViewById(R.id.listView_fees);
        custom_indicator1 = (PagerIndicator) view.findViewById(R.id.custom_indicator1);
        custom_indicator1.setIndicatorStyleResource(R.drawable.shape_cirlce_fill, R.drawable.shape_cirlce_unfill);
    }

    private void getFavoriteCall() {
        if (CommonClass.hasInternetConnection(getActivity())) {
            String url = Constants.SERVER_URL + "json.php?action=GetFavorites";
            String json = "[{\"User_Id\":\"" + mPreferences.getString("User_Id", "") + "\",\"Current_Latitude\":\"" + mPreferences.getString("latitude2", "") + "\",\"Current_Longitude\":\"" + mPreferences.getString("longitude2", "") + "\",\"Lan_Id\":\"" + mPreferences.getString("Lan_Id", "") + "\"}]";
            new post_sync(getActivity(), "GetFavorites", FavouriteFragment1.this, true).execute(url, json);

        } else {
            SnackbarManager.show((Snackbar) Snackbar.with(getActivity()).color(Utils.getColor(getActivity(), R.color.mBlue)).text(Constants.showMessage(getActivity(), mPreferences.getString("Lan_Id", ""), "NOINTERNET")));
        }
    }

    @Override
    public void onResponse(String response, String action) {
        try {
            if (action.equalsIgnoreCase("GetFavorites")) {
                getFavoriteResponse(response);
            } else if (action.equalsIgnoreCase("AddFavorite1")) {
                addFavoriteResponse(response);
            } else if (action.equalsIgnoreCase("PlaceDetailsFav")) {
                searchResponse(response);
            } else if (action.equalsIgnoreCase("DeleteFavorite1")) {
                deleteFavoriteResponse(response);
            }
        } catch (Exception e) {
            Log.e(TAG, "onResponse Exception " + e.getLocalizedMessage());
        }
    }

    public void onClick(View v) {
        if (v == fb_favorite) {
            Intent mIntent = new Intent(getActivity(), (Class) MapDetailFragment.class);
            mIntent.putExtra("nearbies", nearbies);
            if (nearbies.size() == 1) {
                mIntent.putExtra("title", Constants.showMessage(getActivity(), mPreferences.getString("Lan_Id", ""), "Spotone"));
            } else {
                mIntent.putExtra("title", Constants.showMessage(getActivity(), mPreferences.getString("Lan_Id", ""), "spotmultipale"));
            }
            startActivity(mIntent);
        } else if (v == iv_back5) {
            ll_my_favourite1.setVisibility(View.VISIBLE);
            ll_my_favorite3.setVisibility(View.GONE);
            // Constants.mStaticFavCall = 0;
            update();
        } else if (v == iv_search_favorite || v == iv_search_map) {
            mainActivity.favouriteFragment.replaceSearchFragment();

        } else if (v == iv_back_favorite) {
            //TODO
//            mainActivity.setFragment(0);
        } else if (v == ll_see_all_my_favorite3) {
            mainActivity.favouriteFragment.replaceSearchResultFragment(nearbies1, "Similar Places");
//            Intent mIntent = new Intent(getActivity(), SearchResultFragmentActivity.class);
//            mIntent.putExtra("nearbies", nearbies1);
//            mIntent.putExtra("search", "Similar Places");
//            startActivity(mIntent);
        } else if (v == txtDailyWorkingHours) {
            openWeekDaysPopup();
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        Log.d(TAG, "FragmentVisible " + isVisibleToUser);
        if (isVisibleToUser) {
            getFavoriteCall();
        }
    }

    public void onAttach(Context activity) {
        super.onAttach(activity);
        mainActivity = (MainActivity) getActivity();

    }

    public void update() {

        if (mPreferences.getString("User_Id", "").equalsIgnoreCase("0")) {
            llEmptyLayout.setVisibility(View.VISIBLE);
            rv_favorite.setVisibility(View.GONE);

        } else {
            getFavoriteCall();
        }
    }

    public void getFavoriteResponse(String resultString) {
//        Log.d((String) "System out", (String) ("GetFavorites " + resultString));
        nearbies = new ArrayList<>();
        if (resultString.length() > 2 && resultString.length() > 2) {
            try {
                JSONArray jsonArray = new JSONArray(resultString);
                for (int i = 0; i < jsonArray.length(); ++i) {
                    nearby = jsonObjConverter.convertJsonToNearByObj(jsonArray.optJSONObject(i));
                    nearby.setDistance(Utilities.GetRoutDistane(Double.parseDouble(mPreferences.getString("latitude2", "")), Double.parseDouble(mPreferences.getString("longitude2", "")), Double.parseDouble(nearby.getPlace_Latitude()), Double.parseDouble(nearby.getPlace_Longi()), nearby.getDist()));

                    nearbies.add(nearby);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            nearbies = Utilities.sortLocations(nearbies);
        }
        setFavList();
        Constants.mStaticFavCall = 1;
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


    private void setFavList() {
//        Log.d((String) "System out", (String) ("fb_favorite size " + nearbies.size()));
        if (nearbies.size() <= 0) {
            fb_favorite.setVisibility(View.GONE);
        } else {
//            fb_favorite.setVisibility(View.GONE);
            fb_favorite.setVisibility(View.VISIBLE);
        }
        if (nearbies.size() == 1) {
            tv_total_favorite.setText("\"1 " + Constants.showMessage(getActivity(), mPreferences.getString("Lan_Id", ""), "placecount") + "\"");
        } else if (nearbies.size() > 0) {
            tv_total_favorite.setText(("\"" + nearbies.size() + " " + Constants.showMessage(getActivity(), mPreferences.getString("Lan_Id", ""), "placecounts") + "\""));
        } else {
            tv_total_favorite.setText(("\"" + 0 + " " + Constants.showMessage(getActivity(), mPreferences.getString("Lan_Id", ""), "placecounts") + "\""));
        }
        if (nearbies.size() == 0) {
            llEmptyLayout.setVisibility(View.VISIBLE);
            rv_favorite.setVisibility(View.GONE);
        } else {
            rv_favorite.setVisibility(View.VISIBLE);
            llEmptyLayout.setVisibility(View.GONE);
            recyclerAdapter = new RecyclerAdapter(getActivity());
            rv_favorite.setAdapter(null);
            rv_favorite.setAdapter(recyclerAdapter);
        }

    }

    private void addFavoriteCall(String Place_Id) {
        if (CommonClass.hasInternetConnection(getActivity())) {
            String url = Constants.SERVER_URL + "json.php?action=AddFavorite";
            String json = "[{\"User_Id\":\"" + mPreferences.getString("User_Id", "") + "\",\"Place_Id\":\"" + Place_Id + "\"}]";
//            Log.d((String) "System out", (String) ("AddFavorite " + json));
//            new post_sync(this, "AddFavorite1").execute(url, json);
            new post_sync(getActivity(), "AddFavorite1", FavouriteFragment1.this, true).execute(url, json);

        } else {
            Intent intent = new Intent(getActivity(), NoInternet.class);
            startActivity(intent);
            //SnackbarManager.show((Snackbar) Snackbar.with(getActivity()).color(Utils.getColor(getActivity(), R.color.mBlue)).text(Constants.showMessage(getActivity(), mPreferences.getString("Lan_Id", ""), "NOINTERNET")));
        }
    }

    public void addFavoriteResponse(String resultString) {
//        Log.d((String) "System out", (String) resultString);
        if (resultString.length() > 2) {
            try {
                JSONArray jsonArray = new JSONArray(resultString);
                JSONObject jsonObject = jsonArray.optJSONObject(0);
                if (jsonObject.has("status")) {
                    String str = jsonObject.optString("status");
                    if (str.equalsIgnoreCase("true")) {
                        if (mCounter == -1) {
                            nearbies.get(mFlag).setFav_Id(jsonObject.optString("Fav_Id"));
                        }
                        mCounter = -1;
//                        dbAdapter.open();
//                        dbAdapter.updateNearBy(nearbies.get(mFlag).getPlace_Id(), jsonObject.optString("Fav_Id"));
//                        dbAdapter.close();
                        mFlag = 0;
                        Constants.mStaticFavCall = 0;
                        Constants.mStaticNearCall = 0;
                        SnackbarManager.show(Snackbar.with(getActivity()).color(Utils.getColor(getActivity(), R.color.mBlue)).text(Constants.showMessage(getActivity(), mPreferences.getString("Lan_Id", ""), "AddFavourite")));
                        update();
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
//            Log.d((String) "System out", (String) ("DeleteFavorite1 " + json));
//            new post_sync(this, "DeleteFavorite1").execute(url, json);
            new post_sync(getActivity(), "DeleteFavorite1", FavouriteFragment1.this, true).execute(url, json);

        } else {
            Intent intent = new Intent(getActivity(), NoInternet.class);
            startActivity(intent);
            //SnackbarManager.show((Snackbar) Snackbar.with(getActivity()).color(Utils.getColor(getActivity(), R.color.mBlue)).text(Constants.showMessage(getActivity(), mPreferences.getString("Lan_Id", ""), "NOINTERNET")));
        }
    }

    public void deleteFavoriteResponse(String resultString) {
//        Log.d((String) "System out", (String) resultString);
        if (resultString.length() > 2) {
            try {
                String str;
                JSONArray jsonArray = new JSONArray(resultString);
                JSONObject jsonObject = jsonArray.optJSONObject(0);
                if (jsonObject.has("status") && (str = jsonObject.optString("status")).equalsIgnoreCase("true")) {
                    if (mCounter == -1) {
                        nearbies.get(mFlag).setFav_Id("0");
                    }
                    mCounter = -1;
                    mFlag = 0;
                    SnackbarManager.show((Snackbar) Snackbar.with(getActivity()).color(Utils.getColor(getActivity(), R.color.mBlue)).text(Constants.showMessage(getActivity(), mPreferences.getString("Lan_Id", ""), "Removefavorite")));
                    Constants.mStaticFavCall = 0;
                    Constants.mStaticNearCall = 0;
                    update();
                }
            } catch (JSONException e) {
                // empty catch block
            }
        }
    }


    /*public void setDetailInfo(final Nearby nearby) {
//        Log.d("System out", "Inside set detail info " + nearby.getPlace_Id());

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


        final int like;
        final int mId;

        mId = id;
        if (nearby.getFav_Id().equalsIgnoreCase("0")) {
            txt_add_to_fav.setCompoundDrawablesWithIntrinsicBounds(Utilities.getDrawable(getActivity(), R.drawable.ic_favourite_default), null, null, null);
            txt_add_to_fav.setText(Constants.showMessage(getActivity(), mPreferences.getString("Lan_Id", ""), "Add to Favourites"));
//            iv_like_detail_my_favorite3.setImageResource(R.drawable.like_icon_detailpage);
            like = 0;
        } else {
            txt_add_to_fav.setText(Constants.showMessage(getActivity(), mPreferences.getString("Lan_Id", ""), "Remove Favourite"));

            txt_add_to_fav.setCompoundDrawablesWithIntrinsicBounds(Utilities.getDrawable(getActivity(), R.drawable.ic_favourite_active), null, null, null);

//            iv_like_detail_my_favorite3.setImageResource(R.drawable.like_icon_detailpage_selected);
            like = 1;
        }

        txt_add_to_fav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (like == 0) {
                    txt_add_to_fav.setCompoundDrawablesWithIntrinsicBounds(Utilities.getDrawable(getActivity(), R.drawable.ic_favourite_active), null, null, null);
                    txt_add_to_fav.setText(Constants.showMessage(getActivity(), mPreferences.getString("Lan_Id", ""), "Remove Favourite"));

//                    iv_like_detail_my_favorite3.setImageResource(R.drawable.like_icon_detailpage_selected);
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
        });

        tv_distance1_my_favorite3.setText(nearby.getDistance() + Constants.showMessage(getActivity(), mPreferences.getString("Lan_Id", ""), "KM"));

        // tv_time_favorite.setText(Constants.showMessage(getActivity(), mPreferences.getString("Lan_Id", ""), "Timing") + "(" + nearby.getPlace_Close_Note() + ")");

        tv_full_name_my_favorite3.setText(nearby.getPlace_Name());
        tv_info_my_favorite3.setText(nearby.getPlace_Address());
        tv_discription_my_favorite3.setText(nearby.getPlace_Description());
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


        Category_Name = nearby.getCategory_Name();
        Category_Id = nearby.getCategory_Id();

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
                        .description(nearby.getPlace_Name())
                        .image(imageUrl)
                        .setScaleType(BaseSliderView.ScaleType.Fit)
                        .setOnSliderClickListener(new BaseSliderView.OnSliderClickListener() {
                            @Override
                            public void onSliderClick(BaseSliderView slider1) {
                                Intent intent = new Intent(getActivity(), FullPlaceImageViewActivity.class);
                                intent.putExtra("nearBy_id", nearby.getPlace_Id());
                                intent.putExtra("nearBy_Fav_id", nearby.getFav_Id());
//                    intent.putExtra("nearBy_Fav_id",nearby.getFav_Id());
                                intent.putExtra("nearBy_name", nearby.getPlace_Name());
                                intent.putExtra("nearBy_longi", nearby.getPlace_Longi());
                                intent.putExtra("nearBy_lati", nearby.getPlace_Latitude());
                                intent.putExtra("nearBy_main_image", nearby.getPlace_MainImage());
                                intent.putExtra("nearBy_other_images", nearby.getOtherimages());
                                nearByDetails = nearby;
                                startActivityForResult(intent, PLACE_LIKE_FAV);
                            }
                        });

                sliderPlaceImages.addSlider(textSliderView);
            }
            sliderPlaceImages.setCustomIndicator(custom_indicator1);
            sliderPlaceImages.addOnPageChangeListener(this);
//            rlVirtualTour.setVisibility((nearby.getPlaceVRMainImage() != null && !nearby.getPlaceVRMainImage().equals("")) ? View.VISIBLE : View.GONE);
//
        }
        searchCall(nearby.getPlace_Id());
//        }

        txtStartNavigating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String uri = String.format(Locale.ENGLISH, "http://maps.google.com/maps?saddr=%f,%f&daddr=%f,%f", Double.parseDouble(mPreferences.getString("latitude1", "")), Double.parseDouble(mPreferences.getString("longitude1", "")), Double.parseDouble(nearby.getPlace_Latitude()), Double.parseDouble(nearby.getPlace_Longi()));
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
                startActivity(intent);
            }
        });

    }*/


   /* private void addFeesCustomViews(String pohCharges, String price_description) {
//        listView_fees.removeAllViews();
        *//*if (pohCharges != null && !pohCharges.equals("") && !pohCharges.equalsIgnoreCase("null")) {
            LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.row_fees, null);
            NormalTextView txtFees = (NormalTextView) view.findViewById(R.id.txtFees);
            txtFees.setText("");
//            txtFees.append(getSpannableString(Constants.showMessage(getActivity(), mPreferences.getString("Lan_Id", ""), "Fee") + ": ", true));
            txtFees.append(getSpannableString(pohCharges + "", false));
            listView_fees.addView(view);
//        }
        }
        if (price_description != null && !price_description.equals("") && !price_description.equalsIgnoreCase("null") && !price_description.equalsIgnoreCase("-")) {

            LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.row_fees, null);
            NormalTextView txtFees = (NormalTextView) view.findViewById(R.id.txtFees);
            txtFees.setText("");
            txtFees.append(getSpannableString(price_description, false));
            listView_fees.addView(view);
        }
*//*
        if (price_description == null || price_description.equals("") || price_description.equalsIgnoreCase("null") || price_description.trim().length() == 0) {


            price_description = Constants.showMessage(getActivity(), mPreferences.getString("Lan_Id", ""), "No Fees data available");
        }
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.row_fees, null);
        NormalTextView txtFees = (NormalTextView) view.findViewById(R.id.txtFees);
        txtFees.setText("");
//            price_description=price_description.replace("//","/");getSpannableString(price_description, false);
//            price_description=price_description.replace("//","/")
        txtFees.append(getSpannableString(price_description, false));
        listView_fees.addView(view);

    }*/

   /* private SpannableStringBuilder getSpannableString(String s, boolean isBold) {
//        Typeface font = Typeface.createFromAsset(getAssets(), "Akshar.ttf");
        SpannableStringBuilder spannablecontent = new SpannableStringBuilder(s);
        if (isBold)
            spannablecontent.setSpan(new CustomTypefaceSpan("", MyTorismaApplication.typeFace.getBoldFonts()),
                    0, spannablecontent.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        else
            spannablecontent.setSpan(new CustomTypefaceSpan("", MyTorismaApplication.typeFace.getNormalFonts()),
                    0, spannablecontent.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        return spannablecontent;
    }*/

    private void searchCall(String Place_Id) {
        if (CommonClass.hasInternetConnection(getActivity())) {
            String url = Constants.SERVER_URL + "json.php?action=PlaceDetails";
            String json = "[{\"Lan_Id\":\"" + mPreferences.getString("Lan_Id", "") + "\",\"User_Id\":\"" + mPreferences.getString("User_Id", "") + "\",\"Current_Latitude\":\"" + mPreferences.getString("latitude2", "") + "\",\"Current_Longitude\":\"" + mPreferences.getString("longitude2", "") + "\",\"Place_Id\":\"" + Place_Id + "\"}]";
//            Log.d("System out", "PlaceDetailsFav " + json);
            new post_sync(getActivity(), "PlaceDetailsFav", FavouriteFragment1.this, true).execute(url, json);

        } else {
            SnackbarManager.show(Snackbar.with(getActivity()).color(Utils.getColor(getActivity(), R.color.mBlue)).text(Constants.showMessage(getActivity(), mPreferences.getString("Lan_Id", ""), "NOINTERNET")));
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
            gv_detail1_my_favorite3.setAdapter(null);
            gv_detail1_my_favorite3.setAdapter(galleryAdapter2);

            if (nearbies1.size() == 0) {
                ll_see_all_my_favorite3.setVisibility(View.GONE);
            } else {
                ll_see_all_my_favorite3.setVisibility(View.VISIBLE);
            }

        } catch (JSONException e) {
            e.printStackTrace();
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
//            View view = inflater.inflate(R.layout.popup_weekdays, null, false);
//            final PopupWindow popupWindow = new PopupWindow(view, (80 * width) / 100, LinearLayout.LayoutParams.WRAP_CONTENT);
//            if (Build.VERSION.SDK_INT >= 21) {
//                popupWindow.setElevation(5.0f);
//            }
//            popupWindow.setBackgroundDrawable(new BitmapDrawable(getResources(),
//                    ""));
//            popupWindow.setBackgroundDrawable(new ColorDrawable(Utils.getColor(getActivity(), R.color.mTrans1)));
//            popupWindow.setOutsideTouchable(false);
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

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

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
            convertView = mLayoutInflater.inflate(R.layout.detail_gallery, null);
            final ImageView iv_detail = (ImageView) convertView.findViewById(R.id.iv_detail);
            final NormalTextView tv_name = (NormalTextView) convertView.findViewById(R.id.tv_name);
            final NormalTextView tv_km = (NormalTextView) convertView.findViewById(R.id.tv_km);
            final RelativeLayout rlMain = (RelativeLayout) convertView.findViewById(R.id.rlMain);
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) rlMain.getLayoutParams();
            layoutParams.width = (width * 50) / 100;
            layoutParams.height = layoutParams.width;
            rlMain.setLayoutParams(layoutParams);
            tv_name.setText(nearbies1.get(position).getPlace_Name());
            tv_km.setText(nearbies1.get(position).getDist() + Constants.showMessage(getActivity(), mPreferences.getString("Lan_Id", ""), "KM"));
            String imageUrl = Constants.IMAGE_URL + nearbies1.get(position).getPlace_MainImage() + "&w=" + (width);
//            Picasso.with(getActivity()) //
//                    .load(imageUrl) //
//                    .into(iv_detail);
            final LinearLayout llView = (LinearLayout) convertView.findViewById(R.id.llView);

            gv_detail1_my_favorite3.setOnItemClickListener(new com.ftl.tourisma.gallery1.AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(com.ftl.tourisma.gallery1.AdapterView<?> parent, View view, int position, long id) {
                    id = position;
                    if (mHandler != null) {
                        mHandler.removeCallbacks(mRunnable);
                    }
//                    setDetailInfo(nearbies1.get(position));
                    mainActivity.favouriteFragment.replacePlaceDetailsFragment(nearbies1.get(position).getPlace_Id(), "");

                    mNearby = nearbies1.get(position);
                }
            });

//            Log.i("System out", imageUrl);
            imageLoader.displayImage(imageUrl, iv_detail, optionsSimple);
            return convertView;
        }
    }

    private class GalleryAdapter1 extends BaseAdapter {
        Context context;
        Nearby nearby;

        public GalleryAdapter1(Context context, Nearby nearby) {
            this.context = context;
            this.nearby = nearby;
        }

        public int getCount() {
            return strImg1.length;
        }

        public Object getItem(int position) {
            return null;
        }

        public long getItemId(int position) {
            return 0;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater mLayoutInflater;
            mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = mLayoutInflater.inflate(R.layout.detail_gallery_header, null);
            final ImageView iv_header_detail = (ImageView) convertView.findViewById(R.id.iv_header_detail);
            final ImageView iv_share_explorer1 = (ImageView) convertView.findViewById(R.id.iv_share_explorer1);
            final ImageView txtViewAVirtalTourNext = (ImageView) convertView.findViewById(R.id.txtViewAVirtalTourNext);
            final NormalBoldTextView txtViewAVirtalTour = (NormalBoldTextView) convertView.findViewById(R.id.txtViewAVirtalTour);
            final RelativeLayout rlVirtualTour = (RelativeLayout) convertView.findViewById(R.id.rlVirtualTour);
            txtViewAVirtalTour.setText(Constants.showMessage(getActivity(), mPreferences.getString("Lan_Id", ""), "virtualreality"));

            rlVirtualTour.setVisibility((nearby.getPlaceVRMainImage() != null && !nearby.getPlaceVRMainImage().equals("")) ? View.VISIBLE : View.GONE);

            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) dotLayout_detail_my_favorite3.getLayoutParams();
            RelativeLayout.LayoutParams layoutParamsVirtual = (RelativeLayout.LayoutParams) rlVirtualTour.getLayoutParams();
            if (rlVirtualTour.getVisibility() == View.VISIBLE) {
                layoutParams.height = layoutParamsVirtual.height + (layoutParamsVirtual.height * 30) / 100;
                dotLayout_detail_my_favorite3.setLayoutParams(layoutParams);
            } else {
                layoutParams.height = 50;
                dotLayout_detail_my_favorite3.setLayoutParams(layoutParams);
            }

            iv_header_detail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), FullPlaceImageViewActivity.class);
                    intent.putExtra("nearBy_id", nearby.getPlace_Id());
                    intent.putExtra("nearBy_Fav_id", nearby.getFav_Id());
//                    intent.putExtra("nearBy_Fav_id",nearby.getFav_Id());
                    intent.putExtra("nearBy_name", nearby.getPlace_Name());
                    intent.putExtra("nearBy_longi", nearby.getPlace_Longi());
                    intent.putExtra("nearBy_lati", nearby.getPlace_Latitude());
                    intent.putExtra("nearBy_main_image", nearby.getPlace_MainImage());
                    intent.putExtra("nearBy_other_images", nearby.getOtherimages());
                    nearByDetails = nearby;
                    startActivityForResult(intent, PLACE_LIKE_FAV);
                }
            });
            txtViewAVirtalTour.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), SimpleVrPanoramaActivity.class);
                    intent.putExtra("path", nearby.getPlaceVRMainImage());
                    intent.putExtra("path_1", nearby.getVrimages());
                    startActivity(intent);
                }
            });
            txtViewAVirtalTourNext.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), SimpleVrPanoramaActivity.class);
                    intent.putExtra("path", nearby.getPlaceVRMainImage());
                    intent.putExtra("path_1", nearby.getVrimages());
                    startActivity(intent);
                }
            });
            iv_share_explorer1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent mIntent = new Intent(getActivity(), ShareFragmentActivity.class);
                    String share1 = Constants.showMessage(getActivity(), mPreferences.getString("Lan_Id", ""), "share1");
                    String share2 = Constants.showMessage(getActivity(), mPreferences.getString("Lan_Id", ""), "share2");
                    String share3 = Constants.showMessage(getActivity(), mPreferences.getString("Lan_Id", ""), "share3");

                    mIntent.putExtra("myMsg", share1 + " \"" + mPreferences.getString("User_Name", "") + "\" " + share2 + " \"" + nearby.getPlace_Name() + "\" " + share3);
                    startActivity(mIntent);

                    /*Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.setType("text/plain");
                    intent.putExtra(Intent.EXTRA_TEXT, "Hey, \"" + mPreferences.getString("User_Name", "") + "\" would like you to check out this \"" + nearby.getPlace_Name() + "\" attraction. Please download Tourisma App to get more detail. Download the android app by clicking at “weblink”.");
                    startActivity(Intent.createChooser(intent, "Share with"));*/
                }
            });

            String imageUrl = Constants.IMAGE_URL + strImg1[position] + "&w=" + (width);
            Picasso.with(getActivity()) //
                    .load(imageUrl) //
                    .into(iv_header_detail);

//            Log.i("System out", imageUrl);
//            imageLoader2.displayImage(imageUrl, iv_header_detail, options2);

            return convertView;
        }
    }

   /* @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PLACE_LIKE_FAV) {
            if (data != null) {
                nearByDetails.setFav_Id(data.getStringExtra("DATA"));
                setDetailInfo(nearByDetails);
            }
        }
    }*/

    class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {
        private Activity activity;
        public ViewHolder viewHolder;
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
//            imageLoader.displayImage(imageURL, viewHolder.iv_nearby_explorer, options);
            int heightofImage = (height * 60) / 100;
            Picasso.with(getActivity()) //
                    .load(imageURL) //
                    .resize(width, heightofImage)
                    .into(viewHolder.iv_nearby_explorer);

            viewHolder.tv_near.setText(nearbies.get(position).getPlace_Name());
            viewHolder.txtCategory.setText(Constants.showMessage(getActivity(), mPreferences.getString("Lan_Id", ""), "Category") + ": " + nearbies.get(position).getCategory_Name()
            );

            viewHolder.llView.setId(position);
            viewHolder.llView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    ll_my_favourite1.setVisibility(View.GONE);
//                    ll_my_favorite3.setVisibility(View.VISIBLE);
                    id = v.getId();
//                    Category_Name = nearbies.get(position).getCategory_Name();
//                    if (mHandler != null) {
//                        mHandler.removeCallbacks(mRunnable);
//                    }
//                    setDetailInfo(nearbies.get(v.getId()));
                    mainActivity.favouriteFragment.replacePlaceDetailsFragment(nearbies.get(v.getId()).getPlace_Id(), "");
                    mNearby = nearbies.get(v.getId());

//                    Intent intent = new Intent(getActivity(), SearchResultPlaceDetailsActivity.class);
//                    intent.putExtra("placeId", nearbies.get(v.getId()).getPlace_Id());
//                    startActivity(intent);


                }
            });
            viewHolder.iv_nearby_explorer.setId(position);
            viewHolder.iv_nearby_explorer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    Intent intent = new Intent(getActivity(), SearchResultPlaceDetailsActivity.class);
//                    intent.putExtra("placeId", nearbies.get(v.getId()).getPlace_Id());
//                    startActivity(intent);

//                    ll_my_favourite1.setVisibility(View.GONE);
//                    ll_my_favorite3.setVisibility(View.VISIBLE);
                    id = v.getId();
//                    Category_Name = nearbies.get(position).getCategory_Name();
//                    if (mHandler != null) {
//                        mHandler.removeCallbacks(mRunnable);
//                    }
//                    setDetailInfo(nearbies.get(v.getId()));
                    mainActivity.favouriteFragment.replacePlaceDetailsFragment(nearbies.get(v.getId()).getPlace_Id(), "");

                    mNearby = nearbies.get(v.getId());
                }
            });
            //TOdo
            viewHolder.rl_navigator.setId(position);
            viewHolder.rl_navigator.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String uri = String.format(Locale.ENGLISH, "http://maps.google.com/maps?saddr=%f,%f&daddr=%f,%f", Double.parseDouble(mPreferences.getString("latitude1", "")), Double.parseDouble(mPreferences.getString("longitude1", "")), Double.parseDouble(nearbies.get(position).getPlace_Latitude()), Double.parseDouble(nearbies.get(position).getPlace_Longi()));
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                    intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
                    startActivity(intent);
                    //  Utilities.toast(getActivity(), "In-Progress -> It will Navaigate on google map");
                }
            });

            if (nearbies.get(position).getFav_Id().equalsIgnoreCase("0")) {
//                viewHolder.iv_favorite.setImageResource(R.drawable.like_icon);
                viewHolder.imgFav.setActivated(false);
            } else {
                viewHolder.imgFav.setActivated(true);
//                viewHolder.iv_favorite.setImageResource(R.drawable.like_icon_);
            }

            viewHolder.rl_share.setId(position);
            viewHolder.rl_share.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent mIntent = new Intent(getActivity(), ShareFragmentActivity.class);
                    String share1 = Constants.showMessage(getActivity(), mPreferences.getString("Lan_Id", ""), "share1");
                    String share2 = Constants.showMessage(getActivity(), mPreferences.getString("Lan_Id", ""), "share2");
                    String share3 = Constants.showMessage(getActivity(), mPreferences.getString("Lan_Id", ""), "share3");

                    mIntent.putExtra("myMsg", share1 + " \"" + mPreferences.getString("User_Name", "") + "\" " + share2 + " \"" + nearbies.get(v.getId()).getPlace_Name() + "\" " + share3);
                    startActivity(mIntent);
//                        Intent intent = new Intent(Intent.ACTION_SEND);
//                        intent.setType("text/plain");
//                        startActivity(Intent.createChooser(intent, "Share with"));
                }
            });


            viewHolder.rl_fav.setId(position);
            viewHolder.rl_fav.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    for (int j = 0; j < nearbies.size(); j++) {
                        if (v.getId() == j) {
                            if (nearbies.get(j).getFav_Id().equalsIgnoreCase("0")) {
                                viewHolder.imgFav.setActivated(true);
//                                viewHolder.iv_favorite.setImageResource(R.drawable.like_icon_);
                                addFavoriteCall(nearbies.get(j).getPlace_Id());
                                mFlag = j;
                            } else {
                                viewHolder.imgFav.setActivated(false);
//                                viewHolder.imgFav.setImageResource(R.drawable.like_icon);
                                deleteFavoriteCall(nearbies.get(j).getFav_Id());
                                mFlag = j;
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
                            viewHolder.tv_ticket.setText(Constants.showMessage(getActivity(), mPreferences.getString("Lan_Id", ""), "Tickets") + ": " + hourDetails.getPOHCharges());
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
                viewHolder.tv_ticket.setText(Constants.showMessage(getActivity(), mPreferences.getString("Lan_Id", ""), "Tickets") + ": -");
            }
            viewHolder.txtDistance.setText(nearbies.get(position).getDistance() + Constants.showMessage(getActivity(), mPreferences.getString("Lan_Id", ""), "KM"));
            /*double d= Utilities.GetRoutDistane(Double.parseDouble(mPreferences.getString("latitude2", "")), Double.parseDouble(mPreferences.getString("longitude2", "")), Double.parseDouble(nearby.getPlace_Latitude()), Double.parseDouble(nearbies.get(position).getPlace_Longi()),nearbies.get(position).getDist());
            if(d!=-1){
                viewHolder.txtDistance.setText(d + Constants.showMessage(getActivity(), mPreferences.getString("Lan_Id", ""), "KM"));
            }*/

        }

        @Override
        public int getItemCount() {
            return nearbies.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            private ImageView iv_nearby_explorer, imgFav;
            private NormalTextView tv_ticket, txtCategory, txtFav, txtDistance, tv_near, txtShare, tv_timing;
            private LinearLayout llView;
            private View container;
            LinearLayout rl_fav, rl_navigator, rl_share;
            ProgressBar progressBar;

            public ViewHolder(View convertView) {
                super(convertView);
                iv_nearby_explorer = (ImageView) convertView.findViewById(R.id.iv_nearby_explorer);
                imgFav = (ImageView) convertView.findViewById(R.id.imgFav);
//                iv_share_explorer = (ImageView) convertView.findViewById(R.id.iv_share_explorer);
                tv_near = (NormalTextView) convertView.findViewById(R.id.tv_near);
                tv_timing = (NormalTextView) convertView.findViewById(R.id.tv_timing);
                tv_ticket = (NormalTextView) convertView.findViewById(R.id.tv_ticket);
                txtShare = (NormalTextView) convertView.findViewById(R.id.txtShare);
                txtFav = (NormalTextView) convertView.findViewById(R.id.txtFav);
                txtDistance = (NormalTextView) convertView.findViewById(R.id.txtDistance);
//                container = convertView.findViewById(R.id.cv_search_result_adapter);
                llView = (LinearLayout) convertView.findViewById(R.id.llView);
                final RelativeLayout rlNearBy = (RelativeLayout) convertView.findViewById(R.id.rlNearBy);
                rl_fav = (LinearLayout) convertView.findViewById(R.id.rl_fav);
                rl_navigator = (LinearLayout) convertView.findViewById(R.id.rl_navigator);
                rl_share = (LinearLayout) convertView.findViewById(R.id.rl_share);
//                ll_location_near_map = (LinearLayout) view.findViewById(R.id.ll_location_near_map);
//               iv_share_explorer = (ImageView) view.findViewById(R.id.iv_share_explorer);
                txtCategory = (NormalTextView) convertView.findViewById(R.id.txtCategory);
                progressBar = (ProgressBar) convertView.findViewById(R.id.progressBar);
                iv_nearby_explorer.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, (height * 60) / 100));

            }
        }

    }

}