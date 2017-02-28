package com.ftl.tourisma;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
//import android.util.Log;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

import com.ftl.tourisma.activity.IntroScreens;
import com.ftl.tourisma.activity.NoInternet;
import com.ftl.tourisma.custom_views.NormalTextView;
import com.ftl.tourisma.database.DBAdapter;
import com.ftl.tourisma.database.Language;
import com.ftl.tourisma.postsync.PostSync;
import com.ftl.tourisma.postsync.post_sync;
import com.ftl.tourisma.utils.CommonClass;
import com.ftl.tourisma.utils.Constants;
import com.ftl.tourisma.utils.Utilities;
import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.SnackbarManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by fipl11111 on 22-Feb-16.
 */
public class LanguageFragmentActivity extends FragmentActivity implements View.OnClickListener, post_sync.ResponseHandler {

    private static final String TAG = LanguageFragmentActivity.class.getSimpleName();
    private ImageView iv_arrow_language;
    private NormalTextView button_bt_english, button_bt_russian, button_bt_arabic;
    private SharedPreferences mPreferences;
    private SharedPreferences.Editor mEditor;
    private DBAdapter dbAdapter;
    private Language language;
    private NormalTextView txtChooseLanguage;
    Animation animFadeIn, animFadeOut;

    Handler handler = new Handler();
    Runnable runnableIn = new Runnable() {
        @Override
        public void run() {
            txtChooseLanguage.startAnimation(animFadeIn);
            handler.removeCallbacks(runnableIn);
            handler.postDelayed(runnableOut, 3000);
        }
    };
    Runnable runnableOut = new Runnable() {
        @Override
        public void run() {
            txtChooseLanguage.startAnimation(animFadeOut);
            handler.removeCallbacks(runnableOut);
            handler.postDelayed(runnableIn, 200);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_language);

        dbAdapter = new DBAdapter(this);

        mPreferences = getSharedPreferences(Constants.mPref, 0);
        mEditor = mPreferences.edit();
        mEditor.putString("language", "6").commit();
        mEditor.putString("Lan_Id", "6").commit();

        initialisation();

        iv_arrow_language.setOnClickListener(this);
        button_bt_english.setOnClickListener(this);
        button_bt_arabic.setOnClickListener(this);
        button_bt_russian.setOnClickListener(this);

//        txtChooseLanguage.setInAnimation(this, R.anim.fade_in);
//        txtChooseLanguage.setOutAnimation(this, R.anim.fade_out);
// load animations
        animFadeIn = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.fade_in);
        animFadeOut = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.fade_out);
// set animation listeners
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
//                txtChooseLanguage.startAnimation(animFadeIn);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        txtChooseLanguage.setTag("EN");
        handler.postDelayed(runnableIn, 3000);
    }

    private void initialisation() {
        txtChooseLanguage = (NormalTextView) findViewById(R.id.txtChooseLanguage);
        iv_arrow_language = (ImageView) findViewById(R.id.iv_arrow_language);
        button_bt_english = (NormalTextView) findViewById(R.id.button_bt_english);
        button_bt_russian = (NormalTextView) findViewById(R.id.button_bt_russian);
        button_bt_arabic = (NormalTextView) findViewById(R.id.button_bt_arabic);

//        Log.i("System out", "Constants.mLanguage " + Constants.mLanguage);

        if (Constants.mLanguage == 7) {
            mEditor.putString("language", "7").commit();
            mEditor.putString("Lan_Id", "7").commit();

            button_bt_english.setBackground(null);
            button_bt_russian.setBackgroundResource(R.drawable.selector_button);
            button_bt_russian.setPadding((int) getResources().getDimension(R.dimen.m20dp), (int) getResources().getDimension(R.dimen.m5dp), (int) getResources().getDimension(R.dimen.m20dp), (int) getResources().getDimension(R.dimen.m5dp));
            button_bt_arabic.setBackground(null);

            button_bt_english.setTextColor(Utilities.getColor(this, R.color.selector_black_white));
            button_bt_russian.setTextColor(Utilities.getColor(this, R.color.selector_white_blue));
            button_bt_arabic.setTextColor(Utilities.getColor(this, R.color.selector_black_white));
        } else if (Constants.mLanguage == 8) {
            mEditor.putString("language", "8").commit();
            mEditor.putString("Lan_Id", "8").commit();

            button_bt_english.setBackground(null);
            button_bt_russian.setBackground(null);
            button_bt_arabic.setBackgroundResource(R.drawable.selector_button);
            button_bt_arabic.setPadding((int) getResources().getDimension(R.dimen.m20dp), (int) getResources().getDimension(R.dimen.m5dp), (int) getResources().getDimension(R.dimen.m20dp), (int) getResources().getDimension(R.dimen.m5dp));

            button_bt_english.setTextColor(Utilities.getColor(this, R.color.selector_black_white));
            button_bt_russian.setTextColor(Utilities.getColor(this, R.color.selector_black_white));
            button_bt_arabic.setTextColor(Utilities.getColor(this, R.color.selector_white_blue));
        } else if (Constants.mLanguage == 6) {
            mEditor.putString("language", "6").commit();
            mEditor.putString("Lan_Id", "6").commit();

            button_bt_english.setBackgroundResource(R.drawable.selector_button);
            button_bt_english.setPadding((int) getResources().getDimension(R.dimen.m20dp), (int) getResources().getDimension(R.dimen.m5dp), (int) getResources().getDimension(R.dimen.m20dp), (int) getResources().getDimension(R.dimen.m5dp));
            button_bt_russian.setBackground(null);
            button_bt_arabic.setBackground(null);

            button_bt_english.setTextColor(Utilities.getColor(this, R.color.selector_white_blue));
            button_bt_russian.setTextColor(Utilities.getColor(this, R.color.selector_black_white));
            button_bt_arabic.setTextColor(Utilities.getColor(this, R.color.selector_black_white));
        }
    }

    @Override
    public void onClick(View v) {
        if (v == iv_arrow_language) {

            languageCall();
            /*if (mPreferences.getString("language", "").equalsIgnoreCase("6")) {
            } else {
                SnackbarManager.show(Snackbar.with(LanguageFragmentActivity.this).color(Utilities.getColor(this,R.color.mBlue)).text("Please select english for now"));
            }*/
        } else if (v == button_bt_arabic) {
            mEditor.putString("language", "8").commit();
            mEditor.putString("Lan_Id", "8").commit();
            Constants.mLanguage = 8;
            button_bt_english.setBackground(null);
            button_bt_russian.setBackground(null);
            button_bt_arabic.setBackgroundResource(R.drawable.selector_button);
            button_bt_arabic.setPadding((int) getResources().getDimension(R.dimen.m20dp), (int) getResources().getDimension(R.dimen.m5dp), (int) getResources().getDimension(R.dimen.m20dp), (int) getResources().getDimension(R.dimen.m5dp));
            button_bt_english.setTextColor(Utilities.getColor(this, R.color.selector_black_white));
            button_bt_russian.setTextColor(Utilities.getColor(this, R.color.selector_black_white));
            button_bt_arabic.setTextColor(Utilities.getColor(this, R.color.selector_white_blue));

        } else if (v == button_bt_russian) {
            mEditor.putString("language", "7").commit();
            mEditor.putString("Lan_Id", "7").commit();
            Constants.mLanguage = 7;
            button_bt_english.setBackground(null);
            button_bt_russian.setBackgroundResource(R.drawable.selector_button);
            button_bt_russian.setPadding((int) getResources().getDimension(R.dimen.m20dp), (int) getResources().getDimension(R.dimen.m5dp), (int) getResources().getDimension(R.dimen.m20dp), (int) getResources().getDimension(R.dimen.m5dp));
            button_bt_arabic.setBackground(null);

            button_bt_english.setTextColor(Utilities.getColor(this, R.color.selector_black_white));
            button_bt_russian.setTextColor(Utilities.getColor(this, R.color.selector_white_blue));
            button_bt_arabic.setTextColor(Utilities.getColor(this, R.color.selector_black_white));
        } else if (v == button_bt_english) {
            mEditor.putString("language", "6").commit();
            mEditor.putString("Lan_Id", "6").commit();
            Constants.mLanguage = 6;

            button_bt_english.setBackgroundResource(R.drawable.selector_button);
            button_bt_english.setPadding((int) getResources().getDimension(R.dimen.m20dp), (int) getResources().getDimension(R.dimen.m5dp), (int) getResources().getDimension(R.dimen.m20dp), (int) getResources().getDimension(R.dimen.m5dp));
            button_bt_russian.setBackground(null);
            button_bt_arabic.setBackground(null);

            button_bt_english.setTextColor(Utilities.getColor(this, R.color.selector_white_blue));
            button_bt_russian.setTextColor(Utilities.getColor(this, R.color.selector_black_white));
            button_bt_arabic.setTextColor(Utilities.getColor(this, R.color.selector_black_white));
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

//            Log.d("System out", "GetLanguages " + json);
            new PostSync(LanguageFragmentActivity.this, "GetLanguages",LanguageFragmentActivity.this).execute(url, json);
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

                    Intent mIntent = new Intent(LanguageFragmentActivity.this, IntroScreens.class);
                    startActivity(mIntent);
                    finish();

                } catch (JSONException e) {
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
            Log.e(TAG, "onResponse Exception " + e.getLocalizedMessage());
        }
    }
}
