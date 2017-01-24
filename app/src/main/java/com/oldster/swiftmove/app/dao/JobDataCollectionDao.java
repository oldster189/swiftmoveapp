package com.oldster.swiftmove.app.dao;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Old'ster on 29/11/2559.
 */

public class JobDataCollectionDao implements Parcelable {
    @SerializedName("success") private boolean success;
    @SerializedName("data")    private List<JobDataDao> data;

    public JobDataCollectionDao(){

    }

    protected JobDataCollectionDao(Parcel in) {
        success = in.readByte() != 0;
        data = in.createTypedArrayList(JobDataDao.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte((byte) (success ? 1 : 0));
        dest.writeTypedList(data);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<JobDataCollectionDao> CREATOR = new Creator<JobDataCollectionDao>() {
        @Override
        public JobDataCollectionDao createFromParcel(Parcel in) {
            return new JobDataCollectionDao(in);
        }

        @Override
        public JobDataCollectionDao[] newArray(int size) {
            return new JobDataCollectionDao[size];
        }
    };

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public List<JobDataDao> getData() {
        return data;
    }

    public void setData(List<JobDataDao> data) {
        this.data = data;
    }
}
