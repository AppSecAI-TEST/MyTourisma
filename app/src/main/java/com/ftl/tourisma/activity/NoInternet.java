package com.ftl.tourisma.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.ftl.tourisma.R;
import com.ftl.tourisma.SplashFragmentActivity;
import com.ftl.tourisma.utils.CommonClass;
import com.ftl.tourisma.utils.Constants;
import com.ftl.tourisma.utils.Utils;
import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.SnackbarManager;

/**
 * Created by Vinay on 2/28/2017.
 */

public class NoInternet extends Activity {

    private static SharedPreferences mPreferences;
    ImageView no_internet;
    Button try_btn;
    TextView internet_fail_txt, net_fail_txt;
    private SharedPreferences.Editor mEditor;
//    private BroadcastReceiver _closeActivityReceiver = new CloseActivityReceiver();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.no_internet);
        mPreferences = getSharedPreferences(Constants.mPref, 0);
        mEditor = mPreferences.edit();
        dcl_layout_variables();
        onClickListners();

        internet_fail_txt.setText(Constants.showMessage(this, mPreferences.getString("Lan_Id", ""), "nonetworktitle"));
        net_fail_txt.setText(Constants.showMessage(this, mPreferences.getString("Lan_Id", ""), "nonetworkmessage"));
        try_btn.setText(Constants.showMessage(this, mPreferences.getString("Lan_Id", ""), "Try again"));

        /*//registering receiver for broadcast receiver
        IntentFilter filter = new IntentFilter("closeNoInternetActivity");
        this.registerReceiver(_closeActivityReceiver, filter);*/
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
                if (CommonClass.hasInternetConnection(NoInternet.this)) {
                    Intent intent = new Intent(NoInternet.this, SplashFragmentActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    SnackbarManager.show(Snackbar.with(NoInternet.this).color(Utils.getColor(NoInternet.this, R.color.mBlue)).text(Constants.showMessage(NoInternet.this, mPreferences.getString("Lan_Id", ""), "NOINTERNET")));
                }
            }
        });
    }

    /*//unregistering receiver for broadcast receiver
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
    }*/
}
