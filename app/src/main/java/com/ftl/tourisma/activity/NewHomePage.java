package com.ftl.tourisma.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.daimajia.slider.library.Indicators.PagerIndicator;
import com.daimajia.slider.library.SliderLayout;
import com.estimote.sdk.repackaged.gson_v2_3_1.com.google.gson.Gson;
import com.ftl.tourisma.R;
import com.ftl.tourisma.custom_views.NormalTextView;
import com.ftl.tourisma.database.AllCategories;
import com.ftl.tourisma.postsync.post_sync;
import com.ftl.tourisma.utils.CommonClass;
import com.ftl.tourisma.utils.Constants;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

/**
 * Created by Vinay on 7/13/2017.
 */

public class NewHomePage extends Fragment implements post_sync.ResponseHandler {

    RelativeLayout rl_home;
    NormalTextView tv_your_location_header3, tv_recommended, main_description, description, explore_txt, nearby_txt;
    LinearLayout ll_change_city;
    ImageView iv_search_header3, imgFav;
    SliderLayout slider;
    PagerIndicator custom_indicator;
    RecyclerView categories_rv, nearby_rv;
    ArrayList<AllCategories> allCategories = new ArrayList<>();

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
        downloadCategories();

        //setting layoutmanager for categories
        LinearLayoutManager linearLayoutManager_categories = new LinearLayoutManager(getActivity());
        categories_rv.setLayoutManager(linearLayoutManager_categories);

        //setting layoutmanger for nearby places
        LinearLayoutManager linearLayoutManager_nearby = new LinearLayoutManager(getActivity());
        nearby_rv.setLayoutManager(linearLayoutManager_nearby);

        return view;
    }


    public void initialization(View view) {
        rl_home = (RelativeLayout) view.findViewById(R.id.rl_home);
        tv_your_location_header3 = (NormalTextView) view.findViewById(R.id.tv_your_location_header3);
        tv_recommended = (NormalTextView) view.findViewById(R.id.tv_recommended);
        main_description = (NormalTextView) view.findViewById(R.id.main_description);
        description = (NormalTextView) view.findViewById(R.id.description);
        explore_txt = (NormalTextView) view.findViewById(R.id.explore_txt);
        nearby_txt = (NormalTextView) view.findViewById(R.id.nearby_txt);
        ll_change_city = (LinearLayout) view.findViewById(R.id.ll_change_city);
        iv_search_header3 = (ImageView) view.findViewById(R.id.iv_search_header3);
        imgFav = (ImageView) view.findViewById(R.id.imgFav);
        slider = (SliderLayout) view.findViewById(R.id.slider);
        custom_indicator = (PagerIndicator) view.findViewById(R.id.custom_indicator);
        categories_rv = (RecyclerView) view.findViewById(R.id.categories_rv);
        nearby_rv = (RecyclerView) view.findViewById(R.id.nearby_rv);
    }

    public void downloadCategories() {
        if (CommonClass.hasInternetConnection(getActivity())) {
            String url = Constants.SERVER_URL + "json.php?action=GetCategoriesByLanId";
            String json = "[{\"Lan_Id\":\"" + mainActivity.getPreferences().getString("Lan_Id", "") + "\"}]";
            new post_sync(getActivity(), "GetCategoriesByLanId", NewHomePage.this, true).execute(url, json);
        } else {
            Intent intent = new Intent(getActivity(), NoInternet.class);
            startActivity(intent);
        }
    }

    @Override
    public void onResponse(String response, String action) {
        try {
            if (action.equalsIgnoreCase("GetCategoriesByLanId")) {
                getAllCategoriesResponse(response);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getAllCategoriesResponse(String resultString) {
        Gson gson = new Gson();
        allCategories.clear();
        try {
            JSONArray categories_jsonArray = new JSONArray(resultString);
            for (int i = 0; i < categories_jsonArray.length(); i++) {
                allCategories.add(gson.fromJson(categories_jsonArray.get(i).toString(), AllCategories.class));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}
