package com.ftl.tourisma.database;

import java.io.Serializable;

/**
 * Created by fipl11111 on 01-Mar-16.
 */
public class AllCategories implements Serializable {
    String Category_Id, Category_Name, Category_Map_Icon, Lan_Id, Category_Info, Category_Status;

    public String getCategory_Id() {
        return Category_Id;
    }

    public void setCategory_Id(String category_Id) {
        Category_Id = category_Id;
    }

    public String getCategory_Name() {
        return Category_Name;
    }

    public void setCategory_Name(String category_Name) {
        Category_Name = category_Name;
    }

    public String getCategory_Map_Icon() {
        return Category_Map_Icon;
    }

    public void setCategory_Map_Icon(String category_Map_Icon) {
        Category_Map_Icon = category_Map_Icon;
    }

    public String getLan_Id() {
        return Lan_Id;
    }

    public void setLan_Id(String lan_Id) {
        Lan_Id = lan_Id;
    }

    public String getCategory_Info() {
        return Category_Info;
    }

    public void setCategory_Info(String category_Info) {
        Category_Info = category_Info;
    }

    public String getCategory_Status() {
        return Category_Status;
    }

    public void setCategory_Status(String category_Status) {
        Category_Status = category_Status;
    }
}
