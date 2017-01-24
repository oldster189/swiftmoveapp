package com.oldster.swiftmove.app.dao;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Old'ster on 16/9/2559.
 */

public class DriverDataAfterSort implements Parcelable{
    @SerializedName("driver_id")
    private int driverId;
    @SerializedName("driver_fname")
    private String driverFname;
    @SerializedName("driver_lname")
    private String driverLname;
    @SerializedName("driver_email")
    private String driverEmail;
    @SerializedName("driver_tel")
    private String driverTel;
    @SerializedName("driver_fcm_id")
    private String driverFcmId;
    @SerializedName("driver_address")
    private String driverAddress;
    @SerializedName("driver_sex")
    private String driverSex;
    @SerializedName("driver_province")
    private String driverProvince;
    @SerializedName("driver_img_name")
    private String driverImgName;
    @SerializedName("driver_created_at")
    private String driverCreatedAt;
    @SerializedName("driver_change_at")
    private String driverChangeAt;
    @SerializedName("driver_detail_id")
    private String driverDetailId;
    @SerializedName("driver_detail_type")
    private String driverDetailType;
    @SerializedName("driver_detail_brand")
    private String driverDetailBrand;
    @SerializedName("driver_detail_model")
    private String driverDetailModel;
    @SerializedName("driver_detail_color")
    private String driverDetailColor;
    @SerializedName("driver_detail_license_plate")
    private String driverDetailLicensePlate;
    @SerializedName("driver_detail_province_license_plate")
    private String driverDetailProvinceLicensePlate;

    @SerializedName("driver_detail_service_lift_status")
    private String driverDetailServiceLiftStatus;

    @SerializedName("driver_detail_service_lift_price")
    private int driverDetailServiceLiftPrice;

    @SerializedName("driver_detail_service_lift_plus_status")
    private String driverDetailServiceLiftPlusStatus;

    @SerializedName("driver_detail_service_lift_plus_price")
    private int driverDetailServiceLiftPlusPrice;
    @SerializedName("driver_detail_service_cart_status")
    private String driverDetailServiceCartStatus;
    @SerializedName("driver_detail_service_cart_price")
    private int driverDetailServiceCartPrice;
    @SerializedName("driver_detail_charge_start_price")
    private int driverDetailChargeStartPrice;
    @SerializedName("driver_detail_charge_start_km")
    private int driverDetailChargeStartKm;
    @SerializedName("driver_detail_charge")
    private int driverDetailCharge;
    @SerializedName("driver_distance")
    private int driverDistance;
    @SerializedName("driver_rating_avg")
    private double driverRatingAvg;
    @SerializedName("driver_rating_count")
    private int driverRatingCount;


    public DriverDataAfterSort() {
    }


    protected DriverDataAfterSort(Parcel in) {
        driverId = in.readInt();
        driverFname = in.readString();
        driverLname = in.readString();
        driverEmail = in.readString();
        driverTel = in.readString();
        driverFcmId = in.readString();
        driverAddress = in.readString();
        driverSex = in.readString();
        driverProvince = in.readString();
        driverImgName = in.readString();
        driverCreatedAt = in.readString();
        driverChangeAt = in.readString();
        driverDetailId = in.readString();
        driverDetailType = in.readString();
        driverDetailBrand = in.readString();
        driverDetailModel = in.readString();
        driverDetailColor = in.readString();
        driverDetailLicensePlate = in.readString();
        driverDetailProvinceLicensePlate = in.readString();
        driverDetailServiceLiftStatus = in.readString();
        driverDetailServiceLiftPrice = in.readInt();
        driverDetailServiceLiftPlusStatus = in.readString();
        driverDetailServiceLiftPlusPrice = in.readInt();
        driverDetailServiceCartStatus = in.readString();
        driverDetailServiceCartPrice = in.readInt();
        driverDetailChargeStartPrice = in.readInt();
        driverDetailChargeStartKm = in.readInt();
        driverDetailCharge = in.readInt();
        driverDistance = in.readInt();
        driverRatingAvg = in.readDouble();
        driverRatingCount = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(driverId);
        dest.writeString(driverFname);
        dest.writeString(driverLname);
        dest.writeString(driverEmail);
        dest.writeString(driverTel);
        dest.writeString(driverFcmId);
        dest.writeString(driverAddress);
        dest.writeString(driverSex);
        dest.writeString(driverProvince);
        dest.writeString(driverImgName);
        dest.writeString(driverCreatedAt);
        dest.writeString(driverChangeAt);
        dest.writeString(driverDetailId);
        dest.writeString(driverDetailType);
        dest.writeString(driverDetailBrand);
        dest.writeString(driverDetailModel);
        dest.writeString(driverDetailColor);
        dest.writeString(driverDetailLicensePlate);
        dest.writeString(driverDetailProvinceLicensePlate);
        dest.writeString(driverDetailServiceLiftStatus);
        dest.writeInt(driverDetailServiceLiftPrice);
        dest.writeString(driverDetailServiceLiftPlusStatus);
        dest.writeInt(driverDetailServiceLiftPlusPrice);
        dest.writeString(driverDetailServiceCartStatus);
        dest.writeInt(driverDetailServiceCartPrice);
        dest.writeInt(driverDetailChargeStartPrice);
        dest.writeInt(driverDetailChargeStartKm);
        dest.writeInt(driverDetailCharge);
        dest.writeInt(driverDistance);
        dest.writeDouble(driverRatingAvg);
        dest.writeInt(driverRatingCount);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<DriverDataAfterSort> CREATOR = new Creator<DriverDataAfterSort>() {
        @Override
        public DriverDataAfterSort createFromParcel(Parcel in) {
            return new DriverDataAfterSort(in);
        }

        @Override
        public DriverDataAfterSort[] newArray(int size) {
            return new DriverDataAfterSort[size];
        }
    };

    public int getDriverId() {
        return driverId;
    }

    public void setDriverId(int driverId) {
        this.driverId = driverId;
    }

    public String getDriverFname() {
        return driverFname;
    }

    public void setDriverFname(String driverFname) {
        this.driverFname = driverFname;
    }

    public String getDriverLname() {
        return driverLname;
    }

    public void setDriverLname(String driverLname) {
        this.driverLname = driverLname;
    }

    public String getDriverEmail() {
        return driverEmail;
    }

    public void setDriverEmail(String driverEmail) {
        this.driverEmail = driverEmail;
    }

    public String getDriverTel() {
        return driverTel;
    }

    public void setDriverTel(String driverTel) {
        this.driverTel = driverTel;
    }

    public String getDriverFcmId() {
        return driverFcmId;
    }

    public void setDriverFcmId(String driverFcmId) {
        this.driverFcmId = driverFcmId;
    }

    public String getDriverAddress() {
        return driverAddress;
    }

    public void setDriverAddress(String driverAddress) {
        this.driverAddress = driverAddress;
    }

    public String getDriverSex() {
        return driverSex;
    }

    public void setDriverSex(String driverSex) {
        this.driverSex = driverSex;
    }

    public String getDriverProvince() {
        return driverProvince;
    }

    public void setDriverProvince(String driverProvince) {
        this.driverProvince = driverProvince;
    }

    public String getDriverImgName() {
        return driverImgName;
    }

    public void setDriverImgName(String driverImgName) {
        this.driverImgName = driverImgName;
    }

    public String getDriverCreatedAt() {
        return driverCreatedAt;
    }

    public void setDriverCreatedAt(String driverCreatedAt) {
        this.driverCreatedAt = driverCreatedAt;
    }

    public String getDriverChangeAt() {
        return driverChangeAt;
    }

    public void setDriverChangeAt(String driverChangeAt) {
        this.driverChangeAt = driverChangeAt;
    }

    public String getDriverDetailId() {
        return driverDetailId;
    }

    public void setDriverDetailId(String driverDetailId) {
        this.driverDetailId = driverDetailId;
    }

    public String getDriverDetailType() {
        return driverDetailType;
    }

    public void setDriverDetailType(String driverDetailType) {
        this.driverDetailType = driverDetailType;
    }

    public String getDriverDetailBrand() {
        return driverDetailBrand;
    }

    public void setDriverDetailBrand(String driverDetailBrand) {
        this.driverDetailBrand = driverDetailBrand;
    }

    public String getDriverDetailModel() {
        return driverDetailModel;
    }

    public void setDriverDetailModel(String driverDetailModel) {
        this.driverDetailModel = driverDetailModel;
    }

    public String getDriverDetailColor() {
        return driverDetailColor;
    }

    public void setDriverDetailColor(String driverDetailColor) {
        this.driverDetailColor = driverDetailColor;
    }

    public String getDriverDetailLicensePlate() {
        return driverDetailLicensePlate;
    }

    public void setDriverDetailLicensePlate(String driverDetailLicensePlate) {
        this.driverDetailLicensePlate = driverDetailLicensePlate;
    }

    public String getDriverDetailProvinceLicensePlate() {
        return driverDetailProvinceLicensePlate;
    }

    public void setDriverDetailProvinceLicensePlate(String driverDetailProvinceLicensePlate) {
        this.driverDetailProvinceLicensePlate = driverDetailProvinceLicensePlate;
    }

    public String getDriverDetailServiceLiftStatus() {
        return driverDetailServiceLiftStatus;
    }

    public void setDriverDetailServiceLiftStatus(String driverDetailServiceLiftStatus) {
        this.driverDetailServiceLiftStatus = driverDetailServiceLiftStatus;
    }

    public int getDriverDetailServiceLiftPrice() {
        return driverDetailServiceLiftPrice;
    }

    public void setDriverDetailServiceLiftPrice(int driverDetailServiceLiftPrice) {
        this.driverDetailServiceLiftPrice = driverDetailServiceLiftPrice;
    }

    public String getDriverDetailServiceLiftPlusStatus() {
        return driverDetailServiceLiftPlusStatus;
    }

    public void setDriverDetailServiceLiftPlusStatus(String driverDetailServiceLiftPlusStatus) {
        this.driverDetailServiceLiftPlusStatus = driverDetailServiceLiftPlusStatus;
    }

    public int getDriverDetailServiceLiftPlusPrice() {
        return driverDetailServiceLiftPlusPrice;
    }

    public void setDriverDetailServiceLiftPlusPrice(int driverDetailServiceLiftPlusPrice) {
        this.driverDetailServiceLiftPlusPrice = driverDetailServiceLiftPlusPrice;
    }

    public String getDriverDetailServiceCartStatus() {
        return driverDetailServiceCartStatus;
    }

    public void setDriverDetailServiceCartStatus(String driverDetailServiceCartStatus) {
        this.driverDetailServiceCartStatus = driverDetailServiceCartStatus;
    }

    public int getDriverDetailServiceCartPrice() {
        return driverDetailServiceCartPrice;
    }

    public void setDriverDetailServiceCartPrice(int driverDetailServiceCartPrice) {
        this.driverDetailServiceCartPrice = driverDetailServiceCartPrice;
    }

    public int getDriverDetailChargeStartPrice() {
        return driverDetailChargeStartPrice;
    }

    public void setDriverDetailChargeStartPrice(int driverDetailChargeStartPrice) {
        this.driverDetailChargeStartPrice = driverDetailChargeStartPrice;
    }

    public int getDriverDetailChargeStartKm() {
        return driverDetailChargeStartKm;
    }

    public void setDriverDetailChargeStartKm(int driverDetailChargeStartKm) {
        this.driverDetailChargeStartKm = driverDetailChargeStartKm;
    }

    public int getDriverDetailCharge() {
        return driverDetailCharge;
    }

    public void setDriverDetailCharge(int driverDetailCharge) {
        this.driverDetailCharge = driverDetailCharge;
    }

    public int getDriverDistance() {
        return driverDistance;
    }

    public void setDriverDistance(int driverDistance) {
        this.driverDistance = driverDistance;
    }

    public double getDriverRatingAvg() {
        return driverRatingAvg;
    }

    public void setDriverRatingAvg(double driverRatingAvg) {
        this.driverRatingAvg = driverRatingAvg;
    }

    public int getDriverRatingCount() {
        return driverRatingCount;
    }

    public void setDriverRatingCount(int driverRatingCount) {
        this.driverRatingCount = driverRatingCount;
    }
}
