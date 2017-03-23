package com.ftl.tourisma.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.ftl.tourisma.R;
import com.ftl.tourisma.utils.Constants;

/**
 * Created by Vinay on 3/3/2017.
 */

public class CountrySpecific extends FragmentActivity {

    TextView sry_txt, scnd_txt;
    Button change_loc_btn, suggest_loc_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.country_specific);
        dcl_layout_variables();
        onClickListners();
        givePhoneStatePermission();

        //language
        if (Constants.language.equals("arabic")) {
            change_loc_btn.setText("تغيير الموقع");
            suggest_loc_btn.setText("يقترحون الموقع");
            sry_txt.setText(" للأسف !");
            scnd_txt.setText("myTourismaتغطي الحدود الجغرافية لدولة الإمارات العربية المتحدة  فقط حالياً المزيد من الدول سيتم اضافتها قريباً");
        } else if (Constants.language.equals("russian")) {
            change_loc_btn.setText("Изменить местоположение");
            suggest_loc_btn.setText("Предложить местоположение");
            sry_txt.setText("Простите!");
            scnd_txt.setText("В настоящее время myTourisma содержит места только из ОАЭ. Больше стран будут добавлены в ближайшее время. Следите за обновлениями.");
        }
    }

    public void dcl_layout_variables() {
        sry_txt = (TextView) findViewById(R.id.whoops_txt);
        scnd_txt = (TextView) findViewById(R.id.scnd_txt);
        change_loc_btn = (Button) findViewById(R.id.change_loc_btn);
        suggest_loc_btn = (Button) findViewById(R.id.suggest_loc_btn);
    }

    public void onClickListners() {
        change_loc_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = new SelectLocationFragment();
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_frame, fragment, fragment.getClass().getSimpleName()).addToBackStack(null).commit();
            }
        });

        suggest_loc_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent emailIntent = new Intent(Intent.ACTION_SEND);
//                emailIntent.setAction(Intent.ACTION_SEND);
                emailIntent.setClassName("com.google.android.gm", "com.google.android.gm.ComposeActivityGmail");
                emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{"info@mytourisma.com"});
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "myTourisma - Suggest new location");
                emailIntent.putExtra(Intent.EXTRA_TEXT, "Hello,\n" +
                        "\n" +
                        "\n" +
                        "I would like to suggest a new location for myTourisma app\n" +
                        "\n" +
                        "Location name:\n" +
                        "Location:\n" +
                        "\n" +
                        "\n" +
                        "\n" +
                        "Thank you");
                emailIntent.setType("text/plain");
                startActivity(Intent.createChooser(emailIntent, "myTourisma"));
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    public void givePhoneStatePermission() {
        int permissionCheck = ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_PHONE_STATE);
        if (permissionCheck == android.content.pm.PackageManager.PERMISSION_GRANTED) {
            getLocationPermission();
        } else {
            ActivityCompat.requestPermissions(CountrySpecific.this, new String[]{android.Manifest.permission.READ_PHONE_STATE}, 123);
        }
    }

    public void getLocationPermission() {
        int permissionCheck = ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION);
        if (permissionCheck == android.content.pm.PackageManager.PERMISSION_GRANTED) {
            getBluetoothPermission();
        } else {
            ActivityCompat.requestPermissions(CountrySpecific.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 123);
        }
    }

    public void getBluetoothPermission() {
        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_ADMIN);
        int permissionCheck1 = ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH);
        if (permissionCheck == android.content.pm.PackageManager.PERMISSION_GRANTED && permissionCheck1 == android.content.pm.PackageManager.PERMISSION_GRANTED) {
            getWriteStoragePermission();
        } else {
            ActivityCompat.requestPermissions(CountrySpecific.this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 123);
        }
    }

    public void getWriteStoragePermission() {
        int permissionCheck = ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permissionCheck == android.content.pm.PackageManager.PERMISSION_GRANTED) {
            getReadStoragePermission();
        } else {
            ActivityCompat.requestPermissions(CountrySpecific.this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 123);
        }
    }

    public void getReadStoragePermission() {
        int permissionCheck = ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE);
        if (permissionCheck == android.content.pm.PackageManager.PERMISSION_GRANTED) {
            getContactPermission();
        } else {
            ActivityCompat.requestPermissions(CountrySpecific.this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, 123);
        }
    }

    public void getContactPermission() {
        int permissionCheck = ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_CONTACTS);
        if (permissionCheck == android.content.pm.PackageManager.PERMISSION_GRANTED) {
            getAccountPermission();
        } else {
            ActivityCompat.requestPermissions(CountrySpecific.this, new String[]{android.Manifest.permission.READ_CONTACTS}, 123);
        }
    }

    public void getAccountPermission() {
        int permissionCheck = ContextCompat.checkSelfPermission(this, android.Manifest.permission.GET_ACCOUNTS);
        if (permissionCheck == android.content.pm.PackageManager.PERMISSION_GRANTED) {
            getCameraPermission();
        } else {
            ActivityCompat.requestPermissions(CountrySpecific.this, new String[]{android.Manifest.permission.GET_ACCOUNTS}, 123);
        }
    }

    public void getCameraPermission() {
        int permissionCheck = ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA);
        if (permissionCheck == android.content.pm.PackageManager.PERMISSION_GRANTED) {
        } else {
            ActivityCompat.requestPermissions(CountrySpecific.this, new String[]{android.Manifest.permission.CAMERA}, 123);
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
}
