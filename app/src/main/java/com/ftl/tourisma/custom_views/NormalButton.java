package com.ftl.tourisma.custom_views;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.Button;
import android.widget.EditText;

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

  /*  public NormalEditText(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        applyCustomFont();

    }*/

    private void applyCustomFont() {

        setTypeface(MyTorismaApplication.typeFace.getBoldFonts());
    }

    @Override
    public void setTypeface(Typeface tf) {
        super.setTypeface(tf);

    }
}
