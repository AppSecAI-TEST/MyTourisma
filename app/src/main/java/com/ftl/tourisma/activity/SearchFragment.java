package com.ftl.tourisma.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.ftl.tourisma.R;
import com.ftl.tourisma.SearchResultPlaceDetailsActivity;
import com.ftl.tourisma.custom_views.NormalEditText;
import com.ftl.tourisma.custom_views.NormalTextView;
import com.ftl.tourisma.database.FeesDetails;
import com.ftl.tourisma.database.Nearby;
import com.ftl.tourisma.models.HourDetails;
import com.ftl.tourisma.models.SearchPlaces;
import com.ftl.tourisma.postsync.PostSync;
import com.ftl.tourisma.postsync.post_sync;
import com.ftl.tourisma.utils.CommonClass;
import com.ftl.tourisma.utils.Constants;
import com.ftl.tourisma.utils.GPSTracker;
import com.ftl.tourisma.utils.Preference;
import com.ftl.tourisma.utils.Utilities;
import com.ftl.tourisma.utils.Utils;
import com.google.android.gms.maps.model.LatLng;
import com.squareup.picasso.Picasso;

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
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static com.ftl.tourisma.beacons.MyBeaconsService.BEACON_EXITED;
import static com.ftl.tourisma.beacons.MyBeaconsService.PLACE_ID;

/**
 * Created by fipl11111 on 25-Feb-16.
 */
public class SearchFragment extends Fragment implements OnClickListener, post_sync.ResponseHandler {

    public static final int PLACE_DETAILS_FRAGMENT = 10;
    private static final String TAG = "SearchFragment";
    private static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place";
    private static final String TYPE_AUTOCOMPLETE = "/autocomplete";
    private static final String OUT_JSON = "/json";
    //------------ make your specific key ------------
    private static final String API_KEY = "AIzaSyARcU53tPS4oPd6GFnIfNXrog0NtLMOwpI";
    ArrayList<SearchPlaces> searchPlacesNew = new ArrayList<>();
    ArrayList<SearchPlaces> searchPlaces = new ArrayList<>();
    EditText etSearchPlace;
    private SharedPreferences mPreferences;
    private NormalTextView txtEmptyView, txtCancel, txtSearch, txt_snack_msg;
    private SharedPreferences.Editor mEditor;
    private GPSTracker gpsTracker;
    private boolean isLocationChanged;
    private FetchLocations fetchLocations;
    private post_sync postSync;
    private Bundle bundle;
    private Bundle bundleBeaconFromNotification;
    private ListView listview;
    private NormalEditText etAutoDetect;
    private ImageView imgAutoDetect;
    private PlacesAdapter placesAdapter;
    private ArrayList<String> resultList = new ArrayList<>();
    private String latitude, longitude, strAddress;
    private boolean isGpsClicked = false;
    private boolean isSearchResult;
    private String lastSearchedPlace = "";
    private MainActivity mainActivity;
    private View view;

    public static ArrayList<String> autocomplete(String input) {
        ArrayList<String> resultList = null;

        HttpURLConnection conn = null;
        StringBuilder jsonResults = new StringBuilder();
        try {
            StringBuilder sb = new StringBuilder(PLACES_API_BASE + TYPE_AUTOCOMPLETE + OUT_JSON);
            sb.append("?key=" + API_KEY);
            sb.append("&components=");
            sb.append("&input=" + URLEncoder.encode(input, "utf8"));

            URL url = new URL(sb.toString());

            System.out.println("URL: " + url);
            conn = (HttpURLConnection) url.openConnection();
            InputStreamReader in = new InputStreamReader(conn.getInputStream());

            int read;
            char[] buff = new char[1024];
            while ((read = in.read(buff)) != -1) {
                jsonResults.append(buff, 0, read);
            }
        } catch (MalformedURLException e) {
//            Log.e(LOG_TAG, "Error processing Places API URL", e);
            return resultList;
        } catch (IOException e) {
//            Log.e(LOG_TAG, "Error connecting to Places API", e);
            return resultList;
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }

        try {
            // Create a JSON object hierarchy from the results
            JSONObject jsonObj = new JSONObject(jsonResults.toString());
            JSONArray predsJsonArray = jsonObj.getJSONArray("predictions");

            // Extract the Place descriptions from the results
            resultList = new ArrayList<String>(predsJsonArray.length());
            for (int i = 0; i < predsJsonArray.length(); i++) {
                System.out.println(predsJsonArray.getJSONObject(i).getString("description"));
                System.out.println("============================================================");
                resultList.add(predsJsonArray.getJSONObject(i).getString("description"));
            }

        } catch (JSONException e) {
//            Log.e(LOG_TAG, "Cannot process JSON results", e);
        }

        return resultList;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_search, container, false);

        mPreferences = getActivity().getSharedPreferences(Constants.mPref, 0);
        mEditor = mPreferences.edit();

        initialisation();

        latitude = mPreferences.getString("latitude1", "");
        longitude = mPreferences.getString("longitude1", "");
        if (mPreferences.getString(Preference.Pref_City, "").equalsIgnoreCase("")) {
            strAddress = mPreferences.getString(Preference.Pref_Country, "");
        } else {
            strAddress = mPreferences.getString(Preference.Pref_City, "");
        }
        etAutoDetect.setText(strAddress);

        placesAdapter = new PlacesAdapter();
        listview.setAdapter(placesAdapter);
        // listview.setOnItemClickListener(this);

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                etAutoDetect.setText(resultList.get(position));
                //   str1 = resultList.get(position);
//

                hideKeyBoard(etAutoDetect);
                isLocationChanged = true;
                if (etAutoDetect.getText().toString().length() != 0) {
                    if (CommonClass.hasInternetConnection(mainActivity)) {
                        String url = "http://maps.google.com/maps/api/geocode/json?address=" + etAutoDetect.getText().toString() + "&sensor=false";
                        new PostSync(getActivity(), "Address", SearchFragment.this).execute(url);
                    } else {
                        Intent intent = new Intent(getActivity(), NoInternet.class);
                        startActivity(intent);
                        //SnackbarManager.show(Snackbar.with(mainActivity).color(Utils.getColor(mainActivity, R.color.mBlue)).text(Constants.showMessage(getActivity(), mPreferences.getString("Lan_Id", ""), "NOINTERNET")));
                    }
                }
            }
        });
        etSearchPlace.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchplace();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        etAutoDetect.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                isSearchResult = false;
                isLocationChanged = true;
                strAddress = etAutoDetect.getText().toString().trim();
                if (!isGpsClicked) {
                    if (etAutoDetect.getText().toString().length() != 0) {
                        if (CommonClass.hasInternetConnection(mainActivity)) {
                            if (fetchLocations != null) {
                                if (fetchLocations.getStatus().equals(AsyncTask.Status.RUNNING)) {
                                    fetchLocations.cancel(true);
                                }
                            }
                            fetchLocations = new FetchLocations();
                            fetchLocations.execute(etAutoDetect.getText().toString());

                        } else {
                            Intent intent = new Intent(getActivity(), NoInternet.class);
                            startActivity(intent);
                            //SnackbarManager.show(Snackbar.with(getActivity()).color(Utils.getColor(getActivity(), R.color.mBlue)).text(Constants.showMessage(getActivity(), mPreferences.getString("Lan_Id", ""), "NOINTERNET")));
                        }
                    }
                } else if (!etAutoDetect.getText().toString().equalsIgnoreCase(mPreferences.getString(Preference.Pref_City, ""))) {
                    isGpsClicked = false;
                }
            }
        });
        etAutoDetect.setImeOptions(EditorInfo.IME_ACTION_DONE);
        listview.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                Utils.hideKeyboard(getActivity());
                return false;
            }
        });

        return view;
    }

    private void searchplace() {
        if (CommonClass.hasInternetConnection(getActivity())) {
            String url = Constants.SERVER_URL + "json.php?action=SearchPlacesOTG";
            if (strAddress.trim().length() == 0) {
                strAddress = mPreferences.getString(Preference.Pref_City, "");
            }
            if (etSearchPlace.getText().toString().length() <= 1 || isLocationChanged) {
                isLocationChanged = false;
                getLocationFromAddress(getActivity(), etAutoDetect.getText().toString().trim());
                String json = "[{\"Lan_Id\":\"" + mPreferences.getString("Lan_Id", "") + "\",\"User_Id\":\"" + mPreferences.getString("User_Id", "") + "\",\"Current_Latitude\":\"" + mPreferences.getString("latitude2", "") + "\",\"Current_Longitude\":\"" + mPreferences.getString("longitude2", "") + "\",\"keywords\":\"" + etSearchPlace.getText().toString().trim() + "\",\"keyword\":\"" + strAddress + "\"}]";
                if (postSync != null && postSync.getStatus().equals(AsyncTask.Status.RUNNING)) {
                    postSync.cancel(true);
                }

                postSync = new post_sync(getActivity(), "SearchPlacesOTG", SearchFragment.this, false);
                postSync.execute(url, json);

            } else {
                if (searchPlacesNew != null && searchPlacesNew.size() > 0) {
                    searchPlaces = new ArrayList<>();
                    for (SearchPlaces search : searchPlacesNew) {
                        if (search.getPlaceName().toLowerCase().contains(etSearchPlace.getText().toString().toLowerCase())) {
                            searchPlaces.add(search);
                        }
                    }
                    placesAdapter.notifyDataSetChanged();

                }
            }
        } else {
            Intent intent = new Intent(getActivity(), NoInternet.class);
            startActivity(intent);
            //SnackbarManager.show(Snackbar.with(getActivity()).color(Utilities.getColor(getActivity(), R.color.mBlue)).text(Constants.showMessage(getActivity(), mPreferences.getString("Lan_Id", ""), "NOINTERNET")));
        }
    }

    public void addressResponse(String resultString) {

        try {
            JSONObject jsonObject = new JSONObject(resultString);
            try {
                double longitude = ((JSONArray) jsonObject.get("results")).getJSONObject(0)
                        .getJSONObject("geometry").getJSONObject("location")
                        .getDouble("lng");

                double latitude = ((JSONArray) jsonObject.get("results")).getJSONObject(0)
                        .getJSONObject("geometry").getJSONObject("location")
                        .getDouble("lat");

//            mEditor.putString("mAddress", jsonObject.getString("formatted_address")).commit();

                this.latitude = String.valueOf(latitude);
                this.longitude = String.valueOf(longitude);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void searchPlacesOTG(String resultString) {
//        Log.d("System searchResponse", resultString);

        try {
            JSONArray jsonArray = new JSONArray(resultString);
            try {
                searchPlaces = new ArrayList<>();
                if (jsonArray != null && jsonArray.length() > 0) {
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        searchPlaces.add(new SearchPlaces(jsonObject.getString("Place_Name"), jsonObject.getString("Place_MainImage"), jsonObject.getString("Place_Id")));
                    }
                }
                searchPlacesNew = searchPlaces;
                isSearchResult = true;
                placesAdapter.notifyDataSetChanged();
                listview.setEmptyView(txtEmptyView);
                txtEmptyView.setText("Sorry, No place found!");

//            mEditor.putString("mAddress", jsonObject.getString("formatted_address")).commit();


//                Intent mIntent = new Intent(SearchActivity.this, SearchResultFragmentActivity.class);
//                mIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                startActivity(mIntent);
//                finish();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void hideKeyBoard(View view) {
        InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_IMPLICIT_ONLY);
    }

    public void searchPlacesResponse(String resultString) {
        Log.d("System out", resultString);

        ArrayList<Nearby> nearbies = new ArrayList<>();

        try {
            JSONArray jsonArray = new JSONArray(resultString);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.optJSONObject(i);
                Nearby nearby = new Nearby();
                nearby.setPlace_Id(jsonObject.optString("Place_Id"));
                nearby.setCategory_Name(jsonObject.optString("Category_Name"));
                nearby.setPlace_Name(jsonObject.optString("Place_Name"));
                nearby.setPlace_ShortInfo(jsonObject.optString("Place_ShortInfo"));
                nearby.setPlace_MainImage(jsonObject.optString("Place_MainImage"));
//                nearby.setPlace_Description(jsonObject.optString("Place_Description"));

                if (jsonObject.optString("Place_Description") != null && !jsonObject.optString("Place_Description").equalsIgnoreCase("")) {
                    String price = jsonObject.optString("Place_Description");
//                    String a = price.replace("\\*", "");

//                    String b = price.replaceAll("\r", "");
//                    String c = b.replaceAll("\n", System.getProperty("line.separator"));
                    nearby.setPlace_Description(price);
                } else {
                    nearby.setPlace_Description(jsonObject.optString("Place_Description"));

                }

                nearby.setPlace_Address(jsonObject.optString("Place_Address"));

                if (jsonObject.optString("Price_Description") != null && !jsonObject.optString("Price_Description").equalsIgnoreCase("")) {
                    String price = jsonObject.optString("Price_Description");
//                    String a = price.replaceAll("\r", "");
//                    String b = a.replaceAll("\n", System.getProperty("line.separator"));
                    nearby.setPrice_Description(price);
                } else {
                    nearby.setPrice_Description(jsonObject.optString("Price_Description"));

                }

//                nearby.setPrice_Description(jsonObject.optString("Price_Description"));
                nearby.setPlace_Latitude(jsonObject.optString("Place_Latitude"));
                nearby.setPlace_Longi(jsonObject.optString("Place_Longi"));
                nearby.setOtherimages(jsonObject.optString("otherimages"));
                nearby.setDist(jsonObject.optString("dist"));
                nearby.setFav_Id(jsonObject.optString("Fav_Id"));

                JSONArray operation1 = jsonObject.getJSONArray("HourDetails");
                ArrayList<HourDetails> detailsArrayList = new ArrayList<>();
                for (int j = 0; j < operation1.length(); j++) {

                    HourDetails hourDetails = new HourDetails();
                    JSONObject jsonObject2 = operation1.getJSONObject(j);
                    hourDetails.setPlaceId(jsonObject2.getString("Place_Id"));
                    hourDetails.setPOHIsOpen(jsonObject2.getString("POH_Is_Open"));
                    hourDetails.setPOHKey(jsonObject2.getString("POH_Key"));
                    hourDetails.setPOHDay(jsonObject2.getString("POH_Day"));
                    hourDetails.setPOHStartTime(jsonObject2.getString("POH_Start_Time"));
                    hourDetails.setPOHEndTime(jsonObject2.getString("POH_End_Time"));
                    hourDetails.setPOHId(jsonObject2.getString("POH_Id"));
                    hourDetails.setFeesDetails(getFeesObject(jsonObject2));
                    detailsArrayList.add(hourDetails);
                }
                nearby.setHourDetailsArrayList(detailsArrayList);

                double d = Utilities.GetRoutDistane(Double.parseDouble(mPreferences.getString("latitude2", "")), Double.parseDouble(mPreferences.getString("longitude2", "")), Double.parseDouble(nearby.getPlace_Latitude()), Double.parseDouble(nearby.getPlace_Longi()), nearby.getDist());

                nearby.setDistance(d);
                nearbies.add(nearby);
            }
            nearbies = Utilities.sortLocations(nearbies);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (mainActivity.mainTabHostFragment.vpFragment.getCurrentItem() == 1) {
            mainActivity.favouriteFragment.replaceSearchResultFragment(nearbies, etSearchPlace.getText().toString());
        } else {
            mainActivity.exploreNearbyFragment.replaceSearchResultFragment(nearbies, etSearchPlace.getText().toString(), false);
        }
//        Intent mIntent = new Intent(getActivity(), SearchResultFragmentActivity.class);
//        mIntent.putExtra("nearbies", nearbies);
//        mIntent.putExtra("search", etSearchPlace.getText().toString());
//        startActivity(mIntent);"\"" + search + "\""
//        finish();
    }

    public ArrayList<FeesDetails> getFeesObject(JSONObject jsonObject) {
        JSONArray feesDetails = null;
        ArrayList<FeesDetails> feesArrayList = new ArrayList<>();
        try {
            feesDetails = jsonObject.getJSONArray("Fees_Details");
            if (feesDetails != null) {

                for (int k = 0; k < feesDetails.length(); k++) {

                    JSONObject jobjFees = feesDetails.getJSONObject(k);
                    FeesDetails objFees = new FeesDetails();
                    objFees.setFeesName(jobjFees.getString("Fee_Name"));
                    objFees.setFeesValue(jobjFees.getString("Fee_Value"));
                    feesArrayList.add(objFees);
                }
            }
            // hoursOfOperation.setFeesDetailses(feesArrayList);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return feesArrayList;
    }

    @Override
    public void onResponse(String response, String action) {
        try {
            if (action.equalsIgnoreCase("SearchPlacesOTG")) {
                searchPlacesOTG(response);
            } else if (action.equalsIgnoreCase("Address")) {
                addressResponse(response);
            } else if (action.equalsIgnoreCase("SearchPlaces")) {
                searchPlacesResponse(response);
            }
        } catch (Exception e) {
            Log.e(TAG, "onResponse Exception " + e.getLocalizedMessage());
        }
    }

    public void onItemClick(String str) {
        // String str = (String) adapterView.getItemAtPosition(position);
        Log.d("System out", str);

        // str1 = (String) adapterView.getItemAtPosition(position);
        //   str = (String) adapterView.getItemAtPosition(position);
//    //            Toast.makeText(AddAddressSearchAddress_Activity.this, str1, Toast.LENGTH_SHORT).show();

        // String[] s = str1.split(",");
        // Log.i("System out", "Address length " + s.length);
//        for (int j = 0; j < s.length; j++) {
        //  Log.i("System out", s[j]);
//        }
      /*  if (s.length == 6) {
//            mEditor.putString("Address_Unit",s[0]);
//            mEditor.putString("Address_Street",s[1]);
//            mEditor.putString("Address_AddressName",s[2]);
            mEditor.putString(Preference.Pref_City, s[3]).commit();
            mEditor.putString(Preference.Pref_State, s[4]).commit();
            mEditor.putString(Preference.Pref_Country, s[5]).commit();
//            Toast.makeText(AddAddressSearchAddress_Activity.this,str1+" "+s.length, Toast.LENGTH_SHORT).show();
        } else if (s.length == 5) {

//            mEditor.putString("Address_Unit", "");
//            mEditor.putString("Address_Street", s[0]);
//            mEditor.putString("Address_AddressName", s[1]);
            mEditor.putString(Preference.Pref_City, s[1]).commit();
            mEditor.putString(Preference.Pref_State, s[3]).commit();
            mEditor.putString(Preference.Pref_Country, s[4]).commit();
//            Toast.makeText(AddAddressSearchAddress_Activity.this,str1+" "+s.length, Toast.LENGTH_SHORT).show();
        } else if (s.length == 4) {

//            mEditor.putString("Address_Unit", "");
//            mEditor.putString("Address_Street", "");
//            mEditor.putString("Address_AddressName", s[0]);
            mEditor.putString(Preference.Pref_City, s[1]).commit();
            mEditor.putString(Preference.Pref_State, s[2]).commit();
            mEditor.putString(Preference.Pref_Country, s[3]).commit();
//            Toast.makeText(AddAddressSearchAddress_Activity.this,str1+" "+s.length, Toast.LENGTH_SHORT).show();

        } else if (s.length == 3) {

//            mEditor.putString("Address_Unit", "");
//            mEditor.putString("Address_Street", "");
//            mEditor.putString("Address_AddressName", "");
            mEditor.putString(Preference.Pref_City, s[0]).commit();
            mEditor.putString(Preference.Pref_State, s[1]).commit();
            mEditor.putString(Preference.Pref_Country, s[2]).commit();
//            mEditor.commit();
//            Toast.makeText(AddAddressSearchAddress_Activity.this,str1+" "+s.length, Toast.LENGTH_SHORT).show();
        } else if (s.length == 2) {
//            mEditor.putString("Address_Unit", "");
//            mEditor.putString("Address_Street", "");
//            mEditor.putString("Address_AddressName", "");
            mEditor.putString(Preference.Pref_City, s[0]).commit();
            mEditor.putString(Preference.Pref_State, s[0]).commit();
            mEditor.putString(Preference.Pref_Country, s[1]).commit();
        } else {
            mEditor.putString(Preference.Pref_City, s[0]).commit();
            mEditor.putString(Preference.Pref_State, s[0]).commit();
            mEditor.putString(Preference.Pref_Country, s[0]).commit();
        }*/

        InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(etAutoDetect.getWindowToken(), InputMethodManager.HIDE_IMPLICIT_ONLY);

        if (etAutoDetect.getText().toString().length() != 0) {
            if (CommonClass.hasInternetConnection(getActivity())) {
                String url = "http://maps.google.com/maps/api/geocode/json?address=" + etAutoDetect.getText().toString() + "&sensor=false";
                new PostSync(getActivity(), "Address", SearchFragment.this).execute(url);
            } else {
                Intent intent = new Intent(getActivity(), NoInternet.class);
                startActivity(intent);
                //SnackbarManager.show(Snackbar.with(getActivity()).color(Utils.getColor(getActivity(), R.color.mBlue)).text(Constants.showMessage(getActivity(), mPreferences.getString("Lan_Id", ""), "NOINTERNET")));
            }
        }
    }

    private void searchCall() {
        if (CommonClass.hasInternetConnection(getActivity())) {
            if (longitude.equalsIgnoreCase("") && latitude.equalsIgnoreCase("")) {
                if (strAddress != null && !strAddress.equalsIgnoreCase("")) {
                    getLocationFromAddress(getActivity(), strAddress);
                } else {
                    latitude = "23.424076";
                    longitude = "53.847818";
                    strAddress = "United Arab Emirates";
                }
            }
            String url = Constants.SERVER_URL + "json.php?action=SearchPlaces";
            String json = "[{\"Lan_Id\":\"" + mPreferences.getString("Lan_Id", "") + "\",\"User_Id\":\"" + mPreferences.getString("User_Id", "") + "\",\"Current_Latitude\":\"" + latitude + "\",\"Current_Longitude\":\"" + longitude + "\",\"keywords\":\"" + etSearchPlace.getText().toString() + "\",\"Category_Id\":\"" + "" + "\",\"keyword\":\"" + strAddress + "\"}]";
//            Log.d("System out", "SearchPlaces " + json);
            new PostSync(getActivity(), "SearchPlaces", SearchFragment.this).execute(url, json);
        } else {
            Intent intent = new Intent(getActivity(), NoInternet.class);
            startActivity(intent);
            //SnackbarManager.show((Snackbar) Snackbar.with(getActivity()).color(Utils.getColor(getActivity(), R.color.mBlue)).text(Constants.showMessage(getActivity(), mPreferences.getString("Lan_Id", ""), "NOINTERNET")));
        }
    }

    public Address getLocationFromAddress(Context context, String strAddress) {

        Geocoder coder = new Geocoder(context);
        List<Address> address;
        LatLng p1 = null;
        Address location = null;
        try {
            address = coder.getFromLocationName(strAddress, 5);
            if (address == null) {
                return null;
            }
            location = address.get(0);
            latitude = String.valueOf(location.getLatitude());
            longitude = String.valueOf(location.getLongitude());
            if (location.getAddressLine(0) != null)
                this.strAddress = location.getAddressLine(0);
            else if (location.getAdminArea() != null)
                this.strAddress = location.getAdminArea();
            else if (location.getLocality() != null)
                this.strAddress = location.getLocality();
            else if (location.getCountryName() != null)
                this.strAddress = location.getCountryName();


//
//            p1 = new LatLng(location.getLatitude(), location.getLongitude());

        } catch (Exception ex) {

            ex.printStackTrace();
        }

        return location;
    }

    public void getLocationOfAddress() {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        gpsTracker = new GPSTracker(getActivity(), true);
        if (CommonClass.hasInternetConnection(getActivity())) {
            if (gpsTracker.getLatitude() == 0 || gpsTracker.getLongitude() == 0) {
                // if (mPreferences.getString("longitude2", "").equals("")) {
                latitude = "23.424076";
                longitude = "53.847818";
                strAddress = "United Arab Emirates";
                //  }


            } else {
                latitude = String.valueOf(gpsTracker.getLatitude());
                longitude = String.valueOf(gpsTracker.getLongitude());
                getGeoLocation();


            }
        } else {
            Intent intent = new Intent(getActivity(), NoInternet.class);
            startActivity(intent);
            //SnackbarManager.show(Snackbar.with(getActivity()).color(Utils.getColor(getActivity(), R.color.mBlue)).text(Constants.showMessage(getActivity(), mPreferences.getString("Lan_Id", ""), "NOINTERNET")));
        }
    }

    private void initialisation() {

        listview = (ListView) view.findViewById(R.id.listview);

        txtSearch = (NormalTextView) view.findViewById(R.id.txtSearch);
        txtSearch.setText(Constants.showMessage(getActivity(), mPreferences.getString("Lan_Id", ""), "Search"));
        txtSearch.setOnClickListener(this);

        txtEmptyView = (NormalTextView) view.findViewById(R.id.txtEmptyView);
        txtCancel = (NormalTextView) view.findViewById(R.id.txtCancel);
        txtCancel.setText(Constants.showMessage(getActivity(), mPreferences.getString("Lan_Id", ""), "Cancel"));
        txtCancel.setOnClickListener(this);

        etAutoDetect = (NormalEditText) view.findViewById(R.id.etAutoDetect);
        etSearchPlace = (EditText) view.findViewById(R.id.etSearchPlace);

        imgAutoDetect = (ImageView) view.findViewById(R.id.imgAutoDetect);
        imgAutoDetect.setOnClickListener(this);

        if (bundle != null) {
            if (bundle.getString("from") != null && bundle.getString("from").equals("beacon")) {
                Constants.placeId = bundle.getString(PLACE_ID);
            }
        } else if (bundleBeaconFromNotification != null) {
            if (bundleBeaconFromNotification.getString(PLACE_ID) != null && !bundleBeaconFromNotification.getString(PLACE_ID).equals("")) {
                if (!bundleBeaconFromNotification.getString("type").equals(BEACON_EXITED))
                    Constants.placeId = bundleBeaconFromNotification.getString(PLACE_ID);
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imgAutoDetect:
                // getLocationOfAddress();
                Utils.hideKeyboard(getActivity());
                isGpsClicked = true;
                etAutoDetect.setText(mPreferences.getString(Preference.Pref_City, ""));
                latitude = mPreferences.getString("latitude1", "");
                longitude = mPreferences.getString("longitude1", "");
                strAddress = mPreferences.getString(Preference.Pref_City, "");
                break;

            case R.id.txtCancel:
                Utils.hideKeyboard(getActivity());
                mainActivity.onBackPressed();
                break;

            case R.id.txtSearch:
                Utils.hideKeyboard(getActivity());
               /* if (etSearchPlace.getText().toString().trim().length() == 0) {

                } else {*/
                    searchCall();
//                }

                break;
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mainActivity = (MainActivity) context;
    }

    public void getGeoLocation() {
        try {
            Geocoder geo = new Geocoder(getActivity(), Locale.getDefault());
            List<Address> addresses = geo.getFromLocation(Double.parseDouble(latitude), Double.parseDouble(longitude), 1);
            if (addresses.isEmpty()) {
            } else {
                if (addresses.size() > 0) {
                    String str = (addresses.get(0).getAddressLine(0) + ", " + addresses.get(0).getLocality() + ", "
                            + addresses.get(0).getAdminArea() + ", " + addresses.get(0).getCountryName());

                    String umAddress1 = addresses.get(0).getAddressLine(0);
                    String city = addresses.get(0).getLocality();
                    String state = addresses.get(0).getAdminArea();
                    String zipCode = addresses.get(0).getPostalCode();
                    String country = addresses.get(0).getCountryName();

                    String addressmaps = str.replaceAll(" null,", "");
                    // flag = 0;
                    //  etSearch.setHint(addressmaps);
                    etAutoDetect.setText(addressmaps);
                    strAddress = addressmaps;

                    /*Intent mIntent = new Intent(SelectLocationFragmentActivity.this, YourLocationFragmentActivity.class);
                    startActivity(mIntent);
                    finish();*/

                    Log.i("System out", "Get current location city--> " + city);
                    Log.i("System out", "Get current location state--> " + state);
                    Log.i("System out", "Get current location country--> " + country);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();

            String address = "";
            String apiRequest = "https://maps.googleapis.com/maps/api/geocode/json?latlng=" + latitude + "," + longitude; //+ "&ka&sensor=false"
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
                etAutoDetect.setText(address);
                strAddress = address;
            } catch (JSONException e3) {
                e.printStackTrace();
            }

        }
    }

    class FetchLocations extends AsyncTask<String, Void, Object> {

        @Override
        protected Object doInBackground(String... strings) {
            resultList = autocomplete(strings[0]);

            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            placesAdapter.notifyDataSetChanged();
        }
    }

    class FetchPlaces extends AsyncTask<String, Void, Object> {

        @Override
        protected Object doInBackground(String... strings) {
            resultList = autocomplete(strings[0]);

            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            placesAdapter.notifyDataSetChanged();
        }
    }

    class PlacesAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            if (isSearchResult) {
                return searchPlaces == null ? 0 : searchPlaces.size();
            } else {
                return resultList == null ? 0 : resultList.size();
            }
        }

        @Override
        public Object getItem(int position) {
            if (isSearchResult) {
                return searchPlaces.get(position);
            } else {
                return resultList.get(position);
            }
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View view = LayoutInflater.from(getActivity()).inflate(R.layout.row_item_search_place, parent, false);
            TextView txtPlaceName = (TextView) view.findViewById(R.id.txtPlaceName);
            ImageView imageView = (ImageView) view.findViewById(R.id.imgPlace);
            if (isSearchResult) {
                txtPlaceName.setText(searchPlaces.get(position).getPlaceName());
                String imageUrl = Constants.IMAGE_URL + searchPlaces.get(position).getPlaceMainImage() + "&w=" + (imageView.getWidth());
                Picasso.with(getActivity()).load(imageUrl).resize(100, 100).into(imageView);
                imageView.setVisibility(View.VISIBLE);
            } else {
                txtPlaceName.setText(resultList.get(position));
                imageView.setVisibility(View.GONE);

            }
            txtPlaceName.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Utils.hideKeyboard(getActivity());
                    if (isSearchResult) {
                        Intent intent = new Intent(getActivity(), SearchResultPlaceDetailsActivity.class);
                        intent.putExtra("placeId", searchPlaces.get(position).getPlaceId());
                        intent.putExtra("location", etAutoDetect.getText().toString().trim());
                        startActivity(intent);
                    } else {

                        getLocationFromAddress(getActivity(), resultList.get(position));
                        etAutoDetect.setText(strAddress);
                        // onItemClick(resultList.get(position));
                    }
                }
            });
            return view;
        }
    }


}
