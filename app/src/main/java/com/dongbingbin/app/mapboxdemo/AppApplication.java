package com.dongbingbin.app.mapboxdemo;

import android.app.Application;

import com.dongbingbin.app.mapboxdemo.mapbox.CrashHandler;

import io.paperdb.Paper;

public class AppApplication extends Application {

    public static AppApplication getAppApplication(){
        return appApplication;
    }

    private static AppApplication appApplication;

    @Override
    public void onCreate() {
        super.onCreate();
        Paper.init(this);
        appApplication = this;
        CrashHandler.getInstance().init(this);
    }
}
