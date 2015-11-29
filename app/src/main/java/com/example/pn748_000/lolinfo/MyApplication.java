package com.example.pn748_000.lolinfo;

import android.app.Application;
import android.content.Context;

/**
 * Created by pn748_000 on 8/2/2015.
 */
public class MyApplication extends Application {
    private static MyApplication sInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance=this;
    }
    public static MyApplication getInstace(){
        return sInstance;
    }
    public static Context getAppContext(){
        return sInstance.getApplicationContext();
    }
}
