package com.example.well.ndemo;

import android.app.Application;
import android.content.Context;

import com.example.well.ndemo.net.rxretrofit.RxRetrofitApp;


public class MyApplication extends Application{
    public static Context app;

    @Override
    public void onCreate() {
        super.onCreate();
        app=getApplicationContext();
        RxRetrofitApp.init(this);
    }
}
