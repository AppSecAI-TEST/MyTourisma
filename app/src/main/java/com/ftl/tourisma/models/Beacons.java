package com.ftl.tourisma.models;

import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by C162 on 05/11/16.
 */

public class Beacons implements Serializable {
    private String uuid;
    private String major;
    private String beaconId;
    private String lanId;
    private String message;
    private String minor;
    private String exitText;
    private String entryText;
    private String placeId;
    private String imagePath;
    private String range;
    private String beaconName;
    private String nearbyText;

/*
    public beaconsss () {

    }

    public beaconsss (JSONObject json) {

        this.uuid = json.optString("uuid");
        this.major = json.optString("major");
        this.beaconId = json.optString("beacon_id");
        this.lanId = json.optString("lan_id");
        this.message = json.optString("message");
        this.minor = json.optString("minor");
        this.exitText = json.optString("exit_text");
        this.entryText = json.optString("entry_text");
        this.placeId = json.optString("place_id");
        this.imagePath = json.optString("image_path");
        this.range = json.optString("range");
        this.beaconName = json.optString("beacon_name");
        this.nearbyText = json.optString("nearby_text");

    }*/

    public String getUuid() {
        return this.uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getMajor() {
        return this.major;
    }

    public void setMajor(String major) {
        this.major = major;
    }

    public String getBeaconId() {
        return this.beaconId;
    }

    public void setBeaconId(String beaconId) {
        this.beaconId = beaconId;
    }

    public String getLanId() {
        return this.lanId;
    }

    public void setLanId(String lanId) {
        this.lanId = lanId;
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMinor() {
        return this.minor;
    }

    public void setMinor(String minor) {
        this.minor = minor;
    }

    public String getExitText() {
        return this.exitText;
    }

    public void setExitText(String exitText) {
        this.exitText = exitText;
    }

    public String getEntryText() {
        return this.entryText;
    }

    public void setEntryText(String entryText) {
        this.entryText = entryText;
    }

    public String getPlaceId() {
        return this.placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

    public String getImagePath() {
        return this.imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getRange() {
        return this.range;
    }

    public void setRange(String range) {
        this.range = range;
    }

    public String getBeaconName() {
        return this.beaconName;
    }

    public void setBeaconName(String beaconName) {
        this.beaconName = beaconName;
    }

    public String getNearbyText() {
        return this.nearbyText;
    }

    public void setNearbyText(String nearbyText) {
        this.nearbyText = nearbyText;
    }



}
