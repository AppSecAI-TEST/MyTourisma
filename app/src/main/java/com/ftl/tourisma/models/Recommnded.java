package com.ftl.tourisma.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import org.json.*;

import java.util.ArrayList;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Recommnded {

    private String placeLatitude;
    private String lanId;
    private String placeMainImage;
    private ArrayList<HoursOfOperation> hoursOfOperation;
    private String vrimages;
    private String placeDescription;
    private String placeCloseNote;
    private String placeId;
    private String placeName;
    private String otherimages;
    private String placeVRMainImage;
    private String placeShortInfo;
    private String dist;
    private String categoryName;
    private String placeAddress;
    private String favId;
    private String placeRecommended;
    private String categoryMapIcon;
    private String categoryId;
    private String placeLongi;


    @JsonProperty("Place_Latitude")
    public String getPlaceLatitude() {
        return this.placeLatitude;
    }

    public void setPlaceLatitude(String placeLatitude) {
        this.placeLatitude = placeLatitude;
    }

    @JsonProperty("Lan_Id")
    public String getLanId() {
        return this.lanId;
    }

    public void setLanId(String lanId) {
        this.lanId = lanId;
    }

    @JsonProperty("Place_MainImage")
    public String getPlaceMainImage() {
        return this.placeMainImage;
    }

    public void setPlaceMainImage(String placeMainImage) {
        this.placeMainImage = placeMainImage;
    }

    @JsonProperty("HoursOfOperation")
    public ArrayList<HoursOfOperation> getHoursOfOperation() {
        return this.hoursOfOperation;
    }

    public void setHoursOfOperation(ArrayList<HoursOfOperation> hoursOfOperation) {
        this.hoursOfOperation = hoursOfOperation;
    }

    @JsonProperty("vrimages")
    public String getVrimages() {
        return this.vrimages;
    }

    public void setVrimages(String vrimages) {
        this.vrimages = vrimages;
    }

    @JsonProperty("Place_Description")
    public String getPlaceDescription() {
        return this.placeDescription;
    }

    public void setPlaceDescription(String placeDescription) {
        this.placeDescription = placeDescription;
    }

    @JsonProperty("Place_Close_Note")
    public String getPlaceCloseNote() {
        return this.placeCloseNote;
    }

    public void setPlaceCloseNote(String placeCloseNote) {
        this.placeCloseNote = placeCloseNote;
    }

    @JsonProperty("Place_Id")
    public String getPlaceId() {
        return this.placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

    @JsonProperty("Place_Name")
    public String getPlaceName() {
        return this.placeName;
    }

    public void setPlaceName(String placeName) {
        this.placeName = placeName;
    }

    @JsonProperty("otherimages")
    public String getOtherimages() {
        return this.otherimages;
    }

    public void setOtherimages(String otherimages) {
        this.otherimages = otherimages;
    }

    @JsonProperty("Place_VRMainImage")
    public String getPlaceVRMainImage() {
        return this.placeVRMainImage;
    }

    public void setPlaceVRMainImage(String placeVRMainImage) {
        this.placeVRMainImage = placeVRMainImage;
    }

    @JsonProperty("Place_ShortInfo")
    public String getPlaceShortInfo() {
        return this.placeShortInfo;
    }

    public void setPlaceShortInfo(String placeShortInfo) {
        this.placeShortInfo = placeShortInfo;
    }

    @JsonProperty("dist")
    public String getDist() {
        return this.dist;
    }

    public void setDist(String dist) {
        this.dist = dist;
    }

    @JsonProperty("Category_Name")
    public String getCategoryName() {
        return this.categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    @JsonProperty("Place_Address")
    public String getPlaceAddress() {
        return this.placeAddress;
    }

    public void setPlaceAddress(String placeAddress) {
        this.placeAddress = placeAddress;
    }

    @JsonProperty("Fav_Id")
    public String getFavId() {
        return this.favId;
    }

    public void setFavId(String favId) {
        this.favId = favId;
    }

    @JsonProperty("Place_Recommended")
    public String getPlaceRecommended() {
        return this.placeRecommended;
    }

    public void setPlaceRecommended(String placeRecommended) {
        this.placeRecommended = placeRecommended;
    }

    @JsonProperty("Category_Map_Icon")
    public String getCategoryMapIcon() {
        return this.categoryMapIcon;
    }

    public void setCategoryMapIcon(String categoryMapIcon) {
        this.categoryMapIcon = categoryMapIcon;
    }

    @JsonProperty("Category_Id")
    public String getCategoryId() {
        return this.categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    @JsonProperty("Place_Longi")
    public String getPlaceLongi() {
        return this.placeLongi;
    }

    public void setPlaceLongi(String placeLongi) {
        this.placeLongi = placeLongi;
    }


}
