package com.oldster.swiftmove.app.Constants;

public class EndPoints {


    //URL
  //  private static final String URL = "http://54.169.217.171/";
    private static final String URL = "http://192.168.137.1/swiftmove/";
    public static final String BASE_URL_REST = URL + "v1/";

    //URL IMG
    public static final String BASE_URL_IMG = URL + "images/users/";
    public static final String BASE_URL_IMG_NEWS= URL + "images/news/";

    /**
     * FOR USERS
     */

    //POST
    public static final String USER_LOGIN = "user/login";
    public static final String USER_CREATE = "user/create";
    public static final String USER_INSERT_RATING = "user/insert/rating";
    public static final String USER_UPDATE_DRIVER_FAVORITE = "user/update/driver/favorite";

    //PUT
    public static final String USER_UPDATE_DATA = "user/update/data/{uid}";
    public static final String USER_UPDATE_FCM = "user/update/fcm/{uid}";
    public static final String USER_UPDATE_PASSWORD = "user/update/password/{uid}";
    public static final String USER_UPDATE_JOB_STATUS = "user/update/job/status/{jid}";
    public static final String USER_UPDATE_JOB_STATUS_AUTO = "user/update/job/status/auto/{jid}";

    //GET

    public static final String USER_GET_DATA_JOB_PROCESS = "user/job/process/data/{uid}";
    public static final String USER_GET_DATA_JOB_HISTORY = "user/job/history/data/{uid}";
    public static final String USER_GET_FAVORITE = "user/get/favorite/{uid}";

    //PUSH Notification
    public static final String USER_NOTIFICATION = "user/notification/{did}";

    /**
     * FOR DRIVER
     */

    public static final String BASE_URL_IMG_DRIVER = URL + "images/driver/";

    //POST
    public static final String DRIVER_GET_DATA_AFTER_SORT = "driver/position";
    //PUT

    //GET
    public static final String DRIVER_GET_DATA_FAVORITE = "driver/favorite/user/{uid}";
    public static final String DRIVER_GET_DATA_COMMENT = "driver/get/comment/{did}";

    //PUSH Notification


    /**
     * FOR JOB
     */
    public static final String JOB_INSERT = "job/insert";

    public static final String URL_TOM = "http://192.168.137.1/swiftmove/v1/";
}
