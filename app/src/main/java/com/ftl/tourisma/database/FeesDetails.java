package com.ftl.tourisma.database;

import java.io.Serializable;

/**
 * Created by C162 on 07/11/16.
 */

public class FeesDetails implements Serializable {
    private String FeesName;
    private String FeesValue;

    public String getFeesName() {
        return FeesName;
    }

    public void setFeesName(String feesName) {
        FeesName = feesName;
    }

    public String getFeesValue() {
        return FeesValue;
    }

    public void setFeesValue(String feesValue) {
        FeesValue = feesValue;
    }
}
