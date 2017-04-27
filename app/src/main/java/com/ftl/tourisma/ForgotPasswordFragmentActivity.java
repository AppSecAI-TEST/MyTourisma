package com.ftl.tourisma;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

import com.ftl.tourisma.activity.NoInternet;
import com.ftl.tourisma.custom_views.NormalEditText;
import com.ftl.tourisma.custom_views.NormalTextView;
import com.ftl.tourisma.postsync.PostSync;
import com.ftl.tourisma.postsync.post_sync;
import com.ftl.tourisma.utils.CommonClass;
import com.ftl.tourisma.utils.Constants;
import com.ftl.tourisma.utils.Utilities;
import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.SnackbarManager;
import com.wrapp.floatlabelededittext.FloatLabeledEditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static com.ftl.tourisma.constants.WSConstants.STATUS;
import static com.ftl.tourisma.constants.WSConstants.TRUE;

//import android.util.Log;

/**
 * Created by fipl11111 on 29-Feb-16.
 * updated by arp as on 13 October 2016
 */
public class ForgotPasswordFragmentActivity extends FragmentActivity implements OnClickListener, post_sync.ResponseHandler {

    private static final String TAG = ForgotPasswordFragmentActivity.class.getSimpleName();
    private ImageView iv_close_header1;
    private NormalTextView tv_sign_up, txtForgotMessage, tv_submit;
    private NormalEditText et_email_forgot_password;
    private FloatLabeledEditText flet_email_forgot_password;
    private SharedPreferences mPreferences;
    private Handler mHandler;
    private Runnable mRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forget_password_new);

        mPreferences = getSharedPreferences(Constants.mPref, 0);

        initialization();

        iv_close_header1.setOnClickListener(this);
        tv_submit.setOnClickListener(this);

    }

    private void initialization() {

        txtForgotMessage = (NormalTextView) findViewById(R.id.txtForgotMessage);
        txtForgotMessage.setText(Constants.showMessage(ForgotPasswordFragmentActivity.this, mPreferences.getString("Lan_Id", ""), "forgotdetailtitle"));

        iv_close_header1 = (ImageView) findViewById(R.id.img_close);

        tv_sign_up = (NormalTextView) findViewById(R.id.txtTitle);
        tv_sign_up.setText(Constants.showMessage(ForgotPasswordFragmentActivity.this, mPreferences.getString("Lan_Id", ""), "forgottitle"));

        et_email_forgot_password = (NormalEditText) findViewById(R.id.et_email_forgot_password);
        et_email_forgot_password.setHint(Constants.showMessage(ForgotPasswordFragmentActivity.this, mPreferences.getString("Lan_Id", ""), "useridtost"));


        flet_email_forgot_password = (FloatLabeledEditText) findViewById(R.id.flet_email_forgot_password);
        flet_email_forgot_password.setHint(Constants.showMessage(ForgotPasswordFragmentActivity.this, mPreferences.getString("Lan_Id", ""), "useridtost"));

        tv_submit = (NormalTextView) findViewById(R.id.tv_submit);
        tv_submit.setText(Constants.showMessage(ForgotPasswordFragmentActivity.this, mPreferences.getString("Lan_Id", ""), "submittitle"));
    }

    @Override
    public void onClick(View v) {
        if (v == iv_close_header1) {
            finish();
        } else if (v == tv_submit) {
            if (et_email_forgot_password.getText().length() == 0) {
                SnackbarManager.show(Snackbar.with(ForgotPasswordFragmentActivity.this).color(Utilities.getColor(this, R.color.mBlue)).text(Constants.showMessage(ForgotPasswordFragmentActivity.this, mPreferences.getString("Lan_Id", ""), "EMAIL")));
            } else if (!Constants.isValidEmail(et_email_forgot_password.getText().toString())) {
                SnackbarManager.show(Snackbar.with(ForgotPasswordFragmentActivity.this).color(Utilities.getColor(this, R.color.mBlue)).text(Constants.showMessage(ForgotPasswordFragmentActivity.this, mPreferences.getString("Lan_Id", ""), "VALIDEMAIL")));
            } else {
                forgotPasswordCall();
            }
        }
    }

    private void forgotPasswordCall() {
        if (CommonClass.hasInternetConnection(ForgotPasswordFragmentActivity.this)) {
            String url = Constants.SERVER_URL + "json.php?action=ForgotPassword";
            String json = "[" +
                    "{" +
                    "\"User_Email\":\"" + et_email_forgot_password.getText().toString() + "\"," +
                    "\"Lan_Id\":\"" + mPreferences.getString("Lan_Id", "") + "\"" +
                    "}" +
                    "]";
//            RequestData requestData=new RequestData();
//            requestData.setLan_Id(mPreferences.getString("Lan_Id", ""));
//            requestData.setUser_Email(et_email_forgot_password.getText().toString());
//            Log.d("System out", "ForgotPassword " + json);
            new PostSync(ForgotPasswordFragmentActivity.this, "ForgotPassword", ForgotPasswordFragmentActivity.this).execute(url, json);
        } else {
            Intent intent = new Intent(getApplicationContext(), NoInternet.class);
            startActivity(intent);
            //SnackbarManager.show(Snackbar.with(ForgotPasswordFragmentActivity.this).color(Utilities.getColor(this, R.color.mBlue)).text(Constants.showMessage(ForgotPasswordFragmentActivity.this, mPreferences.getString("Lan_Id", ""), "NOINTERNET")));
        }
    }

    public void forgotPasswordResponse(String resultString) {
//        Log.i("System out", resultString);
        try {
            JSONArray jsonArray = new JSONArray(resultString);
            JSONObject jsonObject = jsonArray.optJSONObject(0);
            if (jsonObject.has(STATUS)) {
                if (jsonObject.optString(STATUS).equalsIgnoreCase(TRUE)) {
                    SnackbarManager.show(Snackbar.with(ForgotPasswordFragmentActivity.this).color(Utilities.getColor(this, R.color.mBlue)).text(Constants.showMessage(ForgotPasswordFragmentActivity.this, mPreferences.getString("Lan_Id", ""), "Passwordsent")));
                    mHandler = new Handler();
                    mRunnable = new Runnable() {
                        @Override
                        public void run() {
                            finish();
                        }
                    };
                    mHandler.postDelayed(mRunnable, 2000);
                } else {
                    SnackbarManager.show(Snackbar.with(ForgotPasswordFragmentActivity.this).color(Utilities.getColor(this, R.color.mBlue)).text(jsonObject.optString("status")));
                }
            } else {
                SnackbarManager.show(Snackbar.with(ForgotPasswordFragmentActivity.this).color(Utilities.getColor(this, R.color.mBlue)).text(jsonObject.optString("status")));
            }
        } catch (JSONException e) {
            // Tracking exception
            MyTorismaApplication.getInstance().trackException(e);
            // TODO Auto-generated catch block
//            Log.e("System out", e.getMessage());
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mHandler != null) {
            mHandler.removeCallbacks(mRunnable);
        }
    }

    @Override
    public void onResponse(String response, String action) {

        try {
            if (action.equalsIgnoreCase("ForgotPassword")) {
                forgotPasswordResponse(response);
            }
        } catch (Exception e) {
            // Tracking exception
            MyTorismaApplication.getInstance().trackException(e);
            Log.e(TAG, "onResponse Exception " + e.getLocalizedMessage());
        }
    }
}
