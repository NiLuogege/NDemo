package com.example.well.ndemo.ui.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.amap.api.maps.MapView;
import com.example.well.ndemo.R;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by ${LuoChen} on 2017/4/26 17:09.
 * email:luochen0519@foxmail.com
 *
 * 历史行踪显示Activity
 */

public class MapRecordActivity extends BaseActivity {
    @Bind(R.id.map)
    MapView mMapView;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_record);
        ButterKnife.bind(this);
        mMapView.onCreate(savedInstanceState);
    }
}
