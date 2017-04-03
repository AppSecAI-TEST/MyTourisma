package com.ftl.tourisma.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.ftl.tourisma.R;
import com.ftl.tourisma.utils.CommonClass;
import com.ftl.tourisma.utils.Constants;

/**
 * Created by VirtualDusk on 2/28/2017.
 */

public class NoInternet extends Activity {

    private static SharedPreferences mPreferences;
    ImageView no_internet;
    Button try_btn;
    TextView internet_fail_txt, net_fail_txt;
    private SharedPreferences.Editor mEditor;
    private BroadcastReceiver _closeActivityReceiver = new CloseActivityReceiver();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.no_internet);
        dcl_layout_variables();
        onClickListners();
        mPreferences = getSharedPreferences(Constants.mPref, 0);
        mEditor = mPreferences.edit();

        //setting text in arabic and russian languages
        internet_fail_txt.setText(Constants.showMessage(this, mPreferences.getString("Lan_Id", ""), "nonetworktitle"));
        net_fail_txt.setText(Constants.showMessage(this, mPreferences.getString("Lan_Id", ""), "nonetworkmessage"));
        /*if (Constants.language.equals("arabic")) {
            try_btn.setText("حاول ثانية");
            internet_fail_txt.setText("للأسف !");
            net_fail_txt.setText("جهازك غير متصل بشبكة الإنترنت، الرجاء مراجعة الإعدادات و المحاولة مرة أخرى");
        } else if (Constants.language.equals("russian")) {
            try_btn.setText("ПОПРОБУЙТЕ СНОВА");
            internet_fail_txt.setText(" Ой!!");
            net_fail_txt.setText("Нет соединения с Интернетом. Пожалуйста проверьте свои настройки и попробуйте снова.");
        }*/

        //registering receiver for broadcast receiver
        IntentFilter filter = new IntentFilter("closeNoInternetActivity");
        this.registerReceiver(_closeActivityReceiver, filter);
    }

    //unregistering receiver for broadcast receiver
    @Override
    public void onDestroy() {
        super.onDestroy();
        this.unregisterReceiver(this._closeActivityReceiver);
    }

    public void dcl_layout_variables() {
        no_internet = (ImageView) findViewById(R.id.no_internet);
        try_btn = (Button) findViewById(R.id.try_btn);
        internet_fail_txt = (TextView) findViewById(R.id.internet_fail_txt);
        net_fail_txt = (TextView) findViewById(R.id.net_fail_txt);
    }

    public void onClickListners() {
        try_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CommonClass.hasInternetConnection(getApplicationContext())) {
                    finish();
                } else {

                }
            }
        });
    }

    private class CloseActivityReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            // close this activity!
            finish();
        }
    }
}
