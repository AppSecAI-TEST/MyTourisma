package com.ftl.tourisma.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.estimote.sdk.repackaged.gson_v2_3_1.com.google.gson.Gson;
import com.ftl.tourisma.R;
import com.ftl.tourisma.adapters.AllReviewAdapter;
import com.ftl.tourisma.custom_views.NormalTextView;
import com.ftl.tourisma.database.Nearby;
import com.ftl.tourisma.database.ReviewData;
import com.ftl.tourisma.postsync.PostSync;
import com.ftl.tourisma.postsync.post_sync;
import com.ftl.tourisma.utils.CommonClass;
import com.ftl.tourisma.utils.Constants;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;


/**
 * Created by VirtualDusk on 24-Jul-17.
 */

public class AllRevieFragment extends Fragment implements View.OnClickListener, post_sync.ResponseHandler {
    final String url = "https://www.googleapis.com/language/translate/v2?key=AIzaSyAIKDHIzU5rIuuz0w6zROlUDDr3tHE59W8&target=en&q=";
    TextView submitid;
    String groupId;
    Integer integer1, integer2, integer3, integer4, integer5, integer6;
    Gson gson = new Gson();
    //    int int1, int2, int3, int4, int5;
    ImageView backidImagview;
    ProgressBar progressBar1, progressBar2, progressBar3, progressBar4, progressBar5;
    TextView number1, number2, number3, number4, number5, totalReview, totalRating, reviewtitelid;
    MainActivity mainActivity;
    ImageView staricon;
    NormalTextView reviewtitel;
    ImageView staricon1, staricon2, staricon3, staricon4, staricon5;
    private ArrayList<Nearby> reviewList = new ArrayList<>();
    private RecyclerView recyclerView;
    private AllReviewAdapter allReviewAdapter;
    private SharedPreferences mPreferences;
    private SharedPreferences.Editor mEditor;
    private ReviewData reviewData = new ReviewData();
    private ArrayList<ReviewData> reviewDatas = new ArrayList<>();

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mainActivity = (MainActivity) context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle mIntent = getArguments();
        groupId = mIntent.getString("groupId");
        integer1 = mIntent.getInt("integer1");
        integer2 = mIntent.getInt("integer2");
        integer3 = mIntent.getInt("integer3");
        integer4 = mIntent.getInt("integer4");
        integer5 = mIntent.getInt("integer5");


        // sum of rating/////
        integer6 = integer1 + integer2 + integer3 + integer4 + integer5;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.new_review_recyclerview, container, false);

        mPreferences = getActivity().getSharedPreferences(Constants.mPref, 0);
        mEditor = mPreferences.edit();

        backidImagview = (ImageView) view.findViewById(R.id.backbuttonid);
        backidImagview.setOnClickListener(this);

        //recyclerview starting code ./////
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_viewid);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        reviewtitel = (NormalTextView) view.findViewById(R.id.reviewtitel);
        reviewtitel.setText(Constants.showMessage(getActivity(), mPreferences.getString("Lan_Id", ""), "All_Reviews"));

        staricon = (ImageView) view.findViewById(R.id.staricon);
        // recycler view end coding/////
        number1 = (TextView) view.findViewById(R.id.number1);
        number2 = (TextView) view.findViewById(R.id.number2);
        number3 = (TextView) view.findViewById(R.id.number3);
        number4 = (TextView) view.findViewById(R.id.number4);
        number5 = (TextView) view.findViewById(R.id.number5);

        totalReview = (TextView) view.findViewById(R.id.reviewnoid);
        totalRating = (TextView) view.findViewById(R.id.numbertext);

        submitid = (TextView) view.findViewById(R.id.submitid);
        submitid.setOnClickListener(this);
        submitid.setText(Constants.showMessage(getActivity(), mPreferences.getString("Lan_Id", ""), "RATE_AND_WRITE_A_REVIEW"));
        reviewtitelid = (TextView) view.findViewById(R.id.reviewtitelid);
        reviewtitelid.setText(Constants.showMessage(getActivity(), mPreferences.getString("Lan_Id", ""), "Based_on"));
        // rating progress bar code //
        progressBar1 = (ProgressBar) view.findViewById(R.id.progressBar1);
        progressBar2 = (ProgressBar) view.findViewById(R.id.progressBar2);
        progressBar3 = (ProgressBar) view.findViewById(R.id.progressBar3);
        progressBar4 = (ProgressBar) view.findViewById(R.id.progressBar4);
        progressBar5 = (ProgressBar) view.findViewById(R.id.progressBar5);


        progressBar1.setMax(1);
        progressBar2.setMax(1);
        progressBar3.setMax(1);
        progressBar4.setMax(1);
        progressBar5.setMax(1);
        //star icon
        staricon1 = (ImageView) view.findViewById(R.id.staricon1);
        staricon2 = (ImageView) view.findViewById(R.id.staricon2);
        staricon3 = (ImageView) view.findViewById(R.id.staricon3);
        staricon4 = (ImageView) view.findViewById(R.id.staricon4);
        staricon5 = (ImageView) view.findViewById(R.id.staricon5);
        staricon = (ImageView) view.findViewById(R.id.staricon);

//        //review & rating data//////
//        progressBar1.setProgress(integer1);
//        progressBar2.setProgress(integer2);
//        progressBar3.setProgress(integer3);
//        progressBar4.setProgress(integer4);
//        progressBar5.setProgress(integer5);

//        totalReview.setText(integer6.toString().trim()+ "  "+ "reviews");

        totalReview.setText(integer6.toString().trim() + "  " + Constants.showMessage(getActivity(), mPreferences.getString("Lan_Id", ""), "Reviews"));
        //total rating//
        number1.setText("(" + integer1.toString().trim() + ")");
        number2.setText("(" + integer2.toString().trim() + ")");
        number3.setText("(" + integer3.toString().trim() + ")");
        number4.setText("(" + integer4.toString().trim() + ")");
        number5.setText("(" + integer5.toString().trim() + ")");


        if (integer1 != 0) {

            progressBar1.setProgress(integer1 / integer6);

            staricon1.setImageResource(R.drawable.bar_star_yellow);

        } else if (integer2 != 0) {


            progressBar2.setProgress(integer2 / integer6);
            staricon2.setImageResource(R.drawable.bar_star_yellow);
        } else if (integer3 != 0) {

            progressBar3.setProgress(integer3 / integer6);
            staricon3.setImageResource(R.drawable.bar_star_yellow);
        } else if (integer4 != 0) {

            progressBar4.setProgress(integer4 / integer6);
            staricon4.setImageResource(R.drawable.bar_star_yellow);
        } else if (integer5 != 0) {

            progressBar5.setProgress(integer5 / integer6);
            staricon5.setImageResource(R.drawable.bar_star_yellow);
        } else {

            progressBar1.setProgress(integer1);
            progressBar2.setProgress(integer2);
            progressBar3.setProgress(integer3);
            progressBar4.setProgress(integer4);
            progressBar5.setProgress(integer5);


        }

        float flt = (float) ((integer1 * 5 + integer2 * 4 + integer3 * 3 + integer4 * 2 + integer5 * 1));

        System.out.print("" + flt);

        float avrage = flt / integer6;
        System.out.print("" + avrage);
        if (avrage != 0) {

            totalRating.setText(String.valueOf(avrage));

            staricon.setImageResource(R.drawable.select_star);
        }
        allReviewItem();

        return view;
    }


    @Override
    public void onClick(View v) {
        if (v == submitid) {
            Intent intent = new Intent(getActivity(), RatingSubmition.class);
            Bundle bundle = new Bundle();
            bundle.putString("groupId", groupId);
            intent.putExtras(bundle);
            startActivity(intent);
        } else if (v == backidImagview) {
            mainActivity.onBackPressed();
        }
    }


    private void allReviewItem() {
        if (CommonClass.hasInternetConnection(getActivity())) {
            String url = Constants.SERVER_URL + "json.php?action=GetReviewsByPlaceGroupId";
            String json = "[{\"Group_Id\":\"" + groupId + "\"}]";
            System.out.println("GetReviewsByPlaceGroupId_json " + json);
            new PostSync(getActivity(), "GetReviewsByPlaceGroupId", AllRevieFragment.this).execute(url, json);
        } else {
            Intent intent = new Intent(getActivity(), NoInternet.class);
            startActivity(intent);
        }
    }

    public void allReviewItemResponse(String resultString) {
        reviewDatas.clear();
        try {
            JSONArray review_jsonArray = new JSONArray(resultString);
            for (int i = 0; i < review_jsonArray.length(); i++) {
                reviewDatas.add(gson.fromJson(review_jsonArray.get(i).toString(), ReviewData.class));

                //setting adapter
                allReviewAdapter = new AllReviewAdapter(reviewDatas, getActivity());
                recyclerView.setAdapter(allReviewAdapter);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onResponse(String response, String action) {

        if (action.equalsIgnoreCase("GetReviewsByPlaceGroupId")) {
            allReviewItemResponse(response);
        }
    }


//    private void getResponse() {
//
//        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, url+reviewDatas+"&format=text", null,
//                new Response.Listener<JSONObject>()
//                {
//                    @Override
//                    public void onResponse(JSONObject response) {
//                        try {
//                            JSONObject data=response.getJSONObject("data");
//                            JSONArray translations=data.getJSONArray("translations");
//                            for(int i=0;i<translations.length();i++){
//                                JSONObject js=translations.getJSONObject(i);
//                                TranslatorData td=new TranslatorData();
//                                td.setTranslatedText( js.getString("translatedText"));
//                                js.getString("detectedSourceLanguage");
//                            }
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                },
//                new Response.ErrorListener()
//                {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        Log.d("Error.Response", error.getMessage());
//                    }
//                }
//        );
//        AppController.getInstance().addToRequestQueue(getRequest);
//
//    }

}
