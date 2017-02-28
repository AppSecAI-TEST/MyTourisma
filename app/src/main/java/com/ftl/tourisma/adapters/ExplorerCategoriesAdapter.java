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
    private ArrayList<AllCategories> arrayList;
    Context context;
    boolean isMoreVisible;

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

        if (position == 3) {
            //  GridView.LayoutParams layoutParams= (GridView.LayoutParams)gv_explorer.getLayoutParams();
            //  layoutParams.s
        }
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                   /* if (position == 9) {
                        explorerAdapter1 = new ExplorerAdapter1(getActivity());
                        gv_explorer.setAdapter(null);
                        gv_explorer.setAdapter(explorerAdapter1);
                        gv_explorer.setExpanded(true);
                    } else {*/
               /* ll_cate.setVisibility(View.VISIBLE);
                iv_back3.setVisibility(View.VISIBLE);
                cate_name.setText(allCategories.get(position).getCategory_Name());
                cate_info.setText(allCategories.get(position).getCategory_Info());
                ll_cate1.setVisibility(View.GONE);
                Category_Name = allCategories.get(position).getCategory_Name();
                Category_Id = allCategories.get(position).getCategory_Id();
                cateId = Category_Id;
                isCalledFromCat = true;
                searchCall1();*/
                //  }
            }
        });


        //  ll_explorer.setBackgroundResource(R.drawable.ic_bg_category_);
        // expand(ll_explorer, false);
        tv_explore_grid.setText(arrayList.get(position).getCategory_Name());

        if (position % 2 == 0) {
            ll_right.setVisibility(View.INVISIBLE);
        }
        // gv_explorer.

        return convertView;
    }
}