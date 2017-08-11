package com.ftl.tourisma.database;

/**
 * Created by VirtualDusk on 26-Jul-17.
 */

public class ReviewData {


    private String User_Id;
    private String Group_Id;
    private String Rev_Rating;
    private String Rev_Title;
    private String Rev_Desc;
    private String Rev_Status;
    private String Rev_Date;
    private String User_Name;
    private String User_ProfilePic;

    private String Rev_Id;

    public String getUser_Id() {
        return User_Id;
    }

    public void setUser_Id(String user_Id) {
        User_Id = user_Id;
    }

    public String getRev_Id() {
        return Rev_Id;
    }

    public void setRev_Id(String rev_Id) {
        Rev_Id = rev_Id;
    }

    public String getUser_ProfilePic() {
        return User_ProfilePic;
    }

    public void setUser_ProfilePic(String user_ProfilePic) {
        User_ProfilePic = user_ProfilePic;
    }

    public String getUser_Name() {
        return User_Name;
    }

    public void setUser_Name(String user_Name) {
        User_Name = user_Name;
    }

    public String getRev_Date() {
        return Rev_Date;
    }

    public void setRev_Date(String rev_Date) {
        Rev_Date = rev_Date;
    }

    public String getRev_Status() {
        return Rev_Status;
    }

    public void setRev_Status(String rev_Status) {
        Rev_Status = rev_Status;
    }

    public String getRev_Desc() {
        return Rev_Desc;
    }

    public void setRev_Desc(String rev_Desc) {
        Rev_Desc = rev_Desc;
    }

    public String getRev_Title() {
        return Rev_Title;
    }

    public void setRev_Title(String rev_Title) {
        Rev_Title = rev_Title;
    }

    public String getRev_Rating() {
        return Rev_Rating;
    }

    public void setRev_Rating(String rev_Rating) {
        Rev_Rating = rev_Rating;
    }

    public String getGroup_Id() {
        return Group_Id;
    }

    public void setGroup_Id(String group_Id) {
        Group_Id = group_Id;
    }


}
