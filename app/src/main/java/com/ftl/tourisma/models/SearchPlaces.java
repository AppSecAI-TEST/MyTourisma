package com.ftl.tourisma.models;

import org.json.*;


public class SearchPlaces {
	
    private String placeName;
    private String lanId;
    private String placeMainImage;
    private String placeId;

    public SearchPlaces(String placeName, String placeMainImage, String placeId) {
        this.placeName = placeName;
        this.placeMainImage = placeMainImage;
        this.placeId = placeId;
    }

    //    public myt (JSONObject json) {
//
//        this.placeName = json.optString("Place_Name");
//        this.lanId = json.optString("Lan_Id");
//        this.placeMainImage = json.optString("Place_MainImage");
//        this.placeId = json.optString("Place_Id");
//
//    }
    
    public String getPlaceName() {
        return this.placeName;
    }

    public void setPlaceName(String placeName) {
        this.placeName = placeName;
    }

    public String getLanId() {
        return this.lanId;
    }

    public void setLanId(String lanId) {
        this.lanId = lanId;
    }

    public String getPlaceMainImage() {
        return this.placeMainImage;
    }

    public void setPlaceMainImage(String placeMainImage) {
        this.placeMainImage = placeMainImage;
    }

    public String getPlaceId() {
        return this.placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }


    
}
