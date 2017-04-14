package com.ftl.tourisma;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.view.View;

import com.estimote.sdk.SystemRequirementsChecker;
import com.ftl.tourisma.activity.MainActivity;
import com.ftl.tourisma.custom_views.NormalTextView;
import com.ftl.tourisma.utils.Constants;

/**
 * Created by fipl11111 on 22-Feb-16.
 */
public class SignUpLoginFragmentActivity extends FragmentActivity implements View.OnClickListener {

    private NormalTextView btn_sign_up1, btn_sign_up2, btn_login1;
    private SharedPreferences mPreferences;
    private SharedPreferences.Editor mEditor;
    private NormalTextView tv_started, txtSkipSignUpLogin;

//    final private int REQUEST_CODE_ASK_PHONE_PERMISSIONS = 123;
//    final private int REQUEST_CODE_ASK_LOCATION_PERMISSIONS = 234;
//    final private int REQUEST_CODE_ASK_WRITE_PERMISSIONS = 345;
//    final private int REQUEST_CODE_ASK_READ_PERMISSIONS = 456;
//    final private int REQUEST_CODE_ASK_READ_CONTACT_PERMISSIONS = 567;
//    final private int REQUEST_CODE_ASK_ACCOUNT_PERMISSIONS = 678;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        setContentView(R.layout.activity_signup_login);
        mPreferences = getSharedPreferences(Constants.mPref, 0);
        mEditor = mPreferences.edit();
        initialisation();
        btn_login1.setOnClickListener(this);
        btn_sign_up2.setOnClickListener(this);
        txtSkipSignUpLogin.setOnClickListener(this);

        /*//getting country code
        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        String countryCode = telephonyManager.getSimCountryIso();
        String countryCodeValue = telephonyManager.getNetworkCountryIso();
        String locale = Locale.getDefault().getISO3Country();
        System.out.println("country_code " + countryCode);
        System.out.println("countryCodeValue " + countryCodeValue);
        System.out.println("locale " + locale);

        if (!(countryCodeValue.equals("ae") || countryCodeValue.equals("AE"))) {
            Intent intent = new Intent(SignUpLoginFragmentActivity.this, CountrySpecific.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }*/
    }

    private void initialisation() {
        btn_login1 = (NormalTextView) findViewById(R.id.btn_login1);
        btn_login1.setText(Constants.showMessage(SignUpLoginFragmentActivity.this, mPreferences.getString("Lan_Id", ""), "Login"));
        btn_sign_up1 = (NormalTextView) findViewById(R.id.btn_sign_up1);
        btn_sign_up2 = (NormalTextView) findViewById(R.id.btn_sign_up12);
        btn_sign_up1.setText(Constants.showMessage(SignUpLoginFragmentActivity.this, mPreferences.getString("Lan_Id", ""), "donthaveanaccount"));
        btn_sign_up2.setText(Constants.showMessage(SignUpLoginFragmentActivity.this, mPreferences.getString("Lan_Id", ""), "SignUp"));

//      TODO enable this line  btn_sign_up1.setText(Constants.showMessage(SignUpLoginFragmentActivity.this, mPreferences.getString("Lan_Id", ""), "SignUp"));
//        tv_started = (NormalTextView) findViewById(R.id.tv_started);
//        tv_started.setText(Constants.showMessage(SignUpLoginFragmentActivity.this, mPreferences.getString("Lan_Id", ""), "GetStarted"));
        txtSkipSignUpLogin = (NormalTextView) findViewById(R.id.tvSkipSignUpLogin);
        txtSkipSignUpLogin.setText(Constants.showMessage(SignUpLoginFragmentActivity.this, mPreferences.getString("Lan_Id", ""), "Skip"));
        getAccountPermission();
    }

    @Override
    protected void onResume() {
        super.onResume();
        SystemRequirementsChecker.checkWithDefaultDialogs(this);
    }

    public void getLocationPermission() {
        int permissionCheck = ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION);
        if (permissionCheck == android.content.pm.PackageManager.PERMISSION_GRANTED) {
            getReadStoragePermission();
        } else {
            ActivityCompat.requestPermissions(SignUpLoginFragmentActivity.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 123);
        }
    }

    public void getReadStoragePermission() {
        int permissionCheck = ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE);
        if (permissionCheck == android.content.pm.PackageManager.PERMISSION_GRANTED) {
        } else {
            ActivityCompat.requestPermissions(SignUpLoginFragmentActivity.this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, 123);
        }
    }

    public void giveBluetoothPermission() {
        int permissionCheck = ContextCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH_ADMIN);
        int permissionCheck1 = ContextCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH);
        if (permissionCheck == android.content.pm.PackageManager.PERMISSION_GRANTED && permissionCheck1 == android.content.pm.PackageManager.PERMISSION_GRANTED) {
        } else {
            ActivityCompat.requestPermissions(SignUpLoginFragmentActivity.this, new String[]{android.Manifest.permission.BLUETOOTH_ADMIN}, 123);
            ActivityCompat.requestPermissions(SignUpLoginFragmentActivity.this, new String[]{android.Manifest.permission.BLUETOOTH}, 123);
        }
    }

    public void getWriteStoragePermission() {
        int permissionCheck = ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permissionCheck == android.content.pm.PackageManager.PERMISSION_GRANTED) {
        } else {
            ActivityCompat.requestPermissions(SignUpLoginFragmentActivity.this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 123);
        }
    }

    public void givePhoneStatePermission() {
        int permissionCheck = ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_PHONE_STATE);
        if (permissionCheck == android.content.pm.PackageManager.PERMISSION_GRANTED) {
            giveBluetoothPermission();
        } else {
            ActivityCompat.requestPermissions(SignUpLoginFragmentActivity.this, new String[]{android.Manifest.permission.READ_PHONE_STATE}, 123);
        }
    }

    public void getAccountPermission() {
        int permissionCheck = ContextCompat.checkSelfPermission(this, android.Manifest.permission.GET_ACCOUNTS);
        if (permissionCheck == android.content.pm.PackageManager.PERMISSION_GRANTED) {
        } else {
            ActivityCompat.requestPermissions(SignUpLoginFragmentActivity.this, new String[]{android.Manifest.permission.GET_ACCOUNTS}, 123);
        }
    }

    public void getContactPermission() {
        int permissionCheck = ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_CONTACTS);
        if (permissionCheck == android.content.pm.PackageManager.PERMISSION_GRANTED) {
        } else {
            ActivityCompat.requestPermissions(SignUpLoginFragmentActivity.this, new String[]{android.Manifest.permission.READ_CONTACTS}, 123);
        }
    }

    public void getCameraPermission() {
        int permissionCheck = ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA);
        if (permissionCheck == android.content.pm.PackageManager.PERMISSION_GRANTED) {
        } else {
            ActivityCompat.requestPermissions(SignUpLoginFragmentActivity.this, new String[]{android.Manifest.permission.CAMERA}, 123);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 123:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    getAccountPermission();
                } else {
                    this.finish();
//                    getLocationPermission();
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                break;
            /*case REQUEST_CODE_ASK_PHONE_PERMISSIONS:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    getLocationPermission();
                } else {
                    givePhoneStatePermission();
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                break;
            case REQUEST_CODE_ASK_WRITE_PERMISSIONS:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    getReadStoragePermission();
                } else {
                    getWriteStoragePermission();
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                break;
            case REQUEST_CODE_ASK_READ_PERMISSIONS:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    getContactPermission();
                } else {
                    getReadStoragePermission();
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                break;
            case REQUEST_CODE_ASK_READ_CONTACT_PERMISSIONS:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    getAccountPermission();
                } else {
                    getContactPermission();
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                break;
            case REQUEST_CODE_ASK_ACCOUNT_PERMISSIONS:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {
                    getAccountPermission();
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                break;

*/
            default:
        }
    }

    @Override
    public void onClick(View v) {
        if (v == btn_login1) {
            Intent mIntent = new Intent(SignUpLoginFragmentActivity.this, LoginFragmentActivity.class);
            startActivity(mIntent);
            finish();
        } else if (v == btn_sign_up2) {
            Intent mIntent = new Intent(SignUpLoginFragmentActivity.this, SignupFragmentActivity.class);
            startActivity(mIntent);
            finish();
        } else if (v == txtSkipSignUpLogin) {
            Constants.mFromSelectLocation = 0;
            Intent mIntent = new Intent(SignUpLoginFragmentActivity.this, MainActivity.class);
//            Intent mIntent = new Intent(SignUpLoginFragmentActivity.this, SelectLocationFragmentActivity.class);
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
