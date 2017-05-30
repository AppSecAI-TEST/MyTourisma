package com.ftl.tourisma;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ftl.tourisma.activity.NoInternet;
import com.ftl.tourisma.custom_views.NormalEditText;
import com.ftl.tourisma.custom_views.NormalTextView;
import com.ftl.tourisma.database.DBAdapter;
import com.ftl.tourisma.database.FeesDetails;
import com.ftl.tourisma.database.Nearby;
import com.ftl.tourisma.models.HourDetails;
import com.ftl.tourisma.postsync.PostSync;
import com.ftl.tourisma.postsync.post_sync;
import com.ftl.tourisma.utils.CommonClass;
import com.ftl.tourisma.utils.Constants;
import com.ftl.tourisma.utils.GPSTracker;
import com.ftl.tourisma.utils.Preference;
import com.ftl.tourisma.utils.Utils;
import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.SnackbarManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by fipl11111 on 02-Mar-16.
 */
public class SearchFragmentActivity extends FragmentActivity implements View.OnClickListener, post_sync.ResponseHandler {

    private static final String TAG = SearchFragmentActivity.class.getSimpleName();
    private ImageView iv_back_header4;
    private NormalTextView tv_city_header4;
    private SharedPreferences mPreferences;
    private SharedPreferences.Editor mEditor;
    private ImageView iv_search;
    private Nearby nearby;
    private ArrayList<Nearby> nearbies = new ArrayList<>();
    private NormalEditText edt_search;
    private NormalTextView tv_all_places, tv_suggestion, tv_recent_search, tv_your_location_header4;
    private DBAdapter dbAdapter;
    private LinearLayout ll_all_places, ll_search_history, ll_header4_location;
    private ArrayList<String> searchArray = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);
        setContentView(R.layout.history);

        mPreferences = getSharedPreferences(Constants.mPref, 0);
        mEditor = mPreferences.edit();

        dbAdapter = new DBAdapter(SearchFragmentActivity.this);

        initialisation();
        GPSTracker gpsTracker = new GPSTracker(this, true);
        if (gpsTracker.getLatitude() != 0 && gpsTracker.getLongitude() != 0) {
            mEditor.putString("latitude2", String.valueOf(gpsTracker.getLatitude())).commit();
            mEditor.putString("longitude2", String.valueOf(gpsTracker.getLongitude())).commit();
        }

    }

    private void initialisation() {
        tv_suggestion = (NormalTextView) findViewById(R.id.tv_suggestion);
        tv_suggestion.setText(Constants.showMessage(SearchFragmentActivity.this, mPreferences.getString("Lan_Id", ""), "suggestions"));
        tv_recent_search = (NormalTextView) findViewById(R.id.tv_recent_search);
        tv_recent_search.setText(Constants.showMessage(SearchFragmentActivity.this, mPreferences.getString("Lan_Id", ""), "recentsearch"));
        tv_your_location_header4 = (NormalTextView) findViewById(R.id.tv_your_location_header4);
        tv_your_location_header4.setText(Constants.showMessage(SearchFragmentActivity.this, mPreferences.getString("Lan_Id", ""), "locationtitle"));
        ll_search_history = (LinearLayout) findViewById(R.id.ll_search_history);
        ll_header4_location = (LinearLayout) findViewById(R.id.ll_header4_location);
        ll_header4_location.setOnClickListener(this);
        ll_all_places = (LinearLayout) findViewById(R.id.ll_all_places);
        ll_all_places.setOnClickListener(this);

        dbAdapter.open();
        searchArray = dbAdapter.getSearchStr();
        dbAdapter.close();

        tv_all_places = (NormalTextView) findViewById(R.id.tv_all_places);
        tv_all_places.setText(Constants.showMessage(SearchFragmentActivity.this, mPreferences.getString("Lan_Id", ""), "All Places in") + " " + mPreferences.getString(Preference.Pref_City, "") + ", " + mPreferences.getString(Preference.Pref_State, ""));
        iv_back_header4 = (ImageView) findViewById(R.id.iv_back_header4);
        iv_back_header4.setOnClickListener(this);
        tv_city_header4 = (NormalTextView) findViewById(R.id.tv_city_header4);
        tv_city_header4.setText(mPreferences.getString(Preference.Pref_City, ""));
        iv_search = (ImageView) findViewById(R.id.iv_search);
        iv_search.setOnClickListener(this);
        edt_search = (NormalEditText) findViewById(R.id.edt_search);
        edt_search.setHint(Constants.showMessage(SearchFragmentActivity.this, mPreferences.getString("Lan_Id", ""), "Searchplaceholder"));
        edt_search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    if (edt_search.getText().toString().length() != 0) {
                        dbAdapter.open();
                        int flag = 0;
                        ArrayList<String> str = dbAdapter.getSearchStr();
                        for (int i = 0; i < str.size(); i++) {
                            if (str.get(i).equalsIgnoreCase(edt_search.getText().toString().trim())) {
                                flag = 1;
                                break;
                            }
                        }
                        if (flag == 0) {
                            dbAdapter.insertSearchString(edt_search.getText().toString());
                        }
                        dbAdapter.close();
                        searchCall();
                    } else {
                        SnackbarManager.show((Snackbar) Snackbar.with(SearchFragmentActivity.this).color(Utils.getColor(SearchFragmentActivity.this,R.color.mBlue)).text(Constants.showMessage(SearchFragmentActivity.this, mPreferences.getString("Lan_Id", ""), "Entertext")));
                    }
                    return true;
                }
                return false;
            }
        });

        setSearchHistory();
    }

    public void setSearchHistory() {
        ll_search_history.removeAllViews();

        if (searchArray.size() > 0) {
            ll_all_places.setVisibility(View.VISIBLE);

            for (int i = 0; i < searchArray.size(); i++) {
                LayoutInflater inflater = (LayoutInflater) SearchFragmentActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View view = inflater.inflate(R.layout.history_adapter, null);
                final NormalTextView tv_searched = (NormalTextView) view.findViewById(R.id.tv_searched);
                final NormalTextView tv_type = (NormalTextView) view.findViewById(R.id.tv_type);
                tv_searched.setText(searchArray.get(i));

                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        edt_search.setText(tv_searched.getText());
                        searchCall();
                    }
                });

                tv_type.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        edt_search.setText(tv_searched.getText());
                    }
                });

                ll_search_history.addView(view);
            }
        }
    }

    @Override
    public void onClick(View v) {
        if (v == iv_back_header4) {
            Utils.hideKeyboard(this);
            finish();
        } else if (v == iv_search) {

        } else if (v == ll_all_places) {
            edt_search.setText(mPreferences.getString(Preference.Pref_City, "").toString());
            searchCall();
        } else if (v == ll_header4_location) {
            Intent mIntent = new Intent(SearchFragmentActivity.this, SelectLocationFragmentActivity.class);
            startActivity(mIntent);
        }
    }

    private void searchCall() {
        if (CommonClass.hasInternetConnection(SearchFragmentActivity.this)) {
            String url = Constants.SERVER_URL + "json.php?action=SearchPlaces";
            String json = "[{\"Lan_Id\":\"" + mPreferences.getString("Lan_Id", "") + "\",\"User_Id\":\"" + mPreferences.getString("User_Id", "") + "\",\"Current_Latitude\":\"" + mPreferences.getString("latitude2", "") + "\",\"Current_Longitude\":\"" + mPreferences.getString("longitude2", "") + "\",\"keywords\":\"" + edt_search.getText().toString() + "\",\"Category_Id\":\"" + "" + "\",\"keyword\":\"" + mPreferences.getString(Preference.Pref_City, "") + "\"}]";
            new PostSync(SearchFragmentActivity.this, "SearchPlaces",SearchFragmentActivity.this).execute(url, json);
        } else {
            Intent intent = new Intent(getApplicationContext(), NoInternet.class);
            startActivity(intent);
            //SnackbarManager.show((Snackbar) Snackbar.with(SearchFragmentActivity.this).color(Utils.getColor(this,R.color.mBlue)).text(Constants.showMessage(SearchFragmentActivity.this, mPreferences.getString("Lan_Id", ""), "NOINTERNET")));
        }
    }

    public void searchResponse(String resultString) {
        try {
            JSONArray jsonArray = new JSONArray(resultString);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.optJSONObject(i);
                nearby = new Nearby();
                nearby.setPlace_Id(jsonObject.optString("Place_Id"));
                nearby.setCategory_Name(jsonObject.optString("Category_Name"));
                nearby.setPlace_Name(jsonObject.optString("Place_Name"));
                nearby.setPlace_ShortInfo(jsonObject.optString("Place_ShortInfo"));
                nearby.setPlace_MainImage(jsonObject.optString("Place_MainImage"));
                if (jsonObject.optString("Place_Description") != null && !jsonObject.optString("Place_Description").equalsIgnoreCase("")) {
                    String price = jsonObject.optString("Place_Description");
                    nearby.setPlace_Description(price);
                } else {
                    nearby.setPlace_Description(jsonObject.optString("Place_Description"));
                }
                nearby.setPlace_Address(jsonObject.optString("Place_Address"));

                if(jsonObject.optString("Price_Description")!=null && !jsonObject.optString("Price_Description").equalsIgnoreCase("")) {
                    String price=jsonObject.optString("Price_Description");
                    nearby.setPrice_Description(price);
                }else{
                    nearby.setPrice_Description(jsonObject.optString("Price_Description"));
                }
                nearby.setPlace_Latitude(jsonObject.optString("Place_Latitude"));
                nearby.setPlace_Longi(jsonObject.optString("Place_Longi"));
                nearby.setOtherimages(jsonObject.optString("otherimages"));
                nearby.setDist(jsonObject.optString("dist"));
                nearby.setFav_Id(jsonObject.optString("Fav_Id"));
                nearby.setFree_entry(jsonObject.optString("free_entry"));

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
                nearbies.add(nearby);
            }
        } catch (JSONException e) {
            // Tracking exception
            MyTorismaApplication.getInstance().trackException(e);
            e.printStackTrace();
        }
        Intent mIntent = new Intent(SearchFragmentActivity.this, SearchResultFragmentActivity.class);
        mIntent.putExtra("nearbies", nearbies);
        mIntent.putExtra("search", edt_search.getText().toString());
        startActivity(mIntent);
        finish();
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
        } catch (JSONException e) {
            // Tracking exception
            MyTorismaApplication.getInstance().trackException(e);
            e.printStackTrace();
        }
        return feesArrayList;
    }

    @Override
    public void onResponse(String response, String action) {
        try {
            if (action.equalsIgnoreCase("SearchPlaces")) {
                searchResponse(response);
            }
        } catch (Exception e) {
            // Tracking exception
            MyTorismaApplication.getInstance().trackException(e);
            Log.e(TAG, "onResponse Exception " + e.getLocalizedMessage());
        }
    }
}
