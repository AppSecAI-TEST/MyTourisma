package com.ftl.tourisma.app;

import android.graphics.Color;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;

/**
 * Created by VirtualDusk on 04-Aug-17.
 */

public class MySpannable extends ClickableSpan {

    private boolean isUnderline = true;

    /**
     * Constructor
     */
    public MySpannable(boolean isUnderline) {
        this.isUnderline = isUnderline;
    }

    @Override
    public void updateDrawState(TextPaint ds) {

        ds.setUnderlineText(isUnderline);
        ds.setColor(Color.BLUE);

    }

    @Override
    public void onClick(View widget) {

    }
}
