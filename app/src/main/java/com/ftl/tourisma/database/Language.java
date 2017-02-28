package com.ftl.tourisma.database;

import java.io.Serializable;

/**
 * Created by fipl11111 on 29-Feb-16.
 */
public class Language implements Serializable {

    public String Lan_ID, Lan_name, Lan_Contents, Lan_Status, Msg_ID, Msg_Constant, Msg_Statement, Msg_Status;

    public String getMsg_ID() {
        return Msg_ID;
    }

    public void setMsg_ID(String msg_ID) {
        Msg_ID = msg_ID;
    }

    public String getMsg_Constant() {
        return Msg_Constant;
    }

    public void setMsg_Constant(String msg_Constant) {
        Msg_Constant = msg_Constant;
    }

    public String getMsg_Statement() {
        return Msg_Statement;
    }

    public void setMsg_Statement(String msg_Statement) {
        Msg_Statement = msg_Statement;
    }

    public String getMsg_Status() {
        return Msg_Status;
    }

    public void setMsg_Status(String msg_Status) {
        Msg_Status = msg_Status;
    }

    public String getLan_ID() {
        return Lan_ID;
    }

    public void setLan_ID(String lan_ID) {
        Lan_ID = lan_ID;
    }

    public String getLan_name() {
        return Lan_name;
    }

    public void setLan_name(String lan_name) {
        Lan_name = lan_name;
    }

    public String getLan_Contents() {
        return Lan_Contents;
    }

    public void setLan_Contents(String lan_Contents) {
        Lan_Contents = lan_Contents;
    }

    public String getLan_Status() {
        return Lan_Status;
    }

    public void setLan_Status(String lan_Status) {
        Lan_Status = lan_Status;
    }
}
