package com.example.well.ndemo.silentCamera3;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.well.ndemo.BuildConfig;
import com.example.well.ndemo.R;
import com.example.well.ndemo.silentCamera.Config4Camera;
import com.example.well.ndemo.ui.activity.BaseActivity;
import com.example.well.ndemo.utils.SettingsUtils;
import com.example.well.ndemo.utils.SnackbarUtils;
import com.example.well.ndemo.utils.SystemUtils;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity_silentCamera3 extends BaseActivity {
    @Bind(R.id.ll_root)
    LinearLayout ll_root;

    private static final int CODE_OVERLAY_PERMISSION = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_3);
        ButterKnife.bind(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestDrawOverlaysPermission();
        } else {
            init();
        }
    }

    private void init() {
        Intent intent = new Intent(MainActivity_silentCamera3.this, PhotoWindowService.class);
        startService(intent);
        initReceiver();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void requestDrawOverlaysPermission() {
        if (!Settings.canDrawOverlays(this)) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent, CODE_OVERLAY_PERMISSION);
        }
    }


    private void requestOtherPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA};
            requestPermission(permissions, new PermissionHandler() {
                @Override
                public void onGranted() {
                    super.onGranted();
                    if (BuildConfig.DEBUG) Log.e("MainActivity_silentCame", "onGranted");
                    init();
                }

                @Override
                public void onDenied() {
                    super.onDenied();
                    if (BuildConfig.DEBUG) Log.e("MainActivity_silentCame", "onDenied");
                    SnackbarUtils.showDefaultLongSnackbar(ll_root, "请开启权限否则无法运行");
                }
            });
        }
    }

    private void initReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(SettingsUtils.ACTION_SILENT_MASTER_OK);
        LocalBroadcastManager.getInstance(MainActivity_silentCamera3.this).registerReceiver(mReceiver, filter);
    }

    public void addView(Uri uri) {
        ImageView mImageView = new ImageView(MainActivity_silentCamera3.this);
        if (BuildConfig.DEBUG) Log.e("CGImageActivity", "uri:" + uri);
        mImageView.setImageURI(uri);
        ViewGroup.LayoutParams mLayoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, SystemUtils.dp2px(300, getResources()));
        int i = SystemUtils.dp2px(10, getResources());
        mImageView.setPadding(i, i, i, i);
        mImageView.setLayoutParams(mLayoutParams);
        ll_root.addView(mImageView, Math.min(1, ll_root.getChildCount()));

    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (TextUtils.equals(action, SettingsUtils.ACTION_SILENT_MASTER_OK)) {
                if (BuildConfig.DEBUG) Log.e("CGImageActivity", "拍照成功");
                if (Config4Camera.silentPotoList.size() > 0) {
                    String mUri = Config4Camera.silentPotoList.get(Config4Camera.silentPotoList.size() - 1);
                    addView(Uri.parse(mUri));
                }
            }

        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CODE_OVERLAY_PERMISSION) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (Settings.canDrawOverlays(this)) {
                    Log.e("MainActivity_silentCame", "同意权限");
                    requestOtherPermission();
                } else {
                    if (BuildConfig.DEBUG)
                        Log.e("MainActivity_silentCame", "onActivityResult请开启权限否则无法运行");
                    SnackbarUtils.showDefaultLongSnackbar(ll_root, "请开启权限否则无法运行");
                }
            }
        }


    }


}

