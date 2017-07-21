package com.ftl.tourisma.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ftl.tourisma.MyTorismaApplication;
import com.ftl.tourisma.R;
import com.ftl.tourisma.adapters.NewSelectCityLocationFragmentAdapter;
import com.ftl.tourisma.models.IMAGE_MODEL;
import com.ftl.tourisma.models.NewCities;
import com.ftl.tourisma.postsync.PostSync;
import com.ftl.tourisma.postsync.post_sync;
import com.ftl.tourisma.utils.CommonClass;
import com.ftl.tourisma.utils.Constants;
import com.ftl.tourisma.utils.GPSTracker;
import com.ftl.tourisma.utils.Preference;
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
 * Created by Vinay on 7/4/2017.
 */

public class NewSelectCityLocationFragment extends Fragment implements post_sync.ResponseHandler {

    TextView txtTitle, country_txt, tv_auto_detect, chose_dest_txt;
    ImageView img_close;
    LinearLayout auto_detect_layout;
    RecyclerView location_recycler_view;
    ArrayList<NewCities> newCities = new ArrayList<>();
    ArrayList<IMAGE_MODEL> image_models = new ArrayList<>();
    private View view;
    private SharedPreferences mPreferences;
    private SharedPreferences.Editor mEditor;
    private double latitude, longitude;
    private GPSTracker gpsTracker;
    private String umAddress1, city, zipCode, country, addressmaps, state;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.new_select_location_fragment, container, false);

        mPreferences = getActivity().getSharedPreferences(Constants.mPref, 0);
        mEditor = mPreferences.edit();

        initialization();
        newCitiesCall();
        onClickListners();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        location_recycler_view.setLayoutManager(linearLayoutManager);

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

    public void initialization() {

        txtTitle = (TextView) view.findViewById(R.id.txtTitle);
        if (mPreferences.getString("Lan_Id", "").equals("8")) {
            txtTitle.setText(" : " + Constants.showMessage(getActivity(), mPreferences.getString("Lan_Id", ""), "COUNTRY"));
        } else {
            txtTitle.setText(Constants.showMessage(getActivity(), mPreferences.getString("Lan_Id", ""), "COUNTRY") + " : ");
        }

        country_txt = (TextView) view.findViewById(R.id.country_txt);
        country_txt.setText(Constants.showMessage(getActivity(), mPreferences.getString("Lan_Id", ""), "UAE"));

        tv_auto_detect = (TextView) view.findViewById(R.id.tv_auto_detect);
        tv_auto_detect.setText(Constants.showMessage(getActivity(), mPreferences.getString("Lan_Id", ""), "Are you in UAE? Click here for auto location"));

        chose_dest_txt = (TextView) view.findViewById(R.id.chose_dest_txt);
        chose_dest_txt.setText(Constants.showMessage(getActivity(), mPreferences.getString("Lan_Id", ""), "Or choose your destination"));

        img_close = (ImageView) view.findViewById(R.id.img_close);
        auto_detect_layout = (LinearLayout) view.findViewById(R.id.auto_detect_layout);
        location_recycler_view = (RecyclerView) view.findViewById(R.id.location_recycler_view);
    }

    public void onClickListners() {

        img_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.hideKeyboard(getActivity());
                ((MainActivity) getActivity()).onBackPressed();
            }
        });

        auto_detect_layout.setOnClickListener(new View.OnClickListener() {
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

    private void newCitiesCall() {
        if (CommonClass.hasInternetConnection(getActivity())) {
            String url = Constants.SERVER_URL + "json.php?action=newCmsPlaces";
            String json = "[{\"Lan_Id\":\"" + mPreferences.getString("Lan_Id", "") + "\"}]";
            new PostSync(getActivity(), "newCmsPlaces", NewSelectCityLocationFragment.this).execute(url, json);
        } else {
            Intent intent = new Intent(getActivity(), NoInternet.class);
            startActivity(intent);
        }
    }

    @Override
    public void onResponse(String response, String action) {
        try {
            if (action.equalsIgnoreCase("newCmsPlaces")) {
                newCitiesResponse(response);
            }
        } catch (Exception e) {
            // Tracking exception
            MyTorismaApplication.getInstance().trackException(e);
        }

    }

    public void newCitiesResponse(String resultString) {
        Gson gson = new Gson();
        newCities.clear();
        NewCities newCity = new NewCities();
        if (resultString.length() > 2) {
            try {
                JSONObject jsonObject = new JSONObject(resultString);
                JSONArray location_jsonArray = jsonObject.getJSONArray("Cities");
                for (int i = 0; i < location_jsonArray.length(); i++) {
                    newCity = gson.fromJson(location_jsonArray.get(i).toString(), NewCities.class);
                    newCities.add(newCity);
                }
                location_recycler_view.setAdapter(new NewSelectCityLocationFragmentAdapter(getActivity(), newCities, image_models));
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
        }
    }
}
