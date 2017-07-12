package com.ftl.tourisma.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ftl.tourisma.R;
import com.ftl.tourisma.activity.MainActivity;
import com.ftl.tourisma.activity.SearchFragment;
import com.ftl.tourisma.models.IMAGE_MODEL;
import com.ftl.tourisma.models.NewCities;
import com.ftl.tourisma.utils.Constants;
import com.ftl.tourisma.utils.Preference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Vinay on 7/4/2017.
 */

public class NewSelectCityLocationFragmentAdapter extends RecyclerView.Adapter<NewSelectCityLocationFragmentAdapter.CategoryViewHolder> {

    public static ArrayList<NewCities> newCities = new ArrayList<>();
    ArrayList<IMAGE_MODEL> image_models = new ArrayList<>();
    Activity activity;
    PopupWindow popupWindow;
    View view;
    private SharedPreferences mPreferences;
    private SharedPreferences.Editor mEditor;

    public NewSelectCityLocationFragmentAdapter(Activity activity, ArrayList<NewCities> newCities, ArrayList<IMAGE_MODEL> image_models) {
        this.newCities = newCities;
        this.image_models = image_models;
        this.activity = activity;
    }

    @Override
    public NewSelectCityLocationFragmentAdapter.CategoryViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Initializing the shared preference
        mPreferences = activity.getSharedPreferences(Constants.mPref, 0);
        mEditor = mPreferences.edit();

        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.new_select_location_fragment_recycler, viewGroup, false);
        return new NewSelectCityLocationFragmentAdapter.CategoryViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final NewSelectCityLocationFragmentAdapter.CategoryViewHolder CategoryViewHolder, final int position) {
        CategoryViewHolder.place_name_txt.setText(newCities.get(position).getCity_Name());
        CategoryViewHolder.city_info_txt.setText(Constants.showMessage(activity, mPreferences.getString("Lan_Id", ""), "City info"));
        Picasso.with(activity).load(newCities.get(position).getCity_Main_Image()).into(CategoryViewHolder.place_img);

        CategoryViewHolder.place_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.putString(Preference.Pref_City, newCities.get(position).getCity_Id_Name()).commit();
                Fragment fragment = new SearchFragment();
                FragmentManager fragmentManager = ((MainActivity) activity).getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fram1, fragment, "tag").commit();
            }
        });

        CategoryViewHolder.rlBottomView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.putString(Preference.Pref_City, newCities.get(position).getCity_Id_Name()).commit();
                Fragment fragment = new SearchFragment();
                FragmentManager fragmentManager = ((MainActivity) activity).getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fram1, fragment, "tag").commit();
            }
        });

        CategoryViewHolder.city_info_txt.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                initiatePopupWindow(position);
            }
        });

    }

    @Override
    public int getItemCount() {
        return newCities.size();
    }

    private void initiatePopupWindow(final int position) {
        LayoutInflater layoutInflater = (LayoutInflater) activity.getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View view = layoutInflater.inflate(R.layout.city_info, null);
        WindowManager wm = (WindowManager) activity.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;
//        popupWindow = new PopupWindow(view, (width * 90) / 100, (height * 80) / 100, true);
        popupWindow = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, false);
        popupWindow.setTouchable(true);
        popupWindow.setFocusable(true);
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

        ImageView img_close = (ImageView) view.findViewById(R.id.img_close);
        ImageView next_img = (ImageView) view.findViewById(R.id.next_img);
        ImageView next_img_footer = (ImageView) view.findViewById(R.id.next_img_footer);

        TextView txtTitle = (TextView) view.findViewById(R.id.txtTitle);
        txtTitle.setText(Constants.showMessage(activity, mPreferences.getString("Lan_Id", ""), "View Attractions"));

        ImageView place_img_dialog = (ImageView) view.findViewById(R.id.place_img);
        Picasso.with(activity).load(newCities.get(position).getCity_Image()).into(place_img_dialog);

        TextView place_name_txt = (TextView) view.findViewById(R.id.place_name_txt);
        place_name_txt.setText(newCities.get(position).getCity_Name());

        TextView city_info = (TextView) view.findViewById(R.id.city_info);
        city_info.setText(Constants.showMessage(activity, mPreferences.getString("Lan_Id", ""), "City info"));

        TextView city_desc = (TextView) view.findViewById(R.id.city_desc);
        city_desc.setText(newCities.get(position).getCity_Description());

        ImageView img_close_footer = (ImageView) view.findViewById(R.id.img_close_footer);

        TextView txtTitle_footer = (TextView) view.findViewById(R.id.txtTitle_footer);
        txtTitle_footer.setText(Constants.showMessage(activity, mPreferences.getString("Lan_Id", ""), "View Attractions"));

        img_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });

        img_close_footer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });

        next_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.putString(Preference.Pref_City, newCities.get(position).getCity_Id_Name()).commit();
                Fragment fragment = new SearchFragment();
                FragmentManager fragmentManager = ((MainActivity) activity).getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fram1, fragment, "tag").commit();
                popupWindow.dismiss();
            }
        });

        next_img_footer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.putString(Preference.Pref_City, newCities.get(position).getCity_Id_Name()).commit();
                Fragment fragment = new SearchFragment();
                FragmentManager fragmentManager = ((MainActivity) activity).getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fram1, fragment, "tag").commit();
                popupWindow.dismiss();
            }
        });

        place_img_dialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.putString(Preference.Pref_City, newCities.get(position).getCity_Id_Name()).commit();
                Fragment fragment = new SearchFragment();
                FragmentManager fragmentManager = ((MainActivity) activity).getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fram1, fragment, "tag").commit();
                popupWindow.dismiss();
            }
        });

        txtTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.putString(Preference.Pref_City, newCities.get(position).getCity_Id_Name()).commit();
                Fragment fragment = new SearchFragment();
                FragmentManager fragmentManager = ((MainActivity) activity).getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fram1, fragment, "tag").commit();
                popupWindow.dismiss();
            }
        });

        txtTitle_footer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.putString(Preference.Pref_City, newCities.get(position).getCity_Id_Name()).commit();
                Fragment fragment = new SearchFragment();
                FragmentManager fragmentManager = ((MainActivity) activity).getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fram1, fragment, "tag").commit();
                popupWindow.dismiss();
            }
        });
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

