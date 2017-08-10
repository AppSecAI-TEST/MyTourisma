package com.ftl.tourisma.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.ftl.tourisma.MyTorismaApplication;
import com.ftl.tourisma.R;
import com.ftl.tourisma.custom_views.NormalTextView;
import com.ftl.tourisma.database.Nearby;
import com.ftl.tourisma.models.HourDetails;
import com.ftl.tourisma.postsync.post_sync;
import com.ftl.tourisma.utils.CommonClass;
import com.ftl.tourisma.utils.Constants;
import com.ftl.tourisma.utils.JSONObjConverter;
import com.ftl.tourisma.utils.Preference;
import com.ftl.tourisma.utils.Utilities;
import com.ftl.tourisma.utils.Utils;
import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.SnackbarManager;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.ftl.tourisma.ResolverActivity.TAG;
import static com.ftl.tourisma.utils.Constants.PlaceClosed;
import static com.ftl.tourisma.utils.Constants.PlaceOpenFor24Hours;
import static com.ftl.tourisma.utils.Constants.PlaceOpenWithAnyTime;

/**
 * Created by Vinay on 7/31/2017.
 */

public class NewTagsList extends Fragment implements post_sync.ResponseHandler {

    private static int mCounter = -1;
    int mFlag = 0;
    ArrayList<Nearby> nearbies = new ArrayList<>();
    TagListAdapter tagListAdapter;
    private NormalTextView tv_your_location_search_result, tv_place, txtMessage, txtSuggestPlace, experience_txt, tags_header_txt;
    private ImageView iv_back_search_result, iv_search_result;
    private LinearLayout ll_change_city, llEmptyLayout11;
    private RecyclerView rv_tags_list;
    private MainActivity mainActivity;
    private SharedPreferences mPreferences;
    private SharedPreferences.Editor mEditor;
    private int like;
    private String mPlaceId, mFav, tagId, tag_name;
    private View view;
    private Nearby nearby;

    public static NewTagsList NewInstance(String tagId, String tag_name) {
        Bundle bundle = new Bundle();
        bundle.putString("tagId", tagId);
        bundle.putString("tag_name", tag_name);
        NewTagsList newTagsList = new NewTagsList();
        newTagsList.setArguments(bundle);
        return newTagsList;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mainActivity = (MainActivity) context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        tagId = bundle.getString("tagId");
        tag_name = bundle.getString("tag_name");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //setting shared preferences
        mPreferences = getActivity().getSharedPreferences(Constants.mPref, 0);
        mEditor = mPreferences.edit();

        if (mPreferences.getString("Lan_Id", "").equals("8")) {
            view = inflater.inflate(R.layout.new_taglist_arabic, container, false);
        } else {
            view = inflater.inflate(R.layout.new_tags_list, container, false);
        }

        initialization(view);
        downloadTagsList();
        onClickListners();

        //setting layout manager for tags
        LinearLayoutManager tags_linearLayoutManager = new LinearLayoutManager(getActivity());
        rv_tags_list.setLayoutManager(tags_linearLayoutManager);

        return view;
    }

    public void initialization(View view) {
        rv_tags_list = (RecyclerView) view.findViewById(R.id.rv_tags_list);
        ll_change_city = (LinearLayout) view.findViewById(R.id.ll_change_city);
        llEmptyLayout11 = (LinearLayout) view.findViewById(R.id.llEmptyLayout11);
        iv_back_search_result = (ImageView) view.findViewById(R.id.iv_back_search_result);
        iv_search_result = (ImageView) view.findViewById(R.id.iv_search_result);

        experience_txt = (NormalTextView) view.findViewById(R.id.experience_txt_tags);
        experience_txt.setText(Constants.showMessage(getActivity(), mPreferences.getString("Lan_Id", ""), "EXPERIENCE") + " : ");

        tags_header_txt = (NormalTextView) view.findViewById(R.id.tags_header_txt_tags);
        tags_header_txt.setText(tag_name);

        txtSuggestPlace = (NormalTextView) view.findViewById(R.id.txtSuggestPlace);
        txtSuggestPlace.setText(Constants.showMessage(getActivity(), mPreferences.getString("Lan_Id", ""), "Suggest Place"));

        txtMessage = (NormalTextView) view.findViewById(R.id.txtMessage);
        txtMessage.setText(Constants.showMessage(getActivity(), mPreferences.getString("Lan_Id", ""), "No records available for this search"));

        tv_your_location_search_result = (NormalTextView) view.findViewById(R.id.tv_your_location_search_result);
        tv_your_location_search_result.setText(Constants.showMessage(getActivity(), mainActivity.getPreferences().getString("Lan_Id", ""), "locationtitle"));

        tv_place = (NormalTextView) view.findViewById(R.id.tv_place);
        tv_place.setText(mainActivity.getPreferences().getString(Preference.Pref_City, ""));
    }

    public void onClickListners() {

        iv_back_search_result.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainActivity.onBackPressed();
            }
        });

        iv_search_result.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainActivity.exploreNearbyFragment.replaceSearchFragment();
            }
        });

        ll_change_city.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainActivity.exploreNearbyFragment.replaceLocationFragment();
            }
        });

        txtSuggestPlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                suggestPlace();
            }
        });
    }

    public void suggestPlace() {
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

    public void downloadTagsList() {
        if (CommonClass.hasInternetConnection(getActivity())) {
            String url = Constants.SERVER_URL + "json.php?action=GetPlacesByTagId";
            String json = "[{\"Lan_Id\":\"" + mainActivity.getPreferences().getString("Lan_Id", "") + "\",\"User_Id\":\"" + mainActivity.getPreferences().getString("User_Id", "") + "\",\"Current_Latitude\":\"" + mainActivity.getPreferences().getString("latitude2", "") + "\",\"Current_Longitude\":\"" + mainActivity.getPreferences().getString("longitude2", "") + "\",\"City_Name\":\"" + mainActivity.getPreferences().getString(Preference.Pref_City, "") + "\",\"Tag_Id\":\"" + tagId + "\"}]";
            System.out.println("tagListJson " + json);
            new post_sync(getActivity(), "GetPlacesByTagId", NewTagsList.this, true).execute(url, json);
        } else {
            Intent intent = new Intent(getActivity(), NoInternet.class);
            startActivity(intent);
        }
    }

    @Override
    public void onResponse(String response, String action) {
        if (action.equalsIgnoreCase("GetPlacesByTagId")) {
            getTagsListResponse(response);
        } else if (action.equalsIgnoreCase("AddFavorite")) {
            addFavoriteResponse(response);
        } else if (action.equalsIgnoreCase("DeleteFavorite")) {
            deleteFavoriteResponse(response);
        }
    }

    public void getTagsListResponse(String resultString) {
        JSONObjConverter jonObjConverter = new JSONObjConverter();
        nearbies.clear();
        try {
            JSONArray jsonArray = new JSONArray(resultString);
            for (int i = 0; i < jsonArray.length(); i++) {
                nearby = jonObjConverter.convertJsonToNearByObj(jsonArray.optJSONObject(i));
                nearbies.add(nearby);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //checking array size and setting empty layout
        if (nearbies.size() == 0) {
            llEmptyLayout11.setVisibility(View.VISIBLE);
        } else {
            //setting near by adapter
            tagListAdapter = new TagListAdapter(getActivity(), nearbies);
            rv_tags_list.setAdapter(tagListAdapter);
        }
    }

    private void addFavoriteCall(String Place_Id, String group_id) {
        if (CommonClass.hasInternetConnection(getActivity())) {
            String url = Constants.SERVER_URL + "json.php?action=AddFavorite";
            mPlaceId = Place_Id;
            String json = "[{\"User_Id\":\"" + mainActivity.getPreferences().getString("User_Id", "") + "\",\"Place_Id\":\"" + Place_Id + "\",\"Group_Id\":\"" + group_id + "\"}]";
            new post_sync(getActivity(), "AddFavorite", NewTagsList.this, true).execute(url, json);
            System.out.println("addfav_json" + json);
        } else {
            SnackbarManager.show(Snackbar.with(getActivity()).color(Utilities.getColor(getActivity(), R.color.mBlue)).text(Constants.showMessage(getActivity(), mainActivity.getPreferences().getString("Lan_Id", ""), "NOINTERNET")));
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
                            for (Nearby nearby : nearbies) {
                                if (nearby.getPlace_Id().equals(mPlaceId)) {
                                    nearby.setFav_Id(jsonObject.optString("Fav_Id"));
                                    break;
                                }
                            }
                        }
                        mCounter = -1;
                        mFlag = 0;
                        like = 1;
                        mPlaceId = "";
                        Constants.mStaticFavCall = 0;
                        Constants.mStaticNearCall = 0;
                        SnackbarManager.show(Snackbar.with(getActivity()).color(Utilities.getColor(getActivity(), R.color.mBlue)).text(Constants.showMessage(getActivity(), mainActivity.getPreferences().getString("Lan_Id", ""), "AddFavourite")));
                    }
                }
            } catch (JSONException e) {
                // Tracking exception
                MyTorismaApplication.getInstance().trackException(e);
                e.printStackTrace();
            }
        }
    }

    private void deleteFavoriteCall(String Fav_Id, String group_id) {
        if (CommonClass.hasInternetConnection(getActivity())) {
            String url = Constants.SERVER_URL + "json.php?action=DeleteFavorite";
            String json = "[{\"User_Id\":\"" + mainActivity.getPreferences().getString("User_Id", "") + "\",\"Group_Id\":\"" + group_id + "\"}]";
            mFav = Fav_Id;
            new post_sync(getActivity(), "DeleteFavorite", NewTagsList.this, true).execute(url, json);
        } else {
            Intent intent = new Intent(getActivity(), NoInternet.class);
            startActivity(intent);
        }
    }

    public void deleteFavoriteResponse(String resultString) {
        if (resultString.length() > 2) {
            try {
                JSONArray jsonArray = new JSONArray(resultString);
                JSONObject jsonObject = jsonArray.optJSONObject(0);
                if (jsonObject.has("status")) {
                    String str = jsonObject.optString("status");
                    if (str.equalsIgnoreCase("true")) {
                        if (mCounter == -1) {
                            for (Nearby nearby : nearbies) {
                                if (nearby.getFav_Id().equals(mFav)) {
                                    nearby.setFav_Id("0");
                                    break;
                                }
                            }
                        }
                        mFlag = 0;
                        mFav = "";
                        Constants.mStaticFavCall = 0;
                        Constants.mStaticNearCall = 0;
                        like = 0;
                        SnackbarManager.show(Snackbar.with(getActivity()).color(Utilities.getColor(getActivity(), R.color.mBlue)).text(Constants.showMessage(getActivity(), mainActivity.getPreferences().getString("Lan_Id", ""), "Removefavorite")));
                    }
                }
            } catch (JSONException e) {
                // Tracking exception
                MyTorismaApplication.getInstance().trackException(e);
            }
        }
    }

    //Setting tags list adapter
    class TagListAdapter extends RecyclerView.Adapter<TagListAdapter.ViewHolder> {

        public int height;
        public int width;
        ArrayList<Nearby> nearbies = new ArrayList<>();
        Activity activity;
        private String _24HourTime, _24HourTime1;
        private Date _24HourDt, _24HourDt1;
        private SimpleDateFormat _24HourSDF, _12HourSDF;
        private SharedPreferences mPreferences;
        private SharedPreferences.Editor mEditor;

        public TagListAdapter(Activity activity, ArrayList<Nearby> nearbies) {
            this.nearbies = nearbies;
            this.activity = activity;
        }

        public TagListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.new_home_nearby_recycler, parent, false);

            //setting date format
            _24HourSDF = new SimpleDateFormat("HH:mm");
            _12HourSDF = new SimpleDateFormat("hh:mma");

            // Initializing the shared preference
            mPreferences = activity.getSharedPreferences(Constants.mPref, 0);
            mEditor = mPreferences.edit();

            //getting display metrics
            try {
                DisplayMetrics displaymetrics = new DisplayMetrics();
                activity.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
                height = displaymetrics.heightPixels;
                width = displaymetrics.widthPixels;
            } catch (Exception e) {
                // Tracking exception
                MyTorismaApplication.getInstance().trackException(e);
                e.printStackTrace();
            }

            TagListAdapter.ViewHolder vh = new TagListAdapter.ViewHolder(v);
            return vh;
        }

        @Override
        public void onBindViewHolder(final TagListAdapter.ViewHolder holder, final int position) {
            String imageURL = Constants.IMAGE_URL + nearbies.get(position).getPlace_MainImage() + "&w=" + (width);
            Picasso.with(activity)
                    .load(imageURL)
                    .resize(width, (height * 60) / 100)
                    .into(holder.nearby_img);
            holder.category_txt.setText(nearbies.get(position).getCategory_Name());
            holder.category_txt.setSelected(true);
            holder.category_txt.requestFocus();
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
            holder.dist_txt.setText(nearbies.get(position).getDist() + " " + Constants.showMessage(getActivity(), mPreferences.getString("Lan_Id", ""), "KM"));

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
//                                        deleteFavoriteCall(nearbies.get(j).getFav_Id());
                                    deleteFavoriteCall(nearbies.get(j).getFav_Id(), nearbies.get(j).getGroup_Id());
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
//                                holder.timing_txt.setText(_24HourSDF.format(_24HourDt) + " " + Constants.showMessage(activity, mPreferences.getString("Lan_Id", ""), "TO") + " " + _24HourSDF.format(_24HourDt1));
                                holder.timing_txt.setText(Constants.showMessage(activity, mPreferences.getString("Lan_Id", ""), "Open Now"));
                                holder.timing_txt.setSelected(true);
                                holder.timing_txt.requestFocus();
                            } else {
                                holder.timing_txt.setText("");
                                holder.timing_txt.setSelected(true);
                                holder.timing_txt.requestFocus();
                                dayFoundStatus = 3;
                            }
                        }
                        if (hourDetails.getPOHCharges() != null && !hourDetails.getPOHCharges().equals("") && !hourDetails.getPOHCharges().equalsIgnoreCase("null")) {
                            isTicketSet = true;
                        }
                        break;
                    } else {
                        holder.timing_txt.setText(Constants.showMessage(activity, mPreferences.getString("Lan_Id", ""), "Timing") + ": -");
                        holder.timing_txt.setSelected(true);
                        holder.timing_txt.requestFocus();
                        dayFoundStatus = 3;
                    }
                }
            }
            if (dayFoundStatus == 3) {
            } else if (dayFoundStatus == 2) {
                holder.timing_txt.setText(Constants.showMessage(activity, mPreferences.getString("Lan_Id", ""), "Open Now"));
                holder.timing_txt.setSelected(true);
                holder.timing_txt.requestFocus();
            } else {
                holder.timing_txt.setText(Constants.showMessage(activity, mPreferences.getString("Lan_Id", ""), "Closed"));
                holder.timing_txt.setSelected(true);
                holder.timing_txt.requestFocus();
            }

            holder.rl_main_img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mainActivity.exploreNearbyFragment.replacePlaceDetailsFragment(nearbies.get(position).getPlace_Id(), tv_place.getText().toString(), nearbies.get(position).getGroup_Id());
                }
            });

            holder.rlBottomView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mainActivity.exploreNearbyFragment.replacePlaceDetailsFragment(nearbies.get(position).getPlace_Id(), tv_place.getText().toString(), nearbies.get(position).getGroup_Id());
                }
            });
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
            }
        }
    }
}
