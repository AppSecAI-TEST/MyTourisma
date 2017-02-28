package com.ftl.tourisma.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.SpannableStringBuilder;
import android.util.Log;

import com.ftl.tourisma.R;
import com.ftl.tourisma.models.HourDetails;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import static com.ftl.tourisma.utils.Constants.PlaceClosed;
import static com.ftl.tourisma.utils.Constants.PlaceOpenFor24Hours;
import static com.ftl.tourisma.utils.Constants.PlaceOpenWithAnyTime;

/**
 * Created by c162 on 24/11/16.
 */

public class TimingFunction {
    public static final float NORMAL_FONTS = 0.8f;
    public static final float CURRENT_DAY_FONTS = 0.9f;
    private static final String TAG = TimingFunction.class.getSimpleName();
    private static String _24HourTime;
    private static String _24HourTime1;
    private static SimpleDateFormat _24HourSDF = new SimpleDateFormat("HH:mm");

    private static Date _24HourDt, _24HourDtBreakstart, _24HourDt1BreakEnd;
    private static Date _24HourDt1;

    public static SpannableStringBuilder getTimingWeekDayFormat(Context context, HourDetails hourDetails, SharedPreferences mPreferences, ArrayList<HourDetails> hourDetailses) {
        boolean isCurrentDay = hourDetails.getPOHKey().equals(Utils.getCurrentDay());
        boolean dayFound = false;
        //Checking for if yeasterday's timing is open for place
        if (isCurrentDay) {
            for (HourDetails hourDetailsYesterday : hourDetailses) {
                if (hourDetailsYesterday.getPOHDay().equalsIgnoreCase(Utils.getYesterDayDay())) {
                    if (hourDetailsYesterday.getPOHIsOpen().equals(PlaceOpenWithAnyTime)) {
                        SpannableStringBuilder spannableStringBuilder = TimingFunction.checkYesterDayTiming(context, hourDetails, hourDetailsYesterday, mPreferences);
                        if (spannableStringBuilder != null)
                            dayFound = true;
                    }
                    break;
                }
            }
        }

        if (hourDetails.getPOHIsOpen().equals(PlaceOpenFor24Hours)) {

            //open now
            if (isCurrentDay)
                return Utils.getSpannableString(Constants.showMessage(context, mPreferences.getString("Lan_Id", ""), "Open"), Utils.getColor(context, R.color.mGreen), true, CURRENT_DAY_FONTS);
            else
                return Utils.getSpannableString(Constants.showMessage(context, mPreferences.getString("Lan_Id", ""), "Open"), Utils.getColor(context, R.color.textColor), false, NORMAL_FONTS);

        } else if (hourDetails.getPOHIsOpen().equals(PlaceClosed)) {
            //Closed
        } else {
            if (hourDetails.getPOHStartTime().equals("00:00:00") && hourDetails.getPOHEndTime().equals("00:00:00")) {

            } else if (hourDetails.getPOHStartTime().equalsIgnoreCase("null") && hourDetails.getPOHEndTime().equals("null")) {
            } else {
                _24HourTime = hourDetails.getPOHStartTime();
                _24HourTime1 = hourDetails.getPOHEndTime();
                String _24HourTimeBreakStart = hourDetails.getpOHBreakStart();
                String _24HourTimeBreakEnd = hourDetails.getpOHBreakEnd();
//                _24HourSDF = new SimpleDateFormat("HH:mm");
//                _12HourSDF = new SimpleDateFormat("hh:mma");

                try {
                    _24HourDt = _24HourSDF.parse(_24HourTime);
                    _24HourDt1 = _24HourSDF.parse(_24HourTime1);


                    String time = _24HourSDF.format(_24HourDt) + " " + Constants.showMessage(context, mPreferences.getString("Lan_Id", ""), "TO") + " " + _24HourSDF.format(_24HourDt1);

                    String breakTime = "";
                    if (_24HourTimeBreakEnd != null && _24HourTimeBreakStart != null) {
                        _24HourDtBreakstart = _24HourSDF.parse(_24HourTimeBreakStart);
                        _24HourDt1BreakEnd = _24HourSDF.parse(_24HourTimeBreakEnd);
                        //Utils.toast("11 true"+date);
                        breakTime = "\n( " + Constants.showMessage(context, mPreferences.getString("Lan_Id", ""), "Break Time") + ": " + _24HourSDF.format(_24HourDtBreakstart) + " " + Constants.showMessage(context, mPreferences.getString("Lan_Id", ""), "TO") + " " + _24HourSDF.format(_24HourDt1BreakEnd) + " )";

                    }

                    if (isCurrentDay) {
                        Calendar calendar = Calendar.getInstance();
                        Date c = calendar.getTime();
                        int hour = calendar.get(Calendar.HOUR_OF_DAY);
                        int minute = calendar.get(Calendar.MINUTE);
                        android.text.format.DateFormat dateFormat = new android.text.format.DateFormat();
                        boolean is24HourFormat = dateFormat.is24HourFormat(context);
                        Date date;
                        if (is24HourFormat) {
                            c = _24HourSDF.parse(hour + ":" + minute);
                        } else {
                            String formattedDate = _24HourSDF.format(c.getTime());
                            c = _24HourSDF.parse(formattedDate);
                            //   date = .parse(c.get + ":" + minute+" "+calendar.get);
                        }
                        long timeInMilliseconds = c.getTime();
                        long timeInMilliseconds1 = _24HourDt.getTime();
                        long timeInMilliseconds2 = _24HourDt1.getTime();

                        if (timeInMilliseconds2 <= timeInMilliseconds1) {
                            if (timeInMilliseconds1 <= timeInMilliseconds && timeInMilliseconds2 <= timeInMilliseconds) {
                                return Utils.getSpannableString(time, Utils.getColor(context, R.color.mGreen), true, CURRENT_DAY_FONTS).append(Utils.getSpannableString(breakTime, Utils.getColor(context, R.color.mGreen), true, NORMAL_FONTS));
                            }

                            if (_24HourDtBreakstart != null && _24HourDt1BreakEnd != null) {
                                long timeInMilliseconds3 = _24HourDtBreakstart.getTime();
                                long timeInMilliseconds4 = _24HourDt1BreakEnd.getTime();
                                if (timeInMilliseconds3 <= timeInMilliseconds && timeInMilliseconds4 >= timeInMilliseconds) {
                                    //Utils.toast("11 true"+date);
                                    return Utils.getSpannableString(time, Utils.getColor(context, android.R.color.holo_red_dark), true, CURRENT_DAY_FONTS).append(Utils.getSpannableString(breakTime, Utils.getColor(context, android.R.color.holo_red_dark), true, NORMAL_FONTS));
                                } else {
                                    return Utils.getSpannableString(time, Utils.getColor(context, R.color.mGreen), true, CURRENT_DAY_FONTS).append(Utils.getSpannableString(breakTime, Utils.getColor(context, R.color.mGreen), true, NORMAL_FONTS));

                                }
                            }
                            return Utils.getSpannableString(time, Utils.getColor(context, android.R.color.holo_red_dark), true, CURRENT_DAY_FONTS).append(Utils.getSpannableString(breakTime, Utils.getColor(context, android.R.color.holo_red_dark), true, NORMAL_FONTS));
                        } else if (timeInMilliseconds1 <= timeInMilliseconds && timeInMilliseconds2 >= timeInMilliseconds) {

                            if (_24HourDtBreakstart != null && _24HourDt1BreakEnd != null) {
                                long timeInMilliseconds3 = _24HourDtBreakstart.getTime();
                                long timeInMilliseconds4 = _24HourDt1BreakEnd.getTime();
                                if (timeInMilliseconds3 <= timeInMilliseconds && timeInMilliseconds4 >= timeInMilliseconds) {
                                    //Utils.toast("11 true"+date);

                                    return Utils.getSpannableString(time, Utils.getColor(context, android.R.color.holo_red_dark), true, CURRENT_DAY_FONTS).append(Utils.getSpannableString(breakTime, Utils.getColor(context, android.R.color.holo_red_dark), true, NORMAL_FONTS));
                                } else {
                                    return Utils.getSpannableString(time, Utils.getColor(context, R.color.mGreen), true, CURRENT_DAY_FONTS).append(Utils.getSpannableString(breakTime, Utils.getColor(context, R.color.mGreen), true, NORMAL_FONTS));

                                }
                            }
                            return Utils.getSpannableString(time, Utils.getColor(context, R.color.mGreen), true, CURRENT_DAY_FONTS);
                        } else {
                            return Utils.getSpannableString(time, Utils.getColor(context, android.R.color.holo_red_dark), true, CURRENT_DAY_FONTS).append(Utils.getSpannableString(breakTime, Utils.getColor(context, android.R.color.holo_red_dark), true, NORMAL_FONTS));
                        }
                    } else {
                        return Utils.getSpannableString(time + breakTime, Utils.getColor(context, R.color.textColor), false, NORMAL_FONTS);

                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }


    /*    if (hourDetails.getPOHIsOpen().equals(PlaceOpenFor24Hours)) {
            if (hourDetails.getPOHStartTime().equals("00:00:00") && hourDetails.getPOHEndTime().equals("00:00:00")) {
                //Open status only
                if (isCurrentDay)
                    return Utils.getSpannableString(Constants.showMessage(context, mPreferences.getString("Lan_Id", ""), "Open"), Utils.getColor(context, R.color.mGreen), true);
                else
                    return Utils.getSpannableString(Constants.showMessage(context, mPreferences.getString("Lan_Id", ""), "Open"), Utils.getColor(context, R.color.textColor), true);
            } else if (hourDetails.getPOHEndTime().equals(hourDetails.getPOHStartTime())) {
                //Open status only beacuse both times are same

                if (isCurrentDay)
                    return Utils.getSpannableString(Constants.showMessage(context, mPreferences.getString("Lan_Id", ""), "Open"), Utils.getColor(context, R.color.mGreen), true);
                else
                    return Utils.getSpannableString(Constants.showMessage(context, mPreferences.getString("Lan_Id", ""), "Open"), Utils.getColor(context, R.color.textColor), true);

            } else {
                _24HourTime = hourDetails.getPOHStartTime();
                _24HourTime1 = hourDetails.getPOHEndTime();
//                _24HourSDF = new SimpleDateFormat("HH:mm");
//                _12HourSDF = new SimpleDateFormat("hh:mma");

                try {
                    _24HourDt = _24HourSDF.parse(_24HourTime);
                    _24HourDt1 = _24HourSDF.parse(_24HourTime1);
                    String time = _12HourSDF.format(_24HourDt).toString().replace("AM", Constants.showMessage(context, mPreferences.getString("Lan_Id", ""), "am")).replace("PM", Constants.showMessage(context, mPreferences.getString("Lan_Id", ""), "pm")) + " " + Constants.showMessage(context, mPreferences.getString("Lan_Id", ""), "TO") + " " + _12HourSDF.format(_24HourDt1).toString().replace("AM", Constants.showMessage(context, mPreferences.getString("Lan_Id", ""), "am")).replace("PM", Constants.showMessage(context, mPreferences.getString("Lan_Id", ""), "pm"));

                    if (isCurrentDay) {
                        Calendar calendar = Calendar.getInstance();
                        Date c = calendar.getTime();
                        int hour = calendar.get(Calendar.HOUR);
                        int minute = calendar.get(Calendar.MINUTE);
                        android.text.format.DateFormat dateFormat = new android.text.format.DateFormat();
                        boolean is24HourFormat = dateFormat.is24HourFormat(context);
                        Date date;
                        if (is24HourFormat) {
                            date = _24HourSDF.parse(hour + ":" + minute);
                        } else {
                            String formattedDate = _24HourSDF.format(c.getTime());
//                    SimpleDateFormat displayFormat = new SimpleDateFormat("HH:mm");
//                    SimpleDateFormat parseFormat = new SimpleDateFormat("hh:mm a");
//                    SimpleDateFormat _12HourSDFtemp = new SimpleDateFormat("hh:mm");
                            c = _24HourSDF.parse(formattedDate);
                            //   date = .parse(c.get + ":" + minute+" "+calendar.get);
                        }
                        long timeInMilliseconds = c.getTime();
                        long timeInMilliseconds1 = _24HourDt.getTime();
                        long timeInMilliseconds2 = _24HourDt1.getTime();
                        Log.e(TAG, "timeInMilliseconds : " + timeInMilliseconds);
                        Log.e(TAG, "timeInMilliseconds1 : " + timeInMilliseconds1);
                        Log.e(TAG, "timeInMilliseconds2 : " + timeInMilliseconds2);
                        if (timeInMilliseconds1 <= timeInMilliseconds && timeInMilliseconds2 >= timeInMilliseconds) {
                            //Utils.toast("11 true"+date);
                            return Utils.getSpannableString(time, Utils.getColor(context, R.color.mGreen), true);
//                    spannableStringBuilder = Utils.getSpannableString(Constants.showMessage(context, mPreferences.getString("Lan_Id", ""), "Open Now") + ": ", Utils.getColor(context, R.color.mGreen), true);
                        } else {
                            return Utils.getSpannableString(time, Utils.getColor(context, android.R.color.holo_red_dark), true);

//                    spannableStringBuilder = Utils.getSpannableString(Constants.showMessage(context, mPreferences.getString("Lan_Id", ""), "Closed") + ": ", Utils.getColor(context, android.R.color.holo_red_dark), true);
                            // Utils.toast("22 false"+date);
                        }
                    } else {
                        return Utils.getSpannableString(time, Utils.getColor(context, R.color.textColor), false);

                    }

                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        } else {
            if (isCurrentDay)
                return Utils.getSpannableString(Constants.showMessage(context, mPreferences.getString("Lan_Id", ""), "Closed"), Utils.getColor(context, android.R.color.holo_red_dark), true);
            else
                return Utils.getSpannableString(Constants.showMessage(context, mPreferences.getString("Lan_Id", ""), "Closed"), Utils.getColor(context, R.color.textColor), false);
        }*/

        if (isCurrentDay)
            return Utils.getSpannableString(Constants.showMessage(context, mPreferences.getString("Lan_Id", ""), "Closed"), Utils.getColor(context, android.R.color.holo_red_dark), true, CURRENT_DAY_FONTS);
        else
            return Utils.getSpannableString(Constants.showMessage(context, mPreferences.getString("Lan_Id", ""), "Closed"), Utils.getColor(context, R.color.textColor), false, NORMAL_FONTS);

    }

    public static SpannableStringBuilder getPlaceTiming(Context context, HourDetails hourDetails, SharedPreferences mPreferences) {
        SpannableStringBuilder spannableStringBuilder = null;

        if (hourDetails.getPOHStartTime().equalsIgnoreCase("null") && hourDetails.getPOHEndTime().equalsIgnoreCase("null")) {
            //Open status only

            return null;
        } else if (hourDetails.getPOHStartTime().equals("00:00:00") && hourDetails.getPOHEndTime().equals("00:00:00")) {
            //Open status only

            return null;
        } else if (hourDetails.getPOHEndTime().equals(hourDetails.getPOHStartTime())) {
            //Open status only beacuse both times are same
            return null;

        } else {
            _24HourTime = hourDetails.getPOHStartTime();
            _24HourTime1 = hourDetails.getPOHEndTime();
            try {
                _24HourDt = _24HourSDF.parse(_24HourTime);
                _24HourDt1 = _24HourSDF.parse(_24HourTime1);
                String _24HourTimeBreakStart = hourDetails.getpOHBreakStart();
                String _24HourTimeBreakEnd = hourDetails.getpOHBreakEnd();
                String breakTime = "";
                if (_24HourTimeBreakEnd != null && _24HourTimeBreakStart != null) {
                    _24HourDtBreakstart = _24HourSDF.parse(_24HourTimeBreakStart);
                    _24HourDt1BreakEnd = _24HourSDF.parse(_24HourTimeBreakEnd);
                    //Utils.toast("11 true"+date);
                    breakTime = "\n( " + Constants.showMessage(context, mPreferences.getString("Lan_Id", ""), "Break Time") + ": " + _24HourSDF.format(_24HourDtBreakstart) + " " + Constants.showMessage(context, mPreferences.getString("Lan_Id", ""), "TO") + " " + _24HourSDF.format(_24HourDt1BreakEnd) + " )";

                }
                Calendar calendar = Calendar.getInstance();
                Date c = calendar.getTime();
                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                int minute = calendar.get(Calendar.MINUTE);
                android.text.format.DateFormat dateFormat = new android.text.format.DateFormat();
                boolean is24HourFormat = dateFormat.is24HourFormat(context);
                Date date;
                if (is24HourFormat) {
                    c = _24HourSDF.parse(hour + ":" + minute);
                } else {
                    String formattedDate = _24HourSDF.format(c.getTime());
                    c = _24HourSDF.parse(formattedDate);
                }
                long timeInMilliseconds = c.getTime();
                long timeInMilliseconds1 = _24HourDt.getTime();
                long timeInMilliseconds2 = _24HourDt1.getTime();

                if (timeInMilliseconds2 <= timeInMilliseconds1) {
                    if (timeInMilliseconds1 <= timeInMilliseconds && timeInMilliseconds2 <= timeInMilliseconds) {
                        spannableStringBuilder = Utils.getSpannableString(Constants.showMessage(context, mPreferences.getString("Lan_Id", ""), "Open Now") + ": ", Utils.getColor(context, R.color.mGreen), true, 0);
                    } else {
                        spannableStringBuilder = Utils.getSpannableString(Constants.showMessage(context, mPreferences.getString("Lan_Id", ""), "Closed") + ": ", Utils.getColor(context, android.R.color.holo_red_dark), true, 0);
                    }
                    if (_24HourDtBreakstart != null && _24HourDt1BreakEnd != null) {
                        long timeInMilliseconds3 = _24HourDtBreakstart.getTime();
                        long timeInMilliseconds4 = _24HourDt1BreakEnd.getTime();
                        if (timeInMilliseconds3 <= timeInMilliseconds && timeInMilliseconds4 >= timeInMilliseconds) {
                            //Utils.toast("11 true"+date);
                            spannableStringBuilder = Utils.getSpannableString(Constants.showMessage(context, mPreferences.getString("Lan_Id", ""), "Break Time") + ": ", Utils.getColor(context, android.R.color.holo_red_dark), true, 0);
                        } else {
                            spannableStringBuilder = Utils.getSpannableString(Constants.showMessage(context, mPreferences.getString("Lan_Id", ""), "Open Now") + ": ", Utils.getColor(context, R.color.mGreen), true, 0);

                        }
                    }
                } else if (timeInMilliseconds1 <= timeInMilliseconds && timeInMilliseconds2 >= timeInMilliseconds) {
                    //Utils.toast("11 true"+date);
                    //TODO here need to manage break time

                    if (_24HourDtBreakstart != null && _24HourDt1BreakEnd != null) {
                        long timeInMilliseconds3 = _24HourDtBreakstart.getTime();
                        long timeInMilliseconds4 = _24HourDt1BreakEnd.getTime();
                        if (timeInMilliseconds3 <= timeInMilliseconds && timeInMilliseconds4 >= timeInMilliseconds) {
                            //Utils.toast("11 true"+date);
                            spannableStringBuilder = Utils.getSpannableString(Constants.showMessage(context, mPreferences.getString("Lan_Id", ""), "Break Time") + ": ", Utils.getColor(context, android.R.color.holo_red_dark), true, 0);
                        } else {
                            spannableStringBuilder = Utils.getSpannableString(Constants.showMessage(context, mPreferences.getString("Lan_Id", ""), "Open Now") + ": ", Utils.getColor(context, R.color.mGreen), true, 0);

                        }
                    } else {


                        spannableStringBuilder = Utils.getSpannableString(Constants.showMessage(context, mPreferences.getString("Lan_Id", ""), "Open Now") + ": ", Utils.getColor(context, R.color.mGreen), true, 0);
                    }
                } else {
                    ///if()
                    spannableStringBuilder = Utils.getSpannableString(Constants.showMessage(context, mPreferences.getString("Lan_Id", ""), "Closed") + ": ", Utils.getColor(context, android.R.color.holo_red_dark), true, 0);
                    // Utils.toast("22 false"+date);
                }

            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        spannableStringBuilder.append(_24HourSDF.format(_24HourDt) + " " + Constants.showMessage(context, mPreferences.getString("Lan_Id", ""), "TO") + " " + _24HourSDF.format(_24HourDt1));
//        spannableStringBuilder.append(_12HourSDF.format(_24HourDt).replace("AM", Constants.showMessage(context, mPreferences.getString("Lan_Id", ""), "am")).replace("PM", Constants.showMessage(context, mPreferences.getString("Lan_Id", ""), "pm")) + " " + Constants.showMessage(context, mPreferences.getString("Lan_Id", ""), "TO") + " " + _12HourSDF.format(_24HourDt1).replace("AM", Constants.showMessage(context, mPreferences.getString("Lan_Id", ""), "am")).replace("PM", Constants.showMessage(context, mPreferences.getString("Lan_Id", ""), "pm")));
        return spannableStringBuilder;


    }

    public static SpannableStringBuilder checkYesterDayTiming(Context context, HourDetails hourDetailsCurrentDay, HourDetails hourDetailsYesterday, SharedPreferences mPreferences) {
        SpannableStringBuilder spannableStringBuilder;


        try {

            _24HourTime = hourDetailsYesterday.getPOHStartTime();
            _24HourTime1 = hourDetailsYesterday.getPOHEndTime();
            try {
                _24HourDt = _24HourSDF.parse(_24HourTime);
                _24HourDt1 = _24HourSDF.parse(_24HourTime1);
                Calendar calendar = Calendar.getInstance();
                Date c = calendar.getTime();
                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                int minute = calendar.get(Calendar.MINUTE);
                android.text.format.DateFormat dateFormat = new android.text.format.DateFormat();
                boolean is24HourFormat = dateFormat.is24HourFormat(context);
                Date date;
                if (is24HourFormat) {
                    c = _24HourSDF.parse(hour + ":" + minute);
                } else {
                    String formattedDate = _24HourSDF.format(c.getTime());
                    c = _24HourSDF.parse(formattedDate);
                }
                long timeInMilliseconds = c.getTime();
                long timeInMilliseconds1 = _24HourDt.getTime();
                long timeInMilliseconds2 = _24HourDt1.getTime();


                if (timeInMilliseconds1 > timeInMilliseconds2) {
                    if (timeInMilliseconds2 >= timeInMilliseconds) {
                        spannableStringBuilder = Utils.getSpannableString(Constants.showMessage(context, mPreferences.getString("Lan_Id", ""), "Open Now") + ": ", Utils.getColor(context, R.color.mGreen), true, 0);
                        return spannableStringBuilder;
                    } else {

                    }
                }

            } catch (ParseException e) {
                e.printStackTrace();
            }


        } catch (Exception e) {
            Log.e(TAG, "checkYesterDayTiming Exception: " + e.getLocalizedMessage());
        }
        return null;
    }


}
