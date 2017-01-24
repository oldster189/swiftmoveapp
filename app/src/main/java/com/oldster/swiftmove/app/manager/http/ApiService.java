package com.oldster.swiftmove.app.manager.http;


import com.oldster.swiftmove.app.Constants.EndPoints;
import com.oldster.swiftmove.app.dao.CommentDataCollectionDao;
import com.oldster.swiftmove.app.dao.DriverDataAfterSortCollection;
import com.oldster.swiftmove.app.dao.DriverFavoriteCollectionDao;
import com.oldster.swiftmove.app.dao.JobDataCollectionDao;
import com.oldster.swiftmove.app.dao.TemplatesMessageDado;
import com.oldster.swiftmove.app.dao.UserDataCollectionDao;
import com.oldster.swiftmove.app.dao.UserSendNotification;
import com.oldster.swiftmove.app.dao.UserUpdateFcmDao;
import com.oldster.swiftmove.app.dao.UserUpdatePasswordDao;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ApiService {

    /**
     * FOR USERS
     */

    ////////////////////// POST/////////////////////////////////
    @FormUrlEncoded
    @POST(EndPoints.USER_CREATE)
    Call<UserDataCollectionDao> registerUser(@Field("fname") String fname,
                                             @Field("lname") String lname,
                                             @Field("email") String email,
                                             @Field("password") String password,
                                             @Field("tel") String tel);

    @FormUrlEncoded
    @POST(EndPoints.USER_LOGIN)
    Call<UserDataCollectionDao> loginUser(@Field("email") String email,
                                          @Field("password") String password);


    @FormUrlEncoded
    @POST(EndPoints.USER_NOTIFICATION)
    Call<UserSendNotification> userSentNotification(@Path("did") int did,
                                                    @Field("title") String title,
                                                    @Field("priority") String priority,
                                                    @Field("body") String body);

    @FormUrlEncoded
    @POST(EndPoints.USER_UPDATE_DRIVER_FAVORITE)
    Call<TemplatesMessageDado> userUpdateDriverFavorite(@Field("uid") int uid,
                                                        @Field("did") int did);

    ///////////////////PUT/////////////////////////////

    @FormUrlEncoded
    @PUT(EndPoints.USER_UPDATE_DATA)
    Call<UserDataCollectionDao> updateUser(@Field("fname") String fname,
                                           @Field("lname") String lname,
                                           @Field("email") String email,
                                           @Field("tel") String tel,
                                           @Field("img_name") String imgName,
                                           @Field("img_encode") String imgEncode,
                                           @Field("password_old") String passwordOld,
                                           @Field("password_new") String passwordNew,
                                           @Path("uid") int uid);

    @FormUrlEncoded
    @PUT(EndPoints.USER_UPDATE_FCM)
    Call<UserUpdateFcmDao> userUpdateFcm(@Path("uid") int uid,
                                         @Field("user_fcm_id") String user_fcm_id);

    @FormUrlEncoded
    @PUT(EndPoints.USER_UPDATE_PASSWORD)
    Call<UserUpdatePasswordDao> userUpdatePassword(@Path("uid") int uid,
                                                   @Field("password_old") String password_old,
                                                   @Field("password_new") String password_new);

    ///////////////////////////GET /////////////////////////////////

    @GET(EndPoints.USER_GET_DATA_JOB_PROCESS)
    Call<JobDataCollectionDao> loadDataJobProcess(@Path("uid") int uid);

    @GET(EndPoints.USER_GET_DATA_JOB_HISTORY)
    Call<JobDataCollectionDao> loadDataJobHistory(@Path("uid") int uid);

    /**
     * FOR DRIVER
     */


    ///////////////////////////POST///////////////////////////////////
    @FormUrlEncoded
    @POST(EndPoints.DRIVER_GET_DATA_AFTER_SORT)
    Call<DriverDataAfterSortCollection> loadDataDriver(@Field("province") String province,
                                                       @Field("lat") double lat,
                                                       @Field("lng") double lng,
                                                       @Field("distance") int distance,
                                                       @Field("typeCar") String typeCar);

    @FormUrlEncoded
    @POST(EndPoints.USER_INSERT_RATING)
    Call<TemplatesMessageDado> insertRating(@Field("is_favorite") boolean isFavorite,
                                            @Field("job_id") int jobId,
                                            @Field("rating_value") float ratingValue,
                                            @Field("comment_detail") String commentDetail);


    ////////////////////////////PUT///////////////////////////////////


    ////////////////////////////GET/////////////////////////////////////
    @GET(EndPoints.USER_GET_FAVORITE)
    Call<DriverFavoriteCollectionDao> loadDataDriverFavorite(@Path("uid") int uid);

    @GET(EndPoints.DRIVER_GET_DATA_COMMENT)
    Call<CommentDataCollectionDao> loadDataComment(@Path("did") int did);

    /**
     * FOR JOB
     */

    @FormUrlEncoded
    @PUT(EndPoints.USER_UPDATE_JOB_STATUS)
    Call<TemplatesMessageDado> userUpdateJobStatus(@Path("jid") int jid,
                                                   @Field("status_name") String statusName,
                                                   @Field("status_message") String statusMessage,
                                                   @Field("driver_id") int driverId);


    @FormUrlEncoded
    @PUT(EndPoints.USER_UPDATE_JOB_STATUS_AUTO)
    Call<TemplatesMessageDado> userUpdateJobStatusAuto(@Path("jid") int jid,
                                                       @Field("status_name") String statusName,
                                                       @Field("status_message") String statusMessage,
                                                       @Field("driver_id") int driverId,
                                                       @Field("user_id") int userId);

    @FormUrlEncoded
    @POST(EndPoints.JOB_INSERT)
    Call<TemplatesMessageDado> insertJob(@Field("fr_lat") double frLat,
                                         @Field("fr_lng") double frLng,
                                         @Field("fr_address") String frAddress,
                                         @Field("to_lat") double toLat,
                                         @Field("to_lng") double toLng,
                                         @Field("to_address") String toAddress,
                                         @Field("time") String time,
                                         @Field("date") String date,
                                         @Field("lift_status") boolean liftStatus,
                                         @Field("lift_price") int liftPrice,
                                         @Field("lift_plus_status") boolean liftPlusStatus,
                                         @Field("lift_plus_price") int liftPlusPrice,
                                         @Field("cart_status") boolean cartStatus,
                                         @Field("cart_price") int cartPrice,
                                         @Field("charge_start_price") int chargeStartPrice,
                                         @Field("charge_start_km") int chargeStartKm,
                                         @Field("charge_rate") int chargeRate,
                                         @Field("job_status") String jobStatus,
                                         @Field("job_distance") double jobDistance,
                                         @Field("image1") String image1,
                                         @Field("image2") String image2,
                                         @Field("image3") String image3,
                                         @Field("user_id") int userId,
                                         @Field("driver_id") int driverId,
                                         @Field("total") double total);


}
