package com.oldster.swiftmove.app.dao;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Old'ster on 16/9/2559.
 */

public class DriverDataAfterSortCollection implements Parcelable{
    @SerializedName("success")    private boolean success;
    @SerializedName("driver")    private List<DriverDataAfterSort> driver;

    public DriverDataAfterSortCollection(){

    }

    protected DriverDataAfterSortCollection(Parcel in) {
        success = in.readByte() != 0;
        driver = in.createTypedArrayList(DriverDataAfterSort.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte((byte) (success ? 1 : 0));
        dest.writeTypedList(driver);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<DriverDataAfterSortCollection> CREATOR = new Creator<DriverDataAfterSortCollection>() {
        @Override
        public DriverDataAfterSortCollection createFromParcel(Parcel in) {
            return new DriverDataAfterSortCollection(in);
        }

        @Override
        public DriverDataAfterSortCollection[] newArray(int size) {
            return new DriverDataAfterSortCollection[size];
        }
    };

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public List<DriverDataAfterSort> getDriver() {
        return driver;
    }

    public void setDriver(List<DriverDataAfterSort> driver) {
        this.driver = driver;
    }
}
