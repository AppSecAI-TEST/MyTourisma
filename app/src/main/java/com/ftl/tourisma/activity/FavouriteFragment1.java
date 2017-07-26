package com.ftl.tourisma.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.daimajia.slider.library.Indicators.PagerIndicator;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.Tricks.ViewPagerEx;
import com.ftl.tourisma.MapDetailFragment;
import com.ftl.tourisma.MyTorismaApplication;
import com.ftl.tourisma.OnInfoWindowElemTouchListener;
import com.ftl.tourisma.R;
import com.ftl.tourisma.custom_views.NormalBoldTextView;
import com.ftl.tourisma.custom_views.NormalTextView;
import com.ftl.tourisma.database.Nearby;
import com.ftl.tourisma.minterface.MapFragmentInterface;
import com.ftl.tourisma.minterface.Updatable;
import com.ftl.tourisma.models.HourDetails;
import com.ftl.tourisma.postsync.post_sync;
import com.ftl.tourisma.utils.CommonClass;
import com.ftl.tourisma.utils.Constants;
import com.ftl.tourisma.utils.JSONObjConverter;
import com.ftl.tourisma.utils.Preference;
import com.ftl.tourisma.utils.Utilities;
import com.ftl.tourisma.utils.Utils;
import com.github.clans.fab.FloatingActionButton;
import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.SnackbarManager;
import com.nostra13.universalimageloader.cache.disc.naming.FileNameGenerator;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.display.BitmapDisplayer;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;
import com.pixplicity.easyprefs.library.Prefs;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.ftl.tourisma.utils.Constants.PlaceClosed;
import static com.ftl.tourisma.utils.Constants.PlaceOpenFor24Hours;
import static com.ftl.tourisma.utils.Constants.PlaceOpenWithAnyTime;

public class FavouriteFragment1 extends Fragment implements View.OnClickListener, Updatable, post_sync.ResponseHandler, ViewPagerEx.OnPageChangeListener {

    private static final int PLACE_LIKE_FAV = 1002;
    private static final String TAG = "FavouriteFragment";
    static int mCounter = -1;
    public MainTabHostFragment mainTabHostFragment;
    MainActivity mainActivity;
    //Broadcast Receiver page refreshing functionality
    MyReceiver receiver;
    private LinearLayout llYourLocationToast;
    private FloatingActionButton fb_favorite;
    private RecyclerView rv_favorite;
    private MapFragmentInterface mapFragmentInterface;
    private SharedPreferences mPreferences;
    private SharedPreferences.Editor mEditor;
    private ArrayList<Nearby> nearbies = new ArrayList();
    private Nearby nearby;
    private DisplayImageOptions options, optionsSimple;
    private ImageLoader imageLoader = ImageLoader.getInstance();
    private int width, height, mFlag = 0;
    private RecyclerAdapter recyclerAdapter;
    private SimpleDateFormat _24HourSDF, _12HourSDF;
    private String _24HourTime, _24HourTime1;
    private Date _24HourDt, _24HourDt1;
    private LinearLayout dotLayout_detail_my_favorite3, llEmptyLayout, ll_see_all_my_favorite3, ll_my_favourite1, ll_my_favorite3;
    private int pos = 0;
    private OnInfoWindowElemTouchListener infoButtonListener;
    private ArrayList<Nearby> nearbies1 = new ArrayList();
    private NormalTextView tv_map_location, txtSuggest, tv_my_favorite_list, tv_your_location_header5, tv_about_place_favorite, txt_add_to_fav, tv_similar_favorite, tv_see_all_favorite, tv_total_favorite, txtDailyWorkingHours, txtOpenNowVal, tv_info_my_favorite3, tv_discription_my_favorite3, tv_distance1_my_favorite3, txtStartNavigating;
    private NormalBoldTextView tv_full_name_my_favorite3;
    private com.ftl.tourisma.gallery1.Gallery gv_detail1_my_favorite3;
    private String[] strImg1;
    private String Category_Name, Category_Id;
    private int id;
    private ImageView[] mDotsText1;
    private ImageView iv_back5, iv_search_favorite, iv_back_favorite, iv_search_map;
    private Handler mHandler;
    private Runnable mRunnable;
    private Nearby mNearby = new Nearby();
    private Nearby nearByDetails;
    private Dialog dialog;
    private ArrayList<HourDetails> hourDetailses;
    private View view;
    private JSONObjConverter jsonObjConverter;
    private SliderLayout sliderPlaceImages;
    private PagerIndicator custom_indicator1;
    private ImageView iv_back3;

    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPreferences = getActivity().getSharedPreferences(Constants.mPref, 0);
        mEditor = mPreferences.edit();
        jsonObjConverter = new JSONObjConverter();
        _24HourSDF = new SimpleDateFormat("HH:mm");
        _12HourSDF = new SimpleDateFormat("hh:mma");
        try {
            DisplayMetrics displaymetrics = new DisplayMetrics();
            getActivity().getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
            height = displaymetrics.heightPixels;
            width = displaymetrics.widthPixels;
        } catch (Exception e) {// Tracking exception
            MyTorismaApplication.getInstance().trackException(e);

            e.printStackTrace();
        }

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getActivity()).threadPriority(3).denyCacheImageMultipleSizesInMemory().diskCacheFileNameGenerator((FileNameGenerator) new Md5FileNameGenerator()).tasksProcessingOrder(QueueProcessingType.LIFO).build();
        imageLoader.init(ImageLoaderConfiguration.createDefault(getActivity()));
        optionsSimple = new DisplayImageOptions.Builder()
                .showImageForEmptyUri(null)
                .displayer(new SimpleBitmapDisplayer())
                .showImageOnFail(null).cacheInMemory(true)
                .cacheOnDisk(true).build();
        options = new DisplayImageOptions.Builder().showImageForEmptyUri(null).displayer((BitmapDisplayer) new RoundedBitmapDisplayer(10)).showImageOnFail(null).cacheInMemory(true).cacheOnDisk(true).build();
        ImageLoader.getInstance().init(config);
    }

    @Nullable
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.my_favourite, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
        getFavoriteCall();
    }

    private void initView() {
        iv_search_map = (ImageView) view.findViewById(R.id.iv_search_map);
        iv_search_map.setOnClickListener(this);
        txtSuggest = (NormalTextView) view.findViewById(R.id.txtSuggest);
        txtSuggest.setText(Constants.showMessage(getActivity(), mPreferences.getString("Lan_Id", ""), "Suggest Location"));
        txtSuggest.setOnClickListener(this);
        tv_my_favorite_list = (NormalTextView) view.findViewById(R.id.tv_my_favorite_list);
        tv_my_favorite_list.setText(Constants.showMessage(getActivity(), mPreferences.getString("Lan_Id", ""), "myfavourittitle"));
        txtStartNavigating = (NormalTextView) view.findViewById(R.id.txtStartNavigating);
        txtStartNavigating.setText(Constants.showMessage(getActivity(), mPreferences.getString("Lan_Id", ""), "StartNavigation"));
        tv_about_place_favorite = (NormalTextView) view.findViewById(R.id.tv_about_place_explore);
        tv_about_place_favorite.setText(Constants.showMessage(getActivity(), mPreferences.getString("Lan_Id", ""), "aboutplace"));
        tv_similar_favorite = (NormalTextView) view.findViewById(R.id.tv_similar_explore);
        tv_similar_favorite.setText(Constants.showMessage(getActivity(), mPreferences.getString("Lan_Id", ""), "similarplace"));
        tv_see_all_favorite = (NormalTextView) view.findViewById(R.id.tv_see_all_explore);
        tv_see_all_favorite.setText(Constants.showMessage(getActivity(), mPreferences.getString("Lan_Id", ""), "seeall"));
        NormalTextView txtMessage = (NormalTextView) view.findViewById(R.id.txtMessage);
        NormalTextView txtOk = (NormalTextView) view.findViewById(R.id.txtOk);
        txtOk.setVisibility(View.GONE);
        llEmptyLayout = (LinearLayout) view.findViewById(R.id.llEmptyLayout);
        txtMessage.setText(Constants.showMessage(getActivity(), mPreferences.getString("Lan_Id", ""), "No favourites found"));
        ll_see_all_my_favorite3 = (LinearLayout) view.findViewById(R.id.ll_see_all);
        ll_see_all_my_favorite3.setOnClickListener(this);
        iv_back_favorite = (ImageView) view.findViewById(R.id.iv_back_favorite);
        iv_back_favorite.setOnClickListener(this);
        dotLayout_detail_my_favorite3 = (LinearLayout) view.findViewById(R.id.dotLayout_detail);
        tv_total_favorite = (NormalTextView) view.findViewById(R.id.tv_total_favorite);
        tv_total_favorite.setText(Constants.showMessage(getActivity(), mPreferences.getString("Lan_Id", ""), "searchresuilt"));
        tv_your_location_header5 = (NormalTextView) view.findViewById(R.id.tv_your_location_header5);
        tv_your_location_header5.setText(Constants.showMessage(getActivity(), mPreferences.getString("Lan_Id", ""), "locationtitle"));
        iv_search_favorite = (ImageView) view.findViewById(R.id.iv_search_favorite);
        iv_search_favorite.setOnClickListener(this);
        tv_map_location = (NormalTextView) view.findViewById(R.id.tv_map_location);
        if (mPreferences.getString(Preference.Pref_City, "").equalsIgnoreCase("")) {
            tv_map_location.setText(mPreferences.getString(Preference.Pref_Country, ""));
        } else {
            tv_map_location.setText(mPreferences.getString(Preference.Pref_City, ""));
        }
        fb_favorite = (FloatingActionButton) view.findViewById(R.id.fb_favorite);
        fb_favorite.setOnClickListener(this);
        rv_favorite = (RecyclerView) view.findViewById(R.id.rv_favorite);
        rv_favorite.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        rv_favorite.setLayoutManager(layoutManager);
        ll_my_favourite1 = (LinearLayout) view.findViewById(R.id.ll_my_favorite1);
        ll_my_favorite3 = (LinearLayout) view.findViewById(R.id.ll_my_favorite3);
        tv_full_name_my_favorite3 = (NormalBoldTextView) view.findViewById(R.id.tv_full_name);
        tv_info_my_favorite3 = (NormalTextView) view.findViewById(R.id.tv_info);
        txtOpenNowVal = (NormalTextView) view.findViewById(R.id.txtOpenNowVal);
        txtDailyWorkingHours = (NormalTextView) view.findViewById(R.id.txtDailyWorkingHours);
        txtDailyWorkingHours.setText(Constants.showMessage(getActivity(), mPreferences.getString("Lan_Id", ""), "View Daily Working Hours"));
        txtDailyWorkingHours.setOnClickListener(this);
        tv_distance1_my_favorite3 = (NormalTextView) view.findViewById(R.id.tv_distance1);
        tv_discription_my_favorite3 = (NormalTextView) view.findViewById(R.id.tv_discription);
        txt_add_to_fav = (NormalTextView) view.findViewById(R.id.txt_add_to_fav);
        gv_detail1_my_favorite3 = (com.ftl.tourisma.gallery1.Gallery) view.findViewById(R.id.gv_detail1);
        iv_back5 = (ImageView) view.findViewById(R.id.iv_back5);
        iv_back5.setOnClickListener(this);
        custom_indicator1 = (PagerIndicator) view.findViewById(R.id.custom_indicator1);
        custom_indicator1.setIndicatorStyleResource(R.drawable.shape_cirlce_fill, R.drawable.shape_cirlce_unfill);
    }

    private void getFavoriteCall() {
        if (CommonClass.hasInternetConnection(getActivity())) {
            String url = Constants.SERVER_URL + "json.php?action=GetFavorites";
            String json = "[{\"User_Id\":\"" + mPreferences.getString("User_Id", "") + "\",\"Current_Latitude\":\"" + mPreferences.getString("latitude2", "") + "\",\"Current_Longitude\":\"" + mPreferences.getString("longitude2", "") + "\",\"Lan_Id\":\"" + mPreferences.getString("Lan_Id", "") + "\"}]";
            System.out.println("fav_response" + json);
            new post_sync(getActivity(), "GetFavorites", FavouriteFragment1.this, true).execute(url, json);
        } else {
            Intent intent = new Intent(getActivity(), NoInternet.class);
            startActivity(intent);
        }
    }

    @Override
    public void onResponse(String response, String action) {
        try {
            if (action.equalsIgnoreCase("GetFavorites")) {
                getFavoriteResponse(response);
            } else if (action.equalsIgnoreCase("AddFavorite1")) {
                addFavoriteResponse(response);
            } else if (action.equalsIgnoreCase("DeleteFavorite1")) {
                deleteFavoriteResponse(response);
            }
        } catch (Exception e) {
            // Tracking exception
            MyTorismaApplication.getInstance().trackException(e);
            Log.e(TAG, "onResponse Exception " + e.getLocalizedMessage());
        }
    }

    public void onClick(View v) {
        if (v == fb_favorite) {
            Intent mIntent = new Intent(getActivity(), (Class) MapDetailFragment.class);
            mIntent.putExtra("nearbies", nearbies);
            if (nearbies.size() == 1) {
                mIntent.putExtra("title", Constants.showMessage(getActivity(), mPreferences.getString("Lan_Id", ""), "Spotone"));
            } else {
                mIntent.putExtra("title", Constants.showMessage(getActivity(), mPreferences.getString("Lan_Id", ""), "spotmultipale"));
            }
            startActivity(mIntent);
        } else if (v == iv_back5) {
            ll_my_favourite1.setVisibility(View.VISIBLE);
            ll_my_favorite3.setVisibility(View.GONE);
            update();
        } else if (v == iv_search_favorite || v == iv_search_map) {
            mainActivity.favouriteFragment.replaceSearchFragment();

        } else if (v == iv_back_favorite) {
            //TODO
        } else if (v == ll_see_all_my_favorite3) {
            mainActivity.favouriteFragment.replaceSearchResultFragment(nearbies1, "Similar Places");
        } else if (v == txtSuggest) {
            try {
                Intent emailIntent = new Intent(Intent.ACTION_SEND);
                emailIntent.setData(Uri.parse("info@mytourisma.com"));
                emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{"info@mytourisma.com"});
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "myTourisma - Suggest new location");
                emailIntent.putExtra(Intent.EXTRA_TEXT, "Hello,\n" +
                        "\n" +
                        "\n" +
                        "I would like to suggest a new location for myTourisma app\n" +
                        "\n" +
                        "Location name:\n" +
                        "Location:\n" +
                        "\n" +
                        "\n" +
                        "\n" +
                        "Thank you");
                emailIntent.setType("text/plain");
                final PackageManager pm = getActivity().getPackageManager();
                final List<ResolveInfo> matches = pm.queryIntentActivities(emailIntent, 0);
                ResolveInfo best = null;
                for (final ResolveInfo info : matches)
                    if (info.activityInfo.packageName.endsWith(".gm") || info.activityInfo.name.toLowerCase().contains("gmail"))
                        best = info;
                if (best != null)
                    emailIntent.setClassName(best.activityInfo.packageName, best.activityInfo.name);
                getActivity().startActivity(emailIntent);
            } catch (Exception e) {
                // Tracking exception
                MyTorismaApplication.getInstance().trackException(e);
                Utils.Log(TAG, "suggestPlace Exception: " + e.getLocalizedMessage());
            }
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        Log.d(TAG, "FragmentVisible " + isVisibleToUser);
        if (isVisibleToUser) {
            getFavoriteCall();
        }
    }

    public void onAttach(Context activity) {
        super.onAttach(activity);
        mainActivity = (MainActivity) getActivity();
    }

    public void update() {
        if (mPreferences.getString("User_Id", "").equalsIgnoreCase("0")) {
            llEmptyLayout.setVisibility(View.VISIBLE);
            rv_favorite.setVisibility(View.GONE);
        } else {
            getFavoriteCall();
        }
    }

    public void getFavoriteResponse(String resultString) {
        nearbies = new ArrayList<>();
        if (resultString.length() > 2 && resultString.length() > 2) {
            try {
                JSONArray jsonArray = new JSONArray(resultString);
                for (int i = 0; i < jsonArray.length(); ++i) {
                    nearby = jsonObjConverter.convertJsonToNearByObj(jsonArray.optJSONObject(i));
                    nearby.setDistance(Utilities.GetRoutDistane(Double.parseDouble(mPreferences.getString("latitude2", "")), Double.parseDouble(mPreferences.getString("longitude2", "")), Double.parseDouble(nearby.getPlace_Latitude()), Double.parseDouble(nearby.getPlace_Longi()), nearby.getDist()));
                    nearbies.add(nearby);
                }
            } catch (JSONException e) {
                // Tracking exception
                MyTorismaApplication.getInstance().trackException(e);
                e.printStackTrace();
            }
            nearbies = Utilities.sortLocations(nearbies);
        }
        setFavList();
        Constants.mStaticFavCall = 1;
    }

    private void setFavList() {
        if (nearbies.size() <= 0) {
            fb_favorite.setVisibility(View.GONE);
        } else {
            fb_favorite.setVisibility(View.VISIBLE);
        }
        if (nearbies.size() == 1) {
            tv_total_favorite.setText("\"1 " + Constants.showMessage(getActivity(), mPreferences.getString("Lan_Id", ""), "placecount") + "\"");
        } else if (nearbies.size() > 0) {
            tv_total_favorite.setText(("\"" + nearbies.size() + " " + Constants.showMessage(getActivity(), mPreferences.getString("Lan_Id", ""), "placecounts") + "\""));
        } else {
            tv_total_favorite.setText(("\"" + 0 + " " + Constants.showMessage(getActivity(), mPreferences.getString("Lan_Id", ""), "placecounts") + "\""));
        }
        if (nearbies.size() == 0) {
            llEmptyLayout.setVisibility(View.VISIBLE);
            rv_favorite.setVisibility(View.GONE);
        } else {
            rv_favorite.setVisibility(View.VISIBLE);
            llEmptyLayout.setVisibility(View.GONE);
            recyclerAdapter = new RecyclerAdapter(getActivity());
            rv_favorite.setAdapter(null);
            rv_favorite.setAdapter(recyclerAdapter);
        }
    }

    private void addFavoriteCall(String Place_Id, String group_id) {
        if (CommonClass.hasInternetConnection(getActivity())) {
            String url = Constants.SERVER_URL + "json.php?action=AddFavorite";
            String json = "[{\"User_Id\":\"" + mainActivity.getPreferences().getString("User_Id", "") + "\",\"Place_Id\":\"" + Place_Id + "\",\"Group_Id\":\"" + group_id + "\"}]";
            new post_sync(getActivity(), "AddFavorite1", FavouriteFragment1.this, true).execute(url, json);
        } else {
            Intent intent = new Intent(getActivity(), NoInternet.class);
            startActivity(intent);
        }
    }

    public void addFavoriteResponse(String resultString) {
        if (resultString.length() > 2) {
            try {
                JSONArray jsonArray = new JSONArray(resultString);
                JSONObject jsonObject = jsonArray.optJSONObject(0);
                if (jsonObject.has("status")) {
                    String str = jsonObject.optString("status");
                    if (str.equalsIgnoreCase("true")) {
                        if (mCounter == -1) {
                            nearbies.get(mFlag).setFav_Id(jsonObject.optString("Fav_Id"));
                        }
                        mCounter = -1;
                        mFlag = 0;
                        Constants.mStaticFavCall = 0;
                        Constants.mStaticNearCall = 0;
                        SnackbarManager.show(Snackbar.with(getActivity()).color(Utils.getColor(getActivity(), R.color.mBlue)).text(Constants.showMessage(getActivity(), mPreferences.getString("Lan_Id", ""), "AddFavourite")));
                        update();
                    } else {

                    }
                } else {
                }
            } catch (JSONException e) {
                // Tracking exception
                MyTorismaApplication.getInstance().trackException(e);
                e.printStackTrace();
            }
        }
    }

    private void deleteFavoriteCall(String group_id) {
        if (CommonClass.hasInternetConnection(getActivity())) {
            String url = Constants.SERVER_URL + "json.php?action=DeleteFavorite";
            String json = "[{\"User_Id\":\"" + mainActivity.getPreferences().getString("User_Id", "") + "\",\"Group_Id\":\"" + group_id + "\"}]";
            new post_sync(getActivity(), "DeleteFavorite1", FavouriteFragment1.this, true).execute(url, json);
        } else {
            Intent intent = new Intent(getActivity(), NoInternet.class);
            startActivity(intent);
        }
    }

    public void deleteFavoriteResponse(String resultString) {
        if (resultString.length() > 2) {
            try {
                String str;
                JSONArray jsonArray = new JSONArray(resultString);
                JSONObject jsonObject = jsonArray.optJSONObject(0);
                if (jsonObject.has("status") && (str = jsonObject.optString("status")).equalsIgnoreCase("true")) {
                    if (mCounter == -1) {
                        nearbies.get(mFlag).setFav_Id("0");
                    }
                    mCounter = -1;
                    mFlag = 0;
                    SnackbarManager.show((Snackbar) Snackbar.with(getActivity()).color(Utils.getColor(getActivity(), R.color.mBlue)).text(Constants.showMessage(getActivity(), mPreferences.getString("Lan_Id", ""), "Removefavorite")));
                    Constants.mStaticFavCall = 0;
                    Constants.mStaticNearCall = 0;
                    update();
                }
            } catch (JSONException e) {
                // Tracking exception
                MyTorismaApplication.getInstance().trackException(e);
                // empty catch block
            }
        }
    }

    public void refresh() {
        //your code in refresh.
        Log.i("Refresh", "YES");
    }

    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(receiver);
    }

    public void onResume() {
        super.onResume();
        receiver = new MyReceiver();
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(receiver,
                new IntentFilter("TAG_REFRESH_FAVOURITE"));
        try {
//            recyclerAdapter.notifyDataSetChanged();
        } catch (Exception e) {
            // Tracking exception
            MyTorismaApplication.getInstance().trackException(e);
            e.printStackTrace();
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    //Local broadcast receiver to refresh the favourites page
    private class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            FavouriteFragment1.this.refresh();
            if (Prefs.getInt(Constants.user_id, 0) == 0) {
//                Toast.makeText(getActivity().getApplicationContext(), "Please login", Toast.LENGTH_LONG).show();
            } else {
                getFavoriteCall();
            }

        }
    }

    class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {
        public ViewHolder viewHolder;
        private Activity activity;
        private int lastPostion = -1;

        public RecyclerAdapter(Activity activity) {
            this.activity = activity;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            LayoutInflater inflater = activity.getLayoutInflater();
            View view = inflater.inflate(R.layout.new_nearby_recycler_fav, viewGroup, false);
            viewHolder = new ViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {

            String imageURL = Constants.IMAGE_URL + nearbies.get(position).getPlace_MainImage() + "&w=" + (width);
            Picasso.with(activity)
                    .load(imageURL)
                    .resize(width, (height * 60) / 100)
                    .into(holder.nearby_img);
            holder.category_txt.setText(nearbies.get(position).getCategory_Name());
            holder.place_txt.setText(nearbies.get(position).getPlace_Name());
            if (nearbies.get(position).getFree_entry().equals("0")) {
                holder.ticket_txt.setText(Constants.showMessage(activity, mPreferences.getString("Lan_Id", ""), "Check Details"));
                holder.ticket_txt.setSelected(true);
                holder.ticket_txt.requestFocus();
            } else if (nearbies.get(position).getFree_entry().equals("1")) {
                holder.ticket_txt.setText(Constants.showMessage(activity, mPreferences.getString("Lan_Id", ""), "Free Entry"));
                holder.ticket_txt.setSelected(true);
                holder.ticket_txt.requestFocus();
            }
            holder.dist_txt.setText(nearbies.get(position).getDist());

            if (nearbies.get(position).getFav_Id().equalsIgnoreCase("0")) {
                holder.imgFav.setActivated(false);
            } else {
                holder.imgFav.setActivated(true);
            }

            if (nearbies.get(position).getFav_Id().equalsIgnoreCase("0")) {
                holder.imgFav.setActivated(false);
            } else {
                holder.imgFav.setActivated(true);
            }

            holder.imgFav.setId(position);
            holder.imgFav.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mPreferences.getString("User_Id", "").equalsIgnoreCase("0")) {
                        mainActivity.showGuestSnackToast();
                    } else {
                        for (int j = 0; j < nearbies.size(); j++) {
                            if (v.getId() == j) {
                                if (nearbies.get(j).getFav_Id().equalsIgnoreCase("0")) {
                                    holder.imgFav.setActivated(true);
                                    addFavoriteCall(nearbies.get(j).getPlace_Id(), nearbies.get(j).getGroup_Id());
                                    mFlag = j;
                                } else {
                                    holder.imgFav.setActivated(false);
                                    deleteFavoriteCall(nearbies.get(j).getGroup_Id());
                                    mFlag = j;
                                }
                            }
                        }
                    }
                }
            });

            int dayFoundStatus = 0; //0 -> closed - 2-> open for 24 hours  1-> updated
            boolean isTicketSet = false;
            if (nearbies.get(position).getHourDetailsArrayList() != null && nearbies.get(position).getHourDetailsArrayList().size() > 0) {
                for (HourDetails hourDetails : nearbies.get(position).getHourDetailsArrayList()) {
                    if (hourDetails.getPOHKey().equals(Utils.getCurrentDay())) {
                        if (hourDetails.getPOHIsOpen().equals(PlaceClosed)) {
                            dayFoundStatus = 0;
                        } else if (hourDetails.getPOHIsOpen().equals(PlaceOpenFor24Hours)) {
                            dayFoundStatus = 2;
                        } else if (hourDetails.getPOHIsOpen().equals(PlaceOpenWithAnyTime)) {
                            _24HourTime = hourDetails.getPOHStartTime();
                            _24HourTime1 = hourDetails.getPOHEndTime();
                            _24HourDt = null;
                            _24HourDt1 = null;
                            if (_24HourTime != null && !_24HourTime.equalsIgnoreCase("NULL")) {
                                try {
                                    _24HourDt = _24HourSDF.parse(_24HourTime);
                                } catch (ParseException e) {
                                    // Tracking exception
                                    MyTorismaApplication.getInstance().trackException(e);
                                    e.printStackTrace();
                                }
                            }
                            if (_24HourTime1 != null && !_24HourTime1.equalsIgnoreCase("NULL")) {
                                try {
                                    _24HourDt1 = _24HourSDF.parse(_24HourTime1);
                                } catch (ParseException e) {
                                    // Tracking exception
                                    MyTorismaApplication.getInstance().trackException(e);
                                    e.printStackTrace();
                                }
                            }
                            if (_24HourDt != null && _24HourDt1 != null) {
                                holder.timing_txt.setText(Constants.showMessage(activity, mPreferences.getString("Lan_Id", ""), "Open Now"));
//                                holder.timing_txt.setText(_24HourSDF.format(_24HourDt) + " " + Constants.showMessage(activity, mPreferences.getString("Lan_Id", ""), "TO") + " " + _24HourSDF.format(_24HourDt1));
                            } else {
                                holder.timing_txt.setText("");
                                dayFoundStatus = 3;
                            }
                        }
                        if (hourDetails.getPOHCharges() != null && !hourDetails.getPOHCharges().equals("") && !hourDetails.getPOHCharges().equalsIgnoreCase("null")) {
                            isTicketSet = true;
                        }
                        break;
                    } else {
                        holder.timing_txt.setText(Constants.showMessage(activity, mPreferences.getString("Lan_Id", ""), "Timing") + ": -");
                        dayFoundStatus = 3;
                    }
                }
            }
            if (dayFoundStatus == 3) {
            } else if (dayFoundStatus == 2) {
                holder.timing_txt.setText(Constants.showMessage(activity, mPreferences.getString("Lan_Id", ""), "Open Now"));
            } else {
                holder.timing_txt.setText(Constants.showMessage(activity, mPreferences.getString("Lan_Id", ""), "Closed"));
            }

            holder.rl_main_img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    id = v.getId();
                    mainActivity.favouriteFragment.replacePlaceDetailsFragment(nearbies.get(position).getPlace_Id(), mainActivity.getPreferences().getString(Preference.Pref_City, ""));
                    mNearby = nearbies.get(position);
                }
            });

            holder.rlBottomView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    id = v.getId();
                    mainActivity.favouriteFragment.replacePlaceDetailsFragment(nearbies.get(position).getPlace_Id(), mainActivity.getPreferences().getString(Preference.Pref_City, ""));
                    mNearby = nearbies.get(position);
                }
            });

           /* viewHolder.txtShare.setText(Constants.showMessage(getActivity(), mPreferences.getString("Lan_Id", ""), "Share"));
            viewHolder.txtFav.setText(Constants.showMessage(getActivity(), mPreferences.getString("Lan_Id", ""), "Favourite"));
            String imageURL = Constants.IMAGE_URL + nearbies.get(position).getPlace_MainImage() + "&w=" + (width - 30);
            int heightofImage = (height * 60) / 100;
            Picasso.with(getActivity()) //
                    .load(imageURL) //
                    .resize(width, heightofImage)
                    .into(viewHolder.iv_nearby_explorer);
            viewHolder.tv_near.setText(nearbies.get(position).getPlace_Name());
            viewHolder.txtCategory.setText(Constants.showMessage(getActivity(), mPreferences.getString("Lan_Id", ""), "Category") + ": " + nearbies.get(position).getCategory_Name()
            );
            viewHolder.llView.setId(position);
            viewHolder.llView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    id = v.getId();
                    mainActivity.favouriteFragment.replacePlaceDetailsFragment(nearbies.get(v.getId()).getPlace_Id(), "");
                    mNearby = nearbies.get(v.getId());
                }
            });
            viewHolder.iv_nearby_explorer.setId(position);
            viewHolder.iv_nearby_explorer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    id = v.getId();
                    mainActivity.favouriteFragment.replacePlaceDetailsFragment(nearbies.get(v.getId()).getPlace_Id(), "");
                    mNearby = nearbies.get(v.getId());
                }
            });
            //TOdo
            viewHolder.rl_navigator.setId(position);
            viewHolder.rl_navigator.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    GPSTracker gpsTracker = new GPSTracker(getActivity());
                    if (!gpsTracker.canGetLocation())
                        gpsTracker.showSettingsAlert();
                    else {
                        Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse("http://maps.google.com/maps?q=loc:" + gpsTracker.getLatitude() + "," + gpsTracker.getLongitude() + "&daddr=" + Double.parseDouble(nearbies.get(position).getPlace_Latitude()) + "," + Double.parseDouble(nearbies.get(position).getPlace_Longi())));
                        startActivity(intent);
                    }
                }
            });

            if (nearbies.get(position).getFav_Id().equalsIgnoreCase("0")) {
                viewHolder.imgFav.setActivated(false);
            } else {
                viewHolder.imgFav.setActivated(true);
            }
            viewHolder.rl_share.setId(position);
            viewHolder.rl_share.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent mIntent = new Intent(getActivity(), ShareFragmentActivity.class);
                    String share1 = Constants.showMessage(getActivity(), mPreferences.getString("Lan_Id", ""), "share1");
                    String share2 = Constants.showMessage(getActivity(), mPreferences.getString("Lan_Id", ""), "share2");
                    String share3 = Constants.showMessage(getActivity(), mPreferences.getString("Lan_Id", ""), "share3");
                    mIntent.putExtra("myMsg", share1 + " \"" + mPreferences.getString("User_Name", "") + "\" " + share2 + " \"" + nearbies.get(v.getId()).getPlace_Name() + "\" " + share3);
                    startActivity(mIntent);
                }
            });

            viewHolder.rl_fav.setId(position);
            viewHolder.rl_fav.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    for (int j = 0; j < nearbies.size(); j++) {
                        if (v.getId() == j) {
                            if (nearbies.get(j).getFav_Id().equalsIgnoreCase("0")) {
                                viewHolder.imgFav.setActivated(true);
                                addFavoriteCall(nearbies.get(j).getPlace_Id(), nearbies.get(j).getGroup_Id());
                                mFlag = j;
                            } else {
                                viewHolder.imgFav.setActivated(false);
//                                deleteFavoriteCall(nearbies.get(j).getFav_Id());
                                deleteFavoriteCall(nearbies.get(j).getGroup_Id());

                                mFlag = j;
                            }
                        }
                    }
                }
            });

            int dayFoundStatus = 0; //0 -> closed - 2-> open for 24 hours  1-> updated
            boolean isTicketSet = false;
            if (nearbies.get(position).getHourDetailsArrayList() != null && nearbies.get(position).getHourDetailsArrayList().size() > 0) {
                for (HourDetails hourDetails : nearbies.get(position).getHourDetailsArrayList()) {
                    if (hourDetails.getPOHKey().equals(Utils.getCurrentDay())) {
                        if (hourDetails.getPOHIsOpen().equals(PlaceClosed)) {
                            dayFoundStatus = 0;
                        } else if (hourDetails.getPOHIsOpen().equals(PlaceOpenFor24Hours)) {
                            dayFoundStatus = 2;
                        } else if (hourDetails.getPOHIsOpen().equals(PlaceOpenWithAnyTime)) {
                            _24HourTime = hourDetails.getPOHStartTime();
                            _24HourTime1 = hourDetails.getPOHEndTime();
                            _24HourDt = null;
                            _24HourDt1 = null;
                            if (_24HourTime != null && !_24HourTime.equalsIgnoreCase("NULL")) {
                                try {
                                    _24HourDt = _24HourSDF.parse(_24HourTime);
                                } catch (ParseException e) {
                                    // Tracking exception
                                    MyTorismaApplication.getInstance().trackException(e);
                                    e.printStackTrace();
                                }
                            }
                            if (_24HourTime1 != null && !_24HourTime1.equalsIgnoreCase("NULL")) {
                                try {
                                    _24HourDt1 = _24HourSDF.parse(_24HourTime1);
                                } catch (ParseException e) {
                                    // Tracking exception
                                    MyTorismaApplication.getInstance().trackException(e);
                                    e.printStackTrace();
                                }
                            }
                            if (_24HourDt != null && _24HourDt1 != null) {
                                viewHolder.tv_timing.setText(Constants.showMessage(getActivity(), mPreferences.getString("Lan_Id", ""), "Timing") + ": " + _24HourSDF.format(_24HourDt) + " " + Constants.showMessage(getActivity(), mPreferences.getString("Lan_Id", ""), "TO") + " " + _24HourSDF.format(_24HourDt1));
                            } else {
                                viewHolder.tv_timing.setText(Constants.showMessage(getActivity(), mPreferences.getString("Lan_Id", ""), "Timing") + ": -");
                                dayFoundStatus = 1;
                            }
                        }
                        if (hourDetails.getPOHCharges() != null && !hourDetails.getPOHCharges().equals("") && !hourDetails.getPOHCharges().equalsIgnoreCase("null")) {
                            isTicketSet = true;
//                            viewHolder.tv_ticket.setText(Constants.showMessage(getActivity(), mPreferences.getString("Lan_Id", ""), "Tickets") + ": " + hourDetails.getPOHCharges());
                        }
                        break;
                    } else {
                        viewHolder.tv_timing.setText(Constants.showMessage(getActivity(), mPreferences.getString("Lan_Id", ""), "Timing") + ": -");
                        dayFoundStatus = 1;
                    }
                }
            }
            if (dayFoundStatus == 2) {
                viewHolder.tv_timing.setText(Constants.showMessage(getActivity(), mPreferences.getString("Lan_Id", ""), "Timing") + ": " + Constants.showMessage(getActivity(), mPreferences.getString("Lan_Id", ""), "Open Now"));
            } else if (dayFoundStatus == 0) {
                viewHolder.tv_timing.setText(Constants.showMessage(getActivity(), mPreferences.getString("Lan_Id", ""), "Timing") + ": " + Constants.showMessage(getActivity(), mPreferences.getString("Lan_Id", ""), "Closed"));
            }
            if (!isTicketSet) {
//                viewHolder.tv_ticket.setText(Constants.showMessage(getActivity(), mPreferences.getString("Lan_Id", ""), "Tickets") + ": -");
            }
            viewHolder.txtDistance.setText(nearbies.get(position).getDistance() + Constants.showMessage(getActivity(), mPreferences.getString("Lan_Id", ""), "KM"));

            if (nearbies.get(position).getFree_entry().equals("0")) {
                viewHolder.tv_ticket.setText(Constants.showMessage(getActivity(), mainActivity.getPreferences().getString("Lan_Id", ""), "Tickets") + ": " + Constants.showMessage(getActivity(), mainActivity.getPreferences().getString("Lan_Id", ""), "Check Details"));
            } else if (nearbies.get(position).getFree_entry().equals("1")) {
                viewHolder.tv_ticket.setText(Constants.showMessage(getActivity(), mainActivity.getPreferences().getString("Lan_Id", ""), "Tickets") + ": " + Constants.showMessage(getActivity(), mainActivity.getPreferences().getString("Lan_Id", ""), "Free Entry"));
            }*/
        }

        @Override
        public int getItemCount() {
            return nearbies.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            ImageView nearby_img, imgFav;
            NormalTextView category_txt, place_txt, timing_txt, ticket_txt, dist_txt;
            RelativeLayout rl_main_img, rlBottomView;

            public ViewHolder(View view) {
                super(view);
                nearby_img = (ImageView) view.findViewById(R.id.nearby_img);
                imgFav = (ImageView) view.findViewById(R.id.imgFav);
                category_txt = (NormalTextView) view.findViewById(R.id.nearby_category_txt);
                place_txt = (NormalTextView) view.findViewById(R.id.place_txt);
                timing_txt = (NormalTextView) view.findViewById(R.id.timing_txt);
                ticket_txt = (NormalTextView) view.findViewById(R.id.ticket_txt);
                dist_txt = (NormalTextView) view.findViewById(R.id.dist_txt);
                rl_main_img = (RelativeLayout) view.findViewById(R.id.rl_main_img);
                rlBottomView = (RelativeLayout) view.findViewById(R.id.rlBottomView);

           /* LinearLayout rl_fav, rl_navigator, rl_share;
            ProgressBar progressBar;
            private ImageView iv_nearby_explorer, imgFav;
            private NormalTextView tv_ticket, txtCategory, txtFav, txtDistance, tv_near, txtShare, tv_timing;
            private LinearLayout llView;
            private View container;

            public ViewHolder(View convertView) {
                super(convertView);
                iv_nearby_explorer = (ImageView) convertView.findViewById(R.id.iv_nearby_explorer);
                imgFav = (ImageView) convertView.findViewById(R.id.imgFav);
                tv_near = (NormalTextView) convertView.findViewById(R.id.tv_near);
                tv_timing = (NormalTextView) convertView.findViewById(R.id.tv_timing);
                tv_ticket = (NormalTextView) convertView.findViewById(R.id.tv_ticket);
                txtShare = (NormalTextView) convertView.findViewById(R.id.txtShare);
                txtFav = (NormalTextView) convertView.findViewById(R.id.txtFav);
                txtDistance = (NormalTextView) convertView.findViewById(R.id.txtDistance);
                llView = (LinearLayout) convertView.findViewById(R.id.llView);
                final RelativeLayout rlNearBy = (RelativeLayout) convertView.findViewById(R.id.rlNearBy);
                rl_fav = (LinearLayout) convertView.findViewById(R.id.rl_fav);
                rl_navigator = (LinearLayout) convertView.findViewById(R.id.rl_navigator);
                rl_share = (LinearLayout) convertView.findViewById(R.id.rl_share);
                txtCategory = (NormalTextView) convertView.findViewById(R.id.txtCategory);
                progressBar = (ProgressBar) convertView.findViewById(R.id.progressBar);
                iv_nearby_explorer.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, (height * 60) / 100));*/
            }
        }
    }
}