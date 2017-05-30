package com.ftl.tourisma;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.ShareCompat;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.ftl.tourisma.custom_views.NormalTextView;
import com.ftl.tourisma.utils.Constants;
import com.ftl.tourisma.utils.IsNetworkConnection;
import com.ftl.tourisma.utils.Utils;
import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.SnackbarManager;

import java.io.File;
import java.util.Arrays;

/**
 * Created by fipl11111 on 11-Mar-16.
 */
public class ShareFragmentActivity extends FragmentActivity implements View.OnClickListener {

    public static final String PLAY_STORE = "https://play.google.com/store/apps/details?id=";
    private static final String TAG = ShareFragmentActivity.class.getSimpleName();
    ImageView facebook_share, sms_share, google_share, twitter_share, iv_menu_close;
    String sharetext;
    Boolean searchflag = true;
    int check = 0;
    Activity context;
    SharedPreferences pref;
    LinearLayout ll_share;
    TextView tv_main_invite;
    Intent mIntent;
    String msg;
    private ShareDialog shareDialog;
    private boolean canPresentShareDialog;
    private CallbackManager callbackManager;
    private SharedPreferences mPreferences;
    private NormalTextView tv_sms, tv_google, tv_facebook, tv_twitter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        overridePendingTransition(R.anim.dialog_enter, R.anim.dialog_exit);
        super.onCreate(savedInstanceState);

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        setContentView(R.layout.share_popup);

        mPreferences = getSharedPreferences(Constants.mPref, 0);

        mIntent = getIntent();
        msg = mIntent.getStringExtra("myMsg");

        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        shareDialog = new ShareDialog(this);

        shareDialog.registerCallback(callbackManager, new FacebookCallback<Sharer.Result>() {
            @Override
            public void onSuccess(Sharer.Result result) {

            }

            @Override
            public void onCancel() {
            }

            @Override
            public void onError(FacebookException error) {
            }
        });
        ll_share = (LinearLayout) findViewById(R.id.ll_share);
        tv_main_invite = (NormalTextView) findViewById(R.id.tv_main_invite1);
        tv_main_invite.setText(Constants.showMessage(ShareFragmentActivity.this, mPreferences.getString("Lan_Id", ""), "sharetext"));
        iv_menu_close = (ImageView) findViewById(R.id.iv_menu_close);
        iv_menu_close.setOnClickListener(this);
        facebook_share = (ImageView) findViewById(R.id.facebook_share);
        facebook_share.setOnClickListener(this);
        twitter_share = (ImageView) findViewById(R.id.twitter_share);
        twitter_share.setOnClickListener(this);
        google_share = (ImageView) findViewById(R.id.google_share);
        google_share.setOnClickListener(this);
        sms_share = (ImageView) findViewById(R.id.sms_share);
        sms_share.setOnClickListener(this);
        tv_sms = (NormalTextView) findViewById(R.id.tv_sms);
        tv_sms.setText(Constants.showMessage(ShareFragmentActivity.this, mPreferences.getString("Lan_Id", ""), "smsbutton"));
        tv_google = (NormalTextView) findViewById(R.id.tv_google);
        tv_google.setText(Constants.showMessage(ShareFragmentActivity.this, mPreferences.getString("Lan_Id", ""), "googlebutton"));
        tv_facebook = (NormalTextView) findViewById(R.id.tv_facebook);
        tv_facebook.setText(Constants.showMessage(ShareFragmentActivity.this, mPreferences.getString("Lan_Id", ""), "facebookbutton"));
        tv_twitter = (NormalTextView) findViewById(R.id.tv_twitter);
        tv_twitter.setText(Constants.showMessage(ShareFragmentActivity.this, mPreferences.getString("Lan_Id", ""), "twitterbutton"));
    }

    public void shareEmail() {
        Log.e("System out", "ShareText " + sharetext);

        if (IsNetworkConnection.checkNetworkConnection(this)) {
            Intent emailIntent = new Intent(Intent.ACTION_SEND);
            emailIntent.setType("message/rfc822");
            emailIntent.putExtra(Intent.EXTRA_EMAIL, "");
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Tourisma Info");
            emailIntent.putExtra(Intent.EXTRA_TEXT, msg);

            try {
                this.startActivity(emailIntent);
            } catch (android.content.ActivityNotFoundException ex) {
                SnackbarManager.show(Snackbar.with(ShareFragmentActivity.this).color(Utils.getColor(this, R.color.mBlue)).text("There is no email client installed."));
            }
        }
    }

    public void sendSMS() {
        Log.e("System out", "ShareText " + sharetext);
        Intent i = new Intent(android.content.Intent.ACTION_SEND);
        i.putExtra(Intent.EXTRA_TEXT, msg);
        i.setType("text/plain");
        this.startActivity(i);
    }

    public void shareOnFB() {
        if (IsNetworkConnection.checkNetworkConnection(this)) {
            LoginManager.getInstance().logInWithPublishPermissions(ShareFragmentActivity.this, Arrays.asList("publish_actions"));
            if (ShareDialog.canShow(ShareLinkContent.class)) {
                ShareLinkContent linkContent = new ShareLinkContent.Builder()
                        .setContentTitle("MyTourisma")
                        .setContentDescription(msg)
                        .setContentUrl(Uri.parse("www.mytourisma.com"))
                        .build();
                shareDialog.show(linkContent);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onClick(View v) {
        if (v == facebook_share) {
            shareOnFB();
            finish();
        } else if (v == google_share) {
            try {
                if (isGooglePlusInstalled()) {
                    File media = new File("");
                    Uri uri = Uri.fromFile(media);
                    ContentResolver cr = this.getContentResolver();
                    String mime = cr.getType(uri);
                    Intent shareIntent = ShareCompat.IntentBuilder
                            .from(this)
                            .setText((Html.fromHtml(msg)))
                            .setType("image/jpeg").setStream(uri).getIntent()
                            .setPackage("com.google.android.apps.plus");
                    startActivityForResult(shareIntent, 0);
                    finish();
                } else {
                    final Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(PLAY_STORE + "com.google.android.apps.plus"));
                    startActivity(intent);
                }
            } catch (Exception e) {
                // Tracking exception
                MyTorismaApplication.getInstance().trackException(e);
                Utils.Log(TAG, "google_share Exception: " + e.getLocalizedMessage());
            }
        } else if (v == sms_share) {
            sendSMS();
            finish();
        } else if (v == twitter_share) {
            Intent intent = new Intent(ShareFragmentActivity.this, TwitterDialog.class);
            intent.putExtra("msg", msg);
            startActivity(intent);
            finish();
        } else if (v == iv_menu_close) {
            finish();
        }
    }

    public boolean isGooglePlusInstalled() {
        try {
            getPackageManager().getApplicationInfo("com.google.android.apps.plus", 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }
}
