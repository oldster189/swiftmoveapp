package com.oldster.swiftmove.app.dao;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Old'ster on 28/11/2559.
 */

public class JobDataDao implements Parcelable {
    @SerializedName("job_id")    private int jobId;
    @SerializedName("job_from_latitude")    private double jobFromLatitude;
    @SerializedName("job_from_longitude")    private double jobFromLongitude;
    @SerializedName("job_from_name_address")    private String jobFromNameAddress;

    @SerializedName("job_to_latitude")    private double jobToLatitude;
    @SerializedName("job_to_longitude")    private double jobToLongitude;
    @SerializedName("job_to_name_address")    private String jobToNameAddress;

    @SerializedName("job_time")    private String jobTime;
    @SerializedName("job_date")    private String jobDate;

    @SerializedName("job_service_lift_status")    private String jobServiceLiftStatus;
    @SerializedName("job_service_lift_price")    private int jobServiceLiftPrice;

    @SerializedName("job_service_lift_plus_status")    private String jobServiceLiftPlusStatus;
    @SerializedName("job_service_lift_plus_price")    private int jobServiceLiftPlusPrice;

    @SerializedName("job_service_cart_status")    private String jobServiceCartStatus;
    @SerializedName("job_service_cart_price")    private int jobServiceCartPrice;

    @SerializedName("job_charge_start_price")    private int jobChargeStartPrice;
    @SerializedName("job_charge_start_km")    private int jobChargeStartKm;
    @SerializedName("job_charge")    private int jobCharge;

    @SerializedName("job_status_name")    private String jobStatusName;
    @SerializedName("job_distance")    private double jobDistance;
    @SerializedName("job_created_at")    private String jobCreatedAt;

    @SerializedName("driver_id")    private int driverId;
    @SerializedName("driver_first_name")    private String driverFirstName;
    @SerializedName("driver_last_name")    private String driverLastName;
    @SerializedName("driver_email")    private String driverEmail;
    @SerializedName("driver_tel")    private String driverTel;
    @SerializedName("driver_address")    private String driverAddress;
    @SerializedName("driver_sex")    private String driverSex;
    @SerializedName("driver_province")    private String driverProvince;
    @SerializedName("driver_img_name")    private String driverImgName;
    @SerializedName("driver_fcm_id")    private String driverFcmId;

    @SerializedName("driver_detail_type")    private String driverDetailType;
    @SerializedName("driver_detail_brand")    private String driverDetailBrand;
    @SerializedName("driver_detail_model")    private String driverDetailModel;
    @SerializedName("driver_detail_color")    private String driverDetailColor;
    @SerializedName("driver_detail_license_plate")    private String driverDetailLicensePlate;
    @SerializedName("driver_detail_province_license_plate")    private int driverDetailProvinceLicensePlate;
    @SerializedName("job_comment")    private String jobComment;
    @SerializedName("job_rating")    private double jobRating;

    public JobDataDao(){

    }

    protected JobDataDao(Parcel in) {
        jobId = in.readInt();
        jobFromLatitude = in.readDouble();
        jobFromLongitude = in.readDouble();
        jobFromNameAddress = in.readString();
        jobToLatitude = in.readDouble();
        jobToLongitude = in.readDouble();
        jobToNameAddress = in.readString();
        jobTime = in.readString();
        jobDate = in.readString();
        jobServiceLiftStatus = in.readString();
        jobServiceLiftPrice = in.readInt();
        jobServiceLiftPlusStatus = in.readString();
        jobServiceLiftPlusPrice = in.readInt();
        jobServiceCartStatus = in.readString();
        jobServiceCartPrice = in.readInt();
        jobChargeStartPrice = in.readInt();
        jobChargeStartKm = in.readInt();
        jobCharge = in.readInt();
        jobStatusName = in.readString();
        jobDistance = in.readDouble();
        jobCreatedAt = in.readString();
        driverId = in.readInt();
        driverFirstName = in.readString();
        driverLastName = in.readString();
        driverEmail = in.readString();
        driverTel = in.readString();
        driverAddress = in.readString();
        driverSex = in.readString();
        driverProvince = in.readString();
        driverImgName = in.readString();
        driverFcmId = in.readString();
        driverDetailType = in.readString();
        driverDetailBrand = in.readString();
        driverDetailModel = in.readString();
        driverDetailColor = in.readString();
        driverDetailLicensePlate = in.readString();
        driverDetailProvinceLicensePlate = in.readInt();
        jobComment = in.readString();
        jobRating = in.readDouble();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(jobId);
        dest.writeDouble(jobFromLatitude);
        dest.writeDouble(jobFromLongitude);
        dest.writeString(jobFromNameAddress);
        dest.writeDouble(jobToLatitude);
        dest.writeDouble(jobToLongitude);
        dest.writeString(jobToNameAddress);
        dest.writeString(jobTime);
        dest.writeString(jobDate);
        dest.writeString(jobServiceLiftStatus);
        dest.writeInt(jobServiceLiftPrice);
        dest.writeString(jobServiceLiftPlusStatus);
        dest.writeInt(jobServiceLiftPlusPrice);
        dest.writeString(jobServiceCartStatus);
        dest.writeInt(jobServiceCartPrice);
        dest.writeInt(jobChargeStartPrice);
        dest.writeInt(jobChargeStartKm);
        dest.writeInt(jobCharge);
        dest.writeString(jobStatusName);
        dest.writeDouble(jobDistance);
        dest.writeString(jobCreatedAt);
        dest.writeInt(driverId);
        dest.writeString(driverFirstName);
        dest.writeString(driverLastName);
        dest.writeString(driverEmail);
        dest.writeString(driverTel);
        dest.writeString(driverAddress);
        dest.writeString(driverSex);
        dest.writeString(driverProvince);
        dest.writeString(driverImgName);
        dest.writeString(driverFcmId);
        dest.writeString(driverDetailType);
        dest.writeString(driverDetailBrand);
        dest.writeString(driverDetailModel);
        dest.writeString(driverDetailColor);
        dest.writeString(driverDetailLicensePlate);
        dest.writeInt(driverDetailProvinceLicensePlate);
        dest.writeString(jobComment);
        dest.writeDouble(jobRating);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<JobDataDao> CREATOR = new Creator<JobDataDao>() {
        @Override
        public JobDataDao createFromParcel(Parcel in) {
            return new JobDataDao(in);
        }

        @Override
        public JobDataDao[] newArray(int size) {
            return new JobDataDao[size];
        }
    };

    public double getJobRating() {
        return jobRating;
    }

    public void setJobRating(double jobRating) {
        this.jobRating = jobRating;
    }

    public String getJobComment() {
        return jobComment;
    }

    public void setJobComment(String jobComment) {
        this.jobComment = jobComment;
    }



    public int getJobId() {
        return jobId;
    }

    public void setJobId(int jobId) {
        this.jobId = jobId;
    }

    public double getJobFromLatitude() {
        return jobFromLatitude;
    }

    public void setJobFromLatitude(double jobFromLatitude) {
        this.jobFromLatitude = jobFromLatitude;
    }

    public double getJobFromLongitude() {
        return jobFromLongitude;
    }

    public void setJobFromLongitude(double jobFromLongitude) {
        this.jobFromLongitude = jobFromLongitude;
    }

    public String getJobFromNameAddress() {
        return jobFromNameAddress;
    }

    public void setJobFromNameAddress(String jobFromNameAddress) {
        this.jobFromNameAddress = jobFromNameAddress;
    }

    public double getJobToLatitude() {
        return jobToLatitude;
    }

    public void setJobToLatitude(double jobToLatitude) {
        this.jobToLatitude = jobToLatitude;
    }

    public double getJobToLongitude() {
        return jobToLongitude;
    }

    public void setJobToLongitude(double jobToLongitude) {
        this.jobToLongitude = jobToLongitude;
    }

    public String getJobToNameAddress() {
        return jobToNameAddress;
    }

    public void setJobToNameAddress(String jobToNameAddress) {
        this.jobToNameAddress = jobToNameAddress;
    }

    public String getJobTime() {
        return jobTime;
    }

    public void setJobTime(String jobTime) {
        this.jobTime = jobTime;
    }

    public String getJobDate() {
        return jobDate;
    }

    public void setJobDate(String jobDate) {
        this.jobDate = jobDate;
    }

    public String getJobServiceLiftStatus() {
        return jobServiceLiftStatus;
    }

    public void setJobServiceLiftStatus(String jobServiceLiftStatus) {
        this.jobServiceLiftStatus = jobServiceLiftStatus;
    }

    public int getJobServiceLiftPrice() {
        return jobServiceLiftPrice;
    }

    public void setJobServiceLiftPrice(int jobServiceLiftPrice) {
        this.jobServiceLiftPrice = jobServiceLiftPrice;
    }

    public String getJobServiceLiftPlusStatus() {
        return jobServiceLiftPlusStatus;
    }

    public void setJobServiceLiftPlusStatus(String jobServiceLiftPlusStatus) {
        this.jobServiceLiftPlusStatus = jobServiceLiftPlusStatus;
    }

    public int getJobServiceLiftPlusPrice() {
        return jobServiceLiftPlusPrice;
    }

    public void setJobServiceLiftPlusPrice(int jobServiceLiftPlusPrice) {
        this.jobServiceLiftPlusPrice = jobServiceLiftPlusPrice;
    }

    public String getJobServiceCartStatus() {
        return jobServiceCartStatus;
    }

    public void setJobServiceCartStatus(String jobServiceCartStatus) {
        this.jobServiceCartStatus = jobServiceCartStatus;
    }

    public int getJobServiceCartPrice() {
        return jobServiceCartPrice;
    }

    public void setJobServiceCartPrice(int jobServiceCartPrice) {
        this.jobServiceCartPrice = jobServiceCartPrice;
    }

    public int getJobChargeStartPrice() {
        return jobChargeStartPrice;
    }

    public void setJobChargeStartPrice(int jobChargeStartPrice) {
        this.jobChargeStartPrice = jobChargeStartPrice;
    }

    public int getJobChargeStartKm() {
        return jobChargeStartKm;
    }

    public void setJobChargeStartKm(int jobChargeStartKm) {
        this.jobChargeStartKm = jobChargeStartKm;
    }

    public int getJobCharge() {
        return jobCharge;
    }

    public void setJobCharge(int jobCharge) {
        this.jobCharge = jobCharge;
    }

    public String getJobStatusName() {
        return jobStatusName;
    }

    public void setJobStatusName(String jobStatusName) {
        this.jobStatusName = jobStatusName;
    }

    public double getJobDistance() {
        return jobDistance;
    }

    public void setJobDistance(double jobDistance) {
        this.jobDistance = jobDistance;
    }

    public String getJobCreatedAt() {
        return jobCreatedAt;
    }

    public void setJobCreatedAt(String jobCreatedAt) {
        this.jobCreatedAt = jobCreatedAt;
    }

    public int getDriverId() {
        return driverId;
    }

    public void setDriverId(int driverId) {
        this.driverId = driverId;
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

    public String getDriverFcmId() {
        return driverFcmId;
    }

    public void setDriverFcmId(String driverFcmId) {
        this.driverFcmId = driverFcmId;
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

    public int getDriverDetailProvinceLicensePlate() {
        return driverDetailProvinceLicensePlate;
    }

    public void setDriverDetailProvinceLicensePlate(int driverDetailProvinceLicensePlate) {
        this.driverDetailProvinceLicensePlate = driverDetailProvinceLicensePlate;
    }

}
