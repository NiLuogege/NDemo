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
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.well.ndemo.BuildConfig;
import com.example.well.ndemo.R;
import com.example.well.ndemo.silentCamera.Config4Camera;
import com.example.well.ndemo.utils.SettingsUtils;

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



        mImageView.setImageURI(uri);
        ViewGroup.LayoutParams mLayoutParams =new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        mImageView.setPadding(10, 10, 10, 10);
        mImageView.setLayoutParams(mLayoutParams);

        mImageView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ll_root.removeView(v);
            }
        });
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
