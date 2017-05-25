package com.ftl.tourisma.adapters;

import android.content.Context;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.ftl.tourisma.MyTorismaApplication;
import com.ftl.tourisma.R;
import com.ftl.tourisma.custom_views.NormalTextView;
import com.ftl.tourisma.database.FeesDetails;
import com.ftl.tourisma.utils.CustomTypefaceSpan;

import java.util.ArrayList;

/**
 * Created by C162 on 07/11/16.
 */

public class FeesAdapter extends BaseAdapter {
    private Context context;
    private LayoutInflater layoutInflater;
    private ArrayList<FeesDetails> arrayList = new ArrayList<>();

    public FeesAdapter(Context context) {
        this.context = context;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
        View view = convertView;
        ViewHolder viewHolder;
        view = layoutInflater.inflate(R.layout.row_fees, null);
        viewHolder = new ViewHolder();
        viewHolder.txtFees = (NormalTextView) view.findViewById(R.id.txtFees);
        viewHolder.txtFees.setText("");
        viewHolder.txtFees.append(getSpannableString(arrayList.get(position).getFeesName() + ": ", true));
        viewHolder.txtFees.append(getSpannableString(arrayList.get(position).getFeesValue() + "", false));
//        }
        view.measure(View.MeasureSpec.makeMeasureSpec(
                View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        return view;
    }

    private SpannableStringBuilder getSpannableString(String s, boolean isBold) {
//        Typeface font = Typeface.createFromAsset(getAssets(), "Akshar.ttf");
        SpannableStringBuilder spannablecontent = new SpannableStringBuilder(s);
        if (isBold)
            spannablecontent.setSpan(new CustomTypefaceSpan("", MyTorismaApplication.typeFace.getBoldFonts()),
                    0, spannablecontent.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        else
            spannablecontent.setSpan(new CustomTypefaceSpan("", MyTorismaApplication.typeFace.getNormalFonts()),
                    0, spannablecontent.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        return spannablecontent;
    }

    public void addFeedList(ArrayList<FeesDetails> feesDetailsArrayList) {
        this.arrayList = feesDetailsArrayList;
    }

    class ViewHolder {
        private NormalTextView txtFees;
    }
}
