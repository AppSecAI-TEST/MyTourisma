package com.ftl.tourisma.models;

import android.text.SpannableStringBuilder;

/**
 * Created by C162 on 09/11/16.
 */

public class WeekDaysModel {
    SpannableStringBuilder time;
    String day;
    boolean isCurrentDay;

    public SpannableStringBuilder getTime() {
        return time;
    }

    public void setTime(SpannableStringBuilder time) {
        this.time = time;
    }

    public boolean isCurrentDay() {
        return isCurrentDay;
    }

    public void setCurrentDay(boolean currentDay) {
        isCurrentDay = currentDay;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }
}
