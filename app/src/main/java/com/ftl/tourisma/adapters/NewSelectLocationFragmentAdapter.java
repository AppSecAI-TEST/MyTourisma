package com.ftl.tourisma.adapters;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ftl.tourisma.R;
import com.ftl.tourisma.activity.MainActivity;
import com.ftl.tourisma.models.IMAGE_MODEL;
import com.ftl.tourisma.models.NewCities;
import com.ftl.tourisma.utils.Constants;
import com.ftl.tourisma.utils.Preference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by VirtualDusk on 7/3/2017.
 */

public class NewSelectLocationFragmentAdapter extends RecyclerView.Adapter<NewSelectLocationFragmentAdapter.CategoryViewHolder> {

    ArrayList<NewCities> newCities = new ArrayList<>();
    ArrayList<IMAGE_MODEL> image_models = new ArrayList<>();
    Activity activity;
    private SharedPreferences mPreferences;
    private SharedPreferences.Editor mEditor;

    public NewSelectLocationFragmentAdapter(Activity activity, ArrayList<NewCities> newCities, ArrayList<IMAGE_MODEL> image_models) {
        this.newCities = newCities;
        this.image_models = image_models;
        this.activity = activity;
    }

    @Override
    public NewSelectLocationFragmentAdapter.CategoryViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Initializing the shared preference
        mPreferences = activity.getSharedPreferences(Constants.mPref, 0);
        mEditor = mPreferences.edit();

        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.new_select_location_fragment_recycler, viewGroup, false);
        return new NewSelectLocationFragmentAdapter.CategoryViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final NewSelectLocationFragmentAdapter.CategoryViewHolder CategoryViewHolder, final int position) {
        CategoryViewHolder.place_name_txt.setText(newCities.get(position).getCity_Name());
        CategoryViewHolder.city_info_txt.setText(Constants.showMessage(activity, mPreferences.getString("Lan_Id", ""), "City info"));
        Picasso.with(activity).load(image_models.get(position).getImage()).into(CategoryViewHolder.place_img);
        /*if (newCities.get(position).getCity_Name().contains("Abu Dhabi")) {
            CategoryViewHolder.place_img.setBackgroundResource(R.drawable.abudhabi);
        } else if (newCities.get(position).getCity_Name().contains("Ajman")) {
            CategoryViewHolder.place_img.setBackgroundResource(R.drawable.ajman);
        } else if (newCities.get(position).getCity_Name().contains("Al Ain")) {
            CategoryViewHolder.place_img.setBackgroundResource(R.drawable.alain);
        } else if (newCities.get(position).getCity_Name().contains("Dubai")) {
            CategoryViewHolder.place_img.setBackgroundResource(R.drawable.dubai);
        } else if (newCities.get(position).getCity_Name().contains("Fujairah")) {
            CategoryViewHolder.place_img.setBackgroundResource(R.drawable.fujairah);
        } else if (newCities.get(position).getCity_Name().contains("Hatta")) {
            CategoryViewHolder.place_img.setBackgroundResource(R.drawable.hatta);
        } else if (newCities.get(position).getCity_Name().contains("Sharjah ")) {
            CategoryViewHolder.place_img.setBackgroundResource(R.drawable.sharjah);
        } else if (newCities.get(position).getCity_Name().contains("Ras Al Khaimah ")) {
            CategoryViewHolder.place_img.setBackgroundResource(R.drawable.rasalkhaima);
        } else if (newCities.get(position).getCity_Name().contains("Umm al Quwain ")) {
            CategoryViewHolder.place_img.setBackgroundResource(R.drawable.ummalquain);
        }*/

        CategoryViewHolder.place_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.putString(Preference.Pref_City, newCities.get(position).getCity_Id_Name()).commit();
                Intent mIntent = new Intent(activity, MainActivity.class);
                Constants.mStatic = 0;
                Constants.mFromSelectLocation = 1;
                mIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                activity.startActivity(mIntent);
                activity.finish();
            }
        });

        CategoryViewHolder.rlBottomView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.putString(Preference.Pref_City, newCities.get(position).getCity_Id_Name()).commit();
                Intent mIntent = new Intent(activity, MainActivity.class);
                Constants.mStatic = 0;
                Constants.mFromSelectLocation = 1;
                mIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                activity.startActivity(mIntent);
                activity.finish();
            }
        });

        CategoryViewHolder.city_info_txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }

    @Override
    public int getItemCount() {
        return newCities.size();
    }

    public class CategoryViewHolder extends RecyclerView.ViewHolder {

        ImageView place_img;
        TextView city_info_txt, place_name_txt;
        RelativeLayout rlBottomView;

        public CategoryViewHolder(View v) {
            super(v);
            place_img = (ImageView) v.findViewById(R.id.place_img);
            city_info_txt = (TextView) v.findViewById(R.id.city_info_txt);
            place_name_txt = (TextView) v.findViewById(R.id.place_name_txt);
            rlBottomView = (RelativeLayout) v.findViewById(R.id.rlBottomView);
        }
    }
}
