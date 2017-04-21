package com.ftl.tourisma.database;

import com.ftl.tourisma.models.HourDetails;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by fipl11111 on 01-Mar-16.
 */
public class Nearby implements Serializable {
    ArrayList<HourDetails> hourDetailsArrayList;
    ArrayList<HoursOfOperation> hoursOfOperations = new ArrayList<>();
    private String Place_Id;
    private String free_entry;
    private String Category_Name;
    private String Place_Name;
    private String Price_Description;
    private String Place_ShortInfo;
    private String Place_MainImage;
    private String Place_VRMainImage;
    private String Place_Description;
    private String Place_Address;
    private String Place_Latitude;
    private String Place_Longi;
    private String Place_Recommended;
    private String otherimages;
    private String vrimages;
    private String Category_Map_Icon;
    private String Category_Id;
    private String Lan_Id;
    private String Place_Close_Note;
    private String dist;
    private String Fav_Id;
    private String placeVRMainImage;
    private double distance;
    private String Group_Id;

    public String getPlace_Id() {
        return Place_Id;
    }

    public void setPlace_Id(String place_Id) {
        Place_Id = place_Id;
    }

    public String getFree_entry() {
        return free_entry;
    }

    public void setFree_entry(String free_entry) {
        this.free_entry = free_entry;
    }

    public String getCategory_Name() {
        return Category_Name;
    }

    public void setCategory_Name(String category_Name) {
        Category_Name = category_Name;
    }

    public String getPlace_Name() {
        return Place_Name;
    }

    public void setPlace_Name(String place_Name) {
        Place_Name = place_Name;
    }

    public String getPrice_Description() {
        return Price_Description;
    }

    public void setPrice_Description(String price_Description) {
        Price_Description = price_Description;
    }

    public String getPlace_ShortInfo() {
        return Place_ShortInfo;
    }

    public void setPlace_ShortInfo(String place_ShortInfo) {
        Place_ShortInfo = place_ShortInfo;
    }

    public String getPlace_MainImage() {
        return Place_MainImage;
    }

    public void setPlace_MainImage(String place_MainImage) {
        Place_MainImage = place_MainImage;
    }

    public String getPlace_VRMainImage() {
        return Place_VRMainImage;
    }

    public void setPlace_VRMainImage(String place_VRMainImage) {
        Place_VRMainImage = place_VRMainImage;
    }

    public String getPlace_Description() {
        return Place_Description;
    }

    public void setPlace_Description(String place_Description) {
        Place_Description = place_Description;
    }

    public String getPlace_Address() {
        return Place_Address;
    }

    public void setPlace_Address(String place_Address) {
        Place_Address = place_Address;
    }

    public String getPlace_Latitude() {
        return Place_Latitude;
    }

    public void setPlace_Latitude(String place_Latitude) {
        Place_Latitude = place_Latitude;
    }

    public String getPlace_Longi() {
        return Place_Longi;
    }

    public void setPlace_Longi(String place_Longi) {
        Place_Longi = place_Longi;
    }

    public String getPlace_Recommended() {
        return Place_Recommended;
    }

    public void setPlace_Recommended(String place_Recommended) {
        Place_Recommended = place_Recommended;
    }

    public String getOtherimages() {
        return otherimages;
    }

    public void setOtherimages(String otherimages) {
        this.otherimages = otherimages;
    }

    public String getVrimages() {
        return vrimages;
    }

    public void setVrimages(String vrimages) {
        this.vrimages = vrimages;
    }

    public String getCategory_Map_Icon() {
        return Category_Map_Icon;
    }

    public void setCategory_Map_Icon(String category_Map_Icon) {
        Category_Map_Icon = category_Map_Icon;
    }

    public String getCategory_Id() {
        return Category_Id;
    }

    public void setCategory_Id(String category_Id) {
        Category_Id = category_Id;
    }

    public String getLan_Id() {
        return Lan_Id;
    }

    public void setLan_Id(String lan_Id) {
        Lan_Id = lan_Id;
    }

    public String getPlace_Close_Note() {
        return Place_Close_Note;
    }

    public void setPlace_Close_Note(String place_Close_Note) {
        Place_Close_Note = place_Close_Note;
    }

    public String getDist() {
        return dist;
    }

    public void setDist(String dist) {
        this.dist = dist;
    }

    public String getFav_Id() {
        return Fav_Id;
    }

    public void setFav_Id(String fav_Id) {
        Fav_Id = fav_Id;
    }

    public String getPlaceVRMainImage() {
        return placeVRMainImage;
    }

    public void setPlaceVRMainImage(String placeVRMainImage) {
        this.placeVRMainImage = placeVRMainImage;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public String getGroup_Id() {
        return Group_Id;
    }

    public void setGroup_Id(String group_Id) {
        Group_Id = group_Id;
    }

    public ArrayList<HourDetails> getHourDetailsArrayList() {
        return hourDetailsArrayList;
    }

    public void setHourDetailsArrayList(ArrayList<HourDetails> hourDetailsArrayList) {
        this.hourDetailsArrayList = hourDetailsArrayList;
    }

    public ArrayList<HoursOfOperation> getHoursOfOperations() {
        return hoursOfOperations;
    }

    public void setHoursOfOperations(ArrayList<HoursOfOperation> hoursOfOperations) {
        this.hoursOfOperations = hoursOfOperations;
    }
}
