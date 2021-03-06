package com.ftl.tourisma;

import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.ftl.tourisma.activity.MainActivity;
import com.ftl.tourisma.activity.NoInternet;
import com.ftl.tourisma.custom_views.NormalEditText;
import com.ftl.tourisma.custom_views.NormalTextView;
import com.ftl.tourisma.postsync.PostSync;
import com.ftl.tourisma.postsync.post_sync;
import com.ftl.tourisma.utils.CommonClass;
import com.ftl.tourisma.utils.Constants;
import com.ftl.tourisma.utils.GPSTracker;
import com.ftl.tourisma.utils.Utilities;
import com.ftl.tourisma.utils.Utils;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;
import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.SnackbarManager;
import com.pixplicity.easyprefs.library.Prefs;
import com.wrapp.floatlabelededittext.FloatLabeledEditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by fipl11111 on 25-Feb-16.
 */
public class SignupFragmentActivity extends FragmentActivity implements View.OnClickListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, post_sync.ResponseHandler {

    protected static final int RC_SIGN_IN = 0;
    private static final String TAG = "SignupFragmentActivity";
    protected static GoogleApiClient mGoogleApiClient;
    final private int REQUEST_CODE_ASK_PERMISSIONS = 123;
    protected boolean mIntentInProgress;
    protected boolean mSignInClicked;
    PopupWindow popupWindow;
    private ImageView iv_close_header1, privacy_policy_img;
    private NormalEditText et_username_sign_up, et_password_sign_up, et_email_sign_up;
    private FloatLabeledEditText flet_username_sign_up, flet_password_sign_up, flet_email_sign_up;
    private SharedPreferences mPreferences;
    private SharedPreferences.Editor mEditor;
    private GPSTracker gpsTracker;
    private String lat, lon;
    private CallbackManager callbackManager;
    private String accessTokenNew;
    private ConnectionResult mConnectionResult;
    private NormalTextView sign_up_using_mail, tv_sign_up, tv_forgot_sign_up, tv_signUp, tv_google_plus, fb_login_button_, privacy_policy_txt;

    public static void signOutFromGplus() {
        if (mGoogleApiClient.isConnected()) {
            Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
            mGoogleApiClient.disconnect();
            mGoogleApiClient.connect();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        mPreferences = getSharedPreferences(Constants.mPref, 0);
        mEditor = mPreferences.edit();
        initialisation();
        iv_close_header1.setOnClickListener(this);
        tv_forgot_sign_up.setOnClickListener(this);
        tv_signUp.setOnClickListener(this);
        tv_google_plus.setOnClickListener(this);
        fb_login_button_.setOnClickListener(this);

        privacy_policy_txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPrivacyPolicyPopupDialog();
            }
        });

    }

    private void initialisation() {
        List<String> permissions = new ArrayList<String>();
        permissions.add("public_profile");
        permissions.add("email");
        permissions.add("user_birthday");
        FacebookSdk.sdkInitialize(this);
        FacebookSdk.setApplicationId(getResources().getString(R.string.facebook_app_id));
        callbackManager = CallbackManager.Factory.create();
        FacebookLogin();
        privacy_policy_txt = (NormalTextView) findViewById(R.id.privacy_policy_txt);
        privacy_policy_txt.setText(Constants.showMessage(this, mPreferences.getString("Lan_Id", ""), "PrivacyPolicyTitle"));
        iv_close_header1 = (ImageView) findViewById(R.id.img_close);
        tv_forgot_sign_up = (NormalTextView) findViewById(R.id.tv_forgot_sign_up);
        tv_forgot_sign_up.setText(Constants.showMessage(SignupFragmentActivity.this, mPreferences.getString("Lan_Id", ""), "forgotpassword"));

        tv_sign_up = (NormalTextView) findViewById(R.id.txtTitle);
        tv_sign_up.setText(Constants.showMessage(SignupFragmentActivity.this, mPreferences.getString("Lan_Id", ""), "signupheader"));

        sign_up_using_mail = (NormalTextView) findViewById(R.id.sign_up_using_mail);
        sign_up_using_mail.setText(Constants.showMessage(SignupFragmentActivity.this, mPreferences.getString("Lan_Id", ""), "signupscreentitle"));
        et_username_sign_up = (NormalEditText) findViewById(R.id.et_username_sign_up);
        et_username_sign_up.setHint(Constants.showMessage(SignupFragmentActivity.this, mPreferences.getString("Lan_Id", ""), "nametost"));
        et_password_sign_up = (NormalEditText) findViewById(R.id.et_password_sign_up);
        et_password_sign_up.setHint(Constants.showMessage(SignupFragmentActivity.this, mPreferences.getString("Lan_Id", ""), "passwordtost"));
        et_email_sign_up = (NormalEditText) findViewById(R.id.et_email_sign_up);
        et_email_sign_up.setHint(Constants.showMessage(SignupFragmentActivity.this, mPreferences.getString("Lan_Id", ""), "emailtost"));
        flet_username_sign_up = (FloatLabeledEditText) findViewById(R.id.flet_username_sign_up);
        flet_username_sign_up.setHint(Constants.showMessage(SignupFragmentActivity.this, mPreferences.getString("Lan_Id", ""), "nametost"));
        flet_password_sign_up = (FloatLabeledEditText) findViewById(R.id.flet_password_sign_up);
        flet_password_sign_up.setHint(Constants.showMessage(SignupFragmentActivity.this, mPreferences.getString("Lan_Id", ""), "passwordtost"));
        flet_email_sign_up = (FloatLabeledEditText) findViewById(R.id.flet_email_sign_up);
        flet_email_sign_up.setHint(Constants.showMessage(SignupFragmentActivity.this, mPreferences.getString("Lan_Id", ""), "emailtost"));
        tv_signUp = (NormalTextView) findViewById(R.id.tv_signUp);
        tv_signUp.setText(Constants.showMessage(SignupFragmentActivity.this, mPreferences.getString("Lan_Id", ""), "SignUp"));

        tv_google_plus = (NormalTextView) findViewById(R.id.tv_google_plus);
        fb_login_button_ = (NormalTextView) findViewById(R.id.fb_login_button_);

        fb_login_button_.setText(Constants.showMessage(SignupFragmentActivity.this, mPreferences.getString("Lan_Id", ""), "Sign up via Facebook"));
        tv_google_plus.setText(Constants.showMessage(SignupFragmentActivity.this, mPreferences.getString("Lan_Id", ""), "Sign up via Google+"));
    }

    private void FacebookLogin() {
        if (Utilities.isConnected(this)) {
            LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                @Override
                public void onSuccess(LoginResult loginResult) {
                    accessTokenNew = loginResult.getAccessToken().getToken();
                    mEditor.putString("fb_id", "").commit();
                    mEditor.putString("gpluse_id", "").commit();
                    mEditor.putString("socialname", "").commit();
                    mEditor.putString("socialemail", "").commit();
                    mEditor.putString("socialgender", "").commit();
                    GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(),
                            new GraphRequest.GraphJSONObjectCallback() {
                                @Override
                                public void onCompleted(
                                        JSONObject object,
                                        GraphResponse response) {
                                    try {
                                        mEditor.putString("umEmail", object.getString("email")).commit();
                                        mEditor.putString("fb_id", object.getString("id")).commit();

                                        String fb_firstname, fb_lastname, fb_email;
                                        try {
                                            fb_email = object.getString("email");
                                            fb_firstname = object.getString("name").split(" ")[0];
                                            fb_lastname = object.getString("name").split(" ")[1];
                                        } catch (Exception e) {
                                            fb_firstname = "";
                                            fb_lastname = "";
                                            fb_email = "";
                                            fb_firstname = object.getString("name");
                                        }
                                        mEditor.putString("umRealName", fb_firstname).commit();
                                        mEditor.putString("umLastName", fb_lastname).commit();
                                        et_username_sign_up.setText(object.getString("name"));
                                        et_email_sign_up.setText(fb_email);
                                        LoginManager.getInstance().logOut();
                                        checkFBID();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                    Bundle parameters = new Bundle();
                    parameters.putString("fields", "id,name,email,gender,birthday");
                    request.setParameters(parameters);
                    request.executeAsync();
                }

                @Override
                public void onCancel() {

                }

                @Override
                public void onError(FacebookException exception) {
                    Utilities.Log(TAG, "onError", exception);
                }
            });
        } else {
            Intent intent = new Intent(getApplicationContext(), NoInternet.class);
            startActivity(intent);
            //SnackbarManager.show(Snackbar.with(this).color(Utilities.getColor(this, R.color.mBlue)).text("Please check your internet connection"));
        }
    }

    private void checkFBID() {
        try {
            if (accessTokenNew != null && accessTokenNew.length() > 0 && mPreferences.getString("fb_id", "") != null && mPreferences.getString("umEmail", "") != null && mPreferences.getString("umRealName", "") != null) {
                String FBPICURL = "https://graph.facebook.com/" + mPreferences.getString("fb_id", "") + "/picture?type=large";
                Log.i("System out", "Fb Profile-->" + FBPICURL);
                mPreferences.edit().putString("User_ProfilePic", FBPICURL).commit();
            } else {
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        if (v == iv_close_header1) {
            if (Prefs.getString(Constants.from_login, "").equals("login")) {
                Prefs.putString(Constants.from_login, "logout");
                Intent mIntent = new Intent(SignupFragmentActivity.this, SignUpLoginFragmentActivity.class);
                startActivity(mIntent);
                finish();
            } else {
                Intent mIntent = new Intent(SignupFragmentActivity.this, MainActivity.class);
                startActivity(mIntent);
                finish();
                mEditor.putString("User_Id", "0").commit();
            }
        } else if (v == tv_signUp) {
            if (et_username_sign_up.getText().toString().length() == 0) {
                SnackbarManager.show(Snackbar.with(SignupFragmentActivity.this).color(Utils.getColor(this, R.color.mBlue)).text(Constants.showMessage(SignupFragmentActivity.this, mPreferences.getString("Lan_Id", ""), "NAME")));
            } else if (et_email_sign_up.getText().toString().length() == 0) {
                SnackbarManager.show(Snackbar.with(SignupFragmentActivity.this).color(Utils.getColor(this, R.color.mBlue)).text(Constants.showMessage(SignupFragmentActivity.this, mPreferences.getString("Lan_Id", ""), "EMAIL")));
            } else if (!Constants.isValidEmail(et_email_sign_up.getText().toString())) {
                SnackbarManager.show(Snackbar.with(SignupFragmentActivity.this).color(Utils.getColor(this, R.color.mBlue)).text(Constants.showMessage(SignupFragmentActivity.this, mPreferences.getString("Lan_Id", ""), "VALIDEMAIL")));
            } else if (et_password_sign_up.getText().toString().length() == 0) {
                SnackbarManager.show(Snackbar.with(SignupFragmentActivity.this).color(Utils.getColor(this, R.color.mBlue)).text(Constants.showMessage(SignupFragmentActivity.this, mPreferences.getString("Lan_Id", ""), "PASSWORD")));
            } else if (et_username_sign_up.getText().toString().contains("  ")) {
                SnackbarManager.show(Snackbar.with(SignupFragmentActivity.this).color(Utils.getColor(this, R.color.mBlue)).text(Constants.showMessage(SignupFragmentActivity.this, mPreferences.getString("Lan_Id", ""), "space_validation")));
            } else if (et_password_sign_up.getText().toString().contains("  ")) {
                SnackbarManager.show(Snackbar.with(SignupFragmentActivity.this).color(Utils.getColor(this, R.color.mBlue)).text(Constants.showMessage(SignupFragmentActivity.this, mPreferences.getString("Lan_Id", ""), "space_validation")));
            } else {
                if (mPreferences.getString("latitude2", "").equalsIgnoreCase("0") || mPreferences.getString("longitude2", "").equalsIgnoreCase("0")) {
                } else {
                    singUpCall();
                }
            }
        } else if (v == tv_forgot_sign_up) {
            Intent mIntent = new Intent(SignupFragmentActivity.this, ForgotPasswordFragmentActivity.class);
            startActivity(mIntent);
        } else if (v == tv_google_plus) {
            mGoogleApiClient = new GoogleApiClient.Builder(SignupFragmentActivity.this)
                    .addConnectionCallbacks(SignupFragmentActivity.this)
                    .addOnConnectionFailedListener(SignupFragmentActivity.this).addApi(Plus.API)
                    .addScope(Plus.SCOPE_PLUS_LOGIN).build();
            mGoogleApiClient.connect();
        } else if (v == fb_login_button_) {
            onClickFacebookLogin();
        }
    }

    public void onClickFacebookLogin() {
        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile", "user_friends", "user_about_me", "email", "user_birthday"));
    }

    private void singUpCall() {
        if (CommonClass.hasInternetConnection(SignupFragmentActivity.this)) {
            String url = Constants.SERVER_URL + "json.php?action=SignUp";
            String json = "[{\"User_Email\":\"" + et_email_sign_up.getText().toString() + "\",\"User_Password\":\"" + et_password_sign_up.getText().toString() + "\",\"User_Name\":\"" + et_username_sign_up.getText().toString() + "\",\"User_ProfilePic\":\"" + mPreferences.getString("User_ProfilePic", "") + "\",\"User_Address\":\"" + "" + "\",\"User_Latitude\":\"" + mPreferences.getString("latitude2", "") + "\",\"User_Longi\":\"" + mPreferences.getString("longitude2", "") + "\",\"Lan_Id\":\"" + mPreferences.getString("language", "") + "\",\"User_Facebook_ID\":\"" + mPreferences.getString("fb_id", "") + "\",\"User_GPlus_Id\":\"" + mPreferences.getString("gpluse_id", "") + "\",\"User_About\":\"" + "" + "\"}]";
            new PostSync(SignupFragmentActivity.this, "SignUp", SignupFragmentActivity.this).execute(url, json);
        } else {
            Intent intent = new Intent(getApplicationContext(), NoInternet.class);
            startActivity(intent);
            //SnackbarManager.show(Snackbar.with(SignupFragmentActivity.this).color(Utils.getColor(this,R.color.mBlue)).text(Constants.showMessage(SignupFragmentActivity.this, mPreferences.getString("Lan_Id", ""), "NOINTERNET")));
        }
    }

    public void signUpResponse(String resultString) {
        if (resultString.length() > 2) {
            try {
                JSONArray jsonArray = new JSONArray(resultString);
                JSONObject jsonObject = jsonArray.optJSONObject(0);
                if (jsonObject.has("status")) {
                    String str = jsonObject.optString("status");
                    if (str.equalsIgnoreCase("true")) {
                        mEditor.putString("User_Id", jsonObject.optString("User_Id")).commit();
                        JSONArray jsonArray1 = jsonObject.optJSONArray("user_details");
                        JSONObject jsonObject1 = jsonArray1.optJSONObject(0);
                        mEditor.putString("User_Email", jsonObject1.optString("User_Email")).commit();
                        mEditor.putString("user_Password", jsonObject1.optString("User_Pa ssword")).commit();
                        mEditor.putString("User_Name", jsonObject1.optString("User_Name")).commit();
                        mEditor.putString("User_ProfilePic", jsonObject1.optString("User_ProfilePic")).commit();
                        mEditor.putString("User_Address", jsonObject1.optString("User_Address")).commit();
                        mEditor.putString("User_Latitude", jsonObject1.optString("User_Latitude")).commit();
                        mEditor.putString("User_Longi", jsonObject1.optString("User_Longi")).commit();
                        mEditor.putString("User_DeviceType", jsonObject1.optString("User_DeviceType")).commit();
                        mEditor.putString("User_DeviceToken", jsonObject1.optString("User_DeviceToken")).commit();
                        mEditor.putString("User_Fav_Count", jsonObject1.optString("User _Fav_Count")).commit();
                        mEditor.putString("Lan_Id", jsonObject1.optString("Lan_Id")).commit();
                        mEditor.putString("User_Facebook_ID", jsonObject1.optString("User_Facebook_ID")).commit();
                        mEditor.putString("User_GPlus_Id", jsonObject1.optString("User_GPlus_Id")).commit();
                        mEditor.putString("User_About", jsonObject1.optString("User_About")).commit();
                        Constants.mFromSelectLocation = 0;
                        Intent mIntent = new Intent(SignupFragmentActivity.this, MainActivity.class);
                        mIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        mIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                        startActivity(mIntent);
                        finish();
                    } else {
                        SnackbarManager.show(Snackbar.with(SignupFragmentActivity.this).color(Utils.getColor(this, R.color.mBlue)).text(Constants.showMessage(SignupFragmentActivity.this, mPreferences.getString("Lan_Id", ""), "userExists")));
//                        SnackbarManager.show(Snackbar.with(SignupFragmentActivity.this).color(Utils.getColor(this, R.color.mBlue)).text(jsonObject.optString("status")));
                    }
                } else {
                    SnackbarManager.show(Snackbar.with(SignupFragmentActivity.this).color(Utils.getColor(this, R.color.mBlue)).text(Constants.showMessage(SignupFragmentActivity.this, mPreferences.getString("Lan_Id", ""), "userExists")));
//                    SnackbarManager.show(Snackbar.with(SignupFragmentActivity.this).color(Utils.getColor(this, R.color.mBlue)).text(jsonObject.optString("status")));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        int permissionCheck1 = ContextCompat.checkSelfPermission(this, android.Manifest.permission.GET_ACCOUNTS);
        if (permissionCheck1 == android.content.pm.PackageManager.PERMISSION_GRANTED) {
            mEditor.putString("umEmail", Plus.AccountApi.getAccountName(mGoogleApiClient)).commit();
            et_email_sign_up.setText(Plus.AccountApi.getAccountName(mGoogleApiClient));
            if (Plus.PeopleApi.getCurrentPerson(mGoogleApiClient) != null) {
                Person person = Plus.PeopleApi.getCurrentPerson(mGoogleApiClient);
                Person currentPerson = Plus.PeopleApi.getCurrentPerson(mGoogleApiClient);
                String personName = currentPerson.getDisplayName();
                String personPhoto = String.valueOf(currentPerson.getImage());
                String personGooglePlusProfile = currentPerson.getUrl();
                try {
                    JSONObject obj = new JSONObject(personPhoto.toString());
                    mPreferences.edit().putString("User_ProfilePic", obj.optString("url")).commit();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                et_username_sign_up.setText(personName);
                try {
                    JSONObject jsonObject = new JSONObject(person.toString());
                    String g_firstname, g_lastname;
                    if (jsonObject.getString("displayName").toString().length() != 0) {
                        g_firstname = jsonObject.getString("displayName").split(" ")[0];
                        g_lastname = jsonObject.getString("displayName").split(" ")[1];
                    } else {
                        g_firstname = "";
                        g_lastname = "";
                    }
                    mEditor.putString("gpluse_id", jsonObject.getString("id"));
                    mEditor.putString("umRealName", g_firstname);
                    mEditor.putString("umLastName", g_lastname);
                    mEditor.putString("socialgender", jsonObject.optString("gender"));
                    mEditor.putString("fb_id", "");
                    mEditor.commit();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {

            }
        } else {
            ActivityCompat.requestPermissions(SignupFragmentActivity.this, new String[]{android.Manifest.permission.GET_ACCOUNTS}, REQUEST_CODE_ASK_PERMISSIONS);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.e("System out", "Connection failed");
        if (!mIntentInProgress && connectionResult.hasResolution()) {
            try {
                mIntentInProgress = true;
                connectionResult.startResolutionForResult(SignupFragmentActivity.this, RC_SIGN_IN);
            } catch (IntentSender.SendIntentException e) {
                mIntentInProgress = false;
                mGoogleApiClient.connect();
            }
        }
    }

    protected void signInWithGplus() {
        if (!mGoogleApiClient.isConnecting()) {
            mSignInClicked = true;
            resolveSignInError();
        }
    }

    private void resolveSignInError() {
        if (mConnectionResult.hasResolution()) {
            try {
                mIntentInProgress = true;
                mConnectionResult.startResolutionForResult(this, RC_SIGN_IN);
            } catch (IntentSender.SendIntentException e) {
                mIntentInProgress = false;
                mGoogleApiClient.connect();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int responseCode, Intent intent) {
        if (requestCode == RC_SIGN_IN) {
            if (responseCode != RESULT_OK) {
                mSignInClicked = false;
            }
            mIntentInProgress = false;
            if (!mGoogleApiClient.isConnecting()) {
                mGoogleApiClient.connect();
            }
        } else {
            callbackManager.onActivityResult(requestCode, responseCode, intent);
        }
    }

    public GoogleApiClient getGoogleApiClient() {
        return mGoogleApiClient;
    }

    public void showMessage(String message) {

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent mIntent = new Intent(SignupFragmentActivity.this, SignUpLoginFragmentActivity.class);
        startActivity(mIntent);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (Constants.dialog != null) {
            Constants.dialog.dismiss();
        }
    }

    @Override
    public void onResponse(String response, String action) {
        try {
            if (action.equalsIgnoreCase("SignUp")) {
                signUpResponse(response);
            }
        } catch (Exception e) {
            Log.e(TAG, "onResponse Exception " + e.getLocalizedMessage());
        }
    }

    public void showPrivacyPolicyPopupDialog() {
        LayoutInflater layoutInflater = (LayoutInflater) getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View popupView = layoutInflater.inflate(R.layout.privacy_policy, null);
        popupWindow = new PopupWindow(popupView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, true);
        popupWindow.setTouchable(true);
        popupWindow.setFocusable(true);
        popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0);

        TextView ok = (TextView) popupView.findViewById(R.id.ok);
        TextView desc_txt = (TextView) popupView.findViewById(R.id.desc_txt);
        TextView privacy_txt = (TextView) popupView.findViewById(R.id.titel);

        //setting desc text based on user selected language
        privacy_txt.setText(Constants.showMessage(this, mPreferences.getString("Lan_Id", ""), "PrivacyPolicyTitle"));
        desc_txt.setText(Constants.showMessage(this, mPreferences.getString("Lan_Id", ""), "privacyPolicy"));
        ok.setText(Constants.showMessage(this, mPreferences.getString("Lan_Id", ""), "okButtonPrivacyPolicy"));

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });
    }
}
