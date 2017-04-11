package com.example.well.ndemo.ui.activity;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;

import com.example.well.ndemo.utils.PermissionUtils;
import com.example.well.ndemo.utils.SnackbarUtils;
import com.trello.rxlifecycle.components.support.RxAppCompatActivity;

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
        this.context=this;
    }




    /**
     * 请求权限
     *
     * @param permissions 权限列表
     * @param handler     回调
     */
    protected void requestPermission(String[] permissions, PermissionHandler handler) {
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
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
                    SnackbarUtils.showDefaultLongSnackbar(getWindow().getDecorView().getRootView(),"权限已被拒绝,请在设置-应用-权限中打开");
                }

            } else {
                mHandler.onDenied();
            }
        }
    }


    /**
     * 权限回调接口
     */
    public abstract class PermissionHandler {
        /**
         * 权限通过
         */
        public void onGranted(){}

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
