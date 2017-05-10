package com.ftl.tourisma.adapters;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ftl.tourisma.R;
import com.ftl.tourisma.models.LocationSearch;

import java.util.ArrayList;

/**
 * Created by Vinay on 5/10/2017.
 */

public class SearchLocationAdapter extends RecyclerView.Adapter<SearchLocationAdapter.CategoryViewHolder> {

    public static ArrayList<LocationSearch> locationSearches = new ArrayList<>();
    Activity activity;

    public SearchLocationAdapter(Activity activity, ArrayList<LocationSearch> locationSearches) {
        this.locationSearches = locationSearches;
        this.activity = activity;
    }

    @Override
    public CategoryViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_item, viewGroup, false);
        return new CategoryViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final CategoryViewHolder CategoryViewHolder, int position) {

        CategoryViewHolder.loc_txtvw.setText(locationSearches.get(position).getFullAddress());
    }

    @Override
    public int getItemCount() {
        return locationSearches.size();
    }

    public class CategoryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView loc_txtvw;

        public CategoryViewHolder(View v) {
            super(v);
            loc_txtvw = (TextView) v.findViewById(R.id.textView);
        }

        @Override
        public void onClick(View view) {

        }
    }
}
