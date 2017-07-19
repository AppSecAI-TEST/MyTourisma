package com.ftl.tourisma.activity;

import android.app.Activity;
import android.content.Intent;
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
import com.ftl.tourisma.adapters.CategoriesAdapter;
import com.ftl.tourisma.adapters.HomePageAdapter;
import com.ftl.tourisma.custom_views.NormalTextView;
import com.ftl.tourisma.database.AllCategories;
import com.ftl.tourisma.database.Nearby;
import com.ftl.tourisma.postsync.post_sync;
import com.ftl.tourisma.utils.CommonClass;
import com.ftl.tourisma.utils.Constants;
import com.ftl.tourisma.utils.JSONObjConverter;
import com.ftl.tourisma.utils.Preference;
import com.ftl.tourisma.utils.RecyclerItemClickListener;
import com.ftl.tourisma.utils.Utils;
import com.pixplicity.easyprefs.library.Prefs;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.ftl.tourisma.ResolverActivity.TAG;

/**
 * Created by Vinay on 7/13/2017.
 */

public class NewHomePage extends Fragment implements ViewPagerEx.OnPageChangeListener, post_sync.ResponseHandler {

    RelativeLayout rl_home, rl_recommended;
    ScrollView sv_explorer_location;
    NormalTextView txtMessage, txtSuggest, txtOk, tv_city, tv_your_location_header3, tv_recommended, main_description, description, explore_txt, nearby_txt;
    LinearLayout ll_change_city, llEmptyLayout;
    ImageView iv_search_header3, imgFav;
    SliderLayout slider;
    PagerIndicator custom_indicator;
    RecyclerView categories_rv, nearby_rv;
    int id;
    Nearby recommended;
    Nearby nearby;
    ArrayList<Nearby> recommendeds = new ArrayList<>();
    ArrayList<AllCategories> allCategories = new ArrayList<>();
    ArrayList<Nearby> nearbies = new ArrayList<>();
    CategoriesAdapter categoriesAdapter;
    HomePageAdapter adapter;
    View view;
    MainActivity mainActivity;

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
        onClickListners();
        downloadHomePage();

        //setting layoutmanager for categories
        org.solovyev.android.views.llm.LinearLayoutManager linearLayoutManager_categories = new org.solovyev.android.views.llm.LinearLayoutManager(getActivity());
        categories_rv.setLayoutManager(linearLayoutManager_categories);

        //setting layoutmanger for nearby places
        org.solovyev.android.views.llm.LinearLayoutManager linearLayoutManager_nearby = new org.solovyev.android.views.llm.LinearLayoutManager(getActivity());
        nearby_rv.setLayoutManager(linearLayoutManager_nearby);

        return view;
    }


    public void initialization(View view) {
        sv_explorer_location = (ScrollView) view.findViewById(R.id.sv_explorer_location);
        rl_home = (RelativeLayout) view.findViewById(R.id.rl_home);
        rl_recommended = (RelativeLayout) view.findViewById(R.id.rl_recommended);
        tv_your_location_header3 = (NormalTextView) view.findViewById(R.id.tv_your_location_header3);
        tv_recommended = (NormalTextView) view.findViewById(R.id.tv_recommended);
        main_description = (NormalTextView) view.findViewById(R.id.main_description);
        description = (NormalTextView) view.findViewById(R.id.description);
        explore_txt = (NormalTextView) view.findViewById(R.id.explore_txt);
        nearby_txt = (NormalTextView) view.findViewById(R.id.nearby_txt);

        tv_city = (NormalTextView) view.findViewById(R.id.tv_city);
        tv_city.setText(mainActivity.getPreferences().getString(Preference.Pref_City, ""));

        txtMessage = (NormalTextView) view.findViewById(R.id.txtMessage);
        txtMessage.setText(Constants.showMessage(getActivity(), mainActivity.getPreferences().getString("Lan_Id", ""), "No records available for this place"));

        txtOk = (NormalTextView) view.findViewById(R.id.txtOk);
        txtOk.setText(Constants.showMessage(getActivity(), mainActivity.getPreferences().getString("Lan_Id", ""), "Change Location"));

        txtSuggest = (NormalTextView) view.findViewById(R.id.txtSuggest);
        txtSuggest.setText(Constants.showMessage(getActivity(), mainActivity.getPreferences().getString("Lan_Id", ""), "Suggest Location"));

        ll_change_city = (LinearLayout) view.findViewById(R.id.ll_change_city);
        llEmptyLayout = (LinearLayout) view.findViewById(R.id.llEmptyLayout);
        iv_search_header3 = (ImageView) view.findViewById(R.id.iv_search_header3);
        imgFav = (ImageView) view.findViewById(R.id.imgFav);
        slider = (SliderLayout) view.findViewById(R.id.slider);
        custom_indicator = (PagerIndicator) view.findViewById(R.id.custom_indicator);
        categories_rv = (RecyclerView) view.findViewById(R.id.categories_rv);
        nearby_rv = (RecyclerView) view.findViewById(R.id.nearby_rv);
    }

    public void onClickListners() {

        nearby_rv.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        mainActivity.exploreNearbyFragment.replacePlaceDetailsFragment(HomePageAdapter.nearbies.get(position).getPlace_Id(), tv_city.getText().toString(), HomePageAdapter.nearbies.get(position).getGroup_Id());
                    }
                })
        );

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
            } else if (action.equalsIgnoreCase("newNearBy")) {
                getNearByResponse(response);
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
                allCategories.clear();
                nearbies.clear();

                try {
                    //Adding recommnded to array list
                    JSONObject jsonObject = new JSONObject(resultString);
                    JSONArray recommnded_jsonArray = jsonObject.optJSONArray("recommnded");
                    for (int i = 0; i < recommnded_jsonArray.length(); i++) {
                        recommended = jonObjConverter.convertJsonToNearByObj(recommnded_jsonArray.optJSONObject(i));
                        recommendeds.add(recommended);
                    }

                    //Adding categories to array list
                    JSONArray categories_jsonArray = jsonObject.getJSONArray("category");
                    for (int i = 0; i < categories_jsonArray.length(); i++) {
                        allCategories.add(gson.fromJson(categories_jsonArray.get(i).toString(), AllCategories.class));
                    }
                    categoriesAdapter = new CategoriesAdapter(getActivity(), allCategories);
                    categories_rv.setAdapter(categoriesAdapter);

                    //Adding nearby to array list
                    JSONArray nearby_jsonArray = jsonObject.optJSONArray("nearby");
                    for (int i = 0; i < nearby_jsonArray.length(); i++) {
                        nearby = jonObjConverter.convertJsonToNearByObj(nearby_jsonArray.optJSONObject(i));
                        nearbies.add(nearby);
                    }
                    adapter = new HomePageAdapter(getActivity(), nearbies);
                    nearby_rv.setAdapter(adapter);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                //Setting slider
                setSlider();
                sv_explorer_location.setVisibility(View.VISIBLE);
            }
        });
    }

    public void setSlider() {
        if (recommendeds.size() > 0) {
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
//            downloadNearBy();
        }
    }

    public void downloadNearBy() {
        if (CommonClass.hasInternetConnection(getActivity())) {
            String url = Constants.SERVER_URL + "json.php?action=getNearBy";
            String json = "[{\"Lan_Id\":\"" + mainActivity.getPreferences().getString("Lan_Id", "") + "\",\"User_Id\":\"" + mainActivity.getPreferences().getString("User_Id", "") + "\",\"Current_Latitude\":\"" + mainActivity.getPreferences().getString("latitude2", "") + "\",\"Current_Longitude\":\"" + mainActivity.getPreferences().getString("longitude2", "") + "\",\"City_Name\":\"" + mainActivity.getPreferences().getString(Preference.Pref_City, "") + "\",\"deviceType\":\"" + "Android" + "\",\"deviceId\":\"" + Prefs.getString(Constants.fcm_regid, "") + "\"}]";
            new post_sync(getActivity(), "newNearBy", NewHomePage.this, true).execute(url, json);
        } else {
            Intent intent = new Intent(getActivity(), NoInternet.class);
            startActivity(intent);
        }
    }

    public void getNearByResponse(String resultString) {
        JSONObjConverter jonObjConverter = new JSONObjConverter();
        nearbies.clear();

        //Adding nearby to array list
        try {
            JSONObject jsonObject = new JSONObject(resultString);
            JSONArray nearby_jsonArray = jsonObject.optJSONArray("nearby");
            for (int i = 0; i < nearby_jsonArray.length(); i++) {
                nearby = jonObjConverter.convertJsonToNearByObj(nearby_jsonArray.optJSONObject(i));
                nearbies.add(nearby);
            }
            //Setting nearby Adapter
            adapter = new HomePageAdapter(getActivity(), nearbies);
            nearby_rv.setAdapter(adapter);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //Slider library overrided methods
    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        try {
            if (recommendeds != null && recommendeds.size() > 0 && recommendeds.size() > position) {
                main_description.setText(recommendeds.get(position).getPlace_Name());
                description.setText(recommendeds.get(position).getCategory_Name());
                Animation animFadeIn = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_in);
                main_description.setAnimation(animFadeIn);
                description.setAnimation(animFadeIn);
         /*       imgFav.setActivated(!recommendeds.get(position).getFav_Id().equalsIgnoreCase("0"));
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
                });*/
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
}
