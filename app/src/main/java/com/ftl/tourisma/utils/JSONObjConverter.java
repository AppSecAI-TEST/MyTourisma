package com.ftl.tourisma.utils;

import android.location.Location;

import com.ftl.tourisma.database.FeesDetails;
import com.ftl.tourisma.database.Nearby;
import com.ftl.tourisma.models.HourDetails;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by c162 on 23/11/16.
 */

public class JSONObjConverter {

    private static final String TAG = JSONObjConverter.class.getSimpleName();

    public HourDetails convertJsonToHoursDetailsObj(JSONObject jsonObject) {
        try {
            HourDetails hourDetails = new HourDetails();
            hourDetails.setPlaceId(jsonObject.getString("Place_Id"));
            hourDetails.setPOHIsOpen(jsonObject.getString("POH_Is_Open"));
            hourDetails.setPOHDay(jsonObject.getString("POH_Day"));
            hourDetails.setPOHKey(jsonObject.getString("POH_Key"));
            hourDetails.setPOHCharges(jsonObject.getString("POH_Charges"));
            hourDetails.setpOHBreakStart(jsonObject.getString("POH_Break_Start"));
            hourDetails.setpOHBreakEnd(jsonObject.getString("POH_Break_End"));
            hourDetails.setPOHStartTime(jsonObject.getString("POH_Start_Time"));
            hourDetails.setPOHEndTime(jsonObject.getString("POH_End_Time"));
            hourDetails.setPOHId(jsonObject.getString("POH_Id"));
            // hourDetails.setFeesDetails(getFeesObject(jsonObject));
            return hourDetails;
        } catch (JSONException e) {
            Utils.Log(TAG, "" + e.getLocalizedMessage());
        }
        return null;
    }


    public ArrayList<FeesDetails> getFeesObject(JSONObject jsonObject) {
        JSONArray feesDetails = null;
        ArrayList<FeesDetails> feesArrayList = new ArrayList<>();
        try {
            feesDetails = jsonObject.getJSONArray("Fees_Details");
            if (feesDetails != null) {

                for (int k = 0; k < feesDetails.length(); k++) {

                    JSONObject jobjFees = feesDetails.getJSONObject(k);
                    FeesDetails objFees = new FeesDetails();
                    objFees.setFeesName(jobjFees.getString("Fee_Name"));
                    objFees.setFeesValue(jobjFees.getString("Fee_Value"));
                    feesArrayList.add(objFees);
                }
            }
            // hoursOfOperation.setFeesDetailses(feesArrayList);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return feesArrayList;
    }

    public Nearby convertJsonToNearByObj(JSONObject jsonObject1) {
        try {
            Nearby recommended = new Nearby();
            recommended.setPlace_Id(jsonObject1.optString("Place_Id"));
            recommended.setCategory_Name(jsonObject1.optString("Category_Name"));
            recommended.setPlace_Name(jsonObject1.optString("Place_Name"));
            recommended.setPlace_ShortInfo(jsonObject1.optString("Place_ShortInfo"));
            recommended.setPlace_MainImage(jsonObject1.optString("Place_MainImage"));

//            recommended.setPlace_Description(jsonObject1.optString("Place_Description"));
            if (jsonObject1.optString("Place_Description") != null && !jsonObject1.optString("Place_Description").equalsIgnoreCase("")) {
                String price = jsonObject1.optString("Place_Description");
//                String a = price.replace("\\*", "");
               // String b = price.replaceAll("\r", "");
//                String c = b.replaceAll("\n", System.getProperty("line.separator"));
                recommended.setPlace_Description(price);
            } else {
                recommended.setPlace_Description(jsonObject1.optString("Place_Description"));

            }
            recommended.setPlace_Address(jsonObject1.optString("Place_Address"));
            recommended.setPlace_Latitude(jsonObject1.optString("Place_Latitude"));
            recommended.setPlace_Longi(jsonObject1.optString("Place_Longi"));
            recommended.setOtherimages(jsonObject1.optString("otherimages"));
            recommended.setPlaceVRMainImage(jsonObject1.optString("Place_VRMainImage"));
            recommended.setVrimages(jsonObject1.optString("vrimages"));

            if (jsonObject1.optString("Price_Description") != null && !jsonObject1.optString("Price_Description").equalsIgnoreCase("")) {
                String price = jsonObject1.optString("Price_Description");
                recommended.setPrice_Description(price);
            } else {
                recommended.setPrice_Description(jsonObject1.optString("Price_Description"));

            }
            recommended.setCategory_Map_Icon(jsonObject1.optString("Category_Map_Icon"));
            recommended.setCategory_Id(jsonObject1.optString("Category_Id"));
            recommended.setPlace_Recommended(jsonObject1.optString("Place_Recommended"));
            recommended.setPlace_Close_Note(jsonObject1.optString("Place_Close_Note"));

            recommended.setDist(jsonObject1.optString("dist"));
            recommended.setFav_Id(jsonObject1.optString("Fav_Id"));


            JSONArray operation1 = jsonObject1.getJSONArray("HourDetails");
            ArrayList<HourDetails> detailsArrayList = new ArrayList<>();
            for (int j = 0; j < operation1.length(); j++) {

//                JSONObjConverter jsonObjConverter = new JSONObjConverter();
//                jsonObjConverter.convertJsonToHoursDetailsObj(operation1.getJSONObject(j));
                detailsArrayList.add(convertJsonToHoursDetailsObj(operation1.getJSONObject(j)));
            }
            recommended.setHourDetailsArrayList(detailsArrayList);
            return recommended;
        } catch (JSONException e) {
            Utils.Log(TAG, "" + e.getLocalizedMessage());
        }
        return null;
    }
}
