package com.ftl.tourisma.activity;

import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.transition.Fade;
import android.transition.Slide;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.ftl.tourisma.R;
import com.ftl.tourisma.custom_views.NormalEditText;
import com.ftl.tourisma.custom_views.NormalTextView;
import com.ftl.tourisma.postsync.post_sync;
import com.ftl.tourisma.utils.Constants;
import com.ftl.tourisma.utils.GPSTracker;

/**
 * Created by vinay on 5/5/2017.
 */

public class SearchLocationFragment extends Fragment implements post_sync.ResponseHandler {

    private View view;
    private SharedPreferences mPreferences;
    private SharedPreferences.Editor mEditor;
    private double latitude, longitude;
    private GPSTracker gpsTracker;
    private NormalTextView tv_select, tv_auto_detect;
    private ImageView iv_close_header2, iv_auto_location;
    private NormalEditText etSearch;
    private RecyclerView search_recycler_view;

    private void setupWindowAnimations() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Fade fade = new Fade();
            fade.setDuration(400);
            getActivity().getWindow().setEnterTransition(fade);

            Slide slide = new Slide();
            slide.setDuration(400);
            getActivity().getWindow().setReturnTransition(slide);
        }
    }

    @Nullable
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_select_location, container, false);
        setupWindowAnimations();
        mPreferences = getActivity().getSharedPreferences(Constants.mPref, 0);
        mEditor = mPreferences.edit();
        initialisation();
        gpsTracker = new GPSTracker(getActivity());
        if (gpsTracker.canGetLocation()) {
            gpsTracker.getLocation();
            latitude = gpsTracker.getLatitude();
            longitude = gpsTracker.getLongitude();
        }
        return view;
    }

    private void initialisation() {
        Constants.mStaticFavCall = 0;

        search_recycler_view = (RecyclerView) view.findViewById(R.id.search_recycler_view);

        tv_auto_detect = (NormalTextView) view.findViewById(R.id.tv_auto_detect);
        tv_auto_detect.setText(Constants.showMessage(getActivity(), mPreferences.getString("Lan_Id", ""), "currentlocationtost"));

        tv_select = (NormalTextView) view.findViewById(R.id.txtTitle);
        tv_select.setText(Constants.showMessage(getActivity(), mPreferences.getString("Lan_Id", ""), "selectlocation"));

        iv_close_header2 = (ImageView) view.findViewById(R.id.img_close);
        iv_auto_location = (ImageView) view.findViewById(R.id.iv_auto_location);

        etSearch = (NormalEditText) view.findViewById(R.id.etSearch);
        etSearch.setHint(Constants.showMessage(getActivity(), mPreferences.getString("Lan_Id", ""), "searchlocationtost"));
    }

    @Override
    public void onResponse(String response, String action) {

    }
}
