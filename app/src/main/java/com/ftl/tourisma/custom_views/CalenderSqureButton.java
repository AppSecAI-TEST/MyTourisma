package com.ftl.tourisma.custom_views;

import android.content.Context;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;

/**
 * Created by skpissay on 12-Jun-17.
 */

public class CalenderSqureButton extends AppCompatTextView {


    public CalenderSqureButton(Context context) {
        super(context);
    }

    public CalenderSqureButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
    }

    public CalenderSqureButton(Context context, AttributeSet attrs,
                               int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        // TODO Auto-generated constructor stub
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec);
        /*int lWidth = getMeasuredWidth();

		int lHight = lWidth -  (lWidth/3);
		setMeasuredDimension(lWidth, lHight);*/
    }
}
