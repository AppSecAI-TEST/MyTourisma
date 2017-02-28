package com.ftl.tourisma.utils;

import android.content.Context;
import android.graphics.Typeface;

import com.ftl.tourisma.R;

/**
 * Created by c197 on 19/07/16.
 */
public class MyTypeFace {

    private Context context;
    private Typeface normalFonts,boldFonts, mytiadProFonts;

    public MyTypeFace(Context context) {
        this.context = context;

        normalFonts= Typeface.createFromAsset(context.getAssets(), context.getString(R.string.font_normal_app));
        boldFonts= Typeface.createFromAsset(context.getAssets(), context.getString(R.string.font_bold_app));
        mytiadProFonts = Typeface.createFromAsset(context.getAssets(), context.getString(R.string.font_Muraid_Pro));


    }

    public Typeface getNormalFonts() {
        return normalFonts;
    }

    public Typeface getBoldFonts() {
        return boldFonts;
    }

    public Typeface getMytiadProFonts() {
        return mytiadProFonts;
    }
}
