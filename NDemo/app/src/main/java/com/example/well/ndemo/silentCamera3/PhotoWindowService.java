package com.example.well.ndemo.silentCamera3;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;

import com.example.well.ndemo.BuildConfig;
import com.example.well.ndemo.utils.SettingsUtils;


/**
 * 后台进行拍照的服务
 */
public class PhotoWindowService extends Service {


    private MyPhotoWindowManager myWindowManager;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (BuildConfig.DEBUG) Log.e("PhotoWindowService", "onCreate");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (BuildConfig.DEBUG) Log.e("PhotoWindowService", "onStartCommand");
        initReceiver();
        myWindowManager = new MyPhotoWindowManager();
        createWindow();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (BuildConfig.DEBUG) Log.e("PhotoWindowService", "onDestroy");
        if (mReceiver != null) {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(mReceiver);
        }
    }

    private void createWindow() {
        // 当前界面是桌面，且没有悬浮窗显示，则创建悬浮窗。
        myWindowManager.removeSmallWindow(getApplicationContext());
        myWindowManager.createSmallWindow(getApplicationContext());

    }

    private void initReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(SettingsUtils.ACTION_SILENT_MASTER_START);
        filter.addAction(SettingsUtils.ACTION_SILENT_MASTER_STOP);
        LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver, filter);
    }


    public void startCarema() {
        myWindowManager.startCarema();
    }

    public void stopCarema() {
        myWindowManager.stopCarema();
    }


    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BuildConfig.DEBUG) Log.e("PhotoWindowService", action);
            if (TextUtils.equals(action, SettingsUtils.ACTION_SILENT_MASTER_START)) {//开始
                startCarema();
            } else if (TextUtils.equals(action, SettingsUtils.ACTION_SILENT_MASTER_STOP)) {// 结束
                stopCarema();
            }
        }


    };
}
