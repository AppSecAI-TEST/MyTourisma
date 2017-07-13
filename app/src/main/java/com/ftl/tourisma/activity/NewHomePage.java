package com.ftl.tourisma.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.daimajia.slider.library.Indicators.PagerIndicator;
import com.daimajia.slider.library.SliderLayout;
import com.ftl.tourisma.R;
import com.ftl.tourisma.custom_views.NormalTextView;

/**
 * Created by Vinay on 7/13/2017.
 */

public class NewHomePage extends Fragment {

    RelativeLayout rl_home;
    NormalTextView tv_your_location_header3, tv_recommended, main_description, description, explore_txt, nearby_txt;
    LinearLayout ll_change_city;
    ImageView iv_search_header3, imgFav;
    SliderLayout slider;
    PagerIndicator custom_indicator;
    RecyclerView categories_rv, nearby_rv;
    View view;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.new_home_fragment, container, false);
        initialization(view);
        return view;
    }

    public void initialization(View view){
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
}
