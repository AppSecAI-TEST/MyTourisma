package com.ftl.tourisma.utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.support.v4.app.Fragment;

import com.ftl.tourisma.database.DBAdapter;

import java.util.List;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by harpalsinh on 26-Feb-2016.
 */
public class Constants {

    //    Custom progress dialog
    public static final int LOADER_HEIGHT = 60;
    public static final int LOADER_WIDTH = 60;
    public static String mPref = "My_Pref";
    //        public static String SERVER_URL = "http://35.154.205.155/mytourisma/";
    //    public static String SERVER_URL = "http://ec2-54-93-117-123.eu-central-1.compute.amazonaws.com/";
//    public static String IMG_URL = "http://ec2-54-93-117-123.eu-central-1.compute.amazonaws.com/json.php";
//    public static String BEACON_IMAGE_URL = "http://ec2-54-93-117-123.eu-central-1.compute.amazonaws.com/timthumb.php?src=/uploads/beacons/";
//    public static String IMAGE_URL = "http://ec2-54-93-117-123.eu-central-1.compute.amazonaws.com/timthumb.php?src=/uploads/place/";
//    public static String IMAGE_URL1 = "http://ec2-54-93-117-123.eu-central-1.compute.amazonaws.com/uploads/user/";
//    public static String IMAGE_URL2 = "http://ec2-54-93-117-123.eu-central-1.compute.amazonaws.com/timthumb.php?src=/uploads/category/";
    //    public static String VR_IMAGE = "http://ec2-54-93-117-123.eu-central-1.compute.amazonaws.com/uploads/vrplace/";
    public static String SERVER_URL = "http://13.126.151.196/";
    public static String IMG_URL = "http://13.126.151.196/json.php";
    public static String BEACON_IMAGE_URL = "http://13.126.151.196/timthumb.php?src=/uploads/beacons/";
    public static String IMAGE_URL = "http://13.126.151.196/timthumb.php?src=/uploads/place/";
    public static String IMAGE_URL1 = "http://13.126.151.196/uploads/user/";
    public static String IMAGE_URL2 = "http://13.126.151.196/timthumb.php?src=/uploads/category/";
    public static String VR_IMAGE = "http://13.126.151.196/uploads/vrplace/";
    public static Dialog dialog;
    public static List<Fragment> fragments = new Vector<Fragment>();
    public static int mStatic = 0;
    public static String placeId;
    public static int mFromSelectLocation = 0;
    public static int mStaticFavCall = 0;
    public static int mStaticNearCall = 0;
    public static int mLanguage = 6;
    public static String user_id = "user_id";
    public static String language = "language";
    public static String tutorial = "tutorial";
    public static boolean Dev_mode = true;
    public static String PlaceOpenFor24Hours = "1";
    public static String PlaceClosed = "0";
    public static String PlaceOpenWithAnyTime = "2";
    public static String homepage = "homepage";
    public static String beacons_guestuser_session = "beacons_guestuser_session";
    public static String from_login = "from_login";
    public static String first_time = "first_time";
    public static String searchFragment = "searchFragment";
    public static String fcm_regid = "fcm_regid";

    public static boolean isValidEmail(String email) {
        Pattern pattern;
        Matcher matcher;
        String EMAIL_PATTERN = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        pattern = Pattern.compile(EMAIL_PATTERN);
        matcher = pattern.matcher(email);
        return matcher.matches();
    }

    public static String showMessage(Activity activity, String Lan_ID, String Msg_Constant) {
        String msg = "";
        DBAdapter dbAdapter = new DBAdapter(activity);
        dbAdapter.open();
        msg = dbAdapter.getLanguageMsg(Lan_ID, Msg_Constant);
        dbAdapter.close();
        return msg;
    }

    public static String showMessage(Context activity, String Lan_ID, String Msg_Constant) {
        String msg = "";
        DBAdapter dbAdapter = new DBAdapter(activity);
        dbAdapter.open();
        msg = dbAdapter.getLanguageMsg(Lan_ID, Msg_Constant);
        dbAdapter.close();
        return msg;
    }
}
