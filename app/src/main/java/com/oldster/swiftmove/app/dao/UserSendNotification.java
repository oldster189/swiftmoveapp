package com.oldster.swiftmove.app.dao;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Old'ster on 27/8/2559.
 */

public class UserSendNotification {
    @SerializedName("success")
    private String success;
    @SerializedName("message")
    private String message;

    public UserSendNotification() {

    }

    public String getSuccess() {
        return success;
    }

    public void setSuccess(String success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
