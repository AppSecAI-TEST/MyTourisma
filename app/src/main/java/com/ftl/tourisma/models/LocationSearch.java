package com.ftl.tourisma.models;

/**
 * Created by Vinay on 5/10/2017.
 */

public class LocationSearch {

    private String fullAddress;
    private String cityName;

    public String getFullAddress() {
        return fullAddress;
    }

    public void setFullAddress(String fullAddress) {
        this.fullAddress = fullAddress;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }
}
