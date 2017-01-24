package com.oldster.swiftmove.app.dao;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Old'ster on 5/12/2559.
 */

public class CommentDataCollectionDao {
    @SerializedName("success")
    private    boolean success;
    @SerializedName("data")
    private    List<CommentDataDao> data;

    public CommentDataCollectionDao() {
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public List<CommentDataDao> getData() {
        return data;
    }

    public void setData(List<CommentDataDao> data) {
        this.data = data;
    }
}
