package com.ftl.tourisma.models;

/**
 * Created by VirtualDusk on 7/31/2017.
 */

public class Tags {

    private String Tag_Id;
    private String Tag_Group_Id;
    private String Tag_Name;
    private String Tag_Image;
    private String Tag_Places_Count;

    public String getTag_Id() {
        return Tag_Id;
    }

    public void setTag_Id(String tag_Id) {
        Tag_Id = tag_Id;
    }

    public String getTag_Group_Id() {
        return Tag_Group_Id;
    }

    public void setTag_Group_Id(String tag_Group_Id) {
        Tag_Group_Id = tag_Group_Id;
    }

    public String getTag_Name() {
        return Tag_Name;
    }

    public void setTag_Name(String tag_Name) {
        Tag_Name = tag_Name;
    }

    public String getTag_Image() {
        return Tag_Image;
    }

    public void setTag_Image(String tag_Image) {
        Tag_Image = tag_Image;
    }

    public String getTag_Places_Count() {
        return Tag_Places_Count;
    }

    public void setTag_Places_Count(String tag_Places_Count) {
        Tag_Places_Count = tag_Places_Count;
    }
}
