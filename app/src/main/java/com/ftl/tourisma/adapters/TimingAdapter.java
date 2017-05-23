package com.ftl.tourisma.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.ftl.tourisma.R;
import com.ftl.tourisma.custom_views.NormalTextView;
import com.ftl.tourisma.models.WeekDaysModel;
import com.ftl.tourisma.utils.Constants;
import com.ftl.tourisma.utils.Utils;

import java.util.ArrayList;

import static com.ftl.tourisma.utils.TimingFunction.CURRENT_DAY_FONTS;
import static com.ftl.tourisma.utils.TimingFunction.NORMAL_FONTS;

/**
 * Created by C162 on 09/11/16.
 */

public class TimingAdapter extends BaseAdapter {


    private ArrayList<WeekDaysModel> arrayList;
    private Context context;
    private LayoutInflater layoutInflater;
    private SharedPreferences mPreferences;
    private SharedPreferences.Editor mEditor;

    public TimingAdapter(ArrayList<WeekDaysModel> arrayList, Context context) {
        this.arrayList = arrayList;
        this.context = context;
        this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return arrayList.size();
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
    public View getView(int position, View convertView, ViewGroup parent) {
        mPreferences = context.getSharedPreferences(Constants.mPref, 0);
        mEditor = mPreferences.edit();
        if (mPreferences.getString("Lan_Id", "").equals("8")) {
            convertView = layoutInflater.inflate(R.layout.row_timing_arabic, parent, false);
        } else {
            convertView = layoutInflater.inflate(R.layout.row_timing, parent, false);
        }
        NormalTextView txtDay = (NormalTextView) convertView.findViewById(R.id.txtDay);
        NormalTextView txtTimeStatus = (NormalTextView) convertView.findViewById(R.id.txtTimeStatus);
        txtTimeStatus.setText(arrayList.get(position).getTime());
        txtDay.setText("");
        Rect bounds = new Rect();

        txtTimeStatus.getPaint().getTextBounds(txtTimeStatus.getText().toString(), 0, txtTimeStatus.getText().length(), bounds);
        Utils.Log("TimingAdapter", "Font color -> " + txtTimeStatus.getCurrentTextColor() + " == " + Utils.getColor(context, R.color.textColor));
        Utils.Log("TimingAdapter", "Font size -> " + txtTimeStatus.getTextSize());
//        txtDay.setTextSize(txtTimeStatus.getH());
        if (txtTimeStatus.getCurrentTextColor() == Utils.getColor(context, R.color.textColor)) {
//            Utils.getSpannableString(arrayList.get(position).getDay(), Utils.getColor(context, R.color.textColor), false, NORMAL_FONTS);
            txtDay.setText(Utils.getSpannableString(arrayList.get(position).getDay(), Utils.getColor(context, R.color.textColor), false, NORMAL_FONTS));
        } else {
            txtDay.setText(Utils.getSpannableString(arrayList.get(position).getDay(), Utils.getColor(context, R.color.textColor), false, CURRENT_DAY_FONTS));

        }
        return convertView;
    }
}
