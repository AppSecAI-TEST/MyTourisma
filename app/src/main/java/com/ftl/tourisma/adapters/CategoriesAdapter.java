package com.ftl.tourisma.adapters;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ftl.tourisma.R;
import com.ftl.tourisma.database.AllCategories;
import com.ftl.tourisma.models.IMAGE_MODEL;

import java.util.ArrayList;

/**
 * Created by Vinay on 7/14/2017.
 */

public class CategoriesAdapter extends RecyclerView.Adapter<CategoriesAdapter.CategoryViewHolder> {

    public static ArrayList<AllCategories> allCategories = new ArrayList<>();
    ArrayList<IMAGE_MODEL> image_models = new ArrayList<>();
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


    }

    @Override
    public int getItemCount() {
        return allCategories.size();
    }

    public class CategoryViewHolder extends RecyclerView.ViewHolder {

        public CategoryViewHolder(View v) {
            super(v);

        }
    }
}

