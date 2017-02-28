package com.ftl.tourisma.custom_views;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by c166 on 27/04/16.
 */
public class ScrollDisabledRecyclerView extends RecyclerView {

    public ScrollDisabledRecyclerView(Context context) {
        super(context);
    }

    public ScrollDisabledRecyclerView(Context context,  AttributeSet attrs) {
        super(context, attrs);
    }

    public ScrollDisabledRecyclerView(Context context,  AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent e) {
        return false;
    }
}