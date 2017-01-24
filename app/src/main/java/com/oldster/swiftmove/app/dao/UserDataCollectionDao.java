package com.oldster.swiftmove.app.dao;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Old'ster on 11/8/2559.
 */
public class UserDataCollectionDao {
    @SerializedName("success")
    private boolean success;
    @SerializedName("message")
    private String message;
    @SerializedName("user")
    private List<UserDataDao> user;

    public UserDataCollectionDao() {

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

    public List<UserDataDao> getUser() {
        return user;
    }

    public void setUser(List<UserDataDao> user) {
        this.user = user;
    }
}
