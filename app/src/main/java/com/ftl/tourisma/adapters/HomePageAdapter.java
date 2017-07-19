package com.ftl.tourisma.adapters;

import android.app.Activity;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.ftl.tourisma.MyTorismaApplication;
import com.ftl.tourisma.R;
import com.ftl.tourisma.custom_views.NormalTextView;
import com.ftl.tourisma.database.Nearby;
import com.ftl.tourisma.models.HourDetails;
import com.ftl.tourisma.utils.Constants;
import com.ftl.tourisma.utils.Utils;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static com.ftl.tourisma.utils.Constants.PlaceClosed;
import static com.ftl.tourisma.utils.Constants.PlaceOpenFor24Hours;
import static com.ftl.tourisma.utils.Constants.PlaceOpenWithAnyTime;

/**
 * Created by GRV on 6/20/2017.
 */

public class HomePageAdapter extends RecyclerView.Adapter<HomePageAdapter.ViewHolder> {

    public static ArrayList<Nearby> nearbies = new ArrayList<>();
    public int height;
    public int width;
    Activity activity;
    private String _24HourTime, _24HourTime1;
    private Date _24HourDt, _24HourDt1;
    private SimpleDateFormat _24HourSDF, _12HourSDF;
    private SharedPreferences mPreferences;
    private SharedPreferences.Editor mEditor;

    public HomePageAdapter(Activity activity, ArrayList<Nearby> nearbies) {
        this.nearbies = nearbies;
        this.activity = activity;
    }

    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
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

        ViewHolder vh = new ViewHolder(v);
        return vh;
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
        } else if (nearbies.get(position).getFree_entry().equals("1")) {
            holder.ticket_txt.setText(Constants.showMessage(activity, mPreferences.getString("Lan_Id", ""), "Free Entry"));
        }
        holder.dist_txt.setText(nearbies.get(position).getDist());

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
                            holder.timing_txt.setText(_24HourSDF.format(_24HourDt) + " " + Constants.showMessage(activity, mPreferences.getString("Lan_Id", ""), "TO") + " " + _24HourSDF.format(_24HourDt1));
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

        if (nearbies.get(position).getFav_Id().equalsIgnoreCase("0")) {
            holder.imgFav.setActivated(false);
        } else {
            holder.imgFav.setActivated(true);
        }

    }

    @Override
    public int getItemCount() {
        return nearbies.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView nearby_img, imgFav;
        NormalTextView category_txt, place_txt, timing_txt, ticket_txt, dist_txt;

        public ViewHolder(View view) {
            super(view);
            nearby_img = (ImageView) view.findViewById(R.id.nearby_img);
            imgFav = (ImageView) view.findViewById(R.id.imgFav);
            category_txt = (NormalTextView) view.findViewById(R.id.category_txt);
            place_txt = (NormalTextView) view.findViewById(R.id.place_txt);
            timing_txt = (NormalTextView) view.findViewById(R.id.timing_txt);
            ticket_txt = (NormalTextView) view.findViewById(R.id.ticket_txt);
            dist_txt = (NormalTextView) view.findViewById(R.id.dist_txt);
        }
    }
}