package com.ftl.tourisma.adapters;

import android.app.Activity;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.ftl.tourisma.R;
import com.ftl.tourisma.custom_views.NormalTextView;
import com.ftl.tourisma.database.ReviewData;
import com.ftl.tourisma.database.TranslatorData;
import com.ftl.tourisma.utils.CircleImageView;
import com.ftl.tourisma.utils.Constants;

import java.util.ArrayList;


/**
 * Created by VirtualDusk on 25-Jul-17.
 */

public class AllReviewAdapter extends RecyclerView.Adapter<AllReviewAdapter.ViewHolder> {
    // todo API_KEY should not be stored in plain sight
    private static final String API_KEY = "AIzaSyAIKDHIzU5rIuuz0w6zROlUDDr3tHE59W8";
    TranslatorData td = new TranslatorData();
    ArrayList<ReviewData> reviewDatas;
    Activity activity;
    String description;
    boolean isCheck = true;
    String str_seemore, halfDescription;
    private SharedPreferences mPreferences;
    private SharedPreferences.Editor mEditor;

    public AllReviewAdapter(ArrayList<ReviewData> reviewDatas, Activity activity) {
        this.reviewDatas = reviewDatas;
        this.activity = activity;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mPreferences = activity.getSharedPreferences(Constants.mPref, 0);
        mEditor = mPreferences.edit();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.new_recycler_reviewitem, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        final ReviewData reviewData = reviewDatas.get(position);
        holder.nameText.setText(reviewData.getUser_Name());
        holder.placeText.setText(reviewData.getRev_Title());

        holder.dateText.setText(reviewData.getRev_Date());
        holder.statusText.setText(reviewData.getRev_Status());

        holder.placediscription.setText(reviewData.getRev_Desc());
        str_seemore = "...see more";


        halfDescription = reviewData.getRev_Desc().substring(0, reviewData.getRev_Desc().length() / 2);
//           mainDescription=mNearby.getPlace_Description().substring(String.format(mNearby.getPlace_Description()).length()/2);
        holder.placediscription.setText(halfDescription + str_seemore);

        holder.placediscription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isCheck) {
                    holder.placediscription.setMaxLines(10);
                    holder.placediscription.setText(reviewData.getRev_Desc() + str_seemore);
                    isCheck = false;
                } else {
                    holder.placediscription.setText(halfDescription + str_seemore);
                    isCheck = true;
                }
            }
        });


        Integer integer = new Integer(reviewData.getRev_Rating());
        if (integer == 1) {

            holder.imagstar1.setImageResource(R.drawable.staticstar);
            holder.statusText.setText(Constants.showMessage(activity, mPreferences.getString("Lan_Id", ""), "Bad"));

        } else if (integer == 2) {
            holder.imagstar1.setImageResource(R.drawable.staticstar);
            holder.imagstar2.setImageResource(R.drawable.staticstar);
            holder.statusText.setText(Constants.showMessage(activity, mPreferences.getString("Lan_Id", ""), "Average"));
        } else if (integer == 3) {
            holder.imagstar1.setImageResource(R.drawable.staticstar);
            holder.imagstar2.setImageResource(R.drawable.staticstar);
            holder.imagstar3.setImageResource(R.drawable.staticstar);
            holder.statusText.setText(Constants.showMessage(activity, mPreferences.getString("Lan_Id", ""), "Good"));
        } else if (integer == 4) {
            holder.imagstar1.setImageResource(R.drawable.staticstar);
            holder.imagstar2.setImageResource(R.drawable.staticstar);
            holder.imagstar3.setImageResource(R.drawable.staticstar);
            holder.imagstar4.setImageResource(R.drawable.staticstar);
            holder.statusText.setText(Constants.showMessage(activity, mPreferences.getString("Lan_Id", ""), "Very_good"));
        } else if (integer == 5) {
            holder.imagstar1.setImageResource(R.drawable.staticstar);
            holder.imagstar2.setImageResource(R.drawable.staticstar);
            holder.imagstar3.setImageResource(R.drawable.staticstar);
            holder.imagstar4.setImageResource(R.drawable.staticstar);
            holder.imagstar5.setImageResource(R.drawable.staticstar);
            holder.statusText.setText(Constants.showMessage(activity, mPreferences.getString("Lan_Id", ""), "Awesome"));
        }

//        holder.profilepic.setImageResource(reviewData.getUser_ProfilePic());


        holder.translateid.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                holder.changedescription.setVisibility(View.VISIBLE);
                holder.translateid.setVisibility(View.GONE);
//                holder.changedescription.setText(td.getTranslatedText());
//

            }
        });
        holder.changedescription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.changedescription.setVisibility(View.GONE);
                holder.translateid.setVisibility(View.VISIBLE);


            }
        });
    }

    @Override
    public int getItemCount() {
        return reviewDatas.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        NormalTextView nameText, dateText, placeText, statusText, changedescription, placediscription;
        NormalTextView translateid;
        ImageView imagstar1, imagstar2, imagstar3, imagstar4, imagstar5;
        CircleImageView profilepic;

        public ViewHolder(View itemView) {
            super(itemView);
            nameText = (NormalTextView) itemView.findViewById(R.id.textnameid);
            dateText = (NormalTextView) itemView.findViewById(R.id.dateid);
            placeText = (NormalTextView) itemView.findViewById(R.id.placeid);
            statusText = (NormalTextView) itemView.findViewById(R.id.goodid);
            placediscription = (NormalTextView) itemView.findViewById(R.id.placediscription);
            changedescription = (NormalTextView) itemView.findViewById(R.id.changedescription);
            translateid = (NormalTextView) itemView.findViewById(R.id.translateid);

            imagstar1 = (ImageView) itemView.findViewById(R.id.img1);
            imagstar2 = (ImageView) itemView.findViewById(R.id.img2);
            imagstar3 = (ImageView) itemView.findViewById(R.id.img3);
            imagstar4 = (ImageView) itemView.findViewById(R.id.img4);
            imagstar5 = (ImageView) itemView.findViewById(R.id.img5);
            profilepic = (CircleImageView) itemView.findViewById(R.id.cv_my_profile);
        }
    }

//
//
//
//


}
