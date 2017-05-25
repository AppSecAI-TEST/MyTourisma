package com.ftl.tourisma.custom_views;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;

import com.ftl.tourisma.MyTorismaApplication;

/**
 * Created by C162 on 06/09/16.
 */

public class NormalBoldTextView extends android.support.v7.widget.AppCompatTextView {
    public NormalBoldTextView(Context context) {
        super(context);
        applyCustomFont();
    }

    public NormalBoldTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        applyCustomFont();
    }

    public NormalBoldTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        applyCustomFont();

    }

    private void applyCustomFont() {
        setTypeface(MyTorismaApplication.typeFace.getBoldFonts());
    }

    @Override
    public void setTypeface(Typeface tf) {
        super.setTypeface(tf);
    }
}
