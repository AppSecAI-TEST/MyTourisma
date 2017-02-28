package com.ftl.tourisma.database;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by fipl11111 on 01-Mar-16.
 */
public class Recommended implements Serializable {
    String Place_Id, Category_Name, Place_Name, Place_ShortInfo, Place_MainImage, Place_Description, Place_Address, Place_Latitude, Place_Longi, Place_Recommended, otherimages, dist, Fav_Id;

    ArrayList<HoursOfOperation> hoursOfOperations = new ArrayList<>();

    public ArrayList<HoursOfOperation> getHoursOfOperations() {
        return hoursOfOperations;
    }

    public void setHoursOfOperations(ArrayList<HoursOfOperation> hoursOfOperations) {
        this.hoursOfOperations = hoursOfOperations;
    }

    public String getPlace_Id() {
        return Place_Id;
    }

    public void setPlace_Id(String place_Id) {
        Place_Id = place_Id;
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
}
