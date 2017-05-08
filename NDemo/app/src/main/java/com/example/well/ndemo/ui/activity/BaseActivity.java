package com.example.well.ndemo.ui.activity;

import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatDelegate;
import android.util.Log;

import com.example.well.ndemo.BuildConfig;
import com.example.well.ndemo.utils.PermissionUtils;
import com.example.well.ndemo.utils.SPUtils;
import com.example.well.ndemo.utils.SettingsUtils;
import com.example.well.ndemo.utils.SnackbarUtils;
import com.trello.rxlifecycle.components.support.RxAppCompatActivity;

import java.io.File;

import cn.sharesdk.framework.ShareSDK;

/**
 * Created by ${LuoChen} on 2017/3/3 17:08.
 * email:luochen0519@foxmail.com
 */

public class BaseActivity extends RxAppCompatActivity {
    protected BaseActivity context;

    /**
     * 权限回调Handler
     */
    private PermissionHandler mHandler;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.context = this;
        setNightMode();
    }

    /**
     * 创建根目录
     */
    protected void creatBaseDir() {
        File dir = new File(Environment.getExternalStorageDirectory(), SettingsUtils.SD_DIR);
        if (!dir.exists() || !dir.isDirectory()) {
            if (BuildConfig.DEBUG) Log.e("MyApplication", "creatBaseDir");
            dir.mkdirs();
        }
    }


    /**
     * 初始化shareSDK 官方文档建议初始化放到入口Activity中
     */
    protected void initShareSDK() {
        ShareSDK.initSDK(context, SettingsUtils.SHAREAPPKEY);
    }

    private void setNightMode() {
        boolean isNightOn = SPUtils.getInstance(getApplication()).getBoolean(SettingsUtils.IS_NIGHT_ON);
        if (isNightOn) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }


    /**
     * 请求权限
     *
     * @param permissions 权限列表
     * @param handler     回调
     */
    protected void requestPermission(String[] permissions, PermissionHandler handler) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (PermissionUtils.hasSelfPermissions(this, permissions)) {//同意
                handler.onGranted();
            } else {
                mHandler = handler;
                ActivityCompat.requestPermissions(this, permissions, 001);
            }
        }
    }


    /**
     * 权限请求结果
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (mHandler == null) return;

//        if (PermissionUtils.getTargetSdkVersion(this) < 23 && !PermissionUtils.hasSelfPermissions(this, permissions)) {
//            mHandler.onDenied();
//            return;
//        }

        if (PermissionUtils.verifyPermissions(grantResults)) {
            mHandler.onGranted();
        } else {
            if (!PermissionUtils.shouldShowRequestPermissionRationale(this, permissions)) {
                if (!mHandler.onNeverAsk()) {
                    //TODO
//                    Toast.makeText(this, "权限已被拒绝,请在设置-应用-权限中打开", Toast.LENGTH_SHORT).show();
                    SnackbarUtils.showDefaultLongSnackbar(getWindow().getDecorView().getRootView(), "权限已被拒绝,请在设置-应用-权限中打开");
                }

            } else {
                mHandler.onDenied();
            }
        }
    }

    /**
     * 获取状态栏高度
     * @return
     */
    protected int getStatusBarHeight() {
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            //根据资源ID获取响应的尺寸值
            return getResources().getDimensionPixelSize(resourceId);
        }
        return -1;
    }


    /**
     * 权限回调接口
     */
    public abstract class PermissionHandler {
        /**
         * 权限通过
         */
        public void onGranted() {
        }

        /**
         * 权限拒绝
         */
        public void onDenied() {
        }

        /**
         * 不再询问
         *
         * @return 如果要覆盖原有提示则返回true
         */
        public boolean onNeverAsk() {
            return false;
        }
    }


}
