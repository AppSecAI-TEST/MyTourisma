package com.ftl.tourisma;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.ftl.tourisma.custom_views.NormalTextView;
import com.ftl.tourisma.utils.Constants;
import com.ftl.tourisma.utils.Preference;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Set;

import static com.ftl.tourisma.beacons.MyBeaconsService.BEACON_MESSAGE;
import static com.ftl.tourisma.beacons.MyBeaconsService.PLACE_ID;
import static com.ftl.tourisma.beacons.MyBeaconsService.PLACE_IMAGE;

/**
 * Created by C162 on 05/11/16.
 */

public class BeaconsActivity extends Activity {
    private String placeId, placeImage, message;
    private SharedPreferences mPreferences;
    private SharedPreferences.Editor mEditor;
    private int width, height;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        setContentView(R.layout.activity_beacons);
        final Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            placeId = bundle.getString(PLACE_ID);
            placeImage = bundle.getString(PLACE_IMAGE);
            message = bundle.getString(BEACON_MESSAGE);
        }
        try {
            DisplayMetrics displaymetrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
            height = displaymetrics.heightPixels;
            width = displaymetrics.widthPixels;
        } catch (Exception e) {
            // Tracking exception
            MyTorismaApplication.getInstance().trackException(e);
            e.printStackTrace();
        }

        mPreferences = getSharedPreferences(Constants.mPref, 0);
        mEditor = mPreferences.edit();
        ImageView img_close = (ImageView) findViewById(R.id.img_close);
        img_close.setImageResource(R.drawable.ic_left_arrow);
        img_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        ImageView imgPlace = (ImageView) findViewById(R.id.imgPlace);
        imgPlace.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (height * 60) / 100));
        NormalTextView txtMessage = (NormalTextView) findViewById(R.id.txtMessage);
        NormalTextView txtTitle = (NormalTextView) findViewById(R.id.txtTitle);
        txtTitle.setText(Constants.showMessage(this, mPreferences.getString("Lan_Id", ""), "Beacons Header"));
        NormalTextView txtViewDetails = (NormalTextView) findViewById(R.id.txtViewDetails);
        txtMessage.setText(message);
        txtViewDetails.setText(Constants.showMessage(this, mPreferences.getString("Lan_Id", ""), "Beacons Detail Button"));
        String imageUrl = Constants.BEACON_IMAGE_URL + placeImage + "&w=" + (width);
        Picasso.with(this).load(imageUrl).resize(width, (height * 60) / 100).into(imgPlace, new Callback() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onError() {

            }
        });
        txtViewDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BeaconsActivity.this, SearchResultPlaceDetailsActivity.class);
                Bundle bundle1 = new Bundle();
                bundle1.putString(PLACE_ID, placeId);
                bundle1.putString("from", "beacon");
                intent.putExtra("beaconView", bundle1);
                intent.putExtra("placeId", placeId);
                startActivity(intent);
            }
        });

    }

    public String getBeaconMessage() {
        Set<String> stringSet = Preference.getStringSetPrefs("keyBeacons", getApplicationContext());
        if (stringSet != null) {
            for (String str : stringSet) {
                try {
                    JSONObject jsonObject = new JSONObject(str);
                    if (placeId.equals(jsonObject.getString("place_id")))
                        return jsonObject.getString("message");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        return "";
    }
}
