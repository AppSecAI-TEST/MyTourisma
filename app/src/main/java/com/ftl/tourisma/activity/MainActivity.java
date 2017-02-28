package com.ftl.tourisma.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.estimote.sdk.SystemRequirementsChecker;
import com.ftl.tourisma.BeaconsActivity;
import com.ftl.tourisma.LoginFragmentActivity;
import com.ftl.tourisma.R;
import com.ftl.tourisma.SearchResultPlaceDetailsActivity;
import com.ftl.tourisma.SignupFragmentActivity;
import com.ftl.tourisma.custom_views.NormalTextView;
import com.ftl.tourisma.minterface.MyInterface;
import com.ftl.tourisma.postsync.PostSync;
import com.ftl.tourisma.postsync.post_sync;
import com.ftl.tourisma.utils.CommonClass;
import com.ftl.tourisma.utils.Constants;
import com.ftl.tourisma.utils.GPSTracker;
import com.ftl.tourisma.utils.Preference;
import com.ftl.tourisma.utils.Utilities;
import com.ftl.tourisma.utils.Utils;
import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.SnackbarManager;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;

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
import java.security.cert.PKIXCertPathChecker;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

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
public class MainActivity extends FragmentActivity implements OnClickListener, MyInterface, post_sync.ResponseHandler {

    private static final String TAG = "YourLocationFragmentA_";
    private Object object;
    boolean doubleBackToExitPressedOnce = false;
    private SharedPreferences mPreferences;
    private LinearLayout llYourLocationToast, llBeaconToast;
    private Handler handler = new Handler();
    private Handler handlerBeaconToast = new Handler();
    private Runnable runnable;
    private NormalTextView txt_snack_msg, tv_login_snack, tv_sign_up_snack, tv_snack_msg;
    private LinearLayout ll_login_snack, ll_sign_up_snack;
    private SharedPreferences.Editor mEditor;
    private GPSTracker gpsTracker;

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
    private SearchResultPlaceDetailsFragment searchResultPlaceDetailsFragment;
    public MainTabHostFragment mainTabHostFragment;
    private FragmentManager fragmentManager;
    public MyProfileFragment1 myProfileFragment;
    public int height;
    public int width;
    private ImageLoader imageLoader;
    private DisplayImageOptions options;
    private DisplayImageOptions optionsSimple;

    private void beaconsToast(final String msg, final String msgBeacon, final String img, final String placeId, final int isCloseApproach, final String isClosePromo) {
        if (msg != null && !msg.equals("")) {

            txt_snack_msg.setText(msg);
            txt_snack_msg.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isClosePromo.equals("1")) {
                        Intent intent = new Intent(MainActivity.this, BeaconsActivity.class);
                        intent.putExtra(PLACE_ID, placeId);
                        intent.putExtra(PLACE_IMAGE, img);
                        intent.putExtra(BEACON_MESSAGE, msgBeacon);
                        startActivity(intent);
                    } else {
                        Intent intent = new Intent(MainActivity.this, SearchResultPlaceDetailsActivity.class);
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

    private Bundle bundle;
    private Bundle bundleBeaconFromNotification;
    private View viewFav, viewHome, viewProfile;
    private ImageView ll_favorite_footer1, ll_home_footer1, ll_profile_footer1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        setContentView(R.layout.activity_home);

        bundle = getIntent().getBundleExtra("beaconView");
        bundleBeaconFromNotification = getIntent().getBundleExtra("beacon");

        mPreferences = getSharedPreferences(Constants.mPref, 0);
        mEditor = mPreferences.edit();
        initialisation();
        replaceMainFragment(null);

        if (Constants.mFromSelectLocation == 0) {
            //  Constants.mFromSelectLocation=0;
            getLocationOfAddress();
        }

        if (bundle != null) {

            if (bundle.getString("from") != null && bundle.getString("from").equals("beacon")) {
                Constants.placeId = bundle.getString(PLACE_ID);
//                Intent intent = new Intent(MainActivity.this, SearchResultPlaceDetailsActivity.class);
//                intent.putExtra("placeId", Constants.placeId);
//                startActivity(intent);

                //  addPlaceDetailFragment(Constants.placeId);

            }

        } else if (bundleBeaconFromNotification != null) {
            if (bundleBeaconFromNotification.getString(PLACE_ID) != null && !bundleBeaconFromNotification.getString(PLACE_ID).equals("")) {
                if (bundleBeaconFromNotification.getString(BEACON_IS_CLOSE_PROMO).equals("1")) {
                    Intent intent = new Intent(MainActivity.this, BeaconsActivity.class);
                    intent.putExtra(PLACE_ID, bundleBeaconFromNotification.getString(PLACE_ID));
                    intent.putExtra(PLACE_IMAGE, bundleBeaconFromNotification.getString(PLACE_IMAGE));
                    intent.putExtra(BEACON_MESSAGE, bundleBeaconFromNotification.getString(BEACON_MESSAGE));
                    startActivity(intent);
                } else {
                    Constants.placeId = bundleBeaconFromNotification.getString(PLACE_ID);
                    Intent intent = new Intent(MainActivity.this, SearchResultPlaceDetailsActivity.class);
                    intent.putExtra("placeId", Constants.placeId);
                    intent.putExtra("location", "");
                    startActivity(intent);
//                    addPlaceDetailFragment(Constants.placeId);
                }
            }

//        startService(new Intent(this, MyBeaconsService.class));
        } else {
            callApiForGetBeacons();
        }
        setActiviateIcon(0);
    }

    private void addPlaceDetailFragment(String placeId) {
        Bundle bundle = new Bundle();
        bundle.putString("placeId", placeId);
        searchResultPlaceDetailsFragment = new SearchResultPlaceDetailsFragment();
        addFragment(searchResultPlaceDetailsFragment, true, bundle);
    }

    public void replaceMainFragment(Bundle bundle) {
        mainTabHostFragment = new MainTabHostFragment();
        mainTabHostFragment.setArguments(bundle);
//        addFragment(mainTabHostFragment, true, bundle);
//        SelectLocationFragment searchResultFragmentFragment = new SelectLocationFragment();
//        replaceFragment(searchResultFragmentFragment, true, null);
        fragmentManager.beginTransaction().replace(R.id.frame_container, mainTabHostFragment, MainTabHostFragment.class.getSimpleName()).commit();

    }

    private void callApiForGetBeacons() {
        if (CommonClass.hasInternetConnection(this)) {
            String url = Constants.SERVER_URL + "json.php?action=Beacons";
            String json = "[{\"Lan_Id\":\"" + mPreferences.getString("Lan_Id", "") + "\"}]";

            new PostSync(MainActivity.this, "Beacons", MainActivity.this).execute(url, json);
        } else {
            SnackbarManager.show(Snackbar.with(this).color(Utilities.getColor(this, R.color.mBlue)).text("No Internet connection!"));
        }
    }

    public void addFragment(Fragment fragment, boolean addToBackStack, Bundle bundle) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
//        transaction.setCustomAnimations(R.anim.left_in_fragment, R.anim.left_out_fragment, R.anim.right_in_fragment, R.anim.right_out_fragment);
        String backStateName = fragment.getClass().getName();
        if (addToBackStack) {
            transaction.addToBackStack(backStateName);
        }
        boolean fragmentPopped = fragmentManager.popBackStackImmediate(backStateName, 0);
        if (!fragmentPopped && fragmentManager.findFragmentByTag(backStateName) == null) {
            if (bundle != null) {
                fragment.setArguments(bundle);
            }
            transaction.add(R.id.frame_container, fragment, backStateName);
            transaction.commit();
        }
        if (bundle != null) {
            fragment.setArguments(bundle);
        }
    }

    public void addFragmentFromBottom(Fragment fragment, boolean addToBackStack, Bundle bundle) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
//        transaction.setCustomAnimations(R.anim.push_uppp_in, R.anim.push_up_out_no, 0, 0);

        String backStateName = fragment.getClass().getName();

        if (addToBackStack) {
            transaction.addToBackStack(backStateName);
        }
        boolean fragmentPopped = fragmentManager.popBackStackImmediate(backStateName, 0);
        if (!fragmentPopped && fragmentManager.findFragmentByTag(backStateName) == null) {
            if (bundle != null) {
                fragment.setArguments(bundle);
            }
            transaction.add(R.id.frame_container, fragment, backStateName);
            transaction.commit();
        }
        if (bundle != null) {
            fragment.setArguments(bundle);
        }
    }

    public void setActiviateIcon(int index) {

        switch (index) {
            case 0:
                viewProfile.setVisibility(View.GONE);
                viewFav.setVisibility(View.GONE);
                viewHome.setVisibility(View.VISIBLE);
                break;
            case 1:
                viewProfile.setVisibility(View.GONE);
                viewFav.setVisibility(View.VISIBLE);
                viewHome.setVisibility(View.GONE);
                break;
            case 2:
                viewProfile.setVisibility(View.VISIBLE);
                viewFav.setVisibility(View.GONE);
                viewHome.setVisibility(View.GONE);

                break;

        }
    }

    public void addFragmentZoom(Fragment fragment, boolean addToBackStack, Bundle bundle) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
//        transaction.setCustomAnimations(R.anim.zoom_in, R.anim.translate, R.anim.translate, R.anim.fade_out);

        String backStateName = fragment.getClass().getName();

        if (addToBackStack) {
            transaction.addToBackStack(backStateName);
        }
        boolean fragmentPopped = fragmentManager.popBackStackImmediate(backStateName, 0);
        if (!fragmentPopped && fragmentManager.findFragmentByTag(backStateName) == null) {
            if (bundle != null) {
                fragment.setArguments(bundle);
            }
            transaction.add(R.id.frame_container, fragment, backStateName);
            transaction.commit();
        }
        if (bundle != null) {
            fragment.setArguments(bundle);
        }
    }


    public void getLocationOfAddress() {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        gpsTracker = new GPSTracker(this, true);
        if (CommonClass.hasInternetConnection(this)) {
            if (gpsTracker.getLatitude() == 0 || gpsTracker.getLongitude() == 0) {
                mEditor.putString("latitude1", "23.424076").commit();
                mEditor.putString("longitude1", "53.847818").commit();
                mEditor.putString("latitude2", "23.424076").commit();
                mEditor.putString("longitude2", "53.847818").commit();
                mPreferences.edit().putString(Preference.Pref_City, "United Arab Emirates").commit();

            } else {
                mEditor.putString("latitude1", String.valueOf(gpsTracker.getLatitude())).commit();
                mEditor.putString("longitude1", String.valueOf(gpsTracker.getLongitude())).commit();
                mEditor.putString("latitude2", String.valueOf(gpsTracker.getLatitude())).commit();
                mEditor.putString("longitude2", String.valueOf(gpsTracker.getLongitude())).commit();
                getGeoLocation();


//            Constants.latitude = String.valueOf(gpsTracker.getLatitude());
//            Constants.longitude = String.valueOf(gpsTracker.getLongitude());

                Log.d("System out", "Constant.latitude1 " + mPreferences.getString("latitude1", ""));
                Log.d("System out", "Constant.longitude1 " + mPreferences.getString("longitude1", ""));

            }
        } else {
            Intent intent = new Intent(getApplicationContext(), NoInternet.class);
            startActivity(intent);
            //SnackbarManager.show(Snackbar.with(this).color(getResources().getColor(R.color.mBlue)).text(Constants.showMessage(this, mPreferences.getString("Lan_Id", ""), "NOINTERNET")));
        }
    }

    private void initialisation() {

        fragmentManager = getSupportFragmentManager();
        llYourLocationToast = (LinearLayout) findViewById(R.id.llYourLocationToast);
        ll_login_snack = (LinearLayout) findViewById(R.id.ll_login_snack);
        ll_sign_up_snack = (LinearLayout) findViewById(R.id.ll_sign_up_snack);
        tv_login_snack = (NormalTextView) findViewById(R.id.tv_login_snack);
        llBeaconToast = (LinearLayout) findViewById(R.id.llBeaconToast);
        txt_snack_msg = (NormalTextView) findViewById(R.id.txt_snack_msg);
        tv_sign_up_snack = (NormalTextView) findViewById(R.id.tv_sign_up_snack);
        tv_snack_msg = (NormalTextView) findViewById(R.id.tv_snack_msg);
        ll_sign_up_snack.setOnClickListener(this);
        ll_login_snack.setOnClickListener(this);

        ll_favorite_footer1 = (ImageView) findViewById(R.id.ll_favorite_footer1);
        ll_home_footer1 = (ImageView) findViewById(R.id.ll_home_footer1);
        ll_profile_footer1 = (ImageView) findViewById(R.id.ll_profile_footer1);
        viewHome = (View) findViewById(R.id.viewHome);
        viewFav = (View) findViewById(R.id.viewFav);
        viewProfile = (View) findViewById(R.id.viewProfile);

        ll_favorite_footer1.setOnClickListener(this);
        ll_profile_footer1.setOnClickListener(this);
        ll_home_footer1.setOnClickListener(this);


        try {
            DisplayMetrics displaymetrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
            height = displaymetrics.heightPixels;
            width = displaymetrics.widthPixels;
        } catch (Exception e) {
            e.printStackTrace();
        }

        mPreferences = this.getSharedPreferences(Constants.mPref, 0);
        mEditor = mPreferences.edit();
/*
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this)
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .denyCacheImageMultipleSizesInMemory()
                .diskCacheFileNameGenerator(new Md5FileNameGenerator())
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                .build();*/

        imageLoader = ImageLoader.getInstance();

        imageLoader.init(ImageLoaderConfiguration.createDefault(this));
        options = new DisplayImageOptions.Builder()
                .showImageForEmptyUri(null)
                .displayer(new RoundedBitmapDisplayer(10))
                .showImageOnFail(null).cacheInMemory(true)
                .cacheOnDisk(true).build();
        optionsSimple = new DisplayImageOptions.Builder()
                .showImageForEmptyUri(null)
                .displayer(new SimpleBitmapDisplayer())
                .showImageOnFail(null).cacheInMemory(true)
                .cacheOnDisk(true).build();

    }

    public SharedPreferences getPreferences() {
        if(mPreferences==null){
            mPreferences = this.getSharedPreferences(Constants.mPref, 0);
        }
        return mPreferences;
    }

    @Override
    protected void onResume() {
        super.onResume();
        SystemRequirementsChecker.checkWithDefaultDialogs(this);
        LocalBroadcastManager.getInstance(this).registerReceiver(
                broadcastReceiver,
                new IntentFilter(BROADCAST_BEACON));
//        ServiceProxy.bindService(this, mConnection);
//        ServiceProxy.stopMonitoringForRegion(this, mMyUuid);
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            unregisterReceiver(broadcastReceiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {


        switch (v.getId()) {
            case R.id.ll_favorite_footer1:
                //  dialogFragment.dismiss();
                if (mPreferences.getString("User_Id", "").equalsIgnoreCase("0")) {
                    showGuestSnackToast();
                } else {
                    if (mainTabHostFragment != null) {
                        mainTabHostFragment.vpFragment.setCurrentItem(1);
                    }
                }
                break;
            case R.id.ll_profile_footer1:
                // dialogFragment.dismiss();

                if (mainTabHostFragment != null)
                    mainTabHostFragment.vpFragment.setCurrentItem(2);

                break;
            case R.id.ll_home_footer1:
                // dialogFragment.dismiss();

                if (mainTabHostFragment != null)
                    mainTabHostFragment.vpFragment.setCurrentItem(0);
                break;
            case R.id.ll_sign_up_snack:

                mPreferences.edit().putString("User_id", "").commit();
                Intent mIntent = new Intent(MainActivity.this, SignupFragmentActivity.class);
                startActivity(mIntent);
                finish();

                break;
            case R.id.ll_login_snack:

                mPreferences.edit().putString("User_id", "").commit();
                Intent mIntent1 = new Intent(MainActivity.this, LoginFragmentActivity.class);
                startActivity(mIntent1);
                finish();
                break;

        }
    }


    @Override
    public void backToHome(Object object) {
//        this.object = object;
//        Log.d("System out", "object " + object.toString());
    }

    @Override
    public void showGuestSnackToast() {
        guestSnackToast();
    }

    public void getGeoLocation() {
        try {
            Geocoder geo = new Geocoder(this.getApplicationContext(), Locale.getDefault());
            List<Address> addresses = geo.getFromLocation(Double.parseDouble(mPreferences.getString("latitude2", "")), Double.parseDouble(mPreferences.getString("longitude2", "")), 1);
            if (addresses.isEmpty()) {
            } else {
                if (addresses.size() > 0) {
                    String str = (addresses.get(0).getAddressLine(1) + ", " + addresses.get(0).getLocality() + ", "
                            + addresses.get(0).getAdminArea() + ", " + addresses.get(0).getCountryName());

                    String umAddress1 = addresses.get(0).getAddressLine(0);
                    String city = addresses.get(0).getLocality();
                    mEditor.putString(Preference.Pref_City, city).commit();
                    String state = addresses.get(0).getAdminArea();
                    mEditor.putString(Preference.Pref_State, state).commit();
                    String zipCode = addresses.get(0).getPostalCode();
                    String country = addresses.get(0).getCountryName();
                    mEditor.putString(Preference.Pref_Country, country).commit();

                    String addressmaps = str.replaceAll(" null,", "");
                    // flag = 0;
                    //  etSearch.setHint(addressmaps);

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
    }

    ExploreNearbyFragment exploreNearbyFragment;
    FavouriteMainFragment favouriteFragment;
    MyProfileFragment1 profileFragment;

    /*
    Managing response of Bracon api
     */
    public void getBeaconsResponse(String resultString) {
        if (resultString != null && resultString.length() > 2) {
            try {
                Set<String> set = new HashSet<String>();
                ArrayList<String> beaconsArrayList = new ArrayList<>();

                JSONArray jsonArray = new JSONArray(resultString);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.optJSONObject(i);
                    beaconsArrayList.add(jsonObject.toString());
                }
                set.addAll(beaconsArrayList);
                Preference.setStringSetPrefs("keyBeacons", this, set);
                //scoreEditor.commit();

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onResponse(String response, String action) {
        try {
            if (action.equalsIgnoreCase("Beacons")) {
                getBeaconsResponse(response);
            }
        } catch (Exception e) {
            Log.e(TAG, "onResponse Exception " + e.getLocalizedMessage());
        }
    }

   /* public void replaceSearchResultFragment(ArrayList<Nearby> nearbies1, String search) {
        SearchResultFragmentFragment searchResultFragmentFragment = SearchResultFragmentFragment.NewInstance(nearbies1, search);
//        replaceFragment(searchResultFragmentFragment, true, null);
        fragmentManager.beginTransaction().replace(R.id.frame_container, searchResultFragmentFragment, SearchResultFragmentFragment.class.getSimpleName()).addToBackStack(SearchResultFragmentFragment.class.getSimpleName()).commit();
    }*/

    public void popbackStackFragment() {
        try {
            fragmentManager.popBackStack();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void replaceFragment(Fragment fragment, boolean
            addToBackStack, Bundle bundle) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
//        transaction.setCustomAnimations(R.anim.left_in_fragment, R.anim.left_out_fragment, R.anim.right_in_fragment, R.anim.right_out_fragment);
        String backStateName = fragment.getClass().getName();
        if (addToBackStack) {
            transaction.addToBackStack(backStateName);
        }

        boolean fragmentPopped = fragmentManager.popBackStackImmediate(backStateName, 0);
        if (!fragmentPopped && fragmentManager.findFragmentByTag(backStateName) == null) {
            if (bundle != null) {
                fragment.setArguments(bundle);
            }
            transaction.replace(R.id.frame_container, fragment, backStateName);
            transaction.commit();
        }
    }


    public class PagerAdapter extends FragmentPagerAdapter {

        private List<Fragment> fragments;

        public PagerAdapter(FragmentManager fm, List<Fragment> fragments) {
            super(fm);
            this.fragments = fragments;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    //if (exploreNearbyFragment == null)
                    exploreNearbyFragment = new ExploreNearbyFragment();
                    // else
                    return exploreNearbyFragment;
                //  break;
                case 1:
                    // if (favouriteFragment == null)
                    favouriteFragment = new FavouriteMainFragment();
                    //else
                    return favouriteFragment;
                //  break;
                case 2:
                    //if (profileFragment == null)
                    profileFragment = new MyProfileFragment1();
                    //else
                    return profileFragment;
                // break;
            }
            return this.fragments.get(position);
        }

        @Override
        public int getCount() {
            return this.fragments.size();
        }

      /*  @Override
        public int getItemPosition(Object object) {
//            if (object instanceof FavouriteFragment) {
//                ((FavouriteFragment) object).update();
//            } else if (object instanceof ExploreNearbyFragment) {
//                ((ExploreNearbyFragment) object).update();
//            }
            //don't return POSITION_NONE, avoid fragment recreation.
            return super.getItemPosition(object);
        }*/
    }

    @Override
    public void onBackPressed() {
        if (mainTabHostFragment.vpFragment.getCurrentItem() == 2) {
            if (mPreferences.getString("User_Id", "").equalsIgnoreCase("0")) {
                mainTabHostFragment.vpFragment.setCurrentItem(0);
            } else {
                mainTabHostFragment.vpFragment.setCurrentItem(1);
            }
        } else if (mainTabHostFragment.vpFragment.getCurrentItem() == 1) {

            FragmentManager fm = getSupportFragmentManager();
            if (fm.getBackStackEntryCount() == 0) {
                mainTabHostFragment.vpFragment.setCurrentItem(0);

            } else {
                super.onBackPressed();
            }

        } else {
            if (mainTabHostFragment.vpFragment.getCurrentItem() == 0) {

            }
            FragmentManager fm = getSupportFragmentManager();
            if (fm.getBackStackEntryCount() == 0) {
                if (doubleBackToExitPressedOnce) {
                    super.onBackPressed();
                    return;
                }

                this.doubleBackToExitPressedOnce = true;
                SnackbarManager.show(Snackbar.with(MainActivity.this).color(Utils.getColor(this, R.color.mBlue)).text(Constants.showMessage(MainActivity.this, mPreferences.getString("Lan_Id", ""), "Please click BACK again to exit")));

                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        doubleBackToExitPressedOnce = false;
                    }
                }, 2000);
                //onCloseApp("Are you sure you want to close the App?");
            } else {
                //for(int i = 0; i < fm.getBackStackEntryCount(); ++i) {
//                    fm.popBackStack();
                // }
                super.onBackPressed();
            }
            /*if (object != null) {
                Constants.mStaticNearCall = 0;
                ((ExploreNearbyFragment1) object).update();
                object = null;
//                Log.d("System out", "home.....");
            } else {
                if (doubleBackToExitPressedOnce) {
                    super.onBackPressed();
                    return;
                }

                this.doubleBackToExitPressedOnce = true;
                SnackbarManager.show(Snackbar.with(MainActivity.this).color(Utils.getColor(this, R.color.mBlue)).text(Constants.showMessage(MainActivity.this, mPreferences.getString("Lan_Id", ""), "Please click BACK again to exit")));

                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        doubleBackToExitPressedOnce = false;
                    }
                }, 2000);
            }*/
        }
    }

    public void guestSnackToast() {

        tv_login_snack.setText(Constants.showMessage(MainActivity.this, mPreferences.getString("Lan_Id", ""), "Login"));
        tv_sign_up_snack.setText(Constants.showMessage(MainActivity.this, mPreferences.getString("Lan_Id", ""), "SignUp"));
        tv_snack_msg.setText(Constants.showMessage(MainActivity.this, mPreferences.getString("Lan_Id", ""), "GetStarted"));

        runnable = new Runnable() {
            @Override
            public void run() {
                llYourLocationToast.setVisibility(View.GONE);
            }
        };
        llYourLocationToast.setVisibility(View.VISIBLE);
        handler.postDelayed(runnable, 4000);
    }

    private void beaconsToast(final String msg, final String msgBeacon, final String img, final String placeId, final boolean isClickable, final int isCloseApproach) {

        if (msg != null && !msg.equals("")) {
            txt_snack_msg.setText(msg);
            //   if (isClickable) {
            txt_snack_msg.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isCloseApproach == 1) {
                        Intent intent = new Intent(MainActivity.this, BeaconsActivity.class);
                        intent.putExtra(PLACE_ID, placeId);
                        intent.putExtra(PLACE_IMAGE, img);
                        intent.putExtra(BEACON_MESSAGE, msgBeacon);
                        startActivity(intent);
                    } else {
                        Intent intent = new Intent(MainActivity.this, SearchResultPlaceDetailsActivity.class);
                        intent.putExtra("placeId", placeId);
                        startActivity(intent);
                    }

                }
            });
            //   } else {
            //       txt_snack_msg.setOnClickListener(null);
            //   }

            runnable = new Runnable() {
                @Override
                public void run() {
                    llBeaconToast.setVisibility(View.GONE);
                }
            };
            llBeaconToast.setVisibility(View.VISIBLE);
            handlerBeaconToast.postDelayed(runnable, 5000);
        }
    }
}
