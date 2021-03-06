package com.ftl.tourisma.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import com.daimajia.slider.library.Indicators.PagerIndicator;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.DefaultSliderView;
import com.daimajia.slider.library.Tricks.ViewPagerEx;
import com.estimote.sdk.repackaged.gson_v2_3_1.com.google.gson.Gson;
import com.ftl.tourisma.MyTorismaApplication;
import com.ftl.tourisma.R;
import com.ftl.tourisma.custom_views.NormalTextView;
import com.ftl.tourisma.database.AllCategories;
import com.ftl.tourisma.database.Nearby;
import com.ftl.tourisma.models.HourDetails;
import com.ftl.tourisma.models.Tags;
import com.ftl.tourisma.postsync.post_sync;
import com.ftl.tourisma.utils.CommonClass;
import com.ftl.tourisma.utils.Constants;
import com.ftl.tourisma.utils.JSONObjConverter;
import com.ftl.tourisma.utils.Preference;
import com.ftl.tourisma.utils.Utilities;
import com.ftl.tourisma.utils.Utils;
import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.SnackbarManager;
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

import static com.ftl.tourisma.utils.Constants.PlaceClosed;
import static com.ftl.tourisma.utils.Constants.PlaceOpenFor24Hours;
import static com.ftl.tourisma.utils.Constants.PlaceOpenWithAnyTime;

/**
 * Created by Vinay on 7/13/2017.
 */

public class NewHomePage extends Fragment implements ViewPagerEx.OnPageChangeListener, post_sync.ResponseHandler {

    private static final String TAG = "HomeFragment";
    private static int mFrame = 0;
    private static int mCounter = -1;
    int mFlag = 0;
    MainActivity mainActivity;
    boolean isShowLess;
    private RelativeLayout rl_home, rl_recommended, rv_ShowMoreLess;
    private ScrollView sv_explorer_location;
    private NormalTextView experiance_txt, txtMessage, txtSuggest, txtOk, tv_city, tv_your_location_header3, tv_recommended, main_description, description, explore_txt, nearby_txt;
    private LinearLayout ll_change_city, llEmptyLayout;
    private ImageView iv_search_header3, imgFav, imgShowMoreLess;
    private SliderLayout slider;
    private PagerIndicator custom_indicator;
    private RecyclerView tags_rv, categories_rv, nearby_rv;
    private int id;
    private int like;
    private String mGroupId, mPlaceId, mFav, Category_Id;
    private Nearby recommended;
    private Nearby nearby;
    private ArrayList<Nearby> recommendeds = new ArrayList<>();
    private ArrayList<AllCategories> allCategories = new ArrayList<>();
    private ArrayList<AllCategories> moreCategories = new ArrayList<>();
    private ArrayList<Nearby> nearbies = new ArrayList<>();
    private ArrayList<Nearby> nearbies_category = new ArrayList<>();
    private ArrayList<Tags> tagses = new ArrayList<>();
    private TagsAdapter tagsAdapter;
    private CategoriesAdapter categoriesAdapter;
    private CategoriesAdapter1 categoriesAdapter1;
    private HomePageAdapter adapter;
    private View view;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mainActivity = (MainActivity) getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.new_home_fragment, container, false);
        initialization(view);
        downloadHomePage();
        onClickListners();

        //setting layout manager for tags
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 2);
        tags_rv.setLayoutManager(gridLayoutManager);

        //setting layoutmanager for categories
        LinearLayoutManager linearLayoutManager_categories = new LinearLayoutManager(getActivity());
        categories_rv.setLayoutManager(linearLayoutManager_categories);

        //setting layoutmanger for nearby places
        LinearLayoutManager linearLayoutManager_nearby = new LinearLayoutManager(getActivity());
        nearby_rv.setLayoutManager(linearLayoutManager_nearby);

        return view;
    }

    public void initialization(View view) {
        sv_explorer_location = (ScrollView) view.findViewById(R.id.sv_explorer_location);
        rl_home = (RelativeLayout) view.findViewById(R.id.rl_home);
        rl_recommended = (RelativeLayout) view.findViewById(R.id.rl_recommended);
        rv_ShowMoreLess = (RelativeLayout) view.findViewById(R.id.rv_ShowMoreLess);
        main_description = (NormalTextView) view.findViewById(R.id.main_description);
        description = (NormalTextView) view.findViewById(R.id.description);
        ll_change_city = (LinearLayout) view.findViewById(R.id.ll_change_city);
        llEmptyLayout = (LinearLayout) view.findViewById(R.id.llEmptyLayout);
        iv_search_header3 = (ImageView) view.findViewById(R.id.iv_search_header3);
        imgFav = (ImageView) view.findViewById(R.id.imgFav);
        slider = (SliderLayout) view.findViewById(R.id.slider);
        custom_indicator = (PagerIndicator) view.findViewById(R.id.custom_indicator);
        tags_rv = (RecyclerView) view.findViewById(R.id.tags_rv);
        categories_rv = (RecyclerView) view.findViewById(R.id.categories_rv);
        nearby_rv = (RecyclerView) view.findViewById(R.id.nearby_rv);

        experiance_txt = (NormalTextView) view.findViewById(R.id.experiance_txt);
        experiance_txt.setText(Constants.showMessage(getActivity(), mainActivity.getPreferences().getString("Lan_Id", ""), "EXPERIENCE"));

        tv_recommended = (NormalTextView) view.findViewById(R.id.tv_recommended);
        tv_recommended.setText(Constants.showMessage(getActivity(), mainActivity.getPreferences().getString("Lan_Id", ""), "recommended"));

        explore_txt = (NormalTextView) view.findViewById(R.id.explore_txt);
        explore_txt.setText(Constants.showMessage(getActivity(), mainActivity.getPreferences().getString("Lan_Id", ""), "explore"));

        nearby_txt = (NormalTextView) view.findViewById(R.id.nearby_txt);
        nearby_txt.setText(Constants.showMessage(getActivity(), mainActivity.getPreferences().getString("Lan_Id", ""), "nearby"));

        tv_your_location_header3 = (NormalTextView) view.findViewById(R.id.tv_your_location_header3);
        tv_your_location_header3.setText(Constants.showMessage(getActivity(), mainActivity.getPreferences().getString("Lan_Id", ""), "locationtitle"));

        imgShowMoreLess = (ImageView) view.findViewById(R.id.imgShowMoreLess);

        tv_city = (NormalTextView) view.findViewById(R.id.tv_city);
        tv_city.setText(mainActivity.getPreferences().getString(Preference.Pref_City, ""));

        txtMessage = (NormalTextView) view.findViewById(R.id.txtMessage);
        txtMessage.setText(Constants.showMessage(getActivity(), mainActivity.getPreferences().getString("Lan_Id", ""), "No records available for this place"));

        txtOk = (NormalTextView) view.findViewById(R.id.txtOk);
        txtOk.setText(Constants.showMessage(getActivity(), mainActivity.getPreferences().getString("Lan_Id", ""), "Change Location"));

        txtSuggest = (NormalTextView) view.findViewById(R.id.txtSuggest);
        txtSuggest.setText(Constants.showMessage(getActivity(), mainActivity.getPreferences().getString("Lan_Id", ""), "Suggest Location"));
    }

    public void onClickListners() {

        iv_search_header3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainActivity.exploreNearbyFragment.replaceSearchFragment();
            }
        });

        txtOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainActivity.exploreNearbyFragment.replaceLocationFragment();
            }
        });

        txtSuggest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                suggestPlace();
            }
        });

        ll_change_city.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainActivity.exploreNearbyFragment.replaceLocationFragment();
            }
        });

        imgShowMoreLess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isShowLess) {
                    categoriesAdapter1 = new CategoriesAdapter1(getActivity(), moreCategories);
                    categories_rv.setAdapter(categoriesAdapter1);
                    categoriesAdapter1.notifyDataSetChanged();
                    imgShowMoreLess.setBackgroundResource(R.drawable.show_less);
                } else {
                    categoriesAdapter = new CategoriesAdapter(getActivity(), allCategories);
                    categories_rv.setAdapter(categoriesAdapter);
                    categoriesAdapter.notifyDataSetChanged();
                    imgShowMoreLess.setBackgroundResource(R.drawable.show_more);
                    sv_explorer_location.scrollTo(0, 0);

                }
            }
        });

        rv_ShowMoreLess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isShowLess) {
                    categoriesAdapter1 = new CategoriesAdapter1(getActivity(), moreCategories);
                    categories_rv.setAdapter(categoriesAdapter1);
                    categoriesAdapter1.notifyDataSetChanged();
                    imgShowMoreLess.setBackgroundResource(R.drawable.show_less);
                } else {
                    categoriesAdapter = new CategoriesAdapter(getActivity(), allCategories);
                    categories_rv.setAdapter(categoriesAdapter);
                    categoriesAdapter.notifyDataSetChanged();
                    imgShowMoreLess.setBackgroundResource(R.drawable.show_more);
                    sv_explorer_location.scrollTo(5, 5);

                }
            }
        });
    }

    public void downloadHomePage() {
        if (CommonClass.hasInternetConnection(getActivity())) {
            String url = Constants.SERVER_URL + "json.php?action=newHomePage";
            String json = "[{\"Lan_Id\":\"" + mainActivity.getPreferences().getString("Lan_Id", "") + "\",\"User_Id\":\"" + mainActivity.getPreferences().getString("User_Id", "") + "\",\"Current_Latitude\":\"" + mainActivity.getPreferences().getString("latitude2", "") + "\",\"Current_Longitude\":\"" + mainActivity.getPreferences().getString("longitude2", "") + "\",\"City_Name\":\"" + mainActivity.getPreferences().getString(Preference.Pref_City, "") + "\",\"deviceType\":\"" + "Android" + "\",\"deviceId\":\"" + Prefs.getString(Constants.fcm_regid, "") + "\"}]";
            System.out.println("homepagedata_json " + json);
            System.out.println("homepagedata_url " + url);
            new post_sync(getActivity(), "newHomePage", NewHomePage.this, true).execute(url, json);
        } else {
            Intent intent = new Intent(getActivity(), NoInternet.class);
            startActivity(intent);
        }
    }

    @Override
    public void onResponse(String response, String action) {
        try {
            if (action.equalsIgnoreCase("newHomePage")) {
                getHomePageDataResponse(response);
            } else if (action.equalsIgnoreCase("AddFavorite")) {
                addFavoriteResponse(response);
            } else if (action.equalsIgnoreCase("DeleteFavorite")) {
                deleteFavoriteResponse(response);
            } else if (action.equalsIgnoreCase("PlaceByCategory")) {
                PlaceByCategoryResponse(response);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getHomePageDataResponse(final String resultString) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                JSONObjConverter jonObjConverter = new JSONObjConverter();
                Gson gson = new Gson();
                recommendeds.clear();
                tagses.clear();
                allCategories.clear();
                moreCategories.clear();
                nearbies.clear();

                Log.d("Explore", "newHomePageResponseFromServer " + resultString);

                if (resultString.length() > 2) {
                    try {
                        //Adding recommnded to array list
                        JSONObject jsonObject = new JSONObject(resultString);
                        JSONArray recommnded_jsonArray = jsonObject.getJSONArray("recommnded");
                        for (int i = 0; i < recommnded_jsonArray.length(); i++) {
                            recommended = jonObjConverter.convertJsonToNearByObj(recommnded_jsonArray.getJSONObject(i));
                            recommendeds.add(recommended);
                        }

                        //Adding tags to array list
                        JSONArray tags_jsonArray = jsonObject.getJSONArray("tags");
                        for (int i = 0; i < tags_jsonArray.length(); i++) {
                            JSONObject tag_jsonObject = tags_jsonArray.getJSONObject(i);
                            tagses.add(gson.fromJson(tags_jsonArray.get(i).toString(), Tags.class));
                        }

                        //Adding categories to array list
                        JSONArray categories_jsonArray = jsonObject.getJSONArray("category");
                        for (int i = 0; i < categories_jsonArray.length(); i++) {
                            JSONObject cat_jsonObject = categories_jsonArray.getJSONObject(i);
                            if (!cat_jsonObject.getString("Category_Places").equals("0")) {
                                if (moreCategories.size() <= 3) {
                                    allCategories.add(gson.fromJson(categories_jsonArray.get(i).toString(), AllCategories.class));
                                }
                                moreCategories.add(gson.fromJson(categories_jsonArray.get(i).toString(), AllCategories.class));
                            }
                        }

                        //Adding nearby to array list
                        JSONArray nearby_jsonArray = jsonObject.getJSONArray("nearby");
                        for (int i = 0; i < nearby_jsonArray.length(); i++) {
                            nearby = jonObjConverter.convertJsonToNearByObj(nearby_jsonArray.getJSONObject(i));
                            nearbies.add(nearby);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                //setting slider
                setSlider();
                sv_explorer_location.setVisibility(View.VISIBLE);


                //setting tags adapter
                tagsAdapter = new TagsAdapter(getActivity(), tagses);
                tags_rv.setAdapter(tagsAdapter);

                //setting categories adapter
                categoriesAdapter = new CategoriesAdapter(getActivity(), allCategories);
                categories_rv.setAdapter(categoriesAdapter);

                if (moreCategories.size() <= 3) {
                    rv_ShowMoreLess.setVisibility(View.GONE);
                } else {
                    rv_ShowMoreLess.setVisibility(View.VISIBLE);
                }

                //setting near by adapter
                adapter = new HomePageAdapter(getActivity(), nearbies);
                nearby_rv.setAdapter(adapter);
            }
        });
    }

    public void setSlider() {
        if (recommendeds.size() > 0) {
            rl_recommended.setVisibility(View.VISIBLE);
            slider = (SliderLayout) view.findViewById(R.id.slider);
            slider.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (mainActivity.height * 50) / 100));
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
                                id = slider.getCurrentPosition();
                                mainActivity.exploreNearbyFragment.replacePlaceDetailsFragment(recommendeds.get(id).getPlace_Id(), tv_city.getText().toString(), recommendeds.get(id).getGroup_Id());
                            }
                        }).setPicasso(picasso);
                slider.addSlider(textSliderView);
            }
            slider.setCustomIndicator(custom_indicator);
            slider.addOnPageChangeListener(this);
        } else {
            rl_recommended.setVisibility(View.GONE);
        }
        if (recommendeds.size() == 0) {
            llEmptyLayout.setVisibility(View.VISIBLE);
        } else {

        }
    }

    //Slider library overrided methods
    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(final int position) {
        try {
            if (recommendeds != null && recommendeds.size() > 0 && recommendeds.size() > position) {
                main_description.setText(recommendeds.get(position).getPlace_Name());
                description.setText(recommendeds.get(position).getCategory_Name());
                Animation animFadeIn = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_in);
                main_description.setAnimation(animFadeIn);
                description.setAnimation(animFadeIn);
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
            mPlaceId = Place_Id;
            String json = "[{\"User_Id\":\"" + mainActivity.getPreferences().getString("User_Id", "") + "\",\"Place_Id\":\"" + Place_Id + "\",\"Group_Id\":\"" + group_id + "\"}]";
            new post_sync(getActivity(), "AddFavorite", NewHomePage.this, true).execute(url, json);
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

    private void deleteFavoriteCall(String Fav_Id, String group_id) {
        if (CommonClass.hasInternetConnection(getActivity())) {
            String url = Constants.SERVER_URL + "json.php?action=DeleteFavorite";
            String json = "[{\"User_Id\":\"" + mainActivity.getPreferences().getString("User_Id", "") + "\",\"Group_Id\":\"" + group_id + "\"}]";
            mFav = Fav_Id;
            new post_sync(getActivity(), "DeleteFavorite", NewHomePage.this, true).execute(url, json);
        } else {
            Intent intent = new Intent(getActivity(), NoInternet.class);
            startActivity(intent);
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

    private void downloadPlaceByCategory() {
        if (CommonClass.hasInternetConnection(getActivity())) {
            String url = Constants.SERVER_URL + "json.php?action=PlaceByCategory";
            String json = "[{\"Lan_Id\":\"" + mainActivity.getPreferences().getString("Lan_Id", "") + "\",\"User_Id\":\"" + mainActivity.getPreferences().getString("User_Id", "") + "\",\"Current_Latitude\":\"" + mainActivity.getPreferences().getString("latitude2", "") + "\",\"Current_Longitude\":\"" + mainActivity.getPreferences().getString("longitude2", "") + "\",\"Category_Id\":\"" + Category_Id + "\",\"keyword\":\"" + mainActivity.getPreferences().getString(Preference.Pref_City, "") + "\"}]";
            new post_sync(getActivity(), "PlaceByCategory", NewHomePage.this, true).execute(url, json);
        } else {
            Intent intent = new Intent(getActivity(), NoInternet.class);
            startActivity(intent);
        }
    }

    public void PlaceByCategoryResponse(String resultString) {
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

    //Setting nearby adapter class
    class HomePageAdapter extends RecyclerView.Adapter<HomePageAdapter.ViewHolder> {

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

        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.new_home_nearby_recycler, parent, false);

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

            ViewHolder vh = new ViewHolder(v);
            return vh;
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            String imageURL = Constants.IMAGE_URL + nearbies.get(position).getPlace_MainImage() + "&w=" + (width);
            Picasso.with(activity)
                    .load(imageURL)
                    .resize(width, (height * 60) / 100)
                    .into(holder.nearby_img);
            holder.category_txt.setText(nearbies.get(position).getCategory_Name());
            holder.category_txt.setSelected(true);
            holder.category_txt.requestFocus();
            holder.place_txt.setText(nearbies.get(position).getPlace_Name());
            if (nearbies.get(position).getFree_entry().equals("0")) {
                holder.ticket_txt.setText(Constants.showMessage(activity, mPreferences.getString("Lan_Id", ""), "Check Details"));
                holder.ticket_txt.setSelected(true);
                holder.ticket_txt.requestFocus();
            } else if (nearbies.get(position).getFree_entry().equals("1")) {
                holder.ticket_txt.setText(Constants.showMessage(activity, mPreferences.getString("Lan_Id", ""), "Free Entry"));
                holder.ticket_txt.setSelected(true);
                holder.ticket_txt.requestFocus();
            }
            holder.dist_txt.setText(nearbies.get(position).getDist() + " " + Constants.showMessage(getActivity(), mPreferences.getString("Lan_Id", ""), "KM"));

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
//                                holder.timing_txt.setText(_24HourSDF.format(_24HourDt) + " " + Constants.showMessage(activity, mPreferences.getString("Lan_Id", ""), "TO") + " " + _24HourSDF.format(_24HourDt1));
                                holder.timing_txt.setText(Constants.showMessage(activity, mPreferences.getString("Lan_Id", ""), "Open Now"));
                                holder.timing_txt.setSelected(true);
                                holder.timing_txt.requestFocus();
                            } else {
                                holder.timing_txt.setText("");
                                holder.timing_txt.setSelected(true);
                                holder.timing_txt.requestFocus();
                                dayFoundStatus = 3;
                            }
                        }
                        if (hourDetails.getPOHCharges() != null && !hourDetails.getPOHCharges().equals("") && !hourDetails.getPOHCharges().equalsIgnoreCase("null")) {
                            isTicketSet = true;
                        }
                        break;
                    } else {
                        holder.timing_txt.setText(Constants.showMessage(activity, mPreferences.getString("Lan_Id", ""), "Timing") + ": -");
                        holder.timing_txt.setSelected(true);
                        holder.timing_txt.requestFocus();
                        dayFoundStatus = 3;
                    }
                }
            }
            if (dayFoundStatus == 3) {
            } else if (dayFoundStatus == 2) {
                holder.timing_txt.setText(Constants.showMessage(activity, mPreferences.getString("Lan_Id", ""), "Open Now"));
                holder.timing_txt.setSelected(true);
                holder.timing_txt.requestFocus();
            } else {
                holder.timing_txt.setText(Constants.showMessage(activity, mPreferences.getString("Lan_Id", ""), "Closed"));
                holder.timing_txt.setSelected(true);
                holder.timing_txt.requestFocus();
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

        public class ViewHolder extends RecyclerView.ViewHolder {

            ImageView nearby_img, imgFav;
            NormalTextView category_txt, place_txt, timing_txt, ticket_txt, dist_txt;
            RelativeLayout rl_main_img, rlBottomView;

            public ViewHolder(View view) {
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
    }

    //Setting tags adapter
    class TagsAdapter extends RecyclerView.Adapter<TagsAdapter.ViewHolder> {

        ArrayList<Tags> tagses = new ArrayList<>();
        Activity activity;

        public TagsAdapter(Activity activity, ArrayList<Tags> tagses) {
            this.tagses = tagses;
            this.activity = activity;
        }

        @Override
        public TagsAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.new_home_tags_recycler, viewGroup, false);
            return new TagsAdapter.ViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(final TagsAdapter.ViewHolder viewHolder, final int position) {
            viewHolder.tag_name_txt.setText(tagses.get(position).getTag_Name());
            if (tagses.get(position).getTag_Places_Count().equals("0") || tagses.get(position).getTag_Places_Count().equals("1")) {
                viewHolder.place_count_txt_tags.setText(tagses.get(position).getTag_Places_Count() + " " + Constants.showMessage(getActivity(), mainActivity.getPreferences().getString("Lan_Id", ""), "place"));
            } else {
                viewHolder.place_count_txt_tags.setText(tagses.get(position).getTag_Places_Count() + " " + Constants.showMessage(getActivity(), mainActivity.getPreferences().getString("Lan_Id", ""), "places"));
            }
            Picasso.with(activity)
                    .load(tagses.get(position).getTag_Image())
                    .into(viewHolder.tags_img);
            viewHolder.rl_tags.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mainActivity.exploreNearbyFragment.replaceTagsListFragment(tagses.get(position).getTag_Id(), tagses.get(position).getTag_Name());
                }
            });
        }

        @Override
        public int getItemCount() {
            return tagses.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            NormalTextView tag_name_txt, place_count_txt_tags;
            ImageView tags_img;
            RelativeLayout rl_tags;

            public ViewHolder(View v) {
                super(v);
                tag_name_txt = (NormalTextView) v.findViewById(R.id.tag_name_txt);
                place_count_txt_tags = (NormalTextView) v.findViewById(R.id.place_count_txt_tags);
                tags_img = (ImageView) v.findViewById(R.id.tags_img);
                rl_tags = (RelativeLayout) v.findViewById(R.id.rl_tags);
            }
        }
    }

    //Setting categories adapter for alf arraylist
    class CategoriesAdapter extends RecyclerView.Adapter<CategoriesAdapter.CategoryViewHolder> {

        ArrayList<AllCategories> allCategories = new ArrayList<>();
        Activity activity;

        public CategoriesAdapter(Activity activity, ArrayList<AllCategories> allCategories) {
            this.allCategories = allCategories;
            this.activity = activity;
        }

        @Override
        public CategoriesAdapter.CategoryViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            isShowLess = true;
            View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.new_home_categories_recycler, viewGroup, false);
            return new CategoriesAdapter.CategoryViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(final CategoriesAdapter.CategoryViewHolder CategoryViewHolder, final int position) {

            CategoryViewHolder.category_name.setText(allCategories.get(position).getCategory_Name());

            if (allCategories.get(position).getCategory_Places().equals("0") || allCategories.get(position).getCategory_Places().equals("1")) {
                CategoryViewHolder.place_count_txt.setText(allCategories.get(position).getCategory_Places() + " " + Constants.showMessage(getActivity(), mainActivity.getPreferences().getString("Lan_Id", ""), "place"));
            } else {
                CategoryViewHolder.place_count_txt.setText(allCategories.get(position).getCategory_Places() + " " + Constants.showMessage(getActivity(), mainActivity.getPreferences().getString("Lan_Id", ""), "places"));
            }

            Picasso.with(activity)
                    .load(allCategories.get(position).getCategory_Image())
                    .into(CategoryViewHolder.category_img);

            CategoryViewHolder.rl_categories.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Category_Id = allCategories.get(position).getCategory_Id();
                    downloadPlaceByCategory();
                }
            });
        }

        @Override
        public int getItemCount() {
            return allCategories.size();
        }

        public class CategoryViewHolder extends RecyclerView.ViewHolder {

            NormalTextView category_name, place_count_txt;
            ImageView category_img;
            RelativeLayout rl_categories;

            public CategoryViewHolder(View v) {
                super(v);
                category_name = (NormalTextView) v.findViewById(R.id.category_name);
                place_count_txt = (NormalTextView) v.findViewById(R.id.place_count_txt);
                category_img = (ImageView) v.findViewById(R.id.category_img);
                rl_categories = (RelativeLayout) v.findViewById(R.id.rl_categories);
            }
        }
    }


    //Setting categories adapter for full arraylist
    class CategoriesAdapter1 extends RecyclerView.Adapter<CategoriesAdapter1.CategoryViewHolder> {

        ArrayList<AllCategories> moreCategories = new ArrayList<>();
        Activity activity;

        public CategoriesAdapter1(Activity activity, ArrayList<AllCategories> moreCategories) {
            this.moreCategories = moreCategories;
            this.activity = activity;
        }

        @Override
        public CategoriesAdapter1.CategoryViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            isShowLess = false;
            View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.new_home_categories_recycler, viewGroup, false);
            return new CategoriesAdapter1.CategoryViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(final CategoriesAdapter1.CategoryViewHolder CategoryViewHolder, final int position) {

            CategoryViewHolder.category_name.setText(moreCategories.get(position).getCategory_Name());

            if (moreCategories.get(position).getCategory_Places().equals("0") || moreCategories.get(position).getCategory_Places().equals("1")) {
                CategoryViewHolder.place_count_txt.setText(moreCategories.get(position).getCategory_Places() + " " + Constants.showMessage(getActivity(), mainActivity.getPreferences().getString("Lan_Id", ""), "place"));
            } else {
                CategoryViewHolder.place_count_txt.setText(moreCategories.get(position).getCategory_Places() + " " + Constants.showMessage(getActivity(), mainActivity.getPreferences().getString("Lan_Id", ""), "places"));
            }

            Picasso.with(activity)
                    .load(moreCategories.get(position).getCategory_Image())
                    .into(CategoryViewHolder.category_img);

            CategoryViewHolder.rl_categories.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Category_Id = moreCategories.get(position).getCategory_Id();
                    downloadPlaceByCategory();
                }
            });
        }

        @Override
        public int getItemCount() {
            return moreCategories.size();
        }

        public class CategoryViewHolder extends RecyclerView.ViewHolder {

            NormalTextView category_name, place_count_txt;
            ImageView category_img;
            RelativeLayout rl_categories;

            public CategoryViewHolder(View v) {
                super(v);
                category_name = (NormalTextView) v.findViewById(R.id.category_name);
                place_count_txt = (NormalTextView) v.findViewById(R.id.place_count_txt);
                category_img = (ImageView) v.findViewById(R.id.category_img);
                rl_categories = (RelativeLayout) v.findViewById(R.id.rl_categories);
            }
        }
    }
}
