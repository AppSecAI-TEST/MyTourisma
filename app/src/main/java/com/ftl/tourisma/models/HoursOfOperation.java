package com.ftl.tourisma.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import org.json.*;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class HoursOfOperation {

    private String pOHId;
    private String placeId;
    private String pOHEndDay;
    private String pOHEndTime;
    private String pOHStartDay;
    private String pOHStartTime;
    private String pOHCharges;


    @JsonProperty("POH_Id")
    public String getPOHId() {
        return this.pOHId;
    }

    public void setPOHId(String pOHId) {
        this.pOHId = pOHId;
    }

    @JsonProperty("Place_Id")
    public String getPlaceId() {
        return this.placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

    @JsonProperty("POH_End_Day")
    public String getPOHEndDay() {
        return this.pOHEndDay;
    }

    public void setPOHEndDay(String pOHEndDay) {
        this.pOHEndDay = pOHEndDay;
    }

    @JsonProperty("POH_End_Time")
    public String getPOHEndTime() {
        return this.pOHEndTime;
    }

    public void setPOHEndTime(String pOHEndTime) {
        this.pOHEndTime = pOHEndTime;
    }

    @JsonProperty("POH_Start_Day")
    public String getPOHStartDay() {
        return this.pOHStartDay;
    }

    public void setPOHStartDay(String pOHStartDay) {
        this.pOHStartDay = pOHStartDay;
    }

    @JsonProperty("POH_Start_Time")
    public String getPOHStartTime() {
        return this.pOHStartTime;
    }

    public void setPOHStartTime(String pOHStartTime) {
        this.pOHStartTime = pOHStartTime;
    }

    @JsonProperty("POH_Charges")
    public String getPOHCharges() {
        return this.pOHCharges;
    }

    public void setPOHCharges(String pOHCharges) {
        this.pOHCharges = pOHCharges;
    }


}
