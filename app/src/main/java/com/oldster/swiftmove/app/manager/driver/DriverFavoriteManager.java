package com.oldster.swiftmove.app.manager.driver;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.inthecheesefactory.thecheeselibrary.manager.Contextor;
import com.oldster.swiftmove.app.dao.DriverFavoriteCollectionDao;

public class DriverFavoriteManager {


    private Context mContext;

    private DriverFavoriteCollectionDao dataDriverFavorite;

    public DriverFavoriteManager() {
        mContext = Contextor.getInstance().getContext();
        loadCache();
    }


    public DriverFavoriteCollectionDao getDataDriverFavorite() {
        return dataDriverFavorite;
    }

    public void setDataDriverFavorite(DriverFavoriteCollectionDao dataDriverFavorite) {
        this.dataDriverFavorite = dataDriverFavorite;
        saveCache();
    }

    private void loadCache() {
        SharedPreferences prefs = mContext.getSharedPreferences("DriverFavorite",
                Context.MODE_PRIVATE);
        String json = prefs.getString("driver_favorite",null);
        if (json == null)
            return;
        dataDriverFavorite = new Gson().fromJson(json,DriverFavoriteCollectionDao.class);
    }

    private void saveCache() {
        DriverFavoriteCollectionDao cacheDao = new DriverFavoriteCollectionDao();
        if (dataDriverFavorite != null && dataDriverFavorite.getData() != null)
            cacheDao.setData(dataDriverFavorite.getData());
        String json = new Gson().toJson(cacheDao);
        SharedPreferences prefs = mContext.getSharedPreferences("DriverFavorite",
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("driver_favorite", json);
        editor.apply();
    }

}
