package com.ftl.tourisma.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import com.daimajia.slider.library.Indicators.PagerIndicator;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.DefaultSliderView;
import com.daimajia.slider.library.Tricks.ViewPagerEx;
import com.ftl.tourisma.MapDetailFragment;
import com.ftl.tourisma.R;
import com.ftl.tourisma.ShareFragmentActivity;
import com.ftl.tourisma.custom_views.NormalBoldTextView;
import com.ftl.tourisma.custom_views.NormalTextView;
import com.ftl.tourisma.database.AllCategories;
import com.ftl.tourisma.database.Nearby;
import com.ftl.tourisma.minterface.Updatable;
import com.ftl.tourisma.models.HourDetails;
import com.ftl.tourisma.postsync.post_sync;
import com.ftl.tourisma.utils.CommonClass;
import com.ftl.tourisma.utils.Constants;
import com.ftl.tourisma.utils.ExpandableHeightGridView;
import com.ftl.tourisma.utils.JSONObjConverter;
import com.ftl.tourisma.utils.Preference;
import com.ftl.tourisma.utils.Utilities;
import com.ftl.tourisma.utils.Utils;
import com.github.clans.fab.FloatingActionButton;
import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.SnackbarManager;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
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

import static android.content.Context.LAYOUT_INFLATER_SERVICE;
import static com.ftl.tourisma.utils.Constants.PlaceClosed;
import static com.ftl.tourisma.utils.Constants.PlaceOpenFor24Hours;
import static com.ftl.tourisma.utils.Constants.PlaceOpenWithAnyTime;

/**
 * Created by harpalsinh on 26-Feb-2016.
 */
public class HomeFragment extends Fragment implements View.OnClickListener, Updatable, ViewPagerEx.OnPageChangeListener, post_sync.ResponseHandler {

    private static final String TAG = "HomeFragment";
    private static int mFrame = 0;
    private static int mCounter = -1;
    MainActivity mainActivity;
    ArrayList<HourDetails> hourDetails;
    String group_id;
    //MyReceiver receiver;
    private ArrayList<AllCategories> allCategories = new ArrayList<>();
    private ArrayList<AllCategories> allCategories1 = new ArrayList<>();
    private AllCategories categories;
    private ArrayList<Nearby> recommendeds = new ArrayList<>();
    private Nearby recommended;
    private ArrayList<Nearby> nearbies = new ArrayList<>();
    private ArrayList<Nearby> nearbies_category = new ArrayList<>();
    private ArrayList<Nearby> nearbies1 = new ArrayList<>();
    private Nearby nearby;
    private ExpandableHeightGridView gv_explorer;
    private DisplayImageOptions optionsSimple;
    private ExplorerAdapter explorerAdapter;
    private ExplorerAdapter1 explorerAdapter1;
    private NormalTextView tv_city, txt_recommended, txt_description, txt_categoty;
    private ImageView iv_back3, iv_search_header3, imgFav, mDotsText1[];
    private LinearLayout ll_cate1, ll_change_city, ll_explorer, ll_explorer1, ll_nearby_explorer, llEmptyLayout;
    private RelativeLayout rl_recommended;
    private SimpleDateFormat _24HourSDF, _12HourSDF;
    private String _24HourTime, _24HourTime1;
    private Date _24HourDt, _24HourDt1;
    private int mFlag = 0;
    private NormalBoldTextView tv_full_name;
    private NormalTextView txtShowMoreLess, txtSuggest, txtOk, txtMessage, txt_add_to_fav, tv_discription;
    private int id;
    private String Category_Name, Category_Id;
    private FloatingActionButton fb_category;
    private DisplayImageOptions options;
    private NormalTextView tv_your_location_header3, tv_explore, tv_your_mood, tv_nearby, tv_see_whats;
    private Nearby mNearby = new Nearby();
    private int like;
    private ImageView iv_explore;
    private ScrollView sv_explorer_location;
    private boolean isShowLess;
    private View view;
    private String mGroupId, mPlaceId, mFav;
    private Nearby nearByDetails;
    private SliderLayout slider;
    private PagerIndicator custom_indicator;
    private boolean isCalledFromCat, isFromBeacon;
    private Dialog dialog;
    private boolean isListOfCategoryVisible;
    private SharedPreferences.Editor mEditor;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        _24HourSDF = new SimpleDateFormat("HH:mm");
        _12HourSDF = new SimpleDateFormat("hh:mma");


    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        sv_explorer_location = (ScrollView) view.findViewById(R.id.sv_explorer_location);

        iv_back3 = (ImageView) view.findViewById(R.id.iv_back3);
        iv_back3.setOnClickListener(this);

        iv_explore = (ImageView) view.findViewById(R.id.iv_explore);
        iv_explore.setImageResource(R.drawable.ic_clock);

        tv_your_location_header3 = (NormalTextView) view.findViewById(R.id.tv_your_location_header3);
        tv_your_location_header3.setText(Constants.showMessage(getActivity(), mainActivity.getPreferences().getString("Lan_Id", ""), "locationtitle"));
        tv_explore = (NormalTextView) view.findViewById(R.id.tv_explore);
        tv_explore.setText(Constants.showMessage(getActivity(), mainActivity.getPreferences().getString("Lan_Id", ""), "explore"));
        tv_your_mood = (NormalTextView) view.findViewById(R.id.tv_your_mood);
        tv_your_mood.setText(Constants.showMessage(getActivity(), mainActivity.getPreferences().getString("Lan_Id", ""), "exploresubtitle"));
        tv_nearby = (NormalTextView) view.findViewById(R.id.tv_nearby);
        tv_nearby.setText(Constants.showMessage(getActivity(), mainActivity.getPreferences().getString("Lan_Id", ""), "nearby"));
        tv_see_whats = (NormalTextView) view.findViewById(R.id.tv_see_whats);
        tv_see_whats.setText(Constants.showMessage(getActivity(), mainActivity.getPreferences().getString("Lan_Id", ""), "nearbysubtitle"));

        tv_full_name = (NormalBoldTextView) view.findViewById(R.id.tv_full_name);
        txtShowMoreLess = (NormalTextView) view.findViewById(R.id.txtShowMoreLess);
        txtShowMoreLess.setOnClickListener(this);
        txtShowMoreLess.setText(Constants.showMessage(getActivity(), mainActivity.getPreferences().getString("Lan_Id", ""), "More"));


        txt_add_to_fav = (NormalTextView) view.findViewById(R.id.txt_add_to_fav);
        tv_discription = (NormalTextView) view.findViewById(R.id.tv_discription);

        txtMessage = (NormalTextView) view.findViewById(R.id.txtMessage);
        txtOk = (NormalTextView) view.findViewById(R.id.txtOk);
        txtSuggest = (NormalTextView) view.findViewById(R.id.txtSuggest);
        txtOk.setText(Constants.showMessage(getActivity(), mainActivity.getPreferences().getString("Lan_Id", ""), "Change Location"));
        txtSuggest.setText(Constants.showMessage(getActivity(), mainActivity.getPreferences().getString("Lan_Id", ""), "Suggest Location"));
        txtMessage.setText(Constants.showMessage(getActivity(), mainActivity.getPreferences().getString("Lan_Id", ""), "No records available for this place"));
        txtOk.setOnClickListener(this);
        txtSuggest.setOnClickListener(this);


        fb_category = (FloatingActionButton) view.findViewById(R.id.fb_category);
        fb_category.setOnClickListener(this);

        rl_recommended = (RelativeLayout) view.findViewById(R.id.rl_recommended);
        llEmptyLayout = (LinearLayout) view.findViewById(R.id.llEmptyLayout);
        ll_cate1 = (LinearLayout) view.findViewById(R.id.ll_cate1);

        String str = mainActivity.getPreferences().getString(Preference.Pref_City, "");
        if (str.equalsIgnoreCase("Dubai - United Arab Emirates")) {
            mainActivity.getPreferences().edit().putString(Preference.Pref_City, "Dubai").apply();
        }


        ll_explorer1 = (LinearLayout) view.findViewById(R.id.ll_explorer1);
        llEmptyLayout = (LinearLayout) view.findViewById(R.id.llEmptyLayout);
        llEmptyLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        iv_search_header3 = (ImageView) view.findViewById(R.id.iv_search_header3);
        iv_search_header3.setOnClickListener(this);
        ll_nearby_explorer = (LinearLayout) view.findViewById(R.id.ll_nearby_explorer);
        ll_change_city = (LinearLayout) view.findViewById(R.id.ll_change_city);
        ll_explorer = (LinearLayout) view.findViewById(R.id.ll_explorer);
        ll_change_city.setOnClickListener(this);

        tv_city = (NormalTextView) view.findViewById(R.id.tv_city);
        tv_city.setText(mainActivity.getPreferences().getString(Preference.Pref_City, ""));
        gv_explorer = (ExpandableHeightGridView) view.findViewById(R.id.gv_explorer);
        txt_recommended = (NormalTextView) view.findViewById(R.id.tv_recommended);
        txt_description = (NormalTextView) view.findViewById(R.id.description);
        txt_categoty = (NormalTextView) view.findViewById(R.id.description1);
        imgFav = (ImageView) view.findViewById(R.id.imgFav);

        custom_indicator = (PagerIndicator) view.findViewById(R.id.custom_indicator);
        custom_indicator.setIndicatorStyleResource(R.drawable.shape_cirlce_fill, R.drawable.shape_cirlce_unfill);
        txt_recommended.setText(Constants.showMessage(getActivity(), mainActivity.getPreferences().getString("Lan_Id", ""), "recommended"));

        txt_description.setText("");
        txt_categoty.setText("");

        explorerAdapter = new ExplorerAdapter(getActivity());
        gv_explorer.setAdapter(null);
        gv_explorer.setAdapter(explorerAdapter);
        gv_explorer.setExpanded(true);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (slider != null) {
            slider.removeAllSliders();
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_home, container, false);
        getAllCategoriesCall();
        return view;
    }


    /*//LocalBroadcastManager to refresh home page starts here
    public void refresh() {
        //your code in refresh.
        Log.i("Refresh", "YES");
    }

    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(receiver);
    }

    public void onResume() {
        super.onResume();
        receiver = new MyReceiver();
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(receiver,
                new IntentFilter("TAG_REFRESH_HOMEPAGE"));
        try {
            explorerAdapter.notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Local broadcast receiver to refresh the home page
    private class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            HomeFragment.this.refresh();
            getAllCategoriesCall();
        }
    }
*/

    private void homePageDataCall() {
        isCalledFromCat = false;

        if (CommonClass.hasInternetConnection(getActivity())) {

            String url = Constants.SERVER_URL + "json.php?action=HomePageData";
//            String json = "[{\"Lan_Id\":\"" + mainActivity.getPreferences().getString("Lan_Id", "") + "\",\"User_Id\":\"" + mainActivity.getPreferences().getString("User_Id", "") + "\",\"Current_Latitude\":\"" + mainActivity.getPreferences().getString("latitude2", "") + "\",\"Current_Longitude\":\"" + mainActivity.getPreferences().getString("longitude2", "") + "\",\"keyword\":\"" + mainActivity.getPreferences().getString(Preference.Pref_City, "") + "\"}]";
//            String url = "http://35.154.205.155/mytourisma/json.php?action=HomePageData";
            String json = "[{\"Lan_Id\":\"" + mainActivity.getPreferences().getString("Lan_Id", "") + "\",\"User_Id\":\"" + mainActivity.getPreferences().getString("User_Id", "") + "\",\"Current_Latitude\":\"" + mainActivity.getPreferences().getString("latitude2", "") + "\",\"Current_Longitude\":\"" + mainActivity.getPreferences().getString("longitude2", "") + "\",\"keyword\":\"" + mainActivity.getPreferences().getString(Preference.Pref_City, "") + "\",\"secondary_text\":\"" + mainActivity.getPreferences().getString(Preference.Pref_Country, "") + "\"}]";
            System.out.println("homepagedata_json " + json);
            System.out.println("homepagedata_url " + url);
            new post_sync(getActivity(), "HomePageData", HomeFragment.this, true).execute(url, json);
        } else {
            Intent intent = new Intent(getActivity(), NoInternet.class);
            startActivity(intent);
            //SnackbarManager.show(Snackbar.with(getActivity()).color(Utilities.getColor(getActivity(), R.color.mBlue)).text(Constants.showMessage(getActivity(), mainActivity.getPreferences().getString("Lan_Id", ""), "NOINTERNET")));

        }
    }

    public void homePageDataResponse(final String resultString) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                recommendeds.clear();
                JSONObjConverter jonObjConverter = new JSONObjConverter();
                Log.d("Explore", "result string get all categories " + resultString);
                if (resultString.length() > 2) {
                    try {
                        JSONArray jsonArray = new JSONArray(resultString);
                        JSONObject jsonObject = jsonArray.optJSONObject(0);
                        if (jsonObject.has("recommnded")) {
                            JSONArray jsonArray1 = jsonObject.optJSONArray("recommnded");

                            for (int i = 0; i < jsonArray1.length(); i++) {
                                recommended = jonObjConverter.convertJsonToNearByObj(jsonArray1.optJSONObject(i));
                                recommendeds.add(recommended);
                            }
                        }

                        if (jsonObject.has("nearby")) {
                            nearbies.clear();
                            JSONArray jsonArray1 = jsonObject.optJSONArray("nearby");
                            for (int i = 0; i < jsonArray1.length(); i++) {
                                nearby = jonObjConverter.convertJsonToNearByObj(jsonArray1.optJSONObject(i));
                                nearbies.add(nearby);
                            }
                            Constants.mStaticNearCall = 1;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                if (Constants.placeId != null && !Constants.placeId.equals("")) {
                    if (recommendeds != null && recommendeds.size() > 0) {
                        int id = 0;
                        for (int i = 0; i < recommendeds.size(); i++) {
                            if (recommendeds.get(i).getPlace_Id().equals(Constants.placeId)) {
                                id = i;
                                break;
                            }
                        }
                        ll_explorer1.setVisibility(View.GONE);
                        mFrame = 1;
                        mNearby = recommendeds.get(id);
                        Constants.placeId = null;
                    } else {
                        isFromBeacon = true;
                        searchCall(Constants.placeId);
                        return;
                    }
                }
                setCreateView(isCalledFromCat);

                sv_explorer_location.setVisibility(View.VISIBLE);

                Log.d("Explore", "result string get all categories ->>> End");
            }
        });
    }

    private void getAllCategoriesCall() {
        if (CommonClass.hasInternetConnection(getActivity())) {
            String url = Constants.SERVER_URL + "json.php?action=GetAllCategories";
            String json = "";
            new post_sync(getActivity(), "GetAllCategories", HomeFragment.this, true).execute(url, json);

        } else {
            Intent intent = new Intent(getActivity(), NoInternet.class);
            startActivity(intent);
            //SnackbarManager.show(Snackbar.with(getActivity()).color(Utilities.getColor(getActivity(), R.color.mBlue)).text(Constants.showMessage(getActivity(), mainActivity.getPreferences().getString("Lan_Id", ""), "NOINTERNET")));
        }
    }

    public void getAllCategoriesResponse(String resultString) {
//        Log.d("System out", "result string get all categories " + resultString);
        allCategories.clear();
        allCategories1.clear();
        homePageDataCall();

            if (resultString.length() > 2) {
                try {
                    JSONArray jsonArray = new JSONArray(resultString);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.optJSONObject(i);
                        categories = new AllCategories();
                        categories.setCategory_Id(jsonObject.optString("Category_Id"));
                        categories.setCategory_Name(jsonObject.optString("Category_Name"));
                        categories.setCategory_Map_Icon(jsonObject.optString("Category_Map_Icon"));
                        categories.setLan_Id(jsonObject.optString("Lan_Id"));
                        categories.setCategory_Info(jsonObject.optString("Category_Info"));
                        categories.setCategory_Status(jsonObject.optString("Category_Status"));

                        if (mainActivity.getPreferences().getString("Lan_Id", "").equalsIgnoreCase(jsonObject.optString("Lan_Id"))) {

                            if (allCategories1.size() <= 9) {
                                allCategories.add(categories);
                            }
                            allCategories1.add(categories);

                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        explorerAdapter = new ExplorerAdapter(getActivity());
        gv_explorer.setAdapter(null);
        gv_explorer.setAdapter(explorerAdapter);
        gv_explorer.setExpanded(true);


    }

    private void setCreateView(boolean isCategoryView) {

        ll_change_city.setEnabled(true);

        if (recommendeds.size() > 0) {

            if (!isCategoryView) {
                rl_recommended.setVisibility(View.VISIBLE);
//
                slider = (SliderLayout) view.findViewById(R.id.slider);
                slider.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (mainActivity.height * 60) / 100));
                slider.setDuration(4000);
                slider.removeAllSliders();
                for (Nearby nearby : recommendeds) {
                    String imageUrl = Constants.IMAGE_URL + nearby.getPlace_MainImage() + "&w=" + (mainActivity.width);
                    DefaultSliderView textSliderView = new DefaultSliderView(getActivity());
                    // initialize a SliderLayout
                    Picasso picasso = Picasso.with(getActivity());

                    textSliderView
                            .description(nearby.getPlace_Name())
                            .image(imageUrl)
                            .setScaleType(BaseSliderView.ScaleType.Fit)
                            .setOnSliderClickListener(new BaseSliderView.OnSliderClickListener() {
                                @Override
                                public void onSliderClick(BaseSliderView slider1) {
//                                    ll_explorer1.setVisibility(View.GONE);
                                    mFrame = 1;
                                    id = slider.getCurrentPosition();
                                    mainActivity.exploreNearbyFragment.replacePlaceDetailsFragment(recommendeds.get(id).getPlace_Id(), tv_city.getText().toString());

                                    //setDetailInfo(recommendeds.get(id));
//                                    mNearby = recommendeds.get(id);
                                }
                            }).setPicasso(picasso);


                    slider.addSlider(textSliderView);
                }
                slider.setCustomIndicator(custom_indicator);
                slider.addOnPageChangeListener(this);

            }
        } else {
            rl_recommended.setVisibility(View.GONE);
        }


        setNearBy();
    }

    @Override
    public void onClick(View v) {
        if (v == iv_search_header3) {

            mainActivity.exploreNearbyFragment.replaceSearchFragment();

        } else if (v == ll_change_city || v == txtOk) {

            mainActivity.exploreNearbyFragment.replaceLocationFragment();
        } else if (v == fb_category) {
            Intent mIntent = new Intent(getActivity(), MapDetailFragment.class);
            mIntent.putExtra("nearbies", nearbies);
            mIntent.putExtra("title", Category_Name);
            startActivity(mIntent);
        } else if (v == iv_back3) {
            Constants.mStaticNearCall = 0;

            update();
        } else if (v == txtShowMoreLess) {
            if (isShowLess) {
                explorerAdapter1 = new ExplorerAdapter1(getActivity());
//                expand(ll_explorer, true);
//                ll_explorer.setBackgroundResource(R.drawable.ic_bg_category);
                gv_explorer.setAdapter(null);
                gv_explorer.setAdapter(explorerAdapter1);
                gv_explorer.setExpanded(true);
                txtShowMoreLess.setText(Constants.showMessage(getActivity(), mainActivity.getPreferences().getString("Lan_Id", ""), "Less"));

            } else {
                txtShowMoreLess.setText(Constants.showMessage(getActivity(), mainActivity.getPreferences().getString("Lan_Id", ""), "More"));
                explorerAdapter = new ExplorerAdapter(getActivity());
//                ll_explorer.setBackgroundResource(R.drawable.ic_bg_category_);
//                expand(ll_explorer, false);
                gv_explorer.setAdapter(null);
                gv_explorer.setAdapter(explorerAdapter);
                gv_explorer.setExpanded(true);
            }
        } else if (v == txtSuggest) {
            suggestPlace();
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        try {
            if (isVisibleToUser) {
                isListOfCategoryVisible = false;
                ll_cate1.setVisibility(View.VISIBLE);
                iv_back3.setVisibility(View.INVISIBLE);
                fb_category.setVisibility(View.GONE);
                sv_explorer_location.setVisibility(View.VISIBLE);
                rl_recommended.setVisibility(View.VISIBLE);
                mFrame = 0;
                ll_explorer1.setVisibility(View.VISIBLE);

                if (Constants.mStaticNearCall == 0) {
                    homePageDataCall();
                } else {
                    setCreateView(isCalledFromCat);
                }
            }
        } catch (Exception e) {
            Log.d(TAG, "setUserVisibleHint " + e.getLocalizedMessage());
        }
    }

    @Override
    public void update() {
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//        Log.d(TAG, "onPageScrolled " + position);
    }

    @Override
    public void onPageSelected(final int position) {
        //   Log.d(TAG, "onPageSelected " + position);
        try {
            if (recommendeds != null && recommendeds.size() > 0 && recommendeds.size() > position) {
                txt_description.setText(recommendeds.get(position).getPlace_Name());
                txt_categoty.setText(recommendeds.get(position).getCategory_Name());

                Animation animFadeIn = AnimationUtils.loadAnimation(getActivity(),
                        R.anim.fade_in);

                txt_description.setAnimation(animFadeIn);
                txt_categoty.setAnimation(animFadeIn);

                imgFav.setActivated(!recommendeds.get(position).getFav_Id().equalsIgnoreCase("0"));
//            }
                imgFav.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mainActivity.getPreferences().getString("User_Id", "").equalsIgnoreCase("0")) {
                            mainActivity.showGuestSnackToast();
                        } else {
                            imgFav.setActivated(!imgFav.isActivated());
                            if (imgFav.isActivated()) {
                                mFlag = position;
                                addFavoriteCall(recommendeds.get(position).getPlace_Id(), recommendeds.get(position).getGroup_Id());
                            } else {
                                mFlag = position;
                                deleteFavoriteCall(recommendeds.get(position).getFav_Id());
                            }
                        }

                    }
                });
            }
        } catch (Exception e) {
            Log.e(TAG, "onPageSelected Exception " + e.getLocalizedMessage());
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {
//        Log.d(TAG, "onPageScrollStateChanged " + state);

    }

    @Override
    public void onResponse(String response, String action) {
        try {
            if (action.equalsIgnoreCase("HomePageData")) {
                homePageDataResponse(response);
            } else if (action.equalsIgnoreCase("GetAllCategories")) {
                getAllCategoriesResponse(response);
            } else if (action.equalsIgnoreCase("AddFavorite")) {
                addFavoriteResponse(response);
            } else if (action.equalsIgnoreCase("PlaceByCategory")) {
                searchResponse1(response);
            } else if (action.equalsIgnoreCase("PlaceDetails")) {
                searchResponse(response);
            } else if (action.equalsIgnoreCase("DeleteFavorite")) {
                deleteFavoriteResponse(response);
            }
        } catch (Exception e) {
            Log.e(TAG, "onResponse Exception " + e.getLocalizedMessage());
        }
    }

    public void setNearBy() {

        ll_nearby_explorer.removeAllViews();

        if (nearbies.size() > 0) {
            for (Nearby nearby : nearbies) {
                double d = Utilities.GetRoutDistane(Double.parseDouble(mainActivity.getPreferences().getString("latitude2", "")), Double.parseDouble(mainActivity.getPreferences().getString("longitude2", "")), Double.parseDouble(nearby.getPlace_Latitude()), Double.parseDouble(nearby.getPlace_Longi()), nearby.getDist());

                nearby.setDistance(d);
            }
            nearbies = Utilities.sortLocations(nearbies);

            llEmptyLayout.setVisibility(View.GONE);
            for (int i = 0; i < nearbies.size(); i++) {

                LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(LAYOUT_INFLATER_SERVICE);
                View view = inflater.inflate(R.layout.item_nearby, null);
                final ImageView iv_nearby_explorer = (ImageView) view.findViewById(R.id.iv_nearby_explorer);
                final ImageView imgFav = (ImageView) view.findViewById(R.id.imgFav);
                final LinearLayout llView = (LinearLayout) view.findViewById(R.id.llView);
                final RelativeLayout rlNearBy = (RelativeLayout) view.findViewById(R.id.rlNearBy);
                final LinearLayout rl_fav = (LinearLayout) view.findViewById(R.id.rl_fav);
                final LinearLayout rl_navigator = (LinearLayout) view.findViewById(R.id.rl_navigator);
                final LinearLayout rl_share = (LinearLayout) view.findViewById(R.id.rl_share);
//                final LinearLayout ll_location_near_map = (LinearLayout) view.findViewById(R.id.ll_location_near_map);
//                final ImageView iv_share_explorer = (ImageView) view.findViewById(R.id.iv_share_explorer);
                final NormalTextView tv_near = (NormalTextView) view.findViewById(R.id.tv_near);
                final NormalTextView txtCategory = (NormalTextView) view.findViewById(R.id.txtCategory);
                final NormalTextView tv_timing = (NormalTextView) view.findViewById(R.id.tv_timing);
                final NormalTextView tv_ticket = (NormalTextView) view.findViewById(R.id.tv_ticket);
                final NormalTextView txtShare = (NormalTextView) view.findViewById(R.id.txtShare);
                final NormalTextView txtFav = (NormalTextView) view.findViewById(R.id.txtFav);
                final NormalTextView txtDistance = (NormalTextView) view.findViewById(R.id.txtDistance);
//                final ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
                iv_nearby_explorer.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, (mainActivity.height * 60) / 100));
                rlNearBy.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (mainActivity.height * 60) / 100));

                String imageURL = Constants.IMAGE_URL + nearbies.get(i).getPlace_MainImage() + "&w=" + (mainActivity.width);
//                Log.i("System out", imageURL);

                Picasso.with(getActivity()) //
                        .load(imageURL) //
                        .resize(mainActivity.width, (mainActivity.height * 60) / 100)
                        .into(iv_nearby_explorer);


                //imageLoader.displayImage(imageURL, iv_nearby_explorer, optionsSimple);
//
                tv_near.setText(nearbies.get(i).getPlace_Name());
                tv_ticket.setText(nearbies.get(i).getFree_entry());
/*                if (nearbies.get(i).getFree_entry().equals("0")) {
                    tv_ticket.setText(Constants.showMessage(getActivity(), mainActivity.getPreferences().getString("Lan_Id", ""), "Tickets") + ": " + "Check details");
                } else if (nearbies.get(i).getFree_entry().equals("1")){
                    tv_ticket.setText(Constants.showMessage(getActivity(), mainActivity.getPreferences().getString("Lan_Id", ""), "Tickets") + ": " + "Free entry");
                }*/
                txtCategory.setText(Constants.showMessage(getActivity(), mainActivity.getPreferences().getString("Lan_Id", ""), "Category") + ": " + nearbies.get(i).getCategory_Name());
                txtShare.setText(Constants.showMessage(getActivity(), mainActivity.getPreferences().getString("Lan_Id", ""), "Share"));
                txtFav.setText(Constants.showMessage(getActivity(), mainActivity.getPreferences().getString("Lan_Id", ""), "Favourite"));


                rl_share.setId(i);
                rl_share.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent mIntent = new Intent(getActivity(), ShareFragmentActivity.class);
                        String share1 = Constants.showMessage(getActivity(), mainActivity.getPreferences().getString("Lan_Id", ""), "share1");
                        String share2 = Constants.showMessage(getActivity(), mainActivity.getPreferences().getString("Lan_Id", ""), "share2");
                        String share3 = Constants.showMessage(getActivity(), mainActivity.getPreferences().getString("Lan_Id", ""), "share3");

                        mIntent.putExtra("myMsg", share1 + "\" " + share2 + " \"" + nearbies.get(v.getId()).getPlace_Name() + "\" " + share3);
                        startActivity(mIntent);
//
                    }
                });


                int dayFoundStatus = 0; //0 -> closed - 2-> open for 24 hours  1-> updated
                boolean isTicketSet = false;
                if (nearbies.get(i).getHourDetailsArrayList() != null && nearbies.get(i).getHourDetailsArrayList().size() > 0) {
                    for (HourDetails hourDetails : nearbies.get(i).getHourDetailsArrayList()) {
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
                                    tv_timing.setText(Constants.showMessage(getActivity(), mainActivity.getPreferences().getString("Lan_Id", ""), "Timing") + ": " + _24HourSDF.format(_24HourDt) + " " + Constants.showMessage(getActivity(), mainActivity.getPreferences().getString("Lan_Id", ""), "TO") + " " + _24HourSDF.format(_24HourDt1));
                                } else {
                                    tv_timing.setText(Constants.showMessage(getActivity(), mainActivity.getPreferences().getString("Lan_Id", ""), "Timing") + ": -");
                                    dayFoundStatus = 3;
                                }
                            }

                            if (hourDetails.getPOHCharges() != null && !hourDetails.getPOHCharges().equals("") && !hourDetails.getPOHCharges().equalsIgnoreCase("null")) {
                                isTicketSet = true;
//                                tv_ticket.setText(Constants.showMessage(getActivity(), mainActivity.getPreferences().getString("Lan_Id", ""), "Tickets") + ": " + hourDetails.getPOHCharges());
                            }
                          /*  if (nearbies.get(i).getFree_entry().equals("0")) {
                                tv_ticket.setText(Constants.showMessage(getActivity(), mainActivity.getPreferences().getString("Lan_Id", ""), "Tickets") + ": " + "Check details");
                            } else if (nearbies.get(i).getFree_entry().equals("1")){
                                tv_ticket.setText(Constants.showMessage(getActivity(), mainActivity.getPreferences().getString("Lan_Id", ""), "Tickets") + ": " + "Free entry");
                            }*/
                            break;
                        } else {
                            tv_timing.setText(Constants.showMessage(getActivity(), mainActivity.getPreferences().getString("Lan_Id", ""), "Timing") + ": -");
                            dayFoundStatus = 3;
                        }
                    }
                }
                if (dayFoundStatus == 3) {
                } else if (dayFoundStatus == 2) {
                    tv_timing.setText(Constants.showMessage(getActivity(), mainActivity.getPreferences().getString("Lan_Id", ""), "Timing") + ": " + Constants.showMessage(getActivity(), mainActivity.getPreferences().getString("Lan_Id", ""), "Open Now"));

                } else {
                    tv_timing.setText(Constants.showMessage(getActivity(), mainActivity.getPreferences().getString("Lan_Id", ""), "Timing") + ": " + Constants.showMessage(getActivity(), mainActivity.getPreferences().getString("Lan_Id", ""), "Closed"));
                }
                if (!isTicketSet) {
//                    tv_ticket.setText(Constants.showMessage(getActivity(), mainActivity.getPreferences().getString("Lan_Id", ""), "Tickets") + ": -");
                }
                if (nearbies.get(i).getFav_Id().equalsIgnoreCase("0")) {
                    imgFav.setActivated(false);
                } else {
                    imgFav.setActivated(true);
                }

                txtDistance.setText(nearbies.get(i).getDistance() + Constants.showMessage(getActivity(), mainActivity.getPreferences().getString("Lan_Id", ""), "KM"));

                rl_fav.setId(i);
                rl_fav.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mainActivity.getPreferences().getString("User_Id", "").equalsIgnoreCase("0")) {
                            mainActivity.showGuestSnackToast();
                        } else {
                            for (int j = 0; j < nearbies.size(); j++) {
                                if (v.getId() == j) {
                                    if (nearbies.get(j).getFav_Id().equalsIgnoreCase("0")) {
                                        imgFav.setActivated(true);
                                        addFavoriteCall(nearbies.get(j).getPlace_Id(), nearbies.get(j).getGroup_Id());
                                        mFlag = j;
                                    } else {
                                        imgFav.setActivated(false);
                                        deleteFavoriteCall(nearbies.get(j).getFav_Id());
                                        mFlag = j;
                                    }
                                }
                            }
                        }
                    }
                });

                llView.setId(i);
                llView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                        ll_explorer1.setVisibility(View.GONE);
                        mFrame = 1;
                        id = v.getId();
                        mainActivity.exploreNearbyFragment.replacePlaceDetailsFragment(nearbies.get(v.getId()).getPlace_Id(), tv_city.getText().toString());

                        //setDetailInfo(nearbies.get(v.getId()));
//                        mNearby = nearbies.get(v.getId());
                    }
                });
                iv_nearby_explorer.setId(i);
                iv_nearby_explorer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                        ll_explorer1.setVisibility(View.GONE);
                        mFrame = 1;
                        id = v.getId();
                        mainActivity.exploreNearbyFragment.replacePlaceDetailsFragment(nearbies.get(v.getId()).getPlace_Id(), tv_city.getText().toString());
                        // setDetailInfo(nearbies.get(v.getId()));
//                        mNearby = nearbies.get(v.getId());
                    }
                });

                rl_navigator.setId(i);
                rl_navigator.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        String uri = String.format(Locale.ENGLISH, "http://maps.google.com/maps?saddr=%f,%f&daddr=%f,%f", Double.parseDouble(mainActivity.getPreferences().getString("latitude1", "")), Double.parseDouble(mainActivity.getPreferences().getString("longitude1", "")), Double.parseDouble(nearbies.get(rl_navigator.getId()).getPlace_Latitude()), Double.parseDouble(nearbies.get(rl_navigator.getId()).getPlace_Longi()));
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                        intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
                        startActivity(intent);
                    }
                });

                ll_nearby_explorer.addView(view);
            }
        } else {
            if (!isCalledFromCat)
                llEmptyLayout.setVisibility(View.VISIBLE);
            else {
                sv_explorer_location.setVisibility(View.GONE);
            }
//            SnackbarManager.show(Snackbar.with(getActivity()).color(Utilities.getColor(getActivity(), R.color.mBlue)).text(Constants.showMessage(getActivity(), mainActivity.getPreferences().getString("Lan_Id", ""), "NORECORD")));
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

    private void addFavoriteCall(String Place_Id, String group_id) {
        if (CommonClass.hasInternetConnection(getActivity())) {
            String url = Constants.SERVER_URL + "json.php?action=AddFavorite";
            mPlaceId = Place_Id;
            mGroupId = group_id;
            String groupId = mNearby.getGroup_Id();
            String json = "[{\"User_Id\":\"" + mainActivity.getPreferences().getString("User_Id", "") + "\",\"Place_Id\":\"" + Place_Id + "\",\"Group_Id\":\"" + groupId + "\"}]";
            new post_sync(getActivity(), "AddFavorite", HomeFragment.this, true).execute(url, json);
            System.out.println("addfav_json" + json);
        } else {
            SnackbarManager.show(Snackbar.with(getActivity()).color(Utilities.getColor(getActivity(), R.color.mBlue)).text(Constants.showMessage(getActivity(), mainActivity.getPreferences().getString("Lan_Id", ""), "NOINTERNET")));
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
                        if (mCounter == -1) {
                            for (Nearby nearby : nearbies) {
                                if (nearby.getPlace_Id().equals(mPlaceId)) {
                                    nearby.setFav_Id(jsonObject.optString("Fav_Id"));
                                    setNearBy();
                                    break;
                                }
                            }
                            for (Nearby nearby : recommendeds) {
                                if (nearby.getPlace_Id().equals(mPlaceId)) {
                                    nearby.setFav_Id(jsonObject.optString("Fav_Id"));
//                                    galleryAdapter.notifyDataSetChanged();
                                    break;
                                }
                            }
                        }
                        mCounter = -1;
                        mFlag = 0;
                        like = 1;
                        mPlaceId = "";

                        Constants.mStaticFavCall = 0;
                        Constants.mStaticNearCall = 0;

                        SnackbarManager.show(Snackbar.with(getActivity()).color(Utilities.getColor(getActivity(), R.color.mBlue)).text(Constants.showMessage(getActivity(), mainActivity.getPreferences().getString("Lan_Id", ""), "AddFavourite")));
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void deleteFavoriteCall(String Fav_Id) {
        if (CommonClass.hasInternetConnection(getActivity())) {
            String url = Constants.SERVER_URL + "json.php?action=DeleteFavorite";
            String json = "[{\"User_Id\":\"" + mainActivity.getPreferences().getString("User_Id", "") + "\",\"Fav_Id\":\"" + Fav_Id + "\"}]";
            mFav = Fav_Id;
            new post_sync(getActivity(), "DeleteFavorite", HomeFragment.this, true).execute(url, json);

        } else {
            Intent intent = new Intent(getActivity(), NoInternet.class);
            startActivity(intent);
            //SnackbarManager.show(Snackbar.with(getActivity()).color(Utilities.getColor(getActivity(), R.color.mBlue)).text(Constants.showMessage(getActivity(), mainActivity.getPreferences().getString("Lan_Id", ""), "NOINTERNET")));
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
                        if (mCounter == -1) {
                            // nearbies.get(mFlag).setFav_Id("0");
                            for (Nearby nearby : nearbies) {
                                if (nearby.getFav_Id().equals(mFav)) {
                                    nearby.setFav_Id("0");
                                    setNearBy();

                                    break;
                                }
                            }
                            for (Nearby nearby : recommendeds) {
                                if (nearby.getFav_Id().equals(mFav)) {
                                    nearby.setFav_Id("0");
                                    //galleryAdapter.notifyDataSetChanged();
                                    break;
                                }
                            }
                            // setDetailInfo(nearbies.get(mFlag));
                        }
                        mFlag = 0;
                        mFav = "";
                        Constants.mStaticFavCall = 0;
                        Constants.mStaticNearCall = 0;
                        like = 0;
                        SnackbarManager.show(Snackbar.with(getActivity()).color(Utilities.getColor(getActivity(), R.color.mBlue)).text(Constants.showMessage(getActivity(), mainActivity.getPreferences().getString("Lan_Id", ""), "Removefavorite")));
                    }
                }
            } catch (JSONException e) {

            }
        }
    }

    private void searchCall(String Place_Id) {
        if (CommonClass.hasInternetConnection(getActivity())) {
            String url = Constants.SERVER_URL + "json.php?action=PlaceDetails";
            String json = "[{\"Lan_Id\":\"" + mainActivity.getPreferences().getString("Lan_Id", "") + "\",\"User_Id\":\"" + mainActivity.getPreferences().getString("User_Id", "") + "\",\"Current_Latitude\":\"" + mainActivity.getPreferences().getString("latitude2", "") + "\",\"Current_Longitude\":\"" + mainActivity.getPreferences().getString("longitude2", "") + "\",\"Place_Id\":\"" + Place_Id + "\"}]";
            new post_sync(getActivity(), "PlaceDetails", HomeFragment.this, true).execute(url, json);

        } else {
            Intent intent = new Intent(getActivity(), NoInternet.class);
            startActivity(intent);
            //SnackbarManager.show(Snackbar.with(getActivity()).color(Utilities.getColor(getActivity(), R.color.mBlue)).text(Constants.showMessage(getActivity(), mainActivity.getPreferences().getString("Lan_Id", ""), "NOINTERNET")));
        }
    }

    public void searchResponse(String resultString) {
//        Log.d("System out", resultString);

        JSONObjConverter jsonObjConverter = new JSONObjConverter();
        try {
            JSONArray jsonArray = new JSONArray(resultString);
            JSONObject jsonObject = jsonArray.optJSONObject(0);


            if (jsonObject.has("similar")) {
                nearbies1.clear();
                JSONArray jsonArray1 = jsonObject.optJSONArray("similar");
                for (int i = 0; i < jsonArray1.length(); i++) {
                    Nearby nearby = jsonObjConverter.convertJsonToNearByObj(jsonArray1.optJSONObject(i));


                    if (mNearby.getPlace_Id() != null && nearby.getPlace_Id() != null) {
                        if (!mNearby.getPlace_Id().equals(nearby.getPlace_Id())) {
                            nearby.setDistance(Utilities.GetRoutDistane(Double.parseDouble(mainActivity.getPreferences().getString("latitude2", "")), Double.parseDouble(mainActivity.getPreferences().getString("longitude2", "")), Double.parseDouble(nearby.getPlace_Latitude()), Double.parseDouble(nearby.getPlace_Longi()), nearby.getDist()));
                            nearbies1.add(nearby);
                            for (int idx = 0; idx < nearbies1.size(); idx++) {
                                System.out.println("nearby_arr " + nearbies1.get(idx));
                            }

                        }
                    }
                }
                nearbies1 = Utilities.sortLocations(nearbies1);
            }
            Nearby recommendedTemp = new Nearby();
            if (isFromBeacon) {
                recommendedTemp = jsonObjConverter.convertJsonToNearByObj(jsonObject);

                mFrame = 1;
                mNearby = recommendedTemp;
                isFromBeacon = false;
                Constants.placeId = null;
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void searchCall1() {
        if (CommonClass.hasInternetConnection(getActivity())) {
            String url = Constants.SERVER_URL + "json.php?action=PlaceByCategory";
            String json = "[{\"Lan_Id\":\"" + mainActivity.getPreferences().getString("Lan_Id", "") + "\",\"User_Id\":\"" + mainActivity.getPreferences().getString("User_Id", "") + "\",\"Current_Latitude\":\"" + mainActivity.getPreferences().getString("latitude2", "") + "\",\"Current_Longitude\":\"" + mainActivity.getPreferences().getString("longitude2", "") + "\",\"Category_Id\":\"" + Category_Id + "\",\"keyword\":\"" + mainActivity.getPreferences().getString(Preference.Pref_City, "") + "\"}]";
            new post_sync(getActivity(), "PlaceByCategory", HomeFragment.this, true).execute(url, json);

        } else {
            Intent intent = new Intent(getActivity(), NoInternet.class);
            startActivity(intent);
            //SnackbarManager.show(Snackbar.with(getActivity()).color(Utilities.getColor(getActivity(), R.color.mBlue)).text(Constants.showMessage(getActivity(), mainActivity.getPreferences().getString("Lan_Id", ""), "NOINTERNET")));
        }
    }

    public void searchResponse1(String resultString) {
        JSONObjConverter jonObjConverter = new JSONObjConverter();

        if (resultString.length() > 2) {
            try {
                JSONArray jsonArray = new JSONArray(resultString);
                JSONObject jsonObject = jsonArray.optJSONObject(0);
                if (jsonObject.has("recommnded")) {
                    JSONArray jsonArray1 = jsonObject.optJSONArray("recommnded");
                    for (int i = 0; i < jsonArray1.length(); i++) {
//                            JSONObject jsonObject1 = jsonArray1.optJSONObject(i);
                        recommended = jonObjConverter.convertJsonToNearByObj(jsonArray1.optJSONObject(i));
                        recommendeds.add(recommended);
                    }
                }

                if (jsonObject.has("category")) {
                    nearbies_category.clear();
                    JSONArray jsonArray1 = jsonObject.optJSONArray("category");

                    for (int i = 0; i < jsonArray1.length(); i++) {
                        nearby = jonObjConverter.convertJsonToNearByObj(jsonArray1.optJSONObject(i));
                        nearbies_category.add(nearby);
                    }
                    Constants.mStaticNearCall = 1;

                    mainActivity.exploreNearbyFragment.replaceSearchResultFragment(nearbies_category, tv_city.getText().toString(), true);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onAttach(Context activity) {
        super.onAttach(activity);
        mainActivity = (MainActivity) getActivity();
    }


    private class ExplorerAdapter extends BaseAdapter {
        Context context;

        public ExplorerAdapter(Context context) {
            this.context = context;
        }

        @Override
        public int getCount() {
            return allCategories.size();
        }

        @Override
        public Object getItem(int position) {
            return allCategories.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            LayoutInflater inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.explore_adapter, parent, false);

            final LinearLayout llCategory = (LinearLayout) convertView.findViewById(R.id.llCategory);
            final NormalTextView tv_explore_grid = (NormalTextView) convertView.findViewById(R.id.tv_explore_grid);
            final LinearLayout ll_right = (LinearLayout) convertView.findViewById(R.id.ll_right);


            llCategory.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    isListOfCategoryVisible = true;

                    Category_Id = allCategories.get(position).getCategory_Id();
                    isCalledFromCat = true;
                    searchCall1();
                }
            });
            isShowLess = true;


            ll_explorer.setBackgroundResource(R.drawable.ic_bg_category_);
            tv_explore_grid.setText(allCategories.get(position).getCategory_Name());
            if (position % 2 == 0) {
                ll_right.setVisibility(View.INVISIBLE);
            }

            return convertView;
        }
    }

    private class ExplorerAdapter1 extends BaseAdapter {
        Context context;

        public ExplorerAdapter1(Context context) {
            this.context = context;
        }

        @Override
        public int getCount() {
            return allCategories1.size();
//            return isShowLess?10:allCategories1.size();
        }

        @Override
        public Object getItem(int position) {
            return allCategories1.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            LayoutInflater inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.explore_adapter, parent, false);
            final LinearLayout llCategory = (LinearLayout) convertView.findViewById(R.id.llCategory);

            final NormalTextView tv_explore_grid = (NormalTextView) convertView.findViewById(R.id.tv_explore_grid);
            final LinearLayout ll_right = (LinearLayout) convertView.findViewById(R.id.ll_right);


            isShowLess = false;
            llCategory.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    isListOfCategoryVisible = true;
                    Category_Id = allCategories1.get(position).getCategory_Id();
                    isCalledFromCat = true;
                    searchCall1();


                }
            });

            ll_explorer.setBackgroundResource(R.drawable.ic_bg_category);

            tv_explore_grid.setText(allCategories1.get(position).getCategory_Name());

            if (position % 2 == 0) {
                ll_right.setVisibility(View.INVISIBLE);
            }

            return convertView;
        }
    }
}