package com.ftl.tourisma.custom_views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

/**
 * Created by skpissay on 12-Jun-17.
 */

public class CalendarGridView extends GridView {
    public CalendarGridView(Context context) {
        super(context);
    }

    public CalendarGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CalendarGridView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, MeasureSpec.UNSPECIFIED);
    }

}
