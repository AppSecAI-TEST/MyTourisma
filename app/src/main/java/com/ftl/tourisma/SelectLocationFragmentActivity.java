package com.ftl.tourisma;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.provider.Settings;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.LocalBroadcastManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.transition.Fade;
import android.transition.Slide;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.ftl.tourisma.activity.MainActivity;
import com.ftl.tourisma.activity.NoInternet;
import com.ftl.tourisma.custom_views.NormalEditText;
import com.ftl.tourisma.custom_views.NormalTextView;
import com.ftl.tourisma.postsync.PostSync;
import com.ftl.tourisma.postsync.post_sync;
import com.ftl.tourisma.utils.CommonClass;
import com.ftl.tourisma.utils.Constants;
import com.ftl.tourisma.utils.GPSTracker;
import com.ftl.tourisma.utils.Preference;
import com.ftl.tourisma.utils.Utils;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
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
import java.util.Timer;

import static com.ftl.tourisma.beacons.MyBeaconsService.BEACON_ENTERED;
import static com.ftl.tourisma.beacons.MyBeaconsService.BEACON_ENTRY_TEXT;
import static com.ftl.tourisma.beacons.MyBeaconsService.BEACON_EXITED;
import static com.ftl.tourisma.beacons.MyBeaconsService.BEACON_EXIT_TEXT;
import static com.ftl.tourisma.beacons.MyBeaconsService.BEACON_IS_CLOSE_PROMO;
import static com.ftl.tourisma.beacons.MyBeaconsService.BEACON_MESSAGE;
import static com.ftl.tourisma.beacons.MyBeaconsService.BEACON_NEAR_BY;
import static com.ftl.tourisma.beacons.MyBeaconsService.BEACON_NEAR_BY_TEXT;
import static com.ftl.tourisma.beacons.MyBeaconsService.BROADCAST_BEACON;
import static com.ftl.tourisma.beacons.MyBeaconsService.PLACE_ID;
import static com.ftl.tourisma.beacons.MyBeaconsService.PLACE_IMAGE;

/**
 * Created by fipl11111 on 25-Feb-16.
 */
public class SelectLocationFragmentActivity extends FragmentActivity implements View.OnClickListener, AdapterView.OnItemClickListener, post_sync.ResponseHandler {

    private static final String LOG_TAG = "System out";
    private static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place";
    private static final String TYPE_AUTOCOMPLETE = "/autocomplete";
    private static final String OUT_JSON = "/json";
    //------------ make your specific key ------------
    private static final String API_KEY = "AIzaSyARcU53tPS4oPd6GFnIfNXrog0NtLMOwpI";
    private static final String TAG = SelectLocationFragmentActivity.class.getSimpleName();
    static int mflag = 0;
    //    private static final String API_KEY = "AIzaSyDG7V34oHpHL5OtuXRcvd-TBg4cyg8rWgc";
    private static int flag = -1;
    Handler handler = new Handler();
    private ImageView iv_close_header2;
    //    private AutoCompleteTextView act_address;
    private NormalEditText etSearch;
    private String umAddress1, city, zipCode, country, addressmaps, state;
    private ImageView iv_auto_location;
    private SharedPreferences mPreferences;
    private SharedPreferences.Editor mEditor;
    private String str1;
    private GPSTracker gpsTracker;
    private JSONObject mJsonObject;
    private ListView listview;
    private NormalTextView tv_select, tv_auto_detect;
    private PlacesAdapter adapter;
    private ArrayList<String> resultList = new ArrayList<>();
    private Timer timer;
    private FetchLocations fetchLocations;
    private LinearLayout llBeaconToast;
    private NormalTextView txt_snack_msg;
    private Runnable runnable;
    private Handler handlerBeaconToast = new Handler();
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null) {
                Bundle bundle = intent.getExtras();
                String type = bundle.getString("type");
                if (type.equals(BEACON_ENTERED)) {
                    beaconsToast(bundle.getString(BEACON_ENTRY_TEXT), bundle.getString(BEACON_MESSAGE), bundle.getString(PLACE_IMAGE), bundle.getString(PLACE_ID), 0, bundle.getString(BEACON_IS_CLOSE_PROMO));

                } else if (type.equals(BEACON_EXITED)) {
                    beaconsToast(bundle.getString(BEACON_EXIT_TEXT), bundle.getString(BEACON_MESSAGE), bundle.getString(PLACE_IMAGE), bundle.getString(PLACE_ID), 0, bundle.getString(BEACON_IS_CLOSE_PROMO));

                } else if (type.equals(BEACON_NEAR_BY)) {
                    beaconsToast(bundle.getString(BEACON_NEAR_BY_TEXT), bundle.getString(BEACON_MESSAGE), bundle.getString(PLACE_IMAGE), bundle.getString(PLACE_ID), 1, bundle.getString(BEACON_IS_CLOSE_PROMO));

                }
            }
        }
    };

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

            // Load the results into a StringBuilder
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

    private void setupWindowAnimations() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Fade fade = new Fade();
            fade.setDuration(400);
            getWindow().setEnterTransition(fade);

            Slide slide = new Slide();
            slide.setDuration(400);
            getWindow().setReturnTransition(slide);
        }
    }

    private void beaconsToast(final String msg, final String msgBeacon, final String img, final String placeId, final int isCloseApproach, final String isClosePromo) {
        if (msg != null && !msg.equals("")) {

            txt_snack_msg.setText(msg);
            txt_snack_msg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isClosePromo.equals("1")) {
                        Intent intent = new Intent(SelectLocationFragmentActivity.this, BeaconsActivity.class);
                        intent.putExtra(PLACE_ID, placeId);
                        intent.putExtra(PLACE_IMAGE, img);
                        intent.putExtra(BEACON_MESSAGE, msgBeacon);
                        startActivity(intent);
                    } else {
                        Intent intent = new Intent(SelectLocationFragmentActivity.this, SearchResultPlaceDetailsActivity.class);
                        intent.putExtra("placeId", placeId);
                        startActivity(intent);
                    }


                }
            });


            runnable = new Runnable() {
                @Override
                public void run() {
                    llBeaconToast.setVisibility(View.GONE);
                }
            };
            llBeaconToast.setVisibility(View.VISIBLE);
            handlerBeaconToast.postDelayed(runnable, 4000);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(
                broadcastReceiver,
                new IntentFilter(BROADCAST_BEACON));
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            unregisterReceiver(broadcastReceiver);
        } catch (Exception e) {
            // Tracking exception
            MyTorismaApplication.getInstance().trackException(e);
            e.printStackTrace();
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

        setContentView(R.layout.select_location);
        setupWindowAnimations();
        mPreferences = getSharedPreferences(Constants.mPref, 0);
        mEditor = mPreferences.edit();

        initialisation();

//        mEditor.putString("latitude1", "").commit();
//        mEditor.putString("longitude1", "").commit();
//        mEditor.putString("latitude2", "").commit();
//        mEditor.putString("longitude2", "").commit();

        gpsTracker = new GPSTracker(SelectLocationFragmentActivity.this);
        if (gpsTracker.canGetLocation()) {
            gpsTracker.getLocation();
            mEditor.putString("latitude1", String.valueOf(gpsTracker.getLatitude())).commit();
            mEditor.putString("longitude1", String.valueOf(gpsTracker.getLongitude())).commit();
            mEditor.putString("latitude2", String.valueOf(gpsTracker.getLatitude())).commit();
            mEditor.putString("longitude2", String.valueOf(gpsTracker.getLongitude())).commit();

//            Log.d("System out", "Constant.latitude1 " + mPreferences.getString("latitude1", ""));
//            Log.d("System out", "Constant.longitude1 " + mPreferences.getString("longitude1", ""));

            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    getLocationOfAddress();
                }
            }, 2);
        }

        iv_close_header2.setOnClickListener(this);
        iv_auto_location.setOnClickListener(this);
        tv_auto_detect.setOnClickListener(this);

    }

    private void initialisation() {
        Constants.mStaticFavCall = 0;

        listview = (ListView) findViewById(R.id.listview);

        tv_auto_detect = (NormalTextView) findViewById(R.id.tv_auto_detect);
        tv_auto_detect.setText(Constants.showMessage(SelectLocationFragmentActivity.this, mPreferences.getString("Lan_Id", ""), "currentlocationtost"));

        tv_select = (NormalTextView) findViewById(R.id.txtTitle);
        tv_select.setText(Constants.showMessage(SelectLocationFragmentActivity.this, mPreferences.getString("Lan_Id", ""), "selectlocation"));

        iv_close_header2 = (ImageView) findViewById(R.id.img_close);
        iv_auto_location = (ImageView) findViewById(R.id.iv_auto_location);
//        act_address = (AutoCompleteTextView) findViewById(R.id.act_address);
        etSearch = (NormalEditText) findViewById(R.id.etSearch);
        etSearch.setHint(Constants.showMessage(SelectLocationFragmentActivity.this, mPreferences.getString("Lan_Id", ""), "searchlocationtost"));
//        act_address.setAdapter(new GooglePlacesAutocompleteAdapter(this, R.layout.list_item));
        adapter = new PlacesAdapter();
        listview.setAdapter(adapter);
        // listview.setOnItemClickListener(this);
        llBeaconToast = (LinearLayout) findViewById(R.id.llBeaconToast);
        txt_snack_msg = (NormalTextView) findViewById(R.id.txt_snack_msg);

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                etSearch.setText(resultList.get(position));
                str1 = resultList.get(position);
                String[] s = str1.split(",");
                // Log.i("System out", "Address length " + s.length);
                for (int j = 0; j < s.length; j++) {
                    // Log.i("System out", s[j]);
                }
                if (s.length == 6) {
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
                }

                Utils.hideKeyboard(SelectLocationFragmentActivity.this);

                if (etSearch.getText().toString().length() != 0) {
                    if (CommonClass.hasInternetConnection(SelectLocationFragmentActivity.this)) {
                        String url = "http://maps.google.com/maps/api/geocode/json?address=" + etSearch.getText().toString() + "&sensor=false";
                        new PostSync(SelectLocationFragmentActivity.this, "Address", SelectLocationFragmentActivity.this).execute(url);
                    } else {
                        Intent intent = new Intent(getApplicationContext(), NoInternet.class);
                        startActivity(intent);
                        //SnackbarManager.show(Snackbar.with(SelectLocationFragmentActivity.this).color(Utils.getColor(SelectLocationFragmentActivity.this, R.color.mBlue)).text(Constants.showMessage(SelectLocationFragmentActivity.this, mPreferences.getString("Lan_Id", ""), "NOINTERNET")));
                    }
                }
            }
        });
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (etSearch.getText().toString().length() != 0) {
                    if (CommonClass.hasInternetConnection(SelectLocationFragmentActivity.this)) {

                        if (fetchLocations != null) {
                            if (fetchLocations.getStatus().equals(AsyncTask.Status.RUNNING)) {
                                fetchLocations.cancel(true);
                            }
                        }
                        fetchLocations = new FetchLocations();
                        fetchLocations.execute(etSearch.getText().toString());
                    } else {
                        Intent intent = new Intent(getApplicationContext(), NoInternet.class);
                        startActivity(intent);
                        //SnackbarManager.show(Snackbar.with(SelectLocationFragmentActivity.this).color(Utils.getColor(SelectLocationFragmentActivity.this, R.color.mBlue)).text(Constants.showMessage(SelectLocationFragmentActivity.this, mPreferences.getString("Lan_Id", ""), "NOINTERNET")));
                    }
                }
            }
        });
        etSearch.setImeOptions(EditorInfo.IME_ACTION_DONE);
        etSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    if (etSearch.getText().toString().length() != 0) {
                        if (CommonClass.hasInternetConnection(SelectLocationFragmentActivity.this)) {
                            mflag = 0;
                            String url = "http://maps.google.com/maps/api/geocode/json?address=" + etSearch.getText().toString() + "&sensor=false";
                            new PostSync(SelectLocationFragmentActivity.this, "Address", SelectLocationFragmentActivity.this).execute(url);
                        } else {
                            Intent intent = new Intent(getApplicationContext(), NoInternet.class);
                            startActivity(intent);
                            //SnackbarManager.show(Snackbar.with(SelectLocationFragmentActivity.this).color(Utils.getColor(SelectLocationFragmentActivity.this, R.color.mBlue)).text(Constants.showMessage(SelectLocationFragmentActivity.this, mPreferences.getString("Lan_Id", ""), "NOINTERNET")));
                        }
                    } else {
//
                    }
                }
                return false;
            }
        });

        listview.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                Utils.hideKeyboard(SelectLocationFragmentActivity.this);
                return false;
            }
        });

    }

    @Override
    public void onResponse(String response, String action) {
        try {
            if (action.equalsIgnoreCase("Address")) {
                addressResponse(response);
            }
        } catch (Exception e) {
            // Tracking exception
            MyTorismaApplication.getInstance().trackException(e);
            Log.e(TAG, "onResponse Exception " + e.getLocalizedMessage());
        }
    }

    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        String str = (String) adapterView.getItemAtPosition(position);
        Log.d("System out", str);

        mflag = 1;

        str1 = (String) adapterView.getItemAtPosition(position);
        str = (String) adapterView.getItemAtPosition(position);
//                Toast.makeText(AddAddressSearchAddress_Activity.this, str1, Toast.LENGTH_SHORT).show();

        String[] s = str1.split(",");
        // Log.i("System out", "Address length " + s.length);
//        for (int j = 0; j < s.length; j++) {
        //  Log.i("System out", s[j]);
//        }
        if (s.length == 6) {
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
        }

        InputMethodManager inputManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_IMPLICIT_ONLY);

        if (etSearch.getText().toString().length() != 0) {
            if (CommonClass.hasInternetConnection(SelectLocationFragmentActivity.this)) {
                String url = "http://maps.google.com/maps/api/geocode/json?address=" + etSearch.getText().toString() + "&sensor=false";
                new PostSync(SelectLocationFragmentActivity.this, "Address", SelectLocationFragmentActivity.this).execute(url);
            } else {
                Intent intent = new Intent(getApplicationContext(), NoInternet.class);
                startActivity(intent);
                //SnackbarManager.show(Snackbar.with(SelectLocationFragmentActivity.this).color(Utils.getColor(this, R.color.mBlue)).text(Constants.showMessage(SelectLocationFragmentActivity.this, mPreferences.getString("Lan_Id", ""), "NOINTERNET")));
            }
        }
    }
//    private void hideKeyBoard(View view) {
//        InputMethodManager inputManager = (InputMethodManager) SelectLocationFragmentActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
//        inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_IMPLICIT_ONLY);
//    }

    @Override
    public void onClick(View v) {
        if (v == iv_close_header2) {
            Utils.hideKeyboard(this);
            onBackPressed();

        } else if (v == iv_auto_location || v == tv_auto_detect) {
            etSearch.setText(addressmaps);
            //  if (etSearch.getText().toString().length() == 0) {
            mEditor.putString("mAddress", addressmaps).commit();
            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    getLocationOfAddress();
                    Constants.mFromSelectLocation = 1;
                    Intent mIntent = new Intent(SelectLocationFragmentActivity.this, MainActivity.class);
                    mIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(mIntent);
                    finish();
                }
            }, 2);
            // } else {
//                Constants.mFromSelectLocation = 1;
//                Intent mIntent = new Intent(SelectLocationFragmentActivity.this, YourLocationFragmentActivity.class);
//                mIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                startActivity(mIntent);
//                finish();
            // }
        }
    }

    public void getLocationOfAddress() {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        if (CommonClass.hasInternetConnection(SelectLocationFragmentActivity.this)) {
            if (mPreferences.getString("latitude2", "").length() != 0 && mPreferences.getString("longitude2", "").length() != 0 && !mPreferences.getString("longitude2", "").equalsIgnoreCase("0.0")) {
                try {
                    Geocoder geo = new Geocoder(SelectLocationFragmentActivity.this.getApplicationContext(), Locale.getDefault());
//                    Utils.toast(Double.parseDouble(mPreferences.getString("latitude1", "")) + "**" + Double.parseDouble(mPreferences.getString("longitude1", "")));
                    List<Address> addresses = geo.getFromLocation(Double.parseDouble(mPreferences.getString("latitude1", "")), Double.parseDouble(mPreferences.getString("longitude1", "")), 1);
                    if (addresses.isEmpty()) {
                    } else {
                        if (addresses.size() > 0) {
                            String str = (addresses.get(0).getAddressLine(0) + ", " + addresses.get(0).getLocality() + ", "
                                    + addresses.get(0).getAdminArea() + ", " + addresses.get(0).getCountryName());

                            umAddress1 = addresses.get(0).getAddressLine(0);
                            city = addresses.get(0).getLocality();
                            mEditor.putString(Preference.Pref_City, city).commit();
                            state = addresses.get(0).getAdminArea();
                            mEditor.putString(Preference.Pref_State, state).commit();
                            zipCode = addresses.get(0).getPostalCode();
                            country = addresses.get(0).getCountryName();
                            mEditor.putString(Preference.Pref_Country, country).commit();

                            addressmaps = str.replaceAll(" null,", "");
                            flag = 0;
                            etSearch.setHint(addressmaps);




                    /*Intent mIntent = new Intent(SelectLocationFragmentActivity.this, YourLocationFragmentActivity.class);
                    startActivity(mIntent);
                    finish();*/

//                            Log.i("System out", "Get current location city--> " + city);
//                            Log.i("System out", "Get current location state--> " + state);
//                            Log.i("System out", "Get current location country--> " + country);
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
                if(gpsTracker==null)
                     gpsTracker = new GPSTracker(SelectLocationFragmentActivity.this);
                if (gpsTracker.canGetLocation()) {
//            gpsTracker.getLocation();
                    mEditor.putString("latitude1", String.valueOf(gpsTracker.getLatitude())).commit();
                    mEditor.putString("longitude1", String.valueOf(gpsTracker.getLongitude())).commit();
                    mEditor.putString("latitude2", String.valueOf(gpsTracker.getLatitude())).commit();
                    mEditor.putString("longitude2", String.valueOf(gpsTracker.getLongitude())).commit();

//            Constants.latitude = String.valueOf(gpsTracker.getLatitude());
//            Constants.longitude = String.valueOf(gpsTracker.getLongitude());

//                    Log.d("System out", "Constant.latitude1 " + mPreferences.getString("latitude1", ""));
//                    Log.d("System out", "Constant.longitude1 " + mPreferences.getString("longitude1", ""));

                    new Handler().postDelayed(new Runnable() {

                        @Override
                        public void run() {
                            getLocationOfAddress();
                        }
                    }, 2);
                }
            }
        } else {
            Intent intent = new Intent(getApplicationContext(), NoInternet.class);
            startActivity(intent);
            //SnackbarManager.show(Snackbar.with(SelectLocationFragmentActivity.this).color(Utils.getColor(this, R.color.mBlue)).text(Constants.showMessage(SelectLocationFragmentActivity.this, mPreferences.getString("Lan_Id", ""), "NOINTERNET")));
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    public void addressResponse(String resultString) {
//        Log.d("System out", resultString);

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


                if (gpsTracker == null) {
                    gpsTracker = new GPSTracker();
                }
                if (gpsTracker.getLongitude() == 0 && gpsTracker.getLatitude() == 0) {
                    mEditor.putString("latitude2", String.valueOf(latitude)).commit();
                    mEditor.putString("longitude2", String.valueOf(longitude)).commit();
                    mEditor.putString("latitude1", String.valueOf(latitude)).commit();
                    mEditor.putString("longitude1", String.valueOf(longitude)).commit();
                } else {
                    mEditor.putString("latitude2", String.valueOf(gpsTracker.getLatitude())).commit();
                    mEditor.putString("longitude2", String.valueOf(gpsTracker.getLongitude())).commit();
                    mEditor.putString("latitude1", String.valueOf(gpsTracker.getLatitude())).commit();
                    mEditor.putString("longitude1", String.valueOf(gpsTracker.getLongitude())).commit();
                }
                //    Log.i("System out", "mFlag --- " + mflag);

//                if (mflag == 0) {
                String mCity = ((JSONArray) jsonObject.get("results")).getJSONObject(0)
                        .getJSONArray("address_components").getJSONObject(0)
                        .getString("long_name");
                ;
                mEditor.putString(Preference.Pref_City, mCity).commit();
                if (((JSONArray) jsonObject.get("results")).getJSONObject(0)
                        .getJSONArray("address_components").length() > 1) {
                    String mState = ((JSONArray) jsonObject.get("results")).getJSONObject(0)
                            .getJSONArray("address_components").getJSONObject(1)
                            .getString("long_name");
                    ;
                    mEditor.putString(Preference.Pref_State, mState).commit();
                }
                if (((JSONArray) jsonObject.get("results")).getJSONObject(0)
                        .getJSONArray("address_components").length() > 2) {
                    String mCountry = ((JSONArray) jsonObject.get("results")).getJSONObject(0)
                            .getJSONArray("address_components").getJSONObject(2)
                            .getString("long_name");

                    mEditor.putString(Preference.Pref_Country, mCountry).commit();
                }

//                Constants.latitude = String.valueOf(latitude);
//                Constants.longitude = String.valueOf(longitude);

//                Log.d("System out", "Constant.latitude " + mPreferences.getString("latitude2", ""));
//                Log.d("System out", "Constant.longitude " + mPreferences.getString("longitude2", ""));
            /*Log.d("System out", "City " + mCity);
            Log.d("System out", "Country " + mCountry);
            Log.d("System out", "State " + mState);*/
//                Log.d("System out", "Thread called...");

                Intent mIntent = new Intent(SelectLocationFragmentActivity.this, MainActivity.class);
                Constants.mStatic = 0;
                Constants.mFromSelectLocation = 1;

                mIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(mIntent);
                finish();
            } catch (JSONException e) {
                // Tracking exception
                MyTorismaApplication.getInstance().trackException(e);
                e.printStackTrace();
            }
        } catch (JSONException e) {
            // Tracking exception
            MyTorismaApplication.getInstance().trackException(e);
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        /*Intent intent = new Intent(SelectLocationFragmentActivity.this, YourLocationFragmentActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);*/
    }

    public JSONObject getLocationInfo(String address) {
//        Log.d("System out", "getLocationInfo");
        StringBuilder stringBuilder = new StringBuilder();
        try {
            address = address.replaceAll(" ", "%20");

            HttpPost httppost = new HttpPost("http://maps.google.com/maps/api/geocode/json?address=" + address + "&sensor=false");
            HttpClient client = new DefaultHttpClient();
            HttpResponse response;
            stringBuilder = new StringBuilder();

            response = client.execute(httppost);
            HttpEntity entity = response.getEntity();
            InputStream stream = entity.getContent();
            int b;
            while ((b = stream.read()) != -1) {
                stringBuilder.append((char) b);
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject = new JSONObject(stringBuilder.toString());
            getLatLong(jsonObject);
        } catch (JSONException e) {
            // Tracking exception
            MyTorismaApplication.getInstance().trackException(e);
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return jsonObject;
    }

    public boolean getLatLong(JSONObject jsonObject) {
//        Log.d("System out", "getLatLong");
        try {
            double longitude = ((JSONArray) jsonObject.get("results")).getJSONObject(0)
                    .getJSONObject("geometry").getJSONObject("location")
                    .getDouble("lng");

            double latitude = ((JSONArray) jsonObject.get("results")).getJSONObject(0)
                    .getJSONObject("geometry").getJSONObject("location")
                    .getDouble("lat");

            /*String mCity = ((JSONArray) jsonObject.get("results")).getJSONObject(0)
                    .getJSONArray("address_components").getJSONObject(0)
                    .getString("long_name");
            ;
            String mCountry = ((JSONArray) jsonObject.get("results")).getJSONObject(0)
                    .getJSONArray("address_components").getJSONObject(2)
                    .getString("long_name");
            ;
            String mState = ((JSONArray) jsonObject.get("results")).getJSONObject(0)
                    .getJSONArray("address_components").getJSONObject(3)
                    .getString("long_name");
            ;*/

//            mEditor.putString("mAddress", jsonObject.getString("formatted_address")).commit();

            mEditor.putString("latitude2", String.valueOf(latitude)).commit();
            mEditor.putString("longitude2", String.valueOf(longitude)).commit();

//            Log.d("System out", "Constant.latitude " + mPreferences.getString("latitude2", ""));
//            Log.d("System out", "Constant.longitude " + mPreferences.getString("longitude2", ""));
            /*Log.d("System out", "City " + mCity);
            Log.d("System out", "Country " + mCountry);
            Log.d("System out", "State " + mState);*/
//            Log.d("System out", "Thread called...");
            Constants.mFromSelectLocation = 1;

            Intent mIntent = new Intent(SelectLocationFragmentActivity.this, MainActivity.class);
            mIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(mIntent);
            finish();

        } catch (JSONException e) {
            // Tracking exception
            MyTorismaApplication.getInstance().trackException(e);
            return false;

        }

        return true;
    }

    private void turnGPSOn() {
        String provider = Settings.Secure.getString(getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);

        if (!provider.contains("gps")) { //if gps is disabled
            final Intent poke = new Intent();
            poke.setClassName("com.android.settings", "com.android.settings.widget.SettingsAppWidgetProvider");
            poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
            poke.setData(Uri.parse("3"));
            sendBroadcast(poke);
        }
    }

    private void turnGPSOff() {
        String provider = Settings.Secure.getString(getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);

        if (provider.contains("gps")) { //if gps is enabled
            final Intent poke = new Intent();
            poke.setClassName("com.android.settings", "com.android.settings.widget.SettingsAppWidgetProvider");
            poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
            poke.setData(Uri.parse("3"));
            sendBroadcast(poke);
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
            adapter.notifyDataSetChanged();
        }
    }

    class PlacesAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return resultList == null ? 0 : resultList.size();
        }

        @Override
        public Object getItem(int position) {
            return resultList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = getLayoutInflater().inflate(R.layout.list_item, parent, false);
            TextView textView = (TextView) view.findViewById(R.id.textView);
            textView.setText(resultList.get(position));
            return view;
        }
    }

    class GooglePlacesAutocompleteAdapter extends ArrayAdapter<String> implements Filterable {
        private ArrayList<String> resultList = new ArrayList<>();

        public GooglePlacesAutocompleteAdapter(Context context, int textViewResourceId) {
            super(context, textViewResourceId);
        }

        @Override
        public int getCount() {
            return resultList.size();
        }

        @Override
        public String getItem(int index) {
            return resultList.get(index);
        }

        @Override
        public Filter getFilter() {
            Filter filter = new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    FilterResults filterResults = new FilterResults();
                    if (constraint != null) {
                        // Retrieve the autocomplete results.
                        resultList = autocomplete(constraint.toString());

                        // Assign the data to the FilterResults
                        filterResults.values = resultList;
                        filterResults.count = resultList.size();
                    }
                    return filterResults;
                }

                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {
                    if (results != null && results.count > 0) {
                        notifyDataSetChanged();
                    } else {
                        notifyDataSetInvalidated();
                    }
                }
            };
            return filter;
        }
    }

    private class Connection extends AsyncTask {

        String address;

        public Connection(String address) {
            this.address = address;
        }

        @Override
        protected Object doInBackground(Object... arg0) {
            getLocationInfo(address);
            return null;
        }

    }
}
