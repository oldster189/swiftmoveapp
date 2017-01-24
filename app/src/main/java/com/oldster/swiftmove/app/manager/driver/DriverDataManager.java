package com.oldster.swiftmove.app.manager.driver;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.google.gson.Gson;
import com.inthecheesefactory.thecheeselibrary.manager.Contextor;
import com.oldster.swiftmove.app.dao.DriverDataAfterSortCollection;

/**
 * Created by nuuneoi on 11/16/2014.
 */
public class DriverDataManager {


    private Context mContext;

    private DriverDataAfterSortCollection dataDriver;

    public DriverDataManager() {
        mContext = Contextor.getInstance().getContext();
        //loadCache();
    }


    public DriverDataAfterSortCollection getDataDriver() {
        return dataDriver;
    }

    public void setDataDriver(DriverDataAfterSortCollection dataDriver) {
        this.dataDriver = dataDriver;
        //saveCache();
    }

    public int getCount() {
        if (dataDriver == null) {
            return 0;
        }
        if (dataDriver.getDriver() == null) {
            return 0;
        }
        return dataDriver.getDriver().size();
    }
    public Bundle onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable("driverAll", dataDriver);
        return bundle;
    }

    public void onRestoreInstanceState(Bundle savedInstanceState) {
        dataDriver = savedInstanceState.getParcelable("driverAll");
    }
    private void loadCache() {
        SharedPreferences prefs = mContext.getSharedPreferences("data_driver",
                Context.MODE_PRIVATE);
        String json = prefs.getString("driver_all",null);
        if (json == null)
            return;
        dataDriver = new Gson().fromJson(json,DriverDataAfterSortCollection.class);
    }

    private void saveCache() {

        DriverDataAfterSortCollection cacheDao = new DriverDataAfterSortCollection();
        if (dataDriver != null && dataDriver.getDriver() != null)
            cacheDao.setDriver(dataDriver.getDriver());
        String json = new Gson().toJson(cacheDao);
        SharedPreferences prefs = mContext.getSharedPreferences("data_driver",
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        //Add/Edit/Delete
        editor.putString("driver_all", json);
        editor.apply();
    }

}
