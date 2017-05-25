package com.ftl.tourisma.custom_views;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.EditText;

import com.ftl.tourisma.MyTorismaApplication;


/**
 * Created by C162 on 06/09/16.
 */

public class NormalEditText extends EditText {
    public NormalEditText(Context context) {
        super(context);
        applyCustomFont();
    }

    public NormalEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        applyCustomFont();
    }

    public NormalEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        applyCustomFont();
    }

    private void applyCustomFont() {
        setTypeface(MyTorismaApplication.typeFace.getNormalFonts());
    }

    @Override
    public void setTypeface(Typeface tf) {
        super.setTypeface(tf);
    }
}
