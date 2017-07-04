package com.ftl.tourisma.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
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

    public static ArrayList<NewCities> newCities = new ArrayList<>();
    ArrayList<IMAGE_MODEL> image_models = new ArrayList<>();
    Activity activity;
    PopupWindow popupWindow;
    View view;
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
                Intent mIntent = new Intent(activity, MainActivity.class);
                Constants.mStatic = 0;
                Constants.mFromSelectLocation = 1;
                mIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                activity.startActivity(mIntent);
                activity.finish();
            }
        });

        next_img_footer.setOnClickListener(new View.OnClickListener() {
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

        place_img_dialog.setOnClickListener(new View.OnClickListener() {
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

        txtTitle.setOnClickListener(new View.OnClickListener() {
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

        txtTitle_footer.setOnClickListener(new View.OnClickListener() {
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
