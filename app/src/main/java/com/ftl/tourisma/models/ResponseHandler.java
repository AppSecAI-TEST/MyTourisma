package com.ftl.tourisma.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import org.json.*;
import java.util.ArrayList;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ResponseHandler {
	
    private ArrayList<Nearby> recommnded;
    private ArrayList<Nearby> nearby;

    @JsonProperty("recommnded")
    public ArrayList<Nearby> getRecommnded() {
        return this.recommnded;
    }

    public void setRecommnded(ArrayList<Nearby> recommnded) {
        this.recommnded = recommnded;
    }

    @JsonProperty("nearby")
    public ArrayList<Nearby> getNearby() {
        return this.nearby;
    }

    public void setNearby(ArrayList<Nearby> nearby) {
        this.nearby = nearby;
    }


    
}
