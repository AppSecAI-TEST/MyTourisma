package com.ftl.tourisma.custom_views;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;

import com.ftl.tourisma.MyTorismaApplication;

/**
 * Created by C162 on 06/09/16.
 */

public class NormalTextView extends android.support.v7.widget.AppCompatTextView {
    public NormalTextView(Context context) {
        super(context);
        applyCustomFont();
    }

    public NormalTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        applyCustomFont();

    }

    public NormalTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        applyCustomFont();

    }

    /*  public NormalTextView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
          if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
              super(context, attrs, defStyleAttr, defStyleRes);
          }
          applyCustomFont();

      }
  */


    private void applyCustomFont() {

        setTypeface(MyTorismaApplication.typeFace.getNormalFonts());
    }

    @Override
    public void setTypeface(Typeface tf) {
        super.setTypeface(tf);

    }
}
