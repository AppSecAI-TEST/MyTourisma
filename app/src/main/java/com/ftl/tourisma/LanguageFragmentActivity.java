package com.ftl.tourisma;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;

import com.ftl.tourisma.activity.IntroScreens;
import com.ftl.tourisma.activity.NoInternet;
import com.ftl.tourisma.app.Config;
import com.ftl.tourisma.custom_views.NormalTextView;
import com.ftl.tourisma.database.DBAdapter;
import com.ftl.tourisma.database.Language;
import com.ftl.tourisma.postsync.PostSync;
import com.ftl.tourisma.postsync.post_sync;
import com.ftl.tourisma.utils.CommonClass;
import com.ftl.tourisma.utils.Constants;
import com.github.paolorotolo.appintro.util.AppConstants;
import com.google.firebase.messaging.FirebaseMessaging;
import com.pixplicity.easyprefs.library.Prefs;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by fipl11111 on 22-Feb-16.
 */
public class LanguageFragmentActivity extends FragmentActivity implements View.OnClickListener, post_sync.ResponseHandler {

    private static final String TAG = LanguageFragmentActivity.class.getSimpleName();
    Animation animFadeIn, animFadeOut;
    Handler handler = new Handler();
    // for fcm
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private ImageView iv_arrow_language;
    private NormalTextView button_bt_english, button_bt_russian, button_bt_arabic;
    private SharedPreferences mPreferences;
    private SharedPreferences.Editor mEditor;
    private DBAdapter dbAdapter;
    private Language language;
    private NormalTextView txtChooseLanguage;
    Runnable runnableOut = new Runnable() {
        @Override
        public void run() {
            txtChooseLanguage.startAnimation(animFadeOut);
            handler.removeCallbacks(runnableOut);
            handler.postDelayed(runnableIn, 200);
        }
    };
    Runnable runnableIn = new Runnable() {
        @Override
        public void run() {
            txtChooseLanguage.startAnimation(animFadeIn);
            handler.removeCallbacks(runnableIn);
            handler.postDelayed(runnableOut, 3000);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_language);
        Constants.homepage = "homepage";

        //Initializing database
        dbAdapter = new DBAdapter(this);

        //Initializing SharedPreferences
        mPreferences = getSharedPreferences(Constants.mPref, 0);
        mEditor = mPreferences.edit();

        //Default english language is setted
        mEditor.putString("language", "6").commit();
        mEditor.putString("Lan_Id", "6").commit();

        //Calling multi language api
        languageCall();

        //Intializing the layout views
        initialisation();

        //Setting click listners
        iv_arrow_language.setOnClickListener(this);
        button_bt_english.setOnClickListener(this);
        button_bt_arabic.setOnClickListener(this);
        button_bt_russian.setOnClickListener(this);

        //Preparing animation
        animFadeIn = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.fade_in);
        animFadeOut = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.fade_out);
        animFadeIn.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                if (txtChooseLanguage.getTag().equals("EN")) {
                    txtChooseLanguage.setTag("RU");
                    txtChooseLanguage.setText(R.string.choose_language_string_ru);
                } else if (txtChooseLanguage.getTag().equals("AR")) {
                    txtChooseLanguage.setTag("EN");
                    txtChooseLanguage.setText(R.string.choose_language_string_en);
                } else if (txtChooseLanguage.getTag().equals("RU")) {
                    txtChooseLanguage.setTag("AR");
                    txtChooseLanguage.setText(R.string.choose_language_string_ar);
                }
            }

            @Override
            public void onAnimationEnd(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        animFadeOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                txtChooseLanguage.setText("");

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        txtChooseLanguage.setTag("EN");
        handler.postDelayed(runnableIn, 3000);


        //FCM
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                // checking for type intent filter
                if (intent.getAction().equals(Config.REGISTRATION_COMPLETE)) {
                    // gcm successfully registered
                    // now subscribe to `global` topic to receive app wide notifications
                    FirebaseMessaging.getInstance().subscribeToTopic(Config.TOPIC_GLOBAL);
//                    displayFirebaseRegId();
                } else if (intent.getAction().equals(Config.PUSH_NOTIFICATION)) {
                    // new push notification is received
                    String message = intent.getStringExtra("message");
//                    Toast.makeText(getApplicationContext(), "Push notification: " + message, Toast.LENGTH_LONG).show();
                }
            }
        };

    }

    @Override
    protected void onResume() {
        super.onResume();
        // register GCM registration complete receiver
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.REGISTRATION_COMPLETE));

        // register new push message receiver
        // by doing this, the activity will be notified each time a new message arrives
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.PUSH_NOTIFICATION));

        // clear the notification area when the app is opened
//        NotificationUtils.clearNotifications(getApplicationContext());
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
    }

    //FCM
    private void displayFirebaseRegId() {
        SharedPreferences pref = getApplicationContext().getSharedPreferences(Config.SHARED_PREF, 0);
        String regId = pref.getString("regId", null);

        Log.e(TAG, "Firebase reg id: " + regId);

        if (!TextUtils.isEmpty(regId))
            Toast.makeText(getApplicationContext(), "Firebase Reg Id: " + regId, Toast.LENGTH_LONG).show();
        else
            Toast.makeText(getApplicationContext(), "Firebase Reg Id is not received yet!", Toast.LENGTH_LONG).show();
    }

    private void initialisation() {
        txtChooseLanguage = (NormalTextView) findViewById(R.id.txtChooseLanguage);
        iv_arrow_language = (ImageView) findViewById(R.id.iv_arrow_language);
        button_bt_english = (NormalTextView) findViewById(R.id.button_bt_english);
        button_bt_russian = (NormalTextView) findViewById(R.id.button_bt_russian);
        button_bt_arabic = (NormalTextView) findViewById(R.id.button_bt_arabic);

        if (Constants.mLanguage == 7) {
            mEditor.putString("language", "7").commit();
            mEditor.putString("Lan_Id", "7").commit();

        } else if (Constants.mLanguage == 8) {
            mEditor.putString("language", "8").commit();
            mEditor.putString("Lan_Id", "8").commit();

        } else if (Constants.mLanguage == 6) {
            mEditor.putString("language", "6").commit();
            mEditor.putString("Lan_Id", "6").commit();

        }
    }

    @Override
    public void onClick(View v) {
        if (v == iv_arrow_language) {
            if (Prefs.getString(Constants.tutorial, "").equals("second")) {
                Intent mIntent = new Intent(LanguageFragmentActivity.this, SignUpLoginFragmentActivity.class);
                startActivity(mIntent);
                finish();
            } else {
                Intent mIntent = new Intent(LanguageFragmentActivity.this, IntroScreens.class);
                Prefs.putString(Constants.tutorial, "second");
                startActivity(mIntent);
                finish();
            }
        } else if (v == button_bt_arabic) {
            AppConstants.language = "arabic";
            Constants.language = "arabic";
            mEditor.putString("language", "8").commit();
            mEditor.putString("Lan_Id", "8").commit();
            Constants.mLanguage = 8;

            Intent mIntent = new Intent(LanguageFragmentActivity.this, IntroScreens.class);
            Prefs.putString(Constants.tutorial, "second");
            startActivity(mIntent);
            finish();

        } else if (v == button_bt_russian) {
            AppConstants.language = "russian";
            Constants.language = "russian";
            mEditor.putString("language", "7").commit();
            mEditor.putString("Lan_Id", "7").commit();
            Constants.mLanguage = 7;

            Intent mIntent = new Intent(LanguageFragmentActivity.this, IntroScreens.class);
            Prefs.putString(Constants.tutorial, "second");
            startActivity(mIntent);
            finish();

        } else if (v == button_bt_english) {
            AppConstants.language = "english";
            Constants.language = "english";
            mEditor.putString("language", "6").commit();
            mEditor.putString("Lan_Id", "6").commit();
            Constants.mLanguage = 6;

            Intent mIntent = new Intent(LanguageFragmentActivity.this, IntroScreens.class);
            Prefs.putString(Constants.tutorial, "second");
            startActivity(mIntent);
            finish();

        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void languageCall() {
        if (CommonClass.hasInternetConnection(this)) {
            String url = Constants.SERVER_URL + "json.php?action=GetLanguages";
            String json = "";
            new PostSync(LanguageFragmentActivity.this, "GetLanguages", LanguageFragmentActivity.this).execute(url, json);
        } else {
            Intent intent = new Intent(getApplicationContext(), NoInternet.class);
            startActivity(intent);
            //SnackbarManager.show(Snackbar.with(this).color(Utilities.getColor(this, R.color.mBlue)).text("No Internet connection!"));
        }
    }

    public void languageResponse(String resultString) {
        if (resultString.length() > 2) {
            if (resultString.length() > 2) {
                try {
                    dbAdapter.open();
                    JSONArray jsonArray = new JSONArray(resultString);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.optJSONObject(i);
                        language = new Language();
                        language.setLan_ID(jsonObject.optString("Lan_ID"));
                        language.setLan_name(jsonObject.optString("Lan_name"));
                        language.setLan_Contents(jsonObject.optString("Lan_Contents"));
                        language.setLan_Status(jsonObject.optString("Lan_Status"));

                        JSONArray operation = jsonObject.getJSONArray("messages");
                        for (int j = 0; j < operation.length(); j++) {
                            JSONObject jsonObject2 = operation.getJSONObject(j);
                            language.setMsg_ID(jsonObject2.optString("Msg_ID"));
                            language.setMsg_Constant(jsonObject2.optString("Msg_Constant"));
                            language.setMsg_Statement(jsonObject2.optString("Msg_Statement"));
                            language.setMsg_Status(jsonObject2.optString("Msg_Status"));

                            dbAdapter.insertLanguage(language.getLan_ID(), language.getLan_name(), language.getLan_Contents(), language.getLan_Status(), language.getMsg_ID(), language.getMsg_Constant(), language.getMsg_Statement(), language.getMsg_Status());
                        }
                    }
                    dbAdapter.close();
                } catch (JSONException e) {
                    // Tracking exception
                    MyTorismaApplication.getInstance().trackException(e);
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(runnableIn);
        handler.removeCallbacks(runnableOut);
        if (Constants.dialog != null) {
            Constants.dialog.dismiss();
        }
    }

    @Override
    public void onResponse(String response, String action) {
        try {
            if (action.equalsIgnoreCase("GetLanguages")) {
                languageResponse(response);
            }
        } catch (Exception e) {
            // Tracking exception
            MyTorismaApplication.getInstance().trackException(e);
            Log.e(TAG, "onResponse Exception " + e.getLocalizedMessage());
        }
    }
}
