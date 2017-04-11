package com.ftl.tourisma;

import android.Manifest;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.ftl.tourisma.activity.MainActivity;
import com.ftl.tourisma.activity.NoInternet;
import com.ftl.tourisma.custom_views.NormalEditText;
import com.ftl.tourisma.custom_views.NormalTextView;
import com.ftl.tourisma.database.DBAdapter;
import com.ftl.tourisma.postsync.PostSync;
import com.ftl.tourisma.postsync.post_sync;
import com.ftl.tourisma.utils.CommonClass;
import com.ftl.tourisma.utils.Constants;
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

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * Created by harpalsinh on 25-Feb-16.
 */
public class LoginFragmentActivity extends FragmentActivity implements OnClickListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, post_sync.ResponseHandler {

    protected static final int RC_SIGN_IN = 0;
    private static final String TAG = "LoginFragmentActivity";
    final private int REQUEST_CODE_ASK_PERMISSIONS = 123;
    public GoogleApiClient mGoogleApiClient;
    protected boolean mIntentInProgress;
    protected boolean mSignInClicked;
    private ImageView iv_close_header1;
    private NormalTextView tv_forgot_login, tv_sign_up, tv_login, tv_login_fb, tv_login_g, login_using_mail;
    private NormalEditText et_email_login, et_password_login;
    private FloatLabeledEditText flet_email_login, flet_password_login;
    private SharedPreferences mPreferences;
    private SharedPreferences.Editor mEditor;
    private LoginButton fb_login_btn;
    private CallbackManager callbackManager;
    private String accessTokenNew;
    private TextView tv_google_plus, fb_login_button_;
    private ConnectionResult mConnectionResult;
    private DBAdapter dbAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        super.onCreate(savedInstanceState);
//        FacebookSdk.sdkInitialize(getApplicationContext());
//        callbackManager = CallbackManager.Factory.create();
        // Add code to print out the key hash
        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "com.facebook.samples.hellofacebook",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }
        setContentView(R.layout.activity_login);

        mPreferences = getSharedPreferences(Constants.mPref, 0);
        mEditor = mPreferences.edit();

        dbAdapter = new DBAdapter(LoginFragmentActivity.this);
        dbAdapter.open();
        dbAdapter.close();

        initialisation();

        tv_login.setOnClickListener(this);
        iv_close_header1.setOnClickListener(this);
        tv_forgot_login.setOnClickListener(this);
        tv_login_fb.setOnClickListener(this);
        tv_login_g.setOnClickListener(this);
    }

    private void initialisation() {

        FacebookSdk.sdkInitialize(this);
        FacebookSdk.setApplicationId(getResources().getString(R.string.facebook_app_id));
        callbackManager = CallbackManager.Factory.create();
        FacebookLogin();


        List<String> permissions = new ArrayList<String>();
        permissions.add("public_profile");
        permissions.add("email");
        permissions.add("user_birthday");

        /*fb_login_btn = (LoginButton) findViewById(R.id.fb_login_btn);
        fb_login_btn.setReadPermissions(permissions);

        fb_login_btn.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                accessTokenNew = loginResult.getAccessToken().getToken();
                mEditor.putString("fb_id", "").commit();
                mEditor.putString("socialname", "").commit();
                mEditor.putString("socialemail", "").commit();
                mEditor.putString("socialgender", "").commit();
                GraphRequest request = GraphRequest.newMeRequest(
                        loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(
                                    JSONObject object,
                                    GraphResponse response) {
//                                Log.d("System out", "response get is:" + response.toString());

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
                                    mEditor.putString("User_Email", fb_email).commit();
                                    mEditor.putString("User_Name", object.getString("name")).commit();

                                    LoginManager.getInstance().logOut();

                                    //: {"id":"612593892177596","birthday":"02\/17\/1984",
                                    // "gender":"male","email":"testlast11@gmail.com","name":"Sedy Last"}, error: null}

//                                    checkFBID();
                                    fbConnectCall();
                                } catch (Exception e) {
//                                    Log.d("System out", "please enter valid email or password");
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
            public void onError(FacebookException error) {

            }
        });
*/
        tv_login_g = (NormalTextView) findViewById(R.id.tv_login_g);
        login_using_mail = (NormalTextView) findViewById(R.id.login_using_mail);
        login_using_mail.setText(Constants.showMessage(LoginFragmentActivity.this, mPreferences.getString("Lan_Id", ""), "loginscreentitle"));
        tv_login_fb = (NormalTextView) findViewById(R.id.tv_login_fb);
        tv_login_fb.setText(Constants.showMessage(LoginFragmentActivity.this, mPreferences.getString("Lan_Id", ""), "Log in via Facebook"));
        tv_login_g.setText(Constants.showMessage(LoginFragmentActivity.this, mPreferences.getString("Lan_Id", ""), "Log in via Google+"));
//        if (mPreferences.getString("Lan_Id", "").equalsIgnoreCase("6")) {
//        } else if (mPreferences.getString("Lan_Id", "").equalsIgnoreCase("7")) {
//            tv_login_fb.setText("Войти через Facebook");
//            tv_login_g.setText("Войти через Google+");
//        } else {
//            tv_login_fb.setText("تسجيل الدخول عبر الفيسبوك");
//            tv_login_g.setText("تسجيل الدخول عبر Google+");
//        }
        tv_login = (NormalTextView) findViewById(R.id.tv_login);
        tv_login.setText(Constants.showMessage(LoginFragmentActivity.this, mPreferences.getString("Lan_Id", ""), "Login"));
        tv_forgot_login = (NormalTextView) findViewById(R.id.tv_forgot_login);
        tv_forgot_login.setText(Constants.showMessage(LoginFragmentActivity.this, mPreferences.getString("Lan_Id", ""), "forgotpassword"));
        iv_close_header1 = (ImageView) findViewById(R.id.img_close);
        tv_sign_up = (NormalTextView) findViewById(R.id.txtTitle);
        tv_sign_up.setText(Constants.showMessage(LoginFragmentActivity.this, mPreferences.getString("Lan_Id", ""), "loginheader"));
        et_email_login = (NormalEditText) findViewById(R.id.et_email_login);
        et_email_login.setHint(Constants.showMessage(LoginFragmentActivity.this, mPreferences.getString("Lan_Id", ""), "emailtost"));
        flet_email_login = (FloatLabeledEditText) findViewById(R.id.txt_email);
        flet_email_login.setHint(Constants.showMessage(LoginFragmentActivity.this, mPreferences.getString("Lan_Id", ""), "emailtost"));
        et_password_login = (NormalEditText) findViewById(R.id.et_password_login);
        et_password_login.setHint(Constants.showMessage(LoginFragmentActivity.this, mPreferences.getString("Lan_Id", ""), "passwordtost"));
        flet_password_login = (FloatLabeledEditText) findViewById(R.id.flet_password_login);
        flet_password_login.setHint(Constants.showMessage(LoginFragmentActivity.this, mPreferences.getString("Lan_Id", ""), "passwordtost"));
    }

    private void FacebookLogin() {
        if (Utilities.isConnected(this)) {
            LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                @Override
                public void onSuccess(LoginResult loginResult) {
                    GraphRequest request = GraphRequest.newMeRequest(
                            loginResult.getAccessToken(),
                            new GraphRequest.GraphJSONObjectCallback() {
                                @Override
                                public void onCompleted(
                                        JSONObject object,
                                        GraphResponse response) {
//                                Log.d("System out", "response get is:" + response.toString());

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
                                        mEditor.putString("User_Email", fb_email).commit();
                                        mEditor.putString("User_Name", object.getString("name")).commit();

                                        LoginManager.getInstance().logOut();

                                        //: {"id":"612593892177596","birthday":"02\/17\/1984",
                                        // "gender":"male","email":"testlast11@gmail.com","name":"Sedy Last"}, error: null}

//                                    checkFBID();
                                        fbConnectCall();
                                    } catch (Exception e) {
//                                    Log.d("System out", "please enter valid email or password");
                                        e.printStackTrace();
                                    }
                                }
                            });

                    Bundle parameters = new Bundle();
                    parameters.putString("fields", "id,name,first_name,last_name,link,birthday,picture,email,gender");
                    request.setParameters(parameters);
                    request.executeAsync();
                }

                @Override
                public void onCancel() {
//
                }

                @Override
                public void onError(FacebookException exception) {

//                    Toast.makeText(
//                            getActivity().getApplicationContext(),
//                            "Try Again..." + exception.getMessage(),
//                            Toast.LENGTH_SHORT).show();
                    Utilities.Log(TAG, "onError", exception);


                }
            });
        } else {
            Intent intent = new Intent(getApplicationContext(), NoInternet.class);
            startActivity(intent);
            //SnackbarManager.show(Snackbar.with(LoginFragmentActivity.this).color(Utilities.getColor(this, R.color.mBlue)).text("Please check your internet connection"));
        }
    }

    @Override
    public void onClick(View v) {
        if (v == tv_login) {
            if (et_email_login.getText().toString().length() == 0) {
                SnackbarManager.show(Snackbar.with(LoginFragmentActivity.this).color(Utils.getColor(this,R.color.mBlue)).text(Constants.showMessage(LoginFragmentActivity.this, mPreferences.getString("Lan_Id", ""), "EMAIL")));
            } else if (!Constants.isValidEmail(et_email_login.getText().toString())) {
                SnackbarManager.show(Snackbar.with(LoginFragmentActivity.this).color(Utils.getColor(this,R.color.mBlue)).text(Constants.showMessage(LoginFragmentActivity.this, mPreferences.getString("Lan_Id", ""), "VALIDEMAIL")));
            } else if (et_password_login.getText().toString().length() == 0) {
                SnackbarManager.show(Snackbar.with(LoginFragmentActivity.this).color(Utils.getColor(this,R.color.mBlue)).text(Constants.showMessage(LoginFragmentActivity.this, mPreferences.getString("Lan_Id", ""), "PASSWORD")));
            } else {
                loginCall();
            }
        } else if (v == iv_close_header1) {
            Intent mIntent = new Intent(LoginFragmentActivity.this, SignUpLoginFragmentActivity.class);
            startActivity(mIntent);
            finish();
        } else if (v == tv_forgot_login) {
            Intent mIntent = new Intent(LoginFragmentActivity.this, ForgotPasswordFragmentActivity.class);
            startActivity(mIntent);
        } else if (v == tv_login_g) {
            Prefs.putInt(Constants.user_id, 1);
            mGoogleApiClient = new GoogleApiClient.Builder(LoginFragmentActivity.this)
                    .addConnectionCallbacks(LoginFragmentActivity.this)
                    .addOnConnectionFailedListener(LoginFragmentActivity.this).addApi(Plus.API)
                    .addScope(Plus.SCOPE_PLUS_LOGIN).build();
            mGoogleApiClient.connect();
        } else if (v == tv_login_fb) {
//            fb_login_btn.performClick();
            onClickFacebookLogin();
        }
    }

    public void onClickFacebookLogin() {

        Prefs.putInt(Constants.user_id, 1);

        LoginManager.getInstance().logInWithReadPermissions(
                this,
                // (Arrays.asList("public_profile", "user_friends",
                // "user_birthday", "user_location",
                // "user_about_me", "email")));
                Arrays.asList("public_profile", "user_friends",
                        "user_about_me", "email", "user_birthday"));
    }

    private void loginCall() {

        if (CommonClass.hasInternetConnection(LoginFragmentActivity.this)) {
            String url = Constants.SERVER_URL + "json.php?action=Login";
            String json = "[{\"User_Email\":\"" + et_email_login.getText().toString() + "\",\"User_Password\":\"" + et_password_login.getText().toString() + "\"}]";
//            Log.d("System out", "Login " + json);
//            new PostSync(LoginFragmentActivity.this, "Login").execute(url, json);
            new PostSync(LoginFragmentActivity.this, "Login",LoginFragmentActivity.this).execute(url, json);
        } else {
            Intent intent = new Intent(getApplicationContext(), NoInternet.class);
            startActivity(intent);
            //SnackbarManager.show(Snackbar.with(LoginFragmentActivity.this).color(Utils.getColor(this,R.color.mBlue)).text(Constants.showMessage(LoginFragmentActivity.this, mPreferences.getString("Lan_Id", ""), "NOINTERNET")));
        }
    }

    public void loginResponse(String resultString) {
//        Log.d("System out", "result string loginCall " + resultString);

        if (resultString.length() > 2) {
            try {
                JSONArray jsonArray = new JSONArray(resultString);
                JSONObject jsonObject = jsonArray.optJSONObject(0);
                if (jsonObject.has("User_Id")) {
//                    String str = jsonObject.optString("status");
//                    if (str.equalsIgnoreCase("true")) {
                    Prefs.putInt(Constants.user_id, Integer.parseInt(jsonObject.optString("User_Id")));
                    mEditor.putString("User_Id", jsonObject.optString("User_Id")).commit();
                    mEditor.putString("User_Email", jsonObject.optString("User_Email")).commit();
                    mEditor.putString("User_Password", jsonObject.optString("User_Password")).commit();
                    mEditor.putString("User_Name", jsonObject.optString("User_Name")).commit();
                    mEditor.putString("User_ProfilePic", jsonObject.optString("User_ProfilePic")).commit();
                    mEditor.putString("User_Address", jsonObject.optString("User_Address")).commit();
                    mEditor.putString("User_Latitude", jsonObject.optString("User_Latitude")).commit();
                    mEditor.putString("User_Longi", jsonObject.optString("User_Longi")).commit();
                    mEditor.putString("User_DeviceType", jsonObject.optString("User_DeviceType")).commit();
                    mEditor.putString("User_DeviceToken", jsonObject.optString("User_DeviceToken")).commit();
                    mEditor.putString("User_Fav_Count", jsonObject.optString("User_Fav_Count")).commit();
//                    mEditor.putString("Lan_Id", jsonObject.optString("Lan_Id")).commit();
                    mEditor.putString("User_Facebook_ID", jsonObject.optString("User_Facebook_ID")).commit();
                    mEditor.putString("User_GPlus_Id", jsonObject.optString("User_GPlus_Id")).commit();
                    mEditor.putString("User_About", jsonObject.optString("User_About")).commit();
                    Constants.mFromSelectLocation = 0;

                    Intent mIntent = new Intent(LoginFragmentActivity.this, MainActivity.class);
//                    Intent mIntent = new Intent(LoginFragmentActivity.this, SelectLocationFragmentActivity.class);
                    mIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    mIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                    startActivity(mIntent);
                    finish();
//                    } else {
//                    }
                } else {
                    SnackbarManager.show(Snackbar.with(LoginFragmentActivity.this).color(Utils.getColor(this, R.color.mBlue)).text(Constants.showMessage(LoginFragmentActivity.this, mPreferences.getString("Lan_Id", ""), "INVALID")));
//                    SnackbarManager.show(Snackbar.with(LoginFragmentActivity.this).color(Utils.getColor(this,R.color.mBlue)).text(jsonObject.optString("status")));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public void onConnected(Bundle bundle) {
        int permissionCheck1 = ContextCompat.checkSelfPermission(this, android.Manifest.permission.GET_ACCOUNTS);
//        Log.d("System out", "GET_ACCOUNTS " + permissionCheck1);
        if (permissionCheck1 == android.content.pm.PackageManager.PERMISSION_GRANTED) {
//            Log.d("System out", "account name" + Plus.AccountApi.getAccountName(mGoogleApiClient));
            mEditor.putString("umEmail", Plus.AccountApi.getAccountName(mGoogleApiClient)).commit();
//        et_email_sign_up.setText(Plus.AccountApi.getAccountName(mGoogleApiClient));

//            Log.d("System out", "apis" + Plus.API.toString());
            //Plus.PeopleApi.loadVisible(mGoogleApiClient, null);

            if (Plus.PeopleApi.getCurrentPerson(mGoogleApiClient) != null) {
                Person person = Plus.PeopleApi.getCurrentPerson(mGoogleApiClient);
                Person currentPerson = Plus.PeopleApi.getCurrentPerson(mGoogleApiClient);
                String personName = currentPerson.getDisplayName();
                String personPhoto = String.valueOf(currentPerson.getImage());
                String personGooglePlusProfile = currentPerson.getUrl();
//                Log.d("System out", "personName-->" + personName);
//                Log.d("System out", "personPhoto-->" + personPhoto);
//                Log.d("System out", "personGooglePlusProfile-->" + personGooglePlusProfile);

                try {
                    JSONObject obj = new JSONObject(personPhoto.toString());
                    mPreferences.edit().putString("User_ProfilePic", obj.optString("url")).commit();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

//            et_username_sign_up.setText(personName);

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
                    mEditor.putString("gpluse_id", jsonObject.getString("id")).commit();
                    mEditor.putString("umRealName", g_firstname).commit();
                    mEditor.putString("umLastName", g_lastname).commit();
                    mEditor.putString("socialgender", jsonObject.optString("gender")).commit();
                    mEditor.putString("User_Name", personName).commit();

                    gPlusConnectCall();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
//                Log.d("System out", "detail of users else");
            }
        } else {
            ActivityCompat.requestPermissions(LoginFragmentActivity.this, new String[]{Manifest.permission.GET_ACCOUNTS}, REQUEST_CODE_ASK_PERMISSIONS);
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
                connectionResult.startResolutionForResult(LoginFragmentActivity.this, RC_SIGN_IN);
            } catch (IntentSender.SendIntentException e) {
                mIntentInProgress = false;
                mGoogleApiClient.connect();
            }
        }

//        Intent i = new Intent(this, ResolverActivity.class);
//        i.putExtra(ResolverActivity.CONNECT_RESULT_KEY, connectionResult);
//        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        startActivity(i);

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
//            Log.d("System out", "data " + intent.getExtras().toString());
        }
    }


    public void signOutFromGplus() {
        if (mGoogleApiClient.isConnected()) {
            Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
            mGoogleApiClient.disconnect();
            mGoogleApiClient.connect();
        }
    }


    public GoogleApiClient getGoogleApiClient() {
        return mGoogleApiClient;
    }

    public void showMessage(String message) {
//        login_sn.applyStyle(R.style.Material_Widget_SnackBar_Tablet).text(message).actionText(null).duration(3000).show();
    }

    private void fbConnectCall() {
        if (CommonClass.hasInternetConnection(LoginFragmentActivity.this)) {
            String url = Constants.SERVER_URL + "json.php?action=FBConnect";
            String json = "[{\"User_Email\":\"" + mPreferences.getString("User_Email", "") + "\",\"User_Password\":\"" + "" + "\",\"User_Name\":\"" + mPreferences.getString("User_Name", "") + "\",\"User_ProfilePic\":\"" + "" + "\",\"User_Address\":\"" + "" + "\",\"User_Latitude\":\"" + mPreferences.getString("latitude2", "") + "\",\"User_Longi\":\"" + mPreferences.getString("longitude2", "") + "\",\"Lan_Id\":\"" + mPreferences.getString("language", "") + "\",\"User_Facebook_ID\":\"" + mPreferences.getString("fb_id", "") + "\",\"User_GPlus_Id\":\"" + mPreferences.getString("gpluse_id", "") + "\",\"User_About\":\"" + "" + "\"}]";
//            Log.d("System out", "FBConnect " + json);
//            new PostSync(LoginFragmentActivity.this, "FBConnect").execute(url, json);
            new PostSync(LoginFragmentActivity.this, "FBConnect",LoginFragmentActivity.this).execute(url, json);
        } else {
            Intent intent = new Intent(getApplicationContext(), NoInternet.class);
            startActivity(intent);
            //SnackbarManager.show(Snackbar.with(LoginFragmentActivity.this).color(Utils.getColor(this,R.color.mBlue)).text(Constants.showMessage(LoginFragmentActivity.this, mPreferences.getString("Lan_Id", ""), "NOINTERNET")));
        }
    }

    public void fbConnectResponse(String resultString) {
//        Log.d("System out", "fbConnectResponse " + resultString);

        if (resultString.length() > 2) {
            try {
                JSONArray jsonArray = new JSONArray(resultString);
                JSONObject jsonObject = jsonArray.optJSONObject(0);
                if (jsonObject.has("status")) {
                    String str = jsonObject.optString("status");
                    if (str.equalsIgnoreCase("true")) {

                        JSONArray jsonArray1 = jsonObject.optJSONArray("data");
                        JSONObject jsonObject1 = jsonArray1.optJSONObject(0);

                        mEditor.putString("User_Id", jsonObject1.optString("User_Id")).commit();
//                        mEditor.putString("User_Email", jsonObject1.optString("User_Email")).commit();
                        mEditor.putString("user_Password", jsonObject1.optString("User_Pa ssword")).commit();
//                        mEditor.putString("User_Name", jsonObject1.optString("User_Name")).commit();
                        mEditor.putString("User_ProfilePic", jsonObject1.optString("User_ProfilePic")).commit();
                        mEditor.putString("User_Address", jsonObject1.optString("User_Address")).commit();
                        mEditor.putString("User_Latitude", jsonObject1.optString("User_Latitude")).commit();
                        mEditor.putString("User_Longi", jsonObject1.optString("User_Longi")).commit();
                        mEditor.putString("User_DeviceType", jsonObject1.optString("User_DeviceType")).commit();
                        mEditor.putString("User_DeviceToken", jsonObject1.optString("User_DeviceToken")).commit();
                        mEditor.putString("User_Fav_Count", jsonObject1.optString("User _Fav_Count")).commit();
//                        mEditor.putString("Lan_Id", jsonObject1.optString("Lan_Id")).commit();
                        mEditor.putString("User_Facebook_ID", jsonObject1.optString("User_Facebook_ID")).commit();
                        mEditor.putString("User_GPlus_Id", jsonObject1.optString("User_GPlus_Id")).commit();
                        mEditor.putString("User_About", jsonObject1.optString("User_About")).commit();

                        FragmentManager fm = getSupportFragmentManager();
                        int count = fm.getBackStackEntryCount();
                        for (int i = 0; i < count; ++i) {
                            fm.popBackStackImmediate();
                        }
                        Constants.mFromSelectLocation = 0;

                        Intent mIntent = new Intent(LoginFragmentActivity.this, MainActivity.class);
                        mIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        mIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                        startActivity(mIntent);
                        finish();
                    } else {
                        SnackbarManager.show(Snackbar.with(LoginFragmentActivity.this).color(Utils.getColor(this,R.color.mBlue)).text(jsonObject.optString("status")));
                    }
                } else {
                    Constants.mFromSelectLocation = 0;

                    Intent mIntent = new Intent(LoginFragmentActivity.this, MainActivity.class);
                    startActivity(mIntent);
                    finish();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void gPlusConnectCall() {
        if (CommonClass.hasInternetConnection(LoginFragmentActivity.this)) {
            String url = Constants.SERVER_URL + "json.php?action=GPConnect";
            String json = "[{\"User_Email\":\"" + mPreferences.getString("umEmail", "") + "\",\"User_Password\":\"" + "" + "\",\"User_Name\":\"" + mPreferences.getString("User_Name", "") + "\",\"User_ProfilePic\":\"" + "" + "\",\"User_Address\":\"" + "" + "\",\"User_Latitude\":\"" + mPreferences.getString("latitude2", "") + "\",\"User_Longi\":\"" + mPreferences.getString("longitude2", "") + "\",\"Lan_Id\":\"" + mPreferences.getString("language", "") + "\",\"User_Facebook_ID\":\"" + mPreferences.getString("fb_id", "") + "\",\"User_GPlus_Id\":\"" + mPreferences.getString("gpluse_id", "") + "\",\"User_About\":\"" + "" + "\"}]";
//            Log.d("System out", "GPConnect " + json);
//            new PostSync(LoginFragmentActivity.this, "GPConnect").execute(url, json);
            new PostSync(LoginFragmentActivity.this, "GPConnect",LoginFragmentActivity.this).execute(url, json);
        } else {
            Intent intent = new Intent(getApplicationContext(), NoInternet.class);
            startActivity(intent);
            //SnackbarManager.show(Snackbar.with(LoginFragmentActivity.this).color(Utils.getColor(this,R.color.mBlue)).text(Constants.showMessage(LoginFragmentActivity.this, mPreferences.getString("Lan_Id", ""), "NOINTERNET")));
        }
    }

    public void gPlusResponse(String resultString) {
//        Log.d("System out", "gPlusConnectResponse " + resultString);

        if (resultString.length() > 2) {
            try {
                JSONArray jsonArray = new JSONArray(resultString);
                JSONObject jsonObject = jsonArray.optJSONObject(0);
                if (jsonObject.has("status")) {
                    String str = jsonObject.optString("status");
                    if (str.equalsIgnoreCase("true")) {

                        JSONArray jsonArray1 = jsonObject.optJSONArray("data");
                        JSONObject jsonObject1 = jsonArray1.optJSONObject(0);

                        mEditor.putString("User_Id", jsonObject1.optString("User_Id")).commit();
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
//                        mEditor.putString("Lan_Id", jsonObject1.optString("Lan_Id")).commit();
                        mEditor.putString("User_Facebook_ID", jsonObject1.optString("User_Facebook_ID")).commit();
                        mEditor.putString("User_GPlus_Id", jsonObject1.optString("User_GPlus_Id")).commit();
                        mEditor.putString("User_About", jsonObject1.optString("User_About")).commit();

                        /*FragmentManager fm = getSupportFragmentManager();
                        int count = fm.getBackStackEntryCount();
                        for (int i = 0; i < count; ++i) {
                            fm.popBackStackImmediate();
                        }*/
                        Constants.mFromSelectLocation = 0;

                        Intent mIntent = new Intent(LoginFragmentActivity.this, MainActivity.class);
                        startActivity(mIntent);
                        finish();
                    } else {
                        SnackbarManager.show(Snackbar.with(LoginFragmentActivity.this).color(Utils.getColor(this,R.color.mBlue)).text(jsonObject.optString("status")));
                    }
                } else {
                    Constants.mFromSelectLocation = 0;

                    Intent mIntent = new Intent(LoginFragmentActivity.this, MainActivity.class);
                    startActivity(mIntent);
                    finish();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Intent mIntent = new Intent(LoginFragmentActivity.this, SignUpLoginFragmentActivity.class);
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
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mGoogleApiClient.connect();
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {
                    this.finish();
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    @Override
    public void onResponse(String response, String action) {
        try {
            if (action.equalsIgnoreCase("Login")) {
                loginResponse(response);
            }else if (action.equalsIgnoreCase("FBConnect")) {
                fbConnectResponse(response);
            }else if (action.equalsIgnoreCase("GPConnect")) {
                gPlusResponse(response);
            }
        } catch (Exception e) {
            Log.e(TAG, "onResponse Exception " + e.getLocalizedMessage());
        }
    }
}
