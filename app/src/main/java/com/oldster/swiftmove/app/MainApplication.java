package com.oldster.swiftmove.app;

import android.app.Application;

import com.crashlytics.android.Crashlytics;
import com.inthecheesefactory.thecheeselibrary.manager.Contextor;

import io.fabric.sdk.android.Fabric;

/**
 * Created by Old'ster on 19/8/2559.
 */


public class MainApplication extends Application {


    @Override
    public void onCreate() {
        super.onCreate();

        Fabric.with(this, new Crashlytics());
        // เอา Context ตัวนี้ไปแจกจ่ายให้แอปพลิเคชันตัวอื่น
        Contextor.getInstance().init(getApplicationContext());

    }


    @Override
    public void onTerminate() {
        super.onTerminate();
    }


}

