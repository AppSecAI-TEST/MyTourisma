package com.ftl.tourisma.models;

import com.ftl.tourisma.database.FeesDetails;

import java.io.Serializable;
import java.util.ArrayList;

public class HourDetails implements Serializable {
	
    private String pOHId;
    private String pOHDay;
    private String pOHIsOpen;
    private String pOHKey;
    private String pOHBreakEnd;
    private String pOHBreakStart;
    private String placeId;
    private String plpoaceId;
    private String pOHEndTime;
    private String pOHFamilyCharges;
    private String pOHGroupCharges;
    private ArrayList<FeesDetails> feesDetails;
    private String pOHStartTime;
    private String pOHCharges;
    
	public HourDetails () {
		
	}	

    public String getPOHId() {
        return this.pOHId;
    }

    public void setPOHId(String pOHId) {
        this.pOHId = pOHId;
    }

    public String getPOHDay() {
        return this.pOHDay;
    }

    public void setPOHDay(String pOHDay) {
        this.pOHDay = pOHDay;
    }

    public String getPOHIsOpen() {
        return this.pOHIsOpen;
    }

    public void setPOHIsOpen(String pOHIsOpen) {
        this.pOHIsOpen = pOHIsOpen;
    }

    public String getPOHKey() {
        return this.pOHKey;
    }

    public void setPOHKey(String pOHKey) {
        this.pOHKey = pOHKey;
    }

    public String getPlaceId() {
        return this.placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

    public String getPOHEndTime() {
        return this.pOHEndTime;
    }

    public void setPOHEndTime(String pOHEndTime) {
        this.pOHEndTime = pOHEndTime;
    }

    public String getPOHFamilyCharges() {
        return this.pOHFamilyCharges;
    }

    public void setPOHFamilyCharges(String pOHFamilyCharges) {
        this.pOHFamilyCharges = pOHFamilyCharges;
    }

    public String getPOHGroupCharges() {
        return this.pOHGroupCharges;
    }

    public void setPOHGroupCharges(String pOHGroupCharges) {
        this.pOHGroupCharges = pOHGroupCharges;
    }

    public ArrayList<FeesDetails> getFeesDetails() {
        return this.feesDetails;
    }

    public void setFeesDetails(ArrayList<FeesDetails> feesDetails) {
        this.feesDetails = feesDetails;
    }

    public String getPOHStartTime() {
        return this.pOHStartTime;
    }

    public void setPOHStartTime(String pOHStartTime) {
        this.pOHStartTime = pOHStartTime;
    }

    public String getPOHCharges() {
        return this.pOHCharges;
    }

    public void setPOHCharges(String pOHCharges) {
        this.pOHCharges = pOHCharges;
    }

    public String getpOHBreakEnd() {
        if(pOHBreakEnd==null || pOHBreakEnd.equalsIgnoreCase("null")|| pOHBreakEnd.equals("00:00:00")){
            return null;
        }
        return pOHBreakEnd;
    }

    public void setpOHBreakEnd(String pOHBreakEnd) {
        this.pOHBreakEnd = pOHBreakEnd;
    }

    public String getpOHBreakStart() {
        if(pOHBreakStart==null || pOHBreakStart.equalsIgnoreCase("null")|| pOHBreakStart.equals("00:00:00")){
           return null;
        }
        return pOHBreakStart;
    }

    public void setpOHBreakStart(String pOHBreakStart) {
        this.pOHBreakStart = pOHBreakStart;
    }
}
