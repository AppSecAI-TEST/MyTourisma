package com.ftl.tourisma.adapters;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.ftl.tourisma.R;
import com.ftl.tourisma.custom_views.NormalTextView;
import com.ftl.tourisma.database.AllCategories;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Vinay on 7/14/2017.
 */

public class CategoriesAdapter extends RecyclerView.Adapter<CategoriesAdapter.CategoryViewHolder> {

    ArrayList<AllCategories> allCategories = new ArrayList<>();
    Activity activity;

    public CategoriesAdapter(Activity activity, ArrayList<AllCategories> allCategories) {
        this.allCategories = allCategories;
        this.activity = activity;
    }

    @Override
    public CategoriesAdapter.CategoryViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.new_home_categories_recycler, viewGroup, false);
        return new CategoriesAdapter.CategoryViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final CategoriesAdapter.CategoryViewHolder CategoryViewHolder, final int position) {

        CategoryViewHolder.category_name.setText(allCategories.get(position).getCategory_Name());
        CategoryViewHolder.place_count_txt.setText(allCategories.get(position).getCategory_Places());
        Picasso.with(activity)
                .load(allCategories.get(position).getCategory_Image())
                .into(CategoryViewHolder.category_img);

    }

    @Override
    public int getItemCount() {
        return allCategories.size();
    }

    public class CategoryViewHolder extends RecyclerView.ViewHolder {

        NormalTextView category_name, place_count_txt;
        ImageView category_img;

        public CategoryViewHolder(View v) {
            super(v);
            category_name = (NormalTextView) v.findViewById(R.id.category_name);
            place_count_txt = (NormalTextView) v.findViewById(R.id.place_count_txt);
            category_img = (ImageView) v.findViewById(R.id.category_img);
        }
    }
}

