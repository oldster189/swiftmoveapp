package com.oldster.swiftmove.app.dao;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Old'ster on 5/12/2559.
 */

public class CommentDataDao {
    @SerializedName("user_id")
    private int user_id;
    @SerializedName("user_first_name")
    private String userFirstName;
    @SerializedName("user_last_name")
    private String userLastName;
    @SerializedName("user_img_name")
    private String userImgName;
    @SerializedName("driver_id")
    private int driver_id;
    @SerializedName("driver_first_name")
    private String driverFirstName;
    @SerializedName("driver_last_name")
    private String driverLastName;
    @SerializedName("driver_img_name")
    private String driverImgName;
    @SerializedName("job_comment")
    private String jobComment;
    @SerializedName("job_rating")
    private String jobRating;
    @SerializedName("job_created_at")
    private String jobCreatedAt;

    public CommentDataDao() {
    }

    public String getJobRating() {
        return jobRating;
    }

    public void setJobRating(String jobRating) {
        this.jobRating = jobRating;
    }

    public String getJobCreatedAt() {
        return jobCreatedAt;
    }

    public void setJobCreatedAt(String jobCreatedAt) {
        this.jobCreatedAt = jobCreatedAt;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public String getUserFirstName() {
        return userFirstName;
    }

    public void setUserFirstName(String userFirstName) {
        this.userFirstName = userFirstName;
    }

    public String getUserLastName() {
        return userLastName;
    }

    public void setUserLastName(String userLastName) {
        this.userLastName = userLastName;
    }

    public String getUserImgName() {
        return userImgName;
    }

    public void setUserImgName(String userImgName) {
        this.userImgName = userImgName;
    }

    public int getDriver_id() {
        return driver_id;
    }

    public void setDriver_id(int driver_id) {
        this.driver_id = driver_id;
    }

    public String getDriverFirstName() {
        return driverFirstName;
    }

    public void setDriverFirstName(String driverFirstName) {
        this.driverFirstName = driverFirstName;
    }

    public String getDriverLastName() {
        return driverLastName;
    }

    public void setDriverLastName(String driverLastName) {
        this.driverLastName = driverLastName;
    }

    public String getDriverImgName() {
        return driverImgName;
    }

    public void setDriverImgName(String driverImgName) {
        this.driverImgName = driverImgName;
    }

    public String getJobComment() {
        return jobComment;
    }

    public void setJobComment(String jobComment) {
        this.jobComment = jobComment;
    }


}
