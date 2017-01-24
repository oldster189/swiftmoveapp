package com.oldster.swiftmove.app.dao;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Old'ster on 28/11/2559.
 */

public class DriverFavoriteDao {
    @SerializedName("driver_first_name")
    @Expose
    private String driverFirstName;
    @SerializedName("driver_last_name")
    @Expose
    private String driverLastName;
    @SerializedName("driver_img_name")
    @Expose
    private String driverImgName;
    @SerializedName("driver_id")
    @Expose
    private int driverId;
    @SerializedName("user_id")
    @Expose
    private int userId;
    @SerializedName("driver_detail_type")
    @Expose
    private String driverDetailType;
    @SerializedName("rating_avg")
    @Expose
    private float ratingAvg;
    @SerializedName("rating_count")
    @Expose
    private int ratingCount;

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

    public int getDriverId() {
        return driverId;
    }

    public void setDriverId(int driverId) {
        this.driverId = driverId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getDriverDetailType() {
        return driverDetailType;
    }

    public void setDriverDetailType(String driverDetailType) {
        this.driverDetailType = driverDetailType;
    }

    public float getRatingAvg() {
        return ratingAvg;
    }

    public void setRatingAvg(float ratingAvg) {
        this.ratingAvg = ratingAvg;
    }

    public int getRatingCount() {
        return ratingCount;
    }

    public void setRatingCount(int ratingCount) {
        this.ratingCount = ratingCount;
    }
}
