package com.ftl.tourisma.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Toast;

import com.ftl.tourisma.R;
import com.ftl.tourisma.custom_views.NormalTextView;
import com.ftl.tourisma.postsync.PostSync;
import com.ftl.tourisma.postsync.post_sync;
import com.ftl.tourisma.utils.CommonClass;
import com.ftl.tourisma.utils.Constants;


/**
 * Created by VirtualDusk on 24-Jul-17.
 */

public class RatingSubmition extends AppCompatActivity implements View.OnClickListener, post_sync.ResponseHandler, RatingBar.OnRatingBarChangeListener {

    static float txtRatingValue;
    ImageView backbutton;
    RatingBar ratingBar;
    EditText reviewHeding, reviewDesc;
    NormalTextView saveButton, ratingtextid, reviewtextid, reviewtitel;
    String groupId;
    private SharedPreferences mPreferences;
    private SharedPreferences.Editor mEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_review_layout);

        Bundle bundle = getIntent().getExtras();
        groupId = bundle.getString("groupId");


        mPreferences = getSharedPreferences(Constants.mPref, 0);
        mEditor = mPreferences.edit();
        initView();
    }

    private void initView() {
        reviewtitel = (NormalTextView) findViewById(R.id.reviewtitel);
        reviewtitel.setText(Constants.showMessage(this, mPreferences.getString("Lan_Id", ""), "Write_your_review"));
        backbutton = (ImageView) findViewById(R.id.backid);
        ratingBar = (RatingBar) findViewById(R.id.ratingid);
        reviewHeding = (EditText) findViewById(R.id.reviewinputid);
        reviewHeding.setText(Constants.showMessage(this, mPreferences.getString("Lan_Id", ""), "Review_heading"));
        reviewDesc = (EditText) findViewById(R.id.writeid);
        reviewDesc.setText(Constants.showMessage(this, mPreferences.getString("Lan_Id", ""), "Write_your_review_here"));
        saveButton = (NormalTextView) findViewById(R.id.submitid);
        saveButton.setText(Constants.showMessage(this, mPreferences.getString("Lan_Id", ""), "SUBMIT"));
        saveButton.setOnClickListener(this);
        backbutton.setOnClickListener(this);
        ratingtextid = (NormalTextView) findViewById(R.id.ratingtextid);
        ratingtextid.setText(Constants.showMessage(this, mPreferences.getString("Lan_Id", ""), "Your_Rating"));
        reviewtextid = (NormalTextView) findViewById(R.id.reviewtextid);
        reviewtextid.setText(Constants.showMessage(this, mPreferences.getString("Lan_Id", ""), "Your_Review"));
        LayerDrawable stars = (LayerDrawable) ratingBar
                .getProgressDrawable();
        stars.getDrawable(2).setColorFilter(Color.parseColor("#F8931F"),
                PorterDuff.Mode.SRC_ATOP); // for filled stars
        stars.getDrawable(1).setColorFilter(Color.parseColor("#F8931F"),
                PorterDuff.Mode.SRC_ATOP); // for half filled stars
        stars.getDrawable(0).setColorFilter(Color.parseColor("#cccccc"),
                PorterDuff.Mode.SRC_ATOP); // for empty stars


    }


    @Override
    public void onClick(View v) {

        if (v == saveButton) {

            txtRatingValue = (ratingBar.getRating());
            if (txtRatingValue < 1) {
                Toast.makeText(RatingSubmition.this, "Please add rating", Toast.LENGTH_LONG).show();
            } else if (reviewHeding.getText().toString().length() >= 100) {
                Toast.makeText(RatingSubmition.this, "Maximum characters is 100 ", Toast.LENGTH_SHORT).show();
            } else if (reviewDesc.getText().toString().length() >= 500) {

                Toast.makeText(RatingSubmition.this, "Maximum characters is 500 ", Toast.LENGTH_SHORT).show();
            } else {
                addReviewRating();
            }

        } else if (v == backbutton) {


            finish();
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onResponse(String response, String action) {
        if (action.equalsIgnoreCase("AddReview")) {
            addReviewRatingResponse(response);
        }
    }

    @Override
    public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
        txtRatingValue = rating;
    }

    private void addReviewRating() {
        if (CommonClass.hasInternetConnection(this)) {
            String url = Constants.SERVER_URL + "json.php?action=AddReview";
            String json = "[{\"Group_Id\":\"" + groupId + "\",\"User_Id\":\"" + mPreferences.getString("User_Id", "") + "\",\"Rev_Rating\":\"" + txtRatingValue + "\",\"Rev_Title\":\"" + reviewHeding.getText().toString() + "\",\"Rev_Desc\":\"" + reviewDesc.getText().toString() + "\"}]";
            System.out.println("AddReview_json " + json);
            new PostSync(this, "AddReview", RatingSubmition.this).execute(url, json);
        } else {
            Intent intent = new Intent(this, NoInternet.class);
            startActivity(intent);
        }
    }

    public void addReviewRatingResponse(String resultString) {


    }
}
