package com.ftl.tourisma.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.transition.Fade;
import android.transition.Slide;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.ftl.tourisma.MyTorismaApplication;
import com.ftl.tourisma.R;
import com.ftl.tourisma.adapters.SearchLocationAdapter;
import com.ftl.tourisma.custom_views.NormalEditText;
import com.ftl.tourisma.custom_views.NormalTextView;
import com.ftl.tourisma.models.LocationSearch;
import com.ftl.tourisma.postsync.PostSync;
import com.ftl.tourisma.postsync.post_sync;
import com.ftl.tourisma.utils.CommonClass;
import com.ftl.tourisma.utils.Constants;
import com.ftl.tourisma.utils.GPSTracker;
import com.ftl.tourisma.utils.Preference;
import com.ftl.tourisma.utils.RecyclerItemClickListener;
import com.ftl.tourisma.utils.Utils;
import com.google.gson.Gson;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by vinay on 5/5/2017.
 */

public class SearchLocationFragment extends Fragment implements post_sync.ResponseHandler {

    LocationSearch locationSearch;
    ArrayList<LocationSearch> locationSearches = new ArrayList<>();
    ArrayList<LocationSearch> temp_locationSearches = new ArrayList<>();
    MainActivity mainActivity;
    private View view;
    private SharedPreferences mPreferences;
    private SharedPreferences.Editor mEditor;
    private double latitude, longitude;
    private GPSTracker gpsTracker;
    private NormalTextView tv_select, tv_auto_detect;
    private ImageView iv_close_header2, iv_auto_location;
    private NormalEditText etSearch;
    private RecyclerView search_recycler_view;
    private String umAddress1, city, zipCode, country, addressmaps, state;

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
        view = inflater.inflate(R.layout.fragment_search_location, container, false);
        setupWindowAnimations();
        mPreferences = getActivity().getSharedPreferences(Constants.mPref, 0);
        mEditor = mPreferences.edit();
        initialisation();
        locationCall();
        onClickListners();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        search_recycler_view.setLayoutManager(linearLayoutManager);

        gpsTracker = new GPSTracker(getActivity());
        if (gpsTracker.canGetLocation()) {
            gpsTracker.getLocation();
            latitude = gpsTracker.getLatitude();
            longitude = gpsTracker.getLongitude();
        }

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                getLocationOfAddress(false);
            }
        }, 2);

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

    public void onClickListners() {

        iv_close_header2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.hideKeyboard(getActivity());
                ((MainActivity) getActivity()).onBackPressed();
            }
        });

        search_recycler_view.addOnItemTouchListener(
                new RecyclerItemClickListener(getActivity(), new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        mEditor.putString(Preference.Pref_City, SearchLocationAdapter.locationSearches.get(position).getCityName()).commit();
                        Intent mIntent = new Intent(getActivity(), MainActivity.class);
                        Constants.mStatic = 0;
                        Constants.mFromSelectLocation = 1;
                        mIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(mIntent);
                        getActivity().finish();
                    }
                })
        );

        etSearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().trim().equals("")) {
                    if (s.toString().length() > 0) {
                        temp_locationSearches.clear();
                        for (int i = 0; i < locationSearches.size(); i++) {
                            if (locationSearches.get(i).getFullAddress().toLowerCase().contains(s.toString().toLowerCase()))
                                temp_locationSearches.add(locationSearches.get(i));
                        }
                        SearchLocationAdapter searchLocationAdapter = new SearchLocationAdapter(getActivity(), temp_locationSearches);
                        search_recycler_view.setAdapter(searchLocationAdapter);
                        searchLocationAdapter.notifyDataSetChanged();
                    }
                } else {
                    SearchLocationAdapter searchLocationAdapter = new SearchLocationAdapter(getActivity(), locationSearches);
                    search_recycler_view.setAdapter(searchLocationAdapter);
                    searchLocationAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        tv_auto_detect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        getLocationOfAddress(true);
                        Constants.mFromSelectLocation = 1;
                        Intent mIntent = new Intent(getActivity(), MainActivity.class);
                        mIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(mIntent);
                        getActivity().finish();
                    }
                }, 2);
            }
        });

        iv_auto_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        getLocationOfAddress(true);
                        Constants.mFromSelectLocation = 1;
                        Intent mIntent = new Intent(getActivity(), MainActivity.class);
                        mIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(mIntent);
                        getActivity().finish();
                    }
                }, 2);
            }
        });
    }

    @Override
    public void onResponse(String response, String action) {
        try {
            if (action.equalsIgnoreCase("CmsPlaces")) {
                locationResponse(response);
            }
        } catch (Exception e) {
            // Tracking exception
            MyTorismaApplication.getInstance().trackException(e);
        }
    }

    private void locationCall() {
        if (CommonClass.hasInternetConnection(getActivity())) {
            String url = Constants.SERVER_URL + "json.php?action=CmsPlaces";
//            String url = "http://35.154.205.155/mytourisma/json.php?action=CmsPlaces";
            String json = "";
            new PostSync(getActivity(), "CmsPlaces", SearchLocationFragment.this).execute(url, json);
        } else {
            Intent intent = new Intent(getActivity(), NoInternet.class);
            startActivity(intent);
            //SnackbarManager.show(Snackbar.with(this).color(Utilities.getColor(this, R.color.mBlue)).text("No Internet connection!"));
        }
    }

    public void locationResponse(String resultString) {
        Gson gson = new Gson();
        locationSearches.clear();
        LocationSearch locationSearch = new LocationSearch();
        if (resultString.length() > 2) {
            try {
                JSONObject jsonObject = new JSONObject(resultString);
                JSONArray location_jsonArray = jsonObject.getJSONArray("Address");
                for (int i = 0; i < location_jsonArray.length(); i++) {
                    locationSearch = gson.fromJson(location_jsonArray.get(i).toString(), LocationSearch.class);
                    locationSearches.add(locationSearch);
                }
                search_recycler_view.setAdapter(new SearchLocationAdapter(getActivity(), locationSearches));
            } catch (JSONException e) {
                // Tracking exception
                MyTorismaApplication.getInstance().trackException(e);
                e.printStackTrace();
            }
        }
    }

    public void getLocationOfAddress(final boolean update) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        if (CommonClass.hasInternetConnection(getActivity())) {
            if (latitude != 0 && longitude != 0) {
                try {
                    Geocoder geo = new Geocoder(getActivity().getApplicationContext(), Locale.getDefault());
                    List<Address> addresses = geo.getFromLocation(latitude, longitude, 1);
                    if (addresses.isEmpty()) {
                    } else {
                        if (addresses.size() > 0) {
                            String str = (addresses.get(0).getAddressLine(1) + ", " + addresses.get(0).getLocality() + ", "
                                    + addresses.get(0).getAdminArea() + ", " + addresses.get(0).getCountryName());

                            if (update) {
                                umAddress1 = addresses.get(0).getAddressLine(0);
                                city = addresses.get(0).getLocality();
                                mEditor.putString(Preference.Pref_City, city).commit();
                                state = addresses.get(0).getAdminArea();
                                mEditor.putString(Preference.Pref_State, state).commit();
                                zipCode = addresses.get(0).getPostalCode();
                                country = addresses.get(0).getCountryName();
                                mEditor.putString(Preference.Pref_Country, country).commit();
                            }
                            addressmaps = str.replaceAll(" null,", "");
                            etSearch.setHint(addressmaps);
                        }
                    }

                } catch (Exception e) {
                    // Tracking exception
                    MyTorismaApplication.getInstance().trackException(e);
                    e.printStackTrace();
                    String address = "";
                    String apiRequest = "https://maps.googleapis.com/maps/api/geocode/json?latlng=" + mPreferences.getString("latitude1", "") + "," + mPreferences.getString("longitude1", ""); //+ "&ka&sensor=false"
                    HttpGet httpGet = new HttpGet(apiRequest);
                    HttpClient client = new DefaultHttpClient();
                    HttpResponse response;
                    StringBuilder stringBuilder = new StringBuilder();
                    try {
                        response = client.execute(httpGet);
                        HttpEntity entity = response.getEntity();
                        InputStream stream = entity.getContent();
                        int b;
                        while ((b = stream.read()) != -1) {
                            stringBuilder.append((char) b);
                        }
                    } catch (ClientProtocolException e1) {
                        e1.printStackTrace();
                    } catch (IOException e2) {
                        e2.printStackTrace();
                    }
                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject = new JSONObject(stringBuilder.toString());
                        address = jsonObject.getString("formatted_address");
                    } catch (JSONException e3) {
                        e.printStackTrace();
                    }

                    if (!address.equalsIgnoreCase("")) {
                        etSearch.setText(address);
                    }
                }
            } else {
                if (gpsTracker == null)
                    gpsTracker = new GPSTracker(getActivity());
                if (gpsTracker.canGetLocation()) {
                    longitude = gpsTracker.getLongitude();
                    latitude = gpsTracker.getLatitude();

                    if (update) {
                        mEditor.putString("latitude1", String.valueOf(gpsTracker.getLatitude())).commit();
                        mEditor.putString("longitude1", String.valueOf(gpsTracker.getLongitude())).commit();
                        mEditor.putString("latitude2", String.valueOf(gpsTracker.getLatitude())).commit();
                        mEditor.putString("longitude2", String.valueOf(gpsTracker.getLongitude())).commit();
                    }

                    new Handler().postDelayed(new Runnable() {

                        @Override
                        public void run() {
                            getLocationOfAddress(update);
                        }
                    }, 2);
                }
            }
        } else {
            Intent intent = new Intent(getActivity(), NoInternet.class);
            startActivity(intent);
            //SnackbarManager.show(Snackbar.with(getActivity()).color(Utils.getColor(getActivity(), R.color.mBlue)).text(Constants.showMessage(getActivity(), mPreferences.getString("Lan_Id", ""), "NOINTERNET")));
        }
    }
}
