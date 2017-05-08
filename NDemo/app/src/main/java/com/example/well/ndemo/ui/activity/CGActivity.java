package com.example.well.ndemo.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Button;

import com.amap.api.maps.MapView;
import com.example.well.ndemo.BuildConfig;
import com.example.well.ndemo.R;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by ${LuoChen} on 2017/5/8 10:58.
 * email:luochen0519@foxmail.com
 */

public class CGActivity extends BaseActivity {
    @Bind(R.id.map)
    MapView mMapView;
    @Bind(R.id.btn_cg)
    Button btn_cg;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cg);
        ButterKnife.bind(this);
        mMapView.onCreate(savedInstanceState);// 此方法须覆写，虚拟机需要在很多情况下保存地图绘制的当前状态。

    }


    @OnClick(R.id.btn_cg)
    void btn_cg(){
        if (BuildConfig.DEBUG) Log.e("CGActivity", "btn_cg:");
        Intent intent_cg = new Intent(context, CGImageActivity.class);
        startActivity(intent_cg);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，销毁地图
        mMapView.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView.onResume ()，重新绘制加载地图
        mMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView.onPause ()，暂停地图的绘制
        mMapView.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //在activity执行onSaveInstanceState时执行mMapView.onSaveInstanceState (outState)，保存地图当前的状态
        mMapView.onSaveInstanceState(outState);
    }
}
