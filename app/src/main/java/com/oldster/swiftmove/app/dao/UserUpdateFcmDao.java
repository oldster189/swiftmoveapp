package com.oldster.swiftmove.app.dao;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Old'ster on 24/8/2559.
 */

public class UserUpdateFcmDao {
    @SerializedName("success") private boolean success;
    @SerializedName("message") private String message;

    public UserUpdateFcmDao() {

    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
