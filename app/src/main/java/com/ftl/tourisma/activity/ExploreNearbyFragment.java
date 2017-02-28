package com.ftl.tourisma.activity;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import com.daimajia.slider.library.Indicators.PagerIndicator;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.DefaultSliderView;
import com.daimajia.slider.library.Transformers.BaseTransformer;
import com.daimajia.slider.library.Tricks.ViewPagerEx;
import com.ftl.tourisma.FullPlaceImageViewActivity;
import com.ftl.tourisma.MapDetailFragment;
import com.ftl.tourisma.MyTorismaApplication;
import com.ftl.tourisma.R;
import com.ftl.tourisma.ShareFragmentActivity;
import com.ftl.tourisma.SimpleVrPanoramaActivity;
import com.ftl.tourisma.adapters.TimingAdapter;
import com.ftl.tourisma.custom_views.NormalBoldTextView;
import com.ftl.tourisma.custom_views.NormalTextView;
import com.ftl.tourisma.custom_views.ScrollDisabledRecyclerView;
import com.ftl.tourisma.database.AllCategories;
import com.ftl.tourisma.database.Nearby;
import com.ftl.tourisma.minterface.Updatable;
import com.ftl.tourisma.models.HourDetails;
import com.ftl.tourisma.models.WeekDaysModel;
import com.ftl.tourisma.postsync.post_sync;
import com.ftl.tourisma.utils.CommonClass;
import com.ftl.tourisma.utils.Constants;
import com.ftl.tourisma.utils.CustomTypefaceSpan;
import com.ftl.tourisma.utils.ExpandableHeightGridView;
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

import static android.content.Context.LAYOUT_INFLATER_SERVICE;
import static com.ftl.tourisma.utils.Constants.PlaceClosed;
import static com.ftl.tourisma.utils.Constants.PlaceOpenFor24Hours;
import static com.ftl.tourisma.utils.Constants.PlaceOpenWithAnyTime;


public class ExploreNearbyFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = "ExploreNearbyFragment";

    private View view;
    private MainActivity mainActivity;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        replaceFragment();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_new, container, false);

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }


    @Override
    public void onAttach(Context activity) {
        super.onAttach(activity);
        mainActivity = (MainActivity) getActivity();
    }

    @Override
    public void onClick(View view) {
    }

    public void replaceFragment() {

        getActivity().getSupportFragmentManager().beginTransaction().add(R.id.fram1, new HomeFragment()).commit();
    }

    public void replacePlaceDetailsFragment(String placeId, String location) {
//        if()
        Bundle bundle = new Bundle();
        bundle.putString("placeId", placeId);
        bundle.putString("location", location);
        SearchResultPlaceDetailsFragment searchResultPlaceDetailsFragment = new SearchResultPlaceDetailsFragment();
        searchResultPlaceDetailsFragment.setArguments(bundle);
        getActivity().getSupportFragmentManager().beginTransaction().add(R.id.fram1, searchResultPlaceDetailsFragment).addToBackStack(SearchResultPlaceDetailsFragment.class.getSimpleName()).commit();
    }

    public void replaceSearchFragment() {
        getActivity().getSupportFragmentManager().beginTransaction().add(R.id.fram1, new SearchFragment()).addToBackStack(SearchFragment.class.getSimpleName()).commit();

    }

    public void replaceLocationFragment() {
        getActivity().getSupportFragmentManager().beginTransaction().add(R.id.fram1, new SelectLocationFragment()).addToBackStack(SelectLocationFragment.class.getSimpleName()).commit();

    }

    public void replaceSearchResultFragment(ArrayList<Nearby> nearbies1, String search, boolean isForCategory) {
        SearchResultFragmentFragment searchResultFragmentFragment = SearchResultFragmentFragment.NewInstance(nearbies1, search, isForCategory);
        getActivity().getSupportFragmentManager().beginTransaction().add(R.id.fram1, searchResultFragmentFragment, SearchResultFragmentFragment.class.getSimpleName()).addToBackStack(SearchResultFragmentFragment.class.getSimpleName()).commit();
    }
}



