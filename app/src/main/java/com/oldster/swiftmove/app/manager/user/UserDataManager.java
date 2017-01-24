package com.oldster.swiftmove.app.manager.user;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.inthecheesefactory.thecheeselibrary.manager.Contextor;
import com.oldster.swiftmove.app.dao.UserDataCollectionDao;

/**
 * Created by nuuneoi on 11/16/2014.
 */
public class UserDataManager {

    // Shared Preferences
    private SharedPreferences pref;

    // Editor for Shared preferences
    private SharedPreferences.Editor editor;

    // Shared pref mode
    private int PRIVATE_MODE = 0;

    // Sharedpref file name
    private static final String PREF_NAME = "swiftmove_user";
    private Context mContext;
    private UserDataCollectionDao user;

    public UserDataManager() {
        mContext = Contextor.getInstance().getContext();
        pref = mContext.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
        loadCache();
    }

    public UserDataCollectionDao getUser() {
        return user;
    }

    public void setUser(UserDataCollectionDao user) {
        this.user = user;
        saveCache();
    }

    public int getCount() {
        if (user == null) {
            return 0;
        }
        if (user.getUser() == null) {
            return 0;
        }
        return user.getUser().size();
    }

    private void saveCache() {

        UserDataCollectionDao cacheDao = new UserDataCollectionDao();
        if (user != null && user.getUser() != null)
            cacheDao.setUser(user.getUser());
        String json = new Gson().toJson(cacheDao);
        editor.putString("user", json);
        editor.apply();
    }

    private void loadCache() {
        String json = pref.getString("user", null);
        if (json == null)
            return;
        user = new Gson().fromJson(json, UserDataCollectionDao.class);
    }

    public void clear() {
        editor.clear();
        editor.commit();

    }

}
