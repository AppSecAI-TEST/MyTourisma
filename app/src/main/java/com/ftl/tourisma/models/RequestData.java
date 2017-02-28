package com.ftl.tourisma.models;

/**
 * Created by C162 on 13/10/16.
 */

public class RequestData {
    private String User_Email;
    private String Lan_Id;

    public String getUser_Email() {
        return User_Email;
    }

    public void setUser_Email(String user_Email) {
        User_Email = user_Email;
    }

    public String getLan_Id() {
        return Lan_Id;
    }

    public void setLan_Id(String lan_Id) {
        Lan_Id = lan_Id;
    }
}
