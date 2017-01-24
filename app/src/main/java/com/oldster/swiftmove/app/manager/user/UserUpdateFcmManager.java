package com.oldster.swiftmove.app.manager.user;

import android.content.Context;
import android.content.SharedPreferences;

import com.inthecheesefactory.thecheeselibrary.manager.Contextor;

/**
 * Created by nuuneoi on 11/16/2014.
 */
public class UserUpdateFcmManager {

    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private int PRIVATE_MODE = 0;
    private static final String PREF_NAME = "swiftmove_user_fcm";


    private Context mContext;

    public UserUpdateFcmManager() {
        mContext = Contextor.getInstance().getContext();
        pref = mContext.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    private String fcmToken;

    public String getFcmToken() {
        if (pref.getString("fcmToken", null) != null) {
            fcmToken = pref.getString("fcmToken", null);
            return fcmToken;
        }
        return null;
    }

    public void storeFcmToken(String fcmToken) {
        editor.putString("fcmToken", fcmToken);
        editor.apply();
    }

    public void clear() {
        editor.clear();
        editor.commit();
    }
}
