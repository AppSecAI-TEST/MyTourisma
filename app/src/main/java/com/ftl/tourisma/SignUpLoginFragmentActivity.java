package com.ftl.tourisma;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.View;

import com.ftl.tourisma.activity.MainActivity;
import com.ftl.tourisma.custom_views.NormalTextView;
import com.ftl.tourisma.utils.Constants;
import com.pixplicity.easyprefs.library.Prefs;

/**
 * Created by fipl11111 on 22-Feb-16.
 */
public class SignUpLoginFragmentActivity extends FragmentActivity implements View.OnClickListener {

    private NormalTextView btn_sign_up1, btn_sign_up2, btn_login1;
    private SharedPreferences mPreferences;
    private SharedPreferences.Editor mEditor;
    private NormalTextView tv_started, txtSkipSignUpLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        setContentView(R.layout.activity_signup_login);
        mPreferences = getSharedPreferences(Constants.mPref, 0);
        mEditor = mPreferences.edit();
        initialisation();
        statusCheck();
        getLocationPermission();
        btn_login1.setOnClickListener(this);
        btn_sign_up2.setOnClickListener(this);
        txtSkipSignUpLogin.setOnClickListener(this);
    }

    private void initialisation() {
        btn_login1 = (NormalTextView) findViewById(R.id.btn_login1);
        btn_login1.setText(Constants.showMessage(SignUpLoginFragmentActivity.this, mPreferences.getString("Lan_Id", ""), "Login"));
        btn_sign_up1 = (NormalTextView) findViewById(R.id.btn_sign_up1);
        btn_sign_up2 = (NormalTextView) findViewById(R.id.btn_sign_up12);
        btn_sign_up1.setText(Constants.showMessage(SignUpLoginFragmentActivity.this, mPreferences.getString("Lan_Id", ""), "donthaveanaccount"));
        btn_sign_up2.setText(Constants.showMessage(SignUpLoginFragmentActivity.this, mPreferences.getString("Lan_Id", ""), "SignUp"));
        txtSkipSignUpLogin = (NormalTextView) findViewById(R.id.tvSkipSignUpLogin);
        txtSkipSignUpLogin.setText(Constants.showMessage(SignUpLoginFragmentActivity.this, mPreferences.getString("Lan_Id", ""), "Skip"));
    }

    @Override
    protected void onResume() {
        super.onResume();
//        SystemRequirementsChecker.checkWithDefaultDialogs(this);
    }

    public void statusCheck() {
        final LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            showSettingsAlert();
        }
    }

    private void showSettingsAlert() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("GPS settings");
        builder.setMessage("Your GPS seems to be disabled, Do you want to go to settings menu?")
                .setCancelable(false)
                .setPositiveButton("Settings", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    public void getLocationPermission() {
        int permissionCheck = ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION);
        if (permissionCheck == android.content.pm.PackageManager.PERMISSION_GRANTED) {
        } else {
            ActivityCompat.requestPermissions(SignUpLoginFragmentActivity.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 123);
        }
    }

    public void getAccountPermission() {
        int permissionCheck = ContextCompat.checkSelfPermission(this, android.Manifest.permission.GET_ACCOUNTS);
        if (permissionCheck == android.content.pm.PackageManager.PERMISSION_GRANTED) {
        } else {
            ActivityCompat.requestPermissions(SignUpLoginFragmentActivity.this, new String[]{android.Manifest.permission.GET_ACCOUNTS}, 123);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 123:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getLocationPermission();
                } else {
                    this.finish();
                }
                break;
            default:
        }
    }

    @Override
    public void onClick(View v) {
        if (v == btn_login1) {
            Prefs.putString(Constants.first_time, "first");
            Intent mIntent = new Intent(SignUpLoginFragmentActivity.this, LoginFragmentActivity.class);
            startActivity(mIntent);
            finish();
        } else if (v == btn_sign_up2) {
            Prefs.putString(Constants.from_login, "login");
            Prefs.putString(Constants.first_time, "first");
            Intent mIntent = new Intent(SignUpLoginFragmentActivity.this, SignupFragmentActivity.class);
            startActivity(mIntent);
            finish();
        } else if (v == txtSkipSignUpLogin) {
            Constants.mFromSelectLocation = 0;
            Prefs.putString(Constants.first_time, "first");
            Prefs.putString(Constants.beacons_guestuser_session, "start");
            Intent mIntent = new Intent(SignUpLoginFragmentActivity.this, MainActivity.class);
            startActivity(mIntent);
            finish();
            mEditor.putString("User_Id", "0").commit();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent mIntent = new Intent(SignUpLoginFragmentActivity.this, LanguageFragmentActivity.class);
        startActivity(mIntent);
        finish();
    }
}
