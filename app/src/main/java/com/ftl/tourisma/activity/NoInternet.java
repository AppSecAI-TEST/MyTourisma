package com.ftl.tourisma.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.ftl.tourisma.R;
import com.ftl.tourisma.utils.CommonClass;

/**
 * Created by VirtualDusk on 2/28/2017.
 */

public class NoInternet extends Activity {

    ImageView no_internet;
    Button try_btn;
    TextView internet_fail_txt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.no_internet);
        dcl_layout_variables();
        onClickListners();
    }

    public void dcl_layout_variables() {
        no_internet = (ImageView) findViewById(R.id.no_internet);
        try_btn = (Button) findViewById(R.id.try_btn);
        internet_fail_txt = (TextView) findViewById(R.id.internet_fail_txt);
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
}
