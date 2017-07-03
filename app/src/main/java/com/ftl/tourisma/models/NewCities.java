package com.ftl.tourisma.models;

import java.io.Serializable;

/**
 * Created by VirtualDusk on 7/3/2017.
 */

public class NewCities implements Serializable {

    private String City_Id;
    private String City_Id_Name;
    private String City_Name;
    private String City_Title;
    private String City_Image;
    private String City_Description;

    public String getCity_Id() {
        return City_Id;
    }

    public void setCity_Id(String city_Id) {
        City_Id = city_Id;
    }

    public String getCity_Id_Name() {
        return City_Id_Name;
    }

    public void setCity_Id_Name(String city_Id_Name) {
        City_Id_Name = city_Id_Name;
    }

    public String getCity_Name() {
        return City_Name;
    }

    public void setCity_Name(String city_Name) {
        City_Name = city_Name;
    }

    public String getCity_Title() {
        return City_Title;
    }

    public void setCity_Title(String city_Title) {
        City_Title = city_Title;
    }

    public String getCity_Image() {
        return City_Image;
    }

    public void setCity_Image(String city_Image) {
        City_Image = city_Image;
    }

    public String getCity_Description() {
        return City_Description;
    }

    public void setCity_Description(String city_Description) {
        City_Description = city_Description;
    }
}
