package com.ftl.tourisma.database;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by fipl11111 on 01-Mar-16.
 */
public class HoursOfOperation implements Serializable {
    String POH_Id, Place_Id, POH_Start_Day, POH_End_Day, POH_Start_Time, POH_End_Time, POH_Charges;
    ArrayList<FeesDetails> feesDetailses;

    public String getPOH_Id() {
        return POH_Id;
    }

    public void setPOH_Id(String POH_Id) {
        this.POH_Id = POH_Id;
    }

    public String getPlace_Id() {
        return Place_Id;
    }

    public void setPlace_Id(String place_Id) {
        Place_Id = place_Id;
    }

    public String getPOH_Start_Day() {
        return POH_Start_Day;
    }

    public void setPOH_Start_Day(String POH_Start_Day) {
        this.POH_Start_Day = POH_Start_Day;
    }

    public String getPOH_End_Day() {
        return POH_End_Day;
    }

    public void setPOH_End_Day(String POH_End_Day) {
        this.POH_End_Day = POH_End_Day;
    }

    public String getPOH_Start_Time() {
        return POH_Start_Time;
    }

    public void setPOH_Start_Time(String POH_Start_Time) {
        this.POH_Start_Time = POH_Start_Time;
    }

    public String getPOH_End_Time() {
        return POH_End_Time;
    }

    public void setPOH_End_Time(String POH_End_Time) {
        this.POH_End_Time = POH_End_Time;
    }

    public String getPOH_Charges() {
        return POH_Charges;
    }

    public void setPOH_Charges(String POH_Charges) {
        this.POH_Charges = POH_Charges;
    }

    public ArrayList<FeesDetails> getFeesDetailses() {
        return feesDetailses;
    }

    public void setFeesDetailses(ArrayList<FeesDetails> feesDetailses) {
        this.feesDetailses = feesDetailses;
    }
}
