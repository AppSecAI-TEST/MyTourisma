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
import android.support.v7.widget.RecyclerView;
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
import com.ftl.tourisma.MyTorismaApplication;
import com.ftl.tourisma.R;
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
import com.pixplicity.easyprefs.library.Prefs;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;
import static com.ftl.tourisma.utils.Constants.PlaceClosed;
import static com.ftl.tourisma.utils.Constants.PlaceOpenFor24Hours;
import static com.ftl.tourisma.utils.Constants.PlaceOpenWithAnyTime;

/**
 * Created by harpalsinh on 26-Feb-2016.
 */
public class NewHomeFragment extends Fragment implements View.OnClickListener, Updatable, ViewPagerEx.OnPageChangeListener, post_sync.ResponseHandler {

    private static final String TAG = "HomeFragment";
    private static int mFrame = 0;
    private static int mCounter = -1;
    MainActivity mainActivity;
    ArrayList<HourDetails> hourDetails;
    String group_id;
    int mFlag = 0;
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
    private NormalTextView txt_recommended, txt_description, txt_categoty;
    private ImageView iv_back3, mDotsText1[];
    private LinearLayout ll_cate1, ll_explorer, ll_explorer1, ll_nearby_explorer;
    private SimpleDateFormat _24HourSDF, _12HourSDF;
    private String _24HourTime, _24HourTime1;
    private Date _24HourDt, _24HourDt1;
    private NormalBoldTextView tv_full_name;
    private NormalTextView txt_add_to_fav, tv_discription;
    private String Category_Name, Category_Id;
    private FloatingActionButton fb_category;
    private DisplayImageOptions options;
    private NormalTextView tv_explore, tv_your_mood, tv_nearby, tv_see_whats;
    private Nearby mNearby = new Nearby();
    private ImageView iv_explore;
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
    //    private HomePageAdapter adapter;
    private RecyclerView categories_rv, nearby_rv;
    // new id'// STOPSHIP: 23-07-2017
    private RelativeLayout rl_home, rl_recommended;
    private ScrollView sv_explorer_location;
    private NormalTextView txtShowMoreLess, txtMessage, txtSuggest, txtOk, tv_city, tv_your_location_header3, tv_recommended, main_description, description, explore_txt, nearby_txt;
    private LinearLayout ll_change_city, llEmptyLayout;
    private ImageView iv_search_header3, imgFav;
    private int id;
    private int like;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        _24HourSDF = new SimpleDateFormat("HH:mm");
        _12HourSDF = new SimpleDateFormat("hh:mma");
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

//        explorerAdapter = new ExplorerAdapter(getActivity());
//        gv_explorer.setAdapter(null);
//        gv_explorer.setAdapter(explorerAdapter);
//        gv_explorer.setExpanded(true);
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
        view = inflater.inflate(R.layout.new_home_fragment, container, false);

        sv_explorer_location = (ScrollView) view.findViewById(R.id.sv_explorer_location);
        iv_back3 = (ImageView) view.findViewById(R.id.iv_back3);
        iv_back3.setOnClickListener(this);
//        iv_explore = (ImageView) view.findViewById(R.id.iv_explore);
//        iv_explore.setImageResource(R.drawable.ic_clock);
        tv_your_location_header3 = (NormalTextView) view.findViewById(R.id.tv_your_location_header3);
        tv_your_location_header3.setText(Constants.showMessage(getActivity(), mainActivity.getPreferences().getString("Lan_Id", ""), "locationtitle"));
        explore_txt = (NormalTextView) view.findViewById(R.id.explore_txt);
        explore_txt.setText(Constants.showMessage(getActivity(), mainActivity.getPreferences().getString("Lan_Id", ""), "explore"));
//        tv_your_mood = (NormalTextView) view.findViewById(R.id.tv_your_mood);
//        tv_your_mood.setText(Constants.showMessage(getActivity(), mainActivity.getPreferences().getString("Lan_Id", ""), "exploresubtitle"));
        nearby_txt = (NormalTextView) view.findViewById(R.id.nearby_txt);
        nearby_txt.setText(Constants.showMessage(getActivity(), mainActivity.getPreferences().getString("Lan_Id", ""), "nearby"));
//        tv_see_whats = (NormalTextView) view.findViewById(R.id.tv_see_whats);
//        tv_see_whats.setText(Constants.showMessage(getActivity(), mainActivity.getPreferences().getString("Lan_Id", ""), "nearbysubtitle"));
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
        /*fb_category = (FloatingActionButton) view.findViewById(R.id.fb_category);
        fb_category.setOnClickListener(this);*/
        rl_recommended = (RelativeLayout) view.findViewById(R.id.rl_recommended);
        llEmptyLayout = (LinearLayout) view.findViewById(R.id.llEmptyLayout);
        ll_cate1 = (LinearLayout) view.findViewById(R.id.ll_cate1);
//        nearby_rv = (RecyclerView) view.findViewById(R.id.nearby_rv);

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
        txt_description = (NormalTextView) view.findViewById(R.id.main_description);
        txt_categoty = (NormalTextView) view.findViewById(R.id.description);
        imgFav = (ImageView) view.findViewById(R.id.imgFav);
        custom_indicator = (PagerIndicator) view.findViewById(R.id.custom_indicator);
        custom_indicator.setIndicatorStyleResource(R.drawable.shape_cirlce_fill, R.drawable.shape_cirlce_unfill);
        txt_recommended.setText(Constants.showMessage(getActivity(), mainActivity.getPreferences().getString("Lan_Id", ""), "recommended"));
        txt_description.setText("");
//        txt_categoty.setText("");

        /*//setting layoutmanger for nearby places
        org.solovyev.android.views.llm.LinearLayoutManager linearLayoutManager_nearby = new org.solovyev.android.views.llm.LinearLayoutManager(getActivity());
        nearby_rv.setLayoutManager(linearLayoutManager_nearby);
*/
        getAllCategories();
        return view;
    }

    private void homePageDataCall() {
        isCalledFromCat = false;
        if (CommonClass.hasInternetConnection(getActivity())) {
            String url = Constants.SERVER_URL + "json.php?action=newHomePageData";
            String json = "[{\"Lan_Id\":\"" + mainActivity.getPreferences().getString("Lan_Id", "") + "\",\"User_Id\":\"" + mainActivity.getPreferences().getString("User_Id", "") + "\",\"Current_Latitude\":\"" + mainActivity.getPreferences().getString("latitude2", "") + "\",\"Current_Longitude\":\"" + mainActivity.getPreferences().getString("longitude2", "") + "\",\"City_Name\":\"" + mainActivity.getPreferences().getString(Preference.Pref_City, "") + "\",\"deviceType\":\"" + "Android" + "\",\"deviceId\":\"" + Prefs.getString(Constants.fcm_regid, "") + "\"}]";
            System.out.println("homepagedata_json " + json);
            System.out.println("homepagedata_url " + url);
            new post_sync(getActivity(), "HomePageData", NewHomeFragment.this, true).execute(url, json);
        } else {
            Intent intent = new Intent(getActivity(), NoInternet.class);
            startActivity(intent);
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
                        // Tracking exception
                        MyTorismaApplication.getInstance().trackException(e);
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

    private void getAllCategories() {
        if (CommonClass.hasInternetConnection(getActivity())) {
            String url = Constants.SERVER_URL + "json.php?action=GetCategoriesByLanId";
            String json = "[{\"Lan_Id\":\"" + mainActivity.getPreferences().getString("Lan_Id", "") + "\"}]";
            new post_sync(getActivity(), "GetCategoriesByLanId", NewHomeFragment.this, true).execute(url, json);
        } else {
            Intent intent = new Intent(getActivity(), NoInternet.class);
            startActivity(intent);
        }
    }

    public void getAllCategoriesResponse(String resultString) {
        allCategories.clear();
        allCategories1.clear();
        homePageDataCall();
        try {
            JSONArray jsonArray = new JSONArray(resultString);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.optJSONObject(i);
                categories = new AllCategories();
                categories.setCategory_Id(jsonObject.optString("Category_Id"));
                categories.setCategory_Name(jsonObject.optString("Category_Name"));
                categories.setCategory_Map_Icon(jsonObject.optString("Category_Map_Icon"));
                categories.setLan_Id(jsonObject.optString("Lan_Id"));
                categories.setCategory_Image(jsonObject.optString("Category_Image"));
                categories.setCategory_Info(jsonObject.optString("Category_Info"));
                categories.setCategory_Status(jsonObject.optString("Category_Status"));
                if (allCategories1.size() <= 9) {
                    allCategories.add(categories);
                }
                allCategories1.add(categories);
            }
        } catch (JSONException e) {
            // Tracking exception
            MyTorismaApplication.getInstance().trackException(e);
            e.printStackTrace();
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
                                    mFrame = 1;
                                    id = slider.getCurrentPosition();
                                    mainActivity.exploreNearbyFragment.replacePlaceDetailsFragment(recommendeds.get(id).getPlace_Id(), tv_city.getText().toString(), recommendeds.get(id).getGroup_Id());
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
        if (recommendeds.size() == 0) {
            llEmptyLayout.setVisibility(View.VISIBLE);
        } else {
            setNearBy();
            System.out.println("nearby_response_123");
//            adapter = new HomePageAdapter(getActivity(), nearbies);
//            nearby_rv.setHasFixedSize(true);
//            nearby_rv.setAdapter(adapter);
        }
    }

    @Override
    public void onClick(View v) {
        if (v == iv_search_header3) {
            mainActivity.exploreNearbyFragment.replaceSearchFragment();
        } else if (v == ll_change_city || v == txtOk) {
            mainActivity.exploreNearbyFragment.replaceLocationFragment();
        } else if (v == iv_back3) {
            Constants.mStaticNearCall = 0;
            update();
        } else if (v == txtShowMoreLess) {
            if (isShowLess) {
                explorerAdapter1 = new ExplorerAdapter1(getActivity());
                gv_explorer.setAdapter(null);
                gv_explorer.setAdapter(explorerAdapter1);
                gv_explorer.setExpanded(true);
                txtShowMoreLess.setText(Constants.showMessage(getActivity(), mainActivity.getPreferences().getString("Lan_Id", ""), "Less"));
            } else {
                txtShowMoreLess.setText(Constants.showMessage(getActivity(), mainActivity.getPreferences().getString("Lan_Id", ""), "More"));
                explorerAdapter = new ExplorerAdapter(getActivity());
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
            // Tracking exception
            MyTorismaApplication.getInstance().trackException(e);
            Log.d(TAG, "setUserVisibleHint " + e.getLocalizedMessage());
        }
    }

    @Override
    public void update() {
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(final int position) {
        try {
            if (recommendeds != null && recommendeds.size() > 0 && recommendeds.size() > position) {
                txt_description.setText(recommendeds.get(position).getPlace_Name());
                txt_categoty.setText(recommendeds.get(position).getCategory_Name());
                Animation animFadeIn = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_in);
                txt_description.setAnimation(animFadeIn);
                txt_categoty.setAnimation(animFadeIn);
                imgFav.setActivated(!recommendeds.get(position).getFav_Id().equalsIgnoreCase("0"));
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
//                                deleteFavoriteCall(recommendeds.get(position).getFav_Id());
                                deleteFavoriteCall(recommendeds.get(position).getFav_Id(), recommendeds.get(position).getGroup_Id());
                            }
                        }
                    }
                });
            }
        } catch (Exception e) {
            // Tracking exception
            MyTorismaApplication.getInstance().trackException(e);
            Log.e(TAG, "onPageSelected Exception " + e.getLocalizedMessage());
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

    @Override
    public void onResponse(String response, String action) {
        try {
            if (action.equalsIgnoreCase("HomePageData")) {
                homePageDataResponse(response);
            } else if (action.equalsIgnoreCase("GetCategoriesByLanId")) {
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
            // Tracking exception
            MyTorismaApplication.getInstance().trackException(e);
            Log.e(TAG, "onResponse Exception " + e.getLocalizedMessage());
        }
    }

    public void setNearBy() {
        ll_nearby_explorer.removeAllViews();
        if (nearbies.size() > 0) {
           /* for (Nearby nearby : nearbies) {
                double d = Utilities.GetRoutDistane(Double.parseDouble(mainActivity.getPreferences().getString("latitude2", "")), Double.parseDouble(mainActivity.getPreferences().getString("longitude2", "")), Double.parseDouble(nearby.getPlace_Latitude()), Double.parseDouble(nearby.getPlace_Longi()), nearby.getDist());
                nearby.setDistance(d);
            }*/
//            nearbies = Utilities.sortLocations(nearbies);
            llEmptyLayout.setVisibility(View.GONE);
            for (int position = 0; position < nearbies.size(); position++) {
                LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(LAYOUT_INFLATER_SERVICE);
                View view = inflater.inflate(R.layout.new_home_nearby_recycler, null);
                final ImageView nearby_img = (ImageView) view.findViewById(R.id.nearby_img);
                final ImageView imgFav = (ImageView) view.findViewById(R.id.imgFav);
                final NormalTextView category_txt = (NormalTextView) view.findViewById(R.id.nearby_category_txt);
                final NormalTextView place_txt = (NormalTextView) view.findViewById(R.id.place_txt);
                final NormalTextView timing_txt = (NormalTextView) view.findViewById(R.id.timing_txt);
                final NormalTextView ticket_txt = (NormalTextView) view.findViewById(R.id.ticket_txt);
                final NormalTextView dist_txt = (NormalTextView) view.findViewById(R.id.dist_txt);
                final RelativeLayout rl_main_img = (RelativeLayout) view.findViewById(R.id.rl_main_img);
                final RelativeLayout rlBottomView = (RelativeLayout) view.findViewById(R.id.rlBottomView);

                System.out.println("nearby123 " + nearbies.get(position).getCategory_Name());
                String imageURL = Constants.IMAGE_URL + nearbies.get(position).getPlace_MainImage() + "&w=" + (mainActivity.width);
                Picasso.with(getActivity())
                        .load(imageURL)
                        .resize(mainActivity.width, (mainActivity.height * 60) / 100)
                        .into(nearby_img);
                category_txt.setText(nearbies.get(position).getCategory_Name());
                place_txt.setText(nearbies.get(position).getPlace_Name());
                if (nearbies.get(position).getFree_entry().equals("0")) {
                    ticket_txt.setText(Constants.showMessage(getActivity(), mainActivity.getPreferences().getString("Lan_Id", ""), "Check Details"));
                    ticket_txt.setSelected(true);
                    ticket_txt.requestFocus();
                } else if (nearbies.get(position).getFree_entry().equals("1")) {
                    ticket_txt.setText(Constants.showMessage(getActivity(), mainActivity.getPreferences().getString("Lan_Id", ""), "Free Entry"));
                    ticket_txt.setSelected(true);
                    ticket_txt.requestFocus();
                }
                dist_txt.setText(nearbies.get(position).getDist());

                if (nearbies.get(position).getFav_Id().equalsIgnoreCase("0")) {
                    imgFav.setActivated(false);
                } else {
                    imgFav.setActivated(true);
                }

                if (nearbies.get(position).getFav_Id().equalsIgnoreCase("0")) {
                    imgFav.setActivated(false);
                } else {
                    imgFav.setActivated(true);
                }

                imgFav.setId(position);
                imgFav.setOnClickListener(new View.OnClickListener() {
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
                                        deleteFavoriteCall(nearbies.get(j).getFav_Id(), nearbies.get(j).getGroup_Id());
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
                                        // Tracking exception
                                        MyTorismaApplication.getInstance().trackException(e);
                                        e.printStackTrace();
                                    }
                                }
                                if (_24HourTime1 != null && !_24HourTime1.equalsIgnoreCase("NULL")) {
                                    try {
                                        _24HourDt1 = _24HourSDF.parse(_24HourTime1);
                                    } catch (ParseException e) {
                                        // Tracking exception
                                        MyTorismaApplication.getInstance().trackException(e);
                                        e.printStackTrace();
                                    }
                                }
                                if (_24HourDt != null && _24HourDt1 != null) {
                                    timing_txt.setText(_24HourSDF.format(_24HourDt) + " " + Constants.showMessage(getActivity(), mainActivity.getPreferences().getString("Lan_Id", ""), "TO") + " " + _24HourSDF.format(_24HourDt1));
                                } else {
                                    timing_txt.setText("");
                                    dayFoundStatus = 3;
                                }
                            }
                            if (hourDetails.getPOHCharges() != null && !hourDetails.getPOHCharges().equals("") && !hourDetails.getPOHCharges().equalsIgnoreCase("null")) {
                                isTicketSet = true;
                            }
                            break;
                        } else {
                            timing_txt.setText(Constants.showMessage(getActivity(), mainActivity.getPreferences().getString("Lan_Id", ""), "Timing") + ": -");
                            dayFoundStatus = 3;
                        }
                    }
                }
                if (dayFoundStatus == 3) {
                } else if (dayFoundStatus == 2) {
                    timing_txt.setText(Constants.showMessage(getActivity(), mainActivity.getPreferences().getString("Lan_Id", ""), "Open Now"));
                } else {
                    timing_txt.setText(Constants.showMessage(getActivity(), mainActivity.getPreferences().getString("Lan_Id", ""), "Closed"));
                }

                rl_main_img.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mFrame = 1;
                        id = v.getId();
                        mainActivity.exploreNearbyFragment.replacePlaceDetailsFragment(nearbies.get(v.getId()).getPlace_Id(), tv_city.getText().toString(), nearbies.get(v.getId()).getGroup_Id());
                    }
                });

                rlBottomView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mFrame = 1;
                        id = v.getId();
                        mainActivity.exploreNearbyFragment.replacePlaceDetailsFragment(nearbies.get(v.getId()).getPlace_Id(), tv_city.getText().toString(), nearbies.get(v.getId()).getGroup_Id());
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
            // Tracking exception
            MyTorismaApplication.getInstance().trackException(e);
            Utils.Log(TAG, "suggestPlace Exception: " + e.getLocalizedMessage());
        }
    }

    private void addFavoriteCall(String Place_Id, String group_id) {
        if (CommonClass.hasInternetConnection(getActivity())) {
            String url = Constants.SERVER_URL + "json.php?action=AddFavorite";
//            String url = "http://35.154.205.155/mytourisma/json.php?action=AddFavorite";
            mPlaceId = Place_Id;
            String json = "[{\"User_Id\":\"" + mainActivity.getPreferences().getString("User_Id", "") + "\",\"Place_Id\":\"" + Place_Id + "\",\"Group_Id\":\"" + group_id + "\"}]";
            new post_sync(getActivity(), "AddFavorite", NewHomeFragment.this, true).execute(url, json);
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
//                                    setNearBy();
                                    break;
                                }
                            }
                            for (Nearby nearby : recommendeds) {
                                if (nearby.getPlace_Id().equals(mPlaceId)) {
                                    nearby.setFav_Id(jsonObject.optString("Fav_Id"));
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
                // Tracking exception
                MyTorismaApplication.getInstance().trackException(e);
                e.printStackTrace();
            }
        }
    }

    /*private void deleteFavoriteCall(String Fav_Id, String group_id) {*/
    private void deleteFavoriteCall(String Fav_Id, String group_id) {
        if (CommonClass.hasInternetConnection(getActivity())) {
            String url = Constants.SERVER_URL + "json.php?action=DeleteFavorite";
//            String json = "[{\"User_Id\":\"" + mainActivity.getPreferences().getString("User_Id", "") + "\",\"Fav_Id\":\"" + Fav_Id + "\"}]";
//            String url = "http://35.154.205.155/mytourisma/json.php?action=DeleteFavorite";
            String json = "[{\"User_Id\":\"" + mainActivity.getPreferences().getString("User_Id", "") + "\",\"Group_Id\":\"" + group_id + "\"}]";
            mFav = Fav_Id;
            new post_sync(getActivity(), "DeleteFavorite", NewHomeFragment.this, true).execute(url, json);
        } else {
            Intent intent = new Intent(getActivity(), NoInternet.class);
            startActivity(intent);
            //SnackbarManager.show(Snackbar.with(getActivity()).color(Utilities.getColor(getActivity(), R.color.mBlue)).text(Constants.showMessage(getActivity(), mainActivity.getPreferences().getString("Lan_Id", ""), "NOINTERNET")));
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
                        if (mCounter == -1) {
                            for (Nearby nearby : nearbies) {
                                if (nearby.getFav_Id().equals(mFav)) {
                                    nearby.setFav_Id("0");
//                                    setNearBy();
                                    break;
                                }
                            }
                            for (Nearby nearby : recommendeds) {
                                if (nearby.getFav_Id().equals(mFav)) {
                                    nearby.setFav_Id("0");
                                    break;
                                }
                            }
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
                // Tracking exception
                MyTorismaApplication.getInstance().trackException(e);
            }
        }
    }

    private void searchCall(String Place_Id) {
        if (CommonClass.hasInternetConnection(getActivity())) {
            String url = Constants.SERVER_URL + "json.php?action=PlaceDetails";
            String json = "[{\"Lan_Id\":\"" + mainActivity.getPreferences().getString("Lan_Id", "") + "\",\"User_Id\":\"" + mainActivity.getPreferences().getString("User_Id", "") + "\",\"Current_Latitude\":\"" + mainActivity.getPreferences().getString("latitude2", "") + "\",\"Current_Longitude\":\"" + mainActivity.getPreferences().getString("longitude2", "") + "\",\"Place_Id\":\"" + Place_Id + "\"}]";
            new post_sync(getActivity(), "PlaceDetails", NewHomeFragment.this, true).execute(url, json);
        } else {
            Intent intent = new Intent(getActivity(), NoInternet.class);
            startActivity(intent);
            //SnackbarManager.show(Snackbar.with(getActivity()).color(Utilities.getColor(getActivity(), R.color.mBlue)).text(Constants.showMessage(getActivity(), mainActivity.getPreferences().getString("Lan_Id", ""), "NOINTERNET")));
        }
    }

    public void searchResponse(String resultString) {
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
            // Tracking exception
            MyTorismaApplication.getInstance().trackException(e);
        }
    }

    private void searchCall1() {
        if (CommonClass.hasInternetConnection(getActivity())) {
            String url = Constants.SERVER_URL + "json.php?action=PlaceByCategory";
            String json = "[{\"Lan_Id\":\"" + mainActivity.getPreferences().getString("Lan_Id", "") + "\",\"User_Id\":\"" + mainActivity.getPreferences().getString("User_Id", "") + "\",\"Current_Latitude\":\"" + mainActivity.getPreferences().getString("latitude2", "") + "\",\"Current_Longitude\":\"" + mainActivity.getPreferences().getString("longitude2", "") + "\",\"Category_Id\":\"" + Category_Id + "\",\"keyword\":\"" + mainActivity.getPreferences().getString(Preference.Pref_City, "") + "\"}]";
            new post_sync(getActivity(), "PlaceByCategory", NewHomeFragment.this, true).execute(url, json);
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
                // Tracking exception
                MyTorismaApplication.getInstance().trackException(e);
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
            convertView = inflater.inflate(R.layout.new_home_categories_recycler, parent, false);
            NormalTextView category_name = (NormalTextView) convertView.findViewById(R.id.category_name);
            NormalTextView place_count_txt = (NormalTextView) convertView.findViewById(R.id.place_count_txt);
            ImageView category_img = (ImageView) convertView.findViewById(R.id.category_img);
            isShowLess = true;

            category_name.setText(allCategories.get(position).getCategory_Name());
            place_count_txt.setText(allCategories.get(position).getCategory_Places());
            Picasso.with(getActivity())
                    .load(allCategories.get(position).getCategory_Image())
                    .into(category_img);
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
            convertView = inflater.inflate(R.layout.new_home_categories_recycler, parent, false);
            NormalTextView category_name = (NormalTextView) convertView.findViewById(R.id.category_name);
            NormalTextView place_count_txt = (NormalTextView) convertView.findViewById(R.id.place_count_txt);
            ImageView category_img = (ImageView) convertView.findViewById(R.id.category_img);
            isShowLess = false;

            category_name.setText(allCategories1.get(position).getCategory_Name());
            place_count_txt.setText(allCategories1.get(position).getCategory_Places());
            Picasso.with(getActivity()).load(allCategories1.get(position).getCategory_Image()).into(category_img);

            return convertView;
        }
    }
}
/*
    //Setting nearby adapter class
    private class HomePageAdapter extends RecyclerView.Adapter<HomePageAdapter.CategoryViewHolder> {

        public int height;
        public int width;
        ArrayList<Nearby> nearbies = new ArrayList<>();
        Activity activity;
        private String _24HourTime, _24HourTime1;
        private Date _24HourDt, _24HourDt1;
        private SimpleDateFormat _24HourSDF, _12HourSDF;
        private SharedPreferences mPreferences;
        private SharedPreferences.Editor mEditor;

        public HomePageAdapter(Activity activity, ArrayList<Nearby> nearbies) {
            this.nearbies = nearbies;
            this.activity = activity;
        }

        public HomePageAdapter.CategoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.new_home_nearby_recycler, parent, false);

            //setting date format
            _24HourSDF = new SimpleDateFormat("HH:mm");
            _12HourSDF = new SimpleDateFormat("hh:mma");

            // Initializing the shared preference
            mPreferences = activity.getSharedPreferences(Constants.mPref, 0);
            mEditor = mPreferences.edit();

            //getting display metrics
            try {
                DisplayMetrics displaymetrics = new DisplayMetrics();
                activity.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
                height = displaymetrics.heightPixels;
                width = displaymetrics.widthPixels;
            } catch (Exception e) {
                // Tracking exception
                MyTorismaApplication.getInstance().trackException(e);
                e.printStackTrace();
            }

            return new HomePageAdapter.CategoryViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(final HomePageAdapter.CategoryViewHolder holder, final int position) {
            System.out.println("nearby123 " + nearbies.get(position).getCategory_Name());
            String imageURL = Constants.IMAGE_URL + nearbies.get(position).getPlace_MainImage() + "&w=" + (width);
            Picasso.with(activity)
                    .load(imageURL)
                    .resize(width, (height * 60) / 100)
                    .into(holder.nearby_img);
            holder.category_txt.setText(nearbies.get(position).getCategory_Name());
            holder.place_txt.setText(nearbies.get(position).getPlace_Name());
            if (nearbies.get(position).getFree_entry().equals("0")) {
                holder.ticket_txt.setText(Constants.showMessage(activity, mPreferences.getString("Lan_Id", ""), "Check Details"));
//                holder.ticket_txt.setSelected(true);
//                holder.ticket_txt.requestFocus();
            } else if (nearbies.get(position).getFree_entry().equals("1")) {
                holder.ticket_txt.setText(Constants.showMessage(activity, mPreferences.getString("Lan_Id", ""), "Free Entry"));
//                holder.ticket_txt.setSelected(true);
//                holder.ticket_txt.requestFocus();
            }
            holder.dist_txt.setText(nearbies.get(position).getDist());

            if (nearbies.get(position).getFav_Id().equalsIgnoreCase("0")) {
                holder.imgFav.setActivated(false);
            } else {
                holder.imgFav.setActivated(true);
            }

            if (nearbies.get(position).getFav_Id().equalsIgnoreCase("0")) {
                holder.imgFav.setActivated(false);
            } else {
                holder.imgFav.setActivated(true);
            }

            holder.imgFav.setId(position);
            holder.imgFav.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mPreferences.getString("User_Id", "").equalsIgnoreCase("0")) {
                        mainActivity.showGuestSnackToast();
                    } else {
                        for (int j = 0; j < nearbies.size(); j++) {
                            if (v.getId() == j) {
                                if (nearbies.get(j).getFav_Id().equalsIgnoreCase("0")) {
                                    holder.imgFav.setActivated(true);
                                    addFavoriteCall(nearbies.get(j).getPlace_Id(), nearbies.get(j).getGroup_Id());
                                    mFlag = j;
                                } else {
                                    holder.imgFav.setActivated(false);
//                                        deleteFavoriteCall(nearbies.get(j).getFav_Id());
                                    deleteFavoriteCall(nearbies.get(j).getFav_Id(), nearbies.get(j).getGroup_Id());
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
                                    // Tracking exception
                                    MyTorismaApplication.getInstance().trackException(e);
                                    e.printStackTrace();
                                }
                            }
                            if (_24HourTime1 != null && !_24HourTime1.equalsIgnoreCase("NULL")) {
                                try {
                                    _24HourDt1 = _24HourSDF.parse(_24HourTime1);
                                } catch (ParseException e) {
                                    // Tracking exception
                                    MyTorismaApplication.getInstance().trackException(e);
                                    e.printStackTrace();
                                }
                            }
                            if (_24HourDt != null && _24HourDt1 != null) {
                                holder.timing_txt.setText(_24HourSDF.format(_24HourDt) + " " + Constants.showMessage(activity, mPreferences.getString("Lan_Id", ""), "TO") + " " + _24HourSDF.format(_24HourDt1));
                            } else {
                                holder.timing_txt.setText("");
                                dayFoundStatus = 3;
                            }
                        }
                        if (hourDetails.getPOHCharges() != null && !hourDetails.getPOHCharges().equals("") && !hourDetails.getPOHCharges().equalsIgnoreCase("null")) {
                            isTicketSet = true;
                        }
                        break;
                    } else {
                        holder.timing_txt.setText(Constants.showMessage(activity, mPreferences.getString("Lan_Id", ""), "Timing") + ": -");
                        dayFoundStatus = 3;
                    }
                }
            }
            if (dayFoundStatus == 3) {
            } else if (dayFoundStatus == 2) {
                holder.timing_txt.setText(Constants.showMessage(activity, mPreferences.getString("Lan_Id", ""), "Open Now"));
            } else {
                holder.timing_txt.setText(Constants.showMessage(activity, mPreferences.getString("Lan_Id", ""), "Closed"));
            }

            holder.rl_main_img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mainActivity.exploreNearbyFragment.replacePlaceDetailsFragment(nearbies.get(position).getPlace_Id(), tv_city.getText().toString(), nearbies.get(position).getGroup_Id());
                }
            });

            holder.rlBottomView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mainActivity.exploreNearbyFragment.replacePlaceDetailsFragment(nearbies.get(position).getPlace_Id(), tv_city.getText().toString(), nearbies.get(position).getGroup_Id());
                }
            });
        }

        @Override
        public int getItemCount() {
            return nearbies.size();
        }

        public class CategoryViewHolder extends RecyclerView.ViewHolder {

            ImageView nearby_img, imgFav;
            NormalTextView category_txt, place_txt, timing_txt, ticket_txt, dist_txt;
            RelativeLayout rl_main_img, rlBottomView;

            public CategoryViewHolder(View view) {
                super(view);
                nearby_img = (ImageView) view.findViewById(R.id.nearby_img);
                imgFav = (ImageView) view.findViewById(R.id.imgFav);
                category_txt = (NormalTextView) view.findViewById(R.id.nearby_category_txt);
                place_txt = (NormalTextView) view.findViewById(R.id.place_txt);
                timing_txt = (NormalTextView) view.findViewById(R.id.timing_txt);
                ticket_txt = (NormalTextView) view.findViewById(R.id.ticket_txt);
                dist_txt = (NormalTextView) view.findViewById(R.id.dist_txt);
                rl_main_img = (RelativeLayout) view.findViewById(R.id.rl_main_img);
                rlBottomView = (RelativeLayout) view.findViewById(R.id.rlBottomView);
            }
        }
    }*/
