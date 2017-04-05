package com.ftl.tourisma.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.ftl.tourisma.R;
import com.ftl.tourisma.utils.Constants;

/**
 * Created by Vinay on 2/28/2017.
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
        mPreferences = getSharedPreferences(Constants.mPref, 0);
        mEditor = mPreferences.edit();

        //setting text in arabic and russian languages
        internet_fail_txt.setText(Constants.showMessage(this, mPreferences.getString("Lan_Id", ""), "nonetworktitle"));
        net_fail_txt.setText(Constants.showMessage(this, mPreferences.getString("Lan_Id", ""), "nonetworkmessage"));

        //registering receiver for broadcast receiver
        IntentFilter filter = new IntentFilter("closeNoInternetActivity");
        this.registerReceiver(_closeActivityReceiver, filter);
    }

    public void dcl_layout_variables() {
        no_internet = (ImageView) findViewById(R.id.no_internet);
        try_btn = (Button) findViewById(R.id.try_btn);
        internet_fail_txt = (TextView) findViewById(R.id.internet_fail_txt);
        net_fail_txt = (TextView) findViewById(R.id.net_fail_txt);
    }

    //unregistering receiver for broadcast receiver
    @Override
    public void onDestroy() {
        super.onDestroy();
        this.unregisterReceiver(this._closeActivityReceiver);
    }

    private class CloseActivityReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            // close this activity!
            finish();
        }
    }
}
