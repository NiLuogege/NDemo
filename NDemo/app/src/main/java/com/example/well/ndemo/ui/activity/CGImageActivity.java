package com.example.well.ndemo.ui.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.well.ndemo.BuildConfig;
import com.example.well.ndemo.R;
import com.example.well.ndemo.silentCamera.Config4Camera;
import com.example.well.ndemo.utils.SettingsUtils;
import com.example.well.ndemo.utils.SystemUtils;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by ${LuoChen} on 2017/5/8 11:13.
 * email:luochen0519@foxmail.com
 */

public class CGImageActivity extends BaseActivity{
    @Bind(R.id.ll_root)
    LinearLayout ll_root;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cg_image);
        ButterKnife.bind(this);
        init();
    }

    private void init() {
        initReceiver();
    }

    private void initReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(SettingsUtils.ACTION_SILENT_MASTER_OK);
        LocalBroadcastManager.getInstance(context).registerReceiver(mReceiver, filter);
    }

    public void addView(Uri uri) {
        ImageView mImageView = new ImageView(CGImageActivity.this);


        if (BuildConfig.DEBUG) Log.e("CGImageActivity", "uri:" + uri);
        mImageView.setImageURI(uri);
        ViewGroup.LayoutParams mLayoutParams =new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, SystemUtils.dp2px(300,getResources()));
        int i = SystemUtils.dp2px(10, getResources());
        mImageView.setPadding(i, i, i, i);
        mImageView.setLayoutParams(mLayoutParams);
        ll_root.addView(mImageView, Math.min(1, ll_root.getChildCount()));

    }

    private BroadcastReceiver mReceiver= new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (BuildConfig.DEBUG) Log.e("CGImageActivity", "拍照成功");
            if (Config4Camera.silentPotoList.size() > 0) {
                String mUri = Config4Camera.silentPotoList.get(Config4Camera.silentPotoList.size() - 1);
                addView(Uri.parse(mUri));
            }
        }
    };


}
