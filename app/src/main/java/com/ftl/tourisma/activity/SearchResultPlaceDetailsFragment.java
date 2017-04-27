package com.ftl.tourisma.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.Html;
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
import android.widget.BaseAdapter;
import android.widget.Button;
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
import com.ftl.tourisma.FullPlaceImageViewActivity;
import com.ftl.tourisma.MyTorismaApplication;
import com.ftl.tourisma.R;
import com.ftl.tourisma.ShareFragmentActivity;
import com.ftl.tourisma.SimpleVrPanoramaActivity;
import com.ftl.tourisma.adapters.TimingAdapter;
import com.ftl.tourisma.custom_views.NormalBoldTextView;
import com.ftl.tourisma.custom_views.NormalTextView;
import com.ftl.tourisma.database.FeesDetails;
import com.ftl.tourisma.database.Nearby;
import com.ftl.tourisma.models.HourDetails;
import com.ftl.tourisma.models.WeekDaysModel;
import com.ftl.tourisma.postsync.PostSync;
import com.ftl.tourisma.postsync.post_sync;
import com.ftl.tourisma.utils.CommonClass;
import com.ftl.tourisma.utils.Constants;
import com.ftl.tourisma.utils.CustomTypefaceSpan;
import com.ftl.tourisma.utils.GPSTracker;
import com.ftl.tourisma.utils.JSONObjConverter;
import com.ftl.tourisma.utils.Preference;
import com.ftl.tourisma.utils.TimingFunction;
import com.ftl.tourisma.utils.Utilities;
import com.ftl.tourisma.utils.Utils;
import com.google.android.gms.maps.model.Marker;
import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.SnackbarManager;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.ftl.tourisma.utils.Constants.PlaceOpenFor24Hours;
import static com.ftl.tourisma.utils.Constants.PlaceOpenWithAnyTime;

/**
 * Created by fipl11111 on 02-Mar-16.
 */
public class SearchResultPlaceDetailsFragment extends Fragment implements View.OnClickListener, post_sync.ResponseHandler {

    private static final int PLACE_LIKE_SEARCH = 1003;
    private static final String TAG = "SearchResultPlace";
    static int mCounter = -1;
    ScrollView similar_scroll;
    Button buy_tickets;
    GalleryAdapter2 galleryAdapter2;
    View view;
    private ArrayList<Nearby> nearbies1 = new ArrayList<>();
    private Intent mIntent;
    private int width, height;
    private SharedPreferences mPreferences;
    private SharedPreferences.Editor mEditor;
    private SimpleDateFormat _24HourSDF, _12HourSDF;
    private String _24HourTime, _24HourTime1;
    private Date _24HourDt, _24HourDt1;
    private NormalBoldTextView tv_full_name_search_result2;
    private NormalTextView txtDailyWorkingHours, txtOpenNowVal, txt_add_to_fav, tv_distance1_search_result2, tv_info_search_result2, tv_discription_search_result2;
    private com.ftl.tourisma.gallery1.Gallery gv_detail1_search_result2;
    private ImageView iv_back5;
    private String[] strImg1;
    private int id1;
    private LinearLayout listView_fees, ll_search_result2, ll_see_all;
    private Double latitude, longitude;
    private int pos = 0;
    private Marker marker;
    private String placeId;
    private NormalTextView tv_map_location;
    private int mFlag = 0;
    private ImageView iv_search_map;
    private LinearLayout ll_change_city;
    private NormalTextView tv_fee_search_result;
    private NormalTextView tv_about_place_search_result;
    private NormalTextView tv_similar_search;
    private NormalTextView tv_see_all_search_result;
    private Nearby mNearby = new Nearby();
    private int like;
    private Dialog dialog;
    private ArrayList<HourDetails> hourDetailses;
    private SliderLayout sliderPlaceImages;
    private PagerIndicator custom_indicator1;
    private ImageView imgSharePlace;
    private RelativeLayout rlVirtualTour;
    private NormalTextView tv_similar_explore, txtSuggestPlace, txtStartNavigating, txt_snack_msg, tv_login_snack, tv_sign_up_snack, tv_snack_msg;
    private Nearby nearByDetails;
    private JSONObjConverter jsonObjConverter;
    private boolean isForSimilarPlaces = false;
    private String locationName;
    private MainActivity mainActivity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_place_details, container, false);
        Bundle mIntent = getArguments();
//        nearbies = (ArrayList<Nearby>) mIntent.getSerializableExtra("nearbies");
        placeId = mIntent.getString("placeId");
        locationName = mIntent.getString("location");

        jsonObjConverter = new JSONObjConverter();
        _24HourSDF = new SimpleDateFormat("HH:mm");
        _12HourSDF = new SimpleDateFormat("hh:mma");
        try {
            DisplayMetrics displaymetrics = new DisplayMetrics();
            getActivity().getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
            height = displaymetrics.heightPixels;
            width = displaymetrics.widthPixels;
        } catch (Exception e) {
            // Tracking exception
            MyTorismaApplication.getInstance().trackException(e);
            e.printStackTrace();
        }

        mPreferences = getActivity().getSharedPreferences(Constants.mPref, 0);
        mEditor = mPreferences.edit();

        initialisation(view);


        searchCall(placeId);
        return view;
    }


    private void initialisation(View view) {

        imgSharePlace = (ImageView) view.findViewById(R.id.imgSharePlace);
        imgSharePlace.setOnClickListener(this);

        similar_scroll = (ScrollView) view.findViewById(R.id.similar_scroll);
        buy_tickets = (Button) view.findViewById(R.id.buy_tickets);
        rlVirtualTour = (RelativeLayout) view.findViewById(R.id.rlVirtualTour);
        rlVirtualTour.setOnClickListener(this);
        custom_indicator1 = (PagerIndicator) view.findViewById(R.id.custom_indicator1);
        custom_indicator1.setIndicatorStyleResource(R.drawable.shape_cirlce_fill, R.drawable.shape_cirlce_unfill);

        ll_see_all = (LinearLayout) view.findViewById(R.id.ll_see_all);

        tv_fee_search_result = (NormalTextView) view.findViewById(R.id.tv_fee_explore);
        tv_fee_search_result.setText(Constants.showMessage(getActivity(), mPreferences.getString("Lan_Id", ""), "Fee"));
        txtStartNavigating = (NormalTextView) view.findViewById(R.id.txtStartNavigating);
        txtStartNavigating.setText(Constants.showMessage(getActivity(), mPreferences.getString("Lan_Id", ""), "StartNavigation"));

        tv_about_place_search_result = (NormalTextView) view.findViewById(R.id.tv_about_place_explore);
        tv_about_place_search_result.setText(Constants.showMessage(getActivity(), mPreferences.getString("Lan_Id", ""), "aboutplace"));

        tv_similar_explore = (NormalTextView) view.findViewById(R.id.tv_similar_explore);
        tv_similar_explore.setText(Constants.showMessage(getActivity(), mPreferences.getString("Lan_Id", ""), "similarplace"));

        tv_see_all_search_result = (NormalTextView) view.findViewById(R.id.tv_see_all_explore);
        tv_see_all_search_result.setText(Constants.showMessage(getActivity(), mPreferences.getString("Lan_Id", ""), "seeall"));
        tv_see_all_search_result.setOnClickListener(this);

        ll_change_city = (LinearLayout) view.findViewById(R.id.ll_change_city);

        iv_search_map = (ImageView) view.findViewById(R.id.iv_search_map);
        iv_search_map.setVisibility(View.VISIBLE);
        iv_search_map.setOnClickListener(this);

        ll_change_city.setOnClickListener(this);

        ll_search_result2 = (LinearLayout) view.findViewById(R.id.ll_search_result2);

        tv_map_location = (NormalTextView) view.findViewById(R.id.tv_map_location);

//        tv_map_location.setText(mPreferences.getString(Preference.Pref_City, ""));
        if (locationName == null) {
            if (mPreferences.getString(Preference.Pref_City, "").equalsIgnoreCase("")) {
                tv_map_location.setText(mPreferences.getString(Preference.Pref_Country, ""));
            } else {
            }
        } else {
            tv_map_location.setText(locationName);
        }

        tv_full_name_search_result2 = (NormalBoldTextView) view.findViewById(R.id.tv_full_name);
        tv_info_search_result2 = (NormalTextView) view.findViewById(R.id.tv_info);
        txtOpenNowVal = (NormalTextView) view.findViewById(R.id.txtOpenNowVal);
        txtDailyWorkingHours = (NormalTextView) view.findViewById(R.id.txtDailyWorkingHours);
        txtDailyWorkingHours.setText(Constants.showMessage(getActivity(), mPreferences.getString("Lan_Id", ""), "View Daily Working Hours"));
        txtDailyWorkingHours.setOnClickListener(this);

        tv_distance1_search_result2 = (NormalTextView) view.findViewById(R.id.tv_distance1);
        tv_discription_search_result2 = (NormalTextView) view.findViewById(R.id.tv_discription);
        gv_detail1_search_result2 = (com.ftl.tourisma.gallery1.Gallery) view.findViewById(R.id.gv_detail1);

        iv_back5 = (ImageView) view.findViewById(R.id.iv_back5);
        iv_back5.setOnClickListener(this);

        txt_add_to_fav = (NormalTextView) view.findViewById(R.id.txt_add_to_fav);

        listView_fees = (LinearLayout) view.findViewById(R.id.listView_fees);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mainActivity = (MainActivity) context;
    }

    @Override
    public void onClick(View v) {
        if (v == ll_change_city) {
           /* Intent mIntent = new Intent(getActivity(), SelectLocationFragmentActivity.class);
//            mIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(mIntent);*/
            if (mainActivity.mainTabHostFragment.vpFragment.getCurrentItem() == 1) {

                mainActivity.favouriteFragment.replaceLocationFragment();
            } else {
                mainActivity.exploreNearbyFragment.replaceLocationFragment();

            }
            // getActivity().finish();
        } else if (v == iv_back5) {
            mainActivity.onBackPressed();
//            ll_search_result1.setVisibility(View.VISIBLE);
            /*finish();*/
        } else if (v == iv_search_map) {
            Fragment fragment = new SearchFragment();
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fram1, fragment);
            fragmentTransaction.addToBackStack(SearchFragment.class.getSimpleName());
            fragmentTransaction.commit();
        } else if (v == tv_see_all_search_result) {
            if (mainActivity.mainTabHostFragment.vpFragment.getCurrentItem() == 1) {

                mainActivity.favouriteFragment.replaceSearchResultFragment(nearbies1, "Similar Places");
            } else {
                mainActivity.exploreNearbyFragment.replaceSearchResultFragment(nearbies1, "Similar Places", false);

            }
        } else if (v == txtSuggestPlace) {
            suggestPlace();
        } else if (v == txtDailyWorkingHours) {
            openWeekDaysPopup();
        } else if (v == rlVirtualTour) {
            Intent intent = new Intent(getActivity(), SimpleVrPanoramaActivity.class);
            intent.putExtra("path", mNearby.getPlaceVRMainImage());
            intent.putExtra("path_1", mNearby.getVrimages());
            startActivity(intent);
        } else if (v == imgSharePlace) {
            Intent mIntent = new Intent(getActivity(), ShareFragmentActivity.class);
            String share1 = Constants.showMessage(getActivity(), mPreferences.getString("Lan_Id", ""), "share1");
            String share2 = Constants.showMessage(getActivity(), mPreferences.getString("Lan_Id", ""), "share2");
            String share3 = Constants.showMessage(getActivity(), mPreferences.getString("Lan_Id", ""), "share3");

            mIntent.putExtra("myMsg", share1 + " \"" + mPreferences.getString("User_Name", "") + "\" " + share2 + " \"" + mNearby.getPlace_Name() + "\" " + share3);
            startActivity(mIntent);

        }
    }

    public void suggestPlace() {
        Intent sendIntent = new Intent(Intent.ACTION_VIEW);
        sendIntent.setType("text/plain");
        sendIntent.setData(Uri.parse("info@mytourisma.com"));
        sendIntent.setClassName("com.google.android.gm", "com.google.android.gm.ComposeActivityGmail");
        sendIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{"info@mytourisma.com"});
        sendIntent.putExtra(Intent.EXTRA_SUBJECT, "myTourisma - Suggest new location");
        sendIntent.putExtra(Intent.EXTRA_TEXT, "Hello,\n" +
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

        final PackageManager pm = getActivity().getPackageManager();
        final List<ResolveInfo> matches = pm.queryIntentActivities(sendIntent, 0);
        ResolveInfo best = null;
        for (final ResolveInfo info : matches)
            if (info.activityInfo.packageName.endsWith(".gm") || info.activityInfo.name.toLowerCase().contains("gmail"))
                best = info;
        if (best != null)
            sendIntent.setClassName(best.activityInfo.packageName, best.activityInfo.name);
        startActivity(sendIntent);
    }


    public void setDetailInfo(final Nearby mNearby) {

//        Log.d("System out", "selected id " + id1);
//        Log.d("System out", "mNearby size " + nearbies.size());
//        Log.d("System out", "mNearby getHoursOfOperations size " + mNearby.getHoursOfOperations().size());

        hourDetailses = new ArrayList<>();
        int dayFoundStatus = 0; //0 -> no day found 1-> found but closed - 2-> open but time not found 3 -> done
        if (mNearby.getHourDetailsArrayList() != null && mNearby.getHourDetailsArrayList().size() > 0) {
            hourDetailses = mNearby.getHourDetailsArrayList();
            for (HourDetails hourDetails : mNearby.getHourDetailsArrayList()) {
                if (hourDetails.getPOHKey().equals(Utils.getCurrentDay())) {

                    //Checking for if yeasterday's timing is open for place
                    for (HourDetails hourDetailsYesterday : mNearby.getHourDetailsArrayList()) {
                        if (hourDetailsYesterday.getPOHDay().equalsIgnoreCase(Utils.getYesterDayDay())) {
                            if (hourDetailsYesterday.getPOHIsOpen().equals(PlaceOpenWithAnyTime)) {
                                SpannableStringBuilder spannableStringBuilder = TimingFunction.checkYesterDayTiming(getActivity(), hourDetails, hourDetailsYesterday, mPreferences);
                                if (spannableStringBuilder != null)
                                    dayFoundStatus = 2;
                            }
                            break;
                        }
                    }

                    SpannableStringBuilder time = TimingFunction.getPlaceTiming(getActivity(), hourDetails, mPreferences);
                    if (hourDetails.getPOHIsOpen().equals(PlaceOpenWithAnyTime)) {
                        if (time != null) {
                            if (dayFoundStatus == 0)
                                dayFoundStatus = 3;
                            txtOpenNowVal.setText(time);
                        } else {
                            dayFoundStatus = 2;
                        }

                    } else if (hourDetails.getPOHIsOpen().equals(PlaceOpenFor24Hours)) {
                        dayFoundStatus = 2;
                    } else {

                        dayFoundStatus = 1;

                    }
                }
            }
        }
        if (dayFoundStatus == 2) {
            txtOpenNowVal.setText(Utils.getSpannableString(Constants.showMessage(getActivity(), mPreferences.getString("Lan_Id", ""), "Open Now"), Utils.getColor(getActivity(), R.color.mGreen), true, 0));

            // txtOpenNowVal.setText("");
        } else if (dayFoundStatus == 0 || dayFoundStatus == 1) {

            txtOpenNowVal.setText(Utils.getSpannableString(Constants.showMessage(getActivity(), mPreferences.getString("Lan_Id", ""), "Closed"), Utils.getColor(getActivity(), android.R.color.holo_red_dark), true, 0));
//                    txtOpenNowVal.setText("");
        }


        final int mId;

        mId = id1;
        if (mNearby.getFav_Id().equalsIgnoreCase("0")) {
            txt_add_to_fav.setCompoundDrawablesWithIntrinsicBounds(Utilities.getDrawable(getActivity(), R.drawable.ic_favourite_default), null, null, null);
            txt_add_to_fav.setText(Constants.showMessage(getActivity(), mPreferences.getString("Lan_Id", ""), "Add to Favourites"));
            like = 0;
        } else {
            txt_add_to_fav.setText(Constants.showMessage(getActivity(), mPreferences.getString("Lan_Id", ""), "Remove Favourite"));

            txt_add_to_fav.setCompoundDrawablesWithIntrinsicBounds(Utilities.getDrawable(getActivity(), R.drawable.ic_favourite_active), null, null, null);

            like = 1;
        }

        txt_add_to_fav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPreferences.getString("User_Id", "").equalsIgnoreCase("0")) {
                    ((MainActivity) getActivity()).guestSnackToast();
                } else {
                    if (like == 0) {
                        txt_add_to_fav.setText(Constants.showMessage(getActivity(), mPreferences.getString("Lan_Id", ""), "Remove Favourite"));

                        txt_add_to_fav.setCompoundDrawablesWithIntrinsicBounds(Utilities.getDrawable(getActivity(), R.drawable.ic_favourite_active), null, null, null);

                        addFavoriteCall(mNearby.getPlace_Id());
                        mFlag = mId;
                    } else if (like == 1) {
                        txt_add_to_fav.setText(Constants.showMessage(getActivity(), mPreferences.getString("Lan_Id", ""), "Add to Favourites"));
                        txt_add_to_fav.setCompoundDrawablesWithIntrinsicBounds(Utilities.getDrawable(getActivity(), R.drawable.ic_favourite_default), null, null, null);

                        deleteFavoriteCall(mNearby.getFav_Id());
                        mFlag = mId;
                    }
                    mCounter = 1;
                }
            }
        });

        tv_distance1_search_result2.setText(Utilities.GetRoutDistane(Double.parseDouble(mPreferences.getString("latitude2", "")), Double.parseDouble(mPreferences.getString("longitude2", "")), Double.parseDouble(mNearby.getPlace_Latitude()), Double.parseDouble(mNearby.getPlace_Longi()), mNearby.getDist()) + Constants.showMessage(getActivity(), mPreferences.getString("Lan_Id", ""), "KM"));
//        tv_distance1_search_result2.setText(mNearby.getDistance() + Constants.showMessage(getActivity(), mPreferences.getString("Lan_Id", ""), "KM"));

        tv_full_name_search_result2.setText(mNearby.getPlace_Name());
        tv_info_search_result2.setText(mNearby.getPlace_Address());
        tv_discription_search_result2.setText(Html.fromHtml(Html.fromHtml("<html><body><p style=\"text-align:justify\">" + mNearby.getPlace_Description() + "</p></body></html>").toString()));
        String string = mNearby.getPlace_MainImage();
        if (mNearby.getOtherimages().length() != 0 && !mNearby.getOtherimages().equalsIgnoreCase("null")) {
            string += "," + mNearby.getOtherimages();
        }
        listView_fees.setVisibility(View.VISIBLE);
        boolean isCurrentDayFound = false;
        if (mNearby.getHourDetailsArrayList() != null && mNearby.getHourDetailsArrayList().size() > 0) {
            addFeesCustomViews(mNearby.getHourDetailsArrayList().get(0).getPOHCharges(), mNearby.getPrice_Description());
        } else {
            addFeesCustomViews("-", mNearby.getPrice_Description());
        }


//        Log.d("System out", "string " + string);

        strImg1 = string.split(",");
        if (strImg1.length >= 0) {
            sliderPlaceImages = (SliderLayout) view.findViewById(R.id.sliderPlaceImages);
            sliderPlaceImages.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (height * 60) / 100));
            if (strImg1.length > 1) {
                sliderPlaceImages.setDuration(4000);
            } else {
                sliderPlaceImages.stopAutoCycle();
                sliderPlaceImages.setPagerTransformer(false, new BaseTransformer() {
                    @Override
                    protected void onTransform(View view, float v) {
                    }
                });
            }
            sliderPlaceImages.removeAllSliders();
            for (String imgName : strImg1) {
                String imageUrl = Constants.IMAGE_URL + imgName + "&w=" + (width);
                DefaultSliderView textSliderView = new DefaultSliderView(getActivity());
                // initialize a SliderLayout
                textSliderView
                        .description(mNearby.getPlace_Name())
                        .image(imageUrl)
                        .setScaleType(BaseSliderView.ScaleType.Fit)
                        .setOnSliderClickListener(new BaseSliderView.OnSliderClickListener() {
                            @Override
                            public void onSliderClick(BaseSliderView slider1) {
                                Intent intent = new Intent(getActivity(), FullPlaceImageViewActivity.class);
                                intent.putExtra("nearBy_id", mNearby.getPlace_Id());
                                intent.putExtra("nearBy_Fav_id", mNearby.getFav_Id());
//                    intent.putExtra("nearBy_Fav_id",nearby.getFav_Id());
                                intent.putExtra("nearBy_name", mNearby.getPlace_Name());
                                intent.putExtra("nearBy_longi", mNearby.getPlace_Longi());
                                intent.putExtra("nearBy_lati", mNearby.getPlace_Latitude());
                                intent.putExtra("nearBy_main_image", mNearby.getPlace_MainImage());
                                intent.putExtra("nearBy_other_images", mNearby.getOtherimages());
                                nearByDetails = mNearby;
                                startActivityForResult(intent, PLACE_LIKE_SEARCH);
                            }
                        });

                sliderPlaceImages.addSlider(textSliderView);
            }
            sliderPlaceImages.setCustomIndicator(custom_indicator1);
//            sliderPlaceImages.addOnPageChangeListener(getActivity());
/*            if (mNearby.getPlaceVRMainImage() != null && !mNearby.getPlaceVRMainImage().equals("")){
                rlVirtualTour.setVisibility(View.VISIBLE);
            } else {
                rlVirtualTour.setVisibility(View.GONE);
            }*/
            rlVirtualTour.setVisibility((mNearby.getPlaceVRMainImage() != null && !mNearby.getPlaceVRMainImage().equals("")) ? View.VISIBLE : View.GONE);

        }
        if (isForSimilarPlaces)
            searchCall(mNearby.getPlace_Id());
        txtStartNavigating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GPSTracker gpsTracker = new GPSTracker(getActivity());
                if (!gpsTracker.canGetLocation())
                    gpsTracker.showSettingsAlert();
                else {
                    Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse("http://maps.google.com/maps?q=loc:" + gpsTracker.getLatitude() + "," + gpsTracker.getLongitude() + "&daddr=" + Double.parseDouble(mNearby.getPlace_Latitude()) + "," + Double.parseDouble(mNearby.getPlace_Longi())));
                    startActivity(intent);
                }
                /*String uri = String.format(Locale.ENGLISH, "http://maps.google.com/maps?saddr=%f,%f&daddr=%f,%f", Double.parseDouble(mPreferences.getString("latitude1", "")), Double.parseDouble(mPreferences.getString("longitude1", "")), Double.parseDouble(mNearby.getPlace_Latitude()), Double.parseDouble(mNearby.getPlace_Longi()));
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
                startActivity(intent);*/
            }
        });
        similar_scroll.scrollTo(0, 0);

    }


    private void addFeesCustomViews(String pohCharges, String price_description) {
        listView_fees.removeAllViews();
        if (price_description == null || price_description.equals("") || price_description.equalsIgnoreCase("null") || price_description.trim().length() == 0) {


            price_description = Constants.showMessage(getActivity(), mPreferences.getString("Lan_Id", ""), "No Fees data available");
        }
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.row_fees, null);
        NormalTextView txtFees = (NormalTextView) view.findViewById(R.id.txtFees);
        txtFees.setText("");
        txtFees.append(getSpannableString(price_description, false));
        listView_fees.addView(view);

    }

    private void addFeesCustomViews(ArrayList<FeesDetails> feesDetailsArrayList) {
//        if (convertView == null) {
        listView_fees.removeAllViews();
        if (feesDetailsArrayList.size() == 0) {
            LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.row_fees, null);
            NormalTextView txtFees = (NormalTextView) view.findViewById(R.id.txtFees);
            txtFees.setText("");
            txtFees.append(getSpannableString(Constants.showMessage(getActivity(), mPreferences.getString("Lan_Id", ""), "No Fees data available"), true));
            listView_fees.addView(view);
            return;
        }
        for (FeesDetails feesDetails : feesDetailsArrayList) {
            LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.row_fees, null);
            NormalTextView txtFees = (NormalTextView) view.findViewById(R.id.txtFees);
            txtFees.setText("");
            txtFees.append(getSpannableString(feesDetails.getFeesName() + ": ", true));
            txtFees.append(getSpannableString(feesDetails.getFeesValue() + "", false));
            listView_fees.addView(view);
//        }
        }
    }

    private SpannableStringBuilder getSpannableString(String s, boolean isBold) {
//        Typeface font = Typeface.createFromAsset(getAssets(), "Akshar.ttf");
        //String text=s.replace("\\n", System.getProperty("line.separator")).replace("\\r", "");
        SpannableStringBuilder spannablecontent = new SpannableStringBuilder(s);

        if (isBold)
            spannablecontent.setSpan(new CustomTypefaceSpan("", MyTorismaApplication.typeFace.getBoldFonts()),
                    0, spannablecontent.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        else
            spannablecontent.setSpan(new CustomTypefaceSpan("", MyTorismaApplication.typeFace.getNormalFonts()),
                    0, spannablecontent.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        return spannablecontent;
    }

    @Override
    public void onResponse(String response, String action) {
        try {
            if (action.equalsIgnoreCase("AddFavorite")) {
                addFavoriteResponse(response);
            } else if (action.equalsIgnoreCase("DeleteFavorite")) {
                deleteFavoriteResponse(response);
            } else if (action.equalsIgnoreCase("PlaceDetails")) {
                searchResponse(response);
            }
        } catch (Exception e) {
            // Tracking exception
            MyTorismaApplication.getInstance().trackException(e);
            Log.e(TAG, "onResponse Exception " + e.getLocalizedMessage());
        }
    }


    private void searchCall(String Place_Id) {
        if (!isForSimilarPlaces)
            ll_search_result2.setVisibility(View.GONE);
        if (CommonClass.hasInternetConnection(getActivity())) {
            String url = Constants.SERVER_URL + "json.php?action=PlaceDetails";
            String json = "[{\"Lan_Id\":\"" + mPreferences.getString("Lan_Id", "") + "\",\"User_Id\":\"" + mPreferences.getString("User_Id", "") + "\",\"Current_Latitude\":\"" + mPreferences.getString("latitude2", "") + "\",\"Current_Longitude\":\"" + mPreferences.getString("longitude2", "") + "\",\"Place_Id\":\"" + Place_Id + "\"}]";
//            Log.d("System out", "PlaceDetails " + json);
            new PostSync(getActivity(), "PlaceDetails", SearchResultPlaceDetailsFragment.this).execute(url, json);
        } else {
            Intent intent = new Intent(getActivity(), NoInternet.class);
            startActivity(intent);
            //SnackbarManager.show(Snackbar.with(getActivity()).color(Utils.getColor(getActivity(), R.color.mBlue)).text(Constants.showMessage(getActivity(), mPreferences.getString("Lan_Id", ""), "NOINTERNET")));
        }
    }

    public void searchResponse(String resultString) {
//        Log.d("System out", resultString);
        ll_search_result2.setVisibility(View.VISIBLE);
        try {
            String str = resultString.replaceAll("\\\\", "");
            JSONArray jsonArray = new JSONArray(resultString);
//            JSONArray jsonArray = new JSONArray(resultString);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.optJSONObject(i);

                mNearby = new Nearby();
                mNearby.setPlace_Id(jsonObject.optString("Place_Id"));
                mNearby.setCategory_Name(jsonObject.optString("Category_Name"));
                mNearby.setPlace_Name(jsonObject.optString("Place_Name"));
                mNearby.setPlace_ShortInfo(jsonObject.optString("Place_ShortInfo"));
                mNearby.setPlace_MainImage(jsonObject.optString("Place_MainImage"));
//                mNearby.setPlace_Description(jsonObject.optString("Place_Description"));
                if (jsonObject.optString("Place_Description") != null && !jsonObject.optString("Place_Description").equalsIgnoreCase("")) {

                    String price = jsonObject.optString("Place_Description");
                    //String a = price.replace("\\*", "");
//                    String b = price.replaceAll("\r", "");
//                    String c = b.replaceAll("\n", System.getProperty("line.separator"));
                    mNearby.setPlace_Description(price);

                } else {
                    mNearby.setPlace_Description(jsonObject.optString("Place_Description"));
                }

                mNearby.setPlace_Address(jsonObject.optString("Place_Address"));
                mNearby.setPlace_Latitude(jsonObject.optString("Place_Latitude"));

                if (jsonObject.optString("Price_Description") != null && !jsonObject.optString("Price_Description").equalsIgnoreCase("")) {
                    String price = jsonObject.optString("Price_Description");
                    //String a = price.replaceAll("\r", "");
//                    String b = a.replaceAll("\n", System.getProperty("line.separator"));
                    mNearby.setPrice_Description(price);

                } else {
                    mNearby.setPrice_Description(jsonObject.optString("Price_Description"));
                }
                mNearby.setPlace_Longi(jsonObject.optString("Place_Longi"));
                mNearby.setOtherimages(jsonObject.optString("otherimages"));
                mNearby.setDist(jsonObject.optString("dist"));
                mNearby.setFav_Id(jsonObject.optString("Fav_Id"));
                mNearby.setPlaceVRMainImage(jsonObject.optString("Place_VRMainImage"));
                mNearby.setVrimages(jsonObject.optString("vrimages"));
                mNearby.setFree_entry(jsonObject.optString("free_entry"));

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
                mNearby.setHourDetailsArrayList(detailsArrayList);

                nearbies1.clear();
                if (jsonObject.has("similar")) {
                    JSONArray jsonArray1 = jsonObject.optJSONArray("similar");

                    for (int intSimilar = 0; intSimilar < jsonArray1.length(); intSimilar++) {
                        Nearby nearby1 = jsonObjConverter.convertJsonToNearByObj(jsonArray1.optJSONObject(intSimilar));
                        nearby1.setDistance(Utilities.GetRoutDistane(Double.parseDouble(mPreferences.getString("latitude2", "")), Double.parseDouble(mPreferences.getString("longitude2", "")), Double.parseDouble(nearby1.getPlace_Latitude()), Double.parseDouble(nearby1.getPlace_Longi()), nearby1.getDist()));

                        nearbies1.add(nearby1);
                    }
                    nearbies1 = Utilities.sortLocations(nearbies1);
                }
                if (!isForSimilarPlaces)
                    setDetailInfo(mNearby);
                galleryAdapter2 = new GalleryAdapter2(getActivity());
                gv_detail1_search_result2.setAdapter(null);
                gv_detail1_search_result2.setAdapter(galleryAdapter2);

                if (nearbies1.size() == 0) {
                    ll_see_all.setVisibility(View.GONE);
                } else {
                    ll_see_all.setVisibility(View.VISIBLE);
                }
            }
        } catch (JSONException e) {
            // Tracking exception
            MyTorismaApplication.getInstance().trackException(e);
            e.printStackTrace();
        }
    }

    private void openWeekDaysPopup() {
        try {

            // Inflate the custom layout/view
            WindowManager wm = (WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE);
            Display display = wm.getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            int width = size.x;
            int height = size.y;
            dialog = new Dialog(getActivity());
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(false);
            dialog.getWindow()
                    .getAttributes().windowAnimations = R.style.DialogAnimation;
            dialog.setContentView(R.layout.popup_weekdays);
            Window window = dialog.getWindow();
            window.setLayout((width * 90) / 100, LinearLayout.LayoutParams.WRAP_CONTENT);
            ArrayList<WeekDaysModel> stringArrayList = new ArrayList<>();
            stringArrayList = getTimingArrayList();
            ListView listView = (ListView) dialog.findViewById(R.id.listview_week);
            NormalTextView txtTitle = (NormalTextView) dialog.findViewById(R.id.txtTitle);
            txtTitle.setText(Constants.showMessage(getActivity(), mPreferences.getString("Lan_Id", ""), "Time Details Header"));
            ImageView iv_menu_close = (ImageView) dialog.findViewById(R.id.iv_menu_close);
            iv_menu_close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            listView.setAdapter(new TimingAdapter(stringArrayList, getActivity()));

            dialog.show();
            //popupWindow.showAtLocation(txtOpenNowVal, Gravity.CENTER, 0, 0);


        } catch (Exception e) {
            // Tracking exception
            MyTorismaApplication.getInstance().trackException(e);
            Utils.Log(TAG, "openWeekDaysPopup Exception: " + e.getLocalizedMessage());
        }

    }

    public ArrayList<WeekDaysModel> getTimingArrayList() {
        ArrayList<WeekDaysModel> timingArrayList = new ArrayList<>();
        if (hourDetailses != null) {
            for (HourDetails hourDetails : hourDetailses) {
                WeekDaysModel weekDaysModel = new WeekDaysModel();
                weekDaysModel.setTime(TimingFunction.getTimingWeekDayFormat(getActivity(), hourDetails, mPreferences, hourDetailses));
                weekDaysModel.setDay(Constants.showMessage(getActivity(), mPreferences.getString("Lan_Id", ""), hourDetails.getPOHKey()));

                timingArrayList.add(weekDaysModel);
            }
        }

        return timingArrayList;
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
            // Tracking exception
            MyTorismaApplication.getInstance().trackException(e);
            e.printStackTrace();
        }

        return feesArrayList;
    }

    private void addFavoriteCall(String Place_Id) {
        if (CommonClass.hasInternetConnection(getActivity())) {
            String url = Constants.SERVER_URL + "json.php?action=AddFavorite";
            String json = "[{\"User_Id\":\"" + mPreferences.getString("User_Id", "") + "\",\"Place_Id\":\"" + Place_Id + "\"}]";
//            Log.d("System out", "AddFavorite " + json);
            new PostSync(getActivity(), "AddFavorite", SearchResultPlaceDetailsFragment.this).execute(url, json);
        } else {
            Intent intent = new Intent(getActivity(), NoInternet.class);
            startActivity(intent);
            //SnackbarManager.show((Snackbar) Snackbar.with(getActivity()).color(getResources().getColor(R.color.mBlue)).text(Constants.showMessage(getActivity(), mPreferences.getString("Lan_Id", ""), "NOINTERNET")));
        }
    }

    public void addFavoriteResponse(String resultString) {
//        Log.d("System out", resultString);
//        [{"Fav_Id":23,"status":"true"}]
        if (resultString.length() > 2) {
            try {
                JSONArray jsonArray = new JSONArray(resultString);
                JSONObject jsonObject = jsonArray.optJSONObject(0);
                if (jsonObject.has("status")) {
                    String str = jsonObject.optString("status");
                    if (str.equalsIgnoreCase("true")) {
//                        if (mCounter == -1) {
//                            nearbies.get(mFlag).setFav_Id(jsonObject.optString("Fav_Id"));
//                        }
//                        mCounter = -1;
                        mFlag = 0;
                        like = 1;
                        Constants.mStaticFavCall = 0;
                        SnackbarManager.show(Snackbar.with(getActivity()).color(getResources().getColor(R.color.mBlue)).text(Constants.showMessage(getActivity()
                                , mPreferences.getString("Lan_Id", ""), "AddFavourite")));
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

    private void deleteFavoriteCall(String Fav_Id) {
        if (CommonClass.hasInternetConnection(getActivity())) {
            String url = Constants.SERVER_URL + "json.php?action=DeleteFavorite";
            String json = "[{\"User_Id\":\"" + mPreferences.getString("User_Id", "") + "\",\"Fav_Id\":\"" + Fav_Id + "\"}]";
//            Log.d("System out", "DeleteFavorite " + json);
            new PostSync(getActivity(), "DeleteFavorite", SearchResultPlaceDetailsFragment.this).execute(url, json);
        } else {
            Intent intent = new Intent(getActivity(), NoInternet.class);
            startActivity(intent);
            //SnackbarManager.show((Snackbar) Snackbar.with(getActivity()).color(getResources().getColor(R.color.mBlue)).text(Constants.showMessage(getActivity(), mPreferences.getString("Lan_Id", ""), "NOINTERNET")));
        }
    }

    public void deleteFavoriteResponse(String resultString) {
//        Log.d("System out", resultString);
        if (resultString.length() > 2) {
            try {
                JSONArray jsonArray = new JSONArray(resultString);
                JSONObject jsonObject = jsonArray.optJSONObject(0);
                if (jsonObject.has("status")) {
                    String str = jsonObject.optString("status");
                    if (str.equalsIgnoreCase("true")) {
//                        if (mCounter == -1) {
//                            nearbies.get(mFlag).setFav_Id("0");
//                        }
                        mCounter = -1;
                        mFlag = 0;
                        like = 0;
                        Constants.mStaticFavCall = 0;
                        SnackbarManager.show(Snackbar.with(getActivity()).color(getResources().getColor(R.color.mBlue)).text(Constants.showMessage(getActivity(), mPreferences.getString("Lan_Id", ""), "Removefavorite")));
                    }
                }
            } catch (JSONException e) {
                // Tracking exception
                MyTorismaApplication.getInstance().trackException(e);
                e.printStackTrace();
            }
        }
    }

    private class GalleryAdapter2 extends BaseAdapter {
        Context context;

        public GalleryAdapter2(Context context) {
            this.context = context;
        }

        @Override
        public int getCount() {
            return nearbies1.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            LayoutInflater mLayoutInflater;
            mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = mLayoutInflater.inflate(R.layout.detail_gallery, parent, false);
            final ImageView iv_detail = (ImageView) convertView.findViewById(R.id.iv_detail);
            final NormalTextView tv_name = (NormalTextView) convertView.findViewById(R.id.tv_name);
            final NormalTextView tv_km = (NormalTextView) convertView.findViewById(R.id.tv_km);
            final LinearLayout llView = (LinearLayout) convertView.findViewById(R.id.llView);
            final RelativeLayout rlMain = (RelativeLayout) convertView.findViewById(R.id.rlMain);
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) rlMain.getLayoutParams();
            layoutParams.width = (width * 50) / 100;
            layoutParams.height = (width * 50) / 100;


            rlMain.setLayoutParams(layoutParams);

            tv_name.setText(nearbies1.get(position).getPlace_Name());
            tv_km.setText(nearbies1.get(position).getDistance() + Constants.showMessage(getActivity(), mPreferences.getString("Lan_Id", ""), "KM"));
            /*double d = Utilities.GetRoutDistane(Double.parseDouble(mPreferences.getString("latitude2", "")), Double.parseDouble(mPreferences.getString("longitude2", "")), Double.parseDouble(nearbies1.get(position).getPlace_Latitude()), Double.parseDouble(nearbies1.get(position).getPlace_Longi()), nearbies1.get(position).getDist());
            if(d!=-1)
            tv_km.setText(d + Constants.showMessage(getActivity(), mPreferences.getString("Lan_Id", ""), "KM"));
*/

            String imageUrl = Constants.IMAGE_URL + nearbies1.get(position).getPlace_MainImage() + "&w=" + (width);
            System.out.println("images_url" + imageUrl);

            Picasso.with(getActivity()) //
                    .load(imageUrl) //
                    .resize(width, width)
                    .into(iv_detail);

            gv_detail1_search_result2.setOnItemClickListener(new com.ftl.tourisma.gallery1.AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(com.ftl.tourisma.gallery1.AdapterView<?> parent, View view, int position, long id) {
                    id1 = position;

                    isForSimilarPlaces = true;
                    setDetailInfo(nearbies1.get(position));
                    mNearby = nearbies1.get(position);
                }
            });

            Log.i("System out", imageUrl);
            //  imageLoader2.displayImage(imageUrl, iv_detail, optionsSimple);

            return convertView;
        }
    }
}
