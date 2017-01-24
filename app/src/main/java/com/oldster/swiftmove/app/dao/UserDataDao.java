package com.oldster.swiftmove.app.dao;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Old'ster on 11/8/2559.
 */
public class UserDataDao {
    @SerializedName("user_id")    private int userId;
    @SerializedName("user_fname")    private String userFname;
    @SerializedName("user_lname")    private String userLname;
    @SerializedName("user_email")    private String userEmail;
    @SerializedName("user_tel")    private String userTel;
    @SerializedName("user_fcm_id") private String userFcmId;
    @SerializedName("user_img_name")    private String userImgName;
    @SerializedName("user_created_at")    private String userCreatedAt;
    @SerializedName("user_change_at") private String userChangeAt;


    public UserDataDao() {

    }

    public String getUserFcmId() {
        return userFcmId;
    }

    public void setUserFcmId(String userFcmId) {
        this.userFcmId = userFcmId;
    }

    public String getUserImgName() {
        return userImgName;
    }

    public void setUserImgName(String userImgName) {
        this.userImgName = userImgName;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUserFname() {
        return userFname;
    }

    public void setUserFname(String userFname) {
        this.userFname = userFname;
    }

    public String getUserLname() {
        return userLname;
    }

    public void setUserLname(String userLname) {
        this.userLname = userLname;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserTel() {
        return userTel;
    }

    public void setUserTel(String userTel) {
        this.userTel = userTel;
    }

    public String getUserCreatedAt() {
        return userCreatedAt;
    }

    public void setUserCreatedAt(String userCreatedAt) {
        this.userCreatedAt = userCreatedAt;
    }

    public String getUserChangeAt() {
        return userChangeAt;
    }

    public void setUserChangeAt(String userChangeAt) {
        this.userChangeAt = userChangeAt;
    }
}
