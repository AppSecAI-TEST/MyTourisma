package com.ftl.tourisma.custom_views;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.Button;

import com.ftl.tourisma.MyTorismaApplication;


/**
 * Created by C162 on 06/09/16.
 */

public class NormalButton extends Button {
    public NormalButton(Context context) {
        super(context);
        applyCustomFont();
    }

    public NormalButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        applyCustomFont();
    }

    public NormalButton(Context context, AttributeSet attrs, int defStyleAttr) {
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
