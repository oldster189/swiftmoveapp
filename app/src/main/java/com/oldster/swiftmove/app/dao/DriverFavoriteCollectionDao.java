package com.oldster.swiftmove.app.dao;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Old'ster on 28/11/2559.
 */

public class DriverFavoriteCollectionDao {
    @SerializedName("success")
    private boolean success;
    @SerializedName("data")
    private List<DriverFavoriteDao> data;

    public DriverFavoriteCollectionDao() {

    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public List<DriverFavoriteDao> getData() {
        return data;
    }

    public void setData(List<DriverFavoriteDao> data) {
        this.data = data;
    }
}
