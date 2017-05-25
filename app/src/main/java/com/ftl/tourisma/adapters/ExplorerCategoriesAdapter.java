package com.ftl.tourisma.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;

import com.ftl.tourisma.R;
import com.ftl.tourisma.custom_views.NormalTextView;
import com.ftl.tourisma.database.AllCategories;

import java.util.ArrayList;

/**
 * Created by C162 on 02/11/16.
 */

public class ExplorerCategoriesAdapter extends BaseAdapter {
    Context context;
    boolean isMoreVisible;
    private ArrayList<AllCategories> arrayList;

    public ExplorerCategoriesAdapter(Context context, boolean isMoreVisible, ArrayList<AllCategories> arrayList) {
        this.context = context;
        this.isMoreVisible = isMoreVisible;
        this.arrayList = arrayList;
    }

    @Override
    public int getCount() {
        return isMoreVisible ? 10 : arrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return arrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.explore_adapter, parent, false);
        final NormalTextView tv_explore_grid = (NormalTextView) convertView.findViewById(R.id.tv_explore_grid);
        final LinearLayout ll_right = (LinearLayout) convertView.findViewById(R.id.ll_right);
        tv_explore_grid.setText(arrayList.get(position).getCategory_Name());
        if (position % 2 == 0) {
            ll_right.setVisibility(View.INVISIBLE);
        }
        return convertView;
    }
}